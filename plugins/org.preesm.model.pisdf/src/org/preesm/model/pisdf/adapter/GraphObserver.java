/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2022 - 2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2013)
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
package org.preesm.model.pisdf.adapter;

import java.util.List;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.preesm.commons.graph.Edge;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.PiMMPackage;

/**
 * The purpose of this {@link Adapter} is to observe the {@link Edge} list of a {@link PiGraph} to detect the addition,
 * the deletion and the renaming of {@link PiGraph} element in order to automatically compute the repercussions on
 * {@link PiGraph} and storage indexes. <br>
 * <br>
 *
 * The observer is also used on {@link PiGraph} {@link Fifo} to track the addition/removal of {@link Delay}.
 *
 * @author kdesnos
 *
 */
public class GraphObserver extends AdapterImpl {

  /**
   * Private static class attribute holding the only instance of {@link GraphObserver}. Use to ensure that only a single
   * instance of {@link GraphObserver} is created.
   */
  private static GraphObserver instance = null;

  /**
   * Method to return the current instance of the {@link GraphObserver} class if one has been instantiated or, if not,
   * create and return the new one.
   *
   * @return The single globally-accessible instance of {@link GraphObserver}
   */
  public static GraphObserver getInstance() {
    if (instance == null) {
      instance = new GraphObserver();
    }
    return instance;
  }

  /**
   * Private constructor of the {@link GraphObserver}. <br>
   * Should only be called by {@link GraphObserver#getInstance()}.
   */
  private GraphObserver() {
    // Nothing to do here
  }

  /**
   * Method called when an {@link AbstractVertex} is possibly added to the Observed {@link PiGraph}. <br>
   * <br>
   * This Method create the {@link Port} port corresponding to the added {@link InterfaceActor} or {@link Parameter} and
   * add it to the {@link PiGraph#getInputPorts()}, the {@link PiGraph#getOutputPorts()}, or the
   * {@link PiGraph#getConfigInputPorts()} list of the {@link PiGraph}. It also handles the vertex storage indexes
   *
   * @param vertex
   *          The {@link AbstractVertex} added to the {@link PiGraph}
   * @param graph
   *          The {@link PiGraph}
   */
  private void addVertex(final AbstractVertex vertex, final PiGraph graph) {

    switch (vertex) {
      case final AbstractActor aa when !(aa instanceof DelayActor) -> graph.incrementActorIndex();
      case final Parameter p -> graph.incrementParameterIndex();
      case final Delay d -> graph.incrementDelayIndex();
      default -> {
        /* Nothing */ }
    }
  }

  /**
   * Method called when an {@link Edge} is possibly added to the Observed {@link PiGraph}. <br>
   * <br>
   * It handles the {@link Edge} storage indexes
   *
   * @param vertex
   *          The {@link Edge} added to the {@link PiGraph}
   * @param graph
   *          The {@link PiGraph}
   */
  private void addEdge(final Edge edge, final PiGraph graph) {

    switch (edge) {
      case final Fifo fifo when !fifo.isDelayPresent() -> graph.incrementFifoWithoutDelayIndex();
      case final Fifo fifo when fifo.isDelayPresent() -> graph.incrementFifoWithDelayIndex();
      default -> {
        /* Nothing */ }
    }
  }

  /**
   * Method called when an {@link AbstractVertex} is possibly removed to the Observed {@link PiGraph}. <br>
   * <br>
   * This Method remove the {@link Port} port corresponding to the removed {@link InterfaceActor} or {@link Parameter}
   * and from the {@link PiGraph#getInputPorts()}, the {@link PiGraph#getOutputPorts()}, or the
   * {@link PiGraph#getConfigInputPorts()} list of the {@link PiGraph}. Also handles storage indexes.
   *
   * @param vertex
   *          The {@link AbstractVertex} removed from the {@link PiGraph}
   * @param graph
   *          The {@link PiGraph}
   */

  private void removeVertex(final AbstractVertex vertex, final PiGraph graph) {

    switch (vertex) {
      case final AbstractActor aa when !(aa instanceof DelayActor) -> graph.decrementActorIndex();
      case final Parameter p -> graph.decrementParameterIndex();
      case final Delay d -> graph.decrementDelayIndex();
      default -> {
        /* Nothing */ }
    }
  }

