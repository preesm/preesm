/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
package org.ietr.dftools.algorithm.model.dag;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import org.ietr.dftools.algorithm.model.AbstractEdge;
import org.ietr.dftools.algorithm.model.AbstractVertex;
import org.ietr.dftools.algorithm.model.AbstractVertexPropertyType;
import org.ietr.dftools.algorithm.model.PropertyBean;
import org.ietr.dftools.algorithm.model.PropertyFactory;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;

/**
 * Class used to represent a Vertex in a DIrected Acyclic Graph.
 *
 * @author jpiat
 */
public class DAGVertex extends AbstractVertex<DirectedAcyclicGraph> {

  /** Key to access to property time. */
  public static final String TIME = "time";

  /** Key to access to property nb repeat. */
  public static final String NB_REPEAT = "nb_repeat";

  /** The string representing the kind of this vertex. */
  public static final String DAG_VERTEX = "dag_vertex";

  /** Key to access to property sdf_vertex. */
  public static final String SDF_VERTEX = "sdf_vertex";

  /** Key to access to property vertex_sinks. */
  public static final String SINKS = "vertex_sinks";

  /** Key to access to property vertex_sources. */
  public static final String SOURCES = "vertex_sources";

  static {
    AbstractVertex.public_properties.add(DAGVertex.TIME);
    AbstractVertex.public_properties.add(DAGVertex.NB_REPEAT);
  }

  /**
   * Creates a new DAGVertex.
   */
  public DAGVertex() {
    super();
    setKind(DAGVertex.DAG_VERTEX);
    this.properties = new PropertyBean();
    setId(UUID.randomUUID().toString());
  }

  /**
   * Creates a new DAGVertex with the name "n", the execution time "t" and the number of repetition "nb".
   *
   * @param n
   *          This Vertex name
   * @param t
   *          This Vertex execution time
   * @param nb
   *          This Vertex number of repetition
   */
  public DAGVertex(final String n, final AbstractVertexPropertyType<?> t, final AbstractVertexPropertyType<?> nb) {
    super();
    setKind(DAGVertex.DAG_VERTEX);
    this.properties = new PropertyBean();
    setId(UUID.randomUUID().toString());
    setNbRepeat(nb);
    setTime(t);
    setName(n);
  }

  /**
   * Gives the vertex corresponding to this dag vertex.
   *
   * @return The SDFVertex corresponding to this DAG vertex from the SDF2Dag translation
   */
  public SDFAbstractVertex getCorrespondingSDFVertex() {
    final Object vertex = getPropertyBean().getValue(DAGVertex.SDF_VERTEX);
    if (vertex != null) {
      return (SDFAbstractVertex) vertex;
    }
    return null;
  }

  /**
   * Gives this vertex number of repetition.
   *
   * @return This vertex number of repetition
   */
  public AbstractVertexPropertyType<?> getNbRepeat() {
    if (this.properties.getValue(DAGVertex.NB_REPEAT) != null) {
      return this.properties.getValue(DAGVertex.NB_REPEAT);
    }
    return null;
  }

  /**
   * Gives this Vertex Execution time.
   *
   * @return This vertex execution time
   */
  public AbstractVertexPropertyType<?> getTime() {
    if (this.properties.getValue(DAGVertex.TIME) != null) {
      return this.properties.getValue(DAGVertex.TIME);
    }
    return null;
  }

  /**
   * Gives this vertex incoming Edges.
   *
   * @return The Set of incoming edges
   */
  public Set<DAGEdge> incomingEdges() {
    if (this.properties.getValue(AbstractVertex.BASE_LITERAL) instanceof DirectedAcyclicGraph) {
      final DirectedAcyclicGraph base = this.properties.getValue(AbstractVertex.BASE_LITERAL);
      return base.incomingEdgesOf(this);
    }
    return new TreeSet<>();
  }

  /**
   * Gives this vertex outgoing Edges.
   *
   * @return The Set of outgoing edges
   */
  public Set<DAGEdge> outgoingEdges() {
    if (this.properties.getValue(AbstractVertex.BASE_LITERAL) instanceof DirectedAcyclicGraph) {
      final DirectedAcyclicGraph base = this.properties.getValue(AbstractVertex.BASE_LITERAL);
      return base.outgoingEdgesOf(this);
    }
    return new TreeSet<>();
  }

  /**
   * Set the sdf vertex corresponding to this dag vertex.
   *
   * @param vertex
   *          the new corresponding SDF vertex
   */
  public void setCorrespondingSDFVertex(final SDFAbstractVertex vertex) {
    getPropertyBean().setValue(DAGVertex.SDF_VERTEX, vertex);
  }

  /**
   * Set this vertex number of repetition.
   *
   * @param nb
   *          The repetition number of this vertex
   */
  public void setNbRepeat(final AbstractVertexPropertyType<?> nb) {
    this.properties.setValue(DAGVertex.NB_REPEAT, nb);
  }

  /**
   * Set this vertex execution time.
   *
   * @param t
   *          The execution of this vertex
   */
  public void setTime(final AbstractVertexPropertyType<?> t) {
    this.properties.setValue(DAGVertex.TIME, t);
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getName() + " x" + getNbRepeat();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractVertex#clone()
   */
  @Override
  public DAGVertex copy() {
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.PropertySource#getFactoryForProperty(java.lang.String)
   */
  @Override
  public PropertyFactory getFactoryForProperty(final String propertyName) {
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractVertex#connectionAdded(org.ietr.dftools.algorithm.model.AbstractEdge)
   */
  @Override
  public void connectionAdded(final AbstractEdge<?, ?> e) {
    // nothing specific to do
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.ietr.dftools.algorithm.model.AbstractVertex#connectionRemoved(org.ietr.dftools.algorithm.model.AbstractEdge)
   */
  @Override
  public void connectionRemoved(final AbstractEdge<?, ?> e) {
    // nothing specific to do
  }

  /**
   * Adds the name of a sink to the vertex. <br>
   * This is used by the codegen
   *
   * @param sinkName
   *          the sink name to add
   */
  public void addSinkName(final String sinkName) {
    final List<String> sinkList = getPropertyBean().getValue(DAGVertex.SINKS);
    if (sinkList == null) {
      getPropertyBean().setValue(DAGVertex.SINKS, new ArrayList<String>());
      addSinkName(sinkName);
    } else {
      sinkList.add(sinkName);
    }
  }

  /**
   * Get the sink name list
   *
   * @return list of sink associated with the vertex
   */
  public List<String> getSinkNameList() {
    return getPropertyBean().getValue(DAGVertex.SINKS);
  }

  /**
   * Adds the name of a source to the vertex. <br>
   * This is used by the codegen
   *
   * @param sourceName
   *          the source name to add
   */
  public void addSourceName(final String sourceName) {
    final List<String> sourceList = getPropertyBean().getValue(DAGVertex.SOURCES);
    if (sourceList == null) {
      getPropertyBean().setValue(DAGVertex.SOURCES, new ArrayList<String>());
      addSourceName(sourceName);
    } else {
      sourceList.add(sourceName);
    }
  }

  /**
   * Get the source name list
   *
   * @return list of sources associated with the vertex
   */
  public List<String> getSourceNameList() {
    return getPropertyBean().getValue(DAGVertex.SOURCES);
  }

}
