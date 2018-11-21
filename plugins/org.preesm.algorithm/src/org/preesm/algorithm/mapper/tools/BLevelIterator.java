/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2016)
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

import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.commons.exceptions.PreesmException;

// TODO: Auto-generated Javadoc
/**
 * Iterates the graph in ascending or descending BLevel order. Uses abc implementation to retrieve b levels.
 *
 * @author mpelcat
 */
public class BLevelIterator extends ImplementationIterator {

  /**
   * Instantiates a new b level iterator.
   *
   * @param abc
   *          the abc
   * @param dag
   *          the dag
   * @param directOrder
   *          the direct order
   */
  public BLevelIterator(final LatencyAbc abc, final MapperDAG dag, final boolean directOrder) {
    super(abc, dag, directOrder);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.tools.ImplementationIterator#compare(org.ietr.preesm.mapper.model. MapperDAGVertex,
   * org.ietr.preesm.mapper.model.MapperDAGVertex)
   */
  @Override
  public int compare(MapperDAGVertex arg0, MapperDAGVertex arg1) {

    if (this.abc != null) {
      arg0 = this.abc.translateInImplementationVertex(arg0);
      arg1 = this.abc.translateInImplementationVertex(arg1);
    }

    if (!arg0.getTiming().hasBLevel() || !arg1.getTiming().hasBLevel()) {
      final String msg = "B Level Iterator problem";
      throw new PreesmException(msg);
    }

    long bLevelDifference = (arg0.getTiming().getBLevel() - arg1.getTiming().getBLevel());

    if (!this.directOrder) {
      bLevelDifference = -bLevelDifference;
    }

    if (bLevelDifference == 0) {
      bLevelDifference = arg0.getName().compareTo(arg1.getName());
    }

    // Preventing overflows in conversion from long to int
    if (bLevelDifference >= 0) {
      bLevelDifference = 1;
    } else {
      bLevelDifference = -1;
    }

    return (int) bLevelDifference;
  }
}
