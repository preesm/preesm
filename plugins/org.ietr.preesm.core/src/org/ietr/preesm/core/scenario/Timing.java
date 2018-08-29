/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2013)
 * Pengcheng Mu <pengcheng.mu@insa-rennes.fr> (2008)
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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

// TODO: Auto-generated Javadoc
/**
 * A timing links a vertex (either from SDFGraph or from PiGraph) and an operator definition to a time. Ids are used to
 * make the scenario independent from model implementations.
 *
 * @author mpelcat
 */
public class Timing {

  /** The Constant UNAVAILABLE. */
  public static final Timing UNAVAILABLE = null;

  /** The Constant DEFAULT_TASK_TIME. */
  public static final long DEFAULT_TASK_TIME = 100;

  /** The Constant DEFAULT_SPECIAL_VERTEX_TIME. */
  public static final long DEFAULT_SPECIAL_VERTEX_TIME = 10;

  /** The Constant DEFAULT_EXPRESSION_VALUE. */
  private static final String DEFAULT_EXPRESSION_VALUE = "100";

  /** The Constant DEFAULT_SPECIAL_VERTEX_EXPRESSION_VALUE. */
  public static final String DEFAULT_SPECIAL_VERTEX_EXPRESSION_VALUE = "10";

  /** Long value of the timing, only available if isEvaluated is at true. */
  private long time;

  /** String expression of the timing, can contains parameters from input parameters. */
  private String stringValue;

  /** Boolean indicatin if the expression has been evaluated and thus if the time is available. */
  private boolean isEvaluated;

  /**
   * Operator to which the timing is related (i.e., processing element on which the vertex of the graph takes the given
   * time to execute)
   */
  private final String operatorDefinitionId;

  /** Set of Usable Parameters for the expression. */
  private Set<String> inputParameters;

  /** Vertex for which the timing is given. */
  private final String vertexId;

  /**
   * Constructors.
   *
   * @param operatorDefinitionId
   *          the operator definition id
   * @param vertexId
   *          the vertex id
   */

  public Timing(final String operatorDefinitionId, final String vertexId) {
    this.time = Timing.DEFAULT_TASK_TIME;
    this.stringValue = Timing.DEFAULT_EXPRESSION_VALUE;
    this.inputParameters = new LinkedHashSet<>();
    this.operatorDefinitionId = operatorDefinitionId;
    this.vertexId = vertexId;
    this.isEvaluated = true;
  }

  /**
   * Instantiates a new timing.
   *
   * @param operatorId
   *          the operator id
   * @param sdfVertexId
   *          the sdf vertex id
   * @param time
   *          the time
   */
  public Timing(final String operatorId, final String sdfVertexId, final long time) {
    this(operatorId, sdfVertexId);
    this.time = time;
    this.stringValue = String.valueOf(time);
    this.isEvaluated = true;
  }

  /**
   * Instantiates a new timing.
   *
   * @param operatorId
   *          the operator id
   * @param vertexId
   *          the vertex id
   * @param expression
   *          the expression
   */
  public Timing(final String operatorId, final String vertexId, final String expression) {
    this(operatorId, vertexId);
    this.stringValue = expression;
    this.isEvaluated = false;
    tryToEvaluateWith(new LinkedHashMap<String, Integer>());
  }

  /**
   * Instantiates a new timing.
   *
   * @param operatorId
   *          the operator id
   * @param vertexId
   *          the vertex id
   * @param expression
   *          the expression
   * @param inputParameters
   *          the input parameters
   */
  public Timing(final String operatorId, final String vertexId, final String expression,
      final Set<String> inputParameters) {
    this(operatorId, vertexId);
    this.stringValue = expression;
    this.isEvaluated = false;
    this.inputParameters = inputParameters;
    tryToEvaluateWith(new LinkedHashMap<String, Integer>());
  }

  /**
   * Gets the operator definition id.
   *
   * @return the operator definition id
   */
  public String getOperatorDefinitionId() {
    return this.operatorDefinitionId;
  }

