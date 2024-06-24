/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2020)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.Link;

/**
 * The Class Spider2PreProcessVisitor.
 */
public class Spider2PreProcessVisitor extends PiMMSwitch<Boolean> {
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
  private final List<String> uniqueLoopHeaderFileNameList = new ArrayList<>();

  /** The unique refinement loop List */
  private final List<CHeaderRefinement> uniqueLoopHeaderList = new ArrayList<>();

  /** The unique refinement loop List */
  private final List<String> uniqueLoopProtoypeNameList = new ArrayList<>();

  /** The unique prototype loop List */
  private final List<Spider2CodegenPrototype> uniqueLoopProtoypeList = new ArrayList<>();

  /** The actor to function loop prototype indices */
  private final Map<AbstractActor, Integer> actorToLoopFctMap = new LinkedHashMap<>();

  /** The scenario */
  private final Scenario scenario;

  /** The clusters */
  private final List<Spider2CodegenCluster> clusterList = new ArrayList<>();

  /**
   * Constructor of the class.
   *
   * @param scenario
   *          the scenario of the application.
   */
  public Spider2PreProcessVisitor(final Scenario scenario) {
    this.scenario = scenario;

    /* Generate the cluster list */
    generateClusterList();
  }

  /**
   * Accessors
   */

  /**
   *
   * @return set of unique graph refinement
   */
  public Set<PiGraph> getUniqueGraphSet() {
    return this.uniqueGraphSet;
  }

  /**
   *
   * @return set of unique graph names
   */
  public Set<String> getUniqueGraphNameSet() {
    return this.uniqueGraphNameSet;
  }

  /**
   *
   * @param graph
   *          the graph to evaluate
   * @return list of static parameters of a given graph
   */
  public List<Parameter> getStaticParameters(final PiGraph graph) {
    return this.staticParametersMap.get(graph);
  }

  /**
   *
   * @param graph
   *          the graph to evaluate
   * @return list of static dependent parameters of a given graph
   */
  public Set<Parameter> getStaticDependentParameters(final PiGraph graph) {
    return this.staticDependentParametersMap.get(graph);
  }

  /**
   *
   * @param graph
   *          the graph to evaluate
   * @return list of dynamic parameters of a given graph
   */
  public List<Parameter> getDynamicParameters(final PiGraph graph) {
    return this.dynamicParametersMap.get(graph);
  }

  /**
   *
   * @param graph
   *          the graph to evaluate
   * @return list of dynamic dependent parameters of a given graph
   */
  public Set<Parameter> getDynamicDependentParameters(final PiGraph graph) {
    return this.dynamicDependentParametersMap.get(graph);
  }

  /**
   *
   * @param graph
   *          the graph to evaluate
   * @return set of Spider2CodegenEdge of the graph
   */
  public Set<Spider2CodegenEdge> getEdgeSet(final PiGraph graph) {
    return this.edgeMap.get(graph);
  }

  /**
   *
   * @param graph
   *          the graph to evaluate
   * @return set of unique subgraphs
   */
  public Set<PiGraph> getSubgraphSet(final PiGraph graph) {
    return this.subgraphMap.get(graph);
  }

  /**
   *
   * @param graph
   *          the graph to evaluate
   * @return set of Spider2CodegenActor
   */
  public Set<Spider2CodegenActor> getActorSet(final PiGraph graph) {
    return this.actorsMap.get(graph);
  }

  public Integer getLoopFctIndex(final AbstractActor actor) {
    return this.actorToLoopFctMap.get(actor);
  }

  /**
   *
   * @return list of all unique loop Spider2CodegenPrototype
   */
  public List<Spider2CodegenPrototype> getUniqueLoopPrototypeList() {
    return this.uniqueLoopProtoypeList;
  }

  /**
   *
   * @return list of all unique loop CHeaderRefinement
   */
  public List<String> getUniqueHeaderFileNameList() {
    return this.uniqueLoopHeaderFileNameList;
  }

  public List<CHeaderRefinement> getUniqueLoopHeaderList() {
    return this.uniqueLoopHeaderList;
  }

  /**
   *
   * @return list of all Spider2CodegenCluster
   */
  public List<Spider2CodegenCluster> getClusterList() {
    return this.clusterList;
  }

  /**
   *
   * @return name of the main processing element
   */
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

