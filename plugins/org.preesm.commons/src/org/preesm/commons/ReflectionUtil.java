package org.preesm.commons;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
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

  private static final Set<Bundle> getContributorBundles(final String extensionPointID) {
    final IExtensionRegistry registry = Platform.getExtensionRegistry();
    final IExtensionPoint extensionPoint = registry.getExtensionPoint(extensionPointID);
    final IExtension[] extensions = extensionPoint.getExtensions();
    final Set<Bundle> collect = Stream.of(extensions).map(IExtension::getContributor)
        .map(contributor -> Platform.getBundle(contributor.getName())).collect(Collectors.toSet());
    return Collections.unmodifiableSet(collect);
  }

  private static final Set<BundleWiring> getContributorBundleWirings(final String extensionPointID) {
    final Set<Bundle> contributorBundles = getContributorBundles(extensionPointID);
    final Set<BundleWiring> collect = contributorBundles.stream().map(b -> b.adapt(BundleWiring.class))
        .collect(Collectors.toSet());
    return Collections.unmodifiableSet(collect);
  }

  private static Collection<String> listResources(final BundleWiring bundleWiring) {
    final Set<String> res = new LinkedHashSet<>();

    final Collection<String> allResources = bundleWiring.listResources("/", "*.class",
        BundleWiring.LISTRESOURCES_RECURSE | BundleWiring.LISTRESOURCES_LOCAL);
    final Stream<String> filteredResourcesStream = allResources.stream();
    final Stream<String> mappedResourcesToQualifiedClassName = filteredResourcesStream
        .map(s -> s.replace(".class", "").replace("/", "."));
    res.addAll(mappedResourcesToQualifiedClassName.collect(Collectors.toSet()));

    return Collections.unmodifiableSet(res);
  }

  /**
   *
   */
  public static final Collection<Class<?>> lookupChildClassesOf(final String extensionPointID,
      final Class<?> parentClassOrInterface) {
    final Set<Class<?>> res = new LinkedHashSet<>();

    final Set<BundleWiring> bundleWirings = getContributorBundleWirings(extensionPointID);
    for (final BundleWiring bundleWiring : bundleWirings) {
      final ClassLoader classLoader = bundleWiring.getClassLoader();

      final Collection<String> listResources = listResources(bundleWiring);

      for (final String resource : listResources) {
        try {
          final Class<?> loadClass = classLoader.loadClass(resource);
          if (!loadClass.isInterface() && !Modifier.isAbstract(loadClass.getModifiers())) {
            if (parentClassOrInterface.isAssignableFrom(loadClass)) {
              res.add(loadClass);
            }
          }
        } catch (final ClassNotFoundException | NoClassDefFoundError e) {
          // skip this class
        }
      }
    }

    return res;
  }

  /**
   *
   */
  public static final Collection<Class<?>> lookupAnnotatedClasses(final String extensionPointID,
      final Class<? extends Annotation> annotationToMatch) {
    final Set<Class<?>> res = new LinkedHashSet<>();

    final Set<BundleWiring> bundleWirings = getContributorBundleWirings(extensionPointID);
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

    return res;
  }
}
