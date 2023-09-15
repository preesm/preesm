package org.preesm.algorithm.clustering.scape;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.preesm.algorithm.clustering.partitioner.ClusterPartitionerLOOP;
import org.preesm.algorithm.clustering.partitioner.ClusterPartitionerSEQ;
import org.preesm.algorithm.clustering.partitioner.ClusterPartitionerSRV;
import org.preesm.algorithm.clustering.partitioner.ClusterPartitionerURC;
import org.preesm.algorithm.codegen.idl.Prototype;
import org.preesm.algorithm.schedule.model.ScapeSchedule;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.LOOPSeeker;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioFactory;
import org.preesm.model.scenario.util.ScenarioUserFactory;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.TimingType;

public class ClusteringScape {
  /**
   * Input graph.
   */
  private final PiGraph  graph;
  /**
   * Workflow scenario.
   */
  private final Scenario scenario;
  /**
   * Architecture design.
   */
  private final Design   archi;
  /**
   * Size of the stack.
   */
  private final Long     stackSize;
  /**
   * Number of hierarchical level.
   */
  private final int      levelNumber;
  /**
   * SCAPE mode : 1 (...); 2 (...); 3 (...).
   */
  private final int      mode;

  /**
   * Store the non cluster-able actor chosen manually by the operator.
   */
  private final List<AbstractActor> nonClusterableList;

  int         clusterIndex = -1;// topological index
  private int clusterId    = 0; // index cluster created

  private final Map<Long, List<PiGraph>> hierarchicalLevelOrdered;
  private Long                           totalLevelNumber = 0L;
  private Long                           levelBound       = 0L;
  private Long                           coreEquivalent   = 1L;

  public ClusteringScape(Scenario scenario, Long stackSize, int mode, int levelNumber,
      List<AbstractActor> nonClusterableList) {
    this.graph = scenario.getAlgorithm();
    this.scenario = scenario;
    this.archi = scenario.getDesign();
    this.stackSize = stackSize;
    this.mode = mode;
    this.levelNumber = levelNumber;
    this.nonClusterableList = nonClusterableList;
    this.hierarchicalLevelOrdered = new HashMap<>();
    this.coreEquivalent = new EuclideTransfo(scenario).computeSingleNodeCoreEquivalent();
  }

  public PiGraph execute() {

    final PiGraph euclide = new EuclideTransfo(scenario).execute();
    scenario.setAlgorithm(euclide);
    // construct hierarchical structure
    fillHierarchicalStrcuture();
    // compute cluster-able level ID
    computeClusterableLevel();
    // Coarse clustering while cluster-able level are not reached
    coarseCluster();
    // Pattern identification
    patternIDs();

    final Map<AbstractVertex, Long> brv = PiBRV.compute(graph, BRVMethod.LCM);
    PiBRV.printRV(brv);
    return graph;

  }

  /**
   * Enable specific cluster on specific hierarchical level according to the chosen SCAPE mode
   */
  private void patternIDs() {

    if (mode == 0) {
      executeMode0();

    }
    if (mode == 1) {
      executeMode1();

    }
    if (mode == 2) {
      executeMode2();

    }
  }

  private void executeMode2() {
    for (Long i = levelBound; i >= 0L; i--) {
      for (final PiGraph g : hierarchicalLevelOrdered.get(i)) {
        PiGraph newCluster = null;
        boolean isHasCluster = true;
        do {
          final int size = graph.getAllChildrenGraphs().size();
          final Map<AbstractVertex, Long> rv = PiBRV.compute(g, BRVMethod.LCM);
          newCluster = new ClusterPartitionerLOOP(g, scenario).cluster();// LOOP2 transfo
          if (graph.getAllChildrenGraphs().size() == size) {
            newCluster = new ClusterPartitionerURC(g, scenario, coreEquivalent.intValue(), rv, clusterId,
                nonClusterableList).cluster();// URC transfo
          }
          if (graph.getAllChildrenGraphs().size() == size) {
            newCluster = new ClusterPartitionerSRV(g, scenario, coreEquivalent.intValue(), rv, clusterId,
                nonClusterableList).cluster();// SRV transfo
          }
          if (graph.getAllChildrenGraphs().size() == size) {
            newCluster = new ClusterPartitionerSEQ(g, scenario, coreEquivalent.intValue(), rv, clusterId,
                nonClusterableList, archi).cluster();// SEQ transfo
          }
          if (graph.getAllChildrenGraphs().size() == size) {
            isHasCluster = false;
          }
          if (!newCluster.getChildrenGraphs().isEmpty()) {
            cluster(newCluster.getChildrenGraphs().get(0));
            clusterId++;
          }
        } while (isHasCluster);
      }
    }
  }

