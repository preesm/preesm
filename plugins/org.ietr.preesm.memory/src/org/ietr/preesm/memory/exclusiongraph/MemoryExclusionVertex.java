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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.dftools.algorithm.model.AbstractEdge;
import net.sf.dftools.algorithm.model.AbstractVertex;
import net.sf.dftools.algorithm.model.PropertyBean;
import net.sf.dftools.algorithm.model.PropertyFactory;
import net.sf.dftools.algorithm.model.dag.DAGEdge;

import org.ietr.preesm.core.types.BufferAggregate;
import org.ietr.preesm.core.types.BufferProperties;
import org.ietr.preesm.core.types.DataType;

/**
 * MemoryExclusionVertex is used to represent vertices in the Exclusion graph.
 * 
 * @author kdesnos
 * 
 */
public class MemoryExclusionVertex extends AbstractVertex<MemoryExclusionGraph>
		implements IWeightedVertex<Integer>, Comparable<MemoryExclusionVertex> {

	/**
	 * String used in the {@link PropertyBean} of a
	 * {@link MemoryExclusionVertex} to store the offset at which the memory
	 * object is stored in memory.
	 */
	public static final String MEMORY_OFFSET_PROPERTY = "memory_offset";

	/**
	 * This Map is used as a reference of dataTypes size when creating an vertex
	 * from a DAGEdge
	 */
	static public Map<String, DataType> _dataTypes = new HashMap<String, DataType>();

	/**
	 * This method is used to associate a map of data types to the
	 * MemoryExclusionVertex class. This map will be used when creating a MemEx
	 * Vertex from a DAGEdge to give their real weight to the MemEx graph
	 * vertices.
	 * 
	 * @param dataTypes
	 *            the map of DataType
	 */
	static public void setDataTypes(Map<String, DataType> dataTypes) {
		if (dataTypes != null) {
			_dataTypes = dataTypes;
		}
	}

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
	 * ID of the explode/Implode dag vertex the memory belongs to
	 */
	private String explodeImplode;

	/**
	 * The edge in the DAG that corresponds to this vertex in the exclusion
	 * graph. (This attribute is used only if the vertices corresponds to an
	 * edge in the dag, i.e. a transfer between actors)
	 */
	private DAGEdge edge;

	/**
	 * Constructor of the class
	 * 
	 * @param inputEdge
	 *            the DAG edge corresponding to the constructed vertex
	 */
	public MemoryExclusionVertex(DAGEdge inputEdge) {
		source = inputEdge.getSource().getName();
		sink = inputEdge.getTarget().getName();

		if (inputEdge.getPropertyBean().getValue("explodeName") != null) {
			explodeImplode = inputEdge.getPropertyBean()
					.getValue("explodeName").toString();
		} else {
			explodeImplode = "";
		}

		// try {
		// size = inputEdge.getWeight().intValue();
		// } catch (InvalidExpressionException e) {
		// e.printStackTrace();
		// }
		// if datatype is defined, correct the vertex weight
		BufferAggregate buffers = (BufferAggregate) inputEdge.getPropertyBean()
				.getValue(BufferAggregate.propertyBeanName);
		Iterator<BufferProperties> iter = buffers.iterator();
		int vertexWeight = 0;
		while (iter.hasNext()) {
			BufferProperties properties = iter.next();

			String dataType = properties.getDataType();
			DataType type = _dataTypes.get(dataType);

			if (type != null) {
				vertexWeight += type.getSize() * properties.getSize();
			} else {
				vertexWeight += properties.getSize();
			}
		}

		size = vertexWeight;

		if (vertexWeight == 0) {
			System.out.println("Probable ERROR: Vertex weight is 0");
		}

		this.edge = inputEdge;
	}

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
	public MemoryExclusionVertex(String sourceTask, String sinkTask, int sizeMem) {
		source = sourceTask;
		sink = sinkTask;
		size = sizeMem;
		explodeImplode = "";
	}

	@Override
	public AbstractVertex<MemoryExclusionGraph> clone() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * The comparison of two MemoryExclusionVertex is made according to their
	 * weight
	 */
	public int compareTo(MemoryExclusionVertex o) {
		return this.size - o.size;
	}

	@Override
	public void connectionAdded(AbstractEdge<?, ?> e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionRemoved(AbstractEdge<?, ?> e) {
		// TODO Auto-generated method stub

	}

	/**
	 * Test equality of two {@link MemoryExclusionVertex vertices}.<br>
	 * Two {@link MemoryExclusionVertex vertices} are considered equals if their
	 * {@link #getSource() source} and {@link #getSink() sink} are equals.
	 * Neither the weight nor the explodeImplode attributes of the vertices are
	 * taken into account to test the equality.
	 * 
	 * @param o
	 *            the object to compare.
	 * @return true if the object is a similar vertex, false else.
	 */
	public boolean equals(Object o) {
		if (o instanceof MemoryExclusionVertex) {
			return (this.source.equals(((MemoryExclusionVertex) o).source) && this.sink
					.equals(((MemoryExclusionVertex) o).sink)); // &&
																// this.explodeImplode
			// .equals(((MemoryExclusionVertex) o).explodeImplode));
		} else {
			return false;
		}
	}

	public MemoryExclusionVertex getClone() {
		MemoryExclusionVertex copy;
		copy = new MemoryExclusionVertex(this.source, this.sink, this.size);
		copy.setIdentifier(getIdentifier());
		copy.edge = this.edge;
		return copy;
	}

	/**
	 * @return the edge of the DAG that correspond to this vertex in the
	 *         exclusion Graph
	 */
	public DAGEdge getEdge() {
		return edge;
	}

	/**
	 * @return the explodeImplode
	 */
	public String getExplodeImplode() {
		return explodeImplode;
	}

	@Override
	public PropertyFactory getFactoryForProperty(String propertyName) {
		// TODO Auto-generated method stub
		return null;
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

	/**
	 * Method added to enable the use of contains() method in
	 * Set<MemoryExclusionVertex>
	 */
	public int hashCode() {
		return (new String(sink + "=>" + source)).hashCode();
	}

	/**
	 * @param identifier
	 *            the identifier to set
	 */
	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	public void setWeight(Integer w) {
		size = w.intValue();
	}

	public String toString() {
		return source + "=>" + sink + ":" + size;
	}
}
