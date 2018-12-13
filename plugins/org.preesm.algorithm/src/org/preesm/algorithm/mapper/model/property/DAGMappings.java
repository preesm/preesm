/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2012 - 2014)
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
package org.preesm.algorithm.mapper.model.property;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.commons.CloneableProperty;

// TODO: Auto-generated Javadoc
/**
 * MapperDAG stores mapping properties shared by several of its vertices that have relative constraints. If the mapping
 * of a vertex in the group is modified, all mappings of the vertices in the group are modified.
 *
 * @author mpelcat
 */
public class DAGMappings implements CloneableProperty<DAGMappings> {

  /**
   * A mapping is associated to IDs of the vertices belonging to it (for fast access).
   */
  private Map<String, VertexMapping> mappings = null;

  /**
   * Instantiates a new DAG mappings.
   */
  public DAGMappings() {
    super();
    this.mappings = new LinkedHashMap<>();
  }

  /**
   * Gets the mapping.
   *
   * @param vertexId
   *          the vertex id
   * @return the mapping
   */
  public VertexMapping getMapping(final String vertexId) {
    return this.mappings.get(vertexId);
  }

  /**
   * Associates vertices by making them share a created VertexMapping object.
   *
   * @param vertices
   *          the vertices
   */
  public void associate(final Set<MapperDAGVertex> vertices) {
    final VertexMapping newMapping = new VertexMapping();
    for (final MapperDAGVertex v : vertices) {
      put(v.getName(), newMapping);
    }
  }

  /**
   * Dedicates a created VertexMapping object to a single vertex.
   *
   * @param vertex
   *          the vertex
   */
  public void dedicate(final MapperDAGVertex vertex) {
    final VertexMapping newMapping = new VertexMapping();
    put(vertex.getName(), newMapping);
  }

  /**
   * Associating a vertex to an existing mapping.
   *
   * @param vertexId
   *          the vertex id
   * @param m
   *          the m
   */
  private void put(final String vertexId, final VertexMapping m) {
    this.mappings.put(vertexId, m);
    m.addVertexID(vertexId);
  }

  /**
   * Associating a vertex to an existing mapping.
   *
   * @param vertex
   *          the vertex
   */
  public void remove(final MapperDAGVertex vertex) {
    this.mappings.get(vertex.getName()).removeVertexID(vertex.getName());
    this.mappings.remove(vertex.getName());
  }

  /**
   * Cloning the common mappings of vertices and ensuring that several vertices with same group share the same mapping
   * object.
   *
   * @return the object
   */
  @Override
  public DAGMappings copy() {
    final Map<VertexMapping, VertexMapping> relateOldAndNew = new LinkedHashMap<>();
    final DAGMappings newMappings = new DAGMappings();
    for (final String s : this.mappings.keySet()) {
      final VertexMapping m = this.mappings.get(s);
      if (relateOldAndNew.containsKey(m)) {
        newMappings.put(s, relateOldAndNew.get(m));
      } else {
        final VertexMapping newM = this.mappings.get(s).copy();
        relateOldAndNew.put(m, newM);
        newMappings.put(s, newM);
      }
    }
    return newMappings;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.mappings.toString();
  }

}
