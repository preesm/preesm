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

import org.ietr.preesm.core.codegen.UserFunctionCall.CodeSection;
import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.buffer.BufferAllocation;
import org.ietr.preesm.core.codegen.buffer.SubBuffer;
import org.ietr.preesm.core.codegen.buffer.SubBufferAllocation;
import org.ietr.preesm.core.codegen.model.CodeGenSDFBroadcastVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFForkVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFGraph;
import org.ietr.preesm.core.codegen.model.CodeGenSDFJoinVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFRoundBufferVertex;
import org.ietr.preesm.core.codegen.model.ICodeGenSDFVertex;
import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;
import org.sdf4j.model.sdf.SDFInterfaceVertex;
import org.sdf4j.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.sdf4j.model.sdf.esdf.SDFSourceInterfaceVertex;

public class CompoundCodeElement extends AbstractBufferContainer implements
		ICodeElement {

	private List<ICodeElement> calls;

	private HashMap<SDFEdge, Buffer> allocatedBuffers;

	private SDFAbstractVertex correspondingVertex;

	private String name;

	private AbstractBufferContainer parentContainer;

	public CompoundCodeElement(String name,
			AbstractBufferContainer parentContainer,
			ICodeGenSDFVertex correspondingVertex) {
		super(parentContainer);
		allocatedBuffers = new HashMap<SDFEdge, Buffer>();
		this.name = name;
		this.parentContainer = parentContainer;
		this.correspondingVertex = (SDFAbstractVertex) correspondingVertex;
		calls = new ArrayList<ICodeElement>();
		try {
			if (correspondingVertex.getGraphDescription() != null) {
				CodeGenSDFGraph graph = (CodeGenSDFGraph) correspondingVertex
						.getGraphDescription();
				for (SDFEdge edge : graph.edgeSet()) {
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
					} else if (this.getBuffer(edge) == null) {
						String bufferName = edge.getSourceInterface().getName()
								+ "_" + edge.getTargetInterface().getName();
						if (edge.getTarget() == edge.getSource()) {
							this.addBuffer(new BufferAllocation(new Buffer(
									bufferName, Math.max(edge.getProd()
											.intValue(), edge.getCons()
											.intValue()), new DataType(edge
											.getDataType().toString()), edge,
									parentContainer)));
						} else {
							this.addBuffer(new BufferAllocation(
									new Buffer(bufferName, Math.max(edge
											.getProd().intValue()
											* edge.getSource().getNbRepeat(),
											edge.getCons().intValue()
													* edge.getTarget()
															.getNbRepeat()),
											new DataType(edge.getDataType()
													.toString()), edge,
											parentContainer)));
						}
					}
					if (edge.getDelay().intValue() > 0) {
						UserFunctionCall initCall = new UserFunctionCall(
								"init_" + edge.getTargetInterface().getName(),
								this);
						initCall.addParameter(this.getBuffer(edge));
						initCall.addParameter(new Constant("init_size", edge
								.getDelay().intValue()));
						this.addCall(initCall);
					}
				}
				treatCalls(graph.vertexSet());
			} else {
				this.addCall(new UserFunctionCall(this.correspondingVertex,
						this, CodeSection.LOOP));
			}
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
		}
	}

	public void addBuffer(Buffer buff, SDFEdge edge) {
		if (allocatedBuffers.get(edge) == null) {
			allocatedBuffers.put(edge, buff);
		}
	}

	private void treatCalls(Set<SDFAbstractVertex> vertices) {
		List<SDFAbstractVertex> treated = new ArrayList<SDFAbstractVertex>();
		for (SDFAbstractVertex vertex : vertices) {
			if (vertex instanceof CodeGenSDFForkVertex
					|| vertex instanceof CodeGenSDFJoinVertex
					|| vertex instanceof CodeGenSDFBroadcastVertex
					|| vertex instanceof CodeGenSDFRoundBufferVertex) {
				if (treatSpecialBehaviorVertex(vertex.getName(), this, vertex)) {
					treated.add(vertex);
				}
			}
		}
		for (SDFAbstractVertex vertex : vertices) {
			if (vertex instanceof ICodeGenSDFVertex
					&& !(vertex instanceof SDFInterfaceVertex) && !(vertex instanceof CodeGenSDFBroadcastVertex)) {
				ICodeElement loopCall = CodeElementFactory.createElement(vertex
						.getName(), this, vertex);
				if (loopCall != null) {
					this.addCall(loopCall);
				}
			} else if (vertex instanceof CodeGenSDFBroadcastVertex
					&& !treated.contains(vertex)) {
				SDFEdge incomingEdge = null;
				for (SDFEdge inEdge : vertex.getBase().incomingEdgesOf(vertex)) {
					incomingEdge = inEdge;
				}
				for (SDFEdge outEdge : vertex.getBase().outgoingEdgesOf(vertex)) {
					UserFunctionCall copyCall = new UserFunctionCall("memcpy",
							this);
					copyCall.addParameter(this.getBuffer(outEdge));
					copyCall.addParameter(this.getBuffer(incomingEdge));
					try {
						copyCall.addParameter(new Constant("size", incomingEdge.getCons().intValue()+"*sizeof("+incomingEdge.getDataType().toString()+")"));
					} catch (InvalidExpressionException e) {
						copyCall.addParameter(new Constant("size", 0));
					}
					this.addCall(copyCall);
				}
			}
		}
	}

	public boolean treatSpecialBehaviorVertex(String name,
			AbstractBufferContainer parentContainer, SDFAbstractVertex vertex) {
		try {
			if (vertex instanceof CodeGenSDFForkVertex) {
				SDFEdge incomingEdge = null;
				int i = 0;
				for (SDFEdge inEdge : vertex.getBase().incomingEdgesOf(vertex)) {
					incomingEdge = inEdge;
				}
				Buffer inBuffer = parentContainer.getBuffer(incomingEdge);
				for (SDFEdge outEdge : vertex.getBase().outgoingEdgesOf(vertex)) {
					ConstantValue index = new ConstantValue("", new DataType(
							"int"), ((CodeGenSDFForkVertex) vertex)
							.getEdgeIndex(outEdge));
					String buffName = parentContainer.getBuffer(outEdge)
							.getName();
					SubBuffer subElt = new SubBuffer(buffName, outEdge
							.getProd().intValue(), index, inBuffer, outEdge,
							parentContainer);
					if (allocatedBuffers.get(outEdge) == null) {
						parentContainer.removeBufferAllocation(parentContainer
								.getBuffer(outEdge));
						parentContainer.addBuffer(new SubBufferAllocation(
								subElt));
					}
					i++;
				}
			} else if (vertex instanceof CodeGenSDFJoinVertex) {
				SDFEdge outgoingEdge = null;
				int i = 0;
				for (SDFEdge outEdge : vertex.getBase().outgoingEdgesOf(vertex)) {
					outgoingEdge = outEdge;
				}
				Buffer outBuffer = parentContainer.getBuffer(outgoingEdge);
				for (SDFEdge inEdge : vertex.getBase().incomingEdgesOf(vertex)) {
					ConstantValue index = new ConstantValue("", new DataType(
							"int"), ((CodeGenSDFJoinVertex) vertex)
							.getEdgeIndex(inEdge));
					String buffName = parentContainer.getBuffer(inEdge)
							.getName();
					SubBuffer subElt = new SubBuffer(buffName, inEdge.getCons()
							.intValue(), index, outBuffer, inEdge,
							parentContainer);
					if (allocatedBuffers.get(inEdge) == null) {
						parentContainer.removeBufferAllocation(parentContainer
								.getBuffer(inEdge));
						parentContainer.addBuffer(new SubBufferAllocation(
								subElt));
					}
					i++;
				}
			} else if (vertex instanceof CodeGenSDFBroadcastVertex) {
				SDFEdge incomingEdge = null;
				for (SDFEdge inEdge : vertex.getBase().incomingEdgesOf(vertex)) {
					incomingEdge = inEdge;
				}
				Buffer inBuffer = parentContainer.getBuffer(incomingEdge);
				for (SDFEdge outEdge : vertex.getBase().outgoingEdgesOf(vertex)) {
					if (outEdge.getTarget() instanceof SDFInterfaceVertex) {
						return false;
					}
				}
				for (SDFEdge outEdge : vertex.getBase().outgoingEdgesOf(vertex)) {
					ConstantValue index = new ConstantValue("", new DataType(
							"int"), 0);
					String buffName = parentContainer.getBuffer(outEdge)
							.getName();
					SubBuffer subElt = new SubBuffer(buffName, outEdge
							.getProd().intValue(), index, inBuffer, outEdge,
							parentContainer);
					if (allocatedBuffers.get(outEdge) == null) {
						parentContainer.removeBufferAllocation(parentContainer
								.getBuffer(outEdge));
						parentContainer.addBuffer(new SubBufferAllocation(
								subElt));
					}
				}
			} else if (vertex instanceof CodeGenSDFRoundBufferVertex) {
				SDFEdge outgoingEdge = null;
				for (SDFEdge outEdge : vertex.getBase().outgoingEdgesOf(vertex)) {
					outgoingEdge = outEdge;
				}
				Buffer outBuffer = parentContainer.getBuffer(outgoingEdge);
				for (SDFEdge inEdge : vertex.getBase().incomingEdgesOf(vertex)) {
					ConstantValue index = new ConstantValue("", new DataType(
							"int"), 0);
					String buffName = parentContainer.getBuffer(inEdge)
							.getName();
					SubBuffer subElt = new SubBuffer(buffName, inEdge.getCons()
							.intValue(), index, outBuffer, inEdge,
							parentContainer);
					if (allocatedBuffers.get(inEdge) == null) {
						parentContainer.removeBufferAllocation(parentContainer
								.getBuffer(inEdge));
						parentContainer.addBuffer(new SubBufferAllocation(
								subElt));
					}
					;
				}
			}
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
		}
		return true;
	}

	public Buffer getBuffer(SDFEdge edge) {
		if (allocatedBuffers.get(edge) == null) {
			return super.getBuffer(edge);
		} else {
			return allocatedBuffers.get(edge);
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
		if (this.getParentContainer() instanceof FiniteForLoop) {
			for (BufferAllocation buff : this.getParentContainer()
					.getBufferAllocations()) {
				if (buff != null) {
					buff.accept(printer, currentLocation);
				}
			}
		}
		for (BufferAllocation buff : this.getBufferAllocations()) {
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

}
