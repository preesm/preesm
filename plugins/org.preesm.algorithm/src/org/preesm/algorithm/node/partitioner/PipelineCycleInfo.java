package org.preesm.algorithm.node.partitioner;

import java.util.ArrayList;
import java.util.List;
import org.preesm.algorithm.clustering.scape.ClusteringScape;
import org.preesm.commons.graph.Vertex;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;
import org.preesm.model.scenario.Scenario;

/**
 * This Class identifies cycles and pipelines in the graph
 *
 * @author orenaud
 */
public class PipelineCycleInfo {
  /**
   * Input graph.
   */
  PiGraph                                 graph;
  /**
   * Input scenario.
   */
  Scenario                                scenario;
  /**
   * List of pipeline delays
   */
  private final List<Delay>               pipelineDelays;
  /**
   * List of cycle delays
   */
  private final List<Delay>               cycleDelays;
  /**
   * Actor list for each cycle
   */
  private final List<List<AbstractActor>> cycleActors;

  public PipelineCycleInfo(Scenario scenario) {
    this.scenario = scenario;
    this.graph = scenario.getAlgorithm();

    this.pipelineDelays = new ArrayList<>();
    this.cycleDelays = new ArrayList<>();
    this.cycleActors = new ArrayList<>();
  }

  public void execute() {
    for (final Delay delay : graph.getAllDelays()) {
      final List<AbstractActor> visitedActors = new ArrayList<>();
      final AbstractActor a = (AbstractActor) delay.getContainingFifo().getSource();
      final AbstractActor b = (AbstractActor) delay.getContainingFifo().getTarget();
      if (isPipeline(a, b, visitedActors)) {
        pipelineDelays.add(delay);
      } else {
        cycleDelays.add(delay);
        cycleActors.add(visitedActors);
      }
    }
  }

  /**
   * Recursive method for finding a path from B to A
   *
   * @param start
   *          current node
   * @param target
   *          target node
   * @param visitedActors
   *          List of visited actors
   *
   */
  private static boolean hasPathToA(AbstractActor start, AbstractActor target, List<AbstractActor> visitedActors) {
    if (start.equals(target)) {
      // If the current node is equal to the target, path is found.
      visitedActors.add(start);
      return true;
    }

    // Explore the directSuccessors of the current node
    for (final Vertex successor : start.getDirectSuccessors()) {
      // Check whether the successor is different from the starting node to avoid a double check
      if (!successor.equals(start) && !visitedActors.contains(successor)) {
        visitedActors.add(start);
        // Recursive call to explore the successor
        if (hasPathToA((AbstractActor) successor, target, visitedActors)) {
          return true; // There's a path found
        }
      }
    }
    return false;
  }

  public static boolean isPipeline(AbstractActor a, AbstractActor b, List<AbstractActor> visitedActors) {
    return !hasPathToA(b, a, visitedActors);
  }

  public void removeCycle() {
    if (!cycleActors.isEmpty()) {
      int index = 0;
      for (final List<AbstractActor> cycle : cycleActors) {
        final PiGraph subGraph = new PiSDFSubgraphBuilder(this.graph, cycle, "cycle_" + index++).build();
        // extract delay
        final Delay delay = subGraph.getDelays().get(0);
        // connect target delay to a new input interface
        final DataInputPort delayedActorPortIn = delay.getContainingFifo().getTargetPort();
        final String type = delay.getContainingFifo().getType();
        final Long expressionIn = delayedActorPortIn.getExpression().evaluate();

        final DataInputInterface delayInterface = PiMMUserFactory.instance.createDataInputInterface();
        delayInterface.setContainingGraph(subGraph);
        delayInterface.getDataOutputPorts().get(0).setExpression(expressionIn);
        delayInterface.getGraphPort().setExpression(expressionIn);
        delayInterface.setName(delayedActorPortIn.getName() + "_in");
        delayInterface.getGraphPort().setName(delayedActorPortIn.getName() + "_in");

        final Fifo fifin = PiMMUserFactory.instance.createFifo(delayInterface.getDataOutputPorts().get(0),
            delayedActorPortIn, type);
        fifin.setContainingGraph(subGraph);

        delay.getContainingFifo().setTargetPort((DataInputPort) delayInterface.getGraphPort());

        // connect source delay to a new output interface
        final DataOutputPort delayedActorPortout = delay.getContainingFifo().getSourcePort();
        final Long expressionOut = delayedActorPortout.getExpression().evaluate();

        final DataOutputInterface delayInterfaceOut = PiMMUserFactory.instance.createDataOutputInterface();
        delayInterfaceOut.setContainingGraph(subGraph);
        delayInterfaceOut.getDataInputPorts().get(0).setExpression(expressionOut);
        delayInterfaceOut.getGraphPort().setExpression(expressionOut);
        delayInterfaceOut.setName(delayedActorPortout.getName() + "_out");
        delayInterfaceOut.getGraphPort().setName(delayedActorPortout.getName() + "_out");

        final Fifo fifout = PiMMUserFactory.instance.createFifo(delayedActorPortout,
            delayInterfaceOut.getDataInputPorts().get(0), type);
        fifout.setContainingGraph(subGraph);

        delay.getContainingFifo().setSourcePort((DataOutputPort) delayInterfaceOut.getGraphPort());

        delay.setContainingGraph(graph);
        delay.getActor().setContainingGraph(graph);
        ClusteringScape.cluster(subGraph, scenario, 100000000000000L);
      }

    }
  }

  public List<Delay> getPipelineDelays() {
    return pipelineDelays;
  }

  public List<Delay> getCycleDelays() {
    return cycleDelays;
  }

  public List<List<AbstractActor>> getCycleActors() {
    return cycleActors;
  }

}
