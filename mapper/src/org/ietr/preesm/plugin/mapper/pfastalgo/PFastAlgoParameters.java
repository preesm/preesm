/**
 * 
 */
package org.ietr.preesm.plugin.mapper.pfastalgo;

import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.plugin.abc.ArchitectureSimulatorType;
import org.ietr.preesm.plugin.mapper.AbstractParameters;

/**
 * Parameters for task scheduling FAST algorithm multithread
 * 
 * @author pmenuet
 */
public class PFastAlgoParameters extends AbstractParameters {

	/**
	 * This variable is the one which fix the number of Nodes necessary for each
	 * processor to execute probabilistic jump locally(local refinement). The
	 * number of vertices present in the DAG is determinant for this parameter.
	 * More, if this number is too low the PFastAlgo become no efficient.
	 */
	private int nodesmin;

	/**
	 * This is the number of probabilistic local jump which the user want. The
	 * PFast, as the Fast, does probabilistic jump on the critical path which
	 * modify totally the mapping. The margIn parameter is the number you want
	 * to try probabilistic jump PER vertex to refine the solution locally.
	 */
	private int margIn;

	/**
	 * MaxCount is the number of iteration (big probabilistic jump) the
	 * algorithm does. An iteration is a probabilistic jump on the critical path
	 * to find a better solution.
	 */
	private int maxCount;// number of iteration in the algorithm

	/**
	 * MaxStep determines the number of vertices chosen to do the local
	 * refinement.
	 */
	private int maxStep;// number of search steps in an iteration

	/**
	 * Number of processors available
	 */
	private int procNumber;

	/**
	 * simulator type
	 */
	private ArchitectureSimulatorType simulatorType;

	/**
	 * Constructors
	 */

	public PFastAlgoParameters(TextParameters textParameters) {
		super(textParameters);
		this.maxCount = textParameters.getIntVariable("maxCount");
		this.maxStep = textParameters.getIntVariable("maxStep");
		this.margIn = textParameters.getIntVariable("margIn");
		this.nodesmin = textParameters.getIntVariable("nodesmin");
		this.procNumber = textParameters.getIntVariable("procNumber");
		this.simulatorType = ArchitectureSimulatorType.fromString(textParameters.getVariable("simulatorType"));
	}

	public PFastAlgoParameters(int margIn, int maxCount, int maxStep,
			int nodesmin, int procNumber,
			ArchitectureSimulatorType simulatorType) {
		super();
		textParameters.addVariable("margIn", margIn);
		textParameters.addVariable("maxCount", maxCount);
		textParameters.addVariable("maxStep", maxStep);
		textParameters.addVariable("nodesmin", nodesmin);
		textParameters.addVariable("procNumber", procNumber);
		textParameters.addVariable("simulatorType", simulatorType.toString());
		
		this.margIn = margIn;
		this.maxCount = maxCount;
		this.maxStep = maxStep;
		this.nodesmin = nodesmin;
		this.procNumber = procNumber;
		this.simulatorType = simulatorType;
	}

	/**
	 * Getters and Setters
	 */

	public ArchitectureSimulatorType getSimulatorType() {
		return simulatorType;
	}

	public void setSimulatorType(ArchitectureSimulatorType simulatorType) {
		this.simulatorType = simulatorType;
	}

	public int getProcNumber() {
		return procNumber;
	}

	public void setProcNumber(int procNumber) {
		this.procNumber = procNumber;
	}

	public int getMargIn() {
		return margIn;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public int getMaxStep() {
		return maxStep;
	}

	public void setMargIn(int margIn) {
		this.margIn = margIn;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public void setMaxStep(int maxStep) {
		this.maxStep = maxStep;
	}

	public int getNodesmin() {
		return nodesmin;
	}

	public void setNodesmin(int nodesmin) {
		this.nodesmin = nodesmin;
	}

}
