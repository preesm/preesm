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

package org.ietr.preesm.core.codegen.containers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;
import net.sf.dftools.algorithm.model.parameters.Parameter;
import net.sf.dftools.algorithm.model.psdf.parameters.PSDFDynamicParameter;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.algorithm.model.sdf.SDFEdge;
import net.sf.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import net.sf.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import net.sf.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import net.sf.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import net.sf.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;

import org.ietr.preesm.core.codegen.ICodeElement;
import org.ietr.preesm.core.codegen.VariableAllocation;
import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.buffer.BufferAllocation;
import org.ietr.preesm.core.codegen.calls.Constant;
import org.ietr.preesm.core.codegen.calls.UserFunctionCall;
import org.ietr.preesm.core.codegen.calls.Variable;
import org.ietr.preesm.core.codegen.factories.CodeElementFactory;
import org.ietr.preesm.core.codegen.model.CodeGenSDFBroadcastVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFForkVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFGraph;
import org.ietr.preesm.core.codegen.model.CodeGenSDFInitVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFJoinVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFRoundBufferVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFSubInitVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFTokenInitVertex;
import org.ietr.preesm.core.codegen.model.ICodeGenSDFVertex;
import org.ietr.preesm.core.codegen.model.ICodeGenSpecialBehaviorVertex;
import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.ietr.preesm.core.codegen.types.CodeSectionType;
import org.ietr.preesm.core.codegen.types.DataType;


/**
 * A compound element is a code element containing multiple call or code
 * elements limited by closure. All buffer defined in this elemen have a local
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
			ICodeGenSDFVertex correspondingVertex) {
		super(parentContainer);
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
							&& !(edge.getTarget() instanceof CodeGenSDFTokenInitVertex)) {
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
				treatCalls(graph.vertexSet());
			} else {
				this.addCall(new UserFunctionCall(this.correspondingVertex,
						this, CodeSectionType.loop, false));
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
			AbstractBufferContainer parentContainer) {
		super(parentContainer);
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
	private void treatCalls(Set<SDFAbstractVertex> vertices) {
		List<SDFAbstractVertex> treated = new ArrayList<SDFAbstractVertex>();
		// treat special calls which are mainly related to buffer
		// splitting/grouping or copying
		for (SDFAbstractVertex vertex : vertices) {
			if (vertex instanceof ICodeGenSpecialBehaviorVertex) {
				if (treatSpecialBehaviorVertex(vertex.getName(), this, vertex)) {
					treated.add(vertex);
				}
			}
		}
		// treat other vertices
		for (SDFAbstractVertex vertex : vertices) {
			if (vertex instanceof ICodeGenSDFVertex
					&& !(vertex instanceof SDFInterfaceVertex)
					&& !treated.contains(vertex)) {
				ICodeElement loopCall = CodeElementFactory.createElement(
						vertex.getName(), this, vertex);
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
			// else if (vertex instanceof CodeGenSDFBroadcastVertex
			// && !treated.contains(vertex)) {
			// SDFEdge incomingEdge = null;
			// for (SDFEdge inEdge : vertex.getBase().incomingEdgesOf(vertex)) {
			// incomingEdge = inEdge;
			// }
			// for (SDFEdge outEdge : vertex.getBase().outgoingEdgesOf(vertex))
			// {
			// UserFunctionCall copyCall = new UserFunctionCall("memcpy",
			// this);
			// copyCall.addParameter(this.getBuffer(outEdge));
			// copyCall.addParameter(this.getBuffer(incomingEdge));
			// try {
			// copyCall.addParameter(new Constant("size", incomingEdge
			// .getCons().intValue()
			// + "*sizeof("
			// + incomingEdge.getDataType().toString() + ")"));
			// } catch (InvalidExpressionException e) {
			// copyCall.addParameter(new Constant("size", 0));
			// }
			// this.addCall(copyCall);
			// }
			// }
		}
	}

	/**
	 * Treats special behavior vertices
	 * 
	 * @param name
	 *            The name of the call
	 * @param parentContainer
	 *            The parent container
	 * @param vertex
	 *            The vertex from which to create the call
	 * @return True if the creation succeed, false otherwise
	 */
	public boolean treatSpecialBehaviorVertex(String name,
			AbstractBufferContainer parentContainer, SDFAbstractVertex vertex) {
		try {
			if (vertex instanceof CodeGenSDFForkVertex) {
				((CodeGenSDFForkVertex) vertex)
						.generateSpecialBehavior(parentContainer);
			} else if (vertex instanceof CodeGenSDFJoinVertex) {
				((CodeGenSDFJoinVertex) vertex)
						.generateSpecialBehavior(parentContainer);
			} else if (vertex instanceof CodeGenSDFBroadcastVertex) {
				((CodeGenSDFBroadcastVertex) vertex)
						.generateSpecialBehavior(parentContainer);
			} else if (vertex instanceof CodeGenSDFRoundBufferVertex) {
				((CodeGenSDFRoundBufferVertex) vertex)
						.generateSpecialBehavior(parentContainer);
			}
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
		}
		return true;
	}

	public Buffer getBuffer(SDFEdge edge) {
		if (localBuffers.get(edge) == null) {
			return super.getBuffer(edge);
		} else {
			return localBuffers.get(edge);
		}
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

	public String getName() {
		return name;
	}

	public AbstractBufferContainer getParentContainer() {
		return parentContainer;
	}

	@Override
	public String toString() {
		return name;
	}
}
