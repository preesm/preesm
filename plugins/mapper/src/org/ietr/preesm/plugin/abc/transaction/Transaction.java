/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-Fran�ois Nezan, Micka�l Raulet

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

import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * Transactions are used to modify a graph with the possibility to withdraw
 * actions at any time. They may be complex because the undo process must 
 * work in any order. Local transactions are sometimes used, not for their
 * undoing capabilities but because these actions can be created and stored
 * while going through a graph and then executed altogether afterwards in the
 * right order.
 * 
 * @author mpelcat
 */
public abstract class Transaction {

	// true if this transaction has already been executed
	private boolean isExecuted;
	
	// Vertex that fired the current transaction
	private MapperDAGVertex ref;

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

	public MapperDAGVertex getRef() {
		return ref;
	}

	public void setRef(MapperDAGVertex ref) {
		this.ref = ref;
	}

	@Override
	public abstract String toString();
	
	
}
