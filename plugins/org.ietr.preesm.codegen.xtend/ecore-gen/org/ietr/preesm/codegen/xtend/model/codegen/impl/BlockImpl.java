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
package org.ietr.preesm.codegen.xtend.model.codegen.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.model.codegen.CodeElt;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.xtend.model.codegen.Variable;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Block</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BlockImpl#getCodeElts <em>Code Elts</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BlockImpl#getDeclarations <em>Declarations</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BlockImpl#getName <em>Name</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BlockImpl#getDefinitions <em>Definitions</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BlockImpl extends EObjectImpl implements Block {
  /**
   * The cached value of the '{@link #getCodeElts() <em>Code Elts</em>}' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getCodeElts()
   * @generated
   * @ordered
   */
  protected EList<CodeElt> codeElts;

  /**
   * The cached value of the '{@link #getDeclarations() <em>Declarations</em>} ' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getDeclarations()
   * @generated
   * @ordered
   */
  protected EList<Variable> declarations;

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
  protected String name = BlockImpl.NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getDefinitions() <em>Definitions</em>}' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getDefinitions()
   * @generated
   * @ordered
   */
  protected EList<Variable> definitions;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected BlockImpl() {
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
    return CodegenPackage.Literals.BLOCK;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the code elts
   * @generated
   */
  @Override
  public EList<CodeElt> getCodeElts() {
    if (this.codeElts == null) {
      this.codeElts = new EObjectContainmentEList<>(CodeElt.class, this, CodegenPackage.BLOCK__CODE_ELTS);
    }
    return this.codeElts;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the declarations
   * @generated
   */
  @Override
  public EList<Variable> getDeclarations() {
    if (this.declarations == null) {
      this.declarations = new EObjectWithInverseResolvingEList.ManyInverse<>(Variable.class, this, CodegenPackage.BLOCK__DECLARATIONS,
          CodegenPackage.VARIABLE__USERS);
    }
    return this.declarations;
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
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.BLOCK__NAME, oldName, this.name));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the definitions
   * @generated
   */
  @Override
  public EList<Variable> getDefinitions() {
    if (this.definitions == null) {
      this.definitions = new EObjectContainmentWithInverseEList<>(Variable.class, this, CodegenPackage.BLOCK__DEFINITIONS, CodegenPackage.VARIABLE__CREATOR);
    }
    return this.definitions;
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
  public NotificationChain eInverseAdd(final InternalEObject otherEnd, final int featureID, final NotificationChain msgs) {
    switch (featureID) {
      case CodegenPackage.BLOCK__DECLARATIONS:
        return ((InternalEList<InternalEObject>) (InternalEList<?>) getDeclarations()).basicAdd(otherEnd, msgs);
      case CodegenPackage.BLOCK__DEFINITIONS:
        return ((InternalEList<InternalEObject>) (InternalEList<?>) getDefinitions()).basicAdd(otherEnd, msgs);
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
  public NotificationChain eInverseRemove(final InternalEObject otherEnd, final int featureID, final NotificationChain msgs) {
    switch (featureID) {
      case CodegenPackage.BLOCK__CODE_ELTS:
        return ((InternalEList<?>) getCodeElts()).basicRemove(otherEnd, msgs);
      case CodegenPackage.BLOCK__DECLARATIONS:
        return ((InternalEList<?>) getDeclarations()).basicRemove(otherEnd, msgs);
      case CodegenPackage.BLOCK__DEFINITIONS:
        return ((InternalEList<?>) getDefinitions()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
      case CodegenPackage.BLOCK__CODE_ELTS:
        return getCodeElts();
      case CodegenPackage.BLOCK__DECLARATIONS:
        return getDeclarations();
      case CodegenPackage.BLOCK__NAME:
        return getName();
      case CodegenPackage.BLOCK__DEFINITIONS:
        return getDefinitions();
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
      case CodegenPackage.BLOCK__NAME:
        setName((String) newValue);
        return;
      case CodegenPackage.BLOCK__DEFINITIONS:
        getDefinitions().clear();
        getDefinitions().addAll((Collection<? extends Variable>) newValue);
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
      case CodegenPackage.BLOCK__NAME:
        setName(BlockImpl.NAME_EDEFAULT);
        return;
      case CodegenPackage.BLOCK__DEFINITIONS:
        getDefinitions().clear();
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
      case CodegenPackage.BLOCK__CODE_ELTS:
        return (this.codeElts != null) && !this.codeElts.isEmpty();
      case CodegenPackage.BLOCK__DECLARATIONS:
        return (this.declarations != null) && !this.declarations.isEmpty();
      case CodegenPackage.BLOCK__NAME:
        return BlockImpl.NAME_EDEFAULT == null ? this.name != null : !BlockImpl.NAME_EDEFAULT.equals(this.name);
      case CodegenPackage.BLOCK__DEFINITIONS:
        return (this.definitions != null) && !this.definitions.isEmpty();
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

} // BlockImpl
