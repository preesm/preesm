/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.ecore.EClass;
import org.ietr.preesm.experiment.model.pimm.InputPort;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.SinkInterface;

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
		InputPort port = PiMMFactory.eINSTANCE.createInputPort();
		port.setName(null);
		this.getInputPorts().add(port);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.SINK_INTERFACE;
	}

} // SinkInterfaceImpl
