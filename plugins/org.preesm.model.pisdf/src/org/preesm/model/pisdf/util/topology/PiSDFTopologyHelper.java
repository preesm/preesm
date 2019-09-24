/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019)
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
package org.preesm.model.pisdf.util.topology;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.util.topology.IsThereALongPathSwitch.ThereIsALongPathException;
import org.preesm.model.pisdf.util.topology.PiSDFPredecessorSwitch.IsPredecessorSwitch;
import org.preesm.model.pisdf.util.topology.PiSDFPredecessorSwitch.PredecessorFoundException;
import org.preesm.model.pisdf.util.topology.PiSDFSuccessorSwitch.IsSuccessorSwitch;
import org.preesm.model.pisdf.util.topology.PiSDFSuccessorSwitch.SuccessorFoundException;

/**
 *
 * @author anmorvan
 *
 */
public class PiSDFTopologyHelper {

  private PiSDFTopologyHelper() {
    // forbid instantiation
  }

  public static final Comparator<AbstractActor> getComparator() {
    return new PiSDFTopologicalComparator();
  }

  public static final List<AbstractActor> sort(final List<AbstractActor> actors) {
    return PiSDFTopologicalSorter.depthFirstTopologicalSort(actors);
  }

  /**
   * returns true if potentialPred is actually a predecessor of target
   */
  public static final boolean isPredecessor(final AbstractActor potentialPred, final AbstractActor target) {
    try {
      new IsPredecessorSwitch(target).doSwitch(potentialPred);
      return false;
    } catch (final PredecessorFoundException e) {
      return true;
    }
  }

  /**
   * returns true if potentialSucc is actually a successor of target
   */
  public static final boolean isSuccessor(final AbstractActor potentialSucc, final AbstractActor target) {
    try {
      new IsSuccessorSwitch(target).doSwitch(potentialSucc);
      return false;
    } catch (final SuccessorFoundException e) {
      return true;
    }
  }

  /**
   * Returns true if there is a long path from potentialSucc to target. A long path is defined as a path that encounters
   * more than one Fifo.
   */
  public static final boolean isThereIsALongPath(final AbstractActor potentialSucc, final AbstractActor target) {
    try {
      new IsThereALongPathSwitch(target).doSwitch(potentialSucc);
      return false;
    } catch (final ThereIsALongPathException e) {
      return true;
    }
  }

  /**
   * Used to get actors connected in input of a specified PiSDF actor
   *
   * @param a
   *          actor
   * @return actors that are directly connected in input of a
   */
  public static final List<AbstractActor> getDirectPredecessorsOf(final AbstractActor a) {
    List<AbstractActor> result = new ArrayList<>();
    a.getDataInputPorts().stream().forEach(x -> result.add(x.getIncomingFifo().getSourcePort().getContainingActor()));
    return result;
  }

  /**
   * Used to get actors connected in output of a specified PiSDF actor
   *
   * @param a
   *          actor
   * @return actors that are directly connected in output of a
   */
  public static final List<AbstractActor> getDirectSuccessorsOf(final AbstractActor a) {
    List<AbstractActor> result = new ArrayList<>();
    a.getDataOutputPorts().stream().forEach(x -> result.add(x.getOutgoingFifo().getTargetPort().getContainingActor()));
    return result;
  }

}
