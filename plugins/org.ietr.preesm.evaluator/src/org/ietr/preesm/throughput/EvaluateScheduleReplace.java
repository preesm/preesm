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
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.schedule.ALAPScheduler_DAG;
import org.ietr.preesm.schedule.ASAPScheduler_DAG;
import org.ietr.preesm.schedule.PeriodicScheduler_SDF;
import org.ietr.preesm.throughput.helpers.GraphStructureHelper;
import org.ietr.preesm.throughput.parsers.Identifier;
import org.ietr.preesm.throughput.transformers.SDFTransformer;
import org.ietr.preesm.throughput.transformers.SrSDFTransformer;

/**
 * @author hderoui
 *
 */
public class EvaluateScheduleReplace {

  PreesmScenario preesmScenario;
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
   *          IBSDF graph
   * @param scenario
   *          contains actors duration
   * @return the throughput of the graph
   */
  public double evaluate(SDFGraph inputGraph, PreesmScenario scenario) {
    this.preesmScenario = scenario;

    System.out.println("Computing the throughput of the graph using Evaluate-Schedule-Replace (ESR) method ...");

    // Step 1: Construct the subgraph execution model for the hierarchical actors of the top graph
    System.out.println("Step 1: Construct the subgraph execution model for the hierarchical actors of the top graph");
    subgraphExecutionModelList = new Hashtable<String, SDFGraph>();
    for (SDFAbstractVertex actor : inputGraph.vertexSet()) {
      if (actor.getGraphDescription() != null) {
        constructSubgraphExecutionModel(actor, (SDFGraph) actor.getGraphDescription());
      }
    }

    // Step 2: convert the top graph to a srSDF graph
    System.out.println("Step 2: convert the top graph to a srSDF graph");
    SDFGraph srSDF = SDFTransformer.convertToSrSDF(inputGraph);

    // Step 3: replace the hierarchical actors by their subgraph execution model
    System.out.println("Step 3: replace the hierarchical actors by their subgraph execution model");
    ArrayList<SDFAbstractVertex> actorToReplace = new ArrayList<SDFAbstractVertex>();
    for (SDFAbstractVertex actor : srSDF.vertexSet()) {
      if (((SDFAbstractVertex) actor.getPropertyBean().getValue("baseActor")).getGraphDescription() != null) {
        actorToReplace.add(actor);
      }
    }
    for (SDFAbstractVertex actor : actorToReplace) {
      SDFAbstractVertex baseActor = (SDFAbstractVertex) actor.getPropertyBean().getValue("baseActor");
      GraphStructureHelper.replaceHierarchicalActor(srSDF, actor, subgraphExecutionModelList.get(baseActor.getName()));
    }

    // Step 4: compute the throughput of the top graph using the periodic schedule
    System.out.println("Step 4: compute the throughput using the Periodic Schedule");
    // normalize the graph
    SDFTransformer.normalize(srSDF);
    // compute its normalized period K
    PeriodicScheduler_SDF periodic = new PeriodicScheduler_SDF();
    Fraction k = periodic.computeNormalizedPeriod(srSDF, PeriodicScheduler_SDF.Method.LinearProgram_Gurobi);
    // compute its throughput as 1/K
    double throughput = 1 / k.doubleValue();
    System.out.println("Throughput of the graph = " + throughput);

    return throughput;
  }

