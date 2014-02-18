/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

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

package org.ietr.preesm.mapper.algo.list;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.dag.DAGVertex;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.ietr.preesm.mapper.abc.IAbc;
import org.ietr.preesm.mapper.abc.impl.latency.LatencyAbc;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.tools.BLevelIterator;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DirectedNeighborIndex;

/**
 * Creates the CPN dominant list
 * 
 * @author pmenuet
 * @author mpelcat
 */
public class InitialLists {

	// List of the nodes with a link with the critical path
	protected List<MapperDAGVertex> blockingNodes;

	// List of the nodes which are ordered by the CPNDominant Sequence List
	protected List<MapperDAGVertex> cpnDominant;

	// List of the nodes of the critical path
	protected List<MapperDAGVertex> criticalPath;

	/**
	 * constructors
	 */

	public InitialLists() {
		super();
		cpnDominant = new ArrayList<MapperDAGVertex>();
		blockingNodes = new ArrayList<MapperDAGVertex>();
		criticalPath = new ArrayList<MapperDAGVertex>();

	}

	/**
	 * clone: Clone the initial lists
	 * 
	 * @param :
	 * 
	 * @return : InitialLists
	 */

	@Override
	public InitialLists clone() {

		// variables
		InitialLists initialLists = new InitialLists();
		List<MapperDAGVertex> newlist = new ArrayList<MapperDAGVertex>();

		// retrieve and clone the cpnDominantList
		Iterator<MapperDAGVertex> iter = this.cpnDominant.listIterator();
		while (iter.hasNext()) {
			MapperDAGVertex temp = ((MapperDAGVertex) iter.next()).clone();
			if (temp != null)
				newlist.add(temp);
		}
		initialLists.setCpnDominantList(newlist);

		// retrieve and clone the blockingNodesList
		List<MapperDAGVertex> newlist2 = new ArrayList<MapperDAGVertex>();
		iter = this.blockingNodes.iterator();
		while (iter.hasNext()) {
			MapperDAGVertex temp = ((MapperDAGVertex) iter.next()).clone();
			if (temp != null)
				newlist2.add(temp);
		}
		initialLists.setBlockingNodesList(newlist2);

		// retrieve and clone the finalcriticalpathList
		List<MapperDAGVertex> newlist3 = new ArrayList<MapperDAGVertex>();
		iter = this.criticalPath.iterator();
		while (iter.hasNext()) {
			MapperDAGVertex temp = ((MapperDAGVertex) iter.next()).clone();
			if (temp != null)
				newlist3.add(temp);
		}
		initialLists.setFinalcriticalpathList(newlist3);

		return initialLists;

	}

	/**
	 * checkpredecessor: Choose the vertex necessary to continue the
	 * CPNdominantlist and add it in the CPN dominant list
	 * 
	 * @param : MapperDAG ,MapperDAGVertex, List<MapperDAGVertex>,
	 *        List<MapperDAGVertex>,IArchitectureSimulator
	 * @return : true if a vertex was found
	 */
	private boolean choosePredecessor(MapperDAG dag,
			MapperDAGVertex currentvertex, List<MapperDAGVertex> orderlist,
			LatencyAbc abc) {

		MapperDAGVertex cpnvertex = null;

		DirectedGraph<DAGVertex, DAGEdge> castDag = dag;
		DirectedNeighborIndex<DAGVertex, DAGEdge> neighborindex = new DirectedNeighborIndex<DAGVertex, DAGEdge>(
				castDag);

		Set<DAGVertex> predset = new HashSet<DAGVertex>();

		// check the parents of the current vertex
		cpnvertex = currentvertex;
		predset.addAll(neighborindex.predecessorListOf(currentvertex));

		// Run backward in the DAG to find the node with its parents in the
		// CPNdominantlist and with the maximum b-level
		while (!(orderlist.containsAll(predset))) {
			cpnvertex = ibnChoice(dag, predset, orderlist, abc);
			predset.clear();

			if (cpnvertex != null) {
				predset.addAll(neighborindex
						.predecessorListOf((MapperDAGVertex) cpnvertex));
			} else {
				WorkflowLogger.getLogger().log(Level.SEVERE,
						"Predecessor not found");
				return false;
			}

		}

		orderlist.add(cpnvertex);
		return true;

	}

