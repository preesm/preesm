/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
package org.ietr.preesm.utils.files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.Assert;

/**
 * Utility class to manipulate files. It brings everything needed to extract files from a jar plugin to the filesystem,
 * check if 2 files are identical, read/write files, etc.
 *
 * Code adapted from ORCC (net.sf.orcc.util, https://github.com/orcc/orcc)
 *
 * @author Antoine Lorence
 *
 */
public class FilesManager {

  /**
   * Returns the File found at the given path
   *
   * @param path
   *          path to the file
   * @param bundleFilter
   *          A filter to indicate in which bundle to look for
   */
  public static final File getFile(final String path, final String bundleFilter) {
    final String sanitizedPath = FilesManager.sanitize(path);
    final File file = new File(sanitizedPath);
    if ((file != null) && file.exists()) {
      return file;
    }
    return null;
  }

  /**
   * <p>
   * Copy the file or the folder at given <em>path</em> to the given <em>target folder</em>.
   * </p>
   *
   * <p>
   * It is important to understand that the resource (file or folder) at the given path will be copied <b>into</b> the
   * target folder. For example, <code>extract("/path/to/file.txt", "/home/johndoe")</code> will copy <em>file.txt</em>
   * into <em>/home/johndoe</em>, and <code>extract("/path/to/MyFolder", "/home/johndoe")</code> will create
   * <em>MyFolder</em> directory in <em>/home/johndoe</em> and copy all files from the source folder into it.
   * </p>
   *
   * @param path
   *          The path of the source (folder or file) to copy
   * @param targetFolder
   *          The directory where to copy the source element
   * @param bundleFilter
   *          A filter to indicate in which bundle to look for
   * @return A Result object counting exactly how many files have been really written, and how many haven't because they
   *         were already up-to-date
   * @throws FileNotFoundException
   *           If not resource have been found at the given path
   */
  public static final Result extract(final String path, final String targetFolder, final String bundleFilter)
      throws IOException, URISyntaxException {
    final File targetF = new File(FilesManager.sanitize(targetFolder));
    final URL url = FilesManager.getUrl(path, bundleFilter);
    if (url == null) {
      throw new FileNotFoundException(path);
    }
    if (url.getProtocol().equals("jar")) {
      final List<String> splittedURL = Arrays.asList(url.getFile().split("!"));
      final URI fileUri = new URI(splittedURL.get(0));
      final JarFile jar = new JarFile(new File(fileUri));
      return FilesManager.jarExtract(jar, splittedURL.get(splittedURL.size() - 1), targetF);
    } else {
      return FilesManager.fsExtract(new File(url.toURI()), targetF);
    }
  }

  /**
   * Copy the given <i>source</i> file to the given <em>targetFile</em> path.
   *
   * @param source
   *          An existing File instance
   * @param targetFolder
   *          The target folder to copy the file
   */
  private static final Result fsExtract(final File source, final File targetFolder) throws IOException {
    if (!source.exists()) {
      throw new FileNotFoundException(source.getPath());
    }
    final File target = new File(targetFolder, source.getName());
    if (source.isFile()) {
      if (FilesManager.isContentEqual(source, target)) {
        return Result.newCachedInstance();
      } else {
        return FilesManager.streamExtract(new FileInputStream(source), target);
      }
    } else if (source.isDirectory()) {
      return FilesManager.fsDirectoryExtract(source, target);
    } else {
      throw new IOException("Source is neither a file nor a folder.");
    }
  }

  /**
   * Copy the given <i>source</i> directory and its content into the given <em>targetFolder</em> directory.
   *
   * @param source
   *          The source path of an existing file
   * @param targetFolder
   *          Path to the folder where source will be copied
   */
  private static final Result fsDirectoryExtract(final File source, final File targetFolder) throws IOException {
    Assert.isTrue(source.isDirectory());
    if (!targetFolder.exists()) {
      Assert.isTrue(targetFolder.mkdirs());
    } else {
      Assert.isTrue(targetFolder.isDirectory());
    }

    final Result result = Result.newInstance();
    for (final File file : source.listFiles()) {
      result.merge(FilesManager.fsExtract(file, targetFolder));
    }
    return result;
  }

