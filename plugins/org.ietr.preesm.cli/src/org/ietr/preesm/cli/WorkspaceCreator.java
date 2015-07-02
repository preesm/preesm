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

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.ietr.dftools.workflow.tools.CLIWorkflowLogger;
import org.ietr.preesm.ui.wizards.PreesmProjectNature;
import org.ietr.preesm.utils.files.FilesManager;

/**
 * This application take a folder path in argument and create a valid Eclipse
 * workspace from .project files found in it with the Preesm project nature. The
 * .metadata (workspace information) folder is created in the current Eclipse
 * workspace.
 * 
 * In command-line, workspace folder can be set by using
 * "-data &lt;workspace&gt;" argument.
 * 
 * @author cguy
 * 
 *         Code adapted from ORCC (net.sf.orcc.backends,
 *         https://github.com/orcc/orcc)
 * @author alorence
 * 
 */
public class WorkspaceCreator implements IApplication {

	private final IProgressMonitor progressMonitor;
	private final IWorkspace workspace;
	private boolean wasAutoBuildEnabled;
	private final String nature;

	public WorkspaceCreator() {

		progressMonitor = new NullProgressMonitor();

		nature = PreesmProjectNature.ID;

		workspace = ResourcesPlugin.getWorkspace();
		wasAutoBuildEnabled = false;
	}

	/**
	 * Open searchFolder and try to find .project files inside it. Then, try to
	 * create an eclipse projects and add it to the current workspace.
	 * 
	 * @param searchFolder
	 * @throws CoreException
	 */
	private void searchForProjects(File searchFolder) throws CoreException {

		if (!searchFolder.isDirectory()) {
			throw new RuntimeException("Bad path to search project: "
					+ searchFolder.getPath());
		} else {
			File[] children = searchFolder.listFiles();
			for (File child : children) {
				if (child.isDirectory()) {
					searchForProjects(child);
				} else if (child.getName().equals(
						IProjectDescription.DESCRIPTION_FILE_NAME)) {
					IPath projectPath = new Path(child.getAbsolutePath());

					IProjectDescription description = workspace
							.loadProjectDescription(projectPath);

					if (description.hasNature(nature)) {
						IProject project = workspace.getRoot().getProject(
								description.getName());

						if (project.exists()) {
							project.delete(false, progressMonitor);
							CLIWorkflowLogger
									.traceln("A project named "
											+ project.getName()
											+ " is already registered, "
											+ "deleting previous project from Workspace: ");
						}
						project.create(description, progressMonitor);
						project.open(progressMonitor);
						CLIWorkflowLogger.traceln("New project registered: "
								+ project.getName());
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.
	 * IApplicationContext)
	 */
	@Override
	public Object start(IApplicationContext context) {
		Map<?, ?> map = context.getArguments();

		String[] args = (String[]) map
				.get(IApplicationContext.APPLICATION_ARGS);

		if (args.length == 1) {

			try {

				wasAutoBuildEnabled = CommandLineUtil
						.disableAutoBuild(workspace);

				final String path = FilesManager.sanitize(args[0]);
				File searchPath = new File(path).getCanonicalFile();
				CLIWorkflowLogger.traceln("Register projects from \""
						+ searchPath.getAbsolutePath() + "\" to workspace \""
						+ workspace.getRoot().getLocation() + "\"");
				searchForProjects(searchPath);

				// Avoid warning messages of type "The workspace exited
				// with unsaved changes in the previous session" the next
				// time an IApplication (FrontendCli) will be launched
				// This method can be called ONLY if auto-building has
				// been disabled
				workspace.save(true, progressMonitor);

				final IJobManager manager = Job.getJobManager();
				int i = 0;
				while (!manager.isIdle()) {
					CLIWorkflowLogger.traceln("Waiting for completion of"
							+ " currently running jobs - " + ++i);
					Thread.sleep(500);
				}

			} catch (CoreException e) {
				CLIWorkflowLogger.severeln(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				CLIWorkflowLogger.severeln(e.getMessage());
				e.printStackTrace();
			} catch (InterruptedException e) {
				CLIWorkflowLogger.severeln(e.getMessage());
				e.printStackTrace();
			} finally {
				try {
					if (wasAutoBuildEnabled) {
						CommandLineUtil.enableAutoBuild(workspace);
						wasAutoBuildEnabled = false;
					}
					return IApplication.EXIT_OK;
				} catch (CoreException e) {
					CLIWorkflowLogger.severeln(e.getMessage());
					e.printStackTrace();
				}
			}
		} else {
			CLIWorkflowLogger
					.severeln("Please add the path to a directories containing projects.");
		}

		return IApplication.EXIT_RESTART;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
	public void stop() {
	}

}
