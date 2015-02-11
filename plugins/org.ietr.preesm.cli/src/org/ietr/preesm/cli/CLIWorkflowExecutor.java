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
import java.util.ArrayList;
import java.util.List;
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
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.ietr.dftools.workflow.AbstractWorkflowExecutor;
import org.ietr.dftools.workflow.messages.WorkflowMessages;
import org.ietr.preesm.utils.log.PreesmLogger;

/**
 * IApplication to execute PREESM workflows through command line interface
 * 
 * 
 * @author cguy
 * 
 * Code adapted from ORCC (net.sf.orcc.backends, https://github.com/orcc/orcc)
 * @author Antoine Lorence
 * 
 */
public class CLIWorkflowExecutor extends AbstractWorkflowExecutor implements IApplication {

	/**
	 * Project to test
	 */
	protected IProject project;

	// private boolean debug;

	@Override
	public Object start(IApplicationContext context) throws Exception {
		Options options = getCommandLineOptions();

		try {
			CommandLineParser parser = new PosixParser();

			String cliOpts = StringUtils.join((Object[]) context.getArguments()
					.get(IApplicationContext.APPLICATION_ARGS), " ");
			PreesmLogger.traceln("Command line arguments: " + cliOpts);

			// parse the command line arguments
			CommandLine line = parser.parse(options, (String[]) context
					.getArguments().get(IApplicationContext.APPLICATION_ARGS));

			if (line.getArgs().length != 1) {
				throw new ParseException(
						"Expected project name as last argument", 0);
			}
			String projectName = line.getArgs()[0];
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			project = root.getProject(new Path(projectName).lastSegment());

			// Handle options
			String workflowPath = line.getOptionValue('w');
			String scenarioPath = line.getOptionValue('s');
			// debug = line.hasOption('d');

			// If paths to workflow and scenario are not specified using
			// options, find them in the project given as arguments
			if (workflowPath == null) {
				IFolder workflowsFolder = project.getFolder("/Workflows");
				List<IFile> workflows = new ArrayList<IFile>();
				for (IResource resource : workflowsFolder.members()) {
					if (resource instanceof IFile) {
						IFile file = (IFile) resource;
						if (file.getProjectRelativePath().getFileExtension()
								.equals("workflow"))
							workflows.add(file);
					}
				}
				if (workflows.size() == 1) {
					workflowPath = (project.getLocation().append(workflows.get(
							0).getProjectRelativePath())).toPortableString();
				} else
					PreesmLogger
							.warn("Test projects must contain one and only one workflow file, or the workflow file to be used must be specified using the -w option.");
			}

			if (scenarioPath == null) {
				IFolder scenariosFolder = project.getFolder("/Scenarios");
				List<IFile> scenarios = new ArrayList<IFile>();
				for (IResource resource : scenariosFolder.members()) {
					if (resource instanceof IFile) {
						IFile file = (IFile) resource;
						if (file.getProjectRelativePath().getFileExtension()
								.equals("scenario"))
							scenarios.add(file);
					}
				}
				if (scenarios.size() == 1) {
					workflowPath = (project.getLocation().append(scenarios.get(
							0).getProjectRelativePath())).toPortableString();
				} else
					PreesmLogger
							.warn("Test projects must contain one and only one scenario file, or the scenario file to be used must be specified using the -s option.");
			}

			// Launch the execution of the given workflow with the given
			// scenario
			execute(workflowPath, scenarioPath, null);

		} catch (UnrecognizedOptionException uoe) {
			printUsage(options, uoe.getLocalizedMessage());
		} catch (ParseException exp) {
			printUsage(options, exp.getLocalizedMessage());
		}
		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

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

		opt = new Option("d", "debug", false, "Enable debug mode");
		options.addOption(opt);

		return options;
	}

	/**
	 * Print command line documentation on options
	 * 
	 * @param context
	 * @param options
	 * @param parserMsg
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
		PreesmLogger.logln(level, WorkflowMessages.getString(msgKey, variables));
	}
}
