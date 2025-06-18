package org.preesm.algorithm.synthesis;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.clustering.ClusterBuilder;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memory.allocation.tasks.MemoryScriptTask;
import org.preesm.algorithm.schedule.fpga.AdfgOjalgoFpgaFifoEvaluator;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.memalloc.IMemoryAllocation;
import org.preesm.algorithm.synthesis.memalloc.LegacyMemoryAllocation;
import org.preesm.algorithm.synthesis.memalloc.SimpleMemoryAllocation;
import org.preesm.algorithm.synthesis.schedule.algos.ChocoScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.FpgaScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.IScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.LegacyListScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.PeriodicScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.SimpleScheduler;
import org.preesm.algorithm.synthesis.timer.ActorExecutionTiming;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.CPU;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.FPGA;
import org.preesm.model.slam.SlamFactory;
import org.preesm.model.slam.TimingType;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 *
 * @author jmorin
 *
 */
@PreesmTask(id = "heterogeneous-synthesis", name = "Heterogeneous Synthesis", category = "Synthesis",
    shortDescription = "Schedule and map actors on heterogeneous architecture, no allocation, no codegen",
    description = "",

    parameters = {
        @Parameter(name = "scheduler",
            description = "Scheduler used to schedule and map the tasks. NOT WORKING FOR NOW.",
            values = { @Value(name = "simple", effect = "Naive greedy list scheduler."),
                @Value(name = "legacy", effect = "See workflow task pisdf-mapper.list."),
                @Value(name = "periodic",
                    effect = "List scheduler (without communication times) respecting actor or graph periods, if any."),
                @Value(name = "choco",
                    effect = "Optimal scheduler (without communication times) "
                        + "respecting actor or graph periods, if any.") }),
        @Parameter(name = "allocation", description = "Allocate the memory for buffers. NOT WONKING FOR NOW.",
            values = { @Value(name = "simple"), @Value(name = "legacy") }) },

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "architecture", type = Design.class),
        @Port(name = "scenario", type = Scenario.class) },
    outputs = { @Port(name = "Schedule", type = Schedule.class), @Port(name = "Mapping", type = Mapping.class),
        @Port(name = "Allocation", type = Allocation.class), @Port(name = "HPiSDF", type = PiGraph.class) })

public class PreesmHeterogeneousSynthesisTask extends AbstractTaskImplementation {

  public static final String VALUE_ALLOCATORS_SIMPLE = "simple";
  public static final String VALUE_ALLOCATORS_LEGACY = "legacy";

  public static final String VALUE_SCHEDULER_SIMPLE      = "simple";
  public static final String VALUE_SCHEDULER_LEGACY      = "legacy";
  public static final String VALUE_SCHEDULER_PERIODIC    = "periodic";
  public static final String VALUE_SCHEDULER_CHOCO       = "choco";
  public static final String VALUE_SCHEDULER_FPGA_LINEAR = "adfgfifoevalexact";
  public static final String VALUE_SCHEDULER_FPGA_EXACT  = "adfgfifoevallinear";

