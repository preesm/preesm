/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2017)
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
package org.preesm.algorithm.throughput.tools;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;

/**
 * @author hderoui
 *
 *         IBSDF Transformer class : contains the method to transform an IBSDF graph into a flat srSDF graph
 */
public abstract class IBSDFTransformer {

  /**
   * Converts an IBSDF graph to a flat srSDF graph
   *
   * @param IBSDF
   *          graph
   * @return flat srSDF graph
   */
  public static SDFGraph convertToSrSDF(final SDFGraph IBSDF, final boolean withExecRulres) {
    final Stopwatch timer = new Stopwatch();
    timer.start();

    // Step 1: Convert all the SDF subgraphs to a srSDF subgraphs
    final Map<String, SDFGraph> srSDFsubgraphList = IBSDFTransformer.convertAllSubgraphs(IBSDF, withExecRulres);

    // Step 2: Convert the top SDF graph to a srSDF graph
    final SDFGraph flatSrSDF = SDFTransformer.convertToSrSDF(IBSDF);

    // Step 3: Replace each instance of a hierarchical actor by its srSDF subgraph version
    final Map<String, SDFAbstractVertex> actorsToReplace = GraphStructureHelper.getHierarchicalActors(flatSrSDF);
    final List<SDFAbstractVertex> list = new ArrayList<>(actorsToReplace.values());
    for (final SDFAbstractVertex h : list) {
      final SDFGraph srSubgraph = srSDFsubgraphList
          .get(((SDFAbstractVertex) h.getPropertyBean().getValue("baseActor")).getName());
      GraphStructureHelper.replaceHierarchicalActor(flatSrSDF, h, srSubgraph);

      // add the hierarchical actors of the subgraph to the list of actors to replace
      for (final SDFAbstractVertex a : srSubgraph.vertexSet()) {
        if (a.getGraphDescription() != null) {
          final String actorInstanceNewName = h.getName() + "_" + a.getName();
          actorsToReplace.put(actorInstanceNewName, flatSrSDF.getVertex(actorInstanceNewName));
        }
      }

      // remove the hierarchical actor from the list of actors to replace
      actorsToReplace.remove(h.getName());
    }

    timer.stop();
    return flatSrSDF;
  }

  /**
   * converts all the SDF subgraphs of the hierarchy into srSDF subgraphs
   *
   * @param ibsdf
   *          graph
   * @param withExecRulres
   *          boolean, add execution rules if true
   * @return the list of srSDF subgraphs
   */
  private static Map<String, SDFGraph> convertAllSubgraphs(final SDFGraph ibsdf, final boolean withExecRulres) {
    // the list of srSDF subgraphs
    final Map<String, SDFGraph> srSDFsubgraphsList = new LinkedHashMap<>();

    // get all the hierarchical actors of the IBSDF graph
    final Map<String, SDFAbstractVertex> actorsToConvert = GraphStructureHelper.getAllHierarchicalActors(ibsdf);

    // convert the hierarchical actors SDF subgraphs to a srSDF subgraph
    for (final SDFAbstractVertex h : actorsToConvert.values()) {
      final SDFGraph srSDFsubgraph = SDFTransformer.convertToSrSDF((SDFGraph) h.getGraphDescription());
      // add the execution rules to the srSDF subgraph
      if (withExecRulres) {
        IBSDFTransformer.addExecRules(srSDFsubgraph);
      }
      srSDFsubgraphsList.put(h.getName(), srSDFsubgraph);
    }

    return srSDFsubgraphsList;
  }

  /**
   * Adds the execution rules to a srSDF subgraph
   *
   * @param srSDF
   *          graph
   */
  private static void addExecRules(final SDFGraph srSDF) {
    // step 1: add the two special actors Start and End actors
    GraphStructureHelper.addActor(srSDF, "start", null, 1L, 0., 0, null);
    GraphStructureHelper.addActor(srSDF, "end", null, 1L, 0., 0, null);

    // add the edge back from End actor to Start actor (no need of the edge from start to end)
    GraphStructureHelper.addEdge(srSDF, "end", "to_start", "start", "from_end", 1, 1, 1, null);

    // step 2: add the connection between actors/interfaces and start/end actors
    for (final SDFAbstractVertex actor : srSDF.vertexSet()) {
      if ((actor.getName() != "start") && (actor.getName() != "end")) {
        // add the connection between the interface and start/end actor
        final SDFAbstractVertex baseActor = (SDFAbstractVertex) actor.getPropertyBean().getValue("baseActor");
        if (baseActor instanceof SDFSourceInterfaceVertex) {
          // input interface
          GraphStructureHelper.addEdge(srSDF, actor.getName(), "to_start", "start", "from_" + actor.getName(), 1, 1, 0,
              null);
        } else if (baseActor instanceof SDFSinkInterfaceVertex) {
          // output interface
          GraphStructureHelper.addEdge(srSDF, "end", "to_" + actor.getName(), actor.getName(), "from_end", 1, 1, 0,
              null);
        } else {
          // add the connection between the actor and start/end actor
          GraphStructureHelper.addEdge(srSDF, "start", "to_" + actor.getName(), actor.getName(), "from_start", 1, 1, 0,
              null);
          GraphStructureHelper.addEdge(srSDF, actor.getName(), "to_end", "end", "from_" + actor.getName(), 1, 1, 0,
              null);
        }
      }
    }
  }

}
