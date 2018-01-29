/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Fifo</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Fifo#getSourcePort <em>Source Port</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Fifo#getTargetPort <em>Target Port</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Fifo#getDelay <em>Delay</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Fifo#getType <em>Type</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFifo()
 * @model
 * @generated
 */
public interface Fifo extends EObject {
  /**
   * Returns the value of the '<em><b>Source Port</b></em>' reference. It is bidirectional and its opposite is
   * '{@link org.ietr.preesm.experiment.model.pimm.DataOutputPort#getOutgoingFifo <em>Outgoing Fifo</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Source Port</em>' reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Source Port</em>' reference.
   * @see #setSourcePort(DataOutputPort)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFifo_SourcePort()
   * @see org.ietr.preesm.experiment.model.pimm.DataOutputPort#getOutgoingFifo
   * @model opposite="outgoingFifo" required="true"
   * @generated
   */
  DataOutputPort getSourcePort();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getSourcePort <em>Source Port</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Source Port</em>' reference.
   * @see #getSourcePort()
   * @generated
   */
  void setSourcePort(DataOutputPort value);

  /**
   * Returns the value of the '<em><b>Target Port</b></em>' reference. It is bidirectional and its opposite is
   * '{@link org.ietr.preesm.experiment.model.pimm.DataInputPort#getIncomingFifo <em>Incoming Fifo</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Target Port</em>' reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Target Port</em>' reference.
   * @see #setTargetPort(DataInputPort)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFifo_TargetPort()
   * @see org.ietr.preesm.experiment.model.pimm.DataInputPort#getIncomingFifo
   * @model opposite="incomingFifo" required="true"
   * @generated
   */
  DataInputPort getTargetPort();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getTargetPort <em>Target Port</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Target Port</em>' reference.
   * @see #getTargetPort()
   * @generated
   */
  void setTargetPort(DataInputPort value);

  /**
   * Returns the value of the '<em><b>Delay</b></em>' containment reference. It is bidirectional and its opposite is
   * '{@link org.ietr.preesm.experiment.model.pimm.Delay#getContainingFifo <em>Containing Fifo</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Delay</em>' containment reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Delay</em>' containment reference.
   * @see #setDelay(Delay)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFifo_Delay()
   * @see org.ietr.preesm.experiment.model.pimm.Delay#getContainingFifo
   * @model opposite="containingFifo" containment="true"
   * @generated
   */
  Delay getDelay();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getDelay <em>Delay</em>}' containment reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Delay</em>' containment reference.
   * @see #getDelay()
   * @generated
   */
  void setDelay(Delay value);

  /**
   * Returns the value of the '<em><b>Type</b></em>' attribute. The default value is <code>"void"</code>. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Type</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Type</em>' attribute.
   * @see #setType(String)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFifo_Type()
   * @model default="void" required="true"
   * @generated
   */
  String getType();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getType <em>Type</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Type</em>' attribute.
   * @see #getType()
   * @generated
   */
  void setType(String value);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='return Optional.of(getSourcePort()).map(DataPort::getId).orElseThrow(()
   *        -&gt; new PiGraphException(\"Fifo has no source port.\")) + \"-\" + Optional.of(getTargetPort()).map(DataPort::getId).orElseThrow(() -&gt; new
   *        PiGraphException(\"Fifo has no target port.\"));'"
   * @generated
   */
  String getId();

} // Fifo
