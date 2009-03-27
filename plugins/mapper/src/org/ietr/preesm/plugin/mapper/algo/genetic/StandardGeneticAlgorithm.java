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

package org.ietr.preesm.plugin.mapper.algo.genetic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListSet;

import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.plot.BestLatencyPlotter;
import org.ietr.preesm.plugin.mapper.plot.bestlatency.BestLatencyEditor;
import org.ietr.preesm.plugin.mapper.tools.RandomIterator;

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

		private AbcType simulatorType = null;
		private EdgeSchedType edgeSchedType = null;

		@Override
		public int compare(Chromosome o1, Chromosome o2) {

			long difference = 0;
			if (o1.isDirty())
				o1.evaluate(simulatorType, edgeSchedType);
			if (o2.isDirty())
				o2.evaluate(simulatorType, edgeSchedType);

			difference = o1.getEvaluateCost() - o2.getEvaluateCost();

			if (difference == 0) {
				difference = 1;
			}

			return (int)difference;
		}

		/**
		 * Constructor : FinalTimeComparator
		 * 
		 * @param : ArchitectureSimulatorType, Chromosome
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
			MultiCoreArchitecture archi, IScenario scenario) {

		// create the list of chromosome
		List<Chromosome> population3 = new ArrayList<Chromosome>();

		// transform the MapperDAG into chromosome
		Iterator<MapperDAG> iterator = list.listIterator();
		while (iterator.hasNext()) {
			Chromosome chromosome11 = new Chromosome(iterator.next(), archi, scenario);
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
			List<MapperDAG> populationDAG, MultiCoreArchitecture archi, IScenario scenario,
			AbcType type, EdgeSchedType edgeSchedType, int populationSize,
			int generationNumber, boolean pgeneticalgo) {

		final BestLatencyPlotter demo = new BestLatencyPlotter("Genetic Algorithm", null);

		// Set data window if necessary
		if (!pgeneticalgo) {

			demo.setSUBPLOT_COUNT(1);
			//demo.display();
			BestLatencyEditor.createEditor(demo);
			this.addObserver(demo);
		}

		// Convert MapperDAG in chromosome
		List<Chromosome> population = this.convertListDAGtoListChromo(
				populationDAG, archi, scenario);

		// variables
		int i = 0;
		MutationOperator mutationOperator = new MutationOperator();
		CrossOverOperator crossOverOperator = new CrossOverOperator();
		ConcurrentSkipListSet<Chromosome> chromoSet = new ConcurrentSkipListSet<Chromosome>(
				new FinalTimeComparator(type,edgeSchedType, population.get(0)));
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
				chromoSet.add(mutationOperator.transform(chromosome, type, edgeSchedType));

				// cross over
				chromosome = iter.next();
				chromosome1 = iter.next();
				while (chromosome1.equals(chromosome)) {
					chromosome1 = iter.next();
				}
				chromoSet.add(crossOverOperator.transform(chromosome1,
						chromosome, type, edgeSchedType));

			}

			// keep the populationSize best chromosomes
			while (chromoSet.size() > populationSize) {
				chromoSet.pollLast();
			}

			// Data Window
			long iBest = chromoSet.first().getEvaluateCost();
			setChanged();
			notifyObservers(iBest);

		}

		return chromoSet;
	}
}
