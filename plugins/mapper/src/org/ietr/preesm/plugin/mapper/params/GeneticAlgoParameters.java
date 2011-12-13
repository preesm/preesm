/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

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

package org.ietr.preesm.plugin.mapper.params;

import java.util.Map;
import java.util.logging.Level;

import net.sf.dftools.workflow.tools.WorkflowLogger;

/**
 * Specific parameters of genetic algorithm
 * 
 * @author pmenuet
 * @author mpelcat
 */

public class GeneticAlgoParameters {

	// Number of individuals in the population
	private int populationSize;

	// Number of generation performed by the PGenetic algorithm
	private int generationNumber;

	// Boolean to determine the type of algorithm to make the population
	private boolean pfastused2makepopulation;

	/**
	 * Time in seconds between two FAST probabilistic hops in the critical path
	 */
	private int fastTime = 200;

	/**
	 * Time in seconds spent in one FAST local neighborhood
	 */
	private int fastLocalSearchTime = 10;

	/**
	 * Number of fast iterations to execute before stopping PFast
	 */
	private int fastNumber = 100;

	/**
	 * Constructors
	 * 
	 */

	public GeneticAlgoParameters(Map<String, String> textParameters) {

		this.generationNumber = Integer.valueOf(textParameters
				.get("generationNumber"));
		this.populationSize = Integer.valueOf(textParameters
				.get("populationSize"));
		this.pfastused2makepopulation = Boolean.valueOf(textParameters
				.get("pfastused2makepopulation"));
		if (Integer.valueOf(textParameters.get("fastTime")) != 0) {
			this.fastTime = Integer.valueOf(textParameters.get("fastTime"));
		}
		if (Integer.valueOf(textParameters.get("fastLocalSearchTime")) > 0) {
			this.fastLocalSearchTime = Integer.valueOf(textParameters
					.get("fastLocalSearchTime"));
		}
		if (Integer.valueOf(textParameters.get("fastNumber")) != 0) {
			this.fastNumber = Integer.valueOf(textParameters.get("fastNumber"));
		}

		WorkflowLogger
				.getLogger()
				.log(Level.INFO,
						"The Genetic algo parameters are: generationNumber; populationSize; pfastused2makepopulation=true/false; fastTime in seconds; fastLocalSearchTime in seconds; fastNumber");
	}

	/**
	 * Getters and setters
	 * 
	 */

	public int getPopulationSize() {
		return populationSize;
	}

	public boolean isPfastused2makepopulation() {
		return pfastused2makepopulation;
	}

	public void setPfastused2makepopulation(boolean pfastused2makepopulation) {
		this.pfastused2makepopulation = pfastused2makepopulation;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public int getGenerationNumber() {
		return generationNumber;
	}

	public void setGenerationNumber(int generationNumber) {
		this.generationNumber = generationNumber;
	}

	/**
	 * Returns the Number of fast iterations to execute before stopping PFast
	 */
	public int getFastNumber() {
		return fastNumber;
	}

	/**
	 * Returns the time in seconds for one whole FAST process
	 */
	public int getFastTime() {
		return fastTime;
	}

	/**
	 * Returns the time in seconds spent in one FAST local neighborhood
	 */
	public int getFastLocalSearchTime() {
		return fastLocalSearchTime;
	}

}
