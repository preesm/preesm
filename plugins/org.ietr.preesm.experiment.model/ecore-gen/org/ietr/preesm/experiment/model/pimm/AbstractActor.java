/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Abstract Actor</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.AbstractActor#getDataInputPorts <em>Data Input Ports</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.AbstractActor#getDataOutputPorts <em>Data Output Ports</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.AbstractActor#getConfigOutputPorts <em>Config Output Ports</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getAbstractActor()
 * @model abstract="true"
 * @generated
 */
public interface AbstractActor extends Configurable {
  /**
   * Returns the value of the '<em><b>Data Input Ports</b></em>' containment reference list.
   * The list contents are of type {@link org.ietr.preesm.experiment.model.pimm.DataInputPort}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Data Input Ports</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Data Input Ports</em>' containment reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getAbstractActor_DataInputPorts()
   * @model containment="true"
   * @generated
   */
  EList<DataInputPort> getDataInputPorts();

  /**
   * Returns the value of the '<em><b>Data Output Ports</b></em>' containment reference list.
   * The list contents are of type {@link org.ietr.preesm.experiment.model.pimm.DataOutputPort}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Data Output Ports</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Data Output Ports</em>' containment reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getAbstractActor_DataOutputPorts()
   * @model containment="true"
   * @generated
   */
  EList<DataOutputPort> getDataOutputPorts();

  /**
   * Returns the value of the '<em><b>Config Output Ports</b></em>' containment reference list.
   * The list contents are of type {@link org.ietr.preesm.experiment.model.pimm.ConfigOutputPort}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Config Output Ports</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Config Output Ports</em>' containment reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getAbstractActor_ConfigOutputPorts()
   * @model containment="true"
   * @generated
   */
  EList<ConfigOutputPort> getConfigOutputPorts();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='&lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.DataInputPort%&gt;&gt; _dataInputPorts = this.getDataInputPorts();\n&lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.DataOutputPort%&gt;&gt; _dataOutputPorts = this.getDataOutputPorts();\nreturn &lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.DataPort%&gt;&gt;unmodifiableEList(&lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.DataPort%&gt;&gt;toEList(&lt;%com.google.common.collect.Iterables%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.DataPort%&gt;&gt;concat(_dataInputPorts, _dataOutputPorts)));'"
   * @generated
   */
  EList<DataPort> getAllDataPorts();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='&lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Port%&gt;&gt; _allConfigPorts = super.getAllConfigPorts();\n&lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ConfigOutputPort%&gt;&gt; _configOutputPorts = this.getConfigOutputPorts();\nreturn &lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Port%&gt;&gt;unmodifiableEList(&lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Port%&gt;&gt;toEList(&lt;%com.google.common.collect.Iterables%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Port%&gt;&gt;concat(_allConfigPorts, _configOutputPorts)));'"
   * @generated
   */
  EList<Port> getAllConfigPorts();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='&lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Port%&gt;&gt; _allConfigPorts = this.getAllConfigPorts();\n&lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.DataPort%&gt;&gt; _allDataPorts = this.getAllDataPorts();\nreturn &lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Port%&gt;&gt;unmodifiableEList(&lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Port%&gt;&gt;toEList(&lt;%com.google.common.collect.Iterables%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Port%&gt;&gt;concat(_allConfigPorts, _allDataPorts)));'"
   * @generated
   */
  EList<Port> getAllPorts();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * @deprecated use getVertexPath() instead;
   * <!-- end-model-doc -->
   * @model kind="operation" dataType="org.ietr.preesm.experiment.model.pimm.String" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return this.getVertexPath();'"
   * @generated
   */
  @Deprecated
  String getActorPath();

} // AbstractActor
