package org.ietr.preesm.algorithm.transforms;

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;

public class ClustVertex extends AbstractClust {

	private SDFAbstractVertex vertex;

	public SDFAbstractVertex getVertex() {
		return vertex;
	}

	public void setVertex(SDFAbstractVertex vertex) {
		this.vertex = vertex;
	}
}
