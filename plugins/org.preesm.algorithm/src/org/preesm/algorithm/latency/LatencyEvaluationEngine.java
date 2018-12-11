/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2018)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.algorithm.latency;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.throughput.tools.helpers.GraphStructureHelper;
import org.preesm.algorithm.throughput.tools.helpers.Stopwatch;
import org.preesm.algorithm.throughput.tools.transformers.SDFTransformer;
import org.preesm.model.scenario.PreesmScenario;

/**
 * @author hderoui
 *
 */
public class LatencyEvaluationEngine {

  // list of replacement graphs
  private Map<String, SDFGraph> replacementSubgraphlList;
  private PreesmScenario        scenario;
  Stopwatch                     timer;

  /**
   * computes the maximum latency of the IBSDF graph which is equivalent to a single core execution
   *
   * @return maxLatency
   */
  public long getMinLatencySingleCore(final SDFGraph graph, final PreesmScenario scenario) {
    this.timer = new Stopwatch();
    this.timer.start();

    // sum l(a)*rv_global(a) -- not the local RV
    long minLatencySingleCore = 0;

    // loop actors of the graph
    for (final SDFAbstractVertex actor : graph.vertexSet()) {
      long actorLatency = 0;

      // define the latency of the actor
      if (actor.getGraphDescription() != null) {
        // case of hierarchical actor : compute its subgraph latency
        actorLatency = getSubgraphMinLatencySinlgeCore(actor, scenario);
      } else {
        // case of regular actor : get its latency from the scenario
        if (scenario != null) {
          actorLatency = scenario.getTimingManager().getTimingOrDefault(actor.getId(), "x86").getTime();
        } else {
          actorLatency = (long) actor.getPropertyBean().getValue("duration");
        }
      }

      // multiply the actor latency by its repetition factor
      minLatencySingleCore += actorLatency * actor.getNbRepeatAsLong();
    }

    this.timer.stop();
    System.out
        .println("Minimum Latency of the graph = " + minLatencySingleCore + " computed in " + this.timer.toString());

    return minLatencySingleCore;
  }

  /**
   * computes the maximum latency of a subgraph
   *
   * @return subgraph latency
   */
  public long getSubgraphMinLatencySinlgeCore(final SDFAbstractVertex hierarchicalActor,
      final PreesmScenario scenario) {
    // sum l(a)*rv_global(a) -- not the local RV
    long subgraphLatency = 0;

    // get the subgraph
    final SDFGraph subgraph = (SDFGraph) hierarchicalActor.getGraphDescription();

    // loop actors of the subgraph
    for (final SDFAbstractVertex actor : subgraph.vertexSet()) {
      long actorLatency = 0;

      // define the latency of the actor
      if (actor.getGraphDescription() != null) {
        // case of hierarchical actor : compute its subgraph latency
        actorLatency = getSubgraphMinLatencySinlgeCore(actor, scenario);
      } else {
        // case of regular actor : get its latency from the scenario
        if (scenario != null) {
          actorLatency = scenario.getTimingManager().getTimingOrDefault(actor.getId(), "x86").getTime();
        } else {
          actorLatency = (long) actor.getPropertyBean().getValue("duration");
        }
      }

      // multiply the actor latency by its repetition factor
      subgraphLatency += actorLatency * actor.getNbRepeatAsLong();
    }

    return subgraphLatency;
  }

  /**
   * computes the minimum latency of the IBSDF graph which is equivalent to a multi-core execution with unlimited number
   * of available cores
   *
   * @return minLatency
   */
  public double getMinLatencyMultiCore(final SDFGraph graph, final PreesmScenario scenario, final Boolean retiming) {

    /*
     * Algorithm
     *
     *
     * Step 1: Construct the replacement subgraph of the top graph hierarchical actors
     *
     * Step 2: convert the top graph to a DAG
     *
     * Step 3: replace the hierarchical actors by their replacement subgraph
     *
     * Step 4: compute the longest path of the top graph
     *
     */

    this.scenario = scenario;

    // re-time the IBSDF graph
    if (retiming) {
      GraphStructureHelper.retime(graph);
      System.out.println(
          "Computing the minimum Latency of the graph using the decomposition technique after a retinming phase ...");
    } else {
      System.out.println("Computing the minimum Latency of the graph using the decomposition technique ...");
    }

    this.timer = new Stopwatch();
    this.timer.start();

    // Step 1: Construct the replacement subgraph of the top graph hierarchical actors
    System.out.println("Step 1: Construct the replacement subgraph of toprgraph hierarchical actors");
    this.replacementSubgraphlList = new Hashtable<>();
    for (final SDFAbstractVertex actor : graph.vertexSet()) {
      if (actor.getGraphDescription() != null) {
        process(actor, (SDFGraph) actor.getGraphDescription());
      }
    }

    // Step 2: convert the top graph to a DAG
    System.out.println("Step 2: convert the top graph to a DAG");
    final SDFGraph topgraph_dag = SDFTransformer.convertToDAG(graph);

    // Step 3: replace the hierarchical actors by their replacement subgraph
    System.out.println("Step 3: replace the hierarchical actors by their replacement subgraph");
    final ArrayList<SDFAbstractVertex> actorToReplace = new ArrayList<>();
    for (final SDFAbstractVertex actor : topgraph_dag.vertexSet()) {
      if (((SDFAbstractVertex) actor.getPropertyBean().getValue("baseActor")).getGraphDescription() != null) {
        actorToReplace.add(actor);
      }
    }
    for (final SDFAbstractVertex actor : actorToReplace) {
      final SDFAbstractVertex baseActor = (SDFAbstractVertex) actor.getPropertyBean().getValue("baseActor");
      GraphStructureHelper.replaceHierarchicalActor(topgraph_dag, actor,
          this.replacementSubgraphlList.get(baseActor.getName()));
    }

    // Step 4: compute the longest path of the top graph
    System.out.println("Step 4: compute the longest path of the top graph");
    final double minLatency = GraphStructureHelper.getLongestPath(topgraph_dag, scenario, null);

    this.timer.stop();
    System.out.println("Minimum Latency of the graph = " + minLatency + " computed in " + this.timer.toString());

    return minLatency;
  }

