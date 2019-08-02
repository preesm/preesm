package org.preesm.ui.pisdf.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.PlatformUI;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.WorkspaceUtils;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.serialize.PiWriter;
import org.preesm.ui.utils.FileUtils;

/**
 * Helper to save a PiGraph from a menu command.
 * 
 * @author ahonorat
 */
public class SavePiGraph {

  /**
   * Opens a dialog box to select in which folder to write a PiGraph.
   * <p>
   * The folder can be picked in any project.
   * 
   * @param iProject
   *          original project of the graph.
   * @param graph
   *          to be saved.
   * @param suffix
   *          appends the name of the graph to save it.
   */
  public static void save(final IProject iProject, final PiGraph graph, final String suffix) {
    final IPath targetFolder = FileUtils.browseFiles(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
        "Select result location", "Select the folder where to write the computed PiGraph.", (Collection<String>) null);
    // The commented code is kept in case we would like to restrict the writing process to the given iProject.

    // final IPath inProjectPath = targetFolder.removeFirstSegments(1);
    final String relative = targetFolder.toString();

    // final String sXmlPath = WorkspaceUtils.getAbsolutePath(relative, iProject.getName());
    IPath xmlPath = new Path(relative);
    // Get a complete valid path with all folders existing
    try {
      if (xmlPath.getFileExtension() != null) {
        WorkspaceUtils.createMissingFolders(xmlPath.removeFileExtension().removeLastSegments(1));
      } else {
        WorkspaceUtils.createMissingFolders(xmlPath);
        xmlPath = xmlPath.append(graph.getName() + suffix + ".pi");
      }
    } catch (CoreException | IllegalArgumentException e) {
      throw new PreesmRuntimeException("Path " + relative + " is not a valid path for export.\n" + e.getMessage());
    }

    final URI uri = URI.createPlatformResourceURI(xmlPath.toString(), true);
    // Get the project
    final String platformString = uri.toPlatformString(true);
    final IFile documentFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformString));
    final String osString = documentFile.getLocation().toOSString();
    try (final OutputStream outStream = new FileOutputStream(osString);) {
      // Write the Graph to the OutputStream using the Pi format
      new PiWriter(uri).write(graph, outStream);
    } catch (IOException e) {
      throw new PreesmRuntimeException("Could not open outputstream file " + xmlPath.toString());
    }

    WorkspaceUtils.updateWorkspace();
  }

}
