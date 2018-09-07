/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
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
package org.ietr.preesm.codegen.model.codegen.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.ietr.preesm.codegen.model.codegen.Call;
import org.ietr.preesm.codegen.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.model.codegen.PortDirection;
import org.ietr.preesm.codegen.model.codegen.Variable;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Call</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.impl.CallImpl#getParameters <em>Parameters</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.impl.CallImpl#getName <em>Name</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.impl.CallImpl#getEReference0 <em>EReference0</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.impl.CallImpl#getParameterDirections <em>Parameter
 * Directions</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class CallImpl extends CommentableImpl implements Call {
  /**
   * The cached value of the '{@link #getParameters() <em>Parameters</em>}' reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @see #getParameters()
   * @generated
   * @ordered
   */
  protected EList<Variable> parameters;

  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = CallImpl.NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getEReference0() <em>EReference0</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @see #getEReference0()
   * @generated
   * @ordered
   */
  protected Call eReference0;

  /**
   * The cached value of the '{@link #getParameterDirections() <em>Parameter Directions</em>}' attribute list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getParameterDirections()
   * @generated
   * @ordered
   */
  protected EList<PortDirection> parameterDirections;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected CallImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the e class
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return CodegenPackage.Literals.CALL;
  }

  /**
   * <!-- begin-user-doc --><!-- end-user-doc -->.
   *
   * @return the parameters
   * @generated
   */
  @Override
  public EList<Variable> getParameters() {
    if (this.parameters == null) {
      this.parameters = new EObjectResolvingEList<>(Variable.class, this, CodegenPackage.CALL__PARAMETERS);
    }
    return this.parameters;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the name
   * @generated
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newName
   *          the new name
   * @generated
   */
  @Override
  public void setName(final String newName) {
    final String oldName = this.name;
    this.name = newName;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.CALL__NAME, oldName, this.name));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the e reference 0
   * @generated
   */
  @Override
  public Call getEReference0() {
    if ((this.eReference0 != null) && this.eReference0.eIsProxy()) {
      final InternalEObject oldEReference0 = (InternalEObject) this.eReference0;
      this.eReference0 = (Call) eResolveProxy(oldEReference0);
      if (this.eReference0 != oldEReference0) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodegenPackage.CALL__EREFERENCE0, oldEReference0,
              this.eReference0));
        }
      }
    }
    return this.eReference0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the call
   * @generated
   */
  public Call basicGetEReference0() {
    return this.eReference0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newEReference0
   *          the new e reference 0
   * @generated
   */
  @Override
  public void setEReference0(final Call newEReference0) {
    final Call oldEReference0 = this.eReference0;
    this.eReference0 = newEReference0;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.CALL__EREFERENCE0, oldEReference0,
          this.eReference0));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the parameter directions
   * @generated
   */
  @Override
  public EList<PortDirection> getParameterDirections() {
    if (this.parameterDirections == null) {
      this.parameterDirections = new EDataTypeEList<>(PortDirection.class, this,
          CodegenPackage.CALL__PARAMETER_DIRECTIONS);
    }
    return this.parameterDirections;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param variable
   *          the variable
   * @param direction
   *          the direction
   */
  @Override
  public void addParameter(final Variable variable, final PortDirection direction) {
    getParameters().add(variable);
    getParameterDirections().add(direction);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param variable
   *          the variable
   */
  @Override
  public void removeParameter(final Variable variable) {
    // Must remove the port direction
    // TODO: implement this method
    // Ensure that you remove @generated or mark it @generated NOT
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @param resolve
   *          the resolve
   * @param coreType
   *          the core type
   * @return the object
   * @generated
   */
  @Override
  public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
    switch (featureID) {
      case CodegenPackage.CALL__PARAMETERS:
        return getParameters();
      case CodegenPackage.CALL__NAME:
        return getName();
      case CodegenPackage.CALL__EREFERENCE0:
        if (resolve) {
          return getEReference0();
        }
        return basicGetEReference0();
      case CodegenPackage.CALL__PARAMETER_DIRECTIONS:
        return getParameterDirections();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @param newValue
   *          the new value
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(final int featureID, final Object newValue) {
    switch (featureID) {
      case CodegenPackage.CALL__NAME:
        setName((String) newValue);
        return;
      case CodegenPackage.CALL__EREFERENCE0:
        setEReference0((Call) newValue);
        return;
      case CodegenPackage.CALL__PARAMETER_DIRECTIONS:
        getParameterDirections().clear();
        getParameterDirections().addAll((Collection<? extends PortDirection>) newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @generated
   */
  @Override
  public void eUnset(final int featureID) {
    switch (featureID) {
      case CodegenPackage.CALL__NAME:
        setName(CallImpl.NAME_EDEFAULT);
        return;
      case CodegenPackage.CALL__EREFERENCE0:
        setEReference0((Call) null);
        return;
      case CodegenPackage.CALL__PARAMETER_DIRECTIONS:
        getParameterDirections().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @return true, if successful
   * @generated
   */
  @Override
  public boolean eIsSet(final int featureID) {
    switch (featureID) {
      case CodegenPackage.CALL__PARAMETERS:
        return (this.parameters != null) && !this.parameters.isEmpty();
      case CodegenPackage.CALL__NAME:
        return CallImpl.NAME_EDEFAULT == null ? this.name != null : !CallImpl.NAME_EDEFAULT.equals(this.name);
      case CodegenPackage.CALL__EREFERENCE0:
        return this.eReference0 != null;
      case CodegenPackage.CALL__PARAMETER_DIRECTIONS:
        return (this.parameterDirections != null) && !this.parameterDirections.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the string
   * @generated
   */
  @Override
  public String toString() {
    if (eIsProxy()) {
      return super.toString();
    }

    final StringBuilder result = new StringBuilder(super.toString());
    result.append(" (name: ");
    result.append(this.name);
    result.append(", parameterDirections: ");
    result.append(this.parameterDirections);
    result.append(')');
    return result.toString();
  }

} // CallImpl
