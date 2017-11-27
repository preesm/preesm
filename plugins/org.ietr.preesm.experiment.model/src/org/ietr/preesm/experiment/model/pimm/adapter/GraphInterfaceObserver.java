/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
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
package org.ietr.preesm.experiment.model.pimm.adapter;

import java.util.List;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Port;

// TODO: Auto-generated Javadoc
/**
 * The purpose of this {@link Adapter} is to observe the {@link PiGraph#getVertices()} list of a {@link PiGraph} to detect the addition, the deletion and the
 * renaming of {@link PiGraph} interfaces in order to automatically compute the repercussions on {@link PiGraph#getInputPorts()},
 * {@link PiGraph#getOutputPorts()} and {@link PiGraph#getConfigInputPorts()}.
 *
 * @author kdesnos
 *
 */
public class GraphInterfaceObserver extends AdapterImpl {

  /**
   * Default constructor of the {@link GraphInterfaceObserver}.
   */
  public GraphInterfaceObserver() {
    // Nothing to do here
  }

  /**
   * Method called when an Interface is possibly added to the Observed {@link PiGraph}. <br>
   * <br>
   * This Method create the {@link Port} port corresponding to the added {@link InterfaceActor} or {@link Parameter} and add it to the
   * {@link PiGraph#getInputPorts()}, the {@link PiGraph#getOutputPorts()}, or the {@link PiGraph#getConfigInputPorts()} list of the {@link PiGraph}.
   *
   * @param vertex
   *          The {@link InterfaceActor} or {@link Parameter} added to the {@link PiGraph}
   * @param graph
   *          The {@link PiGraph}
   */
  protected void add(final AbstractVertex vertex, final PiGraph graph) {

    // If the added vertex is an Interface of the graph
    if (vertex instanceof InterfaceActor) {
      addInterfaceActor((InterfaceActor) vertex, graph);
      return;
    }

    // If the added vertex is an Parameter and an Interface of the graph
    if ((vertex instanceof Parameter) && ((Parameter) vertex).isConfigurationInterface()) {
      addParamInterfaceActor((ConfigInputInterface) vertex, graph);
      return;
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
    Port port;
    switch (iActor.getKind()) {
      case DataInputInterface.KIND:
        port = PiMMFactory.eINSTANCE.createDataInputPort();
        graph.getDataInputPorts().add((DataInputPort) port);
        break;
      case DataOutputInterface.KIND:
        port = PiMMFactory.eINSTANCE.createDataOutputPort();
        graph.getDataOutputPorts().add((DataOutputPort) port);
        break;
      case ConfigOutputInterface.KIND:
        port = PiMMFactory.eINSTANCE.createConfigOutputPort();
        graph.getConfigOutputPorts().add((ConfigOutputPort) port);
        break;
      default:
        return;
    }

    // Set the interface properties
    port.setName(iActor.getName());
    iActor.setGraphPort(port);
    return;
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
    final ConfigInputPort port = PiMMFactory.eINSTANCE.createConfigInputPort();
    port.setName(param.getName());
    graph.getConfigInputPorts().add(port);

    // Set the parameter Property
    param.setGraphPort(port);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
   */
  @Override
  public void notifyChanged(final Notification notification) {
    super.notifyChanged(notification);

    // Check if the vertices or Parameters are concerned by this
    // notification
    if ((notification.getNotifier() instanceof PiGraph)
        && ((notification.getFeatureID(null) == PiMMPackage.PI_GRAPH__VERTICES) || (notification.getFeatureID(null) == PiMMPackage.PI_GRAPH__PARAMETERS))) {

      final PiGraph graph = (PiGraph) notification.getNotifier();

      switch (notification.getEventType()) {
        case Notification.ADD: {
          // It is safe to cast because we already checked that the
          // notification
          // was caused by an addition to the graph vertices.
          final AbstractVertex vertex = (AbstractVertex) notification.getNewValue();
          add(vertex, graph);
          break;
        }

        case Notification.ADD_MANY: {
          final List<?> list = (List<?>) notification.getNewValue();
          for (final Object object : list) {
            add((AbstractVertex) object, graph);
          }
          break;
        }

        case Notification.REMOVE: {
          final AbstractVertex vertex = (AbstractVertex) notification.getOldValue();
          remove(vertex, graph);
          break;
        }

        case Notification.REMOVE_MANY: {
          final List<?> list = (List<?>) notification.getOldValue();
          for (final Object object : list) {
            remove((AbstractVertex) object, graph);
          }
          break;
        }

        default:
          break;
      }
    }

    // TODO Add support when a Parameter changes from a config interface to
    // a non config param
  }

  /**
   * Method called when an Interface is possibly removed to the Observed {@link PiGraph}. <br>
   * <br>
   * This Method remove the {@link Port} port corresponding to the removed {@link InterfaceActor} or {@link Parameter} and from the
   * {@link PiGraph#getInputPorts()}, the {@link PiGraph#getOutputPorts()}, or the {@link PiGraph#getConfigInputPorts()} list of the {@link PiGraph}.
   *
   * @param vertex
   *          The {@link InterfaceActor} or {@link Parameter} removed from the {@link PiGraph}
   * @param graph
   *          The {@link PiGraph}
   */
  protected void remove(final AbstractVertex vertex, final PiGraph graph) {

    if (vertex instanceof InterfaceActor) {
      removeInterfaceActor((InterfaceActor) vertex, graph);
      return;
    }

    if ((vertex instanceof Parameter) && ((Parameter) vertex).isConfigurationInterface()) {
      removeParamInterfaceActor((ConfigInputInterface) vertex, graph);
      return;
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
    // We remove from both list, but only one will actually remove
    // something.
    graph.getDataInputPorts().remove(iActor.getGraphPort());
    graph.getDataOutputPorts().remove(iActor.getGraphPort());
    return;
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
