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
package org.ietr.preesm.plugin.mapper.listsched.descriptor;

/**
 * This class describes a time interval
 * 
 * @author pmu
 * 
 */
public class TimeInterval implements Comparable<TimeInterval> {
	/**
	 * Start time of the interval
	 */
	private int startTime = 0;

	/**
	 * Finish time of the interval
	 */
	private int finishTime = 0;

	/**
	 * Construct a time interval with the start and finish time
	 * 
	 * @param startTime
	 *            Start time
	 * @param finishTime
	 *            Finish time
	 */
	public TimeInterval(int startTime, int finishTime) {
		this.startTime = startTime;
		this.finishTime = finishTime;
	}

	@Override
	public int compareTo(TimeInterval arg0) {
		return (startTime - arg0.getStartTime());
	}

	/**
	 * Get the finish time
	 * 
	 * @return The finish time
	 */
	public int getFinishTime() {
		return finishTime;
	}

	/**
	 * Get the start time
	 * 
	 * @return The start time
	 */
	public int getStartTime() {
		return startTime;
	}

	/**
	 * Set the finish time
	 * 
	 * @param finishTime
	 *            The finish time
	 */
	public void setFinishTime(int finishTime) {
		this.finishTime = finishTime;
	}

	/**
	 * Set the start time
	 * 
	 * @param startTime
	 *            The start time
	 */
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

}
