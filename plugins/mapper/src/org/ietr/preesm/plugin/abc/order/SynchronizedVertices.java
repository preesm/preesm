/**
 * 
 */
package org.ietr.preesm.plugin.abc.order;

import java.util.List;

import org.ietr.preesm.plugin.mapper.model.ImplementationVertexProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.TimingVertexProperty;

/**
 * A group of vertices that have the same total order and the same T-Level
 * 
 * @author mpelcat
 */
public class SynchronizedVertices implements IScheduleElement {

	private List<MapperDAGVertex> vertices = null;

	@Override
	public String getName() {
		return null;
	}

	@Override
	public TimingVertexProperty getTimingVertexProperty() {
		if (!vertices.isEmpty()) {
			return vertices.get(0).getTimingVertexProperty();
		}
		return null;
	}

	@Override
	public ImplementationVertexProperty getImplementationVertexProperty() {
		if (!vertices.isEmpty()) {
			return vertices.get(0).getImplementationVertexProperty();
		}
		return null;
	}
}
