package org.ietr.preesm.core.codegen.model;

import org.sdf4j.factories.SDFEdgeFactory;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.types.SDFIntEdgePropertyType;
import org.sdf4j.model.sdf.types.SDFStringEdgePropertyType;

public class CodeGenSDFEdgeFactory extends SDFEdgeFactory{

	@Override
	public CodeGenSDFEdge createEdge(SDFAbstractVertex arg0,
			SDFAbstractVertex arg1) {
		return new CodeGenSDFEdge(new SDFIntEdgePropertyType(1),
				new SDFIntEdgePropertyType(1),
				new SDFIntEdgePropertyType(0),
				new SDFStringEdgePropertyType("char"));
	}

}