  private void generateClusterList() {
    final Design design = this.scenario.getDesign();
    final Map<ComponentInstance, List<ComponentInstance>> clusters = new HashMap<>();
    for (final Link link : design.getLinks()) {
      final ComponentInstance source = link.getSourceComponentInstance();
      final ComponentInstance target = link.getDestinationComponentInstance();
      if (source.getComponent() instanceof ComNode) {
        clusters.computeIfAbsent(source, k -> new ArrayList<>());
        clusters.get(source).add(target);
      } else if (target.getComponent() instanceof ComNode) {
        clusters.computeIfAbsent(target, k -> new ArrayList<>());
        clusters.get(target).add(source);
      }
    }
    for (final Entry<ComponentInstance, List<ComponentInstance>> entry : clusters.entrySet()) {
      this.clusterList.add(new Spider2CodegenCluster(entry.getKey(), entry.getValue()));
    }
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
      final List<
          Parameter> nextParamsToAddList = paramPoolList.stream().filter(x -> x.getInputDependentParameters().stream()
              .filter(dependentParametersSet::contains).count() == x.getInputDependentParameters().size()).toList();
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
        .filter(x -> x.isDependent() && !x.isConfigurable()).collect(Collectors.toList());

    /*
     * We need to ad also the static parameters because a dynamic parameters can inherit from both a dynamic and a
     * static parameter
     */
    final List<Parameter> dynamicParameterInitList = new ArrayList<>(dynamicParametersList);
    dynamicParameterInitList.addAll(staticParametersList);
    dynamicParameterInitList.addAll(staticDependentParametersMap.get(graph));
    dynamicDependentParametersMap.put(graph,
        getOrderedDependentParameter(dynamicParameterInitList, dynamicParameterPool));
  }

