package org.ietr.preesm.plugin.mapper.pfastalgo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.plugin.abc.ArchitectureSimulatorType;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.infinitehomogeneous.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.mapper.fastalgo.FastAlgorithm;
import org.ietr.preesm.plugin.mapper.fastalgo.InitialLists;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * One thread of Task scheduling FAST algorithm multithread 
 * 
 * @author pmenuet
 */
class PFastCallable implements Callable<MapperDAG> {

	// Simulator chosen
	private ArchitectureSimulatorType simulatorType;

	// thread named by threadName
	private String threadName;

	// DAG given by the main thread
	private MapperDAG inputDAG;

	// Architecture used to implement the DAG
	private IArchitecture inputArchi;

	// Set of the nodes upon which we used fast algorithm in the thread
	private Set<String> blockingNodeNames;

	// number of tries to do locally probabilistic jump maximum authorized
	private int margIn;

	// number of iteration to do the fast algorithm used here to determine the
	// number of probabilistic jump we need before the comparison in the main in
	// the algorithm
	private int maxCount;

	// number of search steps in an iteration
	private int maxStep;

	// Variables to know if we have to do the initial scheduling or not
	private boolean alreadyImplanted;

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
			IArchitecture inputArchi, Set<String> blockingNodeNames,
			int maxCount, int maxStep, int margIn, boolean alreadyImplanted,
			ArchitectureSimulatorType simulatorType) {
		threadName = name;
		this.inputDAG = inputDAG;
		this.inputArchi = inputArchi;
		this.blockingNodeNames = blockingNodeNames;
		this.maxCount = maxCount;
		this.maxStep = maxStep;
		this.margIn = margIn;
		this.alreadyImplanted = alreadyImplanted;
		this.simulatorType = simulatorType;
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
		IArchitecture callableArchi;
		List<MapperDAGVertex> callableBlockingNodes = new ArrayList<MapperDAGVertex>();

		// Critic section where the data from the main are copied for the thread
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
		IAbc IHsimu = new InfiniteHomogeneousAbc(
				callableDAG.clone(), callableArchi);
		InitialLists initialLists = new InitialLists();
		initialLists.constructInitialLists(callableDAG, IHsimu);
		IHsimu.resetDAG();

		// performing the fast algorithm
		FastAlgorithm algo = new FastAlgorithm();
		outputDAG = algo.map(threadName, simulatorType, callableDAG,
				callableArchi, initialLists.getCpnDominantList(),
				callableBlockingNodes, initialLists.getFinalcriticalpathList(),
				maxCount, maxStep, margIn, alreadyImplanted, true, null);

		return outputDAG;

	}
}
