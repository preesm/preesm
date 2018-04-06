/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2016)
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
package org.ietr.preesm.pimm.algorithm.pimm2srdag;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.ietr.preesm.core.scenario.ParameterValue;
import org.ietr.preesm.core.scenario.ParameterValueManager;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.expression.ExpressionEvaluator;
import org.ietr.preesm.experiment.model.factory.PiMMUserFactory;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.pimm.algorithm.pimm2sdf.PiGraphExecution;
import org.ietr.preesm.pimm.algorithm.pimm2srdag.visitor.StaticPiMM2SrDAGVisitor;

/**
 * The Class StaticPiMM2SDFLauncher.
 */
public class StaticPiMM2SrDAGLauncher extends PiMMSwitch<Boolean> {

  /** The scenario. */
  private final PreesmScenario scenario;

  /** The graph. */
  private final PiGraph graph;

  /** Map from Pi actors to their Repetition Vector value. */
  protected Map<AbstractVertex, Integer> graphBRV = new LinkedHashMap<>();

  /** Map of all parametersValues */
  protected Map<Parameter, Integer> parametersValues;

  /**
   * Instantiates a new static pi MM 2 SDF launcher.
   *
   * @param scenario
   *          the scenario
   * @param graph
   *          the graph
   */
  public StaticPiMM2SrDAGLauncher(final PreesmScenario scenario, final PiGraph graph) {
    this.scenario = scenario;
    this.graph = graph;
  }

  /**
   * Precondition: All.
   *
   * @return true if consistent, false else
   * @throws StaticPiMM2SrDAGException
   *           the static pi MM 2 SDF exception
   */
  public boolean computeBRV() throws StaticPiMM2SrDAGException {
    // Get all the available values for all the parameters
    this.parametersValues = getParametersValues();
    // Resolve all parameters
    resolveAllParameters(this.graph);

    return true;
  }

  /**
   * Precondition: All.
   *
   * @return the SDFGraph obtained by visiting graph
   * @throws StaticPiMM2SrDAGException
   *           the static pi MM 2 SDF exception
   */
  public MapperDAG launch() throws StaticPiMM2SrDAGException {
    MapperDAG result;

    // Visitor creating the SDFGraph
    StaticPiMM2SrDAGVisitor visitor;
    final PiGraphExecution execution = new PiGraphExecution(this.parametersValues);
    visitor = new StaticPiMM2SrDAGVisitor(execution);
    if (!visitor.doSwitch(this.graph)) {
      if (visitor.getResult() == null) {
        throw new StaticPiMM2SrDAGException("Cannot convert to Sr-DAG, top graph does not contain any actors.");
      }
    }

    result = visitor.getResult();
    return result;
  }

  /**
   * Gets the parameters values.
   *
   * @return the parameters values
   * @throws StaticPiMM2SrDAGException
   *           the static pi MM 2 SDF exception
   */
  private Map<Parameter, Integer> getParametersValues() throws StaticPiMM2SrDAGException {
    final Map<Parameter, Integer> result = new LinkedHashMap<>();

    final ParameterValueManager parameterValueManager = this.scenario.getParameterValueManager();
    final Set<ParameterValue> parameterValues = parameterValueManager.getParameterValues();
    for (final ParameterValue paramValue : parameterValues) {
      switch (paramValue.getType()) {
        case ACTOR_DEPENDENT:
          throw new StaticPiMM2SrDAGException("Parameter " + paramValue.getName() + " is depends on a configuration actor. It is thus impossible to use the"
              + " Static PiMM 2 SDF transformation. Try instead the Dynamic PiMM 2 SDF"
              + " transformation (id: org.ietr.preesm.experiment.pimm2sdf.PiMM2SDFTask)");
        case INDEPENDENT:
          try {
            final int value = Integer.parseInt(paramValue.getValue());
            result.put(paramValue.getParameter(), value);
            break;
          } catch (final NumberFormatException e) {
            // The expression associated to the parameter is an
            // expression (and not an constant int value).
            // Leave it as it is, it will be solved later.
            break;
          }
        default:
          break;
      }
    }

    return result;
  }

