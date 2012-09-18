/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.codegen.model.call;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.algorithm.model.sdf.SDFEdge;
import net.sf.dftools.algorithm.model.sdf.SDFGraph;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.ietr.preesm.codegen.idl.ActorPrototypes;
import org.ietr.preesm.codegen.idl.Prototype;
import org.ietr.preesm.codegen.model.CodeGenArgument;
import org.ietr.preesm.codegen.model.CodeGenParameter;
import org.ietr.preesm.codegen.model.CodeGenSDFTokenInitVertex;
import org.ietr.preesm.codegen.model.ICodeGenSDFVertex;
import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.buffer.Buffer;
import org.ietr.preesm.codegen.model.factories.FunctionArgumentFactory;
import org.ietr.preesm.codegen.model.main.AbstractCodeElement;
import org.ietr.preesm.codegen.model.printer.CodeZoneId;
import org.ietr.preesm.codegen.model.printer.IAbstractPrinter;
import org.ietr.preesm.codegen.model.types.CodeSectionType;
import org.ietr.preesm.core.types.DataType;

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
	private Vector<FunctionArgument> callParameters;

	private FunctionArgument returnSet;

	/**
	 * The vertex responsible of this call
	 */
	private SDFAbstractVertex vertex = null;

	public UserFunctionCall(String name, AbstractBufferContainer parentContainer) {
		super(name, parentContainer, null);
		callParameters = new Vector<FunctionArgument>();
	}

	@SuppressWarnings("unchecked")
	public UserFunctionCall(CodeGenSDFTokenInitVertex vertex,
			AbstractBufferContainer parentContainer, CodeSectionType section,
			boolean ignoreSendReceive) {
		super(vertex.getName(), parentContainer, vertex);

		// Buffers associated to the function call
		callParameters = new Vector<FunctionArgument>();
		SDFEdge outEdge = (SDFEdge) vertex.getBase().outgoingEdgesOf(vertex)
				.toArray()[0];
		if (vertex.getBase().incomingEdgesOf(vertex).size() > 0) {
			this.setName("memcpy");
			for (SDFEdge initEdge : ((SDFGraph) vertex.getBase())
					.incomingEdgesOf(vertex)) {
				if (initEdge.getTargetInterface().getName().equals("init")) {
					Buffer buf = parentContainer.getBuffer(outEdge);
					this.addArgument(outEdge.getSource().getName() + "_to_"
							+ outEdge.getTarget().getName(), buf);

					buf = parentContainer.getBuffer(initEdge);
					this.addArgument(initEdge.getSource().getName() + "_to_"
							+ initEdge.getTarget().getName(), buf);
					try {
						this.addArgument("size", new Constant("size", outEdge
								.getCons().intValue()
								+ "*sizeof("
								+ outEdge.getDataType().toString() + ")"));
					} catch (InvalidExpressionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		// Managing fifo
		else {
			AbstractBufferContainer globalContainer = parentContainer
					.getGlobalContainer();

			Variable delay = new Variable(vertex.getName(),
					new DataType("Fifo"));
			globalContainer.addVariable(delay);
			vertex.setDelayVariable(delay);
			this.setName("new_fifo");
			this.addArgument("fifo", new PointerOn(delay));
			this.addArgument("token_size", new Constant("size", "sizeof("
					+ outEdge.getDataType().toString() + ")"));
			this.addArgument("nb_fifo",
					new Constant("fifo_size", vertex.getInitSize()));
		}

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

		this.vertex = vertex;
		// Buffers associated to the function call
		callParameters = new Vector<FunctionArgument>();
		// Replacing the name of the vertex by the name of the prototype, if any
		// is available.
		if (vertex instanceof ICodeGenSDFVertex) {
			Prototype call = getFunctionPrototype(vertex, section).getLoopPrototype();
			if (call != null) {

				// Filters and orders the buffers to fit the prototype
				// Adds parameters if no buffer fits the prototype name
				if (call != null) {
					callParameters = new Vector<FunctionArgument>(
							call.getNbArgs());
					for (CodeGenArgument arg : call.getArguments().keySet()) {
						FunctionArgument funcArg = FunctionArgumentFactory
								.createFunctionArgument(arg, vertex,
										parentContainer);
						funcArg = FunctionArgumentFactory
								.createFunctionArgument(arg, vertex,
										parentContainer);
						if (funcArg != null) {
							this.addArgument(FunctionArgumentFactory
									.createFunctionArgument(arg, vertex,
											parentContainer), call
									.getArguments().get(arg));
						} else {
							WorkflowLogger
									.getLogger()
									.log(Level.SEVERE,
											"Vertex: "
													+ vertex.getName()
													+ ". Error interpreting the prototype: no port found with name: "
													+ arg.getName());

						}
					}

					for (CodeGenParameter param : call.getParameters().keySet()) {
						FunctionArgument funcArg = FunctionArgumentFactory
								.createFunctionArgument(param, vertex,
										parentContainer);
						if (funcArg != null) {
							this.addArgument(funcArg,
									call.getParameters().get(param));
						} else {
							WorkflowLogger
									.getLogger()
									.log(Level.SEVERE,
											"Vertex: "
													+ vertex.getName()
													+ ". Error interpreting the prototype: no port found with name: "
													+ param.getName());

						}

					}
				}
			}
		}
	}

	private ActorPrototypes getFunctionPrototype(SDFAbstractVertex vertex,
			CodeSectionType section) {
		ActorPrototypes protos = ((ActorPrototypes) vertex.getRefinement());
		Prototype proto = null;
		if (protos != null) {

			switch (section) {
				case beginning:
					if (protos.getInitPrototype() != null) {
						proto = protos.getInitPrototype();
						this.setName(proto.getFunctionName());
					}
					break;
				case loop:
					proto = protos.getLoopPrototype();
					if (proto.getFunctionName().isEmpty()) {
						WorkflowLogger.getLogger().log(
								Level.INFO,
								"Name not found in the IDL for function: "
										+ vertex.getName());
						this.setName(null);
						return null;
					} else {
						this.setName(proto.getFunctionName());
					}
	
					break;
				default:
					break;
			}
		}

		return protos;
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit
		// self
		for (FunctionArgument param : callParameters) {
			if (param != null) {
				param.accept(printer, currentLocation);
			}
		}
	}

	public void addArgument(FunctionArgument param, int pos) {

		if (param == null)
			WorkflowLogger.getLogger().log(Level.SEVERE, "null buffer");
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

	public void addArgument(FunctionArgument param) {
		if (param == null)
			WorkflowLogger.getLogger().log(Level.SEVERE, "null argument");
		else {
			callParameters.add(param);
		}
	}

	/**
	 * Adds an argument to a call; if the argument is null, displays a message
	 * including a display name for debug
	 */
	public void addArgument(String displayName, FunctionArgument param) {

		if (param == null)
			WorkflowLogger.getLogger().log(Level.SEVERE,
					"buffer or parameter was not found: " + displayName);
		else
			callParameters.add(param);
	}

	public void addBuffers(Set<Buffer> buffers) {
		if (buffers != null) {
			int i = 0;
			for (Buffer buffer : buffers) {
				addArgument(buffer, i);
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
	@SuppressWarnings("unchecked")
	public void addVertexBuffers(SDFAbstractVertex vertex, boolean isInputBuffer) {

		Iterator<SDFEdge> eIterator;

		if (isInputBuffer)
			eIterator = vertex.getBase().incomingEdgesOf(vertex).iterator();
		else
			eIterator = vertex.getBase().outgoingEdgesOf(vertex).iterator();

		// Iteration on all the edges of each vertex belonging to ownVertices
		while (eIterator.hasNext()) {
			SDFEdge edge = eIterator.next();

			Buffer buf = getParentContainer().getBuffer((SDFEdge) edge);
			addArgument(edge.getSource().getName() + "_to_"
					+ edge.getTarget().getName(), buf);
		}
	}

	public List<FunctionArgument> getCallParameters() {
		return callParameters;
	}

	public void setReturn(FunctionArgument ret) {
		returnSet = ret;
	}

	public FunctionArgument getReturn() {
		return returnSet;
	}

	/**
	 * Displays pseudo-code for test
	 */
	public String toString() {

		String code = "";
		boolean first = true;

		code += super.toString();
		code += "(";

		Iterator<FunctionArgument> iterator = callParameters.iterator();

		while (iterator.hasNext()) {

			if (!first)
				code += ",";
			else
				first = false;

			FunctionArgument param = iterator.next();

			if (param == null) {
				WorkflowLogger.getLogger().log(Level.SEVERE,
						"buffer or parameter was not found...");
			} else {
				code += param.toString();
			}
		}

		code += ");";

		return code;
	}
	
	/**
	 * Returning the related vertex name if relevant
	 */
	public String getVertexName() {
		if(vertex != null){
			return vertex.getName();
		}
		else{
			return "";
		}
	}
}
