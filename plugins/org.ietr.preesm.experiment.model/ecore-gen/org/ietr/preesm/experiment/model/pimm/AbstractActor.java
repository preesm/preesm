/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Abstract Actor</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.AbstractActor#getDataInputPorts <em>Data Input Ports</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.AbstractActor#getDataOutputPorts <em>Data Output Ports</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.AbstractActor#getConfigOutputPorts <em>Config Output Ports</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.AbstractActor#getContainingGraph <em>Containing Graph</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getAbstractActor()
 * @model abstract="true"
 * @generated
 */
public interface AbstractActor extends Configurable {
  /**
   * Returns the value of the '<em><b>Data Input Ports</b></em>' containment reference list. The list contents are of type
   * {@link org.ietr.preesm.experiment.model.pimm.DataInputPort}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Data Input Ports</em>' containment reference list isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Data Input Ports</em>' containment reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getAbstractActor_DataInputPorts()
   * @model containment="true"
   * @generated
   */
  EList<DataInputPort> getDataInputPorts();

  /**
   * Returns the value of the '<em><b>Data Output Ports</b></em>' containment reference list. The list contents are of type
   * {@link org.ietr.preesm.experiment.model.pimm.DataOutputPort}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Data Output Ports</em>' containment reference list isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Data Output Ports</em>' containment reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getAbstractActor_DataOutputPorts()
   * @model containment="true"
   * @generated
   */
  EList<DataOutputPort> getDataOutputPorts();

  /**
   * Returns the value of the '<em><b>Config Output Ports</b></em>' containment reference list. The list contents are of type
   * {@link org.ietr.preesm.experiment.model.pimm.ConfigOutputPort}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Config Output Ports</em>' containment reference list isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Config Output Ports</em>' containment reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getAbstractActor_ConfigOutputPorts()
   * @model containment="true"
   * @generated
   */
  EList<ConfigOutputPort> getConfigOutputPorts();

  /**
   * Returns the value of the '<em><b>Containing Graph</b></em>' container reference. It is bidirectional and its opposite is
   * '{@link org.ietr.preesm.experiment.model.pimm.PiGraph#getActors <em>Actors</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Containing Graph</em>' container reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Containing Graph</em>' container reference.
   * @see #setContainingGraph(PiGraph)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getAbstractActor_ContainingGraph()
   * @see org.ietr.preesm.experiment.model.pimm.PiGraph#getActors
   * @model opposite="actors" transient="false"
   * @generated
   */
  PiGraph getContainingGraph();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.AbstractActor#getContainingGraph <em>Containing Graph</em>}' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Containing Graph</em>' container reference.
   * @see #getContainingGraph()
   * @generated
   */
  void setContainingGraph(PiGraph value);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='final BasicEList&lt;DataPort&gt; result =
   *        ECollections.newBasicEList();\nresult.addAll(getDataInputPorts());\nresult.addAll(getDataOutputPorts());\nreturn
   *        ECollections.unmodifiableEList(result);'"
   * @generated
   */
  EList<DataPort> getAllDataPorts();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='final BasicEList&lt;Port&gt; result =
   *        ECollections.newBasicEList();\nresult.addAll(super.getAllConfigPorts());\nresult.addAll(getConfigOutputPorts());\nreturn
   *        ECollections.unmodifiableEList(result);'"
   * @generated
   */
  @Override
  EList<Port> getAllConfigPorts();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='final BasicEList&lt;Port&gt; result =
   *        ECollections.newBasicEList();\nresult.addAll(getAllConfigPorts());\nresult.addAll(getAllDataPorts());\nreturn
   *        ECollections.unmodifiableEList(result);'"
   * @generated
   */
  @Override
  EList<Port> getAllPorts();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='if (getContainingGraph() != null) {\n return
   *        getContainingGraph().getActorPath() + \"/\" + getName();\n}\nreturn getName();'"
   * @generated
   */
  String getActorPath();

} // AbstractActor
