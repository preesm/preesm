/*
 * Copyright (c) 2009-2010, IETR/INSA of Rennes and EPFL
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   * Neither the name of the IETR/INSA of Rennes and EPFL nor the names of its
 *     contributors may be used to endorse or promote products derived from this
 *     software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
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
import org.ietr.preesm.ui.wizards.PreesmProjectNature;
import org.ietr.preesm.utils.files.FilesManager;
import org.ietr.preesm.utils.log.PreesmLogger;

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
							PreesmLogger.traceln("Project already registered, "
									+ "nothing to do: " + project.getName());
						} else {
							project.create(description, progressMonitor);
							project.open(progressMonitor);
							PreesmLogger.traceln("New project registered: "
									+ project.getName());
						}
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
				PreesmLogger.traceln("Register projects from \""
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
					PreesmLogger.traceln("Waiting for completion of"
							+ " currently running jobs - " + ++i);
					Thread.sleep(500);
				}

			} catch (CoreException e) {
				PreesmLogger.severeln(e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				PreesmLogger.severeln(e.getMessage());
				e.printStackTrace();
			} catch (InterruptedException e) {
				PreesmLogger.severeln(e.getMessage());
				e.printStackTrace();
			} finally {
				try {
					if (wasAutoBuildEnabled) {
						CommandLineUtil.enableAutoBuild(workspace);
						wasAutoBuildEnabled = false;
					}
					return IApplication.EXIT_OK;
				} catch (CoreException e) {
					PreesmLogger.severeln(e.getMessage());
					e.printStackTrace();
				}
			}
		} else {
			PreesmLogger
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
