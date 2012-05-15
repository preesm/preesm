package org.ietr.preesm.experiment.memory.exclusiongraph;


import org.ietr.preesm.experiment.memory.bounds.IWeightedVertex;

import net.sf.dftools.algorithm.model.AbstractEdge;
import net.sf.dftools.algorithm.model.AbstractVertex;
import net.sf.dftools.algorithm.model.PropertyFactory;
import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;

/**
 * MemoryExclusionVertex is used to represent vertices in the Exclusion
 * graph.
 * 
 * @author kdesnos
 * 
 */
public class MemoryExclusionVertex extends
		AbstractVertex<MemoryExclusionGraph> implements
		IWeightedVertex<Integer>, Comparable<MemoryExclusionVertex> {
	/**
	 * unique identifier of vertex for user convenience
	 */
	private int identifier;

	/**
	 * ID of the task consuming the memory.
	 */
	private String sink;

	/**
	 * Size of the memory used
	 */
	private Integer size;

	/**
	 * ID of the task producing the memory.
	 */
	private String source;

	/**
	 * The edge in the DAG that corresponds to this vertex in the exclusion
	 * graph. 
	 * (This attribute is used only if the vertices corresponds to
	 * an edge in the dag, i.e. a transfer between actors)
	 */
	private DAGEdge edge;

	/**
	 * Constructor of the class
	 * 
	 * @param sourceTask
	 *            The ID of the task producing memory
	 * @param sourceTask
	 *            The ID of the task consuming memory
	 * @param sourceTask
	 *            The size of the memory
	 */
	public MemoryExclusionVertex(String sourceTask, String sinkTask,
			int sizeMem) {
		source = sourceTask;
		sink = sinkTask;
		size = sizeMem;
	}

	public MemoryExclusionVertex getClone() {
		MemoryExclusionVertex copy;
		copy = new MemoryExclusionVertex(this.source, this.sink, this.size);
		copy.setIdentifier(getIdentifier());
		copy.edge = this.edge;
		return copy;
	}

	/**
	 * Constructor of the class
	 * 
	 * @param inputEdge
	 *            the DAG edge corresponding to the constructed vertex
	 */
	public MemoryExclusionVertex(DAGEdge inputEdge) {
		source = inputEdge.getSource().getName();
		sink = inputEdge.getTarget().getName();
		try {
			size = inputEdge.getWeight().intValue();
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
		}
		this.edge = inputEdge;
	}

	/**
	 * The comparison of two MemoryExclusionVertex is made according to their
	 * weight
	 */
	public int compareTo(MemoryExclusionVertex o) {
		return this.size - o.size;
	}

	/**
	 * Test equality of two vertices.<br>
	 * Two vertices are considered equals if their source and sink are the same.
	 * The weight of the vertices is not taken into account.
	 * 
	 * @param o
	 *            the object to compare.
	 * @return true if the object is a similar vertex, false else.
	 */
	public boolean equals(Object o) {
		if (o instanceof MemoryExclusionVertex) {
			return (this.source.equals(((MemoryExclusionVertex) o).source) && this.sink
					.equals(((MemoryExclusionVertex) o).sink));
		} else {
			return false;
		}
	}

	/**
	 * @return the unique identifier of the vertex
	 */
	public int getIdentifier() {
		return identifier;
	}

	/**
	 * @return the sink
	 */
	public String getSink() {
		return sink;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @return the weight
	 */
	public Integer getWeight() {
		return size;
	}

	public void setWeight(Integer w) {
		size = w.intValue();
	}

	/**
	 * @return the edge of the DAG that correspond to this vertex in the
	 *         exclusion Graph
	 */
	public DAGEdge getEdge() {
		return edge;
	}

	/**
	 * Method added to enable the use of contains() method in
	 * Set<MemoryExclusionVertex>
	 */
	public int hashCode() {
		return sink.hashCode() | source.hashCode();
	}

	/**
	 * @param identifier
	 *            the identifier to set
	 */
	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	public String toString() {
		return source + "=>" + sink + ":" + size;
	}

	@Override
	public PropertyFactory getFactoryForProperty(String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractVertex<MemoryExclusionGraph> clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void connectionAdded(AbstractEdge<?, ?> e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionRemoved(AbstractEdge<?, ?> e) {
		// TODO Auto-generated method stub

	}
}
