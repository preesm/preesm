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

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.preesm.commons.exceptions.PreesmResourceException;

/**
 * <p>
 * Set of methods to help developers locate, load or read resources from the Preesm source code (or binary) base. This
 * is useful for loading templates, test inputs, default scripts, etc. This helper is not intended to be used for
 * writing files. Entry point is the {@link #resolve(String, List, Object)} method.
 * </p>
 *
 * <p>
 * The resolved resources have to be located under the '&lt;project&gt;/resources/' folder, and the full path under this
 * folder is required. This path is defined as constant (see {@link #RESOURCE_PATH}) and as project setting in the
 * parent pom.xml file. In order for the resources to be retrievable from an exported Eclipse plug-in, the
 * '&lt;project&gt;/resources/' must be included in the 'bin.includes' section of the 'build.properties' file.
 * </p>
 *
 * <p>
 * A sample call would look like
 *
 * <pre>
 * final URL url = PreesmResourcesHelper.getInstance().resolve("GanttHelp.html", "org.preesm.algorithm",
 *     GanttPlotter.class);
 * final String content = PreesmResourcesHelper.getInstance().read(url);
 * </pre>
 * </p>
 *
 * <p>
 * To find helper methods for input/output (algorithm, generated code, etc.), see {@link PreesmIOHelper}.
 * </p>
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

  /**
   * Returns the singleton instance of this helper.
   */
  public static final PreesmResourcesHelper getInstance() {
    return instance;
  }

  /**
   * Reads the content of the given URI. This method converts the URI to URL firsts, then open a stream to read its
   * content.
   */
  public final String read(final String resource, final Class<?> projectClass) throws IOException {
    final URL url = this.resolve(resource, projectClass);
    return URLHelper.read(url);
  }

  /**
   * <p>
   * Try to resolve the URI of a resource given its path. Lookup strategy is to first use the {@link FileLocator} of
   * Eclipse. The path will be resolved in the project (or bundle) whose symbolic name given as argument, under the
   * 'resources/' folder. The symbolic name of a bundle is given in the META-INF/MANIFEST.MF file.
   * </p>
   *
   * <p>
   * If Eclipse is not running (i.e. running using plain java), this method falls back on
   * {@link ClassLoader#getResources(String)} to try resolving from the classpath resources. In order to lookup in the
   * proper classloader, any class from the project containing the resource has to be given as argument.
   * </p>
   */
  public final URL resolve(final String resource, final Class<?> projectClass) {
    if (resource == null || resource.isEmpty()) {
      throw new IllegalArgumentException("Expecting non empty argument");
    }
    URL url = resolveFromBundle(resource, projectClass);
    if (url == null) {
      url = resolveFromClass(resource, projectClass);
    }
    if (url == null) {
      throw new PreesmResourceException(
          "Could not locate resource '" + resource + "' from class '" + projectClass + "'");
    }
    return url;
  }

  /**
   * Resolves the resource URI from the bundles given in the list. The returned URI represents a URL that has been
   * resolved by {@link FileLocator#resolve(URL)}.
   */
  final URL resolveFromBundle(final String resource, final Class<?> projectClass) {
    if (projectClass == null) {
      throw new IllegalArgumentException("Expecting non null class");
    }
    final ResourcesPlugin plugin = ResourcesPlugin.getPlugin();
    if (plugin == null) {
      // Eclipse is not running (call from plain Java or JUnit)
      return null;
    }
    final Bundle bundle = FrameworkUtil.getBundle(projectClass);
    if (bundle == null) {
      throw new NoSuchElementException(
          "Given bundle filter name '" + projectClass + "' does not exist or is not loaded.");
    }
    // prefix with RESOURCE_PATH to make sure the lookup is done in the resources folder
    // to mimic classpath resources entry.
    final Path resourcePath = new Path(RESOURCE_PATH + resource);
    final URL resolveBundleURL = FileLocator.find(bundle, resourcePath);
    if (resolveBundleURL == null) {
      return null;
    }

    final URL resolve;
    try {
      resolve = FileLocator.resolve(resolveBundleURL);
    } catch (final IOException e) {
      throw new PreesmResourceException("Could not resolve URI" + resolveBundleURL, e);
    }

    return resolve;
  }

  /**
   * Try resolving the resource from the resources in the classpath using classloader
   */
  final URL resolveFromClass(final String resource, final Class<?> projectClass) {
    if (projectClass == null) {
      throw new IllegalArgumentException("Expecting non null argument");
    }

    final ClassLoader classLoader = projectClass.getClassLoader();
    // no need to prefix the resource path with RESOURCE_PATH since the resource folder
    // should already be included in the classpath
    final URL url = classLoader.getResource(resource);
    if (url == null) {
      return null;
    }
    return url;
  }
}
