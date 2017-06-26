/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
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
package org.ietr.preesm.test.it.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.logging.Level;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
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
      throws CoreException, IOException {
    // init
    // init
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IWorkspaceRoot root = workspace.getRoot();
    final IProjectDescription newProjectDescription = workspace.newProjectDescription(projectName);
    final java.nio.file.Path createTempDirectory = Files.createTempDirectory("PREESM_TESTS_");
    newProjectDescription.setLocationURI(createTempDirectory.toUri());
    final IProject project = root.getProject(projectName);
    project.create(newProjectDescription, null);
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
    project.close(null);
    Files.walk(createTempDirectory, FileVisitOption.FOLLOW_LINKS).sorted(Comparator.reverseOrder()).map(java.nio.file.Path::toFile).forEach(File::delete);
    project.delete(true, null);
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
