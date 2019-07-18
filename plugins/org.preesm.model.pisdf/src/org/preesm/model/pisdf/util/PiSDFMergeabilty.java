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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataOutputPort;
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
   * Used to check if these actors are mergeable i.e. do not introduce cycle if clustered
   *
   * @param a
   *          first actor
   * @param b
   *          second actor
   * @return true if the couple is mergeable
   */
  public static boolean isMergeable(final AbstractActor a, final AbstractActor b) {
    final List<AbstractActor> predA = PiSDFTopologyHelper.getPredecessors(a);
    final List<AbstractActor> predB = PiSDFTopologyHelper.getPredecessors(b);
    final List<AbstractActor> succA = PiSDFTopologyHelper.getSuccessors(a);
    final List<AbstractActor> succB = PiSDFTopologyHelper.getSuccessors(b);
    predA.retainAll(succB);
    predB.retainAll(succA);
    return predA.isEmpty() && predB.isEmpty();
  }

  /**
   * Used to get the list of connected-couple that can be merged. Connected-couple means that actors are connected
   * together through one or more Fifo.
   *
   * @param graph
   *          input graph
   * @return list of mergeable connected-couple
   */
  public static List<Pair<AbstractActor, AbstractActor>> getConnectedCouple(final PiGraph graph) {
    List<Pair<AbstractActor, AbstractActor>> listCouple = new LinkedList<>();
    List<AbstractActor> graphActors = graph.getActors();

    // Get every mergeable connected-couple
    for (AbstractActor a : graphActors) {
      for (DataOutputPort dop : a.getDataOutputPorts()) {
        AbstractActor b = dop.getOutgoingFifo().getTargetPort().getContainingActor();
        // Verify that actor are connected together
        if (PiSDFMergeabilty.isMergeable(a, b) && !(b instanceof InterfaceActor)) {
          Pair<AbstractActor, AbstractActor> couple = new ImmutablePair<>(a, b);
          listCouple.add(couple);
        }
      }
    }

    return listCouple;
  }

}
