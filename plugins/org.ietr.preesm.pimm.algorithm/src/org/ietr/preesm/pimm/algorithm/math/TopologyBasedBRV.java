/**
 * 
 */
package org.ietr.preesm.pimm.algorithm.math;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.ietr.dftools.algorithm.Rational;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.pimm.algorithm.math.PiMMHandler.PiMMHandlerException;
import org.math.array.LinearAlgebra;

/**
 * This class is used to compute the basic repetition vector of a static PiSDF graph using topology matrix method.
 * 
 * @author farresti
 */
public class TopologyBasedBRV extends PiBRV {

  public TopologyBasedBRV(final PiMMHandler piHandler) {
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
    final List<List<AbstractActor>> subgraphsWOInterfaces = this.piHandler.getAllConnectedComponentsWOInterfaces();
    for (final List<AbstractActor> subgraph : subgraphsWOInterfaces) {
      // Construct the list of Edges without interfaces
      List<Fifo> listFifo = this.piHandler.getFifosFromCCWOSelfLoop(subgraph);

      // Get the topology matrix
      if (subgraph.isEmpty()) {
        PiMMHandler hdl = new PiMMHandler();
        throw hdl.new PiMMHandlerException("Impossible to compute consistency. Empty graph.");
      }
      // We have only one actor connected to Interface Actor
      // The graph is consistent
      // We just have to update the BRV
      if (listFifo.isEmpty()) {
        this.graphBRV.put(subgraph.get(0), 1);
      } else {
        double[][] topologyMatrix = getTopologyMatrix(listFifo, subgraph);
        int rank = LinearAlgebra.rank(topologyMatrix);
        if (rank != subgraph.size() - 1) {
          PiMMHandler hdl = new PiMMHandler();
          throw hdl.new PiMMHandlerException("Graph not consitent. rank: " + Integer.toString(rank));
        }
        // Compute BRV
        final Vector<Rational> vrb = computeRationnalNullSpace(topologyMatrix);
        final List<Integer> result = Rational.toNatural(new Vector<>(vrb));
        this.graphBRV.putAll(zipToMap(subgraph, result));
      }

      // Update BRV values with interfaces
      updateRVWithInterfaces(this.piHandler.getReferenceGraph(), subgraph);
    }
    for (final PiMMHandler g : this.piHandler.getChildrenGraphsHandler()) {
      TopologyBasedBRV topologyBRV = new TopologyBasedBRV(g);
      topologyBRV.execute();
      this.graphBRV.putAll(topologyBRV.getBRV());
    }
    // WorkflowLogger.getLogger().log(Level.INFO, "Graph [" + this.piHandler.getReferenceGraph().getName() + "]consistent");
    return true;
  }

  private double[][] getTopologyMatrix(final List<Fifo> listFifo, final List<AbstractActor> subgraph) throws PiMMHandlerException {
    double[][] topologyMatrix = new double[listFifo.size()][subgraph.size()];
    for (final Fifo fifo : listFifo) {
      final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
      final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
      // int prod = (int) (ExpressionEvaluator.evaluate(fifo.getSourcePort().getPortRateExpression()));
      // int cons = (int) (ExpressionEvaluator.evaluate(fifo.getTargetPort().getPortRateExpression()));
      int prod = Integer.parseInt(fifo.getSourcePort().getPortRateExpression().getExpressionString());
      int cons = Integer.parseInt(fifo.getTargetPort().getPortRateExpression().getExpressionString());
      if (prod < 0 || cons < 0) {
        final String prodString = "Prod: " + Integer.toString(prod) + "\n";
        final String consString = "Cons: " + Integer.toString(cons) + "\n";
        final String errorString = "Bad production / consumption rates\n";
        PiMMHandler hdl = new PiMMHandler();
        throw hdl.new PiMMHandlerException("Fifo [" + fifo.getId() + "]\n" + prodString + consString + errorString);
      }
      final int sourceIndex = subgraph.indexOf(sourceActor);
      final int targetIndex = subgraph.indexOf(targetActor);
      if (sourceIndex < 0 || targetIndex < 0) {
        PiMMHandler hdl = new PiMMHandler();
        throw hdl.new PiMMHandlerException("Bad index error:\nSource actor index [" + sourceActor.getName() + "]: " + Integer.toString(sourceIndex)
            + "\nTarget actor index [" + targetActor.getName() + "]: " + Integer.toString(targetIndex));
      }
      topologyMatrix[listFifo.indexOf(fifo)][sourceIndex] = prod;
      topologyMatrix[listFifo.indexOf(fifo)][targetIndex] = -cons;
    }

    return topologyMatrix;
  }

