/**
 * 
 */
package org.ietr.preesm.core.scenario.editor;

import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * Class used as a scenario editor tree content to distinguish two vertices with
 * the same name but different paths (stored in "info" property)
 * 
 * @author mpelcat
 */
public class VertexWithPath {


	private SDFAbstractVertex storedVertex;

	public VertexWithPath(SDFAbstractVertex storedVertex) {
		super();
		this.storedVertex = storedVertex;
	}

	public SDFAbstractVertex getStoredVertex() {
		return storedVertex;
	}

	public String getName() {
		return storedVertex.getName();
	}

	/**
	 * Checking equality between vertices but also between their paths
	 */
	public boolean equals(Object e) {
		if (e instanceof VertexWithPath) {
			VertexWithPath v = ((VertexWithPath) e);
			SDFAbstractVertex vStored = v.getStoredVertex();
			SDFAbstractVertex thisStored = this.getStoredVertex();

			boolean equals = vStored.equals(thisStored);

			if (equals) {
				if (!(vStored.getInfo() == null || thisStored.getInfo() == null)) {
					if ((!(vStored.getInfo().isEmpty()) || thisStored.getInfo()
							.isEmpty())) {
						equals = vStored.getInfo()
								.equals(thisStored.getInfo());
					}
				}
			}
			return equals;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return storedVertex.getInfo();
	}
}