  /**
   * Starting point for extraction of a file/folder resource from a jar.
   */
  private static Result jarExtract(final JarFile jar, final String path, final File targetFolder) throws IOException {
    String updatedPath;
    if (path.startsWith("/")) {
      updatedPath = path.substring(1);
    } else {
      updatedPath = path;
    }

    final JarEntry entry = jar.getJarEntry(updatedPath);

    // Remove the last char if it is '/'
    String name;
    if (entry.getName().endsWith("/")) {
      name = entry.getName().substring(0, entry.getName().length() - 1);
    } else {
      name = entry.getName();
    }
    String fileName;
    if (name.lastIndexOf("/") != -1) {
      fileName = name.substring(name.lastIndexOf("/"));
    } else {
      fileName = name;
    }
    if (entry.isDirectory()) {
      return FilesManager.jarDirectoryExtract(jar, entry, new File(targetFolder, fileName));
    } else {
      final List<JarEntry> entries = Collections.list(jar.entries()).stream()
          .filter(je -> je.getName().startsWith(updatedPath)).collect(Collectors.toList());
      if (entries.size() > 1) {
        return FilesManager.jarDirectoryExtract(jar, entry, new File(targetFolder, fileName));
      } else {
        return FilesManager.jarFileExtract(jar, entry, new File(targetFolder, fileName));
      }
    }
  }

  /**
   * Extract all files in the given <em>entry</em> from the given <em>jar</em> into the <em>target folder</em>.
   */
  private static final Result jarDirectoryExtract(final JarFile jar, final JarEntry entry, final File targetFolder)
      throws IOException {
    final String prefix = entry.getName();
    final List<JarEntry> entries = Collections.list(jar.entries()).stream()
        .filter(je -> je.getName().startsWith(prefix)).collect(Collectors.toList());
    final Result result = Result.newInstance();
    for (final JarEntry e : entries) {
      result.merge(FilesManager.jarFileExtract(jar, e, new File(targetFolder, e.getName().substring(prefix.length()))));
    }
    return result;
  }

  /**
   * Extract the file <em>entry</em> from the given <em>jar</em> into the <em>target file</em>.
   */
  private static final Result jarFileExtract(final JarFile jar, final JarEntry entry, final File targetFile)
      throws IOException {
    targetFile.getParentFile().mkdirs();
    if (entry.isDirectory()) {
      targetFile.mkdir();
      return Result.newInstance();
    }
    if (FilesManager.isContentEqual(jar.getInputStream(entry), targetFile)) {
      return Result.newCachedInstance();
    } else {
      return FilesManager.streamExtract(jar.getInputStream(entry), targetFile);
    }
  }

  /**
   * Copy the content represented by the given <em>inputStream</em> into the <em>target file</em>. No checking is
   * performed for equality between input stream and target file. Data are always written.
   *
   * @return A Result object with information about extraction status
   */
  private static final Result streamExtract(final InputStream inputStream, final File targetFile) throws IOException {
    if (!targetFile.getParentFile().exists()) {
      targetFile.getParentFile().mkdirs();
    }
    final InputStream bufferedInput = new BufferedInputStream(inputStream);

    final OutputStream fileOutputStream = new FileOutputStream(targetFile);
    final OutputStream outputStream = new BufferedOutputStream(fileOutputStream);

    final byte[] buffer = new byte[2048];
    int readLength = 0;
    while ((readLength = bufferedInput.read(buffer)) != -1) {
      outputStream.write(buffer, 0, readLength);
    }
    bufferedInput.close();
    outputStream.close();
    fileOutputStream.close();

    return Result.newOkInstance();
  }

  /**
   * Search on the file system for a file or folder corresponding to the given path. If not found, search on the current
   * classpath. If this method returns an URL, it always represents an existing file.
   *
   * @param path
   *          A path
   * @param bundleFilter
   *          A filter to indicate in which bundle to look for
   * @return An URL for an existing file, or null
   */

  static URL getUrl(final String path, final String... bundleNames) throws MalformedURLException, IOException {
    return URLResolver.findFirstInPluginList(path, bundleNames);
  }

  /**
   * Check if given a CharSequence have exactly the same content than file b.
   */
  static boolean isContentEqual(final CharSequence a, final File b) throws IOException {
    return FilesManager.isContentEqual(new ByteArrayInputStream(a.toString().getBytes()), b);
  }

