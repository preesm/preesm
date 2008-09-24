package org.ietr.preesm.plugin.mapper.test;

import java.util.logging.Level;

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
import org.ietr.preesm.plugin.mapper.fastalgo.InitialLists;
import org.ietr.preesm.plugin.mapper.fastalgo.ListScheduler;
import org.ietr.preesm.plugin.mapper.graphtransfo.DAGCreator;
import org.ietr.preesm.plugin.mapper.graphtransfo.SdfToDagConverter;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Tester for the list scheduling algorithm
 * 
 * @author pmenuet
 */
public class ListSchedulingArchiTester {

	/**
	 * ListSchedulingArchiTester Main for tests: example of mapping from random
	 * archi
	 */
	public static void main(String[] args) {

		PreesmLogger.getLogger().setLevel(Level.INFO);

		ListSchedulingArchiTester tester = new ListSchedulingArchiTester();

		DAGCreator dagCreator = new DAGCreator();

		// Generating random sdf dag
		int nbVertex = 50, minInDegree = 1, maxInDegree = 3, minOutDegree = 1, maxOutDegree = 3;
		SDFGraph graph = AlgorithmRetriever.randomDAG(nbVertex, minInDegree,
				maxInDegree, minOutDegree, maxOutDegree, 300,true);

		// Generating archi
		MultiCoreArchitecture archi = Examples.get4C64Archi();

		// Generating constraints
		IScenario scenario = new Scenario();
		TimingManager tmgr = scenario.getTimingManager();
		for (int i = 1; i <= nbVertex; i++) {
			String name = String.format("Vertex %d", i);
			Double taskSize = Math.random() * 1000;
			Timing newt = new Timing(archi.getOperatorDefinition("c64x"), graph
					.getVertex(name), taskSize.intValue());
			tmgr.addTiming(newt);
		}

		// Converting sdf dag in mapper dag
		MapperDAG dag = SdfToDagConverter.convert(graph, archi, scenario,false);

		// perform list scheduling with different architecture but the same
		// simulator
		int choixsimu = 1;
		tester.testArchi(dag, Examples.get1C64Archi(), choixsimu);
		tester.testArchi(dag, Examples.get2C64Archi(), choixsimu);
		tester.testArchi(dag, Examples.get3C64Archi(), choixsimu);
		tester.testArchi(dag, Examples.get4C64Archi(), choixsimu);
		tester.testArchi(dag, Examples.get4C64_6edmaArchi(), choixsimu);

	}

	/**
	 * Test of a SDFDAG on architecture archi
	 * 
	 * @param dag
	 * @param archi
	 * @param choixsimu
	 */
	public void testArchi(MapperDAG dag, MultiCoreArchitecture archi,
			int choixsimu) {

		PreesmLogger.getLogger().log(Level.FINEST,
				"List scheduling initialization");
		IAbc simu = AbstractAbc
				.getInstance(ArchitectureSimulatorType.InfiniteHomogeneous,
						dag, archi);
		InitialLists initial = new InitialLists();

		PreesmLogger.getLogger().log(Level.FINEST,
				"Evaluating constructInitialList ");
		initial.constructInitialLists(dag, simu);

		PreesmLogger.getLogger().log(Level.FINEST,
				"Displaying Cpndominantlist ");
		initial.orderlistdisplay(initial.getCpnDominantList());

		PreesmLogger.getLogger().log(Level.FINEST, "Displaying blockingNodes ");
		initial.orderlistdisplay(initial.getBlockingNodesList());

		PreesmLogger.getLogger().log(Level.FINEST, "Displaying fcp ");
		initial.orderlistdisplay(initial.getFinalcriticalpathList());

		simu.resetDAG();

		// create the list scheduler
		ListScheduler scheduler = new ListScheduler();
		ArchitectureSimulatorType simulatorType = null;
		if (choixsimu == 0)
			simulatorType = ArchitectureSimulatorType.LooselyTimed;
		if (choixsimu == 1)
			simulatorType = ArchitectureSimulatorType.ApproximatelyTimed;
		if (choixsimu == 2)
			simulatorType = ArchitectureSimulatorType.AccuratelyTimed;

		IAbc simu2 = AbstractAbc
				.getInstance(simulatorType, dag, archi);
		PreesmLogger.getLogger().log(Level.FINEST,
				"Evaluating List scheduling ");
		scheduler.schedule(dag, initial.getCpnDominantList(), initial
				.getBlockingNodesList(), initial.getFinalcriticalpathList(),
				simu2, null, null);

		PreesmLogger.getLogger().log(Level.FINEST, "Displaying dag implanted ");
		scheduler.dagimplanteddisplay(dag, simu2);

		int finalTime = simu2.getFinalTime();
		PreesmLogger.getLogger().log(
				Level.FINER,
				"DAG Final Time on " + archi.getNumberOfOperators()
						+ " proc = " + finalTime);

		simu2.plotImplementation();
		simu2.resetDAG();

		PreesmLogger.getLogger().log(Level.FINEST, "Test finished");
	}
}
