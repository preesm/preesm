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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import net.sf.saxon.expr.CodeGeneratorService;

import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.model.CodeGenArgument;
import org.ietr.preesm.core.codegen.model.CodeGenSDFReceiveVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFSendVertex;
import org.ietr.preesm.core.codegen.model.CodeGenParameter;
import org.ietr.preesm.core.codegen.model.FunctionCall;
import org.ietr.preesm.core.codegen.model.ICodeGenSDFVertex;
import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.jgrapht.alg.DirectedNeighborIndex;
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

/**
 * Generated code consists primarily in a succession of code elements. A User
 * Function Call is a call corresponding to an atomic vertex in the graph
 * 
 * @author mpelcat
 * @author jpiat
 */
public class UserFunctionCall extends AbstractCodeElement {

	/**
	 * The buffer set contains all the buffers usable by the user function
	 */
	private Vector<Parameter> callParameters;

	public UserFunctionCall(String name, AbstractBufferContainer parentContainer) {
		super(name, parentContainer, null);
		callParameters = new Vector<Parameter>();
	}

	/**
	 * The prototype IDL is parsed, buffer allocations are retrieved from their
	 * buffer container when they correspond to input or output edges of the
	 * current vertex. The buffers are then chosen and ordered depending on the
	 * prototype. There is a possibility to ignore the send and receive vertices
	 * while retrieving the buffers.
	 */
	public UserFunctionCall(SDFAbstractVertex vertex,
			AbstractBufferContainer parentContainer, CodeSectionType section,
			boolean ignoreSendReceive) {
		super(vertex.getName(), parentContainer, vertex);

		// Buffers associated to the function call
		callParameters = new Vector<Parameter>();

		// Replacing the name of the vertex by the name of the prototype, if any
		// is available.
		if (vertex instanceof ICodeGenSDFVertex) {
			FunctionCall call = getFunctionCall(vertex, section);
			if (call != null) {

				// Candidate buffers that will be added if present in prototype
				Map<SDFEdge, Buffer> candidateBuffers = retrieveBuffersFromEdges(
						vertex, parentContainer, ignoreSendReceive);

				// Filters and orders the buffers to fit the prototype
				// Adds parameters if no buffer fits the prototype name
				if (call != null) {
					callParameters = new Vector<Parameter>(call.getNbArgs());
					for (CodeGenArgument arg : call.getArguments().keySet()) {
						Parameter currentParam = null;
						String argName = arg.getName();
						if (arg.getDirection() == CodeGenArgument.INPUT) {
							for (SDFEdge link : candidateBuffers.keySet()) {
								String port = link.getTargetInterface().getName();
								
								if(port.equals(arg.getName())){
									if(link.getTarget().equals(vertex)){
										currentParam = candidateBuffers.get(link);
									}
									else if(ignoreSendReceive && (link.getTarget() instanceof CodeGenSDFSendVertex)){
										DirectedNeighborIndex<SDFAbstractVertex, SDFEdge> neighIndex = new DirectedNeighborIndex<SDFAbstractVertex, SDFEdge>(vertex.getBase());
										SDFAbstractVertex receive = neighIndex.successorListOf(link.getTarget()).get(0);
										SDFAbstractVertex receiver = neighIndex.successorListOf(receive).get(0);
										if(receiver.equals(vertex)){
											currentParam = candidateBuffers.get(link);
										}
									}
								}
							}
						} else if (arg.getDirection() == CodeGenArgument.OUTPUT) {
							for (SDFEdge link : candidateBuffers.keySet()) {
								String port = link.getSourceInterface().getName();
								if (port.equals(arg.getName())
										&& link.getSource().equals(vertex)){
									currentParam = candidateBuffers.get(link);
								}
							}
						}

						// If no buffer was found with the given port name, a
						// parameter is sought
						if (currentParam == null
								&& arg.getDirection() == CodeGenArgument.INPUT) {
							if (vertex.getArgument(argName) != null) {
								try {
									currentParam = new Constant(argName, vertex
											.getArgument(argName).intValue());
								} catch (InvalidExpressionException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}

						if (currentParam != null) {
							addParameter(currentParam, call.getArguments().get(
									arg));
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

					for (CodeGenParameter param : call.getParameters().keySet()) {
						if (vertex.getArgument(param.getName()) != null) {
							Parameter currentParam;
							try {
								currentParam = new Constant(param.getName(),
										vertex.getArgument(param.getName())
												.intValue());
								addParameter(currentParam, call.getParameters()
										.get(param));
							} catch (InvalidExpressionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Retrieves the buffers possibly used by the function call from the buffer
	 * container. If send and receive are ignored, we consider the input edge of
	 * the send vertex to be the one used as a reference for the buffer.
	 */
	private Map<SDFEdge, Buffer> retrieveBuffersFromEdges(
			SDFAbstractVertex vertex, AbstractBufferContainer parentContainer,
			boolean ignoreSendReceive) {

		Map<SDFEdge, Buffer> candidateBuffers = new HashMap<SDFEdge, Buffer>();
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
			
			if(ignoreSendReceive && edge.getSource() instanceof CodeGenSDFReceiveVertex){
				
				DirectedNeighborIndex<SDFAbstractVertex, SDFEdge> neighIndex = new DirectedNeighborIndex<SDFAbstractVertex, SDFEdge>(vertex.getBase());
				SDFAbstractVertex send = neighIndex.predecessorListOf(edge.getSource()).get(0);
				edge = (SDFEdge)vertex.getBase().incomingEdgesOf(send).toArray()[0];
			}
			
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

		return candidateBuffers;
	}

	private FunctionCall getFunctionCall(SDFAbstractVertex vertex,
			CodeSectionType section) {
		FunctionCall call = ((FunctionCall) vertex.getRefinement());
		if (call != null) {

			switch (section) {
			case beginning:
				if (call.getInitCall() != null) {
					call = call.getInitCall();
					this.setName(call.getFunctionName());
				}
				break;
			case loop:
				if (call.getFunctionName().isEmpty()) {
					PreesmLogger.getLogger().log(
							Level.INFO,
							"Name not found in the IDL for function: "
									+ vertex.getName());
					this.setName(null);
					return null;
				} else {
					this.setName(call.getFunctionName());
				}

				break;
			case end:
				if (call.getEndCall() != null) {
					call = call.getEndCall();
					this.setName(call.getFunctionName());
				}
				break;
			}
		}

		return call;
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit
		// self
		for (Parameter param : callParameters) {
			if (param != null) {
				param.accept(printer, currentLocation);
			}
		}
	}

	public void addParameter(Parameter param, int pos) {

		if (param == null)
			PreesmLogger.getLogger().log(Level.SEVERE, "null buffer");
		else {
			if (pos == callParameters.size()) {
				callParameters.add(param);
			} else if (pos > callParameters.size()) {
				callParameters.setSize(pos);
				callParameters.insertElementAt(param, pos);
			} else {
				callParameters.setElementAt(param, pos);
			}
		}
	}

	public void addParameter(Parameter param) {

		if (param == null)
			PreesmLogger.getLogger().log(Level.SEVERE,
					"buffer or parameter was not found");
		else
			callParameters.add(param);
	}

	public void addBuffers(Set<Buffer> buffers) {
		if (buffers != null) {
			int i = 0;
			for (Buffer buffer : buffers) {
				addParameter(buffer, i);
				i++;
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

	public List<Parameter> getCallParameters() {
		return callParameters;
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
