/**
 * 
 */
package org.ietr.preesm.mapper.gantt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.sf.dftools.architecture.slam.ComponentInstance;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.tools.TopologicalDAGIterator;

/**
 * GanttData carries information for the whole data in a Gantt chart
 * 
 * @author mpelcat
 */
public class GanttData {

	private Map<String, GanttComponent> components = null;

	public GanttData() {
		super();
		components = new HashMap<String, GanttComponent>();
	}

	/**
	 * Getting a component. The component is created if non existing.
	 */
	private GanttComponent getComponent(String id) {
		if (components.get(id) == null) {
			GanttComponent cmp = new GanttComponent(id);
			components.put(id, cmp);
		}
		return components.get(id);
	}

	private boolean insertTask(String taskId, String componentId,
			long startTime, long duration) {
		GanttComponent cmp = getComponent(componentId);
		GanttTask task = new GanttTask(startTime, duration, taskId, cmp);
		return cmp.insertTask(task);
	}

	/**
	 * Inserts all tasks from a given DAG.
	 */
	public boolean insertDag(MapperDAG dag) {
		TopologicalDAGIterator viterator = new TopologicalDAGIterator(dag);

		while (viterator.hasNext()) {
			MapperDAGVertex currentVertex = (MapperDAGVertex) viterator.next();
			ComponentInstance cmp = currentVertex
					.getImplementationVertexProperty().getEffectiveComponent();

			if (cmp != DesignTools.NO_COMPONENT_INSTANCE) {
				long startTime = currentVertex.getTimingVertexProperty()
						.getNewtLevel();
				long duration = currentVertex.getTimingVertexProperty()
						.getCost();
				String id = currentVertex.getName()
						+ " (x"
						+ currentVertex.getInitialVertexProperty()
								.getNbRepeat() + ")";
				if(!insertTask(id, cmp.getInstanceName(), startTime, duration)){
					return false;
				}
			} else {
				WorkflowLogger.getLogger().log(
						Level.SEVERE,
						"Gantt: task can not be displayed in Gantt because it has no component: "
								+ currentVertex);
			}
		}
		return true;
	}
	
	/**
	 * Returns components in alphabetical order.
	 */
	public List<GanttComponent> getComponents(){
		List<GanttComponent> componentList = new ArrayList<GanttComponent>(components.values());
		
		Collections.sort(componentList, new Comparator<GanttComponent>() {
			@Override
			public int compare(GanttComponent c1, GanttComponent c2){
				return c1.getId().compareTo(c2.getId());
			}
		});
		return componentList;
	}
}
