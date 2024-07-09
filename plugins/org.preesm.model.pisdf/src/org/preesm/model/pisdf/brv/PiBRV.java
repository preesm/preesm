/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019 - 2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
import org.preesm.model.pisdf.Actor;
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
    final PiBRV piBRVAlgo = switch (method) {
      case LCM -> new LCMBasedBRV();
      case TOPOLOGY -> new TopologyBasedBRV();
      default -> throw new PreesmRuntimeException("unexpected value for BRV method: [" + method + "]");
    };
    return piBRVAlgo.computeBRV(piGraph);
  }

  /**
   * Print the BRV values of every vertex. For debug purposes.
   */
  public static final void printRV(final Map<AbstractVertex, Long> brv) {
    final Map<PiGraph, Long> levelRV = new LinkedHashMap<>();

    for (final Entry<AbstractVertex, Long> en : brv.entrySet()) {
      final AbstractVertex av = en.getKey();
      final PiGraph container = av.getContainingPiGraph();

      levelRV.computeIfAbsent(container, c -> PiMMHelper.getHierarchichalRV(c, brv));

      final long actorRV = en.getValue();
      final long actorFullRV = actorRV * levelRV.get(container);
      final String msg = av.getVertexPath() + " x" + actorRV + " (total: x" + actorFullRV + ")";
      PreesmLogger.getLogger().log(Level.INFO, msg);
    }
  }

  protected abstract Map<AbstractVertex, Long> computeBRV(final PiGraph piGraph);

  protected Map<AbstractVertex, Long> computeChildrenBRV(final PiGraph parentGraph,
      final Map<AbstractVertex, Long> parentBRV) {
    final Map<AbstractVertex, Long> resultBrv = new LinkedHashMap<>();
    for (final AbstractActor actor : parentGraph.getActors()) {
      PiGraph childGraph = null;
      if (actor instanceof final PiGraph piGraph) {
        childGraph = piGraph;
      } else if (actor instanceof final Actor act && (act.isHierarchical())) {
        childGraph = act.getSubGraph();
      }
      if (childGraph != null) {
        if (parentBRV.get(actor) == 0L) {
          // the BRV of all actors in the subgraph will be 0 as well
          childGraph.getAllActors().forEach(x -> resultBrv.put(x, 0L));
        } else {
          resultBrv.putAll(this.computeBRV(childGraph));
        }
      }
    }
    return resultBrv;
  }

  protected static void updateRVWithInterfaces(final PiGraph graph, final List<AbstractActor> connectedComponent,
      final Map<AbstractVertex, Long> graphBRV) {

    // Compute scaleFactor for input interfaces
    final long scaleFactorIn1 = getInputInterfacesScaleFactor(graph, connectedComponent, 1L, graphBRV, false);
    // Compute scaleFactor for output interfaces
    final long scaleFactorOut1 = getOutputInterfacesScaleFactor(graph, connectedComponent, 1L, graphBRV, false);

    final long scaleFactorMax1 = Math.max(scaleFactorIn1, scaleFactorOut1);
    // Check scaleFactor for input interfaces
    final long scaleFactorInS = getInputInterfacesScaleFactor(graph, connectedComponent, scaleFactorMax1, graphBRV,
        true);
    // Check scaleFactor for output interfaces
    final long scaleFactorOutS = getOutputInterfacesScaleFactor(graph, connectedComponent, scaleFactorMax1, graphBRV,
        true);

    final long scaleFactorMaxS = Math.max(scaleFactorInS, scaleFactorOutS);
    if (scaleFactorMax1 != scaleFactorMaxS) {
      throw new PreesmRuntimeException(
          String.format("Check of scale factor for graph [%s] found a different scale factor (%d vs %d).",
              graph.getVertexPath(), scaleFactorMax1, scaleFactorMaxS));
    }

    if (scaleFactorMaxS > 1) {
      PreesmLogger.getLogger()
          .info(() -> String.format("Scale factor for graph [%s] is: x%d.", graph.getVertexPath(), scaleFactorMaxS));
      // Update RV values based on the interface
      for (final AbstractActor actor : connectedComponent) {
        final long newRV = graphBRV.get(actor) * scaleFactorMaxS;
        graphBRV.put(actor, newRV);
        if ((actor instanceof DelayActor) && (newRV > 1)) {
          final String message = "Inconsistent graph. DelayActor [" + actor.getName() + "] with a repetition vector of "
              + Long.toString(newRV);
          throw new PreesmRuntimeException(message);
        }
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
    final PiGraph motherGraph = graph.getContainingPiGraph();
    if (motherGraph != null) {
      // otherwise it means that we compute a local BRV (from GUI)
      final DataPort opposite = ia.getGraphPort();
      final long oppositeRate = opposite.getExpression().evaluate();
      if (/* motherGraph != graph && */oppositeRate != rate) {
        final String msg = "DataPort [" + opposite.getName() + "] of actor ["
            + opposite.getContainingActor().getVertexPath()
            + "] has different rates from inner interface definition: inner " + rate + " -- outer " + oppositeRate;
        throw new PreesmRuntimeException(msg);
      }
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
   * @param graphBRV
   *          the graph BRV
   * @param emitWarningsForSpecialActors
   *          whether or not warnings about needed special actors should be emit
   * @return new value of scale factor
   */
  private static long getOutputInterfacesScaleFactor(final PiGraph graph, final List<AbstractActor> subgraph,
      final long inscaleFactor, final Map<AbstractVertex, Long> graphBRV, final boolean emitWarningsForSpecialActors) {
    final SortedSet<Long> scaleScaleFactors = new TreeSet<>();
    scaleScaleFactors.add(1L);
    for (final DataOutputInterface out : graph.getDataOutputInterfaces()) {
      final DataInputPort dataInputPort = (DataInputPort) out.getDataPort();
      final long cons = dataInputPort.getPortRateExpression().evaluate();
      checkOppositeInterfaceRate(graph, out, cons);
      final Fifo fifo = dataInputPort.getIncomingFifo();
      final DataOutputPort sourcePort = fifo.getSourcePort();
      final AbstractActor sourceActor = sourcePort.getContainingActor();

      if (sourceActor instanceof InterfaceActor || !subgraph.contains(sourceActor)) {
        continue;
      }

      final long prod = sourcePort.getPortRateExpression().evaluate();
      final long sourceRV = graphBRV.get(sourceActor);
      final long tmp = inscaleFactor * prod * sourceRV;
      if (tmp > 0) {
        long scaleScaleFactor = 1L;
        if (tmp < cons) {
          scaleScaleFactor = (cons + tmp - 1) / tmp;
          scaleScaleFactors.add(scaleScaleFactor);
        }
        final boolean higherProd = tmp > cons && emitWarningsForSpecialActors;
        final boolean lowerUndivisorProd = tmp < cons && cons % tmp != 0;
        if (higherProd || lowerUndivisorProd) {
          // we emit a warning only if producing too much, or not enough but with a wrong multiplicity
          // note that it is not allowed to produce less than the consumed tokens on the output interface
          // at the opposite, if more are produced, a roundbuffer is added.
          String message = String.format(
              "The output interface [%s] does not correspond to its source total production (%d vs %d).",
              out.getVertexPath(), cons, tmp);
          if (higherProd) {
            message += " A Roundbuffer will be added in SRDAG.";
          } else {
            message += String.format(" Scaling factor (>= x%d) is needed.", scaleScaleFactor);
          }
          PreesmLogger.getLogger().log(Level.INFO, message);
        }
      }
    }
    return inscaleFactor * scaleScaleFactors.last();
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
   * @param graphBRV
   *          the graph BRV
   * @param emitWarningsForSpecialActors
   *          whether or not warnings about needed special actors should be emit
   * @return new value of scale factor
   */
  private static long getInputInterfacesScaleFactor(final PiGraph graph, final List<AbstractActor> subgraph,
      final long inscaleFactor, final Map<AbstractVertex, Long> graphBRV, final boolean emitWarningsForSpecialActors) {
    final SortedSet<Long> scaleScaleFactors = new TreeSet<>();
    scaleScaleFactors.add(1L);
    for (final DataInputInterface in : graph.getDataInputInterfaces()) {
      final DataOutputPort dataOutputPort = (DataOutputPort) in.getDataPort();
      final long prod = dataOutputPort.getPortRateExpression().evaluate();
      checkOppositeInterfaceRate(graph, in, prod);
      final Fifo fifo = dataOutputPort.getOutgoingFifo();
      final DataInputPort targetPort = fifo.getTargetPort();
      final AbstractActor targetActor = targetPort.getContainingActor();

      if (targetActor instanceof InterfaceActor || !subgraph.contains(targetActor)) {
        continue;
      }

      final long targetRV = graphBRV.get(targetActor);
      final long cons = targetPort.getPortRateExpression().evaluate();
      final long tmp = inscaleFactor * cons * targetRV;
      if (tmp > 0) {
        long scaleScaleFactor = 1L;
        if (tmp < prod) {
          scaleScaleFactor = (prod + tmp - 1) / tmp;
          scaleScaleFactors.add(scaleScaleFactor);
        }
        final boolean higherCons = tmp > prod && emitWarningsForSpecialActors;
        final boolean lowerUndivisorCons = tmp < prod && prod % tmp != 0;
        if (higherCons || lowerUndivisorCons) {
          // we emit a warning only if consuming too much, or not enough but with a wrong multiplicity
          // note that it is not allowed to leave unconsumed tokens on the input interface
          // at the opposite, if more are consumed, a broadcast is added.
          String message = String.format(
              "The input interface [%s] does not correspond to its target total production (%d vs %d).",
              in.getVertexPath(), prod, tmp);
          if (higherCons) {
            message += " A Broadcast will be added in SRDAG.";
          } else {
            message += String.format(" Scaling factor (>= x%d) is needed.", scaleScaleFactor);
          }
          PreesmLogger.getLogger().log(Level.INFO, message);
        }
      }

    }
    return inscaleFactor * scaleScaleFactors.last();
  }

}
