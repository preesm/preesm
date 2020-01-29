package org.preesm.codegen.xtend.spider2.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.preesm.codegen.xtend.spider2.utils.Spider2CodegenActor;
import org.preesm.codegen.xtend.spider2.utils.Spider2CodegenCluster;
import org.preesm.codegen.xtend.spider2.utils.Spider2CodegenEdge;
import org.preesm.codegen.xtend.spider2.utils.Spider2CodegenPE;
import org.preesm.codegen.xtend.spider2.utils.Spider2CodegenPrototype;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.Parameterizable;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.PiSDFRefinement;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.PiMMSwitch;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComNode;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.Link;

/**
 * The Class Spider2PreProcessVisitor.
 */
public class Spider2PreProcessVisitor extends PiMMSwitch<Boolean> {
  /** The function map. */
  private final Map<AbstractActor, Integer> functionMap = new LinkedHashMap<>();

  /** The unique graph set **/
  private final Set<PiGraph> uniqueGraphSet = new HashSet<>();

  /** The unique graph name set */
  private final Set<String> uniqueGraphNameSet = new HashSet<>();

  /** The graph to static parameters map */
  private final Map<PiGraph, List<Parameter>> staticParametersMap = new LinkedHashMap<>();

  /** The graph to static dependent parameters map */
  private final Map<PiGraph, Set<Parameter>> staticDependentParametersMap = new LinkedHashMap<>();

  /** The graph to dynamic parameters map */
  private final Map<PiGraph, List<Parameter>> dynamicParametersMap = new LinkedHashMap<>();

  /** The graph to dynamic dependent parameters map */
  private final Map<PiGraph, Set<Parameter>> dynamicDependentParametersMap = new LinkedHashMap<>();

  /** The graph to edge map */
  private final Map<PiGraph, Set<Spider2CodegenEdge>> edgeMap = new LinkedHashMap<>();

  /** The graph to subgraph map */
  private final Map<PiGraph, Set<PiGraph>> subgraphMap = new LinkedHashMap<>();

  /** The graph to actors map */
  private final Map<PiGraph, Set<Spider2CodegenActor>> actorsMap = new LinkedHashMap<>();

  /** The unique refinement loop List */
  private final List<CHeaderRefinement> uniqueLoopHeaderList = new ArrayList<>();

  /** The unique prototype loop List */
  private final List<Spider2CodegenPrototype> uniqueLoopProtoypeList = new ArrayList<>();

  /** The actor to function loop prototype indices */
  private final Map<AbstractActor, Integer> actorToLoopFctMap = new LinkedHashMap<>();

  /** The scenario */
  private final Scenario scenario;

  /** Map core types to core type indexes */
  private final Map<Component, Integer>                 coreTypesIds     = new LinkedHashMap<>();
  private final Map<Component, Integer>                 coreCountPerType = new LinkedHashMap<>();
  private final Map<Component, List<ComponentInstance>> peTypeToCores    = new LinkedHashMap<>();

  /** The core ids. */
  private final Map<ComponentInstance, Integer> coreIds = new LinkedHashMap<>();

  /* Map timing strings to actors */
  private final Map<AbstractActor, Map<Component, String>> timings = new LinkedHashMap<>();

  /** The constraints. */
  private final Map<AbstractActor, Set<ComponentInstance>> constraints = new LinkedHashMap<>();

  /** The clusters */
  private final List<Spider2CodegenCluster> clusterList = new ArrayList<>();

  public Spider2PreProcessVisitor(final Scenario scenario) {
    this.scenario = scenario;

    /* Generate the cluster list */
    generateClusterList();
  }

  /**
   * Accessors
   */
  public Map<AbstractActor, Integer> getFunctionMap() {
    return this.functionMap;
  }

  public Set<PiGraph> getUniqueGraphSet() {
    return this.uniqueGraphSet;
  }

  public Set<String> getUniqueGraphNameSet() {
    return this.uniqueGraphNameSet;
  }

