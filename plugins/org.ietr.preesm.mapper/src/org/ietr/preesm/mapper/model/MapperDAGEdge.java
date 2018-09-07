/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.ietr.preesm.mapper.model;

import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.preesm.mapper.model.property.EdgeInit;
import org.ietr.preesm.mapper.model.property.EdgeTiming;

/**
 * Represents an edge in a DAG of type {@link MapperDAG} used in the mapper.
 *
 * @author mpelcat
 */
public class MapperDAGEdge extends DAGEdge {

  /** The initial edge property. */
  protected EdgeInit initialEdgeProperty;

  /** The timing edge property. */
  protected EdgeTiming timingEdgeProperty;

  /**
   * Instantiates a new mapper DAG edge.
   *
   * @param source
   *          the source
   * @param destination
   *          the destination
   */
  public MapperDAGEdge(final MapperDAGVertex source, final MapperDAGVertex destination) {
    this.initialEdgeProperty = new EdgeInit();
    this.timingEdgeProperty = new EdgeTiming();
  }

  /**
   * Gets the inits the.
   *
   * @return the inits the
   */
  public EdgeInit getInit() {
    return this.initialEdgeProperty;
  }

  /**
   * Sets the inits the.
   *
   * @param initialEdgeProperty
   *          the new inits the
   */
  public void setInit(final EdgeInit initialEdgeProperty) {
    this.initialEdgeProperty = initialEdgeProperty;
  }

  /**
   * Gets the timing.
   *
   * @return the timing
   */
  public EdgeTiming getTiming() {
    return this.timingEdgeProperty;
  }

  /**
   * Sets the timing.
   *
   * @param timingEdgeProperty
   *          the new timing
   */
  public void setTiming(final EdgeTiming timingEdgeProperty) {
    this.timingEdgeProperty = timingEdgeProperty;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.dag.DAGEdge#toString()
   */
  @Override
  public String toString() {
    return "<" + getSource().getName() + "," + getTarget().getName() + ">";
  }
}
