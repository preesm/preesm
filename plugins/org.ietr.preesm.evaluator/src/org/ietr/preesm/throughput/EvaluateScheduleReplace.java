package org.ietr.preesm.throughput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import org.apache.commons.lang3.math.Fraction;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.schedule.PeriodicScheduler_SDF;
import org.ietr.preesm.throughput.parsers.Identifier;
import org.ietr.preesm.throughput.transformers.SDFTransformer;
import org.ietr.preesm.throughput.transformers.SrSDFTransformer;

/**
 * @author hderoui
 *
 */
public class EvaluateScheduleReplace {
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
      replace(srSDF, actor);
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
      replace(srSDF, actor);
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
   *          graph
   */
  private void schedule(SDFGraph graph) {
    // ASAP schedule to determine the start/finish date for each actor and the latency constraint
    // ASAPScheduler_DAG ASAP_DAG = new ASAPScheduler_DAG();
    // ASAP_DAG.schedule(graph, false);

    // reset the execution counter of each actor

    // step 2: ALAP schedule ESR paper version
    // ALAPScheduler_DAG ALAP = new ALAPScheduler_DAG();
    // ALAP.schedule(graph, ASAP_DAG.iterDur, false);
  }

  // construction function
  private SDFGraph modelSEM(SDFAbstractVertex h, SDFGraph graph, Fraction K) {
    boolean disconnected = true;
    // construct the replacement graph for the hierarchical actor
    // step 1: get all interfaces execution start time and create a new actor for each interface
    // step 2: construct the time line
    // step 3: connect the interfaces to the time line
    // step 4: add the period actor to the time line

    // list of interfaces
    Hashtable<String, Actor> interfaceActorList = new Hashtable<>();
    Hashtable<Double, ArrayList<Actor>> InterfacesClassifiedByTime = new Hashtable<Double, ArrayList<Actor>>();

    // the replacement graph
    SDFGraph replacementGraph = new SDFGraph(Identifier.generateSDFGraphId());

    // step 1
    // create an actor for each interface
    for (Actor a : graph.actors.values()) {
      // input interfaces
      if (a.BaseActor.type == Actor.Type.INPUTINTERFACE && ((HierarchicalActor) h).getInputInterfacePort(a.BaseActor.id) != null) {
        // create the new actor for the input interface
        Actor newIn = replacementGraph.createActor(h.id + "_" + a.id, 0., 1, null, a.BaseActor);
        interfaceActorList.put(a.id, newIn);

        // get the real start date and the first target actor : to replace with the start date of the interface directly
        // double startDate = Double.MAX_VALUE;
        // for (Edge e : a.OutputEdges.values())
        // if (e.targetActor.startDate < startDate) startDate = e.targetActor.startDate;
        double startDate = a.startDate;
        System.out.println("input interface " + a.id + " start at " + startDate);
        if (startDate != Double.POSITIVE_INFINITY) {
          disconnected = false;
        }

        if (InterfacesClassifiedByTime.containsKey(startDate)) {
          InterfacesClassifiedByTime.get(startDate).add(a);
        } else {
          InterfacesClassifiedByTime.put(startDate, new ArrayList<Actor>());
          InterfacesClassifiedByTime.get(startDate).add(a);
        }
      }

      // output interfaces
      if (a.BaseActor.type == Actor.Type.OUTPUTINTERFACE && ((HierarchicalActor) h).getOutputInterfacePort(a.BaseActor.id) != null) {

        // create the new actor for the output interface
        Actor newOut = replacementGraph.createActor(h.id + "_" + a.id, a.InputEdges.elements().nextElement().sourceActor.duration, 1, null, a.BaseActor);
        interfaceActorList.put(a.id, newOut);

        // get the real start date and the first target actor
        double startDate = a.startDate;
        startDate -= a.InputEdges.elements().nextElement().sourceActor.duration;
        if (InterfacesClassifiedByTime.containsKey(startDate)) {
          InterfacesClassifiedByTime.get(startDate).add(a);
        } else {
          InterfacesClassifiedByTime.put(startDate, new ArrayList<Actor>());
          InterfacesClassifiedByTime.get(startDate).add(a);
        }
      }
    }

    // step 2 & 3:
    // order the time line
    ArrayList<Double> orderedTimeLine = new ArrayList<Double>(InterfacesClassifiedByTime.keySet());
    Collections.sort(orderedTimeLine);

    // construct the time line
    Hashtable<Double, Actor> timeLineActor = new Hashtable<Double, Actor>();

    // create an actor for each time in the time line
    // connect the time line with the interfaces
    for (int i = 0; i < orderedTimeLine.size(); i++) {
      Double time = orderedTimeLine.get(i);
      Actor timeActor = replacementGraph.createActor(h.id + "_time" + time, 0., 1, null, null);
      timeLineActor.put(time, timeActor);
      for (Actor a : InterfacesClassifiedByTime.get(time)) {
        if (a.BaseActor.type == Actor.Type.INPUTINTERFACE) {
          replacementGraph.createEdge(null, interfaceActorList.get(a.id).id, null, timeActor.id, null, 1., 1., 0., null);
        } else {
          replacementGraph.createEdge(null, timeActor.id, null, interfaceActorList.get(a.id).id, null, 1., 1., 0., null);
        }
      }
    }

    // add the connections between the time line actors
    for (int i = 0; i < orderedTimeLine.size() - 1; i++) {
      // create the transition actor
      Actor TransitActor = replacementGraph.createActor(h.id + "_time" + orderedTimeLine.get(i) + "_" + orderedTimeLine.get(i + 1),
          orderedTimeLine.get(i + 1) - orderedTimeLine.get(i), 1, null, null);
      // add the edge connection
      replacementGraph.createEdge(null, timeLineActor.get(orderedTimeLine.get(i)).id, null, TransitActor.id, null, 1., 1., 0., null);
      replacementGraph.createEdge(null, TransitActor.id, null, timeLineActor.get(orderedTimeLine.get(i + 1)).id, null, 1., 1., 0., null);
    }

    // step 4
    // add the K loop to the time line
    if (graph.normalizedPeriod > 0) {
      if (!disconnected) {
        Double firstTime_value = null;
        Double lastTime_value = null;
        // if first != inifity
        if (orderedTimeLine.get(0) != Double.POSITIVE_INFINITY) {
          firstTime_value = orderedTimeLine.get(0);
          // if size = 1
          if (orderedTimeLine.size() <= 1) {
            lastTime_value = orderedTimeLine.get(0);
          } else {
            // if last == positif
            if (orderedTimeLine.get(orderedTimeLine.size() - 1) == Double.POSITIVE_INFINITY) {
              lastTime_value = orderedTimeLine.get(orderedTimeLine.size() - 2);
            } else {
              lastTime_value = orderedTimeLine.get(orderedTimeLine.size() - 1);
            }
          }

          Actor firstTime = timeLineActor.get(firstTime_value);
          Actor lastTime = timeLineActor.get(lastTime_value);
          Actor PeriodicActor = replacementGraph.createActor(h.id + "_periodic", K.numerator - lastTime_value + firstTime_value, 1, null, null);

          // add the back edge of the hierarchical actor
          replacementGraph.createEdge(null, lastTime.id, null, PeriodicActor.id, null, 1., 1., 0., null);
          replacementGraph.createEdge(null, PeriodicActor.id, null, firstTime.id, null, 1., 1., K.denominator, null);

        } else {
          // Actor PeriodicActor = replacementGraph.createActor(h.id + "_periodic", K.numerator, 1, null, null);
          // replacementGraph.createEdge(null, PeriodicActor.id, null, PeriodicActor.id, null, 1., 1., K.denominator, null);
        }
      } else {
        if (K.value() > this.MaxK) {
          this.MaxK = K.value();
        }
      }

    }
    return replacementGraph;
  }