  private void executeMode1() {
    for (final PiGraph g : hierarchicalLevelOrdered.get(levelBound)) {
      PiGraph newCluster = null;
      do {
        final int size = graph.getAllChildrenGraphs().size();
        final Map<AbstractVertex, Long> rv = PiBRV.compute(g, BRVMethod.LCM);
        newCluster = new ClusterPartitionerLOOP(g, scenario).cluster();// LOOP2 transfo
        if (graph.getAllChildrenGraphs().size() == size) {
          newCluster = new ClusterPartitionerURC(g, scenario, coreEquivalent.intValue(), rv, clusterId,
              nonClusterableList).cluster();// URC transfo
        }
        if (graph.getAllChildrenGraphs().size() == size) {
          newCluster = new ClusterPartitionerSRV(g, scenario, coreEquivalent.intValue(), rv, clusterId,
              nonClusterableList).cluster();// SRV transfo
        }
        if (graph.getAllChildrenGraphs().size() == size) {
          newCluster = new ClusterPartitionerSEQ(g, scenario, coreEquivalent.intValue(), rv, clusterId,
              nonClusterableList, archi).cluster();// SEQ transfo
        }
        cluster(newCluster.getChildrenGraphs().get(0));
      } while (newCluster.getChildrenGraphs() != null);
    }
  }

  private void executeMode0() {
    for (final PiGraph g : hierarchicalLevelOrdered.get(levelBound)) {
      PiGraph newCluster = null;
      do {
        final int size = graph.getAllChildrenGraphs().size();
        final Map<AbstractVertex, Long> rv = PiBRV.compute(g, BRVMethod.LCM);
        newCluster = new ClusterPartitionerURC(g, scenario, coreEquivalent.intValue(), rv, clusterId,
            nonClusterableList).cluster();// URC transfo
        if (graph.getAllChildrenGraphs().size() == size) {
          newCluster = new ClusterPartitionerSRV(g, scenario, coreEquivalent.intValue(), rv, clusterId,
              nonClusterableList).cluster();// SRV transfo
        }
        cluster(newCluster.getChildrenGraphs().get(0));
      } while (newCluster.getChildrenGraphs() != null);
    }
  }

  /**
   * Coarsely cluster identified hierarchical level that require it
   */
  private void coarseCluster() {
    for (Long i = totalLevelNumber; i > levelBound; i--) {
      for (final PiGraph g : hierarchicalLevelOrdered.get(i)) {
        cluster(g);
      }
    }
  }

  /**
   * Compute the hierarchical level to be coarsely clustered and identify hierarchical level to be cleverly clustered
   */
  private void computeClusterableLevel() {
    if (mode == 0 || mode == 1) {
      levelBound = (long) levelNumber;

    } else {
      Long count = 0L;
      // detect the highest delay
      for (final Fifo fd : graph.getFifosWithDelay()) {
        // detect loop --> no pipeline and contains hierarchical graph
        final List<AbstractActor> graphLOOPs = new LOOPSeeker(fd.getContainingPiGraph()).seek();
        if (!graphLOOPs.isEmpty() && graphLOOPs.stream().anyMatch(PiGraph.class::isInstance)) {
          // compute high
          for (Long i = 0L; i < totalLevelNumber; i++) {
            if (hierarchicalLevelOrdered.get(i).contains(fd.getContainingPiGraph())) {
              count = Math.max(count, i);
            }
          }
        }
      }
      levelBound = count;
    }

  }

  /**
   * Order the hierarchical subgraph in order to compute cluster in the bottom up way
   */
  private void fillHierarchicalStrcuture() {

    for (final PiGraph g : graph.getAllChildrenGraphs()) {
      Long count = 0L;
      PiGraph tempg = g;
      while (tempg.getContainingPiGraph() != null) {
        tempg = tempg.getContainingPiGraph();
        count++;
      }
      final List<PiGraph> list = new ArrayList<>();
      list.add(g);
      if (hierarchicalLevelOrdered.get(count) == null) {
        hierarchicalLevelOrdered.put(count, list);
      } else {
        hierarchicalLevelOrdered.get(count).add(g);
      }
      if (count > totalLevelNumber) {
        totalLevelNumber = count;
      }

    }
    if (graph.getAllChildrenGraphs().isEmpty()) {
      final List<PiGraph> list = new ArrayList<>();
      list.add(graph);
      hierarchicalLevelOrdered.put(0L, list);
      totalLevelNumber = 0L;
    }

  }

