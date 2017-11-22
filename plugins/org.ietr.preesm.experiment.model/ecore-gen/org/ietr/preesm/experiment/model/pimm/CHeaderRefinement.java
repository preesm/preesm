/**
 */
package org.ietr.preesm.experiment.model.pimm;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>CHeader Refinement</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.CHeaderRefinement#getLoopPrototype <em>Loop Prototype</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.CHeaderRefinement#getInitPrototype <em>Init Prototype</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getCHeaderRefinement()
 * @model
 * @generated
 */
public interface CHeaderRefinement extends Refinement {
  /**
   * Returns the value of the '<em><b>Loop Prototype</b></em>' containment reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Loop Prototype</em>' containment reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Loop Prototype</em>' containment reference.
   * @see #setLoopPrototype(FunctionPrototype)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getCHeaderRefinement_LoopPrototype()
   * @model containment="true" required="true"
   * @generated
   */
  FunctionPrototype getLoopPrototype();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.CHeaderRefinement#getLoopPrototype <em>Loop Prototype</em>}' containment reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Loop Prototype</em>' containment reference.
   * @see #getLoopPrototype()
   * @generated
   */
  void setLoopPrototype(FunctionPrototype value);

  /**
   * Returns the value of the '<em><b>Init Prototype</b></em>' containment reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Init Prototype</em>' containment reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Init Prototype</em>' containment reference.
   * @see #setInitPrototype(FunctionPrototype)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getCHeaderRefinement_InitPrototype()
   * @model containment="true"
   * @generated
   */
  FunctionPrototype getInitPrototype();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.CHeaderRefinement#getInitPrototype <em>Init Prototype</em>}' containment reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Init Prototype</em>' containment reference.
   * @see #getInitPrototype()
   * @generated
   */
  void setInitPrototype(FunctionPrototype value);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='//C Header Refinement means it is obviously not a subgraph\nreturn
   *        false;'"
   * @generated
   */
  @Override
  boolean isHierarchical();

} // CHeaderRefinement
