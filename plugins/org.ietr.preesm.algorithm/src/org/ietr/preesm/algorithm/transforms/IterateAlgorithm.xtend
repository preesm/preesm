/**
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package org.ietr.preesm.algorithm.transforms

import java.util.HashMap
import java.util.Map
import org.eclipse.core.runtime.IProgressMonitor
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.workflow.WorkflowException
import org.ietr.dftools.workflow.elements.Workflow
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType
import org.ietr.preesm.core.scenario.PreesmScenario

/**
 * Repeating N times the same single rate IBSDF algorithm into a new IBSDF graph.
 * A delayed edge of weight 1 is introduced between 2 iterations of the same actor
 * to ensure precedence.
 * 
 * @author mpelcat
 * 
 */
class IterateAlgorithm extends AbstractTaskImplementation {

	/**
	 * Tag for storing the requested number of iterations
	 */
	val NB_IT = "nbIt";

	/**
	 * Adding precedence edges to take into account a hidden state in each actor
	 */
	val SET_STATES = "setStates";

	/**
	 * Inserting mergedGraph into refGraph and adding optionally state edges of weight 1
	 */
	def merge(SDFGraph refGraph, SDFGraph mergedGraphIn, int index, boolean setStates) {

		// Generating a graph clone to avoid concurrent modifications
		var mergedGraph = mergedGraphIn.clone

		for (SDFAbstractVertex vertex : mergedGraph.vertexSet) {
			var mergedVertexName = vertex.getName
			// Id is identical to all iterations, same as for srSDF transformation
			vertex.setId(vertex.getId)
			vertex.setName(vertex.getName + "_" + index)
			refGraph.addVertex(vertex)

			// If a state is introduced, a synthetic edge is put between 2 iterations of each actor
			if (setStates) {
				var current = refGraph.getVertex(mergedVertexName + "_" + index)
				var previous = refGraph.getVertex(mergedVertexName + "_" + (index - 1))
				if (previous != null && current != null) {
					var newEdge = refGraph.addEdge(previous, current)
					newEdge.setProd(new SDFIntEdgePropertyType(1));
					newEdge.setCons(new SDFIntEdgePropertyType(1));

					// Create a new source stateout port
					var statein = new SDFSourceInterfaceVertex()
					statein.setName("statein");
					previous.addSource(statein);

					// Create a new sink statein port
					var stateout = new SDFSinkInterfaceVertex()
					stateout.setName("stateout");
					current.addSink(stateout);
					newEdge.setSourceInterface(stateout)
					newEdge.setTargetInterface(statein)
				}
			}
		}

		for (SDFEdge edge : mergedGraph.edgeSet) {
			var source = mergedGraph.getEdgeSource(edge)
			var target = mergedGraph.getEdgeTarget(edge)
			var newEdge = refGraph.addEdge(source, target)
			newEdge.setSourceInterface(edge.getSourceInterface())
			newEdge.setTargetInterface(edge.getTargetInterface())
			target.setInterfaceVertexExternalLink(newEdge, edge.getTargetInterface())
			source.setInterfaceVertexExternalLink(newEdge, edge.getSourceInterface())

			newEdge.setCons(edge.getCons().clone())
			newEdge.setProd(edge.getProd().clone())
			newEdge.setDelay(edge.getDelay().clone())

		}

		for (String propertyKey : mergedGraph.getPropertyBean().keys()) {
			var property = mergedGraph.getPropertyBean().getValue(propertyKey);
			refGraph.getPropertyBean().setValue(propertyKey, property);
		}
		return refGraph
	}

	/**
	 * Mixing nbIt iterations of a single graph, adding a state in case makeStates = true
	 */
	def iterate(SDFGraph inputAlgorithm, int nbIt, boolean setStates, PreesmScenario scenario) {

		var mainIteration = inputAlgorithm.clone

		var groupId = 0
		// setting first iteration with name "_0"
		for (SDFAbstractVertex vertex : mainIteration.vertexSet) {
			val id = vertex.getId
			// Id is identical to all iterations, same as for srSDF transformation
			vertex.setId(id);
			vertex.setName(vertex.getName + "_0")
			// Adding relative constraints to the scenario if present
			if(scenario != null){
				for(Integer i : 0 .. nbIt - 1){
					scenario.relativeconstraintManager.addConstraint(id,groupId)
				}
			}
			groupId++
		}

		// Incorporating new iterations
		for (Integer i : 1 .. nbIt - 1) {

			// Adding vertices of new_iteration to the ones of mainIteration, vertices are automatically renamed
			mainIteration = merge(mainIteration, inputAlgorithm, i, setStates)
		}

		return mainIteration

	}

	/**
	 * Executing the workflow element
	 */
	override execute(Map<String, Object> inputs, Map<String, String> parameters, IProgressMonitor monitor,
		String nodeName, Workflow workflow) throws WorkflowException {
		val outMap = new HashMap<String, Object>
		val inputAlgorithm = inputs.get("SDF") as SDFGraph
		
		// If we retrieve a scenario, relative constraints are added in the scenario
		var scenario = inputs.get("scenario") as PreesmScenario
		
		val nbIt = Integer.valueOf(parameters.get(NB_IT))
		val setStates = Boolean.valueOf(parameters.get(SET_STATES))
		val outputAlgorithm = iterate(inputAlgorithm, nbIt, setStates, scenario)
		outMap.put("SDF", outputAlgorithm)
		return outMap;
	}

	/**
	 * If no parameter is set, using default value
	 */
	override getDefaultParameters() {
		var defaultParameters = new HashMap<String, String>
		defaultParameters.put(NB_IT, "1")
		defaultParameters.put(SET_STATES, "true")
		return defaultParameters
	}

	/**
	 * Message displayed in the DFTools console
	 */
	override monitorMessage() '''
	Iterating a single rate IBSDF «defaultParameters.get(NB_IT)» time(s).'''

}
