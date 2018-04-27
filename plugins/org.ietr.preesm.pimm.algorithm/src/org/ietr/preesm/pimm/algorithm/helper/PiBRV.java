/**
 *
 */
package org.ietr.preesm.pimm.algorithm.helper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DelayActor;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

/**
 * @author farresti
 *
 */
public abstract class PiBRV {
  /*
   * Repetition Vector value fully linked to an AbstractVertex
   */
  protected Map<AbstractVertex, Integer> graphBRV;

  /** The graph handler structure. */
  protected PiMMHandler piHandler;

  /**
   * Instantiates a new PiBRV object
   *
   * @param piHandler
   *          PiSDF graph handler on which we are working
   */
  public PiBRV(final PiMMHandler piHandler) {
    this.graphBRV = new LinkedHashMap<>();
    this.piHandler = piHandler;
  }

  /**
   *
   * Set the associated PiGraph. If current BRV map was not empty, it is cleared. The BRV is automatically recomputed.
   */
  public void setAssociatedGraphHandler(final PiMMHandler piHandler) throws PiMMHelperException {
    this.piHandler = piHandler;
    if (!this.graphBRV.isEmpty()) {
      this.graphBRV.clear();
      execute();
    }
  }

  /**
   *
   * @return associated PiGraph
   */
  public PiMMHandler getAssociatedGraphHandler() {
    return this.piHandler;
  }

  /**
   * First call will be slower since it has to compute the BRV.
   *
   * @return a map of vertex and associated repetition vector values
   */
  public Map<AbstractVertex, Integer> getBRV() throws PiMMHelperException {
    if (this.graphBRV.isEmpty()) {
      execute();
    }
    return this.graphBRV;
  }

  /**
   * Getter of the Basic Repetition Vector (BRV) value of a given vertex.
   *
   * @param vertex
   *          vertex for which we get the BRV value
   * @return Repetition Vector value Integer associated with the vertex, null if the vertex is not in list
   */
  public Integer getBRVForVertex(final AbstractVertex vertex) {
    return this.graphBRV.get(vertex);
  }

  /**
   * Compute the BRV of the associated graph given a method. This also checks for consistency at the same time.
   *
   * @return true if no error were found, false else
   * @throws PiMMHelperException
   *           the PiMMHandlerException exception
   */
  public abstract boolean execute() throws PiMMHelperException;

  protected void updateRVWithInterfaces(final PiGraph graph, final List<AbstractActor> subgraph) throws PiMMHelperException {
    // Update RV values based on the interface
    int scaleFactor = 1;
    // Compute scaleFactor for input interfaces
    for (final DataInputInterface in : graph.getDataInputInterfaces()) {
      final Fifo fifo = ((DataOutputPort) in.getDataPort()).getOutgoingFifo();
      final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
      if (!(targetActor instanceof InterfaceActor) && subgraph.contains(targetActor)) {
        final int targetRV = this.graphBRV.get(targetActor);
        final int prod = Integer.parseInt(fifo.getSourcePort().getPortRateExpression().getExpressionString());
        final int cons = Integer.parseInt(fifo.getTargetPort().getPortRateExpression().getExpressionString());
        final int tmp = scaleFactor * cons * targetRV;
        if (tmp < prod) {
          final int scaleScaleFactor = prod / tmp;
          if ((scaleScaleFactor * tmp) < prod) {
            scaleFactor *= (scaleScaleFactor + 1);
          } else {
            scaleFactor *= scaleScaleFactor;
          }
        }
        // float prod = Float.parseFloat(fifo.getSourcePort().getPortRateExpression().getExpressionString());
        // float cons = Float.parseFloat(fifo.getTargetPort().getPortRateExpression().getExpressionString());
        // scaleFactor = Math.max(scaleFactor, (int) Math.ceil(prod / (cons * targetRV)));
      }
    }

    // Compute scaleFactor for output interfaces
    for (final DataOutputInterface out : graph.getDataOutputInterfaces()) {
      final Fifo fifo = ((DataInputPort) out.getDataPort()).getIncomingFifo();
      final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
      if (!(sourceActor instanceof InterfaceActor) && subgraph.contains(sourceActor)) {
        final int prod = Integer.parseInt(fifo.getSourcePort().getPortRateExpression().getExpressionString());
        final int cons = Integer.parseInt(fifo.getTargetPort().getPortRateExpression().getExpressionString());
        final int sourceRV = this.graphBRV.get(sourceActor);
        final int tmp = scaleFactor * prod * sourceRV;
        if (tmp < cons) {
          final int scaleScaleFactor = cons / tmp;
          if ((scaleScaleFactor * tmp) < cons) {
            scaleFactor *= (scaleScaleFactor + 1);
          } else {
            scaleFactor *= scaleScaleFactor;
          }
        }
        // float prod = Float.parseFloat(fifo.getSourcePort().getPortRateExpression().getExpressionString());
        // float cons = Float.parseFloat(fifo.getTargetPort().getPortRateExpression().getExpressionString());
        // scaleFactor = Math.max(scaleFactor, (int) Math.ceil(cons / (prod * sourceRV)));
      }
    }

    // Do the actual update
    for (final AbstractActor actor : subgraph) {
      final int newRV = this.graphBRV.get(actor) * scaleFactor;
      this.graphBRV.put(actor, newRV);
      if ((actor instanceof DelayActor) && (newRV != 1)) {
        throw new PiMMHelperException("Inconsistent graph. DelayActor [" + actor.getName() + "] with a repetition vector of " + Integer.toString(newRV));
      }
    }
  }

}
