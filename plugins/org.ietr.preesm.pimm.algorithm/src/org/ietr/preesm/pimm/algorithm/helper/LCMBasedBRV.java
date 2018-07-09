/**
 *
 */
package org.ietr.preesm.pimm.algorithm.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.ietr.dftools.algorithm.Rational;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Expression;
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
      final HashMap<String, Rational> reps = new HashMap<>();
      // Initializes all reps to 0
      for (final AbstractActor actor : subgraph) {
        reps.put(actor.getName(), new Rational(0, 1));
      }

      // Construct the list of Edges without interfaces
      final List<Fifo> listFifos = this.piHandler.getFifosFromCC(subgraph);
      // We have only one actor connected to Interface Actor
      // The graph is consistent
      // We just have to update the BRV
      if (listFifos.isEmpty()) {
        this.graphBRV.put(subgraph.get(0), (long) 1);
      } else {
        // TODO not optimal to use List ?
        final Map<Fifo, List<Long>> fifoProperties = new LinkedHashMap<>();

        // Evaluate prod / cons of FIFOs only once
        initFifoProperties(listFifos, fifoProperties);

        // Pick the first non interface actor and do a Depth First Search to compute RV
        LCMBasedBRV.setReps(subgraph.get(0), new Rational(1, 1), reps, fifoProperties);

        // Computes the LCM of the denominators
        long lcm = 1;
        for (final Rational r : reps.values()) {
          lcm = MathFunctionsHelper.lcm(lcm, r.getDenum());
        }

        // Set actors repetition factor
        computeAndSetRV(subgraph, reps, lcm);

        // Edge condition verification
        checkConsistency(subgraph, fifoProperties);
      }
      // Update BRV values with interfaces
      updateRVWithInterfaces(this.piHandler.getReferenceGraph(), subgraph);
    }

    // Recursively compute BRV of sub-graphs
    // TODO maybe optimize this a recursive call to a secondary recursive method executeRec(final PiGraph graph)
    // or use visitor pattern
    for (final PiMMHandler g : this.piHandler.getChildrenGraphsHandler()) {
      final LCMBasedBRV lcmBRV = new LCMBasedBRV(g);
      lcmBRV.execute();
      this.graphBRV.putAll(lcmBRV.getBRV());
    }
    return true;
  }

  /**
   * Check consistency.
   * 
   * @param subgraph
   *          current connected component
   * @param fifoProperties
   *          fifos properties map
   * @throws PiMMHelperException
   *           the exception
   */
  private void checkConsistency(final List<AbstractActor> subgraph, final Map<Fifo, List<Long>> fifoProperties) throws PiMMHelperException {
    for (final Fifo f : this.piHandler.getFifosFromCC(subgraph)) {
      final AbstractActor sourceActor = f.getSourcePort().getContainingActor();
      final AbstractActor targetActor = f.getTargetPort().getContainingActor();
      if ((targetActor instanceof InterfaceActor) || (sourceActor instanceof InterfaceActor)) {
        continue;
      }
      final long prod = fifoProperties.get(f).get(0);
      final long cons = fifoProperties.get(f).get(1);
      final long sourceRV = this.graphBRV.get(sourceActor);
      final long targetRV = this.graphBRV.get(targetActor);
      if ((sourceRV * prod) != (targetRV * cons)) {
        throw new PiMMHelperException("Graph non consistent: edge source production " + sourceActor.getName() + " " + Long.toString(prod * sourceRV)
            + "!= edge target consumption " + Long.toString(cons * targetRV));
      }
    }
  }

  /**
   * Computes and sets the repetition vector values for all actors of a given connected component.
   * 
   * @param subgraph
   *          current connected component
   * @param reps
   *          current reduced fractions of the actors
   * @param lcm
   *          lcm of the connected component
   */
  private void computeAndSetRV(final List<AbstractActor> subgraph, final HashMap<String, Rational> reps, long lcm) {
    for (final AbstractActor actor : subgraph) {
      final long num = reps.get(actor.getName()).getNum();
      final long denom = reps.get(actor.getName()).getDenum();
      final long rv = ((num * lcm) / denom);
      this.graphBRV.put(actor, rv);
    }
  }

  /**
   * Initialize FIFO properties map to avoid multiple access to same information
   * 
   * @param listFifos
   *          list of all a fifos
   * @param fifoProperties
   *          fifos properties map
   */
  private void initFifoProperties(final List<Fifo> listFifos, final Map<Fifo, List<Long>> fifoProperties) {
    for (final Fifo f : listFifos) {
      final List<Long> fifoProp = new ArrayList<>();
      final DataOutputPort sourcePort = f.getSourcePort();
      final Expression sourcePortRateExpression = sourcePort.getPortRateExpression();
      final long prod = Long.parseLong(sourcePortRateExpression.getExpressionString());
      final DataInputPort targetPort = f.getTargetPort();
      final Expression targetPortRateExpression = targetPort.getPortRateExpression();
      final long cons = Long.parseLong(targetPortRateExpression.getExpressionString());
      fifoProp.add(prod);
      fifoProp.add(cons);
      fifoProperties.put(f, fifoProp);
    }
  }

  /**
   * Iterative function of execute.
   *
   * @param actor
   *          the current actor
   * @param n
   *          the current fraction
   * @param reps
   *          current reduced fractions of the actors
   * @throws PiMMHelperException
   *           the PiBRV exception
   */
  private static void setReps(final AbstractActor actor, final Rational n, final HashMap<String, Rational> reps, final Map<Fifo, List<Long>> fifoProperties)
      throws PiMMHelperException {
    // Update value in the HashMap
    reps.put(actor.getName(), n);

    // DFS forward
    for (final DataOutputPort output : actor.getDataOutputPorts()) {
      final Fifo fifo = output.getOutgoingFifo();
      if (fifo == null) {
        throw new PiMMHelperException("Actor [" + actor.getName() + "] has output port [" + output.getName() + "] not connected to any FIFO.");
      }
      final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
      if (targetActor instanceof InterfaceActor) {
        continue;
      }
      final Rational fa = reps.get(targetActor.getName());
      if (fa.getNum() == 0) {
        final long prod = fifoProperties.get(fifo).get(0);
        final long cons = fifoProperties.get(fifo).get(1);
        final Rational edge = new Rational((int) prod, (int) cons);
        final Rational r = Rational.prod(n, edge);
        LCMBasedBRV.setReps(targetActor, r, reps, fifoProperties);
      }
    }

    // DFS backward
    for (final DataInputPort input : actor.getDataInputPorts()) {
      final Fifo fifo = input.getIncomingFifo();
      if (fifo == null) {
        throw new PiMMHelperException("Actor [" + actor.getName() + "] has input port [" + input.getName() + "] not connected to any FIFO.");
      }
      final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
      if (sourceActor instanceof InterfaceActor) {
        continue;
      }
      final Rational fa = reps.get(sourceActor.getName());
      if (fa.getNum() == 0) {
        final long prod = fifoProperties.get(fifo).get(0);
        final long cons = fifoProperties.get(fifo).get(1);
        final Rational edge = new Rational((int) cons, (int) prod);
        final Rational r = Rational.prod(n, edge);
        LCMBasedBRV.setReps(sourceActor, r, reps, fifoProperties);
      }
    }
  }
}
