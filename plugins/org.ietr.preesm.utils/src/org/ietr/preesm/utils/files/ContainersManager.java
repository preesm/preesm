/*******************************************************************************
 * Copyright or © or Copr. 2014 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2016)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
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
 *******************************************************************************/
package org.ietr.preesm.utils.files;

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

public class ContainersManager {

	/**
	 * Check if an IProject named projectName exists in the workspace
	 * 
	 * @param projectName
	 *            the name for which we want to check if an IProject exists
	 * @return true if an IPoject with name projectName already exists in
	 *         workspace, false otherwise
	 */
	public static boolean projectExists(String projectName) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		return project.exists();
	}

	/**
	 * Check if a IFolder exists in the given IProject
	 * 
	 * @param folderPath
	 *            path for which we want to check if an IFolder exists
	 * @param project
	 *            IProject in which we want to check
	 * @return true if an IFolder with path folderPath already exists in
	 *         project, false otherwise
	 */
	public static boolean folderExistsInto(String folderPath, IProject project) {
		IFolder folder = project.getFolder(folderPath);
		return folder.exists();
	}

	/**
	 * Create a project into the workspace
	 * 
	 * @param projectName
	 *            name of the project to create
	 * @return the created IProject
	 * @throws CoreException
	 */
	public static IProject createProject(String projectName)
			throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
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
	 * Create a folder and all its parent folders into a IProject of the
	 * workspace
	 * 
	 * @param folderPath
	 *            the path for the folder to create
	 * @param project
	 *            the IProject in which to create the folder
	 * @throws CoreException
	 */
	public static IFolder createFolderInto(String folderPath, IProject project)
			throws CoreException {
		if (folderPath != null && folderPath.length() > 0) {
			IFolder folder = project.getFolder(folderPath);
			if (!folder.exists())
				createFolder(folder, false, true, null);
			return folder;
		}
		return null;
	}
	


	/**
	 * Create all non existing folders in the given IPath
	 * 
	 * @param path
	 *            the IPath containing the folders to create
	 * @throws CoreException
	 */
	public static void createMissingFolders(IPath path) throws IllegalArgumentException, CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.getFolder(path);
		createMissingFolders(resource);
	}

	/**
	 * Create all non existing folders in the path of the given IResource
	 * 
	 * @param resource
	 *            the IResource on which path we want to create folders
	 * @throws CoreException
	 */
	public static void createMissingFolders(final IResource resource)
			throws CoreException {
		// If ressource is null or already exists, there is nothing to create
		if (resource == null || resource.exists())
			return;
		// If resource has no parent, create it recursively
		if (!resource.getParent().exists())
			createMissingFolders(resource.getParent());
		// If resource is a folder, create it
		if (resource instanceof IFolder) {
			((IFolder) resource).create(IResource.NONE, true, null);
		}
	}

	/**
	 * Creates a folder and all parent folders if not existing. Project must
	 * exist. <code>org.eclipse.ui.dialogs.ContainerGenerator</code> is too
	 * heavy (creates a runnable)
	 */
	private static void createFolder(IFolder folder, boolean force,
			boolean local, IProgressMonitor monitor) throws CoreException {
		if (!folder.exists()) {
			IContainer parent = folder.getParent();
			if (parent instanceof IFolder) {
				createFolder((IFolder) parent, force, local, null);
			}
			folder.create(force, local, monitor);
		}
	}

	/**
	 * Delete an IProject and its content from the workspace
	 * 
	 * @param projectName
	 *            name of the IProject to delete
	 * @throws CoreException
	 */
	public static void deleteProject(String projectName) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		project.delete(true, true, null);
	}

	/**
	 * Delete an IProject and its content from the workspace
	 * 
	 * @param project
	 *            the IProject to delete
	 * @throws CoreException
	 */
	public static void deleteProject(IProject project) throws CoreException {
		project.delete(true, true, null);
	}
	
	/**
	 * Delete an IFolder and its content from the workspace
	 * 
	 * @param folder
	 *            the IFolder to delete
	 * @throws CoreException
	 */
	public static void deleteFolder(IFolder folder) throws CoreException {
		for (IResource resource : folder.members()) resource.delete(true, null);
		folder.delete(true, null);
	}

	/**
	 * Delete a set of IFile from a IFolder
	 * 
	 * @param fileNames
	 *            names of the file to delete
	 * @param folder
	 *            IFolder from which to delete the files
	 * @throws CoreException
	 */
	public static void deleteFilesFrom(Set<String> fileNames, IFolder folder)
			throws CoreException {
		for (String fileName : fileNames) {
			IFile file = folder.getFile(fileName);
			file.delete(true, null);
		}
	}
}
