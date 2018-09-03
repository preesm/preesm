/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
 * Pengcheng Mu <pengcheng.mu@insa-rennes.fr> (2008)
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
package org.ietr.preesm.core.scenario;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * A ConstraintGroup associates a set of graph definitions and a set of processing units on which they can be matched.
 *
 * @author mpelcat
 */
public class ConstraintGroup {

  /** The set of processing units available for the constraint group. */
  private final Set<String> operatorIds;

  /** The set of graphs belonging to the constraint group. */
  private final Set<String> actorsPaths;

  /**
   * Instantiates a new constraint group.
   */
  public ConstraintGroup() {
    this.operatorIds = new LinkedHashSet<>();
    this.actorsPaths = new LinkedHashSet<>();

  }

  /**
   * Adds the operator id.
   *
   * @param opId
   *          the op id
   */
  public void addOperatorId(final String opId) {
    if (!hasOperatorId(opId)) {
      this.operatorIds.add(opId);
    }

  }

  /**
   * When a vertex is added to the constraints, its hierarchical path is added in its properties in order to separate
   * distinct vertices with same name.
   *
   * @param vertexId
   *          the vertex id
   */
  public void addActorPath(final String vertexId) {
    if (!hasVertexPath(vertexId)) {
      this.actorsPaths.add(vertexId);

    }
  }

  /**
   * Adds the vertex paths.
   *
   * @param vertexIdSet
   *          the vertex id set
   */
  public void addVertexPaths(final Set<String> vertexIdSet) {
    for (final String vertexId : vertexIdSet) {
      addActorPath(vertexId);
    }
  }

  /**
   * Removes the vertex paths.
   *
   * @param vertexIdSet
   *          the vertex id set
   */
  public void removeVertexPaths(final Set<String> vertexIdSet) {
    for (final String vertexId : vertexIdSet) {
      removeVertexPath(vertexId);
    }
  }

  /**
   * Gets the operator ids.
   *
   * @return the operator ids
   */
  public Set<String> getOperatorIds() {
    return new LinkedHashSet<>(this.operatorIds);
  }

  /**
   * Gets the vertex paths.
   *
   * @return the vertex paths
   */
  public Set<String> getVertexPaths() {
    return new LinkedHashSet<>(this.actorsPaths);
  }

  /**
   * Checks for operator id.
   *
   * @param operatorId
   *          the operator id
   * @return true, if successful
   */
  public boolean hasOperatorId(final String operatorId) {

    for (final String opId : this.operatorIds) {
      if (opId.equals(operatorId)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Checks for vertex path.
   *
   * @param vertexInfo
   *          the vertex info
   * @return true, if successful
   */
  public boolean hasVertexPath(final String vertexInfo) {

    for (final String vId : this.actorsPaths) {
      if (vId.equals(vertexInfo)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Removes the operator id.
   *
   * @param operatorId
   *          the operator id
   */
  public void removeOperatorId(final String operatorId) {
    final Iterator<String> it = this.operatorIds.iterator();
    while (it.hasNext()) {
      final String currentopId = it.next();
      if (currentopId.equals(operatorId)) {
        it.remove();
      }
    }
  }

  /**
   * Removes the vertex path.
   *
   * @param sdfVertexInfo
   *          the sdf vertex info
   */
  public void removeVertexPath(final String sdfVertexInfo) {
    final Iterator<String> it = this.actorsPaths.iterator();
    while (it.hasNext()) {
      final String v = it.next();
      if ((v.equals(sdfVertexInfo))) {
        it.remove();
      }
    }

  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String s = "<";
    s += this.operatorIds.toString();
    s += this.actorsPaths.toString();
    s += ">";

    return s;
  }
}
