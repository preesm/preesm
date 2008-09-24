/**
 * 
 */
package org.ietr.preesm.plugin.mapper.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;

/**
 * Represents a vertex in a DAG of type {@link MapperDAG} used in the mapper
 * 
 * @author mpelcat
 */
public class MapperDAGVertex extends DAGVertex {

	/**
	 * Properties set by mapper algorithm via architecture model
	 */
	protected ImplementationVertexProperty implantationVertexProperty;

	/**
	 * Properties set when converting sdf to dag
	 */
	protected InitialVertexProperty initialVertexProperty;

	/**
	 * Properties set by timekeeper
	 */
	protected TimingVertexProperty timingVertexProperty;

	public MapperDAGVertex() {

		this("default", "default", null);
	}
	
	public MapperDAGVertex(String id, MapperDAG base) {

		this(id, id, base);
	}
	
	public MapperDAGVertex(String id, String name, MapperDAG base) {

		super();
		
		this.setName(name);
		this.initialVertexProperty = new InitialVertexProperty();
		this.initialVertexProperty.setParentVertex(this);
		this.implantationVertexProperty = new ImplementationVertexProperty();
		this.timingVertexProperty = new TimingVertexProperty();

		this.setBase(base);
	}
	
	@Override
	public MapperDAGVertex clone() {

		MapperDAGVertex result = new MapperDAGVertex(this.getId(), this
				.getName(), (MapperDAG)this.getBase());
		result.setImplantationVertexProperty(this
				.getImplementationVertexProperty().clone());
		result.setInitialVertexProperty(this.getInitialVertexProperty().clone(result));
		result.setTimingVertexProperty(this.getTimingVertexProperty().clone());
		
		for(String propertyKey : this.getPropertyBean().keys()){
			Object property = this.getPropertyBean().getValue(propertyKey);
			result.getPropertyBean().setValue(propertyKey, property);
		}
		
		return result;
	}
	
	public ImplementationVertexProperty getImplementationVertexProperty() {
		return implantationVertexProperty;
	}

	public InitialVertexProperty getInitialVertexProperty() {
		return initialVertexProperty;
	}

	public TimingVertexProperty getTimingVertexProperty() {
		return timingVertexProperty;
	}

	public void setImplantationVertexProperty(
			ImplementationVertexProperty implantationVertexProperty) {
		this.implantationVertexProperty = implantationVertexProperty;
	}

	public void setInitialVertexProperty(
			InitialVertexProperty initialVertexProperty) {
		this.initialVertexProperty = initialVertexProperty;
	}

	public void setTimingVertexProperty(
			TimingVertexProperty timingVertexProperty) {
		this.timingVertexProperty = timingVertexProperty;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof MapperDAGVertex) {
			MapperDAGVertex v = (MapperDAGVertex) obj;
			return (this.getName().compareToIgnoreCase(v.getName()) == 0);
		}

		return false;
	}

	public String toString() {

		if (implantationVertexProperty.hasEffectiveComponent()) {
			// If the vertex is implanted, displays its component and rank
			return getName()
					+ "("
					+ implantationVertexProperty.getEffectiveComponent()
							.toString() + ","
					+ implantationVertexProperty.getSchedulingTotalOrder()
					+ ")";
		} else {
			// If the vertex is not implanted, displays its weight
			return getName() + "(" + this.getNbRepeat() + ")";
		}
	}

	public Set<MapperDAGVertex> getPredecessorSet() {

		Set<MapperDAGVertex> temp = new HashSet<MapperDAGVertex>();
		Set<DAGEdge> incomingedgeset = this.incomingEdges();
		Iterator<DAGEdge> iter = incomingedgeset.iterator();

		while (iter.hasNext()) {

			MapperDAGEdge edge = (MapperDAGEdge)iter.next();
			temp.add((MapperDAGVertex)edge.getSource());

		}
		return temp;
	}

	public Set<MapperDAGVertex> getSuccessorSet() {

		Set<MapperDAGVertex> temp = new HashSet<MapperDAGVertex>();
		Set<DAGEdge> outgoingSet = this.outgoingEdges();
		Iterator<DAGEdge> iter = outgoingSet.iterator();

		while (iter.hasNext()) {

			MapperDAGEdge edge = (MapperDAGEdge)iter.next();
			temp.add((MapperDAGVertex)edge.getTarget());
		}
		return temp;
	}
}
