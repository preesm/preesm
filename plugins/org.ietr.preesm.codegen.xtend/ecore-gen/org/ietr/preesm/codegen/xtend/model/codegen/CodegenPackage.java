/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2015)
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
package org.ietr.preesm.codegen.xtend.model.codegen;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * 
 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory
 * @model kind="package"
 * @generated
 */
public interface CodegenPackage extends EPackage {
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNAME = "codegen";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_URI = "http://codegen/1.0";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_PREFIX = "codegen";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  CodegenPackage eINSTANCE = org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl.init();

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.CodeElt <em>Code Elt</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodeElt
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCodeElt()
   * @generated
   */
  int CODE_ELT = 1;

  /**
   * The number of structural features of the '<em>Code Elt</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CODE_ELT_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BlockImpl <em>Block</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.BlockImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getBlock()
   * @generated
   */
  int BLOCK = 0;

  /**
   * The feature id for the '<em><b>Code Elts</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BLOCK__CODE_ELTS = CodegenPackage.CODE_ELT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Declarations</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BLOCK__DECLARATIONS = CodegenPackage.CODE_ELT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BLOCK__NAME = CodegenPackage.CODE_ELT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Definitions</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BLOCK__DEFINITIONS = CodegenPackage.CODE_ELT_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Block</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BLOCK_FEATURE_COUNT = CodegenPackage.CODE_ELT_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CallImpl <em>Call</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CallImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCall()
   * @generated
   */
  int CALL = 2;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CALL__PARAMETERS = CodegenPackage.CODE_ELT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CALL__NAME = CodegenPackage.CODE_ELT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>EReference0</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CALL__EREFERENCE0 = CodegenPackage.CODE_ELT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Parameter Directions</b></em>' attribute list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CALL__PARAMETER_DIRECTIONS = CodegenPackage.CODE_ELT_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Call</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CALL_FEATURE_COUNT = CodegenPackage.CODE_ELT_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommentableImpl
   * <em>Commentable</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CommentableImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCommentable()
   * @generated
   */
  int COMMENTABLE = 16;

  /**
   * The feature id for the '<em><b>Comment</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMENTABLE__COMMENT = 0;

  /**
   * The number of structural features of the '<em>Commentable</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int COMMENTABLE_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.VariableImpl
   * <em>Variable</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.VariableImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getVariable()
   * @generated
   */
  int VARIABLE = 3;

  /**
   * The feature id for the '<em><b>Comment</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int VARIABLE__COMMENT = CodegenPackage.COMMENTABLE__COMMENT;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int VARIABLE__NAME = CodegenPackage.COMMENTABLE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int VARIABLE__TYPE = CodegenPackage.COMMENTABLE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Creator</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int VARIABLE__CREATOR = CodegenPackage.COMMENTABLE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Users</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int VARIABLE__USERS = CodegenPackage.COMMENTABLE_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Variable</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int VARIABLE_FEATURE_COUNT = CodegenPackage.COMMENTABLE_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferImpl <em>Buffer</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getBuffer()
   * @generated
   */
  int BUFFER = 4;

  /**
   * The feature id for the '<em><b>Comment</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER__COMMENT = CodegenPackage.VARIABLE__COMMENT;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER__NAME = CodegenPackage.VARIABLE__NAME;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER__TYPE = CodegenPackage.VARIABLE__TYPE;

  /**
   * The feature id for the '<em><b>Creator</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER__CREATOR = CodegenPackage.VARIABLE__CREATOR;

  /**
   * The feature id for the '<em><b>Users</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER__USERS = CodegenPackage.VARIABLE__USERS;

  /**
   * The feature id for the '<em><b>Size</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER__SIZE = CodegenPackage.VARIABLE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Childrens</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER__CHILDRENS = CodegenPackage.VARIABLE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Type Size</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER__TYPE_SIZE = CodegenPackage.VARIABLE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Merged Range</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER__MERGED_RANGE = CodegenPackage.VARIABLE_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Local</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER__LOCAL = CodegenPackage.VARIABLE_FEATURE_COUNT + 4;

  /**
   * The number of structural features of the '<em>Buffer</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER_FEATURE_COUNT = CodegenPackage.VARIABLE_FEATURE_COUNT + 5;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SubBufferImpl <em>Sub
   * Buffer</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.SubBufferImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getSubBuffer()
   * @generated
   */
  int SUB_BUFFER = 5;

  /**
   * The feature id for the '<em><b>Comment</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SUB_BUFFER__COMMENT = CodegenPackage.BUFFER__COMMENT;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SUB_BUFFER__NAME = CodegenPackage.BUFFER__NAME;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SUB_BUFFER__TYPE = CodegenPackage.BUFFER__TYPE;

  /**
   * The feature id for the '<em><b>Creator</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SUB_BUFFER__CREATOR = CodegenPackage.BUFFER__CREATOR;

  /**
   * The feature id for the '<em><b>Users</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SUB_BUFFER__USERS = CodegenPackage.BUFFER__USERS;

  /**
   * The feature id for the '<em><b>Size</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SUB_BUFFER__SIZE = CodegenPackage.BUFFER__SIZE;

  /**
   * The feature id for the '<em><b>Childrens</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SUB_BUFFER__CHILDRENS = CodegenPackage.BUFFER__CHILDRENS;

  /**
   * The feature id for the '<em><b>Type Size</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SUB_BUFFER__TYPE_SIZE = CodegenPackage.BUFFER__TYPE_SIZE;

  /**
   * The feature id for the '<em><b>Merged Range</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SUB_BUFFER__MERGED_RANGE = CodegenPackage.BUFFER__MERGED_RANGE;

  /**
   * The feature id for the '<em><b>Local</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SUB_BUFFER__LOCAL = CodegenPackage.BUFFER__LOCAL;

  /**
   * The feature id for the '<em><b>Container</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SUB_BUFFER__CONTAINER = CodegenPackage.BUFFER_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Offset</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SUB_BUFFER__OFFSET = CodegenPackage.BUFFER_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Sub Buffer</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SUB_BUFFER_FEATURE_COUNT = CodegenPackage.BUFFER_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.ConstantImpl
   * <em>Constant</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.ConstantImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getConstant()
   * @generated
   */
  int CONSTANT = 6;

  /**
   * The feature id for the '<em><b>Comment</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CONSTANT__COMMENT = CodegenPackage.VARIABLE__COMMENT;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CONSTANT__NAME = CodegenPackage.VARIABLE__NAME;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CONSTANT__TYPE = CodegenPackage.VARIABLE__TYPE;

  /**
   * The feature id for the '<em><b>Creator</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CONSTANT__CREATOR = CodegenPackage.VARIABLE__CREATOR;

  /**
   * The feature id for the '<em><b>Users</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CONSTANT__USERS = CodegenPackage.VARIABLE__USERS;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CONSTANT__VALUE = CodegenPackage.VARIABLE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Constant</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CONSTANT_FEATURE_COUNT = CodegenPackage.VARIABLE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.FunctionCallImpl <em>Function
   * Call</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.FunctionCallImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getFunctionCall()
   * @generated
   */
  int FUNCTION_CALL = 7;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FUNCTION_CALL__PARAMETERS = CodegenPackage.CALL__PARAMETERS;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FUNCTION_CALL__NAME = CodegenPackage.CALL__NAME;

  /**
   * The feature id for the '<em><b>EReference0</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FUNCTION_CALL__EREFERENCE0 = CodegenPackage.CALL__EREFERENCE0;

  /**
   * The feature id for the '<em><b>Parameter Directions</b></em>' attribute list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FUNCTION_CALL__PARAMETER_DIRECTIONS = CodegenPackage.CALL__PARAMETER_DIRECTIONS;

  /**
   * The feature id for the '<em><b>Actor Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FUNCTION_CALL__ACTOR_NAME = CodegenPackage.CALL_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Function Call</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int FUNCTION_CALL_FEATURE_COUNT = CodegenPackage.CALL_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl
   * <em>Communication</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCommunication()
   * @generated
   */
  int COMMUNICATION = 8;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION__PARAMETERS = CodegenPackage.CALL__PARAMETERS;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION__NAME = CodegenPackage.CALL__NAME;

