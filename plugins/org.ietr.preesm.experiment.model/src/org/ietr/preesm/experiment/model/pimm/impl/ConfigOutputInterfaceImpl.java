/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.ecore.EClass;

import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.InputPort;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Config Output Interface</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class ConfigOutputInterfaceImpl extends InterfaceActorImpl implements ConfigOutputInterface {
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 */
	protected ConfigOutputInterfaceImpl() {
		super();
		
		kind = KIND;

		// Add the unique input port of the ConfigOutputInterface
		// The port intentionally has no name
		InputPort port = PiMMFactory.eINSTANCE.createInputPort();
		port.setName(null);
		this.getInputPorts().add(port);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.CONFIG_OUTPUT_INTERFACE;
	}

} //ConfigOutputInterfaceImpl
