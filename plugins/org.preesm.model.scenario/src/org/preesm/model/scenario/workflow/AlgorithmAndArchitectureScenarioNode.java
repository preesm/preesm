/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Jonathan Piat [jpiat@laas.fr] (2011)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2015)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2011 - 2012)
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
package org.preesm.model.scenario.workflow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.WorkspaceUtils;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.serialize.ScenarioParser;
import org.preesm.model.slam.Design;
import org.preesm.workflow.implement.AbstractScenarioImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * Implementing the new DFTools scenario node behavior for Preesm. This version generates an architecture with the
 * S-LAM2 meta-model type and an algorithm with the IBSDF type
 *
 * @author mpelcat
 * @author kdesnos
 */
@PreesmTask(id = "org.ietr.preesm.scenario.task", name = "scenario",

    outputs = { @Port(name = "scenario", type = Scenario.class), @Port(name = "architecture", type = Design.class),
        @Port(name = "PiMM", type = PiGraph.class) }

)
public class AlgorithmAndArchitectureScenarioNode extends AbstractScenarioImplementation {

  /**
   * The scenario node in Preesm outputs three elements: Algorithm (SDF or PiMM), architecture and scenario.
   *
   * @param path
   *          the path
   * @return the map
   * @throws PreesmException
   *           the workflow exception
   */
  @Override
  public Map<String, Object> extractData(final String path) {

    final Map<String, Object> outputs = new LinkedHashMap<>();

    // Retrieving the scenario from the given path
    final ScenarioParser scenarioParser = new ScenarioParser();

    final Path relativePath = new Path(path);
    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(relativePath);

    Scenario scenario;
    // Retrieving the algorithm
    PiGraph piAlgorithm = null;

    try {
      scenario = scenarioParser.parseXmlFile(file);
      piAlgorithm = scenario.getAlgorithm();
      applyScenarioParameterValues(scenario);
      // delete previous generated files
      final String codegenPath = scenario.getCodegenDirectory() + File.separator;
      erasePreviousFilesExtensions(codegenPath);

    } catch (FileNotFoundException | CoreException e) {
      throw new PreesmRuntimeException(e.getMessage());
    }

    // Retrieving the architecture
    final Design slamDesign = scenario.getDesign();

    if (!scenario.isProperlySet()) {
      throw new PreesmRuntimeException(
          "Scenario is not complete. Please make sure PiSDF algorithm and Slam design are set properly.");
    }

    outputs.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);
    outputs.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, piAlgorithm);
    outputs.put(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, slamDesign);
    return outputs;
  }

  private void applyScenarioParameterValues(final Scenario scenario) {
    for (final Entry<Parameter, String> paramValue : scenario.getParameterValues().entrySet()) {

      final String newValue = paramValue.getValue();
      if (newValue != null) {
        paramValue.getKey().setExpression(newValue);
      }
      // else, keep original expression (from PiSDF graph)
    }
  }

  private static void erasePreviousFilesExtensions(final String codegenPath) {
    try {
      final IWorkspace workspace = ResourcesPlugin.getWorkspace();
      WorkspaceUtils.updateWorkspace();
      final IFolder f = workspace.getRoot().getFolder(new Path(codegenPath));
      final IPath rawLocation = f.getRawLocation();
      if (rawLocation == null) {
        throw new PreesmRuntimeException("Could not find target project for given path [" + codegenPath
            + "]. Please change path in the scenario editor.");
      }
      final String osString = rawLocation.toOSString();
      final File folder = new File(osString);
      if (!folder.exists()) {
        folder.mkdirs();
        PreesmLogger.getLogger().info("Created missing target dir [" + folder.getAbsolutePath() + "] during codegen");
      } else {
        FileUtils.forceDelete(folder);
      }
      WorkspaceUtils.updateWorkspace();
      if (!f.exists()) {
        f.create(true, true, null);
      }
      WorkspaceUtils.updateWorkspace();
      if (!folder.exists()) {
        throw new FileNotFoundException("Target generation folder [" + folder.getAbsolutePath() + "] does not exist");
      }
    } catch (CoreException | IOException e) {
      throw new PreesmRuntimeException(
          "Could not access target directory [" + codegenPath + "] during code generation. " + e.getMessage(), e);
    } catch (final IllegalArgumentException e) {
      throw new PreesmRuntimeException("Codegen path: " + codegenPath + " is not valid."
          + "\nPlease update the code generation directory in the \"Codegen\" tab of the scenario editor "
          + "(e.g. \"/<project>/Code/generated/\").", e);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Scenario, algorithm and architecture parsing.";
  }

}
