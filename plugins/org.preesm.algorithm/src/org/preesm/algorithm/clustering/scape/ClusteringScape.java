package org.preesm.algorithm.clustering.scape;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.emf.common.util.EMap;
import org.preesm.algorithm.clustering.partitioner.ClusterPartitioner;
import org.preesm.algorithm.clustering.partitioner.ClusterPartitionerLOOP;
import org.preesm.algorithm.clustering.partitioner.ClusterPartitionerSEQ;
import org.preesm.algorithm.clustering.partitioner.ClusterPartitionerSRV;
import org.preesm.algorithm.clustering.partitioner.ClusterPartitionerURC;
import org.preesm.algorithm.codegen.idl.Prototype;
import org.preesm.algorithm.schedule.model.ScapeSchedule;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.ClusteringPatternSeekerLoop;
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioFactory;
import org.preesm.model.scenario.Timings;
import org.preesm.model.scenario.util.ScenarioUserFactory;
import org.preesm.model.slam.CPU;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.GPU;
import org.preesm.model.slam.TimingType;
import org.preesm.model.slam.check.SlamDesignPEtypeChecker;

/**
 * This class apply the Scaling up of Cluster of Actors on Processing Element (SCAPE) transformation. Supported
 * processing elements are x86 CPU cores and cuda GPU kernels.
 *
 * @author orenaud
 * @author emichel
 *
 */
public class ClusteringScape extends ClusterPartitioner {

  /**
   * Size of the stack.
   */
  private final Long stackSize;
  /**
   * Number of hierarchical level.
   */
  private final int  levelNumber;

  int                              clusterIndex   = -1;     // topological index
  private int                      clusterId      = 0;      // index cluster created
  private Map<Long, List<PiGraph>> hierarchicalLevelOrdered;
  private Long                     fulcrumLevelID = 0L;

  private Long                   coreEquivalent = 1L;
  private final Map<Actor, Long> clusterMemory;

  public ClusteringScape(Scenario scenario, Long stackSize, int mode, int levelNumber) {
    this.graph = scenario.getAlgorithm();
    this.scenario = scenario;
    this.archi = scenario.getDesign();
    this.stackSize = stackSize;
    this.mode = mode;
    this.levelNumber = levelNumber;
    this.hierarchicalLevelOrdered = new HashMap<>();
    this.coreEquivalent = EuclideTransfo.computeSingleNodeCoreEquivalent(scenario);
    this.clusterMemory = new LinkedHashMap<>();
  }

