/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
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
package org.ietr.preesm.experiment.model.pimm;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Delay</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Delay#getSizeExpression <em>Size Expression</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Delay#getContainingFifo <em>Containing Fifo</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDelay()
 * @model
 * @generated
 */
public interface Delay extends Configurable {

  /**
   * Returns the value of the '<em><b>Size Expression</b></em>' containment reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Expression</em>' containment reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Size Expression</em>' containment reference.
   * @see #setSizeExpression(Expression)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDelay_SizeExpression()
   * @model containment="true" required="true"
   * @generated
   */
  Expression getSizeExpression();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Delay#getSizeExpression <em>Size Expression</em>}' containment reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Size Expression</em>' containment reference.
   * @see #getSizeExpression()
   * @generated
   */
  void setSizeExpression(Expression value);

  /**
   * Returns the value of the '<em><b>Containing Fifo</b></em>' container reference. It is bidirectional and its opposite is
   * '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getDelay <em>Delay</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Containing Fifo</em>' container reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Containing Fifo</em>' container reference.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDelay_ContainingFifo()
   * @see org.ietr.preesm.experiment.model.pimm.Fifo#getDelay
   * @model opposite="delay" required="true" transient="false" changeable="false"
   * @generated
   */
  Fifo getContainingFifo();
} // Delay
