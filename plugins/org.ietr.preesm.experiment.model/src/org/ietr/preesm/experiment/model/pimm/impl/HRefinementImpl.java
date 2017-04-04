/*******************************************************************************
 * Copyright or © or Copr. 2014 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2014)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Direction;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.HRefinement;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>HRefinement</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.HRefinementImpl#getLoopPrototype <em>Loop Prototype</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.HRefinementImpl#getInitPrototype <em>Init Prototype</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class HRefinementImpl extends RefinementImpl implements HRefinement {
	/**
	 * The cached value of the '{@link #getLoopPrototype() <em>Loop Prototype</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLoopPrototype()
	 * @generated
	 * @ordered
	 */
	protected FunctionPrototype loopPrototype;
	/**
	 * The cached value of the '{@link #getInitPrototype() <em>Init Prototype</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInitPrototype()
	 * @generated
	 * @ordered
	 */
	protected FunctionPrototype initPrototype;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HRefinementImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.HREFINEMENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public FunctionPrototype getLoopPrototype() {
		if (loopPrototype != null && loopPrototype.eIsProxy()) {
			InternalEObject oldLoopPrototype = (InternalEObject)loopPrototype;
			loopPrototype = (FunctionPrototype)eResolveProxy(oldLoopPrototype);
			if (loopPrototype != oldLoopPrototype) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.HREFINEMENT__LOOP_PROTOTYPE, oldLoopPrototype, loopPrototype));
			}
		}
		return loopPrototype;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FunctionPrototype basicGetLoopPrototype() {
		return loopPrototype;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLoopPrototype(FunctionPrototype newLoopPrototype) {
		FunctionPrototype oldLoopPrototype = loopPrototype;
		loopPrototype = newLoopPrototype;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.HREFINEMENT__LOOP_PROTOTYPE, oldLoopPrototype, loopPrototype));
	}
	
	@Override
	public AbstractActor getAbstractActor() {
		if (getLoopPrototype() != null) {
			// Create the actor returned by the function
			AbstractActor result = PiMMFactory.eINSTANCE.createActor();
			
			// Create all its ports corresponding to parameters of the 
			// prototype
			FunctionPrototype loopProto = getLoopPrototype();
			List<FunctionParameter> loopParameters = loopProto.getParameters();
			for(FunctionParameter param : loopParameters){
				if(!param.isIsConfigurationParameter()){
					// Data Port
					if(param.getDirection().equals(Direction.IN)){
						// Data Input
						DataInputPort port = PiMMFactory.eINSTANCE.createDataInputPort();
						port.setName(param.getName());
						result.getDataInputPorts().add(port);
					} else {
						// Data Output
						DataOutputPort port = PiMMFactory.eINSTANCE.createDataOutputPort();
						port.setName(param.getName());
						result.getDataOutputPorts().add(port);
					}
				} else {
					// Config Port
					if(param.getDirection().equals(Direction.IN)){
						// Config Input
						ConfigInputPort port = PiMMFactory.eINSTANCE.createConfigInputPort();
						port.setName(param.getName());
						result.getConfigInputPorts().add(port);
					} else {
						// Config Output
						ConfigOutputPort port = PiMMFactory.eINSTANCE.createConfigOutputPort();
						port.setName(param.getName());
						result.getConfigOutputPorts().add(port);
					}
				}
			}
			
			return result;
		} else {
			return null;
		}
		
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public FunctionPrototype getInitPrototype() {
		if (initPrototype != null && initPrototype.eIsProxy()) {
			InternalEObject oldInitPrototype = (InternalEObject)initPrototype;
			initPrototype = (FunctionPrototype)eResolveProxy(oldInitPrototype);
			if (initPrototype != oldInitPrototype) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.HREFINEMENT__INIT_PROTOTYPE, oldInitPrototype, initPrototype));
			}
		}
		return initPrototype;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FunctionPrototype basicGetInitPrototype() {
		return initPrototype;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setInitPrototype(FunctionPrototype newInitPrototype) {
		FunctionPrototype oldInitPrototype = initPrototype;
		initPrototype = newInitPrototype;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.HREFINEMENT__INIT_PROTOTYPE, oldInitPrototype, initPrototype));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case PiMMPackage.HREFINEMENT__LOOP_PROTOTYPE:
				if (resolve) return getLoopPrototype();
				return basicGetLoopPrototype();
			case PiMMPackage.HREFINEMENT__INIT_PROTOTYPE:
				if (resolve) return getInitPrototype();
				return basicGetInitPrototype();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case PiMMPackage.HREFINEMENT__LOOP_PROTOTYPE:
				setLoopPrototype((FunctionPrototype)newValue);
				return;
			case PiMMPackage.HREFINEMENT__INIT_PROTOTYPE:
				setInitPrototype((FunctionPrototype)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case PiMMPackage.HREFINEMENT__LOOP_PROTOTYPE:
				setLoopPrototype((FunctionPrototype)null);
				return;
			case PiMMPackage.HREFINEMENT__INIT_PROTOTYPE:
				setInitPrototype((FunctionPrototype)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case PiMMPackage.HREFINEMENT__LOOP_PROTOTYPE:
				return loopPrototype != null;
			case PiMMPackage.HREFINEMENT__INIT_PROTOTYPE:
				return initPrototype != null;
		}
		return super.eIsSet(featureID);
	}
	
	@Override
	public void accept(PiMMVisitor v) {
		v.visitHRefinement(this);
	}

} //HRefinementImpl
