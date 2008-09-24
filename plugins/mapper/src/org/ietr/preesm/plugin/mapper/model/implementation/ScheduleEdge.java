package org.ietr.preesm.plugin.mapper.model.implementation;

import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * A schedule edge is automatically generated and expresses the sequential
 * execution of the joined vertices
 * 
 * @author mpelcat   
 */
public class ScheduleEdge extends MapperDAGEdge {

	@Override
	public String toString() {

		String sourceName = "null", destName = "null";

		if (getSource() != null)
			sourceName = getSource().getName();
		if (getSource() != null)
			destName = getTarget().getName();

		return "schedule(" + sourceName + "," + destName + ")";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ScheduleEdge() {
		this(null, null);
	}

	public ScheduleEdge(MapperDAGVertex source, MapperDAGVertex destination) {
		super(source, destination);

		getTimingEdgeProperty().setCost(0);
	}
}
