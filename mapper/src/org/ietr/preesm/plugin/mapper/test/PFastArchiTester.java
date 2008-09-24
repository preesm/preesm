/**
 * 
 */
package org.ietr.preesm.plugin.mapper.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.constraints.IScenario;
import org.ietr.preesm.core.constraints.Scenario;
import org.ietr.preesm.core.constraints.Timing;
import org.ietr.preesm.core.constraints.TimingManager;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.core.workflow.sources.AlgorithmRetriever;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.ArchitectureSimulatorType;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.infinitehomogeneous.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.mapper.fastalgo.InitialLists;
import org.ietr.preesm.plugin.mapper.graphtransfo.DAGCreator;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.pfastalgo.PFastAlgorithm;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Tester for the PFAST algorithm
 * 
 * @author pmenuet
 */
public class PFastArchiTester {

	/**
	 * PFastArchiTester Main for tests: example of mapping from random archi
	 */
	public static void main(String[] args) {

		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.FINE);
		PFastArchiTester tester = new PFastArchiTester();
		DAGCreator dagCreator = new DAGCreator();

		// Generating random sdf dag
		int nbVertex = 50, minInDegree = 1, maxInDegree = 3, minOutDegree = 1, maxOutDegree = 3;
		SDFGraph graph = AlgorithmRetriever.randomDAG(nbVertex, minInDegree,
				maxInDegree, minOutDegree, maxOutDegree, 1500,true);

		// Generating archi
		MultiCoreArchitecture archi = Examples.get4C64Archi();

		// Generating constraints
		IScenario scenario = new Scenario();
		TimingManager tmgr = scenario.getTimingManager();
		for (int i = 1; i <= nbVertex; i++) {
			String name = String.format("Vertex %d", i);
			Double taskSize = Math.random() * 1000 + 500;
			Timing newt = new Timing(archi.getOperatorDefinition("c64x"), graph
					.getVertex(name), taskSize.intValue());
			tmgr.addTiming(newt);
		}

		// Converting sdf dag in mapper dag
		// MapperDAG dag = dagCreator.sdf2dag(graph, archi, constraints);
		MapperDAG dag = dagCreator.dagexample2(archi);

		// Looselytimed
		// choixsimu=0;

		// Approximatelytimed
		// choixsimu = 1;

		// Accuratelytimed
		// choixsimu=2;

		// different architectures with the same simulator
		int choixsimu = 1;
		int nboperator = 7;
		tester.testArchipfast(dag, Examples.get2C64Archi(), choixsimu,
				nboperator);
		tester.testArchipfast(dag, Examples.get3C64Archi(), choixsimu,
				nboperator);
		tester.testArchipfast(dag, Examples.get4C64Archi(), choixsimu,
				nboperator);
		tester.testArchipfast(dag, Examples.get4C64_6edmaArchi(), choixsimu,
				nboperator);

		// different simulators with the same architecture
		tester.testArchipfast(dag, Examples.get4C64Archi(), 0, nboperator);
		tester.testArchipfast(dag, Examples.get4C64Archi(), 1, nboperator);
		tester.testArchipfast(dag, Examples.get4C64Archi(), 2, nboperator);

		logger.log(Level.FINE, "Test demo finished");

	}

	/**
	 * Test of a SDFDAG on architecture archi
	 */
	public void testArchipfast(MapperDAG dag, MultiCoreArchitecture archi,
			int choixsimu, int nboperator) {

		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.FINE);

		logger.log(Level.FINEST, "Initialization list scheduling");
		InitialLists initial = new InitialLists();
		IAbc simu = new InfiniteHomogeneousAbc(
				dag, archi);

		logger.log(Level.FINEST, "Evaluating constructInitialList ");
		initial.constructInitialLists(dag, simu);

		logger.log(Level.FINEST, "Displaying Cpndominantlist ");
		initial.orderlistdisplay(initial.getCpnDominantList());

		logger.log(Level.FINEST, "Displaying blockingNodes ");
		initial.orderlistdisplay(initial.getBlockingNodesList());

		logger.log(Level.FINEST, "Displaying fcp ");
		initial.orderlistdisplay(initial.getFinalcriticalpathList());

		simu.resetDAG();

		// perform the PFast Algorithm
		logger.log(Level.FINEST, "Evaluating Pfast algo");
		PFastAlgorithm pfastAlgorithm = new PFastAlgorithm();

		ArchitectureSimulatorType simulatorType = null;
		if (choixsimu == 0)
			simulatorType = ArchitectureSimulatorType.LooselyTimed;
		if (choixsimu == 1)
			simulatorType = ArchitectureSimulatorType.ApproximatelyTimed;
		if (choixsimu == 2)
			simulatorType = ArchitectureSimulatorType.AccuratelyTimed;

		dag = pfastAlgorithm.map(dag, archi, nboperator, 3, initial, 20, 20, 6,
				simulatorType, false, 10, null);

		// display the found solution
		IAbc simu2 = AbstractAbc
				.getInstance(simulatorType, dag, archi);
		simu2.resetImplementation();
		simu2.setDAG(dag);
		int finale = simu2.getFinalTime();
		logger.log(Level.FINE, "FinalSPlength " + finale);
		simu2.plotImplementation();
		simu2.resetImplementation();

		logger.log(Level.FINE, "Test finished");
	}
}