  /**
   * Gets the time.
   *
   * @return time, only if it is available (if the expression have been evaluated)
   */
  public long getTime() {
    if (this.isEvaluated) {
      return this.time;
    } else {
      return -1;
    }
  }

  /**
   * Gets the vertex id.
   *
   * @return the vertex id
   */
  public String getVertexId() {
    return this.vertexId;
  }

  /**
   * The given time is set if it is strictly positive. Otherwise, 1 is set. In every cases, the expression is set as the
   * corresponding string and considered evaluated
   *
   * @param time
   *          the new time we want to set
   */
  public void setTime(final long time) {
    if (time > 0) {
      this.time = time;
    } else {
      WorkflowLogger.getLogger().log(Level.WARNING,
          "Trying to set a non strictly positive time for vertex " + this.vertexId);
      this.time = 1;
    }
    this.stringValue = String.valueOf(this.time);
    this.isEvaluated = true;
  }

  /**
   * Gets the input parameters.
   *
   * @return the input parameters
   */
  public Set<String> getInputParameters() {
    return this.inputParameters;
  }

  /**
   * Sets the input parameters.
   *
   * @param inputParameters
   *          the new input parameters
   */
  public void setInputParameters(final Set<String> inputParameters) {
    this.inputParameters = inputParameters;
  }

  /**
   * Gets the string value.
   *
   * @return the string value
   */
  public String getStringValue() {
    return this.stringValue;
  }

  /**
   * Sets the string value.
   *
   * @param stringValue
   *          the new string value
   */
  public void setStringValue(final String stringValue) {
    this.stringValue = stringValue;
    this.isEvaluated = false;
    tryToEvaluateWith(new LinkedHashMap<String, Integer>());
  }

  /**
   * Checks if is evaluated.
   *
   * @return true, if is evaluated
   */
  public boolean isEvaluated() {
    return this.isEvaluated;
  }

  /**
   * Test if the {@link Timing} can be parsed (Check Syntax Errors).
   *
   * @return true if it can be parsed.
   */
  public boolean canParse() {
    final JEP jep = new JEP();
    try {
      jep.parse(this.stringValue);
    } catch (final ParseException e) {
      return false;
    }
    return true;
  }

  /**
   * Test if the {@link Timing} can be evaluated (Check Parameters Errors).
   *
   * @return true if it can be evaluated.
   */
  public boolean canEvaluate() {
    final JEP jep = new JEP();
    try {
      for (final String parameter : this.inputParameters) {
        jep.addVariable(parameter, 1);
      }
      final Node parse = jep.parse(this.stringValue);
      jep.evaluate(parse);
      return true;
    } catch (final ParseException e) {
      return false;
    }
  }

  /**
   * Evaluate the timing expression with the given values for parameters. If the evaluation is successful, isEvaluated
   * is set to true
   *
   * @param parametersValues
   *          the map of parameters names and associated values with which we want to evaluate the expression
   */
  public void tryToEvaluateWith(final Map<String, Integer> parametersValues) {
    final JEP jep = new JEP();
    try {
      for (final String parameter : parametersValues.keySet()) {
        jep.addVariable(parameter, parametersValues.get(parameter));
      }
      final Node parse = jep.parse(this.stringValue);
      final Object evaluate = jep.evaluate(parse);
      final long result = ((Double) evaluate).longValue();
      this.time = result;
      this.isEvaluated = true;
    } catch (final ParseException e) {
      this.isEvaluated = false;
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    boolean equals = false;

    if (obj instanceof Timing) {
      final Timing otherT = (Timing) obj;
      equals = this.operatorDefinitionId.equals(otherT.getOperatorDefinitionId());
      equals &= this.vertexId.equals((otherT.getVertexId()));
    }

    return equals;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    if (this.isEvaluated) {
      return "{" + this.vertexId + "," + this.operatorDefinitionId + "," + this.time + "}";
    } else {
      return "{" + this.vertexId + "," + this.operatorDefinitionId + "," + this.stringValue + "}";
    }
  }
}