  public List<Parameter> getStaticParameters(final PiGraph graph) {
    return this.staticParametersMap.get(graph);
  }

  public Set<Parameter> getStaticDependentParameters(final PiGraph graph) {
    return this.staticDependentParametersMap.get(graph);
  }

  public List<Parameter> getDynamicParameters(final PiGraph graph) {
    return this.dynamicParametersMap.get(graph);
  }

  public Set<Parameter> getDynamicDependentParameters(final PiGraph graph) {
    return this.dynamicDependentParametersMap.get(graph);
  }

  public Set<Spider2CodegenEdge> getEdgeSet(final PiGraph graph) {
    return this.edgeMap.get(graph);
  }

  public Set<PiGraph> getSubgraphSet(final PiGraph graph) {
    return this.subgraphMap.get(graph);
  }

  public Set<Spider2CodegenActor> getActorSet(final PiGraph graph) {
    return this.actorsMap.get(graph);
  }

  public Integer getLoopFctIndex(final AbstractActor actor) {
    return this.actorToLoopFctMap.get(actor);
  }

  public List<Spider2CodegenPrototype> getUniqueLoopPrototypeList() {
    return this.uniqueLoopProtoypeList;
  }

  public List<CHeaderRefinement> getUniqueLoopHeaderList() {
    return this.uniqueLoopHeaderList;
  }

  public List<Spider2CodegenCluster> getClusterList() {
    return this.clusterList;
  }

  public String getMainPEName() {
    final Design design = this.scenario.getDesign();
    ComponentInstance mainOperator = this.scenario.getSimulationInfo().getMainOperator();
    if (mainOperator == null) {
      final List<ComponentInstance> orderedOperators = design.getOrderedOperatorComponentInstances();
      if (orderedOperators.isEmpty()) {
        throw new PreesmRuntimeException("no operator found in the scenario");
      }
      /* Warning */
      PreesmLogger.getLogger().log(Level.WARNING,
          () -> "No Main Operator selected in scenario, " + orderedOperators.get(0) + " used by default");
      mainOperator = orderedOperators.get(0);
    }
    final String name = mainOperator.getInstanceName();
    for (final Spider2CodegenCluster cluster : this.clusterList) {
      for (final Spider2CodegenPE pe : cluster.getProcessingElements()) {
        if (pe.getName().equals(name)) {
          return cluster.getName() + name;
        }
      }
    }
    return name;
  }

