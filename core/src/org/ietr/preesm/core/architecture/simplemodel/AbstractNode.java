/**
 * 
 */
package org.ietr.preesm.core.architecture.simplemodel;

import org.ietr.preesm.core.architecture.Component;
import org.ietr.preesm.core.architecture.ComponentDefinition;

/**
 * Node of communication between two operators
 * 
 * @author mpelcat
 */
public abstract class AbstractNode extends Component {

	public AbstractNode(String id, ComponentDefinition definition) {
		super(id, definition);
		// TODO Auto-generated constructor stub
	}

	public boolean isNode() {
		return true;
	}

}
