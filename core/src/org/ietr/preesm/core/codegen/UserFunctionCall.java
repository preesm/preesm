/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.core.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.core.codegen.model.CodeGenArgument;
import org.ietr.preesm.core.codegen.model.CodeGenSDFVertex;
import org.ietr.preesm.core.codegen.model.FunctionCall;
import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

/**
 * Generated code consists primarily in a succession of code elements. A User
 * Function Call is a call corresponding to a vertex in the graph
 * 
 * @author mpelcat
 * @author jpiat
 */
public class UserFunctionCall extends AbstractCodeElement {

	public enum CodeSection {
		INIT, LOOP, END;
	}

	/**
	 * The buffer set contains all the buffers usable by the user function
	 */
	private List<Parameter> callParameters;

	public UserFunctionCall(SDFAbstractVertex vertex,
			AbstractBufferContainer parentContainer, CodeSection section) {
		super(vertex.getName(), parentContainer, vertex);

		// Buffers associated to the function call
		callParameters = new ArrayList<Parameter>();
		// Candidate buffers that will be added if present in prototype
		HashMap<SDFEdge, Buffer> candidateBuffers = new HashMap<SDFEdge, Buffer>();

		// Replacing the name of the vertex by the name of the prototype, if any
		// is available.
		if (vertex instanceof CodeGenSDFVertex) {
			FunctionCall call = ((FunctionCall) vertex.getRefinement());
			if (call != null) {

				switch (section) {
				case INIT:
					if (call.getInitCall() != null) {
						call = call.getInitCall();
						this.setName(call.getFunctionName());
					}
					break;
				case LOOP:
					if (call.getFunctionName().isEmpty()) {
						PreesmLogger.getLogger().log(
								Level.INFO,
								"Name not found in the IDL for function: "
										+ vertex.getName());
					} else {
						this.setName(call.getFunctionName());
					}

					break;
				case END:
					if (call.getEndCall() != null) {
						call = call.getEndCall();
						this.setName(call.getFunctionName());
					}
					break;
				}
			}

			// Adding output buffers
			for (SDFEdge edge : vertex.getBase().outgoingEdgesOf(vertex)) {
				AbstractBufferContainer parentBufferContainer = parentContainer;
				while (parentBufferContainer != null
						&& parentBufferContainer.getBuffer(edge) == null) {
					parentBufferContainer = parentBufferContainer
							.getParentContainer();
				}
				if (parentBufferContainer != null) {
					candidateBuffers.put(edge, parentBufferContainer
							.getBuffer(edge));
				}
			}

			// Adding input buffers
			for (SDFEdge edge : vertex.getBase().incomingEdgesOf(vertex)) {
				AbstractBufferContainer parentBufferContainer = parentContainer;
				while (parentBufferContainer != null
						&& parentBufferContainer.getBuffer(edge) == null) {
					parentBufferContainer = parentBufferContainer
							.getParentContainer();
				}
				if (parentBufferContainer != null) {
					candidateBuffers.put(edge, parentBufferContainer
							.getBuffer(edge));
				}
			}

			// Filters and orders the buffers to fit the prototype
			// Adds parameters if no buffer fits the prototype name
			if (call != null) {
				for (CodeGenArgument arg : call.getArguments()) {
					Parameter currentParam = null;

					String argName = arg.getName();
					if (arg.getDirection() == CodeGenArgument.INPUT) {
						for (SDFEdge link : candidateBuffers.keySet()) {
							if (link.getTargetInterface().getName().equals(
									arg.getName())
									&& link.getTarget().equals(vertex))
								currentParam = candidateBuffers.get(link);
						}
					} else if (arg.getDirection() == CodeGenArgument.OUTPUT) {
						for (SDFEdge link : candidateBuffers.keySet()) {
							if (link.getSourceInterface().getName().equals(
									arg.getName())
									&& link.getSource().equals(vertex))
								currentParam = candidateBuffers.get(link);

						}
					}

					// If no buffer was found with the given port name, a
					// parameter is sought
					if (currentParam == null
							&& arg.getDirection() == CodeGenArgument.INPUT) {
						if (vertex.getArgument(argName) != null) {
							currentParam = new Constant(argName, vertex
									.getArgument(argName).intValue());
						}
					}

					if (currentParam != null) {
						addParameter(currentParam);
					} else {
						PreesmLogger
								.getLogger()
								.log(
										Level.SEVERE,
										"Vertex: "
												+ vertex.getName()
												+ ". Error interpreting the prototype: no port found with name: "
												+ argName);
					}
				}
			}
		}
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit
		// self
		for (Parameter param : callParameters) {
			param.accept(printer, currentLocation);
		}
	}

	public void addParameter(Parameter param) {

		if (param == null)
			PreesmLogger.getLogger().log(Level.SEVERE, "null buffer");
		else
			callParameters.add(param);
	}

	public void addBuffers(Set<Buffer> buffers) {
		if (buffers != null) {
			for (Buffer buffer : buffers) {
				addParameter(buffer);
			}
		}
	}

	/**
	 * Adds to the function call all the buffers created by the vertex.
	 */
	public void addVertexBuffers(SDFAbstractVertex vertex) {
		addVertexBuffers(vertex, true);
		addVertexBuffers(vertex, false);
	}

	/**
	 * Add input or output buffers for a vertex, depending on the direction
	 */
	public void addVertexBuffers(SDFAbstractVertex vertex, boolean isInputBuffer) {

		Iterator<SDFEdge> eIterator;

		if (isInputBuffer)
			eIterator = vertex.getBase().incomingEdgesOf(vertex).iterator();
		else
			eIterator = vertex.getBase().outgoingEdgesOf(vertex).iterator();

		// Iteration on all the edges of each vertex belonging to ownVertices
		while (eIterator.hasNext()) {
			SDFEdge edge = eIterator.next();

			addParameter(getParentContainer().getBuffer((SDFEdge) edge));
		}
	}

	/**
	 * Displays pseudo-code for test
	 */
	public String toString() {

		String code = "";
		boolean first = true;

		code += super.toString();
		code += "(";

		Iterator<Parameter> iterator = callParameters.iterator();

		while (iterator.hasNext()) {

			if (!first)
				code += ",";
			else
				first = false;

			Parameter param = iterator.next();

			code += param.toString();
		}

		code += ");";

		return code;
	}
}
