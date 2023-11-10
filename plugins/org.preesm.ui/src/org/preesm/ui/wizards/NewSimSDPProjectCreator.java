package org.preesm.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.logging.Level;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.preesm.commons.logger.PreesmLogger;

/**
 * The Class NewSimSDPProjectCreator.
 */
public class NewSimSDPProjectCreator {

  /** Strings displayed in the wizard (titles, names, descriptions...) */
  private static final String SOURCE_DIRECTORY_FOLDER = Platform.getBundle("org.preesm.ui").getLocation()
      .replace("reference:file:", "") + "resources/SimSDPproject";

  private NewSimSDPProjectCreator() {
    // disallow instantiation
  }

  /**
   * For this project we need to: copy a project template stored in eclipse containing - the folders needed to run a
   * Preesm project - the pre-filled workflows needed to run a SimSDP project - the SimGrid installation folder
   *
   * @param projectName
   *          the name of the project we want to create
   * @param destinationLocation
   *          location of the project
   *
   */

  public static void createProject(final String projectName, final URI destinationLocation) {

    Assert.isNotNull(projectName);
    Assert.isTrue(projectName.trim().length() != 0);

    final File sourceDirectory = new File(SOURCE_DIRECTORY_FOLDER);

    if (sourceDirectory.exists()) {
      final File destinationDirectory = new File(destinationLocation.getPath());

      copyDirectory(sourceDirectory, destinationDirectory);

      final IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
      if (!newProject.exists()) {
        final IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());
        desc.setLocationURI(destinationLocation);
        try {
          newProject.create(desc, null);
        } catch (final CoreException e) {
          PreesmLogger.getLogger().log(Level.SEVERE, "Could not create base project", e);
        }
      }
      PreesmLogger.getLogger().log(Level.INFO, "Template SimSDP project successfully copied!");
    } else {
      PreesmLogger.getLogger().log(Level.SEVERE, "Could not copy SimSDP base project, missing source file");
    }
  }

  /**
   * Copy a source project folder stored in eclispe to the destination workplace
   *
   * @param source
   *          the project name
   * @param destination
   *          the location
   */
  private static void copyDirectory(File source, File destination) {
    if (source.isDirectory()) {
      if (!destination.exists()) {
        destination.mkdir();
      }
      final String[] files = source.list();
      for (final String file : files) {
        final File srcFile = new File(source, file);
        final File destFile = new File(destination, file);

        copyDirectory(srcFile, destFile);
      }

    } else {
      // copy missing worflows and .project
      try {
        Files.copy(source.toPath(), destination.toPath());
      } catch (final IOException e) {
        PreesmLogger.getLogger().log(Level.SEVERE, "Could not create simsdp template project", e);
      }
    }
  }
}
