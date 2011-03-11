/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

import java.util.List;
import java.util.logging.Level;

import net.sf.dftools.workflow.tools.AbstractWorkflowLogger;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.plugin.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdge;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdgeAdder;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;

/**
 * A transaction that adds one transfer vertex in an implementation and
 * schedules it given the right edge scheduler
 * 
 * @author mpelcat
 */
public class AddTransferVertexTransaction extends Transaction {
	// Inputs
	/**
	 * The beginning of the transfer name. Typically: 'transfer', 'read' or 'write'
	 */
	private String transferType = null;
	
	/**
	 * If not null, the transfer vertices need to be chained with formerly added
	 * ones
	 */
	private Transaction precedingTransaction = null;
	/**
	 * Scheduling the transfer vertices on the media
	 */
	private IEdgeSched edgeScheduler = null;

	/**
	 * Vertices order manager
	 */
	private SchedOrderManager orderManager;

	/**
	 * Implementation DAG to which the vertex is added
	 */
	private MapperDAG implementation = null;

	/**
	 * Route step corresponding to this transfer
	 */
	private AbstractRouteStep step = null;

	/**
	 * time of this transfer
	 */
	private long transferTime = 0;

	/**
	 * Component corresponding to this transfer vertex
	 */
	private ArchitectureComponent effectiveComponent = null;

	/**
	 * Original edge corresponding to this overhead
	 */
	private MapperDAGEdge edge = null;

	/**
	 * Index of the route step within its route
	 * and of the node within its route step
	 */
	private int routeIndex = 0;
	private int nodeIndex = 0;

	// Generated objects
	/**
	 * overhead vertex added
	 */
	private TransferVertex tVertex = null;

	/**
	 * true if the added vertex needs to be scheduled
	 */
	private boolean scheduleVertex = false;

	/**
	 * edges added
	 */
	private MapperDAGEdge newInEdge = null;
	private MapperDAGEdge newOutEdge = null;

	/**
	 * Vertex preceding the transfer. It can be the transfer source or an overhead
	 * or a preceding transfer
	 */
	private MapperDAGVertex currentSource = null;

	/**
	 * Vertex following the transfer. At the time we add the transfer, can be only the
	 * transfer receiver.
	 */
	private MapperDAGVertex currentTarget = null;

	public AddTransferVertexTransaction(String transferType, Transaction precedingTransaction,
			IEdgeSched edgeScheduler, MapperDAGEdge edge,
			MapperDAG implementation, SchedOrderManager orderManager,
			int routeIndex,int nodeIndex, AbstractRouteStep step, long transferTime,
			ArchitectureComponent effectiveComponent, boolean scheduleVertex) {
		super();
		this.transferType = transferType;
		this.precedingTransaction = precedingTransaction;
		this.edgeScheduler = edgeScheduler;
		this.edge = edge;
		this.implementation = implementation;
		this.step = step;
		this.effectiveComponent = effectiveComponent;
		this.orderManager = orderManager;
		this.scheduleVertex = scheduleVertex;
		this.routeIndex = routeIndex;
		this.nodeIndex = nodeIndex;
		this.transferTime = transferTime;

	}

	@Override
	public void execute(List<Object> resultList) {
		super.execute(resultList);
		
		MapperDAGVertex currentTarget = (MapperDAGVertex) edge.getTarget();

		// Linking with previous transaction consists in chaining the new transfers with 
		// the ones from previous transaction
		if (precedingTransaction != null) {
			if (precedingTransaction instanceof AddTransferVertexTransaction) {
				currentSource = ((AddTransferVertexTransaction) precedingTransaction)
						.getTransfer();
				((MapperDAG)currentSource.getBase()).removeAllEdges(currentSource,
						currentTarget);
			}
		} else {
			currentSource = (MapperDAGVertex) edge.getSource();
		}

		String tvertexID = "__" + transferType + routeIndex + "_" + nodeIndex + " ("
				+ ((MapperDAGVertex) edge.getSource()).getName() + ","
				+ currentTarget.getName() + ")";

		if (edge instanceof PrecedenceEdge) {
			AbstractWorkflowLogger.getLogger().log(Level.INFO,
					"no transfer vertex corresponding to a schedule edge");
			return;
		}

		if (transferTime > 0) {
			tVertex = new TransferVertex(tvertexID, implementation,
					(MapperDAGVertex) edge.getSource(), (MapperDAGVertex) edge
							.getTarget(), routeIndex, nodeIndex);

			tVertex.setRouteStep(step);

			tVertex.getTimingVertexProperty().setCost(transferTime);

			tVertex.getImplementationVertexProperty().setEffectiveComponent(
					effectiveComponent);

			implementation.addVertex(tVertex);

			newInEdge = (MapperDAGEdge) implementation.addEdge(currentSource,
					tVertex);
			newOutEdge = (MapperDAGEdge) implementation.addEdge(tVertex,
					currentTarget);

			newInEdge.setInitialEdgeProperty(edge.getInitialEdgeProperty()
					.clone());
			newOutEdge.setInitialEdgeProperty(edge.getInitialEdgeProperty()
					.clone());

			newInEdge.getTimingEdgeProperty().setCost(0);
			newOutEdge.getTimingEdgeProperty().setCost(0);

			newInEdge.setAggregate(edge.getAggregate());
			newOutEdge.setAggregate(edge.getAggregate());
			
			if (scheduleVertex) {
				// Scheduling transfer vertex
				edgeScheduler.schedule(tVertex, currentSource, currentTarget);
				
				new PrecedenceEdgeAdder(orderManager, implementation)
				.scheduleVertex(tVertex);
			}

			if (resultList != null) {
				resultList.add(tVertex);
			}
		}
	}

	@Override
	public String toString() {
		return ("AddTransfer(" + tVertex.toString() + ")");
	}
	
	public TransferVertex getTransfer(){
		return tVertex;
	}
	
	public MapperDAGVertex getSource(){
		return currentSource;
	}
	
	public MapperDAGVertex getTarget(){
		return currentTarget;
	}
}
