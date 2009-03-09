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

package org.ietr.preesm.plugin.mapper.fastalgo;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.impl.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.abc.impl.LooselyTimedAbc;
import org.ietr.preesm.plugin.mapper.algo.fast.FastAlgorithm;
import org.ietr.preesm.plugin.mapper.algo.list.InitialLists;
import org.ietr.preesm.plugin.mapper.algo.list.ListScheduler;
import org.ietr.preesm.plugin.mapper.edgescheduling.EdgeSchedType;
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
		MultiCoreArchitecture archi = Examples.get2C64Archi();

		logger.log(Level.FINEST, "Creating DAG");
		MapperDAG dag = new DAGCreator().dagexample2(archi);

		IAbc simu = new InfiniteHomogeneousAbc(EdgeSchedType.Simple, 
				dag, archi, false);
		simu.getFinalTime();
		InitialLists initial = new InitialLists();

		logger.log(Level.FINEST, "Evaluating constructInitialList ");
		initial.constructInitialLists(dag, simu);
		simu.resetDAG();

		ListScheduler scheduler = new ListScheduler();
		FastAlgorithm algorithm = new FastAlgorithm();

		dag = algorithm.map("test", AbcType.LooselyTimed, EdgeSchedType.Simple,
				dag, archi, initial.getCpnDominant(), initial
						.getBlockingNodes(), initial
						.getCriticalpath(), 50, 50, 16, false, false, null, false, null);
		AbcType abcType = AbcType.LooselyTimed.setSwitchTask(false);
		IAbc simu2 = new LooselyTimedAbc(EdgeSchedType.Simple, 
				dag, archi, abcType);
		simu2.resetImplementation();
		simu2.setDAG(dag);

		logger.log(Level.FINER, "Displaying dag implanted ");
		scheduler.dagimplanteddisplay(dag, simu2);

		logger.log(Level.FINER, "length : " + simu2.getFinalTime());
		assertEquals(simu2.getFinalTime(), 23);
		simu2.plotImplementation(false);
	}

}
