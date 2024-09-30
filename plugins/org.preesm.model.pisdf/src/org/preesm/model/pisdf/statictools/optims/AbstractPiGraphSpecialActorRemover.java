/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
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
package org.preesm.model.pisdf.statictools.optims;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.statictools.PiMMHelper;

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
    // Adding short suffixe
    newDataPorts.forEach(d -> d.setName(d.getName() + "_" + Integer.toString(this.portOffset)));
    this.dataPortsToRemove.add(port);
    this.dataPortsToReplace.put(index, newDataPorts);
  }

  protected boolean removeAndReplaceDataPorts(final List<T> dataPorts) {
    this.dataPortsToRemove.forEach(dataPorts::remove);
    this.dataPortsToReplace.forEach(dataPorts::addAll);
    final boolean retValue = !dataPortsToReplace.isEmpty();
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
  public static boolean removeUnused(final PiGraph graph, final AbstractActor actor) {
    if (actor.getDataInputPorts().size() == 1 && actor.getDataOutputPorts().size() == 1) {
      // 0. Get input rate
      final DataInputPort dataInputPort = actor.getDataInputPorts().get(0);
      final Fifo dipFifo = dataInputPort.getFifo();
      if (dipFifo == null) {
        return false;
      }
      final Expression inputRateExpression = dataInputPort.getPortRateExpression();
      final long inputRate = inputRateExpression.evaluateAsLong();
      // 1. Get output rate
      final DataOutputPort dataOutputPort = actor.getDataOutputPorts().get(0);
      final Fifo dopFifo = dataOutputPort.getFifo();
      if (dopFifo == null) {
        return false;
      }
      final Expression outputRateExpression = dataOutputPort.getPortRateExpression();
      final long outputRate = outputRateExpression.evaluateAsLong();
      if (inputRate == outputRate) {
        // 2. We can remove one of the FIFO and the actor
        if (dipFifo.getDelay() == null) {
          dopFifo.setSourcePort(dipFifo.getSourcePort());
          graph.removeFifo(dipFifo);
        } else if (dopFifo.getDelay() == null) {
          dipFifo.setTargetPort(dopFifo.getTargetPort());
          graph.removeFifo(dopFifo);
        } else {
          return false;
        }
        PiMMHelper.removeActorAndDependencies(graph, actor);
        return true;
      }
    }
    return false;
  }
}
