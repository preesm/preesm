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
package org.ietr.preesm.mapper.model.special;

import org.ietr.dftools.algorithm.model.AbstractVertex;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.types.ImplementationPropertyNames;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;

// TODO: Auto-generated Javadoc
/**
 * A transfer vertex represents a route step.
 *
 * @author mpelcat
 */
public class TransferVertex extends MapperDAGVertex {

  /** The Constant SEND_RECEIVE_COST. */
  public static final long SEND_RECEIVE_COST = 100;

  /** The step. */
  private AbstractRouteStep step;

  /** Source and target of the vertex that originated this transfer. */
  private final MapperDAGVertex source;

  /** The target. */
  private final MapperDAGVertex target;

  /** Index of the route step corresponding to this transfer in the route. */
  private final int routeStepIndex;

  /** Index of the node corresponding to this transfer in the route step. */
  private final int nodeIndex;

  /** Sets the involvement (if any) corresponding to this transfer. */
  private InvolvementVertex involvementVertex = null;

  static {
    {
      AbstractVertex.public_properties.add(ImplementationPropertyNames.SendReceive_OperatorDef);
      AbstractVertex.public_properties.add(ImplementationPropertyNames.SendReceive_dataSize);
    }
  }

  /**
   * Instantiates a new transfer vertex.
   *
   * @param id
   *          the id
   * @param base
   *          the base
   * @param source
   *          the source
   * @param target
   *          the target
   * @param routeStepIndex
   *          the route step index
   * @param nodeIndex
   *          the node index
   */
  public TransferVertex(final String id, final MapperDAG base, final MapperDAGVertex source,
      final MapperDAGVertex target, final int routeStepIndex, final int nodeIndex) {
    super(id, base);
    this.source = source;
    this.target = target;
    this.routeStepIndex = routeStepIndex;
    this.nodeIndex = nodeIndex;

    // Retrieve and Save the corresponding DAGEdge in the properties
    final DAGEdge dagEdge = base.getEdge(source, target);
    setPropertyValue(ImplementationPropertyNames.SendReceive_correspondingDagEdge, dagEdge);
  }

  /**
   * Gets the route step.
   *
   * @return the route step
   */
  public AbstractRouteStep getRouteStep() {
    return this.step;
  }

  /**
   * Sets the route step.
   *
   * @param step
   *          the new route step
   */
  public void setRouteStep(final AbstractRouteStep step) {
    this.step = step;
  }

  /**
   * A transfer vertex follows only one vertex. Returning the transfer predecessor if it is an overhead vertex
   *
   * @return the preceding overhead
   */
  public OverheadVertex getPrecedingOverhead() {
    for (final DAGEdge incomingEdge : ((DirectedAcyclicGraph) getBase()).incomingEdgesOf(this)) {
      if (!(incomingEdge instanceof PrecedenceEdge)) {
        final MapperDAGVertex precV = (MapperDAGVertex) incomingEdge.getSource();
        if (precV instanceof OverheadVertex) {
          return (OverheadVertex) precV;
        }
      }
    }

    return null;
  }

  /**
   * Gets the source.
   *
   * @return the source
   */
  public MapperDAGVertex getSource() {
    return this.source;
  }

  /**
   * Gets the target.
   *
   * @return the target
   */
  public MapperDAGVertex getTarget() {
    return this.target;
  }

  /**
   * Gets the route step index.
   *
   * @return the route step index
   */
  public int getRouteStepIndex() {
    return this.routeStepIndex;
  }

  /**
   * Gets the node index.
   *
   * @return the node index
   */
  public int getNodeIndex() {
    return this.nodeIndex;
  }

  /**
   * Gets the involvement vertex.
   *
   * @return the involvement vertex
   */
  public InvolvementVertex getInvolvementVertex() {
    return this.involvementVertex;
  }

  /**
   * Sets the involvement vertex.
   *
   * @param involvementVertex
   *          the new involvement vertex
   */
  public void setInvolvementVertex(final InvolvementVertex involvementVertex) {
    this.involvementVertex = involvementVertex;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.model.MapperDAGVertex#getPropertyStringValue(java.lang.String)
   */
  @Override
  public String getPropertyStringValue(final String propertyName) {
    if (propertyName.equals(ImplementationPropertyNames.SendReceive_OperatorDef)) {
      return getEffectiveOperator().getComponent().getVlnv().getName();
    }
    return super.getPropertyStringValue(propertyName);
  }

}
