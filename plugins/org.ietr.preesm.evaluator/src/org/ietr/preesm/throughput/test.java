package org.ietr.preesm.throughput;

import java.util.ArrayList;
import java.util.Hashtable;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.throughput.transformers.SDFTransformer;

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

    // test the creation of an SDF graph
    SDFGraph g = testSDFGraphCreation(scenario);

    // test the srSDF conversion
    testSrSDFConversion(g, scenario);

  }

  /**
   * test an input SDF graph
   * 
   * @param graph
   *          SDF input graph
   */
  private static void testSDFGraph(SDFGraph graph, PreesmScenario scenario) {
    System.out.println("------ Test SDFGraph ------");

    System.out.println("=> Liste des vertex :");
    for (SDFAbstractVertex actor : graph.getAllVertices()) {
      try {
        System.out.println(actor.getKind() + "  " + actor.getName() + ", rv= " + actor.getNbRepeat() + ", dur="
            + scenario.getTimingManager().getTimingOrDefault(actor.getId(), "x86"));

        if (actor.getGraphDescription() != null) {
          // System.out.println("Hierarchical duration = " + scenario.getTimingManager().generateVertexTimingFromHierarchy(actor, "x86"));
        }

      } catch (InvalidExpressionException e) {
        e.printStackTrace();
      }

      System.out.println("==> inputs : ");
      for (SDFInterfaceVertex input : actor.getSources()) {
        System.out.println("\t input port name " + input.getName() + " : " + actor.getAssociatedEdge(input).toString());
      }

      System.out.println("==> outputs : ");
      for (SDFInterfaceVertex output : actor.getSinks()) {
        System.out.println("\t output port name " + output.getName() + " : " + actor.getAssociatedEdge(output).toString());
      }
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
            System.out
                .println("new vertex timing : " + vertex.getName() + "=" + scenario.getTimingManager().getTimingOrDefault(vertex.getId(), "x86").getTime());
          } else {
            // case the actor is a hierarchical one, compute its duration from the hierarchy
            scenario.getTimingManager().setTiming(vertex.getId(), "x86",
                scenario.getTimingManager().generateVertexTimingFromHierarchy(vertex, "x86").getTime());
            System.out
                .println("new h-vertex timing : " + vertex.getName() + "=" + scenario.getTimingManager().getTimingOrDefault(vertex.getId(), "x86").getTime());
          }
        } else {
          // case vertex is a port (input or output interface)
          scenario.getTimingManager().setTiming(vertex.getId(), "x86", 0);
          System.out
              .println("new interface timing : " + vertex.getName() + "=" + scenario.getTimingManager().getTimingOrDefault(vertex.getId(), "x86").getTime());
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
  private static void testHierarchy(SDFGraph graph, PreesmScenario scenario) {
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
          System.out.println("vertex " + subActor.getName() + ", rv=" + subActor.getNbRepeat() + ", dur="
              + scenario.getTimingManager().getTimingOrDefault(subActor.getId(), "x86").getTime());
        } else if (subActor instanceof SDFSinkInterfaceVertex) {
          System.out.println("Output interface " + subActor.getName() + ", rv=" + subActor.getNbRepeat() + ", dur="
              + scenario.getTimingManager().getTimingOrDefault(subActor.getId(), "x86").getTime());
        } else if (subActor instanceof SDFSourceInterfaceVertex) {
          System.out.println("Input interface " + subActor.getName() + ", rv=" + subActor.getNbRepeat() + ", dur="
              + scenario.getTimingManager().getTimingOrDefault(subActor.getId(), "x86").getTime());
        } else {
          System.out.println("???? " + subActor.getName());
        }
      }
    }

    System.out.println("----------------------------");
  }

  /**
   * test the manipulation of an SDF graph.
   * 
   * create an SDF graph ABC
   * 
   */
  private static SDFGraph testSDFGraphCreation(PreesmScenario scenario) {
    System.out.println("------ Test SDF graph generation ------");

    System.out.println("create an SDF graph ...");
    // create a graph
    SDFGraph graph = new SDFGraph();
    graph.setName("test");

    // Add some actors
    SDFVertex actor;
    // actor A
    actor = new SDFVertex(graph);
    actor.setId("A");
    actor.setName("A");
    graph.addVertex(actor);

    // actor B
    actor = new SDFVertex(graph);
    actor.setId("B");
    actor.setName("B");
    graph.addVertex(actor);

    // actor C
    actor = new SDFVertex(graph);
    actor.setId("C");
    actor.setName("C");
    // => add an source port
    SDFInterfaceVertex portIn = new SDFSourceInterfaceVertex();
    portIn.setId("b");
    portIn.setName("b");
    portIn.setPropertyValue("port_rate", 3);
    actor.addInterface(portIn);
    // => add a sink port
    SDFInterfaceVertex portOut = new SDFSinkInterfaceVertex();
    portOut.setId("a");
    portOut.setName("a");
    portOut.setPropertyValue("port_rate", 7);
    actor.addInterface(portOut);
    graph.addVertex(actor);

    // Add some edges
    SDFAbstractVertex srcActor;
    SDFInterfaceVertex srcPort;

    // edge AB
    srcActor = graph.getVertex("A");
    srcPort = new SDFSinkInterfaceVertex();
    srcPort.setId("b");
    srcPort.setName("b");
    srcActor.addInterface(srcPort);

    SDFAbstractVertex tgtActor;
    SDFInterfaceVertex tgtPort;

    tgtActor = graph.getVertex("B");
    tgtPort = new SDFSourceInterfaceVertex();
    tgtPort.setId("a");
    tgtPort.setName("a");
    tgtActor.addInterface(tgtPort);

    SDFEdge edge;

    edge = graph.addEdge(srcActor, srcPort, tgtActor, tgtPort);
    edge.setPropertyValue("edgeName", "AB");
    edge.setProd(new SDFIntEdgePropertyType(3));
    edge.setCons(new SDFIntEdgePropertyType(7));
    edge.setDelay(new SDFIntEdgePropertyType(0));

    // edge BC
    srcActor = graph.getVertex("B");
    srcPort = new SDFSinkInterfaceVertex();
    srcPort.setId("c");
    srcPort.setName("c");
    srcActor.addInterface(srcPort);

    tgtActor = graph.getVertex("C");
    tgtPort = tgtActor.getInterface("b");

    edge = graph.addEdge(srcActor, srcPort, tgtActor, tgtPort);
    edge.setPropertyValue("edgeName", "BC");
    edge.setProd(new SDFIntEdgePropertyType(2));
    edge.setCons(new SDFIntEdgePropertyType((Integer) tgtPort.getPropertyBean().getValue("port_rate")));
    edge.setDelay(new SDFIntEdgePropertyType(4));

    // edge CA
    srcActor = graph.getVertex("C");
    srcPort = srcActor.getInterface("a");

    tgtActor = graph.getVertex("A");
    tgtPort = new SDFSourceInterfaceVertex();
    tgtPort.setId("c");
    tgtPort.setName("c");
    tgtActor.addInterface(tgtPort);

    edge = graph.addEdge(srcActor, srcPort, tgtActor, tgtPort);
    edge.setPropertyValue("edgeName", "CA");
    edge.setProd(new SDFIntEdgePropertyType((Integer) srcPort.getPropertyBean().getValue("port_rate")));

    edge.setCons(new SDFIntEdgePropertyType(2));
    edge.setDelay(new SDFIntEdgePropertyType(0));

    System.out.println("SDF graph created !!");
    System.out.println("Print the graph ...");

    testSDFGraph(graph, scenario);

    System.out.println("----------------------------");
    return graph;
  }

  /**
   * test the conversion of SDF graph to srSDF graph
   * 
   * @param graph
   *          SDF graph
   * @param scenario
   *          contains actors duration
   */
  private static void testSrSDFConversion(SDFGraph graph, PreesmScenario scenario) {
    System.out.println("------ Test srSDF convertion ------");
    // convert the SDF graph to a srSDF graph
    SDFGraph srSDF = SDFTransformer.convertToSrSDF(graph);
    // print the srSDF graph
    testSDFGraph(srSDF, scenario);
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
   * scenario.getTimingManager().setTiming(vertex.getId(), "x86", 0): set a duration for an actor. time need o be a strict positive number otherwise 1 is set
   * 
   * 
   * => Hierarchical actor:
   * 
   * actor.getGraphDescription() != null : means that actor is hierarchical
   * 
   * actor.getGraphDescription() : returns the subgraph of the hierarchical actor
   * 
   * 
   * => Interfaces:
   * 
   * subActor instanceof SDFSourceInterfaceVertex : to test if a vertex is an input interface
   * 
   * subActor instanceof SDFSinkInterfaceVertex : to test if a vertex is an output interface
   * 
   * 
   * 
   * TODO:
   * 
   * add a task to define actors parameters like timing collected from an input scenario
   * 
   * change time > 0 to >= : time can equal to 0 for interfaces
   * 
   * RV of interfaces is not computed, it is always equal to 1. => the consumption/production rate of the input/output edges of interfaces need to be multiplied
   * by the real local RV of the interfaces
   * 
   * 
   */
}
