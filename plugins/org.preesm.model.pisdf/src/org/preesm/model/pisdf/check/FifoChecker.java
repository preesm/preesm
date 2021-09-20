/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
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
package org.preesm.model.pisdf.check;

import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;

/**
 * Class to check different properties of the Fifos of a PiGraph. Entry point is the checkFifos method. Invalid Fifos
 * are kept in several sets.
 *
 * @author cguy
 *
 */
public class FifoChecker extends AbstractPiSDFObjectChecker {

  /**
   * Instantiates a new fifo checker.
   */
  public FifoChecker() {
    super();
  }

  /**
   * Instantiates a new fifo checker.
   * 
   * @param throwExceptionLevel
   *          The maximum level of error throwing exceptions.
   * @param loggerLevel
   *          The maximum level of error generating logs.
   */
  public FifoChecker(final CheckerErrorLevel throwExceptionLevel, final CheckerErrorLevel loggerLevel) {
    super(throwExceptionLevel, loggerLevel);
  }

  @Override
  public Boolean casePiGraph(final PiGraph graph) {
    boolean ok = true;
    for (final Fifo f : graph.getFifos()) {
      ok &= doSwitch(f);
    }
    return ok;
  }

  @Override
  public Boolean caseFifo(final Fifo f) {
    boolean ok = true;
    ok &= checkFifoType(f);
    ok &= checkFifoRates(f);
    return ok;
  }

  /**
   * Check production and consumption rates of a Fifo. If one of the rate equals 0, the Fifo is invalid. If both rates
   * equal 0, the Fifo is valid but the user may have forgotten to set the rates
   *
   * @param f
   *          the Fifo to check
   * @return true if no rate of f is at 0, false otherwise
   */
  private boolean checkFifoRates(final Fifo f) {
    final long rateSource = f.getSourcePort().getPortRateExpression().evaluate();
    final long rateTarget = f.getTargetPort().getPortRateExpression().evaluate();
    if ((rateSource == 0 && rateTarget != 0) || (rateSource != 0 && rateTarget == 0)) {
      reportError(CheckerErrorLevel.FATAL_ANALYSIS, f, "Fifo [%s] has one of its rates being 0, but not the other.", f);
      return false;
    }
    return true;
  }

  /**
   * Check the type of a Fifo. If the type is "void", code won't be generated.
   *
   * @param f
   *          the Fifo to check
   * @return true if the type of f is not void, false otherwise
   */
  private boolean checkFifoType(final Fifo f) {
    if (f.getType().equals("void")) {
      reportError(CheckerErrorLevel.FATAL_CODEGEN, f, "Fifo [%s] has void type.", f.getId());
      return false;
    }
    return true;
  }

}
