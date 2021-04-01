/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
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
package org.preesm.model.pisdf.reconnection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 * Parse and connect hierarchical sub-{@link PiGraph} to a top level {@link PiGraph}.
 *
 * @author cguy
 * @author kdesnos
 */
public class SubgraphReconnector extends PiMMSwitch<Boolean> {

  /**
   * The Class ActorByGraphReplacement.
   */
  private class ActorByGraphReplacement {

    /** The to be removed. */
    public final Actor toBeRemoved;

    /** The to be added. */
    public final PiGraph toBeAdded;

    /**
     * Instantiates a new actor by graph replacement.
     *
     * @param toBeRemoved
     *          the to be removed
     * @param toBeAdded
     *          the to be added
     */
    public ActorByGraphReplacement(final Actor toBeRemoved, final PiGraph toBeAdded) {
      this.toBeRemoved = toBeRemoved;
      this.toBeAdded = toBeAdded;
    }
  }

  /** The graph replacements. */
  private final Map<PiGraph, List<ActorByGraphReplacement>> graphReplacements = new LinkedHashMap<>();

  /** The current graph. */
  private PiGraph currentGraph = null;

  /**
   * Connect subgraphs.
   *
   * @param pg
   *          the graph process
   */
  private void connectSubgraphs(final PiGraph pg) {
    doSwitch(pg);
    // Replace Actors with refinement by PiGraphs in pg and all its
    // subgraphs

    for (final Entry<PiGraph, List<ActorByGraphReplacement>> e : this.graphReplacements.entrySet()) {
      for (final ActorByGraphReplacement r : e.getValue()) {
        e.getKey().getVertices().remove(r.toBeRemoved);
        e.getKey().addActor(r.toBeAdded);

      }
    }
  }

  @Override
  public Boolean casePiGraph(final PiGraph pg) {
    final PiGraph oldGraph = this.currentGraph;
    this.currentGraph = pg;
    for (final AbstractActor v : pg.getActors()) {
      doSwitch(v);
    }

    this.currentGraph = oldGraph;
    return true;
  }

  @Override
  public Boolean caseActor(final Actor a) {
    // If the refinement of the Actor a points to the description of
    // PiGraph, visit it to connect the subgraph to its supergraph
    if (a.isHierarchical()) {
      final PiGraph innerGraph = a.getSubGraph();
      if (innerGraph != null) {
        // Connect all Fifos and Dependencies incoming into a and outgoing
        // from a in order to make them incoming into innerGraph and
        // outgoing from innerGraph instead
        SubgraphReconnector.reconnectPiGraph(a, innerGraph);

        doSwitch(innerGraph);

        final ActorByGraphReplacement replacement = new ActorByGraphReplacement(a, innerGraph);
        if (!this.graphReplacements.containsKey(this.currentGraph)) {
          this.graphReplacements.put(this.currentGraph, new ArrayList<ActorByGraphReplacement>());
        }
        this.graphReplacements.get(this.currentGraph).add(replacement);
      }
    }
    return true;
  }

  /**
   * Reconnect pi graph.
   *
   * @param hierarchicalActor
   *          the actor
   * @param subGraph
   *          the subgraph linked to the hierarchical actor
   */
  private static void reconnectPiGraph(final Actor hierarchicalActor, final PiGraph subGraph) {
    SubgraphOriginalActorTracker.trackOriginalActor(hierarchicalActor, subGraph);
    // as we keep the PiGraph subgraphs instead of the hierarchical actor,
    // we do not need to set again their graphPort, {@link org.preesm.model.pisdf.adapter.GraphInterfaceObserver}
    SubgraphReconnector.reconnectDataInputPorts(hierarchicalActor, subGraph);
    SubgraphReconnector.reconnectDataOutputPorts(hierarchicalActor, subGraph);
    SubgraphReconnector.reconnectConfigInputPorts(hierarchicalActor, subGraph);
    SubgraphReconnector.reconnectConfigOutputPorts(hierarchicalActor, subGraph);
  }

