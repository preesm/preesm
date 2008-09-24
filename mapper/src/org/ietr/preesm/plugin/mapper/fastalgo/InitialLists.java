/**
 * 
 */
package org.ietr.preesm.plugin.mapper.fastalgo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.infinitehomogeneous.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.mapper.graphtransfo.DAGCreator;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.tools.BLevelIterator;
import org.ietr.preesm.plugin.mapper.tools.SubsetFinder;
import org.ietr.preesm.plugin.mapper.tools.ToolBox;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DirectedNeighborIndex;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;

/**
 * Creates the CPN dominant list
 * 
 * @author pmenuet
 */
public class InitialLists {

	// List of the nodes with a link with the critical path
	protected List<MapperDAGVertex> blockingNodesList;

	// List of the nodes which are ordered by the CPNDominant Sequence List
	protected List<MapperDAGVertex> cpnDominantList;

	// List of the nodes of the critical path
	protected List<MapperDAGVertex> finalcriticalpathList;

	protected List<MapperDAGVertex> OBNlist;

	/**
	 * constructors
	 */

	public InitialLists() {
		super();
		cpnDominantList = new ArrayList<MapperDAGVertex>();
		blockingNodesList = new ArrayList<MapperDAGVertex>();
		finalcriticalpathList = new ArrayList<MapperDAGVertex>();
		OBNlist = new ArrayList<MapperDAGVertex>();

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
		Iterator<MapperDAGVertex> iter = this.cpnDominantList.listIterator();
		while (iter.hasNext()) {
			MapperDAGVertex temp = ((MapperDAGVertex) iter.next()).clone();
			if (temp != null)
				newlist.add(temp);
		}
		initialLists.setCpnDominantList(newlist);

		// retrieve and clone the blockingNodesList
		List<MapperDAGVertex> newlist2 = new ArrayList<MapperDAGVertex>();
		iter = this.blockingNodesList.iterator();
		while (iter.hasNext()) {
			MapperDAGVertex temp = ((MapperDAGVertex) iter.next()).clone();
			if (temp != null)
				newlist2.add(temp);
		}
		initialLists.setBlockingNodesList(newlist2);

		// retrieve and clone the finalcriticalpathList
		List<MapperDAGVertex> newlist3 = new ArrayList<MapperDAGVertex>();
		iter = this.finalcriticalpathList.iterator();
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
	 * CPNdominantlist and implement it in the lists(CPN dominant list and the
	 * BLocking nodes list)
	 * 
	 * @param : MapperDAG ,MapperDAGVertex, List<MapperDAGVertex>,
	 *        List<MapperDAGVertex>,IArchitectureSimulator
	 * @return : void
	 */
	private void checkpredecessor(MapperDAG dag, MapperDAGVertex currentvertex,
			List<MapperDAGVertex> orderlist,
			List<MapperDAGVertex> blockingnode, IAbc archi) {

		// Variables
		MapperDAGVertex cpnvertex = null;
		
		DirectedGraph<DAGVertex, DAGEdge> castDag = dag;
		DirectedNeighborIndex<DAGVertex, DAGEdge> neighborindex = new DirectedNeighborIndex<DAGVertex, DAGEdge>(castDag);
		
		Set<DAGVertex> predset = new HashSet<DAGVertex>();

		// check the parents of the current vertex
		cpnvertex = currentvertex;
		predset.clear();
		predset.addAll(neighborindex.predecessorListOf(currentvertex));

		// Run backward in the DAG to find the node with its parents in the
		// CPNdominantlist and with the b-level maximum
		while (!(orderlist.containsAll(predset))) {
			cpnvertex = choixIBN(dag, predset, orderlist, archi);
			predset.clear();
			predset.addAll(neighborindex
					.predecessorListOf((MapperDAGVertex) cpnvertex));

		}
		orderlist.add(cpnvertex);

	}

	/**
	 * choixIBN: Determine among the node's predecessors, the vertex necessary
	 * to continue the algorithm
	 * 
	 * @param : MapperDAG ,Set<MapperDAGVertex>,
	 *        List<MapperDAGVertex>,IArchitectureSimulator
	 * @return : MapperDAGVertex
	 */
	private MapperDAGVertex choixIBN(MapperDAG dag,
			Set<DAGVertex> predset, List<MapperDAGVertex> orderlist,
			IAbc archi) {

		// Variables
		Iterator<DAGVertex> iter = predset.iterator();
		MapperDAGVertex currentvertex = null;
		MapperDAGVertex vertexresult = null;
		int blevelmax = 0;
		int tlevelmax = Integer.MAX_VALUE;

		// Check into the predecessor list the one with the biggest b-level or
		// if
		// they have the same with the smallest t-level
		while (iter.hasNext()) {
			currentvertex = (MapperDAGVertex)iter.next();

			if (archi.getBLevel(currentvertex) == blevelmax
					&& !(orderlist.contains(currentvertex))) {
				if (archi.getTLevel(currentvertex) < tlevelmax) {
					tlevelmax = archi.getTLevel(currentvertex);

					vertexresult = currentvertex;
				}
			}

			if (archi.getBLevel(currentvertex) > blevelmax
					&& !(orderlist.contains(currentvertex))) {
				vertexresult = currentvertex;
				blevelmax = archi.getBLevel(currentvertex);
				tlevelmax = archi.getTLevel(currentvertex);
			}

		}

		return vertexresult;

	}

	/**
	 * constructCPN : Critical Path implemented in the CPN-DominantList
	 * (Critical Path Nodes= CPN) and the FCP-list (Final Critical Path = FCP)
	 * 
	 * @param : MapperDAG , List<MapperDAGVertex>, List<MapperDAGVertex>,
	 *        List<MapperDAGVertex>
	 * @return : void
	 */
	private void constructCPN(MapperDAG dag, List<MapperDAGVertex> orderlist,
			List<MapperDAGVertex> blockingnode, List<MapperDAGVertex> fcplist,
			IAbc archi) {

		// variables
		MapperDAGVertex currentvertex;
		MapperDAGVertex cpnvertex = null;
		MapperDAGVertex tempvertex = null;
		int commax = 0;
		BLevelIterator iterator = new BLevelIterator(dag, archi, false);
		
		DirectedGraph<DAGVertex, DAGEdge> castDag = dag;
		DirectedNeighborIndex<DAGVertex, DAGEdge> neighborindex = new DirectedNeighborIndex<DAGVertex, DAGEdge>(
				castDag);
		
		Set<DAGVertex> succset = new HashSet<DAGVertex>();

		// The DAG is entirely read in b-level order by the iterator to find the
		// Critical Path
		currentvertex = iterator.next();
		while (!(currentvertex.incomingEdges().isEmpty()))
			currentvertex = iterator.next();

		// the first CPNdominant is found
		// put it in the order list and the FCP list
		orderlist.add(currentvertex);
		fcplist.add(currentvertex);
		cpnvertex = currentvertex;

		// Find the successor of the first CPN (Critical Path Node)
		succset.addAll(neighborindex
				.successorListOf((MapperDAGVertex) cpnvertex));

		// Do the process while the vertex is not a leaf
		while (!(succset.isEmpty())) {

			Iterator<DAGVertex> iter = succset.iterator();

			// the successors are read to find the next
			// level to process (biggest communication, smallest t-level
			commax = -1;
			while (iter.hasNext()) {

				currentvertex = (MapperDAGVertex)iter.next();
				MapperDAG base = (MapperDAG) currentvertex.getBase();

				if (archi.getCost((MapperDAGEdge) cpnvertex.getBase().getEdge(
						cpnvertex, currentvertex)) == commax) {
					if (archi.getTLevel(currentvertex) < archi
							.getTLevel(tempvertex)) {
						tempvertex = currentvertex;
					}
				} else if (archi.getCost((MapperDAGEdge) base.getEdge(
						cpnvertex, currentvertex)) > commax) {
					commax = archi.getCost((MapperDAGEdge) base.getEdge(
							cpnvertex, currentvertex));
					tempvertex = currentvertex;
				}
			}

			cpnvertex = tempvertex;
			currentvertex = tempvertex;
			fcplist.add(currentvertex);
			succset.clear();
			succset.addAll(neighborindex.successorListOf(currentvertex));
			// Search for the predecessor of the final critical path nodes
			// because they must be implanted before their successors
			while (!(orderlist.contains(currentvertex))) {
				checkpredecessor(dag, currentvertex, orderlist, blockingnode,
						archi);
			}

		}

	}

	/**
	 * constructCPNobn: Add to the CPN dominant list and the Blocking Node list
	 * all the remaining nodes in a decreasing order of b-level
	 * 
	 * @param : MapperDAG , List<MapperDAGVertex>, List<MapperDAGVertex>
	 * @return : void
	 */
	private void constructCPNobn(MapperDAG dag,
			List<MapperDAGVertex> orderlist,
			List<MapperDAGVertex> blockingnode, List<MapperDAGVertex> OBNList,
			IAbc archi) {

		// Variables
		MapperDAGVertex currentvertex = null;
		BLevelIterator iterator = new BLevelIterator(dag, archi, false);

		// Class the OBN into the lists (CPN and Blocking node)
		while (iterator.hasNext()) {
			currentvertex = iterator.next();
			if (!orderlist.contains(currentvertex)) {
				orderlist.add(currentvertex);
				OBNList.add(currentvertex);

			}
		}

	}

	/**
	 * constructCPNDominantlist: Construct the CPN dominant List and the other
	 * lists necessary for the initial scheduler
	 * 
	 * @param : MapperDAG
	 * @param : simu
	 * @return : void
	 */
	public void constructInitialLists(MapperDAG dag, IAbc simu) {

		cpnDominantList.clear();
		blockingNodesList.clear();
		finalcriticalpathList.clear();
		OBNlist.clear();

		// construction step by step of all the lists
		constructCPN(dag, cpnDominantList, blockingNodesList,
				finalcriticalpathList, simu);
		constructCPNobn(dag, cpnDominantList, blockingNodesList, OBNlist, simu);

		Set<DAGVertex> currentset = dag.vertexSet();

		SubsetFinder<DAGVertex, List<MapperDAGVertex>> subsetfinder = new SubsetFinder<DAGVertex, List<MapperDAGVertex>>(
				currentset, finalcriticalpathList) {

			@Override
			protected boolean subsetCondition(DAGVertex tested,
					List<MapperDAGVertex> finalcriticalpathList) {

				boolean test = false;

				if (!(finalcriticalpathList.contains(tested)))
					test = true;

				return test;
			}

		};

		blockingNodesList.clear();
		ToolBox.addAllNodes(blockingNodesList, subsetfinder.subset());
		simu.resetImplementation();
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
		Logger logger = PreesmLogger.getLogger();
		// check all the list
		while (iter.hasNext()) {
			currentvertex = iter.next();

			logger.log(Level.FINEST, threadName + "Vertex "
					+ currentvertex.getName());

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

	public List<MapperDAGVertex> getBlockingNodesList() {
		return blockingNodesList;
	}

	public List<MapperDAGVertex> getCpnDominantList() {
		return cpnDominantList;
	}

	public List<MapperDAGVertex> getFinalcriticalpathList() {
		return finalcriticalpathList;
	}

	public void setBlockingNodesList(List<MapperDAGVertex> blockingNodesList) {
		this.blockingNodesList = blockingNodesList;
	}

	public void setCpnDominantList(List<MapperDAGVertex> cpnDominantList) {
		this.cpnDominantList = cpnDominantList;
	}

	public void setFinalcriticalpathList(
			List<MapperDAGVertex> finalcriticalpathList) {
		this.finalcriticalpathList = finalcriticalpathList;
	}

	public List<MapperDAGVertex> getOBNlist() {
		return OBNlist;
	}

	public void setOBNlist(List<MapperDAGVertex> nlist) {
		OBNlist = nlist;
	}

	/**
	 * Main for tests
	 */
	public static void main(String[] args) {

		List<MapperDAGVertex> testCPN = new ArrayList<MapperDAGVertex>();
		List<MapperDAGVertex> testBL = new ArrayList<MapperDAGVertex>();
		List<MapperDAGVertex> testfcp = new ArrayList<MapperDAGVertex>();
		List<MapperDAGVertex> testOBN = new ArrayList<MapperDAGVertex>();

		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.ALL);

		logger.log(Level.FINEST, "Creating archi");
		MultiCoreArchitecture archi = Examples.get4C64Archi();

		logger.log(Level.FINEST, "Creating DAG");
		MapperDAG dagtest = new DAGCreator().dagexample1(archi);

		IAbc simu = new InfiniteHomogeneousAbc(dagtest, archi);
		simu.getFinalTime();

		InitialLists scheduler = new InitialLists();

		logger.log(Level.FINEST, "Evaluating Cpndominant and fcp ");
		scheduler.constructCPN(dagtest, testCPN, testBL, testfcp, simu);

		logger.log(Level.FINEST, "Displaying Cpndominantlist with IBN ");
		scheduler.orderlistdisplay(testCPN);

		logger.log(Level.FINEST, "Displaying fcp ");
		scheduler.orderlistdisplay(testfcp);

		logger.log(Level.FINEST, "Evaluating Cpndominant with IBN and OBN ");
		scheduler.constructCPNobn(dagtest, testCPN, testBL, testOBN, simu);

		logger
				.log(Level.FINEST,
						"Displaying Cpndominantlist with IBN and OBN ");
		scheduler.orderlistdisplay(testCPN);

		logger.log(Level.FINEST, "Evaluating constructInitialList ");
		scheduler.constructInitialLists(dagtest, simu);

		logger.log(Level.FINEST, "Displaying Cpndominantlist ");
		scheduler.orderlistdisplay(scheduler.cpnDominantList);

		logger.log(Level.FINEST, "Displaying blockingNodes ");
		scheduler.orderlistdisplay(scheduler.blockingNodesList);

		logger.log(Level.FINEST, "Displaying fcp ");
		scheduler.orderlistdisplay(scheduler.finalcriticalpathList);

		logger.log(Level.FINEST, "Test finished");
	}
}