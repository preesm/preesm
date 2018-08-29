/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2014)
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
package org.ietr.preesm.mapper.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.ietr.dftools.algorithm.model.AbstractVertex;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.component.Operator;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.types.ImplementationPropertyNames;
import org.ietr.preesm.mapper.model.property.VertexInit;
import org.ietr.preesm.mapper.model.property.VertexMapping;
import org.ietr.preesm.mapper.model.property.VertexTiming;
import org.ietr.preesm.mapper.model.special.OverheadVertex;
import org.ietr.preesm.mapper.model.special.PrecedenceEdge;
import org.ietr.preesm.mapper.model.special.ReceiveVertex;
import org.ietr.preesm.mapper.model.special.SendVertex;
import org.ietr.preesm.mapper.model.special.TransferVertex;

// TODO: Auto-generated Javadoc
/**
 * Represents a vertex in a DAG of type {@link MapperDAG} used in the mapper.
 *
 * @author mpelcat
 */
public class MapperDAGVertex extends DAGVertex {

  /** Operator to which the vertex has been affected by the mapping algorithm. */
  private ComponentInstance effectiveComponent;

  /** Properties set when converting sdf to dag. */
  private static final String INITIAL_PROPERTY = "INITIAL_PROPERTY";
  // protected InitialVertexProperty initialVertexProperty;

  static {
    {
      AbstractVertex.public_properties.add(ImplementationPropertyNames.Vertex_OperatorDef);
      AbstractVertex.public_properties.add(ImplementationPropertyNames.Vertex_Available_Operators);
      AbstractVertex.public_properties.add(ImplementationPropertyNames.Vertex_originalVertexId);
      AbstractVertex.public_properties.add(ImplementationPropertyNames.Task_duration);
      AbstractVertex.public_properties.add(ImplementationPropertyNames.Vertex_schedulingOrder);
      AbstractVertex.public_properties.add(ImplementationPropertyNames.Vertex_Operator);

    }
  }

  /**
   * Instantiates a new mapper DAG vertex.
   */
  public MapperDAGVertex() {

    this("default", "default", null);
    this.effectiveComponent = DesignTools.NO_COMPONENT_INSTANCE;
  }

  /**
   * Instantiates a new mapper DAG vertex.
   *
   * @param id
   *          the id
   * @param base
   *          the base
   */
  public MapperDAGVertex(final String id, final MapperDAG base) {

    this(id, id, base);
  }

