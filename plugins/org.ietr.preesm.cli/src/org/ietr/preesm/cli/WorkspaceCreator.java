/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
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
package org.ietr.preesm.cli;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
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

// TODO: Auto-generated Javadoc
/**
 * This application take a folder path in argument and create a valid Eclipse workspace from .project files found in it
 * with the Preesm project nature. The .metadata (workspace information) folder is created in the current Eclipse
 * workspace.
 *
 * <p>
 * In command-line, workspace folder can be set by using "-data &lt;workspace&gt;" argument.
 * </p>
 *
 * @author cguy
 *
 *         Code adapted from ORCC (net.sf.orcc.backends, https://github.com/orcc/orcc)
 * @author alorence
 *
 */
public class WorkspaceCreator implements IApplication {

  /** The progress monitor. */
  private final IProgressMonitor progressMonitor;

  /** The workspace. */
  private final IWorkspace workspace;

  /** The was auto build enabled. */
  private boolean wasAutoBuildEnabled;

  /** The nature. */
  private final String nature;

  /**
   * Instantiates a new workspace creator.
   */
  public WorkspaceCreator() {

    this.progressMonitor = new NullProgressMonitor();

    this.nature = PreesmProjectNature.ID;

    this.workspace = ResourcesPlugin.getWorkspace();
    this.wasAutoBuildEnabled = false;
  }

  /**
   * Open searchFolder and try to find .project files inside it. Then, try to create an eclipse projects and add it to
   * the current workspace.
   *
   * @param searchFolder
   *          the search folder
   * @throws CoreException
   *           the core exception
   */
  private void searchForProjects(final File searchFolder) throws CoreException {
    if (searchFolder == null) {
      throw new NullPointerException();
    }
    if (!searchFolder.isDirectory()) {
      throw new RuntimeException("Bad path to search project: " + searchFolder.getPath());
    } else {
      final File[] children = searchFolder.listFiles();
      if (children != null) {
        for (final File child : children) {
          if (child.isDirectory()) {
            searchForProjects(child);
          } else if (child.getName().equals(IProjectDescription.DESCRIPTION_FILE_NAME)) {
            final IPath projectPath = new Path(child.getAbsolutePath());

            final IProjectDescription description = this.workspace.loadProjectDescription(projectPath);

            if (description.hasNature(this.nature)) {
              final IProject project = this.workspace.getRoot().getProject(description.getName());

              if (project.exists()) {
                project.close(this.progressMonitor);
                CLIWorkflowLogger.getLogger().log(Level.FINE, "A project named " + project.getName()
                    + " is already registered, " + "deleting previous project from Workspace: ");
              }
              project.create(description, this.progressMonitor);
              project.open(this.progressMonitor);
              CLIWorkflowLogger.getLogger().log(Level.FINE, "New project registered: " + project.getName());
            }
          }
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app. IApplicationContext)
   */
  @Override
  public Object start(final IApplicationContext context) {
    final Map<?, ?> map = context.getArguments();

    final String[] args = (String[]) map.get(IApplicationContext.APPLICATION_ARGS);

    if (args.length == 1) {

      try {

        this.wasAutoBuildEnabled = CommandLineUtil.disableAutoBuild(this.workspace);

        final String path = FilesManager.sanitize(args[0]);
        final File searchPath = new File(path).getCanonicalFile();
        CLIWorkflowLogger.getLogger().log(Level.SEVERE, "Register projects from \"" + searchPath.getAbsolutePath()
            + "\" to workspace \"" + this.workspace.getRoot().getLocation() + "\"");
        searchForProjects(searchPath);

        // Avoid warning messages of type "The workspace exited
        // with unsaved changes in the previous session" the next
        // time an IApplication (FrontendCli) will be launched
        // This method can be called ONLY if auto-building has
        // been disabled
        this.workspace.save(true, this.progressMonitor);

        final IJobManager manager = Job.getJobManager();
        int i = 0;
        while (!manager.isIdle()) {
          CLIWorkflowLogger.getLogger().log(Level.SEVERE,
              "Waiting for completion of" + " currently running jobs - " + ++i);
          Thread.sleep(500);
        }

      } catch (final CoreException e) {
        CLIWorkflowLogger.getLogger().log(Level.SEVERE, e.getMessage());
        e.printStackTrace();
      } catch (final IOException e) {
        CLIWorkflowLogger.getLogger().log(Level.SEVERE, e.getMessage());
        e.printStackTrace();
      } catch (final InterruptedException e) {
        CLIWorkflowLogger.getLogger().log(Level.SEVERE, e.getMessage());
        e.printStackTrace();
      } finally {
        try {
          if (this.wasAutoBuildEnabled) {
            CommandLineUtil.enableAutoBuild(this.workspace);
            this.wasAutoBuildEnabled = false;
          }
          return IApplication.EXIT_OK;
        } catch (final CoreException e) {
          CLIWorkflowLogger.getLogger().log(Level.SEVERE, e.getMessage());
          e.printStackTrace();
        }
      }
    } else {
      CLIWorkflowLogger.getLogger().log(Level.SEVERE, "Please add the path to a directories containing projects.");
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
