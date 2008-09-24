/**
 * 
 */
package org.ietr.preesm.plugin.mapper.pgeneticalgo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.plugin.abc.ArchitectureSimulatorType;
import org.ietr.preesm.plugin.mapper.geneticalgo.Chromosome;
import org.ietr.preesm.plugin.mapper.geneticalgo.StandardGeneticAlgorithm;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;

/**
 * Task scheduling genetic algorithm multithread
 * 
 * @author pmenuet
 */
public class PGeneticAlgoCallable implements Callable<List<Chromosome>> {

	// Simulator chosen
	private ArchitectureSimulatorType simulatorType;

	// Architecture chosen
	private IArchitecture architecture;

	// thread named by threadName
	private String threadName;

	// DAG given by the main thread
	private List<Chromosome> population;

	// number of tries to do locally probabilistic jump maximum authorized
	private int generationNumber;

	// number of tries to do locally probabilistic jump maximum authorized
	private int populationSize;

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
	public PGeneticAlgoCallable(IArchitecture architecture,
			int generationNumber, List<Chromosome> population,
			int populationSize, ArchitectureSimulatorType simulatorType,
			String threadName) {
		super();
		this.architecture = architecture;
		this.generationNumber = generationNumber;
		this.population = population;
		this.populationSize = populationSize;
		this.simulatorType = simulatorType;
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
				callableMapperDAGList, architecture, simulatorType,
				populationSize, generationNumber, true));

		return outputChromosomeList;

	}

}
