/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2019)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2014)
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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.utils.DesignTools;

// TODO: Auto-generated Javadoc
/**
 * Properties of a mapped vertex. Can be shared by multiple vertices that have the same relative constraints
 *
 * @author mpelcat
 */
public class VertexMapping extends GroupProperty {

  /**
   * Instantiates a new vertex mapping.
   */
  public VertexMapping() {
    super();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.model.property.GroupProperty#clone()
   */
  @Override
  public VertexMapping copy() {

    final VertexMapping property = new VertexMapping();
    property.vertexIDs.addAll(this.vertexIDs);
    return property;
  }

  /**
   * Returns a list of components, computed from initial and relative constraints. If the boolean considerGroupMapping
   * is true, one mapped vertex in the group causes the return of its effective operator.
   *
   * @param vertex
   *          the vertex
   * @param considerGroupMapping
   *          the consider group mapping
   * @return the candidate components
   */
  public List<ComponentInstance> getCandidateComponents(final MapperDAGVertex vertex,
      final boolean considerGroupMapping) {

    final List<ComponentInstance> operators = new ArrayList<>();
    final MapperDAG dag = (MapperDAG) vertex.getBase();

    // Gets all vertices corresponding to the relative constraint group
    final List<MapperDAGVertex> relatedVertices = getVertices(dag);

    if (relatedVertices.isEmpty()) {
      PreesmLogger.getLogger().log(Level.SEVERE, "Relative constraint with no vertex.");
      return operators;
    }

    final MapperDAGVertex firstVertex = relatedVertices.get(0);

    // Adding to the list all candidate components of the first vertex
    operators.addAll(firstVertex.getInit().getInitialOperatorList());

    // computing intersection with other initial operator lists
    for (final MapperDAGVertex locVertex : relatedVertices) {
      DesignTools.retainAll(operators, locVertex.getInit().getInitialOperatorList());

    }

    // If we consider group mapping and a vertex in the group is mapped, we keep its operator only
    if (considerGroupMapping) {
      for (final MapperDAGVertex locVertex : relatedVertices) {
        final ComponentInstance op = locVertex.getEffectiveComponent();
        if (op != null) {
          final Set<ComponentInstance> effectiveOp = new LinkedHashSet<>();
          effectiveOp.add(op);
          operators.retainAll(effectiveOp);
        }
      }
    }

    if (operators.isEmpty()) {
      PreesmLogger.getLogger().log(Level.SEVERE, "Relative constraint with no operator." + relatedVertices);
    }

    return operators;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.model.property.GroupProperty#toString()
   */
  @Override
  public String toString() {
    return "<" + super.toString() + ">";
  }

}
