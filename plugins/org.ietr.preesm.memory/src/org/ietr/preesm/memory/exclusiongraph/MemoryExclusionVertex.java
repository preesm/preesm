/*********************************************************
Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
Karol Desnos

[mpelcat,jnezan,kdesnos]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.memory.exclusiongraph;

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
