package org.ietr.preesm.throughput;

import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.preesm.core.scenario.PreesmScenario;

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
    System.out.println("------ Test ------");

    System.out.println("=> Liste des vertex :");
    for (SDFAbstractVertex actor : graph.vertexSet()) {
      try {
        System.out.println("\nname: " + actor.getName() + " kind: " + actor.getKind() + " rv: " + actor.getNbRepeat());
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

    /*
     * Remarques: ---------- ==> graph: graph.getAllVertices() : returns all actors of the graph graph.getHierarchicalVertexSet() : == graph.getAllVertices()
     * graph.vertexSet() : returns actors of current graph graph.edgeSet() : returns edges of the current graph
     * 
     * 
     * ==> Actors: actor.getName() : the name of the actor (use that) actor.getId() : the id of the actor (for port it differs from the name) actor.getKind() :
     * the kind of the actor : vertex or port (interface) actor.getNbRepeat() : the GLOBAL RV of the actor in the graph not the local RV !! actor.getSinks() :
     * list of source ports actor.getSources() : list of sink ports actor.getAssociatedEdge(input/output port).toString() : returns the edge connected to the
     * input/output port
     * 
     * 
     * 
     * ==> Edges: edge.toString() : print edge info. Example for e(A-p3--|d3|--c2->B) : A.b > B.a {d=3, p=3, c=2} edge.getSource() : source actor
     * edge.getTarget() : target actor edge.getSourceInterface() : source port edge.getTargetInterface() : target port edge.getProd() : production rate
     * edge.getCons() : consumption rate edge.getDelay() : initial marking
     * 
     */
    System.out.println("------------------");
  }
}
