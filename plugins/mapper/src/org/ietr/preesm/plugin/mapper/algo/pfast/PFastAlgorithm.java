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

package org.ietr.preesm.plugin.mapper.algo.pfast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

import net.sf.dftools.architecture.slam.Design;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.order.VertexOrderList;
import org.ietr.preesm.plugin.abc.taskscheduling.AbstractTaskSched;
import org.ietr.preesm.plugin.mapper.algo.fast.FastAlgorithm;
import org.ietr.preesm.plugin.mapper.algo.list.InitialLists;
import org.ietr.preesm.plugin.mapper.algo.list.KwokListScheduler;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.params.AbcParameters;
import org.ietr.preesm.plugin.mapper.params.FastAlgoParameters;
import org.ietr.preesm.plugin.mapper.params.PFastAlgoParameters;
import org.ietr.preesm.plugin.mapper.plot.BestCostPlotter;
import org.ietr.preesm.plugin.mapper.plot.bestcost.BestCostEditor;

/**
 * Task scheduling FAST algorithm multithread
 * 
 * @author pmenuet
 * @author mpelcat
 */
public class PFastAlgorithm extends Observable {

	/**
	 * The scheduling (total order of tasks) for the best found solution.
	 */
	private VertexOrderList bestTotalOrder = null;

	/**
	 * FinalTimeComparator : comparator between two different implementation
	 * based on
	 */
	private class FinalTimeComparator implements Comparator<MapperDAG> {

		/**
		 * @Override compare
		 * 
		 * @param : MapperDAG, MapperDAG
		 * @return : integer
		 */
		@Override
		public int compare(MapperDAG o1, MapperDAG o2) {

			long difference = 0;

			difference = o1.getScheduleCost();

			difference -= o2.getScheduleCost();

			if (difference == 0) {
				difference = 1;
			}

			return (int) difference;
		}

		/**
		 * Constructor : FinalTimeComparator
		 * 
		 * @param : ArchitectureSimulatorType, MapperDAG, IArchitecture
		 * 
		 */
		public FinalTimeComparator(AbcParameters abcParams, MapperDAG dag,
				Design archi, PreesmScenario scenario) {
			super();
		}

	}

	/**
	 * Constructor : PFastAlgorithm
	 * 
	 */
	public PFastAlgorithm() {
		super();
	}

	/**
	 * chooseNbCores : Determine how many processors will be used among the
	 * available ones and return the set of nodes on which each processor will
	 * perform the fast algorithm
	 * 
	 * @param initialLists
	 * @param nboperator
	 *            // number of available processor
	 * @param nodesmin
	 *            // number of nodes necessary for each thread
	 * @param subSet
	 *            // Set with the partial BlockingNodesLists
	 * 
	 * @return integer
	 */
	public int chooseNbCores(InitialLists initialLists, int nboperator,
			int nodesmin, Set<Set<String>> subSet) {

		// initialization
		MapperDAGVertex currentvertex;
		int nbsubsets = 0;
		int nbnodes = 0;
		List<MapperDAGVertex> BNlist = new ArrayList<MapperDAGVertex>();
		BNlist.addAll(initialLists.getBlockingNodes());
		Set<String> tempSet = null;
		nbnodes = nbnodes(BNlist, nbsubsets, nodesmin);

		// find number of thread possible
		nbsubsets = setThreadNumber(BNlist, nboperator, nodesmin);

		if (nbsubsets == 0) {
			WorkflowLogger
					.getLogger()
					.log(Level.SEVERE,
							"Not enough nodes to execute PFAST. Try reducing nodesmin in workflow or use another mapper.");
		}

		// find number of nodes per thread
		nbnodes = nbnodes(BNlist, nbsubsets, nodesmin);

		Iterator<MapperDAGVertex> riter = BNlist.iterator();
		Iterator<Set<String>> itera = subSet.iterator();
		subSet.add(new HashSet<String>());

		// Put the nodes of the BlockingNodes List in the Sublist
		for (int i = 0; i < nbsubsets; i++) {
			subSet.add(new HashSet<String>());
			itera = subSet.iterator();
			tempSet = itera.next();
			for (int j = 0; j < nbnodes; j++) {
				if (!(subSet.containsAll(BNlist))) {
					riter = BNlist.iterator();
					currentvertex = riter.next();
					tempSet.add(new String(currentvertex.getName()));
					BNlist.remove(currentvertex);

				}
			}
		}

		return nbsubsets;
	}

