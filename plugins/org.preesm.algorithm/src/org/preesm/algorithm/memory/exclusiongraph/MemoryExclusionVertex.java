/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Julien Hascoet <jhascoet@kalray.eu> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2015)
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
package org.preesm.algorithm.memory.exclusiongraph;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.xtext.util.Pair;
import org.preesm.algorithm.memory.script.Range;
import org.preesm.algorithm.model.AbstractEdge;
import org.preesm.algorithm.model.AbstractVertex;
import org.preesm.algorithm.model.PropertyBean;
import org.preesm.algorithm.model.PropertyFactory;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.scenario.types.BufferAggregate;
import org.preesm.scenario.types.BufferProperties;
import org.preesm.scenario.types.DataType;

// TODO: Auto-generated Javadoc
/**
 * MemoryExclusionVertex is used to represent vertices in the Exclusion graph.
 *
 * @author kdesnos
 *
 */
public class MemoryExclusionVertex extends AbstractVertex<MemoryExclusionGraph>
    implements IWeightedVertex<Long>, Comparable<MemoryExclusionVertex> {

  /**
   * String used in the {@link PropertyBean} of a {@link MemoryExclusionVertex} to store the offset at which the memory
   * object is stored in memory.
   */
  public static final String MEMORY_OFFSET_PROPERTY = "memory_offset";

  /**
   * Property of the {@link MemoryExclusionVertex}. The object associated to this property is:<br>
   * <code>
   * List&lt;Pair&lt;MemoryExclusionVertex,Pair&lt;Range,Range&gt;&gt;</code> <br>
   * This {@link List} stores {@link Pair} of {@link MemoryExclusionVertex} and {@link Pair}. Each {@link Pair}
   * corresponds to a {@link Range} of real tokens of the memory object and their position in the actual
   * {@link MemoryExclusionVertex} (i.e. the key of the first {@link Pair}). <br>
   * For the host memory object, this property gives the position of the range of bytes of the host within the memory
   * allocated for it.<br>
   * For hosted memory object, this property gives the position of the range(s) of bytes of the hosted memory object
   * relatively to the position of the 0 index of the host memory object within the memory allocated for it.
   */
  public static final String REAL_TOKEN_RANGE_PROPERTY = "real_token_range";

  /**
   * Property of the {@link MemoryExclusionVertex}. The object associated to this property is:<br>
   * <code>
   * List&lt;MemoryExclusionVertex&gt;</code><br>
   * This list contains the fake {@link MemoryExclusionVertex} that are added to the {@link MemoryExclusionGraph} during
   * memory allocation when the current {@link MemoryExclusionVertex} is divided because of scripts. These fake
   * {@link MemoryExclusionVertex} should be removed from the {@link MemoryExclusionGraph} if it is
   * {@link MemoryExclusionGraph#deallocate() deallocated}.
   */
  public static final String FAKE_MOBJECT = "fake_mobject";

  /**
   * Property of the {@link MemoryExclusionVertex}. The object associated to this property is:<br>
   * <code>
   * List&lt;MemoryExclusionVertex&gt;</code><br>
   * This {@link List} stores {@link MemoryExclusionVertex} corresponding to the
   * {@link MemoryExclusionGraph#getAdjacentVertexOf(MemoryExclusionVertex) adjacent vertices} of the current
   * {@link MemoryExclusionVertex} before it was merged as a result of memory scripts execution.
   */
  public static final String ADJACENT_VERTICES_BACKUP = "adjacent_vertices_backup";

  /**
   * Property of the {@link MemoryExclusionVertex}. The object associated to this property is an {@link Integer} that
   * corresponds to the space in bytes between the offset at which the {@link MemoryExclusionVertex} is allocated and
   * the actual beginning of the real token ranges. This property is set after the memory script execution.
   */
  public static final String EMPTY_SPACE_BEFORE = "empty_space_before";

  /**
   * Property of the {@link MemoryExclusionVertex}. The object associated to this property is an {@link Integer} that
   * corresponds to the size in bytes of the {@link MemoryExclusionVertex} when it hosts merged
   * {@link MemoryExclusionVertex} as a result of scripts execution. This value is stored in case the host
   * {@link MemoryExclusionVertex} needs to be deallocated, and restored to the size it has when all hosted
   * {@link MemoryExclusionVertex} are merged.
   */
  public static final String HOST_SIZE = "host_size";

  /**
   * Property associated to {@link MemoryExclusionVertex} that are divided as a result of the application of memory
   * scripts. The object associated to this property is a {@link List} of {@link MemoryExclusionVertex} that corresponds
   * to the {@link MemoryExclusionVertex} in which the parts of the divided {@link MemoryExclusionVertex} will be
   * merged.
   */
  public static final String DIVIDED_PARTS_HOSTS = "divided_parts_hosts";

  /** This Map is used as a reference of dataTypes size when creating an vertex from a DAGEdge. */
  public static Map<String, DataType> _dataTypes = new LinkedHashMap<>();

  /**
   * This method is used to associate a map of data types to the MemoryExclusionVertex class. This map will be used when
   * creating a MemEx Vertex from a DAGEdge to give their real weight to the MemEx graph vertices.
   *
   * @param dataTypes
   *          the map of DataType
   */
  public static void setDataTypes(final Map<String, DataType> dataTypes) {
    if (dataTypes != null) {
      MemoryExclusionVertex._dataTypes = dataTypes;
    }
  }

  /** unique identifier of vertex for user convenience. */
  private long identifier;

  /**
   * ID of the task consuming the memory.
   */
  private final String sink;

  /** Size of the memory used. */
  private long size;

  /**
   * ID of the task producing the memory.
   */
  private final String source;

  /** ID of the explode/Implode dag vertex the memory belongs to. */
  private String explodeImplode;

  /**
   * The edge in the DAG that corresponds to this vertex in the exclusion graph. (This attribute is used only if the
   * vertices corresponds to an edge in the dag, i.e. a transfer between actors)
   */
  private DAGEdge edge;

  /**
   * The {@link DAGVertex} that corresponds to the actor in the DAG associated to this working memory
   * {@link MemoryExclusionVertex}.
   */
  private DAGVertex vertex;

  /**
   * {@link MemoryExclusionVertex} property associated to a {@link List} of {@link Integer} that represent the space
   * <b>in bytes</b> between successive "subbuffers" of a {@link MemoryExclusionVertex}.
   */
  public static final String INTER_BUFFER_SPACES = "inter_buffer_spaces";

  /**
   * Property used with fifo {@link MemoryExclusionVertex memory objects} to relate the size of one token in the fifo.
   */
  public static final String TYPE_SIZE = "type_size";

  /**
   * Constructor of the class.
   *
   * @param inputEdge
   *          the DAG edge corresponding to the constructed vertex
   */
  public MemoryExclusionVertex(final DAGEdge inputEdge) {
    this.source = inputEdge.getSource().getName();
    this.sink = inputEdge.getTarget().getName();

    if (inputEdge.getPropertyBean().getValue("explodeName") != null) {
      this.explodeImplode = inputEdge.getPropertyBean().getValue("explodeName").toString();
    } else {
      this.explodeImplode = "";
    }

    // try {
    // size = inputEdge.getWeight().intValue();
    // } catch (InvalidExpressionException e) {
    // e.printStackTrace();
    // }
    // if datatype is defined, correct the vertex weight
    final BufferAggregate buffers = (BufferAggregate) inputEdge.getPropertyBean()
        .getValue(BufferAggregate.propertyBeanName);
    final Iterator<BufferProperties> iter = buffers.iterator();
    int vertexWeight = 0;
    while (iter.hasNext()) {
      final BufferProperties properties = iter.next();

      final String dataType = properties.getDataType();
      final DataType type = MemoryExclusionVertex._dataTypes.get(dataType);

      if (type != null) {
        vertexWeight += type.getSize() * properties.getSize();
      } else {
        vertexWeight += properties.getSize();
      }
    }

    this.size = vertexWeight;

    if (vertexWeight == 0) {
      PreesmLogger.getLogger().log(Level.WARNING, "Probable ERROR: Vertex weight is 0");
    }

    this.edge = inputEdge;
  }

  /**
   * Constructor of the class.
   *
   * @param sourceTask
   *          The size of the memory
   * @param sinkTask
   *          the sink task
   * @param sizeMem
   *          the size mem
   */
  public MemoryExclusionVertex(final String sourceTask, final String sinkTask, final long sizeMem) {
    this.source = sourceTask;
    this.sink = sinkTask;
    this.size = sizeMem;
    this.explodeImplode = "";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractVertex#clone()
   */
  @Override
  public MemoryExclusionVertex copy() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * The comparison of two MemoryExclusionVertex is made according to their weight.
   *
   * @param o
   *          the o
   * @return the int
   */
  @Override
  public int compareTo(final MemoryExclusionVertex o) {
    return (int) (this.size - o.size);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractVertex#connectionAdded(org.ietr.dftools.algorithm.model.AbstractEdge)
   */
  @Override
  public void connectionAdded(final AbstractEdge<?, ?> e) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.ietr.dftools.algorithm.model.AbstractVertex#connectionRemoved(org.ietr.dftools.algorithm.model.AbstractEdge)
   */
  @Override
  public void connectionRemoved(final AbstractEdge<?, ?> e) {
    // TODO Auto-generated method stub

  }

  /**
   * Test equality of two {@link MemoryExclusionVertex vertices}.<br>
   * Two {@link MemoryExclusionVertex vertices} are considered equals if their {@link #getSource() source} and
   * {@link #getSink() sink} are equals. Neither the weight nor the explodeImplode attributes of the vertices are taken
   * into account to test the equality.
   *
   * <p>
   * Do not change the way the comparison is done since several other classes relate on it, like ScriptRunner#updateMEG
   * method.
   * </p>
   *
   * @param o
   *          the object to compare.
   * @return true if the object is a similar vertex, false else.
   */
  @Override
  public boolean equals(final Object o) {
    if (o instanceof MemoryExclusionVertex) {
      // final boolean sameEdge = this.edge == ((MemoryExclusionVertex) o).edge
      final boolean sameSource = this.source.equals(((MemoryExclusionVertex) o).source);
      final boolean sameSink = this.sink.equals(((MemoryExclusionVertex) o).sink);
      return sameSink && sameSource;// && sameEdge
    } else {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.memory.exclusiongraph.IWeightedVertex#getClone()
   */
  @Override
  public MemoryExclusionVertex getClone() {
    MemoryExclusionVertex copy;
    copy = new MemoryExclusionVertex(this.source, this.sink, this.size);
    copy.setIdentifier(getIdentifier());
    copy.edge = this.edge;
    copy.explodeImplode = this.explodeImplode;
    copy.vertex = this.vertex;
    return copy;
  }

  /**
   * Gets the edge.
   *
   * @return the edge of the DAG that correspond to this vertex in the exclusion Graph
   */
  public DAGEdge getEdge() {
    return this.edge;
  }

  /**
   * Gets the vertex.
   *
   * @return the vertex of the DAG in the exclusion Graph
   */
  public DAGVertex getVertex() {
    return this.vertex;
  }

  /**
   * Sets the vertex.
   */
  public void setVertex(final DAGVertex vertex) {
    this.vertex = vertex;
  }

  /**
   * Gets the explode implode.
   *
   * @return the explodeImplode
   */
  public String getExplodeImplode() {
    return this.explodeImplode;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.PropertySource#getFactoryForProperty(java.lang.String)
   */
  @Override
  public PropertyFactory getFactoryForProperty(final String propertyName) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Gets the identifier.
   *
   * @return the unique identifier of the vertex
   */
  @Override
  public long getIdentifier() {
    return this.identifier;
  }

  /**
   * Gets the sink.
   *
   * @return the sink
   */
  public String getSink() {
    return this.sink;
  }

  /**
   * Gets the source.
   *
   * @return the source
   */
  public String getSource() {
    return this.source;
  }

  /**
   * Gets the weight.
   *
   * @return the weight
   */
  @Override
  public Long getWeight() {
    return this.size;
  }

  /**
   * <p>
   * Method added to enable the use of contains() method in Set &lt;MemoryExclusionVertex&gt;.
   * </p>
   *
   * @return the int
   */
  @Override
  public int hashCode() {
    return (new String(this.sink + "=>" + this.source)).hashCode();
  }

  /**
   * Sets the identifier.
   *
   * @param identifier
   *          the identifier to set
   */
  @Override
  public void setIdentifier(final long identifier) {
    this.identifier = identifier;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.memory.exclusiongraph.IWeightedVertex#setWeight(java.lang.Object)
   */
  @Override
  public void setWeight(final Long w) {
    this.size = w;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.source + "=>" + this.sink + ":" + this.size;
  }
}
