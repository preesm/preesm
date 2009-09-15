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

package org.ietr.preesm.plugin.mapper.params;

import java.util.logging.Level;

import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.core.tools.PreesmLogger;

/**
 * Specific parameters of genetic algorithm
 * 
 * @author pmenuet
 * @author mpelcat
 */

public class GeneticAlgoParameters {

	protected TextParameters textParameters = null;

	// Number of individuals in the population
	private int populationSize;

	// Number of generation performed by the PGenetic algorithm
	private int generationNumber;

	// Boolean to determine the type of algorithm to make the population
	private boolean pfastused2makepopulation;

	/**
	 * Time in seconds between two FAST probabilistic hops in the critical path
	 */
	private int fastTime = -1;

	/**
	 * Number of fast iterations to execute before stopping PFast
	 */
	private int fastNumber = 100;

	/**
	 * Constructors
	 * 
	 */
	public GeneticAlgoParameters(int fastNumber, int fastTime,
			int generationNumber, int populationSize,
			boolean pfastused2makepopulation) {

		textParameters.addVariable("generationNumber", generationNumber);
		textParameters.addVariable("populationSize", populationSize);
		textParameters.addVariable("pfastused2makepopulation",
				pfastused2makepopulation);
		textParameters.addVariable("fastTime",
				fastTime);
		textParameters.addVariable("fastNumber", fastNumber);

		this.generationNumber = generationNumber;
		this.populationSize = populationSize;
		this.pfastused2makepopulation = pfastused2makepopulation;
		this.fastTime = fastTime;
		this.fastNumber = fastNumber;
	}

	public GeneticAlgoParameters(TextParameters textParameters) {

		this.generationNumber = textParameters
				.getIntVariable("generationNumber");
		this.populationSize = textParameters.getIntVariable("populationSize");
		this.pfastused2makepopulation = textParameters
				.getBooleanVariable("pfastused2makepopulation");
		if (textParameters.getIntVariable("fastTime") != 0) {
			this.fastTime = textParameters
					.getIntVariable("fastTime");
		}
		if (textParameters.getIntVariable("fastNumber") != 0) {
			this.fastNumber = textParameters.getIntVariable("fastNumber");
		}

		PreesmLogger
				.getLogger()
				.log(
						Level.INFO,
						"The Genetic algo parameters are: generationNumber; populationSize; pfastused2makepopulation=true/false; fastTime in seconds; fastNumber");
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
	 * Returns the time in seconds between two FAST probabilistic hops in the
	 * critical path
	 */
	public int getFastTime() {
		return fastTime;
	}

}