	/**
	 * setnumber : return the number of processor(thread) necessary to process
	 * the PFastAlgorithm with the List of Vertex
	 * 
	 * @param BLlist
	 *            // BlockingNodesList
	 * @param nboperator
	 *            // number of available processor
	 * @param nodesmin
	 *            // number of nodes necessary for each thread
	 * 
	 * @return integer
	 */

	public int setThreadNumber(List<MapperDAGVertex> BLlist, int nboperator,
			int nodesmin) {

		int nbnodes = BLlist.size();
		int nbsubsets;
		if (nboperator == 0)
			nboperator = 1;
		// verify if we have enough nodes to do the PFastAlgorithm
		if (nbnodes >= nboperator * nodesmin) {
			nbsubsets = nboperator;
		} else {
			nbsubsets = (int) nbnodes / nodesmin;
		}
		return nbsubsets;
	}

	/**
	 * nbnodes : return the number of nodes per partial BlockingNodesList
	 * 
	 * @param BLlist
	 *            // BlockingNodesList
	 * @param nbsubsets
	 *            // Number of thread
	 * @param nodesmin
	 *            // Number of nodes per thread
	 * @return integer
	 */
	public int nbnodes(List<MapperDAGVertex> BLlist, int nbsubsets, int nodesmin) {

		int nbnodes = BLlist.size();
		if (nbsubsets == 0)
			nbsubsets = 1;
		// Find the number of nodes for each thread
		int nbnodeset = (int) nbnodes / nbsubsets;
		if ((nbnodes % nodesmin) != 0)
			nbnodeset++;
		return nbnodeset;
	}

	/**
	 * mapcontain = verify if the vertex is already in the other partial lists
	 * 
	 * @param subSet
	 * @param vertex
	 * 
	 * @return boolean
	 */
	public boolean mapcontain(Set<Set<MapperDAGVertex>> subSet,
			MapperDAGVertex vertex) {

		Iterator<Set<MapperDAGVertex>> itera = subSet.iterator();
		// Verify if the vertex is already in the subset
		while (itera.hasNext()) {
			Set<MapperDAGVertex> temp = itera.next();
			if (temp.contains(vertex))
				return true;
		}
		return false;
	}