  /**
   * Construct the subgraph execution model of a hierarchical actor
   * 
   * @param h
   *          a hierarchical actor
   * @param graph
   *          subgraph
   */
  private void constructSubgraphExecutionModel(SDFAbstractVertex h, SDFGraph graph) {
    // Recursive function
    for (SDFAbstractVertex actor : graph.vertexSet()) {
      if (actor.getGraphDescription() != null) {
        constructSubgraphExecutionModel(actor, (SDFGraph) actor.getGraphDescription());
      }
    }

    // Step 1: convert the SDF to srSDF
    SDFGraph srSDF = SDFTransformer.convertToSrSDF(graph);

    // Step 3: replace the hierarchical actors by their subgraph execution model
    ArrayList<SDFAbstractVertex> actorToReplace = new ArrayList<SDFAbstractVertex>();
    ArrayList<String> subgraphExecutionModelToRemove = new ArrayList<String>();
    for (SDFAbstractVertex actor : srSDF.vertexSet()) {
      SDFAbstractVertex baseActor = (SDFAbstractVertex) actor.getPropertyBean().getValue("baseActor");
      if (baseActor.getGraphDescription() != null) {
        actorToReplace.add(actor);
        // add the parent actor to the list of subgraph execution model to remove
        if (!subgraphExecutionModelToRemove.contains(baseActor.getName())) {
          subgraphExecutionModelToRemove.add(baseActor.getName());
        }
      }
    }
    for (SDFAbstractVertex actor : actorToReplace) {
      SDFAbstractVertex baseActor = (SDFAbstractVertex) actor.getPropertyBean().getValue("baseActor");
      GraphStructureHelper.replaceHierarchicalActor(srSDF, actor, subgraphExecutionModelList.get(baseActor.getName()));
    }

    // delete all replacement graphs that are no longer needed
    for (String actor : subgraphExecutionModelToRemove) {
      subgraphExecutionModelList.remove(actor);
    }

    // Step 4: construct the subgraph execution model of the hierarchical actor subgraph
    SDFGraph SubgraphExecutionModel = process(h, srSDF);

    // save the replacement graph
    subgraphExecutionModelList.put(h.getName(), SubgraphExecutionModel);
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
  private SDFGraph process(SDFAbstractVertex h, SDFGraph srSDF) {
    // Step 1: compute the normalized period K of the graph
    Fraction K = computeK(srSDF);

    // Step 2: convert the srSDF to DAG
    SDFGraph DAG = SrSDFTransformer.convertToDAG(srSDF);

    // Step 3: Schedule the subgraph ASAP + ALAP
    schedule(DAG);

    // Step 4: construct the subgraph execution model
    SDFGraph SubgraphExecutionModel = modelSEM(h, DAG, K);

    return SubgraphExecutionModel;
  }

  /**
   * compute the throughput of the graph CHECK
   * 
   * @param graph
   *          srSDF graph
   * @return normalized period K
   */
  private Fraction computeK(SDFGraph graph) {
    // normalize the graph first
    SDFTransformer.normalize(graph);
    // return the fraction k=L/H computed by the periodic schedule
    PeriodicScheduler_SDF scheduler = new PeriodicScheduler_SDF();
    return scheduler.computeNormalizedPeriod(graph, PeriodicScheduler_SDF.Method.LinearProgram_Gurobi);
  }

  /**
   * ASAP + ALAP schedule
   * 
   * @param graph
   *          DAG
   */
  private void schedule(SDFGraph graph) {
    // ASAP schedule to determine the start/finish date for each actor and the latency constraint
    ASAPScheduler_DAG ASAP_DAG = new ASAPScheduler_DAG();
    ASAP_DAG.schedule(graph, this.preesmScenario);

    // reset the execution counter of each actor
    ASAP_DAG.simulator.resetExecutionCounter();

    // step 2: ALAP schedule ESR paper version
    ALAPScheduler_DAG ALAP = new ALAPScheduler_DAG();
    ALAP.schedule(graph, ASAP_DAG.simulator, ASAP_DAG.dur1Iter);
  }

  // construction function
  private SDFGraph modelSEM(SDFAbstractVertex HActor, SDFGraph subgraph, Fraction K) {
    // construct the replacement graph for the hierarchical actor
    // step 1: get all interfaces execution start time and create a new actor for each interface
    // step 2: construct the time line
    // step 3: connect the interfaces to the time line
    // step 4: add the period actor to the time line

    // create the subgraph execution model
    SDFGraph subgraphExecutionModel = new SDFGraph();
    subgraphExecutionModel.setName(Identifier.generateSDFGraphId());

    // list of time line actors
    Hashtable<Double, SDFAbstractVertex> timeLineActors = new Hashtable<>();

    // create the interfaces and connect them to their associated time actor
    for (IInterface iInterface : HActor.getInterfaces()) {
      // add the interface to the subgraph execution model
      SDFAbstractVertex subgraphInterface = subgraph.getVertex(((SDFInterfaceVertex) iInterface).getName() + "_1");
      SDFAbstractVertex SEM_inetrface = GraphStructureHelper.addActor(subgraphExecutionModel, subgraphInterface.getName(), null,
          subgraphInterface.getNbRepeatAsInteger(), (Double) subgraphInterface.getPropertyBean().getValue("duration"), null,
          (SDFAbstractVertex) subgraphInterface.getPropertyBean().getValue("baseActor"));

      // get the execution start date of the interface
      Double startDate = (Double) subgraphInterface.getPropertyBean().getValue("startDate");

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
    ArrayList<Double> orderedTimeLine = new ArrayList<Double>(timeLineActors.keySet());
    Collections.sort(orderedTimeLine);

    // construct the time line by connecting the time actors using transition actors
    for (int i = 0; i < orderedTimeLine.size() - 1; i++) {
      // add the transition actor to the subgraph execution model
      SDFAbstractVertex TransitionActor = GraphStructureHelper.addActor(subgraphExecutionModel,
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
      double firstTime = orderedTimeLine.get(0);
      double lastTime = orderedTimeLine.get(orderedTimeLine.size() - 1);
      SDFAbstractVertex fistTimeActor = timeLineActors.get(firstTime);
      SDFAbstractVertex lastTimeActor = timeLineActors.get(lastTime);

      // create the period actor
      SDFAbstractVertex periodActor = GraphStructureHelper.addActor(subgraphExecutionModel, "period", null, 1, K.getNumerator() - (lastTime - firstTime), null,
          null);

      // connect the period actor to the time line
      GraphStructureHelper.addEdge(subgraphExecutionModel, lastTimeActor.getName(), null, periodActor.getName(), null, 1, 1, 0, null);
      GraphStructureHelper.addEdge(subgraphExecutionModel, periodActor.getName(), null, fistTimeActor.getName(), null, 1, 1, K.getDenominator(), null);
    }

    return subgraphExecutionModel;
  }

}
