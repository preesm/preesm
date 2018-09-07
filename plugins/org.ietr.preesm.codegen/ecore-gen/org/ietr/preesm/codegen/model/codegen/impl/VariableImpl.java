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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.ietr.preesm.codegen.model.codegen.Block;
import org.ietr.preesm.codegen.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.model.codegen.Variable;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Variable</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.impl.VariableImpl#getName <em>Name</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.impl.VariableImpl#getType <em>Type</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.impl.VariableImpl#getCreator <em>Creator</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.impl.VariableImpl#getUsers <em>Users</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class VariableImpl extends CommentableImpl implements Variable {
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
  protected String name = VariableImpl.NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getType() <em>Type</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
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
  protected String type = VariableImpl.TYPE_EDEFAULT;

  /**
   * The cached value of the '{@link #getUsers() <em>Users</em>}' reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @see #getUsers()
   * @generated
   * @ordered
   */
  protected EList<Block> users;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected VariableImpl() {
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
    return CodegenPackage.Literals.VARIABLE;
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
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.VARIABLE__NAME, oldName, this.name));
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
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.VARIABLE__TYPE, oldType, this.type));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the creator
   * @generated
   */
  @Override
  public Block getCreator() {
    if (eContainerFeatureID() != CodegenPackage.VARIABLE__CREATOR) {
      return null;
    }
    return (Block) eInternalContainer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newCreator
   *          the new creator
   * @param msgs
   *          the msgs
   * @return the notification chain
   * @generated
   */
  public NotificationChain basicSetCreator(final Block newCreator, NotificationChain msgs) {
    msgs = eBasicSetContainer((InternalEObject) newCreator, CodegenPackage.VARIABLE__CREATOR, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newCreator
   *          the new creator
   * @generated
   */
  @Override
  public void setCreator(final Block newCreator) {
    if ((newCreator != eInternalContainer())
        || ((eContainerFeatureID() != CodegenPackage.VARIABLE__CREATOR) && (newCreator != null))) {
      if (EcoreUtil.isAncestor(this, newCreator)) {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null) {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newCreator != null) {
        msgs = ((InternalEObject) newCreator).eInverseAdd(this, CodegenPackage.BLOCK__DEFINITIONS, Block.class, msgs);
      }
      msgs = basicSetCreator(newCreator, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.VARIABLE__CREATOR, newCreator, newCreator));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the users
   * @generated
   */
  @Override
  public EList<Block> getUsers() {
    if (this.users == null) {
      this.users = new EObjectWithInverseResolvingEList.ManyInverse<>(Block.class, this, CodegenPackage.VARIABLE__USERS,
          CodegenPackage.BLOCK__DECLARATIONS);
    }
    return this.users;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public void reaffectCreator(final Block creator) {
    setCreator(creator);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param otherEnd
   *          the other end
   * @param featureID
   *          the feature ID
   * @param msgs
   *          the msgs
   * @return the notification chain
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(final InternalEObject otherEnd, final int featureID, NotificationChain msgs) {
    switch (featureID) {
      case CodegenPackage.VARIABLE__CREATOR:
        if (eInternalContainer() != null) {
          msgs = eBasicRemoveFromContainer(msgs);
        }
        return basicSetCreator((Block) otherEnd, msgs);
      case CodegenPackage.VARIABLE__USERS:
        return ((InternalEList<InternalEObject>) (InternalEList<?>) getUsers()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param otherEnd
   *          the other end
   * @param featureID
   *          the feature ID
   * @param msgs
   *          the msgs
   * @return the notification chain
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(final InternalEObject otherEnd, final int featureID,
      final NotificationChain msgs) {
    switch (featureID) {
      case CodegenPackage.VARIABLE__CREATOR:
        return basicSetCreator(null, msgs);
      case CodegenPackage.VARIABLE__USERS:
        return ((InternalEList<?>) getUsers()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param msgs
   *          the msgs
   * @return the notification chain
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(final NotificationChain msgs) {
    switch (eContainerFeatureID()) {
      case CodegenPackage.VARIABLE__CREATOR:
        return eInternalContainer().eInverseRemove(this, CodegenPackage.BLOCK__DEFINITIONS, Block.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
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
      case CodegenPackage.VARIABLE__NAME:
        return getName();
      case CodegenPackage.VARIABLE__TYPE:
        return getType();
      case CodegenPackage.VARIABLE__CREATOR:
        return getCreator();
      case CodegenPackage.VARIABLE__USERS:
        return getUsers();
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
      case CodegenPackage.VARIABLE__NAME:
        setName((String) newValue);
        return;
      case CodegenPackage.VARIABLE__TYPE:
        setType((String) newValue);
        return;
      case CodegenPackage.VARIABLE__CREATOR:
        setCreator((Block) newValue);
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
      case CodegenPackage.VARIABLE__NAME:
        setName(VariableImpl.NAME_EDEFAULT);
        return;
      case CodegenPackage.VARIABLE__TYPE:
        setType(VariableImpl.TYPE_EDEFAULT);
        return;
      case CodegenPackage.VARIABLE__CREATOR:
        setCreator((Block) null);
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
      case CodegenPackage.VARIABLE__NAME:
        return VariableImpl.NAME_EDEFAULT == null ? this.name != null : !VariableImpl.NAME_EDEFAULT.equals(this.name);
      case CodegenPackage.VARIABLE__TYPE:
        return VariableImpl.TYPE_EDEFAULT == null ? this.type != null : !VariableImpl.TYPE_EDEFAULT.equals(this.type);
      case CodegenPackage.VARIABLE__CREATOR:
        return getCreator() != null;
      case CodegenPackage.VARIABLE__USERS:
        return (this.users != null) && !this.users.isEmpty();
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
    result.append(", type: ");
    result.append(this.type);
    result.append(')');
    return result.toString();
  }

} // VariableImpl
