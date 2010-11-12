/**
 * 
 */
package org.ietr.preesm.core.architecture.simplemodel;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentDefinition;

/**
 * Node of communication between two operators
 * 
 * @author mpelcat
 */
public abstract class AbstractNode extends ArchitectureComponent {

	public AbstractNode(String id, ArchitectureComponentDefinition definition) {
		super(id, definition);
		// TODO Auto-generated constructor stub
	}

	public boolean isNode() {
		return true;
	}

}
