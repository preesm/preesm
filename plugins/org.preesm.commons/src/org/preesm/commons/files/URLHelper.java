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

    Path myPath;
    if ("jar".equals(uri.getScheme())) {
      FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
      myPath = fileSystem.getPath("/");
    } else {
      myPath = Paths.get(uri);
    }
    final List<Path> collect = Files.walk(myPath).map(p -> myPath.relativize(p)).collect(Collectors.toList());
    final List<String> res = new ArrayList<>();
    for (final Path pathToTest : collect) {
      if (collect.stream().anyMatch(currentPath -> currentPath != pathToTest && currentPath.startsWith(pathToTest))) {
        res.add(pathToTest.toString() + "/");
      } else {
        res.add(pathToTest.toString());
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
    list.stream().filter(s -> s.endsWith("/")).forEach(pathString -> {
      destFolder.toPath().resolve(pathString).toFile().mkdir();
    });

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
