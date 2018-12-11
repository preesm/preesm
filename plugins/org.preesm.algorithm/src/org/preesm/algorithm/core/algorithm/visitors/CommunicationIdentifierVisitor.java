/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
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
package org.preesm.algorithm.core.algorithm.visitors;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.preesm.algorithm.model.PropertyBean;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.algorithm.model.visitors.IGraphVisitor;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.scenario.types.ImplementationPropertyNames;
import org.preesm.model.scenario.types.VertexType;
import org.preesm.model.slam.ComponentInstance;

/**
 * Visitor to identify the inter-core communications of a mapped DAG. This visitor is inspired by
 * CommunicationRouter.routeAll() implementation.
 *
 * @author kdesnos
 *
 */
public class CommunicationIdentifierVisitor implements IGraphVisitor<DirectedAcyclicGraph, DAGVertex, DAGEdge> {

  /** The inter core comm. */
  protected Set<DAGEdge> interCoreComm;

  /**
   * Constructor of the CommunicationIdentifier.
   */
  public CommunicationIdentifierVisitor() {
    this.interCoreComm = new LinkedHashSet<>();
  }

  /**
   * Return the result of the visitor algorithm.
   *
   * @return list containing edges that are inter-core communications
   */
  public Set<DAGEdge> getResult() {
    return this.interCoreComm;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.visitors.IGraphVisitor#visit(org.ietr.dftools.algorithm.model.AbstractEdge)
   */
  @Override
  public void visit(final DAGEdge currentEdge) {
    // First, we check that both source and target vertices are tasks
    // i.e. we skip existing send/receive nodes
    final DAGVertex src = currentEdge.getSource();
    final PropertyBean srcBeans = src.getPropertyBean();
    final String srcType = srcBeans.getValue(ImplementationPropertyNames.Vertex_vertexType).toString();
    final DAGVertex tgt = currentEdge.getTarget();
    final PropertyBean tgtBeans = tgt.getPropertyBean();
    final String tgtType = tgtBeans.getValue(ImplementationPropertyNames.Vertex_vertexType).toString();
    if (VertexType.TYPE_TASK.equals(srcType) && VertexType.TYPE_TASK.equals(tgtType)) {

      final ComponentInstance sourceComponent = (ComponentInstance) srcBeans.getValue("Operator");
      final ComponentInstance targetComponent = (ComponentInstance) tgtBeans.getValue("Operator");

      if ((sourceComponent != null) && (targetComponent != null) && (!sourceComponent.equals(targetComponent))) {
        // This code is reached only if the current edge is an
        // inter-core communication
        this.interCoreComm.add(currentEdge);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.visitors.IGraphVisitor#visit(org.ietr.dftools.algorithm.model.AbstractGraph)
   */
  @Override
  public void visit(final DirectedAcyclicGraph dag) throws PreesmException {
    // We iterate the edges
    final Iterator<DAGEdge> iterator = dag.edgeSet().iterator();

    while (iterator.hasNext()) {
      final DAGEdge currentEdge = iterator.next();
      currentEdge.accept(this);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.visitors.IGraphVisitor#visit(org.ietr.dftools.algorithm.model.AbstractVertex)
   */
  @Override
  public void visit(final DAGVertex dagVertex) throws PreesmException {
    // Nothing to do here for this visitor

  }

}
