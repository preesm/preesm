/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import net.sf.dftools.architecture.slam.Design;

import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.plugin.mapper.algo.genetic.Chromosome;
import org.ietr.preesm.plugin.mapper.algo.genetic.StandardGeneticAlgorithm;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.params.AbcParameters;

/**
 * Task scheduling genetic algorithm multithread
 * 
 * @author pmenuet
 */
public class PGeneticAlgoCallable implements Callable<List<Chromosome>> {

	// Simulator chosen
	private AbcParameters abcParams;

	// Architecture chosen
	private Design architecture;

	// thread named by threadName
	private String threadName;

	// DAG given by the main thread
	private List<Chromosome> population;

	// number of tries to do locally probabilistic jump maximum authorized
	private int generationNumber;

	// number of tries to do locally probabilistic jump maximum authorized
	private int populationSize;

	private PreesmScenario scenario;

	/**
	 * Constructor
	 * 
	 * @param architecture
	 * @param generationNumber
	 * @param population
	 * @param populationSize
	 * @param simulatorType
	 * @param threadName
	 */
	public PGeneticAlgoCallable(Design architecture, PreesmScenario scenario,
			int generationNumber, List<Chromosome> population,
			int populationSize, AbcParameters abcParams, String threadName) {
		super();
		this.architecture = architecture;
		this.scenario = scenario;
		this.generationNumber = generationNumber;
		this.population = population;
		this.populationSize = populationSize;
		this.abcParams = abcParams;
		this.threadName = threadName;
	}

	/**
	 * @Override call():
	 * 
	 * @param : void
	 * @return : List<Chromosome>
	 */
	@Override
	public List<Chromosome> call() throws Exception {

		// variables
		List<Chromosome> callableChromosomeList = new ArrayList<Chromosome>();
		List<Chromosome> outputChromosomeList = new ArrayList<Chromosome>();

		// critic section to retrieve the data from the main
		synchronized (callableChromosomeList) {
			callableChromosomeList.addAll(population);
		}

		// transform the chromosome list into a mapperDAG list
		List<MapperDAG> callableMapperDAGList = new ArrayList<MapperDAG>();
		Iterator<Chromosome> iterator = callableChromosomeList.iterator();
		while (iterator.hasNext()) {
			Chromosome chromosome = iterator.next();
			chromosome.updateDAG();
			callableMapperDAGList.add(chromosome.getDag());
		}

		// perform the standard genetic algorithm
		StandardGeneticAlgorithm geneticAlgorithm = new StandardGeneticAlgorithm();
		outputChromosomeList.addAll(geneticAlgorithm.runGeneticAlgo(threadName,
				callableMapperDAGList, architecture, scenario, abcParams,
				populationSize, generationNumber, true));

		return outputChromosomeList;

	}

}
