/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2017 - 2018)
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
package org.ietr.preesm.throughput.tools.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.ietr.dftools.algorithm.model.AbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.dftools.algorithm.model.types.SDFIntEdgePropertyType;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.throughput.tools.parsers.Identifier;

/**
 * @author hderoui
 *
 *         A structure helper to simplify the manipulation of the structure of a SDF graph
 *
 */
public interface GraphStructureHelper {

  public static final String WEIGHT_LP_PROPERTY       = "weight_LP";
  public static final String PORT_RATE_PROPERTY       = "port_rate";
  public static final String BASE_ACTOR_PROPERTY      = "baseActor";
  public static final String NORMALIZED_RATE_PROPERTY = "normalizedRate";
  public static final String DURATION_PROPERTY        = "duration";
  public static final String BASE_EDGE_PROPERTY       = "baseEdge";

  /**
   * Adds a new edge to an SDF graph.
   *
   * @param graph
   *          SDF graph
   * @param srcActorName
   *          the name of the source actor
   * @param srcPortName
   *          the name of the source port
   * @param tgtActorName
   *          the name of the target actor
   * @param tgtPortName
   *          the name of the target port
   * @param prodRate
   *          the production rate of the edge
   * @param consRate
   *          the consumption rate of the edge
   * @param delay
   *          the delay of the edge
   * @param baseEdge
   *          the parent edge
   * @return the created SDF edge
   */

  public static SDFEdge addEdge(final SDFGraph graph, final String srcActorName, final String srcPortName,
      final String tgtActorName, final String tgtPortName, final long prodRate, final long consRate, final long delay,
      final SDFEdge baseEdge) {
    // get the source actor if not exists create one
    SDFAbstractVertex srcActor = graph.getVertex(srcActorName);
    if (srcActor == null) {
      srcActor = GraphStructureHelper.addActor(graph, srcActorName, null, 0, 0, 0, null);
    }
    // get the source port if not create one
    SDFInterfaceVertex srcPort;
    if (srcPortName != null) {
      srcPort = srcActor.getInterface(srcPortName);
      if (srcPort == null) {
        srcPort = GraphStructureHelper.addSinkPort(srcActor, srcPortName, prodRate);
      }
    } else {
      srcPort = GraphStructureHelper.addSinkPort(srcActor, Identifier.generateOutputPortId() + "_to_" + tgtActorName,
          prodRate);
    }

    // get the target actor if not exists create one
    SDFAbstractVertex tgtActor = graph.getVertex(tgtActorName);
    if (tgtActor == null) {
      tgtActor = GraphStructureHelper.addActor(graph, tgtActorName, null, 0, 0, 0, null);
    }
    // get the target port if not exists create one
    SDFInterfaceVertex tgtPort;
    if (tgtPortName != null) {
      tgtPort = tgtActor.getInterface(tgtPortName);
      if (tgtPort == null) {
        tgtPort = GraphStructureHelper.addSrcPort(tgtActor, tgtPortName, consRate);
      }
    } else {
      tgtPort = GraphStructureHelper.addSrcPort(tgtActor, Identifier.generateInputPortId() + "_from_" + srcActorName,
          consRate);
    }

    // add the edge to the srSDF graph
    final SDFEdge edge = graph.addEdge(srcActor, srcPort, tgtActor, tgtPort);
    edge.setPropertyValue("edgeName",
        "from_" + srcActorName + "_" + srcPortName + "_to_" + tgtActorName + "_" + tgtPortName);
    edge.setPropertyValue("edgeId", Identifier.generateEdgeId());
    edge.setProd(new SDFIntEdgePropertyType(prodRate));
    edge.setCons(new SDFIntEdgePropertyType(consRate));
    edge.setDelay(new SDFIntEdgePropertyType(delay));
    edge.setPropertyValue(BASE_EDGE_PROPERTY, baseEdge);

    return edge;
  }