  /**
   * Instantiates a new mapper DAG vertex.
   *
   * @param id
   *          the id
   * @param name
   *          the name
   * @param base
   *          the base
   */
  public MapperDAGVertex(final String id, final String name, final MapperDAG base) {

    super();

    setName(name);
    getPropertyBean().setValue(MapperDAGVertex.INITIAL_PROPERTY, new VertexInit());
    getInit().setParentVertex(this);
    this.effectiveComponent = DesignTools.NO_COMPONENT_INSTANCE;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.dag.DAGVertex#clone()
   */
  @Override
  public MapperDAGVertex clone() {

    MapperDAGVertex result = null;

    if (this instanceof OverheadVertex) {
      result = new OverheadVertex(getId(), (MapperDAG) getBase());
    } else if (this instanceof SendVertex) {
      result = new SendVertex(getId(), (MapperDAG) getBase(), ((SendVertex) this).getSource(),
          ((SendVertex) this).getTarget(), ((SendVertex) this).getRouteStepIndex(), ((SendVertex) this).getNodeIndex());
    } else if (this instanceof ReceiveVertex) {
      result = new ReceiveVertex(getId(), (MapperDAG) getBase(), ((ReceiveVertex) this).getSource(),
          ((ReceiveVertex) this).getTarget(), ((ReceiveVertex) this).getRouteStepIndex(),
          ((ReceiveVertex) this).getNodeIndex());
    } else if (this instanceof TransferVertex) {
      final TransferVertex t = (TransferVertex) this;
      result = new TransferVertex(getId(), (MapperDAG) getBase(), t.getSource(), t.getTarget(), t.getRouteStepIndex(),
          t.getNodeIndex());
    } else {
      result = new MapperDAGVertex(getId(), getName(), (MapperDAG) getBase());
    }

    result.setInit(getInit().clone(result));
    result.setEffectiveComponent(getEffectiveComponent());

    for (final String propertyKey : getPropertyBean().keys()) {
      final Object property = getPropertyBean().getValue(propertyKey);
      result.getPropertyBean().setValue(propertyKey, property);
    }

    return result;
  }

  /**
   * Gets the inits the.
   *
   * @return the inits the
   */
  public VertexInit getInit() {
    return (VertexInit) getPropertyBean().getValue(MapperDAGVertex.INITIAL_PROPERTY);
  }

  /**
   * Sets the inits the.
   *
   * @param initialVertexProperty
   *          the new inits the
   */
  private void setInit(final VertexInit initialVertexProperty) {
    getPropertyBean().setValue(MapperDAGVertex.INITIAL_PROPERTY, initialVertexProperty);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractVertex#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {

    if (obj instanceof MapperDAGVertex) {
      final MapperDAGVertex v = (MapperDAGVertex) obj;
      return (getName().compareTo(v.getName()) == 0);
    }

    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.dag.DAGVertex#toString()
   */
  @Override
  public String toString() {

    String toString = "";
    if (hasEffectiveComponent()) {
      // If the vertex is mapped, displays its component and rank
      toString = getName() + "(";
      toString += getEffectiveComponent().toString();
      if (getTiming() != null) {
        // toString += "," + this.getTiming().getTotalOrder(this);
        toString += "," + getTiming().getTLevel();
        toString += "," + getTiming().getBLevel();
        toString += "," + getTiming().getCost();
      }
      toString += ")";
    } else {
      // If the vertex is not mapped, displays its weight
      toString = getName() + "(" + getNbRepeat() + ")";
    }

    return toString;
  }

  /**
   * Getting all predecessors, ignoring or not the precedence edges. Predecessors are given as keys for a map containing
   * corresponding edges.
   *
   * @param ignorePrecedence
   *          the ignore precedence
   * @return the predecessors
   */
  public Map<MapperDAGVertex, MapperDAGEdge> getPredecessors(final boolean ignorePrecedence) {

    final Map<MapperDAGVertex, MapperDAGEdge> preds = new LinkedHashMap<>();
    final Set<DAGEdge> incomingSet = incomingEdges();

    if (ignorePrecedence) {
      for (final DAGEdge edge : incomingSet) {
        if (!(edge instanceof PrecedenceEdge) && (edge.getSource() != null)) {
          preds.put((MapperDAGVertex) edge.getSource(), (MapperDAGEdge) edge);
        }
      }
    } else {
      for (final DAGEdge edge : incomingSet) {
        if (edge.getSource() != null) {
          preds.put((MapperDAGVertex) edge.getSource(), (MapperDAGEdge) edge);
        }
      }
    }
    return preds;
  }

  /**
   * Getting all successors, ignoring or not the precedence edges Successors are given as keys for a map containing
   * corresponding edges.
   *
   * @param ignorePrecedence
   *          the ignore precedence
   * @return the successors
   */
  public Map<MapperDAGVertex, MapperDAGEdge> getSuccessors(final boolean ignorePrecedence) {

    final Map<MapperDAGVertex, MapperDAGEdge> succs = new LinkedHashMap<>();
    final Set<DAGEdge> outgoingSet = outgoingEdges();

    if (ignorePrecedence) {
      for (final DAGEdge edge : outgoingSet) {
        if (!(edge instanceof PrecedenceEdge) && (edge.getTarget() != null)) {
          succs.put((MapperDAGVertex) edge.getTarget(), (MapperDAGEdge) edge);
        }
      }
    } else {
      for (final DAGEdge edge : outgoingSet) {
        if (edge.getTarget() != null) {
          succs.put((MapperDAGVertex) edge.getTarget(), (MapperDAGEdge) edge);
        }
      }
    }
    return succs;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.dag.DAGVertex#incomingEdges()
   */
  @Override
  public Set<DAGEdge> incomingEdges() {
    // TODO Auto-generated method stub
    return super.incomingEdges();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.dag.DAGVertex#outgoingEdges()
   */
  @Override
  public Set<DAGEdge> outgoingEdges() {
    // TODO Auto-generated method stub
    return super.outgoingEdges();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractVertex#getPropertyStringValue(java.lang.String)
   */
  @Override
  public String getPropertyStringValue(final String propertyName) {
    if (propertyName.equals(ImplementationPropertyNames.Vertex_OperatorDef)) {
      return getEffectiveOperator().getComponent().getVlnv().getName();
    } else if (propertyName.equals(ImplementationPropertyNames.Vertex_Available_Operators)) {
      return getInit().getInitialOperatorList().toString();
    } else if (propertyName.equals(ImplementationPropertyNames.Vertex_originalVertexId)) {
      return getInit().getParentVertex().getId();
    } else if (propertyName.equals(ImplementationPropertyNames.Task_duration)) {
      return String.valueOf(getTiming().getCost());
    } else if (propertyName.equals(ImplementationPropertyNames.Vertex_schedulingOrder)) {
      return String.valueOf(getTiming().getTotalOrder(this));
    } else if (propertyName.equals(ImplementationPropertyNames.Vertex_Operator)) {
      return getEffectiveComponent().getInstanceName();
    }
    return super.getPropertyStringValue(propertyName);
  }

  /**
   * Gets the total order.
   *
   * @return the total order
   */
  public int getTotalOrder() {
    return getTiming().getTotalOrder(this);
  }

  /**
   * Sets the total order.
   *
   * @param schedulingTotalOrder
   *          the new total order
   */
  public void setTotalOrder(final int schedulingTotalOrder) {
    getTiming().setTotalOrder(getName(), schedulingTotalOrder);
  }

  /**
   * Timing properties are store in the graph and possibly shared with other vertices.
   *
   * @return the timing
   */
  public VertexTiming getTiming() {
    return ((MapperDAG) getBase()).getTimings().getTiming(getName());
  }

  /**
   * Mapping group is stored in the graph. It is a group of vertices sharing mapping properties.
   *
   * @return the mapping
   */
  public VertexMapping getMapping() {
    return ((MapperDAG) getBase()).getMappings().getMapping(getName());
  }

  /**
   * A computation vertex has an effective operator: the operator executing it.
   *
   * @return the effective operator
   */
  public ComponentInstance getEffectiveOperator() {
    if ((this.effectiveComponent != null) && (this.effectiveComponent.getComponent() instanceof Operator)) {
      return this.effectiveComponent;
    } else {
      return DesignTools.NO_COMPONENT_INSTANCE;
    }
  }

  /**
   * Checks for effective operator.
   *
   * @return true, if successful
   */
  public boolean hasEffectiveOperator() {
    return getEffectiveOperator() != DesignTools.NO_COMPONENT_INSTANCE;
  }

  /**
   * Sets the effective operator.
   *
   * @param effectiveOperator
   *          the new effective operator
   */
  public void setEffectiveOperator(final ComponentInstance effectiveOperator) {
    this.effectiveComponent = effectiveOperator;
  }

  /**
   * Effective component is common to communication and computation vertices.
   *
   * @return the effective component
   */
  public ComponentInstance getEffectiveComponent() {
    return this.effectiveComponent;
  }

  /**
   * Checks for effective component.
   *
   * @return true, if successful
   */
  public boolean hasEffectiveComponent() {
    return getEffectiveComponent() != DesignTools.NO_COMPONENT_INSTANCE;
  }

  /**
   * Sets the effective component.
   *
   * @param component
   *          the new effective component
   */
  public void setEffectiveComponent(final ComponentInstance component) {
    this.effectiveComponent = component;
  }
}
