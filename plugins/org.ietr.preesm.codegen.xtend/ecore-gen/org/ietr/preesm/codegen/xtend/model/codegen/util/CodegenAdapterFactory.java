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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
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
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code> method for each class of the model. <!--
 * end-user-doc -->
 * 
 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage
 * @generated
 */
public class CodegenAdapterFactory extends AdapterFactoryImpl {
  /**
   * The cached model package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected static CodegenPackage modelPackage;

  /**
   * Creates an instance of the adapter factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public CodegenAdapterFactory() {
    if (CodegenAdapterFactory.modelPackage == null) {
      CodegenAdapterFactory.modelPackage = CodegenPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object. <!-- begin-user-doc --> This implementation returns <code>true</code> if the object
   * is either the model's package or is an instance object of the model. <!-- end-user-doc -->
   * 
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(final Object object) {
    if (object == CodegenAdapterFactory.modelPackage) {
      return true;
    }
    if (object instanceof EObject) {
      return ((EObject) object).eClass().getEPackage() == CodegenAdapterFactory.modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected CodegenSwitch<Adapter> modelSwitch = new CodegenSwitch<Adapter>() {
    @Override
    public Adapter caseBlock(final Block object) {
      return createBlockAdapter();
    }

    @Override
    public Adapter caseCodeElt(final CodeElt object) {
      return createCodeEltAdapter();
    }

    @Override
    public Adapter caseCall(final Call object) {
      return createCallAdapter();
    }

    @Override
    public Adapter caseVariable(final Variable object) {
      return createVariableAdapter();
    }

    @Override
    public Adapter caseBuffer(final Buffer object) {
      return createBufferAdapter();
    }

    @Override
    public Adapter caseSubBuffer(final SubBuffer object) {
      return createSubBufferAdapter();
    }

    @Override
    public Adapter caseConstant(final Constant object) {
      return createConstantAdapter();
    }

    @Override
    public Adapter caseFunctionCall(final FunctionCall object) {
      return createFunctionCallAdapter();
    }

    @Override
    public Adapter caseCommunication(final Communication object) {
      return createCommunicationAdapter();
    }

    @Override
    public Adapter caseCoreBlock(final CoreBlock object) {
      return createCoreBlockAdapter();
    }

    @Override
    public Adapter caseActorBlock(final ActorBlock object) {
      return createActorBlockAdapter();
    }

    @Override
    public Adapter caseLoopBlock(final LoopBlock object) {
      return createLoopBlockAdapter();
    }

    @Override
    public Adapter caseActorCall(final ActorCall object) {
      return createActorCallAdapter();
    }

    @Override
    public Adapter caseCallBlock(final CallBlock object) {
      return createCallBlockAdapter();
    }

    @Override
    public Adapter caseSpecialCall(final SpecialCall object) {
      return createSpecialCallAdapter();
    }

    @Override
    public Adapter caseFifoCall(final FifoCall object) {
      return createFifoCallAdapter();
    }

    @Override
    public Adapter caseCommentable(final Commentable object) {
      return createCommentableAdapter();
    }

    @Override
    public Adapter caseCommunicationNode(final CommunicationNode object) {
      return createCommunicationNodeAdapter();
    }

    @Override
    public Adapter caseSharedMemoryCommunication(final SharedMemoryCommunication object) {
      return createSharedMemoryCommunicationAdapter();
    }

    @Override
    public Adapter caseConstantString(final ConstantString object) {
      return createConstantStringAdapter();
    }

    @Override
    public Adapter caseNullBuffer(final NullBuffer object) {
      return createNullBufferAdapter();
    }

    @Override
    public Adapter caseFiniteLoopBlock(final FiniteLoopBlock object) {
      return createFiniteLoopBlockAdapter();
    }

    @Override
    public Adapter caseIntVar(final IntVar object) {
      return createIntVarAdapter();
    }

    @Override
    public Adapter caseBufferIterator(final BufferIterator object) {
      return createBufferIteratorAdapter();
    }

    @Override
    public Adapter casePapifyAction(final PapifyAction object) {
      return createPapifyActionAdapter();
    }

    @Override
    public Adapter defaultCase(final EObject object) {
      return createEObjectAdapter();
    }
  };

  /**
   * Creates an adapter for the <code>target</code>. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param target
   *          the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(final Notifier target) {
    return this.modelSwitch.doSwitch((EObject) target);
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.Block <em>Block</em>}'. <!-- begin-user-doc --> This
   * default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Block
   * @generated
   */
  public Adapter createBlockAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.CodeElt <em>Code Elt</em>}'. <!-- begin-user-doc --> This
   * default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodeElt
   * @generated
   */
  public Adapter createCodeEltAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.Call <em>Call</em>}'. <!-- begin-user-doc --> This default
   * implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Call
   * @generated
   */
  public Adapter createCallAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.Variable <em>Variable</em>}'. <!-- begin-user-doc --> This
   * default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Variable
   * @generated
   */
  public Adapter createVariableAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.Buffer <em>Buffer</em>}'. <!-- begin-user-doc --> This
   * default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Buffer
   * @generated
   */
  public Adapter createBufferAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer <em>Sub Buffer</em>}'. <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer
   * @generated
   */
  public Adapter createSubBufferAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.Constant <em>Constant</em>}'. <!-- begin-user-doc --> This
   * default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Constant
   * @generated
   */
  public Adapter createConstantAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class ' {@link org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall <em>Function Call</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall
   * @generated
   */
  public Adapter createFunctionCallAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class ' {@link org.ietr.preesm.codegen.xtend.model.codegen.Communication <em>Communication</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Communication
   * @generated
   */
  public Adapter createCommunicationAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock <em>Core Block</em>}'. <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock
   * @generated
   */
  public Adapter createCoreBlockAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock <em>Actor Block</em>}'. <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock
   * @generated
   */
  public Adapter createActorBlockAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock <em>Loop Block</em>}'. <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock
   * @generated
   */
  public Adapter createLoopBlockAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.ActorCall <em>Actor Call</em>}'. <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.ActorCall
   * @generated
   */
  public Adapter createActorCallAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.CallBlock <em>Call Block</em>}'. <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CallBlock
   * @generated
   */
  public Adapter createCallBlockAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall <em>Special Call</em>}'. <!-- begin-user-doc
   * --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases
   * anyway. <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall
   * @generated
   */
  public Adapter createSpecialCallAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall <em>Fifo Call</em>}'. <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoCall
   * @generated
   */
  public Adapter createFifoCallAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.Commentable <em>Commentable</em>}'. <!-- begin-user-doc
   * --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases
   * anyway. <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Commentable
   * @generated
   */
  public Adapter createCommentableAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class ' {@link org.ietr.preesm.codegen.xtend.model.codegen.CommunicationNode <em>Communication Node</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CommunicationNode
   * @generated
   */
  public Adapter createCommunicationNodeAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication <em>Shared Memory
   * Communication</em>}'. <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case
   * when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication
   * @generated
   */
  public Adapter createSharedMemoryCommunicationAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class ' {@link org.ietr.preesm.codegen.xtend.model.codegen.ConstantString <em>Constant String</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.ConstantString
   * @generated
   */
  public Adapter createConstantStringAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.NullBuffer <em>Null Buffer</em>}'. <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.NullBuffer
   * @generated
   */
  public Adapter createNullBufferAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.FiniteLoopBlock <em>Finite Loop Block</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FiniteLoopBlock
   * @generated
   */
  public Adapter createFiniteLoopBlockAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.IntVar <em>Int Var</em>}'. <!-- begin-user-doc --> This
   * default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * 
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.IntVar
   * @generated
   */
  public Adapter createIntVarAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.BufferIterator <em>Buffer Iterator</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.BufferIterator
   * @generated
   */
  public Adapter createBufferIteratorAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.codegen.xtend.model.codegen.PapifyAction <em>Papify Action</em>}'. <!-- begin-user-doc
   * --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases
   * anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.PapifyAction
   * @generated
   */
  public Adapter createPapifyActionAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for the default case. <!-- begin-user-doc --> This default implementation returns null. <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter() {
    return null;
  }

} // CodegenAdapterFactory
