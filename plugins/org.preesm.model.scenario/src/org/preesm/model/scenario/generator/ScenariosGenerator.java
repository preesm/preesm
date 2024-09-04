/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2023) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019 - 2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
 * Mickaël Dardaillon [mickael.dardaillon@insa-rennes.fr] (2020)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.model.scenario.generator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.preesm.commons.DomUtil;
import org.preesm.commons.exceptions.PreesmFrameworkException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.serialize.PiParser;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioConstants;
import org.preesm.model.scenario.serialize.ScenarioWriter;
import org.preesm.model.scenario.util.DefaultTypeSizes;
import org.preesm.model.scenario.util.ScenarioUserFactory;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.SlamPackage;
import org.preesm.model.slam.serialize.IPXACTResourceFactoryImpl;
import org.preesm.model.slam.serialize.SlamParser;
import org.w3c.dom.Document;

/**
 * Class to generate a set of PreesmScenarios from several architectures and algorithms
 *
 * <p>
 * A PreesmScenario is generated for each possible pair of algorithm and architecture.
 * </p>
 *
 * <p>
 * Timings, constraints, and simulation parameters are set to default values (see the createScenario method)
 * </p>
 *
 * @author cguy
 *
 */
public class ScenariosGenerator {

  /** The Constant archiExt. */
  // Constants for extensions and folder names
  private static final String ARCHI_EXT = "slam";

  /** The Constant piAlgoExt. */
  private static final String PI_GRAPH_EXT = "pi";

  /** The Constant archiDirName. */
  private static final String ARCHI_DIR_NAME = "Archi";

  /** The Constant algoDirName. */
  private static final String ALGO_DIR_NAME = "Algo";

  /** The Constant scenarioDirName. */
  private static final String SCENARIO_DIR_NAME = "Scenarios";

  final IFolder  scenarioDir;
  final IProject project;

  /**
   *
   *
   * @param project
   *          the IProject containing the architectures and algorithms. project is supposed to have the
   *          PreesmProjectNature and to follow the standard Preesm folder hierarchy
   */
  public ScenariosGenerator(final IProject project) {
    scenarioDir = project.getFolder(ScenariosGenerator.SCENARIO_DIR_NAME);
    this.project = project;
  }

  /**
   * Generates a set of PreesmScenario from an IProject.
   *
   * @return a set of PreesmScenario, one for each possible pair of architecture and algorithm
   * @throws CoreException
   *           the core exception
   * @throws FileNotFoundException
   *           the file not found exception
   */
  public Set<Scenario> generateScenarios() throws CoreException, FileNotFoundException {
    final IFolder archiDir = project.getFolder(ScenariosGenerator.ARCHI_DIR_NAME);
    final IFolder algoDir = project.getFolder(ScenariosGenerator.ALGO_DIR_NAME);
    return generateScenarios(archiDir, algoDir);
  }

  /**
   * Generate a set of PreesmScenarios from an architecture folder and from an algorithm folder.
   *
   * @param archiDir
   *          the IFolder containing the architectures
   * @param algoDir
   *          the IFolder containing the algorithms
   * @return a set of PreesmScenario, one for each possible pair of architecture and algorithm
   * @throws CoreException
   *           the core exception
   * @throws FileNotFoundException
   *           the file not found exception
   */
  public Set<Scenario> generateScenarios(final IFolder archiDir, final IFolder algoDir)
      throws CoreException, FileNotFoundException {
    final Set<String> archis = new LinkedHashSet<>();
    final Set<String> algos = new LinkedHashSet<>();
    for (final IResource resource : archiDir.members()) {
      if (resource instanceof final IFile file
          && file.getProjectRelativePath().getFileExtension().equals(ScenariosGenerator.ARCHI_EXT)) {
        archis.add(file.getFullPath().toString());
      }
    }
    for (final IResource resource : algoDir.members()) {
      if (resource instanceof final IFile file) {
        final String ext = file.getProjectRelativePath().getFileExtension();
        if (ext.equals(ScenariosGenerator.PI_GRAPH_EXT)) {
          algos.add(file.getFullPath().toString());
        }
      }
    }
    return generateScenarios(project, archis, algos);
  }

  /**
   * Generates a set of PreesmScenario from a set of architectures URL and a set of algorithms URL.
   *
   * @param archis
   *          the set of architectures URL
   * @param algos
   *          the set of algorithms URL
   * @return a set of PreesmScenario, one for each possible pair of architecture and algorithm
   * @throws CoreException
   *           the core exception
   * @throws FileNotFoundException
   *           the file not found exception
   */
  private Set<Scenario> generateScenarios(final IProject project, final Set<String> archis, final Set<String> algos) {
    final Set<Scenario> scenarios = new LinkedHashSet<>();
    for (final String archiURL : archis) {
      for (final String algoURL : algos) {
        scenarios.add(createScenario(project, archiURL, algoURL));
      }
    }
    return scenarios;
  }

