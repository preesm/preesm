/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Antoine Morvan <antoine.morvan.pro@gmail.com> (2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Hascoet <jhascoet@kalray.eu> (2017)
 * Jonathan Piat <jpiat@laas.fr> (2011 - 2012)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
package org.ietr.dftools.algorithm.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.ietr.dftools.algorithm.DFToolsAlgoException;
import org.ietr.dftools.algorithm.factories.IModelVertexFactory;
import org.ietr.dftools.algorithm.model.parameters.IExpressionSolver;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.parameters.NoIntegerValueException;
import org.ietr.dftools.algorithm.model.parameters.Parameter;
import org.ietr.dftools.algorithm.model.parameters.ParameterSet;
import org.ietr.dftools.algorithm.model.parameters.Value;
import org.ietr.dftools.algorithm.model.parameters.Variable;
import org.ietr.dftools.algorithm.model.parameters.VariableSet;
import org.ietr.dftools.algorithm.model.parameters.factories.ArgumentFactory;
import org.ietr.dftools.algorithm.model.parameters.factories.ParameterFactory;
import org.ietr.dftools.algorithm.model.visitors.IGraphVisitor;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedPseudograph;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;

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

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1381565218127310945L;

  /** Property name for property name. */
  public static final String NAME = "name";

  /** Property name for property parameters. */
  public static final String PARAMETERS = "parameters";

  /** Property name for property variables. */
  public static final String VARIABLES = "variables";

  /** Property name for property variables. */
  public static final String MODEL = "kind";

  /**
   * Property name to store the path of the file of the graph.
   */
  public static final String PATH = "path";

  /** This graph parent vertex if it exist. */
  public static final String PARENT_VERTEX = "parent_vertex";

  /** The public properties. */
  protected static final List<String> PUBLIC_PROPERTIES = new ArrayList<>();

  static {
    PUBLIC_PROPERTIES.add(AbstractGraph.NAME);
    PUBLIC_PROPERTIES.add(AbstractGraph.PARAMETERS);
    PUBLIC_PROPERTIES.add(AbstractGraph.VARIABLES);
    PUBLIC_PROPERTIES.add(AbstractGraph.MODEL);
  }

  /** The properties. */
  protected PropertyBean properties;

  /** The observers. */
  protected ArrayList<IModelObserver> observers;

  /** The has changed. */
  protected boolean hasChanged;

  /**
   * Creates a new Instance of Abstract graph with the given factory.
   *
   * @param factory
   *          the factory
   */
  public AbstractGraph(final EdgeFactory<V, E> factory) {
    super(factory);
    this.properties = new PropertyBean();
    this.observers = new ArrayList<>();
    this.hasChanged = false;
  }

  /**
   * Accept.
   *
   * @param visitor
   *          The visitor to accept
   * @throws SDF4JException
   *           the SDF 4 J exception
   */
  public void accept(final IGraphVisitor visitor) throws SDF4JException {
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

  /*
   * (non-Javadoc)
   *
   * @see org.jgrapht.graph.AbstractBaseGraph#addEdge(java.lang.Object, java.lang.Object)
   */
  @Override
  public E addEdge(final V source, final V target) {
    final E edge = super.addEdge(source, target);
    edge.setBase(this);
    this.setChanged();
    this.notifyObservers(edge);
    return edge;
  }

  /**
   * Add an observer to this graph model.
   *
   * @param o
   *          Te observer to be added
   */
  public void addObserver(final IModelObserver o) {
    this.observers.add(o);
  }

  /**
   * Add the given parameter to his graph parameter set.
   *
   * @param param
   *          The parameter to add
   */
  public void addParameter(final Parameter param) {
    if (this.properties.getValue(AbstractGraph.PARAMETERS) == null) {
      setParameterSet(new ParameterSet());
    }
    this.properties.<ParameterSet>getValue(AbstractGraph.PARAMETERS).addParameter(param);
  }

  /**
   * Add the given variable to his graph parameter set.
   *
   * @param var
   *          The variable to add
   */
  public void addVariable(final Variable var) {
    if (this.properties.getValue(AbstractGraph.VARIABLES) == null) {
      setVariableSet(new VariableSet());
    }
    this.properties.<VariableSet>getValue(AbstractGraph.VARIABLES).addVariable(var);
    var.setExpressionSolver(this);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jgrapht.graph.AbstractBaseGraph#addVertex(java.lang.Object)
   */
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
   * This method check whether the source and target {@link AbstractVertex vertices} passed as parameter are linked by a
   * unique edge. If several edges link the two vertices, a {@link RuntimeException} is thrown.
   *
   * @param source
   *          the source {@link AbstractVertex} of the {@link AbstractEdge}
   * @param target
   *          the target {@link AbstractVertex} of the {@link AbstractEdge}
   * @throws RuntimeException
   *           if there are several {@link AbstractEdge} between the source and the target
   */
  protected void checkMultipleEdges(final V source, final V target) {
    if (source != null && target != null && getAllEdges(source, target).size() > 1) {
      throw new DFToolsAlgoException("removeEdge(source,target) cannot be used.\n" + "Reason: there are "
          + getAllEdges(source, target).size() + " edges between actors " + source + " and " + target);
    }
  }

  /**
   * Indicates that this object has no longer changed, or that it has already notified all of its observers of its most
   * recent change, so that the hasChanged method will now return false.
   */
  public void clearChanged() {
    this.hasChanged = true;
  }

  /**
   * Clear the list of observers.
   */
  public void clearObservers() {
    this.observers.clear();
  }

  /**
   * Delete the given observer from the observers list.
   *
   * @param o
   *          the o
   */
  public void deleteObserver(final IModelObserver o) {
    this.observers.remove(o);
  }

  /**
   * Gets the argument factory.
   *
   * @param v
   *          the v
   * @return the argument factory
   */
  public ArgumentFactory getArgumentFactory(final V v) {
    return new ArgumentFactory(v);
  }

  /**
   * Gives the path of a given vertex.
   *
   * @param vertex
   *          The vertex
   * @return The vertex path in the graph hierarchy
   */
  public String getHierarchicalPath(final V vertex) {
    return getHierarchicalPath(vertex, "");
  }

  /**
   * Gives the path of a given vertex.
   *
   * @param vertex
   *          The vertex
   * @param currentPath
   *          the current path
   * @return The vertex path in the graph hierarchy
   */
  private String getHierarchicalPath(final V vertex, final String currentPath) {

    for (final Object v : vertexSet()) {
      final V castV = (V) v;
      String newPath = currentPath + castV.getName();

      if (castV == vertex) {
        return newPath;
      }
      newPath += "/";
      if (vertex.getGraphDescription() != null) {
        newPath = vertex.getGraphDescription().getHierarchicalPath(vertex, newPath);
        if (newPath != null) {
          return newPath;
        }
      }
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
  public V getHierarchicalVertex(final String name) {
    for (final V vertex : vertexSet()) {
      if (vertex.getName().equals(name)) {
        return vertex;
      } else if (vertex.getGraphDescription() != null) {
        final AbstractVertex result = vertex.getGraphDescription().getHierarchicalVertex(name);
        if (result != null) {
          return (V) result;
        }
      }
    }
    return null;
  }

  /**
   * Gives the vertex with the given path.
   *
   * @param path
   *          The vertex path in format MyGrandParentVertex/MyParentVertex/Myself
   * @return The vertex with the given name, null, if the vertex does not exist
   */
  public V getHierarchicalVertexFromPath(final String path) {

    final String[] splitPath = path.split("/");
    int index = 0;
    // Get the first segment of the path, this is the name of the first
    // actor we will look for
    String currentName = splitPath[index];
    index++;
    final StringBuilder currentPath = new StringBuilder();
    // Handle the case where the first segment of path == name
    if (this.getName().equals(currentName)) {
      currentName = splitPath[index];
      index++;
    }
    // Compute the path for the next search (path minus currentName)
    for (int i = index; i < splitPath.length; i++) {
      if (i > index) {
        currentPath.append("/");
      }
      currentPath.append(splitPath[i]);
    }
    // Look for an actor named currentName
    for (final V a : vertexSet()) {
      if (a.getName().equals(currentName)) {
        // If currentPath is empty, then we are at the last hierarchy
        // level
        if ("".equals(currentPath.toString())) {
          // We found the actor
          return a;
          // Otherwise, we need to go deeper in the hierarchy
        } else {
          final IRefinement refinement = a.getRefinement();
          if (refinement instanceof AbstractGraph) {
            final AbstractGraph subgraph = (AbstractGraph) refinement;
            return (V) subgraph.getHierarchicalVertexFromPath(currentPath.toString());
          }
        }
      }
    }
    // If we reach this point, no actor was found, return null
    return null;
  }

  /**
   * Gives the vertex with the given path.
   *
   * @param path
   *          The vertex path in format MyGrandParentVertex/MyParentVertex/Myself
   * @param pathAlreadyRead
   *          The path that we are already in. For example, if we are in the MyParentVertex refinement,
   *          pathAlreadyRead=MyGrandParentVertex/MyParentVertex.
   * @return The vertex with the given name, null, if the vertex does not exist
   */
  public V getHierarchicalVertexFromPath(final String path, String pathAlreadyRead) {

    String vertexToFind = path; // The name of the vertex to find in the
    // current level of hierarchy
    boolean isPathRead = true; // true if we have already read the hierarchy
    // and we are looking for a vertex here

    // Removing the path already read from the path
    if (!pathAlreadyRead.isEmpty()) {
      vertexToFind = vertexToFind.replaceFirst(pathAlreadyRead + "/", "");
      pathAlreadyRead += "/";
    }

    // Selecting the first vertex name to find
    if (vertexToFind.indexOf('/') != -1) {
      vertexToFind = vertexToFind.substring(0, vertexToFind.indexOf('/'));
      isPathRead = false;
    }

    if (vertexToFind.equals(this.getName())) {
      this.getHierarchicalVertexFromPath(path, pathAlreadyRead + vertexToFind);
    }

    final V vertex = getVertex(vertexToFind);
    if (vertex != null) {
      if (isPathRead) {
        return vertex;
      } else if (vertex.getGraphDescription() != null) {
        return (V) vertex.getGraphDescription().getHierarchicalVertexFromPath(path, pathAlreadyRead + vertexToFind);
      }
    }

    return null;
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

  /**
   * Gives this graph name.
   *
   * @return The name of this graph
   */
  public String getName() {
    return this.properties.getValue(AbstractGraph.NAME);
  }

  /**
   * Gives the parameter of this graph with the given name.
   *
   * @param name
   *          The name of the parameter to get
   * @return The parameter with the given name
   */
  public Parameter getParameter(final String name) {
    if (this.properties.getValue(AbstractGraph.PARAMETERS) != null) {
      return this.properties.<ParameterSet>getValue(AbstractGraph.PARAMETERS).getParameter(name);
    }
    return null;
  }

  /**
   * Gets the parameter factory.
   *
   * @return the parameter factory
   */
  public ParameterFactory getParameterFactory() {
    return new ParameterFactory(this);
  }

  /**
   * Gives the parameter set of this graph.
   *
   * @return The set of parameter of this graph
   */
  public ParameterSet getParameters() {
    if (this.properties.getValue(AbstractGraph.PARAMETERS) != null) {
      return this.properties.getValue(AbstractGraph.PARAMETERS);
    }
    return null;
  }

  /**
   * Gets the parent vertex.
   *
   * @return the parent vertex
   */
  public V getParentVertex() {
    return this.properties.getValue(AbstractGraph.PARENT_VERTEX);
  }

  /**
   * Gives this graph PropertyBean.
   *
   * @return This Graph PropertyBean
   */
  @Override
  public PropertyBean getPropertyBean() {
    return this.properties;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.PropertySource#getPropertyStringValue(java.lang.String)
   */
  @Override
  public String getPropertyStringValue(final String propertyName) {
    if (this.getPropertyBean().getValue(propertyName) != null) {
      return this.getPropertyBean().getValue(propertyName).toString();
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.PropertySource#getPublicProperties()
   */
  @Override
  public List<String> getPublicProperties() {
    return AbstractGraph.PUBLIC_PROPERTIES;
  }

  /**
   * Gives the variable of this graph with the given name.
   *
   * @param name
   *          The name of the variable to get
   * @return The variable with the given name
   */
  public Variable getVariable(final String name) {
    if (this.properties.getValue(AbstractGraph.VARIABLES) != null) {
      return this.properties.<VariableSet>getValue(AbstractGraph.VARIABLES).getVariable(name);
    }
    return null;
  }

  /**
   * Gives the variables set of this graph.
   *
   * @return The set of variables of this graph
   */
  public VariableSet getVariables() {
    if (this.properties.getValue(AbstractGraph.VARIABLES) == null) {
      final VariableSet variables = new VariableSet();
      this.setVariableSet(variables);
    }
    return this.properties.getValue(AbstractGraph.VARIABLES);
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

  /**
   * Gets the vertex factory.
   *
   * @return the vertex factory
   */
  public abstract IModelVertexFactory<V> getVertexFactory();

  /**
   * Tests if this object has changed.
   *
   * @return True if the object has changed, false otherwise
   */
  public boolean hasChanged() {
    return this.hasChanged;
  }

  /**
   * If this object has changed, as indicated by the hasChanged method, then notify all of its observers and then call
   * the clearChanged method to indicate that this object has no longer changed.
   */
  public void notifyObservers() {
    if (this.hasChanged) {
      for (final IModelObserver o : this.observers) {
        o.update(this, null);
      }
      clearChanged();
    }
  }

  /**
   * If this object has changed, as indicated by the hasChanged method, then notify all of its observers and then call
   * the clearChanged method to indicate that this object has no longer changed.
   *
   * @param arg
   *          Arguments to be passe to the update method
   */
  public void notifyObservers(final Object arg) {
    if (this.hasChanged) {
      for (final IModelObserver o : this.observers) {
        o.update(this, arg);
      }
      clearChanged();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jgrapht.graph.AbstractBaseGraph#removeEdge(java.lang.Object)
   */
  @Override
  public boolean removeEdge(final E edge) {
    final boolean res = super.removeEdge(edge);
    this.setChanged();
    this.notifyObservers(edge);
    return res;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jgrapht.graph.AbstractBaseGraph#removeVertex(java.lang.Object)
   */
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
  public void setChanged() {
    this.hasChanged = true;
  }

  /**
   * Set this graph name.
   *
   * @param name
   *          The name to set for this graph
   */
  public void setName(final String name) {
    this.properties.setValue(AbstractGraph.NAME, this.properties.getValue(AbstractGraph.NAME), name);
  }

  /**
   * Set the parameter set for this graph.
   *
   * @param parameters
   *          The set of parameters for this graph
   */
  public void setParameterSet(final ParameterSet parameters) {
    this.properties.setValue(AbstractGraph.PARAMETERS, this.properties.getValue(AbstractGraph.PARAMETERS), parameters);
  }

  /**
   * Sets the parent vertex.
   *
   * @param parentVertex
   *          the new parent vertex
   */
  protected void setParentVertex(final V parentVertex) {
    this.properties.setValue(AbstractGraph.PARENT_VERTEX, parentVertex);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.PropertySource#setPropertyValue(java.lang.String, java.lang.Object)
   */
  @Override
  public void setPropertyValue(final String propertyName, final Object value) {
    this.getPropertyBean().setValue(propertyName, value);
  }

  /**
   * Set the variables set for this graph.
   *
   * @param variables
   *          The set of variables for this graph
   */
  public void setVariableSet(final VariableSet variables) {
    this.properties.setValue(AbstractGraph.VARIABLES, this.properties.getValue(AbstractGraph.VARIABLES), variables);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.parameters.IExpressionSolver#solveExpression(java.lang.String,
   * org.ietr.dftools.algorithm.model.parameters.Value)
   */
  @Override
  public long solveExpression(final String expression, final Value caller) {
    long resultValue;
    try {
      final JEP jep = new JEP();
      if (this.getVariables() != null /*
                                       * && !(caller instanceof Argument)
                                       */) {
        for (final String var : this.getVariables().keySet()) {
          if ((this.getVariable(var) == caller) || this.getVariable(var).getValue().equals(expression)) {
            break;
          } else {
            jep.addVariable(var, this.getVariable(var).longValue());
          }
        }
      }
      if ((this.getParameters() != null) && (this.getParentVertex() != null)) {
        addParametersToScope(jep);
      }
      final Node expressionMainNode = jep.parse(expression);
      final Object result = jep.evaluate(expressionMainNode);
      if (result instanceof Number) {
        resultValue = ((Number) result).longValue();
      } else {
        throw (new InvalidExpressionException("Not a numerical expression"));
      }
    } catch (final Exception e) {
      throw (new InvalidExpressionException("Could not parse expresion:" + expression));
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
      } catch (final NoIntegerValueException e) {
        throw new DFToolsAlgoException("Could not evaluate value", e);
      }
    }
  }

  /**
   * Validate model.
   *
   * @return true, if successful
   * @throws SDF4JException
   *           the SDF 4 J exception
   */
  public abstract boolean validateModel();

}
