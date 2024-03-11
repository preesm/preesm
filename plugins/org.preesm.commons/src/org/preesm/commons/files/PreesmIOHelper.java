/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.preesm.commons.exceptions.PreesmRuntimeException;

/**
 *
 * Set of methods to help reading or writing "user" files, that is user input (algorithm, architecture, etc.) or output
 * (generated code, IR, etc.).
 * <p>
 * To find helper methods for Preesm resources (templates, default scripts, etc.), see {@link PreesmResourcesHelper}.
 * <p>
 * TODO complete this class with other methods to load a resource file, as a locate method, returning an URI.
 *
 * TODO use {@link java.nio.file.Files#copy} instead of printing unmodified content?
 *
 * @author anmorvan
 *
 */
public class PreesmIOHelper {

  private static final PreesmIOHelper instance = new PreesmIOHelper();

  public static final PreesmIOHelper getInstance() {
    return instance;
  }

  /**
   * Print the given content at a specific location. Create the file if not existent.
   *
   * @param filePath
   *          Path to the file to write.
   * @param fileName
   *          Name (with extension) of the file to write.
   * @param fileContent
   *          Content to write in the file.
   * @return The printed file.
   */
  public IFile print(final String filePath, final String fileName, final CharSequence fileContent) {
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath + fileName));

    try {
      final IFolder iFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(filePath));
      createFolderRecursively(iFolder, false, true, new NullProgressMonitor());

      if (!iFile.exists()) {
        iFile.create(new ByteArrayInputStream("".getBytes()), false, new NullProgressMonitor());
      }
      iFile.setContents(new ByteArrayInputStream(fileContent.toString().getBytes()), true, false,
          new NullProgressMonitor());
    } catch (final CoreException ex) {
      throw new PreesmRuntimeException("Could not generate source file for " + fileName, ex);
    }
    return iFile;
  }

  /**
   * Read the given content at a specific location.
   *
   * @param filePath
   *          Path to the file to write.
   * @param fileName
   *          Name (with extension) of the file to write.
   * @return fileContent Content to read in the file.
   */
  public final String read(final String filePath, final String fileName) {
    final StringBuilder content = new StringBuilder();
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath + fileName));
    try {

      final InputStream fileContent = iFile.getContents();
      final InputStreamReader inputStreamReader = new InputStreamReader(fileContent);
      final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

      String line;
      while ((line = bufferedReader.readLine()) != null) {
        content.append(line).append("\n");
      }

      // Close the streams
      bufferedReader.close();
      inputStreamReader.close();
      fileContent.close();

    } catch (final CoreException | IOException ex) {
      throw new PreesmRuntimeException("Could not find source file for " + fileName, ex);
    }
    return content.toString();
  }

  /**
   * Appends data to a file, creating the file if it does not exist.
   *
   * @param filePath
   *          The path of the file.
   * @param fileName
   *          The name of the file.
   * @param data
   *          The data to be appended to the file.
   * @return The IFile object representing the appended file.
   */
  public IFile append(final String filePath, final String fileName, final String data) {
    final StringBuilder content = new StringBuilder();

    // if the file exists, we write to it otherwise we create the template
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath + fileName));
    if (iFile.isAccessible()) {
      content.append(PreesmIOHelper.getInstance().read(filePath, fileName));
      content.append(data + "\n");
      PreesmIOHelper.getInstance().print(filePath, fileName, content);
    } else {

      content.append(data + "\n");
      PreesmIOHelper.getInstance().print(filePath, fileName, content);

    }
    return iFile;
  }

  // See
  // https://stackoverflow.com/questions/68075036/eclipse-plugin-how-do-i-create-all-folders-ifolders-in-a-given-path-ipath
  public static void createFolderRecursively(IFolder folder, boolean force, boolean local, IProgressMonitor monitor)
      throws CoreException {
    if (!folder.exists()) {
      final IContainer parent = folder.getParent();
      if (parent instanceof final IFolder ifolder) {
        createFolderRecursively(ifolder, force, local, null);
      }
      folder.create(force, local, monitor);
    }
  }

  public InputStreamReader getFileReader(final String fileLocation, final Class<?> clazz) {
    final URL mainTemplate = PreesmResourcesHelper.getInstance().resolve(fileLocation, clazz);
    InputStreamReader reader = null;
    try {
      reader = new InputStreamReader(mainTemplate.openStream());
    } catch (final IOException e) {
      throw new PreesmRuntimeException("Could not locate main template [" + fileLocation + "].", e);
    }
    return reader;
  }

  public void deleteFolder(String path) {
    final IFolder iFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(path));
    if (iFolder.exists()) {
      try {
        iFolder.delete(true, null);
      } catch (final CoreException e) {
        throw new PreesmRuntimeException(e);
      }
    }
  }

  public void deleteFile(String path) {
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
    if (iFile.exists()) {
      try {
        iFile.delete(true, null);
      } catch (final CoreException e) {
        throw new PreesmRuntimeException(e);
      }
    }
  }

}
