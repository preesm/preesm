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

import org.ietr.preesm.plugin.abc.order.SchedulingOrderManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdge;
import org.sdf4j.model.dag.DAGEdge;

/**
 * Transaction generating the appropriate Precedence Edges to add a new vertex
 * in the middle of a schedule.
 * 
 * @author mpelcat
 */
public class SchedNewVertexTransaction extends Transaction {

	// Inputs
	/**
	 * The object handling the schedulings as well as the total order.
	 */
	private SchedulingOrderManager orderManager;

	/**
	 * Vertex to add in the schedule
	 */
	private MapperDAGVertex newVertex = null;

	/**
	 * Implementation DAG to which the edge is added
	 */
	private MapperDAG implementation = null;

	/**
	 * Added edges
	 */
	private PrecedenceEdge precEdge1 = null;
	private PrecedenceEdge precEdge2 = null;

	public SchedNewVertexTransaction(SchedulingOrderManager orderManager, MapperDAG implementation, MapperDAGVertex newVertex) {
		super();
		this.orderManager = orderManager;
		this.newVertex = newVertex;
		this.implementation = implementation;
	}

	private PrecedenceEdge addPrecedenceEdge(MapperDAGVertex v1, MapperDAGVertex v2){
		PrecedenceEdge precedenceEdge = new PrecedenceEdge();
		precedenceEdge.getTimingEdgeProperty().setCost(0);
		implementation.addEdge(v1, v2, precedenceEdge);
		return precedenceEdge;
	}
	
	@Override
	public void execute() {
		super.execute();

		MapperDAGVertex prev = orderManager.getPreviousVertex(newVertex);
		MapperDAGVertex next = orderManager.getNextVertex(newVertex);
		Set<DAGEdge> prevEdges = implementation.getAllEdges(prev, newVertex);
		Set<DAGEdge> nextEdges = implementation.getAllEdges(newVertex, next);
		
		if(prevEdges != null && prevEdges.isEmpty())
			precEdge1 = addPrecedenceEdge(prev, newVertex);
		
		if(nextEdges != null && nextEdges.isEmpty())
			precEdge2 = addPrecedenceEdge(newVertex, next);
	}

	@Override
	public void undo() {
		super.undo();

		MapperDAGVertex prev = orderManager.getPreviousVertex(newVertex);
		MapperDAGVertex next = orderManager.getNextVertex(newVertex);
		
		implementation.removeEdge(precEdge1);
		implementation.removeEdge(precEdge2);
		
		Set<DAGEdge> edges = implementation.getAllEdges(prev, next);
		
		if(edges != null && edges.isEmpty())
			addPrecedenceEdge(prev, next);
		
	}

}
