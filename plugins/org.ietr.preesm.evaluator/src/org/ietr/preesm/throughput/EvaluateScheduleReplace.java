/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2017 - 2018)
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
package org.ietr.preesm.throughput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import org.apache.commons.lang3.math.Fraction;
import org.ietr.dftools.algorithm.model.IInterface;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.preesm.schedule.ALAPScheduler_DAG;
import org.ietr.preesm.schedule.ASAPScheduler_DAG;
import org.ietr.preesm.schedule.PeriodicScheduler_SDF;
import org.ietr.preesm.throughput.tools.helpers.GraphStructureHelper;
import org.ietr.preesm.throughput.tools.helpers.Stopwatch;
import org.ietr.preesm.throughput.tools.parsers.Identifier;
import org.ietr.preesm.throughput.tools.transformers.SDFTransformer;
import org.ietr.preesm.throughput.tools.transformers.SrSDFTransformer;

/**
 * @author hderoui
 *
 */
public class EvaluateScheduleReplace {

  public Stopwatch timer;
  // private PreesmScenario preesmScenario;
  /*
   * Evaluate-Schedule-Replace technique : Evaluate the throughput of a relaxed execution of an ibsdf graph. It consists of three main process, Evaluate,
   * Schedule and Replace. The technique analyze the subgraph in terms of time dependencies and replace it with a small graph that represents its execution
   * behavior :o :o :o
   *
   */

  // list of replacement graphs
  private Hashtable<String, SDFGraph> subgraphExecutionModelList;

  /**
   * Compute the throughput of an IBSDF graph using the Evaluate-Schedule-Replace method
   *
   * @param inputGraph
   *          IBSDF graph contains actors duration
   * @return the throughput of the graph
   */
  public double evaluate(final SDFGraph inputGraph) {
    // this.preesmScenario = scenario;
    // Re-timing the IBSDF graph
    GraphStructureHelper.retime(inputGraph);

    System.out.println("Computing the throughput of the graph using Evaluate-Schedule-Replace (ESR) method ...");
    this.timer = new Stopwatch();
    this.timer.start();

    // Step 1: Construct the subgraph execution model for the hierarchical actors of the top graph
    System.out.println("Step 1: Construct the subgraph execution model for the hierarchical actors of the top graph");
    this.subgraphExecutionModelList = new Hashtable<>();
    for (final SDFAbstractVertex actor : inputGraph.vertexSet()) {
      if (actor.getGraphDescription() != null) {
        buildSEM(actor, (SDFGraph) actor.getGraphDescription());
      }
    }

    // Step 2: convert the top graph to a srSDF graph
    System.out.println("Step 2: convert the top graph to a srSDF graph");
    final SDFGraph srSDF = SDFTransformer.convertToSrSDF(inputGraph);

    // Step 3: replace the hierarchical actors by their subgraph execution model
    System.out.println("Step 3: replace the hierarchical actors by their subgraph execution model");
    final ArrayList<SDFAbstractVertex> actorToReplace = new ArrayList<>();
    for (final SDFAbstractVertex actor : srSDF.vertexSet()) {
      if (((SDFAbstractVertex) actor.getPropertyBean().getValue("baseActor")).getGraphDescription() != null) {
        actorToReplace.add(actor);
      }
    }
    for (final SDFAbstractVertex actor : actorToReplace) {
      final SDFAbstractVertex baseActor = (SDFAbstractVertex) actor.getPropertyBean().getValue("baseActor");
      GraphStructureHelper.replaceHierarchicalActor(srSDF, actor, this.subgraphExecutionModelList.get(baseActor.getName()));
    }

    // Step 4: compute the throughput of the top graph using the periodic schedule
    System.out.println("Step 4: compute the throughput using the Periodic Schedule");
    // normalize the graph
    SDFTransformer.normalize(srSDF);
    // compute its normalized period K
    final PeriodicScheduler_SDF periodic = new PeriodicScheduler_SDF();
    final Fraction k = periodic.computeNormalizedPeriod(srSDF, PeriodicScheduler_SDF.Method.LinearProgram_Gurobi);
    // compute its throughput as 1/K
    final double throughput = 1 / k.doubleValue();
    this.timer.stop();
    System.out.println("Throughput of the graph = " + throughput + " computed in " + this.timer.toString());

    return throughput;
  }

