/**
 */
package org.ietr.dftools.architecture.slam;

import org.eclipse.emf.common.util.EList;

import org.ietr.dftools.architecture.slam.attributes.VLNV;

import org.ietr.dftools.architecture.slam.component.Component;
import org.ietr.dftools.architecture.slam.component.HierarchyPort;

import org.ietr.dftools.architecture.slam.link.Link;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Design</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.dftools.architecture.slam.Design#getComponentInstances <em>Component Instances</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.Design#getLinks <em>Links</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.Design#getHierarchyPorts <em>Hierarchy Ports</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.Design#getRefined <em>Refined</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.Design#getPath <em>Path</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.Design#getComponentHolder <em>Component Holder</em>}</li>
 * </ul>
 *
 * @see org.ietr.dftools.architecture.slam.SlamPackage#getDesign()
 * @model
 * @generated
 */
public interface Design extends VLNVedElement, ParameterizedElement {
	/**
	 * Returns the value of the '<em><b>Component Instances</b></em>' containment reference list.
	 * The list contents are of type {@link org.ietr.dftools.architecture.slam.ComponentInstance}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Component Instances</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Component Instances</em>' containment reference list.
	 * @see org.ietr.dftools.architecture.slam.SlamPackage#getDesign_ComponentInstances()
	 * @model containment="true" ordered="false"
	 * @generated
	 */
	EList<ComponentInstance> getComponentInstances();

	/**
	 * Returns the value of the '<em><b>Links</b></em>' containment reference list.
	 * The list contents are of type {@link org.ietr.dftools.architecture.slam.link.Link}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Links</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Links</em>' containment reference list.
	 * @see org.ietr.dftools.architecture.slam.SlamPackage#getDesign_Links()
	 * @model containment="true"
	 * @generated
	 */
	EList<Link> getLinks();

	/**
	 * Returns the value of the '<em><b>Hierarchy Ports</b></em>' containment reference list.
	 * The list contents are of type {@link org.ietr.dftools.architecture.slam.component.HierarchyPort}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Hierarchy Ports</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Hierarchy Ports</em>' containment reference list.
	 * @see org.ietr.dftools.architecture.slam.SlamPackage#getDesign_HierarchyPorts()
	 * @model containment="true"
	 * @generated
	 */
	EList<HierarchyPort> getHierarchyPorts();

	/**
	 * Returns the value of the '<em><b>Refined</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.ietr.dftools.architecture.slam.component.Component#getRefinements <em>Refinements</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Refined</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Refined</em>' container reference.
	 * @see #setRefined(Component)
	 * @see org.ietr.dftools.architecture.slam.SlamPackage#getDesign_Refined()
	 * @see org.ietr.dftools.architecture.slam.component.Component#getRefinements
	 * @model opposite="refinements" transient="false"
	 * @generated
	 */
	Component getRefined();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.Design#getRefined <em>Refined</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Refined</em>' container reference.
	 * @see #getRefined()
	 * @generated
	 */
	void setRefined(Component value);

	/**
	 * Returns the value of the '<em><b>Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Path</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Path</em>' attribute.
	 * @see #setPath(String)
	 * @see org.ietr.dftools.architecture.slam.SlamPackage#getDesign_Path()
	 * @model unique="false" dataType="org.ietr.dftools.architecture.slam.String"
	 * @generated
	 */
	String getPath();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.Design#getPath <em>Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Path</em>' attribute.
	 * @see #getPath()
	 * @generated
	 */
	void setPath(String value);

	/**
	 * Returns the value of the '<em><b>Component Holder</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Component Holder</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Component Holder</em>' reference.
	 * @see #setComponentHolder(ComponentHolder)
	 * @see org.ietr.dftools.architecture.slam.SlamPackage#getDesign_ComponentHolder()
	 * @model
	 * @generated
	 */
	ComponentHolder getComponentHolder();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.Design#getComponentHolder <em>Component Holder</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Component Holder</em>' reference.
	 * @see #getComponentHolder()
	 * @generated
	 */
	void setComponentHolder(ComponentHolder value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model unique="false" nameDataType="org.ietr.dftools.architecture.slam.String" nameUnique="false"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.dftools.architecture.slam.ComponentInstance%&gt;, &lt;%java.lang.Boolean%&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.dftools.architecture.slam.ComponentInstance%&gt;, &lt;%java.lang.Boolean%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.Boolean%&gt; apply(final &lt;%org.ietr.dftools.architecture.slam.ComponentInstance%&gt; it)\n\t{\n\t\t&lt;%java.lang.String%&gt; _instanceName = it.getInstanceName();\n\t\treturn &lt;%java.lang.Boolean%&gt;.valueOf(&lt;%com.google.common.base.Objects%&gt;.equal(_instanceName, name));\n\t}\n};\nreturn &lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.dftools.architecture.slam.ComponentInstance%&gt;&gt;exists(this.getComponentInstances(), _function);'"
	 * @generated
	 */
	boolean containsComponentInstance(String name);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model unique="false" nameUnique="false"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.dftools.architecture.slam.component.Component%&gt;, &lt;%java.lang.Boolean%&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.dftools.architecture.slam.component.Component%&gt;, &lt;%java.lang.Boolean%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.Boolean%&gt; apply(final &lt;%org.ietr.dftools.architecture.slam.component.Component%&gt; it)\n\t{\n\t\treturn &lt;%java.lang.Boolean%&gt;.valueOf(&lt;%org.ietr.dftools.architecture.utils.VLNVComparator%&gt;.areSame(it.getVlnv(), name));\n\t}\n};\nreturn &lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.dftools.architecture.slam.component.Component%&gt;&gt;exists(this.getComponentHolder().getComponents(), _function);'"
	 * @generated
	 */
	boolean containsComponent(VLNV name);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model unique="false" nameDataType="org.ietr.dftools.architecture.slam.String" nameUnique="false"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.dftools.architecture.slam.ComponentInstance%&gt;, &lt;%java.lang.Boolean%&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.dftools.architecture.slam.ComponentInstance%&gt;, &lt;%java.lang.Boolean%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.Boolean%&gt; apply(final &lt;%org.ietr.dftools.architecture.slam.ComponentInstance%&gt; it)\n\t{\n\t\t&lt;%java.lang.String%&gt; _instanceName = it.getInstanceName();\n\t\treturn &lt;%java.lang.Boolean%&gt;.valueOf(&lt;%com.google.common.base.Objects%&gt;.equal(_instanceName, name));\n\t}\n};\nreturn &lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.dftools.architecture.slam.ComponentInstance%&gt;&gt;findFirst(this.getComponentInstances(), _function);'"
	 * @generated
	 */
	ComponentInstance getComponentInstance(String name);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model unique="false" nameUnique="false"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.dftools.architecture.slam.component.Component%&gt;, &lt;%java.lang.Boolean%&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.dftools.architecture.slam.component.Component%&gt;, &lt;%java.lang.Boolean%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.Boolean%&gt; apply(final &lt;%org.ietr.dftools.architecture.slam.component.Component%&gt; it)\n\t{\n\t\treturn &lt;%java.lang.Boolean%&gt;.valueOf(&lt;%org.ietr.dftools.architecture.utils.VLNVComparator%&gt;.areSame(it.getVlnv(), name));\n\t}\n};\nreturn &lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.dftools.architecture.slam.component.Component%&gt;&gt;findFirst(this.getComponentHolder().getComponents(), _function);'"
	 * @generated
	 */
	Component getComponent(VLNV name);

} // Design
