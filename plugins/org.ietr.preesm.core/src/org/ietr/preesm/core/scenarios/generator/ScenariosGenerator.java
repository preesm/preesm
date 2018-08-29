/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
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
package org.ietr.preesm.core.scenarios.generator;

import java.io.FileNotFoundException;
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
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.architecture.slam.SlamPackage;
import org.ietr.dftools.architecture.slam.serialize.IPXACTResourceFactoryImpl;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.ScenarioUtils;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.core.scenario.serialize.ScenarioParser;
import org.ietr.preesm.core.scenario.serialize.ScenarioWriter;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.serialize.PiParser;

// TODO: Auto-generated Javadoc
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
  private static final String archiExt = "slam";

  /** The Constant piAlgoExt. */
  private static final String piAlgoExt = "pi";

  /** The Constant sdfAlgoExt. */
  private static final String sdfAlgoExt = "graphml";

  /** The Constant archiDirName. */
  private static final String archiDirName = "Archi";

  /** The Constant algoDirName. */
  private static final String algoDirName = "Algo";

  /** The Constant scenarioDirName. */
  private static final String scenarioDirName = "Scenarios";

  /**
   * Generates a set of PreesmScenario from an IProject.
   *
   * @param project
   *          the IProject containing the architectures and algorithms. project is supposed to have the
   *          PreesmProjectNature and to follow the standard Preesm folder hierarchy
   * @return a set of PreesmScenario, one for each possible pair of architecture and algorithm
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws CoreException
   *           the core exception
   * @throws FileNotFoundException
   *           the file not found exception
   */
  public Set<PreesmScenario> generateScenarios(final IProject project)
      throws InvalidModelException, CoreException, FileNotFoundException {
    final IFolder archiDir = project.getFolder(ScenariosGenerator.archiDirName);
    final IFolder algoDir = project.getFolder(ScenariosGenerator.algoDirName);
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
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws CoreException
   *           the core exception
   * @throws FileNotFoundException
   *           the file not found exception
   */
  public Set<PreesmScenario> generateScenarios(final IFolder archiDir, final IFolder algoDir)
      throws InvalidModelException, CoreException, FileNotFoundException {
    final Set<String> archis = new LinkedHashSet<>();
    final Set<String> algos = new LinkedHashSet<>();
    for (final IResource resource : archiDir.members()) {
      if (resource instanceof IFile) {
        final IFile file = (IFile) resource;
        if (file.getProjectRelativePath().getFileExtension().equals(ScenariosGenerator.archiExt)) {
          archis.add(file.getFullPath().toOSString());
        }
      }
    }
    for (final IResource resource : algoDir.members()) {
      if (resource instanceof IFile) {
        final IFile file = (IFile) resource;
        final String ext = file.getProjectRelativePath().getFileExtension();
        if (ext.equals(ScenariosGenerator.piAlgoExt) || ext.equals(ScenariosGenerator.sdfAlgoExt)) {
          algos.add(file.getFullPath().toOSString());
        }
      }
    }
    return generateScenarios(archis, algos);
  }

  /**
   * Generates a set of PreesmScenario from a set of architectures URL and a set of algorithms URL.
   *
   * @param archis
   *          the set of architectures URL
   * @param algos
   *          the set of algorithms URL
   * @return a set of PreesmScenario, one for each possible pair of architecture and algorithm
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws CoreException
   *           the core exception
   * @throws FileNotFoundException
   *           the file not found exception
   */
  private Set<PreesmScenario> generateScenarios(final Set<String> archis, final Set<String> algos)
      throws InvalidModelException, CoreException, FileNotFoundException {
    final Set<PreesmScenario> scenarios = new LinkedHashSet<>();
    for (final String archiURL : archis) {
      for (final String algoURL : algos) {
        scenarios.add(createScenario(archiURL, algoURL));
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
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws CoreException
   *           the core exception
   * @throws FileNotFoundException
   *           the file not found exception
   */
  private PreesmScenario createScenario(final String archiURL, final String algoURL)
      throws InvalidModelException, CoreException, FileNotFoundException {
    // Create a new PreesmScenario
    final PreesmScenario scenario = new PreesmScenario();
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
    final Design archi = ScenarioParser.parseSlamDesign(archiURL);
    scenario.setArchitectureURL(archiURL);

    // Get com nodes and cores names
    final List<String> coreIds = new ArrayList<>(DesignTools.getOperatorInstanceIds(archi));
    final List<String> comNodeIds = new ArrayList<>(DesignTools.getComNodeInstanceIds(archi));
    // Set default values for constraints, timings and simulation parameters
    if (algoURL.endsWith(ScenariosGenerator.piAlgoExt)) {
      fillPiScenario(scenario, archi, algoURL);
    } else if (algoURL.endsWith(ScenariosGenerator.sdfAlgoExt)) {
      fillSDFScenario(scenario, archi, algoURL);
    }
    // Add a main core (first of the list)
    scenario.getSimulationManager().setMainOperatorName(coreIds.get(0));
    // Add a main com node (first of the list)
    scenario.getSimulationManager().setMainComNodeName(comNodeIds.get(0));
    // Add a average transfer size
    scenario.getSimulationManager().setAverageDataSize(1000);
    // Add a single data type uint
    scenario.getSimulationManager().putDataType(new DataType("uint", 1));

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
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws CoreException
   *           the core exception
   */
  private void fillPiScenario(final PreesmScenario scenario, final Design archi, final String algoURL)
      throws InvalidModelException, CoreException {
    final PiGraph algo = PiParser.getPiGraph(algoURL);
    scenario.setAlgorithmURL(algoURL);
    // Get com nodes and cores names
    final List<String> coreIds = new ArrayList<>(DesignTools.getOperatorInstanceIds(archi));

    // for all different type of cores
    for (final String opId : DesignTools.getOperatorComponentIds(archi)) {
      for (final AbstractActor aa : algo.getAllActors()) {
        // Add timing: aa run on ci in 10000
        scenario.getTimingManager().addTiming(new Timing(opId, aa.getVertexPath(), 10000));
      }
    }
    for (final String coreId : coreIds) {
      for (final AbstractActor aa : algo.getAllActors()) {
        // Add constraint: aa can be run on ci
        scenario.getConstraintGroupManager().addConstraint(coreId, aa);
      }
      // Add special actors operator id (all cores can execute special
      // actors)
      scenario.getSimulationManager().addSpecialVertexOperatorId(coreId);
    }
  }

  /**
   * Set default values to constraints and timings of a PreesmScenario wrt. a SDF algorithm and an architecture
   *
   * @param scenario
   *          the PreesmScenario to fill
   * @param archi
   *          the Design to take into account
   * @param algoURL
   *          the path to the SDFGraph to take into account
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws InvalidModelException
   *           the invalid model exception
   */
  private void fillSDFScenario(final PreesmScenario scenario, final Design archi, final String algoURL)
      throws FileNotFoundException, InvalidModelException {
    final SDFGraph algo = ScenarioParser.getSDFGraph(algoURL);
    scenario.setAlgorithmURL(algoURL);
    // Get com nodes and cores names
    final List<String> coreIds = new ArrayList<>(DesignTools.getOperatorInstanceIds(archi));

    // Set default values for constraints, timings and simulation parameters
    // for all different type of cores
    for (final String opId : DesignTools.getOperatorComponentIds(archi)) {
      for (final SDFAbstractVertex aa : algo.getAllVertices()) {
        // Add timing: aa run on ci in 10000
        scenario.getTimingManager().addTiming(new Timing(opId, aa.getInfo(), 10000));
      }
    }
    for (final String coreId : coreIds) {
      for (final SDFAbstractVertex aa : algo.getAllVertices()) {
        // Add constraint: aa can be run on ci
        scenario.getConstraintGroupManager().addConstraint(coreId, aa);
      }
      // Add special actors operator id (all cores can execute special
      // actors)
      scenario.getSimulationManager().addSpecialVertexOperatorId(coreId);
    }
  }

  /**
   * Generates a set of PreesmScenario from an IProject and save them in a folder.
   *
   * @param project
   *          the IProject containing the architectures and algorithms. project is supposed to have the
   *          PreesmProjectNature and to follow the standard Preesm folder hierarchy
   * @throws CoreException
   *           the core exception
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws FileNotFoundException
   *           the file not found exception
   */
  public void generateAndSaveScenarios(final IProject project)
      throws CoreException, InvalidModelException, FileNotFoundException {
    final IFolder scenarioDir = project.getFolder(ScenariosGenerator.scenarioDirName);
    saveScenarios(generateScenarios(project), scenarioDir);
  }

  /**
   * Generate a set of PreesmScenarios from an architecture folder and from an algorithm folder.
   *
   * @param archiDir
   *          the IFolder containing the architectures
   * @param algoDir
   *          the IFolder containing the algorithms
   * @param scenarioDir
   *          the IFolder where to save the generated PreesmScenarios
   * @throws CoreException
   *           the core exception
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws FileNotFoundException
   *           the file not found exception
   */
  public void generateAndSaveScenarios(final IFolder archiDir, final IFolder algoDir, final IFolder scenarioDir)
      throws CoreException, InvalidModelException, FileNotFoundException {
    saveScenarios(generateScenarios(archiDir, algoDir), scenarioDir);
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
  private void saveScenarios(final Set<PreesmScenario> scenarios, final IFolder scenarioDir) throws CoreException {
    for (final PreesmScenario scenario : scenarios) {
      final String scenarioName = ScenarioUtils.getScenarioName(scenario);
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
  private void saveScenario(final PreesmScenario scenario, final IFile scenarioFile) {
    final ScenarioWriter writer = new ScenarioWriter(scenario);
    writer.generateScenarioDOM();
    writer.writeDom(scenarioFile);
  }
}
