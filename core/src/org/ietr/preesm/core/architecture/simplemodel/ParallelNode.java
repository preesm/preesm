/**
 * 
 */
package org.ietr.preesm.core.architecture.simplemodel;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;

/**
 * A parallel node is a communication node which contention
 * is not taken into account during the deployment simulation
 * 
 * @author mpelcat
 */
public class ParallelNode extends ArchitectureComponent {

	public ParallelNode(String name, ParallelNodeDefinition type) {
		super(name, type);
	}
	
	public ArchitectureComponentType getType(){
		return ArchitectureComponentType.parallelNode;
	}
}
