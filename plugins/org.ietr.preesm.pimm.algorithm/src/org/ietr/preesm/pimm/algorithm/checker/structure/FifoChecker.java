/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
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
package org.ietr.preesm.pimm.algorithm.checker.structure;

import java.util.LinkedHashSet;
import java.util.Set;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

// TODO: Auto-generated Javadoc
/**
 * Class to check different properties of the Fifos of a PiGraph. Entry point is the checkFifos method. Invalid Fifos
 * are kept in several sets.
 *
 * @author cguy
 *
 */
public class FifoChecker {
  // Fifos with "void" type (authorized but can lead to problems when
  /** The fifo with void type. */
  // generating code)
  private final Set<Fifo> fifoWithVoidType;

  /** The fifo with one zero rate. */
  // Fifos with one rate (production or consumption) to 0 but not the other
  private final Set<Fifo> fifoWithOneZeroRate;
  // Fifos with rates (production and consumption) to 0 (authorized but user
  /** The fifo with zero rates. */
  // may have forgotten to set rates)
  private final Set<Fifo> fifoWithZeroRates;

  /**
   * Instantiates a new fifo checker.
   */
  public FifoChecker() {
    this.fifoWithVoidType = new LinkedHashSet<>();
    this.fifoWithOneZeroRate = new LinkedHashSet<>();
    this.fifoWithZeroRates = new LinkedHashSet<>();
  }

  /**
   * Check types and rates of the Fifos of a PiGraph.
   *
   * @param graph
   *          the PiGraph for which we check Fifos
   * @return true if all the Fifos of graph are valid, false otherwise
   */
  public boolean checkFifos(final PiGraph graph) {
    boolean ok = true;
    for (final Fifo f : graph.getFifos()) {
      ok &= checkFifo(f);
    }
    return ok;
  }

  /**
   * Check type and rates of a Fifo.
   *
   * @param f
   *          the Fifo to check
   * @return true if f is valid, false otherwise
   */
  private boolean checkFifo(final Fifo f) {
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
    if (f.getSourcePort().getPortRateExpression().getExpressionAsString().equals("0")) {
      if (f.getTargetPort().getPortRateExpression().getExpressionAsString().equals("0")) {
        this.fifoWithZeroRates.add(f);
        return false;
      } else {
        this.fifoWithOneZeroRate.add(f);
        return false;
      }
    } else if (f.getTargetPort().getPortRateExpression().getExpressionAsString().equals("0")) {
      this.fifoWithOneZeroRate.add(f);
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
      this.fifoWithVoidType.add(f);
      return false;
    }
    return true;
  }

  /**
   * Gets the fifo with void type.
   *
   * @return the fifo with void type
   */
  public Set<Fifo> getFifoWithVoidType() {
    return this.fifoWithVoidType;
  }

  /**
   * Gets the fifo with one zero rate.
   *
   * @return the fifo with one zero rate
   */
  public Set<Fifo> getFifoWithOneZeroRate() {
    return this.fifoWithOneZeroRate;
  }

  /**
   * Gets the fifo with zero rates.
   *
   * @return the fifo with zero rates
   */
  public Set<Fifo> getFifoWithZeroRates() {
    return this.fifoWithZeroRates;
  }
}
