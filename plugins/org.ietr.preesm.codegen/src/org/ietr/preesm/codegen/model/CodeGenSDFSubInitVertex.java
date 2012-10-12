package org.ietr.preesm.codegen.model;

import net.sf.dftools.algorithm.model.psdf.PSDFInitVertex;
import net.sf.dftools.architecture.slam.ComponentInstance;

import org.ietr.preesm.core.types.ImplementationPropertyNames;
import org.ietr.preesm.core.types.VertexType;

public class CodeGenSDFSubInitVertex extends PSDFInitVertex implements
		ICodeGenSDFVertex {

	public static final String TYPE = ImplementationPropertyNames.Vertex_vertexType;

	public CodeGenSDFSubInitVertex() {
		this.getPropertyBean().setValue(TYPE, VertexType.TASK);
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

}
