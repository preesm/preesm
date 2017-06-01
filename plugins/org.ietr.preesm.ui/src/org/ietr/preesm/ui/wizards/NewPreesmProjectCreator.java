/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
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
package org.ietr.preesm.ui.wizards;

import java.net.URI;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;

// TODO: Auto-generated Javadoc
/**
 * The Class NewPreesmProjectCreator.
 */
public class NewPreesmProjectCreator {

  /**
   * For this project we need to: - create the default Eclipse project - add the custom project natures - create the folder structure.
   *
   * @param projectName
   *          the name of the project we want to create
   * @param location
   *          location of the project
   * @return the created project
   */
  public static IProject createProject(final String projectName, final URI location) {

    Assert.isNotNull(projectName);
    Assert.isTrue(projectName.trim().length() != 0);

    IProject project = NewPreesmProjectCreator.createBaseProject(projectName, location);
    try {
      NewPreesmProjectCreator.addNatures(project);

      final String[] paths = { "Algo/generated/flatten", "Algo/generated/singlerate", "Algo/generated/DAG", "Archi", "Code/generated", "Code/include",
          "Code/lib/cmake_modules", "Code/src", "Scenarios", "Workflows" };
      NewPreesmProjectCreator.addToProjectStructure(project, paths);
    } catch (final CoreException e) {
      e.printStackTrace();
      project = null;
    }

    return project;
  }

  /**
   * Just do the basics: create a basic project.
   *
   * @param projectName
   *          the project name
   * @param location
   *          the location
   * @return the i project
   */
  private static IProject createBaseProject(final String projectName, final URI location) {
    // it is acceptable to use the ResourcesPlugin class
    final IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

    if (!newProject.exists()) {
      URI projectLocation = location;
      final IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());
      if ((location != null) && ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(location)) {
        projectLocation = null;
      }

      desc.setLocationURI(projectLocation);

      try {
        newProject.create(desc, null);
        if (!newProject.isOpen()) {
          newProject.open(null);
        }
      } catch (final CoreException e) {
        e.printStackTrace();
      }
    }

    // return cdtProj;
    return newProject;
  }

  /**
   * Create a folder structure.
   *
   * @param newProject
   *          the project to which we want to add folders
   * @param paths
   *          paths to the folders we want to add
   * @throws CoreException
   *           the core exception
   */
  private static void addToProjectStructure(final IProject newProject, final String[] paths) throws CoreException {
    for (final String path : paths) {
      final IFolder etcFolders = newProject.getFolder(path);
      NewPreesmProjectCreator.createFolder(etcFolders);
    }
  }

  /**
   * Creates the folder.
   *
   * @param folder
   *          the folder
   * @throws CoreException
   *           the core exception
   */
  private static void createFolder(final IFolder folder) throws CoreException {
    final IContainer parent = folder.getParent();
    if (parent instanceof IFolder) {
      NewPreesmProjectCreator.createFolder((IFolder) parent);
    }
    if (!folder.exists()) {
      folder.create(false, true, null);
    }
  }

  /**
   * Adds Preesm nature and C nature to the project.
   *
   * @param project
   *          the project to which we want to add the Preesm and C natures
   * @throws CoreException
   *           the core exception
   */
  private static void addNatures(final IProject project) throws CoreException {
    final IProjectDescription description = project.getDescription();
    if (!project.hasNature(PreesmProjectNature.ID)) {
      NewPreesmProjectCreator.addNature(description, PreesmProjectNature.ID);
    }

    project.setDescription(description, null);
  }

  /**
   * Adds a nature to a project description.
   *
   * @param description
   *          the project description to which we want to add the nature
   * @param nature
   *          the id of the nature we want to add
   */
  private static void addNature(final IProjectDescription description, final String nature) {
    final String[] natures = description.getNatureIds();
    final String[] newNatures = new String[natures.length + 1];
    System.arraycopy(natures, 0, newNatures, 0, natures.length);
    newNatures[natures.length] = nature;
    description.setNatureIds(newNatures);
  }
}
