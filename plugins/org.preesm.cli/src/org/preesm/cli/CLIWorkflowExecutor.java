/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2018)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
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
package org.preesm.cli;

import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.preesm.commons.logger.CLIWorkflowLogger;
import org.preesm.workflow.AbstractWorkflowExecutor;

/**
 * IApplication to execute PREESM workflows through command line interface.
 *
 * @author cguy
 *
 *         Code adapted from ORCC (net.sf.orcc.backends, https://github.com/orcc/orcc)
 * @author Antoine Lorence
 */
public class CLIWorkflowExecutor extends AbstractWorkflowExecutor implements IApplication {

  private static final int EXIT_ERROR = 1;

  /** Project containing the. */
  protected IProject project;

  /** The Constant workflowDir. */
  private static final String workflowDir = "/Workflows";

  /** The Constant workflowExt. */
  private static final String workflowExt = "workflow";

  /** The Constant scenarioDir. */
  private static final String scenarioDir = "/Scenarios";

  /** The Constant scenarioExt. */
  private static final String scenarioExt = "scenario";

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
   */
  @Override
  public Object start(final IApplicationContext context) throws Exception {

    // avoid printing whole JVM status when failing
    System.setProperty(IApplicationContext.EXIT_DATA_PROPERTY, "");
    final Options options = getCommandLineOptions();

    try {
      final CommandLineParser parser = new PosixParser();

      final String cliOpts = StringUtils
          .join((Object[]) context.getArguments().get(IApplicationContext.APPLICATION_ARGS), " ");

      // parse the command line arguments
      final CommandLine line = parser.parse(options,
          (String[]) context.getArguments().get(IApplicationContext.APPLICATION_ARGS));
      final boolean isDebug = line.hasOption('d');
      this.setDebug(isDebug);
      this.setLogger(new CLIWorkflowLogger(isDebug));

      getLogger().log(Level.FINE, "Starting workflows execution");
      getLogger().log(Level.FINE, "Command line arguments: " + cliOpts);

      if (line.getArgs().length != 1) {
        throw new ParseException("Expected project name as first argument", 0);
      }
      // Get the project containing the scenarios and workflows to execute
      final String projectName = line.getArgs()[0];
      final IWorkspace workspace = ResourcesPlugin.getWorkspace();
      final IWorkspaceRoot root = workspace.getRoot();
      this.project = root.getProject(new Path(projectName).lastSegment());

      // Refresh the project
      this.project.refreshLocal(IResource.DEPTH_INFINITE, null);

      // Handle options

      // Set of workflows to execute
      Set<String> workflowPaths = new LinkedHashSet<>();
      // Set of scenarios to execute
      Set<String> scenarioPaths = new LinkedHashSet<>();

      String workflowPath = line.getOptionValue('w');
      String scenarioPath = line.getOptionValue('s');
      // If paths to workflow and scenario are not specified using
      // options, find them in the project given as arguments
      if (workflowPath == null) {
        // If there is no workflow path specified, execute all the
        // workflows (files with workflowExt) found in workflowDir of
        // the project
        workflowPaths = getAllFilePathsIn(CLIWorkflowExecutor.workflowExt, this.project,
            CLIWorkflowExecutor.workflowDir);
      } else {
        // Otherwise, format the workflowPath and execute it
        if (!workflowPath.contains(projectName)) {
          workflowPath = projectName + CLIWorkflowExecutor.workflowDir + "/" + workflowPath;
        }
        if (!workflowPath.endsWith(CLIWorkflowExecutor.workflowExt)) {
          workflowPath = workflowPath + "." + CLIWorkflowExecutor.workflowExt;
        }
        workflowPaths.add(workflowPath);
      }

      if (scenarioPath == null) {
        // If there is no scenario path specified, execute all the
        // scenarios (files with scenarioExt) found in scenarioDir of
        // the project
        scenarioPaths = getAllFilePathsIn(CLIWorkflowExecutor.scenarioExt, this.project,
            CLIWorkflowExecutor.scenarioDir);
      } else {
        // Otherwise, format the scenarioPath and execute it
        scenarioPath = this.project.getName() + CLIWorkflowExecutor.scenarioDir + "/" + scenarioPath;
        if (!scenarioPath.endsWith(CLIWorkflowExecutor.scenarioExt)) {
          scenarioPath = scenarioPath + "." + CLIWorkflowExecutor.scenarioExt;
        }
        scenarioPaths.add(scenarioPath);
      }

      getLogger().log(Level.FINE, "Launching workflows execution");
      // Launch the execution of the workflos with the scenarios
      for (final String wPath : workflowPaths) {
        for (final String sPath : scenarioPaths) {
          if (!execute(wPath, sPath, null)) {
            final String message = "Workflow " + wPath + " did not complete its execution normally with scenario "
                + sPath + ".";
            getLogger().log(Level.SEVERE, message);
            return EXIT_ERROR;
          }
        }
      }

    } catch (final UnrecognizedOptionException uoe) {
      printUsage(options, uoe.getLocalizedMessage());
    } catch (final ParseException exp) {
      printUsage(options, exp.getLocalizedMessage());
    }
    return IApplication.EXIT_OK;
  }

  /**
   * Returns the path of all IFiles with extension found in the IFolder named folderName in the given IProject.
   *
   * @param extension
   *          the extension we are looking for
   * @param project
   *          the IProject in which we are looking for
   * @param folderName
   *          the name of the folder in which we are looking for
   * @return the set of paths relative to the workspace for all the files found
   * @throws CoreException
   *           the core exception
   */
  private Set<String> getAllFilePathsIn(final String extension, final IProject project, final String folderName)
      throws CoreException {
    final Set<String> filePaths = new LinkedHashSet<>();
    // Get the IFolder
    final IFolder folder = project.getFolder(folderName);
    // For each of its members
    for (final IResource resource : folder.members()) {
      // If this member is a IFile with the given extension
      if (resource instanceof IFile) {
        final IFile file = (IFile) resource;
        if (file.getProjectRelativePath().getFileExtension().equals(extension)) {
          // add its path to the return set
          filePaths.add((new Path(project.getName()).append(file.getProjectRelativePath())).toPortableString());
        }
      }
    }
    return filePaths;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.equinox.app.IApplication#stop()
   */
  @Override
  public void stop() {
  }

  /**
   * Set and return the command line options to follow the application.
   *
   * @return the command line options
   */
  private Options getCommandLineOptions() {
    final Options options = new Options();
    Option opt;

    opt = new Option("w", "workflow", true, "Workflow path");
    options.addOption(opt);

    opt = new Option("s", "scenario", true, "Scenario path");
    options.addOption(opt);

    opt = new Option("d", "debug", false, "Debug mode: print stack traces when failing");
    options.addOption(opt);
    return options;
  }

  /**
   * Print command line documentation on options.
   *
   * @param options
   *          options to print
   * @param parserMsg
   *          message to print
   */
  private void printUsage(final Options options, final String parserMsg) {

    String footer = "";
    if ((parserMsg != null) && !parserMsg.isEmpty()) {
      footer = "\nMessage of the command line parser :\n" + parserMsg;
    }

    final HelpFormatter helpFormatter = new HelpFormatter();
    helpFormatter.setWidth(80);
    helpFormatter.printHelp(getClass().getSimpleName() + " [options] ", "Valid options are :", options, footer);
  }

}
