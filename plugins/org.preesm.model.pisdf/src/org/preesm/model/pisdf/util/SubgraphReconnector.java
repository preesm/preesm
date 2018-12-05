/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2014 - 2016)
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
package org.preesm.model.pisdf.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputInterface;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;

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

  /** The current actor. */
  // Actor in the outer graph corresponding to the currently visited graph
  private AbstractActor currentActor = null;

  /** The graph replacements. */
  private final Map<PiGraph, List<ActorByGraphReplacement>> graphReplacements = new LinkedHashMap<>();

  /**
   * Gets the graph replacements.
   *
   * @return the graph replacements
   */
  public Map<PiGraph, List<ActorByGraphReplacement>> getGraphReplacements() {
    return this.graphReplacements;
  }

  /** The current graph. */
  private PiGraph currentGraph = null;

  /**
   * Connect subgraphs.
   *
   * @param pg
   *          the graph process
   */
  public void connectSubgraphs(final PiGraph pg) {
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
    for (final Parameter p : pg.getParameters()) {
      doSwitch(p);
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

        this.currentActor = innerGraph;
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

  @Override
  public Boolean caseDataInputInterface(final DataInputInterface dii) {
    // Connect the interface to the incoming fifo from the outer graph, if
    // any
    if (this.currentActor != null) {
      DataInputPort correspondingPort = null;
      for (final DataInputPort dip : this.currentActor.getDataInputPorts()) {
        if (dip.getName().equals(dii.getName())) {
          correspondingPort = dip;
          break;
        }
      }
      if (correspondingPort != null) {
        dii.setGraphPort(correspondingPort);
      }
    }
    return true;
  }

  @Override
  public Boolean caseDataOutputInterface(final DataOutputInterface doi) {
    // Connect the interface to the outgoing fifo to the outer graph, if any
    if (this.currentActor != null) {
      DataOutputPort correspondingPort = null;
      for (final DataOutputPort dop : this.currentActor.getDataOutputPorts()) {
        if (dop.getName().equals(doi.getName())) {
          correspondingPort = dop;
          break;
        }
      }
      if (correspondingPort != null) {
        doi.setGraphPort(correspondingPort);
      }
    }
    return true;
  }

  @Override
  public Boolean caseConfigInputInterface(final ConfigInputInterface cii) {
    // only reconnects if we parse a hierarchical actor and the current actor is the one containing the interface
    if ((this.currentActor != null) && (cii.getContainingGraph() == this.currentActor)) {
      // Connect the interface to the incoming dependencies from the outer
      // graph
      ConfigInputPort correspondingPort = null;
      for (final ConfigInputPort cip : this.currentActor.getConfigInputPorts()) {
        if (cip.getName().equals(cii.getName())) {
          correspondingPort = cip;
          break;
        }
      }

      if (correspondingPort != null) {
        cii.setGraphPort(correspondingPort);

      }
    }
    return true;
  }

  @Override
  public Boolean caseConfigOutputInterface(final ConfigOutputInterface coi) {
    // Connect the interface to the outgoing dependencies to the outer graph
    ConfigOutputPort correspondingPort = null;
    for (final ConfigOutputPort cop : this.currentActor.getConfigOutputPorts()) {
      if (cop.getName().equals(coi.getName())) {
        correspondingPort = cop;
        break;
      }
    }
    if (correspondingPort != null) {
      coi.setGraphPort(correspondingPort);
    }
    return true;
  }

  @Override
  public Boolean caseParameter(final Parameter p) {
    // We only do something for ConfigInputInterface (subclass of
    // Parameter), other parameters are visited but nothing should be done
    // DO NOTHING
    return true;
  }

  @Override
  public Boolean caseDelayActor(final DelayActor da) {
    return true;
  }

  @Override
  public Boolean caseExecutableActor(final ExecutableActor ea) {
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
  public static void reconnectPiGraph(final Actor hierarchicalActor, final PiGraph subGraph) {
    SubgraphOriginalActorTracker.trackOriginalActor(hierarchicalActor, subGraph);
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
          for (final Dependency dep : cop1.getOutgoingDependencies()) {
            cop2.getOutgoingDependencies().add(dep);
            dep.setSetter(cop2);
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
    throw new PreesmException("PiGraph '" + subGraph.getName() + "' does not have a corresponding "
        + port.getClass().getSimpleName() + " named '" + port.getName() + "' for Actor " + hierarchicalActor.getName());
  }
}
