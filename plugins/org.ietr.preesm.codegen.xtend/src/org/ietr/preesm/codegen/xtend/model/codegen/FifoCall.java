/**
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
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
 */
package org.ietr.preesm.codegen.xtend.model.codegen;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Fifo Call</b></em>'. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getDelimiter
 * <em>Delimiter</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getFifoHead
 * <em>Fifo Head</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getFifoTail
 * <em>Fifo Tail</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getFifoCall()
 * @model
 * @generated
 */
public interface FifoCall extends Call {
	/**
	 * Returns the value of the '<em><b>Delimiter</b></em>' attribute. The
	 * literals are from the enumeration
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Delimiter}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Delimiter</em>' attribute isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Delimiter</em>' attribute.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Delimiter
	 * @see #setDelimiter(Delimiter)
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getFifoCall_Delimiter()
	 * @model required="true"
	 * @generated
	 */
	Delimiter getDelimiter();

	/**
	 * Sets the value of the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getDelimiter
	 * <em>Delimiter</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @param value
	 *            the new value of the '<em>Delimiter</em>' attribute.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.Delimiter
	 * @see #getDelimiter()
	 * @generated
	 */
	void setDelimiter(Delimiter value);

	/**
	 * Returns the value of the '<em><b>Fifo Head</b></em>' reference. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Fifo Head</em>' reference isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Fifo Head</em>' reference.
	 * @see #setFifoHead(FifoCall)
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getFifoCall_FifoHead()
	 * @model transient="true"
	 * @generated
	 */
	FifoCall getFifoHead();

	/**
	 * Sets the value of the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getFifoHead
	 * <em>Fifo Head</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @param value
	 *            the new value of the '<em>Fifo Head</em>' reference.
	 * @see #getFifoHead()
	 * @generated
	 */
	void setFifoHead(FifoCall value);

	/**
	 * Returns the value of the '<em><b>Fifo Tail</b></em>' reference. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Fifo Tail</em>' reference isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Fifo Tail</em>' reference.
	 * @see #setFifoTail(FifoCall)
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getFifoCall_FifoTail()
	 * @model transient="true"
	 * @generated
	 */
	FifoCall getFifoTail();

	/**
	 * Sets the value of the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getFifoTail
	 * <em>Fifo Tail</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @param value
	 *            the new value of the '<em>Fifo Tail</em>' reference.
	 * @see #getFifoTail()
	 * @generated
	 */
	void setFifoTail(FifoCall value);

} // FifoCall