	/**
	 * ibnChoice: Chooses among the node's predecessors, the vertex necessary to
	 * continue the algorithm
	 * 
	 * @param : MapperDAG ,Set<MapperDAGVertex>,
	 *        List<MapperDAGVertex>,IArchitectureSimulator
	 * @return : MapperDAGVertex
	 */
	private MapperDAGVertex ibnChoice(MapperDAG dag, Set<DAGVertex> predset,
			List<MapperDAGVertex> orderlist, LatencyAbc archi) {

		Iterator<DAGVertex> iter = predset.iterator();
		MapperDAGVertex currentvertex = null;
		MapperDAGVertex vertexresult = null;
		long blevelmax = 0;
		long tlevelmax = Long.MAX_VALUE;

		// Check into the predecessor list the one with the biggest b-level or
		// if they have the same with the smallest t-level
		while (iter.hasNext()) {
			currentvertex = (MapperDAGVertex) iter.next();
			long bLevel = archi.getBLevel(currentvertex, false);
			long tLevel = archi.getTLevel(currentvertex, false);

			if (bLevel == blevelmax && !(orderlist.contains(currentvertex))) {
				if (tLevel < tlevelmax) {
					tlevelmax = tLevel;
					vertexresult = currentvertex;
				}
			} else if (bLevel > blevelmax
					&& !(orderlist.contains(currentvertex))) {
				vertexresult = currentvertex;
				blevelmax = bLevel;
				tlevelmax = tLevel;
			} else if (bLevel == -1) {
				WorkflowLogger.getLogger().log(
						Level.SEVERE,
						"CPN list construction: b-level can not be computed for vertex "
								+ currentvertex);
			}

		}

		return vertexresult;

	}

	/**
	 * constructCPN : Critical Path implemented in the CPN-DominantList
	 * (Critical Path Nodes= CPN) and the FCP-list (Final Critical Path = FCP).
	 * See YK Kwok thesis p.59
	 */
	public boolean constructCPN(MapperDAG dag,
			List<MapperDAGVertex> cpnDominant,
			List<MapperDAGVertex> criticalPath, LatencyAbc abc) {

		WorkflowLogger.getLogger()
				.log(Level.INFO, "Starting to build CPN list");

		// variables
		MapperDAGVertex currentvertex;
		MapperDAGVertex cpnvertex = null;
		MapperDAGVertex tempvertex = null;
		long commax = 0;

		// Sets the t and b levels
		abc.updateTimings();

		// This step takes time because the whole graph b levels are calculated.
		BLevelIterator iterator = new BLevelIterator(abc, dag, false);

		// The DAG is entirely read in b-level order by the iterator to find the
		// Critical Path
		currentvertex = iterator.next();
		while (!(currentvertex.incomingEdges().isEmpty()))
			currentvertex = iterator.next();

		DirectedGraph<DAGVertex, DAGEdge> castDag = dag;
		DirectedNeighborIndex<DAGVertex, DAGEdge> neighborindex = new DirectedNeighborIndex<DAGVertex, DAGEdge>(
				castDag);

		Set<DAGVertex> succset = new HashSet<DAGVertex>();

		// the first CPNdominant is found
		// put it in the order list and the FCP list
		cpnDominant.add(currentvertex);
		criticalPath.add(currentvertex);
		cpnvertex = currentvertex;
		MapperDAG base = (MapperDAG) currentvertex.getBase();

		// Find the successor of the first CPN (Critical Path Node)
		succset.addAll(neighborindex
				.successorListOf((MapperDAGVertex) cpnvertex));

		WorkflowLogger.getLogger().log(Level.INFO, "Building CPN list.");

		/* Do the process while the vertex is not a leaf */
		while (!(succset.isEmpty())) {
			Iterator<DAGVertex> iter = succset.iterator();

			// the successors are read to find the next
			// level to process (biggest communication, smallest t-level
			commax = -1;
			while (iter.hasNext()) {

				currentvertex = (MapperDAGVertex) iter.next();

				long edgeCost = abc.getCost((MapperDAGEdge) base.getEdge(
						cpnvertex, currentvertex));

				if (edgeCost > commax) {
					commax = edgeCost;
					tempvertex = currentvertex;
				} else if (edgeCost == commax) {
					if (tempvertex == null
							|| abc.getTLevel(currentvertex, false) < abc
									.getTLevel(tempvertex, false)) {
						tempvertex = currentvertex;
					}
				}
			}

			cpnvertex = tempvertex;
			currentvertex = tempvertex;
			criticalPath.add(currentvertex);
			succset.clear();
			succset.addAll(neighborindex.successorListOf(currentvertex));
			/*
			 * Search for the predecessor of the final critical path nodes
			 * because they must be mapped before their successors
			 */
			while (!(cpnDominant.contains(currentvertex))) {
				// If no predecessor was found
				if (!choosePredecessor(dag, currentvertex, cpnDominant, abc)) {
					WorkflowLogger.getLogger().log(
							Level.SEVERE,
							"No predecessor was found for vertex: "
									+ currentvertex.getName());
					return false;
				}
			}

		}

		return true;

	}

