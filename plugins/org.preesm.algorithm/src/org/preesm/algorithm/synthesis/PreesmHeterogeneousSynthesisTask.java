package org.preesm.algorithm.synthesis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.clustering.ClusterBuilder;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.algorithm.schedule.fpga.AdfgOjalgoFpgaFifoEvaluator;
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
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.FPGA;
import org.preesm.model.slam.SlamFactory;
import org.preesm.model.slam.TimingType;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

public class PreesmHeterogeneousSynthesisTask extends AbstractTaskImplementation {

  final PiMMUserFactory PiMMFactory = org.preesm.model.pisdf.factory.PiMMUserFactory.instance;
  SlamFactory           SLAMFactory = SlamFactory.eINSTANCE;

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final PiGraph algorithm = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);

    // clusterize the graph
    final List<PiGraph> clustersList = ClusterBuilder.buildArchHierarchyGraph(algorithm, scenario);

    // ------------------ locally schedule and map the clusters' graphs ------------------

    final Map<PiGraph, SynthesisResult> localSchedulings = new HashMap<>();

    for (final PiGraph cluster : clustersList) {
      // find the right scheduler-mapper based on the cluster's shared archi : cpu, fpga, cgra...
      final String localSchedulerMapperName = switchSchedulerMapper(cluster, scenario);

      final IScheduler localSchedulerMapper = getSchedulerMapperInstance(localSchedulerMapperName);

      final SynthesisResult res = localSchedulerMapper.scheduleAndMap(cluster, architecture, scenario);
      localSchedulings.put(cluster, res);
    }

    // ------------------ replace hierarchical actors with placeholders ------------------

    // find any cpu in scenario, whatever
    final var anyCPU = architecture.getComponentInstances().stream().filter(c -> c.getComponent() instanceof CPU)
        .toList().getFirst();

    for (final PiGraph subGraph : algorithm.getActors().stream().filter(a -> a instanceof PiGraph).map(a -> (PiGraph) a)
        .toList()) {
      final Actor placeholder = PiMMFactory.createActor(subGraph.getName() + "_placeholder");
      placeholder.setRefinement(PiMMFactory.createCHeaderRefinement()); // empty refinement for now

      // set the placeholder's characteristics we need for global scheduling :
      // - latency/throughput
      int latency = 0;
      // check if it is executed of FPGA. If so we have to find (or fabricate) its latency
      if (ClusteringHelper.getMappings(subGraph, scenario).stream().anyMatch(ci -> ci.getComponent() instanceof FPGA)) {
        latency = computeFpgaGraphLatency(subGraph);
      }

      algorithm.addActor(placeholder);
      replaceAndRemoveActor(subGraph, placeholder, algorithm);

      // for now, let's suppose II = Latency
      final Component component = ClusteringHelper.getMappings(subGraph, scenario).getFirst().getComponent();
      scenario.getTimings().setExecutionTime(placeholder, component, 10);
      scenario.getTimings().setTiming(placeholder, component, TimingType.INITIATION_INTERVAL, "10");
      scenario.getConstraints().addConstraint(anyCPU, placeholder); // test, idéalement ça serait une "non-archi"

    }

    // nouvelle api
    final var synthesis = new PreesmSynthesisTask();

    parameters.put("scheduler", PreesmSynthesisTask.VALUE_SCHEDULER_SIMPLE);
    parameters.put("allocation", PreesmSynthesisTask.VALUE_ALLOCATORS_SIMPLE);

    final Map<String, Object> results = synthesis.execute(inputs, parameters, monitor, nodeName, workflow);

    return results;

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
    final List<ComponentInstance> mappings = ClusteringHelper.getMappings(cluster, scenario);
    // Pour le moment, je vais supposer qu'un cluster est mappé à une seule archi. Cela correspond à l'idée que le
    // mapping "niveau archi" est fait entièrement lors de la phase de clustering, qui décide quel cluster est fait sur
    // quel type de PE (ex : tel acteur va sur fpga, mais ne choisit pas quelle fpga).

    final ComponentInstance arch = mappings.getFirst();

    if (arch.getComponent() instanceof CPU) {
      return PreesmSynthesisTask.VALUE_SCHEDULER_SIMPLE; // temporaire
    }

    if (arch.getComponent() instanceof FPGA) {
      return AdfgOjalgoFpgaFifoEvaluator.FIFO_EVALUATOR_ADFG_DEFAULT_LINEAR; // temporaire
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

  /***
   * computes, or at least approximates/overestimates, an FPGA graph's latency
   *
   * @param graph
   *          the graph
   * @return the latency
   */
  private int computeFpgaGraphLatency(PiGraph graph) {
    // TODO actually code it
    return 42;
  }

}