  /**
   * The feature id for the '<em><b>EReference0</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION__EREFERENCE0 = CodegenPackage.CALL__EREFERENCE0;

  /**
   * The feature id for the '<em><b>Parameter Directions</b></em>' attribute list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION__PARAMETER_DIRECTIONS = CodegenPackage.CALL__PARAMETER_DIRECTIONS;

  /**
   * The feature id for the '<em><b>Direction</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION__DIRECTION = CodegenPackage.CALL_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Delimiter</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION__DELIMITER = CodegenPackage.CALL_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Data</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION__DATA = CodegenPackage.CALL_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Send Start</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION__SEND_START = CodegenPackage.CALL_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Send End</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION__SEND_END = CodegenPackage.CALL_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Receive Start</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION__RECEIVE_START = CodegenPackage.CALL_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Receive End</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION__RECEIVE_END = CodegenPackage.CALL_FEATURE_COUNT + 6;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION__ID = CodegenPackage.CALL_FEATURE_COUNT + 7;

  /**
   * The feature id for the '<em><b>Nodes</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION__NODES = CodegenPackage.CALL_FEATURE_COUNT + 8;

  /**
   * The feature id for the '<em><b>Receive Release</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION__RECEIVE_RELEASE = CodegenPackage.CALL_FEATURE_COUNT + 9;

  /**
   * The feature id for the '<em><b>Send Reserve</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION__SEND_RESERVE = CodegenPackage.CALL_FEATURE_COUNT + 10;

  /**
   * The feature id for the '<em><b>Redundant</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION__REDUNDANT = CodegenPackage.CALL_FEATURE_COUNT + 11;

  /**
   * The number of structural features of the '<em>Communication</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION_FEATURE_COUNT = CodegenPackage.CALL_FEATURE_COUNT + 12;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CoreBlockImpl <em>Core
   * Block</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CoreBlockImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCoreBlock()
   * @generated
   */
  int CORE_BLOCK = 9;

  /**
   * The feature id for the '<em><b>Code Elts</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CORE_BLOCK__CODE_ELTS = CodegenPackage.BLOCK__CODE_ELTS;

  /**
   * The feature id for the '<em><b>Declarations</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CORE_BLOCK__DECLARATIONS = CodegenPackage.BLOCK__DECLARATIONS;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CORE_BLOCK__NAME = CodegenPackage.BLOCK__NAME;

  /**
   * The feature id for the '<em><b>Definitions</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CORE_BLOCK__DEFINITIONS = CodegenPackage.BLOCK__DEFINITIONS;

  /**
   * The feature id for the '<em><b>Loop Block</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CORE_BLOCK__LOOP_BLOCK = CodegenPackage.BLOCK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Init Block</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CORE_BLOCK__INIT_BLOCK = CodegenPackage.BLOCK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Core Type</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CORE_BLOCK__CORE_TYPE = CodegenPackage.BLOCK_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Core ID</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CORE_BLOCK__CORE_ID = CodegenPackage.BLOCK_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Core Block</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CORE_BLOCK_FEATURE_COUNT = CodegenPackage.BLOCK_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.ActorBlockImpl <em>Actor
   * Block</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.ActorBlockImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getActorBlock()
   * @generated
   */
  int ACTOR_BLOCK = 10;

  /**
   * The feature id for the '<em><b>Code Elts</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACTOR_BLOCK__CODE_ELTS = CodegenPackage.BLOCK__CODE_ELTS;

  /**
   * The feature id for the '<em><b>Declarations</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACTOR_BLOCK__DECLARATIONS = CodegenPackage.BLOCK__DECLARATIONS;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACTOR_BLOCK__NAME = CodegenPackage.BLOCK__NAME;

  /**
   * The feature id for the '<em><b>Definitions</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACTOR_BLOCK__DEFINITIONS = CodegenPackage.BLOCK__DEFINITIONS;

  /**
   * The feature id for the '<em><b>Loop Block</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACTOR_BLOCK__LOOP_BLOCK = CodegenPackage.BLOCK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Init Block</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACTOR_BLOCK__INIT_BLOCK = CodegenPackage.BLOCK_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Actor Block</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int ACTOR_BLOCK_FEATURE_COUNT = CodegenPackage.BLOCK_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.LoopBlockImpl <em>Loop
   * Block</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.LoopBlockImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getLoopBlock()
   * @generated
   */
  int LOOP_BLOCK = 11;

  /**
   * The feature id for the '<em><b>Code Elts</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int LOOP_BLOCK__CODE_ELTS = CodegenPackage.BLOCK__CODE_ELTS;

  /**
   * The feature id for the '<em><b>Declarations</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int LOOP_BLOCK__DECLARATIONS = CodegenPackage.BLOCK__DECLARATIONS;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int LOOP_BLOCK__NAME = CodegenPackage.BLOCK__NAME;

  /**
   * The feature id for the '<em><b>Definitions</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int LOOP_BLOCK__DEFINITIONS = CodegenPackage.BLOCK__DEFINITIONS;

  /**
   * The number of structural features of the '<em>Loop Block</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int LOOP_BLOCK_FEATURE_COUNT = CodegenPackage.BLOCK_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.ActorCallImpl <em>Actor
   * Call</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.ActorCallImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getActorCall()
   * @generated
   */
  int ACTOR_CALL = 12;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACTOR_CALL__PARAMETERS = CodegenPackage.CALL__PARAMETERS;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACTOR_CALL__NAME = CodegenPackage.CALL__NAME;

  /**
   * The feature id for the '<em><b>EReference0</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACTOR_CALL__EREFERENCE0 = CodegenPackage.CALL__EREFERENCE0;

  /**
   * The feature id for the '<em><b>Parameter Directions</b></em>' attribute list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACTOR_CALL__PARAMETER_DIRECTIONS = CodegenPackage.CALL__PARAMETER_DIRECTIONS;

  /**
   * The number of structural features of the '<em>Actor Call</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACTOR_CALL_FEATURE_COUNT = CodegenPackage.CALL_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CallBlockImpl <em>Call
   * Block</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CallBlockImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCallBlock()
   * @generated
   */
  int CALL_BLOCK = 13;

  /**
   * The feature id for the '<em><b>Code Elts</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CALL_BLOCK__CODE_ELTS = CodegenPackage.BLOCK__CODE_ELTS;

  /**
   * The feature id for the '<em><b>Declarations</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CALL_BLOCK__DECLARATIONS = CodegenPackage.BLOCK__DECLARATIONS;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CALL_BLOCK__NAME = CodegenPackage.BLOCK__NAME;

  /**
   * The feature id for the '<em><b>Definitions</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CALL_BLOCK__DEFINITIONS = CodegenPackage.BLOCK__DEFINITIONS;

  /**
   * The number of structural features of the '<em>Call Block</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CALL_BLOCK_FEATURE_COUNT = CodegenPackage.BLOCK_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SpecialCallImpl <em>Special
   * Call</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.SpecialCallImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getSpecialCall()
   * @generated
   */
  int SPECIAL_CALL = 14;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SPECIAL_CALL__PARAMETERS = CodegenPackage.CALL__PARAMETERS;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SPECIAL_CALL__NAME = CodegenPackage.CALL__NAME;

  /**
   * The feature id for the '<em><b>EReference0</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SPECIAL_CALL__EREFERENCE0 = CodegenPackage.CALL__EREFERENCE0;

  /**
   * The feature id for the '<em><b>Parameter Directions</b></em>' attribute list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SPECIAL_CALL__PARAMETER_DIRECTIONS = CodegenPackage.CALL__PARAMETER_DIRECTIONS;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SPECIAL_CALL__TYPE = CodegenPackage.CALL_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Input Buffers</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int SPECIAL_CALL__INPUT_BUFFERS = CodegenPackage.CALL_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Output Buffers</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int SPECIAL_CALL__OUTPUT_BUFFERS = CodegenPackage.CALL_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Special Call</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int SPECIAL_CALL_FEATURE_COUNT = CodegenPackage.CALL_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.FifoCallImpl <em>Fifo
   * Call</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.FifoCallImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getFifoCall()
   * @generated
   */
  int FIFO_CALL = 15;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FIFO_CALL__PARAMETERS = CodegenPackage.CALL__PARAMETERS;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FIFO_CALL__NAME = CodegenPackage.CALL__NAME;