  /**
   * Adds an actor to an SDF graph
   *
   * @param graph
   *          SDF graph
   * @param actorName
   *          name of the actor
   * @param subgraph
   *          subgraph of the actor if is hierarchical
   * @param repititionFactor
   *          repetition factor of the actor
   * @param latency
   *          the execution duration of the actor
   * @param normalizedPortsRate
   *          the normalized consumption/production rate of the actor
   * @param baseActor
   *          the base actor from the source IBSDF graph
   * @return the created actor
   */
  public static SDFAbstractVertex addActor(final SDFGraph graph, final String actorName, final SDFGraph subgraph,
      final long repititionFactor, final double latency, final double normalizedPortsRate,
      final SDFAbstractVertex baseActor) {
    final SDFAbstractVertex actor = new SDFVertex(graph);
    // set the name
    actor.setId(actorName);
    actor.setName(actorName);

    // add the subgraph if the actor is hierarchical
    if (subgraph != null) {
      actor.setGraphDescription(subgraph);
    }

    // set the repetition factor
    actor.setNbRepeat(repititionFactor);

    // set the execution duration
    actor.setPropertyValue(DURATION_PROPERTY, latency);

    // set the normalized consumption/production rate
    actor.setPropertyValue(NORMALIZED_RATE_PROPERTY, normalizedPortsRate);

    // set the base actor
    if (baseActor != null) {
      actor.setPropertyValue(BASE_ACTOR_PROPERTY, baseActor);
    } else {
      actor.setPropertyValue(BASE_ACTOR_PROPERTY, actor);
    }

    graph.addVertex(actor);
    return actor;
  }

  /**
   * add an input interface to a subgraph
   *
   * @param graph
   *          the SDF subgraph
   * @param interfaceName
   *          the name of the input interface (the same name of its associated port in its hierarchical parent actor)
   * @param rv
   *          the repetition factor of the interface
   * @param l
   *          the execution duration of the interface
   * @param z
   *          the normalized rate
   * @param base
   *          the base interface of the input interface
   * @return the created input interface
   */
  public static SDFSourceInterfaceVertex addInputInterface(final SDFGraph graph, final String interfaceName,
      final long rv, final double l, final double z, final SDFAbstractVertex base) {
    // an interface is an independent actor from its hierarchical parent actor but with the same name of its associated
    // port in the hierarchical parent actor

    // create an input interface and set the parent graph
    final SDFSourceInterfaceVertex in = new SDFSourceInterfaceVertex();
    in.setPropertyValue(AbstractVertex.BASE_LITERAL, graph);

    // set the name
    in.setId(interfaceName);
    in.setName(interfaceName);

    // set the repetition factor
    in.setNbRepeat(rv);

    // set the execution duration
    in.setPropertyValue(DURATION_PROPERTY, l);

    // set the normalized consumption/production rate
    in.setPropertyValue(NORMALIZED_RATE_PROPERTY, z);

    // set the base actor
    if (base != null) {
      in.setPropertyValue(BASE_ACTOR_PROPERTY, base);
    } else {
      in.setPropertyValue(BASE_ACTOR_PROPERTY, in);
    }

    graph.addVertex(in);
    return in;

  }

  /**
   * add an output interface to a subgraph
   *
   * @param graph
   *          the SDF subgraph
   * @param interfaceName
   *          the name of the output interface (the same name of its associated port in its hierarchical parent actor)
   * @param rv
   *          the repetition factor of the interface
   * @param l
   *          the execution duration of the interface
   * @param z
   *          the normalized rate
   * @param base
   *          the base interface of the output interface
   * @return the created output interface
   */
  public static SDFSinkInterfaceVertex addOutputInterface(final SDFGraph graph, final String interfaceName,
      final long rv, final double l, final double z, final SDFAbstractVertex base) {
    // an interface is an independent actor from its hierarchical parent actor but with the same name of its associated
    // port in the hierarchical actor

    // create an output interface and set the parent graph
    final SDFSinkInterfaceVertex out = new SDFSinkInterfaceVertex();
    out.setPropertyValue(AbstractVertex.BASE_LITERAL, graph);

    // set the name
    out.setId(interfaceName);
    out.setName(interfaceName);

    // set the repetition factor
    out.setNbRepeat(rv);

    // set the execution duration
    out.setPropertyValue(DURATION_PROPERTY, l);

    // set the normalized consumption/production rate
    out.setPropertyValue(NORMALIZED_RATE_PROPERTY, z);

    // set the base actor
    if (base != null) {
      out.setPropertyValue(BASE_ACTOR_PROPERTY, base);
    } else {
      out.setPropertyValue(BASE_ACTOR_PROPERTY, out);
    }

    graph.addVertex(out);
    return out;
  }