  void generateClusterList() {
    final Design design = this.scenario.getDesign();
    final Map<ComponentInstance, List<ComponentInstance>> clusters = new HashMap<>();
    for (final Link link : design.getLinks()) {
      final ComponentInstance source = link.getSourceComponentInstance();
      final ComponentInstance target = link.getDestinationComponentInstance();
      if (source.getComponent() instanceof ComNode) {
        if (!clusters.containsKey(source)) {
          clusters.put(source, new ArrayList<>());
        }
        clusters.get(source).add(target);
      } else if (target.getComponent() instanceof ComNode) {
        if (!clusters.containsKey(target)) {
          clusters.put(target, new ArrayList<>());
        }
        clusters.get(target).add(source);
      }
    }
    for (final Entry<ComponentInstance, List<ComponentInstance>> entry : clusters.entrySet()) {
      this.clusterList.add(new Spider2CodegenCluster(entry.getKey(), entry.getValue()));
    }

    // int coreTypeId = 0;
    // for (final Component coreType : design.getOperatorComponents()) {
    // this.coreTypesIds.put(coreType, coreTypeId++);
    // // Link the number of cores associated to each core type
    // final EList<Component> components = design.getComponentHolder().getComponents();
    // for (final Component c : components) {
    // if (c.equals(coreType)) {
    // final List<ComponentInstance> instances = c.getInstances();
    // this.coreCountPerType.put(coreType, instances.size());
    // this.peTypeToCores.put(coreType, instances);
    // }
    // }
    // }
    // final ComponentInstance com = design.getCommunicationComponentInstances().get(0);
    //
    // ComponentInstance mainOperator = this.scenario.getSimulationInfo().getMainOperator();
    // final List<ComponentInstance> orderedOperators = design.getOrderedOperatorComponentInstances();
    // if (mainOperator == null) {
    // /* Warning */
    // PreesmLogger.getLogger().log(Level.WARNING,
    // () -> "No Main Operator selected in scenario, " + orderedOperators.get(0) + " used by default");
    // }
    // this.coreIds.put(mainOperator, 0);
    // int coreId = 1;
    // for (final ComponentInstance core : orderedOperators) {
    // if (!core.equals(mainOperator)) {
    // this.coreIds.put(core, coreId++);
    // }
    // }
    //
    // /* Generate timings */
    // final PiGraph mainGraph = this.scenario.getAlgorithm();
    // for (final AbstractActor actor : mainGraph.getAllActors()) {
    // this.timings.put(actor, new LinkedHashMap<Component, String>());
    // if (this.scenario.getTimings().getActorTimings().containsKey(actor)) {
    // /* Fetch user defined timings */
    // final EMap<Component, String> listTimings = this.scenario.getTimings().getActorTimings().get(actor);
    // for (Entry<Component, String> e : listTimings) {
    // this.timings.get(actor).put(e.getKey(), e.getValue());
    // }
    // } else {
    // /* Add default timing */
    // for (final Component coreType : this.coreTypesIds.keySet()) {
    // if (!this.timings.get(actor).containsKey(coreType)) {
    // this.timings.get(actor).put(coreType, Integer.toString(ScenarioConstants.DEFAULT_TIMING_TASK.getValue()));
    // }
    // }
    // }
    //
    // /* Generate constraints */
    // for (final Entry<ComponentInstance, EList<AbstractActor>> cg : this.scenario.getConstraints()
    // .getGroupConstraints()) {
    // for (final AbstractActor aa : cg.getValue()) {
    // if (this.constraints.get(aa) == null) {
    // this.constraints.put(aa, new LinkedHashSet<ComponentInstance>());
    // }
    // final ComponentInstance core = cg.getKey();
    // this.constraints.get(aa).add(core);
    // }
    // }
    // }
  }

  /**
   * Switch overrides
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
   * Extracts the parameters of the Graph in the different maps.
   * 
   * @param graph
   *          The graph to evaluate
   */
  private void extractParameters(final PiGraph graph) {
    if (staticParametersMap.containsKey(graph)) {
      return;
    }
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
    dynamicDependentParametersMap.put(graph, getOrderedDependentParameter(dynamicParametersList, dynamicParameterPool));
  }

  /**
   * Insert one dynamic parameter if a config vertex set multiple parameters with one output port
   * 
   * @param graph
   *          The graph to evaluate
   */
  private void insertDynamicParameters(final PiGraph graph) {
    for (final AbstractActor actor : graph.getActors().stream().filter(x -> !x.getConfigOutputPorts().isEmpty())
        .collect(Collectors.toList())) {
      for (final ConfigOutputPort cop : actor.getConfigOutputPorts()) {
        if (cop.getOutgoingDependencies().size() > 1) {
          /* Instantiate the new parameter */
          final Parameter param = PiMMUserFactory.instance.createParameter();
          param.setExpression("0");
          param.setName(cop.getName());
          graph.addParameter(param);
          /* Change the old dependencies setter */
          final List<Dependency> outgoinDependencies = cop.getOutgoingDependencies();
          while (!outgoinDependencies.isEmpty()) {
            final Dependency dependency = outgoinDependencies.get(0);
            final Parameter depParameter = ((Parameter) (dependency.getGetter().eContainer()));
            depParameter.setExpression(param.getName());
            dependency.setSetter(param);
          }
          /* Instantiate the new dependency */
          cop.getOutgoingDependencies().clear();
          final Dependency dependency = PiMMUserFactory.instance.createDependency();
          final ConfigInputPort iCfgPort = PiMMUserFactory.instance.createConfigInputPort();
          param.getConfigInputPorts().add(iCfgPort);
          dependency.setSetter(cop);
          dependency.setGetter(iCfgPort);
        }
      }
    }
  }

