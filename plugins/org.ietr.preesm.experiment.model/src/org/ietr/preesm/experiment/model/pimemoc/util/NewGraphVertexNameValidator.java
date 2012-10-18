package org.ietr.preesm.experiment.model.pimemoc.util;

import java.util.Set;

import org.eclipse.jface.dialogs.IInputValidator;
import org.ietr.preesm.experiment.model.pimemoc.Graph;

/**
 * This validator is used to check whether a vertex in a graph already has a
 * given name.
 * 
 * @author kdesnos
 * 
 */
public class NewGraphVertexNameValidator implements IInputValidator {

	protected Graph graph;
	protected Set<String> verticesNames;

	/**
	 * Constructor of the {@link NewGraphVertexNameValidator}
	 * 
	 * @param graph
	 *            the graph to which we want to add a vertex
	 */
	public NewGraphVertexNameValidator(Graph graph) {
		this.graph = graph;
		// Retrieve a list of all the actor names in the graph
		verticesNames = graph.getVerticesNames();
	}

	@Override
	public String isValid(String newActorName) {
		// Check if the name already exists
		String message = null;
		Boolean validName = !verticesNames.contains(newActorName);
		if (!validName) {
			message = "/!\\ An actor with name " + newActorName
					+ " already exists /!\\";
		}
		return message;
	}

}
