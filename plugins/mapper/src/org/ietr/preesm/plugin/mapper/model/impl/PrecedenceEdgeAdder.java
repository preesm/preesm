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
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.order.IScheduleElement;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.abc.transaction.AddPrecedenceEdgeTransaction;
import org.ietr.preesm.plugin.abc.transaction.RemoveEdgeTransaction;
import org.ietr.preesm.plugin.abc.transaction.Transaction;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.sdf4j.model.dag.DAGEdge;

/**
 * The edge adder automatically generates edges between vertices successive on a
 * single operator. It can also remove all the edges of type PrecedenceEdgeAdder
 * from the graph
 * 
 * @author mpelcat
 */
public class PrecedenceEdgeAdder {

	/**
	 * Removes all schedule edges
	 */
	public static void removePrecedenceEdges(MapperDAG implementation,
			TransactionManager transactionManager) {

		for (DAGEdge e : implementation.edgeSet()) {
			if (e instanceof PrecedenceEdge) {
				transactionManager.add(new RemoveEdgeTransaction(
						(MapperDAGEdge) e, implementation));
			}
		}
		transactionManager.execute();
	}

	/**
	 * Adds all necessary precedence edges to an implementation respecting the
	 * order given by the scheduling order manager.
	 */
	public static void addPrecedenceEdges(SchedOrderManager orderManager,
			MapperDAG implementation) {

		TransactionManager localTransactionManager = new TransactionManager();
		Iterator<ArchitectureComponent> opIt = orderManager
				.getArchitectureComponents().iterator();

		// Iterates the schedules
		while (opIt.hasNext()) {
			List<MapperDAGVertex> schedule = orderManager
					.getVertexList(opIt.next());

			Iterator<MapperDAGVertex> schedit = schedule.iterator();

			MapperDAGVertex src;

			// Iterates all vertices in each schedule
			if (schedit.hasNext()) {
				MapperDAGVertex dst = schedit.next();

				while (schedit.hasNext()) {

					src = dst;
					dst = (/*toReview*/MapperDAGVertex)schedit.next();

					if (implementation.getAllEdges(src, dst).isEmpty()) {
						// Adds a transaction
						Transaction transaction = new AddPrecedenceEdgeTransaction(
								implementation, src, dst);
						localTransactionManager.add(transaction);
					}
				}
			}
		}

		// Executes the transactions
		localTransactionManager.execute();
	}

	public static PrecedenceEdge addPrecedenceEdge(MapperDAG implementation,
			MapperDAGVertex v1, MapperDAGVertex v2) {
		PrecedenceEdge precedenceEdge = new PrecedenceEdge();
		precedenceEdge.getTimingEdgeProperty().setCost(0);
		implementation.addEdge(v1, v2, precedenceEdge);
		return precedenceEdge;
	}

	public static void removePrecedenceEdge(MapperDAG implementation,
			MapperDAGVertex v1, MapperDAGVertex v2) {
		Set<DAGEdge> edges = implementation.getAllEdges(v1, v2);

		if (edges != null) {
			if (edges.size() >= 2)
				PreesmLogger.getLogger().log(
						Level.SEVERE,
						"too many edges between " + v1.toString() + " and "
								+ v2.toString());

			for (DAGEdge edge : edges) {
				if (edge instanceof PrecedenceEdge) {
					implementation.removeEdge(edge);
				}
			}
		}
	}

	/**
	 * For Debug purposes, checks that all necessary precedence edges are
	 * present
	 */
	public static void checkPrecedences(SchedOrderManager orderManager,
			MapperDAG implementation, MultiCoreArchitecture archi,
			MapperDAGVertex modifiedVertex) {

		Set<ArchitectureComponent> cmpSet = new HashSet<ArchitectureComponent>();
		cmpSet.addAll(archi.getComponents(ArchitectureComponentType.medium));
		cmpSet.addAll(archi.getComponents(ArchitectureComponentType.operator));

		for (ArchitectureComponent o : cmpSet) {
			List<MapperDAGVertex> schedule = orderManager.getVertexList(o);
			if (schedule != null) {
				MapperDAGVertex pv = null;
				for (IScheduleElement v : schedule) {
					if (pv != null) {
						if (implementation.getAllEdges(pv, (/*toReview*/MapperDAGVertex)v) == null
								|| implementation.getAllEdges(pv, (/*toReview*/MapperDAGVertex)v).isEmpty()) {

							if (!pv.equals(modifiedVertex)
									&& !v.equals(modifiedVertex))
								PreesmLogger.getLogger().log(
										Level.SEVERE,
										"Lacking precedence edge between "
												+ pv.toString() + " and "
												+ v.toString());
						}
					}
					pv = (/*toReview*/MapperDAGVertex)v;
				}
			}
		}
	}

	/**
	 * Schedules a given vertex
	 */
	public static void scheduleVertex(SchedOrderManager orderManager,
			MapperDAG implementation, MapperDAGVertex newVertex) {

		MapperDAGVertex prev = (/*toReview*/MapperDAGVertex)orderManager.getPrevious(newVertex);
		MapperDAGVertex next = (/*toReview*/MapperDAGVertex)orderManager.getNext(newVertex);

		Set<DAGEdge> prevEdges = implementation.getAllEdges(prev, newVertex);
		Set<DAGEdge> nextEdges = implementation.getAllEdges(newVertex, next);

		boolean prevAndNewLinked = (prevEdges != null && !prevEdges.isEmpty());
		boolean newAndNextLinked = (nextEdges != null && !nextEdges.isEmpty());

		if ((prev != null && newVertex != null) && !prevAndNewLinked) {
			addPrecedenceEdge(implementation, prev, newVertex);
			prevAndNewLinked = true;
		}

		if ((newVertex != null && next != null) && !newAndNextLinked) {
			addPrecedenceEdge(implementation, newVertex, next);
			newAndNextLinked = true;
		}

		if (prevAndNewLinked && newAndNextLinked) {
			removePrecedenceEdge(implementation, prev, next);
		}
	}
}
