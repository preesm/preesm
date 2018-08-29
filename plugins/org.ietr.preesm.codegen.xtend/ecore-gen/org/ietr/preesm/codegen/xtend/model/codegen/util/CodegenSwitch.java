/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
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
package org.ietr.preesm.codegen.xtend.model.codegen.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;
import org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.ActorCall;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer;
import org.ietr.preesm.codegen.xtend.model.codegen.BufferIterator;
import org.ietr.preesm.codegen.xtend.model.codegen.Call;
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.CodeElt;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.xtend.model.codegen.Commentable;
import org.ietr.preesm.codegen.xtend.model.codegen.Communication;
import org.ietr.preesm.codegen.xtend.model.codegen.CommunicationNode;
import org.ietr.preesm.codegen.xtend.model.codegen.Constant;
import org.ietr.preesm.codegen.xtend.model.codegen.ConstantString;
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall;
import org.ietr.preesm.codegen.xtend.model.codegen.FiniteLoopBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall;
import org.ietr.preesm.codegen.xtend.model.codegen.IntVar;
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.NullBuffer;
import org.ietr.preesm.codegen.xtend.model.codegen.PapifyAction;
import org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall;
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer;
import org.ietr.preesm.codegen.xtend.model.codegen.Variable;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance hierarchy. It supports the call
 * {@link #doSwitch(EObject) doSwitch(object)} to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object and proceeding up the inheritance hierarchy until a non-null result is
 * returned, which is the result of the switch. <!-- end-user-doc -->
 * 
 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage
 * @generated
 */
public class CodegenSwitch<T> extends Switch<T> {

  /**
   * The cached model package <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected static CodegenPackage modelPackage;

  /**
   * Creates an instance of the switch. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public CodegenSwitch() {
    if (CodegenSwitch.modelPackage == null) {
      CodegenSwitch.modelPackage = CodegenPackage.eINSTANCE;
    }
  }

  /**
   * Checks whether this is a switch for the given package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param ePackage
   *          the package in question.
   * @return whether this is a switch for the given package.
   * @generated
   */
  @Override
  protected boolean isSwitchFor(final EPackage ePackage) {
    return ePackage == CodegenSwitch.modelPackage;
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  @Override
  protected T doSwitch(final int classifierID, final EObject theEObject) {
    switch (classifierID) {
      case CodegenPackage.BLOCK: {
        final Block block = (Block) theEObject;
        T result = caseBlock(block);
        if (result == null) {
          result = caseCodeElt(block);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.CODE_ELT: {
        final CodeElt codeElt = (CodeElt) theEObject;
        T result = caseCodeElt(codeElt);
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.CALL: {
        final Call call = (Call) theEObject;
        T result = caseCall(call);
        if (result == null) {
          result = caseCodeElt(call);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.VARIABLE: {
        final Variable variable = (Variable) theEObject;
        T result = caseVariable(variable);
        if (result == null) {
          result = caseCommentable(variable);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.BUFFER: {
        final Buffer buffer = (Buffer) theEObject;
        T result = caseBuffer(buffer);
        if (result == null) {
          result = caseVariable(buffer);
        }
        if (result == null) {
          result = caseCommentable(buffer);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.SUB_BUFFER: {
        final SubBuffer subBuffer = (SubBuffer) theEObject;
        T result = caseSubBuffer(subBuffer);
        if (result == null) {
          result = caseBuffer(subBuffer);
        }
        if (result == null) {
          result = caseVariable(subBuffer);
        }
        if (result == null) {
          result = caseCommentable(subBuffer);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.CONSTANT: {
        final Constant constant = (Constant) theEObject;
        T result = caseConstant(constant);
        if (result == null) {
          result = caseVariable(constant);
        }
        if (result == null) {
          result = caseCommentable(constant);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.FUNCTION_CALL: {
        final FunctionCall functionCall = (FunctionCall) theEObject;
        T result = caseFunctionCall(functionCall);
        if (result == null) {
          result = caseCall(functionCall);
        }
        if (result == null) {
          result = caseCodeElt(functionCall);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.COMMUNICATION: {
        final Communication communication = (Communication) theEObject;
        T result = caseCommunication(communication);
        if (result == null) {
          result = caseCall(communication);
        }
        if (result == null) {
          result = caseCodeElt(communication);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.CORE_BLOCK: {
        final CoreBlock coreBlock = (CoreBlock) theEObject;
        T result = caseCoreBlock(coreBlock);
        if (result == null) {
          result = caseBlock(coreBlock);
        }
        if (result == null) {
          result = caseCodeElt(coreBlock);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.ACTOR_BLOCK: {
        final ActorBlock actorBlock = (ActorBlock) theEObject;
        T result = caseActorBlock(actorBlock);
        if (result == null) {
          result = caseBlock(actorBlock);
        }
        if (result == null) {
          result = caseCodeElt(actorBlock);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.LOOP_BLOCK: {
        final LoopBlock loopBlock = (LoopBlock) theEObject;
        T result = caseLoopBlock(loopBlock);
        if (result == null) {
          result = caseBlock(loopBlock);
        }
        if (result == null) {
          result = caseCodeElt(loopBlock);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.ACTOR_CALL: {
        final ActorCall actorCall = (ActorCall) theEObject;
        T result = caseActorCall(actorCall);
        if (result == null) {
          result = caseCall(actorCall);
        }
        if (result == null) {
          result = caseCodeElt(actorCall);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.CALL_BLOCK: {
        final CallBlock callBlock = (CallBlock) theEObject;
        T result = caseCallBlock(callBlock);
        if (result == null) {
          result = caseBlock(callBlock);
        }
        if (result == null) {
          result = caseCodeElt(callBlock);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.SPECIAL_CALL: {
        final SpecialCall specialCall = (SpecialCall) theEObject;
        T result = caseSpecialCall(specialCall);
        if (result == null) {
          result = caseCall(specialCall);
        }
        if (result == null) {
          result = caseCodeElt(specialCall);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.FIFO_CALL: {
        final FifoCall fifoCall = (FifoCall) theEObject;
        T result = caseFifoCall(fifoCall);
        if (result == null) {
          result = caseCall(fifoCall);
        }
        if (result == null) {
          result = caseCodeElt(fifoCall);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.COMMENTABLE: {
        final Commentable commentable = (Commentable) theEObject;
        T result = caseCommentable(commentable);
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.COMMUNICATION_NODE: {
        final CommunicationNode communicationNode = (CommunicationNode) theEObject;
        T result = caseCommunicationNode(communicationNode);
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.SHARED_MEMORY_COMMUNICATION: {
        final SharedMemoryCommunication sharedMemoryCommunication = (SharedMemoryCommunication) theEObject;
        T result = caseSharedMemoryCommunication(sharedMemoryCommunication);
        if (result == null) {
          result = caseCommunication(sharedMemoryCommunication);
        }
        if (result == null) {
          result = caseCall(sharedMemoryCommunication);
        }
        if (result == null) {
          result = caseCodeElt(sharedMemoryCommunication);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.CONSTANT_STRING: {
        final ConstantString constantString = (ConstantString) theEObject;
        T result = caseConstantString(constantString);
        if (result == null) {
          result = caseVariable(constantString);
        }
        if (result == null) {
          result = caseCommentable(constantString);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.NULL_BUFFER: {
        final NullBuffer nullBuffer = (NullBuffer) theEObject;
        T result = caseNullBuffer(nullBuffer);
        if (result == null) {
          result = caseSubBuffer(nullBuffer);
        }
        if (result == null) {
          result = caseBuffer(nullBuffer);
        }
        if (result == null) {
          result = caseVariable(nullBuffer);
        }
        if (result == null) {
          result = caseCommentable(nullBuffer);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.FINITE_LOOP_BLOCK: {
        final FiniteLoopBlock finiteLoopBlock = (FiniteLoopBlock) theEObject;
        T result = caseFiniteLoopBlock(finiteLoopBlock);
        if (result == null) {
          result = caseLoopBlock(finiteLoopBlock);
        }
        if (result == null) {
          result = caseBlock(finiteLoopBlock);
        }
        if (result == null) {
          result = caseCodeElt(finiteLoopBlock);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.INT_VAR: {
        final IntVar intVar = (IntVar) theEObject;
        T result = caseIntVar(intVar);
        if (result == null) {
          result = caseVariable(intVar);
        }
        if (result == null) {
          result = caseCommentable(intVar);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.BUFFER_ITERATOR: {
        final BufferIterator bufferIterator = (BufferIterator) theEObject;
        T result = caseBufferIterator(bufferIterator);
        if (result == null) {
          result = caseSubBuffer(bufferIterator);
        }
        if (result == null) {
          result = caseBuffer(bufferIterator);
        }
        if (result == null) {
          result = caseVariable(bufferIterator);
        }
        if (result == null) {
          result = caseCommentable(bufferIterator);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      case CodegenPackage.PAPIFY_ACTION: {
        final PapifyAction papifyAction = (PapifyAction) theEObject;
        T result = casePapifyAction(papifyAction);
        if (result == null) {
          result = caseVariable(papifyAction);
        }
        if (result == null) {
          result = caseCommentable(papifyAction);
        }
        if (result == null) {
          result = defaultCase(theEObject);
        }
        return result;
      }
      default:
        return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Block</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Block</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseBlock(final Block object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Code Elt</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Code Elt</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCodeElt(final CodeElt object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Call</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Call</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCall(final Call object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Variable</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Variable</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseVariable(final Variable object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Buffer</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Buffer</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseBuffer(final Buffer object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Sub Buffer</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Sub Buffer</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSubBuffer(final SubBuffer object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Constant</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Constant</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseConstant(final Constant object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Function Call</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Function Call</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFunctionCall(final FunctionCall object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Communication</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Communication</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCommunication(final Communication object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Core Block</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Core Block</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCoreBlock(final CoreBlock object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Actor Block</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Actor Block</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseActorBlock(final ActorBlock object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Loop Block</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Loop Block</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseLoopBlock(final LoopBlock object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Actor Call</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Actor Call</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseActorCall(final ActorCall object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Call Block</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Call Block</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCallBlock(final CallBlock object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Special Call</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Special Call</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSpecialCall(final SpecialCall object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Fifo Call</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Fifo Call</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFifoCall(final FifoCall object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Commentable</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Commentable</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCommentable(final Commentable object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Communication Node</em>'. <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Communication Node</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCommunicationNode(final CommunicationNode object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Shared Memory Communication</em>'. <!--
   * begin-user-doc --> This implementation returns null; returning a non-null result will terminate the switch. <!--
   * end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Shared Memory Communication</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSharedMemoryCommunication(final SharedMemoryCommunication object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Constant String</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Constant String</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseConstantString(final ConstantString object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Null Buffer</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Null Buffer</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNullBuffer(final NullBuffer object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Finite Loop Block</em>'. <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Finite Loop Block</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFiniteLoopBlock(final FiniteLoopBlock object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Int Var</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Int Var</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIntVar(final IntVar object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Buffer Iterator</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Buffer Iterator</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseBufferIterator(final BufferIterator object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Papify Action</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   *
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Papify Action</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePapifyAction(final PapifyAction object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch, but this is the last case
   * anyway. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  @Override
  public T defaultCase(final EObject object) {
    return null;
  }

} // CodegenSwitch
