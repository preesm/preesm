/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
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
package org.ietr.preesm.pimm.algorithm.pimm2sdf;

import java.util.Map;
import org.ietr.preesm.experiment.model.pimm.Parameter;

/**
 * This class corresponds to one execution of a PiGraph with given values for each parameters.
 *
 * @author cguy
 */
public class PiGraphExecution {

  /** The execution label. */
  private final String executionLabel;

  /** The execution number. */
  private final int executionNumber;

  /** The parameter values. */
  private final Map<Parameter, Integer> parameterValues;

  /**
   * Instantiates a new pi graph execution.
   *
   * @param values
   *          the values
   */
  public PiGraphExecution(final Map<Parameter, Integer> values) {
    this(values, "", 0);
  }

  /**
   * Instantiates a new pi graph execution.
   *
   * @param values
   *          the values
   * @param label
   *          the label
   * @param number
   *          the number
   */
  public PiGraphExecution(final Map<Parameter, Integer> values, final String label, final int number) {
    this.parameterValues = values;
    this.executionLabel = label;
    this.executionNumber = number;
  }

  /**
   * Gets the values.
   *
   * @param param
   *          the p
   * @return the values
   */
  public int getValue(final Parameter param) {
    final Map<Parameter, Integer> paramVals = this.parameterValues;
    return paramVals.get(param);
  }

  /**
   * Checks for value.
   *
   * @param p
   *          the p
   * @return true, if successful
   */
  public boolean hasValue(final Parameter p) {
    return this.parameterValues.containsKey(p);
  }

  /**
   * Gets the execution label.
   *
   * @return the execution label
   */
  public String getExecutionLabel() {
    return this.executionLabel;
  }

  /**
   * Gets the execution number.
   *
   * @return the execution number
   */
  public int getExecutionNumber() {
    return this.executionNumber;
  }
}
