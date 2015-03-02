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

package org.ietr.preesm.codegen.model.containers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.parameters.Parameter;
import org.ietr.dftools.algorithm.model.psdf.parameters.PSDFDynamicParameter;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFBroadcastVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFFifoPullVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFForkVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFGraph;
import org.ietr.preesm.codegen.model.CodeGenSDFInitVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFJoinVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFRoundBufferVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFSubInitVertex;
import org.ietr.preesm.codegen.model.ICodeGenSDFVertex;
import org.ietr.preesm.codegen.model.ICodeGenSpecialBehaviorVertex;
import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.buffer.Buffer;
import org.ietr.preesm.codegen.model.buffer.BufferAllocation;
import org.ietr.preesm.codegen.model.buffer.SubBuffer;
import org.ietr.preesm.codegen.model.buffer.SubBufferAllocation;
import org.ietr.preesm.codegen.model.call.Constant;
import org.ietr.preesm.codegen.model.call.UserFunctionCall;
import org.ietr.preesm.codegen.model.call.Variable;
import org.ietr.preesm.codegen.model.expression.BinaryExpression;
import org.ietr.preesm.codegen.model.expression.ConstantExpression;
import org.ietr.preesm.codegen.model.expression.IExpression;
import org.ietr.preesm.codegen.model.factories.CodeElementFactory;
import org.ietr.preesm.codegen.model.main.ICodeElement;
import org.ietr.preesm.codegen.model.main.VariableAllocation;
import org.ietr.preesm.codegen.model.printer.CodeZoneId;
import org.ietr.preesm.codegen.model.printer.IAbstractPrinter;
import org.ietr.preesm.core.types.DataType;

/**
 * A compound element is a code element containing multiple call or code
 * elements limited by closure. All buffers defined in this element have a local
 * scope
 * 
 * @author jpiat
 * 
 */