  /**
   * Insert one dynamic parameter if a config vertex set multiple parameters with one output port
   *
   * @param graph
   *          The graph to evaluate
   */
  private void insertDynamicParameters(final PiGraph graph) {
    for (final AbstractActor actor : graph.getActors().stream().filter(x -> !x.getConfigOutputPorts().isEmpty())
        .toList()) {
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
    final Set<Spider2CodegenEdge> edgeSet = new LinkedHashSet<>();
    for (final Fifo fifo : graph.getFifos()) {
      /* Add the edge to the edge Set */
      final Spider2CodegenEdge edge = new Spider2CodegenEdge(fifo, this.scenario.getSimulationInfo());
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

    /* Extract the actors */
    extractActors(graph);

    /* Extract edges */
    if (!this.edgeMap.containsKey(graph)) {
      extractEdges(graph);
    }

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
    /* Set the type */
    final String type = actor.isConfigurationActor() ? "CONFIG" : "NORMAL";
    this.actorsMap.get(actor.getContainingPiGraph())
        .add(new Spider2CodegenActor(type, actor, this.scenario, this.clusterList));

    /* Set the refinement */
    if (!(actor.getRefinement() instanceof CHeaderRefinement)) {
      PreesmLogger.getLogger().warning("Actor [" + actor.getName() + "] doesn't have correct refinement.");
    } else {
      final CHeaderRefinement refinement = (CHeaderRefinement) (actor.getRefinement());
      if (!this.uniqueLoopHeaderList.contains(refinement)) {
        this.uniqueLoopHeaderList.add(refinement);
      }
      if (!this.uniqueLoopHeaderFileNameList.contains(refinement.getFileName())) {
        this.uniqueLoopHeaderFileNameList.add(refinement.getFileName());
      }
      final FunctionPrototype loop = refinement.getLoopPrototype();
      final String loopName = loop != null ? loop.getName() : actor.getName();
      if (!this.uniqueLoopProtoypeNameList.contains(loopName)) {
        this.uniqueLoopProtoypeNameList.add(loopName);
        this.uniqueLoopProtoypeList.add(new Spider2CodegenPrototype(refinement));
      }
      this.actorToLoopFctMap.put(actor, this.uniqueLoopProtoypeNameList.indexOf(loopName));
    }
    return true;
  }

  /**
   * Convert broadcast actor that are not perfect broadcast to a repeat + fork pattern for spider2.
   *
   * {@literal text
   * Example:
   * in preesm:    IN[10] -> BR -> 5
   *                            -> 8
   *                            -> 10
   * will be converted in spider to:
   *      IN[10] -> REPEAT -> [23] -> IN[23] -> FORK -> 5
   *                                                 -> 8
   *                                                 -> 10
   *
   *  }
   *
   * @param graph
   *          The graph
   * @param ba
   *          The broadcast actor
   */
  private void convertPreesmBroadcastToSpider(final PiGraph graph, final BroadcastActor ba) {
    /* = Concatenate all the output expression into one = */
    String repeatExpression = "";
    for (final DataOutputPort dop : ba.getDataOutputPorts()) {
      final Expression expr = dop.getExpression();
      if (repeatExpression.equals("")) {
        repeatExpression = expr.getExpressionAsString();
      } else {
        repeatExpression = repeatExpression + " + " + expr.getExpressionAsString();
      }
    }

    /* = Creates a repeat actor = */
    final AbstractActor repeat = PiMMUserFactory.instance.createBroadcastActor();
    graph.addActor(repeat);
    final DataInputPort ipBroadcast = ba.getDataInputPorts().get(0);
    final DataInputPort ipRepeat = PiMMUserFactory.instance.createDataInputPort();
    ipRepeat.setName(ipBroadcast.getName());
    ipRepeat.setExpression(ipBroadcast.getExpression().getExpressionAsString());
    repeat.getDataInputPorts().add(ipRepeat);
    final DataOutputPort opRepeat = PiMMUserFactory.instance.createDataOutputPort();
    opRepeat.setName("out");
    opRepeat.setExpression(repeatExpression);
    repeat.getDataOutputPorts().add(opRepeat);
    repeat.setName("repeat_" + ba.getName());

    /* = Creates a fork actor = */
    final AbstractActor fork = PiMMUserFactory.instance.createForkActor();
    graph.addActor(fork);
    fork.setName("fork_" + ba.getName());

    /* = Connect the repeat to the fork = */
    final DataInputPort ipFork = PiMMUserFactory.instance.createDataInputPort();
    ipFork.setName("in");
    ipFork.setExpression(repeatExpression);
    fork.getDataInputPorts().add(ipFork);
    final Fifo fifoR2F = PiMMUserFactory.instance.createFifo(opRepeat, ipFork, ipBroadcast.getIncomingFifo().getType());
    graph.addFifo(fifoR2F);

    /* = Disconnect broadcast and connect everything to the repeat / fork = */
    ipBroadcast.getIncomingFifo().setTargetPort(ipRepeat);
    for (final DataOutputPort dop : ba.getDataOutputPorts()) {
      final DataOutputPort opFork = PiMMUserFactory.instance.createDataOutputPort();
      opFork.setName(dop.getName());
      opFork.setExpression(dop.getExpression().getExpressionAsString());
      dop.getOutgoingFifo().setSourcePort(opFork);
      fork.getDataOutputPorts().add(opFork);
    }

    /* = Removes the broadcast = */
    graph.removeActor(ba);

    /* = Creates the codegen actors = */
    this.actorsMap.get(graph).add(new Spider2CodegenActor("REPEAT", repeat, this.scenario, this.clusterList));
    this.actorsMap.get(graph).add(new Spider2CodegenActor("FORK", fork, this.scenario, this.clusterList));
  }

  @Override
  public Boolean caseBroadcastActor(final BroadcastActor ba) {
    final PiGraph graph = ba.getContainingPiGraph();
    if (ba.getOutgoingEdges().size() == 1) {
      this.actorsMap.get(graph).add(new Spider2CodegenActor("REPEAT", ba, this.scenario, this.clusterList));
    } else {
      final Expression inputExpr = ba.getDataInputPorts().get(0).getExpression();
      final String inputExprString = inputExpr.getExpressionAsString().replace(" ", "");
      Boolean isPerfectBroadcast = true;
      for (final DataOutputPort dop : ba.getDataOutputPorts()) {
        final Expression expr = dop.getExpression();
        final String exprString = expr.getExpressionAsString().replace(" ", "");
        if (!exprString.equalsIgnoreCase(inputExprString)) {
          isPerfectBroadcast = false;
          break;
        }
      }
      if (isPerfectBroadcast.booleanValue()) {
        this.actorsMap.get(graph).add(new Spider2CodegenActor("DUPLICATE", ba, this.scenario, this.clusterList));
      } else {
        convertPreesmBroadcastToSpider(graph, ba);
      }
    }
    return true;
  }

  @Override
  public Boolean caseJoinActor(final JoinActor ja) {
    this.actorsMap.get(ja.getContainingPiGraph())
        .add(new Spider2CodegenActor("JOIN", ja, this.scenario, this.clusterList));
    return true;
  }

  @Override
  public Boolean caseForkActor(final ForkActor fa) {
    this.actorsMap.get(fa.getContainingPiGraph())
        .add(new Spider2CodegenActor("FORK", fa, this.scenario, this.clusterList));
    return true;
  }

  @Override
  public Boolean caseRoundBufferActor(final RoundBufferActor rba) {
    this.actorsMap.get(rba.getContainingPiGraph())
        .add(new Spider2CodegenActor("TAIL", rba, this.scenario, this.clusterList));
    return true;
  }

  @Override
  public Boolean caseInitActor(final InitActor init) {
    this.actorsMap.get(init.getContainingPiGraph())
        .add(new Spider2CodegenActor("INIT", init, this.scenario, this.clusterList));
    return true;
  }

  @Override
  public Boolean caseEndActor(final EndActor end) {
    this.actorsMap.get(end.getContainingPiGraph())
        .add(new Spider2CodegenActor("END", end, this.scenario, this.clusterList));
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
