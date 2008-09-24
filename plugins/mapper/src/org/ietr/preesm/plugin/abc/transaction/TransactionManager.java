/**
 * 
 */
package org.ietr.preesm.plugin.abc.transaction;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * This is a transaction container that enables the consecutive
 * execution of several listed transactions.
 * 
 * @author mpelcat
 */
public class TransactionManager extends LinkedList<Transaction> {

	public void executeTransactionList(){
		Iterator<Transaction> it = iterator();
		
		while(it.hasNext()){
			Transaction currentT = it.next();
			if(!currentT.isExecuted())
				currentT.execute();
		}
	}
	
	public void undoTransactionList(){
		
		while(!this.isEmpty()){
			Transaction currentT = this.getLast();
			if(currentT.isExecuted())
				currentT.undo();
			this.removeLast();
		}
	}
}