public class CompoundCodeElement extends AbstractCodeContainer implements
		ICodeElement {

	private List<ICodeElement> calls;

	protected HashMap<SDFEdge, Buffer> localBuffers;

	private SDFAbstractVertex correspondingVertex;

	private String name;

	private AbstractBufferContainer parentContainer;

	/**
	 * Creates a Compound Element
	 * 
	 * @param name
	 *            The name of the code element
	 * @param parentContainer
	 *            The parent container of this element
	 * @param correspondingVertex
	 *            The vertex from which to create the element
	 */
	public CompoundCodeElement(String name,
			AbstractBufferContainer parentContainer,
			ICodeGenSDFVertex correspondingVertex, CodeSectionType sectionType) {
		super(parentContainer, sectionType.toString(), "");
		localBuffers = new HashMap<SDFEdge, Buffer>();
		this.name = name;
		this.parentContainer = parentContainer;
		this.correspondingVertex = (SDFAbstractVertex) correspondingVertex;
		calls = new ArrayList<ICodeElement>();
		try {
			if (correspondingVertex.getGraphDescription() != null) { // gets the
				// hierarchy
				// of
				// the
				// vertex
				CodeGenSDFGraph graph = (CodeGenSDFGraph) correspondingVertex
						.getGraphDescription();
				if (graph.getParameters() != null) {
					for (Parameter param : graph.getParameters().values()) {
						if (param instanceof PSDFDynamicParameter) {
							this.getParentContainer().addVariable(
									new Variable(param.getName(), new DataType(
											"long")));
						}
					}
				}
				for (SDFEdge edge : graph.edgeSet()) { // allocate local
					// buffers, or get the
					// buffer from the upper
					// hierarchy that feed
					// the interfaces
					if (edge.getSource() instanceof SDFSourceInterfaceVertex) {
						SDFEdge outEdge = this.correspondingVertex
								.getAssociatedEdge((SDFSourceInterfaceVertex) edge
										.getSource());
						Buffer parentBuffer = parentContainer
								.getBuffer(outEdge);
						this.addBuffer(parentBuffer, edge);
					} else if (edge.getTarget() instanceof SDFSinkInterfaceVertex) {
						SDFEdge outEdge = this.correspondingVertex
								.getAssociatedEdge((SDFSinkInterfaceVertex) edge
										.getTarget());
						Buffer parentBuffer = parentContainer
								.getBuffer(outEdge);
						this.addBuffer(parentBuffer, edge);
					} else if (this.getBuffer(edge) == null
							&& needToBeAllocated(edge)) {
						String bufferName = edge.getSourceInterface().getName()
								+ "_" + edge.getTargetInterface().getName();
						if (edge.getTarget() == edge.getSource()) {
							this.allocateBuffer(edge, bufferName, new DataType(
									edge.getDataType().toString()));
						} else {
							this.allocateBuffer(edge, bufferName, new DataType(
									edge.getDataType().toString()));
						}
					}
					if (edge.getDelay().intValue() > 0
							&& !(edge.getTarget() instanceof CodeGenSDFFifoPullVertex)) {
						UserFunctionCall initCall = new UserFunctionCall(
								"memset", this);

						Buffer buf = this.getBuffer(edge);
						initCall.addArgument(edge.getSource().getName()
								+ "_to_" + edge.getTarget().getName(), buf);
						initCall.addArgument("value", new Constant("value", 0));
						initCall.addArgument("size", new Constant("size", edge
								.getDelay().intValue()));
						this.addCall(initCall);
					}
				}
				manageCalls(graph.vertexSet(), sectionType);
			} else {
				this.addCall(new UserFunctionCall(this.correspondingVertex,
						this, sectionType, false));
			}
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a compound element which content is not directly associated to a
	 * vertex
	 * 
	 * @param name
	 *            The name of the compound element
	 * @param parentContainer
	 *            The parent container of this element
	 */
	public CompoundCodeElement(String name,
			AbstractBufferContainer parentContainer, String sectionType) {
		super(parentContainer, sectionType, "");
		localBuffers = new HashMap<SDFEdge, Buffer>();
		this.name = name;
		this.parentContainer = parentContainer;
		calls = new ArrayList<ICodeElement>();
	}

	public void setCorrespondingVertex(SDFAbstractVertex vertex) {
		this.correspondingVertex = vertex;
	}

	/**
	 * Add a local buffer, and set the edge this buffer is associated to
	 * 
	 * @param buff
	 *            The buffer to add
	 * @param edge
	 *            The edge this buffer is associated to
	 */
	public void addBuffer(Buffer buff, SDFEdge edge) {
		if (localBuffers.get(edge) == null) {
			localBuffers.put(edge, buff);
		}
	}

	private boolean needToBeAllocated(SDFEdge edge) {
		if ((edge.getSource() instanceof SDFBroadcastVertex)
				|| (edge.getTarget() instanceof SDFRoundBufferVertex)) {
			return false;
		}
		return true;
	}

	/**
	 * Treat the calls of this compound element
	 * 
	 * @param vertices
	 */
	private void manageCalls(Set<SDFAbstractVertex> vertices, CodeSectionType sectionType) {
		List<SDFAbstractVertex> treated = new ArrayList<SDFAbstractVertex>();
		// treat special calls which are mainly related to buffer
		// splitting/grouping or copying
		for (SDFAbstractVertex vertex : vertices) {
			if (vertex instanceof ICodeGenSpecialBehaviorVertex) {
				if (manageSpecialBehaviorVertex(vertex.getName(), this, vertex)) {
					treated.add(vertex);
				}
			}
		}
		// treat other vertices
		for (SDFAbstractVertex vertex : vertices) {
			if (vertex instanceof ICodeGenSDFVertex
					&& !(vertex instanceof SDFInterfaceVertex)
					&& !treated.contains(vertex)) {
				ICodeElement loopCall = CodeElementFactory.createElement(this,
						vertex, sectionType);
				if (loopCall != null) {
					if (vertex instanceof CodeGenSDFInitVertex) {
						AbstractBufferContainer parentCodeContainer = this;
						@SuppressWarnings("unused")
						ICodeElement predecessor = this;
						do {
							if (parentCodeContainer instanceof ICodeElement) {
								predecessor = (ICodeElement) parentCodeContainer;
							}
							parentCodeContainer = parentCodeContainer
									.getParentContainer();
						} while ((parentCodeContainer != null && parentCodeContainer instanceof AbstractBufferContainer)
								&& !(parentCodeContainer instanceof AbstractCodeContainer));
						if (parentCodeContainer != null
								&& parentCodeContainer instanceof AbstractCodeContainer) {
							((AbstractCodeContainer) parentCodeContainer)
									.addInitCodeElement(loopCall);
						} else {
							this.addCallAtIndex(loopCall, 0);
						}

					} else if (vertex instanceof CodeGenSDFSubInitVertex) {
						this.addCallAtIndex(loopCall, 0);
					} else {
						// Adding loop call if any
						this.addCall(loopCall);
					}
				}
			}
		}
	}

	/**
	 * Manage special behavior vertices
	 * 
	 * @param name
	 *            The name of the call
	 * @param parentContainer
	 *            The parent container
	 * @param vertex
	 *            The vertex from which to create the call
	 * @return True if the creation succeed, false otherwise
	 */
	public boolean manageSpecialBehaviorVertex(String name,
			AbstractBufferContainer parentContainer, SDFAbstractVertex vertex) {
		try {
			if (vertex instanceof CodeGenSDFForkVertex) {
				generateSpecialBehavior((CodeGenSDFForkVertex) vertex,
						parentContainer);
			} else if (vertex instanceof CodeGenSDFJoinVertex) {
				generateSpecialBehavior(((CodeGenSDFJoinVertex) vertex),
						parentContainer);
			} else if (vertex instanceof CodeGenSDFBroadcastVertex) {
				generateSpecialBehavior((CodeGenSDFBroadcastVertex) vertex,
						parentContainer);
			} else if (vertex instanceof CodeGenSDFRoundBufferVertex) {
				generateSpecialBehavior(((CodeGenSDFRoundBufferVertex) vertex),
						parentContainer);
			}
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Generating code for a round buffer vertex
	 */
	public boolean generateSpecialBehavior(CodeGenSDFRoundBufferVertex vertex,
			AbstractBufferContainer parentContainer)
			throws InvalidExpressionException {

		SDFEdge outgoingEdge = null;
		for (SDFEdge outEdge : ((SDFGraph) vertex.getBase())
				.outgoingEdgesOf(vertex)) {
			outgoingEdge = outEdge;
		}
		Buffer outBuffer = parentContainer.getBuffer(outgoingEdge);
		for (SDFEdge inEdge : ((SDFGraph) vertex.getBase())
				.incomingEdgesOf(vertex)) {
			ConstantExpression index = new ConstantExpression("", new DataType(
					"int"), 0);
			String buffName = inEdge.getSourceInterface().getName() + "_"
					+ inEdge.getTargetInterface().getName();
			IExpression expr = new BinaryExpression("%", new BinaryExpression(
					"*", index, new ConstantExpression(inEdge.getCons()
							.intValue())), new ConstantExpression(
					outBuffer.getSize()));
			SubBuffer subElt = new SubBuffer(buffName, inEdge.getCons()
					.intValue(), expr, outBuffer, inEdge, parentContainer);
			if (parentContainer.getBuffer(inEdge) == null) {
				parentContainer.removeBufferAllocation(parentContainer
						.getBuffer(inEdge));
				parentContainer.addSubBufferAllocation(new SubBufferAllocation(
						subElt));
			}
			;
		}
		return true;
	}

	/**
	 * Generating code for a join vertex
	 */
	public boolean generateSpecialBehavior(CodeGenSDFJoinVertex vertex,
			AbstractBufferContainer parentContainer)
			throws InvalidExpressionException {
		SDFEdge outgoingEdge = null;

		for (SDFEdge outEdge : ((SDFGraph) vertex.getBase())
				.outgoingEdgesOf(vertex)) {
			outgoingEdge = outEdge;
		}
		Buffer outBuffer = parentContainer.getBuffer(outgoingEdge);
		for (SDFEdge inEdge : ((SDFGraph) vertex.getBase())
				.incomingEdgesOf(vertex)) {
			ConstantExpression index = new ConstantExpression("", new DataType(
					"int"),
					vertex.getEdgeIndex(inEdge));
			String buffName = parentContainer.getBuffer(inEdge).getName();
			IExpression expr = new BinaryExpression("%", new BinaryExpression(
					"*", index, new ConstantExpression(inEdge.getCons()
							.intValue())), new ConstantExpression(
					outBuffer.getSize()));
			SubBuffer subElt = new SubBuffer(buffName, inEdge.getCons()
					.intValue(), expr, outBuffer, inEdge, parentContainer);
			if (parentContainer.getBuffer(inEdge) == null) {
				parentContainer.removeBufferAllocation(parentContainer
						.getBuffer(inEdge));
				parentContainer.addSubBufferAllocation(new SubBufferAllocation(
						subElt));
			}

		}
		return true;
	}

	/**
	 * Generating code for a fork vertex
	 */
	public boolean generateSpecialBehavior(CodeGenSDFForkVertex vertex,
			AbstractBufferContainer parentContainer)
			throws InvalidExpressionException {
		SDFEdge incomingEdge = null;

		for (SDFEdge inEdge : ((SDFGraph) vertex.getBase())
				.incomingEdgesOf(vertex)) {
			incomingEdge = inEdge;
		}
		Buffer inBuffer = parentContainer.getBuffer(incomingEdge);
		for (SDFEdge outEdge : ((SDFGraph) vertex.getBase())
				.outgoingEdgesOf(vertex)) {
			ConstantExpression index = new ConstantExpression("", new DataType(
					"int"),
					vertex.getEdgeIndex(outEdge));
			String buffName = parentContainer.getBuffer(outEdge).getName();
			IExpression expr = new BinaryExpression("%", new BinaryExpression(
					"*", index, new ConstantExpression(outEdge.getProd()
							.intValue())), new ConstantExpression(
					inBuffer.getSize()));
			SubBuffer subElt = new SubBuffer(buffName, outEdge.getProd()
					.intValue(), expr, inBuffer, outEdge, parentContainer);
			if (parentContainer.getBuffer(outEdge) == null) {
				parentContainer.removeBufferAllocation(parentContainer
						.getBuffer(outEdge));
				parentContainer.addSubBufferAllocation(new SubBufferAllocation(
						subElt));
			}
		}
		return true;
	}

	@Override
	public Buffer getBuffer(SDFEdge edge) {
		if (localBuffers.get(edge) == null) {
			return super.getBuffer(edge);
		} else {
			return localBuffers.get(edge);
		}
	}

	/**
	 * Generating code for a broadcast vertex
	 */
	public boolean generateSpecialBehavior(CodeGenSDFBroadcastVertex vertex,
			AbstractBufferContainer parentContainer)
			throws InvalidExpressionException {
		SDFEdge incomingEdge = null;
		for (SDFEdge inEdge : ((SDFGraph) vertex.getBase())
				.incomingEdgesOf(vertex)) {
			incomingEdge = inEdge;
		}
		Buffer inBuffer = parentContainer.getBuffer(incomingEdge);
		for (SDFEdge outEdge : ((SDFGraph) vertex.getBase())
				.outgoingEdgesOf(vertex)) {
			if (!(outEdge.getTarget() instanceof SDFInterfaceVertex)) {
				ConstantExpression index = new ConstantExpression("",
						new DataType("int"), 0);
				String buffName = outEdge.getSourceInterface().getName() + "_"
						+ outEdge.getTargetInterface().getName();
				IExpression expr = new BinaryExpression("%",
						new BinaryExpression("*", index,
								new ConstantExpression(outEdge.getCons()
										.intValue())), new ConstantExpression(
								inBuffer.getSize()));
				SubBuffer subElt = new SubBuffer(buffName, outEdge.getProd()
						.intValue(), expr, inBuffer, outEdge, parentContainer);
				if (parentContainer.getBuffer(outEdge) == null) {
					parentContainer.removeBufferAllocation(parentContainer
							.getBuffer(outEdge));
					parentContainer
							.addSubBufferAllocation(new SubBufferAllocation(
									subElt));
				}
			}
		}
		return true;
	}

	@Override
	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation);
		Iterator<VariableAllocation> iterator2 = variables.iterator();
		while (iterator2.hasNext()) {
			VariableAllocation alloc = iterator2.next();
			alloc.accept(printer, currentLocation); // Accepts allocations
		}
		// print the buffer allocator
		bufferAllocator.accept(printer, currentLocation);

		if (this.getParentContainer() instanceof FiniteForLoop) {
			for (BufferAllocation buff : this.getParentContainer()
					.getBufferAllocations()) {
				if (buff != null) {
					buff.accept(printer, currentLocation);
				}
			}
			for (BufferAllocation buff : this.getParentContainer()
					.getSubBufferAllocations()) {
				if (buff != null) {
					buff.accept(printer, currentLocation);
				}
			}
		}
		// for (BufferAllocation buff : this.getBufferAllocations()) {
		// if (buff != null) {
		// buff.accept(printer, currentLocation);
		// }
		// }
		for (BufferAllocation buff : this.getSubBufferAllocations()) {
			if (buff != null) {
				buff.accept(printer, currentLocation);
			}
		}
		for (ICodeElement call : calls) {
			call.accept(printer, currentLocation);
		}
	}

	public void addCall(ICodeElement elt) {
		calls.add(elt);
	}

	public void addCallAtIndex(ICodeElement elt, int index) {
		if (index < calls.size()) {
			calls.add(index, elt);
		} else {
			calls.add(elt);
		}
	}

	@Override
	public SDFAbstractVertex getCorrespondingVertex() {
		return correspondingVertex;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public AbstractBufferContainer getParentContainer() {
		return parentContainer;
	}

	@Override
	public String toString() {
		return name;
	}
}