  /**
   * Adds a source port to an actor
   *
   * @param actor
   *          SDF actor
   * @param portName
   *          name of the port
   * @param portRate
   *          rate of the port
   * @return created port
   */
  public static SDFSourceInterfaceVertex addSrcPort(final SDFAbstractVertex actor, final String portName,
      final long portRate) {
    final SDFSourceInterfaceVertex port = new SDFSourceInterfaceVertex();
    port.setId(portName);
    port.setName(portName);
    port.setPropertyValue(PORT_RATE_PROPERTY, portRate);
    actor.addInterface(port);
    return port;
  }

  /**
   * Adds a sink port to an actor
   *
   * @param actor
   *          SDF actor
   * @param portName
   *          name of the actor
   * @param portRate
   *          rate of the actor
   * @return created port
   */
  public static SDFSinkInterfaceVertex addSinkPort(final SDFAbstractVertex actor, final String portName,
      final long portRate) {
    final SDFSinkInterfaceVertex port = new SDFSinkInterfaceVertex();
    port.setId(portName);
    port.setName(portName);
    port.setPropertyValue(PORT_RATE_PROPERTY, portRate);
    actor.addInterface(port);
    return port;
  }

  /**
   * returns a list of all the hierarchical actors in the hierarchy
   *
   * @param graph
   *          IBSDF graph
   * @return the list of hierarchical actors
   */
  public static Map<String, SDFAbstractVertex> getAllHierarchicalActors(final SDFGraph graph) {
    // list of hierarchical actors
    final Map<String, SDFAbstractVertex> hierarchicalActorsList = new LinkedHashMap<>();
    final ArrayList<SDFGraph> subgraphsToCheck = new ArrayList<>();

    // add the hierarchical actors of the top graph
    for (final SDFAbstractVertex a : graph.vertexSet()) {
      if (a.getGraphDescription() != null) {
        hierarchicalActorsList.put(a.getName(), a);
        subgraphsToCheck.add((SDFGraph) a.getGraphDescription());
      }
    }

    // process all subgraphs in the hierarchy
    while (!subgraphsToCheck.isEmpty()) {
      for (final SDFAbstractVertex a : subgraphsToCheck.get(0).vertexSet()) {
        if (a.getGraphDescription() != null) {
          hierarchicalActorsList.put(a.getName(), a);
          subgraphsToCheck.add((SDFGraph) a.getGraphDescription());
        }
      }
      subgraphsToCheck.remove(0);
    }

    return hierarchicalActorsList;
  }

  /**
   * returns the hierarchical actors of an SDF graph
   *
   * @param graph
   *          SDF graph
   * @return list of hierarchical actors
   */
  public static Map<String, SDFAbstractVertex> getHierarchicalActors(final SDFGraph graph) {
    // list of hierarchical actors
    final Map<String, SDFAbstractVertex> hierarchicalActorsList = new LinkedHashMap<>();
    for (final SDFAbstractVertex a : graph.vertexSet()) {
      if (a.getGraphDescription() != null) {
        hierarchicalActorsList.put(a.getName(), a);
      }
    }
    return hierarchicalActorsList;
  }