  public Scenario execute() {
    // brings RV down to the lowest level
    initHierarchy();

    scenario.setAlgorithm(graph);
    final PiGraph euclide = new EuclideTransfo(scenario).execute();
    scenario.setAlgorithm(euclide);

    // construct hierarchical structure
    hierarchicalLevelOrdered = HierarchicalRoute.fillHierarchicalStructure(graph);

    // compute cluster-able level ID
    fulcrumLevelID = HierarchicalRoute.computeClusterableLevel(graph, scapeMode, levelNumber, hierarchicalLevelOrdered);

    // Coarse clustering while cluster-able level are not reached
    coarseCluster();
    // Pattern identification
    patternIDs();
    // remove the temporary single sources that helped optimization algorithms
    final PiGraph multiBranch = new MultiBranch(graph).removeInitialSource();
    scenario.setAlgorithm(multiBranch);
    // check consistency
    final Map<AbstractVertex, Long> brv = PiBRV.compute(graph, BRVMethod.LCM);
    PiBRV.printRV(brv);
    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ANALYSIS,
        CheckerErrorLevel.NONE);
    pgcc.check(graph);
    scenarioUpdate();
    return scenario;
  }

  private void scenarioUpdate() {
    // TODO: refactor this method

    // remove all actors to force mapping of each to the appropriate architecture type
    scenario.getConstraints().getGroupConstraints().forEach(gp -> gp.getValue().clear());

    // compute CPU-GPU timing
    final Timings time = scenario.getTimings();

    final EMap<AbstractActor, EMap<Component, EMap<TimingType, String>>> map = time.getActorTimings();

    int timingOnCPU = 0;
    int timingOnGPU = 0;

    for (final Entry<AbstractActor, EMap<Component, EMap<TimingType, String>>> actorEntry : map.entrySet()) {
      final AbstractActor actor = actorEntry.getKey();
      final EMap<Component, EMap<TimingType, String>> componentMap = actorEntry.getValue();

      for (final Entry<Component, EMap<TimingType, String>> componentEntry : componentMap.entrySet()) {
        final Component component = componentEntry.getKey();
        final EMap<TimingType, String> timingMap = componentEntry.getValue();

        for (final Entry<TimingType, String> timingEntry : timingMap.entrySet()) {
          final TimingType timingType = timingEntry.getKey();
          final String timingValue = timingEntry.getValue();

          if (component.toString().contains("CPU")) {
            if (actor.getName().contains("srv") || actor.getName().contains("urc")) {
              timingOnCPU += Integer.parseInt(timingValue);
            }
          } else if (component.toString().contains("GPU")) {
            if (actor.getName().contains("srv") || actor.getName().contains("urc")) {
              timingOnGPU += Integer.parseInt(timingValue);
            }
          }
        }
      }
    }
    // map according smart condition
    if (SlamDesignPEtypeChecker.isOnlyCPU(scenario.getDesign())
        || (SlamDesignPEtypeChecker.isDualCPUGPU(scenario.getDesign()) && timingOnGPU > timingOnCPU)) {
      scenario.getConstraints().getGroupConstraints().forEach(groupConstraint -> scenario.getAlgorithm().getAllActors()
          .forEach(actor -> groupConstraint.getValue().add(actor)));

    } else if (SlamDesignPEtypeChecker.isDualCPUGPU(scenario.getDesign()) && timingOnGPU < timingOnCPU) {
      scenario.getConstraints().getGroupConstraints()
          .forEach(gp -> scenario.getAlgorithm().getAllActors().stream()
              .filter(actor -> (gp.getKey().getComponent() instanceof GPU
                  && (actor.getName().contains("srv") || actor.getName().contains("urc")))
                  || (gp.getKey().getComponent() instanceof CPU
                      && !(actor.getName().contains("srv") || actor.getName().contains("urc"))))
              .forEach(gp.getValue()::add));
    }

  }

  /**
   * Sets all subgraphs to 1, with the exception of semi-unrollable cycles. This increases clustering opportunities.
   */
  private void initHierarchy() {
    final List<AbstractActor> graphSingleLOOPs = new ClusteringPatternSeekerLoop(graph).singleLocalseek();
    for (final PiGraph subGraph : graph.getAllChildrenGraphs()) {
      final Map<AbstractVertex, Long> brv = PiBRV.compute(graph, BRVMethod.LCM);
      if (brv.get(subGraph) != 1 && !graphSingleLOOPs.contains(subGraph)) {
        // apply scaling
        final Long scale = brv.get(subGraph);
        for (final DataInputInterface din : subGraph.getDataInputInterfaces()) {
          din.getGraphPort().setExpression(din.getGraphPort().getExpression().evaluate() * scale);
          din.getDataPort().setExpression(din.getGraphPort().getExpression().evaluate());
        }
        for (final DataOutputInterface dout : subGraph.getDataOutputInterfaces()) {
          dout.getGraphPort().setExpression(dout.getGraphPort().getExpression().evaluate() * scale);
          dout.getDataPort().setExpression(dout.getGraphPort().getExpression().evaluate());
        }
      }
    }
  }

  public Map<Actor, Long> getClusterMemory() {
    return clusterMemory;
  }

  /**
   * Enable specific cluster on specific hierarchical level according to the chosen SCAPE mode
   */
  private void patternIDs() {

    switch (scapeMode) {
      case DATA -> executeMode0();
      case DATA_PIPELINE -> executeMode1();
      case DATA_PIPELINE_HIERARCHY -> executeMode2();
      default -> throw new PreesmRuntimeException("Unrecognized Scape mode.");

    }

    if (mode == 1) {
      executeMode1();

    }
    if (mode == 2) {
      executeMode2();

    }
  }

  /**
   * The second extension of the SCAPE method takes into account the hierarchical context when choosing the clustering
   * approach. This allows for the adaptation of parallelism or the coarsening of identified hierarchical levels based
   * on the context.
   *
   */
  private void executeMode2() {
    for (Long i = fulcrumLevelID - 1; i >= 0L; i--) {
      for (final PiGraph g : hierarchicalLevelOrdered.get(i)) {
        PiGraph newCluster = null;
        boolean isHasCluster = true;
        do {
          final int size = graph.getAllChildrenGraphs().size();
          final Map<AbstractVertex, Long> rv = PiBRV.compute(g, BRVMethod.LCM);
          newCluster = new ClusterPartitionerLOOP(g, scenario, coreEquivalent.intValue(), rv, clusterId).cluster();
          if (graph.getAllChildrenGraphs().size() == size) {
            // URC transfo
            newCluster = new ClusterPartitionerURC(scenario, coreEquivalent.intValue(), rv, clusterId, mode).cluster();
          }
          if (graph.getAllChildrenGraphs().size() == size) {
            // SRV transfo
            newCluster = new ClusterPartitionerSRV(scenario, coreEquivalent.intValue(), rv, clusterId, mode).cluster();
          }
          if (graph.getAllChildrenGraphs().size() == size) {
            // SEQ transfo
            newCluster = new ClusterPartitionerSEQ(g, scenario, coreEquivalent.intValue()).cluster();
          }
          if (graph.getAllChildrenGraphs().size() == size) {
            isHasCluster = false;
          }
          if (!newCluster.getChildrenGraphs().isEmpty()) {
            final Long mem = mem(newCluster.getChildrenGraphs().get(0));
            final String clusterName = newCluster.getChildrenGraphs().get(0).getName();
            cluster(newCluster.getChildrenGraphs().get(0), scenario, stackSize);
            clusterMemory.put(findCluster(clusterName), mem);
            clusterId++;
          }
        } while (isHasCluster);
      }
    }
  }

  /**
   * The first extension introduces two additional patterns to the original SCAPE method: LOOP for cycles and SEQ for
   * sequential parts. This extended method retains its parameterised nature, with the aim of reducing data parallelism
   * and enhancing pipeline parallelism to align with the intended target.
   *
   */
  private void executeMode1() {
    for (final PiGraph piGraph : hierarchicalLevelOrdered.get(fulcrumLevelID - 1)) {
      PiGraph newCluster = null;
      boolean isHasCluster = true;
      do {
        final int size = graph.getAllChildrenGraphs().size();
        final Map<AbstractVertex, Long> rv = PiBRV.compute(piGraph, BRVMethod.LCM);
        newCluster = new ClusterPartitionerLOOP(piGraph, scenario, coreEquivalent.intValue(), rv, clusterId).cluster();
        if (graph.getAllChildrenGraphs().size() == size) {
          // URC transfo
          newCluster = new ClusterPartitionerURC(scenario, coreEquivalent.intValue(), rv, clusterId, mode).cluster();
        }
        if (graph.getAllChildrenGraphs().size() == size) {
          // SRV transfo
          newCluster = new ClusterPartitionerSRV(scenario, coreEquivalent.intValue(), rv, clusterId, mode).cluster();
        }
        if (graph.getAllChildrenGraphs().size() == size) {
          // SEQ transfo
          newCluster = new ClusterPartitionerSEQ(piGraph, scenario, coreEquivalent.intValue()).cluster();
        }
        if (graph.getAllChildrenGraphs().size() == size) {
          isHasCluster = false;
        }
        if (!newCluster.getChildrenGraphs().isEmpty()) {
          final int newSize = graph.getAllChildrenGraphs().size();
          for (int i = 0; i < (newSize - size); i++) {
            final Long mem = mem(newCluster.getChildrenGraphs().get(0));
            final String clusterName = newCluster.getChildrenGraphs().get(0).getName();
            cluster(newCluster.getChildrenGraphs().get(0), scenario, stackSize);
            clusterMemory.put(findCluster(clusterName), mem);
          }
          clusterId++;
        }
      } while (isHasCluster);
    }
  }

  /**
   * The original SCAPE method only takes into account two patterns for clustering, which are Actor Unique Repetition
   * Count (URC) and Single Repetition Vector (SRV). This method is parameterised, meaning it accepts a number as a
   * parameter that corresponds to the number of hierarchical levels to be coarsely clustered. The clustering occurs at
   * this specified level, which implies that there can be as many clustering configurations as there are hierarchical
   * levels in the input graph. The goal is two reduce the data parallelism to the target.
   */
  private void executeMode0() {
    final Long fulcrumLevel = fulcrumLevelID - 1;
    if (hierarchicalLevelOrdered.containsKey(fulcrumLevel)) {
      for (final PiGraph piGraph : hierarchicalLevelOrdered.get(fulcrumLevel)) {
        PiGraph newGraph = null;
        boolean isHasCluster = true;
        while (isHasCluster) {
          final int size = graph.getAllChildrenGraphs().size();

          newGraph = applyClusterPartitionersData(piGraph);

          if (graph.getAllChildrenGraphs().size() == size) {
            isHasCluster = false;
          }
          if (!newGraph.getChildrenGraphs().isEmpty()) {
            final Long mem = mem(newGraph.getChildrenGraphs().get(0));
            final String clusterName = newGraph.getChildrenGraphs().get(0).getName();
            cluster(newGraph.getChildrenGraphs().get(0), scenario, stackSize);
            clusterMemory.put(findCluster(clusterName), mem);
            clusterId++;
          }
        }
      }
    }
    topCluster();
  }

  /**
   * SCAPE 0 has a not clever hierarchical management i.e. the identification of the clusters is done on a Parameterized
   * hierarchical level i.e. if you want to roughly group all the graph the parameter must be greater than the number of
   * hierarchical levels in the graph here the function generates a sub-graph of the top hierarchical level once all the
   * children have been grouped.
   */
  private void topCluster() {
    if (levelNumber > hierarchicalLevelOrdered.size()) {

      final List<AbstractActor> graphTOPs = graph.getActors();
      final PiGraph subGraph = new PiSDFSubgraphBuilder(this.graph, graphTOPs, "coarse_" + clusterId).build();
      final Long mem = mem(subGraph);
      final String clusterName = subGraph.getName();
      cluster(subGraph, scenario, stackSize);
      clusterMemory.put(findCluster(clusterName), mem);
    }

  }

  /**
   * The function retrieve the clustered original actor based on name matching in order return clustered memory for
   * later analysis
   *
   * @param clusterName
   *          name of the cluster
   * @return original actor
   */
  private Actor findCluster(String clusterName) {
    for (final AbstractActor a : graph.getAllExecutableActors()) {
      if (a.getName().equals(clusterName)) {
        return (Actor) a;
      }
    }
    return null;
  }

  /**
   *
   * The first extension introduces two additional patterns to the original SCAPE method: LOOP for cycles and SEQ for
   * sequential parts. This extended method retains its parameterised nature, with the aim of reducing data parallelism
   * and enhancing pipeline parallelism to align with the intended target.
   *
   */
  private void executeMode1() {
    for (final PiGraph piGraph : hierarchicalLevelOrdered.get(fulcrumLevelID - 1)) {
      PiGraph newCluster = null;
      boolean isHasCluster = true;

      while (isHasCluster) {

        final int size = graph.getAllChildrenGraphs().size();

        newCluster = applyClusterPartitioners(piGraph);

        if (graph.getAllChildrenGraphs().size() == size) {
          isHasCluster = false;
        }
        if (!newCluster.getChildrenGraphs().isEmpty()) {
          final int newSize = graph.getAllChildrenGraphs().size();
          for (int i = 0; i < (newSize - size); i++) {
            final Long mem = mem(newCluster.getChildrenGraphs().get(0));
            final String clusterName = newCluster.getChildrenGraphs().get(0).getName();
            cluster(newCluster.getChildrenGraphs().get(0), scenario, stackSize);
            clusterMemory.put(findCluster(clusterName), mem);
          }
          clusterId++;
        }
      }
    }
  }

  /**
   * The second extension of the SCAPE method takes into account the hierarchical context when choosing the clustering
   * approach. This allows for the adaptation of parallelism or the coarsening of identified hierarchical levels based
   * on the context.
   *
   */
  private void executeMode2() {
    for (Long i = fulcrumLevelID; i >= 0L; i--) {
      for (final PiGraph piGraph : hierarchicalLevelOrdered.get(i)) {
        PiGraph newGraph = null;
        boolean isHasCluster = true;
        while (isHasCluster) {

          final int size = graph.getAllChildrenGraphs().size();

          newGraph = applyClusterPartitioners(piGraph);

          if (graph.getAllChildrenGraphs().size() == size) {
            isHasCluster = false;
          }
          if (!newGraph.getChildrenGraphs().isEmpty() && isHasCluster) {
            final int lastChildrenGraphCreated = newGraph.getChildrenGraphs().size() - 1;
            final Long mem = mem(newGraph.getChildrenGraphs().get(lastChildrenGraphCreated));
            final String clusterName = newGraph.getChildrenGraphs().get(lastChildrenGraphCreated).getName();
            cluster(newGraph.getChildrenGraphs().get(lastChildrenGraphCreated), scenario, stackSize);
            clusterMemory.put(findCluster(clusterName), mem);
            clusterId++;
          }
        }
      }
    }
  }

  private PiGraph applyClusterPartitionersData(PiGraph piGraph) {
    PiGraph newCluster;

    final int size = graph.getAllChildrenGraphs().size();
    final Map<AbstractVertex, Long> rv = PiBRV.compute(piGraph, BRVMethod.LCM);

    // URC transfo
    newCluster = new ClusterPartitionerURC(piGraph, scenario, coreEquivalent.intValue(), rv, clusterId, scapeMode)
        .cluster();

    if (graph.getAllChildrenGraphs().size() == size) {
      // SRV transfo
      newCluster = new ClusterPartitionerSRV(piGraph, scenario, coreEquivalent.intValue(), rv, clusterId, scapeMode)
          .cluster();
    }
    return newCluster;
  }

  private PiGraph applyClusterPartitioners(PiGraph piGraph) {

    PiGraph newCluster;

    final int size = graph.getAllChildrenGraphs().size();
    final Map<AbstractVertex, Long> rv = PiBRV.compute(piGraph, BRVMethod.LCM);

    newCluster = new ClusterPartitionerLOOP(piGraph, scenario, coreEquivalent.intValue(), rv, clusterId).cluster();

    if (graph.getAllChildrenGraphs().size() == size) {
      // URC transfo
      newCluster = new ClusterPartitionerURC(piGraph, scenario, coreEquivalent.intValue(), rv, clusterId, scapeMode)
          .cluster();
    }

    if (graph.getAllChildrenGraphs().size() == size) {
      // SRV transfo
      newCluster = new ClusterPartitionerSRV(piGraph, scenario, coreEquivalent.intValue(), rv, clusterId, scapeMode)
          .cluster();
    }

    if (graph.getAllChildrenGraphs().size() == size) {
      // SEQ transfo
      newCluster = new ClusterPartitionerSEQ(piGraph, scenario, coreEquivalent.intValue()).cluster();
    }

    return newCluster;
  }

  /**
   * Coarsely cluster the levels below the fulcrumLevel (bound). Hierarchical levels are ordered in ascending order Top
   * = 0, below =n++. The fulcrum level ID indicates what is to be coarsely cluster and identified intelligently: bottom
   * level = coarse nothing, 0 level = coarse everything
   */
  private void coarseCluster() {
    final Long totalLevelNumber = (long) hierarchicalLevelOrdered.size() - 1;
    if (fulcrumLevelID < totalLevelNumber) {

      for (Long i = totalLevelNumber; i > fulcrumLevelID; i--) {
        for (final PiGraph g : hierarchicalLevelOrdered.get(i)) {
          final Long mem = mem(g);
          final String clusterName = g.getName();
          cluster(g, scenario, stackSize);
          clusterMemory.put(findCluster(clusterName), mem);
        }
      }
    }
  }

  private Long mem(PiGraph g) {
    Long mem = 0L;
    for (final AbstractActor a : g.getExecutableActors()) {
      for (final DataOutputPort out : a.getDataOutputPorts()) {
        if (!(out.getFifo().getTarget() instanceof DataOutputInterface)) {
          final Long typeInBit = scenario.getSimulationInfo().getDataTypeSizeInBit(out.getFifo().getType());
          mem += out.getExpression().evaluate() * typeInBit;
        }
      }

    }
    return mem;
  }

  /**
   * Transform a subgraph into a standard actor
   *
   * @param g
   *          The identify clustered subgraph
   */
  public static void cluster(PiGraph g, Scenario scenario, Long stackSize) {

    // add a temporary single source in case of multiple source
    final PiGraph singleBranch = new MultiBranch(g).addInitialSource();
    // compute the cluster schedule
    final List<ScapeSchedule> schedule = new ScheduleScape(singleBranch).execute();
    final Scenario clusterScenario = lastLevelScenario(singleBranch, scenario);
    // retrieve cluster timing
    final Map<AbstractVertex, Long> rv = PiBRV.compute(singleBranch, BRVMethod.LCM);
    final Map<Component, Map<TimingType, String>> sumTiming = clusterTiming(rv, singleBranch, scenario);

    new CodegenScape(clusterScenario, singleBranch, schedule, stackSize);

    replaceBehavior(singleBranch, scenario);
    updateTiming(sumTiming, singleBranch, scenario);
  }

  /**
   * Replace the refinement of the clustered hierarchical actor by a standard Code
   *
   * @param g
   *          The identify clustered subgraph
   */

  private static void replaceBehavior(PiGraph g, Scenario scenario) {

    final PiGraph graph = (PiGraph) g.getContainingGraph();
    final Actor oEmpty = PiMMUserFactory.instance.createActor(g.getName());

    // add refinement
    final Refinement refinement = PiMMUserFactory.instance.createCHeaderRefinement();
    oEmpty.setRefinement(refinement);
    final CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) (oEmpty.getRefinement());
    final Prototype oEmptyPrototype = new Prototype();
    oEmptyPrototype.setIsStandardC(true);
    final String strPath = scenario.getCodegenDirectory();
    final String nameGraph = (((PiGraph) g.getContainingGraph()) != null) ? ((PiGraph) g.getContainingGraph()).getName()
        : g.getName();
    cHeaderRefinement.setFilePath(strPath + "/Cluster_" + nameGraph + "_" + oEmpty.getName() + ".h");
    final FunctionPrototype functionPrototype = PiMMUserFactory.instance.createFunctionPrototype();
    cHeaderRefinement.setLoopPrototype(functionPrototype);
    functionPrototype.setName(oEmpty.getName());

    if (nameGraph.matches("^sub\\d+")) {
      functionPrototype.setName("Cluster_" + ((PiGraph) g.getContainingGraph()).getName() + "_" + oEmpty.getName());
    }
    // fill port
    for (final ConfigInputPort cfg : g.getConfigInputPorts()) {
      final ConfigInputPort cfgInputPort = PiMMUserFactory.instance.copy(cfg);
      oEmpty.getConfigInputPorts().add(cfgInputPort);
      final FunctionArgument functionArgument = PiMMUserFactory.instance.createFunctionArgument();
      functionArgument.setName(cfgInputPort.getName());
      functionArgument.setType("int");

      functionArgument.setIsConfigurationParameter(true);
      functionPrototype.getArguments().add(functionArgument);
    }

    for (final DataInputPort in : g.getDataInputPorts()) {
      final DataInputPort inputPort = PiMMUserFactory.instance.copy(in);
      oEmpty.getDataInputPorts().add(inputPort);
      final FunctionArgument functionArgument = PiMMUserFactory.instance.createFunctionArgument();
      functionArgument.setName(inputPort.getName());
      functionArgument.setType(in.getFifo().getType());
      functionArgument.setDirection(Direction.IN);
      functionPrototype.getArguments().add(functionArgument);
    }

    for (final DataOutputPort out : g.getDataOutputPorts()) {
      final DataOutputPort outputPort = PiMMUserFactory.instance.copy(out);
      oEmpty.getDataOutputPorts().add(outputPort);
      final FunctionArgument functionArgument = PiMMUserFactory.instance.createFunctionArgument();
      functionArgument.setName(outputPort.getName());
      functionArgument.setType(out.getFifo().getType());
      functionArgument.setDirection(Direction.OUT);
      functionPrototype.getArguments().add(functionArgument);
    }

    graph.replaceActor(g, oEmpty);

  }

  /**
   * Used to store timing inside the cluster
   *
   * @param sumTiming
   *          sum of contained actors timing
   * @param lastLevel
   *          PiGraph of the cluster
   */
  private static void updateTiming(Map<Component, Map<TimingType, String>> sumTiming, PiGraph lastLevel,
      Scenario scenario) {
    final PiGraph graph = scenario.getAlgorithm();
    final Design archi = scenario.getDesign();
    AbstractActor aaa = null;
    if (sumTiming != null) {
      for (final AbstractActor aa : graph.getAllActors()) {
        if (lastLevel.getName().equals(aa.getName()) || aa.getName().contains(lastLevel.getName())) {
          aaa = aa;
          // update timing
          for (final Component opId : archi.getProcessingElements()) {
            scenario.getTimings().setTiming(aaa, opId, TimingType.EXECUTION_TIME,
                sumTiming.get(opId).get(TimingType.EXECUTION_TIME));
          }
        }
      }
    }
  }

  /**
   * Used to compute timing inside the cluster
   *
   * @param repetitionVector
   *          list of the cluster repetition vector
   * @param cluster
   *          PiGraph of the cluster
   * @param scenario
   *          contains list of timing
   */
  private static Map<Component, Map<TimingType, String>> clusterTiming(Map<AbstractVertex, Long> repetitionVector,
      PiGraph cluster, Scenario scenario) {
    final Design archi = scenario.getDesign();
    final Map<Component, Map<TimingType, String>> clusterTiming = new HashMap<>();
    // Initialise
    for (final Component opId : archi.getProcessingElements()) {
      final Map<TimingType, String> val = new EnumMap<>(TimingType.class);
      val.put(TimingType.EXECUTION_TIME, String.valueOf(0L));
      clusterTiming.put(opId, val);
    }

    if (scenario.getTimings().getActorTimings().isEmpty()) {
      return clusterTiming; // Early exit
    }

    // Compute the execution time a a cluster running on each architecture type
    for (final Component pe : archi.getProcessingElements()) {
      if (pe instanceof final CPU cpu) {
        clusterTiming.get(pe).replace(TimingType.EXECUTION_TIME,
            String.valueOf(timingCPU(repetitionVector, cluster, scenario, cpu)));
      } else if (pe instanceof final GPU gpu) {
        clusterTiming.get(pe).replace(TimingType.EXECUTION_TIME, String.valueOf(timingGPU(cluster, gpu)));
      }

    }
    PreesmLogger.getLogger().log(Level.INFO,
        "Timing " + cluster.getName() + ": " + clusterTiming.get(archi.getProcessingElements().get(0)));

    return clusterTiming;
  }

  private static Long timingGPU(PiGraph cluster, GPU gpu) {
    // If GPU parameters are different from 0, use their value, otherwise default to 1
    final Long dedicatedMemSpeed = (long) gpu.getDedicatedMemSpeed() != 0 ? (long) gpu.getDedicatedMemSpeed() : 1;
    final Long unifiedMemSpeed = (long) gpu.getUnifiedMemSpeed() != 0 ? (long) gpu.getUnifiedMemSpeed() : 1;
    final String memoryToUse = gpu.getMemoryToUse();

    Long sum = 0L;
    for (final DataPort dataPort : cluster.getAllDataPorts()) {
      final Long gpuInputSize = dataPort.getPortRateExpression().evaluate();
      double time;

      if (memoryToUse.equalsIgnoreCase("unified")) {
        time = ((double) gpuInputSize / (double) unifiedMemSpeed);
      } else {
        time = ((double) gpuInputSize / (double) dedicatedMemSpeed);
      }

      sum += (long) time;
    }

    return sum;
  }

  /**
   * The execution time of a cluster on a CPU processing element is the sum of executable actors only. There is no data
   * transfer within an actor cluster, as it is built sequentially for this type of architecture.
   *
   * @param repetitionVector
   *          list of the cluster repetition vector
   * @param cluster
   *          PiGraph of the cluster
   * @param scenario
   *          contains list of timing
   * @param cpu
   *          CPU-type processing element
   * @return cluster execution time on CPU
   */

  private static Long timingCPU(Map<AbstractVertex, Long> repetitionVector, PiGraph cluster, Scenario scenario,
      CPU cpu) {
    // sum the actor timing contained in the cluster
    Long sum = 0L;
    for (final AbstractActor actor : cluster.getOnlyActors()) {
      if (actor instanceof Actor) {
        final AbstractActor aaa = scenario.getTimings().getActorTimings().keySet().stream()
            .filter(aa -> actor.getName().equals(aa.getName())).findFirst().orElse(null);

        sum += scenario.getTimings().evaluateTimingOrDefault(aaa, cpu, TimingType.EXECUTION_TIME)
            * repetitionVector.get(actor);
      }
    }
    return sum;
  }

  /**
   * Used to create a temporary scenario of the cluster
   *
   * @param subgraph
   *          PiGraph of the cluster
   */
  private static Scenario lastLevelScenario(PiGraph subgraph, Scenario scenario) {
    final Design archi = scenario.getDesign();
    final Scenario clusterScenario = ScenarioUserFactory.createScenario();
    clusterScenario.setCodegenDirectory(scenario.getCodegenDirectory());

    clusterScenario.setAlgorithm(subgraph);
    clusterScenario.setDesign(archi);
    clusterScenario.setTimings(ScenarioFactory.eINSTANCE.createTimings());
    clusterScenario.setEnergyConfig(ScenarioFactory.eINSTANCE.createEnergyConfig());

    final List<ComponentInstance> coreIds = new ArrayList<>(archi.getOperatorComponentInstances());
    // for all different type of cores, allow mapping on it
    for (final ComponentInstance coreId : coreIds) {
      for (final AbstractActor actor : subgraph.getAllActors()) {
        // Add constraint
        clusterScenario.getConstraints().addConstraint(coreId, actor);
      }
    }
    return clusterScenario;
  }

}
