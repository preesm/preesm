/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.Optional;

import java.util.function.Function;

import org.eclipse.core.runtime.IPath;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.CHeaderRefinement;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;

import org.ietr.preesm.experiment.model.pimm.util.RefinementResolver;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>CHeader Refinement</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.CHeaderRefinementImpl#getFilePath <em>File Path</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.CHeaderRefinementImpl#getLoopPrototype <em>Loop Prototype</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.CHeaderRefinementImpl#getInitPrototype <em>Init Prototype</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CHeaderRefinementImpl extends MinimalEObjectImpl.Container implements CHeaderRefinement {
  /**
   * The default value of the '{@link #getFilePath() <em>File Path</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFilePath()
   * @generated
   * @ordered
   */
  protected static final IPath FILE_PATH_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getFilePath() <em>File Path</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFilePath()
   * @generated
   * @ordered
   */
  protected IPath filePath = FILE_PATH_EDEFAULT;

  /**
   * The cached value of the '{@link #getLoopPrototype() <em>Loop Prototype</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLoopPrototype()
   * @generated
   * @ordered
   */
  protected FunctionPrototype loopPrototype;

  /**
   * The cached value of the '{@link #getInitPrototype() <em>Init Prototype</em>}' containment reference.
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
  protected CHeaderRefinementImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.CHEADER_REFINEMENT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IPath getFilePath() {
    return filePath;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setFilePath(IPath newFilePath) {
    IPath oldFilePath = filePath;
    filePath = newFilePath;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.CHEADER_REFINEMENT__FILE_PATH, oldFilePath, filePath));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FunctionPrototype getLoopPrototype() {
    return loopPrototype;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetLoopPrototype(FunctionPrototype newLoopPrototype, NotificationChain msgs) {
    FunctionPrototype oldLoopPrototype = loopPrototype;
    loopPrototype = newLoopPrototype;
    if (eNotificationRequired()) {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.CHEADER_REFINEMENT__LOOP_PROTOTYPE, oldLoopPrototype, newLoopPrototype);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLoopPrototype(FunctionPrototype newLoopPrototype) {
    if (newLoopPrototype != loopPrototype) {
      NotificationChain msgs = null;
      if (loopPrototype != null)
        msgs = ((InternalEObject)loopPrototype).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - PiMMPackage.CHEADER_REFINEMENT__LOOP_PROTOTYPE, null, msgs);
      if (newLoopPrototype != null)
        msgs = ((InternalEObject)newLoopPrototype).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - PiMMPackage.CHEADER_REFINEMENT__LOOP_PROTOTYPE, null, msgs);
      msgs = basicSetLoopPrototype(newLoopPrototype, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.CHEADER_REFINEMENT__LOOP_PROTOTYPE, newLoopPrototype, newLoopPrototype));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FunctionPrototype getInitPrototype() {
    return initPrototype;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetInitPrototype(FunctionPrototype newInitPrototype, NotificationChain msgs) {
    FunctionPrototype oldInitPrototype = initPrototype;
    initPrototype = newInitPrototype;
    if (eNotificationRequired()) {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.CHEADER_REFINEMENT__INIT_PROTOTYPE, oldInitPrototype, newInitPrototype);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setInitPrototype(FunctionPrototype newInitPrototype) {
    if (newInitPrototype != initPrototype) {
      NotificationChain msgs = null;
      if (initPrototype != null)
        msgs = ((InternalEObject)initPrototype).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - PiMMPackage.CHEADER_REFINEMENT__INIT_PROTOTYPE, null, msgs);
      if (newInitPrototype != null)
        msgs = ((InternalEObject)newInitPrototype).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - PiMMPackage.CHEADER_REFINEMENT__INIT_PROTOTYPE, null, msgs);
      msgs = basicSetInitPrototype(newInitPrototype, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.CHEADER_REFINEMENT__INIT_PROTOTYPE, newInitPrototype, newInitPrototype));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isHierarchical() {
    return false;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AbstractActor getAbstractActor() {
    return RefinementResolver.resolveAbstractActor(this);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getFileName() {
    final Function<IPath, String> _function = new Function<IPath, String>() {
      public String apply(final IPath it) {
        return it.lastSegment();
      }
    };
    return Optional.<IPath>ofNullable(this.getFilePath()).<String>map(_function).orElse(null);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.CHEADER_REFINEMENT__LOOP_PROTOTYPE:
        return basicSetLoopPrototype(null, msgs);
      case PiMMPackage.CHEADER_REFINEMENT__INIT_PROTOTYPE:
        return basicSetInitPrototype(null, msgs);
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
      case PiMMPackage.CHEADER_REFINEMENT__FILE_PATH:
        return getFilePath();
      case PiMMPackage.CHEADER_REFINEMENT__LOOP_PROTOTYPE:
        return getLoopPrototype();
      case PiMMPackage.CHEADER_REFINEMENT__INIT_PROTOTYPE:
        return getInitPrototype();
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
      case PiMMPackage.CHEADER_REFINEMENT__FILE_PATH:
        setFilePath((IPath)newValue);
        return;
      case PiMMPackage.CHEADER_REFINEMENT__LOOP_PROTOTYPE:
        setLoopPrototype((FunctionPrototype)newValue);
        return;
      case PiMMPackage.CHEADER_REFINEMENT__INIT_PROTOTYPE:
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
      case PiMMPackage.CHEADER_REFINEMENT__FILE_PATH:
        setFilePath(FILE_PATH_EDEFAULT);
        return;
      case PiMMPackage.CHEADER_REFINEMENT__LOOP_PROTOTYPE:
        setLoopPrototype((FunctionPrototype)null);
        return;
      case PiMMPackage.CHEADER_REFINEMENT__INIT_PROTOTYPE:
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
      case PiMMPackage.CHEADER_REFINEMENT__FILE_PATH:
        return FILE_PATH_EDEFAULT == null ? filePath != null : !FILE_PATH_EDEFAULT.equals(filePath);
      case PiMMPackage.CHEADER_REFINEMENT__LOOP_PROTOTYPE:
        return loopPrototype != null;
      case PiMMPackage.CHEADER_REFINEMENT__INIT_PROTOTYPE:
        return initPrototype != null;
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

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (filePath: ");
    result.append(filePath);
    result.append(')');
    return result.toString();
  }

} //CHeaderRefinementImpl
