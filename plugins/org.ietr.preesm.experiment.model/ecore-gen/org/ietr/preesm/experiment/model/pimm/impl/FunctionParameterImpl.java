/*******************************************************************************
 * Copyright or © or Copr. 2014 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.ietr.preesm.experiment.model.pimm.Direction;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitor;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Function Parameter</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.FunctionParameterImpl#getName <em>Name</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.FunctionParameterImpl#getDirection <em>Direction</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.FunctionParameterImpl#getType <em>Type</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.FunctionParameterImpl#isIsConfigurationParameter <em>Is Configuration Parameter</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FunctionParameterImpl extends EObjectImpl implements FunctionParameter {
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
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
  protected String name = FunctionParameterImpl.NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getDirection() <em>Direction</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getDirection()
   * @generated
   * @ordered
   */
  protected static final Direction DIRECTION_EDEFAULT = Direction.IN;

  /**
   * The cached value of the '{@link #getDirection() <em>Direction</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getDirection()
   * @generated
   * @ordered
   */
  protected Direction direction = FunctionParameterImpl.DIRECTION_EDEFAULT;

  /**
   * The default value of the '{@link #getType() <em>Type</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getType()
   * @generated
   * @ordered
   */
  protected static final String TYPE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getType() <em>Type</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getType()
   * @generated
   * @ordered
   */
  protected String type = FunctionParameterImpl.TYPE_EDEFAULT;

  /**
   * The default value of the '{@link #isIsConfigurationParameter() <em>Is Configuration Parameter</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #isIsConfigurationParameter()
   * @generated
   * @ordered
   */
  protected static final boolean IS_CONFIGURATION_PARAMETER_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isIsConfigurationParameter() <em>Is Configuration Parameter</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #isIsConfigurationParameter()
   * @generated
   * @ordered
   */
  protected boolean isConfigurationParameter = FunctionParameterImpl.IS_CONFIGURATION_PARAMETER_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected FunctionParameterImpl() {
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
    return PiMMPackage.Literals.FUNCTION_PARAMETER;
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
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.FUNCTION_PARAMETER__NAME, oldName, this.name));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the direction
   * @generated
   */
  @Override
  public Direction getDirection() {
    return this.direction;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newDirection
   *          the new direction
   * @generated
   */
  @Override
  public void setDirection(final Direction newDirection) {
    final Direction oldDirection = this.direction;
    this.direction = newDirection == null ? FunctionParameterImpl.DIRECTION_EDEFAULT : newDirection;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.FUNCTION_PARAMETER__DIRECTION, oldDirection, this.direction));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the type
   * @generated
   */
  @Override
  public String getType() {
    return this.type;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newType
   *          the new type
   * @generated
   */
  @Override
  public void setType(final String newType) {
    final String oldType = this.type;
    this.type = newType;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.FUNCTION_PARAMETER__TYPE, oldType, this.type));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return true, if is checks if is configuration parameter
   * @generated
   */
  @Override
  public boolean isIsConfigurationParameter() {
    return this.isConfigurationParameter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newIsConfigurationParameter
   *          the new checks if is configuration parameter
   * @generated
   */
  @Override
  public void setIsConfigurationParameter(final boolean newIsConfigurationParameter) {
    final boolean oldIsConfigurationParameter = this.isConfigurationParameter;
    this.isConfigurationParameter = newIsConfigurationParameter;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.FUNCTION_PARAMETER__IS_CONFIGURATION_PARAMETER, oldIsConfigurationParameter,
          this.isConfigurationParameter));
    }
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
      case PiMMPackage.FUNCTION_PARAMETER__NAME:
        return getName();
      case PiMMPackage.FUNCTION_PARAMETER__DIRECTION:
        return getDirection();
      case PiMMPackage.FUNCTION_PARAMETER__TYPE:
        return getType();
      case PiMMPackage.FUNCTION_PARAMETER__IS_CONFIGURATION_PARAMETER:
        return isIsConfigurationParameter();
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
  @Override
  public void eSet(final int featureID, final Object newValue) {
    switch (featureID) {
      case PiMMPackage.FUNCTION_PARAMETER__NAME:
        setName((String) newValue);
        return;
      case PiMMPackage.FUNCTION_PARAMETER__DIRECTION:
        setDirection((Direction) newValue);
        return;
      case PiMMPackage.FUNCTION_PARAMETER__TYPE:
        setType((String) newValue);
        return;
      case PiMMPackage.FUNCTION_PARAMETER__IS_CONFIGURATION_PARAMETER:
        setIsConfigurationParameter((Boolean) newValue);
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
      case PiMMPackage.FUNCTION_PARAMETER__NAME:
        setName(FunctionParameterImpl.NAME_EDEFAULT);
        return;
      case PiMMPackage.FUNCTION_PARAMETER__DIRECTION:
        setDirection(FunctionParameterImpl.DIRECTION_EDEFAULT);
        return;
      case PiMMPackage.FUNCTION_PARAMETER__TYPE:
        setType(FunctionParameterImpl.TYPE_EDEFAULT);
        return;
      case PiMMPackage.FUNCTION_PARAMETER__IS_CONFIGURATION_PARAMETER:
        setIsConfigurationParameter(FunctionParameterImpl.IS_CONFIGURATION_PARAMETER_EDEFAULT);
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
      case PiMMPackage.FUNCTION_PARAMETER__NAME:
        return FunctionParameterImpl.NAME_EDEFAULT == null ? this.name != null : !FunctionParameterImpl.NAME_EDEFAULT.equals(this.name);
      case PiMMPackage.FUNCTION_PARAMETER__DIRECTION:
        return this.direction != FunctionParameterImpl.DIRECTION_EDEFAULT;
      case PiMMPackage.FUNCTION_PARAMETER__TYPE:
        return FunctionParameterImpl.TYPE_EDEFAULT == null ? this.type != null : !FunctionParameterImpl.TYPE_EDEFAULT.equals(this.type);
      case PiMMPackage.FUNCTION_PARAMETER__IS_CONFIGURATION_PARAMETER:
        return this.isConfigurationParameter != FunctionParameterImpl.IS_CONFIGURATION_PARAMETER_EDEFAULT;
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

    final StringBuffer result = new StringBuffer(super.toString());
    result.append(" (name: ");
    result.append(this.name);
    result.append(", direction: ");
    result.append(this.direction);
    result.append(", type: ");
    result.append(this.type);
    result.append(", isConfigurationParameter: ");
    result.append(this.isConfigurationParameter);
    result.append(')');
    return result.toString();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitable#accept(org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor)
   */
  @Override
  public void accept(final PiMMVisitor v) {
    v.visitFunctionParameter(this);
  }

} // FunctionParameterImpl
