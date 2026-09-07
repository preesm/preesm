package org.preesm.algorithm.synthesis.memalloc.passiveactors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PassiveActor;
import org.preesm.model.pisdf.PassiveInputPort;
import org.preesm.model.pisdf.PassiveOutputPort;
import org.preesm.model.pisdf.PassivePort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 * This class merge adjacent passive actors, to reduce even more the memroy size of the application. The entry point
 * method to make the fusion is the method {@link #doFusion() doFusion}.
 *
 * @author rcazoulat
 */
public class PassiveActorComposer extends PiMMSwitch<Boolean> {

  PiGraph             currentGraph     = null;
  List<AbstractActor> explored;
  PassiveScriptRunner psr;
  int                 offset           = 0;
  int                 uniquePortNameId = 0;

  public PassiveActorComposer(final PassiveScriptRunner psr) {
    explored = new ArrayList<>();
    this.psr = psr;
  }

  /**
   * Entry point method to merge passive actors
   *
   * @param topGraph
   *          the graph to process. It can be hierarchical.
   */
  public void doFusion(PiGraph topGraph) {
    doSwitch(topGraph);
  }

  @Override
  public Boolean casePiGraph(PiGraph graph) {

    PreesmLogger.getLogger().info("in casePiGraph with " + graph.getName());

    graph.getChildrenGraphs().stream().forEach(c -> doSwitch(c));

    currentGraph = graph;

    doSwitch(graph.getActors().getFirst());

    return true;
  }

  @Override
  public Boolean caseAbstractActor(AbstractActor actor) {

    PreesmLogger.getLogger().info("in caseAbstractActor with " + actor.getName());

    explored.add(actor);

    List<AbstractActor> neighbors = getUnknownNeighbors(actor);
    while (!neighbors.isEmpty()) {
      doSwitch(neighbors.getFirst());
      neighbors = getUnknownNeighbors(actor);
    }

    return true;
  }

  @Override
  public Boolean casePassiveActor(PassiveActor actor) {

    PreesmLogger.getLogger().info("in casePassiveActor with " + actor.getName());

    uniquePortNameId = 0;
    PassiveActor result = actor;

    List<PassiveActor> passiveNeighbors = getPassiveNeighbors(result);

    while (!passiveNeighbors.isEmpty()) {
      final PassiveActor passiveNeighbor = passiveNeighbors.getFirst();
      result = fuse(result, passiveNeighbor);
      passiveNeighbors = getPassiveNeighbors(result);
    }

    return caseAbstractActor(result);
  }

  /**
   *
   * @param actor
   *          currentActor
   * @return list of neighbors
   */
  private List<AbstractActor> getNeighbors(AbstractActor actor) {
    return actor.getAllDataPorts().stream().map(p -> p.getOppositePort().getContainingActor()).toList();
  }

  private List<AbstractActor> getUnknownNeighbors(AbstractActor actor) {
    return getNeighbors(actor).stream().filter(a -> !explored.contains(a)).toList();
  }

  /**
   *
   * @param actor
   *          currentActor
   * @return list of neighbors
   */
  private List<PassiveActor> getPassiveNeighbors(AbstractActor actor) {
    return getNeighbors(actor).stream().filter(PassiveActor.class::isInstance).map(a -> (PassiveActor) a).toList();
  }

  /**
   *
   * @param actor1
   *          first actor to fuse
   * @param actor2
   *          second actor to fuse
   * @return the result of the fusion, can be the same instance as actor1 if it is already a composed passive actor.
   *         Otherwise, it will be a new composed passive actor.
   */
  private PassiveActor fuse(PassiveActor actor1, PassiveActor actor2) {

    // Verifying that actors are linked by one and only one fifo
    final List<Fifo> linkingFifos = new ArrayList<>();
    for (final DataPort port : actor1.getAllDataPorts()) {
      if (port.getOppositePort().getContainingActor() == actor2) {
        linkingFifos.add(port.getFifo());
      }
    }
    final String generalLog = "Error while fusing 2 passive actors. Actor " + actor1.getName() + " and actor "
        + actor2.getName() + "must have one and only one fifo connecting them.";

    if (linkingFifos.isEmpty()) {
      throw new PreesmRuntimeException(generalLog + " Right now, they are not linked by any fifos.");
    }
    if (linkingFifos.size() > 1) {
      throw new PreesmRuntimeException(generalLog
          + " Right now, they are linked by more than one fifo, it is not supported by PREESM's current version.");
    }

    final Fifo linkingFifo = linkingFifos.getFirst();
    final PassivePort linkingPortActor1 = (PassivePort) (linkingFifo.getSource() == actor1 ? linkingFifo.getSourcePort()
        : linkingFifo.getTargetPort());
    final PassivePort linkingPortActor2 = (PassivePort) linkingPortActor1.getOppositePort();

    final int offset1 = psr.getBeginEnd(linkingPortActor1).getKey();
    final int offset2 = psr.getBeginEnd(linkingPortActor2).getKey();

    actor1.getAllPassivePorts().stream().forEach(p -> p.setOffset(p.getOffset() + offset2));
    actor2.getAllPassivePorts().stream().forEach(p -> p.setOffset(p.getOffset() + offset1));

    for (final PassivePort port : actor2.getAllPassivePorts()) {
      if (port == linkingPortActor2) {
        continue;
      }
      port.setName(port.getName() + "_" + this.uniquePortNameId++);
      if (port instanceof final PassiveInputPort pip) {
        actor1.getPassiveInputPorts().add(pip);
      } else if (port instanceof final PassiveOutputPort pop) {
        actor1.getPassiveOutputPorts().add(pop);
      }
    }

    for (final ConfigInputPort port : actor2.getConfigInputPorts()) {

      // TODO compare dependency instead of name ?
      final boolean alreadyHere = actor1.getConfigInputPorts().stream()
          .anyMatch(port1 -> Objects.equals(port1.getName(), port.getName()));
      if (!alreadyHere) {
        actor1.getConfigInputPorts().add(port);
      }
    }
    for (final ConfigOutputPort port : actor2.getConfigOutputPorts()) {

      // TODO check if not already here
      actor1.getConfigOutputPorts().add(port);
    }

    currentGraph.removeActor(actor2);
    currentGraph.removeFifo(linkingFifo);
    if (linkingPortActor1 instanceof PassiveInputPort) {
      actor1.getPassiveInputPorts().remove(linkingPortActor1);
    } else if (linkingPortActor1 instanceof PassiveOutputPort) {
      actor1.getPassiveOutputPorts().remove(linkingPortActor1);
    }
    actor1.setName(actor1.getName() + "_" + actor2.getName());

    return actor1;
  }

}
