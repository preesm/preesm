/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019 - 2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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
package org.preesm.model.pisdf.statictools.optims;

import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.statictools.PiMMHelper;

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
    if (graph == null || actor == null) {
      return false;
    }
    for (final DataOutputPort dop : actor.getDataOutputPorts()) {
      final Fifo outgoingFifo = dop.getOutgoingFifo();
      if (outgoingFifo.getDelay() != null) {
        continue;
      }
      final DataInputPort targetPort = outgoingFifo.getTargetPort();
      final AbstractActor targetActor = targetPort.getContainingActor();
      if (targetActor instanceof ForkActor
          && dop.getExpression().evaluateAsLong() == targetPort.getExpression().evaluateAsLong()) {
        fillRemoveAndReplace(actor.getDataOutputPorts(), targetActor.getDataOutputPorts(), dop);
        PiMMHelper.removeActorAndFifo(graph, outgoingFifo, targetActor);
      }
    }

    if (!removeAndReplaceDataPorts(actor.getDataOutputPorts())) {
      return removeUnused(graph, actor);
    }
    return true;
  }

}
