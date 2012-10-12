package org.ietr.preesm.codegen.model;

import net.sf.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import net.sf.dftools.architecture.slam.ComponentInstance;

import org.ietr.preesm.core.types.ImplementationPropertyNames;
import org.ietr.preesm.core.types.VertexType;

/**
 * A round buffer accesses a buffer in a circular way to respect the hierarchy 
 * insulation of the IBSDF model
 * 
 * @author jpiat
 */
public class CodeGenSDFRoundBufferVertex extends SDFRoundBufferVertex implements
		ICodeGenSDFVertex, ICodeGenSpecialBehaviorVertex {

	public static final String TYPE = ImplementationPropertyNames.Vertex_vertexType;

	public CodeGenSDFRoundBufferVertex() {
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

}
