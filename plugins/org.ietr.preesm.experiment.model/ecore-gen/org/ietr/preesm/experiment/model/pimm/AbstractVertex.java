/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Abstract Vertex</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.AbstractVertex#getName <em>Name</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getAbstractVertex()
 * @model abstract="true"
 * @generated
 */
public interface AbstractVertex extends EObject {
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getAbstractVertex_Name()
   * @model required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.AbstractVertex#getName <em>Name</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='final BasicEList&lt;Port&gt; result =
   *        ECollections.newBasicEList();\nreturn ECollections.unmodifiableEList(result);'"
   * @generated
   */
  EList<Port> getAllPorts();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model annotation="http://www.eclipse.org/emf/2002/GenModel body='return getAllPorts().stream().filter(Objects::nonNull).filter(p -&gt; (p.getName() ==
   *        null) &amp;&amp; (portName == null) || (p.getName() != null) &amp;&amp; p.getName().equals(portName)).findFirst().orElse(null);'"
   * @generated
   */
  Port lookupPort(String portName);

} // AbstractVertex
