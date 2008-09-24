/**
 * 
 */
package org.ietr.preesm.plugin.mapper.timekeeper;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.looselytimed.LooselyTimedAbc;
import org.ietr.preesm.plugin.mapper.graphtransfo.DAGCreator;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;

/**
 * @author mpelcat
 * 
 *         Tester of graph time keeper using kwok examples
 */
public class GraphTimeKeeperTest extends TestCase {

	GraphTimeKeeper timekeeper = null;

	/**
	 * @param name
	 */
	public GraphTimeKeeperTest(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		timekeeper = new GraphTimeKeeper();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();

		timekeeper = null;
	}

	public void testForCreation() {
		assertNotNull("timekeeper must be created", timekeeper);
	}

	public void testGetFinalTimeExample1() {

		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.FINER);

		logger.log(Level.FINEST, "Creating 4 cores archi");
		MultiCoreArchitecture archi = Examples.get4C64Archi();

		logger.log(Level.FINEST, "Creating DAG");
		MapperDAG dag = new DAGCreator().dagexample1(archi);

		IAbc simulator = new LooselyTimedAbc(
				dag, archi);

		logger.log(Level.FINEST, "Evaluating DAG");
		// simulator.implantAllVerticesOnOperator(archi.getMainOperator());
		simulator.implant(dag.getMapperDAGVertex("n1"), archi.getOperator("c64x_1"),
				true);

		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_1"))), 2);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_2"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n3"), archi.getOperator("c64x_1"),
				true);

		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_1"))), 5);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_2"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n2"), archi.getOperator("c64x_1"),
				true);

		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_1"))), 8);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_2"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n7"), archi.getOperator("c64x_1"),
				true);

		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_2"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n6"), archi.getOperator("c64x_2"),
				true);

		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n5"), archi.getOperator("c64x_4"),
				true);

		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_4"))), 8);

		simulator.implant(dag.getMapperDAGVertex("n4"), archi.getOperator("c64x_3"),
				true);

		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_3"))), 7);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_4"))), 8);

		simulator.implant(dag.getMapperDAGVertex("n8"), archi.getOperator("c64x_4"),
				true);

		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_3"))), 7);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_4"))), 12);

		simulator.implant(dag.getMapperDAGVertex("n9"), archi.getOperator("c64x_4"),
				true);

		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_3"))), 7);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_4"))), 19);

		// Useless but just to test if it doesn't break anything
		// simulator.setImplantation(dag);

		assertEquals(simulator.getFinalTime(dag.getMapperDAGVertex("n1")), 2);
		assertEquals(simulator.getFinalTime(dag.getMapperDAGVertex("n2")), 8);
		assertEquals(simulator.getFinalTime(dag.getMapperDAGVertex("n3")), 5);
		assertEquals(simulator.getFinalTime(dag.getMapperDAGVertex("n4")), 7);
		assertEquals(simulator.getFinalTime(dag.getMapperDAGVertex("n5")), 8);
		assertEquals(simulator.getFinalTime(dag.getMapperDAGVertex("n6")), 13);
		assertEquals(simulator.getFinalTime(dag.getMapperDAGVertex("n7")), 12);
		assertEquals(simulator.getFinalTime(dag.getMapperDAGVertex("n8")), 12);
		assertEquals(simulator.getFinalTime(dag.getMapperDAGVertex("n9")), 19);

		assertEquals(simulator.getFinalTime(), 19);
	}

	public void testGetFinalTimeExample2() {

		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.FINER);

		logger.log(Level.FINEST, "Creating 4 cores archi");
		MultiCoreArchitecture archi = Examples.get4C64Archi();

		logger.log(Level.FINEST, "Creating DAG");
		MapperDAG dag = new DAGCreator().dagexample2(archi);

		IAbc simulator = new LooselyTimedAbc(
				dag, archi);

		logger.log(Level.FINEST, "Evaluating DAG");
		// simulator.implantAllVerticesOnOperator(archi.getMainOperator());
		simulator.implant(dag.getMapperDAGVertex("n1"), archi.getOperator("c64x_1"),
				true);

		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_1"))), 2);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_2"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n3"), archi.getOperator("c64x_1"),
				true);

		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_1"))), 5);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_2"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n2"), archi.getOperator("c64x_1"),
				true);

		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_1"))), 8);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_2"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n7"), archi.getOperator("c64x_1"),
				true);

		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_2"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n6"), archi.getOperator("c64x_2"),
				true);

		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n5"), archi.getOperator("c64x_4"),
				true);

		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_4"))), 8);

		simulator.implant(dag.getMapperDAGVertex("n4"), archi.getOperator("c64x_3"),
				true);

		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_3"))), 7);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_4"))), 8);

		simulator.implant(dag.getMapperDAGVertex("n8"), archi.getOperator("c64x_4"),
				true);

		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_3"))), 7);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_4"))), 17);

		simulator.implant(dag.getMapperDAGVertex("n9"), archi.getOperator("c64x_4"),
				true);

		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_3"))), 7);
		assertEquals((simulator.getFinalTime(archi.getOperator("c64x_4"))), 24);

		// Useless but just to test if it doesn't break anything
		// simulator.setImplantation(dag);

		assertEquals(simulator.getFinalTime(dag.getMapperDAGVertex("n1")), 2);
		assertEquals(simulator.getFinalTime(dag.getMapperDAGVertex("n2")), 8);
		assertEquals(simulator.getFinalTime(dag.getMapperDAGVertex("n3")), 5);
		assertEquals(simulator.getFinalTime(dag.getMapperDAGVertex("n4")), 7);
		assertEquals(simulator.getFinalTime(dag.getMapperDAGVertex("n5")), 8);
		assertEquals(simulator.getFinalTime(dag.getMapperDAGVertex("n6")), 13);
		assertEquals(simulator.getFinalTime(dag.getMapperDAGVertex("n7")), 12);
		assertEquals(simulator.getFinalTime(dag.getMapperDAGVertex("n8")), 17);
		assertEquals(simulator.getFinalTime(dag.getMapperDAGVertex("n9")), 24);

		assertEquals(simulator.getFinalTime(), 24);
	}

}
