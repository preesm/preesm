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

package org.ietr.preesm.plugin.mapper.test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.core.scenario.TimingManager;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.core.workflow.sources.AlgorithmRetriever;
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.impl.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.mapper.fastalgo.InitialLists;
import org.ietr.preesm.plugin.mapper.geneticalgo.Chromosome;
import org.ietr.preesm.plugin.mapper.graphtransfo.SdfToDagConverter;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.pfastalgo.PFastAlgorithm;
import org.ietr.preesm.plugin.mapper.pgeneticalgo.PGeneticAlgo;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Tester for the Pgenetic algorithm
 * 
 * @author pmenuet
 */
public class PGeneticAlgoTester {

	/**
	 * Main for tests: example of mapping from random archi
	 */
	public static void main(String[] args) {

		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.FINE);
		PGeneticAlgoTester tester = new PGeneticAlgoTester();

		// Generating random sdf dag
		int nbVertex = 100, minInDegree = 1, maxInDegree = 3, minOutDegree = 1, maxOutDegree = 3;
		SDFGraph graph = AlgorithmRetriever.randomDAG(nbVertex, minInDegree,
				maxInDegree, minOutDegree, maxOutDegree, 1500,true);

		// Generating archi
		MultiCoreArchitecture archi = Examples.get2C64Archi();

		// Generating constraints
		IScenario scenario = new Scenario();

		TimingManager tmgr = scenario.getTimingManager();

		for (int i = 1; i <= nbVertex; i++) {
			String name = String.format("Vertex %d", i);
			Double taskSize = Math.random() * 1000 + 500;
			Timing newt = new Timing((OperatorDefinition)archi.getComponentDefinition(ArchitectureComponentType.operator,"c64x"), graph
					.getVertex(name), taskSize.intValue());
			tmgr.addTiming(newt);
		}

		// Converting sdf dag in mapper dag
		MapperDAG dag = SdfToDagConverter.convert(graph, archi, scenario,false);

		// Looselytimed
		// choixsimu=0;

		// Approximatelytimed
		// choixsimu = 1;

		// Accuratelytimed
		// choixsimu=2;

		int choixsimu = 1;
		int nboperator = 7;
		tester.testArchi(dag, Examples.get2C64Archi(), choixsimu, nboperator);
		//tester.testArchi(dag, Examples.get3C64Archi(), choixsimu, nboperator);
		//tester.testArchi(dag, Examples.get4C64Archi(), choixsimu, nboperator);
		//tester.testArchi(dag, Examples.get4C64_6edmaArchi(), choixsimu,
		//		nboperator);
		//tester.testArchi(dag, Examples.get2FaradayArchi(), choixsimu,
		//		nboperator);

		// All types
		//tester.testArchi(dag, Examples.get2FaradayArchi(), 0, nboperator);
		//tester.testArchi(dag, Examples.get2FaradayArchi(), 1, nboperator);
		//tester.testArchi(dag, Examples.get2FaradayArchi(), 2, nboperator);

		logger.log(Level.FINE, "Test demo finished");

	}

	/**
	 * Test of a SDFDAG on architecture archi
	 */
	public void testArchi(MapperDAG dag, MultiCoreArchitecture archi,
			int choixsimu, int nboperator) {

		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.FINE);

		IAbc simu = new InfiniteHomogeneousAbc(
				dag, archi);

		logger.log(Level.FINEST, "Evaluating DAG");

		InitialLists initial = new InitialLists();

		logger.log(Level.FINEST, "Evaluating constructInitialList ");
		initial.constructInitialLists(dag, simu);

		logger.log(Level.FINEST, "Displaying Cpndominantlist ");
		initial.orderlistdisplay(initial.getCpnDominantList());

		logger.log(Level.FINEST, "Displaying blockingNodes ");
		initial.orderlistdisplay(initial.getBlockingNodesList());

		logger.log(Level.FINEST, "Displaying fcp ");
		initial.orderlistdisplay(initial.getFinalcriticalpathList());

		simu.resetDAG();

		logger.log(Level.FINEST, "Constructing population");
		PFastAlgorithm algorithm = new PFastAlgorithm();

		AbcType simulatorType = null;
		if (choixsimu == 0)
			simulatorType = AbcType.LooselyTimed;
		if (choixsimu == 1)
			simulatorType = AbcType.ApproximatelyTimed;
		if (choixsimu == 2)
			simulatorType = AbcType.AccuratelyTimed;

		List<MapperDAG> populist = new ArrayList<MapperDAG>();
		algorithm.map(dag, archi, nboperator, 10, initial, 20, 10, 4,
				simulatorType, true, 10, populist);

		PGeneticAlgo geneticAlgorithm = new PGeneticAlgo();

		List<Chromosome> list = geneticAlgorithm.map(populist, archi,
				simulatorType, 10, 25, nboperator);

		IAbc simu2 = AbstractAbc
				.getInstance(simulatorType, list.get(0).getDag(), archi);
		simu2.resetImplementation();
		simu2.setDAG(list.get(0).getDag());
		simu2.plotImplementation(false);
		simu2.resetImplementation();

		logger.log(Level.FINE, "Test finished");
	}
}
