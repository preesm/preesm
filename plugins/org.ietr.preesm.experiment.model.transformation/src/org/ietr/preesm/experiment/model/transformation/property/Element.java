package org.ietr.preesm.experiment.model.transformation.property;

import org.eclipse.swt.graphics.Image;

public abstract class Element {

	private String name;

	public Element(String aName) {
		super ();
		this .name = aName;
	}

	public String getName() {
		return name;
	}

	public abstract Image getImage();

}
