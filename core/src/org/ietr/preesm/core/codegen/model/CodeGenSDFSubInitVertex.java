package org.ietr.preesm.core.codegen.model;

import org.ietr.preesm.core.architecture.Component;
import org.ietr.preesm.core.codegen.ICodeElement;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.containers.AbstractCodeContainer;
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.psdf.PSDFInitVertex;

public class CodeGenSDFSubInitVertex extends PSDFInitVertex implements
		ICodeGenSDFVertex {

	public static final String TYPE = ImplementationPropertyNames.Vertex_vertexType;

	public CodeGenSDFSubInitVertex() {
		this.getPropertyBean().setValue(TYPE, VertexType.task);
	}

	public Component getOperator() {
		return (Component) this.getPropertyBean().getValue(
				OPERATOR, Component.class);
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

	public String toString() {
		return "";
	}

	@Override
	public ICodeElement getCodeElement(AbstractCodeContainer parentContainer)
			throws InvalidExpressionException {
		// TODO Auto-generated method stub
		return null;
	}

}
