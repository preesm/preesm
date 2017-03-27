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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.architecture.slam.SlamPackage;
import org.ietr.dftools.architecture.slam.serialize.IPXACTResourceFactoryImpl;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.serialize.ScenarioParser;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

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
	 * Manager of relative constraints
	 */
	private RelativeConstraintManager relativeconstraintmanager = null;

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
	 * Manager of parameters values for PiGraphs
	 */
	private ParameterValueManager parameterValueManager = null;

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

	// Map from DAGs names to SDFGraphs from which they are generated
	private Map<String, SDFGraph> dags2sdfs;

	public PreesmScenario() {
		constraintgroupmanager = new ConstraintGroupManager();
		relativeconstraintmanager = new RelativeConstraintManager();
		timingmanager = new TimingManager();
		simulationManager = new SimulationManager();
		codegenManager = new CodegenManager();
		variablesManager = new VariablesManager();
		parameterValueManager = new ParameterValueManager();
		dags2sdfs = new HashMap<String, SDFGraph>();
	}

	public boolean isPISDFScenario() {
		if (algorithmURL.endsWith(".pi"))
			return true;
		else
			return false;
	}

	public boolean isIBSDFScenario() {
		if (algorithmURL.endsWith(".graphml"))
			return true;
		else
			return false;
	}

	public Set<String> getActorNames() {
		if (isPISDFScenario())
			return getPiActorNames();
		else if (isIBSDFScenario())
			return getSDFActorNames();
		else
			return null;
	}

	private Set<String> getSDFActorNames() {
		Set<String> result = new HashSet<String>();
		try {
			SDFGraph graph = ScenarioParser.getSDFGraph(algorithmURL);
			for (SDFAbstractVertex vertex : graph.vertexSet()) {
				result.add(vertex.getName());
			}
		} catch (FileNotFoundException | InvalidModelException e) {
			e.printStackTrace();
		}
		return result;
	}

	private Set<String> getPiActorNames() {
		Set<String> result = new HashSet<String>();
		try {
			PiGraph graph = ScenarioParser.getPiGraph(algorithmURL);
			for (AbstractActor vertex : graph.getVertices()) {
				result.add(vertex.getName());
			}
		} catch (CoreException | InvalidModelException e) {
			e.printStackTrace();
		}
		return result;
	}

	public VariablesManager getVariablesManager() {
		return variablesManager;
	}

	public ConstraintGroupManager getConstraintGroupManager() {
		return constraintgroupmanager;
	}

	public RelativeConstraintManager getRelativeconstraintManager() {
		return relativeconstraintmanager;
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
		if (operatorIds == null) {
			operatorIds = new HashSet<String>();
		}
		return operatorIds;
	}

	public List<String> getOrderedOperatorIds() {
		List<String> opIdList = new ArrayList<String>(getOperatorIds());
		Collections.sort(opIdList, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});

		return opIdList;
	}

	public Set<String> getOperatorDefinitionIds() {
		if (operatorDefinitionIds == null)
			operatorDefinitionIds = new HashSet<String>();
		return operatorDefinitionIds;
	}

	public void setOperatorIds(Set<String> operatorIds) {
		this.operatorIds = operatorIds;
	}

	public void setOperatorDefinitionIds(Set<String> operatorDefinitionIds) {
		this.operatorDefinitionIds = operatorDefinitionIds;
	}

	public Set<String> getComNodeIds() {
		if (comNodeIds == null)
			comNodeIds = new HashSet<String>();
		return comNodeIds;
	}

	public void setComNodeIds(Set<String> comNodeIds) {
		this.comNodeIds = comNodeIds;
	}

	public ParameterValueManager getParameterValueManager() {
		return parameterValueManager;
	}

	public void setParameterValueManager(
			ParameterValueManager parameterValueManager) {
		this.parameterValueManager = parameterValueManager;
	}

	/**
	 * From PiScenario
	 * 
	 * @throws CoreException
	 * @throws InvalidModelException
	 * @throws FileNotFoundException
	 */

	public void update(boolean algorithmChange, boolean architectureChange)
			throws InvalidModelException, CoreException, FileNotFoundException {
		// If the architecture changes, operator ids, operator defintion ids and
		// com node ids are no more valid (they are extracted from the
		// architecture)
		if (architectureChange && architectureURL.endsWith(".slam")) {
			Map<String, Object> extToFactoryMap = Resource.Factory.Registry.INSTANCE
					.getExtensionToFactoryMap();
			Object instance = extToFactoryMap.get("slam");
			if (instance == null) {
				instance = new IPXACTResourceFactoryImpl();
				extToFactoryMap.put("slam", instance);
			}

			if (!EPackage.Registry.INSTANCE.containsKey(SlamPackage.eNS_URI)) {
				EPackage.Registry.INSTANCE.put(SlamPackage.eNS_URI,
						SlamPackage.eINSTANCE);
			}

			// Extract the root object from the resource.
			Design design = ScenarioParser.parseSlamDesign(architectureURL);

			getOperatorIds().clear();
			getOperatorIds().addAll(DesignTools.getOperatorInstanceIds(design));

			getOperatorDefinitionIds().clear();
			getOperatorDefinitionIds().addAll(
					DesignTools.getOperatorComponentIds(design));

			getComNodeIds().clear();
			getComNodeIds().addAll(DesignTools.getComNodeInstanceIds(design));

		}
		// If the algorithm changes, parameters or variables are no more valid
		// (they are set in the algorithm)
		if (algorithmChange) {
			if (isPISDFScenario()) {
				parameterValueManager.updateWith(ScenarioParser
						.getPiGraph(algorithmURL));
			} else if (isIBSDFScenario()) {
				variablesManager.updateWith(ScenarioParser
						.getSDFGraph(algorithmURL));
			}
		}
		// If the algorithm or the architecture changes, timings and constraints
		// are no more valid (they depends on both algo and archi)
		if (algorithmChange || architectureChange) {
			timingmanager.clear();
			constraintgroupmanager.update();
		}
	}

	public Map<String, SDFGraph> getDAGs2SDFs() {
		return dags2sdfs;
	}
}
