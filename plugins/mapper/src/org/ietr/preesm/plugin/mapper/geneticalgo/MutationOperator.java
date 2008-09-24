/**
 * 
 */
package org.ietr.preesm.plugin.mapper.geneticalgo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.plugin.abc.ArchitectureSimulatorType;
import org.ietr.preesm.plugin.mapper.tools.RandomIterator;

/**
 * Operator able to mutate a chromosome
 * 
 * @author pmenuet
 */
public class MutationOperator {

	/**
	 * Constructor
	 */
	public MutationOperator() {
		super();
	}

	/**
	 * transform : Mute the chromosome = change the implementation of one vertex
	 * 
	 * @param chromosome1
	 * @param simulatorType
	 * 
	 * @return Chromosome
	 */
	public Chromosome transform(Chromosome chromosome1,
			ArchitectureSimulatorType simulatorType) {

		// Construct the son
		Chromosome chromosome = chromosome1.clone();
		chromosome.setDirty(true);

		// chose the gene randomly
		Iterator<Gene> iter = new RandomIterator<Gene>(chromosome
				.getChromoList(), new Random());
		Gene currentGene = iter.next();

		// retrieve the operators which can execute the vertex
		List<Operator> list = new ArrayList<Operator>();
		list.addAll(chromosome.getArchi().getOperators());

		// chose one operator randomly
		Iterator<Operator> iterator = new RandomIterator<Operator>(list,
				new Random());
		Operator operator = iterator.next();
		while (operator.getName().equals(currentGene.getOperatorId())) {
			operator = iterator.next();
		}

		// set the change in the gene
		currentGene.setOperatorId(operator.getName());
		chromosome.evaluate(simulatorType);

		return chromosome;

	}

}
