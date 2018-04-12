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

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.commons.lang3.time.StopWatch;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.ParameterValue;
import org.ietr.preesm.core.scenario.ParameterValueManager;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.expression.ExpressionEvaluator;
import org.ietr.preesm.experiment.model.factory.PiMMUserFactory;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.pimm.algorithm.math.LCMBasedBRV;
import org.ietr.preesm.pimm.algorithm.math.PiBRV;
import org.ietr.preesm.pimm.algorithm.math.PiMMHandler;
import org.ietr.preesm.pimm.algorithm.math.PiMMHandler.PiMMHandlerException;
import org.ietr.preesm.pimm.algorithm.math.TopologyBasedBRV;

/**
 * The Class StaticPiMM2SDFLauncher.
 */
public class StaticPiMM2SrDAGLauncher extends PiMMSwitch<Boolean> {

  /** The scenario. */
  private final PreesmScenario scenario;

  /** The graph. */
  private final PiGraph graph;

  /** The graph. */
  private PiMMHandler piHandler;

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
    this.piHandler = new PiMMHandler(graph);
  }

  private static void printRV(final Map<AbstractVertex, Integer> graphBRV) {
    for (final Map.Entry<AbstractVertex, Integer> rv : graphBRV.entrySet()) {
      List<String> hierarchyNames = new ArrayList<>();
      AbstractActor actor = (AbstractActor) rv.getKey();
      while (actor.getContainingGraph() != null) {
        actor = actor.getContainingGraph();
        hierarchyNames.add(actor.getName());
      }
      // Removes top graph name
      hierarchyNames.remove(actor.getName());
      String hierarchyName = String.join("_", Lists.reverse(hierarchyNames));
      if (!hierarchyName.isEmpty()) {
        hierarchyName += "_";
      }
      WorkflowLogger.getLogger().log(Level.INFO, hierarchyName + rv.getKey().getName() + " x" + Integer.toString(rv.getValue()));
    }
  }

  /**
   * Precondition: All.
   *
   * @return the SDFGraph obtained by visiting graph
   * @throws StaticPiMM2SrDAGException
   *           the static pi MM 2 SDF exception
   */
  public MapperDAG launch(int method) throws StaticPiMM2SrDAGException, PiMMHandlerException {

    StopWatch timer = new StopWatch();
    timer.start();
    // // Get all the available values for all the parameters
    // this.parametersValues = getParametersValues();
    // // Resolve all parameters
    // resolveAllParameters(this.graph);
    this.piHandler.resolveAllParameters();
    timer.stop();
    WorkflowLogger.getLogger().log(Level.INFO, "Parameters and rates evaluations: " + timer.toString() + "s.");
    // Compute BRV following the chosen method
    PiBRV piBRVAlgo;
    if (method == 0) {
      piBRVAlgo = new TopologyBasedBRV(this.piHandler);
    } else if (method == 1) {
      piBRVAlgo = new LCMBasedBRV(this.piHandler);
    } else {
      throw new StaticPiMM2SrDAGException("unexpected value for BRV method: [" + Integer.toString(method) + "]");
    }
    timer.reset();
    timer.start();
    piBRVAlgo.execute();
    this.graphBRV = piBRVAlgo.getBRV();
    timer.stop();
    WorkflowLogger.getLogger().log(Level.INFO, "Repetition vector computed in " + timer.toString() + "s.");
    printRV(this.graphBRV);
    // Visitor creating the SR-DAG
    // StaticPiMM2SrDAGVisitor visitor;
    // final PiGraphExecution execution = new PiGraphExecution(this.parametersValues);
    // visitor = new StaticPiMM2SrDAGVisitor(execution);
    // if (!visitor.doSwitch(this.graph)) {
    // if (visitor.getResult() == null) {
    // throw new StaticPiMM2SrDAGException("Cannot convert to Sr-DAG, top graph does not contain any actors.");
    // }
    // }

    return null;// visitor.getResult();
  }

  private void newResolveAllParameters(final PiGraph graph) throws StaticPiMM2SrDAGException {
    this.parametersValues = new LinkedHashMap<>();
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
            this.parametersValues.put(paramValue.getParameter(), value);
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
    // Evaluate remaining parameters of the top level graph.
    // These parameters are defined by expression.
    for (final Parameter p : graph.getParameters()) {
      if (!this.parametersValues.containsKey(p)) {
        // Evaluate the expression wrt. the current values of the
        // parameters and set the result as new expression
        final Expression pExp = PiMMUserFactory.instance.createExpression();
        final Expression valueExpression = p.getValueExpression();
        final long evaluate = ExpressionEvaluator.evaluate(valueExpression);
        pExp.setExpressionString(Long.toString(evaluate));
        p.setExpression(pExp);
        this.parametersValues.put(p, Integer.parseInt(pExp.getExpressionString()));
      }
    }
    // Now evaluate config input interfaces and data port rates for all hierarchy levels
    iterativeComputeDerivedParameterValues(graph);
  }

  /**
   * Gets the parameters values.
   *
   */
  public void iterativeComputeDerivedParameterValues(final PiGraph graph) {
    // We already resolved all static parameters we could.
    // Now, we resolve interfaces
    for (final ConfigInputInterface cii : graph.getConfigInputInterfaces()) {
      final ConfigInputPort graphPort = cii.getGraphPort();
      final Dependency incomingDependency = graphPort.getIncomingDependency();
      final ISetter setter = incomingDependency.getSetter();
      // Setter of an incoming dependency into a ConfigInputInterface must be
      // a parameter
      if (setter instanceof Parameter) {
        final Expression pExp = PiMMUserFactory.instance.createExpression();
        // When we arrive here all upper graphs have been processed.
        // We can then directly evaluate parameter expression here.
        final Expression valueExpression = ((Parameter) setter).getValueExpression();
        final String expressionString = valueExpression.getExpressionString();
        pExp.setExpressionString(expressionString);
        cii.setExpression(pExp);
        this.parametersValues.put((Parameter) cii, Integer.parseInt(expressionString));
      }
    }
    // We finally derive parameter values that have not already been processed
    computeDerivedParameterValues(graph);
    // We compute data port associated rates
    for (final Fifo f : graph.getFifos()) {
      int prod = (int) (ExpressionEvaluator.evaluate(f.getSourcePort().getPortRateExpression()));
      int cons = (int) (ExpressionEvaluator.evaluate(f.getTargetPort().getPortRateExpression()));
      f.getSourcePort().getExpression().setExpressionString(Integer.toString(prod));
      f.getTargetPort().getExpression().setExpressionString(Integer.toString(cons));
    }
    for (final PiGraph g : graph.getChildrenGraphs()) {
      iterativeComputeDerivedParameterValues(g);
    }
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
      // If there is only one value available for Parameter p, we can set it
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
   * Set the value of parameters of a PiGraph when possible (i.e., if we have currently only one available value, or if we can compute the value)
   *
   * @param graph
   *          the PiGraph in which we want to set the values of parameters
   */
  protected void computeDerivedParameterValues(final PiGraph graph) {
    // If there is no value or list of values for one Parameter, the value
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
          WorkflowLogger.getLogger().log(Level.INFO, "TROLOLOLOLOLOLOLO.");
          break;
        }
      }
    }
  }

  /**
   * Set the value of parameters for a PiGraph and all of its sub-graph.
   *
   * @param graph
   *          the PiGraph in which we want to set the values of parameters
   */
  protected void resolveAllParameters(final PiGraph graph) {
    StopWatch timer = new StopWatch();
    timer.start();
    for (Parameter p : graph.getParameters()) {
      doSwitch(p);
    }
    computeDerivedParameterValues(graph);
    timer.stop();
    WorkflowLogger.getLogger().log(Level.INFO, "Parameter evaluation: " + timer.toString() + "s.");
    timer.reset();
    timer.start();
    // Resolve all dataport expression
    for (final Fifo f : graph.getFifos()) {
      int prod = (int) (ExpressionEvaluator.evaluate(f.getSourcePort().getPortRateExpression()));
      int cons = (int) (ExpressionEvaluator.evaluate(f.getTargetPort().getPortRateExpression()));
      f.getSourcePort().getExpression().setExpressionString(Integer.toString(prod));
      f.getTargetPort().getExpression().setExpressionString(Integer.toString(cons));
    }
    timer.stop();
    WorkflowLogger.getLogger().log(Level.INFO, "Rate evaluation: " + timer.toString() + "s.");

    for (final PiGraph g : graph.getChildrenGraphs()) {
      resolveAllParameters(g);
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
