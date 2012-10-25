/**
 */
package org.ietr.preesm.experiment.model.pimemoc.impl;

import org.eclipse.emf.ecore.EClass;
import org.ietr.preesm.experiment.model.pimemoc.InputPort;
import org.ietr.preesm.experiment.model.pimemoc.PIMeMoCFactory;
import org.ietr.preesm.experiment.model.pimemoc.PIMeMoCPackage;
import org.ietr.preesm.experiment.model.pimemoc.SinkInterface;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Sink Interface</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class SinkInterfaceImpl extends InterfaceVertexImpl implements
		SinkInterface {
	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 */
	protected SinkInterfaceImpl() {
		super();

		kind = "snk";

		// Add the unique input port of the Sink Interface
		// The port intentionally has no name
		InputPort port = PIMeMoCFactory.eINSTANCE.createInputPort();
		port.setName(null);
		this.getInputPorts().add(port);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PIMeMoCPackage.Literals.SINK_INTERFACE;
	}

} // SinkInterfaceImpl
