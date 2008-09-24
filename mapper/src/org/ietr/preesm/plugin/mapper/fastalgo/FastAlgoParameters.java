package org.ietr.preesm.plugin.mapper.fastalgo;

import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.plugin.abc.ArchitectureSimulatorType;
import org.ietr.preesm.plugin.mapper.AbstractParameters;

/**
 * Class which purpose is setting the parameters for the fast algorithm
 * 
 * @author pmenuet
 */

public class FastAlgoParameters extends AbstractParameters {

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
	private int maxCount;

	/**
	 * MaxStep determines the number of vertices chosen to do the local
	 * refinement.
	 */
	private int maxStep;

	/**
	 * Simulator type
	 */
	private ArchitectureSimulatorType simulatorType;

	public FastAlgoParameters(TextParameters textParameters) {
		super(textParameters);
		
		this.maxCount = textParameters.getIntVariable("maxCount");
		this.maxStep = textParameters.getIntVariable("maxStep");
		this.margIn = textParameters.getIntVariable("margIn");
		this.simulatorType = ArchitectureSimulatorType.fromString(textParameters.getVariable("simulatorType"));
	}

	/**
	 * 
	 * Constructors
	 * 
	 */

	public FastAlgoParameters(int maxCount, int maxStep, int margIn,
			ArchitectureSimulatorType simulatorType) {
		super();
		textParameters.addVariable("maxCount",maxCount);
		textParameters.addVariable("maxStep",maxStep);
		textParameters.addVariable("margIn",margIn);
		textParameters.addVariable("simulatorType", simulatorType.toString());
		
		this.maxCount = maxCount;
		this.maxStep = maxStep;
		this.margIn = margIn;
		this.simulatorType = simulatorType;
	}

	/**
	 * 
	 * Getters and setters
	 * 
	 */

	public int getMargIn() {
		return margIn;
	}

	public ArchitectureSimulatorType getSimulatorType() {
		return simulatorType;
	}

	public void setSimulatorType(ArchitectureSimulatorType simulatorType) {
		this.simulatorType = simulatorType;
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

}
