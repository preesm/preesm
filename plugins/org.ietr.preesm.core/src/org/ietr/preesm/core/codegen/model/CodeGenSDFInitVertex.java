package org.ietr.preesm.core.codegen.model;

import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;
import net.sf.dftools.algorithm.model.psdf.PSDFInitVertex;
import net.sf.dftools.algorithm.model.psdf.types.PSDFEdgePropertyType;
import net.sf.dftools.algorithm.model.sdf.SDFEdge;
import net.sf.dftools.algorithm.model.sdf.SDFGraph;
import net.sf.dftools.architecture.slam.ComponentInstance;

import org.ietr.preesm.core.codegen.ICodeElement;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.buffer.SubBuffer;
import org.ietr.preesm.core.codegen.calls.Constant;
import org.ietr.preesm.core.codegen.calls.UserFunctionCall;
import org.ietr.preesm.core.codegen.containers.AbstractCodeContainer;
import org.ietr.preesm.core.codegen.containers.CompoundCodeElement;
import org.ietr.preesm.core.codegen.types.CodeSectionType;

public class CodeGenSDFInitVertex extends PSDFInitVertex implements
		ICodeGenSDFVertex {

	public static final String TYPE = ImplementationPropertyNames.Vertex_vertexType;

	public CodeGenSDFInitVertex() {
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

	public String toString() {
		return "";
	}

	@Override
	public ICodeElement getCodeElement(AbstractCodeContainer parentContainer) {
		CompoundCodeElement container = new CompoundCodeElement(this.getName(),
				parentContainer);
		container.setCorrespondingVertex(this);
		UserFunctionCall call = new UserFunctionCall(this, parentContainer,
				CodeSectionType.loop, false);
		container.addCall(call);
		for (SDFEdge edge : ((SDFGraph) this.getBase()).edgeSet()) {
			if (edge.getCons() instanceof PSDFEdgePropertyType) {
				PSDFEdgePropertyType prop = (PSDFEdgePropertyType) edge
						.getCons();
				String name = prop.getSymbolicName();
				if (this.getAffectedParameter(name) != null) {
					Buffer buff = parentContainer.getBuffer(edge);
					while (buff != null && buff instanceof SubBuffer)
						buff = ((SubBuffer) buff).getParentBuffer();
					if (buff != null && buff.getAllocatedSize() == 0) {
						UserFunctionCall mallocCall = new UserFunctionCall(
								"malloc", parentContainer);
						try {
							mallocCall.setReturn(buff);
							mallocCall.addArgument(new Constant("size", name
									+ "*" + edge.getTarget().getNbRepeat()
									+ "*" + "sizeof("
									+ buff.getType().getTypeName() + ")"));
						} catch (InvalidExpressionException e) {
							mallocCall.addArgument(new Constant("size", 0));
						}
						container.addCall(mallocCall);
					}
				}
			} else if (edge.getProd() instanceof PSDFEdgePropertyType) {
				PSDFEdgePropertyType prop = (PSDFEdgePropertyType) edge
						.getProd();
				String name = prop.getSymbolicName();
				if (this.getAffectedParameter(name) != null) {
					Buffer buff = parentContainer.getBuffer(edge);
					while (buff != null && buff instanceof SubBuffer)
						buff = ((SubBuffer) buff).getParentBuffer();
					SDFEdge allocEdge = buff.getEdge();
					if (buff != null) {
						UserFunctionCall mallocCall = new UserFunctionCall(
								"malloc", parentContainer);
						try {
							mallocCall.setReturn(buff);
							mallocCall.addArgument(new Constant("size", name
									+ "*" + allocEdge.getSource().getNbRepeat()
									+ "*" + "sizeof("
									+ buff.getType().getTypeName() + ")"));
						} catch (InvalidExpressionException e) {
							mallocCall.addArgument(new Constant("size", 0));
						}
						container.addCall(mallocCall);
					}
				}
			}
		}
		AbstractBufferContainer oldContainer = parentContainer;
		AbstractBufferContainer newContainer = parentContainer;
		do {
			newContainer = parentContainer.getParentContainer();
		} while ((newContainer != null && newContainer instanceof AbstractBufferContainer)
				&& !(newContainer instanceof AbstractCodeContainer));
		if (newContainer == null) {
			newContainer = oldContainer;
		}
		container.setParentContainer(newContainer);
		return container;
	}

}