  /**
   * Check if given File a have exactly the same content than File b.
   */
  static boolean isContentEqual(final File a, final File b) throws IOException {
    final InputStream fileInputStream = new FileInputStream(a);
    final boolean contentEqual = FilesManager.isContentEqual(fileInputStream, b);
    fileInputStream.close();
    return contentEqual;
  }

  /**
   * <p>
   * Compare the content of input stream <em>a</em> and file <em>b</em>. Returns true if the
   * </p>
   *
   * <p>
   * <strong>Important</strong>: This method will close the input stream <em>a</em> before returning the result.
   * </p>
   *
   * @param a
   *          An input stream
   * @param b
   *          A file
   * @return true if content in a is equals to content in b
   */
  static boolean isContentEqual(final InputStream a, final File b) throws IOException {
    if (!b.exists()) {
      return false;
    }
    final InputStream inputStreamA = new BufferedInputStream(a);
    final InputStream inputStreamB = new BufferedInputStream(new FileInputStream(b));

    int byteA = 0;
    int byteB = 0;
    do {
      byteA = inputStreamA.read();
      byteB = inputStreamB.read();
    } while ((byteA == byteB) && (byteA != -1));
    inputStreamA.close();
    inputStreamB.close();

    return byteA == -1;
  }

  /**
   * Write <em>content</em> to the given <em>targetFolder</em> in a new file called <em>fileName</em>. This method will
   * write the content to the target file only if it is empty or if its content is different than that given.
   *
   * @param content
   *          The text content to write
   * @param targetFolder
   *          The folder where the file should be created
   * @param fileName
   *          The name of the new file
   */
  static Result writeFile(final CharSequence content, final String targetFolder, final String fileName)
      throws IOException {
    return FilesManager.writeFile(content, new File(FilesManager.sanitize(targetFolder), fileName));
  }

  /**
   * Write <em>content</em> to the given file <em>path</em>. This method will write the content to the target file only
   * if it is empty or if its content is different than that given.
   *
   * @param content
   *          The text content to write
   * @param path
   *          The path of the file to write
   */
  static Result writeFile(final CharSequence content, final String path) throws IOException {
    return FilesManager.writeFile(content, new File(FilesManager.sanitize(path)));
  }

  /**
   * Write the <em>content</em> into the <em>targetFile</em> only if necessary.
   */
  static Result writeFile(final CharSequence content, final File targetFile) throws IOException {
    if (FilesManager.isContentEqual(content, targetFile)) {
      return Result.newCachedInstance();
    }

    if (!targetFile.exists()) {
      targetFile.getParentFile().mkdirs();
      targetFile.createNewFile();
    }
    final PrintStream ps = new PrintStream(new FileOutputStream(targetFile));
    ps.print(content);
    ps.close();
    return Result.newOkInstance();
  }

  /**
   * Read the file at the given <em>path</em> and returns its content as a String.
   *
   * @param path
   *          The path of the file to read
   * @returns The content of the file
   * @throws FileNotFoundException
   *           If the file doesn't exists
   */
  static String readFile(final String path, final String... bundleNames) throws IOException, URISyntaxException {
    final URL url = FilesManager.getUrl(path, bundleNames);
    if (url == null) {
      throw new FileNotFoundException(path);
    }
    final StringBuilder builder = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        builder.append(line + "\n");
      }
    }
    return builder.toString();
  }

  /**
   * Transform the given path to a valid filesystem one.
   *
   * <ul>
   * <li>It replaces first '~' by the home directory of the current user.</li>
   * </ul>
   */
  public static final String sanitize(final String path) {
    // We use the following construction because Xtend infer '~' as a String instead of a char
    // path.substring(0,1).equals('~')
    if (!((path == null) || (path.length() == 0)) && path.substring(0, 1).equals("~")) {
      final StringBuilder builder = new StringBuilder(System.getProperty("user.home"));
      builder.append(File.separatorChar).append(path.substring(1));
      return builder.toString();
    }
    return path;
  }

  /**
   * Delete the given d directory and all its content
   */
  public static final void recursiveDelete(final File d) {
    for (final File e : d.listFiles()) {
      if (e.isFile()) {
        e.delete();
      } else if (e.isDirectory()) {
        FilesManager.recursiveDelete(e);
      }
    }
    d.delete();
  }

}
