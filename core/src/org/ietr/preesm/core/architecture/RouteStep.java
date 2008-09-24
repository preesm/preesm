/**
 * 
 */
package org.ietr.preesm.core.architecture;


/**
 * Represents a single step in a route between two operators
 * 
 * @author mpelcat
 */
public class RouteStep {

	private Medium medium;

	private Operator receiver;

	private Operator sender;

	public RouteStep(Operator sender, Medium medium, Operator receiver) {
		super();
		this.sender = sender;
		this.medium = medium;
		this.receiver = receiver;
	}

	public Medium getMedium() {
		return medium;
	}

	public Operator getReceiver() {
		return receiver;
	}

	public Operator getSender() {
		return sender;
	}

	public void setMedium(Medium medium) {
		this.medium = medium;
	}

	public void setReceiver(Operator receiver) {
		this.receiver = receiver;
	}

	public void setSender(Operator sender) {
		this.sender = sender;
	}

	@Override
	public String toString() {
		return "{" + sender.toString() + " -> " + medium.toString() + " -> "
				+ receiver.toString() + "}";
	}
}
