/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy
 * 
 * [mpelcat,jnezan,kdesnos,jheulot,cguy]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.mapper.abc.route.impl;

import java.util.ArrayList;
import java.util.List;

import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.architecture.route.MemRouteStep;
import org.ietr.preesm.mapper.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.mapper.abc.edgescheduling.SimpleEdgeSched;
import org.ietr.preesm.mapper.abc.route.AbstractCommunicationRouter;
import org.ietr.preesm.mapper.abc.route.CommunicationRouter;
import org.ietr.preesm.mapper.abc.route.CommunicationRouterImplementer;
import org.ietr.preesm.mapper.abc.transaction.AddInvolvementVertexTransaction;
import org.ietr.preesm.mapper.abc.transaction.AddSendReceiveTransaction;
import org.ietr.preesm.mapper.abc.transaction.AddTransferVertexTransaction;
import org.ietr.preesm.mapper.abc.transaction.Transaction;
import org.ietr.preesm.mapper.abc.transaction.TransactionManager;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.special.TransferVertex;

/**
 * Class responsible to generate the suited vertices while simulating a shared
 * ram communication
 * 
 * @author mpelcat
 */
public class SharedRamRouterImplementer extends CommunicationRouterImplementer {

	public SharedRamRouterImplementer(AbstractCommunicationRouter user) {
		super(user);
	}

	@Override
	public void removeVertices(MapperDAGEdge edge,
			TransactionManager transactions) {

	}

	/**
	 * Careful!!! Only simple edge scheduler allowed for synchronized edges
	 */
	@Override
	public IEdgeSched getEdgeScheduler() {
		return new SimpleEdgeSched(getOrderManager());
	}

	/**
	 * Adds the simulation vertices
	 */
	@Override
	public Transaction addVertices(AbstractRouteStep routeStep,
			MapperDAGEdge edge, TransactionManager transactions, int type,
			int routeStepIndex, Transaction lastTransaction,
			List<Object> alreadyCreatedVertices) {

		if (routeStep instanceof MemRouteStep) {
			// Adding the transfers
			MemRouteStep ramStep = ((MemRouteStep) routeStep);
			// All the transfers along the path have the same time: the time
			// to transfer the data on the slowest contention node
			long senderTransferTime = ramStep
					.getSenderSideWorstTransferTime(edge.getInit()
							.getDataSize());
			long receiverTransferTime = ramStep
					.getReceiverSideWorstTransferTime(edge.getInit()
							.getDataSize());

			// Adding the transfers of a ram route step
			if (type == CommunicationRouter.transferType) {
				List<ComponentInstance> nodes = ramStep
						.getSenderSideContentionNodes();
				AddTransferVertexTransaction transaction = null;

				for (ComponentInstance node : nodes) {
					int nodeIndex = nodes.indexOf(node);
					transaction = new AddTransferVertexTransaction("write",
							lastTransaction, getEdgeScheduler(), edge,
							getImplementation(), getOrderManager(),
							routeStepIndex, nodeIndex, routeStep,
							senderTransferTime, node, true);
					transactions.add(transaction);
				}

				lastTransaction = transaction;

				nodes = ramStep.getReceiverSideContentionNodes();
				for (ComponentInstance node : nodes) {
					int nodeIndex = nodes.indexOf(node);
					transaction = new AddTransferVertexTransaction("read",
							lastTransaction, getEdgeScheduler(), edge,
							getImplementation(), getOrderManager(),
							routeStepIndex, nodeIndex, routeStep,
							receiverTransferTime, node, true);
					transactions.add(transaction);
				}

				return transaction;
			} else if (type == CommunicationRouter.involvementType) {
				// Adding the involvements
				MapperDAGEdge incomingEdge = null;
				MapperDAGEdge outgoingEdge = null;
				int currentNodeIndex = -1;

				for (Object o : alreadyCreatedVertices) {
					if (o instanceof TransferVertex) {
						TransferVertex v = (TransferVertex) o;
						if (v.getSource().equals(edge.getSource())
								&& v.getTarget().equals(edge.getTarget())
								&& v.getRouteStep() == routeStep
								&& v.getNodeIndex() == 0) {
							// Finding the edge where to add an involvement
							incomingEdge = (MapperDAGEdge) v.incomingEdges()
									.toArray()[0];
						} else if (v.getTarget().equals(edge.getTarget())
								&& v.getSource().equals(edge.getSource())
								&& v.getRouteStep() == routeStep
								&& v.getNodeIndex() > currentNodeIndex) {
							// Finding the edge where to add an involvement
							outgoingEdge = (MapperDAGEdge) v.outgoingEdges()
									.toArray()[0];
							currentNodeIndex = v.getNodeIndex();
						}

					}
				}

				if (incomingEdge != null) {
					transactions.add(new AddInvolvementVertexTransaction(true,
							incomingEdge, getImplementation(), routeStep,
							senderTransferTime, getOrderManager()));
				}

				if (outgoingEdge != null) {
					transactions.add(new AddInvolvementVertexTransaction(false,
							outgoingEdge, getImplementation(), routeStep,
							receiverTransferTime, getOrderManager()));
				}

			} else if (type == CommunicationRouter.synchroType) {

				// Synchronizing the previously created transfers
				List<MapperDAGVertex> toSynchronize = new ArrayList<MapperDAGVertex>();

				for (Object o : alreadyCreatedVertices) {
					if (o instanceof TransferVertex) {
						TransferVertex v = (TransferVertex) o;
						if (v.getSource().equals(edge.getSource())
								&& v.getTarget().equals(edge.getTarget())
								&& v.getRouteStep() == routeStep) {
							toSynchronize.add(v);

							if (v.getInvolvementVertex() != null)
								toSynchronize.add(v.getInvolvementVertex());
						}

					}
				}

				// Synchronizing the vertices in order manager (they
				// have consecutive total order and be scheduled
				// simultaneously).
				/*
				 * if (toSynchronize.size() > 1) { ImplementationCleaner cleaner
				 * = new ImplementationCleaner( getOrderManager(),
				 * getImplementation()); PrecedenceEdgeAdder adder = new
				 * PrecedenceEdgeAdder( getOrderManager(), getImplementation());
				 * MapperDAGVertex last = null; last = null;
				 * 
				 * for (MapperDAGVertex v : toSynchronize) {
				 * cleaner.unscheduleVertex(v); last =
				 * getOrderManager().synchronize(last, v);
				 * adder.scheduleVertex(v); }
				 * 
				 * }
				 */
			} else if (type == CommunicationRouter.sendReceiveType) {

				Transaction transaction = new AddSendReceiveTransaction(
						lastTransaction, edge, getImplementation(),
						getOrderManager(), routeStepIndex, routeStep,
						TransferVertex.SEND_RECEIVE_COST);

				transactions.add(transaction);
				return transaction;
			}
		}

		return null;
	}

}
