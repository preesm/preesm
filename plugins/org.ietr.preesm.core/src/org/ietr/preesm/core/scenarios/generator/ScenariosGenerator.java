/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy, Yaset Oliva Venegas
 *
 * [mpelcat,jnezan,kdesnos,jheulot,cguy,yoliva]@insa-rennes.fr
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software. You can use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and, more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.core.scenarios.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.core.scenario.serialize.ScenarioParser;
import org.ietr.preesm.core.scenario.serialize.ScenarioWriter;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

// TODO: extend to IBSDF algorithms
/**
 * Class to generate a set of PreesmScenarios from several architectures and
 * PiSDF algorithms
 * 
 * A PreesmScenario is generated for each possible pair of algorithm and
 * architecture.
 * 
 * Timings, constraints, and simulation parameters are set to default values
 * (see the createScenario method)
 * 
 * @author cguy
 *
 */
public class ScenariosGenerator {

	// Constants for extensions and folder names
	private static final String archiExt = "slam";
	private static final String algoExt = "pi";
	private static final String archiDirName = "Archi";
	private static final String algoDirName = "Algo";
	private static final String scenarioDirName = "Scenarios";

	/**
	 * Generate a set of PreesmScenarios from an architecture folder and from an
	 * algorithm folder
	 * 
	 * @param archiDir
	 *            the IFolder containing the architectures
	 * @param algoDir
	 *            the IFolder containing the PiSDF algorithms
	 * @return a set of PreesmScenario, one for each possible pair of
	 *         architecture and algorithm
	 * @throws InvalidModelException
	 * @throws CoreException
	 */
	public Set<PreesmScenario> generateScenarios(IFolder archiDir,
			IFolder algoDir) throws InvalidModelException, CoreException {
		Set<String> archis = new HashSet<String>();
		Set<String> algos = new HashSet<String>();
		for (IResource resource : archiDir.members()) {
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				if (file.getProjectRelativePath().getFileExtension()
						.equals(archiExt)) {
					archis.add(file.getFullPath().toOSString());
				}
			}
		}
		for (IResource resource : algoDir.members()) {
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				if (file.getProjectRelativePath().getFileExtension()
						.equals(algoExt)) {
					algos.add(file.getFullPath().toOSString());
				}
			}
		}
		return generateScenarios(archis, algos);
	}

	/**
	 * Generates a set of PreesmScenario from an IProject
	 * 
	 * @param project
	 *            the IProject containing the architectures and PiSDF
	 *            algorithms. project is supposed to have the
	 *            PreesmProjectNature and to follow the standard Preesm folder
	 *            hierarchy
	 * @return a set of PreesmScenario, one for each possible pair of
	 *         architecture and algorithm
	 * @throws InvalidModelException
	 * @throws CoreException
	 */
	public Set<PreesmScenario> generateScenarios(IProject project)
			throws InvalidModelException, CoreException {
		IFolder archiDir = project.getFolder(archiDirName);
		IFolder algoDir = project.getFolder(algoDirName);
		return generateScenarios(archiDir, algoDir);
	}

	/**
	 * Generates a set of PreesmScenario from a set of architectures URL and a
	 * set of PiSDF algorithms URL
	 * 
	 * @param archis
	 *            the set of architectures URL
	 * @param algos
	 *            the set of algorithms URL
	 * @return a set of PreesmScenario, one for each possible pair of
	 *         architecture and algorithm
	 * @throws InvalidModelException
	 * @throws CoreException
	 */
	private Set<PreesmScenario> generateScenarios(Set<String> archis,
			Set<String> algos) throws InvalidModelException, CoreException {
		Set<PreesmScenario> scenarios = new HashSet<PreesmScenario>();
		for (String archiURL : archis) {
			for (String algoURL : algos)
				scenarios.add(createScenario(archiURL, algoURL));
		}
		return scenarios;
	}

	/**
	 * Create a PreesmScenario for a given pair of architecture and PiSDF
	 * algorithm
	 * 
	 * @param archiURL
	 *            the URL of the given architecture
	 * @param algoURL
	 *            the URL of the PiSDF algorithm
	 * @return a PreesmScenario for the architecture and PiSDF algorithm,
	 *         initialized with default values
	 * @throws InvalidModelException
	 * @throws CoreException
	 */
	private PreesmScenario createScenario(String archiURL, String algoURL)
			throws InvalidModelException, CoreException {
		// Create a new PreesmScenario
		PreesmScenario scenario = new PreesmScenario();
		Design archi = ScenarioParser.parseSlamDesign(archiURL);
		PiGraph algo = ScenarioParser.getPiGraph(algoURL);
		// Set algorithm and architecture
		scenario.setAlgorithmURL(algoURL);
		scenario.setArchitectureURL(archiURL);
		// Set default values for constraints, timings and simulation parameters
		for (ComponentInstance ci : archi.getComponentInstances()) {
			String opId = ci.getInstanceName();
			for (AbstractActor aa : algo.getAllVertices()) {
				// Add constraint: aa can be run on ci
				scenario.getConstraintGroupManager().addConstraint(opId, aa);
				// Add timing: aa run on ci in 10000
				scenario.getTimingManager().addTiming(
						new Timing(opId, aa.getPath(), 10000));
			}
			// Add special actors operator id (all cores can execute special
			// actors)
			scenario.getSimulationManager().addSpecialVertexOperatorId(opId);
		}
		// Add a main core (first of the list)
		List<String> coreIds = new ArrayList<String>(
				DesignTools.getOperatorInstanceIds(archi));
		scenario.getSimulationManager().setMainOperatorName(coreIds.get(0));
		// Add a main com node (first of the list)
		List<String> comNodeIds = new ArrayList<String>(
				DesignTools.getComNodeInstanceIds(archi));
		scenario.getSimulationManager().setMainComNodeName(comNodeIds.get(0));
		// Add a average transfer size
		scenario.getSimulationManager().setAverageDataSize(1000);
		// Add a single data type uint
		scenario.getSimulationManager().putDataType(new DataType("uint", 1));

		return scenario;
	}

	/**
	 * Generates a set of PreesmScenario from an IProject and save them in a
	 * folder
	 * 
	 * @param project
	 *            the IProject containing the architectures and PiSDF
	 *            algorithms. project is supposed to have the
	 *            PreesmProjectNature and to follow the standard Preesm folder
	 *            hierarchy
	 * @throws InvalidModelException
	 * @throws CoreException
	 */
	public void generateAndSaveScenarios(IProject project)
			throws CoreException, InvalidModelException {
		IFolder scenarioDir = project.getFolder(scenarioDirName);
		saveScenarios(generateScenarios(project), scenarioDir);
	}

	/**
	 * Generate a set of PreesmScenarios from an architecture folder and from an
	 * algorithm folder
	 * 
	 * @param archiDir
	 *            the IFolder containing the architectures
	 * @param algoDir
	 *            the IFolder containing the PiSDF algorithms
	 * @param scenarioDir
	 *            the IFolder where to save the generated PreesmScenarios
	 * @throws CoreException
	 * @throws InvalidModelException
	 */
	public void generateAndSaveScenarios(IFolder archiDir, IFolder algoDir,
			IFolder scenarioDir) throws CoreException, InvalidModelException {
		saveScenarios(generateScenarios(archiDir, algoDir), scenarioDir);
	}

	/**
	 * Save a set of PreesmScenarios in a given IFolder
	 * 
	 * @param scenarios
	 *            the set of PreesmScenarios to save
	 * @param scenarioDir
	 *            the IFolder where to save the PreesmScenarios
	 * @throws CoreException
	 */
	private void saveScenarios(Set<PreesmScenario> scenarios,
			IFolder scenarioDir) throws CoreException {
		for (PreesmScenario scenario : scenarios) {
			String scenarioName = getScenarioName(scenario);
			IPath scenarioPath = new Path(scenarioName)
					.addFileExtension("scenario");
			IFile scenarioFile = scenarioDir.getFile(scenarioPath);
			if (!scenarioFile.exists())
				scenarioFile.create(null, false, null);
			saveScenario(scenario, scenarioFile);
		}
	}

	/**
	 * Util method generating a name for a given PreesmSceario from its
	 * architecture and algorithm
	 * 
	 * @param scenario
	 *            the PreesmScenario for which we need a name
	 * @return
	 */
	public String getScenarioName(PreesmScenario scenario) {
		IPath algoPath = new Path(scenario.getAlgorithmURL())
				.removeFileExtension();
		String algoName = algoPath.lastSegment();
		IPath archiPath = new Path(scenario.getArchitectureURL())
				.removeFileExtension();
		String archiName = archiPath.lastSegment();
		return algoName + "_" + archiName;
	}

	/**
	 * Save a given PreesmScenario in a given IFile
	 * 
	 * @param scenario
	 *            the PreesmScenario to save
	 * @param scenarioFile
	 *            the IFile in which to save the PreesmScenario
	 */
	private void saveScenario(PreesmScenario scenario, IFile scenarioFile) {
		ScenarioWriter writer = new ScenarioWriter(scenario);
		writer.generateScenarioDOM();
		writer.writeDom(scenarioFile);
	}
}
