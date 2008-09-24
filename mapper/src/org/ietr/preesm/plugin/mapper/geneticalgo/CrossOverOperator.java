/**
 * 
 */
package org.ietr.preesm.plugin.mapper.geneticalgo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.ietr.preesm.plugin.abc.ArchitectureSimulatorType;

/**
 * Operator able to perform cross overs between 2 chromosomes
 * 
 * @author pmenuet
 */
public class CrossOverOperator {

	/**
	 * Constructor
	 */
	public CrossOverOperator() {
		super();
	}

	/**
	 * transform : Take two chromosome and mix their implementation to give the
	 * birth to a new chromosome
	 * 
	 * @param : chromosome1
	 * @param : chromosome2
	 * @param : simulatorType
	 * 
	 * @return : Chromosome
	 */
	public Chromosome transform(Chromosome chromosome1, Chromosome chromosome2,
			ArchitectureSimulatorType simulatorType) {

		// variables
		List<Gene> temp1 = new ArrayList<Gene>();
		List<Gene> temp2 = new ArrayList<Gene>();
		List<Gene> temp = new ArrayList<Gene>();
		Gene gene;
		int i = 0;

		// create the son
		Chromosome chromosome = chromosome1.clone();
		chromosome.setDirty(true);

		// retrieve the information about parent one
		temp1.addAll(chromosome.getChromoList());

		// set the random
		Random rand = new Random();
		long seed = System.nanoTime();
		rand.setSeed(seed);

		// retrieve the cloned information about parent two
		Iterator<Gene> iterator = chromosome2.getChromoList().listIterator();
		while (iterator.hasNext()) {
			gene = iterator.next();
			temp2.add(gene.clone());
		}

		// set the vertex index in the ChromoList with the random and verify if
		// this an authorized index
		int index = rand.nextInt(chromosome.getDag().getNumberOfVertices());
		while (index == 0 || index == chromosome.getDag().getNumberOfVertices()) {
			seed = System.nanoTime();
			rand.setSeed(seed);
			index = rand.nextInt(chromosome.getDag().getNumberOfVertices());
		}

		// Take the chosen vertices in the chromosome one
		Iterator<Gene> iterator2 = temp1.listIterator();
		for (i = 0; i < index && iterator2.hasNext(); i++) {
			temp.add(iterator2.next());
		}

		// Take the chosen vertices in the chromosome 2
		Iterator<Gene> iterator3 = temp2.listIterator(i);
		while (iterator3.hasNext()) {
			temp.add(iterator3.next());
		}

		// Set the data in the son
		chromosome.setChromoList(temp);
		chromosome.evaluate(simulatorType);

		return chromosome;
	}

}