  /**
   * The feature id for the '<em><b>EReference0</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FIFO_CALL__EREFERENCE0 = CodegenPackage.CALL__EREFERENCE0;

  /**
   * The feature id for the '<em><b>Parameter Directions</b></em>' attribute list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FIFO_CALL__PARAMETER_DIRECTIONS = CodegenPackage.CALL__PARAMETER_DIRECTIONS;

  /**
   * The feature id for the '<em><b>Operation</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FIFO_CALL__OPERATION = CodegenPackage.CALL_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Fifo Head</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FIFO_CALL__FIFO_HEAD = CodegenPackage.CALL_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Fifo Tail</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FIFO_CALL__FIFO_TAIL = CodegenPackage.CALL_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Head Buffer</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FIFO_CALL__HEAD_BUFFER = CodegenPackage.CALL_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Body Buffer</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FIFO_CALL__BODY_BUFFER = CodegenPackage.CALL_FEATURE_COUNT + 4;

  /**
   * The number of structural features of the '<em>Fifo Call</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FIFO_CALL_FEATURE_COUNT = CodegenPackage.CALL_FEATURE_COUNT + 5;

  /**
   * The meta object id for the ' {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationNodeImpl
   * <em>Communication Node</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl. CommunicationNodeImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCommunicationNode()
   * @generated
   */
  int COMMUNICATION_NODE = 17;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION_NODE__NAME = 0;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION_NODE__TYPE = 1;

  /**
   * The number of structural features of the '<em>Communication Node</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMMUNICATION_NODE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SharedMemoryCommunicationImpl
   * <em>Shared Memory Communication</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.SharedMemoryCommunicationImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getSharedMemoryCommunication()
   * @generated
   */
  int SHARED_MEMORY_COMMUNICATION = 18;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SHARED_MEMORY_COMMUNICATION__PARAMETERS = CodegenPackage.COMMUNICATION__PARAMETERS;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SHARED_MEMORY_COMMUNICATION__NAME = CodegenPackage.COMMUNICATION__NAME;

  /**
   * The feature id for the '<em><b>EReference0</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SHARED_MEMORY_COMMUNICATION__EREFERENCE0 = CodegenPackage.COMMUNICATION__EREFERENCE0;

  /**
   * The feature id for the '<em><b>Parameter Directions</b></em>' attribute list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SHARED_MEMORY_COMMUNICATION__PARAMETER_DIRECTIONS = CodegenPackage.COMMUNICATION__PARAMETER_DIRECTIONS;

  /**
   * The feature id for the '<em><b>Direction</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SHARED_MEMORY_COMMUNICATION__DIRECTION = CodegenPackage.COMMUNICATION__DIRECTION;

  /**
   * The feature id for the '<em><b>Delimiter</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SHARED_MEMORY_COMMUNICATION__DELIMITER = CodegenPackage.COMMUNICATION__DELIMITER;

  /**
   * The feature id for the '<em><b>Data</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SHARED_MEMORY_COMMUNICATION__DATA = CodegenPackage.COMMUNICATION__DATA;

  /**
   * The feature id for the '<em><b>Send Start</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SHARED_MEMORY_COMMUNICATION__SEND_START = CodegenPackage.COMMUNICATION__SEND_START;

  /**
   * The feature id for the '<em><b>Send End</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SHARED_MEMORY_COMMUNICATION__SEND_END = CodegenPackage.COMMUNICATION__SEND_END;

  /**
   * The feature id for the '<em><b>Receive Start</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SHARED_MEMORY_COMMUNICATION__RECEIVE_START = CodegenPackage.COMMUNICATION__RECEIVE_START;

  /**
   * The feature id for the '<em><b>Receive End</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SHARED_MEMORY_COMMUNICATION__RECEIVE_END = CodegenPackage.COMMUNICATION__RECEIVE_END;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SHARED_MEMORY_COMMUNICATION__ID = CodegenPackage.COMMUNICATION__ID;

  /**
   * The feature id for the '<em><b>Nodes</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SHARED_MEMORY_COMMUNICATION__NODES = CodegenPackage.COMMUNICATION__NODES;

  /**
   * The feature id for the '<em><b>Receive Release</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SHARED_MEMORY_COMMUNICATION__RECEIVE_RELEASE = CodegenPackage.COMMUNICATION__RECEIVE_RELEASE;

  /**
   * The feature id for the '<em><b>Send Reserve</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SHARED_MEMORY_COMMUNICATION__SEND_RESERVE = CodegenPackage.COMMUNICATION__SEND_RESERVE;

  /**
   * The feature id for the '<em><b>Redundant</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SHARED_MEMORY_COMMUNICATION__REDUNDANT = CodegenPackage.COMMUNICATION__REDUNDANT;

  /**
   * The number of structural features of the '<em>Shared Memory Communication</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SHARED_MEMORY_COMMUNICATION_FEATURE_COUNT = CodegenPackage.COMMUNICATION_FEATURE_COUNT + 0;

  /**
   * The meta object id for the ' {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.ConstantStringImpl
   * <em>Constant String</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.ConstantStringImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getConstantString()
   * @generated
   */
  int CONSTANT_STRING = 19;

  /**
   * The feature id for the '<em><b>Comment</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CONSTANT_STRING__COMMENT = CodegenPackage.VARIABLE__COMMENT;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CONSTANT_STRING__NAME = CodegenPackage.VARIABLE__NAME;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CONSTANT_STRING__TYPE = CodegenPackage.VARIABLE__TYPE;

  /**
   * The feature id for the '<em><b>Creator</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CONSTANT_STRING__CREATOR = CodegenPackage.VARIABLE__CREATOR;

  /**
   * The feature id for the '<em><b>Users</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CONSTANT_STRING__USERS = CodegenPackage.VARIABLE__USERS;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CONSTANT_STRING__VALUE = CodegenPackage.VARIABLE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Constant String</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CONSTANT_STRING_FEATURE_COUNT = CodegenPackage.VARIABLE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.NullBufferImpl <em>Null
   * Buffer</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.NullBufferImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getNullBuffer()
   * @generated
   */
  int NULL_BUFFER = 20;

  /**
   * The feature id for the '<em><b>Comment</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NULL_BUFFER__COMMENT = CodegenPackage.SUB_BUFFER__COMMENT;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NULL_BUFFER__NAME = CodegenPackage.SUB_BUFFER__NAME;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NULL_BUFFER__TYPE = CodegenPackage.SUB_BUFFER__TYPE;

  /**
   * The feature id for the '<em><b>Creator</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NULL_BUFFER__CREATOR = CodegenPackage.SUB_BUFFER__CREATOR;

  /**
   * The feature id for the '<em><b>Users</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NULL_BUFFER__USERS = CodegenPackage.SUB_BUFFER__USERS;

  /**
   * The feature id for the '<em><b>Size</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NULL_BUFFER__SIZE = CodegenPackage.SUB_BUFFER__SIZE;

  /**
   * The feature id for the '<em><b>Childrens</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NULL_BUFFER__CHILDRENS = CodegenPackage.SUB_BUFFER__CHILDRENS;

  /**
   * The feature id for the '<em><b>Type Size</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NULL_BUFFER__TYPE_SIZE = CodegenPackage.SUB_BUFFER__TYPE_SIZE;

  /**
   * The feature id for the '<em><b>Merged Range</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NULL_BUFFER__MERGED_RANGE = CodegenPackage.SUB_BUFFER__MERGED_RANGE;

  /**
   * The feature id for the '<em><b>Local</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NULL_BUFFER__LOCAL = CodegenPackage.SUB_BUFFER__LOCAL;

  /**
   * The feature id for the '<em><b>Container</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NULL_BUFFER__CONTAINER = CodegenPackage.SUB_BUFFER__CONTAINER;

  /**
   * The feature id for the '<em><b>Offset</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int NULL_BUFFER__OFFSET = CodegenPackage.SUB_BUFFER__OFFSET;

  /**
   * The number of structural features of the '<em>Null Buffer</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int NULL_BUFFER_FEATURE_COUNT = CodegenPackage.SUB_BUFFER_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.FiniteLoopBlockImpl <em>Finite
   * Loop Block</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.FiniteLoopBlockImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getFiniteLoopBlock()
   * @generated
   */
  int FINITE_LOOP_BLOCK = 21;

