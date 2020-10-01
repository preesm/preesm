/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
   * @return Folder selected by the user (may be null).
   */
  public static IPath save(final IProject iProject, final PiGraph graph, final String suffix) {
    final IPath targetFolder = FileUtils.browseFiles(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
        "Select result location", "Select the folder where to write the computed PiGraph.", (Collection<String>) null);
    if (targetFolder == null) {
      // the user canceled the save operation.
      return targetFolder;
    }
    saveInFolder(iProject, targetFolder, graph, suffix);

    return targetFolder;

  }

  /**
   * Write a PiGraph in the given folder path.
   * 
   * @param iProject
   *          original project of the graph.
   * @param targetFolder
   *          folder where to save the graph.
   * @param graph
   *          to be saved.
   * @param suffix
   *          appends the name of the graph to save it.
   */
  public static void saveInFolder(final IProject iProject, final IPath targetFolder, final PiGraph graph,
      final String suffix) {
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
