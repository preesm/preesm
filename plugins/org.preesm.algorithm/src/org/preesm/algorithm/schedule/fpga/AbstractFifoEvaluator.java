package org.preesm.algorithm.schedule.fpga;

import java.util.Map;
import org.preesm.algorithm.pisdf.autodelays.AbstractGraph.FifoAbstraction;
import org.preesm.model.pisdf.AbstractVertex;

/**
 * This class is a base class for the evaluation of FIFO, i.e. computation of start and finish time of a producer
 * considering a consumer, and computation of the fifo size.
 * 
 * @author ahonorat
 */
public abstract class AbstractFifoEvaluator {

  final Map<AbstractVertex, Long> brv;

  public AbstractFifoEvaluator(final Map<AbstractVertex, Long> brv) {
    this.brv = brv;
  }

  /**
   * Get the number of firings of the consumer triggered by executing {@code nbfiringsOpposite} last firings of the
   * producer, without considering delays.
   * 
   * @param fa
   *          Fifo abstraction to consider.
   * @param nbfiringsOpposite
   *          Number of firings of the opposite side.
   * @param reverse
   *          If true, reverse the fifo direction.
   * @return Number of firings of the consumer, or producer if {@code reverse} is true.
   */
  public static final long nbfOpposite(final FifoAbstraction fa, final long nbfiringsOpposite, final boolean reverse) {
    long nbf;
    if (reverse) { // nbff
      nbf = (nbfiringsOpposite * fa.getConsRate() + fa.getProdRate() - 1) / fa.getProdRate();
    } else { // nblf
      nbf = (nbfiringsOpposite * fa.getProdRate() + fa.getConsRate() - 1) / fa.getConsRate();
    }
    return nbf;
  }

}
