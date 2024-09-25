/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023 - 2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2022)
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
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.preesm.commons.files.PreesmResourcesHelper;
import org.preesm.commons.files.URLHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.workflow.AbstractWorkflowExecutor;

/**
 *
 * @author anmorvan
 *
 */
public class WorkflowRunner {

  private static ConcurrentHashMap<String, ReentrantLock> mutexMap = new ConcurrentHashMap<>();

  private static void acquireMutex(String mutexName) {
    // check if lock exist, create it if not
    mutexMap.putIfAbsent(mutexName, new ReentrantLock());

    // acquire lock with lock()
    mutexMap.get(mutexName).lock();
  }

  private static void releaseMutex(String mutexName) {
    // release lock
    mutexMap.get(mutexName).unlock();
  }

  /**
   * Executes a workflow on a given scenario. Exclusively used for unit tests. The project can be in the resources
   * folder or at a specific location.
   *
   * @param projectRoot
   *          The location of the project, or null if in resources folder
   * @param projectName
   *          The project name
   * @param workflowFilePathStr
   *          the path to the workflow file within the project
   * @param scenarioFilePathStr
   *          the path to the scenario file within the project
   * @return true, if successful
   */
  public static final boolean runWorkFlow(final String projectRoot, final String projectName,
      final String workflowFilePathStr, final String scenarioFilePathStr) throws CoreException, IOException {

    acquireMutex(projectName);

    // init workspace and project
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IWorkspaceRoot root = workspace.getRoot();
    final IProjectDescription newProjectDescription = workspace.newProjectDescription(projectName);
    final java.nio.file.Path createTempDirectory = Files.createTempDirectory("PREESM_TESTS_");
    newProjectDescription.setLocationURI(createTempDirectory.toUri());
    final IProject project = root.getProject(projectName);
    project.create(newProjectDescription, null);
    project.open(null);

    final Logger logger = PreesmLogger.getLogger();
    logger.setLevel(Level.ALL);

    try {
      // copy content
      URL resolve;

      if (projectRoot == null) {
        resolve = PreesmResourcesHelper.getInstance().resolve(projectName, WorkflowRunner.class);
      } else {
        resolve = new URL("file://" + projectRoot);
      }
      URLHelper.copyContent(resolve, project);

      // run workflow
      final AbstractWorkflowExecutor workflowManager = new PreesmTestWorkflowExecutor();
      workflowManager.setDebug(true);
      final String workflowPath = "/" + projectName + workflowFilePathStr;
      final String scenarioPath = "/" + projectName + scenarioFilePathStr;

      final boolean success = workflowManager.execute(workflowPath, scenarioPath, null, true);

      return success;
    } finally {
      // clean
      project.close(null);
      final Stream<java.nio.file.Path> walk = Files.walk(createTempDirectory, FileVisitOption.FOLLOW_LINKS);
      walk.sorted(Comparator.reverseOrder()).map(java.nio.file.Path::toFile).forEach(File::delete);
      walk.close();
      project.delete(true, null);

      releaseMutex(projectName);
    }
  }
}
