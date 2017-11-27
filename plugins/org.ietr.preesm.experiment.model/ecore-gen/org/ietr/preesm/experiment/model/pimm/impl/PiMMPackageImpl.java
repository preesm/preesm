/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
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
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.BroadcastActor;
import org.ietr.preesm.experiment.model.pimm.CHeaderRefinement;
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.Configurable;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Direction;
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
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.PiSDFRefinement;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;
import org.ietr.preesm.experiment.model.pimm.visitor.VisitorPackage;
import org.ietr.preesm.experiment.model.pimm.visitor.impl.VisitorPackageImpl;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 *
 * @generated
 */
public class PiMMPackageImpl extends EPackageImpl implements PiMMPackage {

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass parameterizableEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private EClass configurableEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass abstractVertexEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass abstractActorEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass piGraphEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass actorEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass portEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass dataInputPortEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass dataOutputPortEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass configInputPortEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass configOutputPortEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass fifoEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass interfaceActorEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass dataInputInterfaceEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass dataOutputInterfaceEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass configOutputInterfaceEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private EClass refinementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private EClass piSDFRefinementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass parameterEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass dependencyEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass iSetterEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass delayEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass expressionEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private EClass cHeaderRefinementEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass functionPrototypeEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass functionParameterEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass dataPortEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass broadcastActorEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass joinActorEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass forkActorEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass roundBufferActorEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass executableActorEClass = null;

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
  private EEnum portMemoryAnnotationEEnum = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EDataType iPathEDataType = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private EClass configInputInterfaceEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package package
   * URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also performs initialization of the package, or
   * returns the registered package, if one already exists. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private PiMMPackageImpl() {
    super(PiMMPackage.eNS_URI, PiMMFactory.eINSTANCE);
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
   * This method is used to initialize {@link PiMMPackage#eINSTANCE} when that field is accessed. Clients should not invoke it directly. Instead, they should
   * simply access that field to obtain the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static PiMMPackage init() {
    if (PiMMPackageImpl.isInited) {
      return (PiMMPackage) EPackage.Registry.INSTANCE.getEPackage(PiMMPackage.eNS_URI);
    }

    // Obtain or create and register package
    final PiMMPackageImpl thePiMMPackage = (PiMMPackageImpl) (EPackage.Registry.INSTANCE.get(PiMMPackage.eNS_URI) instanceof PiMMPackageImpl
        ? EPackage.Registry.INSTANCE.get(PiMMPackage.eNS_URI)
        : new PiMMPackageImpl());

    PiMMPackageImpl.isInited = true;

    // Obtain or create and register interdependencies
    final VisitorPackageImpl theVisitorPackage = (VisitorPackageImpl) (EPackage.Registry.INSTANCE
        .getEPackage(VisitorPackage.eNS_URI) instanceof VisitorPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(VisitorPackage.eNS_URI)
            : VisitorPackage.eINSTANCE);

    // Create package meta-data objects
    thePiMMPackage.createPackageContents();
    theVisitorPackage.createPackageContents();

    // Initialize created meta-data
    thePiMMPackage.initializePackageContents();
    theVisitorPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    thePiMMPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(PiMMPackage.eNS_URI, thePiMMPackage);
    return thePiMMPackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the parameterizable
   * @generated
   */
  @Override
  public EClass getParameterizable() {
    return this.parameterizableEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EClass getConfigurable() {
    return this.configurableEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EReference getConfigurable_ConfigInputPorts() {
    return (EReference) this.configurableEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the abstract vertex
   * @generated
   */
  @Override
  public EClass getAbstractVertex() {
    return this.abstractVertexEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the abstract vertex name
   * @generated
   */
  @Override
  public EAttribute getAbstractVertex_Name() {
    return (EAttribute) this.abstractVertexEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the abstract actor
   * @generated
   */
  @Override
  public EClass getAbstractActor() {
    return this.abstractActorEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the abstract actor data input ports
   * @generated
   */
  @Override
  public EReference getAbstractActor_DataInputPorts() {
    return (EReference) this.abstractActorEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the abstract actor data output ports
   * @generated
   */
  @Override
  public EReference getAbstractActor_DataOutputPorts() {
    return (EReference) this.abstractActorEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the abstract actor config output ports
   * @generated
   */
  @Override
  public EReference getAbstractActor_ConfigOutputPorts() {
    return (EReference) this.abstractActorEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the pi graph
   * @generated
   */
  @Override
  public EClass getPiGraph() {
    return this.piGraphEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the pi graph vertices
   * @generated
   */
  @Override
  public EReference getPiGraph_Vertices() {
    return (EReference) this.piGraphEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the pi graph fifos
   * @generated
   */
  @Override
  public EReference getPiGraph_Fifos() {
    return (EReference) this.piGraphEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the pi graph parameters
   * @generated
   */
  @Override
  public EReference getPiGraph_Parameters() {
    return (EReference) this.piGraphEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the pi graph dependencies
   * @generated
   */
  @Override
  public EReference getPiGraph_Dependencies() {
    return (EReference) this.piGraphEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the actor
   * @generated
   */
  @Override
  public EClass getActor() {
    return this.actorEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the actor refinement
   * @generated
   */
  @Override
  public EReference getActor_Refinement() {
    return (EReference) this.actorEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the actor memory script path
   * @generated
   */
  @Override
  public EAttribute getActor_MemoryScriptPath() {
    return (EAttribute) this.actorEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the port
   * @generated
   */
  @Override
  public EClass getPort() {
    return this.portEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the port name
   * @generated
   */
  @Override
  public EAttribute getPort_Name() {
    return (EAttribute) this.portEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the port kind
   * @generated
   */
  @Override
  public EAttribute getPort_Kind() {
    return (EAttribute) this.portEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the data input port
   * @generated
   */
  @Override
  public EClass getDataInputPort() {
    return this.dataInputPortEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the data input port incoming fifo
   * @generated
   */
  @Override
  public EReference getDataInputPort_IncomingFifo() {
    return (EReference) this.dataInputPortEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the data output port
   * @generated
   */
  @Override
  public EClass getDataOutputPort() {
    return this.dataOutputPortEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the data output port outgoing fifo
   * @generated
   */
  @Override
  public EReference getDataOutputPort_OutgoingFifo() {
    return (EReference) this.dataOutputPortEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the config input port
   * @generated
   */
  @Override
  public EClass getConfigInputPort() {
    return this.configInputPortEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the config input port incoming dependency
   * @generated
   */
  @Override
  public EReference getConfigInputPort_IncomingDependency() {
    return (EReference) this.configInputPortEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EReference getConfigInputPort_Configurable() {
    return (EReference) this.configInputPortEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the config output port
   * @generated
   */
  @Override
  public EClass getConfigOutputPort() {
    return this.configOutputPortEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifo
   * @generated
   */
  @Override
  public EClass getFifo() {
    return this.fifoEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifo source port
   * @generated
   */
  @Override
  public EReference getFifo_SourcePort() {
    return (EReference) this.fifoEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifo target port
   * @generated
   */
  @Override
  public EReference getFifo_TargetPort() {
    return (EReference) this.fifoEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifo delay
   * @generated
   */
  @Override
  public EReference getFifo_Delay() {
    return (EReference) this.fifoEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifo id
   * @generated
   */
  @Override
  public EAttribute getFifo_Id() {
    return (EAttribute) this.fifoEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifo type
   * @generated
   */
  @Override
  public EAttribute getFifo_Type() {
    return (EAttribute) this.fifoEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the interface actor
   * @generated
   */
  @Override
  public EClass getInterfaceActor() {
    return this.interfaceActorEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the interface actor graph port
   * @generated
   */
  @Override
  public EReference getInterfaceActor_GraphPort() {
    return (EReference) this.interfaceActorEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the interface actor kind
   * @generated
   */
  @Override
  public EAttribute getInterfaceActor_Kind() {
    return (EAttribute) this.interfaceActorEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the data input interface
   * @generated
   */
  @Override
  public EClass getDataInputInterface() {
    return this.dataInputInterfaceEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the data output interface
   * @generated
   */
  @Override
  public EClass getDataOutputInterface() {
    return this.dataOutputInterfaceEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the config output interface
   * @generated
   */
  @Override
  public EClass getConfigOutputInterface() {
    return this.configOutputInterfaceEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EClass getRefinement() {
    return this.refinementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EAttribute getRefinement_FilePath() {
    return (EAttribute) this.refinementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EClass getPiSDFRefinement() {
    return this.piSDFRefinementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the parameter
   * @generated
   */
  @Override
  public EClass getParameter() {
    return this.parameterEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the parameter graph port
   * @generated
   */
  @Override
  public EReference getParameter_GraphPort() {
    return (EReference) this.parameterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EReference getParameter_ValueExpression() {
    return (EReference) this.parameterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the dependency
   * @generated
   */
  @Override
  public EClass getDependency() {
    return this.dependencyEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the dependency setter
   * @generated
   */
  @Override
  public EReference getDependency_Setter() {
    return (EReference) this.dependencyEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the dependency getter
   * @generated
   */
  @Override
  public EReference getDependency_Getter() {
    return (EReference) this.dependencyEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the i setter
   * @generated
   */
  @Override
  public EClass getISetter() {
    return this.iSetterEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the i setter outgoing dependencies
   * @generated
   */
  @Override
  public EReference getISetter_OutgoingDependencies() {
    return (EReference) this.iSetterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the delay
   * @generated
   */
  @Override
  public EClass getDelay() {
    return this.delayEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EReference getDelay_SizeExpression() {
    return (EReference) this.delayEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the expression
   * @generated
   */
  @Override
  public EClass getExpression() {
    return this.expressionEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EAttribute getExpression_ExpressionString() {
    return (EAttribute) this.expressionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EClass getCHeaderRefinement() {
    return this.cHeaderRefinementEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EReference getCHeaderRefinement_LoopPrototype() {
    return (EReference) this.cHeaderRefinementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EReference getCHeaderRefinement_InitPrototype() {
    return (EReference) this.cHeaderRefinementEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the function prototype
   * @generated
   */
  @Override
  public EClass getFunctionPrototype() {
    return this.functionPrototypeEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the function prototype name
   * @generated
   */
  @Override
  public EAttribute getFunctionPrototype_Name() {
    return (EAttribute) this.functionPrototypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the function prototype parameters
   * @generated
   */
  @Override
  public EReference getFunctionPrototype_Parameters() {
    return (EReference) this.functionPrototypeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the function parameter
   * @generated
   */
  @Override
  public EClass getFunctionParameter() {
    return this.functionParameterEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the function parameter name
   * @generated
   */
  @Override
  public EAttribute getFunctionParameter_Name() {
    return (EAttribute) this.functionParameterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the function parameter direction
   * @generated
   */
  @Override
  public EAttribute getFunctionParameter_Direction() {
    return (EAttribute) this.functionParameterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the function parameter type
   * @generated
   */
  @Override
  public EAttribute getFunctionParameter_Type() {
    return (EAttribute) this.functionParameterEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the function parameter is configuration parameter
   * @generated
   */
  @Override
  public EAttribute getFunctionParameter_IsConfigurationParameter() {
    return (EAttribute) this.functionParameterEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the data port
   * @generated
   */
  @Override
  public EClass getDataPort() {
    return this.dataPortEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EReference getDataPort_PortRateExpression() {
    return (EReference) this.dataPortEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the data port annotation
   * @generated
   */
  @Override
  public EAttribute getDataPort_Annotation() {
    return (EAttribute) this.dataPortEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the broadcast actor
   * @generated
   */
  @Override
  public EClass getBroadcastActor() {
    return this.broadcastActorEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the join actor
   * @generated
   */
  @Override
  public EClass getJoinActor() {
    return this.joinActorEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fork actor
   * @generated
   */
  @Override
  public EClass getForkActor() {
    return this.forkActorEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the round buffer actor
   * @generated
   */
  @Override
  public EClass getRoundBufferActor() {
    return this.roundBufferActorEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the executable actor
   * @generated
   */
  @Override
  public EClass getExecutableActor() {
    return this.executableActorEClass;
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
   * @return the port memory annotation
   * @generated
   */
  @Override
  public EEnum getPortMemoryAnnotation() {
    return this.portMemoryAnnotationEEnum;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the i path
   * @generated
   */
  @Override
  public EDataType getIPath() {
    return this.iPathEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the config input interface
   * @generated
   */
  @Override
  public EClass getConfigInputInterface() {
    return this.configInputInterfaceEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the pi MM factory
   * @generated
   */
  @Override
  public PiMMFactory getPiMMFactory() {
    return (PiMMFactory) getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package. This method is guarded to have no affect on any invocation but its first. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  public void createPackageContents() {
    if (this.isCreated) {
      return;
    }
    this.isCreated = true;

    // Create classes and their features
    this.parameterizableEClass = createEClass(PiMMPackage.PARAMETERIZABLE);

    this.abstractVertexEClass = createEClass(PiMMPackage.ABSTRACT_VERTEX);
    createEAttribute(this.abstractVertexEClass, PiMMPackage.ABSTRACT_VERTEX__NAME);

    this.configurableEClass = createEClass(PiMMPackage.CONFIGURABLE);
    createEReference(this.configurableEClass, PiMMPackage.CONFIGURABLE__CONFIG_INPUT_PORTS);

    this.abstractActorEClass = createEClass(PiMMPackage.ABSTRACT_ACTOR);
    createEReference(this.abstractActorEClass, PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS);
    createEReference(this.abstractActorEClass, PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS);
    createEReference(this.abstractActorEClass, PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS);

    this.piGraphEClass = createEClass(PiMMPackage.PI_GRAPH);
    createEReference(this.piGraphEClass, PiMMPackage.PI_GRAPH__VERTICES);
    createEReference(this.piGraphEClass, PiMMPackage.PI_GRAPH__FIFOS);
    createEReference(this.piGraphEClass, PiMMPackage.PI_GRAPH__PARAMETERS);
    createEReference(this.piGraphEClass, PiMMPackage.PI_GRAPH__DEPENDENCIES);

    this.executableActorEClass = createEClass(PiMMPackage.EXECUTABLE_ACTOR);

    this.actorEClass = createEClass(PiMMPackage.ACTOR);
    createEReference(this.actorEClass, PiMMPackage.ACTOR__REFINEMENT);
    createEAttribute(this.actorEClass, PiMMPackage.ACTOR__MEMORY_SCRIPT_PATH);

    this.broadcastActorEClass = createEClass(PiMMPackage.BROADCAST_ACTOR);

    this.joinActorEClass = createEClass(PiMMPackage.JOIN_ACTOR);

    this.forkActorEClass = createEClass(PiMMPackage.FORK_ACTOR);

    this.roundBufferActorEClass = createEClass(PiMMPackage.ROUND_BUFFER_ACTOR);

    this.portEClass = createEClass(PiMMPackage.PORT);
    createEAttribute(this.portEClass, PiMMPackage.PORT__NAME);
    createEAttribute(this.portEClass, PiMMPackage.PORT__KIND);

    this.dataInputPortEClass = createEClass(PiMMPackage.DATA_INPUT_PORT);
    createEReference(this.dataInputPortEClass, PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO);

    this.dataOutputPortEClass = createEClass(PiMMPackage.DATA_OUTPUT_PORT);
    createEReference(this.dataOutputPortEClass, PiMMPackage.DATA_OUTPUT_PORT__OUTGOING_FIFO);

    this.configInputPortEClass = createEClass(PiMMPackage.CONFIG_INPUT_PORT);
    createEReference(this.configInputPortEClass, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY);
    createEReference(this.configInputPortEClass, PiMMPackage.CONFIG_INPUT_PORT__CONFIGURABLE);

    this.configOutputPortEClass = createEClass(PiMMPackage.CONFIG_OUTPUT_PORT);

    this.fifoEClass = createEClass(PiMMPackage.FIFO);
    createEReference(this.fifoEClass, PiMMPackage.FIFO__SOURCE_PORT);
    createEReference(this.fifoEClass, PiMMPackage.FIFO__TARGET_PORT);
    createEReference(this.fifoEClass, PiMMPackage.FIFO__DELAY);
    createEAttribute(this.fifoEClass, PiMMPackage.FIFO__ID);
    createEAttribute(this.fifoEClass, PiMMPackage.FIFO__TYPE);

    this.interfaceActorEClass = createEClass(PiMMPackage.INTERFACE_ACTOR);
    createEReference(this.interfaceActorEClass, PiMMPackage.INTERFACE_ACTOR__GRAPH_PORT);
    createEAttribute(this.interfaceActorEClass, PiMMPackage.INTERFACE_ACTOR__KIND);

    this.dataInputInterfaceEClass = createEClass(PiMMPackage.DATA_INPUT_INTERFACE);

    this.dataOutputInterfaceEClass = createEClass(PiMMPackage.DATA_OUTPUT_INTERFACE);

    this.configInputInterfaceEClass = createEClass(PiMMPackage.CONFIG_INPUT_INTERFACE);

    this.configOutputInterfaceEClass = createEClass(PiMMPackage.CONFIG_OUTPUT_INTERFACE);

    this.refinementEClass = createEClass(PiMMPackage.REFINEMENT);
    createEAttribute(this.refinementEClass, PiMMPackage.REFINEMENT__FILE_PATH);

    this.piSDFRefinementEClass = createEClass(PiMMPackage.PI_SDF_REFINEMENT);

    this.cHeaderRefinementEClass = createEClass(PiMMPackage.CHEADER_REFINEMENT);
    createEReference(this.cHeaderRefinementEClass, PiMMPackage.CHEADER_REFINEMENT__LOOP_PROTOTYPE);
    createEReference(this.cHeaderRefinementEClass, PiMMPackage.CHEADER_REFINEMENT__INIT_PROTOTYPE);

    this.parameterEClass = createEClass(PiMMPackage.PARAMETER);
    createEReference(this.parameterEClass, PiMMPackage.PARAMETER__GRAPH_PORT);
    createEReference(this.parameterEClass, PiMMPackage.PARAMETER__VALUE_EXPRESSION);

    this.dependencyEClass = createEClass(PiMMPackage.DEPENDENCY);
    createEReference(this.dependencyEClass, PiMMPackage.DEPENDENCY__SETTER);
    createEReference(this.dependencyEClass, PiMMPackage.DEPENDENCY__GETTER);

    this.iSetterEClass = createEClass(PiMMPackage.ISETTER);
    createEReference(this.iSetterEClass, PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES);

    this.delayEClass = createEClass(PiMMPackage.DELAY);
    createEReference(this.delayEClass, PiMMPackage.DELAY__SIZE_EXPRESSION);

    this.expressionEClass = createEClass(PiMMPackage.EXPRESSION);
    createEAttribute(this.expressionEClass, PiMMPackage.EXPRESSION__EXPRESSION_STRING);

    this.functionPrototypeEClass = createEClass(PiMMPackage.FUNCTION_PROTOTYPE);
    createEAttribute(this.functionPrototypeEClass, PiMMPackage.FUNCTION_PROTOTYPE__NAME);
    createEReference(this.functionPrototypeEClass, PiMMPackage.FUNCTION_PROTOTYPE__PARAMETERS);

    this.functionParameterEClass = createEClass(PiMMPackage.FUNCTION_PARAMETER);
    createEAttribute(this.functionParameterEClass, PiMMPackage.FUNCTION_PARAMETER__NAME);
    createEAttribute(this.functionParameterEClass, PiMMPackage.FUNCTION_PARAMETER__DIRECTION);
    createEAttribute(this.functionParameterEClass, PiMMPackage.FUNCTION_PARAMETER__TYPE);
    createEAttribute(this.functionParameterEClass, PiMMPackage.FUNCTION_PARAMETER__IS_CONFIGURATION_PARAMETER);

    this.dataPortEClass = createEClass(PiMMPackage.DATA_PORT);
    createEReference(this.dataPortEClass, PiMMPackage.DATA_PORT__PORT_RATE_EXPRESSION);
    createEAttribute(this.dataPortEClass, PiMMPackage.DATA_PORT__ANNOTATION);

    // Create enums
    this.directionEEnum = createEEnum(PiMMPackage.DIRECTION);
    this.portMemoryAnnotationEEnum = createEEnum(PiMMPackage.PORT_MEMORY_ANNOTATION);

    // Create data types
    this.iPathEDataType = createEDataType(PiMMPackage.IPATH);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model. This method is guarded to have no affect on any invocation but its first. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public void initializePackageContents() {
    if (this.isInitialized) {
      return;
    }
    this.isInitialized = true;

    // Initialize package
    setName(PiMMPackage.eNAME);
    setNsPrefix(PiMMPackage.eNS_PREFIX);
    setNsURI(PiMMPackage.eNS_URI);

    // Obtain other dependent packages
    final VisitorPackage theVisitorPackage = (VisitorPackage) EPackage.Registry.INSTANCE.getEPackage(VisitorPackage.eNS_URI);

    // Add subpackages
    getESubpackages().add(theVisitorPackage);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    this.parameterizableEClass.getESuperTypes().add(theVisitorPackage.getPiMMVisitable());
    this.abstractVertexEClass.getESuperTypes().add(theVisitorPackage.getPiMMVisitable());
    this.configurableEClass.getESuperTypes().add(getAbstractVertex());
    this.configurableEClass.getESuperTypes().add(getParameterizable());
    this.abstractActorEClass.getESuperTypes().add(getConfigurable());
    this.piGraphEClass.getESuperTypes().add(getAbstractActor());
    this.executableActorEClass.getESuperTypes().add(getAbstractActor());
    this.actorEClass.getESuperTypes().add(getExecutableActor());
    this.broadcastActorEClass.getESuperTypes().add(getExecutableActor());
    this.joinActorEClass.getESuperTypes().add(getExecutableActor());
    this.forkActorEClass.getESuperTypes().add(getExecutableActor());
    this.roundBufferActorEClass.getESuperTypes().add(getExecutableActor());
    this.portEClass.getESuperTypes().add(theVisitorPackage.getPiMMVisitable());
    this.dataInputPortEClass.getESuperTypes().add(getDataPort());
    this.dataOutputPortEClass.getESuperTypes().add(getDataPort());
    this.configInputPortEClass.getESuperTypes().add(getPort());
    this.configOutputPortEClass.getESuperTypes().add(getDataOutputPort());
    this.configOutputPortEClass.getESuperTypes().add(getISetter());
    this.fifoEClass.getESuperTypes().add(theVisitorPackage.getPiMMVisitable());
    this.interfaceActorEClass.getESuperTypes().add(getAbstractActor());
    this.dataInputInterfaceEClass.getESuperTypes().add(getInterfaceActor());
    this.dataOutputInterfaceEClass.getESuperTypes().add(getInterfaceActor());
    this.configInputInterfaceEClass.getESuperTypes().add(getParameter());
    this.configOutputInterfaceEClass.getESuperTypes().add(getInterfaceActor());
    this.refinementEClass.getESuperTypes().add(theVisitorPackage.getPiMMVisitable());
    this.piSDFRefinementEClass.getESuperTypes().add(getRefinement());
    this.cHeaderRefinementEClass.getESuperTypes().add(getRefinement());
    this.parameterEClass.getESuperTypes().add(getConfigurable());
    this.parameterEClass.getESuperTypes().add(getISetter());
    this.dependencyEClass.getESuperTypes().add(theVisitorPackage.getPiMMVisitable());
    this.delayEClass.getESuperTypes().add(getConfigurable());
    this.expressionEClass.getESuperTypes().add(theVisitorPackage.getPiMMVisitable());
    this.functionPrototypeEClass.getESuperTypes().add(theVisitorPackage.getPiMMVisitable());
    this.functionParameterEClass.getESuperTypes().add(theVisitorPackage.getPiMMVisitable());
    this.dataPortEClass.getESuperTypes().add(getPort());

    // Initialize classes and features; add operations and parameters
    initEClass(this.parameterizableEClass, Parameterizable.class, "Parameterizable", EPackageImpl.IS_ABSTRACT, EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    addEOperation(this.parameterizableEClass, getParameter(), "getInputParameters", 0, -1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    addEOperation(this.parameterizableEClass, this.ecorePackage.getEBoolean(), "isLocallyStatic", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    initEClass(this.abstractVertexEClass, AbstractVertex.class, "AbstractVertex", EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAbstractVertex_Name(), this.ecorePackage.getEString(), "name", null, 1, 1, AbstractVertex.class, !EPackageImpl.IS_TRANSIENT,
        !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE,
        !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    addEOperation(this.abstractVertexEClass, getPort(), "getAllPorts", 0, -1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    EOperation op = addEOperation(this.abstractVertexEClass, getPort(), "lookupPort", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, this.ecorePackage.getEString(), "portName", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    initEClass(this.configurableEClass, Configurable.class, "Configurable", EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getConfigurable_ConfigInputPorts(), getConfigInputPort(), getConfigInputPort_Configurable(), "configInputPorts", null, 0, -1,
        Configurable.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, EPackageImpl.IS_COMPOSITE,
        !EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    addEOperation(this.configurableEClass, getParameter(), "getInputParameters", 0, -1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.configurableEClass, getPort(), "lookupPortConnectedWithParameter", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, getParameter(), "parameter", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    addEOperation(this.configurableEClass, getPort(), "getAllConfigPorts", 0, -1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    addEOperation(this.configurableEClass, getPort(), "getAllPorts", 0, -1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    initEClass(this.abstractActorEClass, AbstractActor.class, "AbstractActor", EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAbstractActor_DataInputPorts(), getDataInputPort(), null, "dataInputPorts", null, 0, -1, AbstractActor.class, !EPackageImpl.IS_TRANSIENT,
        !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, EPackageImpl.IS_COMPOSITE, !EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getAbstractActor_DataOutputPorts(), getDataOutputPort(), null, "dataOutputPorts", null, 0, -1, AbstractActor.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, EPackageImpl.IS_COMPOSITE, !EPackageImpl.IS_RESOLVE_PROXIES,
        !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getAbstractActor_ConfigOutputPorts(), getConfigOutputPort(), null, "configOutputPorts", null, 0, -1, AbstractActor.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, EPackageImpl.IS_COMPOSITE, !EPackageImpl.IS_RESOLVE_PROXIES,
        !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    addEOperation(this.abstractActorEClass, getDataPort(), "getAllDataPorts", 0, -1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    addEOperation(this.abstractActorEClass, getPort(), "getAllConfigPorts", 0, -1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    addEOperation(this.abstractActorEClass, getPort(), "getAllPorts", 0, -1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    initEClass(this.piGraphEClass, PiGraph.class, "PiGraph", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getPiGraph_Vertices(), getAbstractActor(), null, "vertices", null, 0, -1, PiGraph.class, !EPackageImpl.IS_TRANSIENT,
        !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, EPackageImpl.IS_COMPOSITE, !EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getPiGraph_Fifos(), getFifo(), null, "fifos", null, 0, -1, PiGraph.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE,
        EPackageImpl.IS_CHANGEABLE, EPackageImpl.IS_COMPOSITE, !EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE,
        !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getPiGraph_Parameters(), getParameter(), null, "parameters", null, 0, -1, PiGraph.class, !EPackageImpl.IS_TRANSIENT,
        !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, EPackageImpl.IS_COMPOSITE, !EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getPiGraph_Dependencies(), getDependency(), null, "dependencies", null, 0, -1, PiGraph.class, !EPackageImpl.IS_TRANSIENT,
        !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, EPackageImpl.IS_COMPOSITE, !EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    addEOperation(this.piGraphEClass, this.ecorePackage.getEString(), "getVerticesNames", 0, -1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    addEOperation(this.piGraphEClass, this.ecorePackage.getEString(), "getParametersNames", 0, -1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    addEOperation(this.piGraphEClass, getActor(), "getActors", 0, -1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    addEOperation(this.piGraphEClass, getParameter(), "getAllParameters", 0, -1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    initEClass(this.executableActorEClass, ExecutableActor.class, "ExecutableActor", EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    initEClass(this.actorEClass, Actor.class, "Actor", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getActor_Refinement(), getRefinement(), null, "refinement", null, 1, 1, Actor.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE,
        EPackageImpl.IS_CHANGEABLE, EPackageImpl.IS_COMPOSITE, !EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE,
        !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEAttribute(getActor_MemoryScriptPath(), getIPath(), "memoryScriptPath", null, 0, 1, Actor.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE,
        EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);

    addEOperation(this.actorEClass, this.ecorePackage.getEBoolean(), "isConfigurationActor", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    addEOperation(this.actorEClass, this.ecorePackage.getEBoolean(), "isHierarchical", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    initEClass(this.broadcastActorEClass, BroadcastActor.class, "BroadcastActor", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    initEClass(this.joinActorEClass, JoinActor.class, "JoinActor", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    initEClass(this.forkActorEClass, ForkActor.class, "ForkActor", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    initEClass(this.roundBufferActorEClass, RoundBufferActor.class, "RoundBufferActor", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    initEClass(this.portEClass, Port.class, "Port", EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPort_Name(), this.ecorePackage.getEString(), "name", null, 0, 1, Port.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE,
        EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);
    initEAttribute(getPort_Kind(), this.ecorePackage.getEString(), "kind", null, 1, 1, Port.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE,
        !EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);

    initEClass(this.dataInputPortEClass, DataInputPort.class, "DataInputPort", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDataInputPort_IncomingFifo(), getFifo(), getFifo_TargetPort(), "incomingFifo", null, 0, 1, DataInputPort.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE, EPackageImpl.IS_RESOLVE_PROXIES,
        !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    initEClass(this.dataOutputPortEClass, DataOutputPort.class, "DataOutputPort", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDataOutputPort_OutgoingFifo(), getFifo(), getFifo_SourcePort(), "outgoingFifo", null, 0, 1, DataOutputPort.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE, EPackageImpl.IS_RESOLVE_PROXIES,
        !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    initEClass(this.configInputPortEClass, ConfigInputPort.class, "ConfigInputPort", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getConfigInputPort_IncomingDependency(), getDependency(), getDependency_Getter(), "incomingDependency", null, 0, 1, ConfigInputPort.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE, EPackageImpl.IS_RESOLVE_PROXIES,
        !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getConfigInputPort_Configurable(), getConfigurable(), getConfigurable_ConfigInputPorts(), "configurable", null, 0, 1, ConfigInputPort.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE, !EPackageImpl.IS_RESOLVE_PROXIES,
        !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    initEClass(this.configOutputPortEClass, ConfigOutputPort.class, "ConfigOutputPort", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    addEOperation(this.configOutputPortEClass, this.ecorePackage.getEBoolean(), "isLocallyStatic", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    initEClass(this.fifoEClass, Fifo.class, "Fifo", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getFifo_SourcePort(), getDataOutputPort(), getDataOutputPort_OutgoingFifo(), "sourcePort", null, 1, 1, Fifo.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE, EPackageImpl.IS_RESOLVE_PROXIES,
        !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getFifo_TargetPort(), getDataInputPort(), getDataInputPort_IncomingFifo(), "targetPort", null, 1, 1, Fifo.class, !EPackageImpl.IS_TRANSIENT,
        !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE, EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getFifo_Delay(), getDelay(), null, "delay", null, 0, 1, Fifo.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE,
        EPackageImpl.IS_CHANGEABLE, EPackageImpl.IS_COMPOSITE, !EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE,
        !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEAttribute(getFifo_Id(), this.ecorePackage.getEString(), "id", null, 1, 1, Fifo.class, !EPackageImpl.IS_TRANSIENT, EPackageImpl.IS_VOLATILE,
        !EPackageImpl.IS_CHANGEABLE, EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEAttribute(getFifo_Type(), this.ecorePackage.getEString(), "type", "void", 1, 1, Fifo.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE,
        EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);

    initEClass(this.interfaceActorEClass, InterfaceActor.class, "InterfaceActor", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getInterfaceActor_GraphPort(), getPort(), null, "graphPort", null, 1, 1, InterfaceActor.class, !EPackageImpl.IS_TRANSIENT,
        !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE, EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEAttribute(getInterfaceActor_Kind(), this.ecorePackage.getEString(), "kind", null, 1, 1, InterfaceActor.class, !EPackageImpl.IS_TRANSIENT,
        !EPackageImpl.IS_VOLATILE, !EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE,
        !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    addEOperation(this.interfaceActorEClass, getDataPort(), "getDataPort", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    initEClass(this.dataInputInterfaceEClass, DataInputInterface.class, "DataInputInterface", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    initEClass(this.dataOutputInterfaceEClass, DataOutputInterface.class, "DataOutputInterface", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    initEClass(this.configInputInterfaceEClass, ConfigInputInterface.class, "ConfigInputInterface", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    addEOperation(this.configInputInterfaceEClass, this.ecorePackage.getEBoolean(), "isLocallyStatic", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    addEOperation(this.configInputInterfaceEClass, this.ecorePackage.getEBoolean(), "isConfigurationInterface", 0, 1, EPackageImpl.IS_UNIQUE,
        EPackageImpl.IS_ORDERED);

    initEClass(this.configOutputInterfaceEClass, ConfigOutputInterface.class, "ConfigOutputInterface", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    initEClass(this.refinementEClass, Refinement.class, "Refinement", EPackageImpl.IS_ABSTRACT, EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getRefinement_FilePath(), getIPath(), "filePath", null, 0, 1, Refinement.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE,
        EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED,
        EPackageImpl.IS_ORDERED);

    addEOperation(this.refinementEClass, getAbstractActor(), "getAbstractActor", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    addEOperation(this.refinementEClass, this.ecorePackage.getEString(), "getFileName", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    addEOperation(this.refinementEClass, this.ecorePackage.getEBoolean(), "isHierarchical", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    initEClass(this.piSDFRefinementEClass, PiSDFRefinement.class, "PiSDFRefinement", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    addEOperation(this.piSDFRefinementEClass, this.ecorePackage.getEBoolean(), "isHierarchical", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    initEClass(this.cHeaderRefinementEClass, CHeaderRefinement.class, "CHeaderRefinement", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCHeaderRefinement_LoopPrototype(), getFunctionPrototype(), null, "loopPrototype", null, 1, 1, CHeaderRefinement.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, EPackageImpl.IS_COMPOSITE, !EPackageImpl.IS_RESOLVE_PROXIES,
        !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getCHeaderRefinement_InitPrototype(), getFunctionPrototype(), null, "initPrototype", null, 0, 1, CHeaderRefinement.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, EPackageImpl.IS_COMPOSITE, !EPackageImpl.IS_RESOLVE_PROXIES,
        !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    addEOperation(this.cHeaderRefinementEClass, this.ecorePackage.getEBoolean(), "isHierarchical", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    initEClass(this.parameterEClass, Parameter.class, "Parameter", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getParameter_GraphPort(), getConfigInputPort(), null, "graphPort", null, 1, 1, Parameter.class, !EPackageImpl.IS_TRANSIENT,
        !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE, EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getParameter_ValueExpression(), getExpression(), null, "valueExpression", null, 1, 1, Parameter.class, !EPackageImpl.IS_TRANSIENT,
        !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, EPackageImpl.IS_COMPOSITE, !EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    addEOperation(this.parameterEClass, this.ecorePackage.getEBoolean(), "isLocallyStatic", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    addEOperation(this.parameterEClass, this.ecorePackage.getEBoolean(), "isDependent", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    addEOperation(this.parameterEClass, this.ecorePackage.getEBoolean(), "isConfigurationInterface", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    initEClass(this.dependencyEClass, Dependency.class, "Dependency", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDependency_Setter(), getISetter(), getISetter_OutgoingDependencies(), "setter", null, 1, 1, Dependency.class, !EPackageImpl.IS_TRANSIENT,
        !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE, EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getDependency_Getter(), getConfigInputPort(), getConfigInputPort_IncomingDependency(), "getter", null, 1, 1, Dependency.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE, EPackageImpl.IS_RESOLVE_PROXIES,
        !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    initEClass(this.iSetterEClass, ISetter.class, "ISetter", EPackageImpl.IS_ABSTRACT, EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getISetter_OutgoingDependencies(), getDependency(), getDependency_Setter(), "outgoingDependencies", null, 0, -1, ISetter.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_COMPOSITE, EPackageImpl.IS_RESOLVE_PROXIES,
        !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    addEOperation(this.iSetterEClass, this.ecorePackage.getEBoolean(), "isLocallyStatic", 0, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    initEClass(this.delayEClass, Delay.class, "Delay", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDelay_SizeExpression(), getExpression(), null, "sizeExpression", null, 1, 1, Delay.class, !EPackageImpl.IS_TRANSIENT,
        !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, EPackageImpl.IS_COMPOSITE, !EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    initEClass(this.expressionEClass, Expression.class, "Expression", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getExpression_ExpressionString(), this.ecorePackage.getEString(), "expressionString", "0", 1, 1, Expression.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    addEOperation(this.expressionEClass, this.ecorePackage.getEString(), "evaluate", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    initEClass(this.functionPrototypeEClass, FunctionPrototype.class, "FunctionPrototype", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getFunctionPrototype_Name(), this.ecorePackage.getEString(), "name", null, 0, 1, FunctionPrototype.class, !EPackageImpl.IS_TRANSIENT,
        !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE,
        !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEReference(getFunctionPrototype_Parameters(), getFunctionParameter(), null, "parameters", null, 0, -1, FunctionPrototype.class,
        !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, EPackageImpl.IS_COMPOSITE, !EPackageImpl.IS_RESOLVE_PROXIES,
        !EPackageImpl.IS_UNSETTABLE, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    initEClass(this.functionParameterEClass, FunctionParameter.class, "FunctionParameter", !EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getFunctionParameter_Name(), this.ecorePackage.getEString(), "name", null, 0, 1, FunctionParameter.class, !EPackageImpl.IS_TRANSIENT,
        !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE,
        !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEAttribute(getFunctionParameter_Direction(), getDirection(), "direction", null, 0, 1, FunctionParameter.class, !EPackageImpl.IS_TRANSIENT,
        !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE,
        !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEAttribute(getFunctionParameter_Type(), this.ecorePackage.getEString(), "type", null, 0, 1, FunctionParameter.class, !EPackageImpl.IS_TRANSIENT,
        !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE,
        !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEAttribute(getFunctionParameter_IsConfigurationParameter(), this.ecorePackage.getEBoolean(), "isConfigurationParameter", null, 0, 1,
        FunctionParameter.class, !EPackageImpl.IS_TRANSIENT, !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE,
        !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    initEClass(this.dataPortEClass, DataPort.class, "DataPort", EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE, EPackageImpl.IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDataPort_PortRateExpression(), getExpression(), null, "portRateExpression", null, 1, 1, DataPort.class, !EPackageImpl.IS_TRANSIENT,
        !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, EPackageImpl.IS_COMPOSITE, !EPackageImpl.IS_RESOLVE_PROXIES, !EPackageImpl.IS_UNSETTABLE,
        EPackageImpl.IS_UNIQUE, !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);
    initEAttribute(getDataPort_Annotation(), getPortMemoryAnnotation(), "annotation", null, 0, 1, DataPort.class, !EPackageImpl.IS_TRANSIENT,
        !EPackageImpl.IS_VOLATILE, EPackageImpl.IS_CHANGEABLE, !EPackageImpl.IS_UNSETTABLE, !EPackageImpl.IS_ID, EPackageImpl.IS_UNIQUE,
        !EPackageImpl.IS_DERIVED, EPackageImpl.IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(this.directionEEnum, Direction.class, "Direction");
    addEEnumLiteral(this.directionEEnum, Direction.IN);
    addEEnumLiteral(this.directionEEnum, Direction.OUT);

    initEEnum(this.portMemoryAnnotationEEnum, PortMemoryAnnotation.class, "PortMemoryAnnotation");
    addEEnumLiteral(this.portMemoryAnnotationEEnum, PortMemoryAnnotation.NONE);
    addEEnumLiteral(this.portMemoryAnnotationEEnum, PortMemoryAnnotation.READ_ONLY);
    addEEnumLiteral(this.portMemoryAnnotationEEnum, PortMemoryAnnotation.WRITE_ONLY);
    addEEnumLiteral(this.portMemoryAnnotationEEnum, PortMemoryAnnotation.UNUSED);

    // Initialize data types
    initEDataType(this.iPathEDataType, IPath.class, "IPath", EPackageImpl.IS_SERIALIZABLE, !EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(PiMMPackage.eNS_URI);
  }

} // PiMMPackageImpl