  final PiMMUserFactory PiMMFactory = org.preesm.model.pisdf.factory.PiMMUserFactory.instance;
  SlamFactory           SLAMFactory = SlamFactory.eINSTANCE;

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final PiGraph original_algorithm = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);

    final PiGraph algorithm = PiMMUserFactory.instance.copyPiGraphWithHistory(original_algorithm);

    // later used to compute the gantt
    final Map<AbstractActor, ActorExecutionTiming> execTimings = new HashMap<>();

    final boolean CLUSTERIZE = parameters.get("clusterize").equals("true");

    if (CLUSTERIZE) {
      // clusterize the graph
      final List<PiGraph> clustersList = ClusterBuilder.buildArchHierarchyGraph(algorithm, scenario);

      // -------------------------------------------------------------------------------------
      // ------------------- locally schedule and map the clusters' graphs -------------------

      final Map<PiGraph, SynthesisResult> localSchedulings = new HashMap<>();

      for (final PiGraph cluster : clustersList) {
        // find the right scheduler-mapper based on the cluster's shared archi : cpu, fpga, cgra...
        final String localSchedulerMapperName = switchSchedulerMapper(cluster, scenario);

        final IScheduler localSchedulerMapper = getSchedulerMapperInstance(localSchedulerMapperName);

        final SynthesisResult res = localSchedulerMapper.scheduleAndMap(cluster, architecture, scenario);
        localSchedulings.put(cluster, res);
      }

      // --------------------------------------------------------------------------------------
      // -------------------- replace hierarchical actors with placeholders -------------------

      // find the main PE
      ComponentInstance mainCPU;
      if (scenario.getSimulationInfo().getMainOperator() instanceof CPU) {
        mainCPU = scenario.getSimulationInfo().getMainOperator();
      } else {
        mainCPU = architecture.getComponentInstances().stream().filter(c -> c.getComponent() instanceof CPU).toList()
            .getFirst();
      }

      // iterate over clusters (PiGraphs for now, maybe something else later to avoid confusion with simple hier.
      // actors)
      for (final PiGraph subGraph : algorithm.getActors().stream().filter(a -> a instanceof PiGraph)
          .map(a -> (PiGraph) a).toList()) {
        final Actor placeholder = PiMMFactory.createActor(subGraph.getName() + "_placeholder");
        placeholder.setRefinement(PiMMFactory.createCHeaderRefinement()); // empty refinement for now

        // set the placeholder's characteristics we need for global scheduling : // - latency/throughput
        int latency = 0;

        // check if it is executed of FPGA. If so we have to find (or fabricate) its latency
        if (scenario.getPossibleMappings(subGraph).stream().anyMatch(ci -> ci.getComponent() instanceof FPGA)) {
          latency = computeFpgaGraphLatency(subGraph);
        } else {
          // On est d'accord que c'est bien la durée d'un firing de l'acteur ?
          latency = localSchedulings.get(subGraph).schedule.getSpan();
        }

        algorithm.addActor(placeholder);
        replaceAndRemoveActor(subGraph, placeholder, algorithm);

        // for now, let's suppose II = Latency
        scenario.getConstraints().addConstraint(mainCPU, placeholder); // test,idéalement ça serait une "non-archi"
        scenario.getTimings().setExecutionTime(placeholder, mainCPU.getComponent(), latency);
        scenario.getTimings().setTiming(placeholder, mainCPU.getComponent(), TimingType.INITIATION_INTERVAL,
            Integer.toString(latency));
      }

    }

    // ------------------ schedule the global graph ------------------
    // partially copied from PreesmSynthesisTask, gradually copy code from there to construct a working task body

    final Map<String, Object> outputs = new LinkedHashMap<>();

    final String schedulerName = parameters.get("scheduler").toLowerCase();

    final IScheduler scheduler = selectScheduler(schedulerName);

    PreesmLogger.getLogger().log(Level.INFO, () -> " -- Scheduling -- " + schedulerName);
    final SynthesisResult scheduleAndMap = scheduler.scheduleAndMap(algorithm, architecture, scenario);

    IMemoryAllocation alloc;
    final Map<String, String> memAllocParams = new HashMap<>();
    if (parameters.containsKey(MemoryScriptTask.PARAM_LOG)) {
      memAllocParams.put(MemoryScriptTask.PARAM_LOG, parameters.get(MemoryScriptTask.PARAM_LOG));
      alloc = new LegacyMemoryAllocation(memAllocParams);
    } else {
      alloc = new LegacyMemoryAllocation();
    }
    final Allocation memalloc = alloc.allocateMemory(algorithm, architecture, scenario, scheduleAndMap.schedule,
        scheduleAndMap.mapping);

    outputs.put("Schedule", scheduleAndMap.schedule);
    outputs.put("Mapping", scheduleAndMap.mapping);
    outputs.put("Allocation", memalloc);
    outputs.put("HPiSDF", algorithm);

    return outputs;

  }

  private IScheduler selectScheduler(final String schedulerName) {
    return switch (schedulerName) {
      case VALUE_SCHEDULER_SIMPLE -> new SimpleScheduler();
      case VALUE_SCHEDULER_LEGACY -> new LegacyListScheduler();
      case VALUE_SCHEDULER_PERIODIC -> new PeriodicScheduler();
      case VALUE_SCHEDULER_CHOCO -> new ChocoScheduler();
      case VALUE_SCHEDULER_FPGA_LINEAR -> new FpgaScheduler(VALUE_SCHEDULER_FPGA_LINEAR);
      case VALUE_SCHEDULER_FPGA_EXACT -> new FpgaScheduler(VALUE_SCHEDULER_FPGA_EXACT);
      default -> throw new PreesmRuntimeException("unknown scheduler: " + schedulerName);
    };
  }

  private IMemoryAllocation selectAllocation(final String allocationName) {
    return switch (allocationName) {
      case VALUE_ALLOCATORS_SIMPLE -> new SimpleMemoryAllocation();
      case VALUE_ALLOCATORS_LEGACY -> new LegacyMemoryAllocation();
      default -> throw new PreesmRuntimeException("unknown allocation: " + allocationName);
    };
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
   * Selects the adapted scheduler for a given cluster based on its mapping arch. public or private, I don't care
   *
   * @param cluster
   *          the cluster to map
   * @param scenario
   *          the scenario
   * @return the selected mapper's string name/identifier
   **/
  private String switchSchedulerMapper(AbstractActor cluster, Scenario scenario) {
    final List<ComponentInstance> mappings = scenario.getPossibleMappings(cluster);
    // Pour le moment, je vais supposer qu'un cluster est mappé à une seule archi. Cela correspond à l'idée que le
    // mapping "niveau archi" est fait entièrement lors de la phase de clustering, qui décide quel cluster est fait sur
    // quel type de PE (ex : tel acteur va sur fpga, mais ne choisit pas quelle fpga).

    final ComponentInstance arch = mappings.getFirst();

    // TODO find a way to have different choices for a type of archi. Ex : for fpga, exact or linear.
    return switch (arch.getComponent()) {
      case final CPU cpu -> PreesmSynthesisTask.VALUE_SCHEDULER_LEGACY;
      case final FPGA fpga -> PreesmSynthesisTask.VALUE_SCHEDULER_FPGA_LINEAR;
      default -> throw new PreesmSynthesisException("No mapper available for component " + arch.getInstanceName());
    };

  }

  /***
   * Returns the corresponding scheduler-mapper instance based on its name. public or private, I don't care
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
   * Creates a placeholder actor to replace a cluster actor, with the same timing characteristics. public or private, I
   * don't care
   *
   * @param oldA
   *          clusterActor
   * @param newA
   *          the new placeholder actor
   * @param graph
   *          the application graph
   */
  private void replaceAndRemoveActor(AbstractActor oldA, AbstractActor newA, PiGraph graph) {

    // TODO brancher les dépendances dans le placeholder
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
      // create a new dependency that will be plugged to a new config port
      final ConfigInputPort newcip = PiMMFactory.createConfigInputPort();
      newcip.setName(oldcip.getName());

      final Dependency newDep = PiMMFactory.createDependency(oldcip.getIncomingDependency().getSetter(), newcip);

      newA.getConfigInputPorts().add(newcip);
      graph.addDependency(newDep);
    }

    // remove the old cluster actor from the graph
    graph.removeActorAndDependencies(oldA);

  }

  /***
   * computes, or at least approximates/overestimates, an FPGA graph's latency. For now it just returns 42.
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
