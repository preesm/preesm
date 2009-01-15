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
import java.util.logging.Level;

import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdgeAdder;
import org.sdf4j.model.dag.DAGEdge;

/**
 * A transaction that removes one vertex in an implementation
 * 
 * @author mpelcat
 */
public class RemoveVertexTransaction extends Transaction {
	// Inputs
	/**
	 * Implementation DAG from which the vertex is removed
	 */
	private MapperDAG implementation = null;
	
	/**
	 * vertex removed
	 */
	private MapperDAGVertex vertex = null;
	
	/**
	 * Order manager
	 */
	private SchedOrderManager orderManager = null;
	
	
	public RemoveVertexTransaction(MapperDAGVertex vertex,
			MapperDAG implementation,SchedOrderManager orderManager) {
		super();
		this.vertex = vertex;
		this.implementation = implementation;
		this.orderManager = orderManager;
	}

	@Override
	public void execute() {
		super.execute();

		//Unscheduling first
		MapperDAGVertex prev = orderManager.getPreviousVertex(vertex);
		MapperDAGVertex next = orderManager.getNextVertex(vertex);


		if(prev != null){
			PrecedenceEdgeAdder.removePrecedenceEdge(implementation, prev, vertex);
		}
		
		if(next != null){
			PrecedenceEdgeAdder.removePrecedenceEdge(implementation, vertex, next);
		}

		Set<DAGEdge> edges = implementation.getAllEdges(prev, next);

		if ((prev != null && next != null) && (edges == null || edges.isEmpty())){
			PrecedenceEdgeAdder.addPrecedenceEdge(implementation, prev, next);
		}
		
		implementation.removeVertex(vertex);
		orderManager.remove(vertex, true);
	}

	@Override
	public void undo() {
		PreesmLogger.getLogger().log(Level.SEVERE,"DEBUG: No possible undo");
		super.undo();
	}

	@Override
	public String toString() {
		return("RemoveVertex(" + vertex.toString() +")");
	}

}
