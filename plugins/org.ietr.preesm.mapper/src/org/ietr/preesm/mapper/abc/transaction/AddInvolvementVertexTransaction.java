/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2012 - 2014)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/

package org.ietr.preesm.mapper.abc.transaction;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.mapper.abc.order.OrderManager;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.special.InvolvementVertex;
import org.ietr.preesm.mapper.model.special.PrecedenceEdge;
import org.ietr.preesm.mapper.model.special.PrecedenceEdgeAdder;
import org.ietr.preesm.mapper.model.special.TransferVertex;

// TODO: Auto-generated Javadoc
/**
 * Transaction executing the addition of an involvement vertex.
 *
 * @author mpelcat
 */
public class AddInvolvementVertexTransaction extends Transaction {

  // Inputs
  /**
   * Determining if the current involvement is executed by the sender or by the receiver of the
   * transfer.
   */
  private final boolean isSender;

  /** Implementation DAG to which the vertex is added. */
  private MapperDAG implementation = null;

  /** Route step corresponding to this involvement. */
  private AbstractRouteStep step = null;

  /** time of this involvement. */
  long involvementTime = 0;

  /** Original edge and transfer corresponding to this involvement. */
  private MapperDAGEdge edge = null;

  /** manager keeping scheduling orders. */
  private OrderManager orderManager = null;

  // Generated objects
  /** involvement vertex added. */
  private InvolvementVertex iVertex = null;

  // private MapperDAGEdge newOutEdge = null;

  /**
   * Instantiates a new adds the involvement vertex transaction.
   *
   * @param isSender
   *          the is sender
   * @param edge
   *          the edge
   * @param implementation
   *          the implementation
   * @param step
   *          the step
   * @param involvementTime
   *          the involvement time
   * @param orderManager
   *          the order manager
   */
  public AddInvolvementVertexTransaction(final boolean isSender, final MapperDAGEdge edge,
      final MapperDAG implementation, final AbstractRouteStep step, final long involvementTime,
      final OrderManager orderManager) {
    super();
    this.isSender = isSender;
    this.edge = edge;
    this.implementation = implementation;
    this.step = step;
    this.orderManager = orderManager;
    this.involvementTime = involvementTime;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ietr.preesm.mapper.abc.transaction.Transaction#execute(java.util.List)
   */
  @Override
  public void execute(final List<Object> resultList) {

    super.execute(resultList);

    final MapperDAGVertex currentSource = (MapperDAGVertex) this.edge.getSource();
    final MapperDAGVertex currentTarget = (MapperDAGVertex) this.edge.getTarget();

    if (this.edge instanceof PrecedenceEdge) {
      WorkflowLogger.getLogger().log(Level.INFO,
          "no involvement vertex corresponding to a schedule edge");
      return;
    }

    final String ivertexID = "__involvement (" + currentSource.getName() + ","
        + currentTarget.getName() + ")";

    if (this.involvementTime > 0) {
      this.iVertex = new InvolvementVertex(ivertexID, this.implementation);
      this.implementation.getTimings().dedicate(this.iVertex);
      this.implementation.getMappings().dedicate(this.iVertex);

      this.implementation.addVertex(this.iVertex);
      this.iVertex.getTiming().setCost(this.involvementTime);

      if (this.isSender) {
        this.iVertex.setEffectiveOperator(this.step.getSender());
        ((TransferVertex) currentTarget).setInvolvementVertex(this.iVertex);
      } else {
        this.iVertex.setEffectiveOperator(this.step.getReceiver());
        ((TransferVertex) currentSource).setInvolvementVertex(this.iVertex);
      }

      if (this.isSender) {
        final MapperDAGEdge newInEdge = (MapperDAGEdge) this.implementation.addEdge(currentSource,
            this.iVertex);
        newInEdge.setInit(this.edge.getInit().clone());
        newInEdge.getTiming().setCost(0);

        MapperDAGVertex receiverVertex = currentTarget;
        do {
          final Set<MapperDAGVertex> succs = receiverVertex.getSuccessors(false).keySet();
          if (succs.isEmpty() && (receiverVertex instanceof TransferVertex)) {
            WorkflowLogger.getLogger().log(Level.SEVERE,
                "Transfer has no successor: " + receiverVertex.getName());
          }

          for (final MapperDAGVertex next : receiverVertex.getSuccessors(false).keySet()) {
            if (next != null) {
              receiverVertex = next;
            }
          }
        } while (receiverVertex instanceof TransferVertex);

        final MapperDAGEdge newoutEdge = (MapperDAGEdge) this.implementation.addEdge(this.iVertex,
            receiverVertex);
        newoutEdge.setInit(this.edge.getInit().clone());
        newoutEdge.getTiming().setCost(0);

        // TODO: Look at switching possibilities
        /*
         * if (false) { TaskSwitcher taskSwitcher = new TaskSwitcher();
         * taskSwitcher.setOrderManager(orderManager);
         * taskSwitcher.insertVertexBefore(currentTarget, iVertex); } else
         */
        this.orderManager.insertBefore(currentTarget, this.iVertex);

      } else {
        final MapperDAGEdge newOutEdge = (MapperDAGEdge) this.implementation.addEdge(this.iVertex,
            currentTarget);
        newOutEdge.setInit(this.edge.getInit().clone());
        newOutEdge.getTiming().setCost(0);

        this.orderManager.insertAfter(currentSource, this.iVertex);
      }

      // Scheduling involvement vertex
      new PrecedenceEdgeAdder(this.orderManager, this.implementation).scheduleVertex(this.iVertex);

      if (resultList != null) {
        resultList.add(this.iVertex);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ietr.preesm.mapper.abc.transaction.Transaction#toString()
   */
  @Override
  public String toString() {
    return ("AddInvolvement(" + this.iVertex.toString() + ")");
  }

}
