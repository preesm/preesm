/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2014)
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
package org.ietr.preesm.codegen.xtend.model.codegen;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> A {@link SubBuffer} is used to access a limited portion of a parent {@link Buffer}. <br>
 * The {@link #getOffset() offset} of a {@link SubBuffer} gives the position of the portion of a {@link Buffer} accessed by this {@link SubBuffer}. The parent
 * {@link Buffer} of a {@link SubBuffer} is called its {@link #getContainer() container}. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer#getContainer <em>Container</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer#getOffset <em>Offset</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getSubBuffer()
 * @model
 * @generated
 */
public interface SubBuffer extends Buffer {
  /**
   * Returns the value of the '<em><b>Container</b></em>' reference. It is bidirectional and its opposite is
   * '{@link org.ietr.preesm.codegen.xtend.model.codegen.Buffer#getChildrens <em>Childrens</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Container</em>' reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Container</em>' reference.
   * @see #setContainer(Buffer)
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getSubBuffer_Container()
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Buffer#getChildrens
   * @model opposite="childrens" required="true"
   * @generated
   */
  Buffer getContainer();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer#getContainer <em>Container</em>}' reference. <!-- begin-user-doc -->
   * The container of the {@link SubBuffer} is a {@link Buffer} containing the current {@link SubBuffer}. If the {@link SubBuffer} already has a
   * {@link #getCreator() creator}, it will be added to the {@link #getUsers()} list of the new container. (If an old container is replaced, the creator of the
   * current SubBuffer will not be removed from its users list)<!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Container</em>' reference.
   * @see #getContainer()
   * @generated
   */
  void setContainer(Buffer value);

  /**
   * Returns the value of the '<em><b>Offset</b></em>' attribute. <!-- begin-user-doc --> For coherence reason, offset is expressed in bytes, no matter the
   * {@link #getType()} of the {@link SubBuffer} or the type of its {@link #getContainer()}. <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Offset</em>' attribute.
   * @see #setOffset(int)
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getSubBuffer_Offset()
   * @model required="true"
   * @generated
   */
  int getOffset();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer#getOffset <em>Offset</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Offset</em>' attribute.
   * @see #getOffset()
   * @generated
   */
  void setOffset(int value);

} // SubBuffer
