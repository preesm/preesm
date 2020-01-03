/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
 * @author ahonorat
 * @author farresti
 */
class LCMBasedBRV extends PiBRV {

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
      // Initializes all reps to 0
      for (final AbstractActor actor : subgraph) {
        // If unconnected, each actor is executed once.
        graphBRV.put(actor, 1L);
      }

      // initialize reps in topological order
      final Map<AbstractActor, LongFraction> reps = initRepsDFS(subgraph);

      // Evaluate prod / cons of FIFOs in topological order
      final Map<Fifo, Pair<Long, Long>> fifoProperties = initFifoProperties(reps);

      if (!fifoProperties.isEmpty()) {

        setReps(fifoProperties, reps);

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
   * Get and check fifo properties.
   *
   * @param f
   *          Fifo to analyze.
   * @param src
   *          Source Actor of the fifo
   * @param tgt
   *          Target Actor of the fifo
   * @return Pair of the production rate as key, and the consumption rate as value.
   */
  private static Pair<Long, Long> computeFifoProperties(Fifo f, AbstractActor src, AbstractActor tgt) {
    final DataOutputPort sourcePort = f.getSourcePort();
    final Expression sourcePortRateExpression = sourcePort.getPortRateExpression();
    final long prod = sourcePortRateExpression.evaluate();
    final DataInputPort targetPort = f.getTargetPort();
    final Expression targetPortRateExpression = targetPort.getPortRateExpression();
    final long cons = targetPortRateExpression.evaluate();
    Pair<Long, Long> res = new Pair<>(prod, cons);
    if ((prod == 0 && cons != 0) || (prod != 0 && cons == 0)) {
      String message = "Non valid edge prod / cons from actor " + src.getName() + "[" + sourcePort.getName() + "] to "
          + tgt.getName() + "[" + targetPort.getName() + "].";
      throw new PreesmRuntimeException(message);
    }
    return res;
  }

  /**
   * Initialize FIFO properties (production and consumption rates) map to avoid multiple access to same information
   *
   * @param reps
   *          fifos properties map in topological order of reps
   * @return Fifo properties in same order as reps actors.
   */
  private static Map<Fifo, Pair<Long, Long>> initFifoProperties(final Map<AbstractActor, LongFraction> reps) {
    final Map<Fifo, Pair<Long, Long>> fifoProperties = new LinkedHashMap<>();

    for (AbstractActor actor : reps.keySet()) {
      for (final DataInputPort input : actor.getDataInputPorts()) {
        final Fifo fifo = input.getIncomingFifo();
        if (fifo == null) {
          String message = "Actor [" + actor.getName() + "] has input port [" + input.getName()
              + "] not connected to any FIFO.";
          throw new PreesmRuntimeException(message);
        }
        final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
        if (sourceActor instanceof InterfaceActor) {
          continue;
        }
        fifoProperties.put(fifo, computeFifoProperties(fifo, sourceActor, actor));
      }

      for (final DataOutputPort output : actor.getDataOutputPorts()) {
        final Fifo fifo = output.getOutgoingFifo();
        if (fifo == null) {
          String message = "Actor [" + actor.getName() + "] has output port [" + output.getName()
              + "] not connected to any FIFO.";
          throw new PreesmRuntimeException(message);
        }
        final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
        if (targetActor instanceof InterfaceActor) {
          continue;
        }
        fifoProperties.put(fifo, computeFifoProperties(fifo, actor, targetActor));
      }
    }

    return fifoProperties;
  }

  /**
   * Perform Depth First Search and return actors in the same order as visited (with initial value 0L).
   *
   * @param subgraph
   *          On which to perform DFS.
   * @return Ordered actors regarding the DFS traversal of subgraph.
   */
  private static Map<AbstractActor, LongFraction> initRepsDFS(List<AbstractActor> subgraph) {
    Map<AbstractActor, LongFraction> reps = new LinkedHashMap<>();

    LinkedList<AbstractActor> toVisit = new LinkedList<>();

    for (AbstractActor aa : subgraph) {
      if (!reps.containsKey(aa)) {

        toVisit.addFirst(aa);
        while (!toVisit.isEmpty()) {
          AbstractActor actor = toVisit.removeFirst();
          reps.put(actor, new LongFraction(0L));
          for (final DataInputPort input : actor.getDataInputPorts()) {
            final Fifo fifo = input.getIncomingFifo();
            if (fifo == null) {
              String message = "Actor [" + actor.getName() + "] has input port [" + input.getName()
                  + "] not connected to any FIFO.";
              throw new PreesmRuntimeException(message);
            }
            final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
            if (sourceActor instanceof InterfaceActor) {
              continue;
            }
            if (!toVisit.contains(sourceActor) && !reps.containsKey(sourceActor)) {
              toVisit.addFirst(sourceActor);
            }
          }
          for (final DataOutputPort output : actor.getDataOutputPorts()) {
            final Fifo fifo = output.getOutgoingFifo();
            if (fifo == null) {
              String message = "Actor [" + actor.getName() + "] has output port [" + output.getName()
                  + "] not connected to any FIFO.";
              PreesmLogger.getLogger().log(Level.SEVERE, message);
              throw new PreesmRuntimeException(message);
            }
            final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
            if (targetActor instanceof InterfaceActor) {
              continue;
            }
            if (!toVisit.contains(targetActor) && !reps.containsKey(targetActor)) {
              toVisit.addFirst(targetActor);
            }
          }
        }
      }
    }
    return reps;
  }

  /**
   * Set fraction per actor in the topological order of reps/fifos.
   *
   * @param fifoProperties
   *          Production and consumption of Fifos.
   * @param reps
   *          Repetition fraction to be computed (initially all equal to 0).
   */
  private static void setReps(Map<Fifo, Pair<Long, Long>> fifoProperties, Map<AbstractActor, LongFraction> reps) {

    for (Entry<Fifo, Pair<Long, Long>> e : fifoProperties.entrySet()) {
      Fifo f = e.getKey();
      Pair<Long, Long> p = e.getValue();
      long prod = p.getKey();
      long cons = p.getValue();
      final DataOutputPort sourcePort = f.getSourcePort();
      final DataInputPort targetPort = f.getTargetPort();
      AbstractActor src = sourcePort.getContainingActor();
      AbstractActor tgt = targetPort.getContainingActor();

      LongFraction srcRep = reps.get(src);
      LongFraction tgtRep = reps.get(tgt);

      if (srcRep.getNumerator() == 0 && prod > 0) {
        LongFraction ratio = new LongFraction(cons, prod);
        if (tgtRep.getNumerator() > 0) {
          ratio = ratio.multiply(tgtRep);
        }
        reps.replace(src, ratio);
        srcRep = ratio;
      }

      if (tgtRep.getNumerator() == 0 && cons > 0) {
        LongFraction ratio = new LongFraction(prod, cons);
        if (srcRep.getNumerator() > 0) {
          ratio = ratio.multiply(srcRep);
        }
        reps.replace(tgt, ratio);
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
    for (final Entry<Fifo, Pair<Long, Long>> e : fifoProperties.entrySet()) {
      final Fifo f = e.getKey();
      final AbstractActor sourceActor = f.getSourcePort().getContainingActor();
      final AbstractActor targetActor = f.getTargetPort().getContainingActor();
      if ((targetActor instanceof InterfaceActor) || (sourceActor instanceof InterfaceActor)) {
        continue;
      }
      final Pair<Long, Long> pair = e.getValue();
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
  private void computeAndSetRV(final List<AbstractActor> subgraph, final Map<AbstractActor, LongFraction> reps,
      long lcm, final Map<AbstractVertex, Long> graphBRV) {
    for (final AbstractActor actor : subgraph) {
      final LongFraction ratio = reps.get(actor);
      final long num = ratio.getNumerator();
      final long denom = ratio.getDenominator();
      final long rv = (num * (lcm / denom));
      graphBRV.put(actor, rv);
    }
  }
}
