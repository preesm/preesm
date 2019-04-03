package org.preesm.commons;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.IContributor;
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
public class ReflectionUitl {

  /**
   *
   */
  public static final Collection<Class<?>> lookupChildClassesOf(final String extensionPointID,
      final Class<?> parentClassOrInterface) {
    final Set<Class<?>> res = new HashSet<>();

    final long currentTimeMillis = System.currentTimeMillis();
    final IExtensionRegistry registry = Platform.getExtensionRegistry();
    final IExtensionPoint extensionPoint = registry.getExtensionPoint(extensionPointID);
    final IExtension[] extensions = extensionPoint.getExtensions();
    for (final IExtension extension : extensions) {
      final IContributor contributor = extension.getContributor();
      final String name = contributor.getName();
      final Bundle bundle = Platform.getBundle(name);
      final BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
      final ClassLoader classLoader = bundleWiring.getClassLoader();

      final Collection<String> listResources = bundleWiring.listResources("/", "*", BundleWiring.LISTRESOURCES_RECURSE)
          .stream().filter(s -> s.endsWith(".class") && !s.contains("$"))
          .map(s -> s.replace(".class", "").replace("/", ".")).collect(Collectors.toSet());

      for (final String resource : listResources) {
        try {
          final Class<?> loadClass = classLoader.loadClass(resource);
          if (!loadClass.isInterface()) {
            if (parentClassOrInterface.isAssignableFrom(loadClass)) {
              res.add(loadClass);
            }
          }
        } catch (final ClassNotFoundException | NoClassDefFoundError e) {
          // skip this class
        }
      }
    }
    long elapsed = System.currentTimeMillis() - currentTimeMillis;
    System.out.println(((double) elapsed / 1000));

    return res;
  }

  /**
   *
   */
  public static final Collection<Class<?>> lookupAnnotatedClasses(final String extensionPointID,
      final Class<? extends Annotation> annotationToMatch) {
    final Set<Class<?>> res = new HashSet<>();

    final long currentTimeMillis = System.currentTimeMillis();
    final IExtensionRegistry registry = Platform.getExtensionRegistry();
    final IExtensionPoint extensionPoint = registry.getExtensionPoint(extensionPointID);
    final IExtension[] extensions = extensionPoint.getExtensions();
    for (final IExtension extension : extensions) {
      final IContributor contributor = extension.getContributor();
      final String name = contributor.getName();
      final Bundle bundle = Platform.getBundle(name);
      final BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
      final ClassLoader classLoader = bundleWiring.getClassLoader();

      final Collection<String> listResources = bundleWiring.listResources("/", "*", BundleWiring.LISTRESOURCES_RECURSE)
          .stream().filter(s -> s.endsWith(".class") && !s.contains("$"))
          .map(s -> s.replace(".class", "").replace("/", ".")).collect(Collectors.toSet());

      for (final String resource : listResources) {
        try {
          final Class<?> loadClass = classLoader.loadClass(resource);
          if (!loadClass.isInterface() && loadClass.isAnnotationPresent(annotationToMatch)) {
            res.add(loadClass);
          }
        } catch (final ClassNotFoundException | NoClassDefFoundError e) {
          // skip this class
        }
      }
    }
    long elapsed = System.currentTimeMillis() - currentTimeMillis;
    System.out.println(((double) elapsed / 1000));

    return res;
  }
}
