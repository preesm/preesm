package org.ietr.preesm.experiment.model.pimm.adapter;

import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.InputPort;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.OutputPort;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.SinkInterface;
import org.ietr.preesm.experiment.model.pimm.SourceInterface;

/**
 * The purpose of this {@link Adapter} is to observe the
 * {@link Graph#getVertices()} list of a {@link Graph} to detect the addition,
 * the deletion and the renaming of {@link Graph} interfaces in order to
 * automatically compute the repercussions on {@link Graph#getInputPorts()},
 * {@link Graph#getOutputPorts()} and {@link Graph#getConfigInputPorts()}.
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
	 * Method called when an Interface is possibly added to the Observed
	 * {@link Graph}. <br>
	 * <br>
	 * This Method create the {@link Port} port corresponding to the added
	 * {@link InterfaceActor} or {@link Parameter} and add it to the
	 * {@link Graph#getInputPorts()}, the {@link Graph#getOutputPorts()}, or the
	 * {@link Graph#getConfigInputPorts()} list of the {@link Graph}.
	 * 
	 * @param vertex
	 *            The {@link InterfaceActor} or {@link Parameter} added to the
	 *            {@link Graph}
	 * @param graph
	 *            The {@link Graph}
	 */
	protected void add(AbstractVertex vertex, Graph graph) {

		// If the added vertex is an Interface of the graph
		if (vertex instanceof InterfaceActor) {
			addInterfaceActor((InterfaceActor) vertex, graph);
			return;
		}

		// If the added vertex is an Parameter and an Interface of the graph
		if (vertex instanceof Parameter
				&& ((Parameter) vertex).isConfigurationInterface()) {
			addParamInterfaceActor((Parameter) vertex, graph);
			return;
		}
	}

	/**
	 * Create the {@link Port} corresponding to an {@link InterfaceActor} added
	 * to the {@link Graph}.
	 * 
	 * @param iActor
	 *            the {@link InterfaceActor} added to the {@link Graph}
	 * @param graph
	 *            the observed {@link Graph}
	 */
	protected void addInterfaceActor(InterfaceActor iActor, Graph graph) {
		// Create the Associated port and store it in the appropriate List
		Port port;
		switch (iActor.getKind()) {
		case SourceInterface.KIND:
			port = PiMMFactory.eINSTANCE.createInputPort();
			graph.getInputPorts().add((InputPort) port);
			break;
		case SinkInterface.KIND:
			port = PiMMFactory.eINSTANCE.createOutputPort();
			graph.getOutputPorts().add((OutputPort) port);
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
	 * Create the {@link Port} corresponding to an interface {@link Parameter}
	 * added to the {@link Graph}.
	 * 
	 * @param param
	 *            the {@link Parameter} added to the {@link Graph}
	 * @param graph
	 *            the observed {@link Graph}
	 */
	protected void addParamInterfaceActor(Parameter param, Graph graph) {
		ConfigInputPort port = PiMMFactory.eINSTANCE.createConfigInputPort();
		port.setName(param.getName());
		graph.getConfigInputPorts().add(port);

		// Set the parameter Property
		param.setGraphPort(port);
	}

	@Override
	public void notifyChanged(Notification notification) {
		super.notifyChanged(notification);

		// Check if the vertices or Parameters are concerned by this
		// notification
		if (notification.getNotifier() instanceof Graph
				&& (notification.getFeatureID(null) == PiMMPackage.GRAPH__VERTICES || notification
						.getFeatureID(null) == PiMMPackage.GRAPH__PARAMETERS)) {

			Graph graph = (Graph) notification.getNotifier();

			switch (notification.getEventType()) {
			case Notification.ADD: {
				// It is safe to cast because we already checked that the
				// notification
				// was caused by an addition to the graph vertices.
				AbstractVertex vertex = (AbstractVertex) notification
						.getNewValue();
				add(vertex, graph);
				break;
			}

			case Notification.ADD_MANY: {
				List<?> list = (List<?>) notification.getNewValue();
				for (Object object : list) {
					add((AbstractVertex) object, graph);
				}
				break;
			}

			case Notification.REMOVE: {
				AbstractVertex vertex = (AbstractVertex) notification
						.getOldValue();
				remove(vertex, graph);
				break;
			}

			case Notification.REMOVE_MANY: {
				List<?> list = (List<?>) notification.getOldValue();
				for (Object object : list) {
					remove((AbstractVertex) object, graph);
				}
				break;
			}
			}
		}

		// TODO Add support when a Parameter changes from a config interface to
		// a non config param
	}

	/**
	 * Method called when an Interface is possibly removed to the Observed
	 * {@link Graph}. <br>
	 * <br>
	 * This Method remove the {@link Port} port corresponding to the removed
	 * {@link InterfaceActor} or {@link Parameter} and from the
	 * {@link Graph#getInputPorts()}, the {@link Graph#getOutputPorts()}, or the
	 * {@link Graph#getConfigInputPorts()} list of the {@link Graph}.
	 * 
	 * @param vertex
	 *            The {@link InterfaceActor} or {@link Parameter} removed from
	 *            the {@link Graph}
	 * @param graph
	 *            The {@link Graph}
	 */
	protected void remove(AbstractVertex vertex, Graph graph) {

		if (vertex instanceof InterfaceActor) {
			removeInterfaceActor((InterfaceActor) vertex, graph);
			return;
		}

		if (vertex instanceof Parameter
				&& ((Parameter) vertex).isConfigurationInterface()) {
			removeParamInterfaceActor((Parameter) vertex, graph);
			return;
		}
	}

	/**
	 * Remove the {@link Port} corresponding to an {@link InterfaceActor}
	 * removed to the {@link Graph}.
	 * 
	 * @param iActor
	 *            the {@link InterfaceActor} removed from the {@link Graph}
	 * @param graph
	 *            the observed {@link Graph}
	 */
	protected void removeInterfaceActor(InterfaceActor iActor, Graph graph) {
		// We remove from both list, but only one will actually remove
		// something.
		graph.getInputPorts().remove(iActor.getGraphPort());
		graph.getOutputPorts().remove(iActor.getGraphPort());
		return;
	}

	/**
	 * Remove the {@link Port} corresponding to an Interface {@link Parameter}
	 * removed from the {@link Graph}.
	 * 
	 * @param param
	 *            the Interface {@link Parameter} removed from the {@link Graph}
	 * @param graph
	 *            the observed {@link Graph}
	 */
	protected void removeParamInterfaceActor(Parameter param, Graph graph) {
		graph.getConfigInputPorts().remove(param.getGraphPort());
	}
}