  /**
   * Transform a subgraph into a standard actor
   *
   * @param g
   *          The identify clustered subgraph
   */
  private void cluster(PiGraph g) {
    // compute the cluster schedule

    final List<ScapeSchedule> schedule = new ScheduleScape(g).execute();
    final Scenario clusterScenario = lastLevelScenario(g);
    // retrieve cluster timing
    final Map<AbstractVertex, Long> rv = PiBRV.compute(g, BRVMethod.LCM);
    final Map<Component, Map<TimingType, String>> sumTiming = clusterTiming(rv, g);

    new CodegenScape(clusterScenario, g, schedule, stackSize);

    replaceBehavior(g);
    updateTiming(sumTiming, g);

  }

  /**
   * Replace the refinement of the clustered hierarchical actor by a standard Code
   *
   * @param g
   *          The identify clustered subgraph
   */

  private void replaceBehavior(PiGraph g) {
    final Actor oEmpty = PiMMUserFactory.instance.createActor();
    oEmpty.setName(g.getName());
    // add refinement
    final Refinement refinement = PiMMUserFactory.instance.createCHeaderRefinement();
    oEmpty.setRefinement(refinement);
    final CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) (oEmpty.getRefinement());
    final Prototype oEmptyPrototype = new Prototype();
    oEmptyPrototype.setIsStandardC(true);
    final String strPath = scenario.getCodegenDirectory();
    cHeaderRefinement.setFilePath(
        strPath + "/Cluster_" + ((PiGraph) g.getContainingGraph()).getName() + "_" + oEmpty.getName() + ".h");
    final FunctionPrototype functionPrototype = PiMMUserFactory.instance.createFunctionPrototype();
    cHeaderRefinement.setLoopPrototype(functionPrototype);
    functionPrototype.setName(oEmpty.getName());
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
    if (oEmpty.getName().contains("loop")) {
      pipLoopTransfo(oEmpty, this.coreEquivalent);
    }
  }

  /**
   * Unrolled LOOP pattern on the gcd above number of Processing element pipelined cluster.
   *
   * @param oEmpty
   *          loop element to be duplicate and pipelined
   * @param value
   *          highest divisor of brv(loop) just above the number of processing element
   */
  private void pipLoopTransfo(AbstractActor oEmpty, Long value) {
    final List<AbstractActor> dupActorsList = new LinkedList<>();
    for (int i = 1; i < value; i++) {
      final AbstractActor dupActor = PiMMUserFactory.instance.copy(oEmpty);
      dupActor.setName(oEmpty.getName() + "_" + i);
      dupActor.setContainingGraph(oEmpty.getContainingGraph());
      dupActorsList.add(dupActor);
    }
    for (final DataInputPort in : oEmpty.getDataInputPorts()) {
      if (!in.getFifo().isHasADelay()) {
        final ForkActor frk = PiMMUserFactory.instance.createForkActor();
        frk.setName("Fork_" + oEmpty.getName());
        frk.setContainingGraph(oEmpty.getContainingGraph());

        // connect din to frk
        final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
        din.setName("in");
        din.setExpression(in.getFifo().getSourcePort().getExpression().evaluate());

        frk.getDataInputPorts().add(din);
        final Fifo fin = PiMMUserFactory.instance.createFifo();
        fin.setType(in.getFifo().getType());
        fin.setSourcePort(in.getFifo().getSourcePort());
        fin.setTargetPort(din);

        fin.setContainingGraph(oEmpty.getContainingGraph());

        // connect fork to oEmpty_0
        final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
        dout.setName("out_0");
        dout.setExpression(in.getFifo().getTargetPort().getExpression().evaluate());
        frk.getDataOutputPorts().add(dout);
        final Fifo fout = PiMMUserFactory.instance.createFifo();
        fout.setType(in.getFifo().getType());
        fout.setSourcePort(dout);
        fout.setTargetPort(in);
        fout.setContainingGraph(oEmpty.getContainingGraph());
        // remove extra fifo --> non en fait c'est bon

        // connect fork to duplicated actors
        for (int i = 1; i < value; i++) {
          final DataOutputPort doutn = PiMMUserFactory.instance.createDataOutputPort();
          doutn.setName("out_" + i);
          doutn.setExpression(in.getFifo().getTargetPort().getExpression().evaluate());
          frk.getDataOutputPorts().add(doutn);
          final Fifo foutn = PiMMUserFactory.instance.createFifo();
          foutn.setType(in.getFifo().getType());
          foutn.setSourcePort(doutn);
          foutn.setContainingGraph(oEmpty.getContainingGraph());
          dupActorsList.get(i - 1).getDataInputPorts().stream().filter(x -> x.getName().equals(in.getName()))
              .forEach(x -> x.setIncomingFifo(foutn));

        }
      } else if (in.getFifo().getDelay().hasSetterActor()) {
        final Fifo fd = PiMMUserFactory.instance.createFifo();
        fd.setSourcePort(in.getFifo().getDelay().getSetterPort());
        fd.setTargetPort(in);
        fd.setContainingGraph(oEmpty.getContainingGraph());

      } else {
        PreesmLogger.getLogger().log(Level.INFO, "unrolled loops requires setter and getter affected to a local delay");

      }
    }

    for (final DataOutputPort out : oEmpty.getDataOutputPorts()) {
      if (!out.getFifo().isHasADelay()) {
        final JoinActor jn = PiMMUserFactory.instance.createJoinActor();
        jn.setName("Join_" + oEmpty.getName());
        jn.setContainingGraph(oEmpty.getContainingGraph());

        // connect Join to dout
        final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
        dout.setName("out");
        dout.setExpression(out.getFifo().getTargetPort().getExpression().evaluate());
        jn.getDataOutputPorts().add(dout);
        final Fifo fout = PiMMUserFactory.instance.createFifo();
        fout.setSourcePort(dout);
        fout.setTargetPort(out.getFifo().getTargetPort());
        fout.setContainingGraph(oEmpty.getContainingGraph());

        // connect oEmpty_0 to Join
        final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
        din.setName("in_0");
        din.setExpression(out.getFifo().getSourcePort().getExpression().evaluate());
        jn.getDataInputPorts().add(din);
        final Fifo fin = PiMMUserFactory.instance.createFifo();
        fin.setSourcePort(out);
        fin.setTargetPort(din);
        fin.setContainingGraph(oEmpty.getContainingGraph());

        // connect duplicated actors to Join
        for (int i = 1; i < value; i++) {
          final DataInputPort dinn = PiMMUserFactory.instance.createDataInputPort();
          dinn.setName("in_" + i);
          dinn.setExpression(out.getFifo().getSourcePort().getExpression().evaluate());
          jn.getDataInputPorts().add(dinn);
          final Fifo finn = PiMMUserFactory.instance.createFifo();
          finn.setTargetPort(dinn);
          finn.setContainingGraph(oEmpty.getContainingGraph());
          dupActorsList.get(i - 1).getDataOutputPorts().stream().filter(x -> x.getName().equals(out.getName()))
              .forEach(x -> x.setOutgoingFifo(finn));

        }
      } else {
        // if getter

        if (out.getFifo().getDelay().hasGetterActor()) {
          final Fifo fdout = PiMMUserFactory.instance.createFifo();
          dupActorsList.get((int) (value - 2)).getDataOutputPorts().stream().filter(x -> x.getFifo() == null)
              .forEach(x -> x.setOutgoingFifo(fdout));
          fdout.setTargetPort(out.getFifo().getDelay().getGetterPort());
          fdout.setContainingGraph(oEmpty.getContainingGraph());

        } else {
          PreesmLogger.getLogger().log(Level.INFO,
              "unrolled loops requires setter and getter affected to a local delay");
        }
        // connect oEmpty delayed output to 1st duplicated actor
        final Fifo fdin = PiMMUserFactory.instance.createFifo();
        fdin.setSourcePort(out);
        dupActorsList.get(0).getDataInputPorts().stream().filter(x -> x.getFifo() == null)
            .forEach(x -> x.setIncomingFifo(fdin));
        fdin.setContainingGraph(oEmpty.getContainingGraph());

      }
    }
    // interconnect duplicated actor on their delayed port
    for (int i = 0; i < value - 2; i++) {
      final Fifo fd = PiMMUserFactory.instance.createFifo();
      dupActorsList.get(i).getDataOutputPorts().stream().filter(x -> x.getFifo() == null)
          .forEach(x -> x.setOutgoingFifo(fd));
      dupActorsList.get(i + 1).getDataInputPorts().stream().filter(x -> x.getFifo() == null)
          .forEach(x -> x.setIncomingFifo(fd));
      fd.setContainingGraph(oEmpty.getContainingGraph());
    }
    // remove delay
    ((PiGraph) oEmpty.getContainingGraph()).getDelays().stream()
        .filter(x -> x.getContainingFifo().getSourcePort() == null)
        .forEach(x -> ((PiGraph) oEmpty.getContainingGraph()).removeDelay(x));
    // remove empty fifo
    ((PiGraph) oEmpty.getContainingGraph()).getFifos().stream().filter(x -> x.getSourcePort() == null)
        .forEach(x -> ((PiGraph) oEmpty.getContainingGraph()).removeFifo(x));
    ((PiGraph) oEmpty.getContainingGraph()).getFifos().stream().filter(x -> x.getTargetPort() == null)
        .forEach(x -> ((PiGraph) oEmpty.getContainingGraph()).removeFifo(x));

  }

  /**
   * Used to store timing inside the cluster
   *
   * @param sumTiming
   *          sum of contained actors timing
   * @param lastLevel
   *          PiGraph of the cluster
   */
  private void updateTiming(Map<Component, Map<TimingType, String>> sumTiming, PiGraph lastLevel) {
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
   * @param copiedCluster
   *          PiGraph of the cluster
   * @param scenario2
   *          contains list of timing
   */
  private Map<Component, Map<TimingType, String>> clusterTiming(Map<AbstractVertex, Long> repetitionVector,
      PiGraph cluster) {
    final Map<Component, Map<TimingType, String>> clusterTiming = new HashMap<>();
    // Initialise
    for (final Component opId : archi.getProcessingElements()) {
      final Map<TimingType, String> val = new EnumMap<>(TimingType.class);
      val.put(TimingType.EXECUTION_TIME, String.valueOf(0L));
      clusterTiming.put(opId, val);
    }
    if (!scenario.getTimings().getActorTimings().isEmpty()) {
      for (final AbstractActor actor : cluster.getOnlyActors()) {
        AbstractActor aaa = null;
        for (final AbstractActor aa : scenario.getTimings().getActorTimings().keySet()) {
          if (actor.getName().equals(aa.getName())) {
            aaa = aa;
          }
        }
        for (final Component opId : archi.getProcessingElements()) {
          final Long sum = scenario.getTimings().evaluateTimingOrDefault(aaa, opId, TimingType.EXECUTION_TIME)
              * repetitionVector.get(actor);
          clusterTiming.get(opId).replace(TimingType.EXECUTION_TIME, String.valueOf(sum));
        }
      }
    }
    return clusterTiming;
  }

  /**
   * Used to create a temporary scenario of the cluster
   *
   * @param copiedCluster
   *          PiGraph of the cluster
   */
  private Scenario lastLevelScenario(PiGraph copiedCluster) {
    final Scenario clusterScenario = ScenarioUserFactory.createScenario();

    clusterScenario.setAlgorithm(copiedCluster);
    clusterScenario.setDesign(archi);
    clusterScenario.setTimings(ScenarioFactory.eINSTANCE.createTimings());
    clusterScenario.setEnergyConfig(ScenarioFactory.eINSTANCE.createEnergyConfig());

    final List<ComponentInstance> coreIds = new ArrayList<>(archi.getOperatorComponentInstances());
    // for all different type of cores, allow mapping on it
    for (final ComponentInstance coreId : coreIds) {
      for (final AbstractActor actor : copiedCluster.getAllActors()) {
        // Add constraint
        clusterScenario.getConstraints().addConstraint(coreId, actor);

      }
    }
    clusterScenario.getSimulationInfo().getDataTypes().map().put("uchar", (long) 8);
    clusterScenario.getSimulationInfo().getDataTypes().map().put("char", (long) 8);
    clusterScenario.getSimulationInfo().getDataTypes().map().put("unsigned char", (long) 8);
    clusterScenario.getSimulationInfo().getDataTypes().map().put("int", (long) 32);
    clusterScenario.getSimulationInfo().getDataTypes().map().put("float", (long) 32);
    clusterScenario.getSimulationInfo().getDataTypes().map().put("double", (long) 64);
    clusterScenario.getSimulationInfo().getDataTypes().map().put("SiftKpt", (long) 4448);
    clusterScenario.getSimulationInfo().getDataTypes().map().put("coord", (long) 64);
    clusterScenario.getSimulationInfo().getDataTypes().map().put("coordf", (long) 64);
    return clusterScenario;
  }

}
