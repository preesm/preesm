package org.preesm.algorithm.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.preesm.algorithm.clustering.deprecated.EuclideTransfo;
import org.preesm.algorithm.clustering.heuristics.PartitionerHeuristic;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/***
 * I Swear I tried to keep this heuristic as simple as possible
 */
public class SimplePartitionerHeuristic extends PartitionerHeuristic {

  /**
   * Number of equivalent Processing Elements
   */
  long nPEs;

  /**
   * Basic repetition vector, one value for each vertex of the graph
   */
  Map<AbstractVertex, Long> brv;

  boolean verbose = false;

  @Override
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Design arch,
      Map<String, String> taskParameters) {

    super.initHeuristicParameters(graph, scenario, arch, taskParameters);

    // Computing number of equivalent cores
    this.nPEs = EuclideTransfo.computeSingleNodeCoreEquivalent(scenario);

    // Computing the basic repetition vector of the graph
    this.brv = PiBRV.compute(graph, BRVMethod.LCM);
  }

  @Override
  public List<PiGraph> balanceFirings(PiGraph topgraph, PiGraph cluster1) {
    /***
     *
     */
    final List<PiGraph> clusters = new ArrayList<>();
    clusters.add(cluster1);

    // Log utils
    final List<Long> cluster1OldExprs = cluster1.getAllDataPorts().stream()
        .map(dp -> dp.getExpression().evaluateAsLong()).toList();
    List<Long> oldExprs;
    String log;

    // Computing how much execution of the subgraph there is in the cluster
    // For example, if actor A is repeating 8 times and actor B 16 times, then clusterRepetition will be equal to 8.
    final long clusterRepetition = MathFunctionsHelper
        .gcd(cluster1.getActors().stream().filter(a -> brv.get(a) != null).map(a -> brv.get(a)).toList());

    // if nPEs is not a divisor of clusterRepetition
    final long rest = clusterRepetition % this.nPEs;

    // clusterRepetition, without the rest. Used to compute scale and scale1
    final long perfectClusterRep = clusterRepetition - rest;

    // Number of time the cluster will be repeated in top graph, without rest
    // Using gcd for now, we don't take into account delays.
    // TODO : use ClusteringHelper.computeScalingFactor instead -> for delay management
    final long perfectScale = MathFunctionsHelper.gcd(perfectClusterRep, this.nPEs);

    // Number of repetition of subgraph in cluster,
    final long perfectRatio = perfectClusterRep / perfectScale;

    // Number of time cluster1 will be repeated in top graph
    final long scale1 = perfectScale - rest; // if rest = 0, nothing happens

    // Number of time subgraph will be repeated in total, with cluster1 weights
    final long repetition1 = scale1 * perfectRatio; // if rest = 0, nothing happens

    // Number of repetition of subgraph in cluster1
    final long ratio1 = repetition1 / scale1;

    // Log
    if (verbose) {
      log = "[Partitioning] > rest = " + rest + ", scale 1 = " + scale1 + ", repetition 1 = " + repetition1
          + ", ratio1 = " + ratio1;
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

        // TODO : adapt this behavior to special actor, and then to passive actors !
        // If there is a broadcast actor just before the cluster, it is mandatory to
        // create a new one in the cluster, so that the memory optimizations of the
        // broadcast actor are made in the cluster too, and to don't fool the
        // broadcast actor in the subgraph, that will duplicate more data to the cluster.
        if (previousActor instanceof BroadcastActor) {

          // Log utils
          oldExprs = previousActor.getAllDataPorts().stream().map(dp -> dp.getExpression().evaluateAsLong()).toList();

          // 1. Modify output port of broadcast actor
          final DataOutputPort prevActorOutPort = dataInterface.getGraphPort().getFifo().getSourcePort();
          final long newExpr = tokensOneExec * scale1;
          prevActorOutPort.setExpression(newExpr);

          // Log -> track broadcast modification
          if (verbose) {
            log = makeCompareLog(previousActor, topgraph, oldExprs);
            PreesmLogger.getLogger().info(log);
          }

          // 2. Make a broadcast actor in the cluster to compensate
          final BroadcastActor brdActor = PiMMUserFactory.instance
              .createBroadcastActor(previousActor.getName() + "_in_cluster");
          cluster1.addActor(brdActor);

          final DataInputPort brdInPort = PiMMUserFactory.instance.createDataInputPort();
          final DataOutputPort brdOutPort = PiMMUserFactory.instance.createDataOutputPort();
          brdInPort.setName("in");
          brdOutPort.setName("out");
          brdInPort.setExpression(tokensOneExec);
          brdOutPort.setExpression(tokensOneExec * (double) ratio1);

          brdActor.getDataInputPorts().add(brdInPort);
          brdActor.getDataOutputPorts().add(brdOutPort);

          final Fifo inFifo = dataInterface.getDataPort().getFifo();
          final DataInputPort inPortNextActor = inFifo.getTargetPort();

          inFifo.setTargetPort(brdInPort);
          final Fifo outFifo = PiMMUserFactory.instance.createFifo(brdOutPort, inPortNextActor, inFifo.getType());
          cluster1.addFifo(outFifo);

          // Log -> track creation of brdActor in cluster1
          if (verbose) {
            log = makeCompareLog(brdActor, cluster1, null);
            PreesmLogger.getLogger().info(log);
          }

          expr = tokensOneExec;

        } else {

          expr = tokensOneExec * ratio1;
        }

        // Case if dataInterface is an output interface
      } else {
        expr = dataInterface.getDataPort().getFifo().getSourcePort().getExpression().evaluateAsLong() * ratio1;
      }

      // Top graph & subgraph ports values modifications
      dataInterface.getGraphPort().setExpression(expr);
      dataInterface.getDataPort().setExpression(expr);
    }

    // Log -> track cluster1 modification
    if (verbose) {
      log = makeCompareLog(cluster1, topgraph, cluster1OldExprs);
      PreesmLogger.getLogger().info(log);
    }
    // If the rest of division of the cluster repetition and the number of PEs is not equal to 0,
    // Then we have to duplicate the cluster in two, with two different repetitions, and two different scales.
    // For example, if a cluster is repeating itself 9 times (clusterRepetition = 9),
    // and there is 4 PEs (this.nPEs = 4), then there will be 2 clusters, because 9 % 4 = 1.
    // The first cluster will be repeated 3 times (scale with 2 instances of the cluster,
    // and the second will be repeated 1 time, but with 3 instances of the cluster (not the same weight).
    // In other words, if scale1 is not equal to 0, we have to duplicate the cluster in the top graph.
    // ------------------------------------------------------------------------------------------- //
    // Building cluster2, if needed
    // ------------------------------------------------------------------------------------------- //
    if (rest != 0) {

      // Creating a new cluster with scale2, and adding it to top graph, with all the necessary rooting
      final PiGraph cluster2 = PiMMUserFactory.instance.copyPiGraphWithHistory(cluster1);
      topgraph.addActor(cluster2);
      final String cluster1Name = cluster1.getName();
      cluster2.setName(cluster1Name + "_2");
      cluster1.setName(cluster1Name + "_1");
      clusters.add(cluster2);

      // Number of repetition of subgraph in cluster2
      final long ratio2 = perfectRatio + 1;

      // Number of time cluster2 will be repeated in top graph
      final long scale2 = rest;

      // Log
      if (verbose) {
        log = "[Partitioning] > scale 2 = " + scale2 + ", ratio2 = " + ratio2;
        PreesmLogger.getLogger().info(log);
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

        // If previous actor is a broadcast actor :
        // 1. Adding one output port for cluster2, in the (outer) broadcast
        // 2. Adding a broadcast actor in cluster2, if needed
        if (previousActor instanceof BroadcastActor) {

          // 1. Modifying outer broadcast

          // Log util
          oldExprs = previousActor.getAllDataPorts().stream().map(dp -> dp.getExpression().evaluateAsLong()).toList();

          // Modyfing out port 1 + creating out port 2
          final DataOutputPort brdOutPort1 = inputInterface1.getGraphPort().getFifo().getSourcePort();
          final DataOutputPort brdOutPort2 = PiMMUserFactory.instance.createDataOutputPort();
          previousActor.getDataOutputPorts().add(brdOutPort2);

          final String brdPortsName = brdOutPort1.getName();
          brdOutPort1.setName(brdPortsName + "_1");
          brdOutPort2.setName(brdPortsName + "_2");
          brdOutPort2.setExpression(tokensOneExec * (double) scale2);

          final Fifo brd2cluster2Fifo = PiMMUserFactory.instance.createFifo(brdOutPort2, inputInterface2.getGraphPort(),
              brdOutPort1.getFifo().getType());

          topgraph.addFifo(brd2cluster2Fifo);

          // Log -> track broadcast modification
          if (verbose) {
            log = makeCompareLog(previousActor, topgraph, oldExprs);
            PreesmLogger.getLogger().info(log);
          }

          // 2. Modify existing broadcast
          // Because cluster2 is a copy of cluster1, the broadcast is already created.
          // We just need to modify its ports expression.

          final BroadcastActor brdActor = (BroadcastActor) inputInterface2.getDataPort().getFifo().getTarget();
          final DataInputPort brdInPort = brdActor.getDataInputPorts().get(0);
          final DataOutputPort brdOutPort = brdActor.getDataOutputPorts().get(0);

          brdInPort.setExpression(tokensOneExec);
          brdOutPort.setExpression(tokensOneExec * (double) ratio2);

          // Log -> track brdActor creation in cluster2
          if (verbose) {
            log = makeCompareLog(brdActor, cluster2, null);
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

          final long expr1 = tokensOneExec * ratio1;
          final long expr2 = tokensOneExec * ratio2;

          inputInterface2.getGraphPort().setExpression(expr2);
          inputInterface2.getDataPort().setExpression(expr2);

          forkInPort.setExpression(inFifo.getSourcePort().getExpression().evaluateAsDouble());
          forkOutPort1.setExpression(expr1 * (double) scale1);
          forkOutPort2.setExpression(expr2 * (double) scale2);

          // Log -> track forkActor creation
          if (verbose) {
            log = makeCompareLog(forkActor, topgraph, null);
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
        final long expr1 = tokensOneExec * ratio1;
        final long expr2 = tokensOneExec * ratio2;

        outputInterface2.getGraphPort().setExpression(expr2);
        outputInterface2.getDataPort().setExpression(expr2);

        joinOutPort.setExpression(outFifo.getTargetPort().getExpression().evaluateAsDouble());
        joinInPort1.setExpression(expr1 * (double) scale1);
        joinInPort2.setExpression(expr2 * (double) scale2);

        // Log -> track joinActor creation
        if (verbose) {
          log = makeCompareLog(joinActor, topgraph, null);
          PreesmLogger.getLogger().info(log);
        }
      }

    }

    return clusters;

  }

  private static String makeCompareLog(final AbstractActor actor, final PiGraph containingGraph,
      final List<Long> oldExprs) {
    String log;
    final List<String> portsName = actor.getAllDataPorts().stream().map(dp -> dp.getName()).toList();
    final List<
        Long> newExprs = actor.getAllDataPorts().stream().map(dp -> dp.getExpression().evaluateAsLong()).toList();
    if (oldExprs == null) {
      log = "[Partitioning] >  Creating " + actor.getName() + " in " + containingGraph.getName() + " : ";
      for (int i = 0; i < portsName.size(); i++) {
        log += "\n       Port \"" + portsName.get(i) + "\", val = " + newExprs.get(i);
        log += defineProdConsLog(actor, portsName.get(i));
      }
    } else {
      final int nIter = Math.min(newExprs.size(), oldExprs.size());
      log = "[Partitioning] >  Modifying " + actor.getName() + " in " + containingGraph.getName() + " : ";
      for (int i = 0; i < nIter; i++) {
        log += "\n       Port \"" + portsName.get(i) + "\", old val = " + oldExprs.get(i) + ", new val = "
            + newExprs.get(i);
        log += defineProdConsLog(actor, portsName.get(i));

      }

      if (newExprs.size() < oldExprs.size()) {
        for (int i = nIter; i < oldExprs.size(); i++) {
          log += "\n       Unknown port removed. Old val = " + oldExprs.get(i);

        }
      } else {
        for (int i = nIter; i < newExprs.size(); i++) {
          log += "\n       Port \"" + portsName.get(i) + "\" added. Val = " + newExprs.get(i);
          log += defineProdConsLog(actor, portsName.get(i));

        }
      }
    }
    return log;
  }

  private static String defineProdConsLog(final AbstractActor actor, final String dpName) {
    String log = "";

    final DataPort dp = actor.getAllDataPorts().stream().filter(p -> p.getName() == dpName).toList().get(0);
    if (dp == null) {
      return log;
    }

    if (dp instanceof DataInputPort) {
      log += " <- out port \"" + dp.getFifo().getSourcePort().getName() + "\"of actor \""
          + dp.getFifo().getSource().getName() + "\"";
    } else {
      log += " -> in port \"" + dp.getFifo().getTargetPort().getName() + "\"of actor \""
          + dp.getFifo().getTarget().getName() + "\"";

    }
    return log;
  }
}
