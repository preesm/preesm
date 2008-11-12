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

package org.ietr.preesm.plugin.mapper.timekeeper;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.impl.LooselyTimedAbc;
import org.ietr.preesm.plugin.mapper.graphtransfo.DAGCreator;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;

/**
 * @author mpelcat
 * 
 *         Tester of graph time keeper using kwok examples
 */
public class GraphTimeKeeperTest extends TestCase {

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

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();

	}

	public void testForCreation() {
	}

	public void testGetFinalTimeExample1() {

		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.FINER);

		logger.log(Level.FINEST, "Creating 2 cores archi");
		MultiCoreArchitecture archi = Examples.get2C64Archi();

		logger.log(Level.FINEST, "Creating DAG");
		MapperDAG dag = new DAGCreator().dagexample1(archi);

		IAbc simulator = new LooselyTimedAbc(
				dag, archi);

		logger.log(Level.FINEST, "Evaluating DAG");
		// simulator.implantAllVerticesOnOperator(archi.getMainOperator());
		simulator.implant(dag.getMapperDAGVertex("n1"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_1"),
				true);

		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"))), 2);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n3"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_1"),
				true);

		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"))), 5);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n2"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_1"),
				true);

		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"))), 8);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n7"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_1"),
				true);

		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n6"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_2"),
				true);

		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n5"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_4"),
				true);

		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"))), 8);

		simulator.implant(dag.getMapperDAGVertex("n4"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_3"),
				true);

		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"))), 7);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"))), 8);

		simulator.implant(dag.getMapperDAGVertex("n8"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_4"),
				true);

		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"))), 7);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"))), 12);

		simulator.implant(dag.getMapperDAGVertex("n9"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_4"),
				true);

		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"))), 7);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"))), 19);

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

		logger.log(Level.FINEST, "Creating 2 cores archi");
		MultiCoreArchitecture archi = Examples.get2C64Archi();

		logger.log(Level.FINEST, "Creating DAG");
		MapperDAG dag = new DAGCreator().dagexample2(archi);

		IAbc simulator = new LooselyTimedAbc(
				dag, archi);

		logger.log(Level.FINEST, "Evaluating DAG");
		// simulator.implantAllVerticesOnOperator(archi.getMainOperator());
		simulator.implant(dag.getMapperDAGVertex("n1"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_1"),
				true);

		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"))), 2);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n3"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_1"),
				true);

		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"))), 5);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n2"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_1"),
				true);

		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"))), 8);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n7"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_1"),
				true);

		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n6"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_2"),
				true);

		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"))), 0);

		simulator.implant(dag.getMapperDAGVertex("n5"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_4"),
				true);

		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"))), 0);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"))), 8);

		simulator.implant(dag.getMapperDAGVertex("n4"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_3"),
				true);

		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"))), 7);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"))), 8);

		simulator.implant(dag.getMapperDAGVertex("n8"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_4"),
				true);

		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"))), 7);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"))), 17);

		simulator.implant(dag.getMapperDAGVertex("n9"), (Operator)archi.getComponent(ArchitectureComponentType.operator,"c64x_4"),
				true);

		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_1"))), 12);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_2"))), 13);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_3"))), 7);
		assertEquals((simulator.getFinalTime(archi.getComponent(ArchitectureComponentType.operator,"c64x_4"))), 24);

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
