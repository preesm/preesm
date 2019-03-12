/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018 - 2019)
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
package org.preesm.model.pisdf.brv;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.statictools.PiMMHelper;

/**
 * @author farresti
 *
 */
public abstract class PiBRV {

  /**
   * Compute the BRV of the associated graph given a method. This also checks for consistency at the same time.
   *
   * @return the BRV as a Map that associates a long value (the repetition value) for every AbstractVertex
   */
  public static final Map<AbstractVertex, Long> compute(final PiGraph piGraph, final BRVMethod method) {
    final PiBRV piBRVAlgo;
    switch (method) {
      case LCM:
        piBRVAlgo = new LCMBasedBRV();
        break;
      case TOPOLOGY:
        piBRVAlgo = new TopologyBasedBRV();
        break;
      default:
        throw new PreesmRuntimeException("unexpected value for BRV method: [" + method + "]");
    }
    return piBRVAlgo.computeBRV(piGraph);
  }

  /**
   * Print the BRV values of every vertex. For debug purposes.
   */
  public static final void printRV(final Map<AbstractVertex, Long> brv) {
    final Map<PiGraph, Long> levelRV = new HashMap<>();

    for (final Entry<AbstractVertex, Long> en : brv.entrySet()) {
      final AbstractVertex av = en.getKey();
      final PiGraph container = av.getContainingPiGraph();
      if (!levelRV.containsKey(container)) {
        levelRV.put(container, PiMMHelper.getHierarchichalRV(container, brv));
      }
      final long actorRV = en.getValue();
      final long actorFullRV = actorRV * levelRV.get(container);
      final String msg = en.getKey().getVertexPath() + " x" + actorRV + " (total: x" + actorFullRV + ")";
      PreesmLogger.getLogger().log(Level.INFO, msg);
    }
  }

  protected abstract Map<AbstractVertex, Long> computeBRV(final PiGraph piGraph);

  protected Map<AbstractVertex, Long> computeChildrenBRV(final PiGraph parentGraph) {
    final Map<AbstractVertex, Long> resultBrv = new LinkedHashMap<>();
    parentGraph.getChildrenGraphs().forEach(childGraph -> resultBrv.putAll(this.computeBRV(childGraph)));
    return resultBrv;
  }

  protected static void updateRVWithInterfaces(final PiGraph graph, final List<AbstractActor> subgraph,
      final Map<AbstractVertex, Long> graphBRV) {
    // Update RV values based on the interface
    long scaleFactor = 1;

    // Compute scaleFactor for input interfaces
    scaleFactor = getInputInterfacesScaleFactor(graph, subgraph, scaleFactor, graphBRV);

    // Compute scaleFactor for output interfaces
    scaleFactor = getOutputInterfacesScaleFactor(graph, subgraph, scaleFactor, graphBRV);

    // Do the actual update
    for (final AbstractActor actor : subgraph) {
      final long newRV = graphBRV.get(actor) * scaleFactor;
      graphBRV.put(actor, newRV);
      if ((actor instanceof DelayActor) && (newRV != 1)) {
        String message = "Inconsistent graph. DelayActor [" + actor.getName() + "] with a repetition vector of "
            + Long.toString(newRV);
        throw new PreesmRuntimeException(message);
      }
    }
  }

  /**
   * Check if both sides of an interface have the same rate, otherwise throws {@link PreesmRuntimeException}.
   * 
   * @param graph
   *          Current inner graph.
   * @param ia
   *          Interface to check (from inner graph).
   * @param rate
   *          Rate of the interface to check.
   */
  private static void checkOppositeInterfaceRate(PiGraph graph, InterfaceActor ia, long rate) {
    PiGraph motherGraph = graph.getContainingPiGraph();
    if (motherGraph != null) {
      // otherwise it means that we compute a local BRV (from GUI)
      DataPort opposite = graph.lookupGraphDataPortForInterfaceActor(ia);
      long oppositeRate = opposite.getExpression().evaluate();
      if (/* motherGraph != graph && */oppositeRate != rate) {
        String msg = "DataPort [" + opposite.getName() + "] of actor [" + opposite.getContainingActor().getName()
            + "] has different rates from inner interface definition: inner " + rate + " -- outer " + oppositeRate;
        throw new PreesmRuntimeException(msg);
      }
    }
  }

  /**
   * Emit a warning if several scale factors have been found.
   * 
   * @param graph
   *          Analyzed graph.
   * @param scaleScaleFactors
   *          Factors found from the interfaces of the graph.
   */
  private static void emitMultipleScaleFactorsWarning(PiGraph graph, SortedSet<Long> scaleScaleFactors) {
    if (scaleScaleFactors.size() > 2) {
      PreesmLogger.getLogger().log(Level.INFO,
          "Several different scaling factors have been derived from the input interfaces of the graph "
              + graph.getName() + ", the highest one has been selected: " + scaleScaleFactors.last());
    }
  }

