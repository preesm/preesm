package org.preesm.algorithm.node.partitioner;

import java.util.ArrayList;
import java.util.List;
import org.preesm.commons.graph.Vertex;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.PiGraph;

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

  public PipelineCycleInfo(PiGraph graph) {
    this.graph = graph;
    this.pipelineDelays = new ArrayList<>();
    this.cycleDelays = new ArrayList<>();
    this.cycleActors = new ArrayList<>();
  }

  public void execute() {
    for (final Delay delay : graph.getAllDelays()) {
      // if (delay.getLevel().equals(PersistenceLevel.PERMANENT)) {
      final List<AbstractActor> visitedActors = new ArrayList<>();
      final AbstractActor a = (AbstractActor) delay.getContainingFifo().getSource();
      final AbstractActor b = (AbstractActor) delay.getContainingFifo().getTarget();
      if (isPipeline(a, b, visitedActors)) {
        pipelineDelays.add(delay);
      } else {
        cycleDelays.add(delay);
        cycleActors.add(visitedActors);
      }
      // }
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
