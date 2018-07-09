/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Pi Graph</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getPiGraph()
 * @model
 * @generated
 */
public interface PiGraph extends AbstractActor, Graph {
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model actorUnique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return this.getVertices().add(actor);'"
   * @generated
   */
  boolean addActor(AbstractActor actor);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model parameterUnique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return this.getVertices().add(parameter);'"
   * @generated
   */
  boolean addParameter(Parameter parameter);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model fifoUnique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return this.getEdges().add(fifo);'"
   * @generated
   */
  boolean addFifo(Fifo fifo);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model dependencyUnique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return this.getEdges().add(dependency);'"
   * @generated
   */
  boolean addDependency(Dependency dependency);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return &lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;unmodifiableEList(&lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;toEList(&lt;%com.google.common.collect.Iterables%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;filter(this.getVertices(), &lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;.class)));'"
   * @generated
   */
  EList<Parameter> getParameters();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return &lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;&gt;unmodifiableEList(&lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;&gt;toEList(&lt;%com.google.common.collect.Iterables%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;&gt;filter(this.getVertices(), &lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;.class)));'"
   * @generated
   */
  EList<AbstractActor> getActors();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return &lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Fifo%&gt;&gt;unmodifiableEList(&lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Fifo%&gt;&gt;toEList(&lt;%com.google.common.collect.Iterables%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Fifo%&gt;&gt;filter(this.getEdges(), &lt;%org.ietr.preesm.experiment.model.pimm.Fifo%&gt;.class)));'"
   * @generated
   */
  EList<Fifo> getFifos();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return &lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;unmodifiableEList(&lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;toEList(&lt;%com.google.common.collect.Iterables%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;filter(this.getEdges(), &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;.class)));'"
   * @generated
   */
  EList<Dependency> getDependencies();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model actorUnique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return this.getVertices().remove(actor);'"
   * @generated
   */
  boolean removeActor(AbstractActor actor);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;, &lt;%java.lang.Boolean%&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;, &lt;%java.lang.Boolean%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.Boolean%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt; it)\n\t{\n\t\treturn &lt;%java.lang.Boolean%&gt;.valueOf((!((it instanceof &lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputInterface%&gt;) || (it instanceof &lt;%org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface%&gt;))));\n\t}\n};\nreturn &lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;unmodifiableEList(&lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;toEList(&lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;filter(this.getParameters(), _function)));'"
   * @generated
   */
  EList<Parameter> getOnlyParameters();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" dataType="org.ietr.preesm.experiment.model.pimm.String"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;, &lt;%java.lang.String%&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;, &lt;%java.lang.String%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.String%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt; it)\n\t{\n\t\treturn it.getName();\n\t}\n};\nreturn &lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%java.lang.String%&gt;&gt;unmodifiableEList(&lt;%org.eclipse.emf.ecore.xcore.lib.XcoreEListExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;, &lt;%java.lang.String%&gt;&gt;map(this.getActors(), _function));'"
   * @generated
   */
  EList<String> getActorsNames();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" dataType="org.ietr.preesm.experiment.model.pimm.String"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;, &lt;%java.lang.String%&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;, &lt;%java.lang.String%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.String%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt; it)\n\t{\n\t\treturn it.getName();\n\t}\n};\nreturn &lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%java.lang.String%&gt;&gt;unmodifiableEList(&lt;%org.eclipse.emf.ecore.xcore.lib.XcoreEListExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;, &lt;%java.lang.String%&gt;&gt;map(this.getParameters(), _function));'"
   * @generated
   */
  EList<String> getParametersNames();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return &lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Actor%&gt;&gt;unmodifiableEList(&lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Actor%&gt;&gt;toEList(&lt;%com.google.common.collect.Iterables%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Actor%&gt;&gt;filter(this.getActors(), &lt;%org.ietr.preesm.experiment.model.pimm.Actor%&gt;.class)));'"
   * @generated
   */
  EList<Actor> getActorsWithRefinement();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='&lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt; _parameters = this.getParameters();\nfinal &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;, &lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;, &lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;&gt;()\n{\n\tpublic &lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt; it)\n\t{\n\t\treturn it.getAllParameters();\n\t}\n};\n&lt;%java.lang.Iterable%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt; _flatten = &lt;%com.google.common.collect.Iterables%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;concat(&lt;%org.eclipse.emf.ecore.xcore.lib.XcoreEListExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;, &lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;&gt;map(this.getChildrenGraphs(), _function));\nreturn &lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;unmodifiableEList(&lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;toEList(&lt;%com.google.common.collect.Iterables%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;concat(_parameters, _flatten)));'"
   * @generated
   */
  EList<Parameter> getAllParameters();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;, &lt;%java.lang.Boolean%&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;, &lt;%java.lang.Boolean%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.Boolean%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt; it)\n\t{\n\t\treturn &lt;%java.lang.Boolean%&gt;.valueOf((!(it instanceof &lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;)));\n\t}\n};\nreturn &lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;&gt;unmodifiableEList(&lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;&gt;toEList(&lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;&gt;filter(this.getActors(), _function)));'"
   * @generated
   */
  EList<AbstractActor> getOnlyActors();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='&lt;%java.lang.Iterable%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;&gt; _filter = &lt;%com.google.common.collect.Iterables%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;&gt;filter(this.getActors(), &lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;.class);\nfinal &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Actor%&gt;, &lt;%java.lang.Boolean%&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Actor%&gt;, &lt;%java.lang.Boolean%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.Boolean%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.Actor%&gt; it)\n\t{\n\t\treturn &lt;%java.lang.Boolean%&gt;.valueOf(it.isHierarchical());\n\t}\n};\nfinal &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Actor%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;&gt; _function_1 = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Actor%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;&gt;()\n{\n\tpublic &lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.Actor%&gt; it)\n\t{\n\t\treturn it.getSubGraph();\n\t}\n};\n&lt;%java.lang.Iterable%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;&gt; _map = &lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Actor%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;&gt;map(&lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Actor%&gt;&gt;filter(this.getActorsWithRefinement(), _function), _function_1);\nreturn &lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;&gt;unmodifiableEList(&lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;&gt;toEList(&lt;%com.google.common.collect.Iterables%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;&gt;concat(_filter, _map)));'"
   * @generated
   */
  EList<PiGraph> getChildrenGraphs();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='&lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;&gt; _actors = this.getActors();\nfinal &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;, &lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;, &lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;&gt;&gt;()\n{\n\tpublic &lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt; it)\n\t{\n\t\treturn it.getAllActors();\n\t}\n};\n&lt;%java.lang.Iterable%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;&gt; _flatten = &lt;%com.google.common.collect.Iterables%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;&gt;concat(&lt;%org.eclipse.emf.ecore.xcore.lib.XcoreEListExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;, &lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;&gt;&gt;map(this.getChildrenGraphs(), _function));\nreturn &lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;&gt;unmodifiableEList(&lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;&gt;toEList(&lt;%com.google.common.collect.Iterables%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;&gt;concat(_actors, _flatten)));'"
   * @generated
   */
  EList<AbstractActor> getAllActors();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model unique="false" parameterNameDataType="org.ietr.preesm.experiment.model.pimm.String" parameterNameUnique="false" graphNameDataType="org.ietr.preesm.experiment.model.pimm.String" graphNameUnique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;, &lt;%java.lang.Boolean%&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;, &lt;%java.lang.Boolean%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.Boolean%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt; it)\n\t{\n\t\treturn &lt;%java.lang.Boolean%&gt;.valueOf((&lt;%com.google.common.base.Objects%&gt;.equal(it.getName(), parameterName) &amp;&amp; &lt;%com.google.common.base.Objects%&gt;.equal(((&lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;) it.getContainingGraph()).getName(), graphName)));\n\t}\n};\nreturn &lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;findFirst(this.getAllParameters(), _function);'"
   * @generated
   */
  Parameter lookupParameterGivenGraph(String parameterName, String graphName);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Find the first {@link DataPort} of this {@link PiGraph} whose name matches the name of the {@link InterfaceActor} given as argument.
   * Return null if none matches.
   * <!-- end-model-doc -->
   * @model unique="false" interfaceActorUnique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.DataPort%&gt;, &lt;%java.lang.Boolean%&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.DataPort%&gt;, &lt;%java.lang.Boolean%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.Boolean%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.DataPort%&gt; it)\n\t{\n\t\t&lt;%java.lang.String%&gt; _name = it.getName();\n\t\t&lt;%java.lang.String%&gt; _name_1 = interfaceActor.getName();\n\t\treturn &lt;%java.lang.Boolean%&gt;.valueOf(&lt;%com.google.common.base.Objects%&gt;.equal(_name, _name_1));\n\t}\n};\nreturn &lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.DataPort%&gt;&gt;findFirst(this.getAllDataPorts(), _function);'"
   * @generated
   */
  DataPort lookupGraphDataPortForInterfaceActor(InterfaceActor interfaceActor);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model unique="false" vertexNameDataType="org.ietr.preesm.experiment.model.pimm.String" vertexNameUnique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='&lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;&gt; _actors = this.getActors();\n&lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt; _parameters = this.getParameters();\nfinal &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Configurable%&gt;, &lt;%java.lang.Boolean%&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Configurable%&gt;, &lt;%java.lang.Boolean%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.Boolean%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.Configurable%&gt; it)\n\t{\n\t\t&lt;%java.lang.String%&gt; _name = it.getName();\n\t\treturn &lt;%java.lang.Boolean%&gt;.valueOf(&lt;%com.google.common.base.Objects%&gt;.equal(_name, vertexName));\n\t}\n};\nreturn &lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Configurable%&gt;&gt;findFirst(&lt;%com.google.common.collect.Iterables%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Configurable%&gt;&gt;concat(_actors, _parameters), _function);'"
   * @generated
   */
  AbstractVertex lookupVertex(String vertexName);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model unique="false" fifoIdDataType="org.ietr.preesm.experiment.model.pimm.String" fifoIdUnique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Fifo%&gt;, &lt;%java.lang.Boolean%&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Fifo%&gt;, &lt;%java.lang.Boolean%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.Boolean%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.Fifo%&gt; it)\n\t{\n\t\t&lt;%java.lang.String%&gt; _id = it.getId();\n\t\treturn &lt;%java.lang.Boolean%&gt;.valueOf(&lt;%com.google.common.base.Objects%&gt;.equal(_id, fifoId));\n\t}\n};\nreturn &lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Fifo%&gt;&gt;findFirst(this.getFifos(), _function);'"
   * @generated
   */
  Fifo lookupFifo(String fifoId);

} // PiGraph
