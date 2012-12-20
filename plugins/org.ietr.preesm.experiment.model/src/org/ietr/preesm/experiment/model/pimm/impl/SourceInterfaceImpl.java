/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.ecore.EClass;

import org.ietr.preesm.experiment.model.pimm.OutputPort;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.SourceInterface;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Source Interface</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class SourceInterfaceImpl extends InterfaceActorImpl implements
		SourceInterface {

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 */
	protected SourceInterfaceImpl() {
		super();
		kind = KIND;

		// Add the unique output port of the Source Interface
		// The port intentionally has no name 
		OutputPort port = PiMMFactory.eINSTANCE.createOutputPort();
		port.setName(null);
		this.getOutputPorts().add(port);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.SOURCE_INTERFACE;
	}

} // SourceInterfaceImpl
