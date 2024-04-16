/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
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
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.PiMMPackage;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 * The purpose of this {@link Adapter} is to observe the {@link Vertex} list and {@link Edge} of a {@link PiGraph} to
 * detect the addition, the deletion and the renaming of {@link PiGraph} element in order to automatically compute the
 * repercussions on {@link PiGraph} and storage indexes. <br>
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
  protected void addVertex(final AbstractVertex vertex, final PiGraph graph) {

    if ((vertex instanceof AbstractActor) && !(vertex instanceof DelayActor)) {
      if (vertex instanceof final InterfaceActor iActorVertex) {
        // If the added vertex is an Interface of the graph
        addInterfaceActor(iActorVertex, graph);
      }
      graph.updateActorIndex();
    } else if (vertex instanceof final Parameter paramVertex) {
      if (paramVertex.isConfigurationInterface()) {
        // If the added vertex is an Parameter and an Interface of the graph
        addParamInterfaceActor((ConfigInputInterface) vertex, graph);
      }
      graph.updateParameterIndex();
    } else if ((vertex instanceof Delay)) {
      graph.updateDelayIndex();
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
  protected void addEdge(final Edge edge, final PiGraph graph) {

    if (edge instanceof final Fifo fifo) {
      if (fifo.isHasADelay()) {
        graph.updateFifoWithDelayIndex();
      } else {
        graph.updateFifoWithoutDelayIndex();
      }
    } else if (edge instanceof Dependency) {
      // Nothing to do
    }
  }

  /**
   * Create the {@link Port} corresponding to an {@link InterfaceActor} added to the {@link PiGraph}.
   *
   * @param iActor
   *          the {@link InterfaceActor} added to the {@link PiGraph}
   * @param graph
   *          the observed {@link PiGraph}
   */
  protected void addInterfaceActor(final InterfaceActor iActor, final PiGraph graph) {
    // Create the Associated port and store it in the appropriate List
    DataPort port;
    switch (iActor.getKind()) {
      case DATA_INPUT:
        port = PiMMUserFactory.instance.createDataInputPort();
        graph.getDataInputPorts().add((DataInputPort) port);
        break;
      case DATA_OUTPUT:
        port = PiMMUserFactory.instance.createDataOutputPort();
        graph.getDataOutputPorts().add((DataOutputPort) port);
        break;
      case CFG_OUTPUT:
        port = PiMMUserFactory.instance.createConfigOutputPort();
        graph.getConfigOutputPorts().add((ConfigOutputPort) port);
        break;
      default:
        return;
    }

    // Set the interface properties
    port.setName(iActor.getName());
    iActor.setGraphPort(port);
  }

  /**
   * Create the {@link Port} corresponding to an interface {@link Parameter} added to the {@link PiGraph}.
   *
   * @param param
   *          the {@link Parameter} added to the {@link PiGraph}
   * @param graph
   *          the observed {@link PiGraph}
   */
  protected void addParamInterfaceActor(final ConfigInputInterface param, final PiGraph graph) {
    final ConfigInputPort port = PiMMUserFactory.instance.createConfigInputPort();
    port.setName(param.getName());
    graph.getConfigInputPorts().add(port);

    // Set the parameter Property
    param.setGraphPort(port);
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
        // If a delay as been added to the fifo
        if ((oldDelay == null) && (newDelay != null)) {
          // The delay was attached to the fifo
          graph.removeFifo(fifo);
          fifo.setHasADelay(true);
          graph.addFifo(fifo);
        } else if ((oldDelay != null) && (newDelay != null)) {
          // The fifo had its delay replaced by another
          // Nothing to do
        }
        if ((oldDelay != null) && (newDelay == null)) {
          // The delay was removed from the fifo

          // Ensuring the Fifo to remove is still tagged as a Fifo with delay
          // The Undo feature will untag the fifo before removing it from the graph
          fifo.setHasADelay(true);

          graph.removeFifo(fifo);
          fifo.setHasADelay(false);
          graph.addFifo(fifo);
        } else {
          // should never go there
        }
      }
    }

    // TODO Add support when a Parameter changes from a config interface to a non config param
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
  protected void removeVertex(final AbstractVertex vertex, final PiGraph graph) {
    if ((vertex instanceof final Parameter paramVertex)) {
      if (paramVertex.isConfigurationInterface()) {
        // If the added vertex is an Parameter and an Interface of the graph
        removeParamInterfaceActor((ConfigInputInterface) vertex, graph);
      }
      graph.updateParameterIndex();
    } else if ((vertex instanceof AbstractActor) && !(vertex instanceof DelayActor)) {
      if (vertex instanceof final InterfaceActor iActorVertex) {
        removeInterfaceActor(iActorVertex, graph);
      }
      graph.updateActorIndex();
    } else if (vertex instanceof Delay) {
      graph.updateDelayIndex();
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
  protected void removeEdge(final Edge edge, final PiGraph graph) {
    if (edge instanceof final Fifo fifo) {
      // If the added vertex is an Interface of the graph
      if (fifo.isHasADelay()) {
        graph.updateFifoWithDelayIndex();
      } else {
        graph.updateFifoWithoutDelayIndex();
      }
    } else if (edge instanceof Dependency) {
      // Nothing to do
    }
  }

  /**
   * Remove the {@link Port} corresponding to an {@link InterfaceActor} removed to the {@link PiGraph}.
   *
   * @param iActor
   *          the {@link InterfaceActor} removed from the {@link PiGraph}
   * @param graph
   *          the observed {@link PiGraph}
   */
  protected void removeInterfaceActor(final InterfaceActor iActor, final PiGraph graph) {
    // We remove from both list, but only one will actually remove something.
    graph.getDataInputPorts().remove(iActor.getGraphPort());
    graph.getDataOutputPorts().remove(iActor.getGraphPort());
  }

  /**
   * Remove the {@link Port} corresponding to an Interface {@link Parameter} removed from the {@link PiGraph}.
   *
   * @param param
   *          the Interface {@link Parameter} removed from the {@link PiGraph}
   * @param graph
   *          the observed {@link PiGraph}
   */
  protected void removeParamInterfaceActor(final ConfigInputInterface param, final PiGraph graph) {
    graph.getConfigInputPorts().remove(param.getGraphPort());
  }
}
