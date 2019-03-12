/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018 - 2019)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Alexandre Honorat <alexandre.honorat@insa-rennes.fr> (2019)
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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.LongFraction;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.statictools.PiMMHelper;

/**
 * This class is used to compute the basic repetition vector of a static PiSDF graph using LCM method.
 *
 * @author farresti
 */
class LCMBasedBRV extends PiBRV {

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.pimm.algorithm.math.PiBRV#execute()
   */
  @Override
  public Map<AbstractVertex, Long> computeBRV(final PiGraph piGraph) {
    Map<AbstractVertex, Long> graphBRV = new LinkedHashMap<>();
    if (piGraph == null) {
      final String msg = "cannot compute BRV for null graph.";
      throw new PreesmRuntimeException(msg);
    }

    // Get all sub graph composing the current graph
    final List<List<AbstractActor>> subgraphsWOInterfaces = PiMMHelper.getAllConnectedComponentsWOInterfaces(piGraph);

    for (final List<AbstractActor> subgraph : subgraphsWOInterfaces) {
      final HashMap<String, LongFraction> reps = new HashMap<>();
      // Initializes all reps to 0
      for (final AbstractActor actor : subgraph) {
        reps.put(actor.getName(), new LongFraction(0L));
      }

      // Construct the list of Edges without interfaces
      final List<Fifo> listFifos = PiMMHelper.getFifosFromCC(subgraph);
      // We have only one actor connected to Interface Actor
      // The graph is consistent
      // We just have to update the BRV
      if (listFifos.isEmpty()) {
        graphBRV.put(subgraph.get(0), 1L);
      } else {
        final Map<Fifo, Pair<Long, Long>> fifoProperties = new HashMap<>();

        // Evaluate prod / cons of FIFOs only once
        initFifoProperties(listFifos, fifoProperties);

        setRepsDFS(subgraph, fifoProperties, reps);

        // Computes the LCM of the denominators
        long lcm = 1;
        for (final LongFraction r : reps.values()) {
          lcm = MathFunctionsHelper.lcm(lcm, r.getDenominator());
        }

        // Set actors repetition factor
        computeAndSetRV(subgraph, reps, lcm, graphBRV);

        // Edge condition verification
        checkConsistency(subgraph, fifoProperties, graphBRV);
      }
      // Update BRV values with interfaces
      updateRVWithInterfaces(piGraph, subgraph, graphBRV);
    }

    // Recursively compute BRV of sub-graphs
    // TODO maybe optimize this a recursive call to a secondary recursive method executeRec(final PiGraph graph)
    // or use visitor pattern
    graphBRV.putAll(computeChildrenBRV(piGraph));
    return graphBRV;
  }

  /**
   * Initialize FIFO properties map to avoid multiple access to same information
   *
   * @param listFifos
   *          list of all a fifos
   * @param fifoProperties
   *          fifos properties map
   */
  private void initFifoProperties(final List<Fifo> listFifos, final Map<Fifo, Pair<Long, Long>> fifoProperties) {
    for (final Fifo f : listFifos) {
      fifoProperties.put(f, computeFifoProperties(f));
    }
  }

  /**
   * Get fifo properties.
   * 
   * @param f
   *          Fifo to analyze.
   * @return Pair of the production rate as key, and the consumption rate as value.
   */
  private Pair<Long, Long> computeFifoProperties(Fifo f) {
    final DataOutputPort sourcePort = f.getSourcePort();
    final Expression sourcePortRateExpression = sourcePort.getPortRateExpression();
    final long prod = sourcePortRateExpression.evaluate();
    final DataInputPort targetPort = f.getTargetPort();
    final Expression targetPortRateExpression = targetPort.getPortRateExpression();
    final long cons = targetPortRateExpression.evaluate();
    return new Pair<>(prod, cons);
  }

  /**
   * Check if both rates are different from 0 or both equal to 0. Otherwise it throws a {@link PreesmRuntimeException}.
   * 
   * @param fifo
   *          Fifo to check.
   * @param rates
   *          Properties of the fifo (prod and cons).
   */
  private void checkValidFifo(Fifo fifo, Pair<Long, Long> rates) {
    if ((rates.getKey() == 0 && rates.getValue() != 0) || (rates.getKey() != 0 && rates.getValue() == 0)) {
      DataOutputPort dop = fifo.getSourcePort();
      DataInputPort dip = fifo.getTargetPort();
      AbstractActor src = dop.getContainingActor();
      AbstractActor tgt = dip.getContainingActor();
      String message = "Non valid edge prod / cons from actor " + src.getName() + "[" + dop.getName() + "] to "
          + tgt.getName() + "[" + dip.getName() + "].";
      throw new PreesmRuntimeException(message);
    }
  }