  private void process(final SDFAbstractVertex h, final SDFGraph subgraph) {
    // Step1: process the hierarchical actors of the subgraph
    for (final SDFAbstractVertex actor : subgraph.vertexSet()) {
      if (actor.getGraphDescription() != null) {
        process(actor, (SDFGraph) actor.getGraphDescription());
      }
    }

    // Step 2: convert the subgraph to a DAG
    final SDFGraph subgraph_dag = SDFTransformer.convertToDAG(subgraph);

    // Step 3: replace the hierarchical actors by their replacement subgraph
    final ArrayList<SDFAbstractVertex> actorToReplace = new ArrayList<>();
    final ArrayList<String> subgraphExecutionModelToRemove = new ArrayList<>();
    for (final SDFAbstractVertex actor : subgraph_dag.vertexSet()) {
      final SDFAbstractVertex baseActor = (SDFAbstractVertex) actor.getPropertyBean().getValue("baseActor");
      if (baseActor.getGraphDescription() != null) {
        actorToReplace.add(actor);
        // add the parent actor to the list of subgraph execution model to remove
        if (!subgraphExecutionModelToRemove.contains(baseActor.getName())) {
          subgraphExecutionModelToRemove.add(baseActor.getName());
        }
      }
    }
    for (final SDFAbstractVertex actor : actorToReplace) {
      final SDFAbstractVertex baseActor = (SDFAbstractVertex) actor.getPropertyBean().getValue("baseActor");
      GraphStructureHelper.replaceHierarchicalActor(subgraph_dag, actor,
          this.replacementSubgraphlList.get(baseActor.getName()));
    }

    // delete all replacement graphs that are no longer needed
    for (final String actor : subgraphExecutionModelToRemove) {
      this.replacementSubgraphlList.remove(actor);
    }

    // Step 4: compute the longest path of the subgraph
    final SDFGraph replGraph = constructReplacementGraph(h, subgraph_dag);

    // save the replacement graph
    this.replacementSubgraphlList.put(h.getName(), replGraph);
  }

  /**
   * construct the replacement graph of the hierarchical actor
   *
   * @param h
   *          hierarchical actor
   * @param subgraph_dag
   *          DAG version of the subgraph in which all the sub-hierarchical actor was replaced by its replacement graph
   * @return replacement graph of the hierarchical actor
   */
  private SDFGraph constructReplacementGraph(final SDFAbstractVertex h, final SDFGraph subgraph_dag) {
    // version simple

    // construct the replacement graph of the hierarchical actor
    final SDFGraph replGraph = new SDFGraph();

    // Step 1: define the list of inputs and outputs
    final ArrayList<SDFAbstractVertex> inputActors = new ArrayList<>();
    final ArrayList<SDFAbstractVertex> outputActors = new ArrayList<>();

    // loop actors
    for (final SDFAbstractVertex actor : subgraph_dag.vertexSet()) {
      // check if the actor has no inputs
      if (actor.getSources().isEmpty()) {
        inputActors.add(actor);

        // create the associated actor in the replacement graph
        GraphStructureHelper.addActor(replGraph, actor.getName(), null, actor.getNbRepeatAsLong(), 0., 0,
            (SDFAbstractVertex) actor.getPropertyBean().getValue("baseActor"));
      }

      // check if the actor has no outputs
      if (actor.getSinks().isEmpty()) {
        outputActors.add(actor);

        // get actor duration
        double duration;
        if (this.scenario != null) {
          duration = this.scenario.getTimingManager().getTimingOrDefault(actor.getId(), "x86").getTime();
        } else {
          duration = (Double) actor.getPropertyBean().getValue("duration");
        }

        // create the associated actor in the replacement graph
        GraphStructureHelper.addActor(replGraph, actor.getName(), null, actor.getNbRepeatAsLong(), duration, 0,
            (SDFAbstractVertex) actor.getPropertyBean().getValue("baseActor"));
      }
    }

    // Step 2: sort actors
    final List<SDFAbstractVertex> topoSortList = GraphStructureHelper.topologicalSorting(subgraph_dag);

    // table of distances
    Map<String, Double> distance;

    // Step 3: for each input actor compute the longest path to the output actors
    for (final SDFAbstractVertex actor : inputActors) {
      distance = GraphStructureHelper.getLongestPathToAllTargets(actor, this.scenario, topoSortList);
      // for each output actor (if connected to the current input actor), add an actor with a duration equal
      // to the distance from the input actor and the output actor
      for (final SDFAbstractVertex output : outputActors) {
        final Double output_distance = distance.get(output.getName());
        if (output_distance != Double.NEGATIVE_INFINITY) {

          // add edge
          final SDFEdge e = GraphStructureHelper.addEdge(replGraph, actor.getName(), null, output.getName(), null, 1, 1,
              0, null);
          e.setPropertyValue("weight_LP", output_distance);
        }
      }

    }

    // Step 4: simplify the replacement graph

    return replGraph;
  }

}
