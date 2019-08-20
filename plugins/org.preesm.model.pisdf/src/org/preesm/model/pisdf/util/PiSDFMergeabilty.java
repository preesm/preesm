/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * dylangageot [gageot.dylan@gmail.com] (2019)
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
package org.preesm.model.pisdf.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.preesm.commons.CollectionUtil;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.topology.PiSDFTopologyHelper;

/**
 * @author dgageot
 *
 *         Handle useful method to get actor predecessors/successors and determine if a couple of actors is mergeable
 *         i.e. clusterizable. Many of theses methods have been converted from SDF to PiSDF, originally developed by
 *         Julien Hascoet.
 *
 */
public class PiSDFMergeabilty {

  /**
   * 
   * One of the three conditions to ensure that mergeability is possible
   * 
   * @param x
   *          first actor
   * @param y
   *          second actor
   * @param brv
   *          repetition vector
   * @return true if precedence shift condition is valid, false otherwise
   */
  public static boolean isPrecedenceShiftConditionValid(AbstractActor x, AbstractActor y,
      Map<AbstractVertex, Long> brv) {

    List<Fifo> incomingFifos = new LinkedList<>();
    List<Fifo> outgoingFifos = new LinkedList<>();

    for (DataInputPort dip : x.getDataInputPorts()) {
      // Add all incoming fifo that are not contained in the future cluster
      if (dip.getIncomingFifo().getSource() != y) {
        incomingFifos.add(dip.getIncomingFifo());
      }
    }

    for (DataOutputPort dop : x.getDataOutputPorts()) {
      // Add all outgoing fifo that are not contained in the future cluster
      if (dop.getOutgoingFifo().getTarget() != y) {
        outgoingFifos.add(dop.getOutgoingFifo());
      }
    }

    // Compute cluster repetition
    List<AbstractVertex> actorList = new LinkedList<>();
    actorList.add(x);
    actorList.add(y);
    long clusterRepetition = MathFunctionsHelper.gcd(CollectionUtil.mapGetAll(brv, actorList));

    boolean result = true;
    boolean delayInside = false;

    for (Fifo incomingFifo : incomingFifos) {
      long prod = incomingFifo.getSourcePort().getExpression().evaluate();
      long cons = incomingFifo.getTargetPort().getExpression().evaluate();
      long individualRepetition = brv.get(x) / clusterRepetition;
      Delay delay = incomingFifo.getDelay();
      long delayValue;
      // If there is a delay, evaluate it capacity
      if (delay == null) {
        delayValue = 0;
      } else {
        delayValue = delay.getExpression().evaluate();
        delayInside = true;
      }
      long k1 = prod / (individualRepetition * cons);
      long k2 = delayValue / (individualRepetition * cons);
      // Precedence shift condition verification
      if (!((k1 == (int) (k1)) && (k2 == (int) (k2)) && (k1 > 0) && (k2 >= 0))) {
        result = false;
      }
    }

    for (Fifo outgoingFifo : outgoingFifos) {
      double prod = outgoingFifo.getSourcePort().getExpression().evaluate();
      double cons = outgoingFifo.getTargetPort().getExpression().evaluate();
      double individualRepetition = brv.get(x) / clusterRepetition;
      Delay delay = outgoingFifo.getDelay();
      double delayValue;
      if (delay == null) {
        delayValue = 0;
      } else {
        delayValue = delay.getExpression().evaluate();
        delayInside = true;
      }
      double k1 = cons / (individualRepetition * prod);
      double k2 = delayValue / (individualRepetition * prod);
      // Precedence shift condition verification
      if (!((k1 == (int) (k1)) && (k2 == (int) (k2)) && (k1 > 0) && (k2 >= 0))) {
        result = false;
      }
    }

    // If no delay was present on all fifos, the result can be true without compromising clustering
    return result || !delayInside;
  }

  /**
   * Used to check if these actors do not introduce cycle if clustered
   *
   * @param x
   *          first actor
   * @param y
   *          second actor
   * @return true if the couple does not introduce cycle
   */
  public static boolean isCycleIntroductionConditionValid(final AbstractActor x, final AbstractActor y) {
    return !PiSDFTopologyHelper.isMoreThanOneSimplePath(x, y);
  }

  /**
   * @param x
   *          first actor
   * @param y
   *          second actor
   * @param brv
   *          repetitionVector
   * @return true if condition is verified
   */
  public static boolean isHiddenDelayConditionValid(AbstractActor x, AbstractActor y, Map<AbstractVertex, Long> brv) {
    List<Fifo> outgoingFifos = new LinkedList<>();

    for (DataOutputPort dop : x.getDataOutputPorts()) {
      // Add all outgoing fifo that are not contained in the future cluster
      if (dop.getOutgoingFifo().getDelay() == null) {
        outgoingFifos.add(dop.getOutgoingFifo());
      }
    }

    long k1 = brv.get(x) / brv.get(y);
    long k2 = brv.get(y) / brv.get(x);
    return !outgoingFifos.isEmpty() && ((k1 == (int) (k1)) || (k2 == (int) (k2)));
  }

  /**
   * @param x
   *          first actor
   * @param y
   *          second actor
   * @param brv
   *          repetition vector
   * @return true if mergeability is verified
   */
  public static boolean isMergeable(AbstractActor x, AbstractActor y, Map<AbstractVertex, Long> brv) {
    // Verify that actors are contained into BRV
    if (!brv.containsKey(x) || !brv.containsKey(y)) {
      throw new PreesmRuntimeException("PiSDFMergeability: Actors not contained into repetition vector");
    }
    // Verify theses fourth conditions
    boolean precedenceShiftA = isPrecedenceShiftConditionValid(x, y, brv);
    boolean precedenceShiftB = isPrecedenceShiftConditionValid(y, x, brv);
    boolean cycleIntroduction = isCycleIntroductionConditionValid(x, y);
    boolean hiddenDelay = isHiddenDelayConditionValid(x, y, brv);
    return cycleIntroduction && hiddenDelay && precedenceShiftA && precedenceShiftB;
  }

  /**
   * Used to get the list of connected-couple that can be merged. Connected-couple means that actors are connected
   * together through one or more Fifo.
   *
   * @param graph
   *          input graph
   * @return list of mergeable connected-couple
   */
  public static List<Pair<AbstractActor, AbstractActor>> getConnectedCouple(final PiGraph graph,
      final Map<AbstractVertex, Long> brv) {
    List<Pair<AbstractActor, AbstractActor>> listCouple = new LinkedList<>();

    // Retrieve actors that are not interface nor delay
    List<AbstractActor> graphActors = new LinkedList<>();
    for (AbstractActor actor : graph.getActors()) {
      if (!(actor instanceof InterfaceActor) && !(actor instanceof DelayActor)) {
        graphActors.add(actor);
      }
    }

    // Get every mergeable connected-couple
    for (AbstractActor a : graphActors) {
      for (DataOutputPort dop : a.getDataOutputPorts()) {
        AbstractActor b = dop.getOutgoingFifo().getTargetPort().getContainingActor();
        // Verify that actor are connected together and mergeable
        if (!(b instanceof InterfaceActor) && (b != a) && PiSDFMergeabilty.isMergeable(a, b, brv)) {
          Pair<AbstractActor, AbstractActor> couple = new ImmutablePair<>(a, b);
          listCouple.add(couple);
        }
      }
    }

    return listCouple;
  }

}
