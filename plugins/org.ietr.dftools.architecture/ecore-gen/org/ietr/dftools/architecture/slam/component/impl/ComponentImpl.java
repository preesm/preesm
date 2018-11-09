/**
 */
package org.ietr.dftools.architecture.slam.component.impl;

import com.google.common.base.Objects;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.xtext.xbase.lib.Functions.Function1;

import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;

import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.architecture.slam.ParameterizedElement;
import org.ietr.dftools.architecture.slam.SlamPackage;

import org.ietr.dftools.architecture.slam.attributes.Parameter;

import org.ietr.dftools.architecture.slam.component.ComInterface;
import org.ietr.dftools.architecture.slam.component.Component;
import org.ietr.dftools.architecture.slam.component.ComponentPackage;

import org.ietr.dftools.architecture.slam.impl.VLNVedElementImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Component</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.dftools.architecture.slam.component.impl.ComponentImpl#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.component.impl.ComponentImpl#getInterfaces <em>Interfaces</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.component.impl.ComponentImpl#getInstances <em>Instances</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.component.impl.ComponentImpl#getRefinements <em>Refinements</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ComponentImpl extends VLNVedElementImpl implements Component {
	/**
	 * The cached value of the '{@link #getParameters() <em>Parameters</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameters()
	 * @generated
	 * @ordered
	 */
	protected EList<Parameter> parameters;

	/**
	 * The cached value of the '{@link #getInterfaces() <em>Interfaces</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInterfaces()
	 * @generated
	 * @ordered
	 */
	protected EList<ComInterface> interfaces;

	/**
	 * The cached value of the '{@link #getInstances() <em>Instances</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInstances()
	 * @generated
	 * @ordered
	 */
	protected EList<ComponentInstance> instances;

	/**
	 * The cached value of the '{@link #getRefinements() <em>Refinements</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRefinements()
	 * @generated
	 * @ordered
	 */
	protected EList<Design> refinements;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComponentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ComponentPackage.Literals.COMPONENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Parameter> getParameters() {
		if (parameters == null) {
			parameters = new EObjectContainmentEList<Parameter>(Parameter.class, this, ComponentPackage.COMPONENT__PARAMETERS);
		}
		return parameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ComInterface> getInterfaces() {
		if (interfaces == null) {
			interfaces = new EObjectContainmentWithInverseEList<ComInterface>(ComInterface.class, this, ComponentPackage.COMPONENT__INTERFACES, ComponentPackage.COM_INTERFACE__COMPONENT);
		}
		return interfaces;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ComponentInstance> getInstances() {
		if (instances == null) {
			instances = new EObjectWithInverseResolvingEList<ComponentInstance>(ComponentInstance.class, this, ComponentPackage.COMPONENT__INSTANCES, SlamPackage.COMPONENT_INSTANCE__COMPONENT);
		}
		return instances;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Design> getRefinements() {
		if (refinements == null) {
			refinements = new EObjectContainmentWithInverseEList<Design>(Design.class, this, ComponentPackage.COMPONENT__REFINEMENTS, SlamPackage.DESIGN__REFINED);
		}
		return refinements;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComInterface getInterface(final String name) {
		final Function1<ComInterface, Boolean> _function = new Function1<ComInterface, Boolean>() {
			public Boolean apply(final ComInterface it) {
				String _name = it.getName();
				Pair<ComInterface, String> _mappedTo = Pair.<ComInterface, String>of(it, _name);
				return Boolean.valueOf(Objects.equal(_mappedTo, name));
			}
		};
		return IterableExtensions.<ComInterface>findFirst(this.getInterfaces(), _function);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ComponentPackage.COMPONENT__INTERFACES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getInterfaces()).basicAdd(otherEnd, msgs);
			case ComponentPackage.COMPONENT__INSTANCES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getInstances()).basicAdd(otherEnd, msgs);
			case ComponentPackage.COMPONENT__REFINEMENTS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getRefinements()).basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ComponentPackage.COMPONENT__PARAMETERS:
				return ((InternalEList<?>)getParameters()).basicRemove(otherEnd, msgs);
			case ComponentPackage.COMPONENT__INTERFACES:
				return ((InternalEList<?>)getInterfaces()).basicRemove(otherEnd, msgs);
			case ComponentPackage.COMPONENT__INSTANCES:
				return ((InternalEList<?>)getInstances()).basicRemove(otherEnd, msgs);
			case ComponentPackage.COMPONENT__REFINEMENTS:
				return ((InternalEList<?>)getRefinements()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ComponentPackage.COMPONENT__PARAMETERS:
				return getParameters();
			case ComponentPackage.COMPONENT__INTERFACES:
				return getInterfaces();
			case ComponentPackage.COMPONENT__INSTANCES:
				return getInstances();
			case ComponentPackage.COMPONENT__REFINEMENTS:
				return getRefinements();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ComponentPackage.COMPONENT__PARAMETERS:
				getParameters().clear();
				getParameters().addAll((Collection<? extends Parameter>)newValue);
				return;
			case ComponentPackage.COMPONENT__INTERFACES:
				getInterfaces().clear();
				getInterfaces().addAll((Collection<? extends ComInterface>)newValue);
				return;
			case ComponentPackage.COMPONENT__INSTANCES:
				getInstances().clear();
				getInstances().addAll((Collection<? extends ComponentInstance>)newValue);
				return;
			case ComponentPackage.COMPONENT__REFINEMENTS:
				getRefinements().clear();
				getRefinements().addAll((Collection<? extends Design>)newValue);
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
			case ComponentPackage.COMPONENT__PARAMETERS:
				getParameters().clear();
				return;
			case ComponentPackage.COMPONENT__INTERFACES:
				getInterfaces().clear();
				return;
			case ComponentPackage.COMPONENT__INSTANCES:
				getInstances().clear();
				return;
			case ComponentPackage.COMPONENT__REFINEMENTS:
				getRefinements().clear();
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
			case ComponentPackage.COMPONENT__PARAMETERS:
				return parameters != null && !parameters.isEmpty();
			case ComponentPackage.COMPONENT__INTERFACES:
				return interfaces != null && !interfaces.isEmpty();
			case ComponentPackage.COMPONENT__INSTANCES:
				return instances != null && !instances.isEmpty();
			case ComponentPackage.COMPONENT__REFINEMENTS:
				return refinements != null && !refinements.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		if (baseClass == ParameterizedElement.class) {
			switch (derivedFeatureID) {
				case ComponentPackage.COMPONENT__PARAMETERS: return SlamPackage.PARAMETERIZED_ELEMENT__PARAMETERS;
				default: return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
		if (baseClass == ParameterizedElement.class) {
			switch (baseFeatureID) {
				case SlamPackage.PARAMETERIZED_ELEMENT__PARAMETERS: return ComponentPackage.COMPONENT__PARAMETERS;
				default: return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
	}

} //ComponentImpl
