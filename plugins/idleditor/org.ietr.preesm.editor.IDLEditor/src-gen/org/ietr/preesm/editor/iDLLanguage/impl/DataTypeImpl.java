/**
 * <copyright>
 * </copyright>
 *

 */
package org.ietr.preesm.editor.iDLLanguage.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.ietr.preesm.editor.iDLLanguage.BaseType;
import org.ietr.preesm.editor.iDLLanguage.DataType;
import org.ietr.preesm.editor.iDLLanguage.IDLLanguagePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Data Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.editor.iDLLanguage.impl.DataTypeImpl#getBtype <em>Btype</em>}</li>
 *   <li>{@link org.ietr.preesm.editor.iDLLanguage.impl.DataTypeImpl#getCtype <em>Ctype</em>}</li>
 *   <li>{@link org.ietr.preesm.editor.iDLLanguage.impl.DataTypeImpl#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DataTypeImpl extends MinimalEObjectImpl.Container implements DataType
{
  /**
   * The default value of the '{@link #getBtype() <em>Btype</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBtype()
   * @generated
   * @ordered
   */
  protected static final BaseType BTYPE_EDEFAULT = BaseType.INT;

  /**
   * The cached value of the '{@link #getBtype() <em>Btype</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBtype()
   * @generated
   * @ordered
   */
  protected BaseType btype = BTYPE_EDEFAULT;

  /**
   * The cached value of the '{@link #getCtype() <em>Ctype</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCtype()
   * @generated
   * @ordered
   */
  protected DataType ctype;

  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DataTypeImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return IDLLanguagePackage.Literals.DATA_TYPE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BaseType getBtype()
  {
    return btype;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setBtype(BaseType newBtype)
  {
    BaseType oldBtype = btype;
    btype = newBtype == null ? BTYPE_EDEFAULT : newBtype;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, IDLLanguagePackage.DATA_TYPE__BTYPE, oldBtype, btype));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DataType getCtype()
  {
    if (ctype != null && ctype.eIsProxy())
    {
      InternalEObject oldCtype = (InternalEObject)ctype;
      ctype = (DataType)eResolveProxy(oldCtype);
      if (ctype != oldCtype)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, IDLLanguagePackage.DATA_TYPE__CTYPE, oldCtype, ctype));
      }
    }
    return ctype;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DataType basicGetCtype()
  {
    return ctype;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setCtype(DataType newCtype)
  {
    DataType oldCtype = ctype;
    ctype = newCtype;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, IDLLanguagePackage.DATA_TYPE__CTYPE, oldCtype, ctype));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, IDLLanguagePackage.DATA_TYPE__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case IDLLanguagePackage.DATA_TYPE__BTYPE:
        return getBtype();
      case IDLLanguagePackage.DATA_TYPE__CTYPE:
        if (resolve) return getCtype();
        return basicGetCtype();
      case IDLLanguagePackage.DATA_TYPE__NAME:
        return getName();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case IDLLanguagePackage.DATA_TYPE__BTYPE:
        setBtype((BaseType)newValue);
        return;
      case IDLLanguagePackage.DATA_TYPE__CTYPE:
        setCtype((DataType)newValue);
        return;
      case IDLLanguagePackage.DATA_TYPE__NAME:
        setName((String)newValue);
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
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case IDLLanguagePackage.DATA_TYPE__BTYPE:
        setBtype(BTYPE_EDEFAULT);
        return;
      case IDLLanguagePackage.DATA_TYPE__CTYPE:
        setCtype((DataType)null);
        return;
      case IDLLanguagePackage.DATA_TYPE__NAME:
        setName(NAME_EDEFAULT);
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
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case IDLLanguagePackage.DATA_TYPE__BTYPE:
        return btype != BTYPE_EDEFAULT;
      case IDLLanguagePackage.DATA_TYPE__CTYPE:
        return ctype != null;
      case IDLLanguagePackage.DATA_TYPE__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (btype: ");
    result.append(btype);
    result.append(", name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} //DataTypeImpl