  /**
   * Parameters of a top graph must be visited before parameters of a subgraph, since the expression of ConfigurationInputInterface depends on the value of its
   * connected Parameter.
   *
   * @param p
   *          the p
   */
  @Override
  public Boolean caseParameter(Parameter p) {
    if (p.isConfigurationInterface()) {
      final ConfigInputInterface cii = (ConfigInputInterface) p;
      final ConfigInputPort graphPort = cii.getGraphPort();
      final Dependency incomingDependency = graphPort.getIncomingDependency();
      final ISetter setter = incomingDependency.getSetter();
      // Setter of an incoming dependency into a ConfigInputInterface must
      // be a parameter
      if (setter instanceof Parameter) {
        final Expression setterParam = ((Parameter) setter).getValueExpression();
        final Expression pExp = PiMMUserFactory.instance.createExpression();
        pExp.setExpressionString(setterParam.getExpressionString());
        cii.setExpression(pExp);
      }
    } else {
      // If there is only one value available for Parameter p, we can set
      // its
      if (this.parametersValues.containsKey(p)) {
        final Integer value = this.parametersValues.get(p);
        final Expression pExp = PiMMUserFactory.instance.createExpression();
        pExp.setExpressionString(value.toString());
        p.setExpression(pExp);
      }
    }
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitConfigInputInterface(org.ietr.preesm.experiment.model.pimm.ConfigInputInterface)
   */
  @Override
  public Boolean caseConfigInputInterface(final ConfigInputInterface cii) {
    final ConfigInputPort graphPort = cii.getGraphPort();
    final Dependency incomingDependency = graphPort.getIncomingDependency();
    final ISetter setter = incomingDependency.getSetter();
    // Setter of an incoming dependency into a ConfigInputInterface must be
    // a parameter
    if (setter instanceof Parameter) {
      final Expression pExp = PiMMUserFactory.instance.createExpression();
      pExp.setExpressionString(((Parameter) setter).getValueExpression().getExpressionString());
      cii.setExpression(pExp);
    }
    return true;
  }

  /**
   * The Class StaticPiMM2SDFException.
   */
  public void resolveAllParameters(final PiGraph graph) {
    for (Parameter p : graph.getParameters()) {
      doSwitch(p);
    }
    computeDerivedParameterValues(graph);
    for (final PiGraph g : graph.getChildrenGraphs()) {
      resolveAllParameters(g);
    }
  }

  /**
   * Set the value of parameters of a PiGraph when possible (i.e., if we have currently only one available value, or if we can compute the value)
   *
   * @param graph
   *          the PiGraph in which we want to set the values of parameters
   * @param execution
   *          the list of available values for each parameter
   */
  protected void computeDerivedParameterValues(final PiGraph graph) {
    // If there is no value or list of valuse for one Parameter, the value
    // of the parameter is derived (i.e., computed from other parameters
    // values), we can evaluate it (after the values of other parameters
    // have been fixed)
    for (final Parameter p : graph.getParameters()) {
      if (!this.parametersValues.containsKey(p)) {
        // Evaluate the expression wrt. the current values of the
        // parameters and set the result as new expression
        final Expression pExp = PiMMUserFactory.instance.createExpression();
        final Expression valueExpression = p.getValueExpression();
        final long evaluate = ExpressionEvaluator.evaluate(valueExpression);
        pExp.setExpressionString(Long.toString(evaluate));
        p.setExpression(pExp);
        try {
          final int value = Integer.parseInt(p.getExpression().getExpressionString());
          this.parametersValues.put(p, value);
        } catch (final NumberFormatException e) {
          break;
        }
      }
    }
  }

  /**
   * The Class StaticPiMM2SrDaGException.
   */
  public class StaticPiMM2SrDAGException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8272147472427685537L;

    /**
     * Instantiates a new static pi MM 2 SDF exception.
     *
     * @param message
     *          the message
     */
    public StaticPiMM2SrDAGException(final String message) {
      super(message);
    }
  }

}
