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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import org.jgrapht.graph.DirectedPseudograph;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.preesm.algorithm.model.factories.IModelVertexFactory;
import org.preesm.algorithm.model.parameters.ArgumentFactory;
import org.preesm.algorithm.model.parameters.IExpressionSolver;
import org.preesm.algorithm.model.parameters.Parameter;
import org.preesm.algorithm.model.parameters.ParameterFactory;
import org.preesm.algorithm.model.parameters.ParameterSet;
import org.preesm.algorithm.model.parameters.Value;
import org.preesm.algorithm.model.parameters.Variable;
import org.preesm.algorithm.model.parameters.VariableSet;
import org.preesm.commons.CloneableProperty;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.exceptions.PreesmRuntimeException;
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
    implements PropertySource, IRefinement, IExpressionSolver, IModelObserver, CloneableProperty {

  private static final long   serialVersionUID               = 1381565218127310945L;
  private static final String NAME_PROPERTY_LITERAL          = "name";
  public static final String  PARAMETERS_PROPERTY_LITERAL    = "parameters";
  public static final String  VARIABLES_PROPERTY_LITERAL     = "variables";
  public static final String  KIND_PROPERTY_LITERAL          = "kind";
  public static final String  PATH_PROPERTY_LITERAL          = "path";
  private static final String PARENT_VERTEX_PROPERTY_LITERAL = "parent_vertex";

  private final PropertyBean                        properties;
  private final transient ArrayList<IModelObserver> observers;
  private boolean                                   hasChanged;

  /**
   * Creates a new Instance of Abstract graph with the given factory.
   *
   */
  protected AbstractGraph(final Supplier<E> edgeSupplier) {
    super(null, edgeSupplier, false);
    this.properties = new PropertyBean();
    this.observers = new ArrayList<>();
    this.hasChanged = false;
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
    this.setChanged();
    this.notifyObservers(edge);
    return edge;
  }

  @Override
  public E addEdge(final V source, final V target) {
    final E edge = super.addEdge(source, target);
    edge.setBase(this);
    this.setChanged();
    this.notifyObservers(edge);
    return edge;
  }

  /**
   * Add the given parameter to his graph parameter set.
   *
   * @param param
   *          The parameter to add
   */
  public void addParameter(final Parameter param) {
    if (this.properties.getValue(AbstractGraph.PARAMETERS_PROPERTY_LITERAL) == null) {
      setParameterSet(new ParameterSet());
    }
    this.properties.<ParameterSet>getValue(AbstractGraph.PARAMETERS_PROPERTY_LITERAL).addParameter(param);
  }

  /**
   * Add the given variable to his graph parameter set.
   *
   * @param variable
   *          The variable to add
   */
  public void addVariable(final Variable variable) {
    if (this.properties.getValue(AbstractGraph.VARIABLES_PROPERTY_LITERAL) == null) {
      setVariableSet(new VariableSet());
    }
    this.properties.<VariableSet>getValue(AbstractGraph.VARIABLES_PROPERTY_LITERAL).addVariable(variable);
    variable.setExpressionSolver(this);
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
    this.setChanged();
    this.notifyObservers(vertex);
    return result;
  }

  /**
   * Indicates that this object has no longer changed, or that it has already notified all of its observers of its most
   * recent change, so that the hasChanged method will now return false.
   */
  private void clearChanged() {
    this.hasChanged = true;
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

  public ParameterFactory getParameterFactory() {
    return new ParameterFactory();
  }

  /**
   */
  public ParameterSet getParameters() {
    if (this.properties.getValue(AbstractGraph.PARAMETERS_PROPERTY_LITERAL) != null) {
      return this.properties.getValue(AbstractGraph.PARAMETERS_PROPERTY_LITERAL);
    }
    return null;
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
   * Gives the variable of this graph with the given name.
   *
   * @param name
   *          The name of the variable to get
   * @return The variable with the given name
   */
  public Variable getVariable(final String name) {
    if (this.properties.getValue(AbstractGraph.VARIABLES_PROPERTY_LITERAL) != null) {
      return this.properties.<VariableSet>getValue(AbstractGraph.VARIABLES_PROPERTY_LITERAL).getVariable(name);
    }
    return null;
  }

  /**
   * Gives the variables set of this graph.
   *
   * @return The set of variables of this graph
   */
  public VariableSet getVariables() {
    if (this.properties.getValue(AbstractGraph.VARIABLES_PROPERTY_LITERAL) == null) {
      final VariableSet variables = new VariableSet();
      this.setVariableSet(variables);
    }
    return this.properties.getValue(AbstractGraph.VARIABLES_PROPERTY_LITERAL);
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

  /**
   * If this object has changed, as indicated by the hasChanged method, then notify all of its observers and then call
   * the clearChanged method to indicate that this object has no longer changed.
   *
   * @param arg
   *          Arguments to be passe to the update method
   */
  private void notifyObservers(final Object arg) {
    if (this.hasChanged) {
      for (final IModelObserver o : this.observers) {
        o.update(this, arg);
      }
      clearChanged();
    }
  }

  @Override
  public boolean removeEdge(final E edge) {
    final boolean res = super.removeEdge(edge);
    this.setChanged();
    this.notifyObservers(edge);
    return res;
  }

  @Override
  public boolean removeVertex(final V vertex) {
    final boolean result = super.removeVertex(vertex);
    this.setChanged();
    this.notifyObservers(vertex);
    return result;
  }

  /**
   * Marks this Observable object as having been changed the hasChanged method will now return true.
   */
  private void setChanged() {
    this.hasChanged = true;
  }

  public void setName(final String name) {
    this.properties.setValue(AbstractGraph.NAME_PROPERTY_LITERAL,
        this.properties.getValue(AbstractGraph.NAME_PROPERTY_LITERAL), name);
  }

  public void setParameterSet(final ParameterSet parameters) {
    this.properties.setValue(AbstractGraph.PARAMETERS_PROPERTY_LITERAL,
        this.properties.getValue(AbstractGraph.PARAMETERS_PROPERTY_LITERAL), parameters);
  }

  protected void setParentVertex(final V parentVertex) {
    this.properties.setValue(AbstractGraph.PARENT_VERTEX_PROPERTY_LITERAL, parentVertex);
  }

  @Override
  public void setPropertyValue(final String propertyName, final Object value) {
    this.getPropertyBean().setValue(propertyName, value);
  }

  public void setVariableSet(final VariableSet variables) {
    this.properties.setValue(AbstractGraph.VARIABLES_PROPERTY_LITERAL,
        this.properties.getValue(AbstractGraph.VARIABLES_PROPERTY_LITERAL), variables);
  }

  @Override
  public long solveExpression(final String expression, final Value caller) {
    long resultValue;
    try {
      final JEP jep = new JEP();
      if (this.getVariables() != null /*
                                       * && !(caller instanceof Argument)
                                       */) {
        for (final String variable : this.getVariables().keySet()) {
          if ((this.getVariable(variable) == caller) || this.getVariable(variable).getValue().equals(expression)) {
            break;
          }
          jep.addVariable(variable, this.getVariable(variable).longValue());
        }
      }
      if ((this.getParameters() != null) && (this.getParentVertex() != null)) {
        addParametersToScope(jep);
      }
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

  private void addParametersToScope(final JEP jep) {
    for (final String arg : this.getParameters().keySet()) {
      try {
        Long paramValue = this.getParameters().get(arg).getValue();
        if (paramValue == null) {
          paramValue = this.getParentVertex().getArgument(arg).longValue();
          this.getParameters().get(arg).setValue(paramValue);
        }
        jep.addVariable(arg, paramValue);
      } catch (final ExpressionEvaluationException e) {
        throw new PreesmRuntimeException("Could not evaluate value", e);
      }
    }
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
