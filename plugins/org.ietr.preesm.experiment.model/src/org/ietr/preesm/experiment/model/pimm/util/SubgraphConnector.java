/*******************************************************************************
 * Copyright or © or Copr. 2014 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2014 - 2016)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.experiment.model.pimm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.BroadcastActor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.HRefinement;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;

/**
 * Parse and connect hierarchical sub-{@link PiGraph} to a top level {@link PiGraph}
 *
 * @author cguy
 * @author kdesnos
 *
 */
public class SubgraphConnector extends PiMMVisitor {

  // Actor in the outer graph corresponding to the currently visited graph
  private AbstractActor currentActor = null;

  private final Map<PiGraph, List<ActorByGraphReplacement>> graphReplacements = new HashMap<>();

  public Map<PiGraph, List<ActorByGraphReplacement>> getGraphReplacements() {
    return this.graphReplacements;
  }

  private PiGraph currentGraph = null;

  /**
   *
   * @param pg the graph process
   */
  public void connectSubgraphs(final PiGraph pg) {
    pg.accept(this);
    // Replace Actors with refinement by PiGraphs in pg and all its
    // subgraphs
    for (final PiGraph key : this.graphReplacements.keySet()) {
      for (final SubgraphConnector.ActorByGraphReplacement r : this.graphReplacements.get(key)) {
        key.getVertices().remove(r.toBeRemoved);
        key.getVertices().add(r.toBeAdded);
      }
    }
  }

  @Override
  public void visitPiGraph(final PiGraph pg) {
    final PiGraph oldGraph = this.currentGraph;
    this.currentGraph = pg;
    for (final AbstractActor v : pg.getVertices()) {
      v.accept(this);
    }
    for (final Parameter p : pg.getParameters()) {
      p.accept(this);
    }
    this.currentGraph = oldGraph;
  }

  @Override
  public void visitActor(final Actor a) {
    // If the refinement of the Actor a points to the description of
    // PiGraph, visit it to connect the subgraph to its supergraph
    final AbstractActor aa = a.getRefinement().getAbstractActor();
    if ((aa != null) && (aa instanceof PiGraph)) {
      final PiGraph innerGraph = (PiGraph) aa;
      // Connect all Fifos and Dependencies incoming into a and outgoing
      // from a in order to make them incoming into innerGraph and
      // outgoing from innerGraph instead
      reconnectPiGraph(a, innerGraph);

      this.currentActor = innerGraph;
      innerGraph.accept(this);

      final ActorByGraphReplacement replacement = new ActorByGraphReplacement(a, innerGraph);
      if (!this.graphReplacements.containsKey(this.currentGraph)) {
        this.graphReplacements.put(this.currentGraph, new ArrayList<ActorByGraphReplacement>());
      }
      this.graphReplacements.get(this.currentGraph).add(replacement);
    }
  }

  /*
   * Connect all the ports of the PiGraph to the Fifos and Dependencies connected to the ports of the Actor
   */
  private void reconnectPiGraph(final Actor a, final PiGraph pg) {
    boolean found = false;
    for (final DataInputPort dip1 : a.getDataInputPorts()) {
      found = false;
      for (final DataInputPort dip2 : pg.getDataInputPorts()) {
        if (dip1.getName().equals(dip2.getName())) {
          final Fifo fifo = dip1.getIncomingFifo();
          dip2.setIncomingFifo(fifo);
          fifo.setTargetPort(dip2);

          dip2.setExpression(dip1.getExpression());
          dip2.setAnnotation(dip1.getAnnotation());

          found = true;
          break;
        }
      }
      if (!found) {
        throw new RuntimeException("PiGraph" + pg.getName() + "does not have a corresponding DataInputPort for " + dip1.getName() + " of Actor " + a.getName());
      }
    }
    for (final DataOutputPort dop1 : a.getDataOutputPorts()) {
      found = false;
      for (final DataOutputPort dop2 : pg.getDataOutputPorts()) {
        if (dop1.getName().equals(dop2.getName())) {
          final Fifo fifo = dop1.getOutgoingFifo();
          dop2.setOutgoingFifo(fifo);
          fifo.setSourcePort(dop2);

          dop2.setExpression(dop1.getExpression());
          dop2.setAnnotation(dop1.getAnnotation());

          found = true;
          break;
        }
      }
      if (!found) {
        throw new RuntimeException(
            "PiGraph" + pg.getName() + "does not have a corresponding DataOutputPort for " + dop1.getName() + " of Actor " + a.getName());
      }
    }
    for (final ConfigInputPort cip1 : a.getConfigInputPorts()) {
      found = false;
      for (final ConfigInputPort cip2 : pg.getConfigInputPorts()) {
        if (cip1.getName().equals(cip2.getName())) {
          final Dependency dep = cip1.getIncomingDependency();
          cip2.setIncomingDependency(dep);
          dep.setGetter(cip2);
          found = true;
          break;
        }
      }
      if (!found) {
        throw new RuntimeException(
            "PiGraph" + pg.getName() + "does not have a corresponding ConfigInputPort for " + cip1.getName() + " of Actor " + a.getName());
      }
    }
    for (final ConfigOutputPort cop1 : a.getConfigOutputPorts()) {
      found = false;
      for (final ConfigOutputPort cop2 : pg.getConfigOutputPorts()) {
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
        throw new RuntimeException(
            "PiGraph" + pg.getName() + "does not have a corresponding ConfigOutputPort for " + cop1.getName() + " of Actor " + a.getName());
      }
    }
  }

