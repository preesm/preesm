/**
 * 
 */
package org.ietr.preesm.pimm.algorithm.pimmoptims;

import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

/**
 * @author farresti
 *
 */
public class ForkOptimization extends AbstractPiGraphSpecialActorRemover<DataOutputPort> {

  /**
   * Remove the Broadcast or Fork -> Fork connections
   * 
   * <pre>
   *               | F | -> out_0 
   * | BR | -> out |   | -> out_1
   * 
   * becomes  | BR | -> out_0
   *          |    | -> out_1
   * </pre>
   * 
   * @param graph
   *          the graph
   * @param actor
   *          the broadcast or fork actor to evaluate
   * 
   * @return true if at least one ForkActor has been removed, false else
   */
  @Override
  public final boolean remove(final PiGraph graph, final AbstractActor actor) {
    if (graph == null) {
      return false;
    }
    for (final DataOutputPort dop : actor.getDataOutputPorts()) {
      final Fifo outgoingFifo = dop.getOutgoingFifo();
      final DataInputPort targetPort = outgoingFifo.getTargetPort();
      final AbstractActor targetActor = targetPort.getContainingActor();
      if (targetActor instanceof ForkActor) {
        fillRemoveAndReplace(actor.getDataOutputPorts(), targetActor.getDataOutputPorts(), dop);
        removeActorAndFifo(graph, outgoingFifo, targetActor);
      }
    }
    if (!removeAndReplace(actor.getDataOutputPorts())) {
      return removeUnused(graph, actor);
    }
    return true;
  }

}