  private long getRealSourcePortIx(final AbstractActor actor, final DataOutputPort sourcePort) {
    if (actor instanceof Actor) {
      final Actor a = (Actor) (actor);
      final CHeaderRefinement refinement = (CHeaderRefinement) (a.getRefinement());
      final FunctionPrototype proto = refinement.getLoopPrototype();
      final List<FunctionArgument> args = proto.getOutputArguments();
      final List<FunctionArgument> matchArgs = args.stream().filter(x -> x.getName().equals(sourcePort.getName()))
          .collect(Collectors.toList());
      if (matchArgs.size() != 1) {
        throw new PreesmRuntimeException("Did not find match for output port [" + sourcePort.getName()
            + "] in the function [" + proto.getName() + "].");
      }
      return args.indexOf(matchArgs.get(0));
    }
    return actor.getDataOutputPorts().indexOf(sourcePort);
  }

  private long getRealSinkPortIx(final AbstractActor actor, final DataInputPort targetPort) {
    if (actor instanceof Actor) {
      final Actor a = (Actor) (actor);
      final CHeaderRefinement refinement = (CHeaderRefinement) (a.getRefinement());
      final FunctionPrototype proto = refinement.getLoopPrototype();
      final List<FunctionArgument> args = proto.getInputArguments();
      final List<FunctionArgument> matchArgs = args.stream().filter(x -> x.getName().equals(targetPort.getName()))
          .collect(Collectors.toList());
      if (matchArgs.size() != 1) {
        throw new PreesmRuntimeException("Did not find match for input port [" + targetPort.getName()
            + "] in the function [" + proto.getName() + "].");
      }
      return args.indexOf(matchArgs.get(0));
    }
    return actor.getDataInputPorts().indexOf(targetPort);
  }

  /**
   * Extracts the edges of the Graph in the edgeMap.
   * 
   * @param graph
   *          The graph to evaluate
   */
  private void extractEdges(final PiGraph graph) {
    if (edgeMap.containsKey(graph)) {
      return;
    }
    Set<Spider2CodegenEdge> edgeSet = new LinkedHashSet<>();
    for (final Fifo fifo : graph.getFifos()) {
      /* Retrieve source information */
      final DataOutputPort sourcePort = fifo.getSourcePort();
      final AbstractActor source = sourcePort.getContainingActor();
      final long sourceIx = getRealSourcePortIx(source, sourcePort);
      /* We need to substitute the real parameter name in the expression */
      String sourceRateExpression = "(" + sourcePort.getExpression().getExpressionAsString() + ")";
      for (final ConfigInputPort iCfg : source.getConfigInputPorts()) {
        if (sourceRateExpression.matches(".*?\\b" + iCfg.getName() + "\\b.*?")) {
          final String realName = ((Parameter) (iCfg.getIncomingDependency().getSetter())).getName();
          sourceRateExpression = sourceRateExpression.replace(iCfg.getName(), realName);
        }
      }
      /* We need to add the size of the FIFO */
      sourceRateExpression += " * " + this.scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifo.getType());

      /* Retrieve sink information */
      final DataInputPort targetPort = fifo.getTargetPort();
      final AbstractActor sink = targetPort.getContainingActor();
      final long sinkIx = getRealSinkPortIx(sink, targetPort);
      /* We need to substitute the real parameter name in the expression */
      String sinkRateExpression = "(" + targetPort.getExpression().getExpressionAsString() + ")";
      for (final ConfigInputPort iCfg : sink.getConfigInputPorts()) {
        if (sinkRateExpression.matches(".*?\\b" + iCfg.getName() + "\\b.*?")) {
          final String realName = ((Parameter) (iCfg.getIncomingDependency().getSetter())).getName();
          sinkRateExpression = sinkRateExpression.replace(iCfg.getName(), realName);
        }
      }
      /* We need to add the size of the FIFO */
      sinkRateExpression += " * " + this.scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifo.getType());

