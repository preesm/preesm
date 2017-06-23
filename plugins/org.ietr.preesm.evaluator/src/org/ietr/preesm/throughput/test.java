package org.ietr.preesm.throughput;

import java.util.ArrayList;
import java.util.Hashtable;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;

/**
 * @author hderoui
 * 
 *         class to test the Preesm graph structure
 */
public class test {

  /**
   * entry-point
   */
  public static void start(SDFGraph graph, PreesmScenario scenario) {

    // test sdf
    testSDFGraph(graph, scenario);

    // test scenarion
    testScenario(graph, scenario);

    // test hierarchy
    testHierarchy(graph, scenario);

  }

  /**
   * test an input SDF graph
   * 
   * @param graph
   *          SDF input graph
   */
  public static void testSDFGraph(SDFGraph graph, PreesmScenario scenario) {
    System.out.println("------ Test SDFGraph ------");

    System.out.println("=> Liste des vertex :");
    for (SDFAbstractVertex actor : graph.getAllVertices()) {
      try {
        System.out.println(actor.getKind() + "  " + actor.getName() + ", rv= " + actor.getNbRepeat() + ", dur="
            + scenario.getTimingManager().getTimingOrDefault(actor.getId(), "x86"));

        if (actor.getGraphDescription() != null) {
          System.out.println("Hierarchical duration = " + scenario.getTimingManager().generateVertexTimingFromHierarchy(actor, "x86"));
        }

      } catch (InvalidExpressionException e) {
        e.printStackTrace();
      }

      // System.out.println("==> inputs : ");
      // for (SDFInterfaceVertex input : actor.getSources()) {
      // System.out.println("\t input port name " + input.getName() + " : " + actor.getAssociatedEdge(input).toString());
      // }

      // System.out.println("==> outputs : ");
      // for (SDFInterfaceVertex output : actor.getSinks()) {
      // System.out.println("\t output port name " + output.getName() + " : " + actor.getAssociatedEdge(output).toString());
      // }
    }

    System.out.println("\n=> Liste des edges :");
    for (SDFEdge edge : graph.edgeSet()) {
      System.out.println("name: " + edge.toString());
      System.out.println("e(" + edge.getSource().getName() + "," + edge.getTarget().getName() + "), p(" + edge.getSourceInterface().getName() + ","
          + edge.getTargetInterface().getName() + "), prod=" + edge.getProd() + " cons= " + edge.getCons() + " M0= " + edge.getDelay());
    }

    System.out.println("---------------------------");
  }

  private static void testScenario(SDFGraph graph, PreesmScenario scenario) {
    System.out.println("------ Test Scenario ------");

    // list of SDF actors : returns only actors of the top graph
    for (String actor : scenario.getActorNames()) {
      System.out.println(actor);
    }

    // list of timings : empty list
    for (Timing timing : scenario.getTimingManager().getTimings()) {
      System.out.println(timing.getVertexId() + " = " + timing.getTime());
    }

    // default value of actors timing
    System.out.println("Task " + Timing.DEFAULT_TASK_TIME);
    System.out.println("Special vertex " + Timing.DEFAULT_SPECIAL_VERTEX_TIME);
    System.out.println("Special vertex expression " + Timing.DEFAULT_SPECIAL_VERTEX_EXPRESSION_VALUE);

    // set duration = 0 for interfaces and define time for hierarchical actors
    for (SDFAbstractVertex vertex : graph.getAllVertices()) {
      Timing timing = scenario.getTimingManager().getTimingOrDefault(vertex.getId(), "x86");
      if (timing.getVertexId() == "default") {
        // case vertex is an actor
        if (vertex.getKind() == "vertex") {
          // case the actor is a regular one
          if (vertex.getGraphDescription() == null) {
            scenario.getTimingManager().setTiming(vertex.getId(), "x86", 1);
          } else {
            // case the actor is a hierarchical one, compute its duration from the hierarchy
            scenario.getTimingManager().setTiming(vertex.getId(), "x86",
                scenario.getTimingManager().generateVertexTimingFromHierarchy(vertex, "x86").getTime());
          }
        } else {
          // case vertex is a port (input or output interface)
          scenario.getTimingManager().setTiming(vertex.getId(), "x86", 0);
        }
      }
    }

    // check the modification
    for (SDFAbstractVertex vertex : graph.getAllVertices()) {
      System.out.println(vertex.getKind() + " " + vertex.getName() + " dur=" + scenario.getTimingManager().getTimingOrDefault(vertex.getId(), "x86").getTime());
    }

    System.out.println("---------------------------");
  }

