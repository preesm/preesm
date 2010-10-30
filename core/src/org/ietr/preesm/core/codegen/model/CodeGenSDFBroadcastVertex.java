/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

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

package org.ietr.preesm.core.codegen.model;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.codegen.Assignment;
import org.ietr.preesm.core.codegen.ICodeElement;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.buffer.BufferAtIndex;
import org.ietr.preesm.core.codegen.buffer.SubBuffer;
import org.ietr.preesm.core.codegen.buffer.SubBufferAllocation;
import org.ietr.preesm.core.codegen.calls.Constant;
import org.ietr.preesm.core.codegen.calls.UserFunctionCall;
import org.ietr.preesm.core.codegen.containers.AbstractCodeContainer;
import org.ietr.preesm.core.codegen.containers.CompoundCodeElement;
import org.ietr.preesm.core.codegen.expression.BinaryExpression;
import org.ietr.preesm.core.codegen.expression.ConstantExpression;
import org.ietr.preesm.core.codegen.expression.IExpression;
import org.ietr.preesm.core.codegen.types.DataType;
import org.sdf4j.SDFMath;
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.sdf.SDFEdge;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.SDFInterfaceVertex;
import org.sdf4j.model.sdf.esdf.SDFBroadcastVertex;

public class CodeGenSDFBroadcastVertex extends SDFBroadcastVertex implements
		ICodeGenSDFVertex, ICodeGenSpecialBehaviorVertex {

	public static final String TYPE = ImplementationPropertyNames.Vertex_vertexType;

	public CodeGenSDFBroadcastVertex() {
		this.getPropertyBean().setValue(TYPE, VertexType.task);
	}

	public ArchitectureComponent getOperator() {
		return (ArchitectureComponent) this.getPropertyBean().getValue(
				OPERATOR, ArchitectureComponent.class);
	}

	public void setOperator(ArchitectureComponent op) {
		this.getPropertyBean().setValue(OPERATOR, getOperator(), op);
	}

	public int getPos() {
		if (this.getPropertyBean().getValue(POS) != null) {
			return (Integer) this.getPropertyBean()
					.getValue(POS, Integer.class);
		}
		return 0;
	}

	public void setPos(int pos) {
		this.getPropertyBean().setValue(POS, getPos(), pos);
	}

	@Override
	public ICodeElement getCodeElement(AbstractCodeContainer parentContainer) {
		SDFEdge incomingEdge = null;
		CompoundCodeElement container = new CompoundCodeElement(this.getName(),
				parentContainer);
		container.setCorrespondingVertex(this);
		for (SDFEdge inEdge : ((SDFGraph) this.getBase()).incomingEdgesOf(this)) {
			incomingEdge = inEdge;
		}
		for (SDFEdge outEdge : ((SDFGraph) this.getBase())
				.outgoingEdgesOf(this)) {
			try {
				if (outEdge.getProd().intValue() == incomingEdge.getCons()
						.intValue()) {
					if (outEdge.getTarget() instanceof CodeGenSDFSendVertex
							|| outEdge.getTarget() instanceof CodeGenSDFSinkInterfaceVertex) {
						UserFunctionCall copyCall = new UserFunctionCall(
								"memcpy", parentContainer);
						copyCall.addArgument(parentContainer.getBuffer(outEdge));
						copyCall.addArgument(parentContainer
								.getBuffer(incomingEdge));
						try {
							copyCall.addArgument(new Constant("size",
									incomingEdge.getCons().intValue()
											+ "*sizeof("
											+ incomingEdge.getDataType()
													.toString() + ")"));
						} catch (InvalidExpressionException e) {
							copyCall.addArgument(new Constant("size", 0));
						}
						container.addCall(copyCall);
					} else {
						Buffer outBuff = parentContainer.getBuffer(outEdge);
						if (outBuff == null) {
							container.addBuffer(
									container.getBuffer(incomingEdge), outEdge);
						} else {
							outBuff.setSize(0);
							Assignment ass = new Assignment(outBuff, "&"
									+ parentContainer.getBuffer(incomingEdge)
											.getName() + "[0]");
							container.addCall(ass);
						}

					}
				} else if (outEdge.getProd().intValue() > incomingEdge
						.getCons().intValue()) {
					int minToken = SDFMath.gcd(outEdge.getProd().intValue(),
							incomingEdge.getCons().intValue());
					for (int i = 0; i < outEdge.getProd().intValue() / minToken; i++) {
						UserFunctionCall copyCall = new UserFunctionCall(
								"memcpy", parentContainer);
						copyCall.addArgument(new BufferAtIndex(
								new ConstantExpression(i * minToken),
								parentContainer.getBuffer(outEdge)));
						copyCall.addArgument(parentContainer
								.getBuffer(incomingEdge));
						copyCall.addArgument(new Constant("size", minToken
								+ "*sizeof("
								+ incomingEdge.getDataType().toString() + ")"));
						container.addCall(copyCall);
					}
				}
			} catch (InvalidExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return container;
	}

	@Override
	public boolean generateSpecialBehavior(
			AbstractBufferContainer parentContainer)
			throws InvalidExpressionException {
		SDFEdge incomingEdge = null;
		for (SDFEdge inEdge : ((SDFGraph) this.getBase()).incomingEdgesOf(this)) {
			incomingEdge = inEdge;
		}
		Buffer inBuffer = parentContainer.getBuffer(incomingEdge);
		for (SDFEdge outEdge : ((SDFGraph) this.getBase())
				.outgoingEdgesOf(this)) {
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

}
