package org.ietr.preesm.throughput.transformers;

import java.util.Hashtable;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.preesm.throughput.parsers.GraphStructureHelper;

public abstract class IBSDFTransformer {

  /**
   * Converts an IBSDF graph to a flat srSDF graph
   * 
   * @param IBSDF
   *          graph
   * @return flat srSDF graph
   */
  public static SDFGraph convertTosrSDF(SDFGraph IBSDF, boolean withExecRulres) {
    // Step 1: Convert all the SDF subgraphs to a srSDF subgraphs
    Hashtable<String, SDFGraph> srSDFsubgraphList = convertAllSubgraphs(IBSDF, withExecRulres);

    // Step 2: Convert the top SDF graph to a srSDF graph
    SDFGraph flatSrSDF = SDFTransformer.convertToSrSDF(IBSDF);

    // Step 3: Replace each instance of a hierarchical actor by its srSDF subgraph version
    // get the hierarchical actors of the top graph
    Hashtable<String, SDFAbstractVertex> actorsToReplcae = new Hashtable<String, SDFAbstractVertex>();
    for (SDFAbstractVertex a : flatSrSDF.vertexSet()) {
      if (a.getGraphDescription() != null) {
        actorsToReplcae.put(a.getName(), a);
      }
    }

    // loop the list until it be empty
    while (actorsToReplcae.size() != 0) {
      // // actor to process
      // Actor h = actorsToReplcae.elements().nextElement();
      //
      // // get its single-rate subgraph
      // SDFGraph srSubgraph = srSubGraphs.get(h.BaseActor.id);
      //
      // // add every actor and edge of the subgraph to the single-rate graph
      // for (Actor a : srSubgraph.actors.values()) {
      // if (a.type == Actor.Type.HIERARCHICAL)
      // actorsToReplcae.put(h.id + "_" + a.id, singleRate.createHierarchicalActor(h.id + "_" + a.id, a.duration, 1, null, a.BaseActor));
      // else
      // singleRate.createActor(h.id + "_" + a.id, a.duration, 1, null, a.BaseActor);
      // }
      // for (Edge e : srSubgraph.edges.values())
      // singleRate.createEdge(null, h.id + "_" + e.sourceActor.id, null, h.id + "_" + e.targetActor.id, null, e.cons, e.prod, e.initialMarking, e.BaseEdge);
      //
      // // replace the hierarchical actor
      // // connect the input edges to interfaces
      // ArrayList<Edge> edges = new ArrayList<Edge>();
      // for (Edge e : h.InputEdges.values())
      // edges.add(e);
      //
      // for (Edge e : edges) {
      // if (withRulres) {
      // Actor targetActor = singleRate.actors.get(h.id + "_IN");
      // e.replaceTargetActor(targetActor);
      // } else {
      // String interfacePort = e.BaseEdge.targetActorPort;
      // String interfaceId = ((HierarchicalActor) h.BaseActor).InputInterfaces.get(interfacePort);
      // Actor targetActor = singleRate.actors.get(h.id + "_" + interfaceId + "_1");
      // e.replaceTargetActor(targetActor);
      // }
      // }
      // // connect the output edges to interfaces
      // edges = new ArrayList<Edge>();
      // for (Edge e : h.OutputEdges.values())
      // edges.add(e);
      //
      // for (Edge e : edges) {
      // if (withRulres) {
      // Actor sourceActor = singleRate.actors.get(h.id + "_OUT");
      // e.replaceSourceActor(sourceActor);
      // } else {
      // String interfacePort = e.BaseEdge.sourceActorPort;
      // String interfaceId = ((HierarchicalActor) h.BaseActor).OutputInterfaces.get(interfacePort);
      // Actor sourceActor = singleRate.actors.get(h.id + "_" + interfaceId + "_1");
      // e.replaceSourceActor(sourceActor);
      // }
      // }
      // // remove the hierarchical actor from the single-rate graph
      // singleRate.actors.remove(h.id);
      //
      // // remove the actor from the list of actors to process
      // actorsToReplcae.remove(h.id);
    }

    return flatSrSDF;
  }

