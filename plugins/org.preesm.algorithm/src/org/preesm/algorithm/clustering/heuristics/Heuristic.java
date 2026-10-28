package org.preesm.algorithm.clustering.heuristics;

import java.util.Map;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * This interface represents any kind of heuristic, that can be used in a {@link PiGraph graph} clustering process. If a
 * new heuristic is created, it has to be registered in the {@link HeuristicGetter} class.
 *
 * @author rcazoulat
 */
public interface Heuristic {

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
  public abstract void initHeuristicParameters(PiGraph graph, Scenario scenario, Design arch,
      Map<String, String> taskParameters);

}
