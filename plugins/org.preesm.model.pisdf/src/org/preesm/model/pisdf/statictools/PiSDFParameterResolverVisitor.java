/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019 - 2020)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2019 - 2020)
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
/**
 *
 */
package org.preesm.model.pisdf.statictools;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.ExpressionEvaluationException;
import org.preesm.commons.math.JEPWrapper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.ExpressionHolder;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PeriodicElement;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 * @author farresti
 *
 */
public class PiSDFParameterResolverVisitor extends PiMMSwitch<Boolean> {

  /*
   * Map used to rapidly check if a parameter value has already been resolved
   */
  private final Map<Parameter, Long> parameterValues;

  public PiSDFParameterResolverVisitor() {
    this(new LinkedHashMap<>());
  }

  /**
   * private constructor for initializing visit of children subgraphs
   */
  private PiSDFParameterResolverVisitor(final Map<Parameter, Long> parameterValues) {
    this.parameterValues = parameterValues;
  }

  private static void resolveActorPorts(final AbstractActor actor, final Map<String, Long> paramValues) {
    // Init the JEP parser associated with the actor
    // Iterate over all data ports of the actor and resolve their rates
    for (final DataPort dp : actor.getAllDataPorts()) {
      try {
        resolveExpression(dp, paramValues);
      } catch (final ExpressionEvaluationException e) {
        throw new PreesmRuntimeException(
            "Could not evaluate port [" + dp.getName() + "] of actor [" + actor.getName() + "]", e);
      }
    }

    // Parse delays as well
    if (actor instanceof final PiGraph piGraph) {
      piGraph.getDelays().forEach(d -> resolveExpression(d, paramValues));
    }
  }

  /**
   * Fast evaluator for data port rate expression.<br>
   * If rate expression is a constant, then parsing is ignored since it is already done. <br>
   * This implementation uses benefit of the fact that the parser is initialized once for a given actor.
   *
   * @param holder
   *          the expression to evaluate
   * @param actorParser
   *          parser of the actor containing the port
   * @throws PiMMHelperException
   *           the PiMMHandlerException exception
   */
  private static void resolveExpression(final ExpressionHolder holder, final Map<String, Long> paramValues) {
    final Expression expression = holder.getExpression();
    final long value = JEPWrapper.evaluate(expression.getExpressionAsString(), paramValues);
    holder.setExpression(value);
  }

  /**
   * Static parameters. There should be no problem of order with ConfigInputInterface as the interfaces depending on
   * local parameters are located in lower levels of hierarchy.
   *
   * @param p
   *          the p
   */
  @Override
  public Boolean caseParameter(final Parameter p) {
    if (!this.parameterValues.containsKey(p)) {
      // Evaluate the expression wrt. the current values of the
      // parameters and set the result as new expression
      final Expression valueExpression = p.getValueExpression();
      final long value = valueExpression.evaluate();
      p.setExpression(value);
      this.parameterValues.put(p, value);
    }

    return true;
  }

  /*
   * ConfigInputInterface parameters resolver.
   *
   */
  @Override
  public Boolean caseConfigInputInterface(final ConfigInputInterface cii) {
    final ConfigInputPort graphPort = cii.getGraphPort();
    final Dependency incomingDependency = graphPort.getIncomingDependency();
    Parameter paramToEvaluate = cii;
    if (incomingDependency == null) {
      PreesmLogger.getLogger().warning(() -> cii.eContainer() + " has a config input port without incoming dependency: "
          + graphPort.getName() + "\nDefault value is used instead.");
    } else {
      // regular case
      final ISetter setter = incomingDependency.getSetter();
      // Setter of an incoming dependency into a ConfigInputInterface must be
      // a parameter
      if (!(setter instanceof Parameter)) {
        throw new UnsupportedOperationException(
            "In a static PiMM graph, setter of an incomming dependency must be a parameter.");
      }
      paramToEvaluate = ((Parameter) setter);
    }
    // When we arrive here all upper graphs have been processed.
    // We can then directly evaluate parameter expression here.
    final Expression valueExpression = paramToEvaluate.getValueExpression();
    final long evaluate = valueExpression.evaluate();
    cii.setExpression(evaluate);
    this.parameterValues.put(cii, evaluate);
    return true;
  }