  /**
   * replace a hierarchical actor by a sdf/srSDF graph
   *
   * @param parentGraph
   *          the graph containing the hierarchical actor
   * @param h
   *          the hierarchical actor
   * @param replacementGraph
   *          the replacement graph
   */
  public static void replaceHierarchicalActor(final SDFGraph parentGraph, final SDFAbstractVertex h,
      final SDFGraph replacementGraph) {

    // Step 1: add the replacement subgraph into the parent graph
    for (final SDFAbstractVertex a : replacementGraph.vertexSet()) {
      GraphStructureHelper.addActor(parentGraph, h.getName() + "_" + a.getName(), (SDFGraph) a.getGraphDescription(),
          1L, (Double) a.getPropertyBean().getValue(DURATION_PROPERTY), 0,
          (SDFAbstractVertex) a.getPropertyBean().getValue(BASE_ACTOR_PROPERTY));
    }
    for (final SDFEdge e : replacementGraph.edgeSet()) {
      final String cloneEdgeSrcName = h.getName() + "_" + e.getSource().getName();
      final String cloneEdgeTgtName = h.getName() + "_" + e.getTarget().getName();
      final SDFEdge edgeClone = GraphStructureHelper.addEdge(parentGraph, cloneEdgeSrcName, null, cloneEdgeTgtName,
          null, e.getProd().longValue(), e.getCons().longValue(), e.getDelay().longValue(),
          (SDFEdge) e.getPropertyBean().getValue("baseedge"));

      // copy properties
      if (e.getPropertyBean().getValue(WEIGHT_LP_PROPERTY) != null) {
        edgeClone.setPropertyValue(WEIGHT_LP_PROPERTY, (double) e.getPropertyBean().getValue(WEIGHT_LP_PROPERTY));
      }
    }

    // Step 2: connect the interfaces of the added graph with actors of the parent graph
    // disconnect the edges from the hierarchical actor and connect them to their associated interface in the subgraph
    List<SDFEdge> edges = new ArrayList<>();
    for (final SDFInterfaceVertex input : h.getSources()) {
      edges.add(h.getAssociatedEdge(input));
    }
    for (final SDFEdge edge : edges) {
      final String hierarchicalPortName = ((SDFEdge) edge.getPropertyBean().getValue(BASE_EDGE_PROPERTY))
          .getTargetInterface().getName();
      final String subgraphInterfaceName = h.getName() + "_" + hierarchicalPortName + "_1";
      GraphStructureHelper.replaceEdgeTargetActor(parentGraph, edge, subgraphInterfaceName, null);
    }

    // disconnect the edges from the hierarchical actor and connect them to their associated interface in the subgraph
    edges = new ArrayList<>();
    for (final SDFInterfaceVertex output : h.getSinks()) {
      edges.add(h.getAssociatedEdge(output));
    }
    for (final SDFEdge edge : edges) {
      final String hierarchicalPortName = ((SDFEdge) edge.getPropertyBean().getValue(BASE_EDGE_PROPERTY))
          .getSourceInterface().getName();
      final String subgraphInterfaceName = h.getName() + "_" + hierarchicalPortName + "_1";
      GraphStructureHelper.replaceEdgeSourceActor(parentGraph, edge, subgraphInterfaceName, null);
    }

    // remove the hierarchical actor from the parent graph
    parentGraph.removeVertex(h);
  }

  /**
   * replaces the source actor of an edge by a new actor and a new source port
   *
   * @param graph
   *          SDF graph
   * @param edge
   *          SDF Edge
   * @param newSourceActor
   *          new source actor
   * @param newSourcePort
   *          new source port
   */
  public static SDFEdge replaceEdgeSourceActor(final SDFGraph graph, final SDFEdge edge, final String newSourceActor,
      final String newSourcePort) {
    // create the new edge
    final SDFEdge nweEdge = GraphStructureHelper.addEdge(graph, newSourceActor, newSourcePort,
        edge.getTarget().getName(), edge.getTargetInterface().getName(), edge.getProd().longValue(),
        edge.getCons().longValue(), edge.getDelay().longValue(),
        (SDFEdge) edge.getPropertyBean().getValue(BASE_EDGE_PROPERTY));
    // remove the old edge
    graph.removeEdge(edge);
    return nweEdge;
  }

  /**
   * replaces the target actor of an edge by a new actor and a new target port
   *
   * @param graph
   *          SDF graph
   * @param edge
   *          SDF Edge
   * @param newTargetActor
   *          new target Actor
   * @param newTargetPort
   *          new target port
   */
  public static SDFEdge replaceEdgeTargetActor(final SDFGraph graph, final SDFEdge edge, final String newTargetActor,
      final String newTargetPort) {
    // create the new edge
    final SDFEdge nweEdge = GraphStructureHelper.addEdge(graph, edge.getSource().getName(),
        edge.getSourceInterface().getName(), newTargetActor, newTargetPort, edge.getProd().longValue(),
        edge.getCons().longValue(), edge.getDelay().longValue(),
        (SDFEdge) edge.getPropertyBean().getValue(BASE_EDGE_PROPERTY));
    // remove the old edge
    graph.removeEdge(edge);
    return nweEdge;
  }

  /**
   * re-time the IBSDF graph to reveal the hidden delays
   *
   * @param graph
   *          IBSDF graph
   */
  public static void retime(final SDFGraph graph) {
    // reveal the hidden delays of each hierarchical actor in the top graph
    for (final SDFAbstractVertex actor : graph.vertexSet()) {
      if (actor.getGraphDescription() != null) {
        GraphStructureHelper.revealHiddenDelays(actor);
      }
    }
  }

