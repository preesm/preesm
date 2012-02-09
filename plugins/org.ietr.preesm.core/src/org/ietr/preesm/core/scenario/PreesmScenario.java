/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.core.scenario;

import java.util.Set;

/**
 * Storing all information of a scenario
 * 
 * @author mpelcat
 */
public class PreesmScenario {

	/**
	 * Manager of constraint groups
	 */
	private ConstraintGroupManager constraintgroupmanager = null;

	/**
	 * Manager of timings
	 */
	private TimingManager timingmanager = null;

	/**
	 * Manager of simulation parameters
	 */
	private SimulationManager simulationManager = null;

	/**
	 * Manager of graph variables
	 */
	private VariablesManager variablesManager = null;

	/**
	 * Manager of code generation parameters
	 */
	private CodegenManager codegenManager = null;

	/**
	 * Path to the algorithm file
	 */
	private String algorithmURL = "";

	/**
	 * Path to the architecture file
	 */
	private String architectureURL = "";

	/**
	 * current architecture properties
	 */
	private Set<String> operatorIds = null;
	private Set<String> operatorDefinitionIds = null;
	private Set<String> comNodeIds = null;

	/**
	 * Path to the scenario file
	 */
	private String scenarioURL = "";

	public PreesmScenario() {
		constraintgroupmanager = new ConstraintGroupManager();
		timingmanager = new TimingManager();
		simulationManager = new SimulationManager();
		codegenManager = new CodegenManager();
		variablesManager = new VariablesManager();
	}

	public VariablesManager getVariablesManager() {
		return variablesManager;
	}

	public ConstraintGroupManager getConstraintGroupManager() {
		return constraintgroupmanager;
	}

	public TimingManager getTimingManager() {
		return timingmanager;
	}

	public String getAlgorithmURL() {
		return algorithmURL;
	}

	public void setAlgorithmURL(String algorithmURL) {
		this.algorithmURL = algorithmURL;
	}

	public String getArchitectureURL() {
		return architectureURL;
	}

	public void setArchitectureURL(String architectureURL) {
		this.architectureURL = architectureURL;
	}

	public SimulationManager getSimulationManager() {
		return simulationManager;
	}

	public CodegenManager getCodegenManager() {
		return codegenManager;
	}

	public String getScenarioURL() {
		return scenarioURL;
	}

	public void setScenarioURL(String scenarioURL) {
		this.scenarioURL = scenarioURL;
	}

	public Set<String> getOperatorIds() {
		return operatorIds;
	}

	public Set<String> getOperatorDefinitionIds() {
		return operatorDefinitionIds;
	}

	public void setOperatorIds(Set<String> operatorIds) {
		this.operatorIds = operatorIds;
	}

	public void setOperatorDefinitionIds(Set<String> operatorDefinitionIds) {
		this.operatorDefinitionIds = operatorDefinitionIds;
	}

	public Set<String> getComNodeIds() {
		return comNodeIds;
	}

	public void setComNodeIds(Set<String> comNodeIds) {
		this.comNodeIds = comNodeIds;
	}
}
