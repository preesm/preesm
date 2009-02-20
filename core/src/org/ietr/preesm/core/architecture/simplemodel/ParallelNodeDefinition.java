package org.ietr.preesm.core.architecture.simplemodel;

import org.ietr.preesm.core.architecture.ArchitectureComponentDefinition;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;

public class ParallelNodeDefinition extends ArchitectureComponentDefinition {

	public ParallelNodeDefinition(String id) {
		super(id, "parallelNode");
	}

	public ArchitectureComponentType getType() {
		return ArchitectureComponentType.parallelNode;
	}

	public ParallelNodeDefinition clone() {

		// A new OperatorDefinition is created with same id
		ParallelNodeDefinition newdef = new ParallelNodeDefinition(this.getId());

		return newdef;
	}

	public void fill(ArchitectureComponentDefinition origin) {
	}
}
