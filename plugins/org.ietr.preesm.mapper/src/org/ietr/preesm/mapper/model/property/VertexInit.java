/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2013)
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
package org.ietr.preesm.mapper.model.property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.mapper.abc.SpecialVertexManager;
import org.ietr.preesm.mapper.model.MapperDAGVertex;

// TODO: Auto-generated Javadoc
/**
 * Properties of a mapped vertex set when converting dag to mapper dag.
 *
 * @author mpelcat
 */
public class VertexInit {

  /** Corresponding vertex. */
  private MapperDAGVertex parentVertex;

  /** Timings for the given vertex on available operators. */
  private final List<Timing> timings;

  /** Available operators. */
  private final List<ComponentInstance> operators;

  /** Number of repetitions that may ponderate the timing. */
  private int nbRepeat;

  /**
   * Gets the nb repeat.
   *
   * @return the nb repeat
   */
  public int getNbRepeat() {
    return this.nbRepeat;
  }

  /**
   * Instantiates a new vertex init.
   */
  public VertexInit() {
    super();
    this.timings = new ArrayList<>();
    this.nbRepeat = 1;
    this.parentVertex = null;
    this.operators = new ArrayList<>();
  }

  /**
   * Sets the nb repeat.
   *
   * @param nbRepeat
   *          the new nb repeat
   */
  public void setNbRepeat(final int nbRepeat) {
    this.nbRepeat = nbRepeat;
  }

  /**
   * Adds the timing.
   *
   * @param timing
   *          the timing
   */
  public void addTiming(final Timing timing) {
    if (getTiming(timing.getOperatorDefinitionId()) == null) {
      this.timings.add(timing);
    }
  }

  /**
   * Enabling the current vertex on the given operator. The operation is straightforward for normal vertices. For special vertices, a test is done on the
   * neighbors.
   *
   * @param operator
   *          the operator
   */
  public void addOperator(final ComponentInstance operator) {
    if (operator != null) {
      this.operators.add(operator);
    }
  }

  /**
   * Clone.
   *
   * @param parentVertex
   *          the parent vertex
   * @return the vertex init
   */
  public VertexInit clone(final MapperDAGVertex parentVertex) {

    final VertexInit property = new VertexInit();

    if (parentVertex != null) {
      property.setParentVertex(parentVertex);
    }

    final Iterator<Timing> it = getTimings().iterator();
    while (it.hasNext()) {
      final Timing next = it.next();
      property.addTiming(next);
    }

    final Iterator<ComponentInstance> it2 = this.operators.iterator();
    while (it2.hasNext()) {
      final ComponentInstance next = it2.next();
      property.addOperator(next);
    }

    property.setNbRepeat(this.nbRepeat);

    return property;

  }

  /**
   * Returns all the operators that can execute the vertex. Special vertices are originally enabled on every operator but their status is updated depending on
   * the mapping of their neighbors
   *
   * @return the initial operator list
   */
  public List<ComponentInstance> getInitialOperatorList() {
    return Collections.unmodifiableList(this.operators);
  }

  /**
   * Checks in the vertex initial properties if it can be mapped on the given operator. For special vertices, the predecessors and successor mapping
   * possibilities are studied
   *
   * @param operator
   *          the operator
   * @return true, if is mapable
   */
  public boolean isMapable(final ComponentInstance operator) {

    for (final ComponentInstance op : this.operators) {
      if (op.getInstanceName().equals(operator.getInstanceName())) {
        return true;
      }
    }

    return false;
  }

  /**
   * Gets the parent vertex.
   *
   * @return the parent vertex
   */
  public MapperDAGVertex getParentVertex() {
    return this.parentVertex;
  }

  /**
   * Returns the timing of the operation = number of repetitions * scenario time. Special vertices have specific time computation
   *
   * @param operator
   *          the operator
   * @return the time
   */
  public long getTime(final ComponentInstance operator) {

    long time = 0;

    if (operator != DesignTools.NO_COMPONENT_INSTANCE) {

      // Non special vertex timings are retrieved from scenario
      // Special vertex timings were computed from scenario
      final Timing returntiming = getTiming(operator.getComponent().getVlnv().getName());

      if (!SpecialVertexManager.isSpecial(this.parentVertex)) {

        if (returntiming != Timing.UNAVAILABLE) {
          if (returntiming.getTime() != 0) {
            // The basic timing is multiplied by the number of
            // repetitions
            time = returntiming.getTime() * this.nbRepeat;
          } else {
            time = Timing.DEFAULT_TASK_TIME;
          }
        }
      } else {
        // Special vertex timings are retrieved
        if (returntiming != Timing.UNAVAILABLE) {
          if (returntiming.getTime() != 0) {
            time = returntiming.getTime();
          } else {
            time = Timing.DEFAULT_SPECIAL_VERTEX_TIME;
          }
        } else {
          time = Timing.DEFAULT_SPECIAL_VERTEX_TIME;
        }
      }
    }

    return time;
  }

  /**
   * Gets the timing.
   *
   * @param operatordefId
   *          the operatordef id
   * @return the timing
   */
  public Timing getTiming(final String operatordefId) {

    Timing returntiming = Timing.UNAVAILABLE;

    final Iterator<Timing> iterator = this.timings.iterator();

    while (iterator.hasNext()) {
      final Timing currenttiming = iterator.next();

      if (operatordefId.equals(currenttiming.getOperatorDefinitionId())) {
        returntiming = currenttiming;
        break;
      }
    }

    return returntiming;
  }

  /**
   * Gets the timings.
   *
   * @return the timings
   */
  public List<Timing> getTimings() {
    return Collections.unmodifiableList(this.timings);
  }

  /**
   * Checks if the vertex first non special predecessors can be mapped on the given operator.
   *
   * @param operator
   *          the operator
   * @return true, if is pred mapable
   */
  public boolean isPredMapable(final ComponentInstance operator) {

    boolean predMapable = false;

    for (final MapperDAGVertex pred : this.parentVertex.getPredecessors(true).keySet()) {
      if (pred == null) {
        return false;
      } else if (SpecialVertexManager.isSpecial(pred)) {
        predMapable |= pred.getInit().isPredMapable(operator);
      } else {
        predMapable |= pred.getInit().isMapable(operator);
      }
    }

    return predMapable;
  }

  /**
   * Checks if the vertex first non special successor can be mapped on the given operator.
   *
   * @param operator
   *          the operator
   * @return true, if is succ mapable
   */
  public boolean isSuccMapable(final ComponentInstance operator) {

    boolean succMapable = false;

    for (final MapperDAGVertex succ : this.parentVertex.getSuccessors(true).keySet()) {
      if (succ == null) {
        return false;
      } else if (SpecialVertexManager.isSpecial(succ)) {
        succMapable |= succ.getInit().isSuccMapable(operator);
      } else {
        succMapable |= succ.getInit().isMapable(operator);
      }
    }

    return succMapable;
  }

  /**
   * Sets the parent vertex.
   *
   * @param parentVertex
   *          the new parent vertex
   */
  public void setParentVertex(final MapperDAGVertex parentVertex) {
    this.parentVertex = parentVertex;
  }
}
