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

import java.util.logging.Level;

import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.architecture.RouteStep;
import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.architecture.simplemodel.MediumDefinition;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.plugin.abc.order.SchedulingOrderManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.implementation.PrecedenceEdge;
import org.ietr.preesm.plugin.mapper.model.implementation.ReceiveVertex;
import org.ietr.preesm.plugin.mapper.model.implementation.SendVertex;
import org.ietr.preesm.plugin.mapper.model.implementation.TransferVertex;

/**
 * A transaction that adds a send and a receive vertex in an implentation.
 * 
 * @author mpelcat
 */
public class AddSendReceiveTransaction extends Transaction {
	// Inputs
	/**
	 * Implementation DAG to which the vertex is added
	 */
	private MapperDAG implementation = null;

	/**
	 * Route step corresponding to this overhead
	 */
	private RouteStep step = null;

	/**
	 * Original edge corresponding to this overhead
	 */
	private MapperDAGEdge edge = null;

	/**
	 * manager keeping scheduling orders
	 */
	private SchedulingOrderManager orderManager = null;

	/**
	 * Cost of the transfer to give to the transfer vertex
	 */
	private int transferCost = 0;

	/**
	 * Index of the route step within its route
	 */
	private int routeIndex = 0;
	
	// Generated objects
	/**
	 * overhead vertex added
	 */
	private TransferVertex sendVertex = null;
	private TransferVertex receiveVertex = null;
	
	/**
	 * edges added
	 */
	private MapperDAGEdge newEdge1 = null;
	private MapperDAGEdge newEdge2 = null;
	private MapperDAGEdge newEdge3 = null;
	
	
	public AddSendReceiveTransaction(MapperDAGEdge edge,
			MapperDAG implementation, SchedulingOrderManager orderManager,
			int routeIndex, RouteStep step, int transferCost) {
		super();
		this.edge = edge;
		this.implementation = implementation;
		this.orderManager = orderManager;
		this.routeIndex = routeIndex;
		this.step = step;
		this.transferCost = transferCost;
	}

	@Override
	public void execute() {
		super.execute();

		MapperDAGVertex currentSource = (MapperDAGVertex)edge.getSource();
		MapperDAGVertex currentTarget = (MapperDAGVertex)edge.getTarget();

		// Careful!!! Those names are used in code generation
		String sendVertexID = "snd" + currentSource.getName()
				+ currentTarget.getName() + routeIndex;

		String receiveVertexID = "rcv" + currentSource.getName()
		+ currentTarget.getName() + routeIndex;
		
		Medium currentMedium = step.getMedium();

		if (edge instanceof PrecedenceEdge) {
			PreesmLogger.getLogger().log(Level.INFO,
					"no transfer vertex corresponding to a schedule edge");
			return;
		}
		
		if (currentMedium != null) {

			MediumDefinition def = (MediumDefinition) currentMedium
					.getDefinition();

			if (def.getInvSpeed() != 0) {
				
				Operator senderOperator = step.getSender();
				Operator receiverOperator = step.getReceiver();
				
				sendVertex = new SendVertex(sendVertexID, implementation);
				sendVertex.setRouteStep(step);
				sendVertex.getTimingVertexProperty().setCost(transferCost);
				sendVertex.getImplementationVertexProperty().setEffectiveOperator(senderOperator);
				orderManager.insertVertexAfter(currentSource, sendVertex);
				implementation.addVertex(sendVertex);
				
				receiveVertex = new ReceiveVertex(receiveVertexID, implementation);
				receiveVertex.setRouteStep(step);
				receiveVertex.getTimingVertexProperty().setCost(transferCost);
				receiveVertex.getImplementationVertexProperty().setEffectiveOperator(receiverOperator);
				orderManager.insertVertexAfter(sendVertex, receiveVertex);
				implementation.addVertex(receiveVertex);

				newEdge1 = (MapperDAGEdge)implementation.addEdge(currentSource, sendVertex);
				newEdge2 = (MapperDAGEdge)implementation.addEdge(sendVertex, receiveVertex);
				newEdge3 = (MapperDAGEdge)implementation.addEdge(receiveVertex, currentTarget);

				newEdge1.setInitialEdgeProperty(edge.getInitialEdgeProperty().clone());
				newEdge2.setInitialEdgeProperty(edge.getInitialEdgeProperty().clone());
				newEdge3.setInitialEdgeProperty(edge.getInitialEdgeProperty().clone());
				
				newEdge1.getTimingEdgeProperty().setCost(0);
				newEdge2.getTimingEdgeProperty().setCost(0);
				newEdge3.getTimingEdgeProperty().setCost(0);

			}
		}
	}

	@Override
	public void undo() {
		super.undo();

		implementation.removeEdge(newEdge1);
		implementation.removeEdge(newEdge2);
		implementation.removeEdge(newEdge3);
		implementation.removeVertex(sendVertex);
		orderManager.remove(sendVertex, true);
		implementation.removeVertex(receiveVertex);
		orderManager.remove(receiveVertex, true);
	}


}
