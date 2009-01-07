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

import java.util.Set;

import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.OverheadVertexAdder;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdge;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertexAdder;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;

/**
 * Transaction generating the appropriate overheads created by the implantation
 * of a new vertex. Undoing this transaction deletes all overheads coming from
 * or going to this vertex, even if they were not created by the transaction itself.
 * This is necessary to handle the complicated cases of undo/redo in random order.
 * 
 * @author mpelcat
 */
public class AddNewVertexOverheadsTransaction extends Transaction {

	// Inputs
	/**
	 * The object handling the schedulings as well as the total order.
	 */
	private SchedOrderManager orderManager;

	/**
	 * Vertex to add in the schedule
	 */
	private MapperDAGVertex newVertex = null;

	/**
	 * Implementation DAG to which the edge is added
	 */
	private MapperDAG implementation = null;
	
	private TransactionManager localTransactionManager = null;
	private OverheadVertexAdder overheadVertexAdder = null;

	public AddNewVertexOverheadsTransaction(SchedOrderManager orderManager,
			MapperDAG implementation, MapperDAGVertex newVertex) {
		super();
		this.orderManager = orderManager;
		this.newVertex = newVertex;
		this.implementation = implementation;

		localTransactionManager = new TransactionManager();
		overheadVertexAdder = new OverheadVertexAdder(orderManager);
	}

	@Override
	public void execute() {
		super.execute();

		 Set<DAGVertex> transfers = TransferVertexAdder.getAllTransfers(newVertex, implementation, localTransactionManager);
		
		overheadVertexAdder.removeAllOverheads(transfers, implementation, localTransactionManager);

		for (DAGVertex tvertex : transfers) {
			for(DAGEdge incomingEdge : implementation.incomingEdgesOf(tvertex)){
				if(!(incomingEdge instanceof PrecedenceEdge)){
					localTransactionManager.add(new AddOverheadVertexTransaction((MapperDAGEdge)incomingEdge,implementation, ((TransferVertex)tvertex).getRouteStep(), orderManager),null);
				}
			}
		}

		
		localTransactionManager.execute();
		localTransactionManager.clear();

	}

	@Override
	public void undo() {
		super.undo();
		
		Set<DAGVertex> transfers = TransferVertexAdder.getAllTransfers(newVertex, implementation, localTransactionManager);
		
		overheadVertexAdder.removeAllOverheads(transfers, implementation, localTransactionManager);
	}

}
