package org.ietr.preesm.plugin.mapper.geneticalgo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListSet;

import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.constraints.IScenario;
import org.ietr.preesm.core.constraints.Scenario;
import org.ietr.preesm.core.constraints.Timing;
import org.ietr.preesm.core.constraints.TimingManager;
import org.ietr.preesm.core.workflow.sources.AlgorithmRetriever;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.ArchitectureSimulatorType;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.infinitehomogeneous.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.mapper.fastalgo.InitialLists;
import org.ietr.preesm.plugin.mapper.graphtransfo.DAGCreator;
import org.ietr.preesm.plugin.mapper.graphtransfo.SdfToDagConverter;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.pfastalgo.PFastAlgorithm;
import org.ietr.preesm.plugin.mapper.plot.PlotBestLatency;
import org.ietr.preesm.plugin.mapper.tools.RandomIterator;
import org.jfree.ui.RefineryUtilities;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Main class of genetic algorithms
 * 
 * @author pmenuet
 */
public class StandardGeneticAlgorithm extends Observable {

	/**
	 * FinalTimeComparator : comparator between two different implementation
	 * 
	 * @param : void
	 * @return : void
	 */
	private class FinalTimeComparator implements Comparator<Chromosome> {

		ArchitectureSimulatorType simulatorType = null;

		@Override
		public int compare(Chromosome o1, Chromosome o2) {

			int difference = 0;
			if (o1.isDirty())
				o1.evaluate(simulatorType);
			if (o2.isDirty())
				o2.evaluate(simulatorType);

			difference = o1.getEvaluateCost() - o2.getEvaluateCost();

			if (difference == 0) {
				difference = 1;
			}

			return difference;
		}

		/**
		 * Constructor : FinalTimeComparator
		 * 
		 * @param : ArchitectureSimulatorType, Chromosome
		 * 
		 */
		public FinalTimeComparator(ArchitectureSimulatorType type,
				Chromosome chromosome) {
			super();
			this.simulatorType = type;
		}

	}

	/**
	 * Constructor
	 */
	public StandardGeneticAlgorithm() {
		super();
	}

	/**
	 * convertListDAGtoListChromo : Do the transformation based on a list of DAG
	 * to have a list of chromosome
	 * 
	 * @param List
	 *            <MapperDAG>
	 * @param archi
	 * 
	 * @return List<Chromosome>
	 */
	public List<Chromosome> convertListDAGtoListChromo(List<MapperDAG> list,
			IArchitecture archi) {

		// create the list of chromosome
		List<Chromosome> population3 = new ArrayList<Chromosome>();

		// transform the MapperDAG into chromosome
		Iterator<MapperDAG> iterator = list.listIterator();
		while (iterator.hasNext()) {
			Chromosome chromosome11 = new Chromosome(iterator.next(), archi);
			population3.add(chromosome11);
		}

		return population3;
	}