  /***
   * reveal the hidden delays of the hierarchical actor subgraph
   *
   * @param h
   *          hierarchical actor
   */
  public static void revealHiddenDelays(final SDFAbstractVertex h) {
    // get the subgraph of the hierarchical actor
    final SDFGraph subgraph = (SDFGraph) h.getGraphDescription();

    // reveal the hidden delays of each hierarchical actor in the subgraph
    for (final SDFAbstractVertex actor : subgraph.vertexSet()) {
      if (actor.getGraphDescription() != null) {
        GraphStructureHelper.revealHiddenDelays(actor);
      }
    }

    // execute the subgraph to reveal the hidden delays
    // Step 1: add an empty loop edge for each actor with no inputs (including inputInterfaces)
    final ArrayList<SDFEdge> emptyLoopsList = new ArrayList<>();
    for (final SDFAbstractVertex actor : subgraph.vertexSet()) {
      if (actor.getSources().isEmpty()) {
        final SDFEdge e = GraphStructureHelper.addEdge(subgraph, actor.getName(), null, actor.getName(), null, 1, 1, 0,
            null);
        emptyLoopsList.add(e);
      }
    }

    // Step 2: execute actor as long as they are ready
    final GraphSimulationHelper simulator = new GraphSimulationHelper(subgraph);
    Map<SDFAbstractVertex, Long> readyActors = simulator.getReadyActorsNbExecutions();
    while (!readyActors.isEmpty()) {
      for (final Entry<SDFAbstractVertex, Long> e : readyActors.entrySet()) {
        simulator.execute(e.getKey(), e.getValue());
      }
      readyActors = simulator.getReadyActorsNbExecutions();
    }

    // Step3: remove the empty loops
    for (final SDFEdge e : emptyLoopsList) {
      subgraph.removeEdge(e);
    }

    // for each output interface add RV(h)*n*prod delays on its output edge
    for (final SDFAbstractVertex actor : subgraph.vertexSet()) {
      if (actor instanceof SDFSinkInterfaceVertex) {
        final SDFEdge e = h.getAssociatedEdge(h.getInterface(actor.getName()));
        final long oldMarking = e.getDelay().longValue();
        final long newMarking = oldMarking
            + (h.getNbRepeatAsLong() * (simulator.getExecutionCounter(actor) * e.getProd().longValue()));
        e.setDelay(new SDFIntEdgePropertyType(newMarking));
      }
    }

    // save the current state as the initial state
    simulator.resetExecutionCounter();
  }

  /**
   * computes a topological sorting of the complete DAG
   *
   * @param dag
   *          graph
   * @return list of sorted actors
   */
  public static List<SDFAbstractVertex> topologicalSorting(final SDFGraph dag) {
    // the sorted list (inverse order of topological sorting)
    // TODO uses stack instead
    final List<SDFAbstractVertex> sortedActors = new ArrayList<>();

    // Mark all the actors as not visited
    final Map<String, Boolean> visited = new LinkedHashMap<>();
    for (final SDFAbstractVertex actor : dag.vertexSet()) {
      visited.put(actor.getName(), false);
    }

    // Topological Sort starting from all actors one by on
    for (final SDFAbstractVertex actor : dag.vertexSet()) {
      if (!visited.get(actor.getName())) {
        GraphStructureHelper.recurciveTopologicalSorting(actor, visited, sortedActors);
      }
    }

    // inverse the order to get the final list
    Collections.reverse(sortedActors);
    return sortedActors;
  }

  /**
   * computes a topological sorting of the DAG starting from a source actor
   *
   * @param source
   *          actor to start from
   * @param visited
   *          list of visited actors for a complete topological sorting
   * @param sortedActors
   *          the resulted list
   */
  public static void recurciveTopologicalSorting(final SDFAbstractVertex source, final Map<String, Boolean> visited,
      final List<SDFAbstractVertex> sortedActors) {
    // Mark the current actor as visited
    visited.put(source.getName(), true);

    // explore all the target sink actors of the current source
    for (final SDFInterfaceVertex output : source.getSinks()) {
      // get the target actor
      final SDFAbstractVertex target = source.getAssociatedEdge(output).getTarget();
      if (!visited.containsKey(target.getName()) || !visited.get(target.getName())) {
        GraphStructureHelper.recurciveTopologicalSorting(target, visited, sortedActors);
      }
    }

    // add the current actor to the sorted list
    sortedActors.add(source);

  }