  /**
   * build the Subgraph Execution Model of a hierarchical actor
   *
   * @param h
   *          a hierarchical actor
   * @param graph
   *          subgraph
   */
  private void buildSEM(final SDFAbstractVertex h, final SDFGraph graph) {
    // Recursive function
    for (final SDFAbstractVertex actor : graph.vertexSet()) {
      if (actor.getGraphDescription() != null) {
        buildSEM(actor, (SDFGraph) actor.getGraphDescription());
      }
    }

    // Step 1: convert the SDF to srSDF
    final SDFGraph srSDF = SDFTransformer.convertToSrSDF(graph);

    // Step 3: replace the hierarchical actors by their subgraph execution model
    final ArrayList<SDFAbstractVertex> actorToReplace = new ArrayList<>();
    final ArrayList<String> subgraphExecutionModelToRemove = new ArrayList<>();
    for (final SDFAbstractVertex actor : srSDF.vertexSet()) {
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
      GraphStructureHelper.replaceHierarchicalActor(srSDF, actor, this.subgraphExecutionModelList.get(baseActor.getName()));
    }

    // delete all replacement graphs that are no longer needed
    for (final String actor : subgraphExecutionModelToRemove) {
      this.subgraphExecutionModelList.remove(actor);
    }

    // Step 4: construct the subgraph execution model of the hierarchical actor subgraph
    final SDFGraph SubgraphExecutionModel = process(h, srSDF);

    // save the replacement graph
    this.subgraphExecutionModelList.put(h.getName(), SubgraphExecutionModel);
  }

  /**
   * process a subgraph : model its best execution by a small srSDF graph
   *
   * @param h
   *          hierarchical actor
   * @param graph
   *          srSDF graph
   * @return subgraph execution model
   */
  private SDFGraph process(final SDFAbstractVertex h, final SDFGraph srSDF) {
    // Step 1: compute the normalized period K of the graph
    final Fraction K = computeK(srSDF);

    // Step 2: convert the srSDF to DAG
    final SDFGraph DAG = SrSDFTransformer.convertToDAG(srSDF);

    // Step 3: Schedule the subgraph ASAP + ALAP
    schedule(DAG);

    // Step 4: construct the subgraph execution model
    final SDFGraph SubgraphExecutionModel = assembleSEM(h, DAG, K);

    return SubgraphExecutionModel;
  }

  /**
   * compute the throughput of the graph CHECK
   *
   * @param graph
   *          srSDF graph
   * @return normalized period K
   */
  private Fraction computeK(final SDFGraph graph) {
    // normalize the graph first
    SDFTransformer.normalize(graph);
    // return the fraction k=L/H computed by the periodic schedule
    final PeriodicScheduler_SDF scheduler = new PeriodicScheduler_SDF();
    return scheduler.computeNormalizedPeriod(graph, PeriodicScheduler_SDF.Method.LinearProgram_Gurobi);
  }

  /**
   * ASAP + ALAP schedule
   *
   * @param graph
   *          DAG
   */
  private void schedule(final SDFGraph graph) {
    // ASAP schedule to determine the start/finish date for each actor and the latency constraint
    final ASAPScheduler_DAG ASAP_DAG = new ASAPScheduler_DAG();
    ASAP_DAG.schedule(graph);

    // reset the execution counter of each actor
    ASAP_DAG.simulator.resetExecutionCounter();

    // step 2: ALAP schedule ESR paper version
    final ALAPScheduler_DAG ALAP = new ALAPScheduler_DAG();
    ALAP.schedule(graph, ASAP_DAG.simulator, ASAP_DAG.dur1Iter);
  }

