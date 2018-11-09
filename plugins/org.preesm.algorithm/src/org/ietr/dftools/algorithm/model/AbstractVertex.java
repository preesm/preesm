/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Antoine Morvan <antoine.morvan.pro@gmail.com> (2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Hervé Yviquel <hyviquel@gmail.com> (2012)
 * Jonathan Piat <jpiat@laas.fr> (2011 - 2012)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2014)
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import org.ietr.dftools.algorithm.model.parameters.Argument;
import org.ietr.dftools.algorithm.model.parameters.ArgumentSet;
import org.ietr.dftools.algorithm.model.visitors.IGraphVisitor;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;

/**
 * Abstract class for all vertex types.
 *
 * @author jpiat
 * @param <G>
 *          the generic type
 */
@SuppressWarnings("unchecked")
public abstract class AbstractVertex<G> extends Observable
    implements PropertySource, Observer, CloneableProperty<AbstractVertex<G>> {

  /** The properties. */
  protected PropertyBean properties;

  /** Property name for property base. */
  public static final String BASE_LITERAL = "base";
  @Deprecated
  public static final String BASE         = BASE_LITERAL;

  /** Property name for property graph_desc. */
  public static final String REFINEMENT_LITERAL = "graph_desc";
  @Deprecated
  public static final String REFINEMENT         = REFINEMENT_LITERAL;

  /** Property name for property arguments. */
  public static final String ARGUMENTS_LITERAL = "arguments";

  /** Property name for property id. */
  public static final String ID_LITERAL = "id";

  /** Property name for property name. */
  public static final String NAME_LITERAL = "name";

  /** Property name for property name. */
  public static final String INFO_LITERAL = "info";

  /** Property kind for property name. */
  public static final String KIND_LITERAL = "kind";
  @Deprecated
  public static final String KIND         = KIND_LITERAL;

  /** The public properties. */
  protected static final List<
      String> public_properties = new ArrayList<>(Arrays.asList(AbstractVertex.ARGUMENTS_LITERAL,
          AbstractVertex.REFINEMENT_LITERAL, AbstractVertex.NAME_LITERAL, AbstractVertex.KIND_LITERAL));

  /** The interfaces. */
  protected List<IInterface> interfaces;

  /**
   * Creates a new Instance of Abstract vertex.
   */
  public AbstractVertex() {
    this.properties = new PropertyBean();
    this.interfaces = new ArrayList<>();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.PropertySource#getPublicProperties()
   */
  @Override
  public List<String> getPublicProperties() {
    return AbstractVertex.public_properties;
  }

  /**
   * Accept.
   *
   * @param visitor
   *          The visitor to accept
   * @throws SDF4JException
   *           the SDF 4 J exception
   */
  @SuppressWarnings("rawtypes")
  public void accept(final IGraphVisitor visitor) throws SDF4JException {
    visitor.visit(this);
  }

  /**
   * Add a list of interface to this vertex.
   *
   * @param port
   *          the port
   * @return true, if successful
   */
  public boolean addInterface(final IInterface port) {
    this.interfaces.add(port);
    return true;
  }

  @Deprecated
  public void addInterfaces(List<IInterface> list) {
    throw new UnsupportedOperationException("This method is deprecated and subject to removal soon.");
  }

  /**
   * Gets the interfaces.
   *
   * @return the interfaces
   */
  public List<IInterface> getInterfaces() {
    return this.interfaces;
  }

  /**
   * Give this vertex parent graph.
   *
   * @return The parent graph of this vertex
   */
  @SuppressWarnings("rawtypes")
  public AbstractGraph getBase() {
    if (this.properties.getValue(AbstractVertex.BASE_LITERAL) != null) {
      return this.properties.getValue(AbstractVertex.BASE_LITERAL);
    }
    return null;
  }

  /**
   * Gives the vertex id.
   *
   * @return The id of the vertex
   */
  public String getId() {
    return this.properties.getValue(AbstractVertex.ID_LITERAL);
  }

  /**
   * Gives this graph name.
   *
   * @return The name of this graph
   */
  public String getName() {
    return this.properties.getValue(AbstractVertex.NAME_LITERAL);
  }

  /**
   * Gives this graph info property.
   *
   * @return The info property of this graph
   */
  public String getInfo() {
    return this.properties.getValue(AbstractVertex.INFO_LITERAL);
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

  // Refinement of the vertex (can be an AbstractGraph describing the behavior
  // of the vertex, or a header file giving signatures of functions to call
  /** The refinement. */
  // when executing the vertex).
  private IRefinement refinement;

  /**
   * Gets the refinement.
   *
   * @return the refinement
   */
  public IRefinement getRefinement() {
    return this.refinement;
  }

  /**
   * Sets the refinement.
   *
   * @param desc
   *          the new refinement
   */
  public void setRefinement(final IRefinement desc) {
    this.properties.setValue(AbstractVertex.REFINEMENT_LITERAL,
        this.properties.getValue(AbstractVertex.REFINEMENT_LITERAL), desc);
    this.refinement = desc;
    setChanged();
    this.notifyObservers();
  }

  /**
   * Sets the graph description.
   *
   * @param desc
   *          the new graph description
   */
  @SuppressWarnings("rawtypes")
  public void setGraphDescription(final AbstractGraph desc) {
    this.properties.setValue(AbstractVertex.REFINEMENT_LITERAL,
        this.properties.getValue(AbstractVertex.REFINEMENT_LITERAL), desc);
    this.refinement = desc;
    desc.setParentVertex(this);
  }

  /**
   * Gets the graph description. Returns null if the refinement is concrete actor definition (C code).
   *
   * @return the graph description if the refinement is a Graph, null otherwise
   */
  @SuppressWarnings("rawtypes")
  public AbstractGraph getGraphDescription() {
    if (this.refinement instanceof AbstractGraph) {
      return (AbstractGraph) this.refinement;
    } else {
      return null;
    }
  }

  /**
   * Set this graph's base (parent) graph.
   *
   * @param base
   *          the new base
   */
  protected void setBase(final G base) {
    this.properties.setValue(AbstractVertex.BASE_LITERAL, base);
  }

  /**
   * Sets the id of the vertex.
   *
   * @param id
   *          The id to set for this vertex
   */
  public void setId(final String id) {
    this.properties.setValue(AbstractVertex.ID_LITERAL, this.properties.getValue(AbstractVertex.ID_LITERAL), id);
  }

  /**
   * Set this graph name.
   *
   * @param name
   *          The name to set for this graph
   */
  public void setName(final String name) {
    this.properties.setValue(AbstractVertex.NAME_LITERAL, this.properties.getValue(AbstractVertex.NAME_LITERAL), name);
  }

  /**
   * Set this graph info property.
   *
   * @param info
   *          The info property to set for this graph
   */
  public void setInfo(final String info) {
    this.properties.setValue(AbstractVertex.INFO_LITERAL, this.properties.getValue(AbstractVertex.INFO_LITERAL), info);
  }

  /*
   * (non-Javadoc)
   *
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  @Override
  @SuppressWarnings("rawtypes")
  public void update(final Observable o, final Object arg) {
    if ((arg instanceof String) && (o instanceof AbstractEdge)) {
      final Object property = ((AbstractVertex) o).getPropertyBean().getValue((String) arg);
      if (property != null) {
        this.getPropertyBean().setValue((String) arg, property);
      }
    }
  }

  /**
   * Gives the argument of this graph with the given name.
   *
   * @param name
   *          The name of the argument to get
   * @return The argument with the given name
   */
  public Argument getArgument(final String name) {
    if (this.properties.getValue(AbstractVertex.ARGUMENTS_LITERAL) != null) {
      final Argument arg = this.properties.<ArgumentSet>getValue(AbstractVertex.ARGUMENTS_LITERAL).getArgument(name);
      if (arg != null) {
        arg.setExpressionSolver(this.getBase());
      }
      return arg;
    }
    return null;
  }

  /**
   * Gives the argument set of this graph.
   *
   * @return The set of argument of this graph
   */
  public ArgumentSet getArguments() {
    if (this.properties.getValue(AbstractVertex.ARGUMENTS_LITERAL) == null) {
      final ArgumentSet arguments = new ArgumentSet();
      this.setArgumentSet(arguments);
    }
    this.properties.<ArgumentSet>getValue(AbstractVertex.ARGUMENTS_LITERAL).setExpressionSolver(this.getBase());
    return this.properties.<ArgumentSet>getValue(AbstractVertex.ARGUMENTS_LITERAL);
  }

  /**
   * Set the arguments set for this vertex.
   *
   * @param arguments
   *          The set of arguments for this graph
   */
  public void setArgumentSet(final ArgumentSet arguments) {
    this.properties.setValue(AbstractVertex.ARGUMENTS_LITERAL,
        this.properties.getValue(AbstractVertex.ARGUMENTS_LITERAL), arguments);
    arguments.setExpressionSolver(this.getBase());
  }

  /**
   * Add the given argument to his graph argument set.
   *
   * @param arg
   *          The argument to add
   */
  public void addArgument(final Argument arg) {
    if (this.properties.getValue(AbstractVertex.ARGUMENTS_LITERAL) == null) {
      setArgumentSet(new ArgumentSet());
    }
    this.properties.<ArgumentSet>getValue(AbstractVertex.ARGUMENTS_LITERAL).addArgument(arg);
    arg.setExpressionSolver(this.getBase());
  }

  /**
   * Sets this vertex kind.
   *
   * @param kind
   *          The kind of the vertex (port, vertex)
   */
  public void setKind(final String kind) {
    this.properties.setValue(AbstractVertex.KIND_LITERAL, this.properties.getValue(AbstractVertex.KIND_LITERAL), kind);
  }

  /**
   * gets this vertex kind.
   *
   * @return The string representation of the kind of this vertex
   */
  public String getKind() {
    return this.properties.getValue(AbstractVertex.KIND_LITERAL);
  }

  /**
   * Notify the vertex that it has been connected using the given edge.
   *
   * @param e
   *          the e
   */
  public void connectionAdded(AbstractEdge<?, ?> e) {
    // nothing by default
  }

  /**
   * Notify the vertex that it has been disconnected froms the given edge.
   *
   * @param e
   *          the e
   */
  public void connectionRemoved(AbstractEdge<?, ?> e) {
    // nothing by default
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  @SuppressWarnings("rawtypes")
  public boolean equals(final Object e) {
    if (e instanceof AbstractVertex) {
      return ((AbstractVertex) e).getName().equals(this.getName());
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getName());
  }

  /**
   * Gives the edge this interface is associated to.
   *
   * @param port
   *          The for which to look for edges
   * @return The found edge, null if not edge match the given port
   */
  @SuppressWarnings("rawtypes")
  public AbstractEdge getAssociatedEdge(final IInterface port) {
    final AbstractVertex portVertex = (AbstractVertex) port;
    for (final Object edgeObj : getBase().incomingEdgesOf(this)) {
      final AbstractEdge edge = (AbstractEdge) edgeObj;
      if (((edge.getTargetLabel() != null) && edge.getTargetLabel().equals(portVertex.getName()))) {
        return edge;
      }
    }
    for (final Object edgeObj : getBase().outgoingEdgesOf(this)) {
      final AbstractEdge edge = (AbstractEdge) edgeObj;
      if ((edge.getSourceLabel() != null) && edge.getSourceLabel().equals(portVertex.getName())) {
        return edge;
      }
    }
    return null;
  }

}
