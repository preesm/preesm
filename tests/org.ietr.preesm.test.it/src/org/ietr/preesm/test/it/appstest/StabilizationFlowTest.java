package org.ietr.preesm.test.it.appstest;

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
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.dftools.ui.workflow.tools.DFToolsWorkflowLogger;
import org.ietr.dftools.workflow.WorkflowManager;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class StabilizationFlowTest {

  private static final String PROJECT_NAME = "org.ietr.preesm.stabilization";

  /**
   *
   */
  @Before
  public final void initProject() throws CoreException {
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final IProject project = root.getProject(StabilizationFlowTest.PROJECT_NAME);
    project.create(null);
    project.open(null);

    final WorkflowLogger logger = WorkflowLogger.getLogger();
    logger.setLevel(Level.ALL);
    DFToolsWorkflowLogger.runFromCLI();
  }

  /**
   *
   */
  @After
  public final void deleteProject() throws CoreException {
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final IProject project = root.getProject(StabilizationFlowTest.PROJECT_NAME);
    project.delete(true, true, null);
  }

  final void copyFiles(final File srcFolder, final IContainer destFolder) throws CoreException, FileNotFoundException {
    for (final File f : srcFolder.listFiles()) {
      if (f.isDirectory()) {
        final IFolder newFolder = destFolder.getFolder(new Path(f.getName()));
        if (newFolder.exists()) {
          continue;
        }
        newFolder.create(true, true, null);
        copyFiles(f, newFolder);
      } else {
        final IFile newFile = destFolder.getFile(new Path(f.getName()));
        if (newFile.exists()) {
          continue;
        }
        newFile.create(new FileInputStream(f), false, null);
      }
    }
  }

  @Test
  public void testStabilizationFlow() throws FileNotFoundException, InvalidModelException, CoreException {

    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final IProject project = root.getProject(StabilizationFlowTest.PROJECT_NAME);
    copyFiles(new File("resources/org.ietr.preesm.stabilization/"), project);
    final WorkflowManager workflowManager = new WorkflowManager();
    final String workflowPath = "/" + StabilizationFlowTest.PROJECT_NAME + "/Workflows/Codegen.workflow";
    final String scenarioPath = "/" + StabilizationFlowTest.PROJECT_NAME + "/Scenarios/4coresX86.scenario";

    final boolean success = workflowManager.execute(workflowPath, scenarioPath, null);
    Assert.assertTrue(success);
  }
}