	/**
	 * constructCPNobn: Add to the CPN dominant list and the Blocking Node list
	 * all the remaining nodes in a decreasing order of b-level
	 * 
	 * @param : MapperDAG , List<MapperDAGVertex>, List<MapperDAGVertex>
	 * @return : void
	 */
	private void addCPNobn(MapperDAG dag, List<MapperDAGVertex> orderlist,
			IAbc abc) {

		// Variables
		MapperDAGVertex currentvertex = null;
		BLevelIterator iterator = new BLevelIterator(abc, dag, false);

		// Class the OBN into the lists (CPN and Blocking node)
		while (iterator.hasNext()) {
			currentvertex = iterator.next();
			if (!orderlist.contains(currentvertex)) {
				orderlist.add(currentvertex);

			}
		}

	}

	/**
	 * constructCPNDominantlist: Construct the CPN dominant List and the other
	 * lists necessary for the initial scheduler
	 * 
	 * A CPN is a node included in a critical path. An IBN is a node from which
	 * there is a path reaching a CPN. An OBN is a node which is neither a CPN
	 * nor an IBN.
	 * 
	 * @param : MapperDAG
	 * @param : simu
	 * @return : true if the initial lists were constructed
	 */
	public boolean constructInitialLists(MapperDAG dag, IAbc simu) {

		cpnDominant.clear();
		blockingNodes.clear();
		criticalPath.clear();

		if (simu instanceof LatencyAbc) {
			// construction of critical path and CPN dominant list with CPN and
			// IBN actors
			if (!constructCPN(dag, cpnDominant, criticalPath, (LatencyAbc) simu)) {
				WorkflowLogger.getLogger().log(Level.SEVERE,
						"Problem with initial list construction");
				return false;
			}
		} else {
			WorkflowLogger.getLogger().log(Level.SEVERE,
					"To construct initial lists, a latency ABC is needed.");
			return false;
		}

		WorkflowLogger.getLogger().log(Level.INFO,
				"Adding OBN actors to CPN and IBN actors in CPN dominant list");
		addCPNobn(dag, cpnDominant, simu);
		
		for (DAGVertex v : dag.vertexSet()) {
			if (!(criticalPath.contains(v))) {
				blockingNodes.add((MapperDAGVertex) v);
			}
		}

		simu.resetImplementation();

		return true;
	}

	/**
	 * orderlistdisplay: Display a List with the logger
	 * 
	 * @param : threadName
	 * @param : List<MapperDAGVertex>
	 * @return : void
	 */
	public void orderlistdisplay(String threadName,
			List<MapperDAGVertex> tempset) {

		// Variables
		Iterator<MapperDAGVertex> iter = tempset.iterator();
		MapperDAGVertex currentvertex;
		Logger logger = WorkflowLogger.getLogger();
		// check all the list
		while (iter.hasNext()) {
			currentvertex = iter.next();

			logger.log(Level.FINEST,
					threadName + "Vertex " + currentvertex.getName());

		}
	}

	/**
	 * orderlistdisplay: Display a List with the logger
	 * 
	 * @param : List<MapperDAGVertex>
	 * @return : void
	 */
	public void orderlistdisplay(List<MapperDAGVertex> tempset) {

		orderlistdisplay("", tempset);
	}

	/**
	 * 
	 * getters and setters
	 * 
	 */

	public List<MapperDAGVertex> getBlockingNodes() {
		return blockingNodes;
	}

	public List<MapperDAGVertex> getCpnDominant() {
		return cpnDominant;
	}

	public List<MapperDAGVertex> getCriticalpath() {
		return criticalPath;
	}

	public void setBlockingNodesList(List<MapperDAGVertex> blockingNodesList) {
		this.blockingNodes = blockingNodesList;
	}

	public void setCpnDominantList(List<MapperDAGVertex> cpnDominantList) {
		this.cpnDominant = cpnDominantList;
	}

	public void setFinalcriticalpathList(
			List<MapperDAGVertex> finalcriticalpathList) {
		this.criticalPath = finalcriticalpathList;
	}
}