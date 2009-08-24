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

import java.util.List;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.abc.taskscheduling.TaskSwitcher;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.InvolvementVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdge;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdgeAdder;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;

/**
 * Transaction executing the addition of an involvement vertex.
 * 
 * @author mpelcat
 */
public class AddInvolvementVertexTransaction extends Transaction {

	// Inputs
	/**
	 * Determining if the current involvement is executed by the sender or by
	 * the receiver
	 */
	private boolean isSender;

	/**
	 * Implementation DAG to which the vertex is added
	 */
	private MapperDAG implementation = null;

	/**
	 * Route step corresponding to this involvement
	 */
	private AbstractRouteStep step = null;

	/**
	 * time of this involvement
	 */
	long involvementTime = 0;

	/**
	 * Original edge and transfer corresponding to this involvement
	 */
	private MapperDAGEdge edge = null;

	/**
	 * manager keeping scheduling orders
	 */
	private SchedOrderManager orderManager = null;

	// Generated objects
	/**
	 * involvement vertex added
	 */
	private InvolvementVertex iVertex = null;

	// private MapperDAGEdge newOutEdge = null;

	public AddInvolvementVertexTransaction(boolean isSender,
			MapperDAGEdge edge, MapperDAG implementation,
			AbstractRouteStep step, long involvementTime,
			SchedOrderManager orderManager) {
		super();
		this.isSender = isSender;
		this.edge = edge;
		this.implementation = implementation;
		this.step = step;
		this.orderManager = orderManager;
		this.involvementTime = involvementTime;
	}

	@Override
	public void execute(List<Object> resultList) {

		super.execute(resultList);

		MapperDAGVertex currentSource = (MapperDAGVertex) edge.getSource();
		MapperDAGVertex currentTarget = (MapperDAGVertex) edge.getTarget();

		if (edge instanceof PrecedenceEdge) {
			PreesmLogger.getLogger().log(Level.INFO,
					"no involvement vertex corresponding to a schedule edge");
			return;
		}

		String ivertexID = "__involvement (" + currentSource.getName() + ","
				+ currentTarget.getName() + ")";

		if (involvementTime > 0) {
			iVertex = new InvolvementVertex(ivertexID, implementation);

			iVertex.getTimingVertexProperty().setCost(involvementTime);

			if (isSender) {
				iVertex.getImplementationVertexProperty().setEffectiveOperator(
						step.getSender());
				((TransferVertex) currentTarget).setInvolvementVertex(iVertex);
			} else {
				iVertex.getImplementationVertexProperty().setEffectiveOperator(
						step.getReceiver());
				((TransferVertex) currentSource).setInvolvementVertex(iVertex);
			}

			implementation.addVertex(iVertex);

			if (isSender) {
				MapperDAGEdge newInEdge = (MapperDAGEdge) implementation.addEdge(
						currentSource, iVertex);
				newInEdge.setInitialEdgeProperty(edge.getInitialEdgeProperty()
						.clone());
				newInEdge.getTimingEdgeProperty().setCost(0);

				if (true) {
					TaskSwitcher taskSwitcher = new TaskSwitcher(orderManager);
					taskSwitcher.insertVertexBefore(currentTarget, iVertex);
				} else {
					orderManager.insertVertexBefore(currentTarget, iVertex);
				}
			} else {
				MapperDAGEdge newOutEdge = (MapperDAGEdge) implementation.addEdge(iVertex,
						currentTarget);
				newOutEdge.setInitialEdgeProperty(edge.getInitialEdgeProperty()
						.clone());
				newOutEdge.getTimingEdgeProperty().setCost(0);

				orderManager.insertVertexAfter(currentSource, iVertex);
			}

			// Scheduling involvement vertex
			PrecedenceEdgeAdder.scheduleVertex(orderManager, implementation,
					iVertex);

			if (resultList != null) {
				resultList.add(iVertex);
			}
		}
	}

	@Override
	public String toString() {
		return ("AddInvolvement(" + iVertex.toString() + ")");
	}

}
