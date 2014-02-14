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

package org.ietr.preesm.mapper.params;

import java.util.Map;
import java.util.logging.Level;

import org.ietr.dftools.workflow.tools.WorkflowLogger;

/**
 * Class which purpose is setting the parameters for the fast algorithm
 * 
 * @author pmenuet
 * @author mpelcat
 */

public class FastAlgoParameters {

	/**
	 * true if we need to display the intermediate solutions
	 */
	private boolean displaySolutions;

	/**
	 * Time in seconds we want to run FAST
	 */
	private int fastTime = 200;

	/**
	 * Time in seconds spent in one FAST local neighborhood
	 */
	private int fastLocalSearchTime = 10;

	public FastAlgoParameters(Map<String, String> textParameters) {

		this.displaySolutions = Boolean.valueOf(textParameters
				.get("displaySolutions"));
		if (Integer.valueOf(textParameters.get("fastTime")) > 0) {
			this.fastTime = Integer.valueOf(textParameters.get("fastTime"));
		}
		if (Integer.valueOf(textParameters.get("fastLocalSearchTime")) > 0) {
			this.fastLocalSearchTime = Integer.valueOf(textParameters
					.get("fastLocalSearchTime"));
		}

		WorkflowLogger
				.getLogger()
				.log(Level.INFO,
						"The Fast algo parameters are: displaySolutions=true/false; fastTime in seconds; fastLocalSearchTime in seconds");
	}

	/**
	 * 
	 * Constructors
	 * 
	 */

	public FastAlgoParameters(int fastTime, int fastLocalSearchTime,
			boolean displaySolutions) {

		this.displaySolutions = displaySolutions;
		this.fastTime = fastTime;
		this.fastLocalSearchTime = fastLocalSearchTime;
	}

	/**
	 * 
	 * Getters and setters
	 * 
	 */

	public boolean isDisplaySolutions() {
		return displaySolutions;
	}

	public void setDisplaySolutions(boolean displaySolutions) {
		this.displaySolutions = displaySolutions;
	}

	/**
	 * Returns the time in seconds for the whole FAST process
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
