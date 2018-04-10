/**
 * 
 */
package org.ietr.preesm.pimm.algorithm.math;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.ietr.preesm.experiment.model.expression.ExpressionEvaluator;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.pimm.algorithm.math.PiMMHandler.PiMMHandlerException;

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
  public void setAssociatedGraphHandler(final PiMMHandler piHandler) throws PiMMHandlerException {
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
  public Map<AbstractVertex, Integer> getBRV() throws PiMMHandlerException {
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
   * @throws PiBRVException
   *           the PiBRVException exception
   */
  public abstract boolean execute() throws PiMMHandlerException;

  protected void updateRVWithInterfaces(final PiGraph graph, final List<AbstractActor> subgraph) throws PiMMHandlerException {
    // Update RV values based on the interface
    int scaleFactor = 1;
    // Compute scaleFactor for input interfaces
    for (DataInputInterface in : graph.getDataInputInterfaces()) {
      final Fifo fifo = ((DataOutputPort) in.getDataPort()).getOutgoingFifo();
      final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
      if (!(targetActor instanceof InterfaceActor) && subgraph.contains(targetActor)) {
        float prod = (float) (ExpressionEvaluator.evaluate(fifo.getSourcePort().getPortRateExpression()));
        float cons = (float) (ExpressionEvaluator.evaluate(fifo.getTargetPort().getPortRateExpression()));
        int targetRV = this.graphBRV.get(targetActor);
        scaleFactor = Math.max(scaleFactor, (int) Math.ceil(prod / (cons * targetRV)));
      }
    }

    // Compute scaleFactor for output interfaces
    for (DataOutputInterface out : graph.getDataOutputInterfaces()) {
      final Fifo fifo = ((DataInputPort) out.getDataPort()).getIncomingFifo();
      final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
      if (!(sourceActor instanceof InterfaceActor) && subgraph.contains(sourceActor)) {
        float prod = (float) (ExpressionEvaluator.evaluate(fifo.getSourcePort().getPortRateExpression()));
        float cons = (float) (ExpressionEvaluator.evaluate(fifo.getTargetPort().getPortRateExpression()));
        int sourceRV = this.graphBRV.get(sourceActor);
        scaleFactor = Math.max(scaleFactor, (int) Math.ceil(prod / (cons * sourceRV)));
      }
    }

    // Do the actual update
    for (AbstractActor actor : subgraph) {
      int newRV = this.graphBRV.get(actor) * scaleFactor;
      this.graphBRV.put(actor, newRV);
      if ((actor instanceof Delay) && newRV != 1) {
        PiMMHandler hdl = new PiMMHandler();
        throw (hdl.new PiMMHandlerException("Inconsistent graph. Delay [" + actor.getName() + "] with a repetition vector of " + Integer.toString(newRV)));
      }
    }
  }

}
