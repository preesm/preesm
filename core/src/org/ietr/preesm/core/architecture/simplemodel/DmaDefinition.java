/**
 * 
 */
package org.ietr.preesm.core.architecture.simplemodel;

import org.ietr.preesm.core.architecture.ArchitectureComponentDefinition;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;

/**
 * @author mpelcat
 *
 */
public class DmaDefinition extends ArchitectureComponentDefinition {

	public DmaDefinition(String id) {
		super(id, "dma");
	}

	public ArchitectureComponentType getType() {
		return ArchitectureComponentType.dma;
	}

	public DmaDefinition clone() {

		// A new OperatorDefinition is created with same id
		DmaDefinition newdef = new DmaDefinition(this.getId());

		return newdef;
	}

	public void fill(ArchitectureComponentDefinition origin) {
	}
}
