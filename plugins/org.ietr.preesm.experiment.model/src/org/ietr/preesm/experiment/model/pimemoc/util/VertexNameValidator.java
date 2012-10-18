package org.ietr.preesm.experiment.model.pimemoc.util;

import java.util.Set;

import org.eclipse.jface.dialogs.IInputValidator;
import org.ietr.preesm.experiment.model.pimemoc.AbstractVertex;
import org.ietr.preesm.experiment.model.pimemoc.Graph;

/**
 * This validator is used to check whether a vertex in a graph already has a
 * given name.
 * 
 * @author kdesnos
 * 
 */
public class VertexNameValidator implements IInputValidator {

	protected Graph graph;
	protected Set<String> verticesNames;

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
		// Retrieve a list of all the actor names in the graph
		verticesNames = graph.getVerticesNames();
		if(renamedVertex != null)
		{
			verticesNames.remove(renamedVertex.getName());
		}
	}

	@Override
	public String isValid(String newActorName) {
		String message = null;
		// Check if the name is not empty
		if (newActorName.length() < 1) {
			message = "/!\\ Actor name cannot be empty /!\\";
			return message;
		}

		// Check if the name contains a space
		if (newActorName.contains(" ")) {
			message = "/!\\ Actor name must not contain spaces /!\\";
			return message;
		}

		// Check if the name already exists
		if (verticesNames.contains(newActorName)) {
			message = "/!\\ An actor with name " + newActorName
					+ " already exists /!\\";
			return message;
		}
		return message;
	}

}
