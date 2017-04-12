/*******************************************************************************
 * Copyright or © or Copr. 2014 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
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
package org.ietr.preesm.core.scenario;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import org.eclipse.emf.ecore.EObject;
import org.ietr.preesm.core.scenario.ParameterValue.ParameterType;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

// TODO: Auto-generated Javadoc
/**
 * Manager class for parameters, storing values given by the user to parameters.
 *
 * @author cguy
 */
public class ParameterValueManager {

  /** The parameter values. */
  // Set of ParameterValues
  private Set<ParameterValue> parameterValues;

  /**
   * Instantiates a new parameter value manager.
   */
  public ParameterValueManager() {
    this.parameterValues = new HashSet<>();
  }

  /**
   * Gets the parameter values.
   *
   * @return the parameter values
   */
  public Set<ParameterValue> getParameterValues() {
    return this.parameterValues;
  }

  /**
   * Gets the sorted parameter values.
   *
   * @return the sorted parameter values
   */
  public Set<ParameterValue> getSortedParameterValues() {
    final Set<ParameterValue> result = new ConcurrentSkipListSet<>((o1, o2) -> o1.getName().compareTo(o2.getName()));
    result.addAll(this.parameterValues);
    return result;
  }

  /**
   * Sets the parameter values.
   *
   * @param parameterValues
   *          the new parameter values
   */
  public void setParameterValues(final Set<ParameterValue> parameterValues) {
    this.parameterValues = parameterValues;
  }

  /**
   * Adds the independent parameter value.
   *
   * @param parameter
   *          the parameter
   * @param value
   *          the value
   * @param parent
   *          the parent
   */
  public void addIndependentParameterValue(final Parameter parameter, final String value, final String parent) {
    final ParameterValue pValue = new ParameterValue(parameter, ParameterType.INDEPENDENT, parent);
    pValue.setValue(value);
    this.parameterValues.add(pValue);
  }

  /**
   * Adds the actor dependent parameter value.
   *
   * @param parameter
   *          the parameter
   * @param values
   *          the values
   * @param parent
   *          the parent
   */
  public void addActorDependentParameterValue(final Parameter parameter, final Set<Integer> values, final String parent) {
    final ParameterValue pValue = new ParameterValue(parameter, ParameterType.ACTOR_DEPENDENT, parent);
    pValue.setValues(values);
    this.parameterValues.add(pValue);
  }

  /**
   * Adds the parameter dependent parameter value.
   *
   * @param param
   *          the param
   * @param parent
   *          the parent
   */
  private void addParameterDependentParameterValue(final Parameter param, final String parent) {
    final Set<String> inputParametersNames = new HashSet<>();
    for (final Parameter p : param.getInputParameters()) {
      inputParametersNames.add(p.getName());
    }

    addParameterDependentParameterValue(param, param.getExpression().getString(), inputParametersNames, parent);
  }

  /**
   * Adds the parameter dependent parameter value.
   *
   * @param parameter
   *          the parameter
   * @param expression
   *          the expression
   * @param inputParameters
   *          the input parameters
   * @param parent
   *          the parent
   */
  public void addParameterDependentParameterValue(final Parameter parameter, final String expression, final Set<String> inputParameters, final String parent) {
    final ParameterValue pValue = new ParameterValue(parameter, ParameterType.PARAMETER_DEPENDENT, parent);
    pValue.setExpression(expression);
    pValue.setInputParameters(inputParameters);
    this.parameterValues.add(pValue);
  }

  /**
   * Adds the parameter value.
   *
   * @param param
   *          the param
   */
  public void addParameterValue(final Parameter param) {
    final EObject container = param.eContainer();
    String parent = "";
    if (container instanceof PiGraph) {
      parent = ((PiGraph) container).getName();
    }
    Set<Parameter> inputParameters = new HashSet<>();
    inputParameters = param.getInputParameters();

    if (param.isLocallyStatic()) {
      if (param.isDependent()) {
        // Add a parameter dependent value (a locally static parameter
        // cannot be actor dependent)
        addParameterDependentParameterValue(param, parent);
      } else {
        // Add an independent parameter value
        addIndependentParameterValue(param, param.getExpression().getString(), parent);
      }
    } else {
      final boolean isActorDependent = inputParameters.size() < param.getConfigInputPorts().size();

      if (isActorDependent) {
        final Set<Integer> values = new HashSet<>();
        values.add(1);
        // Add an actor dependent value
        addActorDependentParameterValue(param, values, parent);
      } else {
        // Add a parameter dependent value
        addParameterDependentParameterValue(param, parent);
      }
    }
  }

  /**
   * Update with.
   *
   * @param graph
   *          the graph
   */
  public void updateWith(final PiGraph graph) {
    getParameterValues().clear();
    for (final Parameter p : graph.getAllParameters()) {
      if (!p.isConfigurationInterface()) {
        addParameterValue(p);
      }
    }
  }
}
