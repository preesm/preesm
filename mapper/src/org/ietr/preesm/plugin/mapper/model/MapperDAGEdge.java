package org.ietr.preesm.plugin.mapper.model;

import org.sdf4j.model.dag.DAGEdge;

/**
 * Represents an edge in a DAG of type {@link MapperDAG} used in the mapper
 * 
 * @author mpelcat
 */
public class MapperDAGEdge extends DAGEdge {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8013444915048927047L;

	protected InitialEdgeProperty initialEdgeProperty;

	protected TimingEdgeProperty timingEdgeProperty;
	
	/**
	 */
	public MapperDAGEdge(MapperDAGVertex source, MapperDAGVertex destination) {
		initialEdgeProperty = new InitialEdgeProperty();
		timingEdgeProperty = new TimingEdgeProperty();
	}

	public InitialEdgeProperty getInitialEdgeProperty() {
		return initialEdgeProperty;
	}

	public void setInitialEdgeProperty(InitialEdgeProperty initialEdgeProperty) {
		this.initialEdgeProperty = initialEdgeProperty;
	}

	public TimingEdgeProperty getTimingEdgeProperty() {
		return timingEdgeProperty;
	}

	public void setTimingEdgeProperty(TimingEdgeProperty timingEdgeProperty) {
		this.timingEdgeProperty = timingEdgeProperty;
	}
}
