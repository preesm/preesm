package org.ietr.preesm.cli;

import java.text.ParseException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.ietr.dftools.workflow.tools.CLIWorkflowLogger;
import org.ietr.preesm.core.scenarios.generator.ScenariosGenerator;

public class CLIScenariosGenerator implements
		IApplication {

	/**
	 * Project containing the
	 */
	protected IProject project;

	@Override
	public Object start(IApplicationContext context) throws Exception {
		Options options = getCommandLineOptions();

		try {
			CommandLineParser parser = new PosixParser();

			String cliOpts = StringUtils.join((Object[]) context.getArguments()
					.get(IApplicationContext.APPLICATION_ARGS), " ");
			CLIWorkflowLogger.traceln("Starting scenarios generation");
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

			new ScenariosGenerator().generateAndSaveScenarios(project);

		} catch (UnrecognizedOptionException uoe) {
			printUsage(options, uoe.getLocalizedMessage());
		} catch (ParseException exp) {
			printUsage(options, exp.getLocalizedMessage());
		}
		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
	}

	/**
	 * Set and return the command line options to follow the application
	 */
	private Options getCommandLineOptions() {
		Options options = new Options();
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
}
