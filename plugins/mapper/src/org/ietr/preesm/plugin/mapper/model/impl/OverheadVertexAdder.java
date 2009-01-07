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

package org.ietr.preesm.plugin.mapper.model.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.abc.transaction.AddNewVertexOverheadsTransaction;
import org.ietr.preesm.plugin.abc.transaction.AddOverheadVertexTransaction;
import org.ietr.preesm.plugin.abc.transaction.RemoveVertexTransaction;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;

/**
 * Adds overheads and schedules them on cores
 * 
 * @author mpelcat
 */
public class OverheadVertexAdder {

	private SchedOrderManager orderManager;

	public OverheadVertexAdder(SchedOrderManager orderManager) {
		super();
		this.orderManager = orderManager;
	}

	/**
	 * Adds all necessary overhead vertices
	 */
	public void addOverheadVertices(MapperDAG implementation, TransactionManager transactionManager) {
		
		// We iterate the edges and process the ones with a transfer vertex as
		// destination
		Iterator<DAGEdge> iterator = implementation.edgeSet().iterator();

		while (iterator.hasNext()) {
			MapperDAGEdge currentEdge = (MapperDAGEdge)iterator.next();

			if (!(currentEdge instanceof PrecedenceEdge)
					&& currentEdge.getTarget() instanceof TransferVertex) {

				TransferVertex tvertex = (TransferVertex) currentEdge
						.getTarget();

				transactionManager.add(new AddOverheadVertexTransaction(currentEdge,implementation, tvertex.getRouteStep(), orderManager),null);
				
			}
		}

		transactionManager.execute();
	}

	/**
	 * Adds all necessary overhead vertices
	 */
	public void addOverheadVertices(MapperDAG implementation, TransactionManager transactionManager, MapperDAGVertex refVertex) {

		transactionManager.add(new AddNewVertexOverheadsTransaction(orderManager, implementation, refVertex), refVertex);
		transactionManager.execute();
	}

	/**
	 * Removes all overheads from routes coming from or going to vertex
	 */
	public void removeAllOverheads(Set<DAGVertex> transfers, MapperDAG implementation,
			TransactionManager transactionManager){

		for (DAGVertex v : transfers) {
			if (v instanceof TransferVertex) {
				MapperDAGVertex o = ((TransferVertex)v).getPrecedingOverhead();
				if (o != null && o instanceof OverheadVertex){
					transactionManager.add(new RemoveVertexTransaction(o,implementation,orderManager), null);
				}
			}
		}
	
		transactionManager.execute();
	}

	/**
	 * Gets all overheads from routes coming from or going to vertex.
	 */
	public static Set<OverheadVertex> getAllOverheads(MapperDAGVertex vertex, MapperDAG implementation,
			TransactionManager transactionManager){

		Set<DAGVertex> transfers = TransferVertexAdder.getAllTransfers(vertex, implementation,transactionManager);
		Set<OverheadVertex> overheads = new HashSet<OverheadVertex>();
		
		for (DAGVertex v : transfers) {
			if (v instanceof TransferVertex) {
				MapperDAGVertex o = ((TransferVertex)v).getPrecedingOverhead();
				if (o != null && o instanceof OverheadVertex){
					overheads.add((OverheadVertex)o);
				}
			}
		}
		
		return overheads;
	}
}