      /* Add the edge to the edge Set */
      final Spider2CodegenEdge edge = new Spider2CodegenEdge(source, sourceIx, sourceRateExpression, sink, sinkIx,
          sinkRateExpression);
      edge.setDelay(fifo.getDelay());
      edgeSet.add(edge);
    }
    edgeMap.put(graph, edgeSet);
  }

  private void extractActors(final PiGraph graph) {
    this.actorsMap.put(graph, new HashSet<>());
    for (final AbstractActor actor : graph.getOnlyActors()) {
      if (!(actor instanceof DelayActor) && !(actor instanceof InterfaceActor)) {
        doSwitch(actor);
      }
    }
  }

  @Override
  public Boolean casePiGraph(final PiGraph graph) {
    /* Insert the pigraph in the set */
    this.uniqueGraphSet.add(graph);

    /* Insert one dynamic parameter if a config vertex set multiple parameters with one output port */
    insertDynamicParameters(graph);

    /* Extract parameters */
    if (!this.staticParametersMap.containsKey(graph)) {
      extractParameters(graph);
    }

    /* Extract edges */
    if (!this.edgeMap.containsKey(graph)) {
      extractEdges(graph);
    }

    /* Extract the actors */
    extractActors(graph);

    /* Go through the subgraphs */
    this.subgraphMap.put(graph, new HashSet<>());
    for (final PiGraph subgraph : graph.getChildrenGraphs()) {
      final String originalGraphFileName = subgraph.getPiGraphName();
      this.subgraphMap.get(graph).add(subgraph);
      if (!this.uniqueGraphNameSet.contains(originalGraphFileName)) {
        this.uniqueGraphNameSet.add(originalGraphFileName);
        casePiGraph(subgraph);
      }
    }
    return true;
  }

  @Override
  public Boolean caseActor(final Actor actor) {
    this.actorsMap.get(actor.getContainingPiGraph()).add(new Spider2CodegenActor("NORMAL", actor));
    if (!(actor.getRefinement() instanceof CHeaderRefinement)) {
      PreesmLogger.getLogger().warning("Actor [" + actor.getName() + "] doesn't have correct refinement.");
    } else {
      CHeaderRefinement refinement = (CHeaderRefinement) (actor.getRefinement());
      if (!this.uniqueLoopHeaderList.contains(refinement)) {
        this.uniqueLoopHeaderList.add(refinement);
        this.uniqueLoopProtoypeList.add(new Spider2CodegenPrototype(refinement));
      }
      this.actorToLoopFctMap.put(actor, this.uniqueLoopHeaderList.indexOf(refinement));
    }
    return true;
  }

  @Override
  public Boolean caseBroadcastActor(final BroadcastActor ba) {
    this.actorsMap.get(ba.getContainingPiGraph()).add(new Spider2CodegenActor("DUPLICATE", ba));
    return true;
  }

  @Override
  public Boolean caseJoinActor(final JoinActor ja) {
    this.actorsMap.get(ja.getContainingPiGraph()).add(new Spider2CodegenActor("JOIN", ja));
    return true;
  }

  @Override
  public Boolean caseForkActor(final ForkActor fa) {
    this.actorsMap.get(fa.getContainingPiGraph()).add(new Spider2CodegenActor("FORK", fa));
    return true;
  }

  @Override
  public Boolean caseRoundBufferActor(final RoundBufferActor rba) {
    this.actorsMap.get(rba.getContainingPiGraph()).add(new Spider2CodegenActor("TAIL", rba));
    return true;
  }

  @Override
  public Boolean caseInitActor(final InitActor init) {
    this.actorsMap.get(init.getContainingPiGraph()).add(new Spider2CodegenActor("INIT", init));
    return true;
  }

  @Override
  public Boolean caseEndActor(final EndActor end) {
    this.actorsMap.get(end.getContainingPiGraph()).add(new Spider2CodegenActor("END", end));
    return true;
  }

  @Override
  public Boolean caseParameter(final Parameter p) {
    throw new UnsupportedOperationException();
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
