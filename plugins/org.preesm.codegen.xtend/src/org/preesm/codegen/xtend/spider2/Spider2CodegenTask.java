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
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * The Class Spider2CodegenTask.
 */
@PreesmTask(id = "org.preesm.codegen.xtend.Spider2CodegenTask", name = "Spider2 Codegen", category = "Code Generation",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),
        @Port(name = "architecture", type = Design.class) },

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
            values = { @Value(name = "bestfit_list_scheduling", effect = "(Default)"),
                @Value(name = "round_robin_list_scheduling"), @Value(name = "greedy_scheduling") }),
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
  /** The Constant PARAM_VERBOSE. */
  public static final String PARAM_VERBOSE             = "use-verbose";
  /** The Constant PARAM_GRAPH_OPTIMS. */
  public static final String PARAM_GRAPH_OPTIMS        = "enable-srdag-optims";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    // Retrieve inputs
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final PiGraph topGraph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
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

    // Parse the pigraph
    final Spider2Codegen codegen = new Spider2Codegen(scenario, architecture, topGraph, folder);
    // Get Spider2 config
    final Spider2Config spiderConfig = new Spider2Config(parameters);

    // If the codegen path does not contain src or include folder make them.
    // If the folders are not empty change .c extensions to .cpp
    codegen.makeIncludeAndSourceFolder(workspace);
    if (spiderConfig.getMoveIncludes()) {
      codegen.moveIncludesToFolder(workspace);
    }

    // Generate code for the different pi graph
    codegen.generateGraphCodes();

    // Generate code for the architecture (if needed)
    if (spiderConfig.getGenerateArchiFile()) {
      PreesmLogger.getLogger().log(Level.INFO, "Generating architecture description code.");
    }

    // Generate code for main application header
    codegen.generateApplicationHeader();

    // Generate code for the runtime kernels
    codegen.generateKernelHeader();

    // Generate code for the main entry point (if top level graph does not have input nor output interfaces)
    if (topGraph.getDataInputInterfaces().isEmpty() && topGraph.getDataOutputInterfaces().isEmpty()) {
      PreesmLogger.getLogger().log(Level.INFO, "Generating stand-alone application code.");
      codegen.generateMainCode();
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
      for (File file : folder.listFiles()) {
        try {
          Files.delete(file.toPath());
        } catch (IOException e) {
          PreesmLogger.getLogger().log(Level.FINE, "Could not delete file [" + file.toPath().toString() + "].");
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
