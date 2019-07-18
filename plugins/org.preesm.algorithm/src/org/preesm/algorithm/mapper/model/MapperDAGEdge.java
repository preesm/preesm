/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Jonathan Piat [jpiat@laas.fr] (2011)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2012)
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
package org.preesm.algorithm.mapper.model;

import org.preesm.algorithm.mapper.model.property.EdgeInit;
import org.preesm.algorithm.mapper.model.property.EdgeTiming;
import org.preesm.algorithm.model.dag.DAGEdge;

/**
 * Represents an edge in a DAG of type {@link MapperDAG} used in the mapper.
 *
 * @author mpelcat
 */
public class MapperDAGEdge extends DAGEdge {

  private EdgeInit   initialEdgeProperty;
  private EdgeTiming timingEdgeProperty;

  public MapperDAGEdge() {
    this.initialEdgeProperty = new EdgeInit();
    this.timingEdgeProperty = new EdgeTiming();
  }

  public EdgeInit getInit() {
    return this.initialEdgeProperty;
  }

  public void setInit(final EdgeInit initialEdgeProperty) {
    this.initialEdgeProperty = initialEdgeProperty;
  }

  public EdgeTiming getTiming() {
    return this.timingEdgeProperty;
  }

  public void setTiming(final EdgeTiming timingEdgeProperty) {
    this.timingEdgeProperty = timingEdgeProperty;
  }

  @Override
  public String toString() {
    return "<" + getSource().getName() + "," + getTarget().getName() + ">";
  }
}
