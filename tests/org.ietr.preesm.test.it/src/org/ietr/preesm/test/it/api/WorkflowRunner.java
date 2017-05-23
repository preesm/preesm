package org.ietr.preesm.test.it.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.ui.workflow.tools.DFToolsWorkflowLogger;
import org.ietr.dftools.workflow.WorkflowManager;
import org.ietr.dftools.workflow.tools.WorkflowLogger;

/**
 *
 * @author anmorvan
 *
 */
public class WorkflowRunner {

  private static final String PROJECT_RESOURCES_LOCAL_PATH = "resources";

  /**
   */
  public static final boolean runWorkFlow(final String projectName, final String workflowFilePathStr, final String scenarioFilePathStr)
      throws CoreException, FileNotFoundException {
    // init
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final IProject project = root.getProject(projectName);
    project.create(null);
    project.open(null);

    DFToolsWorkflowLogger.runFromCLI();
    final WorkflowLogger logger = WorkflowLogger.getLogger();
    logger.setLevel(Level.ALL);

    // run workflow
    WorkflowRunner.copyFiles(new File(WorkflowRunner.PROJECT_RESOURCES_LOCAL_PATH + "/" + projectName + "/"), project);
    final WorkflowManager workflowManager = new WorkflowManager();
    workflowManager.setDebug(true);
    final String workflowPath = "/" + projectName + workflowFilePathStr;
    final String scenarioPath = "/" + projectName + scenarioFilePathStr;

    final boolean success = workflowManager.execute(workflowPath, scenarioPath, null);

    // clean
    project.delete(true, true, null);
    return success;
  }

  private static final void copyFiles(final File srcFolder, final IContainer destFolder) throws CoreException, FileNotFoundException {
    for (final File f : srcFolder.listFiles()) {
      if (f.isDirectory()) {
        final IFolder newFolder = destFolder.getFolder(new Path(f.getName()));
        if (newFolder.exists()) {
          continue;
        }
        newFolder.create(true, true, null);
        WorkflowRunner.copyFiles(f, newFolder);
      } else {
        final IFile newFile = destFolder.getFile(new Path(f.getName()));
        if (newFile.exists()) {
          continue;
        }
        newFile.create(new FileInputStream(f), false, null);
      }
    }
  }
}
