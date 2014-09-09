/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
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
 ******************************************************************************/
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Abstract Vertex</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl#getDataInputPorts <em>Data Input Ports</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl#getDataOutputPorts <em>Data Output Ports</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl#getConfigOutputPorts <em>Config Output Ports</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class AbstractActorImpl extends AbstractVertexImpl implements
		AbstractActor {
	/**
	 * The cached value of the '{@link #getDataInputPorts() <em>Data Input Ports</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataInputPorts()
	 * @generated
	 * @ordered
	 */
	protected EList<DataInputPort> dataInputPorts;

	/**
	 * The cached value of the '{@link #getDataOutputPorts() <em>Data Output Ports</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDataOutputPorts()
	 * @generated
	 * @ordered
	 */
	protected EList<DataOutputPort> dataOutputPorts;

	/**
	 * The cached value of the '{@link #getConfigOutputPorts() <em>Config Output Ports</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConfigOutputPorts()
	 * @generated
	 * @ordered
	 */
	protected EList<ConfigOutputPort> configOutputPorts;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected AbstractActorImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS:
				return getDataInputPorts();
			case PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS:
				return getDataOutputPorts();
			case PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS:
				return getConfigOutputPorts();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS:
				return ((InternalEList<?>)getDataInputPorts()).basicRemove(otherEnd, msgs);
			case PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS:
				return ((InternalEList<?>)getDataOutputPorts()).basicRemove(otherEnd, msgs);
			case PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS:
				return ((InternalEList<?>)getConfigOutputPorts()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS:
				return dataInputPorts != null && !dataInputPorts.isEmpty();
			case PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS:
				return dataOutputPorts != null && !dataOutputPorts.isEmpty();
			case PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS:
				return configOutputPorts != null && !configOutputPorts.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS:
				getDataInputPorts().clear();
				getDataInputPorts().addAll((Collection<? extends DataInputPort>)newValue);
				return;
			case PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS:
				getDataOutputPorts().clear();
				getDataOutputPorts().addAll((Collection<? extends DataOutputPort>)newValue);
				return;
			case PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS:
				getConfigOutputPorts().clear();
				getConfigOutputPorts().addAll((Collection<? extends ConfigOutputPort>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.ABSTRACT_ACTOR;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<DataInputPort> getDataInputPorts() {
		if (dataInputPorts == null) {
			dataInputPorts = new EObjectContainmentEList<DataInputPort>(DataInputPort.class, this, PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS);
		}
		return dataInputPorts;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<DataOutputPort> getDataOutputPorts() {
		if (dataOutputPorts == null) {
			dataOutputPorts = new EObjectContainmentEList<DataOutputPort>(DataOutputPort.class, this, PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS);
		}
		return dataOutputPorts;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS:
				getDataInputPorts().clear();
				return;
			case PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS:
				getDataOutputPorts().clear();
				return;
			case PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS:
				getConfigOutputPorts().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ConfigOutputPort> getConfigOutputPorts() {
		if (configOutputPorts == null) {
			configOutputPorts = new EObjectContainmentEList<ConfigOutputPort>(ConfigOutputPort.class, this, PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS);
		}
		return configOutputPorts;
	}

	@Override
	public Port getPortNamed(String portName) {
		// If the super method return a port, return it
		Port p = super.getPortNamed(portName);
		if (p != null) {
			return p;
		}

		List<Port> ports = new ArrayList<Port>(getDataInputPorts());

		ports.addAll(getDataOutputPorts());
		ports.addAll(getConfigOutputPorts());

		for (Object port : ports) {
			String name = ((Port) port).getName();
			if (name == null && portName == null) {
				return (Port) port;
			}
			if (name != null && name.equals(portName)) {
				return (Port) port;
			}
		}
		return null;
	}

	public String getPath() {
		if (eContainer != null && eContainer instanceof AbstractActor) {
			return ((AbstractActor) eContainer).getPath() + "/" + getName();
		}
		return getName();
	}
	
	@Override
	public void accept(PiMMVisitor v) {
		v.visitAbstractActor(this);
	}

} // AbstractActoImpl
