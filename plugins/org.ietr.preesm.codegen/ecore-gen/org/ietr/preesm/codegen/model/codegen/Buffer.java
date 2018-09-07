/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2015)
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
package org.ietr.preesm.codegen.model.codegen;

import org.eclipse.emf.common.util.EList;
import org.ietr.preesm.memory.script.Range;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> A {@link Buffer} is a {@link Variable} that represents an set of object of a given
 * {@link #getType() type}. It can be seen as equivalent to an array in C code. <br>
 * A {@link Buffer} has a {@link #getSize() size} which is the number of element of its {@link #getType() type} it can
 * store.<!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.Buffer#getSize <em>Size</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.Buffer#getChildrens <em>Childrens</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.Buffer#getTypeSize <em>Type Size</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.Buffer#getMergedRange <em>Merged Range</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.Buffer#isLocal <em>Local</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.codegen.model.codegen.CodegenPackage#getBuffer()
 * @model
 * @generated
 */
public interface Buffer extends Variable {
  /**
   * Returns the value of the '<em><b>Size</b></em>' attribute. <!-- begin-user-doc --> Number of elements within the
   * {@link Buffer}. Each element is of type {@link #getType()} and requires {@link #getTypeSize()} bytes for its
   * allocation. <!-- end-user-doc -->
   *
   * @return the value of the '<em>Size</em>' attribute.
   * @see #setSize(int)
   * @see org.ietr.preesm.codegen.model.codegen.CodegenPackage#getBuffer_Size()
   * @model required="true"
   * @generated
   */
  int getSize();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.model.codegen.Buffer#getSize <em>Size</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Size</em>' attribute.
   * @see #getSize()
   * @generated
   */
  void setSize(int value);

  /**
   * Returns the value of the '<em><b>Childrens</b></em>' reference list. The list contents are of type
   * {@link org.ietr.preesm.codegen.model.codegen.SubBuffer}. It is bidirectional and its opposite is
   * '{@link org.ietr.preesm.codegen.model.codegen.SubBuffer#getContainer <em>Container</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Childrens</em>' reference list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Childrens</em>' reference list.
   * @see org.ietr.preesm.codegen.model.codegen.CodegenPackage#getBuffer_Childrens()
   * @see org.ietr.preesm.codegen.model.codegen.SubBuffer#getContainer
   * @model opposite="container"
   * @generated
   */
  EList<SubBuffer> getChildrens();

  /**
   * Returns the value of the '<em><b>Type Size</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * This value corresponds to the size of the {@link Buffer} {@link #getType()} in bytes.
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Type Size</em>' attribute.
   * @see #setTypeSize(int)
   * @see org.ietr.preesm.codegen.model.codegen.CodegenPackage#getBuffer_TypeSize()
   * @model required="true"
   * @generated
   */
  int getTypeSize();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.model.codegen.Buffer#getTypeSize <em>Type Size</em>}'
   * attribute. <!-- begin-user-doc --><!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Type Size</em>' attribute.
   * @see #getTypeSize()
   * @generated
   */
  void setTypeSize(int value);

  /**
   * Returns the value of the '<em><b>Merged Range</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Merged Range</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Merged Range</em>' attribute.
   * @see #setMergedRange(EList)
   * @see org.ietr.preesm.codegen.model.codegen.CodegenPackage#getBuffer_MergedRange()
   * @model many="false" transient="true"
   * @generated
   */
  EList<Range> getMergedRange();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.model.codegen.Buffer#getMergedRange <em>Merged Range</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Merged Range</em>' attribute.
   * @see #getMergedRange()
   * @generated
   */
  void setMergedRange(EList<Range> value);

  /**
   * Returns the value of the '<em><b>Local</b></em>' attribute. The default value is <code>"false"</code>. <!--
   * begin-user-doc -->
   * <p>
   * This attribute specifies whether this {@link Buffer} is local to a single {@link CoreBlock} of (<code>true</code>)
   * or if it may be accessed from several {@link CoreBlock} (<code>false</code>).
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Local</em>' attribute.
   * @see #setLocal(boolean)
   * @see org.ietr.preesm.codegen.model.codegen.CodegenPackage#getBuffer_Local()
   * @model default="false"
   * @generated
   */
  boolean isLocal();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.model.codegen.Buffer#isLocal <em>Local</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Local</em>' attribute.
   * @see #isLocal()
   * @generated
   */
  void setLocal(boolean value);

} // Buffer
