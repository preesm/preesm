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


/**
 * 
 */
package org.ietr.preesm.core.architecture;

/**
 * These properties are used by the approximately timed architecture
 * simulator to evaluate the performance of an implementation
 *         
 * @author mpelcat
 */
public class ApproximatelyTimedMediumProperty {

	/**
	 * Transmission overhead on sender in TU(Time Unit) The usual utilization is
	 * with cycles
	 */
	int overhead = 0;

	/**
	 * Reception time on receiver in TU(Time Unit) The usual utilization is with
	 * cycles
	 */
	int receptionTime = 0;

	/**
	 * Transfer speed in TU(Time Unit)/AU (Allocation Unit) The usual
	 * utilization is with cycles/Byte
	 * 
	 * The speed can depend on parameters like data size
	 */
	int speed = 0;

	public ApproximatelyTimedMediumProperty(int speed, int overhead,
			int receptionTime) {
		super();
		this.speed = speed;
		this.overhead = overhead;
		this.receptionTime = receptionTime;
	}

	@Override
	public ApproximatelyTimedMediumProperty clone() {
		return new ApproximatelyTimedMediumProperty(this.getSpeed(), this
				.getOverhead(), this.getReceptionTime());
	}

	/**
	 * @return the overhead
	 */
	public int getOverhead() {
		return overhead;
	}

	/**
	 * @return the receptionTime
	 */
	public int getReceptionTime() {
		return receptionTime;
	}

	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
	}
}
