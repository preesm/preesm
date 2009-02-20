package org.ietr.preesm.core.architecture.simplemodel;

import org.ietr.preesm.core.architecture.ArchitectureComponentDefinition;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;

public class ContentionNodeDefinition extends ArchitectureComponentDefinition {


	public ContentionNodeDefinition(String id) {
		super(id, "contentionNode");
	}

	public ArchitectureComponentType getType() {
		return ArchitectureComponentType.contentionNode;
	}

	public ContentionNodeDefinition clone() {

		// A new OperatorDefinition is created with same id
		ContentionNodeDefinition newdef = new ContentionNodeDefinition(this.getId());

		return newdef;
	}

	public void fill(ArchitectureComponentDefinition origin) {
	}
}
