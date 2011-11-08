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

package org.ietr.preesm.plugin.mapper.algo.list;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.dftools.architecture.slam.ComponentInstance;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * List scheduler from Yu Kwong Kwok PhD thesis
 * 
 * @author pmenuet
 * @author mpelcat
 */
public class KwokListScheduler {

	/**
	 * constructor
	 */
	public KwokListScheduler() {
		super();
	}

	/**
	 * operatorvertexstarttime: Return the date when the operator is ready to
	 * process the vertex
	 * 
	 * @param minimizeVStartorOpEnd
	 *            If true, we minimize the starting date of the vertex; if
	 *            false, we minimize the current scheduling length on the
	 *            considered operator
	 */
	public long listImplementationCost(MapperDAG dag, MapperDAGVertex vertex,
			ComponentInstance operator, IAbc simu, boolean minimizeVStartorOpEnd) {

		// check the vertex is into the DAG
		vertex = dag.getMapperDAGVertex(vertex.getName());

		// maps the vertex on the operator
		simu.map(vertex, operator, true);
		simu.updateFinalCosts();

		// check if the vertex is a source vertex with no predecessors
		if (minimizeVStartorOpEnd) {
			return simu.getFinalCost(vertex);
		} else {
			return simu.getFinalCost(operator);
		}
	}

	/**
	 * schedule: Do a mapping with the help of the lists (CPN-Dominant list,
	 * Blocking node list and the FCP list) and the architecture. It can take
	 * one vertex already mapped with a particular operator chosen by the user
	 * and only one.
	 * 
	 * @param : MapperDAG, List<MapperDAGVertex>, List<MapperDAGVertex>,
	 *        List<MapperDAGVertex>,IArchitectureSimulator,
	 *        Operator,MapperDAGVertex
	 * 
	 * @return : Implemented MapperDAG
	 */

	public MapperDAG schedule(MapperDAG dag, List<MapperDAGVertex> orderlist,
			IAbc archisimu, ComponentInstance operatorfcp,
			MapperDAGVertex fcpvertex) {

		boolean minimizeVStartorOpEnd = false;

		// Variables
		ComponentInstance chosenoperator = null;
		Logger logger = WorkflowLogger.getLogger();

		// Maps the fastest one to be ready among the operators in the vertex
		// check the vertex by priority in the CPN-Dominant list
		logger.log(Level.FINEST, " entering schedule ");
		for (MapperDAGVertex currentvertex : orderlist) {

			// Mapping forced by the user or the Fast algorithm
			if (currentvertex.equals(fcpvertex)) {
				archisimu.map(currentvertex, operatorfcp, true);
			} else {

				long time = Long.MAX_VALUE;
				// Choose the operator

				List<ComponentInstance> opList = archisimu
						.getCandidateOperators(currentvertex);
				if (opList.size() == 1) {
					chosenoperator = (ComponentInstance) opList.toArray()[0];
				} else {
					for (ComponentInstance currentoperator : opList) {

						long test = listImplementationCost(dag, currentvertex,
								currentoperator, archisimu,
								minimizeVStartorOpEnd);
						// test the earliest ready operator
						if (test < time) {
							chosenoperator = currentoperator;
							time = test;
						}

					}
				}

				// Map on the chosen operator
				archisimu.map(currentvertex, chosenoperator, true);

				int currentVertexRank = orderlist.indexOf(currentvertex);
				if ((currentVertexRank % 100) == 0 && (fcpvertex == null)
						&& (currentVertexRank != 0)) {
					logger.log(Level.INFO, "list scheduling: "
							+ currentVertexRank + " vertices mapped ");
				}

				chosenoperator = null;

			}
		}

		/*
		 * List<IScheduleElement> alreadyRescheduled = new
		 * ArrayList<IScheduleElement>(); for (int i = 0; i < 20; i++) {
		 * archisimu.reschedule(alreadyRescheduled); }
		 */
		// archisimu.rescheduleTransfers(orderlist);
		// archisimu.retrieveTotalOrder();

		return dag;
	}
}
