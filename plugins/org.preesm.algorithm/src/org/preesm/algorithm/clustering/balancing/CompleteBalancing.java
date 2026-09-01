package org.preesm.algorithm.clustering.balancing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.preesm.algorithm.clustering.ClusterCreationTask;
import org.preesm.algorithm.clustering.heuristics.BalancingHeuristic;
import org.preesm.algorithm.clustering.synthesis.ClusterSynthesisHelper;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/***
 * This {@link BalancingHeuristic heuristic} will balance weights of a cluster, and might create a copy of it if needed.
 * For example, if the subgraph is repeating itself 9 times but there is only 4 CPU cores, There will be a first cluster
 * repeated 3 times enclosing 2 subgraph repetitions, and a second cluster repeated 2 times enclosing 3 subgraph
 * repetitions. It also creates {@link ForkActor fork} and {@link JoinActor join} actors if a second cluster is created.
 * Additionally, it will add {@link SpecialActor special actors} in the cluster to unlock memory reuse around and inside
 * the cluster.
 *
 * @author rcazoulat
 */
public class CompleteBalancing extends BalancingHeuristic {

  /**
   * Basic repetition vector, one value for each vertex of the graph
   */
  Map<AbstractVertex, Long> brv;

  boolean verbose;

  @Override
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Design arch,
      Map<String, String> taskParameters) {
    super.initHeuristicParameters(graph, scenario, arch, taskParameters);
    brv = PiBRV.compute(graph, BRVMethod.LCM);
    verbose = taskParameters == null || "true".equalsIgnoreCase(taskParameters.get(ClusterCreationTask.PARAM_VERBOSE));
  }

  @Override
  public List<PiGraph> balanceFirings(PiGraph topgraph, PiGraph cluster1, long nPEs) {

    if (topgraph == null) {
      throw new PreesmRuntimeException("top graph is null");
    }

    if (cluster1 == null) {
      throw new PreesmRuntimeException("cluster1 is null");
    }

    // The balancing might create multiple clusters if needed. The return object is then a list of clusters
    final List<PiGraph> clusters = new ArrayList<>();
    clusters.add(cluster1);

    // Used for logs
    final List<Long> cluster1OldExprs = cluster1.getAllDataPorts().stream()
        .map(dp -> dp.getExpression().evaluateAsLong()).toList();
    List<Long> oldExprs;
    String log;

    // Here, we make a distinction between "subgraph" and "cluster".
    // Subgraph means the pack of actors, independently if they are in hierarchical actor or not,
    // and cluster means the hierarchical actor, independently of the contained actors.

    // Computing The number of time the subgraph will be repeated.
    // For example, if actor A is repeating 8 times and actor B 16 times, then clusterRepetition will be equal to 8.
    final long clusterRep = brv.get(cluster1);

    // if nPEs is not a divisor of clusterRepetition, rest != 0
    // if cluster repetition < nPEs, we manually set rest to 0
    final long rest = clusterRep >= nPEs ? clusterRep % nPEs : 0;

    // clusterRepetition, without the rest.
    final long perfectClusterRep = clusterRep - rest;

    // Number of time the cluster will be repeated in top graph, without rest
    // If cluster repetition < nPEs, we manually set it to perfectClusterRep
    // For example, if clusterRep = 12 and nPEs = 8, then outerRep = 4.
    final long perfectOuterRep = clusterRep >= nPEs ? MathFunctionsHelper.gcd(perfectClusterRep, nPEs)
        : perfectClusterRep;

    // Number of repetition of subgraph in cluster,
    // For example, if clusterRep = 12 and nPEs = 8, then innerRep = 3.
    final long perfectInnerRep = perfectClusterRep / perfectOuterRep;

    // Number of time cluster1 will be repeated in top graph
    final long outerRep1 = perfectOuterRep - rest; // if rest = 0, nothing happens

    // Number of time subgraph will be repeated in total, with cluster1 weights
    final long subgraphRep1 = outerRep1 * perfectInnerRep; // same: if rest = 0, nothing happens

    // Number of repetition of subgraph in cluster1
    final long innerRep1 = subgraphRep1 / outerRep1;

    // Log
    if (verbose) {
      log = "[Partitioning] > subgraph rep = " + clusterRep + ", rest = " + rest + ", outerRep 1 = " + outerRep1
          + ", innerRep = " + innerRep1;
      PreesmLogger.getLogger().info(log);
    }

    // ------------------------------------------------------------------------------------------- //
    // Modifying the expressions of the ports of interfaces for cluster1
    // ------------------------------------------------------------------------------------------- //
    for (final DataInterface dataInterface : cluster1.getDataInterfaces()) {

      long expr;

      // Case if dataInterface is an input interface
      if (dataInterface instanceof DataInputInterface) {

        // Number of tokens needed on the port for one execution of the subgraph
        // No need to compute this with the RV value of the data input interface,
        // As it is always one.
        final long tokensOneExec = dataInterface.getDataPort().getFifo().getTargetPort().getExpression()
            .evaluateAsLong();
        final AbstractActor previousActor = dataInterface.getGraphPort().getFifo().getSource();

        if (previousActor instanceof final BroadcastActor brdActor) {

          // Log utils
          oldExprs = brdActor.getAllDataPorts().stream().map(dp -> dp.getExpression().evaluateAsLong()).toList();

          // 1. Modify output port of broadcast actor
          final DataOutputPort brdOutPort = dataInterface.getGraphPort().getFifo().getSourcePort();
          final long newExpr = tokensOneExec * outerRep1;
          brdOutPort.setExpression(newExpr);

          // Log -> track broadcast modification
          if (verbose) {
            log = BalancingHelper.makeCompareLog(previousActor, topgraph, oldExprs);
            PreesmLogger.getLogger().info(log);
          }
          expr = tokensOneExec;
        } else {
          expr = tokensOneExec * innerRep1;
        }

        // Case if dataInterface is an output interface
      } else {
        expr = dataInterface.getDataPort().getFifo().getSourcePort().getExpression().evaluateAsLong() * innerRep1;
      }

      // Top graph & subgraph ports values modifications
      dataInterface.getGraphPort().setExpression(expr);
      dataInterface.getDataPort().setExpression(expr);
    }

    // Log -> track cluster1 modification
    if (verbose) {
      log = BalancingHelper.makeCompareLog(cluster1, topgraph, cluster1OldExprs);
      PreesmLogger.getLogger().info(log);
    }

    // ------------------------------------------------------------------------------------------- //
    // Building cluster2, if needed
    // ------------------------------------------------------------------------------------------- //
    // If the rest of division of the cluster repetition and the number of PEs is not equal to 0,
    // Then we have to duplicate the cluster in two, with two different repetitions, and two different scales.
    // For example, if a cluster is repeating itself 9 times (clusterRepetition = 9),
    // and there is 4 PEs (this.nPEs = 4), then there will be 2 clusters, because 9 % 4 = 1.
    // The first cluster will be repeated 3 times with an inner repetition of 2,
    // and the second will be repeated 1 time, but with an inner repetition of 3.
    // In other words, if rest is not equal to 0, we have to duplicate the cluster in the top graph.
    if (rest != 0) {

      // Creating a new cluster with scale2, and adding it to top graph, with all the necessary rooting
      final PiGraph cluster2 = PiMMUserFactory.instance.copyPiGraphWithHistory(cluster1);
      topgraph.addActor(cluster2);
      final String cluster1Name = cluster1.getName();
      cluster2.setName(cluster1Name + "_2");
      cluster1.setName(cluster1Name + "_1");
      clusters.add(cluster2);

      // Number of repetition of subgraph in cluster2
      final long innerRep = perfectInnerRep + 1;

      // Number of time cluster2 will be repeated in top graph
      final long outerRep2 = rest;

      // Log
      if (verbose) {
        log = "[Partitioning] > scale 2 = " + outerRep2 + ", ratio2 = " + innerRep;
        PreesmLogger.getLogger().info(log);
      }

      // ------------------------------------------------------------------------------------------- //
      // Creating Dependencies
      // ------------------------------------------------------------------------------------------- //
      for (final ConfigInputInterface param : cluster2.getConfigInputInterfaces()) {
        final ConfigInputInterface oriParam = PreesmCopyTracker.getSource(param);
        final Dependency oriDep = oriParam.getGraphPort().getIncomingDependency();
        final Dependency newDep = PiMMUserFactory.instance.createDependency();
        newDep.setSetter(oriDep.getSetter());
        newDep.setGetter(param.getGraphPort());
        param.getGraphPort().setIncomingDependency(newDep);
        topgraph.addDependency(newDep);
      }

      // ------------------------------------------------------------------------------------------- //
      // Creating fork actors / Modifying broadcast actor
      // ------------------------------------------------------------------------------------------- //
      long forkCounter = 0;
      int interfaceIdx = 0;
      final int nbrInputInterfaces = cluster1.getDataInputInterfaces().size();
      while (interfaceIdx < nbrInputInterfaces) {

        // Getting data input interfaces of the two clusters (that are the same)
        final DataInputInterface inputInterface1 = cluster1.getDataInputInterfaces().get(interfaceIdx);
        final DataInputInterface inputInterface2 = cluster2.getDataInputInterfaces().get(interfaceIdx);
        interfaceIdx++;

        final Long tokensOneExec = inputInterface1.getDataPort().getFifo().getTargetPort().getExpression()
            .evaluateAsLong();

        final AbstractActor previousActor = inputInterface1.getGraphPort().getFifo().getSource();

        // If previous actor is a broadcast actor we add one output port for cluster2, in the outer broadcast
        if (previousActor instanceof final BroadcastActor brdActor) {

          // Log util
          oldExprs = brdActor.getAllDataPorts().stream().map(dp -> dp.getExpression().evaluateAsLong()).toList();

          // Modyfing out port 1 + creating out port 2
          final DataOutputPort brdOutPort1 = inputInterface1.getGraphPort().getFifo().getSourcePort();
          final DataOutputPort brdOutPort2 = PiMMUserFactory.instance.createDataOutputPort();
          brdActor.getDataOutputPorts().add(brdOutPort2);

          final String brdPortsName = brdOutPort1.getName();
          brdOutPort1.setName(brdPortsName + "_1");
          brdOutPort2.setName(brdPortsName + "_2");
          brdOutPort2.setExpression(tokensOneExec * (double) outerRep2);

          final Fifo brd2cluster2Fifo = PiMMUserFactory.instance.createFifo(brdOutPort2, inputInterface2.getGraphPort(),
              brdOutPort1.getFifo().getType());

          topgraph.addFifo(brd2cluster2Fifo);

          // Log -> track broadcast modification
          if (verbose) {
            log = BalancingHelper.makeCompareLog(brdActor, topgraph, oldExprs);
            PreesmLogger.getLogger().info(log);
          }

          // Setting expression of input interface 2
          inputInterface2.getGraphPort().setExpression(tokensOneExec);
          inputInterface2.getDataPort().setExpression(tokensOneExec);

        } else {

          // Creating fork actor
          final ForkActor forkActor = PiMMUserFactory.instance.createForkActor();
          forkActor.setName("fork_" + forkCounter++ + "_" + cluster1Name);

          // Creating ports of fork actor
          final DataInputPort forkInPort = PiMMUserFactory.instance.createDataInputPort();
          final DataOutputPort forkOutPort1 = PiMMUserFactory.instance.createDataOutputPort();
          final DataOutputPort forkOutPort2 = PiMMUserFactory.instance.createDataOutputPort();
          forkInPort.setName("in");
          forkOutPort1.setName("out_1");
          forkOutPort2.setName("out_2");
          forkActor.getDataInputPorts().add(forkInPort);
          forkActor.getDataOutputPorts().add(forkOutPort1);
          forkActor.getDataOutputPorts().add(forkOutPort2);

          // Getting input port of data input interfaces
          final DataInputPort cluster1InPort = inputInterface1.getGraphPort();
          final DataInputPort clister2InPort = inputInterface2.getGraphPort();

          // Getting initial fifo, between interface and the previous actor
          final Fifo inFifo = cluster1InPort.getFifo();

          // modifying the target port of fifo
          inFifo.setTargetPort(forkInPort);
          final String fifosType = inFifo.getType();

          final Fifo fifoForkCluster1 = PiMMUserFactory.instance.createFifo(forkOutPort1, cluster1InPort, fifosType);
          final Fifo fifoForkCluster2 = PiMMUserFactory.instance.createFifo(forkOutPort2, clister2InPort, fifosType);

          topgraph.addActor(forkActor);
          topgraph.addFifo(fifoForkCluster1);
          topgraph.addFifo(fifoForkCluster2);

          // Retrieving tokens needed for one execution of subgraph in cluster
          final long expr1 = tokensOneExec * innerRep1;
          final long expr2 = tokensOneExec * innerRep;

          inputInterface2.getGraphPort().setExpression(expr2);
          inputInterface2.getDataPort().setExpression(expr2);

          forkInPort.setExpression(expr1 * (double) outerRep1 + expr2 * (double) outerRep2);
          forkOutPort1.setExpression(expr1 * (double) outerRep1);
          forkOutPort2.setExpression(expr2 * (double) outerRep2);

          // Log -> track forkActor creation
          if (verbose) {
            log = BalancingHelper.makeCompareLog(forkActor, topgraph, null);
            PreesmLogger.getLogger().info(log);
          }
        }
      }

      // ------------------------------------------------------------------------------------------- //
      // Creating join actors
      // ------------------------------------------------------------------------------------------- //
      long joinCounter = 0;
      interfaceIdx = 0;
      final int nbrOutputInterfaces = cluster1.getDataOutputInterfaces().size();
      while (interfaceIdx < nbrOutputInterfaces) {
        // Getting data input interfaces of the two clusters (that are the same)
        final DataOutputInterface outputInterface1 = cluster1.getDataOutputInterfaces().get(interfaceIdx);
        final DataOutputInterface outputInterface2 = cluster2.getDataOutputInterfaces().get(interfaceIdx);
        interfaceIdx++;

        // Creating join actor
        final JoinActor joinActor = PiMMUserFactory.instance.createJoinActor();
        joinActor.setName("join_" + joinCounter++ + "_" + cluster1Name);

        // Creating ports of fork actor
        final DataInputPort joinInPort1 = PiMMUserFactory.instance.createDataInputPort();
        final DataInputPort joinInPort2 = PiMMUserFactory.instance.createDataInputPort();
        final DataOutputPort joinOutPort = PiMMUserFactory.instance.createDataOutputPort();
        joinInPort1.setName("in_1");
        joinInPort2.setName("in_2");
        joinOutPort.setName("out");
        joinActor.getDataInputPorts().add(joinInPort1);
        joinActor.getDataInputPorts().add(joinInPort2);
        joinActor.getDataOutputPorts().add(joinOutPort);

        // Getting output port of data input interfaces
        final DataOutputPort cluster1OutPort = outputInterface1.getGraphPort();
        final DataOutputPort clister2OutPort = outputInterface2.getGraphPort();

        // Getting initial fifo, between interface and the previous actor
        final Fifo outFifo = cluster1OutPort.getFifo();

        // modifying the target port of fifo
        outFifo.setSourcePort(joinOutPort);
        final String fifosType = outFifo.getType();

        final Fifo fifoJoinCluster1 = PiMMUserFactory.instance.createFifo(cluster1OutPort, joinInPort1, fifosType);
        final Fifo fifoJoinCluster2 = PiMMUserFactory.instance.createFifo(clister2OutPort, joinInPort2, fifosType);

        topgraph.addActor(joinActor);
        topgraph.addFifo(fifoJoinCluster1);
        topgraph.addFifo(fifoJoinCluster2);

        // Retrieving tokens needed for one execution of subgraph in cluster
        final Long tokensOneExec = outputInterface2.getDataPort().getFifo().getSourcePort().getExpression()
            .evaluateAsLong();
        final long expr1 = tokensOneExec * innerRep1;
        final long expr2 = tokensOneExec * innerRep;

        outputInterface2.getGraphPort().setExpression(expr2);
        outputInterface2.getDataPort().setExpression(expr2);

        joinOutPort.setExpression(expr1 * (double) outerRep1 + expr2 * (double) outerRep2);
        joinInPort1.setExpression(expr1 * (double) outerRep1);
        joinInPort2.setExpression(expr2 * (double) outerRep2);

        // Log -> track joinActor creation
        if (verbose) {
          log = BalancingHelper.makeCompareLog(joinActor, topgraph, null);
          PreesmLogger.getLogger().info(log);
        }
      }
    }

    // Facilitate the memory reuse in cluster
    clusters.stream().forEach(ClusterSynthesisHelper::addSpecialActors);

    return clusters;

  }
}
