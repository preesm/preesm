package org.ietr.preesm.core.architecture.simplemodel;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;

/**
 * A contention node is a communication node which contention
 * is taken into account during the deployment simulation
 * 
 * @author mpelcat
 */
public class ContentionNode extends ArchitectureComponent {

	public ContentionNode(String name, ContentionNodeDefinition type) {
		super(name, type);
	}
	
	public ArchitectureComponentType getType(){
		return ArchitectureComponentType.contentionNode;
	}
}
