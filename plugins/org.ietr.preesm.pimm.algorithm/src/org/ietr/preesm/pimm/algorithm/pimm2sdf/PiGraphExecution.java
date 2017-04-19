/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.pimm.algorithm.pimm2sdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

// TODO: Auto-generated Javadoc
/**
 * This class corresponds to one execution of a PiGraph with given values for each parameters.
 *
 * @author cguy
 */
public class PiGraphExecution {

  /** The executed pi graph. */
  private final PiGraph executedPiGraph;

  /** The execution label. */
  private final String executionLabel;

  /** The execution number. */
  private final int executionNumber;

  /** The parameter values. */
  private final Map<String, List<Integer>> parameterValues;

  /**
   * Instantiates a new pi graph execution.
   *
   * @param graph
   *          the graph
   * @param values
   *          the values
   */
  public PiGraphExecution(final PiGraph graph, final Map<String, List<Integer>> values) {
    this.executedPiGraph = graph;
    this.parameterValues = values;
    this.executionLabel = "";
    this.executionNumber = 0;
  }

  /**
   * Instantiates a new pi graph execution.
   *
   * @param graph
   *          the graph
   * @param values
   *          the values
   * @param label
   *          the label
   * @param number
   *          the number
   */
  public PiGraphExecution(final PiGraph graph, final Map<String, List<Integer>> values, final String label, final int number) {
    this.executedPiGraph = graph;
    this.parameterValues = values;
    this.executionLabel = label;
    this.executionNumber = number;
  }

  /**
   * Gets the values.
   *
   * @param p
   *          the p
   * @return the values
   */
  public List<Integer> getValues(final Parameter p) {
    return this.parameterValues.get(p.getName());
  }

  /**
   * Gets the unique value.
   *
   * @param p
   *          the p
   * @return the unique value
   */
  public Integer getUniqueValue(final Parameter p) {
    final List<Integer> pValues = getValues(p);
    if ((pValues != null) && (pValues.size() == 1)) {
      return pValues.get(0);
    } else {
      return null;
    }
  }

  /**
   * Checks for value.
   *
   * @param p
   *          the p
   * @return true, if successful
   */
  public boolean hasValue(final Parameter p) {
    return ((getValues(p) != null) && !getValues(p).isEmpty());
  }

  /**
   * Gets the number of inner executions.
   *
   * @param subgraph
   *          the subgraph
   * @return the number of inner executions
   */
  public int getNumberOfInnerExecutions(final PiGraph subgraph) {
    int maxNumberOfValues = 0;
    for (final String s : this.parameterValues.keySet()) {
      if (getSubgraphParametersNames(subgraph).contains(s)) {
        final int size = this.parameterValues.get(s).size();
        if (size > maxNumberOfValues) {
          maxNumberOfValues = size;
        }
      }
    }
    return maxNumberOfValues;
  }

  /**
   * Extract inner execution.
   *
   * @param subgraph
   *          the subgraph
   * @param selector
   *          the selector
   * @return the pi graph execution
   */
  public PiGraphExecution extractInnerExecution(final PiGraph subgraph, final int selector) {
    final Map<String, List<Integer>> innerParameterValues = new HashMap<>();
    for (final String s : this.parameterValues.keySet()) {
      if (getSubgraphParametersNames(subgraph).contains(s)) {
        final int size = this.parameterValues.get(s).size();
        final List<Integer> value = new ArrayList<>();
        value.add(this.parameterValues.get(s).get(selector % size));
        innerParameterValues.put(s, value);

      } else {
        innerParameterValues.put(s, this.parameterValues.get(s));
      }
    }
    return new PiGraphExecution(this.executedPiGraph, innerParameterValues, this.executionLabel + "_" + selector, selector);
  }

  /**
   * Gets the subgraph parameters names.
   *
   * @param subgraph
   *          the subgraph
   * @return the subgraph parameters names
   */
  private Set<String> getSubgraphParametersNames(final PiGraph subgraph) {
    final Set<String> parametersNames = new HashSet<>();
    for (final Parameter p : subgraph.getParameters()) {
      parametersNames.add(p.getName());
    }
    return parametersNames;
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
