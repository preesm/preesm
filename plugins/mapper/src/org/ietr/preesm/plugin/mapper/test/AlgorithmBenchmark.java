package org.ietr.preesm.plugin.mapper.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.preesm.core.architecture.ArchitectureInterface;
import org.ietr.preesm.core.architecture.ArchitectureInterfaceDefinition;
import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.Medium;
import org.ietr.preesm.core.architecture.MediumDefinition;
import org.ietr.preesm.core.architecture.MediumProperty;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.ietr.preesm.core.constraints.IScenario;
import org.ietr.preesm.core.constraints.Scenario;
import org.ietr.preesm.core.constraints.Timing;
import org.ietr.preesm.core.constraints.TimingManager;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.core.workflow.sources.AlgorithmRetriever;
import org.ietr.preesm.plugin.abc.ArchitectureSimulatorType;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.infinitehomogeneous.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.mapper.fastalgo.FastAlgorithm;
import org.ietr.preesm.plugin.mapper.fastalgo.InitialLists;
import org.ietr.preesm.plugin.mapper.graphtransfo.DAGCreator;
import org.ietr.preesm.plugin.mapper.graphtransfo.SdfToDagConverter;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Comparing algorithms
 * 
 * @author mpelcat
 */

public class AlgorithmBenchmark {

	/**
	 * Generates an archi with 4 C64x and an EDMA at 1 cycle/byte
	 * 
	 */
	public static MultiCoreArchitecture get4C64Archi(){
		MultiCoreArchitecture archi = new MultiCoreArchitecture("4C64Archi");
		
		OperatorDefinition opdef = new OperatorDefinition("C64x");
		MediumDefinition edma = new MediumDefinition("edma");
		
		//speed of 1 cycle/byte; overhead of 100 cycles; reception time of 0 cycle
		edma.setMediumProperty(new MediumProperty(1,100,0));
		
		ArchitectureInterfaceDefinition intfdef = new ArchitectureInterfaceDefinition(edma,ArchitectureInterfaceDefinition.INFINITE);

		Operator op1 = archi.addOperator(new Operator("C64x_1", opdef), true);
		op1.addInterface(new ArchitectureInterface(intfdef, op1));
		Operator op2 = archi.addOperator(new Operator("C64x_2", opdef), false);
		op2.addInterface(new ArchitectureInterface(intfdef, op2));
		Operator op3 = archi.addOperator(new Operator("C64x_3", opdef), false);
		op3.addInterface(new ArchitectureInterface(intfdef, op3));
		Operator op4 = archi.addOperator(new Operator("C64x_4", opdef), false);
		op4.addInterface(new ArchitectureInterface(intfdef, op4));

		Medium m = new Medium("edma", edma, intfdef);
		archi.addMedium(m,op1,op2,true);
		
		archi.connect(m, op3);
		archi.connect(m, op4);
		
		return archi;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		AlgorithmBenchmark bench =  new AlgorithmBenchmark();

		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.FINE);

		DAGCreator dagCreator = new DAGCreator();

		// Generating random sdf dag
		int nbVertex = 250, minInDegree = 1, maxInDegree = 3, minOutDegree = 1, maxOutDegree = 3;
		SDFGraph graph = AlgorithmRetriever.randomDAG(nbVertex, minInDegree,
				maxInDegree, minOutDegree, maxOutDegree, 1000,false);
		

		// Generating archi
		MultiCoreArchitecture archi = get4C64Archi();

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
		MapperDAG dag = SdfToDagConverter.convert(graph, archi, scenario,false);
		
		BenchmarkWriter writer = new BenchmarkWriter(BenchmarkWriter.absoluteLatency);

		// Looselytimed
		int choixsimu=0;
		writer.init();
		bench.testFAST(dag, choixsimu, writer);

		// Approximatelytimed
		choixsimu = 1;
		writer.init();
		bench.testFAST(dag, choixsimu, writer);

		// Accuratelytimed
		choixsimu=2;
		writer.init();
		bench.testFAST(dag, choixsimu, writer);
		
	}

	public void testFAST(MapperDAG dag, int choixsimu, BenchmarkWriter writer){


		writer.println("testFAST" + choixsimu);

		testFASTArchi(dag, Examples.get4C64Archi(), choixsimu, writer);
	}

	public void testGenetic(){
		
	}
	
	/**
	 * Test of a SDFDAG on architecture archi
	 * 
	 */
	public void testFASTArchi(MapperDAG dag, MultiCoreArchitecture archi,
			int choixsimu, BenchmarkWriter writer) {

		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.FINE);

		IAbc simu = new InfiniteHomogeneousAbc(
				dag, archi);
		logger.log(Level.FINEST, "Initialization list scheduling");

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

		// perform tester fast algorithm
		logger.log(Level.FINEST, "Evaluating fast algo");
		FastAlgorithm algorithm = new FastAlgorithm();

		// chosen simulator
		ArchitectureSimulatorType simulatorType = null;
		if (choixsimu == 0)
			simulatorType = ArchitectureSimulatorType.LooselyTimed;
		if (choixsimu == 1)
			simulatorType = ArchitectureSimulatorType.ApproximatelyTimed;
		if (choixsimu == 2)
			simulatorType = ArchitectureSimulatorType.AccuratelyTimed;

		

		dag = algorithm.map("", simulatorType, dag, archi, initial
				.getCpnDominantList(), initial.getBlockingNodesList(), initial
				.getFinalcriticalpathList(), 0, 0, 0, false, false, writer);

	}
}