  @Override
  public void visitDataInputInterface(final DataInputInterface dii) {
    // Connect the interface to the incoming fifo from the outer graph, if
    // any
    if (this.currentActor != null) {
      DataInputPort correspondingPort = null;
      for (final DataInputPort dip : this.currentActor.getDataInputPorts()) {
        if (dip.getName() == dii.getName()) {
          correspondingPort = dip;
          break;
        }
      }
      if (correspondingPort != null) {
        dii.setGraphPort(correspondingPort);
      }
    }
  }

  @Override
  public void visitDataOutputInterface(final DataOutputInterface doi) {
    // Connect the interface to the outgoing fifo to the outer graph, if any
    if (this.currentActor != null) {
      DataOutputPort correspondingPort = null;
      for (final DataOutputPort dop : this.currentActor.getDataOutputPorts()) {
        if (dop.getName() == doi.getName()) {
          correspondingPort = dop;
          break;
        }
      }
      if (correspondingPort != null) {
        doi.setGraphPort(correspondingPort);
      }
    }
  }

  @Override
  public void visitConfigInputInterface(final ConfigInputInterface cii) {
    // Connect the interface to the incoming dependencies from the outer
    // graph
    ConfigInputPort correspondingPort = null;
    for (final ConfigInputPort cip : this.currentActor.getConfigInputPorts()) {
      if (cip.getName() == cii.getName()) {
        correspondingPort = cip;
        break;
      }
    }
    if (correspondingPort != null) {
      cii.setGraphPort(correspondingPort);
    }
  }

  @Override
  public void visitConfigOutputInterface(final ConfigOutputInterface coi) {
    // Connect the interface to the outgoing dependencies to the outer graph
    ConfigOutputPort correspondingPort = null;
    for (final ConfigOutputPort cop : this.currentActor.getConfigOutputPorts()) {
      if (cop.getName() == coi.getName()) {
        correspondingPort = cop;
        break;
      }
    }
    if (correspondingPort != null) {
      coi.setGraphPort(correspondingPort);
    }
  }

  @Override
  public void visitParameter(final Parameter p) {
    // We only do something for ConfigInputInterface (subclass of
    // Parameter), other parameters are visited but nothing should be done
    // DO NOTHING
  }

  @Override
  public void visitAbstractActor(final AbstractActor aa) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitAbstractVertex(final AbstractVertex av) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitConfigInputPort(final ConfigInputPort cip) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitConfigOutputPort(final ConfigOutputPort cop) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitDataPort(final DataPort p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitDataInputPort(final DataInputPort dip) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitDataOutputPort(final DataOutputPort dop) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitDelay(final Delay d) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitDependency(final Dependency d) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitExpression(final Expression e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitFifo(final Fifo f) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitInterfaceActor(final InterfaceActor ia) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitISetter(final ISetter is) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitParameterizable(final Parameterizable p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitPort(final Port p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitRefinement(final Refinement r) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitFunctionParameter(final FunctionParameter functionParameter) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitFunctionPrototype(final FunctionPrototype functionPrototype) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitBroadcastActor(final BroadcastActor ba) {
    // Do nothing
  }

  @Override
  public void visitJoinActor(final JoinActor ja) {
    // Do nothing
  }

  @Override
  public void visitForkActor(final ForkActor fa) {
    // Do nothing
  }

  @Override
  public void visitRoundBufferActor(final RoundBufferActor rba) {
    // Do nothing
  }

  @Override
  public void visitExecutableActor(final ExecutableActor ea) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitHRefinement(final HRefinement hRefinement) {
    throw new UnsupportedOperationException();
  }

  /**
   *
   */
  public class ActorByGraphReplacement {
    public Actor   toBeRemoved;
    public PiGraph toBeAdded;

    public ActorByGraphReplacement(final Actor toBeRemoved, final PiGraph toBeAdded) {
      this.toBeRemoved = toBeRemoved;
      this.toBeAdded = toBeAdded;
    }
  }
}
