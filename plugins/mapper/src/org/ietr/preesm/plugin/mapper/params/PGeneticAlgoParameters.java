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
 * Parameters for task scheduling genetic algorithm multithread
 * 
 * @author pmenuet
 * @author mpelcat
 */
public class PGeneticAlgoParameters {

	protected TextParameters textParameters = null;

	// Number of individuals the algorithm will keep in the best population
	private int populationSize;

	// Number of generation performed by the PGenetic algorithm
	private int generationNumber;

	// Number of thread/processor available to perform the PGenetic Algorithm
	private int procNumber;

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
	 */

	public PGeneticAlgoParameters(TextParameters textParameters) {

		this.generationNumber = textParameters
				.getIntVariable("generationNumber");
		this.populationSize = textParameters.getIntVariable("populationSize");
		this.procNumber = textParameters.getIntVariable("procNumber");
		this.pfastused2makepopulation = textParameters
				.getBooleanVariable("pfastused2makepopulation");
		if (textParameters.getIntVariable("fastTime") > 0) {
			this.fastTime = textParameters
					.getIntVariable("fastTime");
		}
		if (textParameters.getIntVariable("fastLocalSearchTime") > 0) {
			this.fastLocalSearchTime = textParameters
					.getIntVariable("fastLocalSearchTime");
		}
		if (textParameters.getIntVariable("fastNumber") != 0) {
			this.fastNumber = textParameters.getIntVariable("fastNumber");
		}

		PreesmLogger
				.getLogger()
				.log(
						Level.INFO,
						"The Genetic algo parameters are: generationNumber; populationSize; procNumber; pfastused2makepopulation=true/false; fastTime in seconds; fastLocalSearchTime in seconds; fastNumber");
	}

	public PGeneticAlgoParameters(int fastNumber,
			int fastTime,int fastLocalSearchTime, int generationNumber,
			int populationSize, int procNumber, boolean pfastused2makepopulation) {

		textParameters.addVariable("generationNumber", generationNumber);
		textParameters.addVariable("populationSize", populationSize);
		textParameters.addVariable("processorNumber", procNumber);
		textParameters.addVariable("pfastused2makepopulation",
				pfastused2makepopulation);
		textParameters.addVariable("fastTime",
				fastTime);
		textParameters.addVariable("fastNumber", fastNumber);

		this.generationNumber = generationNumber;
		this.populationSize = populationSize;
		this.procNumber = procNumber;
		this.pfastused2makepopulation = pfastused2makepopulation;
		this.fastTime = fastTime;
		this.fastLocalSearchTime = fastLocalSearchTime;
		this.fastNumber = fastNumber;
	}

	/**
	 * @return the pfastused2makepopulation
	 */
	public boolean isPfastused2makepopulation() {
		return pfastused2makepopulation;
	}

	/**
	 * @param pfastused2makepopulation
	 *            the pfastused2makepopulation to set
	 */
	public void setPfastused2makepopulation(boolean pfastused2makepopulation) {
		this.pfastused2makepopulation = pfastused2makepopulation;
	}

	/**
	 * @return the populationSize
	 */
	public int getPopulationSize() {
		return populationSize;
	}

	/**
	 * @param populationSize
	 *            the populationSize to set
	 */
	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	/**
	 * @return the generationNumber
	 */
	public int getGenerationNumber() {
		return generationNumber;
	}

	/**
	 * @param generationNumber
	 *            the generationNumber to set
	 */
	public void setGenerationNumber(int generationNumber) {
		this.generationNumber = generationNumber;
	}

	/**
	 * @return the processorNumber
	 */
	public int getProcessorNumber() {
		return procNumber;
	}

	/**
	 * @param processorNumber
	 *            the processorNumber to set
	 */
	public void setProcessorNumber(int processorNumber) {
		this.procNumber = processorNumber;
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
