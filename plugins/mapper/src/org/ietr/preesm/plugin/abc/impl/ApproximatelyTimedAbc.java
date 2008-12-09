/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-Fran�ois Nezan, Micka�l Raulet

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

import java.util.logging.Level;

import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.CommunicationRouter;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.edgescheduling.AbstractEdgeSched;
import org.ietr.preesm.plugin.mapper.edgescheduling.EdgeSchedType;
import org.ietr.preesm.plugin.mapper.edgescheduling.IEdgeSched;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdgeAdder;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertexAdder;
import org.sdf4j.model.dag.DAGEdge;

/**
 * An approximately timed architecture simulator associates a complex
 * cost to each inter-core communication. This cost is composed of an
 * overhead on the sender, a transfer time on the medium and a reception
 * time on the receiver. Scheduling transfer vertices are added and
 * mapped to the media architecture components
 *         
 * @author mpelcat   
 */
public class ApproximatelyTimedAbc extends
		AbstractAbc {

	/**
	 * simulator of the transfers
	 */
	protected CommunicationRouter router;

	/**
	 * Transfer vertex adder for edge scheduling
	 */
	protected TransferVertexAdder tvertexAdder;

	/**
	 * Current precedence edge adder: called exclusively by simulator to schedule
	 * vertices on the different operators
	 */
	protected PrecedenceEdgeAdder precedenceEdgeAdder;

	/**
	 * Scheduling the transfer vertices on the media
	 */
	protected IEdgeSched edgeScheduler;

	/**
	 * Constructor of the simulator from a "blank" implementation where every
	 * vertex has not been implanted yet.
	 */
	public ApproximatelyTimedAbc(EdgeSchedType edgeSchedType, MapperDAG dag,
			MultiCoreArchitecture archi) {
		super(dag, archi);

		// The media simulator calculates the edges costs
		edgeScheduler = AbstractEdgeSched.getInstance(edgeSchedType,orderManager);
		router = new CommunicationRouter(archi);
		tvertexAdder = new TransferVertexAdder(edgeScheduler, router, orderManager, false, false);
		precedenceEdgeAdder = new PrecedenceEdgeAdder(orderManager);
	}

	/**
	 * Called when a new vertex operator is set
	 */
	@Override
	protected void fireNewMappedVertex(MapperDAGVertex vertex) {
/*
		PreesmLogger.getLogger().log(Level.INFO,
				"mapping " + vertex.getName());
		*/
		Operator effectiveOp = vertex.getImplementationVertexProperty()
				.getEffectiveOperator();

		if (effectiveOp == Operator.NO_COMPONENT) {
			PreesmLogger.getLogger().severe(
					"implementation of " + vertex.getName() + " failed");
		} else {
			int vertextime = vertex.getInitialVertexProperty().getTime(
					effectiveOp);

			// Set costs
			vertex.getTimingVertexProperty().setCost(vertextime);

			setEdgesCosts(vertex.incomingEdges());
			setEdgesCosts(vertex.outgoingEdges());

			transactionManager.undoTransactions(vertex);

			precedenceEdgeAdder.scheduleNewVertex(implementation,transactionManager,vertex,vertex);
			tvertexAdder.addTransferVertices(implementation,transactionManager, vertex);
			scheduleT(implementation,transactionManager,vertex);

			/*
			// Exhaustive recalculation
			transactionManager.undoTransactionList();
			
			tvertexAdder.addTransferVertices(implementation,transactionManager,null);
			precedenceEdgeAdder.addPrecedenceEdges(implementation,transactionManager);*/

		}
	}

	/**
	 * Adds all necessary schedule edges for the transfers of a given
	 * vertex
	 */
	public void scheduleT(MapperDAG implementation,
			TransactionManager transactionManager, MapperDAGVertex refVertex) {

		for (DAGEdge edge : refVertex.incomingEdges()) {
			MapperDAGVertex vertex = (MapperDAGVertex) edge.getSource();
			if (vertex instanceof TransferVertex) {
				precedenceEdgeAdder.scheduleNewVertex(implementation,transactionManager,vertex,refVertex);

			}
		}

		for (DAGEdge edge : refVertex.outgoingEdges()) {
			MapperDAGVertex vertex = (MapperDAGVertex) edge.getTarget();
			if (vertex instanceof TransferVertex) {
				precedenceEdgeAdder.scheduleNewVertex(implementation,transactionManager,vertex,refVertex);
			}
		}
	}

	@Override
	protected void fireNewUnmappedVertex(MapperDAGVertex vertex) {

		Operator effectiveOp = vertex.getImplementationVertexProperty()
				.getEffectiveOperator();

		// unimplanting a vertex resets the cost of the current vertex
		// and its edges
		// It also removes incoming and outgoing schedule edges
		if (effectiveOp == Operator.NO_COMPONENT) {
			vertex.getTimingVertexProperty().resetCost();

			resetCost(vertex.incomingEdges());
			resetCost(vertex.outgoingEdges());

		} else {
			PreesmLogger.getLogger().severe(
					"unimplementation of " + vertex.getName() + " failed");
		}
	}

	/**
	 * Asks the time keeper to update timings. Crucial and costly operation.
	 * Depending on the king of timings we want, calls the necessary updates.
	 */
	@Override
	protected final void updateTimings() {

		timeKeeper.updateTLevels();
	}

	/**
	 * Edge scheduling vertices are added. Thus useless edge costs are removed
	 */
	protected final void setEdgeCost(MapperDAGEdge edge) {

		edge.getTimingEdgeProperty().setCost(0);

	}

	public AbcType getType(){
		return AbcType.ApproximatelyTimed;
	}
}
