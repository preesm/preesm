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

package org.ietr.preesm.plugin.mapper.params;

import java.util.logging.Level;

import net.sf.dftools.workflow.tools.AbstractWorkflowLogger;

import org.ietr.preesm.core.task.TextParameters;

/**
 * Parameters for task scheduling FAST algorithm multithread
 * 
 * @author pmenuet
 * @author mpelcat
 */
public class PFastAlgoParameters extends SchedulingParameters {
	
	/**
	 * This variable is the one which fix the number of Nodes necessary for each
	 * processor to execute probabilistic jump locally(local refinement). The
	 * number of vertices present in the DAG is determinant for this parameter.
	 * More, if this number is too low the PFastAlgo become no efficient.
	 */
	private int nodesMin;

	/**
	 * Number of processors available
	 */
	private int procNumber;

	/**
	 * true if we need to display the intermediate solutions
	 */
	private boolean displaySolutions;

	/**
	 * Time in seconds to run one FAST
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

	public PFastAlgoParameters(TextParameters textParameters) {
		super(textParameters);
		
		this.nodesMin = textParameters.getIntVariable("nodesMin");
		this.procNumber = textParameters.getIntVariable("procNumber");
		this.displaySolutions = textParameters.getBooleanVariable("displaySolutions");
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
		
		AbstractWorkflowLogger
		.getLogger()
		.log(
				Level.INFO,
				"The PFast algo parameters are: nodesMin; procNumber; displaySolutions=true/false; fastTime in seconds; fastLocalSearchTime in seconds; fastNumber");

	}

	public PFastAlgoParameters(int fastNumber, int fastTime,int fastLocalSearchTime, boolean displaySolutions,
			int nodesmin, int procNumber) {
		// DAG is not exported now so DAGExportPath = null
		super((String)null);
		
		this.nodesMin = nodesmin;
		this.procNumber = procNumber;
		this.displaySolutions = displaySolutions;
		this.fastTime = fastTime;
		this.fastLocalSearchTime = fastLocalSearchTime;
		this.fastNumber = fastNumber;
	}

	/**
	 * Getters and Setters
	 */

	public int getProcNumber() {
		return procNumber;
	}

	public void setProcNumber(int procNumber) {
		this.procNumber = procNumber;
	}

	public boolean isDisplaySolutions() {
		return displaySolutions;
	}

	public void setDisplaySolutions(boolean displaySolutions) {
		this.displaySolutions = displaySolutions;
	}

	public int getNodesmin() {
		return nodesMin;
	}

	public void setNodesmin(int nodesmin) {
		this.nodesMin = nodesmin;
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
