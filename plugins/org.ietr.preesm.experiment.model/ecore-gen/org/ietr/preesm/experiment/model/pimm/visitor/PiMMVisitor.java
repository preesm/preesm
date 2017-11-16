/**
 */
package org.ietr.preesm.experiment.model.pimm.visitor;

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
import org.ietr.preesm.experiment.model.pimm.PiSDFRefinement;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Pi MM Visitor</b></em>'. <!-- end-user-doc -->
 *
 *
 * @see org.ietr.preesm.experiment.model.pimm.visitor.VisitorPackage#getPiMMVisitor()
 * @model abstract="true"
 * @generated
 */
public interface PiMMVisitor extends EObject {
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visit(PiMMVisitable subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitAbstractActor(AbstractActor subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitAbstractVertex(AbstractVertex subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitActor(Actor subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitConfigInputInterface(ConfigInputInterface subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitConfigInputPort(ConfigInputPort subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitConfigOutputInterface(ConfigOutputInterface subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitConfigOutputPort(ConfigOutputPort subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitDataInputInterface(DataInputInterface subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitDataInputPort(DataInputPort subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitDataOutputInterface(DataOutputInterface subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitDataOutputPort(DataOutputPort subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitDelay(Delay subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitDependency(Dependency subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitExpression(Expression subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitFifo(Fifo subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitInterfaceActor(InterfaceActor subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitISetter(ISetter subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitParameter(Parameter subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitParameterizable(Parameterizable subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitPiGraph(PiGraph subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitPort(Port subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitRefinement(PiSDFRefinement subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitFunctionParameter(FunctionParameter subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitFunctionPrototype(FunctionPrototype subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitHRefinement(CHeaderRefinement subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitDataPort(DataPort subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitBroadcastActor(BroadcastActor subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitJoinActor(JoinActor subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitForkActor(ForkActor subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitRoundBufferActor(RoundBufferActor subject);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void visitExecutableActor(ExecutableActor subject);

} // PiMMVisitor