  /**
   * assemble the Subgraph Execution Model of the hierarchical actor
   *
   * @param HActor
   *          hierarchical actor
   * @param subgraph
   *          the subgraph of the hierarchical actor (scheduled by ASAP+ALAP)
   * @param K
   *          the normalized period of the graph
   * @return subgraph execution model
   */
  private SDFGraph assembleSEM(final SDFAbstractVertex HActor, final SDFGraph subgraph, final Fraction K) {
    // construct the replacement graph for the hierarchical actor
    // step 1: get all interfaces execution start time and create a new actor for each interface
    // step 2: construct the time line
    // step 3: connect the interfaces to the time line
    // step 4: add the period actor to the time line

    // create the subgraph execution model
    final SDFGraph subgraphExecutionModel = new SDFGraph();
    subgraphExecutionModel.setName(Identifier.generateSDFGraphId());

    // list of time line actors
    final Hashtable<Double, SDFAbstractVertex> timeLineActors = new Hashtable<>();

    // create the interfaces and connect them to their associated time actor
    for (final IInterface iInterface : HActor.getInterfaces()) {
      // add the interface to the subgraph execution model
      final SDFAbstractVertex subgraphInterface = subgraph.getVertex(((SDFInterfaceVertex) iInterface).getName() + "_1");
      final SDFAbstractVertex SEM_inetrface = GraphStructureHelper.addActor(subgraphExecutionModel, subgraphInterface.getName(), null,
          subgraphInterface.getNbRepeatAsInteger(), (Double) subgraphInterface.getPropertyBean().getValue("duration"), null,
          (SDFAbstractVertex) subgraphInterface.getPropertyBean().getValue("baseActor"));

      // get the execution start date of the interface
      final Double startDate = (Double) subgraphInterface.getPropertyBean().getValue("startDate");

      // get the associated time actor, if not yet exists then create one and add it to the subgraph execution model
      SDFAbstractVertex timeActor = null;
      if (timeLineActors.containsKey(startDate)) {
        timeActor = timeLineActors.get(startDate);
      } else {
        timeActor = GraphStructureHelper.addActor(subgraphExecutionModel, "time" + startDate, null, 1, 0., null, null);
        timeLineActors.put(startDate, timeActor);
      }

      // connect the interface to its associated time line actor
      if (iInterface instanceof SDFSourceInterfaceVertex) {
        // case of input interface : add an edge from the interface to the time actor
        GraphStructureHelper.addEdge(subgraphExecutionModel, SEM_inetrface.getName(), null, timeActor.getName(), null, 1, 1, 0, null);
      } else if (iInterface instanceof SDFSinkInterfaceVertex) {
        // case of output interface : add an edge from the time actor to the interface
        GraphStructureHelper.addEdge(subgraphExecutionModel, timeActor.getName(), null, SEM_inetrface.getName(), null, 1, 1, 0, null);
      }
    }

    // sort the time actors and connect them by transition actors
    final ArrayList<Double> orderedTimeLine = new ArrayList<>(timeLineActors.keySet());
    Collections.sort(orderedTimeLine);

    // construct the time line by connecting the time actors using transition actors
    for (int i = 0; i < (orderedTimeLine.size() - 1); i++) {
      // add the transition actor to the subgraph execution model
      final SDFAbstractVertex TransitionActor = GraphStructureHelper.addActor(subgraphExecutionModel,
          "time" + orderedTimeLine.get(i) + "_to_time" + orderedTimeLine.get(i + 1), null, 1, orderedTimeLine.get(i + 1) - orderedTimeLine.get(i), null, null);

      // add time actor i with the time actor i+1 through the transition actor
      GraphStructureHelper.addEdge(subgraphExecutionModel, timeLineActors.get(orderedTimeLine.get(i)).getName(), null, TransitionActor.getName(), null, 1, 1, 0,
          null);
      GraphStructureHelper.addEdge(subgraphExecutionModel, TransitionActor.getName(), null, timeLineActors.get(orderedTimeLine.get(i + 1)).getName(), null, 1,
          1, 0, null);
    }

    // add the period actor
    if (K.doubleValue() > 0) {
      // get the first and the last time actor of time line
      final double firstTime = orderedTimeLine.get(0);
      final double lastTime = orderedTimeLine.get(orderedTimeLine.size() - 1);
      final SDFAbstractVertex fistTimeActor = timeLineActors.get(firstTime);
      final SDFAbstractVertex lastTimeActor = timeLineActors.get(lastTime);

      // create the period actor
      final SDFAbstractVertex periodActor = GraphStructureHelper.addActor(subgraphExecutionModel, "period", null, 1, K.getNumerator() - (lastTime - firstTime),
          null, null);

      // connect the period actor to the time line
      GraphStructureHelper.addEdge(subgraphExecutionModel, lastTimeActor.getName(), null, periodActor.getName(), null, 1, 1, 0, null);
      GraphStructureHelper.addEdge(subgraphExecutionModel, periodActor.getName(), null, fistTimeActor.getName(), null, 1, 1, K.getDenominator(), null);
    }

    return subgraphExecutionModel;
  }

}
