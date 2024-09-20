/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2014)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2013)
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
package org.preesm.algorithm.mapper.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import org.jgrapht.traverse.AbstractGraphIterator;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;

/**
 * Iterates the graph in ascending or descending order using the given compare function that respects topological order.
 * If an ABC is given, the implementation iterator makes it available for the compare method
 *
 * @author mpelcat
 */
public abstract class ImplementationIterator extends AbstractGraphIterator<DAGVertex, DAGEdge>
    implements Comparator<MapperDAGVertex> {

  /** Ordered vertex list parsed by the iterator. */
  private int currentIndex = -1;

  /** Iteration in ascending or descending iteration order. */
  protected boolean directOrder;

  /** ABC made available for the compare method. */
  protected LatencyAbc abc = null;

  /** Ordered vertex list parsed by the iterator. */
  private List<MapperDAGVertex> orderedlist;

  /**
   */
  protected ImplementationIterator(final LatencyAbc abc, final MapperDAG dag, final boolean directOrder) {
    super(dag);
    this.directOrder = directOrder;
    this.abc = abc;
    createOrderedList(dag);
    this.currentIndex = 0;
  }

  /**
   * Creates the ordered list.
   *
   * @param implementation
   *          the implementation
   */
  private void createOrderedList(final MapperDAG implementation) {
    // Creating a sorted list using the current class as a comparator
    this.orderedlist = new ArrayList<>();
    for (final DAGVertex dv : implementation.vertexSet()) {
      this.orderedlist.add((MapperDAGVertex) dv);
    }

    Collections.sort(this.orderedlist, this);
  }

  public List<MapperDAGVertex> getOrderedlist() {
    return this.orderedlist;
  }

  @Override
  public boolean hasNext() {
    return (this.currentIndex < this.orderedlist.size());
  }

  @Override
  public MapperDAGVertex next() {
    final int index = this.currentIndex++;
    if (index > this.orderedlist.size() - 1) {
      throw new NoSuchElementException();
    }
    return this.orderedlist.get(index);
  }

}
