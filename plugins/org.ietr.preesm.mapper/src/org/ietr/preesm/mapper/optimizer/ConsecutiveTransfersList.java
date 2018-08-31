package org.ietr.preesm.mapper.optimizer;

import java.util.ArrayList;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.preesm.mapper.model.special.ReceiveVertex;
import org.ietr.preesm.mapper.model.special.SendVertex;
import org.ietr.preesm.mapper.model.special.TransferVertex;

/**
 *
 * @author anmorvan
 *
 */
public class ConsecutiveTransfersList extends ArrayList<ConsecutiveTransfersGroup> {

  private static final long             serialVersionUID = 6193786397249653544L;
  private final ComponentInstance       component;
  private final ConsecutiveTransfersMap map;
  private DAGVertex                     lastVertex       = null;

  public ConsecutiveTransfersList(final ConsecutiveTransfersMap map, final ComponentInstance component) {
    this.map = map;
    this.component = component;
  }

  private ConsecutiveTransfersGroup getLastGroup() {
    if (isEmpty()) {
      this.add(new ConsecutiveTransfersGroup(this, getComponent()));
    }
    return get(size() - 1);
  }

  /**
   *
   */
  public void process(final DAGVertex currentVertex) {
    if (currentVertex instanceof SendVertex) {
      addSend((SendVertex) currentVertex);
    } else if (currentVertex instanceof ReceiveVertex) {
      addReceive((ReceiveVertex) currentVertex);
    } else {
      // simply change last vertex
    }
    this.lastVertex = currentVertex;
  }

  private void breakGroup() {
    if (!getLastGroup().isEmpty()) {
      this.add(new ConsecutiveTransfersGroup(this, getComponent()));
    }
  }

  private void addReceive(final ReceiveVertex currentVertex) {
    if (!(this.lastVertex instanceof ReceiveVertex)) {
      breakGroup();
    }
    addToGroup(currentVertex);
  }

  private void addSend(final SendVertex currentVertex) {
    if (!(this.lastVertex instanceof SendVertex)) {
      breakGroup();
    }
    addToGroup(currentVertex);

  }

  private void addToGroup(final TransferVertex currentVertex) {
    final ConsecutiveTransfersGroup lastGroup = getLastGroup();
    lastGroup.add(currentVertex);
    currentVertex.getPropertyBean().setValue("SYNC_GROUP", lastGroup.getGroupID());
  }

  public ComponentInstance getComponent() {
    return this.component;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (final ConsecutiveTransfersGroup group : this) {
      if (first) {
        first = false;
      } else {
        sb.append("-");
      }
      sb.append(group.toString());
    }
    return sb.toString();
  }

  public ConsecutiveTransfersMap getMap() {
    return this.map;
  }

}
