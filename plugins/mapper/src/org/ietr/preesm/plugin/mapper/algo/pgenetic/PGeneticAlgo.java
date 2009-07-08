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

package org.ietr.preesm.plugin.mapper.algo.pgenetic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.plugin.mapper.algo.genetic.Chromosome;
import org.ietr.preesm.plugin.mapper.algo.genetic.StandardGeneticAlgorithm;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.plot.BestLatencyPlotter;
import org.ietr.preesm.plugin.mapper.plot.bestlatency.BestLatencyEditor;

/**
 * Task scheduling genetic algorithm
 * 
 * @author pmenuet
 */
public class PGeneticAlgo extends Observable {

	/**
	 * FinalTimeComparator : comparator between two different implementation
	 * 
	 * @param : void
	 * @return : void
	 */
	private class FinalTimeComparator implements Comparator<Chromosome> {

		private AbcType simulatorType = null;
		private EdgeSchedType edgeSchedType = null;

		@Override
		public int compare(Chromosome o1, Chromosome o2) {

			long difference = 0;
			if (o1.isDirty())
				o1.evaluate(simulatorType,edgeSchedType);
			if (o2.isDirty())
				o2.evaluate(simulatorType,edgeSchedType);

			difference = o1.getEvaluateCost() - o2.getEvaluateCost();

			if (difference == 0) {
				difference = 1;
			}

			return (int)difference;
		}

		/**
		 * Constructor : FinalTimeComparator
		 * 
		 * @param : ArchitectureSimulatorType, Chromosome, IArchitecture
		 * 
		 */
		public FinalTimeComparator(AbcType type, EdgeSchedType edgeSchedType,
				Chromosome chromosome) {
			super();
			this.simulatorType = type;
			this.edgeSchedType = edgeSchedType;
		}

	}

	/**
	 * Constructor
	 */
	public PGeneticAlgo() {
		super();
	}

	/**
	 * map = perform the PGenetic Algorithm (it's the main)
	 * 
	 * @param populationDAG
	 * @param archi
	 * @param type
	 * @param populationSize
	 * @param generationNumber
	 * @param processorNumber
	 * 
	 * @return List<Chromosome>
	 */
	public List<Chromosome> map(List<MapperDAG> populationDAG,
			MultiCoreArchitecture archi, IScenario scenario, AbcType type,EdgeSchedType edgeSchedType,
			int populationSize, int generationNumber, int processorNumber) {

		// variables
		processorNumber = processorNumber - 1;

		// Convert list of MapperDAG into a List of Chromosomes
		StandardGeneticAlgorithm algorithm = new StandardGeneticAlgorithm();
		List<Chromosome> population = algorithm.convertListDAGtoListChromo(
				populationDAG, archi,scenario);

		// List which allow to save all the population (of all Thread)
		List<List<Chromosome>> listMap = new ArrayList<List<Chromosome>>();

		// best Population
		ConcurrentSkipListSet<Chromosome> result = new ConcurrentSkipListSet<Chromosome>(
				new FinalTimeComparator(type,edgeSchedType, population.get(0)));
		Logger logger = PreesmLogger.getLogger();

		// if only one processor is used we must do the Standard Genetic
		// Algorithm
		if (processorNumber == 0) {
			StandardGeneticAlgorithm geneticAlgorithm = new StandardGeneticAlgorithm();
			result = geneticAlgorithm.runGeneticAlgo("genetic algorithm",
					populationDAG, archi,scenario, type,edgeSchedType, populationSize,
					generationNumber, false);
			List<Chromosome> result2 = new ArrayList<Chromosome>();
			result2.addAll(result);
			return result2;
		}

		// Set Data window
		final BestLatencyPlotter demo = new BestLatencyPlotter(
				"Parallel genetic Algorithm", null);
		demo.setSUBPLOT_COUNT(1);
		//demo.display();
		BestLatencyEditor.createEditor(demo);
		this.addObserver(demo);

		// set best population
		result.addAll(population);

		// Copy the initial population processorNumber times (number of thread
		// we will create)
		for (int i = 0; i < processorNumber; i++) {
			List<Chromosome> list = new ArrayList<Chromosome>();
			Iterator<Chromosome> iterator = population.listIterator();
			while (iterator.hasNext()) {
				list.add(iterator.next().clone());
			}
			listMap.add(list);
		}

		// simple verification and variables
		if (result.first().isDirty())
			result.first().evaluate(type, edgeSchedType);
		long iBest = result.first().getEvaluateCost();
		setChanged();
		notifyObservers(iBest);
		int i = 0;
		int k = 0;
		int totalgeneration = 0;
		Iterator<List<Chromosome>> itefinal = listMap.listIterator();

		// Perform the PGenetic Algorithm
		for (int j = 2; totalgeneration < generationNumber; j++) {

			itefinal = listMap.listIterator();
			logger.log(Level.FINE, "regroup number " + j);

			// Mode Pause
			while (demo.getActionType() == 2)
				;

			// Mode stop
			if (demo.getActionType() == 1) {
				List<Chromosome> result2 = new ArrayList<Chromosome>();
				result2.addAll(result);
				return result2;
			}

			// determine the number of generation we need for the threads this
			// time
			int generationNumberTemp = Math.max(((Double) Math
					.ceil(((double) generationNumber)// / ((double) j)
							/ ((double) processorNumber))).intValue(), 1);

			// create ExecutorService to manage threads
			Set<FutureTask<List<Chromosome>>> futureTasks = new HashSet<FutureTask<List<Chromosome>>>();
			ExecutorService es = Executors.newFixedThreadPool(processorNumber);

			// Create the threads with the right parameters
			Iterator<List<Chromosome>> iter = listMap.listIterator();
			for (i = k; i < processorNumber + k; i++) {
				String name = String.format("thread%d", i);

				// step 9/11
				PGeneticAlgoCallable thread = new PGeneticAlgoCallable(archi,scenario,
						generationNumberTemp, iter.next(), populationSize,
						type, name);
				// Add the thread in the pool for the executor
				FutureTask<List<Chromosome>> task = new FutureTask<List<Chromosome>>(
						thread);
				futureTasks.add(task);
				es.submit(task);
			}
			k = i;

			// The thread are launch
			try {
				Iterator<FutureTask<List<Chromosome>>> it = futureTasks
						.iterator();

				// retrieve all the results
				while (it.hasNext()) {

					FutureTask<List<Chromosome>> task = it.next();

					List<Chromosome> temp = task.get();

					itefinal.next().addAll(temp);
					result.addAll(temp);
				}

				// reduce the best population to keep the best individuals
				while (result.size() > populationSize) {

					result.pollLast();
				}

				// step 12
				iBest = result.first().getEvaluateCost();
				setChanged();
				notifyObservers(iBest);
				es.shutdown();

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

			// distribute the best solution to all the population
			Iterator<List<Chromosome>> bestDistributed = listMap.listIterator();
			while (bestDistributed.hasNext()) {
				bestDistributed.next().add(result.first().clone());
			}

			totalgeneration += generationNumberTemp;
		}

		List<Chromosome> result2 = new ArrayList<Chromosome>();
		result2.addAll(result);
		return result2;
	}

}