  /**
   * Method called when an {@link Edge} is possibly removed to the Observed {@link PiGraph}. <br>
   * <br>
   * Handles storage indexes.
   *
   * @param edge
   *          The {@link Edge} removed from the {@link PiGraph}
   * @param graph
   *          The {@link PiGraph}
   */
  private void removeEdge(final Edge edge, final PiGraph graph) {

    switch (edge) {
      case final Fifo fifo when !fifo.isDelayPresent() -> graph.decrementFifoWithoutDelayIndex();
      case final Fifo fifo when fifo.isDelayPresent() -> graph.decrementFifoWithDelayIndex();
      default -> {
        /* Nothing */ }
    }
  }

  @Override
  public void notifyChanged(final Notification notification) {
    super.notifyChanged(notification);

    // Check if the vertices or Parameters are concerned by this
    // notification
    if ((notification.getNotifier() instanceof final PiGraph graph)
        && (notification.getFeatureID(null) == PiMMPackage.PI_GRAPH__VERTICES)) {

      switch (notification.getEventType()) {
        case Notification.ADD:
          // It is safe to cast because we already checked that the
          // notification was caused by an addition to the graph vertices.
          final AbstractVertex vertextoAdd = (AbstractVertex) notification.getNewValue();
          addVertex(vertextoAdd, graph);
          break;

        case Notification.ADD_MANY:
          final List<?> listToAdd = (List<?>) notification.getNewValue();
          listToAdd.forEach(o -> addVertex((AbstractVertex) o, graph));
          break;

        case Notification.REMOVE:
          final AbstractVertex vertexToRemove = (AbstractVertex) notification.getOldValue();
          removeVertex(vertexToRemove, graph);
          break;

        case Notification.REMOVE_MANY:
          final List<?> listToRemove = (List<?>) notification.getOldValue();
          listToRemove.forEach(o -> removeVertex((AbstractVertex) o, graph));
          break;

        default:
          // nothing
      }
    } else if ((notification.getNotifier() instanceof final PiGraph graph)
        && (notification.getFeatureID(null) == PiMMPackage.PI_GRAPH__EDGES)) {

      switch (notification.getEventType()) {
        case Notification.ADD:
          // It is safe to cast because we already checked that the
          // notification was caused by an addition to the graph edge.
          final Edge edgetoAdd = (Edge) notification.getNewValue();
          addEdge(edgetoAdd, graph);
          break;

        case Notification.ADD_MANY:
          final List<?> listToAdd = (List<?>) notification.getNewValue();
          listToAdd.forEach(o -> addEdge((Edge) o, graph));
          break;

        case Notification.REMOVE:
          final Edge edgeToRemove = (Edge) notification.getOldValue();
          removeEdge(edgeToRemove, graph);
          break;

        case Notification.REMOVE_MANY:
          final List<?> listToRemove = (List<?>) notification.getOldValue();
          listToRemove.forEach(o -> removeEdge((Edge) o, graph));
          break;

        default:
          // nothing
      }
    } else if ((notification.getNotifier() instanceof final Fifo fifo)
        && (notification.getFeatureID(null) == PiMMPackage.FIFO__DELAY)) {

      final PiGraph graph = fifo.getContainingPiGraph();

      // if the fifo isn't in a graph, nothing to do
      if (graph == null) {
        return;
      }

      final Delay oldDelay = (Delay) notification.getOldValue();
      final Delay newDelay = (Delay) notification.getNewValue();

      // Only the SET event is checked
      if (notification.getEventType() == Notification.SET) {
        // If the fifo change flipped between FifoWithDelay and FifoWithoutDelay, it needs to be re-placed in the list
        if ((oldDelay == null && newDelay != null) || (oldDelay != null && newDelay == null)) {
          handleFifoDelayChange(graph, fifo);
        }
      }
    }

    // TODO Add support when a Parameter changes from a config interface to a non config param
  }

  private synchronized void handleFifoDelayChange(PiGraph graph, Fifo fifo) {
    // remove observer
    graph.eAdapters().remove(this);

    // remove fifo from graph
    graph.removeFifo(fifo);

    // manually decrement index
    if (fifo.isDelayPresent()) {
      graph.decrementFifoWithoutDelayIndex();
    } else {
      graph.decrementFifoWithDelayIndex();
    }

    // re-attach observer
    graph.eAdapters().add(this);

    // add fifo to graph
    graph.addFifo(fifo);
  }

}
