/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
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
package org.ietr.preesm.codegen.xtend.model.codegen.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.ActorCall;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer;
import org.ietr.preesm.codegen.xtend.model.codegen.BufferIterator;
import org.ietr.preesm.codegen.xtend.model.codegen.Call;
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.CodeElt;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.xtend.model.codegen.Commentable;
import org.ietr.preesm.codegen.xtend.model.codegen.Communication;
import org.ietr.preesm.codegen.xtend.model.codegen.CommunicationNode;
import org.ietr.preesm.codegen.xtend.model.codegen.Constant;
import org.ietr.preesm.codegen.xtend.model.codegen.ConstantString;
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.Delimiter;
import org.ietr.preesm.codegen.xtend.model.codegen.Direction;
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall;
import org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation;
import org.ietr.preesm.codegen.xtend.model.codegen.FiniteLoopBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall;
import org.ietr.preesm.codegen.xtend.model.codegen.IntVar;
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.NullBuffer;
import org.ietr.preesm.codegen.xtend.model.codegen.PapifyAction;
import org.ietr.preesm.codegen.xtend.model.codegen.PortDirection;
import org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialType;
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer;
import org.ietr.preesm.codegen.xtend.model.codegen.Variable;
import org.ietr.preesm.memory.script.Range;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class CodegenPackageImpl extends EPackageImpl implements CodegenPackage {

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass blockEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass codeEltEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass callEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass variableEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass bufferEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass subBufferEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass constantEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass functionCallEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass communicationEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass coreBlockEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass actorBlockEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass loopBlockEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass actorCallEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass callBlockEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass specialCallEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass fifoCallEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass commentableEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass communicationNodeEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass sharedMemoryCommunicationEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass constantStringEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass nullBufferEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass finiteLoopBlockEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass intVarEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass bufferIteratorEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private EClass papifyActionEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EEnum directionEEnum = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EEnum delimiterEEnum = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EEnum specialTypeEEnum = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EEnum fifoOperationEEnum = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EEnum portDirectionEEnum = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EDataType rangeEDataType = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private CodegenPackageImpl() {
    super(CodegenPackage.eNS_URI, CodegenFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>
   * This method is used to initialize {@link CodegenPackage#eINSTANCE} when that field is accessed. Clients should not
   * invoke it directly. Instead, they should simply access that field to obtain the package. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static CodegenPackage init() {
    if (CodegenPackageImpl.isInited) {
      return (CodegenPackage) EPackage.Registry.INSTANCE.getEPackage(CodegenPackage.eNS_URI);
    }

    // Obtain or create and register package
    final Object registeredCodegenPackage = EPackage.Registry.INSTANCE.get(CodegenPackage.eNS_URI);
    final CodegenPackageImpl theCodegenPackage = registeredCodegenPackage instanceof CodegenPackageImpl
        ? (CodegenPackageImpl) registeredCodegenPackage
        : new CodegenPackageImpl();

    CodegenPackageImpl.isInited = true;

    // Create package meta-data objects
    theCodegenPackage.createPackageContents();

    // Initialize created meta-data
    theCodegenPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theCodegenPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(CodegenPackage.eNS_URI, theCodegenPackage);
    return theCodegenPackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the block
   * @generated
   */
  @Override
  public EClass getBlock() {
    return this.blockEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the block code elts
   * @generated
   */
  @Override
  public EReference getBlock_CodeElts() {
    return (EReference) this.blockEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the block declarations
   * @generated
   */
  @Override
  public EReference getBlock_Declarations() {
    return (EReference) this.blockEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the block name
   * @generated
   */
  @Override
  public EAttribute getBlock_Name() {
    return (EAttribute) this.blockEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the block definitions
   * @generated
   */
  @Override
  public EReference getBlock_Definitions() {
    return (EReference) this.blockEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the code elt
   * @generated
   */
  @Override
  public EClass getCodeElt() {
    return this.codeEltEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the call
   * @generated
   */
  @Override
  public EClass getCall() {
    return this.callEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the call parameters
   * @generated
   */
  @Override
  public EReference getCall_Parameters() {
    return (EReference) this.callEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the call name
   * @generated
   */
  @Override
  public EAttribute getCall_Name() {
    return (EAttribute) this.callEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the call E reference 0
   * @generated
   */
  @Override
  public EReference getCall_EReference0() {
    return (EReference) this.callEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the call parameter directions
   * @generated
   */
  @Override
  public EAttribute getCall_ParameterDirections() {
    return (EAttribute) this.callEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the variable
   * @generated
   */
  @Override
  public EClass getVariable() {
    return this.variableEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the variable name
   * @generated
   */
  @Override
  public EAttribute getVariable_Name() {
    return (EAttribute) this.variableEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the variable type
   * @generated
   */
  @Override
  public EAttribute getVariable_Type() {
    return (EAttribute) this.variableEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the variable creator
   * @generated
   */
  @Override
  public EReference getVariable_Creator() {
    return (EReference) this.variableEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the variable users
   * @generated
   */
  @Override
  public EReference getVariable_Users() {
    return (EReference) this.variableEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the buffer
   * @generated
   */
  @Override
  public EClass getBuffer() {
    return this.bufferEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the buffer size
   * @generated
   */
  @Override
  public EAttribute getBuffer_Size() {
    return (EAttribute) this.bufferEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the buffer childrens
   * @generated
   */
  @Override
  public EReference getBuffer_Childrens() {
    return (EReference) this.bufferEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the buffer type size
   * @generated
   */
  @Override
  public EAttribute getBuffer_TypeSize() {
    return (EAttribute) this.bufferEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the buffer merged range
   * @generated
   */
  @Override
  public EAttribute getBuffer_MergedRange() {
    return (EAttribute) this.bufferEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the buffer local
   * @generated
   */
  @Override
  public EAttribute getBuffer_Local() {
    return (EAttribute) this.bufferEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the sub buffer
   * @generated
   */
  @Override
  public EClass getSubBuffer() {
    return this.subBufferEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the sub buffer container
   * @generated
   */
  @Override
  public EReference getSubBuffer_Container() {
    return (EReference) this.subBufferEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the sub buffer offset
   * @generated
   */
  @Override
  public EAttribute getSubBuffer_Offset() {
    return (EAttribute) this.subBufferEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the constant
   * @generated
   */
  @Override
  public EClass getConstant() {
    return this.constantEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the constant value
   * @generated
   */
  @Override
  public EAttribute getConstant_Value() {
    return (EAttribute) this.constantEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the function call
   * @generated
   */
  @Override
  public EClass getFunctionCall() {
    return this.functionCallEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the function call actor name
   * @generated
   */
  @Override
  public EAttribute getFunctionCall_ActorName() {
    return (EAttribute) this.functionCallEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication
   * @generated
   */
  @Override
  public EClass getCommunication() {
    return this.communicationEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication direction
   * @generated
   */
  @Override
  public EAttribute getCommunication_Direction() {
    return (EAttribute) this.communicationEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication delimiter
   * @generated
   */
  @Override
  public EAttribute getCommunication_Delimiter() {
    return (EAttribute) this.communicationEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication data
   * @generated
   */
  @Override
  public EReference getCommunication_Data() {
    return (EReference) this.communicationEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication send start
   * @generated
   */
  @Override
  public EReference getCommunication_SendStart() {
    return (EReference) this.communicationEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication send end
   * @generated
   */
  @Override
  public EReference getCommunication_SendEnd() {
    return (EReference) this.communicationEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication receive start
   * @generated
   */
  @Override
  public EReference getCommunication_ReceiveStart() {
    return (EReference) this.communicationEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication receive end
   * @generated
   */
  @Override
  public EReference getCommunication_ReceiveEnd() {
    return (EReference) this.communicationEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication id
   * @generated
   */
  @Override
  public EAttribute getCommunication_Id() {
    return (EAttribute) this.communicationEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication nodes
   * @generated
   */
  @Override
  public EReference getCommunication_Nodes() {
    return (EReference) this.communicationEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication receive release
   * @generated
   */
  @Override
  public EReference getCommunication_ReceiveRelease() {
    return (EReference) this.communicationEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication send reserve
   * @generated
   */
  @Override
  public EReference getCommunication_SendReserve() {
    return (EReference) this.communicationEClass.getEStructuralFeatures().get(10);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public EAttribute getCommunication_Redundant() {
    return (EAttribute) this.communicationEClass.getEStructuralFeatures().get(11);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the core block
   * @generated
   */
  @Override
  public EClass getCoreBlock() {
    return this.coreBlockEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the core block loop block
   * @generated
   */
  @Override
  public EReference getCoreBlock_LoopBlock() {
    return (EReference) this.coreBlockEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the core block init block
   * @generated
   */
  @Override
  public EReference getCoreBlock_InitBlock() {
    return (EReference) this.coreBlockEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the core block core type
   * @generated
   */
  @Override
  public EAttribute getCoreBlock_CoreType() {
    return (EAttribute) this.coreBlockEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public EAttribute getCoreBlock_CoreID() {
    return (EAttribute) this.coreBlockEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the actor block
   * @generated
   */
  @Override
  public EClass getActorBlock() {
    return this.actorBlockEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the actor block loop block
   * @generated
   */
  @Override
  public EReference getActorBlock_LoopBlock() {
    return (EReference) this.actorBlockEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the actor block init block
   * @generated
   */
  @Override
  public EReference getActorBlock_InitBlock() {
    return (EReference) this.actorBlockEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the loop block
   * @generated
   */
  @Override
  public EClass getLoopBlock() {
    return this.loopBlockEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the actor call
   * @generated
   */
  @Override
  public EClass getActorCall() {
    return this.actorCallEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the call block
   * @generated
   */
  @Override
  public EClass getCallBlock() {
    return this.callBlockEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the special call
   * @generated
   */
  @Override
  public EClass getSpecialCall() {
    return this.specialCallEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the special call type
   * @generated
   */
  @Override
  public EAttribute getSpecialCall_Type() {
    return (EAttribute) this.specialCallEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the special call input buffers
   * @generated
   */
  @Override
  public EReference getSpecialCall_InputBuffers() {
    return (EReference) this.specialCallEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the special call output buffers
   * @generated
   */
  @Override
  public EReference getSpecialCall_OutputBuffers() {
    return (EReference) this.specialCallEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifo call
   * @generated
   */
  @Override
  public EClass getFifoCall() {
    return this.fifoCallEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifo call operation
   * @generated
   */
  @Override
  public EAttribute getFifoCall_Operation() {
    return (EAttribute) this.fifoCallEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifo call fifo head
   * @generated
   */
  @Override
  public EReference getFifoCall_FifoHead() {
    return (EReference) this.fifoCallEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifo call fifo tail
   * @generated
   */
  @Override
  public EReference getFifoCall_FifoTail() {
    return (EReference) this.fifoCallEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifo call head buffer
   * @generated
   */
  @Override
  public EReference getFifoCall_HeadBuffer() {
    return (EReference) this.fifoCallEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifo call body buffer
   * @generated
   */
  @Override
  public EReference getFifoCall_BodyBuffer() {
    return (EReference) this.fifoCallEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the commentable
   * @generated
   */
  @Override
  public EClass getCommentable() {
    return this.commentableEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the commentable comment
   * @generated
   */
  @Override
  public EAttribute getCommentable_Comment() {
    return (EAttribute) this.commentableEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication node
   * @generated
   */
  @Override
  public EClass getCommunicationNode() {
    return this.communicationNodeEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication node name
   * @generated
   */
  @Override
  public EAttribute getCommunicationNode_Name() {
    return (EAttribute) this.communicationNodeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication node type
   * @generated
   */
  @Override
  public EAttribute getCommunicationNode_Type() {
    return (EAttribute) this.communicationNodeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the shared memory communication
   * @generated
   */
  @Override
  public EClass getSharedMemoryCommunication() {
    return this.sharedMemoryCommunicationEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the constant string
   * @generated
   */
  @Override
  public EClass getConstantString() {
    return this.constantStringEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the constant string value
   * @generated
   */
  @Override
  public EAttribute getConstantString_Value() {
    return (EAttribute) this.constantStringEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the null buffer
   * @generated
   */
  @Override
  public EClass getNullBuffer() {
    return this.nullBufferEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public EClass getFiniteLoopBlock() {
    return this.finiteLoopBlockEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public EAttribute getFiniteLoopBlock_NbIter() {
    return (EAttribute) this.finiteLoopBlockEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public EReference getFiniteLoopBlock_Iter() {
    return (EReference) this.finiteLoopBlockEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public EReference getFiniteLoopBlock_InBuffers() {
    return (EReference) this.finiteLoopBlockEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public EReference getFiniteLoopBlock_OutBuffers() {
    return (EReference) this.finiteLoopBlockEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public EClass getIntVar() {
    return this.intVarEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public EClass getBufferIterator() {
    return this.bufferIteratorEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public EAttribute getBufferIterator_IterSize() {
    return (EAttribute) this.bufferIteratorEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public EReference getBufferIterator_Iter() {
    return (EReference) this.bufferIteratorEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public EClass getPapifyAction() {
    return this.papifyActionEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the direction
   * @generated
   */
  @Override
  public EEnum getDirection() {
    return this.directionEEnum;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the delimiter
   * @generated
   */
  @Override
  public EEnum getDelimiter() {
    return this.delimiterEEnum;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the special type
   * @generated
   */
  @Override
  public EEnum getSpecialType() {
    return this.specialTypeEEnum;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifo operation
   * @generated
   */
  @Override
  public EEnum getFifoOperation() {
    return this.fifoOperationEEnum;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the port direction
   * @generated
   */
  @Override
  public EEnum getPortDirection() {
    return this.portDirectionEEnum;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the range
   * @generated
   */
  @Override
  public EDataType getrange() {
    return this.rangeEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the codegen factory
   * @generated
   */
  @Override
  public CodegenFactory getCodegenFactory() {
    return (CodegenFactory) getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package. This method is guarded to have no affect on any invocation but its
   * first. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void createPackageContents() {
    if (this.isCreated) {
      return;
    }
    this.isCreated = true;

    // Create classes and their features
    this.blockEClass = createEClass(CodegenPackage.BLOCK);
    createEReference(this.blockEClass, CodegenPackage.BLOCK__CODE_ELTS);
    createEReference(this.blockEClass, CodegenPackage.BLOCK__DECLARATIONS);
    createEAttribute(this.blockEClass, CodegenPackage.BLOCK__NAME);
    createEReference(this.blockEClass, CodegenPackage.BLOCK__DEFINITIONS);

    this.codeEltEClass = createEClass(CodegenPackage.CODE_ELT);

    this.callEClass = createEClass(CodegenPackage.CALL);
    createEReference(this.callEClass, CodegenPackage.CALL__PARAMETERS);
    createEAttribute(this.callEClass, CodegenPackage.CALL__NAME);
    createEReference(this.callEClass, CodegenPackage.CALL__EREFERENCE0);
    createEAttribute(this.callEClass, CodegenPackage.CALL__PARAMETER_DIRECTIONS);

    this.variableEClass = createEClass(CodegenPackage.VARIABLE);
    createEAttribute(this.variableEClass, CodegenPackage.VARIABLE__NAME);
    createEAttribute(this.variableEClass, CodegenPackage.VARIABLE__TYPE);
    createEReference(this.variableEClass, CodegenPackage.VARIABLE__CREATOR);
    createEReference(this.variableEClass, CodegenPackage.VARIABLE__USERS);

    this.bufferEClass = createEClass(CodegenPackage.BUFFER);
    createEAttribute(this.bufferEClass, CodegenPackage.BUFFER__SIZE);
    createEReference(this.bufferEClass, CodegenPackage.BUFFER__CHILDRENS);
    createEAttribute(this.bufferEClass, CodegenPackage.BUFFER__TYPE_SIZE);
    createEAttribute(this.bufferEClass, CodegenPackage.BUFFER__MERGED_RANGE);
    createEAttribute(this.bufferEClass, CodegenPackage.BUFFER__LOCAL);

    this.subBufferEClass = createEClass(CodegenPackage.SUB_BUFFER);
    createEReference(this.subBufferEClass, CodegenPackage.SUB_BUFFER__CONTAINER);
    createEAttribute(this.subBufferEClass, CodegenPackage.SUB_BUFFER__OFFSET);

    this.constantEClass = createEClass(CodegenPackage.CONSTANT);
    createEAttribute(this.constantEClass, CodegenPackage.CONSTANT__VALUE);

    this.functionCallEClass = createEClass(CodegenPackage.FUNCTION_CALL);
    createEAttribute(this.functionCallEClass, CodegenPackage.FUNCTION_CALL__ACTOR_NAME);

    this.communicationEClass = createEClass(CodegenPackage.COMMUNICATION);
    createEAttribute(this.communicationEClass, CodegenPackage.COMMUNICATION__DIRECTION);
    createEAttribute(this.communicationEClass, CodegenPackage.COMMUNICATION__DELIMITER);
    createEReference(this.communicationEClass, CodegenPackage.COMMUNICATION__DATA);
    createEReference(this.communicationEClass, CodegenPackage.COMMUNICATION__SEND_START);
    createEReference(this.communicationEClass, CodegenPackage.COMMUNICATION__SEND_END);
    createEReference(this.communicationEClass, CodegenPackage.COMMUNICATION__RECEIVE_START);
    createEReference(this.communicationEClass, CodegenPackage.COMMUNICATION__RECEIVE_END);
    createEAttribute(this.communicationEClass, CodegenPackage.COMMUNICATION__ID);
    createEReference(this.communicationEClass, CodegenPackage.COMMUNICATION__NODES);
    createEReference(this.communicationEClass, CodegenPackage.COMMUNICATION__RECEIVE_RELEASE);
    createEReference(this.communicationEClass, CodegenPackage.COMMUNICATION__SEND_RESERVE);
    createEAttribute(this.communicationEClass, CodegenPackage.COMMUNICATION__REDUNDANT);

    this.coreBlockEClass = createEClass(CodegenPackage.CORE_BLOCK);
    createEReference(this.coreBlockEClass, CodegenPackage.CORE_BLOCK__LOOP_BLOCK);
    createEReference(this.coreBlockEClass, CodegenPackage.CORE_BLOCK__INIT_BLOCK);
    createEAttribute(this.coreBlockEClass, CodegenPackage.CORE_BLOCK__CORE_TYPE);
    createEAttribute(this.coreBlockEClass, CodegenPackage.CORE_BLOCK__CORE_ID);

    this.actorBlockEClass = createEClass(CodegenPackage.ACTOR_BLOCK);
    createEReference(this.actorBlockEClass, CodegenPackage.ACTOR_BLOCK__LOOP_BLOCK);
    createEReference(this.actorBlockEClass, CodegenPackage.ACTOR_BLOCK__INIT_BLOCK);

    this.loopBlockEClass = createEClass(CodegenPackage.LOOP_BLOCK);

    this.actorCallEClass = createEClass(CodegenPackage.ACTOR_CALL);

    this.callBlockEClass = createEClass(CodegenPackage.CALL_BLOCK);

    this.specialCallEClass = createEClass(CodegenPackage.SPECIAL_CALL);
    createEAttribute(this.specialCallEClass, CodegenPackage.SPECIAL_CALL__TYPE);
    createEReference(this.specialCallEClass, CodegenPackage.SPECIAL_CALL__INPUT_BUFFERS);
    createEReference(this.specialCallEClass, CodegenPackage.SPECIAL_CALL__OUTPUT_BUFFERS);

    this.fifoCallEClass = createEClass(CodegenPackage.FIFO_CALL);
    createEAttribute(this.fifoCallEClass, CodegenPackage.FIFO_CALL__OPERATION);
    createEReference(this.fifoCallEClass, CodegenPackage.FIFO_CALL__FIFO_HEAD);
    createEReference(this.fifoCallEClass, CodegenPackage.FIFO_CALL__FIFO_TAIL);
    createEReference(this.fifoCallEClass, CodegenPackage.FIFO_CALL__HEAD_BUFFER);
    createEReference(this.fifoCallEClass, CodegenPackage.FIFO_CALL__BODY_BUFFER);

    this.commentableEClass = createEClass(CodegenPackage.COMMENTABLE);
    createEAttribute(this.commentableEClass, CodegenPackage.COMMENTABLE__COMMENT);

    this.communicationNodeEClass = createEClass(CodegenPackage.COMMUNICATION_NODE);
    createEAttribute(this.communicationNodeEClass, CodegenPackage.COMMUNICATION_NODE__NAME);
    createEAttribute(this.communicationNodeEClass, CodegenPackage.COMMUNICATION_NODE__TYPE);

    this.sharedMemoryCommunicationEClass = createEClass(CodegenPackage.SHARED_MEMORY_COMMUNICATION);

    this.constantStringEClass = createEClass(CodegenPackage.CONSTANT_STRING);
    createEAttribute(this.constantStringEClass, CodegenPackage.CONSTANT_STRING__VALUE);

    this.nullBufferEClass = createEClass(CodegenPackage.NULL_BUFFER);

    this.finiteLoopBlockEClass = createEClass(CodegenPackage.FINITE_LOOP_BLOCK);
    createEAttribute(this.finiteLoopBlockEClass, CodegenPackage.FINITE_LOOP_BLOCK__NB_ITER);
    createEReference(this.finiteLoopBlockEClass, CodegenPackage.FINITE_LOOP_BLOCK__ITER);
    createEReference(this.finiteLoopBlockEClass, CodegenPackage.FINITE_LOOP_BLOCK__IN_BUFFERS);
    createEReference(this.finiteLoopBlockEClass, CodegenPackage.FINITE_LOOP_BLOCK__OUT_BUFFERS);

    this.intVarEClass = createEClass(CodegenPackage.INT_VAR);

    this.bufferIteratorEClass = createEClass(CodegenPackage.BUFFER_ITERATOR);
    createEAttribute(this.bufferIteratorEClass, CodegenPackage.BUFFER_ITERATOR__ITER_SIZE);
    createEReference(this.bufferIteratorEClass, CodegenPackage.BUFFER_ITERATOR__ITER);

    this.papifyActionEClass = createEClass(CodegenPackage.PAPIFY_ACTION);

    // Create enums
    this.directionEEnum = createEEnum(CodegenPackage.DIRECTION);
    this.delimiterEEnum = createEEnum(CodegenPackage.DELIMITER);
    this.specialTypeEEnum = createEEnum(CodegenPackage.SPECIAL_TYPE);
    this.fifoOperationEEnum = createEEnum(CodegenPackage.FIFO_OPERATION);
    this.portDirectionEEnum = createEEnum(CodegenPackage.PORT_DIRECTION);

    // Create data types
    this.rangeEDataType = createEDataType(CodegenPackage.RANGE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model. This method is guarded to have no affect on any
   * invocation but its first. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void initializePackageContents() {
    if (this.isInitialized) {
      return;
    }
    this.isInitialized = true;

    // Initialize package
    setName(CodegenPackage.eNAME);
    setNsPrefix(CodegenPackage.eNS_PREFIX);
    setNsURI(CodegenPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    this.blockEClass.getESuperTypes().add(getCodeElt());
    this.codeEltEClass.getESuperTypes().add(getCommentable());
    this.callEClass.getESuperTypes().add(getCodeElt());
    this.variableEClass.getESuperTypes().add(getCommentable());
    this.bufferEClass.getESuperTypes().add(getVariable());
    this.subBufferEClass.getESuperTypes().add(getBuffer());
    this.constantEClass.getESuperTypes().add(getVariable());
    this.functionCallEClass.getESuperTypes().add(getCall());
    this.communicationEClass.getESuperTypes().add(getCall());
    this.coreBlockEClass.getESuperTypes().add(getBlock());
    this.actorBlockEClass.getESuperTypes().add(getBlock());
    this.loopBlockEClass.getESuperTypes().add(getBlock());
    this.actorCallEClass.getESuperTypes().add(getCall());
    this.callBlockEClass.getESuperTypes().add(getBlock());
    this.specialCallEClass.getESuperTypes().add(getCall());
    this.fifoCallEClass.getESuperTypes().add(getCall());
    this.communicationNodeEClass.getESuperTypes().add(getCommentable());
    this.sharedMemoryCommunicationEClass.getESuperTypes().add(getCommunication());
    this.constantStringEClass.getESuperTypes().add(getVariable());
    this.nullBufferEClass.getESuperTypes().add(getSubBuffer());
    this.finiteLoopBlockEClass.getESuperTypes().add(getLoopBlock());
    this.intVarEClass.getESuperTypes().add(getVariable());
    this.bufferIteratorEClass.getESuperTypes().add(getSubBuffer());
    this.papifyActionEClass.getESuperTypes().add(getVariable());

    // Initialize classes and features; add operations and parameters
    initEClass(this.blockEClass, Block.class, "Block", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getBlock_CodeElts(), getCodeElt(), null, "codeElts", null, 0, -1, Block.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, !EPackageImpl.IS_CHANGEABLE, EPackageImpl.IS_COMPOSITE,
        !EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEReference(getBlock_Declarations(), getVariable(), getVariable_Users(), "declarations", null, 0, -1,
        Block.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, !EPackageImpl.IS_CHANGEABLE,
        !EPackageImpl.IS_COMPOSITE, EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEAttribute(getBlock_Name(), this.ecorePackage.getEString(), "name", null, 0, 1, Block.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getBlock_Definitions(), getVariable(), getVariable_Creator(), "definitions", null, 0, -1,
        Block.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE,
        EPackageImpl.IS_COMPOSITE, !EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    initEClass(this.codeEltEClass, CodeElt.class, "CodeElt", EPackageImpl.IS_ABSTRACT, EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    initEClass(this.callEClass, Call.class, "Call", EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCall_Parameters(), getVariable(), null, "parameters", null, 0, -1, Call.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, !EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE,
        EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEAttribute(getCall_Name(), this.ecorePackage.getEString(), "name", null, 1, 1, Call.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getCall_EReference0(), getCall(), null, "EReference0", null, 0, 1, Call.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE,
        EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEAttribute(getCall_ParameterDirections(), getPortDirection(), "parameterDirections", null, 0, -1, Call.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        !EPackageImpl.IS_ID, !EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    EOperation op = addEOperation(this.callEClass, null, "addParameter", 0, 1, EPackageImpl.IS_UNIQUE,
        EPackageImpl.IS_ORDERED);
    addEParameter(op, getVariable(), "variable", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, getPortDirection(), "direction", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.callEClass, null, "removeParameter", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, getVariable(), "variable", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    initEClass(this.variableEClass, Variable.class, "Variable", EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getVariable_Name(), this.ecorePackage.getEString(), "name", null, 1, 1, Variable.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEAttribute(getVariable_Type(), this.ecorePackage.getEString(), "type", null, 1, 1, Variable.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getVariable_Creator(), getBlock(), getBlock_Definitions(), "creator", null, 0, 1, Variable.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE,
        !EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEReference(getVariable_Users(), getBlock(), getBlock_Declarations(), "users", null, 1, -1, Variable.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, !EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE,
        EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);

    op = addEOperation(this.variableEClass, null, "reaffectCreator", 0, 1, EPackageImpl.IS_UNIQUE,
        EPackageImpl.IS_ORDERED);
    addEParameter(op, getBlock(), "creator", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    initEClass(this.bufferEClass, Buffer.class, "Buffer", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getBuffer_Size(), this.ecorePackage.getEInt(), "size", null, 1, 1, Buffer.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getBuffer_Childrens(), getSubBuffer(), getSubBuffer_Container(), "childrens", null, 0, -1,
        Buffer.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE,
        !EPackageImpl.IS_COMPOSITE, EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEAttribute(getBuffer_TypeSize(), this.ecorePackage.getEInt(), "typeSize", null, 1, 1, Buffer.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    final EGenericType g1 = createEGenericType(this.ecorePackage.getEEList());
    final EGenericType g2 = createEGenericType(getrange());
    g1.getETypeArguments().add(g2);
    initEAttribute(getBuffer_MergedRange(), g1, "mergedRange", null, 0, 1, Buffer.class, EPackageImpl.IS_TRANSIENT,
        !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEAttribute(getBuffer_Local(), this.ecorePackage.getEBoolean(), "local", "false", 0, 1, Buffer.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    initEClass(this.subBufferEClass, SubBuffer.class, "SubBuffer", !EPackageImpl.IS_ABSTRACT,
        !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSubBuffer_Container(), getBuffer(), getBuffer_Childrens(), "container", null, 1, 1,
        SubBuffer.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE,
        !EPackageImpl.IS_COMPOSITE, EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEAttribute(getSubBuffer_Offset(), this.ecorePackage.getEInt(), "offset", null, 1, 1, SubBuffer.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.subBufferEClass, null, "reaffectContainer", 0, 1, EPackageImpl.IS_UNIQUE,
        EPackageImpl.IS_ORDERED);
    addEParameter(op, getBuffer(), "newContainer", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    initEClass(this.constantEClass, Constant.class, "Constant", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getConstant_Value(), this.ecorePackage.getELong(), "value", null, 1, 1, Constant.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    initEClass(this.functionCallEClass, FunctionCall.class, "FunctionCall", !EPackageImpl.IS_ABSTRACT,
        !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getFunctionCall_ActorName(), this.ecorePackage.getEString(), "actorName", null, 1, 1,
        FunctionCall.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE,
        !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);

    initEClass(this.communicationEClass, Communication.class, "Communication", !EPackageImpl.IS_ABSTRACT,
        !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getCommunication_Direction(), getDirection(), "direction", null, 1, 1, Communication.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEAttribute(getCommunication_Delimiter(), getDelimiter(), "delimiter", null, 1, 1, Communication.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getCommunication_Data(), getBuffer(), null, "data", null, 1, 1, Communication.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE,
        EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEReference(getCommunication_SendStart(), getCommunication(), null, "sendStart", null, 0, 1, Communication.class,
        EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE,
        EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEReference(getCommunication_SendEnd(), getCommunication(), null, "sendEnd", null, 0, 1, Communication.class,
        EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE,
        EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEReference(getCommunication_ReceiveStart(), getCommunication(), null, "receiveStart", null, 0, 1,
        Communication.class, EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE,
        !EPackageImpl.IS_COMPOSITE, EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getCommunication_ReceiveEnd(), getCommunication(), null, "receiveEnd", null, 0, 1,
        Communication.class, EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE,
        !EPackageImpl.IS_COMPOSITE, EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEAttribute(getCommunication_Id(), this.ecorePackage.getEInt(), "id", null, 1, 1, Communication.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getCommunication_Nodes(), getCommunicationNode(), null, "nodes", null, 1, -1, Communication.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, EPackageImpl.IS_COMPOSITE,
        !EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEReference(getCommunication_ReceiveRelease(), getCommunication(), null, "receiveRelease", null, 0, 1,
        Communication.class, EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE,
        !EPackageImpl.IS_COMPOSITE, EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getCommunication_SendReserve(), getCommunication(), null, "sendReserve", null, 0, 1,
        Communication.class, EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE,
        !EPackageImpl.IS_COMPOSITE, EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEAttribute(getCommunication_Redundant(), this.ecorePackage.getEBoolean(), "redundant", "false", 0, 1,
        Communication.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE,
        !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);

    addEOperation(this.communicationEClass, getCoreBlock(), "getCoreContainer", 1, 1, EPackageImpl.IS_UNIQUE,
        EPackageImpl.IS_ORDERED);

    initEClass(this.coreBlockEClass, CoreBlock.class, "CoreBlock", !EPackageImpl.IS_ABSTRACT,
        !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCoreBlock_LoopBlock(), getLoopBlock(), null, "loopBlock", null, 1, 1, CoreBlock.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE,
        EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEReference(getCoreBlock_InitBlock(), getCallBlock(), null, "initBlock", null, 1, 1, CoreBlock.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE,
        EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEAttribute(getCoreBlock_CoreType(), this.ecorePackage.getEString(), "coreType", null, 1, 1, CoreBlock.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEAttribute(getCoreBlock_CoreID(), this.ecorePackage.getEInt(), "coreID", null, 1, 1, CoreBlock.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_ID, !EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, !EPackageImpl.IS_ORDERED);

    initEClass(this.actorBlockEClass, ActorBlock.class, "ActorBlock", !EPackageImpl.IS_ABSTRACT,
        !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getActorBlock_LoopBlock(), getLoopBlock(), null, "loopBlock", null, 1, 1, ActorBlock.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE,
        EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEReference(getActorBlock_InitBlock(), getCallBlock(), null, "initBlock", null, 1, 1, ActorBlock.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE,
        EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);

    initEClass(this.loopBlockEClass, LoopBlock.class, "LoopBlock", !EPackageImpl.IS_ABSTRACT,
        !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    initEClass(this.actorCallEClass, ActorCall.class, "ActorCall", !EPackageImpl.IS_ABSTRACT,
        !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    initEClass(this.callBlockEClass, CallBlock.class, "CallBlock", !EPackageImpl.IS_ABSTRACT,
        !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    initEClass(this.specialCallEClass, SpecialCall.class, "SpecialCall", !EPackageImpl.IS_ABSTRACT,
        !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSpecialCall_Type(), getSpecialType(), "type", null, 1, 1, SpecialCall.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getSpecialCall_InputBuffers(), getBuffer(), null, "inputBuffers", null, 1, -1, SpecialCall.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, !EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE,
        EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEReference(getSpecialCall_OutputBuffers(), getBuffer(), null, "outputBuffers", null, 1, -1, SpecialCall.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, !EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE,
        !EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);

    addEOperation(this.specialCallEClass, this.ecorePackage.getEBoolean(), "isFork", 0, 1, EPackageImpl.IS_UNIQUE,
        EPackageImpl.IS_ORDERED);

    addEOperation(this.specialCallEClass, this.ecorePackage.getEBoolean(), "isJoin", 0, 1, EPackageImpl.IS_UNIQUE,
        EPackageImpl.IS_ORDERED);

    addEOperation(this.specialCallEClass, this.ecorePackage.getEBoolean(), "isBroadcast", 0, 1, EPackageImpl.IS_UNIQUE,
        EPackageImpl.IS_ORDERED);

    addEOperation(this.specialCallEClass, this.ecorePackage.getEBoolean(), "isRoundBuffer", 0, 1,
        EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.specialCallEClass, null, "addInputBuffer", 0, 1, EPackageImpl.IS_UNIQUE,
        EPackageImpl.IS_ORDERED);
    addEParameter(op, getBuffer(), "buffer", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.specialCallEClass, null, "addOutputBuffer", 0, 1, EPackageImpl.IS_UNIQUE,
        EPackageImpl.IS_ORDERED);
    addEParameter(op, getBuffer(), "buffer", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.specialCallEClass, null, "removeInputBuffer", 0, 1, EPackageImpl.IS_UNIQUE,
        EPackageImpl.IS_ORDERED);
    addEParameter(op, getBuffer(), "buffer", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.specialCallEClass, null, "removeOutputBuffer", 0, 1, EPackageImpl.IS_UNIQUE,
        EPackageImpl.IS_ORDERED);
    addEParameter(op, getBuffer(), "buffer", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    initEClass(this.fifoCallEClass, FifoCall.class, "FifoCall", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getFifoCall_Operation(), getFifoOperation(), "operation", null, 1, 1, FifoCall.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getFifoCall_FifoHead(), getFifoCall(), null, "fifoHead", null, 0, 1, FifoCall.class,
        EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE,
        EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEReference(getFifoCall_FifoTail(), getFifoCall(), null, "fifoTail", null, 0, 1, FifoCall.class,
        EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE,
        EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEReference(getFifoCall_HeadBuffer(), getBuffer(), null, "headBuffer", null, 0, 1, FifoCall.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE,
        EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEReference(getFifoCall_BodyBuffer(), getBuffer(), null, "bodyBuffer", null, 0, 1, FifoCall.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE,
        EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);

    initEClass(this.commentableEClass, Commentable.class, "Commentable", EPackageImpl.IS_ABSTRACT,
        !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getCommentable_Comment(), this.ecorePackage.getEString(), "comment", null, 0, 1, Commentable.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    initEClass(this.communicationNodeEClass, CommunicationNode.class, "CommunicationNode", !EPackageImpl.IS_ABSTRACT,
        !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getCommunicationNode_Name(), this.ecorePackage.getEString(), "name", null, 1, 1,
        CommunicationNode.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE,
        !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEAttribute(getCommunicationNode_Type(), this.ecorePackage.getEString(), "type", null, 1, 1,
        CommunicationNode.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE,
        !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);

    initEClass(this.sharedMemoryCommunicationEClass, SharedMemoryCommunication.class, "SharedMemoryCommunication",
        !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    initEClass(this.constantStringEClass, ConstantString.class, "ConstantString", !EPackageImpl.IS_ABSTRACT,
        !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getConstantString_Value(), this.ecorePackage.getEString(), "value", null, 1, 1, ConstantString.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    initEClass(this.nullBufferEClass, NullBuffer.class, "NullBuffer", !EPackageImpl.IS_ABSTRACT,
        !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    initEClass(this.finiteLoopBlockEClass, FiniteLoopBlock.class, "FiniteLoopBlock", !EPackageImpl.IS_ABSTRACT,
        !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getFiniteLoopBlock_NbIter(), this.ecorePackage.getEInt(), "nbIter", null, 1, 1,
        FiniteLoopBlock.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE,
        !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEReference(getFiniteLoopBlock_Iter(), getIntVar(), null, "iter", null, 0, 1, FiniteLoopBlock.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE,
        EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEReference(getFiniteLoopBlock_InBuffers(), getBufferIterator(), null, "inBuffers", null, 0, -1,
        FiniteLoopBlock.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE,
        !EPackageImpl.IS_COMPOSITE, EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getFiniteLoopBlock_OutBuffers(), getBufferIterator(), null, "outBuffers", null, 0, -1,
        FiniteLoopBlock.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE,
        !EPackageImpl.IS_COMPOSITE, EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    initEClass(this.intVarEClass, IntVar.class, "IntVar", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    initEClass(this.bufferIteratorEClass, BufferIterator.class, "BufferIterator", !EPackageImpl.IS_ABSTRACT,
        !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getBufferIterator_IterSize(), this.ecorePackage.getEInt(), "iterSize", null, 0, 1,
        BufferIterator.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE,
        !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEReference(getBufferIterator_Iter(), getIntVar(), null, "iter", null, 0, 1, BufferIterator.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE,
        EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);

    initEClass(this.papifyActionEClass, PapifyAction.class, "PapifyAction", !EPackageImpl.IS_ABSTRACT,
        !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    // Initialize enums and add enum literals
    initEEnum(this.directionEEnum, Direction.class, "Direction");
    addEEnumLiteral(this.directionEEnum, Direction.SEND);
    addEEnumLiteral(this.directionEEnum, Direction.RECEIVE);

    initEEnum(this.delimiterEEnum, Delimiter.class, "Delimiter");
    addEEnumLiteral(this.delimiterEEnum, Delimiter.START);
    addEEnumLiteral(this.delimiterEEnum, Delimiter.END);

    initEEnum(this.specialTypeEEnum, SpecialType.class, "SpecialType");
    addEEnumLiteral(this.specialTypeEEnum, SpecialType.FORK);
    addEEnumLiteral(this.specialTypeEEnum, SpecialType.JOIN);
    addEEnumLiteral(this.specialTypeEEnum, SpecialType.BROADCAST);
    addEEnumLiteral(this.specialTypeEEnum, SpecialType.ROUND_BUFFER);

    initEEnum(this.fifoOperationEEnum, FifoOperation.class, "FifoOperation");
    addEEnumLiteral(this.fifoOperationEEnum, FifoOperation.PUSH);
    addEEnumLiteral(this.fifoOperationEEnum, FifoOperation.POP);
    addEEnumLiteral(this.fifoOperationEEnum, FifoOperation.INIT);

    initEEnum(this.portDirectionEEnum, PortDirection.class, "PortDirection");
    addEEnumLiteral(this.portDirectionEEnum, PortDirection.INPUT);
    addEEnumLiteral(this.portDirectionEEnum, PortDirection.OUTPUT);
    addEEnumLiteral(this.portDirectionEEnum, PortDirection.NONE);

    // Initialize data types
    initEDataType(this.rangeEDataType, Range.class, "range", EPackageImpl.IS_SERIALIZABLE,
        !EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(CodegenPackage.eNS_URI);
  }

} // CodegenPackageImpl