  /**
   * The feature id for the '<em><b>Code Elts</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FINITE_LOOP_BLOCK__CODE_ELTS = CodegenPackage.LOOP_BLOCK__CODE_ELTS;

  /**
   * The feature id for the '<em><b>Declarations</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FINITE_LOOP_BLOCK__DECLARATIONS = CodegenPackage.LOOP_BLOCK__DECLARATIONS;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FINITE_LOOP_BLOCK__NAME = CodegenPackage.LOOP_BLOCK__NAME;

  /**
   * The feature id for the '<em><b>Definitions</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FINITE_LOOP_BLOCK__DEFINITIONS = CodegenPackage.LOOP_BLOCK__DEFINITIONS;

  /**
   * The feature id for the '<em><b>Nb Iter</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FINITE_LOOP_BLOCK__NB_ITER = CodegenPackage.LOOP_BLOCK_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Iter</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FINITE_LOOP_BLOCK__ITER = CodegenPackage.LOOP_BLOCK_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>In Buffers</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FINITE_LOOP_BLOCK__IN_BUFFERS = CodegenPackage.LOOP_BLOCK_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Out Buffers</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FINITE_LOOP_BLOCK__OUT_BUFFERS = CodegenPackage.LOOP_BLOCK_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Finite Loop Block</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int FINITE_LOOP_BLOCK_FEATURE_COUNT = CodegenPackage.LOOP_BLOCK_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.IntVarImpl <em>Int Var</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.IntVarImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getIntVar()
   * @generated
   */
  int INT_VAR = 22;

  /**
   * The feature id for the '<em><b>Comment</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int INT_VAR__COMMENT = CodegenPackage.VARIABLE__COMMENT;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int INT_VAR__NAME = CodegenPackage.VARIABLE__NAME;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int INT_VAR__TYPE = CodegenPackage.VARIABLE__TYPE;

  /**
   * The feature id for the '<em><b>Creator</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int INT_VAR__CREATOR = CodegenPackage.VARIABLE__CREATOR;

  /**
   * The feature id for the '<em><b>Users</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int INT_VAR__USERS = CodegenPackage.VARIABLE__USERS;

  /**
   * The number of structural features of the '<em>Int Var</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int INT_VAR_FEATURE_COUNT = CodegenPackage.VARIABLE_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferIteratorImpl <em>Buffer
   * Iterator</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferIteratorImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getBufferIterator()
   * @generated
   */
  int BUFFER_ITERATOR = 23;

  /**
   * The feature id for the '<em><b>Comment</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER_ITERATOR__COMMENT = CodegenPackage.SUB_BUFFER__COMMENT;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER_ITERATOR__NAME = CodegenPackage.SUB_BUFFER__NAME;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER_ITERATOR__TYPE = CodegenPackage.SUB_BUFFER__TYPE;

  /**
   * The feature id for the '<em><b>Creator</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER_ITERATOR__CREATOR = CodegenPackage.SUB_BUFFER__CREATOR;

  /**
   * The feature id for the '<em><b>Users</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER_ITERATOR__USERS = CodegenPackage.SUB_BUFFER__USERS;

  /**
   * The feature id for the '<em><b>Size</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER_ITERATOR__SIZE = CodegenPackage.SUB_BUFFER__SIZE;

  /**
   * The feature id for the '<em><b>Childrens</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER_ITERATOR__CHILDRENS = CodegenPackage.SUB_BUFFER__CHILDRENS;

  /**
   * The feature id for the '<em><b>Type Size</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER_ITERATOR__TYPE_SIZE = CodegenPackage.SUB_BUFFER__TYPE_SIZE;

  /**
   * The feature id for the '<em><b>Merged Range</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER_ITERATOR__MERGED_RANGE = CodegenPackage.SUB_BUFFER__MERGED_RANGE;

  /**
   * The feature id for the '<em><b>Local</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER_ITERATOR__LOCAL = CodegenPackage.SUB_BUFFER__LOCAL;

  /**
   * The feature id for the '<em><b>Container</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER_ITERATOR__CONTAINER = CodegenPackage.SUB_BUFFER__CONTAINER;

  /**
   * The feature id for the '<em><b>Offset</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER_ITERATOR__OFFSET = CodegenPackage.SUB_BUFFER__OFFSET;

  /**
   * The feature id for the '<em><b>Iter Size</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER_ITERATOR__ITER_SIZE = CodegenPackage.SUB_BUFFER_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Iter</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER_ITERATOR__ITER = CodegenPackage.SUB_BUFFER_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Buffer Iterator</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int BUFFER_ITERATOR_FEATURE_COUNT = CodegenPackage.SUB_BUFFER_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.PapifyActionImpl <em>Papify
   * Action</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.PapifyActionImpl
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getPapifyAction()
   * @generated
   */
  int PAPIFY_ACTION = 24;

