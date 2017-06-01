/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
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
package org.ietr.preesm.mapper.model.property;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;

// TODO: Auto-generated Javadoc
/**
 * Property that corresponds to a group of vertices.
 *
 * @author mpelcat
 */
public abstract class GroupProperty implements Cloneable {

  /** IDs of the vertices that share the property. */
  private final Set<String> vertexIDs;

  /**
   * Instantiates a new group property.
   */
  public GroupProperty() {
    this.vertexIDs = new HashSet<>();
  }

  /**
   * Duplicating the group property.
   *
   * @return the object
   */
  @Override
  protected Object clone() {
    GroupProperty newProp = null;
    try {
      newProp = (GroupProperty) super.clone();
      newProp.vertexIDs.addAll(this.vertexIDs);
    } catch (final CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return newProp;
  }

  /**
   * Adding a new member to the group.
   *
   * @param id
   *          the id
   */
  public void addVertexID(final String id) {
    for (final String i : this.vertexIDs) {
      if (i.equals(id)) {
        return;
      }
    }
    this.vertexIDs.add(id);
  }

  /**
   * Removing a member from the group.
   *
   * @param id
   *          the id
   */
  public void removeVertexID(final String id) {
    final Iterator<String> it = this.vertexIDs.iterator();
    while (it.hasNext()) {
      final String i = it.next();
      if (i.equals(id)) {
        it.remove();
      }
    }
  }

  /**
   * Returns the number of actors sharing the same property.
   *
   * @return the number of vertices
   */
  public int getNumberOfVertices() {
    return this.vertexIDs.size();
  }

  /**
   * Gets the vertices corresponding to the group.
   *
   * @param dag
   *          the dag
   * @return the vertices
   */
  public List<MapperDAGVertex> getVertices(final MapperDAG dag) {
    final List<MapperDAGVertex> vertices = new ArrayList<>();
    for (final String id : this.vertexIDs) {
      vertices.add((MapperDAGVertex) dag.getVertex(id));
    }

    return vertices;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.vertexIDs.toString();
  }
}
