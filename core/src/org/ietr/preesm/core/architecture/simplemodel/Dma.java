/**
 * 
 */
package org.ietr.preesm.core.architecture.simplemodel;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;

/**
 * A Direct Memory Access is set up by an operator to transfer data
 * 
 * @author mpelcat
 */
public class Dma extends ArchitectureComponent {

	public Dma(String name, DmaDefinition type) {
		super(name, type);

	}
	
	public ArchitectureComponentType getType(){
		return ArchitectureComponentType.dma;
	}
}
