/**
 * 
 */
package org.ietr.preesm.core.architecture.route;

import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.architecture.simplemodel.Operator;

/**
 * Represents a single step in a route between two operators
 * 
 * @author mpelcat
 */
public abstract class AbstractRouteStep {

	private Operator receiver;

	private Operator sender;

	public AbstractRouteStep(Operator sender, Operator receiver) {
		super();
		this.sender = sender;
		this.receiver = receiver;
	}

	public Operator getReceiver() {
		return receiver;
	}

	public Operator getSender() {
		return sender;
	}

	public void setReceiver(Operator receiver) {
		this.receiver = receiver;
	}

	public void setSender(Operator sender) {
		this.sender = sender;
	}
}
