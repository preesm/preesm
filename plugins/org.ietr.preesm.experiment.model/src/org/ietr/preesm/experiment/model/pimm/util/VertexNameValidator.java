package org.ietr.preesm.experiment.model.pimm.util;

import java.util.Set;

import org.eclipse.jface.dialogs.IInputValidator;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Graph;

/**
 * This validator is used to check whether a vertex in a graph already has a
 * given name.
 * 
 * @author kdesnos
 * 
 */
public class VertexNameValidator implements IInputValidator {

	protected Graph graph;
	protected Set<String> existingNames;

	/**
	 * Constructor of the {@link VertexNameValidator}
	 * 
	 * @param graph
	 *            the graph to which we want to add/rename a vertex
	 * @param the
	 *            vertex currently renamed, or <code>null</code> if creating a
	 *            new vertex.
	 */
	public VertexNameValidator(Graph graph, AbstractVertex renamedVertex) {
		this.graph = graph;
		// Retrieve a list of all the actor and parameter names in the graph
		existingNames = graph.getVerticesNames();
		existingNames.addAll(graph.getParametersNames());

		if (renamedVertex != null) {
			existingNames.remove(renamedVertex.getName());
		}
	}

	@Override
	public String isValid(String newVertexName) {
		String message = null;
		// Check if the name is not empty
		if (newVertexName.length() < 1) {
			message = "/!\\ Name cannot be empty /!\\";
			return message;
		}

		// Check if the name contains a space
		if (newVertexName.contains(" ")) {
			message = "/!\\ Name must not contain spaces /!\\";
			return message;
		}

		// Check if the name already exists
		if (existingNames.contains(newVertexName)) {
			message = "/!\\ An actor or a parameter with name " + newVertexName
					+ " already exists /!\\";
			return message;
		}
		return message;
	}

}
