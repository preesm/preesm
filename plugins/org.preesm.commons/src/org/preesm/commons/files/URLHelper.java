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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 *
 * @author anmorvan
 *
 */
public class URLHelper {

  private URLHelper() {
    // forbid instantiation
  }

  /**
   * resolve path under baseUrl
   */
  public static final URL resolve(final URL baseUrl, final String path) {
    try {
      return baseUrl.toURI().resolve(path).toURL();
    } catch (final MalformedURLException | URISyntaxException e) {
      return null;
    }
  }

  /**
   * Reads the content of the given URL.
   */
  public static final String read(final URL url) throws IOException {
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
   * Assumes srcUrl is a "folder" URL
   */
  public static final List<String> list(final URL url) throws IOException {
    final URI uri;
    try {
      uri = url.toURI();
    } catch (final URISyntaxException e) {
      throw new IllegalArgumentException("Could not convert URL", e);
    }

    final Path myPath;
    if ("jar".equals(uri.getScheme())) {
      try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap())) {
        myPath = fileSystem.getPath("/");
      }
    } else {
      myPath = Paths.get(uri);
    }
    final List<Path> collect;
    try (final Stream<Path> walk = Files.walk(myPath)) {
      collect = walk.map(myPath::relativize).collect(Collectors.toList());
    }
    final List<String> res = new ArrayList<>();
    for (final Path pathToTest : collect) {
      final String pathString = pathToTest.toString().replace('\\', '/');
      if (collect.stream().anyMatch(currentPath -> currentPath != pathToTest && currentPath.startsWith(pathToTest))) {
        res.add(pathString + "/");
      } else {
        res.add(pathString);
      }
    }

    return res;
  }

  /**
   * Assumes srcUrl is a "folder" URL
   */
  public static final void copyContent(final URL srcUrl, final IContainer destContainer) throws IOException {
    final File destFolder = destContainer.getRawLocation().toFile();
    URLHelper.copyContent(srcUrl, destFolder);
    try {
      destContainer.touch(null);
      destContainer.refreshLocal(IResource.DEPTH_INFINITE, null);
    } catch (final CoreException e) {
      // skip
    }
  }

  /**
   * Assumes srcUrl is a "folder" URL
   */
  public static final void copyContent(final URL srcUrl, final File destFolder) throws IOException {
    final List<String> list = URLHelper.list(srcUrl);

    // init folder structure
    list.stream().filter(s -> s.endsWith("/"))
        .forEach(pathString -> destFolder.toPath().resolve(pathString).toFile().mkdir());

    // copy files & content
    list.stream().filter(s -> !s.endsWith("/") && !s.isEmpty()).forEach(pathString -> {
      try {
        final URL u = srcUrl.toURI().resolve(pathString).toURL();
        final File file = destFolder.toPath().resolve(pathString).toFile();
        FileUtils.copyInputStreamToFile(u.openStream(), file);
      } catch (final Exception e) {
        // skip
      }
    });
  }

}
