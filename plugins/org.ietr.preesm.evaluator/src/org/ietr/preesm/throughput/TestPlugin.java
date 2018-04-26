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
import java.util.Hashtable;
import org.ietr.dftools.algorithm.model.IInterface;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.deadlock.IBSDFConsistency;
import org.ietr.preesm.throughput.tools.helpers.GraphStructureHelper;
import org.ietr.preesm.throughput.tools.helpers.Stopwatch;
import org.ietr.preesm.throughput.tools.transformers.IBSDFTransformer;

/**
 * @author hderoui
 *
 *         class to test the Preesm graph structure
 */
public class TestPlugin {

  /**
   * entry-point
   */
  public static void start(final SDFGraph graph, final PreesmScenario scenario) {

    // // test sdf
    // testSDFGraph(graph, scenario);
    //
    // // test scenarion
    // testScenario(graph, scenario);
    //
    // // test hierarchy
    // testHierarchy(graph, scenario);
    //
    // // test the creation of an SDF graph
    // SDFGraph g = testSDFGraphCreation(scenario);
    //
    // // test the srSDF conversion
    // testSrSDFConversion(g, scenario);
    //
    // // test the creation of an IBSDF graph
    // testIBSDFGraphCreation(scenario);

    // test structure of the graph
    // testStructure(graph, scenario);

    // test periodic schedule
    // testPeriodicSchedule(graph, scenario);

    // test ALAP schedule
    // testIterationDurationShouldBeComputed();

    // test IBSDF to srSDF
    // testIBSDFGraphShouldBeTranformedToFlatSrSDFGraph();

    // test critical path
    TestPlugin.testCriticalPath();

  }

  /**
   * test an input SDF graph
   *
   * @param graph
   *          SDF input graph
   */

