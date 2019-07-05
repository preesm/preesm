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
package org.preesm.commons;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.emf.common.EMFPlugin.EclipsePlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.logger.PreesmLogger;

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

  private final Map<String, Class<?>> taskRegistry = new LinkedHashMap<>();

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
    final List<Class<?>> allTasks = ReflectionUtil.lookupAnnotatedClasses(PreesmPlugin.PREESM_PLUGIN_EXTENSION_POINT_ID,
        PreesmTask.class);
    for (final Class<?> task : allTasks) {
      final String id = task.getAnnotation(PreesmTask.class).id();
      if (taskRegistry.containsKey(id)) {
        PreesmLogger.getLogger().warning("Several tasks have the same id '" + id + "'.");
      }
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
    final Map<String, Set<Class<?>>> res = new LinkedHashMap<>();
    for (final Entry<String, Class<?>> taskEntry : taskRegistry.entrySet()) {
      final Class<?> task = taskEntry.getValue();
      final String category = task.getAnnotation(PreesmTask.class).category();

      final Set<Class<?>> categoryContent = res.getOrDefault(category, new LinkedHashSet<>());
      categoryContent.add(task);

      res.put(category, categoryContent);
    }
    return Collections.unmodifiableMap(res);
  }

}
