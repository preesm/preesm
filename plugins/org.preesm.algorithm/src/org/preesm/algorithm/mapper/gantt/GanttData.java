/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2012 - 2014)
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
package org.preesm.algorithm.mapper.gantt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.preesm.algorithm.iterators.TopologicalDAGIterator;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.utils.DesignTools;

/**
 * GanttData carries information that can be displayed in a Gantt chart.
 *
 * @author mpelcat
 */
public class GanttData {

  /** The components. */
  private Map<String, GanttComponent> components = null;

  /**
   * Instantiates a new gantt data.
   */
  public GanttData() {
    super();
    this.components = new LinkedHashMap<>();
  }

  /**
   * Getting a component. The component is created if non existing.
   *
   * @param id
   *          the id
   * @return the component
   */
  private GanttComponent getComponent(final String id) {
    if (this.components.get(id) == null) {
      final GanttComponent cmp = new GanttComponent(id);
      this.components.put(id, cmp);
    }
    return this.components.get(id);
  }

  /**
   * Insert task.
   *
   * @param taskId
   *          the task id
   * @param componentId
   *          the component id
   * @param startTime
   *          the start time
   * @param duration
   *          the duration
   * @return true, if successful
   */
  private boolean insertTask(final String taskId, final String componentId, final long startTime, final long duration) {
    final GanttComponent cmp = getComponent(componentId);
    final GanttTask task = new GanttTask(startTime, duration, taskId, cmp);
    return cmp.insertTask(task);
  }

  /**
   * Inserts all tasks from a given DAG.
   *
   * @param dag
   *          the dag
   * @return true, if successful
   */
  public boolean insertDag(final MapperDAG dag) {
    final TopologicalDAGIterator viterator = new TopologicalDAGIterator(dag);

    while (viterator.hasNext()) {
      final MapperDAGVertex currentVertex = (MapperDAGVertex) viterator.next();
      final ComponentInstance cmp = currentVertex.getEffectiveComponent();

      if (cmp != DesignTools.NO_COMPONENT_INSTANCE) {
        final long startTime = currentVertex.getTiming().getTLevel();
        final long duration = currentVertex.getTiming().getCost();
        final String id = currentVertex.getName() + " (x" + currentVertex.getInit().getNbRepeat() + ")";
        if (!insertTask(id, cmp.getInstanceName(), startTime, duration)) {
          return false;
        }
      } else {
        final String message = "Gantt: task can not be displayed in Gantt because it has no component: "
            + currentVertex;
        PreesmLogger.getLogger().log(Level.SEVERE, message);
      }
    }
    return true;
  }

  /**
   * Returns components in alphabetical order.
   *
   * @return the components
   */
  public List<GanttComponent> getComponents() {
    final List<GanttComponent> componentList = new ArrayList<>(this.components.values());

    Collections.sort(componentList, (c1, c2) -> c1.getId().compareTo(c2.getId()));
    return componentList;
  }
}