	/**
	 * 
	 * runGeneticAlgo : Function who is a genetic algorithm with one crossover
	 * operator and one mutation operator
	 * 
	 * @param threadname
	 * @param populationDAG
	 * @param archi
	 * @param type
	 * @param populationSize
	 * @param generationNumber
	 * @param pgeneticalgo
	 * 
	 * @return ConcurrentSkipListSet<Chromosome>
	 */
	public ConcurrentSkipListSet<Chromosome> runGeneticAlgo(String threadname,
			List<MapperDAG> populationDAG, IArchitecture archi,
			ArchitectureSimulatorType type, int populationSize,
			int generationNumber, boolean pgeneticalgo) {

		final PlotBestLatency demo = new PlotBestLatency("Genetic Algorithm");

		// Set data window if necessary
		if (!pgeneticalgo) {

			demo.setSUBPLOT_COUNT(1);
			demo.pack();
			RefineryUtilities.centerFrameOnScreen(demo);
			demo.setVisible(true);
			this.addObserver(demo);
		}

		// Convert MapperDAG in chromosome
		List<Chromosome> population = this.convertListDAGtoListChromo(
				populationDAG, archi);

		// variables
		int i = 0;
		MutationOperator mutationOperator = new MutationOperator();
		CrossOverOperator crossOverOperator = new CrossOverOperator();
		ConcurrentSkipListSet<Chromosome> chromoSet = new ConcurrentSkipListSet<Chromosome>(
				new FinalTimeComparator(type, population.get(0)));
		chromoSet.addAll(population);
		Chromosome chromosome;
		Chromosome chromosome1;

		// Do generationnumber times the algorithm
		while (i < generationNumber) {

			// Button data window
			if (!pgeneticalgo) {
				while (demo.getActionType() == 2)
					;

				// Mode stop
				if (demo.getActionType() == 1)
					return chromoSet;

			}

			// count the generation
			i++;

			// transform ConcurrentSkipListSet into List to use Random Iterator
			List<Chromosome> list = new ArrayList<Chromosome>();
			list.addAll(chromoSet);
			Iterator<Chromosome> iter = new RandomIterator<Chromosome>(list,
					new Random());

			// Create populationSize sons with mutation and populationSize sons
			// with cross over
			for (int j = 0; j < populationSize; j++) {

				// mutation
				chromosome = iter.next();
				chromoSet.add(mutationOperator.transform(chromosome, type));

				// cross over
				chromosome = iter.next();
				chromosome1 = iter.next();
				while (chromosome1.equals(chromosome)) {
					chromosome1 = iter.next();
				}
				chromoSet.add(crossOverOperator.transform(chromosome1,
						chromosome, type));

			}

			// keep the populationSize best chromosomes
			while (chromoSet.size() > populationSize) {
				chromoSet.pollLast();
			}

			// Data Window
			int iBest = chromoSet.first().getEvaluateCost();
			setChanged();
			notifyObservers(iBest);

		}

		return chromoSet;
	}

	/**
	 * Main for example
	 */
	public static void main(String[] args) {

		StandardGeneticAlgorithm geneticAlgorithm = new StandardGeneticAlgorithm();
		DAGCreator dagCreator = new DAGCreator();
		MultiCoreArchitecture archi = Examples.get4C64Archi();

		// Generating random sdf dag
		int nbVertex = 50, minInDegree = 1, maxInDegree = 3, minOutDegree = 1, maxOutDegree = 3;
		SDFGraph graph = AlgorithmRetriever.randomDAG(nbVertex, minInDegree,
				maxInDegree, minOutDegree, maxOutDegree, 100,true);

		// Generating constraints
		IScenario scenario = new Scenario();
		TimingManager tmgr = scenario.getTimingManager();
		for (int i = 1; i <= nbVertex; i++) {
			String name = String.format("Vertex %d", i);
			Timing newt = new Timing(archi.getOperatorDefinition("c64x"), graph
					.getVertex(name), 50);
			tmgr.addTiming(newt);
		}

		// Converting sdf dag in mapper dag
		MapperDAG dag = SdfToDagConverter.convert(graph, archi, scenario,false);
		// MapperDAG dag = dagCreator.dagexample2(archi);

		IAbc simu = new InfiniteHomogeneousAbc(
				dag, archi);
		InitialLists initialLists = new InitialLists();
		initialLists.constructInitialLists(dag, simu);
		simu.resetDAG();

		// Simulator Type
		ArchitectureSimulatorType type = ArchitectureSimulatorType.AccuratelyTimed;

		// create population using Pfast
		PFastAlgorithm algorithm = new PFastAlgorithm();
		List<MapperDAG> population = new ArrayList<MapperDAG>();
		algorithm.map(dag, archi, 2, 2, initialLists, 10, 6, 3, type, true, 4,
				population);

		// Perform the StandardGeneticAlgo
		ConcurrentSkipListSet<Chromosome> skipListSet;
		skipListSet = geneticAlgorithm.runGeneticAlgo("test", population,
				archi, ArchitectureSimulatorType.AccuratelyTimed, 6, 25, false);

		// best solution
		Chromosome chromosome7 = skipListSet.first().clone();
		chromosome7.evaluate(ArchitectureSimulatorType.AccuratelyTimed);
		IAbc simu2 = AbstractAbc
				.getInstance(ArchitectureSimulatorType.AccuratelyTimed,
						chromosome7.getDag(), archi);
		simu2.setDAG(chromosome7.getDag());
		simu2.plotImplementation();

	}
}