	/**
	 * map = perform the Pfast Algo (it is the main thread)
	 * 
	 * // Determine if we want the Pfast solution or a population of good
	 * solution to perform another algorithm
	 * 
	 * @param populationsize
	 *            // if we want a population this parameter determine how many
	 *            individuals we want in the population
	 * @param populationList
	 *            // List of MapperDAG which are solution
	 * 
	 * @return MapperDAG
	 */
	public MapperDAG map(MapperDAG dag, Design archi,
			PreesmScenario scenario, InitialLists initialLists,
			AbcParameters abcParams, PFastAlgoParameters pFastParams,
			boolean population, int populationsize, boolean isDisplaySolutions,
			List<MapperDAG> populationList, AbstractTaskSched taskSched) {

		int i = 0;
		if (populationsize < 1)
			populationsize = 1;
		int k = 0;
		int totalsearchcount = 0;

		Vector<MapperDAGVertex> cpnDominantVector = new Vector<MapperDAGVertex>(
				initialLists.getCpnDominant());
		Vector<MapperDAGVertex> blockingnodeVector = new Vector<MapperDAGVertex>(
				initialLists.getBlockingNodes());
		Vector<MapperDAGVertex> fcpVector = new Vector<MapperDAGVertex>(
				initialLists.getCriticalpath());
		MapperDAG dagfinal;
		KwokListScheduler scheduler = new KwokListScheduler();
		IAbc archisimu = AbstractAbc.getInstance(abcParams, dag, archi,
				scenario);
		Set<Set<String>> subSet = new HashSet<Set<String>>();

		FastAlgoParameters fastParams = new FastAlgoParameters(
				pFastParams.getFastTime(),
				pFastParams.getFastLocalSearchTime(),
				pFastParams.isDisplaySolutions());

		// if only one operator the fast must be used
		if (pFastParams.getProcNumber() == 0) {
			FastAlgorithm algorithm = new FastAlgorithm(initialLists, scenario);

			dag = algorithm.map("Fast", abcParams, fastParams, dag, archi,
					false, false, false, null, cpnDominantVector,
					blockingnodeVector, fcpVector, taskSched);
			return dag;
		}

		// Data window set
		Semaphore pauseSemaphore = new Semaphore(1);
		final BestCostPlotter costPlotter = new BestCostPlotter(
				"PFast Algorithm", pauseSemaphore);

		if (!population) {
			costPlotter.setSUBPLOT_COUNT(1);
			// demo.display();
			BestCostEditor.createEditor(costPlotter);

			this.addObserver(costPlotter);
		}

		// step 1
		// step 2
		dagfinal = scheduler.schedule(dag, cpnDominantVector, archisimu, null,
				null).clone();

		bestTotalOrder = archisimu.getTotalOrder();
		archisimu.updateFinalCosts();
		long iBest = (Long) archisimu.getFinalCost();

		setChanged();
		notifyObservers(iBest);
		dagfinal.setScheduleCost(iBest);
		dag.setScheduleCost(iBest);
		// step 3/4
		int nbCores = chooseNbCores(initialLists, pFastParams.getProcNumber(),
				pFastParams.getNodesmin(), subSet);

		Iterator<Set<String>> subiter = subSet.iterator();
		ConcurrentSkipListSet<MapperDAG> mappedDAGSet = new ConcurrentSkipListSet<MapperDAG>(
				new FinalTimeComparator(abcParams, dagfinal, archi, scenario));

		// step 5/7/8
		while (totalsearchcount < pFastParams.getFastNumber()) {

			// step 11

			// create ExecutorService to manage threads
			subiter = subSet.iterator();
			Set<FutureTask<MapperDAG>> futureTasks = new HashSet<FutureTask<MapperDAG>>();
			ExecutorService es = Executors.newFixedThreadPool(nbCores);

			// step 6
			for (i = k; i < nbCores + k; i++) {
				String name = String.format("thread%d", i);

				// step 9/11
				PFastCallable thread = new PFastCallable(name, dag, archi,
						subiter.next(), isDisplaySolutions, true, abcParams,
						fastParams, scenario);

				FutureTask<MapperDAG> task = new FutureTask<MapperDAG>(thread);
				futureTasks.add(task);
				es.submit(task);

			}
			k = i;

			// step 10
			try {

				Iterator<FutureTask<MapperDAG>> it = futureTasks.iterator();

				while (it.hasNext()) {

					FutureTask<MapperDAG> task = it.next();

					MapperDAG currentOutDAG = task.get();
					mappedDAGSet.add(currentOutDAG);

				}
				while (mappedDAGSet.size() > populationsize) {

					mappedDAGSet.pollLast();
				}

				// step 12
				if (!population) {
					dag = mappedDAGSet.first().clone();

					iBest = dag.getScheduleCost();
					setChanged();
					notifyObservers(iBest);

					// Mode Pause
					while (costPlotter.getActionType() == 2)
						;

					// Mode stop
					if (costPlotter.getActionType() == 1)
						break;

				}

				es.shutdown();

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			// step 13
			totalsearchcount++;

		}

		if (population) {
			Iterator<MapperDAG> ite = mappedDAGSet.iterator();
			while (ite.hasNext()) {
				MapperDAG currentdag;
				currentdag = ite.next();
				populationList.add(currentdag);
			}

		}

		bestTotalOrder = (VertexOrderList) mappedDAGSet.first()
				.getPropertyBean().getValue("bestTotalOrder");
		dagfinal = mappedDAGSet.first().clone();

		return dagfinal;
	}

	public VertexOrderList getBestTotalOrder() {
		return bestTotalOrder;
	}
}
