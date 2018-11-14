/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2016)
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
package org.preesm.commons.files;

import java.util.Set;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

// TODO: Auto-generated Javadoc
/**
 * The Class ContainersManager.
 */
public class ContainersManager {

  /**
   * Check if an IProject named projectName exists in the workspace.
   *
   * @param projectName
   *          the name for which we want to check if an IProject exists
   * @return true if an IPoject with name projectName already exists in workspace, false otherwise
   */
  public static boolean projectExists(final String projectName) {
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final IProject project = root.getProject(projectName);
    return project.exists();
  }

  /**
   * Check if a IFolder exists in the given IProject.
   *
   * @param folderPath
   *          path for which we want to check if an IFolder exists
   * @param project
   *          IProject in which we want to check
   * @return true if an IFolder with path folderPath already exists in project, false otherwise
   */
  public static boolean folderExistsInto(final String folderPath, final IProject project) {
    final IFolder folder = project.getFolder(folderPath);
    return folder.exists();
  }

  /**
   * Create a project into the workspace.
   *
   * @param projectName
   *          name of the project to create
   * @return the created IProject
   * @throws CoreException
   *           the core exception
   */
  public static IProject createProject(final String projectName) throws CoreException {
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final IProject project = root.getProject(projectName);
    if (!project.exists()) {
      project.create(null);
    } else {
      project.refreshLocal(IResource.DEPTH_INFINITE, null);
    }

    if (!project.isOpen()) {
      project.open(null);
    }

    return project;
  }

  /**
   * Create a folder and all its parent folders into a IProject of the workspace.
   *
   * @param folderPath
   *          the path for the folder to create
   * @param project
   *          the IProject in which to create the folder
   * @return the i folder
   * @throws CoreException
   *           the core exception
   */
  public static IFolder createFolderInto(final String folderPath, final IProject project) throws CoreException {
    if ((folderPath != null) && (folderPath.length() > 0)) {
      final IFolder folder = project.getFolder(folderPath);
      if (!folder.exists()) {
        ContainersManager.createFolder(folder, false, true, null);
      }
      return folder;
    }
    return null;
  }

  /**
   * Create all non existing folders in the given IPath.
   *
   * @param path
   *          the IPath containing the folders to create
   * @throws IllegalArgumentException
   *           the illegal argument exception
   * @throws CoreException
   *           the core exception
   */
  public static void createMissingFolders(final IPath path) throws IllegalArgumentException, CoreException {
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final IResource resource = root.getFolder(path);
    ContainersManager.createMissingFolders(resource);
  }

  /**
   * Create all non existing folders in the path of the given IResource.
   *
   * @param resource
   *          the IResource on which path we want to create folders
   * @throws CoreException
   *           the core exception
   */
  public static void createMissingFolders(final IResource resource) throws CoreException {
    // If ressource is null or already exists, there is nothing to create
    if ((resource == null) || resource.exists()) {
      return;
    }
    // If resource has no parent, create it recursively
    if (!resource.getParent().exists()) {
      ContainersManager.createMissingFolders(resource.getParent());
    }
    // If resource is a folder, create it
    if (resource instanceof IFolder) {
      ((IFolder) resource).create(IResource.NONE, true, null);
    }
  }

  /**
   * Creates a folder and all parent folders if not existing. Project must exist.
   * <code>org.eclipse.ui.dialogs.ContainerGenerator</code> is too heavy (creates a runnable)
   *
   * @param folder
   *          the folder
   * @param force
   *          the force
   * @param local
   *          the local
   * @param monitor
   *          the monitor
   * @throws CoreException
   *           the core exception
   */
  private static void createFolder(final IFolder folder, final boolean force, final boolean local,
      final IProgressMonitor monitor) throws CoreException {
    if (!folder.exists()) {
      final IContainer parent = folder.getParent();
      if (parent instanceof IFolder) {
        ContainersManager.createFolder((IFolder) parent, force, local, null);
      }
      folder.create(force, local, monitor);
    }
  }

  /**
   * Delete an IProject and its content from the workspace.
   *
   * @param projectName
   *          name of the IProject to delete
   * @throws CoreException
   *           the core exception
   */
  public static void deleteProject(final String projectName) throws CoreException {
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final IProject project = root.getProject(projectName);
    project.delete(true, true, null);
  }

  /**
   * Delete an IProject and its content from the workspace.
   *
   * @param project
   *          the IProject to delete
   * @throws CoreException
   *           the core exception
   */
  public static void deleteProject(final IProject project) throws CoreException {
    project.delete(true, true, null);
  }

  /**
   * Delete an IFolder and its content from the workspace.
   *
   * @param folder
   *          the IFolder to delete
   * @throws CoreException
   *           the core exception
   */
  public static void deleteFolder(final IFolder folder) throws CoreException {
    for (final IResource resource : folder.members()) {
      resource.delete(true, null);
    }
    folder.delete(true, null);
  }

  /**
   * Delete a set of IFile from a IFolder.
   *
   * @param fileNames
   *          names of the file to delete
   * @param folder
   *          IFolder from which to delete the files
   * @throws CoreException
   *           the core exception
   */
  public static void deleteFilesFrom(final Set<String> fileNames, final IFolder folder) throws CoreException {
    for (final String fileName : fileNames) {
      final IFile file = folder.getFile(fileName);
      file.delete(true, null);
    }
  }
}
