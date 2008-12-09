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

import java.util.Iterator;

import org.ietr.preesm.core.architecture.Route;
import org.ietr.preesm.core.architecture.RouteStep;
import org.ietr.preesm.core.codegen.DataType;
import org.ietr.preesm.core.codegen.sdfProperties.BufferAggregate;
import org.ietr.preesm.core.codegen.sdfProperties.BufferProperties;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.plugin.abc.CommunicationRouter;
import org.ietr.preesm.plugin.abc.order.SchedulingOrderManager;
import org.ietr.preesm.plugin.abc.transaction.AddSendReceiveTransaction;
import org.ietr.preesm.plugin.abc.transaction.AddTransferVertexTransaction;
import org.ietr.preesm.plugin.abc.transaction.RemoveEdgeTransaction;
import org.ietr.preesm.plugin.abc.transaction.Transaction;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.ImplementationVertexProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.sdf4j.model.AbstractEdge;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * The TransferVertexAdder creates the vertices allowing edge scheduling
 * 
 * @author mpelcat   
 */
public class TransferVertexAdder {

	private CommunicationRouter router;

	private SchedulingOrderManager orderManager;

	/**
	 * True if we want to add a send and a receive operation,
	 * false if a simple transfer vertex should be added  
	 */
	private boolean sendReceive;

	/**
	 * True if we the edge that will go through transfers replaces the original edge.
	 * False if both paths are kept
	 */
	private boolean rmvOrigEdge;

	public TransferVertexAdder(CommunicationRouter router,
			SchedulingOrderManager orderManager, boolean sendReceive, boolean rmvOrigEdge) {
		super();
		this.router = router;
		this.orderManager = orderManager;
		this.sendReceive = sendReceive;
		this.rmvOrigEdge = rmvOrigEdge;
	}

	/**
	 * Adds all necessary transfer vertices
	 */
	public void addTransferVertices(MapperDAG implementation, TransactionManager transactionManager, MapperDAGVertex refVertex) {
		
		// We iterate the edges and process the ones with different allocations
		Iterator<DAGEdge> iterator = null;
		
		if(refVertex != null){
			// Adding transfer vertices only for the inputs of a given vertex
			iterator = refVertex.incomingEdges().iterator();
		}
		else{
			// Adding transfer vertices for every eligible edges
			iterator = implementation.edgeSet().iterator();
		}

		while (iterator.hasNext()) {
			MapperDAGEdge currentEdge = (MapperDAGEdge)iterator.next();

			if (!(currentEdge instanceof PrecedenceEdge)) {
				ImplementationVertexProperty currentSourceProp = ((MapperDAGVertex)currentEdge
						.getSource()).getImplementationVertexProperty();
				ImplementationVertexProperty currentDestProp = ((MapperDAGVertex)currentEdge
						.getTarget()).getImplementationVertexProperty();

				if (currentSourceProp.hasEffectiveOperator()
						&& currentDestProp.hasEffectiveOperator()) {
					if (currentSourceProp.getEffectiveOperator() != currentDestProp
							.getEffectiveOperator()) {
						// Adds several transfers for one edge depending on the route steps
						addTransferVertices(currentEdge, implementation,
								 transactionManager,refVertex);
					}
				}
			}
		}

		transactionManager.executeTransactionList();
	}

	/**
	 * Adds one transfer vertex per route step. It does not remove the original
	 * edge
	 */
	public void addTransferVertices(MapperDAGEdge edge, MapperDAG implementation,
			TransactionManager transactionManager, MapperDAGVertex refVertex) {

		MapperDAGVertex currentSource = (MapperDAGVertex)edge.getSource();
		MapperDAGVertex currentDest = (MapperDAGVertex)edge.getTarget();

		Route route = router.getRoute(currentSource
				.getImplementationVertexProperty().getEffectiveOperator(),
				currentDest.getImplementationVertexProperty()
						.getEffectiveOperator());


		Iterator<RouteStep> it = route.iterator();
		int i = 1;

		while (it.hasNext()) {
			RouteStep step = it.next();
			
			
			Transaction transaction = null;
			
			if(sendReceive){
				// TODO: set a size to send and receive. From medium definition?
				transaction = new AddSendReceiveTransaction(edge,implementation,orderManager,i,step,100);
			}
			else{
				
				int transferCost = router.evaluateTransfer(edge, step.getSender(), step
						.getReceiver());
				
				transaction = new AddTransferVertexTransaction(edge,implementation,orderManager,i,step,transferCost);
			}
			
			transactionManager.add(transaction,refVertex);

			if(rmvOrigEdge){
				transactionManager.add(new RemoveEdgeTransaction(edge,implementation),refVertex);
			}
			
			i++;
		}
	}

	/**
	 * Aggregate is imported from the SDF edge. An aggregate in SDF is a set of
	 * sdf edges that were merged into one DAG edge.
	 */
	public void addAggregateFromSDF(MapperDAGEdge edge, IScenario scenario) {

		BufferAggregate agg = new BufferAggregate();

		// Iterating the SDF aggregates
		for (AbstractEdge<SDFGraph, SDFAbstractVertex> aggMember : edge
				.getAggregate()) {
			SDFEdge sdfAggMember = (SDFEdge) aggMember;

			DataType dataType = scenario.getSimulationManager().getDataType(sdfAggMember.getDataType().toString());
			BufferProperties props = new BufferProperties(dataType, sdfAggMember
					.getSourceInterface().getName(), sdfAggMember
					.getTargetInterface().getName(), sdfAggMember.getProd()
					.intValue());

			agg.add(props);
		}
		edge.getPropertyBean().setValue(BufferAggregate.propertyBeanName, agg);
	}
}
