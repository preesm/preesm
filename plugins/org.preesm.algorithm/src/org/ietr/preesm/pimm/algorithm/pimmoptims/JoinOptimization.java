/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
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
/**
 *
 */
package org.ietr.preesm.pimm.algorithm.pimmoptims;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.ietr.preesm.pimm.algorithm.helper.PiMMHelperException;
import org.ietr.preesm.pimm.algorithm.pimm2srdag.visitor.PiMMSRVerticesLinker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.workflow.WorkflowException;

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
      final DataOutputPort sourcePort = incomingFifo.getSourcePort();
      final AbstractActor sourceActor = sourcePort.getContainingActor();
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
