/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2012 - 2014)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.mapper.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.util.DesignTools;

// TODO: Auto-generated Javadoc
/**
 * Centralizing constraints of related vertices.
 *
 * @author mpelcat
 */
public class RelativeConstraint {

  /** The vertices. */
  private final List<MapperDAGVertex> vertices;

  /**
   * Instantiates a new relative constraint.
   */
  public RelativeConstraint() {
    super();
    this.vertices = new ArrayList<>();
  }

  /**
   * Gets the vertices.
   *
   * @return the vertices
   */
  public List<MapperDAGVertex> getVertices() {
    return this.vertices;
  }

  /**
   * Adds the vertex.
   *
   * @param vertex
   *          the vertex
   */
  public void addVertex(final MapperDAGVertex vertex) {
    this.vertices.add(vertex);
  }

  /**
   * Merge.
   *
   * @param constraints
   *          the constraints
   */
  public void merge(final RelativeConstraint constraints) {
    for (final MapperDAGVertex v : constraints.getVertices()) {
      if (!this.vertices.contains(v)) {
        addVertex(v);
      }
    }
  }

  /**
   * Gets the operators intersection.
   *
   * @return the operators intersection
   */
  public List<ComponentInstance> getOperatorsIntersection() {

    final List<ComponentInstance> operators = new ArrayList<>();

    if (this.vertices.isEmpty()) {
      WorkflowLogger.getLogger().log(Level.SEVERE, "Relative constraint with no vertex.");

      return operators;
    } else {
      final MapperDAGVertex firstVertex = this.vertices.get(0);
      final ComponentInstance op = firstVertex.getEffectiveOperator();
      if ((op != null) && (this.vertices.size() > 1)) {
        // Forcing the mapper to put together related vertices
        operators.add(op);
      } else {
        operators.addAll(firstVertex.getInit().getInitialOperatorList());
      }
    }

    for (int i = 1; i < this.vertices.size(); i++) {
      final MapperDAGVertex vertex = this.vertices.get(i);
      final ComponentInstance op = vertex.getEffectiveOperator();
      if (op != null) {
        if (DesignTools.contains(operators, op)) {
          operators.clear();
          operators.add(op);
        } else {
          operators.clear();
        }
      } else {
        DesignTools.retainAll(operators, vertex.getInit().getInitialOperatorList());
      }
    }

    return operators;
  }
}