  /**
   * Perform Depth First Search and return fifos in the same order as visited.
   * 
   * @param subgraph
   *          On which to perform DFS.
   * @param reps
   *          Repetition fraction to be computed (initially all equal to 0).
   * @param fifoProperties
   *          Production and consumption of Fifos.
   * @return Ordered Fifos regarding the DFS traversal. Only fifos going to not yet visited actors are present in the
   *         list.
   */
  private void setRepsDFS(List<AbstractActor> subgraph, Map<Fifo, Pair<Long, Long>> fifoProperties,
      HashMap<String, LongFraction> reps) {
    LinkedList<AbstractActor> toVisit = new LinkedList<>();
    AbstractActor root = subgraph.get(0);
    reps.put(root.getName(), new LongFraction(1L));
    toVisit.addFirst(root);
    while (!toVisit.isEmpty()) {
      AbstractActor actor = toVisit.removeFirst();
      LongFraction n = reps.get(actor.getName());
      for (final DataOutputPort output : actor.getDataOutputPorts()) {
        final Fifo fifo = output.getOutgoingFifo();
        if (fifo == null) {
          String message = "Actor [" + actor.getName() + "] has output port [" + output.getName()
              + "] not connected to any FIFO.";
          PreesmLogger.getLogger().log(Level.SEVERE, message);
          throw new PreesmRuntimeException(message);
        }
        Pair<Long, Long> pair = fifoProperties.getOrDefault(fifo, computeFifoProperties(fifo));
        checkValidFifo(fifo, pair);
        final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
        if (targetActor instanceof InterfaceActor) {
          continue;
        }
        final LongFraction fa = reps.get(targetActor.getName());
        final long prod = pair.getKey();
        final long cons = pair.getValue();
        if (fa.getNumerator() == 0 && cons != 0 && n.getNumerator() != 0) {
          final LongFraction edge = new LongFraction(prod, cons);
          final LongFraction r = n.multiply(edge);
          if (!toVisit.contains(targetActor)) {
            reps.put(targetActor.getName(), r);
            toVisit.addFirst(targetActor);
          }

        }
      }
      for (final DataInputPort input : actor.getDataInputPorts()) {
        final Fifo fifo = input.getIncomingFifo();
        if (fifo == null) {
          String message = "Actor [" + actor.getName() + "] has input port [" + input.getName()
              + "] not connected to any FIFO.";
          throw new PreesmRuntimeException(message);
        }
        final Pair<Long, Long> pair = fifoProperties.getOrDefault(fifo, computeFifoProperties(fifo));
        checkValidFifo(fifo, pair);
        final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
        if (sourceActor instanceof InterfaceActor) {
          continue;
        }
        final LongFraction sa = reps.get(sourceActor.getName());
        final long prod = pair.getKey();
        final long cons = pair.getValue();
        if (sa.getNumerator() == 0 && prod != 0 && n.getNumerator() != 0) {
          final LongFraction edge = new LongFraction(cons, prod);
          final LongFraction r = n.multiply(edge);
          if (!toVisit.contains(sourceActor)) {
            reps.put(sourceActor.getName(), r);
            toVisit.addFirst(sourceActor);
          }
        }
      }
    }
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
  private void checkConsistency(final List<AbstractActor> subgraph, final Map<Fifo, Pair<Long, Long>> fifoProperties,
      final Map<AbstractVertex, Long> graphBRV) {
    for (final Fifo f : PiMMHelper.getFifosFromCC(subgraph)) {
      final AbstractActor sourceActor = f.getSourcePort().getContainingActor();
      final AbstractActor targetActor = f.getTargetPort().getContainingActor();
      if ((targetActor instanceof InterfaceActor) || (sourceActor instanceof InterfaceActor)) {
        continue;
      }
      final Pair<Long, Long> pair = fifoProperties.get(f);
      final long prod = pair.getKey();
      final long cons = pair.getValue();
      final long sourceRV = graphBRV.get(sourceActor);
      final long targetRV = graphBRV.get(targetActor);

      if (prod * sourceRV != cons * targetRV) {
        String message = "Graph non consistent: edge source production [" + sourceActor.getName() + "] with rate ["
            + (prod * sourceRV) + "] != edge target consumption [" + targetActor.getName() + "] with rate ["
            + (cons * targetRV) + "]";
        throw new PreesmRuntimeException(message);
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
  private void computeAndSetRV(final List<AbstractActor> subgraph, final HashMap<String, LongFraction> reps, long lcm,
      final Map<AbstractVertex, Long> graphBRV) {
    for (final AbstractActor actor : subgraph) {
      final LongFraction ratio = reps.get(actor.getName());
      final long num = ratio.getNumerator();
      final long denom = ratio.getDenominator();
      final long rv = (num * (lcm / denom));
      graphBRV.put(actor, rv);
    }
  }
}
