/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
package org.preesm.algorithm.transforms;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.preesm.algorithm.model.types.LongEdgePropertyType;
import org.preesm.scenario.PreesmScenario;
import org.preesm.scenario.RelativeConstraintManager;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * Repeating N times the same single rate IBSDF algorithm into a new IBSDF graph. A delayed edge of weight 1 is
 * introduced between 2 iterations of the same actor to ensure precedence.
 *
 * @author mpelcat
 *
 */
class IterateAlgorithm extends AbstractTaskImplementation {

  /**
   * Tag for storing the requested number of iterations
   */
  static final String NB_IT = "nbIt";

  /**
   * Adding precedence edges to take into account a hidden state in each actor
   */
  static final String SET_STATES = "setStates";

  /**
   * Inserting mergedGraph into refGraph and adding optionally state edges of weight 1
   */
  SDFGraph merge(SDFGraph refGraph, SDFGraph mergedGraphIn, int index, boolean setStates) {

    // Generating a graph clone to avoid concurrent modifications
    SDFGraph mergedGraph = mergedGraphIn.copy();

    for (SDFAbstractVertex vertex : mergedGraph.vertexSet()) {
      String mergedVertexName = vertex.getName();
      // Id is identical to all iterations, same as for srSDF transformation
      vertex.setId(vertex.getId());
      vertex.setName(vertex.getName() + "_" + index);
      refGraph.addVertex(vertex);

      // If a state is introduced, a synthetic edge is put between 2 iterations of each actor
      if (setStates) {
        SDFAbstractVertex current = refGraph.getVertex(mergedVertexName + "_" + index);
        SDFAbstractVertex previous = refGraph.getVertex(mergedVertexName + "_" + (index - 1));
        if (previous != null && current != null) {
          SDFEdge newEdge = refGraph.addEdge(previous, current);
          newEdge.setProd(new LongEdgePropertyType(1));
          newEdge.setCons(new LongEdgePropertyType(1));

          // Create a new source stateout port
          SDFSourceInterfaceVertex statein = new SDFSourceInterfaceVertex();
          statein.setName("statein");
          previous.addSource(statein);

          // Create a new sink statein port
          SDFSinkInterfaceVertex stateout = new SDFSinkInterfaceVertex();
          stateout.setName("stateout");
          current.addSink(stateout);
          newEdge.setSourceInterface(stateout);
          newEdge.setTargetInterface(statein);
        }
      }
    }

    for (SDFEdge edge : mergedGraph.edgeSet()) {
      SDFAbstractVertex source = mergedGraph.getEdgeSource(edge);
      SDFAbstractVertex target = mergedGraph.getEdgeTarget(edge);
      SDFEdge newEdge = refGraph.addEdge(source, target);
      newEdge.setSourceInterface(edge.getSourceInterface());
      newEdge.setTargetInterface(edge.getTargetInterface());
      target.setInterfaceVertexExternalLink(newEdge, edge.getTargetInterface());
      source.setInterfaceVertexExternalLink(newEdge, edge.getSourceInterface());

      newEdge.setCons(edge.getCons().copy());
      newEdge.setProd(edge.getProd().copy());
      newEdge.setDelay(edge.getDelay().copy());

    }

    for (String propertyKey : mergedGraph.getPropertyBean().keys()) {
      String property = mergedGraph.getPropertyBean().getValue(propertyKey);
      refGraph.getPropertyBean().setValue(propertyKey, property);
    }
    return refGraph;
  }

  /**
   * Mixing nbIt iterations of a single graph, adding a state in case makeStates = true
   */
  SDFGraph iterate(SDFGraph inputAlgorithm, int nbIt, boolean setStates, PreesmScenario scenario) {

    SDFGraph mainIteration = inputAlgorithm.copy();

    if (nbIt > 1) {
      int groupId = 0;
      // setting first iteration with name "_0"
      for (SDFAbstractVertex vertex : mainIteration.vertexSet()) {
        String id = vertex.getId();
        // Id is identical to all iterations, same as for srSDF transformation
        vertex.setId(id);
        vertex.setName(vertex.getName() + "_0");
        // Adding relative constraints to the scenario if present
        if (scenario != null) {
          final RelativeConstraintManager relativeconstraintManager = scenario.getRelativeconstraintManager();
          relativeconstraintManager.addConstraint(id, groupId);
        }
        groupId++;
      }

      // Incorporating new iterations
      for (int i = 1; i < nbIt; i++) {

        // Adding vertices of new_iteration to the ones of mainIteration, vertices are automatically renamed
        mainIteration = merge(mainIteration, inputAlgorithm, i, setStates);
      }
    }

    return mainIteration;

  }

  /**
   * Executing the workflow element
   */
  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    final Map<String, Object> outMap = new LinkedHashMap<String, Object>();
    final SDFGraph inputAlgorithm = (SDFGraph) inputs.get("SDF");

    // If we retrieve a scenario, relative constraints are added in the scenario
    PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");

    final int nbIt = Integer.valueOf(parameters.get(NB_IT));
    final boolean setStates = Boolean.valueOf(parameters.get(SET_STATES));
    SDFGraph outputAlgorithm = iterate(inputAlgorithm, nbIt, setStates, scenario);
    outMap.put("SDF", outputAlgorithm);
    return outMap;
  }

  /**
   * If no parameter is set, using default value
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    Map<String, String> defaultParameters = new LinkedHashMap<String, String>();
    defaultParameters.put(NB_IT, "1");
    defaultParameters.put(SET_STATES, "true");
    return defaultParameters;
  }

  /**
   * Message displayed in the DFTools console
   */

  @Override
  public String monitorMessage() {
    return "Iterating a single rate IBSDF " + getDefaultParameters().get(NB_IT) + " time(s).";
  }
}