  /**
   * test the hierarchy of the graph
   * 
   * @param graph
   *          IBSDF graph
   * @param scenario
   *          scenario that contains the time manager
   */
  public static void testHierarchy(SDFGraph graph, PreesmScenario scenario) {
    System.out.println("------ Test Hierarchy ------");
    // list of hierarchical actors
    Hashtable<String, SDFAbstractVertex> HActorList = new Hashtable<String, SDFAbstractVertex>();
    ArrayList<SDFAbstractVertex> actorsToProcess = new ArrayList<SDFAbstractVertex>();

    // add hierarchical actors of the top graph
    for (SDFAbstractVertex actor : graph.vertexSet()) {
      if (actor.getGraphDescription() != null) {
        HActorList.put(actor.getName(), actor);
        actorsToProcess.add(actor);
      }
    }

    // add the rest hierarchical actors of the hierarchy
    while (!actorsToProcess.isEmpty()) {
      SDFAbstractVertex h = actorsToProcess.get(0);
      SDFGraph subGraph = (SDFGraph) h.getGraphDescription();
      for (SDFAbstractVertex actor : subGraph.vertexSet()) {
        if (actor.getGraphDescription() != null) {
          HActorList.put(actor.getName(), actor);
          actorsToProcess.add(actor);
        }
      }
      actorsToProcess.remove(0);
    }

    // print the hierarchical actors
    for (SDFAbstractVertex h : HActorList.values()) {
      System.out.println("H Actor " + h.getName() + " : rv=" + h.getNbRepeat() + " dur=" + scenario.getTimingManager().getTimingOrDefault(h.getId(), "x86"));
      System.out.println("Liste of subgraph Actors: ");
      SDFGraph subGraph = (SDFGraph) h.getGraphDescription();
      for (SDFAbstractVertex subActor : subGraph.vertexSet()) {
        if (subActor.getKind() == "vertex") {
          System.out.println("vertex " + subActor.getName());
        } else if (subActor instanceof SDFSinkInterfaceVertex) {
          System.out.println("Output interface " + subActor.getName());
        } else if (subActor instanceof SDFSourceInterfaceVertex) {
          System.out.println("Input interface " + subActor.getName());
        } else {
          System.out.println("???? " + subActor.getName());
        }
      }
    }

    System.out.println("----------------------------");
  }

  /*
   * Remarks:
   * 
   * ==> graph:
   * 
   * graph.getAllVertices() : returns all actors of the graph
   * 
   * graph.getHierarchicalVertexSet() : == graph.getAllVertices()
   * 
   * graph.vertexSet() : returns actors of current graph
   * 
   * graph.edgeSet() : returns edges of the current graph
   * 
   * 
   * 
   * ==> Actors:
   * 
   * actor.getName() : the name of the actor (use that)
   * 
   * actor.getId() : the id of the actor (for port it differs from the name)
   * 
   * actor.getKind() : the kind of the actor : vertex or port (interface)
   * 
   * actor.getNbRepeat() : the GLOBAL RV of the actor in the graph not the local RV !!
   * 
   * actor.getSinks() : list of source ports
   * 
   * actor.getSources() : list of sink ports
   * 
   * actor.getAssociatedEdge(input/output port).toString() : returns the edge connected to the input/output port
   * 
   * 
   * 
   * ==> Edges:
   * 
   * edge.toString() : print edge info. Example for e(A-p3--|d3|--c2->B) : A.b > B.a {d=3, p=3, c=2}
   * 
   * edge.getSource() : source actor
   * 
   * edge.getTarget() : target actor
   * 
   * edge.getSourceInterface() : source port
   * 
   * edge.getTargetInterface() : target port
   * 
   * edge.getProd() : production rate
   * 
   * edge.getCons() : consumption rate
   * 
   * edge.getDelay() : initial marking
   * 
   * 
   * ==> Scenario:
   * 
   * scenario.getTimingManager().getTimingOrDefault(actor.getId(), "x86") : return a Timing object (A, x86, dur)
   * 
   * timing.getVertexId() : returns the actor id
   * 
   * timing.getTime() : return actor duration
   * 
   * scenario.getTimingManager().generateVertexTimingFromHierarchy(actor, "x86") : returns the duration of a hierarchical actor when sub-actors are executing
   * sequentially
   * 
   * timing.DEFAULT_TASK_TIME : default duration of tasks (Ports and Hierarchical actors)= 100
   * 
   * Timing.DEFAULT_SPECIAL_VERTEX_TIME : default duration of Special vertices = 10
   * 
   * 
   * 
   * => Hierarchical actor:
   * 
   * actor.getGraphDescription() != null : means that actor is hierarchical
   * 
   * actor.getGraphDescription() : returns the subgraph of the hierarchical actor
   * 
   * 
   * 
   * TODO:
   * 
   * add a task to define actors parameters like timing collected from an input scenario
   * 
   */
}
