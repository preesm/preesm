/**
 * 
 */
package org.ietr.preesm.pimm.algorithm.math;

import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.lang3.math.Fraction;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.experiment.model.expression.ExpressionEvaluator;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.pimm.algorithm.math.PiMMHandler.PiMMHandlerException;
import org.ietr.preesm.throughput.tools.helpers.MathFunctionsHelper;

/**
 * This class is used to compute the basic repetition vector of a static PiSDF graph using LCM method.
 * 
 * @author farresti
 */
public class LCMBasedBRV extends PiBRV {

  public LCMBasedBRV(final PiMMHandler piHandler) {
    super(piHandler);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ietr.preesm.pimm.algorithm.math.PiBRV#execute()
   */
  @Override
  public boolean execute() throws PiMMHandlerException {
    if (this.piHandler.getReferenceGraph() == null) {
      WorkflowLogger.getLogger().log(Level.SEVERE, "cannot compute BRV for null graph.");
      return false;
    }

    // Get all sub graph composing the current graph
    final List<List<AbstractActor>> subgraphsWOInterfaces = this.piHandler.getAllSubgraphsWOInterfaces();

    for (final List<AbstractActor> subgraph : subgraphsWOInterfaces) {
      Hashtable<String, Fraction> reps = new Hashtable<>();
      // Initializes all reps to 0
      for (final AbstractActor actor : subgraph) {
        reps.put(actor.getName(), Fraction.getFraction(0));
      }

      // Pick the first non interface actor and do a Depth First Search to compute RV
      SetReps(subgraph.get(0), Fraction.getFraction(1), reps);

      // Computes the LCM of the denominators
      double lcm = 1;
      for (Fraction f : reps.values()) {
        lcm = MathFunctionsHelper.lcm(lcm, f.getDenominator());
      }

      // Set actors repetition factor
      for (AbstractActor actor : subgraph) {
        final int num = reps.get(actor.getName()).getNumerator();
        final int denom = reps.get(actor.getName()).getDenominator();
        final int rv = (int) ((num * lcm) / denom);
        this.graphBRV.put(actor, rv);
      }

      // Edge condition verification
      for (final Fifo f : this.piHandler.getFifosFromSubgraph(subgraph)) {
        final AbstractActor sourceActor = f.getSourcePort().getContainingActor();
        final AbstractActor targetActor = f.getTargetPort().getContainingActor();
        // TODO optimize this non sense.
        // have one structure of subgraph containing FIFOs and actors
        // if (!listActor.contains(sourceActor) || !listActor.contains(targetActor)) {
        // continue;
        // }
        if (targetActor instanceof InterfaceActor || sourceActor instanceof InterfaceActor) {
          continue;
        }
        int prod = (int) (ExpressionEvaluator.evaluate(f.getSourcePort().getPortRateExpression()));
        int cons = (int) (ExpressionEvaluator.evaluate(f.getTargetPort().getPortRateExpression()));
        int sourceRV = this.graphBRV.get(sourceActor);
        int targetRV = this.graphBRV.get(targetActor);
        if (sourceRV * prod != targetRV * cons) {
          PiMMHandler hdl = new PiMMHandler();
          throw hdl.new PiMMHandlerException(
              "Graph non consistent: edge source production " + Integer.toString(prod) + "!= edge target consumption " + Integer.toString(cons));
        }
      }

      // Update BRV values with interfaces
      updateRVWithInterfaces(this.piHandler.getReferenceGraph(), subgraph);
    }

    // Recursively compute BRV of sub-graphs
    // TODO maybe optimize this a recursive call to a secondary recursive method executeRec(final PiGraph graph)
    for (final PiMMHandler g : this.piHandler.getChildrenGraphsHandler()) {
      LCMBasedBRV lcmBRV = new LCMBasedBRV(g);
      lcmBRV.execute();
      this.graphBRV.putAll(lcmBRV.getBRV());
    }
    return true;
  }

  /**
   * Iterative function of execute.
   *
   * @param actor
   *          the current actor
   * @param n
   *          the current fraction
   * @param reps
   *
   * @throws PiBRVException
   *           the PiBRV exception
   */
  private static void SetReps(final AbstractActor actor, Fraction n, Hashtable<String, Fraction> reps) throws PiMMHandlerException {
    // Update value in the hashtable
    reps.put(actor.getName(), n);

    // DFS forward
    for (DataOutputPort output : actor.getDataOutputPorts()) {
      Fifo fifo = output.getOutgoingFifo();
      if (fifo == null) {
        PiMMHandler hdl = new PiMMHandler();
        throw hdl.new PiMMHandlerException("Actor [" + actor.getName() + "] has output port [" + output.getName() + "] not connected to any FIFO.");
      }
      final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
      if (targetActor instanceof InterfaceActor) {
        continue;
      }
      Fraction fa = reps.get(targetActor.getName());
      if (fa.getNumerator() == 0) {
        int cons = (int) (ExpressionEvaluator.evaluate(fifo.getTargetPort().getPortRateExpression()));
        int prod = (int) (ExpressionEvaluator.evaluate(fifo.getSourcePort().getPortRateExpression()));
        Fraction f = Fraction.getFraction(n.getNumerator() * prod, n.getDenominator() * cons);
        SetReps(targetActor, f.reduce(), reps);
      }
    }

    // DFS backward
    for (DataInputPort input : actor.getDataInputPorts()) {
      Fifo fifo = input.getIncomingFifo();
      if (fifo == null) {
        PiMMHandler hdl = new PiMMHandler();
        throw hdl.new PiMMHandlerException("Actor [" + actor.getName() + "] has input port [" + input.getName() + "] not connected to any FIFO.");
      }
      final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
      if (sourceActor instanceof InterfaceActor) {
        continue;
      }
      Fraction fa = reps.get(sourceActor.getName());
      if (fa.getNumerator() == 0) {
        int cons = (int) (ExpressionEvaluator.evaluate(fifo.getTargetPort().getPortRateExpression()));
        int prod = (int) (ExpressionEvaluator.evaluate(fifo.getSourcePort().getPortRateExpression()));
        Fraction f = Fraction.getFraction(n.getNumerator() * cons, n.getDenominator() * prod);
        SetReps(sourceActor, f.reduce(), reps);
      }
    }
  }
}
