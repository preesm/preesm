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

import java.util.HashMap;

public class OperationDescriptor {

	protected AlgorithmDescriptor algorithm;

	protected HashMap<String, OperationDescriptor> OperationDescriptorBuffer = null;

	protected String name;

	protected OperationType type;

	protected int ASAP = 0;

	protected int ALAP = 0;

	protected boolean scheduled = false;

	public OperationDescriptor(String name) {
		this.name = name;
	}

	public OperationDescriptor(String name,
			HashMap<String, OperationDescriptor> OperationDescriptorBuffer) {
		this.name = name;
		this.OperationDescriptorBuffer = OperationDescriptorBuffer;
	}

	public OperationDescriptor(String name, AlgorithmDescriptor algorithm) {
		this.name = name;
		this.algorithm = algorithm;
		this.OperationDescriptorBuffer = algorithm.getOperations();
	}

	public AlgorithmDescriptor getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(AlgorithmDescriptor algorithm) {
		this.algorithm = algorithm;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public OperationType getType() {
		return type;
	}

	/**
	 * 
	 */
	public int getAEST() {
		return ASAP;
	}

	/**
	 * 
	 */
	public int getALST() {
		return ALAP;
	}

	/**
	 * 
	 */
	public int getASAP() {
		return ASAP;
	}

	public void setASAP(int asap) {
		ASAP = asap;
	}

	/**
	 * 
	 */
	public int getALAP() {
		return ALAP;
	}

	public void setALAP(int alap) {
		ALAP = alap;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isScheduled() {
		return scheduled;
	}

	/**
	 * 
	 */
	public void setScheduled() {
		scheduled = true;
	}

	/**
	 * 
	 */
	public void clearScheduled() {
		scheduled = false;
	}

}
