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

package org.ietr.preesm.plugin.mapper.algo.list;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.core.scenario.TimingManager;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.core.workflow.sources.AlgorithmRetriever;
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.plugin.abc.impl.latency.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.abc.impl.latency.LatencyAbc;
import org.ietr.preesm.plugin.abc.impl.latency.LooselyTimedAbc;
import org.ietr.preesm.plugin.mapper.graphtransfo.SdfToDagConverter;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.tools.OperatorIterator;
import org.ietr.preesm.plugin.mapper.tools.TopologicalDAGIterator;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DirectedNeighborIndex;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * List scheduler
 * 
 * @author pmenuet
 */
public class ListScheduler {

	/**
	 * constructor
	 */
	public ListScheduler() {
		super();
	}

	/**
	 * dagimplanteddisplay: Display the DAG with the vertex name and the
	 * operator of this vertex
	 * 
	 * @param : threadName
	 * @param : MapperDAG
	 * @param : simu
	 * 
	 * @return : void
	 */
	public void dagimplanteddisplay(String threadName, MapperDAG dag,
			IAbc simu) {

		// Variables
		TopologicalDAGIterator iter = new TopologicalDAGIterator(dag);
		MapperDAGVertex currentvertex;
		Logger logger = PreesmLogger.getLogger();
		// check all the DAG
		while (iter.hasNext()) {
			currentvertex = (MapperDAGVertex)iter.next();

			logger.log(Level.FINER, threadName + "Vertex "
					+ currentvertex.getName() + " operator "
					+ simu.getEffectiveComponent(currentvertex).getName());

		}
	}

	/**
	 * dagimplanteddisplay: Display the DAG with the vertex name and the
	 * operator of this vertex
	 * 
	 * @param : MapperDAG
	 * @param : simu
	 * 
	 * @return : void
	 */
	public void dagimplanteddisplay(MapperDAG dag, IAbc simu) {

		dagimplanteddisplay("", dag, simu);
	}

	/**
	 * operatorvertexstarttime: Return the date when the operator is ready to
	 * process the vertex
	 * 
	 * @param : MapperDAG, MapperDAGVertex, Operator, IArchitectureSimulator
	 * @return : integer
	 */
	public long operatorvertexstarttime(MapperDAG dag, MapperDAGVertex vertex,
			Operator operator, IAbc simu) {

		// Variables
		long temptime;
		long starttime = 0;
		MapperDAGVertex preccurrentvertex;
		Set<DAGVertex> predset = new HashSet<DAGVertex>();

		// check the vertex is into the DAG
		vertex = dag.getMapperDAGVertex(vertex.getName());

		DirectedGraph<DAGVertex, DAGEdge> castDag = dag;
		DirectedNeighborIndex<DAGVertex, DAGEdge> neighborindex = new DirectedNeighborIndex<DAGVertex, DAGEdge>(
				castDag);

		predset.addAll(neighborindex.predecessorListOf(vertex));
		Iterator<DAGVertex> iter = predset.iterator();
		Logger logger = PreesmLogger.getLogger();

		logger.log(Level.FINEST, " entering operator/vertex start time ");
		
		// implant the vertex on the operator
		simu.implant(vertex, operator, true);
		simu.updateFinalCosts();
		
		logger
				.log(Level.FINEST, " implant the vertex on "
						+ operator.getName());
		

		// check if the vertex is a source vertex with no predecessors
		if (predset.isEmpty())
			return simu.getFinalCost(operator);

		// Search the predecessors to find when the data will be available
		while (iter.hasNext()) {
			preccurrentvertex = (MapperDAGVertex)iter.next();
			logger.log(Level.FINEST, " test parent time ");
			temptime = 0;
			simu.retrieveTotalOrder();
			temptime = simu.getFinalCost(preccurrentvertex);
			logger.log(Level.FINEST, " time parent " + temptime);

			// test if the data are already available in this operator
			if (!(simu.getEffectiveComponent(preccurrentvertex)
					.equals(operator))) {
				temptime = Math.max(temptime
						+ simu.getCost((MapperDAGEdge)dag.getEdge(preccurrentvertex, vertex)),
						simu.getFinalCost(operator));
			} else {
				temptime = Math.max(temptime, simu.getFinalCost(operator));
			}
			
			logger.log(Level.FINEST, " time with edge " + temptime);
			if (temptime > starttime) {
				starttime = temptime;
				logger.log(Level.FINEST, " start time " + starttime);
			}

		}

		//PreesmLogger.getLogger().log(Level.INFO,"tps: " + starttime + "end: " + (((LatencyAbc)simu).getTLevel(vertex, true) + ((LatencyAbc)simu).getCost(vertex)));
			
		return starttime;
	}

	/**
	 * schedule: Do a mapping with the help of the lists (CPN-Dominant list,
	 * Blocking node list and the FCP list) and the architecture. It can take
	 * one vertex already implanted with a particular operator chosen by the
	 * user and only one.
	 * 
	 * @param : MapperDAG, List<MapperDAGVertex>, List<MapperDAGVertex>,
	 *        List<MapperDAGVertex>,IArchitectureSimulator,
	 *        Operator,MapperDAGVertex
	 * 
	 * @return : Implemented MapperDAG
	 */

	public MapperDAG schedule(MapperDAG dag, List<MapperDAGVertex> orderlist, IAbc archisimu,
			Operator operatorfcp, MapperDAGVertex fcpvertex) {

		// Variables
		Operator currentoperator = null;
		Operator chosenoperator = null;
		List<Operator> operatorlist = new ArrayList<Operator>();
		Logger logger = PreesmLogger.getLogger();

		// Implant the fastest one to be ready among the operators in the vertex
		// check the vertex by priority in the CPN-Dominant list
		logger.log(Level.FINEST, " entering schedule ");
		for (MapperDAGVertex currentvertex : orderlist) {
			logger.log(Level.FINEST, " vertex to implant "
					+ currentvertex.getName());

			// Implanting forced by the user or the Fast algorithm
			if (currentvertex.equals(fcpvertex)) {
				archisimu.implant(currentvertex, operatorfcp, true);
				logger.log(Level.FINEST, " vertex " + currentvertex.getName()
						+ " already implanted ");

			} else {

				long time = Long.MAX_VALUE;
				// Choose the operator
				OperatorIterator iterop = new OperatorIterator(currentvertex,
						archisimu.getArchitecture());

				while (iterop.hasNext()) {

					currentoperator = iterop.next();
					logger.log(Level.FINEST, " Considering vertex "
							+ currentvertex.getName() + " on the operator "
							+ currentoperator.getName());

					long test = operatorvertexstarttime(dag, currentvertex,
							currentoperator, archisimu);
					// test the earliest ready operator
					if (test < time) {
						chosenoperator = currentoperator;
						logger.log(Level.FINEST, " Chosen operator is : "
								+ chosenoperator.getName());
						time = test;
					}

				}
				// Implant the chosen operator in the CPN-Dominant list
				archisimu.implant(currentvertex, chosenoperator, true);
				
				if(chosenoperator != null)
					logger.log(Level.FINEST, " Chosen operator "
							+ chosenoperator.getName() + " is implanted ");

				chosenoperator = null;
				operatorlist.clear();

			}
		}
		
		archisimu.rescheduleTransfers(orderlist);
		archisimu.retrieveTotalOrder();

		return dag;
	}

}
