package org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor;

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