  // replacement function
  private void replace(SDFGraph graph, SDFAbstractVertex h) {
    // replace an instance of the hierarchical actor by its replacement graph
    // step 1: clone the replacement graph of the hierarchical actor
    // step 2: connect the interfaces with source and target actors

    // step 1
    // clone the replacement graph
    SDFGraph replGraph = subgraphExecutionModelList.get(h.BaseActor.id);

    // clone actors
    Hashtable<String, Actor> timelinegraphActors = new Hashtable<>();
    Hashtable<String, Actor> timelinegraphInterfaces = new Hashtable<>();

    for (Actor t : replGraph.actors.values()) {
      String[] actorIDSplit = t.id.split("_");
      actorIDSplit[0] = h.id;
      String actorID = String.join("_", actorIDSplit);

      Actor nt = graph.createActor(actorID, t.duration, t.repetitionFactor, t.normalizationValue, t.BaseActor);
      timelinegraphActors.put(t.id, nt);

      if (t.BaseActor.type == Actor.Type.INPUTINTERFACE) {
        timelinegraphInterfaces.put(((HierarchicalActor) h.BaseActor).getInputInterfacePort(t.BaseActor.id), nt);
      }
      if (t.BaseActor.type == Actor.Type.OUTPUTINTERFACE) {
        timelinegraphInterfaces.put(((HierarchicalActor) h.BaseActor).getOutputInterfacePort(t.BaseActor.id), nt);
      }
    }
    // clone the edges
    for (Edge e : replGraph.edges.values()) {
      graph.createEdge(null, timelinegraphActors.get(e.sourceActor.id).id, null, timelinegraphActors.get(e.targetActor.id).id, null, e.cons, e.prod,
          e.initialMarking, e.BaseEdge);
    }

    // 2. connect the interfaces interface
    // process the input interfaces
    ArrayList<Edge> edgelist = new ArrayList<Edge>();
    for (Edge e : h.InputEdges.values()) {
      edgelist.add(e);
    }
    for (Edge e : edgelist) {
      e.replaceTargetActor(timelinegraphInterfaces.get(e.BaseEdge.targetActorPort));
    }

    // process the output interfaces
    edgelist = new ArrayList<Edge>();
    for (Edge e : h.OutputEdges.values()) {
      edgelist.add(e);
    }
    for (Edge e : edgelist) {
      e.replaceSourceActor(timelinegraphInterfaces.get(e.BaseEdge.sourceActorPort));

      // reveal the hidden delays
      // int n = HActorsOutputInterfaceCount.get(h.BaseActor.id).get(timelinegraphInterfaces.get(e.BaseEdge.sourceActorPort).BaseActor.id);
      // e.delay += (n*e.cons);
      // e.initialMarking = e.delay;
    }

    // remove the hierarchical actor from the top graph
    graph.actors.remove(h.id);
  }

}
