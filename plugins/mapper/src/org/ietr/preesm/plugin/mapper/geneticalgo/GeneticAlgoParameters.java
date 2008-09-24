/**
 * 
 */
package org.ietr.preesm.plugin.mapper.geneticalgo;

import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.plugin.abc.ArchitectureSimulatorType;
import org.ietr.preesm.plugin.mapper.AbstractParameters;

/**
 * Specific parameters of genetic algorithm
 * 
 * @author pmenuet
 */
public class GeneticAlgoParameters extends AbstractParameters {

	// Architecture simulator on which the genetic algorithm is performed
	private ArchitectureSimulatorType simulatorType;

	// Number of individuals in the population
	private int populationSize;

	// Number of generation performed by the PGenetic algorithm
	private int generationNumber;

	// Boolean to determine the type of algorithm to make the population
	private boolean pfastused2makepopulation;

	/**
	 * Constructors
	 * 
	 */
	public GeneticAlgoParameters(int generationNumber, int populationSize,
			ArchitectureSimulatorType simulatorType,
			boolean pfastused2makepopulation) {
		super();
		
		textParameters.addVariable("generationNumber", generationNumber);
		textParameters.addVariable("populationSize", populationSize);
		textParameters.addVariable("simulatorType", simulatorType.toString());
		textParameters.addVariable("pfastused2makepopulation", pfastused2makepopulation);
		
		this.generationNumber = generationNumber;
		this.populationSize = populationSize;
		this.simulatorType = simulatorType;
		this.pfastused2makepopulation = pfastused2makepopulation;
	}

	public GeneticAlgoParameters(TextParameters textParameters) {
		super(textParameters);
		
		this.generationNumber = textParameters.getIntVariable("generationNumber");
		this.populationSize = textParameters.getIntVariable("populationSize");
		this.simulatorType = ArchitectureSimulatorType.fromString(textParameters.getVariable("simulatorType"));
		this.pfastused2makepopulation = textParameters.getBooleanVariable("pfastused2makepopulation");
	}

	/**
	 * Getters and setters
	 * 
	 */

	public int getPopulationSize() {
		return populationSize;
	}

	public boolean isPfastused2makepopulation() {
		return pfastused2makepopulation;
	}

	public void setPfastused2makepopulation(boolean pfastused2makepopulation) {
		this.pfastused2makepopulation = pfastused2makepopulation;
	}

	public ArchitectureSimulatorType getSimulatorType() {
		return simulatorType;
	}

	public void setSimulatorType(ArchitectureSimulatorType simulatorType) {
		this.simulatorType = simulatorType;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public int getGenerationNumber() {
		return generationNumber;
	}

	public void setGenerationNumber(int generationNumber) {
		this.generationNumber = generationNumber;
	}

}
