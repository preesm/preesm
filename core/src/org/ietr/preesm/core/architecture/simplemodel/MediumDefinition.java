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

package org.ietr.preesm.core.architecture.simplemodel;

import org.ietr.preesm.core.architecture.ArchitectureComponentDefinition;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;

/**
 * The medium definition describes the medium capabilities
 * Its properties are used by the timed architecture simulator to
 * evaluate the performance of an implementation
 *         
 * @author mpelcat
 */
public class MediumDefinition extends ArchitectureComponentDefinition {

	/**
	 * Properties used by architecture simulator
	 */

	/**
	 * Transfer inverse speed in TU(Time Unit)/AU (Allocation Unit) The usual
	 * utilization is with cycles/Byte
	 * 
	 * The speed can depend on parameters like data size
	 */
	float invSpeed = 0f;


	/**
	 * Transmission overhead on sender in TU(Time Unit) The usual utilization is
	 * with cycles
	 */
	int overhead = 0;

	public MediumDefinition(MediumDefinition origin) {
		super(origin.getId(), "medium");
		
		this.fill(origin);
	}

	public MediumDefinition(String id) {
		super(id, "medium");
	}

	public MediumDefinition(String id,float invSpeed, int overhead) {
		super(id, "medium");
		setInvSpeed(invSpeed);
		setOverhead(overhead);
	}
	
	public ArchitectureComponentType getType(){
		return ArchitectureComponentType.medium;
	}

	@Override
	public MediumDefinition clone() {
		return new MediumDefinition(this.getId(),this.getInvSpeed(), this.getOverhead());
	}

	public void fill(ArchitectureComponentDefinition origin){
		this.invSpeed = ((MediumDefinition)origin).getInvSpeed();
		this.overhead = ((MediumDefinition)origin).getOverhead();
	}
	
	public float getInvSpeed() {
		return invSpeed;
	}

	public int getOverhead() {
		return overhead;
	}

	public void setInvSpeed(float invSpeed) {
		this.invSpeed = invSpeed;
	}

	public void setOverhead(int overhead) {
		this.overhead = overhead;
	}
	
	public long getTransferTime(long transferSize){
		Long datasize = transferSize;
		Double time = datasize.doubleValue() * getInvSpeed();
		time = Math.ceil(time);
		return time.longValue();
	}
}
