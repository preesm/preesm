package org.preesm.commons.files;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;
import org.preesm.commons.exceptions.PreesmRuntimeException;

/**
 *
 * Set of methods to help developers locate, load or read resources from the Preesm source code (or binary) base. This
 * is useful for load templates, test inputs, default scripts, etc. This helper is not intended to be used for writing
 * files.
 *
 *
 *
 * To find helper methods for input/output (algorithm, generated code, etc.), see {@link PreesmIOHelper}.
 *
 * @author anmorvan
 *
 */
public class PreesmResourcesHelper {

  /**
   * Path included in the classpath as defined in the parent pom.xml file.
   *
   *
   */
  private static final String RESOURCE_PATH = "resources/";

  /**
   * Singleton
   */
  private static final PreesmResourcesHelper instance = new PreesmResourcesHelper();

  public static final PreesmResourcesHelper getInstance() {
    return instance;
  }

  /**
   * Reads the content of the given URI. This method converts the URI to URL firsts, then open a stream to read its
   * content.
   */
  public final String read(final URI uri) throws IOException {
    if (uri == null) {
      throw new FileNotFoundException();
    }
    final URL url = uri.toURL();
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
   * Try to resolve the URI of a resource given its path. Lookup strategy is to first use the {@link FileLocator} of
   * Eclipse, then use {@link ClassLoader#getResources(String)}
   */
  public final URI resolve(final String resource, final List<String> bundleFilterList, final Object o) {
    URI uri = resolveFromBundles(resource, bundleFilterList);
    if (uri == null) {
      uri = resolveFromClass(resource, o);
    }
    return uri;
  }

  final URI resolveFromClass(final String resource, final Object o) {
    final URI uri = resolveFromClassloader(resource, o);
    if (uri != null) {
      return uri;
    }
    return null;
  }

  final URI resolveFromClassloader(final String resource, final Object o) {
    final ClassLoader classLoader = o.getClass().getClassLoader();
    final URL url = classLoader.getResource(resource);
    if (url == null) {
      return null;
    }
    URI res = null;
    try {
      res = url.toURI();
    } catch (final URISyntaxException e) {
      throw new PreesmRuntimeException("Could not create URI", e);
    }
    return res;
  }

  /**
   * Resolves the resource URI from the bundles given in the list. The returned URI represents a URL that has been
   * resolved by {@link FileLocator#resolve(URL)}.
   */
  final URI resolveFromBundles(final String resource, final List<String> bundleFilterList) {

    URL resolveBundleURL = resolveFromBundlesFileLocator(resource, bundleFilterList);
    if (resolveBundleURL == null) {
      resolveBundleURL = resolveFromBundleWiring(resource, bundleFilterList);
    }
    if (resolveBundleURL == null) {
      return null;
    }

    final URL resolve;
    try {
      resolve = FileLocator.resolve(resolveBundleURL);
    } catch (final IOException e) {
      throw new PreesmRuntimeException("Could not resolve URI" + resolveBundleURL, e);
    }

    final URI uri;
    try {
      uri = resolve.toURI();
    } catch (final URISyntaxException e) {
      throw new PreesmRuntimeException("Could not create URI", e);
    }

    return uri;
  }

  /**
   * Alternative to {@link #resolveFromBundlesFileLocator} that does not use {@link FileLocator}
   */
  final URL resolveFromBundlesEntries(final String resource, final List<String> bundleFilterList) {
    final ResourcesPlugin plugin = ResourcesPlugin.getPlugin();
    if (plugin == null) {
      // Eclipse is not running (call from plain Java or JUnit)
      return null;
    }
    final Bundle[] allBundles = plugin.getBundle().getBundleContext().getBundles();
    return Stream.of(allBundles)
        .filter(b -> bundleFilterList.isEmpty() || bundleFilterList.contains(b.getSymbolicName()))
        .map(b -> b.getEntry(resource)).filter(Objects::nonNull).findFirst().orElse(null);
  }

  final URL resolveFromBundlesFileLocator(final String resource, final List<String> bundleFilterList) {
    final ResourcesPlugin plugin = ResourcesPlugin.getPlugin();
    if (plugin == null) {
      // Eclipse is not running (call from plain Java or JUnit)
      return null;
    }
    final Bundle[] allBundles = plugin.getBundle().getBundleContext().getBundles();

    for (final Bundle bundle : allBundles) {
      if (bundleFilterList.isEmpty() || bundleFilterList.contains(bundle.getSymbolicName())) {
        final Path resourcePath = new Path(resource);
        final URL res = FileLocator.find(bundle, resourcePath);
        if (res != null) {
          return res;
        }
      }
    }
    return null;
  }

  final URL resolveFromBundleWiring(final String resource, final List<String> bundleFilterList) {
    final ResourcesPlugin plugin = ResourcesPlugin.getPlugin();
    if (plugin == null) {
      // Eclipse is not running (call from plain Java or JUnit)
      return null;
    }
    final Bundle[] allBundles = plugin.getBundle().getBundleContext().getBundles();
    for (final Bundle bundle : allBundles) {
      if (bundleFilterList.isEmpty() || bundleFilterList.contains(bundle.getSymbolicName())) {
        final BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

        final Collection<String> allResources = bundleWiring.listResources("/", RESOURCE_PATH + resource,
            BundleWiring.LISTRESOURCES_LOCAL);

        for (final String resultPath : allResources) {
          final URL candidate = resolveFromBundlesFileLocator(resultPath, bundleFilterList);
          if (candidate != null) {
            return candidate;
          }
        }
      }
    }
    return null;
  }
}
