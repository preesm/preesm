package org.preesm.algorithm.parallelism.counter;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.graph.Vertex;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * Clustering Task
 *
 * @author orenaud
 *
 */
@PreesmTask(id = "parallelism.counter.task.identifier", name = "Parallelism Counter Task",
    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class) },
    outputs = { @Port(name = "count", type = int.class) })

public class ParallelismCounter extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    // Task inputs
    final PiGraph inputGraph = (PiGraph) inputs.get("PiMM");
    final Scenario scenario = (Scenario) inputs.get("scenario");

    final int levelCount = computeLevel(inputGraph);
    int parallelismCount = 0;
    final Map<AbstractVertex, Long> brv = PiBRV.compute(inputGraph, BRVMethod.LCM);

    for (int indexLevel = 1; indexLevel <= levelCount; indexLevel++) {
      final PiGraph lastLevel = computeLastLevel(brv, inputGraph);
      parallelismCount = Math.max(parallelismCount, computeParallelismCount(lastLevel, scenario));

    }

    final Map<String, Object> output = new HashMap<>();

    // return scenario updated
    output.put("count", parallelismCount);
    return output;
  }

  /**
   * Used to compute the sum of possible cluster
   *
   * @param graph
   *          graph
   */
  private int computeLevel(PiGraph graph) {
    // EList<AbstractActor> amount = graph.getAllActors();// all actor all level
    final int levelCount = graph.getAllChildrenGraphs().size() + 1; // sum (hierarchical actors) + top
    return levelCount;
  }

  /**
   * Used to search for the level to be clustered, the first level to clusterize is the one that contains actor the
   * biggest brv
   *
   * @param brv
   *          list of repetion vector
   */
  private PiGraph computeLastLevel(Map<AbstractVertex, Long> brv, PiGraph graph) {
    PiGraph lastLevel;
    Long brvMax = (long) 0;
    AbstractVertex vertexMax = null;

    // retrieve max brv

    for (final AbstractVertex v : brv.keySet()) {
      if (graph.getActorIndex() <= 1) {
        return lastLevel = graph;
      }
      if (brv.get(v) > brvMax) {
        brvMax = brv.get(v);
        vertexMax = v;
      }
    }

    // last level
    lastLevel = vertexMax.getContainingPiGraph();
    // first cluster its content if it exists
    while (!lastLevel.getChildrenGraphs().isEmpty()) {
      lastLevel = lastLevel.getChildrenGraphs().get(0);
    }

    return lastLevel;
  }

  /**
   * Used to compute parallelism count
   *
   * @param brv
   *          list of repetion vector
   */
  private int computeParallelismCount(PiGraph lastLevel, Scenario scenario) {
    final Map<Long, EList<AbstractActor>> parallelismCounter = new HashMap<>();
    int maxCount = 0;
    final EList<AbstractActor> la = null;
    // Init
    for (final DataInputInterface i : lastLevel.getDataInputInterfaces()) {
      la.add((AbstractActor) i.getDirectSuccessors().get(0));
    }
    for (final AbstractActor a : lastLevel.getActors()) {
      if (a.getDataInputPorts().isEmpty() && (a instanceof Actor || a instanceof SpecialActor)) {
        la.add(a);
      }
    }
    parallelismCounter.put(0L, la);
    la.clear();
    // Loop
    Long currentRank = 1L;
    int count = 0;
    for (final AbstractActor a : parallelismCounter.get(currentRank - 1L)) {
      for (final Vertex aa : a.getDirectSuccessors()) {
        boolean flag = false;
        for (final Vertex aaa : aa.getDirectPredecessors()) {
          if (parallelismCounter.containsValue(aaa)) {
            flag = true;
          }
        }
        if (flag = false) {
          la.add((AbstractActor) aa);
        }
      }
      count++;
      if (count == parallelismCounter.get(currentRank - 1L).size()) {
        count = 0;
        parallelismCounter.put(currentRank, la);
        la.clear();
        maxCount = Math.max(parallelismCounter.get(currentRank - 1L).size(),
            parallelismCounter.get(currentRank).size());
        currentRank++;
      }
    }
    return maxCount;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of parallelism counter Task";
  }

}
