package org.preesm.algorithm.clustering.heuristics;

import java.util.Map;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

public abstract class Heuristic {
  // Map<String, Object> params; // --> powerful relic of ancient time ...
  protected PiGraph  graph    = null;
  protected Scenario scenario = null;
  protected Design   arch     = null;

  /***
   *
   * @param graph
   *          the main graph to cluster
   * @param scenario
   *          the scenario of the graph
   * @param arch
   *          the architecture S-LAM Graph
   * @param taskParameters
   *          all the parameters of the workflow task, that could give useful informations to the heuristic
   */
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Design arch,
      Map<String, String> taskParameters) {
    this.graph = graph;
    this.scenario = scenario;
    this.arch = arch;

  }

}
