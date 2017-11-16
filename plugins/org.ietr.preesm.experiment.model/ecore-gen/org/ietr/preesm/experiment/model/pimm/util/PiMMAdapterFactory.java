/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2013)
 * Romina Racca <romina.racca@gmail.com> (2013)
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
/**
 */
package org.ietr.preesm.experiment.model.pimm.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.BroadcastActor;
import org.ietr.preesm.experiment.model.pimm.CHeaderRefinement;
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.PiSDFRefinement;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;
import org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitable;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code> method for each class of the model. <!--
 * end-user-doc -->
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage
 * @generated
 */
public class PiMMAdapterFactory extends AdapterFactoryImpl {
  /**
   * The cached model package. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected static PiMMPackage modelPackage;

  /**
   * Creates an instance of the adapter factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public PiMMAdapterFactory() {
    if (PiMMAdapterFactory.modelPackage == null) {
      PiMMAdapterFactory.modelPackage = PiMMPackage.eINSTANCE;
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
    if (object == PiMMAdapterFactory.modelPackage) {
      return true;
    }
    if (object instanceof EObject) {
      return ((EObject) object).eClass().getEPackage() == PiMMAdapterFactory.modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected PiMMSwitch<Adapter> modelSwitch = new PiMMSwitch<Adapter>() {
    @Override
    public Adapter caseParameterizable(final Parameterizable object) {
      return createParameterizableAdapter();
    }

    @Override
    public Adapter caseAbstractVertex(final AbstractVertex object) {
      return createAbstractVertexAdapter();
    }

    @Override
    public Adapter caseAbstractActor(final AbstractActor object) {
      return createAbstractActorAdapter();
    }

    @Override
    public Adapter casePiGraph(final PiGraph object) {
      return createPiGraphAdapter();
    }

    @Override
    public Adapter caseActor(final Actor object) {
      return createActorAdapter();
    }

    @Override
    public Adapter casePort(final Port object) {
      return createPortAdapter();
    }

    @Override
    public Adapter caseDataInputPort(final DataInputPort object) {
      return createDataInputPortAdapter();
    }

    @Override
    public Adapter caseDataOutputPort(final DataOutputPort object) {
      return createDataOutputPortAdapter();
    }

    @Override
    public Adapter caseConfigInputPort(final ConfigInputPort object) {
      return createConfigInputPortAdapter();
    }

    @Override
    public Adapter caseConfigOutputPort(final ConfigOutputPort object) {
      return createConfigOutputPortAdapter();
    }

    @Override
    public Adapter caseFifo(final Fifo object) {
      return createFifoAdapter();
    }

    @Override
    public Adapter caseInterfaceActor(final InterfaceActor object) {
      return createInterfaceActorAdapter();
    }

    @Override
    public Adapter caseDataInputInterface(final DataInputInterface object) {
      return createDataInputInterfaceAdapter();
    }

    @Override
    public Adapter caseDataOutputInterface(final DataOutputInterface object) {
      return createDataOutputInterfaceAdapter();
    }

    @Override
    public Adapter caseConfigInputInterface(final ConfigInputInterface object) {
      return createConfigInputInterfaceAdapter();
    }

    @Override
    public Adapter caseConfigOutputInterface(final ConfigOutputInterface object) {
      return createConfigOutputInterfaceAdapter();
    }

    @Override
    public Adapter casePiSDFRefinement(final PiSDFRefinement object) {
      return createPiSDFRefinementAdapter();
    }

    @Override
    public Adapter caseParameter(final Parameter object) {
      return createParameterAdapter();
    }

    @Override
    public Adapter caseDependency(final Dependency object) {
      return createDependencyAdapter();
    }

    @Override
    public Adapter caseISetter(final ISetter object) {
      return createISetterAdapter();
    }

    @Override
    public Adapter caseDelay(final Delay object) {
      return createDelayAdapter();
    }

    @Override
    public Adapter caseExpression(final Expression object) {
      return createExpressionAdapter();
    }

    @Override
    public Adapter caseCHeaderRefinement(final CHeaderRefinement object) {
      return createCHeaderRefinementAdapter();
    }

    @Override
    public Adapter caseFunctionPrototype(final FunctionPrototype object) {
      return createFunctionPrototypeAdapter();
    }

    @Override
    public Adapter caseFunctionParameter(final FunctionParameter object) {
      return createFunctionParameterAdapter();
    }

    @Override
    public Adapter caseDataPort(final DataPort object) {
      return createDataPortAdapter();
    }

    @Override
    public Adapter caseBroadcastActor(final BroadcastActor object) {
      return createBroadcastActorAdapter();
    }

    @Override
    public Adapter caseJoinActor(final JoinActor object) {
      return createJoinActorAdapter();
    }

    @Override
    public Adapter caseForkActor(final ForkActor object) {
      return createForkActorAdapter();
    }

    @Override
    public Adapter caseRoundBufferActor(final RoundBufferActor object) {
      return createRoundBufferActorAdapter();
    }

    @Override
    public Adapter caseExecutableActor(final ExecutableActor object) {
      return createExecutableActorAdapter();
    }

    @Override
    public Adapter casePiMMVisitable(final PiMMVisitable object) {
      return createPiMMVisitableAdapter();
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
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.Parameterizable <em>Parameterizable</em>}'. <!-- begin-user-doc
   * --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases
   * anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.Parameterizable
   * @generated
   */
  public Adapter createParameterizableAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.AbstractVertex <em>Abstract Vertex</em>}'. <!-- begin-user-doc
   * --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases
   * anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.AbstractVertex
   * @generated
   */
  public Adapter createAbstractVertexAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.AbstractActor <em>Abstract Actor</em>}'. <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.AbstractActor
   * @generated
   */
  public Adapter createAbstractActorAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.PiGraph <em>Pi Graph</em>}'. <!-- begin-user-doc --> This
   * default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.PiGraph
   * @generated
   */
  public Adapter createPiGraphAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.Actor <em>Actor</em>}'. <!-- begin-user-doc --> This default
   * implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.Actor
   * @generated
   */
  public Adapter createActorAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.Port <em>Port</em>}'. <!-- begin-user-doc --> This default
   * implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.Port
   * @generated
   */
  public Adapter createPortAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.DataInputPort <em>Data Input Port</em>}'. <!-- begin-user-doc
   * --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases
   * anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.DataInputPort
   * @generated
   */
  public Adapter createDataInputPortAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.DataOutputPort <em>Data Output Port</em>}'. <!-- begin-user-doc
   * --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases
   * anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.DataOutputPort
   * @generated
   */
  public Adapter createDataOutputPortAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.ConfigInputPort <em>Config Input Port</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.ConfigInputPort
   * @generated
   */
  public Adapter createConfigInputPortAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.ConfigOutputPort <em>Config Output Port</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.ConfigOutputPort
   * @generated
   */
  public Adapter createConfigOutputPortAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.Fifo <em>Fifo</em>}'. <!-- begin-user-doc --> This default
   * implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.Fifo
   * @generated
   */
  public Adapter createFifoAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.InterfaceActor <em>Interface Actor</em>}'. <!-- begin-user-doc
   * --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases
   * anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.InterfaceActor
   * @generated
   */
  public Adapter createInterfaceActorAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.DataInputInterface <em>Data Input Interface</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.DataInputInterface
   * @generated
   */
  public Adapter createDataInputInterfaceAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.DataOutputInterface <em>Data Output Interface</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.DataOutputInterface
   * @generated
   */
  public Adapter createDataOutputInterfaceAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface <em>Config Output Interface</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface
   * @generated
   */
  public Adapter createConfigOutputInterfaceAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.PiSDFRefinement <em>Pi SDF Refinement</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.PiSDFRefinement
   * @generated
   */
  public Adapter createPiSDFRefinementAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.Parameter <em>Parameter</em>}'. <!-- begin-user-doc --> This
   * default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.Parameter
   * @generated
   */
  public Adapter createParameterAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.Dependency <em>Dependency</em>}'. <!-- begin-user-doc --> This
   * default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.Dependency
   * @generated
   */
  public Adapter createDependencyAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.ISetter <em>ISetter</em>}'. <!-- begin-user-doc --> This default
   * implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.ISetter
   * @generated
   */
  public Adapter createISetterAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.Delay <em>Delay</em>}'. <!-- begin-user-doc --> This default
   * implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.Delay
   * @generated
   */
  public Adapter createDelayAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.Expression <em>Expression</em>}'. <!-- begin-user-doc --> This
   * default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.Expression
   * @generated
   */
  public Adapter createExpressionAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.CHeaderRefinement <em>CHeader Refinement</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.CHeaderRefinement
   * @generated
   */
  public Adapter createCHeaderRefinementAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.FunctionPrototype <em>Function Prototype</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.FunctionPrototype
   * @generated
   */
  public Adapter createFunctionPrototypeAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.FunctionParameter <em>Function Parameter</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.FunctionParameter
   * @generated
   */
  public Adapter createFunctionParameterAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.DataPort <em>Data Port</em>}'. <!-- begin-user-doc --> This
   * default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.DataPort
   * @generated
   */
  public Adapter createDataPortAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.BroadcastActor <em>Broadcast Actor</em>}'. <!-- begin-user-doc
   * --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases
   * anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.BroadcastActor
   * @generated
   */
  public Adapter createBroadcastActorAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.JoinActor <em>Join Actor</em>}'. <!-- begin-user-doc --> This
   * default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.JoinActor
   * @generated
   */
  public Adapter createJoinActorAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.ForkActor <em>Fork Actor</em>}'. <!-- begin-user-doc --> This
   * default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.ForkActor
   * @generated
   */
  public Adapter createForkActorAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.RoundBufferActor <em>Round Buffer Actor</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.RoundBufferActor
   * @generated
   */
  public Adapter createRoundBufferActorAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.ExecutableActor <em>Executable Actor</em>}'. <!-- begin-user-doc
   * --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases
   * anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.ExecutableActor
   * @generated
   */
  public Adapter createExecutableActorAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitable <em>Pi MM Visitable</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitable
   * @generated
   */
  public Adapter createPiMMVisitableAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.ConfigInputInterface <em>Config Input Interface</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.ConfigInputInterface
   * @generated
   */
  public Adapter createConfigInputInterfaceAdapter() {
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

} // PiMMAdapterFactory
