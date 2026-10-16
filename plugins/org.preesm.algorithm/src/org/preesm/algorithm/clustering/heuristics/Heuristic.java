package org.preesm.algorithm.clustering.heuristics;

import java.util.Map;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * This class represents any kind of heuristic, that can be used in a {@link PiGraph graph} clustering process. If a new
 * heuristic is created, it can be registered in the {@link HeuristicGetter} class.
 *
 * @author rcazoulat
 */
public abstract class Heuristic {

  /**
   * The {@link top graph} of the algorithm, accessible to every {@link Heuristic heuristics} if not set to null.
   */
  protected PiGraph graph = null;

  /**
   * The {@link Scenario scenario}, accessible to every {@link Heuristic heuristics} if not set to null.
   */
  protected Scenario scenario = null;

  /**
   * The {@link Design architecture}, accessible to every {@link Heuristic heuristics} if not set to null.
   */
  protected Design arch = null;

  /***
   *
   * @param graph
   *          the main {@link PiGraph graph} to cluster
   * @param scenario
   *          the {@link Scenario scenario} of the graph
   * @param arch
   *          the {@link Design architecture} S-LAM Graph
   * @param taskParameters
   *          all the parameters of the workflow {@link Clustering2Task task}, that could give useful informations to
   *          the {@link Heuristic heuristic}
   */
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Design arch,
      Map<String, String> taskParameters) {
    this.graph = graph;
    this.scenario = scenario;
    this.arch = arch;

  }

}
