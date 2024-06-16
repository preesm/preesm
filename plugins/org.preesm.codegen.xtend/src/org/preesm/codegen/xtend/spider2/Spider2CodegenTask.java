/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2020)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
package org.preesm.codegen.xtend.spider2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.preesm.codegen.xtend.spider2.utils.Spider2Config;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * The Class Spider2CodegenTask.
 */
@PreesmTask(id = "org.preesm.codegen.xtend.Spider2CodegenTask", name = "Spider2 Codegen", category = "Code Generation",

    inputs = { @Port(name = "scenario", type = Scenario.class) },

    shortDescription = "Generate code for spider2 library for dynamic PiSDF.",

    parameters = {
        @Parameter(name = "generate archi", description = "Whether to generate archi file from slam description or not",
            values = { @Value(name = "true / false", effect = "(Default = true)") }),
        @Parameter(name = "generate cmakelist", description = "Whether to generate CMakeList.txt or not",
            values = { @Value(name = "true / false", effect = "(Default = true)") }),
        @Parameter(name = "move includes",
            description = "Whether to move the includes of the project into the generated include folder",
            values = { @Value(name = "true / false", effect = "(Default = false)") }),
        @Parameter(name = "scheduler", description = "Runtime scheduler to use.",
            values = { @Value(name = "LIST", effect = "(Default)"), @Value(name = "GREEDY") }),
        @Parameter(name = "mapper", description = "Runtime mapper to use.",
            values = { @Value(name = "BEST_FIT", effect = "(Default)"), @Value(name = "ROUND_ROBIN") }),
        @Parameter(name = "allocator", description = "Runtime fifo allocator to use.",
            values = { @Value(name = "DEFAULT", effect = "(Default)"), @Value(name = "DEFAULT_NOSYNC") }),
        @Parameter(name = "executionPolicy", description = "Runtime execution policy to use.",
            values = { @Value(name = "DELAYED", effect = "(Default)"), @Value(name = "JIT") }),
        @Parameter(name = "runtime", description = "Runtime algorithm to use.",
            values = { @Value(name = "PISDF_BASED", effect = "(Default)"), @Value(name = "SRDAG_BASED") }),
        @Parameter(name = "runMode", description = "Run mode to use.",
            values = { @Value(name = "LOOP", effect = "(Default)"), @Value(name = "INFINITE"),
                @Value(name = "EXTERN_LOOP") }),
        @Parameter(name = "use-verbose", description = "Whether to enable verbose log.",
            values = { @Value(name = "true / false", effect = "") }),
        @Parameter(name = "enable-srdag-optims", description = "Whether to optimize the srdag-graphs at runtime or not",
            values = { @Value(name = "true / false", effect = "") }) })
public class Spider2CodegenTask extends AbstractTaskImplementation {
  /** The Constant PARAM_GENERATE_ARCHI_FILE. */
  public static final String PARAM_GENERATE_ARCHI_FILE = "generate archi";
  /** The Constant PARAM_GENERATE_CMAKELIST. */
  public static final String PARAM_GENERATE_CMAKELIST  = "generate cmakelist";
  /** The Constant PARAM_GENERATE_CMAKELIST. */
  public static final String PARAM_MOVE_INCLUDES       = "move includes";
  /** The Constant PARAM_SCHEDULER. */
  public static final String PARAM_SCHEDULER           = "scheduler";
  /** The Constant PARAM_MAPPER. */
  public static final String PARAM_MAPPER              = "mapper";
  /** The Constant PARAM_ALLOCATOR. */
  public static final String PARAM_ALLOCATOR           = "allocator";
  /** The Constant PARAM_EXEC_POLICY. */
  public static final String PARAM_EXEC_POLICY         = "executionPolicy";
  /** The Constant PARAM_RUN_MODE. */
  public static final String PARAM_RUN_MODE            = "runMode";
  /** The Constant PARAM_ALGO. */
  public static final String PARAM_ALGO                = "runtime";
  /** The Constant PARAM_VERBOSE. */
  public static final String PARAM_VERBOSE             = "use-verbose";
  /** The Constant PARAM_GRAPH_OPTIMS. */
  public static final String PARAM_GRAPH_OPTIMS        = "enable-srdag-optims";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    // Retrieve inputs
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    // Get the workspace
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    if (scenario.getCodegenDirectory() == null) {
      throw new PreesmRuntimeException("Codegen path has not been specified in scenario, cannot go further.");
    }