  /**
   * computes a topological sorting of the DAG starting from a source actor
   *
   * @param source
   *          actor to start from
   * @return list of sorted actors
   */
  public static List<SDFAbstractVertex> partialTopologicalSorting(final SDFAbstractVertex source) {
    // prepare the lists
    final Map<String, Boolean> visited = new LinkedHashMap<>();
    final List<SDFAbstractVertex> sortedActors = new ArrayList<>();

    // do a partial topological sorting
    GraphStructureHelper.recurciveTopologicalSorting(source, visited, sortedActors);

    // return the final list
    Collections.reverse(sortedActors);
    return sortedActors;
  }

  /**
   * Computes the longest path of a DAG
   *
   * @param source
   *          source actor
   * @return list of distances
   */
  public static Map<String, Double> getLongestPathToAllTargets(final SDFAbstractVertex source,
      final PreesmScenario scenario, List<SDFAbstractVertex> topoSortList) {

    // get the topological sorting of the graph
    if (topoSortList == null) {
      topoSortList = GraphStructureHelper.partialTopologicalSorting(source);
    }

    // table of distances
    final Map<String, Double> distance = new LinkedHashMap<>();
    for (final SDFAbstractVertex actor : topoSortList) {
      distance.put(actor.getName(), Double.NEGATIVE_INFINITY);
    }
    distance.replace(source.getName(), 0.);

    for (int i = 0; i < topoSortList.size(); i++) {
      // get the current source actor
      final SDFAbstractVertex currentSource = topoSortList.get(i);

      // define the edge weight as the duration of the current source actor
      double actorDuration;
      if (scenario != null) {
        actorDuration = scenario.getTimingManager().getTimingOrDefault(currentSource.getId(), "x86").getTime();
      } else {
        actorDuration = (double) currentSource.getPropertyBean().getValue(DURATION_PROPERTY);
      }

      // update the distances
      for (final SDFInterfaceVertex output : currentSource.getSinks()) {
        double edgeWeight = actorDuration;
        // get the associated output edge and its weight if defined
        final SDFEdge outputEdge = currentSource.getAssociatedEdge(output);
        if (outputEdge.getPropertyBean().getValue(WEIGHT_LP_PROPERTY) != null) {
          edgeWeight = (double) outputEdge.getPropertyBean().getValue(WEIGHT_LP_PROPERTY);
        }

        // get the target actor of the associated output edge
        final SDFAbstractVertex currentTarget = outputEdge.getTarget();

        // update the distance of the current target
        if (distance.get(currentTarget.getName()) < (distance.get(currentSource.getName()) + edgeWeight)) {
          distance.replace(currentTarget.getName(), distance.get(currentSource.getName()) + edgeWeight);
        }
      }
    }

    return distance;
  }

  /**
   * @param source
   *          actor
   * @param target
   *          actor
   * @return value of the longest path between the source and the target
   */
  public static double getLongestPathToTarget(final SDFAbstractVertex source, final SDFAbstractVertex target,
      final PreesmScenario scenario, List<SDFAbstractVertex> topoSortList) {

    // get the topological sorting of the graph
    if (topoSortList == null) {
      topoSortList = GraphStructureHelper.partialTopologicalSorting(source);
    }

    // table of distances
    final Map<String, Double> distance = new LinkedHashMap<>();
    for (final SDFAbstractVertex actor : topoSortList) {
      distance.put(actor.getName(), Double.NEGATIVE_INFINITY);
    }
    distance.replace(source.getName(), 0.);

    for (int i = 0; i < topoSortList.size(); i++) {
      // get the current source actor
      final SDFAbstractVertex currentSource = topoSortList.get(i);

      // check if the target actor is reached
      if (currentSource.getName().equals(target.getName())) {
        return distance.get(target.getName());
      } else {

        // define the edge weight as the duration of the current source actor
        double actorDuration;
        if (scenario != null) {
          actorDuration = scenario.getTimingManager().getTimingOrDefault(currentSource.getId(), "x86").getTime();
        } else {
          actorDuration = (double) currentSource.getPropertyBean().getValue(DURATION_PROPERTY);
        }

        // update the distances
        for (final SDFInterfaceVertex output : currentSource.getSinks()) {
          double edgeWeight = actorDuration;
          // get the associated output edge and its weight if defined
          final SDFEdge outputEdge = currentSource.getAssociatedEdge(output);
          if (outputEdge.getPropertyBean().getValue(WEIGHT_LP_PROPERTY) != null) {
            edgeWeight = (double) outputEdge.getPropertyBean().getValue(WEIGHT_LP_PROPERTY);
          }

          // get the target actor of the associated output edge
          final SDFAbstractVertex currentTarget = outputEdge.getTarget();

          // update the distance of the current target
          if (distance.get(currentTarget.getName()) < (distance.get(currentSource.getName()) + edgeWeight)) {
            distance.replace(currentTarget.getName(), distance.get(currentSource.getName()) + edgeWeight);
          }
        }
      }
    }

    // return the distance from the source to the target
    return distance.get(target.getName());
  }

