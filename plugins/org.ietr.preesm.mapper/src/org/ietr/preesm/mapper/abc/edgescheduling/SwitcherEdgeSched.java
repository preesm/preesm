/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
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
package org.ietr.preesm.mapper.abc.edgescheduling;

import java.util.Random;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.preesm.mapper.abc.order.OrderManager;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.special.TransferVertex;

// TODO: Auto-generated Javadoc
/**
 * An advanced edge scheduler. It looks for the largest free interval in scheduling and schedules the new communication
 * in this slot.
 *
 * @author mpelcat
 */
public class SwitcherEdgeSched extends AbstractEdgeSched {

  /** The interval finder. */
  private IntervalFinder intervalFinder = null;

  /**
   * Instantiates a new switcher edge sched.
   *
   * @param orderManager
   *          the order manager
   */
  public SwitcherEdgeSched(final OrderManager orderManager) {
    super(orderManager);

    this.intervalFinder = new IntervalFinder(orderManager);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.edgescheduling.IEdgeSched#schedule(org.ietr.preesm.mapper.model.
   * special.TransferVertex, org.ietr.preesm.mapper.model.MapperDAGVertex, org.ietr.preesm.mapper.model.MapperDAGVertex)
   */
  @Override
  public void schedule(final TransferVertex vertex, final MapperDAGVertex source, final MapperDAGVertex target) {

    final ComponentInstance component = vertex.getEffectiveComponent();
    // intervalFinder.displayCurrentSchedule(vertex, source);
    final Interval largestInterval = this.intervalFinder.findLargestFreeInterval(component, source, target);

    if (largestInterval.getDuration() > 0) {
      this.orderManager.insertAtIndex(largestInterval.getTotalOrderIndex(), vertex);
    } else {
      final int sourceIndex = this.intervalFinder.getOrderManager().totalIndexOf(source) + 1;
      final int targetIndex = this.intervalFinder.getOrderManager().totalIndexOf(target);

      if ((targetIndex - sourceIndex) > 0) {
        final Random r = new Random();
        final int nextInt = r.nextInt(Integer.MAX_VALUE);
        int randomVal = Math.abs(nextInt);
        randomVal = randomVal % (targetIndex - sourceIndex);
        final int index = sourceIndex + randomVal;
        this.orderManager.insertAtIndex(index, vertex);
      } else {
        this.orderManager.insertAfter(source, vertex);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.edgescheduling.IEdgeSched#getEdgeSchedType()
   */
  @Override
  public EdgeSchedType getEdgeSchedType() {
    return EdgeSchedType.Switcher;
  }
}
