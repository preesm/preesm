/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2014)
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
package org.ietr.preesm.core.scenario;

import java.util.LinkedHashSet;
import java.util.Set;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

/**
 * Value(s) of a parameter in a graph. It can be: Static, Dependent or Dynamic.
 *
 * @author jheulot
 */
public class ParameterValue {
  /**
   * Different type of Parameter.
   */
  public enum ParameterType {

    /** The independent. */
    // No configuration input port
    INDEPENDENT,

    /** The actor dependent. */
    // Direct dependency from a configuration actor to this parameter
    ACTOR_DEPENDENT,
    // Configuration input ports, but none directly dependent from a
    /** The parameter dependent. */
    // configuration actor
    PARAMETER_DEPENDENT
  }

  /** Parameter for which we keep value(s). */
  private Parameter parameter;

  /** The name of the parameter. */
  private final String name;

  /** The parameter type. */
  private final ParameterType type;

  /** The corresponding parent vertex. */
  private final String parentVertex;

  /** Type specific attributes. */
  /* INDEPENDENT */
  private String value;

  /** The values. */
  /* ACTOR_DEPENDENT */
  private Set<Integer> values;

  /** The input parameters. */
  /* PARAMETER_DEPENDENT */
  private Set<String> inputParameters;

  /** The expression. */
  private String expression;

  /**
   * Gets the expression.
   *
   * @return the expression
   */
  public String getExpression() {
    return this.expression;
  }

  /**
   * Sets the input parameters.
   *
   * @param inputParameters
   *          the inputParameters to set
   */
  public void setInputParameters(final Set<String> inputParameters) {
    this.inputParameters = inputParameters;
  }

  /**
   * Sets the expression.
   *
   * @param expression
   *          the expression to set
   */
  public void setExpression(final String expression) {
    this.expression = expression;
  }

  /**
   * Instantiates a new parameter value.
   *
   * @param parameter
   *          the parameter
   * @param type
   *          the type
   * @param parent
   *          the parent
   */
  public ParameterValue(final Parameter parameter, final ParameterType type, final String parent) {
    setParameter(parameter);
    this.name = parameter.getName();
    this.type = type;
    this.values = new LinkedHashSet<>();
    this.inputParameters = new LinkedHashSet<>();
    this.parentVertex = parent;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  public ParameterType getType() {
    return this.type;
  }

  /**
   * Gets the parent vertex.
   *
   * @return the parent
   */
  public String getParentVertex() {
    return this.parentVertex;
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  public String getValue() {
    return this.value;
  }

  /**
   * Gets the values.
   *
   * @return the values
   */
  public Set<Integer> getValues() {
    return this.values;
  }

  /**
   * Sets the value.
   *
   * @param value
   *          the value to set
   */
  public void setValue(final String value) {
    this.value = value;
  }

  /**
   * Sets the values.
   *
   * @param values
   *          the values to set
   */
  public void setValues(final Set<Integer> values) {
    this.values = values;
  }

  /**
   * Gets the input parameters.
   *
   * @return the inputParameters
   */
  public Set<String> getInputParameters() {
    return this.inputParameters;
  }

  /**
   * Test if the parameter value is defined correctly.
   *
   * @return if the parameter value is defined correctly
   */
  public boolean isValid() {
    switch (this.type) {
      case INDEPENDENT:
        return true;
      case ACTOR_DEPENDENT:
        return !this.values.isEmpty();
      case PARAMETER_DEPENDENT:
        final JEP jep = new JEP();
        try {
          for (final String parameter : this.inputParameters) {
            jep.addVariable(parameter, 1);
          }
          final Node parse = jep.parse(this.expression);
          jep.evaluate(parse);
          return true;
        } catch (final ParseException e) {
          return false;
        }
      default:
        return false;
    }
  }

  /**
   * Gets the parameter.
   *
   * @return the parameter
   */
  public Parameter getParameter() {
    return this.parameter;
  }

  /**
   * Sets the parameter.
   *
   * @param parameter
   *          the new parameter
   */
  public void setParameter(final Parameter parameter) {
    this.parameter = parameter;
  }
}
