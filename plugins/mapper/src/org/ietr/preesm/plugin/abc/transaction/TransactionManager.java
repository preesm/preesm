/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.plugin.abc.transaction;

import java.util.Iterator;
import java.util.LinkedList;

import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * This is a transaction container that enables the consecutive
 * execution of several listed transactions.
 * 
 * @author mpelcat
 */
public class TransactionManager {
	
	LinkedList<Transaction> transactionList = new LinkedList<Transaction>();
	
	public void executeTransactionList(){
		Iterator<Transaction> it = transactionList.iterator();
		
		while(it.hasNext()){
			Transaction currentT = it.next();
			if(!currentT.isExecuted())
				currentT.execute();
		}
	}
	
	public void undoTransactionList(){
		
		while(!transactionList.isEmpty()){
			Transaction currentT = transactionList.getLast();
			if(currentT.isExecuted())
				currentT.undo();
			transactionList.removeLast();
		}
	}
	
	public void undoTransactions(MapperDAGVertex refVertex){
		
		// All transactions relative to a vertex are grouped. Once we are out
		// of this group, we can stop undoing them
		boolean alreadyUndoneFew=false;
		
		Iterator<Transaction> it = transactionList.descendingIterator();
		
		while(it.hasNext()){
			Transaction currentT = it.next();
			if(currentT.getRef() != null && currentT.getRef().equals(refVertex)){
				if(currentT.isExecuted())
					currentT.undo();
				it.remove(); // Removing the transation from the list
				alreadyUndoneFew = true;
			}
			else if(alreadyUndoneFew){
				return;
			}
		}
	}
	
	public void add(Transaction transaction, MapperDAGVertex refVertex){
		transaction.setRef(refVertex);
		transactionList.add(transaction);
	}
}
