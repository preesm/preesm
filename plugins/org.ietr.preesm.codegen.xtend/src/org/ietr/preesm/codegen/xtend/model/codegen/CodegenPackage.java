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
	 * The feature id for the '<em><b>Declarations</b></em>' containment
	 * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int BLOCK__DECLARATIONS = CODE_ELT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Block</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int BLOCK_FEATURE_COUNT = CODE_ELT_FEATURE_COUNT + 2;

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
	 * The number of structural features of the '<em>Call</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CALL_FEATURE_COUNT = CODE_ELT_FEATURE_COUNT + 2;

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
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int VARIABLE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int VARIABLE__TYPE = 1;

	/**
	 * The number of structural features of the '<em>Variable</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int VARIABLE_FEATURE_COUNT = 2;

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
	 * The feature id for the '<em><b>Size</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int BUFFER__SIZE = VARIABLE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Buffer</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int BUFFER_FEATURE_COUNT = VARIABLE_FEATURE_COUNT + 1;

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
	 * The feature id for the '<em><b>Size</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int SUB_BUFFER__SIZE = BUFFER__SIZE;

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
	 * The number of structural features of the '<em>Constant</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONSTANT_FEATURE_COUNT = VARIABLE_FEATURE_COUNT + 0;

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
	 * The number of structural features of the '<em>Function Call</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int FUNCTION_CALL_FEATURE_COUNT = CALL_FEATURE_COUNT + 0;

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
	 * The number of structural features of the '<em>Communication</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int COMMUNICATION_FEATURE_COUNT = CALL_FEATURE_COUNT + 2;

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
	 * The feature id for the '<em><b>Declarations</b></em>' containment
	 * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CORE_BLOCK__DECLARATIONS = BLOCK__DECLARATIONS;

	/**
	 * The number of structural features of the '<em>Core Block</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CORE_BLOCK_FEATURE_COUNT = BLOCK_FEATURE_COUNT + 0;

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
	 * The feature id for the '<em><b>Declarations</b></em>' containment
	 * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTOR_BLOCK__DECLARATIONS = BLOCK__DECLARATIONS;

	/**
	 * The number of structural features of the '<em>Actor Block</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ACTOR_BLOCK_FEATURE_COUNT = BLOCK_FEATURE_COUNT + 0;

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
	 * The feature id for the '<em><b>Declarations</b></em>' containment
	 * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int LOOP_BLOCK__DECLARATIONS = BLOCK__DECLARATIONS;

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
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Direction
	 * <em>Direction</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Direction
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getDirection()
	 * @generated
	 */
	int DIRECTION = 12;

	/**
	 * The meta object id for the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Delimiter
	 * <em>Delimiter</em>}' enum. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Delimiter
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenPackageImpl#getDelimiter()
	 * @generated
	 */
	int DELIMITER = 13;

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
	 * Returns the meta object for the containment reference list '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Block#getDeclarations
	 * <em>Declarations</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '
	 *         <em>Declarations</em>'.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Block#getDeclarations()
	 * @see #getBlock()
	 * @generated
	 */
	EReference getBlock_Declarations();

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
		 * containment reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		EReference BLOCK__DECLARATIONS = eINSTANCE.getBlock_Declarations();

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

	}

} // CodegenPackage
