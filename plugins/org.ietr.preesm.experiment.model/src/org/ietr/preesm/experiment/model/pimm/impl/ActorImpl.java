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

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Actor</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ActorImpl#getRefinement <em>Refinement</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ActorImpl#isConfigurationActor <em>Configuration Actor</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ActorImpl#getMemoryScriptPath <em>Memory Script Path</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ActorImpl extends AbstractActorImpl implements Actor {
	/**
	 * The cached value of the '{@link #getRefinement() <em>Refinement</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getRefinement()
	 * @generated
	 * @ordered
	 */
	protected Refinement refinement;

	/**
	 * The default value of the '{@link #isConfigurationActor() <em>Configuration Actor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isConfigurationActor()
	 * @generated
	 * @ordered
	 */
	protected static final boolean CONFIGURATION_ACTOR_EDEFAULT = false;

	/**
	 * The default value of the '{@link #getMemoryScriptPath() <em>Memory Script Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMemoryScriptPath()
	 * @generated
	 * @ordered
	 */
	protected static final IPath MEMORY_SCRIPT_PATH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMemoryScriptPath() <em>Memory Script Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMemoryScriptPath()
	 * @generated
	 * @ordered
	 */
	protected IPath memoryScriptPath = MEMORY_SCRIPT_PATH_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 */
	protected ActorImpl() {
		super();
		this.setRefinement(PiMMFactory.eINSTANCE.createRefinement());
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.ACTOR;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Refinement getRefinement() {
		return refinement;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetRefinement(Refinement newRefinement,
			NotificationChain msgs) {
		Refinement oldRefinement = refinement;
		refinement = newRefinement;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.ACTOR__REFINEMENT, oldRefinement, newRefinement);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setRefinement(Refinement newRefinement) {
		if (newRefinement != refinement) {
			NotificationChain msgs = null;
			if (refinement != null)
				msgs = ((InternalEObject)refinement).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - PiMMPackage.ACTOR__REFINEMENT, null, msgs);
			if (newRefinement != null)
				msgs = ((InternalEObject)newRefinement).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - PiMMPackage.ACTOR__REFINEMENT, null, msgs);
			msgs = basicSetRefinement(newRefinement, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.ACTOR__REFINEMENT, newRefinement, newRefinement));
	}

	/**
	 * <!-- begin-user-doc --> Check whether the {@link Actor} is a
	 * configuration {@link Actor}.<br>
	 * <br>
	 * An {@link Actor} is a configuration {@link Actor} if it sets a
	 * {@link Parameter} value through a {@link ConfigOutputPort}.
	 * 
	 * @return <code>true</code> if the {@link Actor} is a configuration
	 *         {@link Actor} <code>false</code> else.<!-- end-user-doc -->
	 * 
	 */
	public boolean isConfigurationActor() {
		boolean result = false;

		List<ConfigOutputPort> ports = this.getConfigOutputPorts();
		for (ConfigOutputPort port : ports) {
			// If the port has an outgoing dependency
			if (!port.getOutgoingDependencies().isEmpty()) {
				// As soon as there is one dependency, the actor is a
				// configuration actor
				Dependency dependency = port.getOutgoingDependencies().get(0);
				Parameterizable parameterizable = (Parameterizable) dependency
						.getGetter().eContainer();

				// Should always be the case
				if (parameterizable instanceof Parameter) {
					result = true;
				} else {
					throw new RuntimeException(
							"Actor configuration output ports can"
									+ " only set the value of a Parameter. "
									+ parameterizable.eClass()
									+ " cannot be set directly.");
				}
			}
		}

		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetConfigurationActor() {
		// TODO: implement this method to return whether the 'Configuration Actor' attribute is set
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IPath getMemoryScriptPath() {
		return memoryScriptPath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMemoryScriptPath(IPath newMemoryScriptPath) {
		IPath oldMemoryScriptPath = memoryScriptPath;
		memoryScriptPath = newMemoryScriptPath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.ACTOR__MEMORY_SCRIPT_PATH, oldMemoryScriptPath, memoryScriptPath));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PiMMPackage.ACTOR__REFINEMENT:
				return basicSetRefinement(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case PiMMPackage.ACTOR__REFINEMENT:
				return getRefinement();
			case PiMMPackage.ACTOR__CONFIGURATION_ACTOR:
				return isConfigurationActor();
			case PiMMPackage.ACTOR__MEMORY_SCRIPT_PATH:
				return getMemoryScriptPath();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case PiMMPackage.ACTOR__REFINEMENT:
				setRefinement((Refinement)newValue);
				return;
			case PiMMPackage.ACTOR__MEMORY_SCRIPT_PATH:
				setMemoryScriptPath((IPath)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case PiMMPackage.ACTOR__REFINEMENT:
				setRefinement((Refinement)null);
				return;
			case PiMMPackage.ACTOR__MEMORY_SCRIPT_PATH:
				setMemoryScriptPath(MEMORY_SCRIPT_PATH_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case PiMMPackage.ACTOR__REFINEMENT:
				return refinement != null;
			case PiMMPackage.ACTOR__CONFIGURATION_ACTOR:
				return isSetConfigurationActor();
			case PiMMPackage.ACTOR__MEMORY_SCRIPT_PATH:
				return MEMORY_SCRIPT_PATH_EDEFAULT == null ? memoryScriptPath != null : !MEMORY_SCRIPT_PATH_EDEFAULT.equals(memoryScriptPath);
		}
		return super.eIsSet(featureID);
	}
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (memoryScriptPath: ");
		result.append(memoryScriptPath);
		result.append(')');
		return result.toString();
	}

	/**
	 * Test if the actor is a hierarchical one.
	 * @return true, if it is.
	 */
	@Override
	public boolean isHierarchical() {
		return !(this.getRefinement().getFilePath() == null || this.getRefinement().getFilePath().isEmpty()) ;
	}
	
	/**
	 * Get the graph from hierarchy.
	 * @return The {@link PiGraph}
	 */
	@Override
	public PiGraph getGraph() {
		AbstractActor subgraph = this.getRefinement().getAbstractActor();		
		if(subgraph instanceof PiGraph) 
			return (PiGraph) subgraph;
		else			
			return null;
	}

	@Override
	public void accept(PiMMVisitor v) {
		v.visitActor(this);
	}

} // ActorImpl
