/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013 - 2014)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
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

import org.eclipse.core.runtime.IPath;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Actor</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Actor#getRefinement <em>Refinement</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Actor#getMemoryScriptPath <em>Memory Script Path</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getActor()
 * @model
 * @generated
 */
public interface Actor extends ExecutableActor {

  /**
   * Returns the value of the '<em><b>Refinement</b></em>' containment reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Refinement</em>' containment reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Refinement</em>' containment reference.
   * @see #setRefinement(Refinement)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getActor_Refinement()
   * @model containment="true" required="true"
   * @generated
   */
  Refinement getRefinement();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Actor#getRefinement <em>Refinement</em>}' containment reference. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Refinement</em>' containment reference.
   * @see #getRefinement()
   * @generated
   */
  void setRefinement(Refinement value);

  /**
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Configuration Actor</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='// an Actor is considered as a Configuration Actor iff it has at least a
   *        ConfigOutputPort that is connected to a getter\nreturn
   *        getConfigOutputPorts().stream().filter(Objects::nonNull).map(ConfigOutputPort::getOutgoingDependencies).filter(l -&gt; !l.isEmpty()).map(l -&gt;
   *        l.get(0))\n .map(Dependency::getGetter).filter(Objects::nonNull).anyMatch(x -&gt; true);'"
   * @generated
   */
  boolean isConfigurationActor();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='return getRefinement().isHierarchical();'"
   * @generated
   */
  boolean isHierarchical();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='return
   *        Optional.of(getRefinement()).map(Refinement::getAbstractActor).orElse(null);'"
   * @generated
   */
  AbstractActor getChildAbstractActor();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='if (isHierarchical()) {\n\treturn (PiGraph) getChildAbstractActor();\n}
   *        else {\n\tthrow new UnsupportedOperationException(\"Cannot get the subgraph of a non hierarchical actor.\");\n}'"
   * @generated
   */
  PiGraph getSubGraph();

  /**
   * Returns the value of the '<em><b>Memory Script Path</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Memory Script Path</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Memory Script Path</em>' attribute.
   * @see #setMemoryScriptPath(IPath)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getActor_MemoryScriptPath()
   * @model dataType="org.ietr.preesm.experiment.model.pimm.IPath"
   * @generated
   */
  IPath getMemoryScriptPath();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Actor#getMemoryScriptPath <em>Memory Script Path</em>}' attribute. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Memory Script Path</em>' attribute.
   * @see #getMemoryScriptPath()
   * @generated
   */
  void setMemoryScriptPath(IPath value);
} // Actor
