package org.ietr.preesm.core.codegen.model;

import org.ietr.preesm.core.architecture.Component;
import org.ietr.preesm.core.codegen.ICodeElement;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.buffer.Buffer;
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
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.sdf.SDFEdge;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.esdf.SDFRoundBufferVertex;

public class CodeGenSDFRoundBufferVertex extends SDFRoundBufferVertex implements
		ICodeGenSDFVertex, ICodeGenSpecialBehaviorVertex {

	public static final String TYPE = ImplementationPropertyNames.Vertex_vertexType;

	public CodeGenSDFRoundBufferVertex() {
		this.getPropertyBean().setValue(TYPE, VertexType.task);
	}

	public Component getOperator() {
		return (Component) this.getPropertyBean().getValue(OPERATOR,
				Component.class);
	}

	public void setOperator(Component op) {
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
		SDFEdge outgoingEdge = null;
		CompoundCodeElement container = new CompoundCodeElement(this.getName(),
				parentContainer);
		container.setCorrespondingVertex(this);
		for (SDFEdge outedge : ((SDFGraph) this.getBase())
				.outgoingEdgesOf(this)) {
			outgoingEdge = outedge;
		}
		for (SDFEdge inEdge : ((SDFGraph) this.getBase()).incomingEdgesOf(this)) {
			if (this.getEdgeIndex(inEdge) == (((SDFGraph) this.getBase())
					.incomingEdgesOf(this).size() - 1)) {
				UserFunctionCall copyCall = new UserFunctionCall("memcpy",
						parentContainer);
				try {
					copyCall.addArgument(parentContainer
							.getBuffer(outgoingEdge));
					copyCall.addArgument(parentContainer.getBuffer(inEdge));
					copyCall.addArgument(new Constant("size", inEdge.getProd()
							.intValue()
							+ "*sizeof("
							+ outgoingEdge.getDataType().toString() + ")"));
				} catch (InvalidExpressionException e) {
					copyCall.addArgument(new Constant("size", 0));
				}
				container.addCall(copyCall);
			}
		}
		return container;
	}

	@Override
	public boolean generateSpecialBehavior(
			AbstractBufferContainer parentContainer)
			throws InvalidExpressionException {

		SDFEdge outgoingEdge = null;
		for (SDFEdge outEdge : ((SDFGraph) this.getBase())
				.outgoingEdgesOf(this)) {
			outgoingEdge = outEdge;
		}
		Buffer outBuffer = parentContainer.getBuffer(outgoingEdge);
		for (SDFEdge inEdge : ((SDFGraph) this.getBase()).incomingEdgesOf(this)) {
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

}
