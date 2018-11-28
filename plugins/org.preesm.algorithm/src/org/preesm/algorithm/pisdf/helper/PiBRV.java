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
package org.preesm.algorithm.pisdf.helper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;

/**
 * @author farresti
 *
 */
public abstract class PiBRV {
  /*
   * Repetition Vector value fully linked to an AbstractVertex
   */
  /** The graph handler structure. */
  protected PiMMHandler piHandler;

  /**
   * Instantiates a new PiBRV object
   *
   * @param piHandler
   *          PiSDF graph handler on which we are working
   */
  public PiBRV(final PiMMHandler piHandler) {
    this.piHandler = piHandler;
  }

  /**
   * Compute the BRV of the associated graph given a method. This also checks for consistency at the same time.
   *
   * @return true if no error were found, false else
   * @throws PiMMHelperException
   *           the PiMMHandlerException exception
   */
  public abstract Map<AbstractVertex, Long> computeBRV(final PiGraph piGraph);

  protected Map<AbstractVertex, Long> computeChildrenBRV(final PiGraph parentGraph) {
    final Map<AbstractVertex, Long> resultBrv = new LinkedHashMap<>();
    parentGraph.getChildrenGraphs().forEach(childGraph -> resultBrv.putAll(this.computeBRV(childGraph)));
    return resultBrv;
  }

  protected void updateRVWithInterfaces(final PiGraph graph, final List<AbstractActor> subgraph,
      final Map<AbstractVertex, Long> graphBRV) {
    // Update RV values based on the interface
    long scaleFactor = 1;

    // Compute scaleFactor for input interfaces
    scaleFactor = getInputInterfacesScaleFactor(graph, subgraph, scaleFactor, graphBRV);

    // Compute scaleFactor for output interfaces
    scaleFactor = getOutputInterfacesScaleFactor(graph, subgraph, scaleFactor, graphBRV);

    // Do the actual update
    for (final AbstractActor actor : subgraph) {
      final long newRV = graphBRV.get(actor) * scaleFactor;
      graphBRV.put(actor, newRV);
      if ((actor instanceof DelayActor) && (newRV != 1)) {
        throw new PreesmException("Inconsistent graph. DelayActor [" + actor.getName()
            + "] with a repetition vector of " + Long.toString(newRV));
      }
    }
  }

  /**
   * Compute the scale factor to apply to RV values based on DataInputInterfaces
   *
   * @param graph
   *          the graph
   * @param subgraph
   *          the current connected component
   * @param scaleFactor
   *          the current scaleFactor
   * @return new value of scale factor
   */
  private long getOutputInterfacesScaleFactor(final PiGraph graph, final List<AbstractActor> subgraph,
      final long inscaleFactor, final Map<AbstractVertex, Long> graphBRV) {
    long scaleFactor = inscaleFactor;
    for (final DataOutputInterface out : graph.getDataOutputInterfaces()) {
      final DataInputPort dataInputPort = (DataInputPort) out.getDataPort();
      final Fifo fifo = dataInputPort.getIncomingFifo();
      final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
      if (!(sourceActor instanceof InterfaceActor) && subgraph.contains(sourceActor)) {
        final long prod = fifo.getSourcePort().getPortRateExpression().evaluate();
        final long cons = fifo.getTargetPort().getPortRateExpression().evaluate();
        final long sourceRV = graphBRV.get(sourceActor);
        final long tmp = scaleFactor * prod * sourceRV;
        if (tmp < cons) {
          final long scaleScaleFactor = cons / tmp;
          if ((scaleScaleFactor * tmp) < cons) {
            scaleFactor *= (scaleScaleFactor + 1);
          } else {
            scaleFactor *= scaleScaleFactor;
          }
        }
      }
    }
    return scaleFactor;
  }

  /**
   * Compute the scale factor to apply to RV values based on DataOutputInterfaces
   *
   * @param graph
   *          the graph
   * @param subgraph
   *          the current connected component
   * @param scaleFactor
   *          the current scaleFactor
   * @return new value of scale factor
   */
  private long getInputInterfacesScaleFactor(final PiGraph graph, final List<AbstractActor> subgraph,
      final long inscaleFactor, final Map<AbstractVertex, Long> graphBRV) {
    long scaleFactor = inscaleFactor;
    for (final DataInputInterface in : graph.getDataInputInterfaces()) {
      final DataOutputPort dataOutputPort = (DataOutputPort) in.getDataPort();
      final Fifo fifo = dataOutputPort.getOutgoingFifo();
      final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
      if (!(targetActor instanceof InterfaceActor) && subgraph.contains(targetActor)) {
        final long targetRV = graphBRV.get(targetActor);
        final long prod = fifo.getSourcePort().getPortRateExpression().evaluate();
        final long cons = fifo.getTargetPort().getPortRateExpression().evaluate();
        final long tmp = scaleFactor * cons * targetRV;
        if (tmp < prod) {
          final long scaleScaleFactor = prod / tmp;
          if ((scaleScaleFactor * tmp) < prod) {
            scaleFactor *= (scaleScaleFactor + 1);
          } else {
            scaleFactor *= scaleScaleFactor;
          }
        }
      }
    }
    return scaleFactor;
  }

}
