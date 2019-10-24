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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.common.notify.Notification;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.preesm.commons.model.PreesmContentAdapter;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
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
public class PiSDFTopologyHelper extends PreesmContentAdapter {

  private final PiGraph                            pigraph;
  private DirectedPseudograph<AbstractActor, Fifo> internalGraphCache = null;

  public PiSDFTopologyHelper(final PiGraph pigraph) {
    this.pigraph = pigraph;
    this.pigraph.eAdapters().add(this);
  }

  @Override
  public void notifyChanged(Notification notification) {
    this.internalGraphCache = null;
  }

  /**
   * Build a DAG from a PiGraph and a schedule that represents precedence (thus hold no data). The graph transitive
   * closure is computed.
   */
  private final DirectedPseudograph<AbstractActor, Fifo> getGraph() {
    if (internalGraphCache != null) {
      return internalGraphCache;
    } else {
      final DirectedPseudograph<AbstractActor, Fifo> internalGraph = new DirectedPseudograph<>(null, null, false);
      pigraph.getActors().forEach(internalGraph::addVertex);
      pigraph.getFifos().forEach(fifo -> internalGraph.addEdge(fifo.getSourcePort().getContainingActor(),
          fifo.getTargetPort().getContainingActor()));
      internalGraphCache = internalGraph;
      return internalGraph;
    }
  }

  /**
   *
   */
  public final List<AbstractActor> sort() {
    final TopologicalOrderIterator<AbstractActor,
        Fifo> topologicalOrderIterator = new TopologicalOrderIterator<>(getGraph());
    final List<AbstractActor> arrayList = new ArrayList<>(this.pigraph.getActors().size());
    topologicalOrderIterator.forEachRemaining(arrayList::add);
    return Collections.unmodifiableList(arrayList);
  }

  /**
   * returns true if potentialPred is actually a predecessor of target
   */
  public final boolean isPredecessor(final AbstractActor potentialPred, final AbstractActor target) {
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
  public final boolean isSuccessor(final AbstractActor potentialSucc, final AbstractActor target) {
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
  public final boolean isThereIsALongPath(final AbstractActor potentialSucc, final AbstractActor target) {
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
  public final List<AbstractActor> getDirectPredecessorsOf(final AbstractActor a) {
    final List<AbstractActor> result = new ArrayList<>();
    a.getDataInputPorts().stream().forEach(x -> result.add(x.getIncomingFifo().getSourcePort().getContainingActor()));
    return Collections.unmodifiableList(result);
  }

  /**
   * Used to get actors connected in output of a specified PiSDF actor
   *
   * @param a
   *          actor
   * @return actors that are directly connected in output of a
   */
  public final List<AbstractActor> getDirectSuccessorsOf(final AbstractActor a) {
    final List<AbstractActor> result = new ArrayList<>();
    a.getDataOutputPorts().stream().forEach(x -> result.add(x.getOutgoingFifo().getTargetPort().getContainingActor()));
    return Collections.unmodifiableList(result);
  }

  /**
   * Get all predecessors of actor. Will loop infinitely if actor is not part of a DAG.
   */
  public final List<AbstractActor> getAllPredecessorsOf(final AbstractActor actor) {
    final Set<AbstractActor> result = new LinkedHashSet<>();
    final List<AbstractActor> directPredecessorsOf = getDirectPredecessorsOf(actor);
    result.addAll(directPredecessorsOf);
    directPredecessorsOf.stream().map(this::getAllPredecessorsOf).flatMap(List::stream).distinct().forEach(result::add);
    return Collections.unmodifiableList(new ArrayList<>(result));
  }

  /**
   * Get all successors of actor. Will loop infinitely if actor is not part of a DAG.
   */
  public final List<AbstractActor> getAllSuccessorsOf(final AbstractActor actor) {
    final Set<AbstractActor> result = new LinkedHashSet<>();
    final List<AbstractActor> directSuccessorsOf = getDirectSuccessorsOf(actor);
    result.addAll(directSuccessorsOf);
    directSuccessorsOf.stream().map(this::getAllSuccessorsOf).flatMap(List::stream).distinct().forEach(result::add);
    return Collections.unmodifiableList(new ArrayList<>(result));
  }

  /**
   * Get all edges on the paths to all predecessors of actor. Will loop infinitely if actor is not part of a DAG.
   */
  public final List<Fifo> getPredecessorEdgesOf(final AbstractActor actor) {
    final Set<Fifo> result = new LinkedHashSet<>();
    actor.getDataInputPorts().stream().map(DataPort::getFifo).forEach(result::add);
    final List<AbstractActor> allPredecessorsOf = getAllPredecessorsOf(actor);
    allPredecessorsOf.stream().map(AbstractActor::getDataInputPorts).flatMap(List::stream).map(DataPort::getFifo)
        .forEach(result::add);
    return Collections.unmodifiableList(new ArrayList<>(result));
  }

  /**
   * Get all edges on the paths to all successors of actor. Will loop infinitely if actor is not part of a DAG.
   */
  public final List<Fifo> getSuccessorEdgesOf(final AbstractActor actor) {
    final Set<Fifo> result = new LinkedHashSet<>();
    actor.getDataOutputPorts().stream().map(DataPort::getFifo).forEach(result::add);
    final List<AbstractActor> allSuccessorsOf = getAllSuccessorsOf(actor);
    allSuccessorsOf.stream().map(AbstractActor::getDataOutputPorts).flatMap(List::stream).map(DataPort::getFifo)
        .forEach(result::add);
    return Collections.unmodifiableList(new ArrayList<>(result));
  }
}