  private static Hashtable<String, SDFGraph> convertAllSubgraphs(SDFGraph IBSDF, boolean withExecRulres) {
    // add the hierarchical actors of the top graph
    Hashtable<String, SDFAbstractVertex> actorsToProcess = new Hashtable<String, SDFAbstractVertex>();
    for (SDFAbstractVertex a : IBSDF.vertexSet()) {
      if (a.getGraphDescription() != null) {
        actorsToProcess.put(a.getName(), a);
      }
    }

    // get all the hierarchical actors of the hierarchy
    Hashtable<String, SDFAbstractVertex> actorsToConvert = new Hashtable<String, SDFAbstractVertex>();
    while (actorsToProcess.size() != 0) {
      SDFAbstractVertex h = actorsToProcess.elements().nextElement();
      actorsToConvert.put(h.getName(), h);

      // add the hierarchical actors of the top graph
      for (SDFAbstractVertex a : ((SDFGraph) h.getGraphDescription()).vertexSet()) {
        if (a.getGraphDescription() != null) {
          actorsToProcess.put(a.getName(), a);
        }
      }
      actorsToProcess.remove(h.getName());
    }

    // convert the SDF subgraph of all the hierarchical acotrs in the list
    Hashtable<String, SDFGraph> srSDFsubgraphsList = new Hashtable<String, SDFGraph>();
    for (SDFAbstractVertex a : actorsToConvert.values()) {

      // convert the subgraph to a SRSDF (classical conversion)
      SDFGraph srSDFsubgraph = SDFTransformer.convertToSrSDF((SDFGraph) a.getGraphDescription());

      // add the execution rules to the srSDF subgraph
      if (withExecRulres) {
        addExecRules(srSDFsubgraph);
      }

      srSDFsubgraphsList.put(a.getName(), srSDFsubgraph);
    }

    return null;
  }

  /**
   * Adds the execution rules to the srSDF subgraph
   * 
   * @param srSDF
   *          graph
   */
  private static void addExecRules(SDFGraph srSDF) {
    // step 1: add the two special actors Start and End actors
    GraphStructureHelper.addActor(srSDF, "start", null, 1, 0., null, null);
    GraphStructureHelper.addActor(srSDF, "end", null, 1, 0., null, null);

    // add the edge back from End actor to Start actor (no need of the edge from start to end)
    GraphStructureHelper.addEdge(srSDF, "end", "to_start", "start", "from_end", 1, 1, 1, null);

    // step 2: add the connection between actors/interfaces and start/end actors
    for (SDFAbstractVertex actor : srSDF.vertexSet()) {
      if (actor.getName() != "start" && actor.getName() != "end") {
        if (actor.getKind() == "port") {
          // add the connection between the interface and start/end actor
          if (actor instanceof SDFSourceInterfaceVertex) {
            // input interface
            GraphStructureHelper.addEdge(srSDF, actor.getName(), "to_start", "start", "from_" + actor.getName(), 1, 1, 0, null);
          } else if (actor instanceof SDFSinkInterfaceVertex) {
            // output interface
            GraphStructureHelper.addEdge(srSDF, "end", "to_" + actor.getName(), actor.getName(), "from_end", 1, 1, 0, null);
          } else {
            // unknown kind of port (unsupported)
            System.err.println("Unsupported kind of port !!");
          }
        } else {
          // add the connection between the actor and start/end actor
          GraphStructureHelper.addEdge(srSDF, "start", "to_" + actor.getName(), actor.getName(), "from_start", 1, 1, 0, null);
          GraphStructureHelper.addEdge(srSDF, actor.getName(), "to_end", "end", "from_" + actor.getName(), 1, 1, 0, null);
        }
      }
    }
  }

}
