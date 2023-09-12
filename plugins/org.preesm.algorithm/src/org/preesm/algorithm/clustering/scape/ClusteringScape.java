package org.preesm.algorithm.clustering.scape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import org.preesm.algorithm.clustering.partitioner.ClusterPartitionerLOOP;
import org.preesm.algorithm.clustering.partitioner.ClusterPartitionerSEQ;
import org.preesm.algorithm.clustering.partitioner.ClusterPartitionerSRV;
import org.preesm.algorithm.clustering.partitioner.ClusterPartitionerURC;
import org.preesm.algorithm.codegen.idl.Prototype;
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
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
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
   * Number of Processing elements.
   */
  private final Long     coreNumber;
  /**
   * Number of hierarchical level.
   */
  private final int      levelNumber;
  /**
   * SCAPE mode : 1 (...); 2 (...); 3 (...).
   */
  private final int      mode;

  /**
   * assign elements of the same cluster to the same component.
   */
  private Component                 clusterComponent;
  /**
   * Store the non cluster-able actor chosen manually by the operator.
   */
  private final List<AbstractActor> nonClusterableList;

  int         clusterIndex = -1;// topological index
  private int clusterId    = 0; // index cluster created

  private final Map<Long, List<PiGraph>> hierarchicalLevelOrdered;
  private Long                           totalLevelNumber = 0L;
  private Long                           levelBound       = 0L;
  private final boolean                  isLastCluster    = false;
  private final List<String[]>           loopedBuffer     = new ArrayList<>();
  private final String                   previousInitFunc = "";

  public ClusteringScape(PiGraph graph, Scenario scenario, Design archi, Long stackSize, Long coreNumber, int mode,
      int levelNumber, List<AbstractActor> nonClusterableList) {
    this.graph = graph;
    this.scenario = scenario;
    this.archi = archi;
    this.stackSize = stackSize;
    this.coreNumber = coreNumber;
    this.mode = mode;
    this.levelNumber = levelNumber;
    this.nonClusterableList = nonClusterableList;
    this.hierarchicalLevelOrdered = new HashMap<>();
  }

  public PiGraph execute() {
    // construct hierarchical structure
    fillHierarchicalStrcuture();
    // compute cluster-able level ID
    computeClusterableLevel();
    // Coarse clustering while cluster-able level are not reached
    coarseCluster();
    // Pattern identification
    patternIDs();
    // pipeline

    final Map<AbstractVertex, Long> brv = PiBRV.compute(graph, BRVMethod.LCM);
    PiBRV.printRV(brv);
    return graph;

  }

  /**
   * Enable specific cluster on specific hierarchical level according to the chosen SCAPE mode
   */
  private void patternIDs() {

    if (mode == 0) {
      for (final PiGraph g : hierarchicalLevelOrdered.get(levelBound)) {
        PiGraph newCluster = null;
        do {
          final int size = graph.getAllChildrenGraphs().size();
          final Map<AbstractVertex, Long> rv = PiBRV.compute(g, BRVMethod.LCM);
          newCluster = new ClusterPartitionerURC(g, scenario, coreNumber.intValue(), rv, clusterId, nonClusterableList)
              .cluster();// URC transfo
          if (graph.getAllChildrenGraphs().size() == size) {
            newCluster = new ClusterPartitionerSRV(g, scenario, coreNumber.intValue(), rv, clusterId,
                nonClusterableList).cluster();// SRV transfo
          }
          cluster(newCluster);
        } while (newCluster != null);
      }
    }
    if (mode == 1) {
      for (final PiGraph g : hierarchicalLevelOrdered.get(levelBound)) {
        PiGraph newCluster = null;
        do {
          final int size = graph.getAllChildrenGraphs().size();
          final Map<AbstractVertex, Long> rv = PiBRV.compute(g, BRVMethod.LCM);
          newCluster = new ClusterPartitionerLOOP(g, scenario).cluster();// LOOP2 transfo
          if (graph.getAllChildrenGraphs().size() == size) {
            newCluster = new ClusterPartitionerURC(g, scenario, coreNumber.intValue(), rv, clusterId,
                nonClusterableList).cluster();// URC transfo
          }
          if (graph.getAllChildrenGraphs().size() == size) {
            newCluster = new ClusterPartitionerSRV(g, scenario, coreNumber.intValue(), rv, clusterId,
                nonClusterableList).cluster();// SRV transfo
          }
          if (graph.getAllChildrenGraphs().size() == size) {
            newCluster = new ClusterPartitionerSEQ(g, scenario, coreNumber.intValue(), rv, clusterId,
                nonClusterableList, archi).cluster();// SEQ transfo
          }
          cluster(newCluster.getChildrenGraphs().get(0));
        } while (newCluster.getChildrenGraphs() != null);
      }
    }
    if (mode == 2) {
      for (Long i = levelBound; i >= 0L; i--) {
        for (final PiGraph g : hierarchicalLevelOrdered.get(i)) {
          PiGraph newCluster = null;
          boolean isHasCluster = true;
          do {
            final int size = graph.getAllChildrenGraphs().size();
            final Map<AbstractVertex, Long> rv = PiBRV.compute(g, BRVMethod.LCM);
            newCluster = new ClusterPartitionerLOOP(g, scenario).cluster();// LOOP2 transfo
            if (graph.getAllChildrenGraphs().size() == size) {
              newCluster = new ClusterPartitionerURC(g, scenario, coreNumber.intValue(), rv, clusterId,
                  nonClusterableList).cluster();// URC transfo
            }
            if (graph.getAllChildrenGraphs().size() == size) {
              newCluster = new ClusterPartitionerSRV(g, scenario, coreNumber.intValue(), rv, clusterId,
                  nonClusterableList).cluster();// SRV transfo
            }
            if (graph.getAllChildrenGraphs().size() == size) {
              newCluster = new ClusterPartitionerSEQ(g, scenario, coreNumber.intValue(), rv, clusterId,
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
        // detect loop
        // TODO
        // detect
        final PiGraph g = fd.getContainingPiGraph();
        for (Long i = 0L; i < totalLevelNumber; i++) {
          if (hierarchicalLevelOrdered.get(i).contains(g)) {
            count = Math.max(count, i);
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
    Map<AbstractVertex, Long> rv = PiBRV.compute(g, BRVMethod.LCM);
    // compute the cluster schedule
    String schedulesMap;
    schedulesMap = registerClusterSchedule(g, rv);

    final Scenario clusterScenario = lastLevelScenario(g);
    // retrieve cluster timing
    rv = PiBRV.compute(g, BRVMethod.LCM);
    final Long sumTiming = clusterTiming(rv, g, scenario);

    rv = PiBRV.compute(g, BRVMethod.LCM);
    new CodegenScape(clusterScenario, g, schedulesMap, stackSize);

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
        final Actor set = PiMMUserFactory.instance.createActor();
        set.setName("setter");
        final Refinement refinement = PiMMUserFactory.instance.createCHeaderRefinement();

        set.setRefinement(refinement);
        // Set the refinement
        final CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) ((set).getRefinement());
        final Prototype oEmptyPrototype = new Prototype();
        oEmptyPrototype.setIsStandardC(true);
        cHeaderRefinement.setFilePath(((Actor) oEmpty).getRefinement().getFilePath());
        final FunctionPrototype functionPrototype = PiMMUserFactory.instance.createFunctionPrototype();
        cHeaderRefinement.setLoopPrototype(functionPrototype);
        functionPrototype.setName(((Actor) oEmpty).getRefinement().getFileName());

        set.setContainingGraph(oEmpty.getContainingGraph());
        final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
        set.getDataOutputPorts().add(dout);
        dout.setName("out");
        dout.setExpression(in.getFifo().getDelay().getExpression().evaluate());
        final Fifo fd = PiMMUserFactory.instance.createFifo();
        fd.setSourcePort(set.getDataOutputPorts().get(0));
        fd.setTargetPort(in);
        fd.setContainingGraph(oEmpty.getContainingGraph());
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
          final Actor get = PiMMUserFactory.instance.createActor();
          get.setName("getter");
          /// ******
          final Refinement refinement = PiMMUserFactory.instance.createCHeaderRefinement();
          get.setRefinement(refinement);
          // Set the refinement
          final CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) ((get).getRefinement());
          final Prototype oEmptyPrototype = new Prototype();
          oEmptyPrototype.setIsStandardC(true);
          cHeaderRefinement.setFilePath(((Actor) oEmpty).getRefinement().getFilePath());
          final FunctionPrototype functionPrototype = PiMMUserFactory.instance.createFunctionPrototype();
          cHeaderRefinement.setLoopPrototype(functionPrototype);
          functionPrototype.setName(((Actor) oEmpty).getRefinement().getFileName());

          get.setContainingGraph(oEmpty.getContainingGraph());
          final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
          get.getDataInputPorts().add(din);
          din.setName("in");
          din.setExpression(out.getFifo().getDelay().getExpression().evaluate());
          final Fifo fdout = PiMMUserFactory.instance.createFifo();
          dupActorsList.get((int) (value - 2)).getDataOutputPorts().stream().filter(x -> x.getFifo() == null)
              .forEach(x -> x.setOutgoingFifo(fdout));
          fdout.setTargetPort(get.getDataInputPorts().get(0));
          fdout.setContainingGraph(oEmpty.getContainingGraph());
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
  private void updateTiming(Long sumTiming, PiGraph lastLevel) {
    AbstractActor aaa = null;
    if (sumTiming != null) {
      for (final AbstractActor aa : graph.getAllActors()) {
        if (lastLevel.getName().equals(aa.getName()) || aa.getName().contains(lastLevel.getName())) {
          aaa = aa;
          // update timing
          scenario.getTimings().setTiming(aaa, clusterComponent, TimingType.EXECUTION_TIME, String.valueOf(sumTiming));
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
  private Long clusterTiming(Map<AbstractVertex, Long> repetitionVector, PiGraph copiedCluster, Scenario scenario2) {
    long sumTiming = 0L;
    Component clusterComponent = null;
    if (!scenario2.getTimings().getActorTimings().isEmpty()) {

      for (final AbstractActor a : copiedCluster.getAllActors()) {
        if (a instanceof Actor) {

          AbstractActor aaa = null;
          for (final AbstractActor aa : scenario.getTimings().getActorTimings().keySet()) {
            if (a.getName().equals(aa.getName())) {
              aaa = aa;
            }
          }
          if (aaa != null) {
            final String time = scenario.getTimings().getActorTimings().get(aaa).get(0).getValue()
                .get(TimingType.EXECUTION_TIME);
            Long brv = repetitionVector.get(a);
            if (brv == null) {
              brv = (long) 1;
            }
            sumTiming = sumTiming + (Long.valueOf(time) * brv);

            clusterComponent = scenario.getTimings().getActorTimings().get(aaa).get(0).getKey();
            this.clusterComponent = clusterComponent;
          } else {
            this.clusterComponent = archi.getComponentHolder().getComponents().get(0);
            final String time = "100";
            final Long brv = repetitionVector.get(a);
            sumTiming = sumTiming + (Long.valueOf(time) * brv);
          }
        }

      }
    } else {
      for (final AbstractActor a : copiedCluster.getAllActors()) {
        if (a instanceof Actor) {
          this.clusterComponent = archi.getComponentHolder().getComponents().get(0);// temp
          final String time = "100";
          final Long brv = repetitionVector.get(a);
          sumTiming = sumTiming + (Long.valueOf(time) * brv);
        }
      }
    }
    return sumTiming;
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

  /**
   * Used to compute the schedule of the cluster with APGAN method
   *
   * @param graph
   *          PiGraph of the cluster
   * @param rv
   *          List of repetition vector
   */
  private String registerClusterSchedule(PiGraph graph, Map<AbstractVertex, Long> rv) {
    // Schedule subgraph

    StringBuilder result = new StringBuilder();
    List<AbstractActor> actorList = graph.getAllExecutableActors();

    if (rv.size() == 1) {
      result.append(rv.get(actorList.get(0)) + "(" + actorList.get(0).getName() + ")");
      return result.toString();

    }
    // Compute BRV

    final Map<AbstractVertex, Long> repetitionVector = rv;

    // compute repetition count (gcd(q(a),q(b)))
    final Map<AbstractVertex, Map<AbstractVertex, Long>> repetitionCount = new LinkedHashMap<>();

    for (final AbstractActor element : graph.getAllExecutableActors()) {

      for (final DataOutputPort element2 : element.getDataOutputPorts()) {
        if (!(element2.getFifo().getTarget() instanceof DataOutputInterface) && !element2.getFifo().isHasADelay()
            && repetitionVector.containsKey(element) && repetitionVector.containsKey(element2.getFifo().getTarget())) {
          final Long rep = gcd(repetitionVector.get(element), repetitionVector.get(element2.getFifo().getTarget()));

          final Map<AbstractVertex, Long> map = new LinkedHashMap<>();
          map.put((AbstractVertex) element2.getFifo().getTarget(), rep);
          if (!repetitionCount.containsKey(element)) {
            if (!element2.getFifo().isHasADelay()) {
              repetitionCount.put(element, map);
            }
          } else if (!element2.getFifo().isHasADelay()) {
            repetitionCount.get(element).put((AbstractVertex) element2.getFifo().getTarget(), rep);
          }
        }
      }
    }

    // find actors in order of succession

    int totalsize = graph.getAllExecutableActors().size();// 5
    final List<AbstractActor> actorListOrdered = new ArrayList<>();
    int flag = 0;
    if (!graph.getDataInputInterfaces().isEmpty()) {
      for (int i = 0; i < graph.getActors().size(); i++) {
        if (graph.getActors().get(i) instanceof DataInputInterface) {
          flag = 0;
          final AbstractActor curentActor = (AbstractActor) graph.getActors().get(i).getDataOutputPorts().get(0)
              .getFifo().getTarget();
          if (!actorListOrdered.contains(curentActor)) {

            for (int iii = 0; iii < curentActor.getDataInputPorts().size(); iii++) {
              if (actorListOrdered.contains(curentActor.getDataInputPorts().get(iii).getFifo().getSource())
                  || curentActor.getDataInputPorts().get(iii).getFifo().getSource() instanceof DataInputInterface
                  || curentActor.getDataInputPorts().get(iii).getFifo().isHasADelay()) {
                flag++;
              }
            }
            if (flag == curentActor.getDataInputPorts().size()) {

              actorListOrdered.add(curentActor);// fill a
              totalsize--;// 4
            }
          }
        }
      }
      for (final AbstractActor a : actorList) {
        flag = 0;
        if (a.getDataInputPorts().isEmpty() && (!actorListOrdered.contains(a))) {
          for (int i = 0; i < a.getDataInputPorts().size(); i++) {
            if (actorListOrdered.contains(a.getDataInputPorts().get(i).getFifo().getSource())) {
              flag++;
            }
          }
          if (flag == a.getDataInputPorts().size()) {
            actorListOrdered.add(a);
            totalsize--;
          }

        }
      }
    } else {
      for (final AbstractActor a : actorList) {
        if (a.getDataInputPorts().isEmpty() && (!actorListOrdered.contains(a))) {
          actorListOrdered.add(a);
          totalsize--;

        }
      }
    }
    int curentSize = actorListOrdered.size();// 1

    for (int i = 0; i < curentSize; i++) {
      for (int ii = 0; ii < actorListOrdered.get(i).getDataOutputPorts().size(); ii++) {
        flag = 0;
        final AbstractActor precActor = (AbstractActor) actorListOrdered.get(i).getDataOutputPorts().get(ii).getFifo()
            .getTarget();
        if (!actorListOrdered.contains(precActor)) {
          for (int iii = 0; iii < precActor.getDataInputPorts().size(); iii++) {
            if (actorListOrdered.contains(precActor.getDataInputPorts().get(iii).getFifo().getSource())
                || precActor.getDataInputPorts().get(iii).getFifo().getSource() instanceof DataInputInterface
                || precActor.getDataInputPorts().get(iii).getFifo().isHasADelay()) {
              flag++;
            }
          }
          if (flag == ((AbstractActor) actorListOrdered.get(i).getDataOutputPorts().get(ii).getFifo().getTarget())
              .getDataInputPorts().size()) {
            actorListOrdered
                .add((AbstractActor) actorListOrdered.get(i).getDataOutputPorts().get(ii).getFifo().getTarget());// fill

            totalsize--;// 3
          }

        }

      }
    }
    flag = 0;
    int precSize = curentSize;
    curentSize = actorListOrdered.size();// 2
    while (totalsize > 0) {
      for (int i = precSize; i < curentSize; i++) {
        for (int ii = 0; ii < actorListOrdered.get(i).getDataOutputPorts().size(); ii++) {
          flag = 0;
          final AbstractActor curentActor = (AbstractActor) actorListOrdered.get(i).getDataOutputPorts().get(ii)
              .getFifo().getTarget();
          if (!actorListOrdered.contains(curentActor)) {
            for (int iii = 0; iii < curentActor.getDataInputPorts().size(); iii++) {
              final AbstractActor precActor = (AbstractActor) curentActor.getDataInputPorts().get(iii).getFifo()
                  .getSource();
              if (actorListOrdered.contains(precActor) || precActor instanceof DataInputInterface
                  || curentActor.getDataInputPorts().get(iii).getFifo().isHasADelay()) {
                flag++;
              }
            }
            if (flag == curentActor.getDataInputPorts().size()) {
              actorListOrdered
                  .add((AbstractActor) actorListOrdered.get(i).getDataOutputPorts().get(ii).getFifo().getTarget());

              totalsize--;// 2
            }

          }
        }
      }
      precSize = curentSize;
      curentSize = actorListOrdered.size();// 3++
    }
    actorList = actorListOrdered;
    Long lastIndex = (long) 0;

    AbstractActor maxLeft = null;
    AbstractActor maxRight = null;
    Long max = (long) 0;
    while (repetitionVector.size() > 1) {
      boolean removeRight = false;

      // find pair with the biggest repetition count

      if (result.isEmpty()) {
        maxLeft = null;
        maxRight = null;
        max = (long) 0;
        for (int i = 0; i < actorList.size(); i++) {
          final int rang = actorList.size() - i - 1;

          if (repetitionCount.containsKey(actorList.get(rang))) {
            for (final AbstractVertex a : repetitionCount.get(actorList.get(rang)).keySet()) {
              if (repetitionCount.get(actorList.get(rang)).get(a) > max) {

                max = repetitionCount.get(actorList.get(rang)).get(a);
                maxLeft = actorList.get(rang);
                maxRight = (AbstractActor) a;

              }
            }
          }
        }

      }
      if (repetitionCount.isEmpty()) {
        for (int i = 0; i < actorList.size(); i++) {
          final int rang = actorList.size() - i - 1;
          if (actorList.get(rang) instanceof Actor || actorList.get(rang) instanceof SpecialActor) {
            result.append("*" + actorList.get(rang).getName());
          }
        }
        return result.toString();
      }
      if (repetitionVector.get(maxLeft) == null) {
        boolean hasPredecessor = false;
        for (int i = 0; i < actorList.size(); i++) {
          final int rang = actorList.size() - i - 1;
          if (repetitionCount.containsKey(actorList.get(rang))) {
            if (repetitionCount.get(actorList.get(rang)).containsKey(maxRight) && !hasPredecessor) {
              max = repetitionCount.get(actorList.get(rang)).get(maxRight);
              maxLeft = actorList.get(rang);
              hasPredecessor = true;

            }

          }
        }
        if (!hasPredecessor) {
          for (final AbstractActor element : actorList) {

            if (repetitionCount.containsKey(maxRight)) {
              if (repetitionCount.get(maxRight).containsKey(element) && !hasPredecessor) {
                max = repetitionCount.get(maxRight).get(element);
                maxLeft = maxRight;
                maxRight = element;
                hasPredecessor = true;
              }
            }
          }
        }
      } else if (repetitionVector.get(maxRight) == null) {
        boolean hasSuccessor = false;
        for (int i = 0; i < actorList.size(); i++) {
          final int rang = actorList.size() - i - 1;
          if (repetitionCount.containsKey(actorList.get(rang))) { // if maxLeft to right
            if (repetitionCount.get(actorList.get(rang)).containsKey(maxLeft) && !hasSuccessor) {
              max = repetitionCount.get(actorList.get(rang)).get(maxLeft);
              maxRight = maxLeft;
              maxLeft = actorList.get(rang);

              hasSuccessor = true;
            }

          }
        }
        if (!hasSuccessor) {
          for (final AbstractActor element : actorList) {
            if (repetitionCount.containsKey(maxLeft)) { // if maxLeft to left
              if (repetitionCount.get(maxLeft).containsKey(element) && !hasSuccessor) {
                max = repetitionCount.get(maxLeft).get(element);
                maxRight = element;
                hasSuccessor = true;
              }

            }
          }
        }
      }

      // compute String schedule
      if (result.isEmpty()) {
        if (max == 1) {
          result.append(maxLeft.getName() + "*" + maxRight.getName());
        } else {
          result.append(String.valueOf(max) + "(" + maxLeft.getName() + "*" + maxRight.getName() + ")");
        }
        lastIndex = max;
        if (maxRight.getDataInputPorts().size() > maxLeft.getDataOutputPorts().size()) {
          removeRight = false;
        } else {
          removeRight = true;
        }
      } else {
        if (repetitionVector.get(maxRight) == null || repetitionVector.get(maxLeft) == null) {
          for (final AbstractVertex a : repetitionVector.keySet()) {
            if (!result.toString().contains(a.getName())) {
              result.append("*" + a.getName());
            }
          }
          return result.toString();
        }
        if (repetitionVector.get(maxRight) == 1 && repetitionVector.get(maxLeft) == 1) {
          // if rv = 1*
          if (maxRight != null) {
            if (!result.toString().contains(maxRight.getName())) {
              result.append("*" + maxRight.getName());
              removeRight = true;
            } else {
              result.insert(0, maxLeft.getName() + "*");
            }
          }
          lastIndex = (long) 1;

        } else if (repetitionVector.get(maxRight) > 1 || repetitionVector.get(maxLeft) > 1) {
          // if same loop
          if (!result.toString().contains(maxRight.getName())) { // ajout de maxRight
            // add loop
            if (Objects.equals(repetitionVector.get(maxRight), lastIndex)) {
              if (repetitionVector.get(maxRight) > 1) {
                if (result.toString().contains(repetitionVector.get(maxRight) + "(")
                    && result.indexOf(String.valueOf(repetitionVector.get(maxRight))) == 0) {
                  final String temp = result.toString().replace(repetitionVector.get(maxRight) + "(", "");
                  final StringBuilder temp2 = new StringBuilder(temp.replaceFirst("\\)", ""));
                  result = temp2;
                  result.insert(0, repetitionVector.get(maxRight) + "(");
                  result.append("*" + maxRight.getName() + ")");
                } else if (result.toString().contains(repetitionVector.get(maxRight) + "(")
                    && result.indexOf(String.valueOf(repetitionVector.get(maxRight))) != 0) {
                  final char[] temp = result.toString().toCharArray();
                  String tempi = "";
                  for (int i = 0; i < temp.length - 2; i++) {
                    tempi = tempi + temp[i];
                  }
                  result = new StringBuilder(tempi + "*" + maxRight.getName() + ")");
                }

                lastIndex = repetitionVector.get(maxRight);

              } else {
                result.append("*" + maxRight.getName());
                lastIndex = (long) 1;
              }
            } else {
              // add into prec loop
              if (Long.valueOf(result.charAt(result.lastIndexOf("(") - 1)).equals(repetitionVector.get(maxRight))) {
                result.toString().replace(repetitionVector.get(maxRight) + "(", "");

              }
              result.append("*" + repetitionVector.get(maxRight) + "(" + maxRight.getName() + ")");
              lastIndex = repetitionVector.get(maxRight);
            }
            removeRight = true;
          } else if (!result.toString().contains(maxLeft.getName())) { // ajout de maxLeft
            if (Objects.equals(repetitionVector.get(maxLeft), lastIndex)) {
              if (repetitionVector.get(maxLeft) > 1) {
                if (result.toString().contains(repetitionVector.get(maxLeft) + "(")) {
                  final StringBuilder temp = new StringBuilder(
                      result.toString().replace(repetitionVector.get(maxLeft) + "(", ""));
                  result = temp;
                }
                result.insert(0, repetitionVector.get(maxLeft) + "(" + maxLeft.getName() + "*");
                lastIndex = repetitionVector.get(maxLeft);
              } else {
                result.insert(0, maxLeft.getName() + "*");
                lastIndex = (long) 1;
              }
            } else {
              final String[] temp = result.toString().split("\\(");
              if (temp[0].equals(repetitionVector.get(maxLeft))) {

                final StringBuilder tempo = new StringBuilder(
                    result.toString().replace(repetitionVector.get(maxLeft) + "(", ""));
                result = tempo;
                result.insert(0, repetitionVector.get(maxLeft) + "(" + maxLeft.getName());
                lastIndex = repetitionVector.get(maxLeft);
              } else if (repetitionVector.get(maxLeft) > 1) {
                result.insert(0, ")" + "*");
                result.insert(0, repetitionVector.get(maxLeft) + "(" + maxLeft.getName());
                lastIndex = repetitionVector.get(maxLeft);
              } else {
                result.insert(0, maxLeft.getName() + "*");
                lastIndex = (long) 1;
              }

            }

          }

        } else if (!result.toString().contains(maxRight.getName())) {
          if (Long.valueOf(result.charAt(result.lastIndexOf("(") - 1)).equals(repetitionVector.get(maxRight))) {

            result.toString().replace(repetitionVector.get(maxRight) + "(", "");

          } else {
            result.insert(0, ")" + "*");
          }
          result.append("*" + repetitionVector.get(maxRight) + "(" + maxRight.getName() + ")");
          removeRight = true;
        } else {
          if (result.toString().contains(repetitionVector.get(maxLeft) + "(")) {
            final StringBuilder temp = new StringBuilder(
                result.toString().replace(repetitionVector.get(maxLeft) + "(", ""));
            result = temp;
          } else {
            result.insert(0, ")" + "*");
          }
          result.insert(0, repetitionVector.get(maxLeft) + "(" + maxLeft.getName());
        }

      }

      // remove/replace clustered actor
      if (!removeRight) {
        repetitionVector.remove(maxLeft);
        for (final AbstractActor a : graph.getAllExecutableActors()) {
          if (repetitionCount.containsKey(a)) {
            if (repetitionCount.get(a).containsKey(maxLeft)) {
              for (final AbstractVertex aa : repetitionCount.get(maxLeft).keySet()) {
                if (!a.equals(aa)) {
                  repetitionCount.get(a).put(aa, repetitionCount.get(maxLeft).get(aa));
                }
              }
              repetitionCount.get(a).remove(maxLeft);
            }
          }

        }
        repetitionCount.remove(maxLeft);
        if (repetitionCount.containsKey(maxLeft)) {
          repetitionCount.remove(maxLeft);
        }

      } else {
        repetitionVector.remove(maxRight);
        for (final AbstractActor a : graph.getAllExecutableActors()) {
          if (repetitionCount.containsKey(a)) {
            if (repetitionCount.get(a).containsKey(maxRight)) {
              if (repetitionCount.containsKey(maxRight)) {
                if (!repetitionCount.get(maxRight).entrySet().isEmpty()) {
                  for (final AbstractVertex aa : repetitionCount.get(maxRight).keySet()) {
                    if (!a.equals(aa)) {
                      repetitionCount.get(a).put(aa, repetitionCount.get(maxRight).get(aa));
                    }
                  }
                  repetitionCount.get(a).remove(maxRight);
                }
              }
            }
          }

        }
        repetitionCount.remove(maxRight);
      }
    }

    return result.toString();
  }

  /**
   * Used to compute the greatest common denominator between 2 long values
   *
   * @param long1
   *          long value 1
   * @param long2
   *          long value 2
   */
  private Long gcd(Long long1, Long long2) {
    if (long2 == 0) {
      return long1;
    }
    return gcd(long2, long1 % long2);
  }

}
