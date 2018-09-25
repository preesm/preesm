/**
 * 
 */
package org.ietr.preesm.pimm.algorithm.pimmoptims;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.pimm.algorithm.helper.PiMMHelperException;
import org.ietr.preesm.pimm.algorithm.pimm2srdag.visitor.PiMMSRVerticesLinker;

/**
 * @author farresti
 *
 */
public class JoinOptimization extends AbstractPiGraphSpecialActorRemover<DataInputPort> {

  /**
   * Remove the Join -> RoundBuffer / Join connections
   * 
   * <pre>
   * in_0 -> | J | -> in | RB | 
   * in_1 -> |   |  
   * 
   * becomes  in_0 -> | RB | 
   *          in_1 -> |    |
   * </pre>
   * 
   * @param graph
   *          the graph
   * @param actor
   *          the RoundBuffer or join actor to evaluate
   * @return true if at least one JoinActor has been removed, false else
   */
  @Override
  public final boolean remove(final PiGraph graph, final AbstractActor actor) {
    if (graph == null) {
      return false;
    }
    for (final DataInputPort dip : actor.getDataInputPorts()) {
      final Fifo incomingFifo = dip.getIncomingFifo();
      final DataOutputPort targetPort = incomingFifo.getSourcePort();
      final AbstractActor sourceActor = targetPort.getContainingActor();
      if (sourceActor instanceof JoinActor) {
        fillRemoveAndReplace(actor.getDataInputPorts(), sourceActor.getDataInputPorts(), dip);
        removeActorAndFifo(graph, incomingFifo, sourceActor);
      }
    }
    if (!removeAndReplace(actor.getDataInputPorts())) {
      return removeUnused(graph, actor);
    }
    return true;
  }

  /**
   * Optimize Join to Fork connections
   * 
   * We first remove the Join and Fork actors and re-do the SR-Link between sources of the Join and the sinks of the
   * Fork.
   * 
   * @param graph
   *          The graph
   * @param actor
   *          The join actor
   * @return true if join is followed by a fork, false else
   */
  public final boolean removeJoinFork(final PiGraph graph, final JoinActor actor) {
    if (graph == null) {
      return false;
    }
    final DataOutputPort out = actor.getDataOutputPorts().get(0);
    final Fifo fifo = out.getFifo();
    final AbstractActor target = fifo.getTargetPort().getContainingActor();
    if (target instanceof ForkActor) {
      // We build the list of source and sink set
      final Map<DataOutputPort, AbstractVertex> sourceSet = new LinkedHashMap<>();
      final Map<DataInputPort, AbstractVertex> sinkSet = new LinkedHashMap<>();
      // 1. Construct the maps
      actor.getDataInputPorts().forEach(p -> sourceSet.put(p.getIncomingFifo().getSourcePort(),
          p.getIncomingFifo().getSourcePort().getContainingActor()));
      target.getDataOutputPorts().forEach(p -> sinkSet.put(p.getOutgoingFifo().getTargetPort(),
          p.getOutgoingFifo().getTargetPort().getContainingActor()));
      // 2. Remove all the FIFOs
      final List<Fifo> fifoToRemove = new ArrayList<>();
      actor.getDataInputPorts().forEach(p -> fifoToRemove.add(p.getFifo()));
      target.getDataOutputPorts().forEach(p -> fifoToRemove.add(p.getFifo()));

      // 3. We remove the JoinActor and the ForkActor and we re-do the SR-Link between the sources / sinks
      final PiMMSRVerticesLinker srLinker = new PiMMSRVerticesLinker();
      try {
        srLinker.execute(sourceSet, sinkSet);
      } catch (final PiMMHelperException e) {
        throw new WorkflowException(e.getMessage());
      }
      fifoToRemove.forEach(graph::removeFifo);
      graph.removeActor(actor);
      graph.removeActor(target);
      graph.removeFifo(fifo);
      return true;
    }
    return false;
  }
}
