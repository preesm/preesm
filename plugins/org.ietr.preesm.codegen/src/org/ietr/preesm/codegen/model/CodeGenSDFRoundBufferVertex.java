package org.ietr.preesm.codegen.model;

import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;
import net.sf.dftools.algorithm.model.sdf.SDFEdge;
import net.sf.dftools.algorithm.model.sdf.SDFGraph;
import net.sf.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import net.sf.dftools.architecture.slam.ComponentInstance;

import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.buffer.Buffer;
import org.ietr.preesm.codegen.model.buffer.SubBuffer;
import org.ietr.preesm.codegen.model.buffer.SubBufferAllocation;
import org.ietr.preesm.codegen.model.expression.BinaryExpression;
import org.ietr.preesm.codegen.model.expression.ConstantExpression;
import org.ietr.preesm.codegen.model.expression.IExpression;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.core.types.ImplementationPropertyNames;
import org.ietr.preesm.core.types.VertexType;

public class CodeGenSDFRoundBufferVertex extends SDFRoundBufferVertex implements
		ICodeGenSDFVertex, ICodeGenSpecialBehaviorVertex {

	public static final String TYPE = ImplementationPropertyNames.Vertex_vertexType;

	public CodeGenSDFRoundBufferVertex() {
		this.getPropertyBean().setValue(TYPE, VertexType.task);
	}

	public ComponentInstance getOperator() {
		return (ComponentInstance) this.getPropertyBean().getValue(OPERATOR,
				ComponentInstance.class);
	}

	public void setOperator(ComponentInstance op) {
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