  private static void reconnectConfigOutputPorts(final Actor hierarchicalActor, final PiGraph subGraph) {
    boolean found;
    for (final ConfigOutputPort cop1 : hierarchicalActor.getConfigOutputPorts()) {
      found = false;
      for (final ConfigOutputPort cop2 : subGraph.getConfigOutputPorts()) {
        if (cop1.getName().equals(cop2.getName())) {
          final Fifo fifo = cop1.getOutgoingFifo();
          if (fifo != null) {
            cop2.setOutgoingFifo(fifo);
            fifo.setSourcePort(cop2);
            cop2.setExpression(cop1.getPortRateExpression().getExpressionAsString());
            cop2.setAnnotation(cop1.getAnnotation());
          }
          final List<Dependency> depsToReconnect = new ArrayList<>(cop1.getOutgoingDependencies());
          for (final Dependency dep : depsToReconnect) {
            // this will also automatically set the setter of the dep to cop2
            // and remove the dep from the list of outgoing deps of cop1
            // (this is why we need to copy the list first)
            cop2.getOutgoingDependencies().add(dep);
          }

          found = true;
          break;
        }
      }
      if (!found) {
        SubgraphReconnector.error(hierarchicalActor, subGraph, cop1);
      }
    }
  }

  private static void reconnectConfigInputPorts(final Actor hierarchicalActor, final PiGraph subGraph) {
    for (final ConfigInputPort topGraphActorConfigInputPort : hierarchicalActor.getConfigInputPorts()) {
      boolean found = false;
      for (final ConfigInputPort subGraphConfigInputPorts : subGraph.getConfigInputPorts()) {
        if (topGraphActorConfigInputPort.getName().equals(subGraphConfigInputPorts.getName())) {
          final Dependency topGraphDep = topGraphActorConfigInputPort.getIncomingDependency();
          found = (topGraphDep != null);
          if (found) {
            subGraphConfigInputPorts.setIncomingDependency(topGraphDep);
            topGraphDep.setGetter(subGraphConfigInputPorts);
          }
          break;
        }
      }
      if (!found) {
        SubgraphReconnector.error(hierarchicalActor, subGraph, topGraphActorConfigInputPort);
      }
    }
  }

  private static void reconnectDataOutputPorts(final Actor hierarchicalActor, final PiGraph subGraph) {
    boolean found;
    for (final DataOutputPort dop1 : hierarchicalActor.getDataOutputPorts()) {
      found = false;
      for (final DataOutputPort dop2 : subGraph.getDataOutputPorts()) {
        if (dop1.getName().equals(dop2.getName())) {
          final Fifo fifo = dop1.getOutgoingFifo();
          if (fifo != null) {
            dop2.setOutgoingFifo(fifo);
            fifo.setSourcePort(dop2);

            dop2.setExpression(dop1.getPortRateExpression().getExpressionAsString());
            dop2.setAnnotation(dop1.getAnnotation());
          }
          found = true;
          break;
        }
      }
      if (!found) {
        SubgraphReconnector.error(hierarchicalActor, subGraph, dop1);
      }
    }
  }

  private static void reconnectDataInputPorts(final Actor hierarchicalActor, final PiGraph subGraph) {
    boolean found;
    for (final DataInputPort dip1 : hierarchicalActor.getDataInputPorts()) {
      found = false;
      for (final DataInputPort dip2 : subGraph.getDataInputPorts()) {
        if (dip1.getName().equals(dip2.getName())) {
          final Fifo fifo = dip1.getIncomingFifo();
          if (fifo != null) {
            dip2.setIncomingFifo(fifo);
            fifo.setTargetPort(dip2);

            dip2.setExpression(dip1.getPortRateExpression().getExpressionAsString());
            dip2.setAnnotation(dip1.getAnnotation());
          }
          found = true;
          break;
        }
      }
      if (!found) {
        SubgraphReconnector.error(hierarchicalActor, subGraph, dip1);
      }
    }
  }

  private static void error(final Actor hierarchicalActor, final PiGraph subGraph, final Port port) {
    throw new PreesmRuntimeException("PiGraph '" + subGraph.getName() + "' does not have a corresponding "
        + port.getClass().getSimpleName() + " named '" + port.getName() + "' for Actor " + hierarchicalActor.getName());
  }

  public static void reconnectChildren(PiGraph graph) {
    final SubgraphReconnector connector = new SubgraphReconnector();
    connector.connectSubgraphs(graph);
  }
}
