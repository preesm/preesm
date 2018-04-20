/**
 * 
 */
package org.ietr.preesm.pimm.algorithm.helper;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.ietr.dftools.algorithm.Rational;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
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
  public boolean execute() throws PiMMHelperException {
    if (this.piHandler.getReferenceGraph() == null) {
      WorkflowLogger.getLogger().log(Level.SEVERE, "cannot compute BRV for null graph.");
      return false;
    }

    // Get all sub graph composing the current graph
    final List<List<AbstractActor>> subgraphsWOInterfaces = this.piHandler.getAllConnectedComponentsWOInterfaces();

    for (final List<AbstractActor> subgraph : subgraphsWOInterfaces) {
      Hashtable<String, Rational> reps = new Hashtable<>();
      // Initializes all reps to 0
      for (final AbstractActor actor : subgraph) {
        reps.put(actor.getName(), new Rational(0, 1));
      }

      // Construct the list of Edges without interfaces
      List<Fifo> listFifos = this.piHandler.getFifosFromCC(subgraph);
      // We have only one actor connected to Interface Actor
      // The graph is consistent
      // We just have to update the BRV
      if (listFifos.isEmpty()) {
        this.graphBRV.put(subgraph.get(0), 1);
      } else {
        // TODO not optimal to use List ?
        final Map<Fifo, List<Integer>> fifoProperties = new LinkedHashMap<>();
        // Evaluate prod / cons of FIFOs only once
        for (final Fifo f : listFifos) {
          List<Integer> fifoProp = new ArrayList<>();
          int prod = Integer.parseInt(f.getSourcePort().getPortRateExpression().getExpressionString());
          int cons = Integer.parseInt(f.getTargetPort().getPortRateExpression().getExpressionString());
          fifoProp.add(prod);
          fifoProp.add(cons);
          fifoProperties.put(f, fifoProp);
        }
        // Pick the first non interface actor and do a Depth First Search to compute RV
        SetReps(subgraph.get(0), new Rational(1, 1), reps, fifoProperties);

        // Computes the LCM of the denominators
        long lcm = 1;
        for (Rational r : reps.values()) {
          lcm = MathFunctionsHelper.lcm(lcm, r.getDenum());
        }

        // Set actors repetition factor
        for (AbstractActor actor : subgraph) {
          final int num = reps.get(actor.getName()).getNum();
          final int denom = reps.get(actor.getName()).getDenum();
          final int rv = (int) ((num * lcm) / denom);
          this.graphBRV.put(actor, rv);
        }

        // Edge condition verification
        for (final Fifo f : this.piHandler.getFifosFromCC(subgraph)) {
          final AbstractActor sourceActor = f.getSourcePort().getContainingActor();
          final AbstractActor targetActor = f.getTargetPort().getContainingActor();
          if (targetActor instanceof InterfaceActor || sourceActor instanceof InterfaceActor) {
            continue;
          }
          int prod = fifoProperties.get(f).get(0);
          int cons = fifoProperties.get(f).get(1);
          int sourceRV = this.graphBRV.get(sourceActor);
          int targetRV = this.graphBRV.get(targetActor);
          if (sourceRV * prod != targetRV * cons) {
            throw new PiMMHelperException("Graph non consistent: edge source production " + Integer.toString(prod * sourceRV) + "!= edge target consumption "
                + Integer.toString(cons * targetRV));
          }
        }
      }
      // Update BRV values with interfaces
      updateRVWithInterfaces(this.piHandler.getReferenceGraph(), subgraph);
    }

    // Recursively compute BRV of sub-graphs
    // // TODO maybe optimize this a recursive call to a secondary recursive method executeRec(final PiGraph graph)
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
   * @throws PiMMHelperException
   *           the PiBRV exception
   */
  private static void SetReps(final AbstractActor actor, Rational n, Hashtable<String, Rational> reps, final Map<Fifo, List<Integer>> fifoProperties)
      throws PiMMHelperException {
    // Update value in the hashtable
    reps.put(actor.getName(), n);

    // DFS forward
    for (DataOutputPort output : actor.getDataOutputPorts()) {
      Fifo fifo = output.getOutgoingFifo();
      if (fifo == null) {
        throw new PiMMHelperException("Actor [" + actor.getName() + "] has output port [" + output.getName() + "] not connected to any FIFO.");
      }
      final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
      if (targetActor instanceof InterfaceActor) {
        continue;
      }
      Rational fa = reps.get(targetActor.getName());
      if (fa.getNum() == 0) {
        int prod = fifoProperties.get(fifo).get(0);
        int cons = fifoProperties.get(fifo).get(1);
        Rational edge = new Rational(prod, cons);
        Rational r = Rational.prod(n, edge);
        SetReps(targetActor, r, reps, fifoProperties);
      }
    }

    // DFS backward
    for (DataInputPort input : actor.getDataInputPorts()) {
      Fifo fifo = input.getIncomingFifo();
      if (fifo == null) {
        throw new PiMMHelperException("Actor [" + actor.getName() + "] has input port [" + input.getName() + "] not connected to any FIFO.");
      }
      final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
      if (sourceActor instanceof InterfaceActor) {
        continue;
      }
      Rational fa = reps.get(sourceActor.getName());
      if (fa.getNum() == 0) {
        int prod = fifoProperties.get(fifo).get(0);
        int cons = fifoProperties.get(fifo).get(1);
        Rational edge = new Rational(cons, prod);
        Rational r = Rational.prod(n, edge);
        SetReps(sourceActor, r, reps, fifoProperties);
      }
    }
  }
}
