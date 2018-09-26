package org.ietr.preesm.pimm.algorithm.pimmoptims;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

/**
 * 
 * @author farresti
 *
 * @param <T>
 *          Type of Data port to handle
 */
public abstract class AbstractPiGraphSpecialActorRemover<T extends DataPort> {

  private final List<T>               dataPortsToRemove  = new ArrayList<>();
  private final Map<Integer, List<T>> dataPortsToReplace = new LinkedHashMap<>();
  private Integer                     portOffset         = 0;

  /**
   */
  public abstract boolean remove(final PiGraph graph, final AbstractActor actor);

  protected void fillRemoveAndReplace(final List<T> oldDataPorts, final List<T> newDataPorts, final T port) {
    final int index = oldDataPorts.indexOf(port) + this.portOffset;
    this.portOffset += newDataPorts.size() - 1;
    this.dataPortsToRemove.add(port);
    this.dataPortsToReplace.put(index, newDataPorts);
  }

  protected void removeActorAndFifo(final PiGraph graph, final Fifo fifo, final AbstractActor actor) {
    graph.removeActor(actor);
    graph.removeFifo(fifo);
  }

  protected boolean removeAndReplace(final List<T> dataPorts) {
    this.dataPortsToRemove.forEach(dataPorts::remove);
    this.dataPortsToReplace.forEach(dataPorts::addAll);
    final boolean retValue = !this.dataPortsToReplace.isEmpty();
    this.dataPortsToRemove.clear();
    this.dataPortsToReplace.clear();
    return retValue;
  }

  /**
   * Remove a special actor with only one input / output and with same rates.
   * 
   * @param graph
   *          The graph in which the actor is.
   * @param actor
   *          The actor to analyze
   * @return true if the actor was removed, false else
   */
  protected boolean removeUnused(final PiGraph graph, final AbstractActor actor) {
    if (actor.getDataInputPorts().size() == 1 && actor.getDataOutputPorts().size() == 1) {
      // 0. Get input rate
      final DataInputPort dataInputPort = actor.getDataInputPorts().get(0);
      final Expression inputRateExpression = dataInputPort.getPortRateExpression();
      final long inputRate = Long.parseLong(inputRateExpression.getExpressionString());
      // 1. Get output rate
      final DataOutputPort dataOutputPort = actor.getDataOutputPorts().get(0);
      final Expression outputRateExpression = dataOutputPort.getPortRateExpression();
      final long outputRate = Long.parseLong(outputRateExpression.getExpressionString());
      if (inputRate == outputRate) {
        // 2. We can remove one of the FIFO and the actor
        final Fifo outgoingFifo = dataOutputPort.getOutgoingFifo();
        final Fifo incomingFifo = dataInputPort.getIncomingFifo();
        outgoingFifo.setSourcePort(incomingFifo.getSourcePort());
        graph.removeFifo(incomingFifo);
        graph.removeActor(actor);
        return true;
      }
    }
    return false;
  }
}
