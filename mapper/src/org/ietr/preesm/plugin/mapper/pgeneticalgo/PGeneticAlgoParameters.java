/**
 * 
 */
package org.ietr.preesm.plugin.mapper.pgeneticalgo;

import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.plugin.abc.ArchitectureSimulatorType;
import org.ietr.preesm.plugin.mapper.AbstractParameters;

/**
 * Parameters for task scheduling genetic algorithm multithread
 * 
 * @author pmenuet
 */
public class PGeneticAlgoParameters extends AbstractParameters {

	// Type of simulator used to perform the PGenetic Algorithm
	private ArchitectureSimulatorType simulatorType;

	// Number of individuals the algorithm will keep in the best population
	private int populationSize;

	// Number of generation performed by the PGenetic algorithm
	private int generationNumber;

	// Number of thread/processor available to perform the PGenetic Algorithm
	private int procNumber;

	// Boolean to determine the type of algorithm to make the population
	private boolean pfastused2makepopulation;

	/**
	 * Constructors
	 */

	public PGeneticAlgoParameters(TextParameters textParameters) {
		super(textParameters);

		this.generationNumber = textParameters.getIntVariable("generationNumber");
		this.populationSize = textParameters.getIntVariable("populationSize");
		this.procNumber = textParameters.getIntVariable("procNumber");
		this.simulatorType = ArchitectureSimulatorType.fromString(textParameters.getVariable("simulatorType"));
		this.pfastused2makepopulation = textParameters.getBooleanVariable("pfastused2makepopulation");
	
	}

	/**
	 * 
	 * @param archi
	 * @param generationNumber
	 * @param populationDAG
	 * @param populationSize
	 * @param procNumber
	 * @param type
	 */
	public PGeneticAlgoParameters(int generationNumber,
			int populationSize, int procNumber,
			ArchitectureSimulatorType simulatorType,
			boolean pfastused2makepopulation) {
		super();

		textParameters.addVariable("generationNumber", generationNumber);
		textParameters.addVariable("populationSize", populationSize);
		textParameters.addVariable("processorNumber", procNumber);
		textParameters.addVariable("simulatorType", simulatorType.toString());
		textParameters.addVariable("pfastused2makepopulation", pfastused2makepopulation);
		
		this.generationNumber = generationNumber;
		this.populationSize = populationSize;
		this.procNumber = procNumber;
		this.simulatorType = simulatorType;
		this.pfastused2makepopulation = pfastused2makepopulation;
	}

	/**
	 * @return the type
	 */
	public ArchitectureSimulatorType getSimulatorType() {
		return simulatorType;
	}

	/**
	 * @return the pfastused2makepopulation
	 */
	public boolean isPfastused2makepopulation() {
		return pfastused2makepopulation;
	}

	/**
	 * @param pfastused2makepopulation
	 *            the pfastused2makepopulation to set
	 */
	public void setPfastused2makepopulation(boolean pfastused2makepopulation) {
		this.pfastused2makepopulation = pfastused2makepopulation;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setSimulatorType(ArchitectureSimulatorType simulatorType) {
		this.simulatorType = simulatorType;
	}

	/**
	 * @return the populationSize
	 */
	public int getPopulationSize() {
		return populationSize;
	}

	/**
	 * @param populationSize
	 *            the populationSize to set
	 */
	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	/**
	 * @return the generationNumber
	 */
	public int getGenerationNumber() {
		return generationNumber;
	}

	/**
	 * @param generationNumber
	 *            the generationNumber to set
	 */
	public void setGenerationNumber(int generationNumber) {
		this.generationNumber = generationNumber;
	}

	/**
	 * @return the processorNumber
	 */
	public int getProcessorNumber() {
		return procNumber;
	}

	/**
	 * @param processorNumber
	 *            the processorNumber to set
	 */
	public void setProcessorNumber(int processorNumber) {
		this.procNumber = processorNumber;
	}

}
