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
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.cli;

import java.text.ParseException;
import java.util.HashSet;
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
import org.ietr.dftools.ui.workflow.tools.DFToolsWorkflowLogger;
import org.ietr.dftools.workflow.AbstractWorkflowExecutor;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.messages.WorkflowMessages;
import org.ietr.dftools.workflow.tools.CLIWorkflowLogger;

/**
 * IApplication to execute PREESM workflows through command line interface
 * 
 * 
 * @author cguy
 * 
 *         Code adapted from ORCC (net.sf.orcc.backends,
 *         https://github.com/orcc/orcc)
 * @author Antoine Lorence
 * 
 */
public class CLIWorkflowExecutor extends AbstractWorkflowExecutor implements
		IApplication {

	/**
	 * Project containing the
	 */
	protected IProject project;

	private static final String workflowDir = "/Workflows";
	private static final String workflowExt = "workflow";
	private static final String scenarioDir = "/Scenarios";
	private static final String scenarioExt = "scenario";

	@Override
	public Object start(IApplicationContext context) throws Exception {
		Options options = getCommandLineOptions();

		try {
			CommandLineParser parser = new PosixParser();

			String cliOpts = StringUtils.join((Object[]) context.getArguments()
					.get(IApplicationContext.APPLICATION_ARGS), " ");
			CLIWorkflowLogger.traceln("Starting workflows execution");
			CLIWorkflowLogger.traceln("Command line arguments: " + cliOpts);

			// parse the command line arguments
			CommandLine line = parser.parse(options, (String[]) context
					.getArguments().get(IApplicationContext.APPLICATION_ARGS));

			if (line.getArgs().length != 1) {
				throw new ParseException(
						"Expected project name as first argument", 0);
			}
			// Get the project containing the scenarios and workflows to execute
			String projectName = line.getArgs()[0];
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			project = root.getProject(new Path(projectName).lastSegment());
			
			// Refresh the project
			project.refreshLocal(IResource.DEPTH_INFINITE, null);

			// Handle options
			String workflowPath = line.getOptionValue('w');
			String scenarioPath = line.getOptionValue('s');

			// Set of workflows to execute
			Set<String> workflowPaths = new HashSet<String>();
			// Set of scenarios to execute
			Set<String> scenarioPaths = new HashSet<String>();

			// If paths to workflow and scenario are not specified using
			// options, find them in the project given as arguments
			if (workflowPath == null) {
				// If there is no workflow path specified, execute all the
				// workflows (files with workflowExt) found in workflowDir of
				// the project
				workflowPaths = getAllFilePathsIn(workflowExt, project,
						workflowDir);
			} else {
				// Otherwise, format the workflowPath and execute it
				if (!workflowPath.contains(projectName))
					workflowPath = projectName + workflowDir + "/"
							+ workflowPath;
				if (!workflowPath.endsWith(workflowExt))
					workflowPath = workflowPath + "." + workflowExt;
				workflowPaths.add(workflowPath);
			}

			if (scenarioPath == null) {
				// If there is no scenario path specified, execute all the
				// scenarios (files with scenarioExt) found in scenarioDir of
				// the project
				scenarioPaths = getAllFilePathsIn(scenarioExt, project,
						scenarioDir);
			} else {
				// Otherwise, format the scenarioPath and execute it
				if (!scenarioPath.contains(projectName))
					scenarioPath = projectName + scenarioDir + "/"
							+ scenarioPath;
				if (!scenarioPath.endsWith(scenarioExt))
					scenarioPath = scenarioPath + "." + scenarioExt;
				scenarioPaths.add(scenarioPath);
			}

			CLIWorkflowLogger.traceln("Launching workflows execution");
			// Launch the execution of the workflos with the scenarios
			DFToolsWorkflowLogger.runFromCLI();
			for (String wPath : workflowPaths) {
				for (String sPath : scenarioPaths) {
					CLIWorkflowLogger
							.traceln("Launching execution of workflow: "
									+ wPath + " with scenario: " + sPath);
					if (!execute(wPath, sPath, null)) {
						throw new WorkflowException(
								"Workflow "
										+ wPath
										+ " did not complete its execution normally with scenario "
										+ sPath + ".");
					}
				}
			}

		} catch (UnrecognizedOptionException uoe) {
			printUsage(options, uoe.getLocalizedMessage());
		} catch (ParseException exp) {
			printUsage(options, exp.getLocalizedMessage());
		}
		return IApplication.EXIT_OK;
	}

	/**
	 * Returns the path of all IFiles with extension found in the IFolder named
	 * folderName in the given IProject
	 * 
	 * @param extension
	 *            the extension we are looking for
	 * @param project
	 *            the IProject in which we are looking for
	 * @param folderName
	 *            the name of the folder in which we are looking for
	 * @return the set of paths relative to the workspace for all the files
	 *         found
	 * @throws CoreException
	 */
	private Set<String> getAllFilePathsIn(String extension, IProject project,
			String folderName) throws CoreException {
		Set<String> filePaths = new HashSet<String>();
		// Get the IFolder
		IFolder folder = project.getFolder(folderName);
		// For each of its members
		for (IResource resource : folder.members()) {
			// If this member is a IFile with the given extension
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				if (file.getProjectRelativePath().getFileExtension()
						.equals(extension)) {
					// add its path to the return set
					filePaths.add((new Path(project.getName()).append(file
							.getProjectRelativePath())).toPortableString());
				}
			}
		}
		return filePaths;
	}

	@Override
	public void stop() {
	}

	/**
	 * Set and return the command line options to follow the application
	 */
	private Options getCommandLineOptions() {
		Options options = new Options();
		Option opt;

		opt = new Option("w", "workflow", true, "Workflow path");
		options.addOption(opt);

		opt = new Option("s", "scenario", true, "Scenario path");
		options.addOption(opt);

		return options;
	}

	/**
	 * Print command line documentation on options
	 * 
	 * @param options
	 *            options to print
	 * @param parserMsg
	 *            message to print
	 */
	private void printUsage(Options options, String parserMsg) {

		String footer = "";
		if (parserMsg != null && !parserMsg.isEmpty()) {
			footer = "\nMessage of the command line parser :\n" + parserMsg;
		}

		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.setWidth(80);
		helpFormatter.printHelp(getClass().getSimpleName() + " [options] ",
				"Valid options are :", options, footer);
	}

	/**
	 * Log method for workflow execution without eclipse UI
	 */
	@Override
	protected void log(Level level, String msgKey, String... variables) {
		CLIWorkflowLogger.logln(level,
				WorkflowMessages.getString(msgKey, variables));
	}
}
