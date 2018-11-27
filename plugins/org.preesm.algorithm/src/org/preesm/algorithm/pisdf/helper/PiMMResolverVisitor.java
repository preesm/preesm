/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
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
package org.preesm.algorithm.pisdf.helper;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.nfunk.jep.JEP;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.ExpressionHolder;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 * @author farresti
 *
 */
public class PiMMResolverVisitor extends PiMMSwitch<Boolean> {

  /*
   * Map used to rapidly check if a parameter value has allready been resolved
   */
  final Map<Parameter, Long> parameterValues;

  public PiMMResolverVisitor(final Map<Parameter, Long> parameterValues) {
    this.parameterValues = parameterValues;
  }

  /**
   * Set the value of parameters of a PiGraph when possible (i.e., if we have currently only one available value, or if
   * we can compute the value)
   *
   * @param graph
   *          the PiGraph in which we want to set the values of parameters
   */
  private void computeDerivedParameterValues(final PiGraph graph, final Map<Parameter, Long> parameterValues) {
    // If there is no value or list of values for one Parameter, the value
    // of the parameter is derived (i.e., computed from other parameters
    // values), we can evaluate it (after the values of other parameters
    // have been fixed)
    for (final Parameter p : graph.getParameters()) {
      if (!parameterValues.containsKey(p)) {
        // Evaluate the expression wrt. the current values of the
        // parameters and set the result as new expression
        final Expression valueExpression = p.getValueExpression();
        final long evaluate = valueExpression.evaluate();
        p.setExpression(evaluate);
        parameterValues.put(p, evaluate);
      }
    }
  }

  private JEP initJep(final Map<String, Long> portValues) {
    final JEP jep = new JEP();
    if (portValues != null) {
      portValues.forEach(jep::addVariable);
    }
    // TODO move to JEP 3 and get rid of these
    jep.addStandardConstants();
    jep.addStandardFunctions();
    return jep;
  }

  private void parseJEP(final AbstractActor actor, final Map<String, Long> portValues) {
    // Init the JEP parser associated with the actor
    final JEP jepParser = initJep(portValues);
    // Iterate over all data ports of the actor and resolve their rates
    for (final DataPort dp : actor.getAllDataPorts()) {
      resolveExpression(dp, jepParser);
    }

    // Parse delays as well
    if (actor instanceof PiGraph) {
      for (final Delay d : ((PiGraph) actor).getDelays()) {
        resolveExpression(d, jepParser);
      }
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
  private void resolveExpression(final ExpressionHolder holder, final JEP jep) {
    final Expression expression = holder.getExpression();
    final long value = new JEPFastExpressionResolver(jep).doSwitch(expression);
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
    if (!p.isLocallyStatic()) {
      throw new PreesmException(
          "Parameter " + p.getName() + " is depends on a configuration actor. It is thus impossible to use the"
              + " Static PiMM 2 SDF transformation. Try instead the Dynamic PiMM 2 SDF"
              + " transformation (id: org.ietr.preesm.experiment.pimm2sdf.PiMM2SDFTask)");
    }
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
    final ISetter setter = incomingDependency.getSetter();
    // Setter of an incoming dependency into a ConfigInputInterface must be
    // a parameter
    if (setter instanceof Parameter) {
      // When we arrive here all upper graphs have been processed.
      // We can then directly evaluate parameter expression here.
      final Expression valueExpression = ((Parameter) setter).getValueExpression();
      final long evaluate = valueExpression.evaluate();
      cii.setExpression(evaluate);
      this.parameterValues.put(cii, evaluate);
    } else {
      throw new UnsupportedOperationException(
          "In a static PiMM graph, setter of an incomming dependency must be a parameter.");
    }
    return true;
  }

  @Override
  public Boolean caseExecutableActor(final ExecutableActor actor) {
    // Map that associate to every parameter of an acotr the corresponding value in the graph
    final Map<String, Long> portValues = new LinkedHashMap<>();
    // We have to fetch the corresponding parameter port for normal actors
    for (final Parameter p : actor.getInputParameters()) {
      final EList<ConfigInputPort> ports = actor.lookupConfigInputPortsConnectedWithParameter(p);
      for (ConfigInputPort port : ports) {
        portValues.put(port.getName(), this.parameterValues.get(p));
      }
    }
    parseJEP(actor, portValues);
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
    parseJEP(actor, portValues);
    return true;
  }

  @Override
  public Boolean casePiGraph(final PiGraph graph) {
    // Resolve input interfaces
    for (final ConfigInputInterface p : graph.getConfigInputInterfaces()) {
      doSwitch(p);
    }
    // Resolve locally static parameters
    for (final Parameter p : graph.getOnlyParameters()) {
      doSwitch(p);
    }

    // Finally, we derive parameter values that have not already been processed
    computeDerivedParameterValues(graph, this.parameterValues);

    // We can now resolve data port rates for this graph
    for (final AbstractActor actor : graph.getOnlyActors()) {
      doSwitch(actor);
    }

    // Deals with data ports of the graph
    // Map that associate to every parameter of an acotr the corresponding value in the graph
    final Map<String, Long> portValues = new LinkedHashMap<>();
    // We have to fetch the corresponding parameter port for normal actors
    // Port of a parameter may have a dependency to higher level parameter
    for (final Parameter p : graph.getInputParameters()) {
      final EList<ConfigInputPort> ports = graph.lookupConfigInputPortsConnectedWithParameter(p);
      for (ConfigInputPort port : ports) {
        portValues.put(port.getName(), this.parameterValues.get(p));
      }
    }
    parseJEP(graph, portValues);

    // Switch on child subgraphs
    for (final PiGraph g : graph.getChildrenGraphs()) {
      final PiMMResolverVisitor piMMResolverVisitor = new PiMMResolverVisitor(this.parameterValues);
      piMMResolverVisitor.doSwitch(g);
    }

    return true;
  }

}