  public static <K, V> Map<K, V> zipToMap(List<K> keys, List<V> values) {
    return IntStream.range(0, keys.size()).boxed().collect(Collectors.toMap(keys::get, values::get));
  }

  /**
   * Compute rationnal null space.
   *
   * @param matrix
   *          the matrix
   * @return the vector
   */
  private static Vector<Rational> computeRationnalNullSpace(final double[][] matrix) {
    final Vector<Rational> vrb = new Vector<>();
    final int li = matrix.length;
    int col = 1;

    if (li != 0) {
      col = matrix[0].length;
    }

    if ((li == 0) || (col == 1)) {
      for (int i = 0; i < col; i++) {
        vrb.add(new Rational(1, 1));
      }
      return vrb;
    }

    final Rational[][] rationnalTopology = new Rational[li][col];

    for (int i = 0; i < li; i++) {
      for (int j = 0; j < col; j++) {
        rationnalTopology[i][j] = new Rational(((Double) matrix[i][j]).intValue(), 1);
      }
    }
    int switchIndices = 1;
    while (rationnalTopology[0][0].zero()) {
      final Rational[] buffer = rationnalTopology[0];
      rationnalTopology[0] = rationnalTopology[switchIndices];
      rationnalTopology[switchIndices] = buffer;
      switchIndices++;
    }
    int pivot = 0;
    for (int i = 0; i < col; i++) {
      double pivotMax = 0;
      int maxIndex = i;
      for (int t = i; t < li; t++) {
        if (Math.abs(rationnalTopology[t][i].doubleValue()) > pivotMax) {
          maxIndex = t;
          pivotMax = Math.abs(rationnalTopology[t][i].doubleValue());
        }
      }
      if ((pivotMax != 0) && (maxIndex != i)) {
        final Rational[] buffer = rationnalTopology[i];
        rationnalTopology[i] = rationnalTopology[maxIndex];
        rationnalTopology[maxIndex] = buffer;
        pivot = i;
      } else if ((maxIndex == i) && (pivotMax != 0)) {
        pivot = i;
      } else {
        break;
      }
      final Rational odlPivot = rationnalTopology[i][i].clone();
      for (int t = i; t < col; t++) {
        rationnalTopology[i][t] = Rational.div(rationnalTopology[i][t], odlPivot);
      }
      for (int j = i + 1; j < li; j++) {
        if (!rationnalTopology[j][i].zero()) {
          final Rational oldji = new Rational(rationnalTopology[j][i].getNum(), rationnalTopology[j][i].getDenum());
          for (int k = 0; k < col; k++) {
            rationnalTopology[j][k] = Rational.sub(rationnalTopology[j][k],
                Rational.prod(rationnalTopology[i][k], Rational.div(oldji, rationnalTopology[pivot][pivot])));
          }
        }
      }
    }
    for (int i = 0; i < col; i++) {
      vrb.add(new Rational(1, 1));
    }
    int i = li - 1;
    while (i >= 0) {
      Rational val = new Rational(0, 0);
      for (int k = i + 1; k < col; k++) {
        val = Rational.add(val, Rational.prod(rationnalTopology[i][k], vrb.get(k)));
      }
      if (!val.zero()) {
        if (rationnalTopology[i][i].zero()) {
          System.out.println("elt diagonal zero");
        }
        vrb.set(i, Rational.div(val.abs(), rationnalTopology[i][i]));
      }
      i--;
    }
    return vrb;
  }

}
