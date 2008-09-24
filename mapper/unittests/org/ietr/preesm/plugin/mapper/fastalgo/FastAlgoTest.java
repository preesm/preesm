/**
 * 
 */
package org.ietr.preesm.plugin.mapper.fastalgo;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.plugin.abc.ArchitectureSimulatorType;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.infinitehomogeneous.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.abc.looselytimed.LooselyTimedAbc;
import org.ietr.preesm.plugin.mapper.graphtransfo.DAGCreator;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;

/**
 * Tester of fast algorithm using kwok examples
 * 
 * @author pmenuet
 * 
 */
public class FastAlgoTest extends TestCase {

	FastAlgorithm fastAlgorithm = null;

	/**
	 * Constructor
	 */
	public FastAlgoTest(String name) {
		super(name);
	}

	/**
	 * Set up
	 */
	protected void setUp() throws Exception {
		super.setUp();
		fastAlgorithm = new FastAlgorithm();
	}

	/**
	 * destroy the fast algorithm in the tester
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		fastAlgorithm = null;
	}

	/**
	 * Example of the tester
	 */
	public void testExample2() {

		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.FINER);

		logger.log(Level.FINEST, "Creating 4 cores archi");
		MultiCoreArchitecture archi = Examples.get4C64Archi();

		logger.log(Level.FINEST, "Creating DAG");
		MapperDAG dag = new DAGCreator().dagexample2(archi);

		IAbc simu = new InfiniteHomogeneousAbc(
				dag, archi);
		simu.getFinalTime();
		InitialLists initial = new InitialLists();

		logger.log(Level.FINEST, "Evaluating constructInitialList ");
		initial.constructInitialLists(dag, simu);
		simu.resetDAG();

		ListScheduler scheduler = new ListScheduler();
		FastAlgorithm algorithm = new FastAlgorithm();

		dag = algorithm.map("test", ArchitectureSimulatorType.LooselyTimed,
				dag, archi, initial.getCpnDominantList(), initial
						.getBlockingNodesList(), initial
						.getFinalcriticalpathList(), 50, 50, 16, false, false, null);

		IAbc simu2 = new LooselyTimedAbc(
				dag, archi);
		simu2.resetImplementation();
		simu2.setDAG(dag);

		logger.log(Level.FINER, "Displaying dag implanted ");
		scheduler.dagimplanteddisplay(dag, simu2);

		logger.log(Level.FINER, "length : " + simu2.getFinalTime());
		assertEquals(simu2.getFinalTime(), 23);
		simu2.plotImplementation();
	}

}