  @Override
  public Boolean caseExecutableActor(final ExecutableActor actor) {
    // Map that associate to every parameter of an acotr the corresponding value in the graph
    final Map<String, Long> portValues = new LinkedHashMap<>();
    // We have to fetch the corresponding parameter port for normal actors
    for (final Parameter p : actor.getInputParameters()) {
      final EList<ConfigInputPort> ports = actor.lookupConfigInputPortsConnectedWithParameter(p);
      for (final ConfigInputPort port : ports) {
        portValues.put(port.getName(), this.parameterValues.get(p));
      }
    }
    resolveActorPorts(actor, portValues);
    // Resolve actor period
    if (actor instanceof final PeriodicElement pe) {
      resolveExpression(pe, portValues);
    }
    return true;
  }

  @Override
  public Boolean caseInterfaceActor(final InterfaceActor actor) {
    return caseOtherActors(actor);
  }

  @Override
  public Boolean caseDelayActor(final DelayActor actor) {
    return caseOtherActors(actor);
  }

  private Boolean caseOtherActors(final AbstractActor actor) {
    // Map that associate to every parameter of an acotr the corresponding value in the graph
    final Map<String, Long> portValues = new LinkedHashMap<>();
    // Data interface actors do not have parameter ports, thus expression is directly graph parameter
    for (final Parameter p : actor.getInputParameters()) {
      portValues.put(p.getName(), this.parameterValues.get(p));
    }
    resolveActorPorts(actor, portValues);
    return true;
  }

  @Override
  public Boolean casePiGraph(final PiGraph graph) {
    if (!graph.isLocallyStatic()) {
      throw new PreesmRuntimeException("PiGraph " + graph.getName()
          + " has configuration actors. It is thus impossible to use the" + " Static PiMM to SRDAG transformation.");
    }

    // Resolve input interfaces
    for (final ConfigInputInterface p : graph.getConfigInputInterfaces()) {
      doSwitch(p);
    }
    // Resolve locally static parameters (neither ConfigInputInterface nor ConfigOutputInterface)
    // Thus, ConfigOutputInterface are never evaluated, but we already restrict this analysis to
    // locally static graphs, which must not depend on ConfigOutputInterface/Port by definition.
    for (final Parameter p : graph.getOnlyParameters()) {
      doSwitch(p);
    }

    // Resolve graph period
    graph.setExpression(graph.getPeriod().evaluate());

    // We can now resolve data port rates for this graph
    for (final AbstractActor actor : graph.getOnlyActors()) {
      doSwitch(actor);
    }

    // Deals with data ports of the graph
    // Map that associate to every parameter of an actor the corresponding value in the graph
    final Map<String, Long> portValues = new LinkedHashMap<>();
    // We have to fetch the corresponding parameter port for normal actors
    // Port of a parameter may have a dependency to higher level parameter
    for (final Parameter p : graph.getInputParameters()) {
      final EList<ConfigInputPort> ports = graph.lookupConfigInputPortsConnectedWithParameter(p);
      for (final ConfigInputPort port : ports) {
        portValues.put(port.getName(), this.parameterValues.get(p));
      }
    }
    resolveActorPorts(graph, portValues);

    // Switch on child subgraphs
    for (final PiGraph g : graph.getChildrenGraphs()) {
      final PiSDFParameterResolverVisitor piMMResolverVisitor = new PiSDFParameterResolverVisitor(this.parameterValues);
      piMMResolverVisitor.doSwitch(g);
    }

    return true;
  }

}
