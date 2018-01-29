/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.PiSDFRefinement;
import org.ietr.preesm.experiment.model.pimm.util.RefinementResolver;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Pi SDF Refinement</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.PiSDFRefinementImpl#getFilePath <em>File Path</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PiSDFRefinementImpl extends EObjectImpl implements PiSDFRefinement {
  /**
   * The default value of the '{@link #getFilePath() <em>File Path</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getFilePath()
   * @generated
   * @ordered
   */
  protected static final IPath FILE_PATH_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getFilePath() <em>File Path</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getFilePath()
   * @generated
   * @ordered
   */
  protected IPath filePath = PiSDFRefinementImpl.FILE_PATH_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected PiSDFRefinementImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.PI_SDF_REFINEMENT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public IPath getFilePath() {
    return this.filePath;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setFilePath(final IPath newFilePath) {
    final IPath oldFilePath = this.filePath;
    this.filePath = newFilePath;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.PI_SDF_REFINEMENT__FILE_PATH, oldFilePath, this.filePath));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean isHierarchical() {
    return (getFilePath() != null) && !getFilePath().isEmpty();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public AbstractActor getAbstractActor() {
    return RefinementResolver.resolveAbstractActor(this);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String getFileName() {
    return (getFilePath() == null) ? null : getFilePath().lastSegment();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
    switch (featureID) {
      case PiMMPackage.PI_SDF_REFINEMENT__FILE_PATH:
        return getFilePath();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void eSet(final int featureID, final Object newValue) {
    switch (featureID) {
      case PiMMPackage.PI_SDF_REFINEMENT__FILE_PATH:
        setFilePath((IPath) newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void eUnset(final int featureID) {
    switch (featureID) {
      case PiMMPackage.PI_SDF_REFINEMENT__FILE_PATH:
        setFilePath(PiSDFRefinementImpl.FILE_PATH_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean eIsSet(final int featureID) {
    switch (featureID) {
      case PiMMPackage.PI_SDF_REFINEMENT__FILE_PATH:
        return PiSDFRefinementImpl.FILE_PATH_EDEFAULT == null ? this.filePath != null : !PiSDFRefinementImpl.FILE_PATH_EDEFAULT.equals(this.filePath);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String toString() {
    if (eIsProxy()) {
      return super.toString();
    }

    final StringBuffer result = new StringBuffer(super.toString());
    result.append(" (filePath: ");
    result.append(this.filePath);
    result.append(')');
    return result.toString();
  }

} // PiSDFRefinementImpl
