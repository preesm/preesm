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
package org.ietr.preesm.pimm.algorithm.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.math.array.LinearAlgebra;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.LongFraction;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Fifo;

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
  public boolean execute() throws PiMMHelperException {
    if (this.piHandler.getReferenceGraph() == null) {
      PreesmLogger.getLogger().log(Level.SEVERE, "cannot compute BRV for null graph.");
      return false;
    }
    // Get all sub graph composing the current graph
    final List<List<AbstractActor>> subgraphsWOInterfaces = this.piHandler.getAllConnectedComponentsWOInterfaces();
    for (final List<AbstractActor> subgraph : subgraphsWOInterfaces) {
      // Construct the list of Edges without interfaces
      final List<Fifo> listFifo = this.piHandler.getFifosFromCCWOSelfLoop(subgraph);

      // Get the topology matrix
      if (subgraph.isEmpty()) {
        throw new PiMMHelperException("Impossible to compute consistency. Empty graph.");
      }
      // We have only one actor connected to Interface Actor
      // The graph is consistent
      // We just have to update the BRV
      if (listFifo.isEmpty()) {
        this.graphBRV.put(subgraph.get(0), (long) 1);
      } else {
        final double[][] topologyMatrix = getTopologyMatrix(listFifo, subgraph);
        final long rank = LinearAlgebra.rank(topologyMatrix);
        if (rank != (subgraph.size() - 1)) {
          throw new PiMMHelperException("Graph not consitent. rank: " + Long.toString(rank) + ", expected: "
              + Long.toString(subgraph.size() - 1));
        }
        // Compute BRV
        final List<LongFraction> vrb = TopologyBasedBRV.computeRationnalNullSpace(topologyMatrix);
        // final List<Long> result = Rational.toNatural(new Vector<>(vrb))
        final List<Long> result = new ArrayList<>();
        MathFunctionsHelper.toNatural(vrb).forEach(rv -> result.add((long) rv));
        this.graphBRV.putAll(TopologyBasedBRV.zipToMap(subgraph, result));
      }

      // Update BRV values with interfaces
      updateRVWithInterfaces(this.piHandler.getReferenceGraph(), subgraph);
    }
    for (final PiMMHandler g : this.piHandler.getChildrenGraphsHandler()) {
      final TopologyBasedBRV topologyBRV = new TopologyBasedBRV(g);
      topologyBRV.execute();
      this.graphBRV.putAll(topologyBRV.getBRV());
    }
    return true;
  }

  private double[][] getTopologyMatrix(final List<Fifo> listFifo, final List<AbstractActor> subgraph)
      throws PiMMHelperException {
    final double[][] topologyMatrix = new double[listFifo.size()][subgraph.size()];
    for (final Fifo fifo : listFifo) {
      final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
      final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
      final long prod = fifo.getSourcePort().getPortRateExpression().evaluate();
      final long cons = fifo.getTargetPort().getPortRateExpression().evaluate();
      if ((prod < 0) || (cons < 0)) {
        final String prodString = "Prod: " + Long.toString(prod) + "\n";
        final String consString = "Cons: " + Long.toString(cons) + "\n";
        final String errorString = "Bad production / consumption rates\n";
        throw new PiMMHelperException("Fifo [" + fifo.getId() + "]\n" + prodString + consString + errorString);
      }
      final int sourceIndex = subgraph.indexOf(sourceActor);
      final int targetIndex = subgraph.indexOf(targetActor);
      if ((sourceIndex < 0) || (targetIndex < 0)) {
        throw new PiMMHelperException(
            "Bad index error:\nSource actor index [" + sourceActor.getName() + "]: " + Integer.toString(sourceIndex)
                + "\nTarget actor index [" + targetActor.getName() + "]: " + Integer.toString(targetIndex));
      }
      topologyMatrix[listFifo.indexOf(fifo)][sourceIndex] = prod;
      topologyMatrix[listFifo.indexOf(fifo)][targetIndex] = -cons;
    }

    return topologyMatrix;
  }

  public static <K, V> Map<K, V> zipToMap(final List<K> keys, final List<V> values) {
    return IntStream.range(0, keys.size()).boxed().collect(Collectors.toMap(keys::get, values::get));
  }

  /**
   * Compute rationnal null space.
   *
   * @param matrix
   *          the matrix
   * @return the vector
   */
  private static List<LongFraction> computeRationnalNullSpace(final double[][] matrix) {
    final List<LongFraction> vrb = new ArrayList<>();
    final int numberOfRows = matrix.length;
    int numberOfColumns = 1;

    if (numberOfRows != 0) {
      numberOfColumns = matrix[0].length;
    }

    if ((numberOfRows == 0) || (numberOfColumns == 1)) {
      for (int i = 0; i < numberOfColumns; i++) {
        vrb.add(new LongFraction(1, 1));
      }
      return vrb;
    }

    final LongFraction[][] rationnalTopology = new LongFraction[numberOfRows][numberOfColumns];

    for (int i = 0; i < numberOfRows; i++) {
      for (int j = 0; j < numberOfColumns; j++) {
        rationnalTopology[i][j] = new LongFraction(((Double) matrix[i][j]).longValue(), 1);
      }
    }
    int switchIndices = 1;
    while (rationnalTopology[0][0].isZero()) {
      final LongFraction[] buffer = rationnalTopology[0];
      rationnalTopology[0] = rationnalTopology[switchIndices];
      rationnalTopology[switchIndices] = buffer;
      switchIndices++;
    }
    int pivot = 0;
    for (int i = 0; i < numberOfColumns; i++) {
      double pivotMax = 0;
      int maxIndex = i;
      for (int t = i; t < numberOfRows; t++) {
        if (Math.abs(rationnalTopology[t][i].doubleValue()) > pivotMax) {
          maxIndex = t;
          pivotMax = Math.abs(rationnalTopology[t][i].doubleValue());
        }
      }
      if ((pivotMax != 0) && (maxIndex != i)) {
        final LongFraction[] buffer = rationnalTopology[i];
        rationnalTopology[i] = rationnalTopology[maxIndex];
        rationnalTopology[maxIndex] = buffer;
        pivot = i;
      } else if ((maxIndex == i) && (pivotMax != 0)) {
        pivot = i;
      } else {
        break;
      }
      final LongFraction odlPivot = new LongFraction(rationnalTopology[i][i]);
      for (int t = i; t < numberOfColumns; t++) {
        rationnalTopology[i][t] = rationnalTopology[i][t].divide(odlPivot);
      }
      for (int j = i + 1; j < numberOfRows; j++) {
        if (!rationnalTopology[j][i].isZero()) {
          final LongFraction oldji = new LongFraction(rationnalTopology[j][i].getNumerator(),
              rationnalTopology[j][i].getDenominator());
          for (int k = 0; k < numberOfColumns; k++) {
            rationnalTopology[j][k] = rationnalTopology[j][k]
                .subtract(rationnalTopology[i][k].multiply(oldji.divide(rationnalTopology[pivot][pivot])));
          }
        }
      }
    }
    for (int i = 0; i < numberOfColumns; i++) {
      vrb.add(new LongFraction(1, 1));
    }
    int i = numberOfRows - 1;
    while (i >= 0) {
      LongFraction val = new LongFraction(0, 0);
      for (int k = i + 1; k < numberOfColumns; k++) {
        val = val.add(rationnalTopology[i][k].multiply(vrb.get(k)));
      }
      if (!val.isZero()) {
        if (rationnalTopology[i][i].isZero()) {
          System.out.println("elt diagonal zero");
        }
        vrb.set(i, val.abs().divide(rationnalTopology[i][i]));
      }
      i--;
    }
    return vrb;
  }

}
