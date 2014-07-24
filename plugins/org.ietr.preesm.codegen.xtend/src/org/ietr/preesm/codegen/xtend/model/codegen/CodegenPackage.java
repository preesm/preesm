/**
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
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
 */
package org.ietr.preesm.codegen.xtend.model.codegen;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains
 * accessors for the meta objects to represent
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
	 * The singleton instance of the package. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	CodegenPackage eINSTANCE = org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl
			.init();

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.CodeElt
	 * <em>Code Elt</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodeElt
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCodeElt()
	 * @generated
	 */
	int CODE_ELT = 1;

	/**
	 * The number of structural features of the '<em>Code Elt</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CODE_ELT_FEATURE_COUNT = 0;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BlockImpl
	 * <em>Block</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.BlockImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getBlock()
	 * @generated
	 */
	int BLOCK = 0;

	/**
	 * The feature id for the '<em><b>Code Elts</b></em>' containment reference
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int BLOCK__CODE_ELTS = CODE_ELT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Declarations</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int BLOCK__DECLARATIONS = CODE_ELT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int BLOCK__NAME = CODE_ELT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Definitions</b></em>' containment
	 * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int BLOCK__DEFINITIONS = CODE_ELT_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Block</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int BLOCK_FEATURE_COUNT = CODE_ELT_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CallImpl
	 * <em>Call</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CallImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCall()
	 * @generated
	 */
	int CALL = 2;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CALL__PARAMETERS = CODE_ELT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CALL__NAME = CODE_ELT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>EReference0</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CALL__EREFERENCE0 = CODE_ELT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Parameter Directions</b></em>' attribute
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CALL__PARAMETER_DIRECTIONS = CODE_ELT_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Call</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CALL_FEATURE_COUNT = CODE_ELT_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommentableImpl
	 * <em>Commentable</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CommentableImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCommentable()
	 * @generated
	 */
	int COMMENTABLE = 16;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMENTABLE__COMMENT = 0;

	/**
	 * The number of structural features of the '<em>Commentable</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMENTABLE_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.VariableImpl
	 * <em>Variable</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.VariableImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getVariable()
	 * @generated
	 */
	int VARIABLE = 3;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int VARIABLE__COMMENT = COMMENTABLE__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int VARIABLE__NAME = COMMENTABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int VARIABLE__TYPE = COMMENTABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Creator</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int VARIABLE__CREATOR = COMMENTABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Users</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int VARIABLE__USERS = COMMENTABLE_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Variable</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int VARIABLE_FEATURE_COUNT = COMMENTABLE_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferImpl
	 * <em>Buffer</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getBuffer()
	 * @generated
	 */
	int BUFFER = 4;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int BUFFER__COMMENT = VARIABLE__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int BUFFER__NAME = VARIABLE__NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int BUFFER__TYPE = VARIABLE__TYPE;

	/**
	 * The feature id for the '<em><b>Creator</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int BUFFER__CREATOR = VARIABLE__CREATOR;

	/**
	 * The feature id for the '<em><b>Users</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int BUFFER__USERS = VARIABLE__USERS;

	/**
	 * The feature id for the '<em><b>Size</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int BUFFER__SIZE = VARIABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Childrens</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int BUFFER__CHILDRENS = VARIABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Type Size</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int BUFFER__TYPE_SIZE = VARIABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Merged Range</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int BUFFER__MERGED_RANGE = VARIABLE_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Buffer</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int BUFFER_FEATURE_COUNT = VARIABLE_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SubBufferImpl
	 * <em>Sub Buffer</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.SubBufferImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getSubBuffer()
	 * @generated
	 */
	int SUB_BUFFER = 5;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SUB_BUFFER__COMMENT = BUFFER__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SUB_BUFFER__NAME = BUFFER__NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SUB_BUFFER__TYPE = BUFFER__TYPE;

	/**
	 * The feature id for the '<em><b>Creator</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SUB_BUFFER__CREATOR = BUFFER__CREATOR;

	/**
	 * The feature id for the '<em><b>Users</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SUB_BUFFER__USERS = BUFFER__USERS;

	/**
	 * The feature id for the '<em><b>Size</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SUB_BUFFER__SIZE = BUFFER__SIZE;

	/**
	 * The feature id for the '<em><b>Childrens</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SUB_BUFFER__CHILDRENS = BUFFER__CHILDRENS;

	/**
	 * The feature id for the '<em><b>Type Size</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SUB_BUFFER__TYPE_SIZE = BUFFER__TYPE_SIZE;

	/**
	 * The feature id for the '<em><b>Merged Range</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SUB_BUFFER__MERGED_RANGE = BUFFER__MERGED_RANGE;

	/**
	 * The feature id for the '<em><b>Container</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SUB_BUFFER__CONTAINER = BUFFER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Offset</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SUB_BUFFER__OFFSET = BUFFER_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Sub Buffer</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SUB_BUFFER_FEATURE_COUNT = BUFFER_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.ConstantImpl
	 * <em>Constant</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.ConstantImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getConstant()
	 * @generated
	 */
	int CONSTANT = 6;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONSTANT__COMMENT = VARIABLE__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONSTANT__NAME = VARIABLE__NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONSTANT__TYPE = VARIABLE__TYPE;

	/**
	 * The feature id for the '<em><b>Creator</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONSTANT__CREATOR = VARIABLE__CREATOR;

	/**
	 * The feature id for the '<em><b>Users</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONSTANT__USERS = VARIABLE__USERS;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONSTANT__VALUE = VARIABLE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Constant</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONSTANT_FEATURE_COUNT = VARIABLE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.FunctionCallImpl
	 * <em>Function Call</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.FunctionCallImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getFunctionCall()
	 * @generated
	 */
	int FUNCTION_CALL = 7;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int FUNCTION_CALL__PARAMETERS = CALL__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int FUNCTION_CALL__NAME = CALL__NAME;

	/**
	 * The feature id for the '<em><b>EReference0</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int FUNCTION_CALL__EREFERENCE0 = CALL__EREFERENCE0;

	/**
	 * The feature id for the '<em><b>Parameter Directions</b></em>' attribute
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int FUNCTION_CALL__PARAMETER_DIRECTIONS = CALL__PARAMETER_DIRECTIONS;

	/**
	 * The feature id for the '<em><b>Actor Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int FUNCTION_CALL__ACTOR_NAME = CALL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Function Call</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int FUNCTION_CALL_FEATURE_COUNT = CALL_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl
	 * <em>Communication</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCommunication()
	 * @generated
	 */
	int COMMUNICATION = 8;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION__PARAMETERS = CALL__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION__NAME = CALL__NAME;

	/**
	 * The feature id for the '<em><b>EReference0</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION__EREFERENCE0 = CALL__EREFERENCE0;

	/**
	 * The feature id for the '<em><b>Parameter Directions</b></em>' attribute
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION__PARAMETER_DIRECTIONS = CALL__PARAMETER_DIRECTIONS;

	/**
	 * The feature id for the '<em><b>Direction</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION__DIRECTION = CALL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Delimiter</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION__DELIMITER = CALL_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Data</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION__DATA = CALL_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Send Start</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION__SEND_START = CALL_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Send End</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION__SEND_END = CALL_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Receive Start</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION__RECEIVE_START = CALL_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Receive End</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION__RECEIVE_END = CALL_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION__ID = CALL_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Nodes</b></em>' containment reference
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION__NODES = CALL_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Receive Release</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION__RECEIVE_RELEASE = CALL_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Send Reserve</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION__SEND_RESERVE = CALL_FEATURE_COUNT + 10;

	/**
	 * The number of structural features of the '<em>Communication</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION_FEATURE_COUNT = CALL_FEATURE_COUNT + 11;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CoreBlockImpl
	 * <em>Core Block</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CoreBlockImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCoreBlock()
	 * @generated
	 */
	int CORE_BLOCK = 9;

	/**
	 * The feature id for the '<em><b>Code Elts</b></em>' containment reference
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CORE_BLOCK__CODE_ELTS = BLOCK__CODE_ELTS;

	/**
	 * The feature id for the '<em><b>Declarations</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CORE_BLOCK__DECLARATIONS = BLOCK__DECLARATIONS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CORE_BLOCK__NAME = BLOCK__NAME;

	/**
	 * The feature id for the '<em><b>Definitions</b></em>' containment
	 * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CORE_BLOCK__DEFINITIONS = BLOCK__DEFINITIONS;

	/**
	 * The feature id for the '<em><b>Loop Block</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CORE_BLOCK__LOOP_BLOCK = BLOCK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Init Block</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CORE_BLOCK__INIT_BLOCK = BLOCK_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Core Type</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CORE_BLOCK__CORE_TYPE = BLOCK_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Core Block</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CORE_BLOCK_FEATURE_COUNT = BLOCK_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.ActorBlockImpl
	 * <em>Actor Block</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.ActorBlockImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getActorBlock()
	 * @generated
	 */
	int ACTOR_BLOCK = 10;

	/**
	 * The feature id for the '<em><b>Code Elts</b></em>' containment reference
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTOR_BLOCK__CODE_ELTS = BLOCK__CODE_ELTS;

	/**
	 * The feature id for the '<em><b>Declarations</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTOR_BLOCK__DECLARATIONS = BLOCK__DECLARATIONS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTOR_BLOCK__NAME = BLOCK__NAME;

	/**
	 * The feature id for the '<em><b>Definitions</b></em>' containment
	 * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTOR_BLOCK__DEFINITIONS = BLOCK__DEFINITIONS;

	/**
	 * The feature id for the '<em><b>Loop Block</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTOR_BLOCK__LOOP_BLOCK = BLOCK_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Init Block</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTOR_BLOCK__INIT_BLOCK = BLOCK_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Actor Block</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTOR_BLOCK_FEATURE_COUNT = BLOCK_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.LoopBlockImpl
	 * <em>Loop Block</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.LoopBlockImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getLoopBlock()
	 * @generated
	 */
	int LOOP_BLOCK = 11;

	/**
	 * The feature id for the '<em><b>Code Elts</b></em>' containment reference
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int LOOP_BLOCK__CODE_ELTS = BLOCK__CODE_ELTS;

	/**
	 * The feature id for the '<em><b>Declarations</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int LOOP_BLOCK__DECLARATIONS = BLOCK__DECLARATIONS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int LOOP_BLOCK__NAME = BLOCK__NAME;

	/**
	 * The feature id for the '<em><b>Definitions</b></em>' containment
	 * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int LOOP_BLOCK__DEFINITIONS = BLOCK__DEFINITIONS;

	/**
	 * The number of structural features of the '<em>Loop Block</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int LOOP_BLOCK_FEATURE_COUNT = BLOCK_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.ActorCallImpl
	 * <em>Actor Call</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.ActorCallImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getActorCall()
	 * @generated
	 */
	int ACTOR_CALL = 12;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTOR_CALL__PARAMETERS = CALL__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTOR_CALL__NAME = CALL__NAME;

	/**
	 * The feature id for the '<em><b>EReference0</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTOR_CALL__EREFERENCE0 = CALL__EREFERENCE0;

	/**
	 * The feature id for the '<em><b>Parameter Directions</b></em>' attribute
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTOR_CALL__PARAMETER_DIRECTIONS = CALL__PARAMETER_DIRECTIONS;

	/**
	 * The number of structural features of the '<em>Actor Call</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTOR_CALL_FEATURE_COUNT = CALL_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CallBlockImpl
	 * <em>Call Block</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CallBlockImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCallBlock()
	 * @generated
	 */
	int CALL_BLOCK = 13;

	/**
	 * The feature id for the '<em><b>Code Elts</b></em>' containment reference
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CALL_BLOCK__CODE_ELTS = BLOCK__CODE_ELTS;

	/**
	 * The feature id for the '<em><b>Declarations</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CALL_BLOCK__DECLARATIONS = BLOCK__DECLARATIONS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CALL_BLOCK__NAME = BLOCK__NAME;

	/**
	 * The feature id for the '<em><b>Definitions</b></em>' containment
	 * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CALL_BLOCK__DEFINITIONS = BLOCK__DEFINITIONS;

	/**
	 * The number of structural features of the '<em>Call Block</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CALL_BLOCK_FEATURE_COUNT = BLOCK_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SpecialCallImpl
	 * <em>Special Call</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.SpecialCallImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getSpecialCall()
	 * @generated
	 */
	int SPECIAL_CALL = 14;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SPECIAL_CALL__PARAMETERS = CALL__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SPECIAL_CALL__NAME = CALL__NAME;

	/**
	 * The feature id for the '<em><b>EReference0</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SPECIAL_CALL__EREFERENCE0 = CALL__EREFERENCE0;

	/**
	 * The feature id for the '<em><b>Parameter Directions</b></em>' attribute
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SPECIAL_CALL__PARAMETER_DIRECTIONS = CALL__PARAMETER_DIRECTIONS;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SPECIAL_CALL__TYPE = CALL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Input Buffers</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SPECIAL_CALL__INPUT_BUFFERS = CALL_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Output Buffers</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SPECIAL_CALL__OUTPUT_BUFFERS = CALL_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Special Call</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SPECIAL_CALL_FEATURE_COUNT = CALL_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.FifoCallImpl
	 * <em>Fifo Call</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.FifoCallImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getFifoCall()
	 * @generated
	 */
	int FIFO_CALL = 15;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int FIFO_CALL__PARAMETERS = CALL__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int FIFO_CALL__NAME = CALL__NAME;

	/**
	 * The feature id for the '<em><b>EReference0</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int FIFO_CALL__EREFERENCE0 = CALL__EREFERENCE0;

	/**
	 * The feature id for the '<em><b>Parameter Directions</b></em>' attribute
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int FIFO_CALL__PARAMETER_DIRECTIONS = CALL__PARAMETER_DIRECTIONS;

	/**
	 * The feature id for the '<em><b>Operation</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int FIFO_CALL__OPERATION = CALL_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Fifo Head</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int FIFO_CALL__FIFO_HEAD = CALL_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Fifo Tail</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int FIFO_CALL__FIFO_TAIL = CALL_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Head Buffer</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int FIFO_CALL__HEAD_BUFFER = CALL_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Body Buffer</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int FIFO_CALL__BODY_BUFFER = CALL_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Fifo Call</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int FIFO_CALL_FEATURE_COUNT = CALL_FEATURE_COUNT + 5;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationNodeImpl
	 * <em>Communication Node</em>}' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationNodeImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCommunicationNode()
	 * @generated
	 */
	int COMMUNICATION_NODE = 17;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION_NODE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION_NODE__TYPE = 1;

	/**
	 * The number of structural features of the '<em>Communication Node</em>'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION_NODE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SemaphoreImpl
	 * <em>Semaphore</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.SemaphoreImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getSemaphore()
	 * @generated
	 */
	int SEMAPHORE = 18;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SEMAPHORE__COMMENT = VARIABLE__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SEMAPHORE__NAME = VARIABLE__NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SEMAPHORE__TYPE = VARIABLE__TYPE;

	/**
	 * The feature id for the '<em><b>Creator</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SEMAPHORE__CREATOR = VARIABLE__CREATOR;

	/**
	 * The feature id for the '<em><b>Users</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SEMAPHORE__USERS = VARIABLE__USERS;

	/**
	 * The number of structural features of the '<em>Semaphore</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SEMAPHORE_FEATURE_COUNT = VARIABLE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SharedMemoryCommunicationImpl
	 * <em>Shared Memory Communication</em>}' class. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.SharedMemoryCommunicationImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getSharedMemoryCommunication()
	 * @generated
	 */
	int SHARED_MEMORY_COMMUNICATION = 19;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SHARED_MEMORY_COMMUNICATION__PARAMETERS = COMMUNICATION__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SHARED_MEMORY_COMMUNICATION__NAME = COMMUNICATION__NAME;

	/**
	 * The feature id for the '<em><b>EReference0</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SHARED_MEMORY_COMMUNICATION__EREFERENCE0 = COMMUNICATION__EREFERENCE0;

	/**
	 * The feature id for the '<em><b>Parameter Directions</b></em>' attribute
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SHARED_MEMORY_COMMUNICATION__PARAMETER_DIRECTIONS = COMMUNICATION__PARAMETER_DIRECTIONS;

	/**
	 * The feature id for the '<em><b>Direction</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SHARED_MEMORY_COMMUNICATION__DIRECTION = COMMUNICATION__DIRECTION;

	/**
	 * The feature id for the '<em><b>Delimiter</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SHARED_MEMORY_COMMUNICATION__DELIMITER = COMMUNICATION__DELIMITER;

	/**
	 * The feature id for the '<em><b>Data</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SHARED_MEMORY_COMMUNICATION__DATA = COMMUNICATION__DATA;

	/**
	 * The feature id for the '<em><b>Send Start</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SHARED_MEMORY_COMMUNICATION__SEND_START = COMMUNICATION__SEND_START;

	/**
	 * The feature id for the '<em><b>Send End</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SHARED_MEMORY_COMMUNICATION__SEND_END = COMMUNICATION__SEND_END;

	/**
	 * The feature id for the '<em><b>Receive Start</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SHARED_MEMORY_COMMUNICATION__RECEIVE_START = COMMUNICATION__RECEIVE_START;

	/**
	 * The feature id for the '<em><b>Receive End</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SHARED_MEMORY_COMMUNICATION__RECEIVE_END = COMMUNICATION__RECEIVE_END;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SHARED_MEMORY_COMMUNICATION__ID = COMMUNICATION__ID;

	/**
	 * The feature id for the '<em><b>Nodes</b></em>' containment reference
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SHARED_MEMORY_COMMUNICATION__NODES = COMMUNICATION__NODES;

	/**
	 * The feature id for the '<em><b>Receive Release</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SHARED_MEMORY_COMMUNICATION__RECEIVE_RELEASE = COMMUNICATION__RECEIVE_RELEASE;

	/**
	 * The feature id for the '<em><b>Send Reserve</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SHARED_MEMORY_COMMUNICATION__SEND_RESERVE = COMMUNICATION__SEND_RESERVE;

	/**
	 * The feature id for the '<em><b>Semaphore</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SHARED_MEMORY_COMMUNICATION__SEMAPHORE = COMMUNICATION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '
	 * <em>Shared Memory Communication</em>' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SHARED_MEMORY_COMMUNICATION_FEATURE_COUNT = COMMUNICATION_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.ConstantStringImpl
	 * <em>Constant String</em>}' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.ConstantStringImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getConstantString()
	 * @generated
	 */
	int CONSTANT_STRING = 20;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONSTANT_STRING__COMMENT = VARIABLE__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONSTANT_STRING__NAME = VARIABLE__NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONSTANT_STRING__TYPE = VARIABLE__TYPE;

	/**
	 * The feature id for the '<em><b>Creator</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONSTANT_STRING__CREATOR = VARIABLE__CREATOR;

	/**
	 * The feature id for the '<em><b>Users</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONSTANT_STRING__USERS = VARIABLE__USERS;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONSTANT_STRING__VALUE = VARIABLE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Constant String</em>'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONSTANT_STRING_FEATURE_COUNT = VARIABLE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.NullBufferImpl
	 * <em>Null Buffer</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.NullBufferImpl
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getNullBuffer()
	 * @generated
	 */
	int NULL_BUFFER = 21;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int NULL_BUFFER__COMMENT = BUFFER__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int NULL_BUFFER__NAME = BUFFER__NAME;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int NULL_BUFFER__TYPE = BUFFER__TYPE;

	/**
	 * The feature id for the '<em><b>Creator</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int NULL_BUFFER__CREATOR = BUFFER__CREATOR;

	/**
	 * The feature id for the '<em><b>Users</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int NULL_BUFFER__USERS = BUFFER__USERS;

	/**
	 * The feature id for the '<em><b>Size</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int NULL_BUFFER__SIZE = BUFFER__SIZE;

	/**
	 * The feature id for the '<em><b>Childrens</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int NULL_BUFFER__CHILDRENS = BUFFER__CHILDRENS;

	/**
	 * The feature id for the '<em><b>Type Size</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int NULL_BUFFER__TYPE_SIZE = BUFFER__TYPE_SIZE;

	/**
	 * The feature id for the '<em><b>Merged Range</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int NULL_BUFFER__MERGED_RANGE = BUFFER__MERGED_RANGE;

	/**
	 * The number of structural features of the '<em>Null Buffer</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int NULL_BUFFER_FEATURE_COUNT = BUFFER_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Direction
	 * <em>Direction</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Direction
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getDirection()
	 * @generated
	 */
	int DIRECTION = 22;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Delimiter
	 * <em>Delimiter</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Delimiter
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getDelimiter()
	 * @generated
	 */
	int DELIMITER = 23;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialType
	 * <em>Special Type</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.SpecialType
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getSpecialType()
	 * @generated
	 */
	int SPECIAL_TYPE = 24;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation
	 * <em>Fifo Operation</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getFifoOperation()
	 * @generated
	 */
	int FIFO_OPERATION = 25;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.PortDirection
	 * <em>Port Direction</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.PortDirection
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getPortDirection()
	 * @generated
	 */
	int PORT_DIRECTION = 26;

	/**
	 * The meta object id for the '<em>range</em>' data type. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.ietr.preesm.experiment.memory.Range
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getrange()
	 * @generated
	 */
	int RANGE = 27;

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Block <em>Block</em>}
	 * '. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Block</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Block
	 * @generated
	 */
	EClass getBlock();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Block#getCodeElts
	 * <em>Code Elts</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '
	 *         <em>Code Elts</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Block#getCodeElts()
	 * @see #getBlock()
	 * @generated
	 */
	EReference getBlock_CodeElts();

	/**
	 * Returns the meta object for the reference list '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Block#getDeclarations
	 * <em>Declarations</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference list '<em>Declarations</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Block#getDeclarations()
	 * @see #getBlock()
	 * @generated
	 */
	EReference getBlock_Declarations();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Block#getName
	 * <em>Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Block#getName()
	 * @see #getBlock()
	 * @generated
	 */
	EAttribute getBlock_Name();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Block#getDefinitions
	 * <em>Definitions</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '
	 *         <em>Definitions</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Block#getDefinitions()
	 * @see #getBlock()
	 * @generated
	 */
	EReference getBlock_Definitions();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.CodeElt
	 * <em>Code Elt</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Code Elt</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodeElt
	 * @generated
	 */
	EClass getCodeElt();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Call <em>Call</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Call</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Call
	 * @generated
	 */
	EClass getCall();

	/**
	 * Returns the meta object for the reference list '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Call#getParameters
	 * <em>Parameters</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference list '<em>Parameters</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Call#getParameters()
	 * @see #getCall()
	 * @generated
	 */
	EReference getCall_Parameters();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Call#getName
	 * <em>Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Call#getName()
	 * @see #getCall()
	 * @generated
	 */
	EAttribute getCall_Name();

	/**
	 * Returns the meta object for the reference '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Call#getEReference0
	 * <em>EReference0</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>EReference0</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Call#getEReference0()
	 * @see #getCall()
	 * @generated
	 */
	EReference getCall_EReference0();

	/**
	 * Returns the meta object for the attribute list '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Call#getParameterDirections
	 * <em>Parameter Directions</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for the attribute list '
	 *         <em>Parameter Directions</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Call#getParameterDirections()
	 * @see #getCall()
	 * @generated
	 */
	EAttribute getCall_ParameterDirections();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Variable
	 * <em>Variable</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Variable</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Variable
	 * @generated
	 */
	EClass getVariable();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Variable#getName
	 * <em>Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Variable#getName()
	 * @see #getVariable()
	 * @generated
	 */
	EAttribute getVariable_Name();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Variable#getType
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
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Variable#getCreator
	 * <em>Creator</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the container reference '<em>Creator</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Variable#getCreator()
	 * @see #getVariable()
	 * @generated
	 */
	EReference getVariable_Creator();

	/**
	 * Returns the meta object for the reference list '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Variable#getUsers
	 * <em>Users</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference list '<em>Users</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Variable#getUsers()
	 * @see #getVariable()
	 * @generated
	 */
	EReference getVariable_Users();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Buffer
	 * <em>Buffer</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Buffer</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Buffer
	 * @generated
	 */
	EClass getBuffer();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Buffer#getSize
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
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Buffer#getChildrens
	 * <em>Childrens</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference list '<em>Childrens</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Buffer#getChildrens()
	 * @see #getBuffer()
	 * @generated
	 */
	EReference getBuffer_Childrens();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Buffer#getTypeSize
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
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Buffer#getMergedRange
	 * <em>Merged Range</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Merged Range</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Buffer#getMergedRange()
	 * @see #getBuffer()
	 * @generated
	 */
	EAttribute getBuffer_MergedRange();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer
	 * <em>Sub Buffer</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Sub Buffer</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer
	 * @generated
	 */
	EClass getSubBuffer();

	/**
	 * Returns the meta object for the reference '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer#getContainer
	 * <em>Container</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Container</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer#getContainer()
	 * @see #getSubBuffer()
	 * @generated
	 */
	EReference getSubBuffer_Container();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer#getOffset
	 * <em>Offset</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Offset</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer#getOffset()
	 * @see #getSubBuffer()
	 * @generated
	 */
	EAttribute getSubBuffer_Offset();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Constant
	 * <em>Constant</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Constant</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Constant
	 * @generated
	 */
	EClass getConstant();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Constant#getValue
	 * <em>Value</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Constant#getValue()
	 * @see #getConstant()
	 * @generated
	 */
	EAttribute getConstant_Value();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall
	 * <em>Function Call</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Function Call</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall
	 * @generated
	 */
	EClass getFunctionCall();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall#getActorName
	 * <em>Actor Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Actor Name</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall#getActorName()
	 * @see #getFunctionCall()
	 * @generated
	 */
	EAttribute getFunctionCall_ActorName();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication
	 * <em>Communication</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Communication</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication
	 * @generated
	 */
	EClass getCommunication();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getDirection
	 * <em>Direction</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Direction</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getDirection()
	 * @see #getCommunication()
	 * @generated
	 */
	EAttribute getCommunication_Direction();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getDelimiter
	 * <em>Delimiter</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Delimiter</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getDelimiter()
	 * @see #getCommunication()
	 * @generated
	 */
	EAttribute getCommunication_Delimiter();

	/**
	 * Returns the meta object for the reference '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getData
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
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getSendStart
	 * <em>Send Start</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Send Start</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getSendStart()
	 * @see #getCommunication()
	 * @generated
	 */
	EReference getCommunication_SendStart();

	/**
	 * Returns the meta object for the reference '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getSendEnd
	 * <em>Send End</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Send End</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getSendEnd()
	 * @see #getCommunication()
	 * @generated
	 */
	EReference getCommunication_SendEnd();

	/**
	 * Returns the meta object for the reference '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getReceiveStart
	 * <em>Receive Start</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Receive Start</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getReceiveStart()
	 * @see #getCommunication()
	 * @generated
	 */
	EReference getCommunication_ReceiveStart();

	/**
	 * Returns the meta object for the reference '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getReceiveEnd
	 * <em>Receive End</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Receive End</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getReceiveEnd()
	 * @see #getCommunication()
	 * @generated
	 */
	EReference getCommunication_ReceiveEnd();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getId
	 * <em>Id</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getId()
	 * @see #getCommunication()
	 * @generated
	 */
	EAttribute getCommunication_Id();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getNodes
	 * <em>Nodes</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '
	 *         <em>Nodes</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getNodes()
	 * @see #getCommunication()
	 * @generated
	 */
	EReference getCommunication_Nodes();

	/**
	 * Returns the meta object for the reference '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getReceiveRelease
	 * <em>Receive Release</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Receive Release</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getReceiveRelease()
	 * @see #getCommunication()
	 * @generated
	 */
	EReference getCommunication_ReceiveRelease();

	/**
	 * Returns the meta object for the reference '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication#getSendReserve
	 * <em>Send Reserve</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Send Reserve</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication#getSendReserve()
	 * @see #getCommunication()
	 * @generated
	 */
	EReference getCommunication_SendReserve();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock
	 * <em>Core Block</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Core Block</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock
	 * @generated
	 */
	EClass getCoreBlock();

	/**
	 * Returns the meta object for the reference '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getLoopBlock
	 * <em>Loop Block</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Loop Block</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getLoopBlock()
	 * @see #getCoreBlock()
	 * @generated
	 */
	EReference getCoreBlock_LoopBlock();

	/**
	 * Returns the meta object for the reference '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getInitBlock
	 * <em>Init Block</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Init Block</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getInitBlock()
	 * @see #getCoreBlock()
	 * @generated
	 */
	EReference getCoreBlock_InitBlock();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getCoreType
	 * <em>Core Type</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Core Type</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getCoreType()
	 * @see #getCoreBlock()
	 * @generated
	 */
	EAttribute getCoreBlock_CoreType();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock
	 * <em>Actor Block</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Actor Block</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock
	 * @generated
	 */
	EClass getActorBlock();

	/**
	 * Returns the meta object for the reference '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock#getLoopBlock
	 * <em>Loop Block</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Loop Block</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock#getLoopBlock()
	 * @see #getActorBlock()
	 * @generated
	 */
	EReference getActorBlock_LoopBlock();

	/**
	 * Returns the meta object for the reference '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock#getInitBlock
	 * <em>Init Block</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Init Block</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock#getInitBlock()
	 * @see #getActorBlock()
	 * @generated
	 */
	EReference getActorBlock_InitBlock();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock
	 * <em>Loop Block</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Loop Block</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock
	 * @generated
	 */
	EClass getLoopBlock();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.ActorCall
	 * <em>Actor Call</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Actor Call</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.ActorCall
	 * @generated
	 */
	EClass getActorCall();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.CallBlock
	 * <em>Call Block</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Call Block</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.CallBlock
	 * @generated
	 */
	EClass getCallBlock();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall
	 * <em>Special Call</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Special Call</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall
	 * @generated
	 */
	EClass getSpecialCall();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall#getType
	 * <em>Type</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall#getType()
	 * @see #getSpecialCall()
	 * @generated
	 */
	EAttribute getSpecialCall_Type();

	/**
	 * Returns the meta object for the reference list '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall#getInputBuffers
	 * <em>Input Buffers</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference list '<em>Input Buffers</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall#getInputBuffers()
	 * @see #getSpecialCall()
	 * @generated
	 */
	EReference getSpecialCall_InputBuffers();

	/**
	 * Returns the meta object for the reference list '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall#getOutputBuffers
	 * <em>Output Buffers</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference list '<em>Output Buffers</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall#getOutputBuffers()
	 * @see #getSpecialCall()
	 * @generated
	 */
	EReference getSpecialCall_OutputBuffers();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall
	 * <em>Fifo Call</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Fifo Call</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoCall
	 * @generated
	 */
	EClass getFifoCall();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getOperation
	 * <em>Operation</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Operation</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getOperation()
	 * @see #getFifoCall()
	 * @generated
	 */
	EAttribute getFifoCall_Operation();

	/**
	 * Returns the meta object for the reference '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getFifoHead
	 * <em>Fifo Head</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Fifo Head</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getFifoHead()
	 * @see #getFifoCall()
	 * @generated
	 */
	EReference getFifoCall_FifoHead();

	/**
	 * Returns the meta object for the reference '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getFifoTail
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
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getHeadBuffer
	 * <em>Head Buffer</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Head Buffer</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getHeadBuffer()
	 * @see #getFifoCall()
	 * @generated
	 */
	EReference getFifoCall_HeadBuffer();

	/**
	 * Returns the meta object for the reference '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getBodyBuffer
	 * <em>Body Buffer</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Body Buffer</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getBodyBuffer()
	 * @see #getFifoCall()
	 * @generated
	 */
	EReference getFifoCall_BodyBuffer();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Commentable
	 * <em>Commentable</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Commentable</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Commentable
	 * @generated
	 */
	EClass getCommentable();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Commentable#getComment
	 * <em>Comment</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Comment</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Commentable#getComment()
	 * @see #getCommentable()
	 * @generated
	 */
	EAttribute getCommentable_Comment();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.CommunicationNode
	 * <em>Communication Node</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @return the meta object for class '<em>Communication Node</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.CommunicationNode
	 * @generated
	 */
	EClass getCommunicationNode();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.CommunicationNode#getName
	 * <em>Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.CommunicationNode#getName()
	 * @see #getCommunicationNode()
	 * @generated
	 */
	EAttribute getCommunicationNode_Name();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.CommunicationNode#getType
	 * <em>Type</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.CommunicationNode#getType()
	 * @see #getCommunicationNode()
	 * @generated
	 */
	EAttribute getCommunicationNode_Type();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Semaphore
	 * <em>Semaphore</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Semaphore</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Semaphore
	 * @generated
	 */
	EClass getSemaphore();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication
	 * <em>Shared Memory Communication</em>}'. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Shared Memory Communication</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication
	 * @generated
	 */
	EClass getSharedMemoryCommunication();

	/**
	 * Returns the meta object for the reference '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication#getSemaphore
	 * <em>Semaphore</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Semaphore</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication#getSemaphore()
	 * @see #getSharedMemoryCommunication()
	 * @generated
	 */
	EReference getSharedMemoryCommunication_Semaphore();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.ConstantString
	 * <em>Constant String</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Constant String</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.ConstantString
	 * @generated
	 */
	EClass getConstantString();

	/**
	 * Returns the meta object for the attribute '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.ConstantString#getValue
	 * <em>Value</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.ConstantString#getValue()
	 * @see #getConstantString()
	 * @generated
	 */
	EAttribute getConstantString_Value();

	/**
	 * Returns the meta object for class '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.NullBuffer
	 * <em>Null Buffer</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Null Buffer</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.NullBuffer
	 * @generated
	 */
	EClass getNullBuffer();

	/**
	 * Returns the meta object for enum '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Direction
	 * <em>Direction</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for enum '<em>Direction</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Direction
	 * @generated
	 */
	EEnum getDirection();

	/**
	 * Returns the meta object for enum '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Delimiter
	 * <em>Delimiter</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for enum '<em>Delimiter</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Delimiter
	 * @generated
	 */
	EEnum getDelimiter();

	/**
	 * Returns the meta object for enum '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialType
	 * <em>Special Type</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for enum '<em>Special Type</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.SpecialType
	 * @generated
	 */
	EEnum getSpecialType();

	/**
	 * Returns the meta object for enum '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation
	 * <em>Fifo Operation</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for enum '<em>Fifo Operation</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation
	 * @generated
	 */
	EEnum getFifoOperation();

	/**
	 * Returns the meta object for enum '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.PortDirection
	 * <em>Port Direction</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for enum '<em>Port Direction</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.PortDirection
	 * @generated
	 */
	EEnum getPortDirection();

	/**
	 * Returns the meta object for data type '
	 * {@link org.ietr.preesm.experiment.memory.Range <em>range</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for data type '<em>range</em>'.
	 * @see org.ietr.preesm.experiment.memory.Range
	 * @model instanceClass="org.ietr.preesm.experiment.memory.Range"
	 * @generated
	 */
	EDataType getrange();

	/**
	 * Returns the factory that creates the instances of the model. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	CodegenFactory getCodegenFactory();

	/**
	 * <!-- begin-user-doc --> Defines literals for the meta objects that
	 * represent
	 * <ul>
	 * <li>each class,</li>
	 * <li>each feature of each class,</li>
	 * <li>each enum,</li>
	 * <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BlockImpl
		 * <em>Block</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.BlockImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getBlock()
		 * @generated
		 */
		EClass BLOCK = eINSTANCE.getBlock();

		/**
		 * The meta object literal for the '<em><b>Code Elts</b></em>'
		 * containment reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		EReference BLOCK__CODE_ELTS = eINSTANCE.getBlock_CodeElts();

		/**
		 * The meta object literal for the '<em><b>Declarations</b></em>'
		 * reference list feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference BLOCK__DECLARATIONS = eINSTANCE.getBlock_Declarations();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute BLOCK__NAME = eINSTANCE.getBlock_Name();

		/**
		 * The meta object literal for the '<em><b>Definitions</b></em>'
		 * containment reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		EReference BLOCK__DEFINITIONS = eINSTANCE.getBlock_Definitions();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.CodeElt
		 * <em>Code Elt</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodeElt
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCodeElt()
		 * @generated
		 */
		EClass CODE_ELT = eINSTANCE.getCodeElt();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CallImpl
		 * <em>Call</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CallImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCall()
		 * @generated
		 */
		EClass CALL = eINSTANCE.getCall();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>'
		 * reference list feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference CALL__PARAMETERS = eINSTANCE.getCall_Parameters();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute CALL__NAME = eINSTANCE.getCall_Name();

		/**
		 * The meta object literal for the '<em><b>EReference0</b></em>'
		 * reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference CALL__EREFERENCE0 = eINSTANCE.getCall_EReference0();

		/**
		 * The meta object literal for the '<em><b>Parameter Directions</b></em>
		 * ' attribute list feature. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @generated
		 */
		EAttribute CALL__PARAMETER_DIRECTIONS = eINSTANCE
				.getCall_ParameterDirections();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.VariableImpl
		 * <em>Variable</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.VariableImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getVariable()
		 * @generated
		 */
		EClass VARIABLE = eINSTANCE.getVariable();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute VARIABLE__NAME = eINSTANCE.getVariable_Name();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute VARIABLE__TYPE = eINSTANCE.getVariable_Type();

		/**
		 * The meta object literal for the '<em><b>Creator</b></em>' container
		 * reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference VARIABLE__CREATOR = eINSTANCE.getVariable_Creator();

		/**
		 * The meta object literal for the '<em><b>Users</b></em>' reference
		 * list feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference VARIABLE__USERS = eINSTANCE.getVariable_Users();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferImpl
		 * <em>Buffer</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getBuffer()
		 * @generated
		 */
		EClass BUFFER = eINSTANCE.getBuffer();

		/**
		 * The meta object literal for the '<em><b>Size</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute BUFFER__SIZE = eINSTANCE.getBuffer_Size();

		/**
		 * The meta object literal for the '<em><b>Childrens</b></em>' reference
		 * list feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference BUFFER__CHILDRENS = eINSTANCE.getBuffer_Childrens();

		/**
		 * The meta object literal for the '<em><b>Type Size</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute BUFFER__TYPE_SIZE = eINSTANCE.getBuffer_TypeSize();

		/**
		 * The meta object literal for the '<em><b>Merged Range</b></em>'
		 * attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute BUFFER__MERGED_RANGE = eINSTANCE.getBuffer_MergedRange();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SubBufferImpl
		 * <em>Sub Buffer</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.SubBufferImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getSubBuffer()
		 * @generated
		 */
		EClass SUB_BUFFER = eINSTANCE.getSubBuffer();

		/**
		 * The meta object literal for the '<em><b>Container</b></em>' reference
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference SUB_BUFFER__CONTAINER = eINSTANCE.getSubBuffer_Container();

		/**
		 * The meta object literal for the '<em><b>Offset</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute SUB_BUFFER__OFFSET = eINSTANCE.getSubBuffer_Offset();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.ConstantImpl
		 * <em>Constant</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.ConstantImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getConstant()
		 * @generated
		 */
		EClass CONSTANT = eINSTANCE.getConstant();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute CONSTANT__VALUE = eINSTANCE.getConstant_Value();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.FunctionCallImpl
		 * <em>Function Call</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.FunctionCallImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getFunctionCall()
		 * @generated
		 */
		EClass FUNCTION_CALL = eINSTANCE.getFunctionCall();

		/**
		 * The meta object literal for the '<em><b>Actor Name</b></em>'
		 * attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute FUNCTION_CALL__ACTOR_NAME = eINSTANCE
				.getFunctionCall_ActorName();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl
		 * <em>Communication</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCommunication()
		 * @generated
		 */
		EClass COMMUNICATION = eINSTANCE.getCommunication();

		/**
		 * The meta object literal for the '<em><b>Direction</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute COMMUNICATION__DIRECTION = eINSTANCE
				.getCommunication_Direction();

		/**
		 * The meta object literal for the '<em><b>Delimiter</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute COMMUNICATION__DELIMITER = eINSTANCE
				.getCommunication_Delimiter();

		/**
		 * The meta object literal for the '<em><b>Data</b></em>' reference
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference COMMUNICATION__DATA = eINSTANCE.getCommunication_Data();

		/**
		 * The meta object literal for the '<em><b>Send Start</b></em>'
		 * reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference COMMUNICATION__SEND_START = eINSTANCE
				.getCommunication_SendStart();

		/**
		 * The meta object literal for the '<em><b>Send End</b></em>' reference
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference COMMUNICATION__SEND_END = eINSTANCE
				.getCommunication_SendEnd();

		/**
		 * The meta object literal for the '<em><b>Receive Start</b></em>'
		 * reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference COMMUNICATION__RECEIVE_START = eINSTANCE
				.getCommunication_ReceiveStart();

		/**
		 * The meta object literal for the '<em><b>Receive End</b></em>'
		 * reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference COMMUNICATION__RECEIVE_END = eINSTANCE
				.getCommunication_ReceiveEnd();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute COMMUNICATION__ID = eINSTANCE.getCommunication_Id();

		/**
		 * The meta object literal for the '<em><b>Nodes</b></em>' containment
		 * reference list feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference COMMUNICATION__NODES = eINSTANCE.getCommunication_Nodes();

		/**
		 * The meta object literal for the '<em><b>Receive Release</b></em>'
		 * reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference COMMUNICATION__RECEIVE_RELEASE = eINSTANCE
				.getCommunication_ReceiveRelease();

		/**
		 * The meta object literal for the '<em><b>Send Reserve</b></em>'
		 * reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference COMMUNICATION__SEND_RESERVE = eINSTANCE
				.getCommunication_SendReserve();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CoreBlockImpl
		 * <em>Core Block</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CoreBlockImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCoreBlock()
		 * @generated
		 */
		EClass CORE_BLOCK = eINSTANCE.getCoreBlock();

		/**
		 * The meta object literal for the '<em><b>Loop Block</b></em>'
		 * reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference CORE_BLOCK__LOOP_BLOCK = eINSTANCE.getCoreBlock_LoopBlock();

		/**
		 * The meta object literal for the '<em><b>Init Block</b></em>'
		 * reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference CORE_BLOCK__INIT_BLOCK = eINSTANCE.getCoreBlock_InitBlock();

		/**
		 * The meta object literal for the '<em><b>Core Type</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute CORE_BLOCK__CORE_TYPE = eINSTANCE.getCoreBlock_CoreType();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.ActorBlockImpl
		 * <em>Actor Block</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.ActorBlockImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getActorBlock()
		 * @generated
		 */
		EClass ACTOR_BLOCK = eINSTANCE.getActorBlock();

		/**
		 * The meta object literal for the '<em><b>Loop Block</b></em>'
		 * reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference ACTOR_BLOCK__LOOP_BLOCK = eINSTANCE
				.getActorBlock_LoopBlock();

		/**
		 * The meta object literal for the '<em><b>Init Block</b></em>'
		 * reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference ACTOR_BLOCK__INIT_BLOCK = eINSTANCE
				.getActorBlock_InitBlock();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.LoopBlockImpl
		 * <em>Loop Block</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.LoopBlockImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getLoopBlock()
		 * @generated
		 */
		EClass LOOP_BLOCK = eINSTANCE.getLoopBlock();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.ActorCallImpl
		 * <em>Actor Call</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.ActorCallImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getActorCall()
		 * @generated
		 */
		EClass ACTOR_CALL = eINSTANCE.getActorCall();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CallBlockImpl
		 * <em>Call Block</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CallBlockImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCallBlock()
		 * @generated
		 */
		EClass CALL_BLOCK = eINSTANCE.getCallBlock();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SpecialCallImpl
		 * <em>Special Call</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.SpecialCallImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getSpecialCall()
		 * @generated
		 */
		EClass SPECIAL_CALL = eINSTANCE.getSpecialCall();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute SPECIAL_CALL__TYPE = eINSTANCE.getSpecialCall_Type();

		/**
		 * The meta object literal for the '<em><b>Input Buffers</b></em>'
		 * reference list feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference SPECIAL_CALL__INPUT_BUFFERS = eINSTANCE
				.getSpecialCall_InputBuffers();

		/**
		 * The meta object literal for the '<em><b>Output Buffers</b></em>'
		 * reference list feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference SPECIAL_CALL__OUTPUT_BUFFERS = eINSTANCE
				.getSpecialCall_OutputBuffers();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.FifoCallImpl
		 * <em>Fifo Call</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.FifoCallImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getFifoCall()
		 * @generated
		 */
		EClass FIFO_CALL = eINSTANCE.getFifoCall();

		/**
		 * The meta object literal for the '<em><b>Operation</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute FIFO_CALL__OPERATION = eINSTANCE.getFifoCall_Operation();

		/**
		 * The meta object literal for the '<em><b>Fifo Head</b></em>' reference
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference FIFO_CALL__FIFO_HEAD = eINSTANCE.getFifoCall_FifoHead();

		/**
		 * The meta object literal for the '<em><b>Fifo Tail</b></em>' reference
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference FIFO_CALL__FIFO_TAIL = eINSTANCE.getFifoCall_FifoTail();

		/**
		 * The meta object literal for the '<em><b>Head Buffer</b></em>'
		 * reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference FIFO_CALL__HEAD_BUFFER = eINSTANCE.getFifoCall_HeadBuffer();

		/**
		 * The meta object literal for the '<em><b>Body Buffer</b></em>'
		 * reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference FIFO_CALL__BODY_BUFFER = eINSTANCE.getFifoCall_BodyBuffer();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommentableImpl
		 * <em>Commentable</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CommentableImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCommentable()
		 * @generated
		 */
		EClass COMMENTABLE = eINSTANCE.getCommentable();

		/**
		 * The meta object literal for the '<em><b>Comment</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute COMMENTABLE__COMMENT = eINSTANCE.getCommentable_Comment();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationNodeImpl
		 * <em>Communication Node</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationNodeImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getCommunicationNode()
		 * @generated
		 */
		EClass COMMUNICATION_NODE = eINSTANCE.getCommunicationNode();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute COMMUNICATION_NODE__NAME = eINSTANCE
				.getCommunicationNode_Name();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute COMMUNICATION_NODE__TYPE = eINSTANCE
				.getCommunicationNode_Type();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SemaphoreImpl
		 * <em>Semaphore</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.SemaphoreImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getSemaphore()
		 * @generated
		 */
		EClass SEMAPHORE = eINSTANCE.getSemaphore();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SharedMemoryCommunicationImpl
		 * <em>Shared Memory Communication</em>}' class. <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.SharedMemoryCommunicationImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getSharedMemoryCommunication()
		 * @generated
		 */
		EClass SHARED_MEMORY_COMMUNICATION = eINSTANCE
				.getSharedMemoryCommunication();

		/**
		 * The meta object literal for the '<em><b>Semaphore</b></em>' reference
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference SHARED_MEMORY_COMMUNICATION__SEMAPHORE = eINSTANCE
				.getSharedMemoryCommunication_Semaphore();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.ConstantStringImpl
		 * <em>Constant String</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.ConstantStringImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getConstantString()
		 * @generated
		 */
		EClass CONSTANT_STRING = eINSTANCE.getConstantString();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute CONSTANT_STRING__VALUE = eINSTANCE.getConstantString_Value();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.NullBufferImpl
		 * <em>Null Buffer</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.NullBufferImpl
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getNullBuffer()
		 * @generated
		 */
		EClass NULL_BUFFER = eINSTANCE.getNullBuffer();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Direction
		 * <em>Direction</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.Direction
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getDirection()
		 * @generated
		 */
		EEnum DIRECTION = eINSTANCE.getDirection();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Delimiter
		 * <em>Delimiter</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.Delimiter
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getDelimiter()
		 * @generated
		 */
		EEnum DELIMITER = eINSTANCE.getDelimiter();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialType
		 * <em>Special Type</em>}' enum. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.SpecialType
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getSpecialType()
		 * @generated
		 */
		EEnum SPECIAL_TYPE = eINSTANCE.getSpecialType();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation
		 * <em>Fifo Operation</em>}' enum. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getFifoOperation()
		 * @generated
		 */
		EEnum FIFO_OPERATION = eINSTANCE.getFifoOperation();

		/**
		 * The meta object literal for the '
		 * {@link org.ietr.preesm.codegen.xtend.model.codegen.PortDirection
		 * <em>Port Direction</em>}' enum. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.PortDirection
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getPortDirection()
		 * @generated
		 */
		EEnum PORT_DIRECTION = eINSTANCE.getPortDirection();

		/**
		 * The meta object literal for the '<em>range</em>' data type. <!--
		 * begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see org.ietr.preesm.experiment.memory.Range
		 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getrange()
		 * @generated
		 */
		EDataType RANGE = eINSTANCE.getrange();

	}

} // CodegenPackage
