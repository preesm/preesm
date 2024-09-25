/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Jonathan Piat [jpiat@laas.fr] (2011)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2013)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2014)
 * Mickaël Dardaillon [mickael.dardaillon@insa-rennes.fr] (2020)
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
package org.preesm.algorithm.mapper.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.preesm.algorithm.mapper.graphtransfo.ImplementationPropertyNames;
import org.preesm.algorithm.mapper.model.property.VertexInit;
import org.preesm.algorithm.mapper.model.property.VertexMapping;
import org.preesm.algorithm.mapper.model.property.VertexTiming;
import org.preesm.algorithm.mapper.model.special.OverheadVertex;
import org.preesm.algorithm.mapper.model.special.PrecedenceEdge;
import org.preesm.algorithm.mapper.model.special.ReceiveVertex;
import org.preesm.algorithm.mapper.model.special.SendVertex;
import org.preesm.algorithm.mapper.model.special.TransferVertex;
import org.preesm.algorithm.model.AbstractVertex;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.ProcessingElement;

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

  public static final String DAG_END_VERTEX = "dag_end_vertex";

  /** Key to access to property dag_broadcast_vertex. */
  public static final String DAG_BROADCAST_VERTEX = "dag_broadcast_vertex";

  /** Key to access to property special_type. */
  public static final String SPECIAL_TYPE = "special_type";

  /** Key to access to property special_type_broadcast. */
  public static final String SPECIAL_TYPE_BROADCAST = "special_type_broadcast";

  /** Key to access to property special_type_roundbuffer. */
  public static final String SPECIAL_TYPE_ROUNDBUFFER = "special_type_roundbuffer";

  /** Key to access to property dag_init_vertex. */
  public static final String DAG_INIT_VERTEX = "dag_init_vertex";

  /** Persistence level of a delay */
  public static final String PERSISTENCE_LEVEL = "persistence_level";

  /** The Constant END_REFERENCE. */
  public static final String END_REFERENCE = "END_REFERENCE";

  /** The Constant INIT_SIZE. */
  public static final String INIT_SIZE = "INIT_SIZE";

  /** Key to access to property dag_broadcast_vertex. */
  public static final String DAG_FORK_VERTEX = "dag_fork_vertex";

  /** Key to access to property dag_broadcast_vertex. */
  public static final String DAG_JOIN_VERTEX = "dag_join_vertex";
  /** Key to access to property dag_broadcast_vertex. */
  public static final String DAG_GPU_OFFLOAD = "gpu_offload_vertex";

  static {
    AbstractVertex.public_properties.add(ImplementationPropertyNames.VERTEX_OPERATOR_DEF);
    AbstractVertex.public_properties.add(ImplementationPropertyNames.VERTEX_AVAILABLE_OPERATORS);
    AbstractVertex.public_properties.add(ImplementationPropertyNames.VERTEX_ORIGINAL_VERTEX_ID);
    AbstractVertex.public_properties.add(ImplementationPropertyNames.TASK_DURATION);
    AbstractVertex.public_properties.add(ImplementationPropertyNames.VERTEX_SCHEDULING_ORDER);
    AbstractVertex.public_properties.add(ImplementationPropertyNames.VERTEX_OPERATOR);
  }

  /**
   * Instantiates a new mapper DAG vertex.
   */
  public MapperDAGVertex(final org.preesm.model.pisdf.AbstractVertex referencePiVertex) {
    this("default", referencePiVertex);
    this.effectiveComponent = null;
  }

  /**
   * Instantiates a new mapper DAG vertex.
   *
   * @param name
   *          the name
   */
  public MapperDAGVertex(final String name, final org.preesm.model.pisdf.AbstractVertex referencePiVertex) {

    super(referencePiVertex);

    setName(name);

    getPropertyBean().setValue(MapperDAGVertex.INITIAL_PROPERTY, new VertexInit());
    getInit().setParentVertex(this);
    this.effectiveComponent = null;
  }

  /**
   *
   */
  @Override
  public <T extends org.preesm.model.pisdf.AbstractVertex> T getReferencePiVertex() {
    final T referencePiVertex = super.getReferencePiVertex();
    if (referencePiVertex == null) {
      final MapperDAG dag = (MapperDAG) this.getBase();
      final PiGraph referencePiMMGraph = dag.getReferencePiMMGraph();
      // reference PiSDF graph should be SRDAG at this point.
      @SuppressWarnings("unchecked")
      final T lookupVertex = (T) referencePiMMGraph.lookupVertex(this.getName());
      return lookupVertex;
    }
    return referencePiVertex;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.dag.DAGVertex#clone()
   */
  @Override
  public MapperDAGVertex copy() {

    MapperDAGVertex result = null;

    if (this instanceof OverheadVertex) {
      result = new OverheadVertex(getId(), origVertex);

    } else if (this instanceof SendVertex) {
      result = new SendVertex(getId(), (MapperDAG) getBase(), ((SendVertex) this).getSource(),
          ((SendVertex) this).getTarget(), ((SendVertex) this).getRouteStepIndex(), ((SendVertex) this).getNodeIndex(),
          origVertex);
    } else if (this instanceof ReceiveVertex) {
      result = new ReceiveVertex(getId(), (MapperDAG) getBase(), ((ReceiveVertex) this).getSource(),
          ((ReceiveVertex) this).getTarget(), ((ReceiveVertex) this).getRouteStepIndex(),
          ((ReceiveVertex) this).getNodeIndex(), origVertex);
    } else if (this instanceof final TransferVertex t) {
      result = new TransferVertex(getId(), (MapperDAG) getBase(), t.getSource(), t.getTarget(), t.getRouteStepIndex(),
          t.getNodeIndex(), origVertex);
    } else {
      result = new MapperDAGVertex(getName(), origVertex);
    }
    final VertexInit copy = getInit().copy();
    copy.setParentVertex(result);
    result.setInit(copy);
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
    return getPropertyBean().getValue(MapperDAGVertex.INITIAL_PROPERTY);
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

  @Override
  public boolean equals(final Object obj) {

    if (obj instanceof final MapperDAGVertex v) {
      return (getName().compareTo(v.getName()) == 0);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName());
  }

  @Override
  public String toString() {

    String toString = "";
    if (hasEffectiveComponent()) {
      // If the vertex is mapped, displays its component and rank
      toString = getName() + "(";
      toString += getEffectiveComponent().toString();
      if (getTiming() != null) {
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
   * @see org.ietr.dftools.algorithm.model.AbstractVertex#getPropertyStringValue(java.lang.String)
   */
  @Override
  public String getPropertyStringValue(final String propertyName) {
    if (propertyName.equals(ImplementationPropertyNames.VERTEX_OPERATOR_DEF)) {
      return getEffectiveOperator().getComponent().getVlnv().getName();
    }

    if (propertyName.equals(ImplementationPropertyNames.VERTEX_AVAILABLE_OPERATORS)) {
      return getInit().getInitialOperatorList().toString();
    }
    if (propertyName.equals(ImplementationPropertyNames.VERTEX_ORIGINAL_VERTEX_ID)) {
      return getInit().getParentVertex().getId();
    }
    if (propertyName.equals(ImplementationPropertyNames.TASK_DURATION)) {
      return String.valueOf(getTiming().getCost());
    }
    if (propertyName.equals(ImplementationPropertyNames.VERTEX_SCHEDULING_ORDER)) {
      return String.valueOf(getTiming().getTotalOrder(this));
    }
    if (propertyName.equals(ImplementationPropertyNames.VERTEX_OPERATOR)) {
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

  public org.preesm.model.pisdf.AbstractVertex getOrigVertex() {
    return origVertex;
  }

  /**
   * A computation vertex has an effective operator: the operator executing it.
   *
   * @return the effective operator
   */
  public ComponentInstance getEffectiveOperator() {
    if ((this.effectiveComponent != null) && (this.effectiveComponent.getComponent() instanceof ProcessingElement)) {
      return this.effectiveComponent;
    }
    return null;
  }

  /**
   * Checks for effective operator.
   *
   * @return true, if successful
   */
  public boolean hasEffectiveOperator() {
    return getEffectiveOperator() != null;
  }

  public void setEffectiveComponent(final ComponentInstance component) {
    this.effectiveComponent = component;
  }

  public ComponentInstance getEffectiveComponent() {
    return this.effectiveComponent;
  }

  /**
   * Checks for effective component.
   *
   * @return true, if successful
   */
  public boolean hasEffectiveComponent() {
    return getEffectiveComponent() != null;
  }
}
