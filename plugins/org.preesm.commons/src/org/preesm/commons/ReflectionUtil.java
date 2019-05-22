/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2019)
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
package org.preesm.commons;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

/**
 *
 * @author anmorvan
 *
 */
public class ReflectionUtil {

  private static final List<Bundle> getContributorBundles(final String extensionPointID) {
    final IExtensionRegistry registry = Platform.getExtensionRegistry();
    final IExtensionPoint extensionPoint = registry.getExtensionPoint(extensionPointID);
    final IExtension[] extensions = extensionPoint.getExtensions();
    final List<Bundle> collect = Stream.of(extensions).map(IExtension::getContributor)
        .map(contributor -> Platform.getBundle(contributor.getName())).collect(Collectors.toList());
    return Collections.unmodifiableList(collect);
  }

  private static final List<BundleWiring> getContributorBundleWirings(final String extensionPointID) {
    final List<Bundle> contributorBundles = getContributorBundles(extensionPointID);
    final List<BundleWiring> collect = contributorBundles.stream().map(b -> b.adapt(BundleWiring.class))
        .collect(Collectors.toList());
    return Collections.unmodifiableList(collect);
  }

  private static List<String> listResources(final BundleWiring bundleWiring) {
    final List<String> res = new ArrayList<>();

    final Collection<String> allResources = bundleWiring.listResources("/", "*.class",
        BundleWiring.LISTRESOURCES_RECURSE | BundleWiring.LISTRESOURCES_LOCAL);
    final Stream<String> filteredResourcesStream = allResources.stream();
    final Stream<String> mappedResourcesToQualifiedClassName = filteredResourcesStream
        .map(s -> s.replace(".class", "").replace("/", "."));
    res.addAll(mappedResourcesToQualifiedClassName.collect(Collectors.toList()));

    return Collections.unmodifiableList(res);
  }

  /**
   *
   */
  public static final <T> List<Class<? extends T>> lookupChildClassesOf(final String extensionPointID,
      final Class<T> parentClassOrInterface) {
    final List<Class<? extends T>> res = new ArrayList<>();

    final List<BundleWiring> bundleWirings = getContributorBundleWirings(extensionPointID);
    for (final BundleWiring bundleWiring : bundleWirings) {
      final ClassLoader classLoader = bundleWiring.getClassLoader();

      final Collection<String> listResources = listResources(bundleWiring);

      for (final String resource : listResources) {
        try {
          final Class<?> loadClass = classLoader.loadClass(resource);
          if (!loadClass.isInterface() && !Modifier.isAbstract(loadClass.getModifiers())) {
            if (parentClassOrInterface.isAssignableFrom(loadClass)) {
              final Class<? extends T> asSubclass = loadClass.asSubclass(parentClassOrInterface);
              res.add(asSubclass);
            }
          }
        } catch (final ClassNotFoundException | NoClassDefFoundError e) {
          // skip this class
        }
      }
    }
    Collections.sort(res, (c1, c2) -> c1.getName().compareTo(c2.getName()));
    return Collections.unmodifiableList(res);
  }

  /**
   *
   */
  public static final List<Class<?>> lookupAnnotatedClasses(final String extensionPointID,
      final Class<? extends Annotation> annotationToMatch) {
    final List<Class<?>> res = new ArrayList<>();

    final List<BundleWiring> bundleWirings = getContributorBundleWirings(extensionPointID);
    for (final BundleWiring bundleWiring : bundleWirings) {
      final ClassLoader classLoader = bundleWiring.getClassLoader();

      final Collection<String> listResources = listResources(bundleWiring);

      for (final String resource : listResources) {
        try {
          final Class<?> loadClass = classLoader.loadClass(resource);
          if (!loadClass.isInterface() && !Modifier.isAbstract(loadClass.getModifiers())
              && loadClass.isAnnotationPresent(annotationToMatch)) {
            res.add(loadClass);
          }
        } catch (final ClassNotFoundException | NoClassDefFoundError e) {
          // skip this class
        }
      }
    }
    Collections.sort(res, (c1, c2) -> c1.getName().compareTo(c2.getName()));
    return Collections.unmodifiableList(res);
  }
}