  /**
   * Compute the scale factor to apply to RV values based on DataInputInterfaces
   *
   * @param graph
   *          the graph
   * @param subgraph
   *          the current connected component
   * @param scaleFactor
   *          the current scaleFactor
   * @return new value of scale factor
   */
  private static long getOutputInterfacesScaleFactor(final PiGraph graph, final List<AbstractActor> subgraph,
      final long inscaleFactor, final Map<AbstractVertex, Long> graphBRV) {
    SortedSet<Long> scaleScaleFactors = new TreeSet<>();
    scaleScaleFactors.add(1L);
    boolean emitScaleWarning = false;
    for (final DataOutputInterface out : graph.getDataOutputInterfaces()) {
      final DataInputPort dataInputPort = (DataInputPort) out.getDataPort();
      final long cons = dataInputPort.getPortRateExpression().evaluate();
      checkOppositeInterfaceRate(graph, out, cons);
      final Fifo fifo = dataInputPort.getIncomingFifo();
      final DataOutputPort sourcePort = fifo.getSourcePort();
      final AbstractActor sourceActor = sourcePort.getContainingActor();

      if (!(sourceActor instanceof InterfaceActor) && subgraph.contains(sourceActor)) {
        final long prod = sourcePort.getPortRateExpression().evaluate();
        final long sourceRV = graphBRV.get(sourceActor);
        final long tmp = inscaleFactor * prod * sourceRV;
        if (tmp > cons || (tmp < cons && cons % tmp != 0)) {
          emitScaleWarning = true;
          // we emit a warning only if producing too much, or not enough but with a wrong multiplicity
          // note that it is not allowed to produce less than the consumed tokens on the output interface
          // at the opposite, if more are produced, a roundbuffer is added.
        }
        if (tmp > 0 && tmp < cons) {
          final long scaleScaleFactor = (cons + tmp - 1) / tmp;
          scaleScaleFactors.add(scaleScaleFactor);
        }
      }
    }
    emitMultipleScaleFactorsWarning(graph, scaleScaleFactors);
    long res = inscaleFactor * scaleScaleFactors.last();
    if (emitScaleWarning) {
      PreesmLogger.getLogger().log(Level.INFO,
          "The output interfaces of the graph " + graph.getName()
              + " are not corresponding to their source total production." + " Roundbuffers and scaling factor (x" + res
              + ") will be applied.");
    }
    return res;
  }

  /**
   * Compute the scale factor to apply to RV values based on DataOutputInterfaces. It also checks the input interfaces
   * properties.
   *
   * @param graph
   *          the graph
   * @param subgraph
   *          the current connected component
   * @param scaleFactor
   *          the current scaleFactor
   * @return new value of scale factor
   */
  private static long getInputInterfacesScaleFactor(final PiGraph graph, final List<AbstractActor> subgraph,
      final long inscaleFactor, final Map<AbstractVertex, Long> graphBRV) {
    SortedSet<Long> scaleScaleFactors = new TreeSet<>();
    scaleScaleFactors.add(1L);
    boolean emitScaleWarning = false;
    for (final DataInputInterface in : graph.getDataInputInterfaces()) {
      final DataOutputPort dataOutputPort = (DataOutputPort) in.getDataPort();
      final long prod = dataOutputPort.getPortRateExpression().evaluate();
      checkOppositeInterfaceRate(graph, in, prod);
      final Fifo fifo = dataOutputPort.getOutgoingFifo();
      final DataInputPort targetPort = fifo.getTargetPort();
      final AbstractActor targetActor = targetPort.getContainingActor();
      if (!(targetActor instanceof InterfaceActor) && subgraph.contains(targetActor)) {
        final long targetRV = graphBRV.get(targetActor);
        final long cons = targetPort.getPortRateExpression().evaluate();
        final long tmp = inscaleFactor * cons * targetRV;
        if (tmp > prod || (tmp < prod && prod % tmp != 0)) {
          emitScaleWarning = true;
          // we emit a warning only if consuming too much, or not enough but with a wrong multiplicity
          // note that it is not allowed to leave unconsumed tokens on the input interface
          // at the opposite, if more are consumed, a broadcast is added.
        }
        if (tmp > 0 && tmp < prod) {
          final long scaleScaleFactor = (prod + tmp - 1) / tmp;
          scaleScaleFactors.add(scaleScaleFactor);
        }
      }
    }
    emitMultipleScaleFactorsWarning(graph, scaleScaleFactors);
    long res = inscaleFactor * scaleScaleFactors.last();
    if (emitScaleWarning) {
      PreesmLogger.getLogger().log(Level.INFO,
          "The input interfaces of the graph " + graph.getName()
              + " are not corresponding to their target total consumption." + " Broadcasts and scaling factor (x" + res
              + ") will be applied.");
    }
    return res;
  }

}
