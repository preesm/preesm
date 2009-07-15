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
package org.ietr.preesm.plugin.scheduling.listsched.descriptor;

import java.util.HashMap;

/**
 * This class gives a description of an operation, which could be a computation
 * or a communication
 * 
 * @author pmu
 * 
 */
public class OperationDescriptor {
	/**
	 * The algorithm associated with the operation
	 */
	protected AlgorithmDescriptor algorithm;

	/**
	 * The operation buffer
	 */
	protected HashMap<String, OperationDescriptor> OperationDescriptorBuffer = null;

	/**
	 * Name of the operation
	 */
	protected String name;

	/**
	 * Operation type, could be computation or communication
	 */
	protected OperationType type;

	/**
	 * As Soon As Possible start time of the operation
	 */
	protected int ASAP = 0;

	/**
	 * As Late As Possible start time of the operation
	 */
	protected int ALAP = 0;

	/**
	 * Indicates whether the operation is scheduled
	 */
	protected boolean scheduled = false;

	/**
	 * Construct an OperationDescriptor with the given name
	 * 
	 * @param name
	 *            Operation name
	 */
	public OperationDescriptor(String name) {
		this.name = name;
	}

	/**
	 * Construct an OperationDescriptor with the given name and associated
	 * algorithm
	 * 
	 * @param name
	 *            Operation name
	 * @param algorithm
	 *            Associated algorithm
	 */
	public OperationDescriptor(String name, AlgorithmDescriptor algorithm) {
		this.name = name;
		this.algorithm = algorithm;
		this.OperationDescriptorBuffer = algorithm.getOperations();
	}

	/**
	 * Construct an OperationDescriptor with the given name and operation buffer
	 * 
	 * @param name
	 *            Operation name
	 * @param OperationDescriptorBuffer
	 *            Operation buffer
	 */
	public OperationDescriptor(String name,
			HashMap<String, OperationDescriptor> OperationDescriptorBuffer) {
		this.name = name;
		this.OperationDescriptorBuffer = OperationDescriptorBuffer;
	}

	/**
	 * Clear the mark of scheduled
	 */
	public void clearScheduled() {
		scheduled = false;
	}

	/**
	 * Get ALAP
	 * 
	 * @return ALAP
	 */
	public int getALAP() {
		return ALAP;
	}

	/**
	 * Get associated algorithm
	 * 
	 * @return Associated algorithm
	 */
	public AlgorithmDescriptor getAlgorithm() {
		return algorithm;
	}

	/**
	 * Get ASAP
	 * 
	 * @return ASAP
	 */
	public int getASAP() {
		return ASAP;
	}

	/**
	 * Get operation name
	 * 
	 * @return Operation name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get operation type
	 * 
	 * @return Operation type
	 */
	public OperationType getType() {
		return type;
	}

	/**
	 * Is the operation scheduled?
	 * 
	 * @return true=yes, false=no
	 */
	public boolean isScheduled() {
		return scheduled;
	}

	/**
	 * Set ALAP
	 * 
	 * @param alap
	 *            ALAP
	 */
	public void setALAP(int alap) {
		ALAP = alap;
	}

	/**
	 * Set associated algorithm
	 * 
	 * @param algorithm
	 *            Associated algorithm
	 */
	public void setAlgorithm(AlgorithmDescriptor algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * Set ASAP
	 * 
	 * @param asap
	 *            ASAP
	 */
	public void setASAP(int asap) {
		ASAP = asap;
	}

	/**
	 * Set operation name
	 * 
	 * @param name
	 *            Operation name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Mark the operation as scheduled
	 */
	public void setScheduled() {
		scheduled = true;
	}
}
