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
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.ietr.preesm.plugin.mapper.params.AbcParameters;

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
			AbcParameters abcParams) {

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
		chromosome.evaluate(abcParams);

		return chromosome;
	}

}
