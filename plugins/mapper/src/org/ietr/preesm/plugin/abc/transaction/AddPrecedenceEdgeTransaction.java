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

import org.ietr.preesm.plugin.abc.order.SchedulingOrderManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdge;

/**
 * Transaction executing the addition of a {@link PrecedenceEdge}.
 * 
 * @author mpelcat
 */
public class AddPrecedenceEdgeTransaction extends Transaction {

	// Inputs
	/**
	 * The object handling the schedulings as well as the total order.
	 */
	private SchedulingOrderManager orderManager;
	
	/**
	 * Implementation DAG to which the edge is added
	 */
	private MapperDAG implementation = null;

	/**
	 * Source of the added edge
	 */
	private MapperDAGVertex source = null;

	/**
	 * Destination of the added edge
	 */
	private MapperDAGVertex destination = null;

	/**
	 * Boolean precising which one between the source and the target created
	 * this transaction
	 */
	public static final int simpleDelete = 0; // Removing the edge only
	public static final int compensateSourceRemoval = 1; // Removing the edge
	// and adding a new edge between the target and its predecessor
	public static final int compensateTargetRemoval = 2; // Removing the edge
	// and adding a new edge between the source and its successor
	private int undoType = simpleDelete;

	// Generated objects
	/**
	 * edges added
	 */
	private PrecedenceEdge precedenceEdge = null;

	public AddPrecedenceEdgeTransaction(SchedulingOrderManager orderManager, MapperDAG implementation,
			MapperDAGVertex source, MapperDAGVertex destination, int undoType) {
		super();
		this.orderManager = orderManager;
		this.destination = destination;
		this.implementation = implementation;
		this.source = source;
		this.undoType = undoType;
	}

	@Override
	public void execute() {
		super.execute();

		precedenceEdge = new PrecedenceEdge();
		precedenceEdge.getTimingEdgeProperty().setCost(0);
		implementation.addEdge(source, destination, precedenceEdge);
	}

	@Override
	public void undo() {
		super.undo();

		// Clever Undo. Removes the precedence edge but adds a new edge if
		// necessary between the preceding and following vertices in the 
		// current schedule.
		implementation.removeEdge(precedenceEdge);
		
		if(undoType == compensateSourceRemoval){
			MapperDAGVertex prev = orderManager.getPreviousVertex(destination);
			
			if(prev != null){
				precedenceEdge = new PrecedenceEdge();
				precedenceEdge.getTimingEdgeProperty().setCost(0);
				implementation.addEdge(prev, destination, precedenceEdge);
			}
		}
		else if(undoType == compensateTargetRemoval){
			
		}

	}

}
