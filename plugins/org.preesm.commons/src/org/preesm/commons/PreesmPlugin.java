package org.preesm.commons;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.emf.common.EMFPlugin.EclipsePlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.preesm.commons.doc.annotations.PreesmTask;

/**
 *
 *
 */
public class PreesmPlugin extends EclipsePlugin implements BundleActivator {

  private static PreesmPlugin instance;

  public static final PreesmPlugin getInstance() {
    return instance;
  }

  public static final String PREESM_PLUGIN_EXTENSION_POINT_ID = "org.preesm.commons.plugin";

  private final Map<String, Class<?>> taskRegistry = new HashMap<>();

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    instance = this;
    fillRegistry();
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    taskRegistry.clear();
    instance = null;
    super.stop(context);
  }

  private final void fillRegistry() {
    taskRegistry.clear();
    final Collection<Class<?>> allTasks = ReflectionUtil
        .lookupAnnotatedClasses(PreesmPlugin.PREESM_PLUGIN_EXTENSION_POINT_ID, PreesmTask.class);
    for (final Class<?> task : allTasks) {
      final String id = task.getAnnotation(PreesmTask.class).id();
      taskRegistry.put(id, task);
    }
  }

  public final Class<?> getTask(final String id) {
    return taskRegistry.get(id);
  }

  /**
   * Returns a Map where Keys are category names and values are tasks associated to this category
   */
  public final Map<String, Set<Class<?>>> getTasksByCategory() {
    final Map<String, Set<Class<?>>> res = new HashMap<>();
    for (final Entry<String, Class<?>> taskEntry : taskRegistry.entrySet()) {
      final Class<?> task = taskEntry.getValue();
      final String category = task.getAnnotation(PreesmTask.class).category();

      final Set<Class<?>> categoryContent = res.getOrDefault(category, new HashSet<>());
      categoryContent.add(task);

      res.put(category, categoryContent);
    }
    return Collections.unmodifiableMap(res);
  }

}
