/**
 * <copyright>
 * </copyright>
 *

 */
package org.ietr.preesm.editor.iDLLanguage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.ietr.preesm.editor.iDLLanguage.IDLLanguageFactory
 * @model kind="package"
 * @generated
 */
public interface IDLLanguagePackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "iDLLanguage";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.ietr.org/preesm/editor/IDLLanguage";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "iDLLanguage";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  IDLLanguagePackage eINSTANCE = org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl.init();

  /**
   * The meta object id for the '{@link org.ietr.preesm.editor.iDLLanguage.impl.IDLImpl <em>IDL</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLImpl
   * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getIDL()
   * @generated
   */
  int IDL = 0;

  /**
   * The feature id for the '<em><b>Elements</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int IDL__ELEMENTS = 0;

  /**
   * The number of structural features of the '<em>IDL</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int IDL_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.ietr.preesm.editor.iDLLanguage.impl.ModuleImpl <em>Module</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.editor.iDLLanguage.impl.ModuleImpl
   * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getModule()
   * @generated
   */
  int MODULE = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE__NAME = 0;

  /**
   * The feature id for the '<em><b>Types</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE__TYPES = 1;

  /**
   * The feature id for the '<em><b>Interfaces</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE__INTERFACES = 2;

  /**
   * The number of structural features of the '<em>Module</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.ietr.preesm.editor.iDLLanguage.impl.DataTypeImpl <em>Data Type</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.editor.iDLLanguage.impl.DataTypeImpl
   * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getDataType()
   * @generated
   */
  int DATA_TYPE = 2;

  /**
   * The feature id for the '<em><b>Btype</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_TYPE__BTYPE = 0;

  /**
   * The feature id for the '<em><b>Ctype</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_TYPE__CTYPE = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_TYPE__NAME = 2;

  /**
   * The number of structural features of the '<em>Data Type</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_TYPE_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.ietr.preesm.editor.iDLLanguage.impl.InterfaceImpl <em>Interface</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.editor.iDLLanguage.impl.InterfaceImpl
   * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getInterface()
   * @generated
   */
  int INTERFACE = 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTERFACE__NAME = 0;

  /**
   * The feature id for the '<em><b>Function</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTERFACE__FUNCTION = 1;

  /**
   * The number of structural features of the '<em>Interface</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTERFACE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.ietr.preesm.editor.iDLLanguage.impl.FunctionImpl <em>Function</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.editor.iDLLanguage.impl.FunctionImpl
   * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getFunction()
   * @generated
   */
  int FUNCTION = 4;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION__NAME = 0;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION__PARAMETERS = 1;

  /**
   * The number of structural features of the '<em>Function</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.ietr.preesm.editor.iDLLanguage.impl.ParameterImpl <em>Parameter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.editor.iDLLanguage.impl.ParameterImpl
   * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getParameter()
   * @generated
   */
  int PARAMETER = 5;

  /**
   * The feature id for the '<em><b>Direction</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETER__DIRECTION = 0;

  /**
   * The feature id for the '<em><b>Type</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETER__TYPE = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETER__NAME = 2;

  /**
   * The number of structural features of the '<em>Parameter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETER_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.ietr.preesm.editor.iDLLanguage.impl.TypeStarImpl <em>Type Star</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.editor.iDLLanguage.impl.TypeStarImpl
   * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getTypeStar()
   * @generated
   */
  int TYPE_STAR = 6;

  /**
   * The feature id for the '<em><b>Btype</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TYPE_STAR__BTYPE = 0;

  /**
   * The feature id for the '<em><b>Ctype</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TYPE_STAR__CTYPE = 1;

  /**
   * The number of structural features of the '<em>Type Star</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TYPE_STAR_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.ietr.preesm.editor.iDLLanguage.BaseType <em>Base Type</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.editor.iDLLanguage.BaseType
   * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getBaseType()
   * @generated
   */
  int BASE_TYPE = 7;

  /**
   * The meta object id for the '{@link org.ietr.preesm.editor.iDLLanguage.InterfaceName <em>Interface Name</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.editor.iDLLanguage.InterfaceName
   * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getInterfaceName()
   * @generated
   */
  int INTERFACE_NAME = 8;

  /**
   * The meta object id for the '{@link org.ietr.preesm.editor.iDLLanguage.Direction <em>Direction</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.editor.iDLLanguage.Direction
   * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getDirection()
   * @generated
   */
  int DIRECTION = 9;


  /**
   * Returns the meta object for class '{@link org.ietr.preesm.editor.iDLLanguage.IDL <em>IDL</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>IDL</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.IDL
   * @generated
   */
  EClass getIDL();

  /**
   * Returns the meta object for the containment reference list '{@link org.ietr.preesm.editor.iDLLanguage.IDL#getElements <em>Elements</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Elements</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.IDL#getElements()
   * @see #getIDL()
   * @generated
   */
  EReference getIDL_Elements();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.editor.iDLLanguage.Module <em>Module</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Module</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.Module
   * @generated
   */
  EClass getModule();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.editor.iDLLanguage.Module#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.Module#getName()
   * @see #getModule()
   * @generated
   */
  EAttribute getModule_Name();

  /**
   * Returns the meta object for the containment reference list '{@link org.ietr.preesm.editor.iDLLanguage.Module#getTypes <em>Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Types</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.Module#getTypes()
   * @see #getModule()
   * @generated
   */
  EReference getModule_Types();

  /**
   * Returns the meta object for the containment reference list '{@link org.ietr.preesm.editor.iDLLanguage.Module#getInterfaces <em>Interfaces</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Interfaces</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.Module#getInterfaces()
   * @see #getModule()
   * @generated
   */
  EReference getModule_Interfaces();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.editor.iDLLanguage.DataType <em>Data Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Data Type</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.DataType
   * @generated
   */
  EClass getDataType();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.editor.iDLLanguage.DataType#getBtype <em>Btype</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Btype</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.DataType#getBtype()
   * @see #getDataType()
   * @generated
   */
  EAttribute getDataType_Btype();

  /**
   * Returns the meta object for the reference '{@link org.ietr.preesm.editor.iDLLanguage.DataType#getCtype <em>Ctype</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Ctype</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.DataType#getCtype()
   * @see #getDataType()
   * @generated
   */
  EReference getDataType_Ctype();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.editor.iDLLanguage.DataType#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.DataType#getName()
   * @see #getDataType()
   * @generated
   */
  EAttribute getDataType_Name();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.editor.iDLLanguage.Interface <em>Interface</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Interface</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.Interface
   * @generated
   */
  EClass getInterface();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.editor.iDLLanguage.Interface#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.Interface#getName()
   * @see #getInterface()
   * @generated
   */
  EAttribute getInterface_Name();

  /**
   * Returns the meta object for the containment reference '{@link org.ietr.preesm.editor.iDLLanguage.Interface#getFunction <em>Function</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Function</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.Interface#getFunction()
   * @see #getInterface()
   * @generated
   */
  EReference getInterface_Function();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.editor.iDLLanguage.Function <em>Function</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Function</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.Function
   * @generated
   */
  EClass getFunction();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.editor.iDLLanguage.Function#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.Function#getName()
   * @see #getFunction()
   * @generated
   */
  EAttribute getFunction_Name();

  /**
   * Returns the meta object for the containment reference list '{@link org.ietr.preesm.editor.iDLLanguage.Function#getParameters <em>Parameters</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Parameters</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.Function#getParameters()
   * @see #getFunction()
   * @generated
   */
  EReference getFunction_Parameters();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.editor.iDLLanguage.Parameter <em>Parameter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Parameter</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.Parameter
   * @generated
   */
  EClass getParameter();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.editor.iDLLanguage.Parameter#getDirection <em>Direction</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Direction</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.Parameter#getDirection()
   * @see #getParameter()
   * @generated
   */
  EAttribute getParameter_Direction();

  /**
   * Returns the meta object for the containment reference '{@link org.ietr.preesm.editor.iDLLanguage.Parameter#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Type</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.Parameter#getType()
   * @see #getParameter()
   * @generated
   */
  EReference getParameter_Type();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.editor.iDLLanguage.Parameter#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.Parameter#getName()
   * @see #getParameter()
   * @generated
   */
  EAttribute getParameter_Name();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.editor.iDLLanguage.TypeStar <em>Type Star</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Type Star</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.TypeStar
   * @generated
   */
  EClass getTypeStar();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.editor.iDLLanguage.TypeStar#getBtype <em>Btype</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Btype</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.TypeStar#getBtype()
   * @see #getTypeStar()
   * @generated
   */
  EAttribute getTypeStar_Btype();

  /**
   * Returns the meta object for the reference '{@link org.ietr.preesm.editor.iDLLanguage.TypeStar#getCtype <em>Ctype</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Ctype</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.TypeStar#getCtype()
   * @see #getTypeStar()
   * @generated
   */
  EReference getTypeStar_Ctype();

  /**
   * Returns the meta object for enum '{@link org.ietr.preesm.editor.iDLLanguage.BaseType <em>Base Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Base Type</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.BaseType
   * @generated
   */
  EEnum getBaseType();

  /**
   * Returns the meta object for enum '{@link org.ietr.preesm.editor.iDLLanguage.InterfaceName <em>Interface Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Interface Name</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.InterfaceName
   * @generated
   */
  EEnum getInterfaceName();

  /**
   * Returns the meta object for enum '{@link org.ietr.preesm.editor.iDLLanguage.Direction <em>Direction</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Direction</em>'.
   * @see org.ietr.preesm.editor.iDLLanguage.Direction
   * @generated
   */
  EEnum getDirection();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  IDLLanguageFactory getIDLLanguageFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.ietr.preesm.editor.iDLLanguage.impl.IDLImpl <em>IDL</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLImpl
     * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getIDL()
     * @generated
     */
    EClass IDL = eINSTANCE.getIDL();

    /**
     * The meta object literal for the '<em><b>Elements</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference IDL__ELEMENTS = eINSTANCE.getIDL_Elements();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.editor.iDLLanguage.impl.ModuleImpl <em>Module</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.editor.iDLLanguage.impl.ModuleImpl
     * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getModule()
     * @generated
     */
    EClass MODULE = eINSTANCE.getModule();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODULE__NAME = eINSTANCE.getModule_Name();

    /**
     * The meta object literal for the '<em><b>Types</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODULE__TYPES = eINSTANCE.getModule_Types();

    /**
     * The meta object literal for the '<em><b>Interfaces</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODULE__INTERFACES = eINSTANCE.getModule_Interfaces();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.editor.iDLLanguage.impl.DataTypeImpl <em>Data Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.editor.iDLLanguage.impl.DataTypeImpl
     * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getDataType()
     * @generated
     */
    EClass DATA_TYPE = eINSTANCE.getDataType();

    /**
     * The meta object literal for the '<em><b>Btype</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DATA_TYPE__BTYPE = eINSTANCE.getDataType_Btype();

    /**
     * The meta object literal for the '<em><b>Ctype</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DATA_TYPE__CTYPE = eINSTANCE.getDataType_Ctype();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DATA_TYPE__NAME = eINSTANCE.getDataType_Name();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.editor.iDLLanguage.impl.InterfaceImpl <em>Interface</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.editor.iDLLanguage.impl.InterfaceImpl
     * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getInterface()
     * @generated
     */
    EClass INTERFACE = eINSTANCE.getInterface();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INTERFACE__NAME = eINSTANCE.getInterface_Name();

    /**
     * The meta object literal for the '<em><b>Function</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference INTERFACE__FUNCTION = eINSTANCE.getInterface_Function();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.editor.iDLLanguage.impl.FunctionImpl <em>Function</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.editor.iDLLanguage.impl.FunctionImpl
     * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getFunction()
     * @generated
     */
    EClass FUNCTION = eINSTANCE.getFunction();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute FUNCTION__NAME = eINSTANCE.getFunction_Name();

    /**
     * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference FUNCTION__PARAMETERS = eINSTANCE.getFunction_Parameters();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.editor.iDLLanguage.impl.ParameterImpl <em>Parameter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.editor.iDLLanguage.impl.ParameterImpl
     * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getParameter()
     * @generated
     */
    EClass PARAMETER = eINSTANCE.getParameter();

    /**
     * The meta object literal for the '<em><b>Direction</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PARAMETER__DIRECTION = eINSTANCE.getParameter_Direction();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PARAMETER__TYPE = eINSTANCE.getParameter_Type();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PARAMETER__NAME = eINSTANCE.getParameter_Name();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.editor.iDLLanguage.impl.TypeStarImpl <em>Type Star</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.editor.iDLLanguage.impl.TypeStarImpl
     * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getTypeStar()
     * @generated
     */
    EClass TYPE_STAR = eINSTANCE.getTypeStar();

    /**
     * The meta object literal for the '<em><b>Btype</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TYPE_STAR__BTYPE = eINSTANCE.getTypeStar_Btype();

    /**
     * The meta object literal for the '<em><b>Ctype</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TYPE_STAR__CTYPE = eINSTANCE.getTypeStar_Ctype();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.editor.iDLLanguage.BaseType <em>Base Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.editor.iDLLanguage.BaseType
     * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getBaseType()
     * @generated
     */
    EEnum BASE_TYPE = eINSTANCE.getBaseType();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.editor.iDLLanguage.InterfaceName <em>Interface Name</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.editor.iDLLanguage.InterfaceName
     * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getInterfaceName()
     * @generated
     */
    EEnum INTERFACE_NAME = eINSTANCE.getInterfaceName();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.editor.iDLLanguage.Direction <em>Direction</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.editor.iDLLanguage.Direction
     * @see org.ietr.preesm.editor.iDLLanguage.impl.IDLLanguagePackageImpl#getDirection()
     * @generated
     */
    EEnum DIRECTION = eINSTANCE.getDirection();

  }

} //IDLLanguagePackage
