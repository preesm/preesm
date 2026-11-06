/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.algorithm.model;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import org.jgrapht.graph.DirectedPseudograph;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.preesm.algorithm.model.factories.IModelVertexFactory;
import org.preesm.algorithm.model.parameters.ArgumentFactory;
import org.preesm.algorithm.model.parameters.IExpressionSolver;
import org.preesm.algorithm.model.parameters.Value;
import org.preesm.commons.CloneableProperty;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.math.ExpressionEvaluationException;

/**
 * Abstract class common to all graphs.
 *
 * @author jpiat
 * @author kdesnos
 * @param <V>
 *          the value type
 * @param <E>
 *          the element type
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbstractGraph<V extends AbstractVertex, E extends AbstractEdge> extends DirectedPseudograph<V, E>
    implements PropertySource, IRefinement, IExpressionSolver, CloneableProperty {

  private static final long   serialVersionUID               = 1381565218127310945L;
  private static final String NAME_PROPERTY_LITERAL          = "name";
  public static final String  PARAMETERS_PROPERTY_LITERAL    = "parameters";
  public static final String  VARIABLES_PROPERTY_LITERAL     = "variables";
  public static final String  KIND_PROPERTY_LITERAL          = "kind";
  public static final String  PATH_PROPERTY_LITERAL          = "path";
  private static final String PARENT_VERTEX_PROPERTY_LITERAL = "parent_vertex";

  private final PropertyBean properties;

  /**
   * Creates a new Instance of Abstract graph with the given factory.
   *
   */
  protected AbstractGraph(final Supplier<E> edgeSupplier) {
    super(null, edgeSupplier, false);
    this.properties = new PropertyBean();
  }

  /**
   * Accept.
   *
   * @param visitor
   *          The visitor to accept
   * @throws PreesmException
   *           the SDF 4 J exception
   */
  public void accept(final IGraphVisitor visitor) {
    visitor.visit(this);
  }

  /**
   * Edge adding method used by parser.
   *
   * @param source
   *          The source vertex of the edge
   * @param sourcePort
   *          The source port of the edge
   * @param target
   *          The target vertex of the edge
   * @param targetPort
   *          The target port of the edge
   * @return the e
   */
  public E addEdge(final V source, final IInterface sourcePort, final V target, final IInterface targetPort) {
    final E edge = super.addEdge(source, target);
    edge.setSourceLabel(sourcePort.getName());
    edge.setTargetLabel(targetPort.getName());
    edge.setBase(this);
    return edge;
  }

  @Override
  public E addEdge(final V source, final V target) {
    final E edge = super.addEdge(source, target);
    edge.setBase(this);
    return edge;
  }

  @Override
  public boolean addVertex(final V vertex) {
    int number = 0;
    String name = vertex.getName();
    while (this.getVertex(name) != null) {
      name = vertex.getName() + "_" + number;
      number++;
    }
    vertex.setName(name);
    final boolean result = super.addVertex(vertex);
    vertex.setBase(this);
    return result;
  }

  public ArgumentFactory getArgumentFactory() {
    return new ArgumentFactory();
  }

  /**
   * Gives the vertex set of current graph merged with the vertex set of all its subgraphs.
   *
   * @return The vertex with the given name, null, if the vertex does not exist
   */
  public Set<V> getHierarchicalVertexSet() {

    final Set<V> vset = new LinkedHashSet<>(vertexSet());

    for (final V vertex : vertexSet()) {
      if (vertex.getGraphDescription() != null) {
        vset.addAll(vertex.getGraphDescription().getHierarchicalVertexSet());
      }
    }
    return vset;
  }

  public String getName() {
    return this.properties.getValue(AbstractGraph.NAME_PROPERTY_LITERAL);
  }

  public V getParentVertex() {
    return this.properties.getValue(AbstractGraph.PARENT_VERTEX_PROPERTY_LITERAL);
  }

  @Override
  public PropertyBean getPropertyBean() {
    return this.properties;
  }

  @Override
  public String getPropertyStringValue(final String propertyName) {
    if (this.getPropertyBean().getValue(propertyName) != null) {
      return this.getPropertyBean().getValue(propertyName).toString();
    }
    return null;
  }

  /**
   * Gives the vertex with the given name.
   *
   * @param name
   *          The vertex name
   * @return The vertex with the given name, null, if the vertex does not exist
   */
  public V getVertex(final String name) {
    for (final V vertex : vertexSet()) {
      if (vertex.getName().equals(name)) {
        return vertex;
      }
    }
    return null;
  }

  public abstract IModelVertexFactory<V> getVertexFactory();

  @Override
  public boolean removeEdge(final E edge) {
    final boolean res = super.removeEdge(edge);
    return res;
  }

  @Override
  public boolean removeVertex(final V vertex) {
    final boolean result = super.removeVertex(vertex);
    return result;
  }

  public void setName(final String name) {
    this.properties.setValue(AbstractGraph.NAME_PROPERTY_LITERAL,
        this.properties.getValue(AbstractGraph.NAME_PROPERTY_LITERAL), name);
  }

  protected void setParentVertex(final V parentVertex) {
    this.properties.setValue(AbstractGraph.PARENT_VERTEX_PROPERTY_LITERAL, parentVertex);
  }

  @Override
  public void setPropertyValue(final String propertyName, final Object value) {
    this.getPropertyBean().setValue(propertyName, value);
  }

  @Override
  public long solveExpression(final String expression, final Value caller) {
    long resultValue;
    try {
      final JEP jep = new JEP();

      final Node expressionMainNode = jep.parse(expression);
      final Object result = jep.evaluate(expressionMainNode);
      if (!(result instanceof Number)) {
        throw (new ExpressionEvaluationException("Not a numerical expression"));
      }
      resultValue = ((Number) result).longValue();
    } catch (final Exception e) {
      throw (new ExpressionEvaluationException("Could not parse expresion:" + expression));
    }
    return resultValue;
  }

  /**
   * Validate model.
   *
   * @return true, if successful
   * @throws PreesmException
   *           the SDF 4 J exception
   */
  public abstract boolean validateModel();

}