    // Get the name of the folder for code generation
    final String directoryPath = scenario.getCodegenDirectory();
    if ((directoryPath.lastIndexOf('/') + 1) == (directoryPath.length() - 1)) {
      throw new PreesmRuntimeException("Error:Codegen folder should not end with a /");
    }
    final String codegenPath = directoryPath + '/';
    if (codegenPath.equals("/")) {
      throw new PreesmRuntimeException("Error: A Codegen folder must be specified in Scenario");
    }
    // If the codegen folder does not exist make it, if it exists clears it
    final File folder = cleanCodegenFolder(workspace, codegenPath);

    // Get top graph
    final PiGraph topGraph = scenario.getAlgorithm();

    // Parse the pigraph
    final Spider2Codegen codegen = new Spider2Codegen(scenario, topGraph, folder);
    codegen.init();

    // Get Spider2 config
    final Spider2Config spiderConfig = new Spider2Config(parameters);

    // If the codegen path does not contain src or include folder make them.
    // If the folders are not empty change .c extensions to .cpp
    codegen.makeFolders(workspace);

    if (spiderConfig.getMoveIncludes()) {
      codegen.moveIncludesToFolder(workspace);
    }

    // Generate code for the different pi graph
    codegen.generateGraphCodes();

    // Generate code for the architecture (if needed)
    if (spiderConfig.getGenerateArchiFile()) {
      PreesmLogger.getLogger().log(Level.INFO, "Generating architecture description code.");
      codegen.generateArchiCode();
    }

    // Generate code for main application header
    codegen.generateApplicationHeader();

    // Generate code for the runtime kernels
    codegen.generateKernelCode();

    // Generate code for the main entry point (if top level graph does not have input nor output interfaces)
    if (topGraph.getDataInputInterfaces().isEmpty() && topGraph.getDataOutputInterfaces().isEmpty()) {
      PreesmLogger.getLogger().log(Level.INFO, "Generating stand-alone application code.");
      codegen.generateMainCode(spiderConfig);
    } else {
      PreesmLogger.getLogger().log(Level.INFO, "Generating software acceleration code.");
    }

    // Generate default CMakeList.txt (if needed)
    if (spiderConfig.getGenerateCMakeList()) {
      PreesmLogger.getLogger().log(Level.INFO, "Generating default CMakeList.txt.");
      codegen.generateCMakeList();
    }

    codegen.end();

    return new LinkedHashMap<>();
  }

  private static final File cleanCodegenFolder(final IWorkspace workspace, final String path) {
    final IFolder f = workspace.getRoot().getFolder(new Path(path));
    final IPath rawLocation = f.getRawLocation();
    if (rawLocation == null) {
      throw new PreesmRuntimeException(
          "Could not find target project for given path [" + path + "]. Please change path in the scenario editor.");
    }
    final File folder = new File(rawLocation.toOSString());
    folder.mkdirs();
    if (folder.isDirectory()) {
      // clean the folder
      for (final File file : folder.listFiles()) {
        try {
          Files.delete(file.toPath());
        } catch (final IOException e) {
          PreesmLogger.getLogger().fine(() -> "Could not delete file [" + file.toPath().toString() + "].");
        }
      }
    }
    return folder;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    // Create an empty parameters map
    return new LinkedHashMap<>();
  }

  @Override
  public String monitorMessage() {
    return "Generating C++ code for Spider2 Library.";
  }

}
