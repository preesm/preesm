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

package org.ietr.preesm.plugin.abc.impl;

import java.util.Iterator;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.architecture.route.MediumRouteStep;
import org.ietr.preesm.core.architecture.route.Route;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.abc.route.RouteCalculator;
import org.ietr.preesm.plugin.abc.transaction.AddSendReceiveTransaction;
import org.ietr.preesm.plugin.abc.transaction.RemoveEdgeTransaction;
import org.ietr.preesm.plugin.abc.transaction.Transaction;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.ImplementationVertexProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdge;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;
import org.sdf4j.model.dag.DAGEdge;

/**
 * The TransferVertexAdder creates the vertices allowing edge scheduling
 * 
 * @author mpelcat
 */
public class ImplementationFiller {

	private RouteCalculator router;

	private SchedOrderManager orderManager;
	/**
	 * Scheduling the transfer vertices on the media
	 */
	protected IEdgeSched edgeScheduler;

	public ImplementationFiller(IEdgeSched edgeScheduler,
			RouteCalculator router, SchedOrderManager orderManager) {
		super();
		this.edgeScheduler = edgeScheduler;
		this.router = router;
		this.orderManager = orderManager;
	}

	/**
	 * Adds one transfer vertex per route step. It does not remove the original
	 * edge
	 */
	public void addSendReceiveVertices(MapperDAGEdge edge,
			MapperDAG implementation, TransactionManager transactionManager) {

		MapperDAGVertex currentSource = (MapperDAGVertex) edge.getSource();
		MapperDAGVertex currentDest = (MapperDAGVertex) edge.getTarget();

		Operator sourceOp = currentSource.getImplementationVertexProperty()
				.getEffectiveOperator();
		Operator destOp = currentDest.getImplementationVertexProperty()
				.getEffectiveOperator();

		Route route = router.getRoute(sourceOp, destOp);

		if (route == null) {
			PreesmLogger.getLogger().log(
					Level.SEVERE,
					"No route was found between the cores: " + sourceOp
							+ " and " + destOp);
			return;
		}
		Iterator<AbstractRouteStep> it = route.iterator();
		int i = 1;
		// Transactions need to be linked so that the communication vertices
		// created are also linked
		Transaction precedingTransaction = null;

		while (it.hasNext()) {
			MediumRouteStep step = (MediumRouteStep)it.next();

			Transaction transaction = null;

			// TODO: set a size to send and receive. From medium definition?
			transaction = new AddSendReceiveTransaction(
					precedingTransaction, edge, implementation,
					orderManager, i, step,
					TransferVertex.SEND_RECEIVE_COST);

			transactionManager.add(transaction);
			precedingTransaction = transaction;

			i++;
		}
	}
}
