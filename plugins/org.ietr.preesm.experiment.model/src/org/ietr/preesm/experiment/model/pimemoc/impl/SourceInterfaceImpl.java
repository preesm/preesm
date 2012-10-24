/**
 */
package org.ietr.preesm.experiment.model.pimemoc.impl;

import org.eclipse.emf.ecore.EClass;

import org.ietr.preesm.experiment.model.pimemoc.OutputPort;
import org.ietr.preesm.experiment.model.pimemoc.PIMeMoCFactory;
import org.ietr.preesm.experiment.model.pimemoc.PIMeMoCPackage;
import org.ietr.preesm.experiment.model.pimemoc.SourceInterface;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Source Interface</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class SourceInterfaceImpl extends InterfaceVertexImpl implements
		SourceInterface {
	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 */
	protected SourceInterfaceImpl() {
		super();
		kind = "src";

		// Add the unique output port of the Source Interface
		// The port intentionally has no name 
		OutputPort port = PIMeMoCFactory.eINSTANCE.createOutputPort();
		this.getOutputPorts().add(port);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PIMeMoCPackage.Literals.SOURCE_INTERFACE;
	}

} // SourceInterfaceImpl
