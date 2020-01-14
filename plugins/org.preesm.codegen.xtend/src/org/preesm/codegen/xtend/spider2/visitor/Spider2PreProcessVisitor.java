package org.preesm.codegen.xtend.spider2.visitor;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.Parameterizable;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.PiSDFRefinement;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 * The Class Spider2PreProcessVisitor.
 */
public class Spider2PreProcessVisitor extends PiMMSwitch<Boolean> {
  /** The function map. */
  private final Map<AbstractActor, Integer> functionMap = new LinkedHashMap<>();

  /** The unique graph set **/
  private final Set<PiGraph> uniqueGraphSet = new HashSet<>();

  /** The graph to static parameters map */
  private final Map<PiGraph, List<Parameter>> staticParametersMap = new LinkedHashMap<>();

  /** The graph to static dependent parameters map */
  private final Map<PiGraph, Set<Parameter>> staticDependentParametersMap = new LinkedHashMap<>();

  /** The graph to dynamic parameters map */
  private final Map<PiGraph, List<Parameter>> dynamicParametersMap = new LinkedHashMap<>();

  /** The graph to dynamic dependent parameters map */
  private final Map<PiGraph, Set<Parameter>> dynamicDependentParametersMap = new LinkedHashMap<>();

  /**
   * Accessors
   */
  public Map<AbstractActor, Integer> getFunctionMap() {
    return this.functionMap;
  }

  public Set<PiGraph> getUniqueGraphSet() {
    return this.uniqueGraphSet;
  }

  public List<Parameter> getStaticParameters(final PiGraph graph) {
    return staticParametersMap.get(graph);
  }

  public Set<Parameter> getStaticDependentParameters(final PiGraph graph) {
    return staticDependentParametersMap.get(graph);
  }

  public List<Parameter> getDynamicParameters(final PiGraph graph) {
    return dynamicParametersMap.get(graph);
  }

  public Set<Parameter> getDynamicDependentParameters(final PiGraph graph) {
    return dynamicDependentParametersMap.get(graph);
  }

  /**
   * Private methods
   */

  /**
   * Build an ordered Set of Parameter enforcing the dependency tree structure of the PiMM.
   * 
   * @param initList
   *          Seed list corresponding to root parameters.
   * @param paramPoolList
   *          Pool of parameter to use to sort (will be empty after the function call).
   * @return set of parameter.
   */
  private Set<Parameter> getOrderedDependentParameter(final List<Parameter> initList, List<Parameter> paramPoolList) {
    final Set<Parameter> dependentParametersSet = new LinkedHashSet<>(initList);
    while (!paramPoolList.isEmpty()) {
      /* Get only the parameter that can be added to the current stage due to their dependencies */
      final List<Parameter> nextParamsToAddList = paramPoolList
          .stream().filter(x -> x.getInputDependentParameters().stream()
              .filter(in -> dependentParametersSet.contains(in)).count() == x.getInputDependentParameters().size())
          .collect(Collectors.toList());
      dependentParametersSet.addAll(nextParamsToAddList);
      paramPoolList.removeAll(nextParamsToAddList);
    }
    /* Remove init list from the set */
    dependentParametersSet.removeAll(initList);
    return dependentParametersSet;
  }

  /**
   * Switch overrides
   */

  @Override
  public Boolean caseAbstractActor(final AbstractActor aa) {
    return true;
  }

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

    if (!staticParametersMap.containsKey(graph)) {
      /* Extract the static parameters */
      final List<Parameter> restrictedStaticParametersList = graph.getParameters().stream()
          .filter(x -> !x.isDependent() && x.isLocallyStatic() && !x.isConfigurationInterface())
          .collect(Collectors.toList());
      staticParametersMap.put(graph, restrictedStaticParametersList);

      /* Extract the static dependent parameters */
      final List<Parameter> staticParametersList = graph.getParameters().stream()
          .filter(x -> !x.isDependent() && x.isLocallyStatic()).collect(Collectors.toList());
      final List<Parameter> staticParameterPool = graph.getParameters().stream()
          .filter(x -> x.isDependent() && x.isLocallyStatic()).collect(Collectors.toList());
      staticDependentParametersMap.put(graph, getOrderedDependentParameter(staticParametersList, staticParameterPool));

      /* Extract the dynamic parameters */
      final List<Parameter> dynamicParametersList = graph.getParameters().stream().filter(x -> x.isConfigurable())
          .collect(Collectors.toList());
      dynamicParametersMap.put(graph, dynamicParametersList);

      /* Extract the dynamic dependent parameters */
      final List<Parameter> dynamicParameterPool = graph.getParameters().stream()
          .filter(x -> x.isDependent() && !x.isLocallyStatic()).collect(Collectors.toList());
      dynamicDependentParametersMap.put(graph,
          getOrderedDependentParameter(dynamicParametersList, dynamicParameterPool));
    }

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
  public Boolean caseBroadcastActor(final BroadcastActor ba) {
    caseAbstractActor(ba);
    return true;
  }

  @Override
  public Boolean caseJoinActor(final JoinActor ja) {
    caseAbstractActor(ja);
    return true;
  }

  @Override
  public Boolean caseForkActor(final ForkActor fa) {
    caseAbstractActor(fa);
    return true;
  }

  @Override
  public Boolean caseRoundBufferActor(final RoundBufferActor rba) {
    caseAbstractActor(rba);
    return true;
  }

  @Override
  public Boolean caseInitActor(final InitActor rba) {
    caseAbstractActor(rba);
    return true;
  }

  @Override
  public Boolean caseEndActor(final EndActor rba) {
    caseAbstractActor(rba);
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