  /**
   * The feature id for the '<em><b>Comment</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int PAPIFY_ACTION__COMMENT = CodegenPackage.VARIABLE__COMMENT;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int PAPIFY_ACTION__NAME = CodegenPackage.VARIABLE__NAME;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int PAPIFY_ACTION__TYPE = CodegenPackage.VARIABLE__TYPE;

  /**
   * The feature id for the '<em><b>Creator</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int PAPIFY_ACTION__CREATOR = CodegenPackage.VARIABLE__CREATOR;

  /**
   * The feature id for the '<em><b>Users</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int PAPIFY_ACTION__USERS = CodegenPackage.VARIABLE__USERS;

  /**
   * The number of structural features of the '<em>Papify Action</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int PAPIFY_ACTION_FEATURE_COUNT = CodegenPackage.VARIABLE_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.Direction <em>Direction</em>}' enum.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Direction
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getDirection()
   * @generated
   */
  int DIRECTION = 25;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.Delimiter <em>Delimiter</em>}' enum.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Delimiter
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getDelimiter()
   * @generated
   */
  int DELIMITER = 26;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialType <em>Special Type</em>}'
   * enum. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.SpecialType
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getSpecialType()
   * @generated
   */
  int SPECIAL_TYPE = 27;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation <em>Fifo
   * Operation</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getFifoOperation()
   * @generated
   */
  int FIFO_OPERATION = 28;

  /**
   * The meta object id for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.PortDirection <em>Port
   * Direction</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.codegen.xtend.model.codegen.PortDirection
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getPortDirection()
   * @generated
   */
  int PORT_DIRECTION = 29;

  /**
   * The meta object id for the '<em>range</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.ietr.preesm.memory.script.Range
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getrange()
   * @generated
   */
  int RANGE = 30;

  /**
   * Returns the meta object for class ' {@link org.ietr.preesm.codegen.xtend.model.codegen.Block <em>Block</em>} '.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Block</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Block
   * @generated
   */
  EClass getBlock();

  /**
   * Returns the meta object for the containment reference list
   * '{@link org.ietr.preesm.codegen.xtend.model.codegen.Block#getCodeElts <em>Code Elts</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @return the meta object for the containment reference list '<em>Code Elts</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Block#getCodeElts()
   * @see #getBlock()
   * @generated
   */
  EReference getBlock_CodeElts();

  /**
   * Returns the meta object for the reference list '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.Block#getDeclarations <em>Declarations</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference list '<em>Declarations</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Block#getDeclarations()
   * @see #getBlock()
   * @generated
   */
  EReference getBlock_Declarations();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.codegen.xtend.model.codegen.Block#getName
   * <em>Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Block#getName()
   * @see #getBlock()
   * @generated
   */
  EAttribute getBlock_Name();

  /**
   * Returns the meta object for the containment reference list
   * '{@link org.ietr.preesm.codegen.xtend.model.codegen.Block#getDefinitions <em>Definitions</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Definitions</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Block#getDefinitions()
   * @see #getBlock()
   * @generated
   */
  EReference getBlock_Definitions();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.CodeElt <em>Code Elt</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Code Elt</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodeElt
   * @generated
   */
  EClass getCodeElt();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.Call <em>Call</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Call</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Call
   * @generated
   */
  EClass getCall();

  /**
   * Returns the meta object for the reference list '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.Call#getParameters <em>Parameters</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference list '<em>Parameters</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Call#getParameters()
   * @see #getCall()
   * @generated
   */
  EReference getCall_Parameters();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.codegen.xtend.model.codegen.Call#getName
   * <em>Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Call#getName()
   * @see #getCall()
   * @generated
   */
  EAttribute getCall_Name();

  /**
   * Returns the meta object for the reference ' {@link org.ietr.preesm.codegen.xtend.model.codegen.Call#getEReference0
   * <em>EReference0</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>EReference0</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Call#getEReference0()
   * @see #getCall()
   * @generated
   */
  EReference getCall_EReference0();

  /**
   * Returns the meta object for the attribute list
   * '{@link org.ietr.preesm.codegen.xtend.model.codegen.Call#getParameterDirections <em>Parameter Directions</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute list '<em>Parameter Directions</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Call#getParameterDirections()
   * @see #getCall()
   * @generated
   */
  EAttribute getCall_ParameterDirections();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.Variable <em>Variable</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Variable</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Variable
   * @generated
   */
  EClass getVariable();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.codegen.xtend.model.codegen.Variable#getName
   * <em>Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Variable#getName()
   * @see #getVariable()
   * @generated
   */
  EAttribute getVariable_Name();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.codegen.xtend.model.codegen.Variable#getType
   * <em>Type</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Type</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Variable#getType()
   * @see #getVariable()
   * @generated
   */
  EAttribute getVariable_Type();

  /**
   * Returns the meta object for the container reference '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.Variable#getCreator <em>Creator</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @return the meta object for the container reference '<em>Creator</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Variable#getCreator()
   * @see #getVariable()
   * @generated
   */
  EReference getVariable_Creator();

  /**
   * Returns the meta object for the reference list
   * '{@link org.ietr.preesm.codegen.xtend.model.codegen.Variable#getUsersList <em>Users</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @return the meta object for the reference list '<em>Users</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Variable#getUsersList()
   * @see #getVariable()
   * @generated
   */
  EReference getVariable_Users();

  /**
   * Returns the meta object for class ' {@link org.ietr.preesm.codegen.xtend.model.codegen.Buffer <em>Buffer</em> }'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Buffer</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Buffer
   * @generated
   */
  EClass getBuffer();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.codegen.xtend.model.codegen.Buffer#getSize
   * <em>Size</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Size</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Buffer#getSize()
   * @see #getBuffer()
   * @generated
   */
  EAttribute getBuffer_Size();

  /**
   * Returns the meta object for the reference list '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.Buffer#getChildrens <em>Childrens</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference list '<em>Childrens</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Buffer#getChildrens()
   * @see #getBuffer()
   * @generated
   */
  EReference getBuffer_Childrens();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.codegen.xtend.model.codegen.Buffer#getTypeSize
   * <em>Type Size</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Type Size</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Buffer#getTypeSize()
   * @see #getBuffer()
   * @generated
   */
  EAttribute getBuffer_TypeSize();

  /**
   * Returns the meta object for the attribute '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.Buffer#getMergedRange <em>Merged Range</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Merged Range</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Buffer#getMergedRange()
   * @see #getBuffer()
   * @generated
   */
  EAttribute getBuffer_MergedRange();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.codegen.xtend.model.codegen.Buffer#isLocal
   * <em>Local</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Local</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Buffer#isLocal()
   * @see #getBuffer()
   * @generated
   */
  EAttribute getBuffer_Local();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer <em>Sub
   * Buffer</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Sub Buffer</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer
   * @generated
   */
  EClass getSubBuffer();

  /**
   * Returns the meta object for the reference '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer#getContainer <em>Container</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Container</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer#getContainer()
   * @see #getSubBuffer()
   * @generated
   */
  EReference getSubBuffer_Container();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer#getOffset
   * <em>Offset</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Offset</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer#getOffset()
   * @see #getSubBuffer()
   * @generated
   */
  EAttribute getSubBuffer_Offset();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.Constant <em>Constant</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Constant</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Constant
   * @generated
   */
  EClass getConstant();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.codegen.xtend.model.codegen.Constant#getValue
   * <em>Value</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Constant#getValue()
   * @see #getConstant()
   * @generated
   */
  EAttribute getConstant_Value();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall <em>Function
   * Call</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Function Call</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall
   * @generated
   */
  EClass getFunctionCall();

  /**
   * Returns the meta object for the attribute '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall#getActorName <em>Actor Name</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Actor Name</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall#getActorName()
   * @see #getFunctionCall()
   * @generated
   */
  EAttribute getFunctionCall_ActorName();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.Communication
   * <em>Communication</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Communication</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication
   * @generated
   */
  EClass getCommunication();

  /**
   * Returns the meta object for the attribute '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getDirection <em>Direction</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Direction</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getDirection()
   * @see #getCommunication()
   * @generated
   */
  EAttribute getCommunication_Direction();

  /**
   * Returns the meta object for the attribute '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getDelimiter <em>Delimiter</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Delimiter</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getDelimiter()
   * @see #getCommunication()
   * @generated
   */
  EAttribute getCommunication_Delimiter();

  /**
   * Returns the meta object for the reference '{@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getData
   * <em>Data</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference '<em>Data</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getData()
   * @see #getCommunication()
   * @generated
   */
  EReference getCommunication_Data();

  /**
   * Returns the meta object for the reference '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getSendStart <em>Send Start</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Send Start</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getSendStart()
   * @see #getCommunication()
   * @generated
   */
  EReference getCommunication_SendStart();

  /**
   * Returns the meta object for the reference '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getSendEnd <em>Send End</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Send End</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getSendEnd()
   * @see #getCommunication()
   * @generated
   */
  EReference getCommunication_SendEnd();

  /**
   * Returns the meta object for the reference '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getReceiveStart <em>Receive Start</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Receive Start</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getReceiveStart()
   * @see #getCommunication()
   * @generated
   */
  EReference getCommunication_ReceiveStart();

  /**
   * Returns the meta object for the reference '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getReceiveEnd <em>Receive End</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Receive End</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getReceiveEnd()
   * @see #getCommunication()
   * @generated
   */
  EReference getCommunication_ReceiveEnd();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getId
   * <em>Id</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getId()
   * @see #getCommunication()
   * @generated
   */
  EAttribute getCommunication_Id();

  /**
   * Returns the meta object for the containment reference list
   * '{@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getNodes <em>Nodes</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Nodes</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getNodes()
   * @see #getCommunication()
   * @generated
   */
  EReference getCommunication_Nodes();

  /**
   * Returns the meta object for the reference
   * '{@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getReceiveRelease <em>Receive Release</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference '<em>Receive Release</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getReceiveRelease()
   * @see #getCommunication()
   * @generated
   */
  EReference getCommunication_ReceiveRelease();

  /**
   * Returns the meta object for the reference '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getSendReserve <em>Send Reserve</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Send Reserve</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getSendReserve()
   * @see #getCommunication()
   * @generated
   */
  EReference getCommunication_SendReserve();

  /**
   * Returns the meta object for the attribute
   * '{@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#isRedundant <em>Redundant</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Redundant</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#isRedundant()
   * @see #getCommunication()
   * @generated
   */
  EAttribute getCommunication_Redundant();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock <em>Core
   * Block</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Core Block</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock
   * @generated
   */
  EClass getCoreBlock();

  /**
   * Returns the meta object for the reference '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getLoopBlock <em>Loop Block</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Loop Block</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getLoopBlock()
   * @see #getCoreBlock()
   * @generated
   */
  EReference getCoreBlock_LoopBlock();

  /**
   * Returns the meta object for the reference '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getInitBlock <em>Init Block</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Init Block</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getInitBlock()
   * @see #getCoreBlock()
   * @generated
   */
  EReference getCoreBlock_InitBlock();

  /**
   * Returns the meta object for the attribute '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getCoreType <em>Core Type</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Core Type</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getCoreType()
   * @see #getCoreBlock()
   * @generated
   */
  EAttribute getCoreBlock_CoreType();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getCoreID
   * <em>Core ID</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Core ID</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getCoreID()
   * @see #getCoreBlock()
   * @generated
   */
  EAttribute getCoreBlock_CoreID();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock <em>Actor
   * Block</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Actor Block</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock
   * @generated
   */
  EClass getActorBlock();

  /**
   * Returns the meta object for the reference '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock#getLoopBlock <em>Loop Block</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Loop Block</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock#getLoopBlock()
   * @see #getActorBlock()
   * @generated
   */
  EReference getActorBlock_LoopBlock();

  /**
   * Returns the meta object for the reference '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock#getInitBlock <em>Init Block</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Init Block</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock#getInitBlock()
   * @see #getActorBlock()
   * @generated
   */
  EReference getActorBlock_InitBlock();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock <em>Loop
   * Block</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Loop Block</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock
   * @generated
   */
  EClass getLoopBlock();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.ActorCall <em>Actor
   * Call</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Actor Call</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.ActorCall
   * @generated
   */
  EClass getActorCall();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.CallBlock <em>Call
   * Block</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Call Block</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CallBlock
   * @generated
   */
  EClass getCallBlock();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall <em>Special
   * Call</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Special Call</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall
   * @generated
   */
  EClass getSpecialCall();

  /**
   * Returns the meta object for the attribute '{@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall#getType
   * <em>Type</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Type</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall#getType()
   * @see #getSpecialCall()
   * @generated
   */
  EAttribute getSpecialCall_Type();

  /**
   * Returns the meta object for the reference list
   * '{@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall#getInputBuffers <em>Input Buffers</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference list '<em>Input Buffers</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall#getInputBuffers()
   * @see #getSpecialCall()
   * @generated
   */
  EReference getSpecialCall_InputBuffers();

  /**
   * Returns the meta object for the reference list
   * '{@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall#getOutputBuffers <em>Output Buffers</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference list '<em>Output Buffers</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall#getOutputBuffers()
   * @see #getSpecialCall()
   * @generated
   */
  EReference getSpecialCall_OutputBuffers();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall <em>Fifo
   * Call</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Fifo Call</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoCall
   * @generated
   */
  EClass getFifoCall();

  /**
   * Returns the meta object for the attribute '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getOperation <em>Operation</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Operation</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getOperation()
   * @see #getFifoCall()
   * @generated
   */
  EAttribute getFifoCall_Operation();

  /**
   * Returns the meta object for the reference ' {@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getFifoHead
   * <em>Fifo Head</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Fifo Head</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getFifoHead()
   * @see #getFifoCall()
   * @generated
   */
  EReference getFifoCall_FifoHead();

  /**
   * Returns the meta object for the reference ' {@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getFifoTail
   * <em>Fifo Tail</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Fifo Tail</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getFifoTail()
   * @see #getFifoCall()
   * @generated
   */
  EReference getFifoCall_FifoTail();

  /**
   * Returns the meta object for the reference '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getHeadBuffer <em>Head Buffer</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Head Buffer</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getHeadBuffer()
   * @see #getFifoCall()
   * @generated
   */
  EReference getFifoCall_HeadBuffer();

  /**
   * Returns the meta object for the reference '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getBodyBuffer <em>Body Buffer</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Body Buffer</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getBodyBuffer()
   * @see #getFifoCall()
   * @generated
   */
  EReference getFifoCall_BodyBuffer();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.Commentable
   * <em>Commentable</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Commentable</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Commentable
   * @generated
   */
  EClass getCommentable();

  /**
   * Returns the meta object for the attribute '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.Commentable#getComment <em>Comment</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Comment</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Commentable#getComment()
   * @see #getCommentable()
   * @generated
   */
  EAttribute getCommentable_Comment();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.CommunicationNode
   * <em>Communication Node</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Communication Node</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CommunicationNode
   * @generated
   */
  EClass getCommunicationNode();

  /**
   * Returns the meta object for the attribute '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.CommunicationNode#getName <em>Name</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CommunicationNode#getName()
   * @see #getCommunicationNode()
   * @generated
   */
  EAttribute getCommunicationNode_Name();

  /**
   * Returns the meta object for the attribute '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.CommunicationNode#getType <em>Type</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Type</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CommunicationNode#getType()
   * @see #getCommunicationNode()
   * @generated
   */
  EAttribute getCommunicationNode_Type();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication
   * <em>Shared Memory Communication</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Shared Memory Communication</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication
   * @generated
   */
  EClass getSharedMemoryCommunication();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.ConstantString <em>Constant
   * String</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Constant String</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.ConstantString
   * @generated
   */
  EClass getConstantString();

  /**
   * Returns the meta object for the attribute '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.ConstantString#getValue <em>Value</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.ConstantString#getValue()
   * @see #getConstantString()
   * @generated
   */
  EAttribute getConstantString_Value();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.NullBuffer <em>Null
   * Buffer</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Null Buffer</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.NullBuffer
   * @generated
   */
  EClass getNullBuffer();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.FiniteLoopBlock <em>Finite
   * Loop Block</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Finite Loop Block</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FiniteLoopBlock
   * @generated
   */
  EClass getFiniteLoopBlock();

  /**
   * Returns the meta object for the attribute
   * '{@link org.ietr.preesm.codegen.xtend.model.codegen.FiniteLoopBlock#getNbIter <em>Nb Iter</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Nb Iter</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FiniteLoopBlock#getNbIter()
   * @see #getFiniteLoopBlock()
   * @generated
   */
  EAttribute getFiniteLoopBlock_NbIter();

  /**
   * Returns the meta object for the reference
   * '{@link org.ietr.preesm.codegen.xtend.model.codegen.FiniteLoopBlock#getIter <em>Iter</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference '<em>Iter</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FiniteLoopBlock#getIter()
   * @see #getFiniteLoopBlock()
   * @generated
   */
  EReference getFiniteLoopBlock_Iter();

  /**
   * Returns the meta object for the reference list
   * '{@link org.ietr.preesm.codegen.xtend.model.codegen.FiniteLoopBlock#getInBuffers <em>In Buffers</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference list '<em>In Buffers</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FiniteLoopBlock#getInBuffers()
   * @see #getFiniteLoopBlock()
   * @generated
   */
  EReference getFiniteLoopBlock_InBuffers();

  /**
   * Returns the meta object for the reference list
   * '{@link org.ietr.preesm.codegen.xtend.model.codegen.FiniteLoopBlock#getOutBuffers <em>Out Buffers</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference list '<em>Out Buffers</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FiniteLoopBlock#getOutBuffers()
   * @see #getFiniteLoopBlock()
   * @generated
   */
  EReference getFiniteLoopBlock_OutBuffers();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.IntVar <em>Int Var</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Int Var</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.IntVar
   * @generated
   */
  EClass getIntVar();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.BufferIterator <em>Buffer
   * Iterator</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Buffer Iterator</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.BufferIterator
   * @generated
   */
  EClass getBufferIterator();

  /**
   * Returns the meta object for the attribute
   * '{@link org.ietr.preesm.codegen.xtend.model.codegen.BufferIterator#getIterSize <em>Iter Size</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Iter Size</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.BufferIterator#getIterSize()
   * @see #getBufferIterator()
   * @generated
   */
  EAttribute getBufferIterator_IterSize();

  /**
   * Returns the meta object for the reference
   * '{@link org.ietr.preesm.codegen.xtend.model.codegen.BufferIterator#getIter <em>Iter</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @return the meta object for the reference '<em>Iter</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.BufferIterator#getIter()
   * @see #getBufferIterator()
   * @generated
   */
  EReference getBufferIterator_Iter();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.codegen.xtend.model.codegen.PapifyAction <em>Papify
   * Action</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Papify Action</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.PapifyAction
   * @generated
   */
  EClass getPapifyAction();

  /**
   * Returns the meta object for enum '{@link org.ietr.preesm.codegen.xtend.model.codegen.Direction
   * <em>Direction</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for enum '<em>Direction</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Direction
   * @generated
   */
  EEnum getDirection();

  /**
   * Returns the meta object for enum '{@link org.ietr.preesm.codegen.xtend.model.codegen.Delimiter
   * <em>Delimiter</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for enum '<em>Delimiter</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Delimiter
   * @generated
   */
  EEnum getDelimiter();

  /**
   * Returns the meta object for enum '{@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialType <em>Special
   * Type</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for enum '<em>Special Type</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.SpecialType
   * @generated
   */
  EEnum getSpecialType();

  /**
   * Returns the meta object for enum '{@link org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation <em>Fifo
   * Operation</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for enum '<em>Fifo Operation</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation
   * @generated
   */
  EEnum getFifoOperation();

  /**
   * Returns the meta object for enum '{@link org.ietr.preesm.codegen.xtend.model.codegen.PortDirection <em>Port
   * Direction</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for enum '<em>Port Direction</em>'.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.PortDirection
   * @generated
   */
  EEnum getPortDirection();

  /**
   * Returns the meta object for data type '{@link org.ietr.preesm.memory.script.Range <em>range</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for data type '<em>range</em>'.
   * @see org.ietr.preesm.memory.script.Range
   * @model instanceClass="org.ietr.preesm.memory.script.Range"
   * @generated
   */
  EDataType getrange();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the factory that creates the instances of the model.
   * @generated
   */
  CodegenFactory getCodegenFactory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->.
   *
   * @generated
   */
  interface Literals {
    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BlockImpl
     * <em>Block</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.BlockImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getBlock()
     * @generated
     */
    EClass BLOCK = CodegenPackage.eINSTANCE.getBlock();

    /**
     * The meta object literal for the '<em><b>Code Elts</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference BLOCK__CODE_ELTS = CodegenPackage.eINSTANCE.getBlock_CodeElts();

    /**
     * The meta object literal for the '<em><b>Declarations</b></em>' reference list feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference BLOCK__DECLARATIONS = CodegenPackage.eINSTANCE.getBlock_Declarations();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute BLOCK__NAME = CodegenPackage.eINSTANCE.getBlock_Name();

    /**
     * The meta object literal for the '<em><b>Definitions</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference BLOCK__DEFINITIONS = CodegenPackage.eINSTANCE.getBlock_Definitions();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.CodeElt <em>Code Elt</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.codegen.xtend.model.codegen.CodeElt
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCodeElt()
     * @generated
     */
    EClass CODE_ELT = CodegenPackage.eINSTANCE.getCodeElt();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CallImpl <em>Call</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CallImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCall()
     * @generated
     */
    EClass CALL = CodegenPackage.eINSTANCE.getCall();

    /**
     * The meta object literal for the '<em><b>Parameters</b></em>' reference list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference CALL__PARAMETERS = CodegenPackage.eINSTANCE.getCall_Parameters();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CALL__NAME = CodegenPackage.eINSTANCE.getCall_Name();

    /**
     * The meta object literal for the '<em><b>EReference0</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference CALL__EREFERENCE0 = CodegenPackage.eINSTANCE.getCall_EReference0();

    /**
     * The meta object literal for the '<em><b>Parameter Directions</b></em>' attribute list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute CALL__PARAMETER_DIRECTIONS = CodegenPackage.eINSTANCE.getCall_ParameterDirections();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.VariableImpl
     * <em>Variable</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.VariableImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getVariable()
     * @generated
     */
    EClass VARIABLE = CodegenPackage.eINSTANCE.getVariable();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute VARIABLE__NAME = CodegenPackage.eINSTANCE.getVariable_Name();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute VARIABLE__TYPE = CodegenPackage.eINSTANCE.getVariable_Type();

    /**
     * The meta object literal for the '<em><b>Creator</b></em>' container reference feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference VARIABLE__CREATOR = CodegenPackage.eINSTANCE.getVariable_Creator();

    /**
     * The meta object literal for the '<em><b>Users</b></em>' reference list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference VARIABLE__USERS = CodegenPackage.eINSTANCE.getVariable_Users();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferImpl
     * <em>Buffer</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getBuffer()
     * @generated
     */
    EClass BUFFER = CodegenPackage.eINSTANCE.getBuffer();

    /**
     * The meta object literal for the '<em><b>Size</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute BUFFER__SIZE = CodegenPackage.eINSTANCE.getBuffer_Size();

    /**
     * The meta object literal for the '<em><b>Childrens</b></em>' reference list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference BUFFER__CHILDRENS = CodegenPackage.eINSTANCE.getBuffer_Childrens();

    /**
     * The meta object literal for the '<em><b>Type Size</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute BUFFER__TYPE_SIZE = CodegenPackage.eINSTANCE.getBuffer_TypeSize();

    /**
     * The meta object literal for the '<em><b>Merged Range</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute BUFFER__MERGED_RANGE = CodegenPackage.eINSTANCE.getBuffer_MergedRange();

    /**
     * The meta object literal for the '<em><b>Local</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute BUFFER__LOCAL = CodegenPackage.eINSTANCE.getBuffer_Local();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SubBufferImpl <em>Sub
     * Buffer</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.SubBufferImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getSubBuffer()
     * @generated
     */
    EClass SUB_BUFFER = CodegenPackage.eINSTANCE.getSubBuffer();

    /**
     * The meta object literal for the '<em><b>Container</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference SUB_BUFFER__CONTAINER = CodegenPackage.eINSTANCE.getSubBuffer_Container();

    /**
     * The meta object literal for the '<em><b>Offset</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute SUB_BUFFER__OFFSET = CodegenPackage.eINSTANCE.getSubBuffer_Offset();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.ConstantImpl
     * <em>Constant</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.ConstantImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getConstant()
     * @generated
     */
    EClass CONSTANT = CodegenPackage.eINSTANCE.getConstant();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CONSTANT__VALUE = CodegenPackage.eINSTANCE.getConstant_Value();

    /**
     * The meta object literal for the ' {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.FunctionCallImpl
     * <em>Function Call</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl. FunctionCallImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getFunctionCall()
     * @generated
     */
    EClass FUNCTION_CALL = CodegenPackage.eINSTANCE.getFunctionCall();

    /**
     * The meta object literal for the '<em><b>Actor Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute FUNCTION_CALL__ACTOR_NAME = CodegenPackage.eINSTANCE.getFunctionCall_ActorName();

    /**
     * The meta object literal for the ' {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl
     * <em>Communication</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl. CommunicationImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCommunication()
     * @generated
     */
    EClass COMMUNICATION = CodegenPackage.eINSTANCE.getCommunication();

    /**
     * The meta object literal for the '<em><b>Direction</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute COMMUNICATION__DIRECTION = CodegenPackage.eINSTANCE.getCommunication_Direction();

    /**
     * The meta object literal for the '<em><b>Delimiter</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute COMMUNICATION__DELIMITER = CodegenPackage.eINSTANCE.getCommunication_Delimiter();

    /**
     * The meta object literal for the '<em><b>Data</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference COMMUNICATION__DATA = CodegenPackage.eINSTANCE.getCommunication_Data();

    /**
     * The meta object literal for the '<em><b>Send Start</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference COMMUNICATION__SEND_START = CodegenPackage.eINSTANCE.getCommunication_SendStart();

    /**
     * The meta object literal for the '<em><b>Send End</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference COMMUNICATION__SEND_END = CodegenPackage.eINSTANCE.getCommunication_SendEnd();

    /**
     * The meta object literal for the '<em><b>Receive Start</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference COMMUNICATION__RECEIVE_START = CodegenPackage.eINSTANCE.getCommunication_ReceiveStart();

    /**
     * The meta object literal for the '<em><b>Receive End</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference COMMUNICATION__RECEIVE_END = CodegenPackage.eINSTANCE.getCommunication_ReceiveEnd();

    /**
     * The meta object literal for the '<em><b>Id</b></em>' attribute feature. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     */
    EAttribute COMMUNICATION__ID = CodegenPackage.eINSTANCE.getCommunication_Id();

    /**
     * The meta object literal for the '<em><b>Nodes</b></em>' containment reference list feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference COMMUNICATION__NODES = CodegenPackage.eINSTANCE.getCommunication_Nodes();

    /**
     * The meta object literal for the '<em><b>Receive Release</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference COMMUNICATION__RECEIVE_RELEASE = CodegenPackage.eINSTANCE.getCommunication_ReceiveRelease();

    /**
     * The meta object literal for the '<em><b>Send Reserve</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference COMMUNICATION__SEND_RESERVE = CodegenPackage.eINSTANCE.getCommunication_SendReserve();

    /**
     * The meta object literal for the '<em><b>Redundant</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute COMMUNICATION__REDUNDANT = CodegenPackage.eINSTANCE.getCommunication_Redundant();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CoreBlockImpl <em>Core
     * Block</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CoreBlockImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCoreBlock()
     * @generated
     */
    EClass CORE_BLOCK = CodegenPackage.eINSTANCE.getCoreBlock();

    /**
     * The meta object literal for the '<em><b>Loop Block</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference CORE_BLOCK__LOOP_BLOCK = CodegenPackage.eINSTANCE.getCoreBlock_LoopBlock();

    /**
     * The meta object literal for the '<em><b>Init Block</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference CORE_BLOCK__INIT_BLOCK = CodegenPackage.eINSTANCE.getCoreBlock_InitBlock();

    /**
     * The meta object literal for the '<em><b>Core Type</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CORE_BLOCK__CORE_TYPE = CodegenPackage.eINSTANCE.getCoreBlock_CoreType();

    /**
     * The meta object literal for the '<em><b>Core ID</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CORE_BLOCK__CORE_ID = CodegenPackage.eINSTANCE.getCoreBlock_CoreID();

    /**
     * The meta object literal for the ' {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.ActorBlockImpl
     * <em>Actor Block</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.ActorBlockImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getActorBlock()
     * @generated
     */
    EClass ACTOR_BLOCK = CodegenPackage.eINSTANCE.getActorBlock();

    /**
     * The meta object literal for the '<em><b>Loop Block</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference ACTOR_BLOCK__LOOP_BLOCK = CodegenPackage.eINSTANCE.getActorBlock_LoopBlock();

    /**
     * The meta object literal for the '<em><b>Init Block</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference ACTOR_BLOCK__INIT_BLOCK = CodegenPackage.eINSTANCE.getActorBlock_InitBlock();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.LoopBlockImpl <em>Loop
     * Block</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.LoopBlockImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getLoopBlock()
     * @generated
     */
    EClass LOOP_BLOCK = CodegenPackage.eINSTANCE.getLoopBlock();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.ActorCallImpl <em>Actor
     * Call</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.ActorCallImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getActorCall()
     * @generated
     */
    EClass ACTOR_CALL = CodegenPackage.eINSTANCE.getActorCall();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CallBlockImpl <em>Call
     * Block</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CallBlockImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCallBlock()
     * @generated
     */
    EClass CALL_BLOCK = CodegenPackage.eINSTANCE.getCallBlock();

    /**
     * The meta object literal for the ' {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SpecialCallImpl
     * <em>Special Call</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.SpecialCallImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getSpecialCall()
     * @generated
     */
    EClass SPECIAL_CALL = CodegenPackage.eINSTANCE.getSpecialCall();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute SPECIAL_CALL__TYPE = CodegenPackage.eINSTANCE.getSpecialCall_Type();

    /**
     * The meta object literal for the '<em><b>Input Buffers</b></em>' reference list feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference SPECIAL_CALL__INPUT_BUFFERS = CodegenPackage.eINSTANCE.getSpecialCall_InputBuffers();

    /**
     * The meta object literal for the '<em><b>Output Buffers</b></em>' reference list feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference SPECIAL_CALL__OUTPUT_BUFFERS = CodegenPackage.eINSTANCE.getSpecialCall_OutputBuffers();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.FifoCallImpl <em>Fifo
     * Call</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.FifoCallImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getFifoCall()
     * @generated
     */
    EClass FIFO_CALL = CodegenPackage.eINSTANCE.getFifoCall();

    /**
     * The meta object literal for the '<em><b>Operation</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute FIFO_CALL__OPERATION = CodegenPackage.eINSTANCE.getFifoCall_Operation();

    /**
     * The meta object literal for the '<em><b>Fifo Head</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference FIFO_CALL__FIFO_HEAD = CodegenPackage.eINSTANCE.getFifoCall_FifoHead();

    /**
     * The meta object literal for the '<em><b>Fifo Tail</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference FIFO_CALL__FIFO_TAIL = CodegenPackage.eINSTANCE.getFifoCall_FifoTail();

    /**
     * The meta object literal for the '<em><b>Head Buffer</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference FIFO_CALL__HEAD_BUFFER = CodegenPackage.eINSTANCE.getFifoCall_HeadBuffer();

    /**
     * The meta object literal for the '<em><b>Body Buffer</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference FIFO_CALL__BODY_BUFFER = CodegenPackage.eINSTANCE.getFifoCall_BodyBuffer();

    /**
     * The meta object literal for the ' {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommentableImpl
     * <em>Commentable</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CommentableImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCommentable()
     * @generated
     */
    EClass COMMENTABLE = CodegenPackage.eINSTANCE.getCommentable();

    /**
     * The meta object literal for the '<em><b>Comment</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute COMMENTABLE__COMMENT = CodegenPackage.eINSTANCE.getCommentable_Comment();

    /**
     * The meta object literal for the ' {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationNodeImpl
     * <em>Communication Node</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl. CommunicationNodeImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCommunicationNode()
     * @generated
     */
    EClass COMMUNICATION_NODE = CodegenPackage.eINSTANCE.getCommunicationNode();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute COMMUNICATION_NODE__NAME = CodegenPackage.eINSTANCE.getCommunicationNode_Name();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute COMMUNICATION_NODE__TYPE = CodegenPackage.eINSTANCE.getCommunicationNode_Type();

    /**
     * The meta object literal for the
     * '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SharedMemoryCommunicationImpl <em>Shared Memory
     * Communication</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.SharedMemoryCommunicationImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getSharedMemoryCommunication()
     * @generated
     */
    EClass SHARED_MEMORY_COMMUNICATION = CodegenPackage.eINSTANCE.getSharedMemoryCommunication();

    /**
     * The meta object literal for the ' {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.ConstantStringImpl
     * <em>Constant String</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl. ConstantStringImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getConstantString()
     * @generated
     */
    EClass CONSTANT_STRING = CodegenPackage.eINSTANCE.getConstantString();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute CONSTANT_STRING__VALUE = CodegenPackage.eINSTANCE.getConstantString_Value();

    /**
     * The meta object literal for the ' {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.NullBufferImpl <em>Null
     * Buffer</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.NullBufferImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getNullBuffer()
     * @generated
     */
    EClass NULL_BUFFER = CodegenPackage.eINSTANCE.getNullBuffer();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.FiniteLoopBlockImpl
     * <em>Finite Loop Block</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.FiniteLoopBlockImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getFiniteLoopBlock()
     * @generated
     */
    EClass FINITE_LOOP_BLOCK = CodegenPackage.eINSTANCE.getFiniteLoopBlock();

    /**
     * The meta object literal for the '<em><b>Nb Iter</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute FINITE_LOOP_BLOCK__NB_ITER = CodegenPackage.eINSTANCE.getFiniteLoopBlock_NbIter();

    /**
     * The meta object literal for the '<em><b>Iter</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference FINITE_LOOP_BLOCK__ITER = CodegenPackage.eINSTANCE.getFiniteLoopBlock_Iter();

    /**
     * The meta object literal for the '<em><b>In Buffers</b></em>' reference list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference FINITE_LOOP_BLOCK__IN_BUFFERS = CodegenPackage.eINSTANCE.getFiniteLoopBlock_InBuffers();

    /**
     * The meta object literal for the '<em><b>Out Buffers</b></em>' reference list feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference FINITE_LOOP_BLOCK__OUT_BUFFERS = CodegenPackage.eINSTANCE.getFiniteLoopBlock_OutBuffers();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.IntVarImpl <em>Int
     * Var</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.IntVarImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getIntVar()
     * @generated
     */
    EClass INT_VAR = CodegenPackage.eINSTANCE.getIntVar();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferIteratorImpl
     * <em>Buffer Iterator</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferIteratorImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getBufferIterator()
     * @generated
     */
    EClass BUFFER_ITERATOR = CodegenPackage.eINSTANCE.getBufferIterator();

    /**
     * The meta object literal for the '<em><b>Iter Size</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute BUFFER_ITERATOR__ITER_SIZE = CodegenPackage.eINSTANCE.getBufferIterator_IterSize();

    /**
     * The meta object literal for the '<em><b>Iter</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference BUFFER_ITERATOR__ITER = CodegenPackage.eINSTANCE.getBufferIterator_Iter();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.PapifyActionImpl
     * <em>Papify Action</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.PapifyActionImpl
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getPapifyAction()
     * @generated
     */
    EClass PAPIFY_ACTION = CodegenPackage.eINSTANCE.getPapifyAction();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.Direction
     * <em>Direction</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.codegen.xtend.model.codegen.Direction
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getDirection()
     * @generated
     */
    EEnum DIRECTION = CodegenPackage.eINSTANCE.getDirection();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.Delimiter
     * <em>Delimiter</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.codegen.xtend.model.codegen.Delimiter
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getDelimiter()
     * @generated
     */
    EEnum DELIMITER = CodegenPackage.eINSTANCE.getDelimiter();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialType <em>Special
     * Type</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.codegen.xtend.model.codegen.SpecialType
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getSpecialType()
     * @generated
     */
    EEnum SPECIAL_TYPE = CodegenPackage.eINSTANCE.getSpecialType();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation <em>Fifo
     * Operation</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getFifoOperation()
     * @generated
     */
    EEnum FIFO_OPERATION = CodegenPackage.eINSTANCE.getFifoOperation();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.codegen.xtend.model.codegen.PortDirection <em>Port
     * Direction</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.codegen.xtend.model.codegen.PortDirection
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getPortDirection()
     * @generated
     */
    EEnum PORT_DIRECTION = CodegenPackage.eINSTANCE.getPortDirection();

    /**
     * The meta object literal for the '<em>range</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.ietr.preesm.memory.script.Range
     * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getrange()
     * @generated
     */
    EDataType RANGE = CodegenPackage.eINSTANCE.getrange();

  }

} // CodegenPackage
