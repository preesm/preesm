/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2014)
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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.ActorCall;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer;
import org.ietr.preesm.codegen.xtend.model.codegen.BufferIterator;
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;
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
import org.ietr.preesm.codegen.xtend.model.codegen.PortDirection;
import org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialType;
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer;
import org.ietr.preesm.memory.script.Range;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 *
 * @generated
 */
public class CodegenFactoryImpl extends EFactoryImpl implements CodegenFactory {

  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public static CodegenFactory init() {
    try {
      final CodegenFactory theCodegenFactory = (CodegenFactory) EPackage.Registry.INSTANCE.getEFactory(CodegenPackage.eNS_URI);
      if (theCodegenFactory != null) {
        return theCodegenFactory;
      }
    } catch (final Exception exception) {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new CodegenFactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public CodegenFactoryImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param eClass
   *          the e class
   * @return the e object
   * @generated
   */
  @Override
  public EObject create(final EClass eClass) {
    switch (eClass.getClassifierID()) {
      case CodegenPackage.BLOCK:
        return createBlock();
      case CodegenPackage.BUFFER:
        return createBuffer();
      case CodegenPackage.SUB_BUFFER:
        return createSubBuffer();
      case CodegenPackage.CONSTANT:
        return createConstant();
      case CodegenPackage.FUNCTION_CALL:
        return createFunctionCall();
      case CodegenPackage.COMMUNICATION:
        return createCommunication();
      case CodegenPackage.CORE_BLOCK:
        return createCoreBlock();
      case CodegenPackage.ACTOR_BLOCK:
        return createActorBlock();
      case CodegenPackage.LOOP_BLOCK:
        return createLoopBlock();
      case CodegenPackage.ACTOR_CALL:
        return createActorCall();
      case CodegenPackage.CALL_BLOCK:
        return createCallBlock();
      case CodegenPackage.SPECIAL_CALL:
        return createSpecialCall();
      case CodegenPackage.FIFO_CALL:
        return createFifoCall();
      case CodegenPackage.COMMUNICATION_NODE:
        return createCommunicationNode();
      case CodegenPackage.SHARED_MEMORY_COMMUNICATION:
        return createSharedMemoryCommunication();
      case CodegenPackage.CONSTANT_STRING:
        return createConstantString();
      case CodegenPackage.NULL_BUFFER:
        return createNullBuffer();
      case CodegenPackage.FINITE_LOOP_BLOCK:
        return createFiniteLoopBlock();
      case CodegenPackage.INT_VAR:
        return createIntVar();
      case CodegenPackage.BUFFER_ITERATOR:
        return createBufferIterator();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param eDataType
   *          the e data type
   * @param initialValue
   *          the initial value
   * @return the object
   * @generated
   */
  @Override
  public Object createFromString(final EDataType eDataType, final String initialValue) {
    switch (eDataType.getClassifierID()) {
      case CodegenPackage.DIRECTION:
        return createDirectionFromString(eDataType, initialValue);
      case CodegenPackage.DELIMITER:
        return createDelimiterFromString(eDataType, initialValue);
      case CodegenPackage.SPECIAL_TYPE:
        return createSpecialTypeFromString(eDataType, initialValue);
      case CodegenPackage.FIFO_OPERATION:
        return createFifoOperationFromString(eDataType, initialValue);
      case CodegenPackage.PORT_DIRECTION:
        return createPortDirectionFromString(eDataType, initialValue);
      case CodegenPackage.RANGE:
        return createrangeFromString(eDataType, initialValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param eDataType
   *          the e data type
   * @param instanceValue
   *          the instance value
   * @return the string
   * @generated
   */
  @Override
  public String convertToString(final EDataType eDataType, final Object instanceValue) {
    switch (eDataType.getClassifierID()) {
      case CodegenPackage.DIRECTION:
        return convertDirectionToString(eDataType, instanceValue);
      case CodegenPackage.DELIMITER:
        return convertDelimiterToString(eDataType, instanceValue);
      case CodegenPackage.SPECIAL_TYPE:
        return convertSpecialTypeToString(eDataType, instanceValue);
      case CodegenPackage.FIFO_OPERATION:
        return convertFifoOperationToString(eDataType, instanceValue);
      case CodegenPackage.PORT_DIRECTION:
        return convertPortDirectionToString(eDataType, instanceValue);
      case CodegenPackage.RANGE:
        return convertrangeToString(eDataType, instanceValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the block
   * @generated
   */
  @Override
  public Block createBlock() {
    final BlockImpl block = new BlockImpl();
    return block;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the buffer
   * @generated
   */
  @Override
  public Buffer createBuffer() {
    final BufferImpl buffer = new BufferImpl();
    return buffer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the sub buffer
   * @generated
   */
  @Override
  public SubBuffer createSubBuffer() {
    final SubBufferImpl subBuffer = new SubBufferImpl();
    return subBuffer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the constant
   * @generated
   */
  @Override
  public Constant createConstant() {
    final ConstantImpl constant = new ConstantImpl();
    return constant;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the function call
   * @generated
   */
  @Override
  public FunctionCall createFunctionCall() {
    final FunctionCallImpl functionCall = new FunctionCallImpl();
    return functionCall;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication
   * @generated
   */
  @Override
  public Communication createCommunication() {
    final CommunicationImpl communication = new CommunicationImpl();
    return communication;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the core block
   * @generated
   */
  @Override
  public CoreBlock createCoreBlock() {
    final CoreBlockImpl coreBlock = new CoreBlockImpl();
    return coreBlock;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the actor block
   * @generated
   */
  @Override
  public ActorBlock createActorBlock() {
    final ActorBlockImpl actorBlock = new ActorBlockImpl();
    return actorBlock;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the loop block
   * @generated
   */
  @Override
  public LoopBlock createLoopBlock() {
    final LoopBlockImpl loopBlock = new LoopBlockImpl();
    return loopBlock;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the actor call
   * @generated
   */
  @Override
  public ActorCall createActorCall() {
    final ActorCallImpl actorCall = new ActorCallImpl();
    return actorCall;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the call block
   * @generated
   */
  @Override
  public CallBlock createCallBlock() {
    final CallBlockImpl callBlock = new CallBlockImpl();
    return callBlock;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the special call
   * @generated
   */
  @Override
  public SpecialCall createSpecialCall() {
    final SpecialCallImpl specialCall = new SpecialCallImpl();
    return specialCall;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifo call
   * @generated
   */
  @Override
  public FifoCall createFifoCall() {
    final FifoCallImpl fifoCall = new FifoCallImpl();
    return fifoCall;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication node
   * @generated
   */
  @Override
  public CommunicationNode createCommunicationNode() {
    final CommunicationNodeImpl communicationNode = new CommunicationNodeImpl();
    return communicationNode;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the shared memory communication
   * @generated
   */
  @Override
  public SharedMemoryCommunication createSharedMemoryCommunication() {
    final SharedMemoryCommunicationImpl sharedMemoryCommunication = new SharedMemoryCommunicationImpl();
    return sharedMemoryCommunication;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the constant string
   * @generated
   */
  @Override
  public ConstantString createConstantString() {
    final ConstantStringImpl constantString = new ConstantStringImpl();
    return constantString;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the null buffer
   * @generated
   */
  @Override
  public NullBuffer createNullBuffer() {
    final NullBufferImpl nullBuffer = new NullBufferImpl();
    return nullBuffer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public FiniteLoopBlock createFiniteLoopBlock() {
    final FiniteLoopBlockImpl finiteLoopBlock = new FiniteLoopBlockImpl();
    return finiteLoopBlock;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public IntVar createIntVar() {
    final IntVarImpl intVar = new IntVarImpl();
    return intVar;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public BufferIterator createBufferIterator() {
    final BufferIteratorImpl bufferIterator = new BufferIteratorImpl();
    return bufferIterator;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param eDataType
   *          the e data type
   * @param initialValue
   *          the initial value
   * @return the direction
   * @generated
   */
  public Direction createDirectionFromString(final EDataType eDataType, final String initialValue) {
    final Direction result = Direction.get(initialValue);
    if (result == null) {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param eDataType
   *          the e data type
   * @param instanceValue
   *          the instance value
   * @return the string
   * @generated
   */
  public String convertDirectionToString(final EDataType eDataType, final Object instanceValue) {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param eDataType
   *          the e data type
   * @param initialValue
   *          the initial value
   * @return the delimiter
   * @generated
   */
  public Delimiter createDelimiterFromString(final EDataType eDataType, final String initialValue) {
    final Delimiter result = Delimiter.get(initialValue);
    if (result == null) {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param eDataType
   *          the e data type
   * @param instanceValue
   *          the instance value
   * @return the string
   * @generated
   */
  public String convertDelimiterToString(final EDataType eDataType, final Object instanceValue) {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param eDataType
   *          the e data type
   * @param initialValue
   *          the initial value
   * @return the special type
   * @generated
   */
  public SpecialType createSpecialTypeFromString(final EDataType eDataType, final String initialValue) {
    final SpecialType result = SpecialType.get(initialValue);
    if (result == null) {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param eDataType
   *          the e data type
   * @param instanceValue
   *          the instance value
   * @return the string
   * @generated
   */
  public String convertSpecialTypeToString(final EDataType eDataType, final Object instanceValue) {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param eDataType
   *          the e data type
   * @param initialValue
   *          the initial value
   * @return the fifo operation
   * @generated
   */
  public FifoOperation createFifoOperationFromString(final EDataType eDataType, final String initialValue) {
    final FifoOperation result = FifoOperation.get(initialValue);
    if (result == null) {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param eDataType
   *          the e data type
   * @param instanceValue
   *          the instance value
   * @return the string
   * @generated
   */
  public String convertFifoOperationToString(final EDataType eDataType, final Object instanceValue) {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param eDataType
   *          the e data type
   * @param initialValue
   *          the initial value
   * @return the port direction
   * @generated
   */
  public PortDirection createPortDirectionFromString(final EDataType eDataType, final String initialValue) {
    final PortDirection result = PortDirection.get(initialValue);
    if (result == null) {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param eDataType
   *          the e data type
   * @param instanceValue
   *          the instance value
   * @return the string
   * @generated
   */
  public String convertPortDirectionToString(final EDataType eDataType, final Object instanceValue) {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param eDataType
   *          the e data type
   * @param initialValue
   *          the initial value
   * @return the org.ietr.preesm.memory.script. range
   * @generated
   */
  public Range createrangeFromString(final EDataType eDataType, final String initialValue) {
    return (Range) super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param eDataType
   *          the e data type
   * @param instanceValue
   *          the instance value
   * @return the string
   * @generated
   */
  public String convertrangeToString(final EDataType eDataType, final Object instanceValue) {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the codegen package
   * @generated
   */
  @Override
  public CodegenPackage getCodegenPackage() {
    return (CodegenPackage) getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the package
   * @deprecated
   * @generated
   */
  @Deprecated
  public static CodegenPackage getPackage() {
    return CodegenPackage.eINSTANCE;
  }

} // CodegenFactoryImpl