  /**
   * Create a PreesmScenario for a given pair of architecture and algorithm.
   *
   * @param archiURL
   *          the URL of the given architecture
   * @param algoURL
   *          the URL of the algorithm
   * @return a PreesmScenario for the architecture and algorithm, initialized with default values
   * @throws CoreException
   *           the core exception
   * @throws FileNotFoundException
   *           the file not found exception
   */
  private Scenario createScenario(final IProject project, final String archiURL, final String algoURL) {
    // Create a new PreesmScenario
    final Scenario scenario = ScenarioUserFactory.createScenario();
    // Handle factory registry
    final Map<String, Object> extToFactoryMap = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
    Object instance = extToFactoryMap.get("slam");
    if (instance == null) {
      instance = new IPXACTResourceFactoryImpl();
      extToFactoryMap.put("slam", instance);
    }
    if (!EPackage.Registry.INSTANCE.containsKey(SlamPackage.eNS_URI)) {
      EPackage.Registry.INSTANCE.put(SlamPackage.eNS_URI, SlamPackage.eINSTANCE);
    }
    // Set algorithm and architecture

    final Design archi = SlamParser.parseSlamDesign(archiURL);
    scenario.setDesign(archi);

    final PiGraph piGraph = PiParser.getPiGraphWithReconnection(algoURL);
    scenario.setAlgorithm(piGraph);

    // Get com nodes and cores names
    final List<ComponentInstance> coreIds = new ArrayList<>(archi.getOperatorComponentInstances());
    final List<ComponentInstance> comNodeIds = archi.getCommunicationComponentInstances();
    // Set default values for constraints, timings and simulation parameters
    if (algoURL.endsWith(ScenariosGenerator.PI_GRAPH_EXT)) {
      fillPiScenario(scenario, archi, piGraph);
    }
    // Add a main core (first of the list)
    if (!coreIds.isEmpty()) {
      scenario.getSimulationInfo().setMainOperator(coreIds.get(0));
    }
    // Add a main com node (first of the list)
    if (!comNodeIds.isEmpty()) {
      scenario.getSimulationInfo().setMainComNode(comNodeIds.get(0));
    }
    // Add a average transfer size
    scenario.getSimulationInfo().setAverageDataSize(ScenarioConstants.DEFAULT_AVG_DATA_TRANSFER_SIZE.getValue());
    // Set the default data type sizes
    for (final Fifo f : scenario.getAlgorithm().getAllFifos()) {
      final String typeName = f.getType();
      scenario.getSimulationInfo().getDataTypes().put(typeName,
          DefaultTypeSizes.getInstance().getTypeSizeOrDefault(typeName));
    }

    scenario.setCodegenDirectory("/" + project.getName() + "/Code/generated/");
    return scenario;
  }

  /**
   * Set default values to constraints and timings of a PreesmScenario wrt. a PiSDF algorithm and an architecture
   *
   * @param scenario
   *          the PreesmScenario to fill
   * @param archi
   *          the Design to take into account
   * @param algoURL
   *          the path to the PiGraph to take into account
   * @throws CoreException
   *           the core exception
   */
  private void fillPiScenario(final Scenario scenario, final Design archi, final PiGraph piGraph) {
    // Get com nodes and cores names
    scenario.setAlgorithm(piGraph);
    final List<ComponentInstance> coreIds = new ArrayList<>(archi.getOperatorComponentInstances());

    // for all different type of cores, add default timing
    for (final Component opId : archi.getProcessingElements()) {
      for (final AbstractActor aa : piGraph.getAllActors()) {
        scenario.getTimings().setExecutionTime(aa, opId, ScenarioConstants.DEFAULT_TIMING_TASK.getValue());
      }
    }
    // for all different type of cores, allow mapping on it
    for (final ComponentInstance coreId : coreIds) {
      for (final AbstractActor actor : piGraph.getAllActors()) {
        // Add constraint: aa can be run on ci
        scenario.getConstraints().addConstraint(coreId, actor);
      }
      // Add special actors operator id (all cores can execute special
      // actors)
      scenario.getSimulationInfo().addSpecialVertexOperator(coreId);
    }

  }

  /**
   * Generates a set of PreesmScenario from an IProject and save them in a folder.
   *
   * @throws CoreException
   *           the core exception
   * @throws FileNotFoundException
   *           the file not found exception
   */
  public void generateAndSaveScenarios() throws CoreException, FileNotFoundException {
    saveScenarios(generateScenarios(), scenarioDir);
  }

  /**
   * Save a set of PreesmScenarios in a given IFolder.
   *
   * @param scenarios
   *          the set of PreesmScenarios to save
   * @param scenarioDir
   *          the IFolder where to save the PreesmScenarios
   * @throws CoreException
   *           the core exception
   */
  public void saveScenarios(final Set<Scenario> scenarios, final IFolder scenarioDir) throws CoreException {
    for (final Scenario scenario : scenarios) {
      final String scenarioName = scenario.getScenarioName();
      final IPath scenarioPath = new Path(scenarioName).addFileExtension("scenario");
      final IFile scenarioFile = scenarioDir.getFile(scenarioPath);
      if (!scenarioFile.exists()) {
        scenarioFile.create(null, false, null);
      }
      saveScenario(scenario, scenarioFile);
    }
  }

  /**
   * Save a given PreesmScenario in a given IFile.
   *
   * @param scenario
   *          the PreesmScenario to save
   * @param scenarioFile
   *          the IFile in which to save the PreesmScenario
   */
  public void saveScenario(final Scenario scenario, final IFile scenarioFile) {
    final ScenarioWriter writer = new ScenarioWriter(scenario);
    final Document generateScenarioDOM = writer.generateScenarioDOM();
    try (final ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
      DomUtil.writeDocument(generateScenarioDOM, byteStream);
      scenarioFile.setContents(new ByteArrayInputStream(byteStream.toByteArray()), true, false,
          new NullProgressMonitor());
    } catch (final IOException | CoreException e) {
      throw new PreesmFrameworkException(e);
    }
  }
}
