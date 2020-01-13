package org.preesm.codegen.xtend.spider2.visitor;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.Parameterizable;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.PiSDFRefinement;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 * The Class Spider2PreProcessVisitor.
 */
public class Spider2PreProcessVisitor extends PiMMSwitch<Boolean> {
  /** The function map. */
  private final Map<AbstractActor, Integer> functionMap = new LinkedHashMap<>();

  /** The unique graph set **/
  private final Set<PiGraph> uniqueGraphSet = new HashSet<>();

  /**
   * Accessors
   */
  public Map<AbstractActor, Integer> getFunctionMap() {
    return this.functionMap;
  }

  public Set<PiGraph> getUniqueGraphSet() {
    return this.uniqueGraphSet;
  }

  /**
   * Switch overrides
   */

  @Override
  public Boolean caseActor(final Actor actor) {
    if (actor.getRefinement() == null) {
      throw new PreesmRuntimeException("Actor [" + actor.getName() + "] does not have correct refinement.");
    } else {
      this.functionMap.put(actor, this.functionMap.size());
    }

    for (final DataInputPort dip : actor.getDataInputPorts()) {
      actor.getDataInputPorts().indexOf(dip);
    }
    return true;
  }

  @Override
  public Boolean casePiGraph(final PiGraph graph) {
    /* Insert the pigraph in the set */
    this.uniqueGraphSet.add(graph);

    /* Go through the graph */
    caseAbstractActor(graph);
    for (final AbstractActor a : graph.getActors()) {
      doSwitch(a);
    }
    for (final Parameter p : graph.getParameters()) {
      doSwitch(p);
    }
    return true;
  }

  @Override
  public Boolean caseParameter(final Parameter p) {
    // Fix currentAbstractVertexName
    // this.currentAbstractVertexName = "param_" + p.getName();
    // Visit configuration input ports to fill cfgInPortMap
    // caseConfigurable(p);
    // Fill the setterMap
    // this.setterMap.put(p, this.currentAbstractVertexName);
    return true;
  }

  @Override
  public Boolean caseExpression(final Expression e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseFifo(final Fifo f) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseISetter(final ISetter is) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseParameterizable(final Parameterizable p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean casePort(final Port p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDataPort(final DataPort p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean casePiSDFRefinement(final PiSDFRefinement pisdfRefinement) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseFunctionArgument(final FunctionArgument functionParameter) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseFunctionPrototype(final FunctionPrototype functionPrototype) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseCHeaderRefinement(final CHeaderRefinement hRefinement) {
    throw new UnsupportedOperationException();
  }
}
