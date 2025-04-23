package org.preesm.algorithm.synthesis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.clustering.ClusterBuilder;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.algorithm.schedule.fpga.AdfgOjalgoFpgaFifoEvaluator;
import org.preesm.algorithm.schedule.sdf.HeterogeneousScheduler;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.algorithm.synthesis.schedule.algos.FpgaScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.IScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.SimpleScheduler;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.CPU;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.FPGA;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

public class PreesmHeterogeneousSynthesisTask extends AbstractTaskImplementation {

  final PiMMUserFactory PiMMFactory = org.preesm.model.pisdf.factory.PiMMUserFactory.instance;

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final PiGraph algorithm = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);

    // clusterize the graph
    final List<PiGraph> clustersList = ClusterBuilder.buildArchHierarchyGraph(algorithm, scenario);

    // ------------------ locally schedule and map the clusters' graphs ------------------

    for (final PiGraph cluster : clustersList) {
      // find the right scheduler-mapper based on the cluster's shared archi : cpu, fpga, cgra...
      final String localSchedulerMapperName = switchSchedulerMapper(cluster, scenario);

      final IScheduler localSchedulerMapper = getSchedulerMapperInstance(localSchedulerMapperName);

      final SynthesisResult res = localSchedulerMapper.scheduleAndMap(cluster, architecture, scenario);
    }

    // ------------------ replace hierarchical actors with placeholders ------------------

    for (final AbstractActor actor : algorithm.getActors()) {
      if (actor instanceof PiGraph) {
        final Actor placeholder = PiMMFactory.createActor(actor.getName() + "_placeholder");
        placeholder.setRefinement(PiMMFactory.createCHeaderRefinement()); // empty refinement for now
        algorithm.addActor(placeholder);
        replaceAndRemoveActor(actor, placeholder, algorithm);
        // scenario.getConstraints().addConstraint(cpu1, placeholder); // test, idéalement ça serait une "non-archi"
      }
    }

    // On retire la fpga de la liste d'archi pour que le scheduling CPU ne râle pas
    // design.getComponentHolder().getComponents().remove(fpga1.getComponent());

    // vieille api
    /*
     * parameters.put("Check", "True"); parameters.put("Optimize synchronization", "True");
     * parameters.put("balanceLoads", "True"); parameters.put("edgeSchedType", "Simple");
     * parameters.put("simulatorType", "AccuratelyTimed");
     *
     * Map<String, Object> schedule; schedule = HeterogeneousScheduler.schedule(inputs, parameters, monitor, nodeName,
     * workflow);
     *
     * assertNotNull(algo); // très très peu d'idées assertNotNull(schedule);
     *
     * final LatencyAbc ABCSchedule = (LatencyAbc) schedule.get("ABC"); // le latencyABC final MapperDAG resImpl = final
     * MapperDAG resImpl = ABCSchedule.getImplementation(); final MapperDAGVertex actor12 =
     * resImpl.getMapperDAGVertex("actor12"); ABCSchedule.getEffectiveComponent(actor12); ABCSchedule.getFinalLatency();
     * final VertexTiming timing = actor12.getTiming();
     *
     * ABCSchedule.getTotalOrder();
     */

    // nouvelle api
    final var synthesis = new PreesmSynthesisTask();

    parameters.put("scheduler", PreesmSynthesisTask.VALUE_SCHEDULER_SIMPLE);
    parameters.put("allocation", PreesmSynthesisTask.VALUE_ALLOCATORS_SIMPLE);

    final Map<String, Object> results = synthesis.execute(inputs, parameters, monitor, nodeName, workflow);

    final SynthesisResult schedule_mapping = HeterogeneousScheduler.schedule(algorithm, architecture, scenario);

    final ScheduleOrderManager scheduleOM = new ScheduleOrderManager(algorithm, schedule_mapping.schedule);

    return new HashMap<>();

  }

  @Override
  public Map<String, String> getDefaultParameters() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String monitorMessage() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Selects the adapted scheduler for a given cluster based on its mapping arch P.S : I don't care if you want to set
   * it public ;)
   *
   * @param cluster
   *          the cluster to map
   * @param scenario
   *          the scenario
   * @return the selected mapper's string name/identifier
   **/
  private String switchSchedulerMapper(AbstractActor cluster, Scenario scenario) {
    final List<ComponentInstance> mappings = ClusteringHelper.getArch(cluster, scenario);
    // Pour le moment, je vais supposer qu'un cluster est mappé à une seule archi. Cela correspond à l'idée que le
    // mapping "niveau archi" est fait entièrement lors de la phase de clustering, qui décide quel cluster est fait sur
    // quel type de PE.

    final ComponentInstance arch = mappings.getFirst();

    if (arch.getComponent() instanceof CPU) {
      return PreesmSynthesisTask.VALUE_SCHEDULER_SIMPLE; // temporaire
    }

    if (arch.getComponent() instanceof FPGA) {
      return AdfgOjalgoFpgaFifoEvaluator.FIFO_EVALUATOR_ADFG_DEFAULT_LINEAR;
    }
    // temporaire

    throw new PreesmSynthesisException("No mapper available for component " + arch.getInstanceName());

  }

  /***
   * Returns the corresponding scheduler-mapper instance based on its name. P.S : I don't care if you want to set it
   * public ;)
   *
   * @param localSchedulerMapperName
   *          the scheduler's name
   * @return the sceduler-mapper instance
   */
  private IScheduler getSchedulerMapperInstance(String localSchedulerMapperName) {
    // TODO expand switch
    switch (localSchedulerMapperName) {
      case AdfgOjalgoFpgaFifoEvaluator.FIFO_EVALUATOR_ADFG_DEFAULT_LINEAR,
          AdfgOjalgoFpgaFifoEvaluator.FIFO_EVALUATOR_ADFG_DEFAULT_EXACT:
        return new FpgaScheduler(localSchedulerMapperName);
      case PreesmSynthesisTask.VALUE_SCHEDULER_SIMPLE, PreesmSynthesisTask.VALUE_SCHEDULER_PERIODIC,
          PreesmSynthesisTask.VALUE_SCHEDULER_LEGACY:
        return new SimpleScheduler();
      default:
        PreesmLogger.getLogger().log(Level.SEVERE,
            () -> "This scheduler is not implemented : " + localSchedulerMapperName);
        return null;
    }
  }

  /***
   * Creates a placeholder actor to replace a cluster actor, with the same timing characteristics.
   *
   * @param oldA
   *          clusterActor
   * @param newA
   *          the new placeholder actor
   * @param graph
   *          the application graph
   */
  private void replaceAndRemoveActor(AbstractActor oldA, AbstractActor newA, PiGraph graph) {

    // clone input and output outer interfaces
    // plug fifos and copy rates
    for (final DataInputPort olddip : oldA.getDataInputPorts()) {
      final DataInputPort newdip = PiMMFactory.createDataInputPort(olddip.getName());

      newdip.setExpression(olddip.getExpression());
      newA.getDataInputPorts().add(newdip);
      newdip.setIncomingFifo(olddip.getFifo());
    }
    for (final DataOutputPort olddop : oldA.getDataOutputPorts()) {
      final DataOutputPort newdop = PiMMFactory.createDataOutputPort(olddop.getName());

      newdop.setExpression(olddop.getExpression());
      newA.getDataOutputPorts().add(newdop);
      newdop.setOutgoingFifo(olddop.getFifo());
    }

    for (final ConfigInputPort oldcip : oldA.getConfigInputPorts()) {
      final ConfigInputPort newcip = PiMMFactory.createConfigInputPort();

      newA.getConfigInputPorts().add(newcip);
      newcip.setIncomingDependency(oldcip.getIncomingDependency());
    }

    // remove the old actor from the graph
    graph.removeActorAndDependencies(oldA);

  }

}
