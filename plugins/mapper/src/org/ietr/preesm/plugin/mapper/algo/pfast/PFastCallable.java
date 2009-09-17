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

package org.ietr.preesm.plugin.mapper.algo.pfast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.impl.latency.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.abc.taskscheduling.TopologicalTaskSched;
import org.ietr.preesm.plugin.mapper.algo.fast.FastAlgorithm;
import org.ietr.preesm.plugin.mapper.algo.list.InitialLists;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.params.AbcParameters;
import org.ietr.preesm.plugin.mapper.params.FastAlgoParameters;

/**
 * One thread of Task scheduling FAST algorithm multithread
 * 
 * @author pmenuet
 */
class PFastCallable implements Callable<MapperDAG> {

	// Simulator chosen
	private AbcParameters abcParams;

	// thread named by threadName
	private String threadName;

	// DAG given by the main thread
	private MapperDAG inputDAG;

	// Architecture used to implement the DAG
	private MultiCoreArchitecture inputArchi;

	// Set of the nodes upon which we used fast algorithm in the thread
	private Set<String> blockingNodeNames;

	// parameters for the fast algorithm
	private FastAlgoParameters fastParams;

	// True if we want to display the best found solutions
	private boolean isDisplaySolutions;

	// Variables to know if we have to do the initial scheduling or not
	private boolean alreadyImplanted;

	private IScenario scenario;

	/**
	 * Constructor
	 * 
	 * @param name
	 * @param inputDAG
	 * @param inputArchi
	 * @param blockingNodeIds
	 * @param maxCount
	 * @param maxStep
	 * @param margIn
	 * @param alreadyImplanted
	 * @param simulatorType
	 */
	public PFastCallable(String name, MapperDAG inputDAG,
			MultiCoreArchitecture inputArchi, Set<String> blockingNodeNames,
			boolean isDisplaySolutions, boolean alreadyImplanted,
			AbcParameters abcParams, FastAlgoParameters fastParams,
			IScenario scenario) {
		threadName = name;
		this.inputDAG = inputDAG;
		this.inputArchi = inputArchi;
		this.blockingNodeNames = blockingNodeNames;
		this.fastParams = fastParams;
		this.alreadyImplanted = alreadyImplanted;
		this.abcParams = abcParams;
		this.isDisplaySolutions = isDisplaySolutions;
		this.scenario = scenario;
	}

	/**
	 * @Override call():
	 * 
	 * @param : void
	 * @return : MapperDAG
	 */
	@Override
	public MapperDAG call() throws Exception {

		// intern variables
		MapperDAG callableDAG;
		MapperDAG outputDAG;
		MultiCoreArchitecture callableArchi;
		List<MapperDAGVertex> callableBlockingNodes = new ArrayList<MapperDAGVertex>();

		// Critical sections where the data from the main thread are copied for
		// this thread
		synchronized (inputDAG) {
			callableDAG = inputDAG.clone();
		}

		synchronized (inputArchi) {
			callableArchi = inputArchi.clone();
		}

		synchronized (blockingNodeNames) {
			callableBlockingNodes.addAll(callableDAG
					.getVertexSet(blockingNodeNames));
		}

		// Create the CPN Dominant Sequence
		IAbc IHsimu = new InfiniteHomogeneousAbc(abcParams,
				callableDAG.clone(), callableArchi, scenario);
		InitialLists initialLists = new InitialLists();
		initialLists.constructInitialLists(callableDAG, IHsimu);

		TopologicalTaskSched taskSched = new TopologicalTaskSched(IHsimu
				.getTotalOrder());
		IHsimu.resetDAG();

		// performing the fast algorithm
		FastAlgorithm algo = new FastAlgorithm(initialLists, scenario);
		outputDAG = algo.map(threadName, abcParams, fastParams, callableDAG,
				callableArchi, alreadyImplanted, true, isDisplaySolutions,
				null, initialLists.getCpnDominant(), callableBlockingNodes,
				initialLists.getCriticalpath(), taskSched);

		// Saving best total order for future display
		outputDAG.getPropertyBean().setValue("bestTotalOrder",
				algo.getBestTotalOrder());

		return outputDAG;

	}
}
