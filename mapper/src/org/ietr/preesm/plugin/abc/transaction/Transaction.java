/**
 * 
 */
package org.ietr.preesm.plugin.abc.transaction;

/**
 * Transactions are used to add implementation vertices and edges to a graph.
 * 
 * @author mpelcat
 */
public abstract class Transaction {

	private boolean isExecuted;
	

	public Transaction() {
		super();
		isExecuted = false;
	}
	
	public boolean isExecuted() {
		return isExecuted;
	}
	
	public void execute(){
		isExecuted = true;
	}

	public void undo(){
		isExecuted = false;
	}
}