  private static void testSDFGraph(final SDFGraph graph, final PreesmScenario scenario) {
    System.out.println("------ Test SDFGraph ------");

    System.out.println("=> Liste des vertex :");
    for (final SDFAbstractVertex actor : graph.getAllVertices()) {
      try {
        System.out.println(actor.getKind() + "  " + actor.getName() + ", rv= " + actor.getNbRepeat() + ", dur="
            + scenario.getTimingManager().getTimingOrDefault(actor.getId(), "x86"));

        if (actor.getGraphDescription() != null) {
          // System.out.println("Hierarchical duration = " + scenario.getTimingManager().generateVertexTimingFromHierarchy(actor, "x86"));
        }

      } catch (final InvalidExpressionException e) {
        e.printStackTrace();
      }

      System.out.println("\t interfaces : ");
      for (final IInterface inter : actor.getInterfaces()) {
        final SDFInterfaceVertex input = (SDFInterfaceVertex) inter;
        System.out.println("\t\t input port name " + input.getName() + " : " + actor.getAssociatedEdge(input).toString());
      }

      System.out.println("\t inputs : ");
      for (final SDFInterfaceVertex input : actor.getSources()) {
        System.out.println("\t\t input port name " + input.getName() + " : " + actor.getAssociatedEdge(input).toString());
      }

      System.out.println("\t outputs : ");
      for (final SDFInterfaceVertex output : actor.getSinks()) {
        System.out.println("\t\t output port name " + output.getName() + " : " + actor.getAssociatedEdge(output).toString());
      }
    }

    System.out.println("\n=> Liste des edges :");
    for (final SDFEdge edge : graph.edgeSet()) {
      System.out.println("name: " + edge.toString());
      System.out.println("e(" + edge.getSource().getName() + "," + edge.getTarget().getName() + "), p(" + edge.getSourceInterface().getName() + ","
          + edge.getTargetInterface().getName() + "), prod=" + edge.getProd() + " cons= " + edge.getCons() + " M0= " + edge.getDelay());
    }

    for (final SDFAbstractVertex actor : graph.getAllVertices()) {
      if (actor.getGraphDescription() != null) {
        System.out.println("edges of " + actor.getName());
        for (final SDFEdge edge : ((SDFGraph) actor.getGraphDescription()).edgeSet()) {
          System.out.println("name: " + edge.toString());
          System.out.println("e(" + edge.getSource().getName() + "," + edge.getTarget().getName() + "), p(" + edge.getSourceInterface().getName() + ","
              + edge.getTargetInterface().getName() + "), prod=" + edge.getProd() + " cons= " + edge.getCons() + " M0= " + edge.getDelay());
        }
      }
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

  private static void testHierarchy(final SDFGraph graph, final PreesmScenario scenario) {
    System.out.println("------ Test Hierarchy ------");
    // list of hierarchical actors
    final Hashtable<String, SDFAbstractVertex> HActorList = new Hashtable<>();
    final ArrayList<SDFAbstractVertex> actorsToProcess = new ArrayList<>();

    // add hierarchical actors of the top graph
    for (final SDFAbstractVertex actor : graph.vertexSet()) {
      if (actor.getGraphDescription() != null) {
        HActorList.put(actor.getName(), actor);
        actorsToProcess.add(actor);
      }
    }

    // add the rest hierarchical actors of the hierarchy
    while (!actorsToProcess.isEmpty()) {
      final SDFAbstractVertex h = actorsToProcess.get(0);
      final SDFGraph subGraph = (SDFGraph) h.getGraphDescription();
      for (final SDFAbstractVertex actor : subGraph.vertexSet()) {
        if (actor.getGraphDescription() != null) {
          HActorList.put(actor.getName(), actor);
          actorsToProcess.add(actor);
        }
      }
      actorsToProcess.remove(0);
    }

    // print the hierarchical actors
    for (final SDFAbstractVertex h : HActorList.values()) {
      System.out.println("H Actor " + h.getName() + " : rv=" + h.getNbRepeat() + " dur=" + scenario.getTimingManager().getTimingOrDefault(h.getId(), "x86"));
      System.out.println("Liste of subgraph Actors: ");
      final SDFGraph subGraph = (SDFGraph) h.getGraphDescription();
      for (final SDFAbstractVertex subActor : subGraph.vertexSet()) {
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

  private static SDFGraph testSDFGraphCreation(final PreesmScenario scenario) {
    System.out.println("------ Test SDF graph generation ------");

    System.out.println("create an SDF graph ...");
    // create a graph
    final SDFGraph graph = new SDFGraph();
    graph.setName("test");

    // // Add some actors
    // SDFVertex actor;
    // // actor A
    // actor = new SDFVertex(graph);
    // actor.setId("A");
    // actor.setName("A");
    // graph.addVertex(actor);
    //
    // // actor B
    // actor = new SDFVertex(graph);
    // actor.setId("B");
    // actor.setName("B");
    // graph.addVertex(actor);
    //
    // // actor C
    // actor = new SDFVertex(graph);
    // actor.setId("C");
    // actor.setName("C");
    // // => add an source port
    // SDFInterfaceVertex portIn = new SDFSourceInterfaceVertex();
    // portIn.setId("b");
    // portIn.setName("b");
    // portIn.setPropertyValue("port_rate", 3);
    // actor.addInterface(portIn);
    // // => add a sink port
    // SDFInterfaceVertex portOut = new SDFSinkInterfaceVertex();
    // portOut.setId("a");
    // portOut.setName("a");
    // portOut.setPropertyValue("port_rate", 7);
    // actor.addInterface(portOut);
    // graph.addVertex(actor);

    // // Add some edges
    // SDFAbstractVertex srcActor;
    // SDFInterfaceVertex srcPort;
    //
    // // edge AB
    // srcActor = graph.getVertex("A");
    // srcPort = new SDFSinkInterfaceVertex();
    // srcPort.setId("b");
    // srcPort.setName("b");
    // srcActor.addInterface(srcPort);
    //
    // SDFAbstractVertex tgtActor;
    // SDFInterfaceVertex tgtPort;
    //
    // tgtActor = graph.getVertex("B");
    // tgtPort = new SDFSourceInterfaceVertex();
    // tgtPort.setId("a");
    // tgtPort.setName("a");
    // tgtActor.addInterface(tgtPort);
    //
    // SDFEdge edge;
    //
    // edge = graph.addEdge(srcActor, srcPort, tgtActor, tgtPort);
    // edge.setPropertyValue("edgeName", "AB");
    // edge.setProd(new SDFIntEdgePropertyType(3));
    // edge.setCons(new SDFIntEdgePropertyType(7));
    // edge.setDelay(new SDFIntEdgePropertyType(0));
    //
    // // edge BC
    // srcActor = graph.getVertex("B");
    // srcPort = new SDFSinkInterfaceVertex();
    // srcPort.setId("c");
    // srcPort.setName("c");
    // srcActor.addInterface(srcPort);
    //
    // tgtActor = graph.getVertex("C");
    // tgtPort = tgtActor.getInterface("b");
    //
    // edge = graph.addEdge(srcActor, srcPort, tgtActor, tgtPort);
    // edge.setPropertyValue("edgeName", "BC");
    // edge.setProd(new SDFIntEdgePropertyType(2));
    // edge.setCons(new SDFIntEdgePropertyType((Integer) tgtPort.getPropertyBean().getValue("port_rate")));
    // edge.setDelay(new SDFIntEdgePropertyType(4));
    //
    // // edge CA
    // srcActor = graph.getVertex("C");
    // srcPort = srcActor.getInterface("a");
    //
    // tgtActor = graph.getVertex("A");
    // tgtPort = new SDFSourceInterfaceVertex();
    // tgtPort.setId("c");
    // tgtPort.setName("c");
    // tgtActor.addInterface(tgtPort);
    //
    // edge = graph.addEdge(srcActor, srcPort, tgtActor, tgtPort);
    // edge.setPropertyValue("edgeName", "CA");
    // edge.setProd(new SDFIntEdgePropertyType((Integer) srcPort.getPropertyBean().getValue("port_rate")));
    // edge.setCons(new SDFIntEdgePropertyType(2));
    // edge.setDelay(new SDFIntEdgePropertyType(0));

    GraphStructureHelper.addActor(graph, "A", null, null, 1., null, null);
    GraphStructureHelper.addActor(graph, "B", null, null, 1., null, null);
    GraphStructureHelper.addActor(graph, "C", null, null, 1., null, null);
    GraphStructureHelper.addSrcPort(graph.getVertex("C"), "b", 3);
    GraphStructureHelper.addSinkPort(graph.getVertex("C"), "a", 7);

    GraphStructureHelper.addEdge(graph, "A", "b", "B", "a", 3, 7, 0, null);
    GraphStructureHelper.addEdge(graph, "B", "c", "C", "b", 2, 3, 4, null);
    GraphStructureHelper.addEdge(graph, "C", "a", "A", "c", 7, 2, 0, null);

    System.out.println("SDF graph created !!");
    System.out.println("Print the graph ...");

    for (final SDFAbstractVertex actor : graph.vertexSet()) {
      System.out.println("Function:test: duration=" + actor.getPropertyBean().getValue("duration") + " actorName=" + actor.getName());
    }

    // testSDFGraph(graph, scenario);

    System.out.println("----------------------------");
    return graph;
  }

  /**
   * @return SDF Graph
   */

  private static SDFGraph generateDAGOfGraphABC() {
    // Actors: A B C
    // Edges: AC=(1,1); BC=(1,1);
    // RV[A=1, B=1, C=1]
    // Actors duration: A=5, B=2, C=7
    // Duration of the first iteration = 12

    // create DAG testABC
    final SDFGraph graph = new SDFGraph();
    graph.setName("testABC");

    // add actors
    GraphStructureHelper.addActor(graph, "A", null, 1, 5., null, null);
    GraphStructureHelper.addActor(graph, "B", null, 1, 2., null, null);
    GraphStructureHelper.addActor(graph, "C", null, 1, 7., null, null);

    // add edges
    GraphStructureHelper.addEdge(graph, "A", null, "C", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "B", null, "C", null, 1, 1, 0, null);

    return graph;
  }

  /**
   *
   */
  private static void testCriticalPath() {
    final Stopwatch timer = new Stopwatch();

    // generate an IBSDF graph
    final SDFGraph ibsdf = TestPlugin.generateIBSDFGraph();

    // flatten the hierarchy
    final SDFGraph flatSrSDF = IBSDFTransformer.convertToSrSDF(ibsdf, false);

    // topological sorting
    timer.start();
    final ArrayList<SDFAbstractVertex> topologicalSorting = GraphStructureHelper.topologicalSorting(flatSrSDF);
    timer.stop();

    System.out.println("topological sorting computed in " + timer.toString() + ", the ordered actors: ");
    for (final SDFAbstractVertex actor : topologicalSorting) {
      System.out.println(actor.getName() + " ");
    }

  }

  /**
   * generate an IBSDF graph to test methods
   *
   * @return IBSDF graph
   */
  private static SDFGraph generateIBSDFGraph() {
    // Actors: A B[DEF] C
    // actor B is a hierarchical actor described by the subgraph DEF
    // a is the input interface of the subgraph DEF, associated with the input edge coming from A
    // the input interface is linked to the sub-actor E
    // c is the output interface of the subgraph DEF, associated with the output edge going to C
    // the output interface is linked to the sub-actor F

    // Edges of the top graph ABC : AB=(3,2); BC=(1,1); CA=(2,3)
    // Edges of the subgraph DEF : aE=(2,1); EF=(2,3); FD=(1,2); DE=(3,1); Fc=(3,1)

    // RV(top-graph) = [A=2, B=3, C=3]
    // RV(sub-graph) = [D=2, E=6, F=4]
    // after computing the RV of the subgraph the consumption/production rate of the interfaces are multiplied by their RV, then RV of interfaces is set to 1
    // the resulted rates of edges : aE=(6,1); Fc=(3,12)

    // create the subgraph
    final SDFGraph subgraph = new SDFGraph();
    subgraph.setName("subgraph");
    GraphStructureHelper.addActor(subgraph, "D", null, null, 1., null, null);
    GraphStructureHelper.addActor(subgraph, "E", null, null, 1., null, null);
    GraphStructureHelper.addActor(subgraph, "F", null, null, 1., null, null);
    GraphStructureHelper.addInputInterface(subgraph, "a", null, 0., null, null);
    GraphStructureHelper.addOutputInterface(subgraph, "c", null, 0., null, null);

    GraphStructureHelper.addEdge(subgraph, "a", null, "E", null, 2, 1, 0, null);
    GraphStructureHelper.addEdge(subgraph, "E", null, "F", null, 2, 3, 0, null);
    GraphStructureHelper.addEdge(subgraph, "F", null, "D", null, 1, 2, 0, null);
    GraphStructureHelper.addEdge(subgraph, "D", null, "E", null, 3, 1, 3, null);
    GraphStructureHelper.addEdge(subgraph, "F", null, "c", null, 3, 1, 0, null);

    // create the top graph and add the subgraph to the hierarchical actor B
    final SDFGraph topgraph = new SDFGraph();
    topgraph.setName("topgraph");
    GraphStructureHelper.addActor(topgraph, "A", null, null, 1., null, null);
    GraphStructureHelper.addActor(topgraph, "B", subgraph, null, null, null, null);
    GraphStructureHelper.addActor(topgraph, "C", null, null, 1., null, null);

    GraphStructureHelper.addEdge(topgraph, "A", null, "B", "a", 3, 2, 3, null);
    GraphStructureHelper.addEdge(topgraph, "B", "c", "C", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(topgraph, "C", null, "A", null, 2, 3, 3, null);

    IBSDFConsistency.computeRV(topgraph);
    return topgraph;
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