  /**
   * @param dag
   *          graph
   * @return value of the longest path in the graph
   */
  public static double getLongestPath(final SDFGraph dag, final PreesmScenario scenario,
      List<SDFAbstractVertex> topoSortList) {
    // add a source and a target actor
    final SDFAbstractVertex source = GraphStructureHelper.addActor(dag, "S_LongestPath", null, 1L, 0, 0, null);
    final SDFAbstractVertex target = GraphStructureHelper.addActor(dag, "T_LongestPath", null, 1L, 0, 0, null);

    // connect the source to all input actors and the target to all output actors
    for (final SDFAbstractVertex actor : dag.vertexSet()) {
      // check if the actor has no input edges
      if (actor.getSources().isEmpty()) {
        // add an edge between the source and the current actor
        GraphStructureHelper.addEdge(dag, source.getName(), null, actor.getName(), null, 1, 1, 0, null);
      }
      // check if the actor has no output edges
      if (actor.getSinks().isEmpty()) {
        // add an edge between the current actor and the target
        GraphStructureHelper.addEdge(dag, actor.getName(), null, target.getName(), null, 1, 1, 0, null);
      }
    }

    // get the topological sorting of the graph
    if (topoSortList == null) {
      topoSortList = GraphStructureHelper.topologicalSorting(dag);
    } else {
      topoSortList.add(0, source);
      topoSortList.add(topoSortList.size(), target);
    }

    // compute the longest path from the source
    final Map<String,
        Double> distance = GraphStructureHelper.getLongestPathToAllTargets(source, scenario, topoSortList);
    final double lp = distance.get(target.getName());

    // remove the source and the target from the graph
    dag.removeVertex(source);
    dag.removeVertex(target);

    return lp;
  }

  /**
   * Clone an IBSDF graph
   *
   * @param ibsdf
   *          input IBSDF graph
   * @return IBSDF graph
   */
  public static SDFGraph cloneIBSDF(final SDFGraph ibsdf) {
    // list of hierarchical actor to be cloned
    final ArrayList<SDFAbstractVertex> hierarchicalActorsToBeCloned = new ArrayList<>();

    // clone the tograph
    final SDFGraph clonedIBSDF = ibsdf.clone();

    // add the hierarchical actors of the new top-graph to the list
    Map<String, SDFAbstractVertex> hActors = GraphStructureHelper.getHierarchicalActors(clonedIBSDF);
    for (final SDFAbstractVertex a : hActors.values()) {
      hierarchicalActorsToBeCloned.add(a);
    }

    // for each hierarchical actor clone its subgraph and set the new graph as its original subgraph
    while (!hierarchicalActorsToBeCloned.isEmpty()) {
      // get a hierarchical actor
      final SDFAbstractVertex h = hierarchicalActorsToBeCloned.get(0);
      // clone its subgraph
      final SDFGraph cloneSubgraph = ((SDFGraph) h.getGraphDescription()).clone();
      // set the subgraph cloned
      h.setGraphDescription(cloneSubgraph);

      // add the hierarchical actors of the new subgraph to the list
      hActors = GraphStructureHelper.getHierarchicalActors(cloneSubgraph);
      for (final SDFAbstractVertex a : hActors.values()) {
        hierarchicalActorsToBeCloned.add(a);
      }

      // remove the hierarchical actor proceeded
      hierarchicalActorsToBeCloned.remove(0);
    }

    return clonedIBSDF;
  }
}
