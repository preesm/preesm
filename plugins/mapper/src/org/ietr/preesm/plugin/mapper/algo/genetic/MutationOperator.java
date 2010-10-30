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

package org.ietr.preesm.plugin.mapper.algo.genetic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.plugin.mapper.params.AbcParameters;
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
			AbcParameters abcParams) {

		// Construct the son
		Chromosome chromosome = chromosome1.clone();
		chromosome.setDirty(true);

		// chose the gene randomly
		Iterator<Gene> iter = new RandomIterator<Gene>(chromosome
				.getChromoList(), new Random());
		Gene currentGene = iter.next();

		// retrieve the operators which can execute the vertex
		List<ArchitectureComponent> list = new ArrayList<ArchitectureComponent>();
		list.addAll(chromosome.getArchi().getComponents(ArchitectureComponentType.operator));

		// chose one operator randomly
		Iterator<ArchitectureComponent> iterator = new RandomIterator<ArchitectureComponent>(list,
				new Random());
		Operator operator = (Operator)iterator.next();
		while (operator.getName().equals(currentGene.getOperatorId())) {
			operator = (Operator)iterator.next();
		}

		// set the change in the gene
		currentGene.setOperatorId(operator.getName());
		chromosome.evaluate(abcParams);

		return chromosome;

	}

}
