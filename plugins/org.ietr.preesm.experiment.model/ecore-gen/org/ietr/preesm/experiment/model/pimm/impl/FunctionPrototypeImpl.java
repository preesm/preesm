/*******************************************************************************
 * Copyright or © or Copr. 2014 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
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

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Function Prototype</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.FunctionPrototypeImpl#getName <em>Name</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.FunctionPrototypeImpl#getParameters <em>Parameters</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class FunctionPrototypeImpl extends EObjectImpl implements FunctionPrototype {
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
  protected String name = FunctionPrototypeImpl.NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getParameters() <em>Parameters</em>}' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getParameters()
   * @generated
   * @ordered
   */
  protected EList<FunctionParameter> parameters;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected FunctionPrototypeImpl() {
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
    return PiMMPackage.Literals.FUNCTION_PROTOTYPE;
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
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.FUNCTION_PROTOTYPE__NAME, oldName, this.name));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the parameters
   * @generated
   */
  @Override
  public EList<FunctionParameter> getParameters() {
    if (this.parameters == null) {
      this.parameters = new EObjectResolvingEList<>(FunctionParameter.class, this, PiMMPackage.FUNCTION_PROTOTYPE__PARAMETERS);
    }
    return this.parameters;
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
      case PiMMPackage.FUNCTION_PROTOTYPE__NAME:
        return getName();
      case PiMMPackage.FUNCTION_PROTOTYPE__PARAMETERS:
        return getParameters();
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
      case PiMMPackage.FUNCTION_PROTOTYPE__NAME:
        setName((String) newValue);
        return;
      case PiMMPackage.FUNCTION_PROTOTYPE__PARAMETERS:
        getParameters().clear();
        getParameters().addAll((Collection<? extends FunctionParameter>) newValue);
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
      case PiMMPackage.FUNCTION_PROTOTYPE__NAME:
        setName(FunctionPrototypeImpl.NAME_EDEFAULT);
        return;
      case PiMMPackage.FUNCTION_PROTOTYPE__PARAMETERS:
        getParameters().clear();
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
      case PiMMPackage.FUNCTION_PROTOTYPE__NAME:
        return FunctionPrototypeImpl.NAME_EDEFAULT == null ? this.name != null : !FunctionPrototypeImpl.NAME_EDEFAULT.equals(this.name);
      case PiMMPackage.FUNCTION_PROTOTYPE__PARAMETERS:
        return (this.parameters != null) && !this.parameters.isEmpty();
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
    v.visitFunctionPrototype(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ietr.preesm.experiment.model.pimm.FunctionPrototype#format()
   */
  @Override
  public String format() {
    String result = getName() + "(";
    boolean first = true;
    for (final FunctionParameter p : getParameters()) {
      if (first) {
        first = false;
      } else {
        result += ", ";
      }
      result += p.getType();
      result += (!p.isIsConfigurationParameter()) ? " * " : "";
      result += " " + p.getName();
    }
    result += ")";
    return result;
  }

} // FunctionPrototypeImpl
