/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
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
/**
 *
 */
package org.preesm.model.pisdf.brv;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RRQRDecomposition;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.math.LongFraction;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.statictools.PiMMHelper;

/**
 * This class is used to compute the basic repetition vector of a static PiSDF graph using topology matrix method.
 *
 * @author farresti
 */
class TopologyBasedBRV extends PiBRV {

  @Override
  public Map<AbstractVertex, Long> computeBRV(final PiGraph piGraph) {
    final Map<AbstractVertex, Long> graphBRV = new LinkedHashMap<>();
    if (piGraph == null) {
      final String msg = "cannot compute BRV for null graph.";
      throw new PreesmRuntimeException(msg);
    }
    // Get all sub graph composing the current graph
    final List<List<AbstractActor>> subgraphsWOInterfaces = PiMMHelper.getAllConnectedComponentsWOInterfaces(piGraph);
    for (final List<AbstractActor> subgraph : subgraphsWOInterfaces) {
      // Construct the list of Edges without interfaces
      final List<Fifo> listFifo = PiMMHelper.getFifosFromCCWOSelfLoop(subgraph);

      // Get the topology matrix
      if (subgraph.isEmpty()) {
        throw new PreesmRuntimeException("Impossible to compute consistency. Empty graph.");
      }
      // We have only one actor connected to Interface Actor
      // The graph is consistent
      // We just have to update the BRV
      if (listFifo.isEmpty()) {
        graphBRV.put(subgraph.get(0), 1L);
      } else {
        final double[][] topologyMatrix = getTopologyMatrix(listFifo, subgraph);
        final Array2DRowRealMatrix topoMatrix = new Array2DRowRealMatrix(topologyMatrix);
        final RRQRDecomposition decomp = new RRQRDecomposition(topoMatrix);
        final int rank = decomp.getRank(1);

        if (rank != (subgraph.size() - 1)) {
          throw new PreesmRuntimeException("Graph not consitent. rank: " + Long.toString(rank) + ", expected: "
              + Integer.toString(subgraph.size() - 1));
        }
        // Compute BRV
        final List<LongFraction> vrb = MathFunctionsHelper.computeRationnalNullSpace(topologyMatrix);
        // final List<Long> result = Rational.toNatural(new Vector<>(vrb))
        final List<Long> result = new ArrayList<>(MathFunctionsHelper.toNatural(vrb));
        graphBRV.putAll(TopologyBasedBRV.zipToMap(subgraph, result));
      }

      // Update BRV values with interfaces
      updateRVWithInterfaces(piGraph, subgraph, graphBRV);
    }
    computeChildrenBRV(piGraph, graphBRV);
    return graphBRV;
  }

  private double[][] getTopologyMatrix(final List<Fifo> listFifo, final List<AbstractActor> subgraph) {
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
        throw new PreesmRuntimeException("Fifo [" + fifo.getId() + "]\n" + prodString + consString + errorString);
      }
      final int sourceIndex = subgraph.indexOf(sourceActor);
      final int targetIndex = subgraph.indexOf(targetActor);
      if ((sourceIndex < 0) || (targetIndex < 0)) {
        throw new PreesmRuntimeException(
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

}
