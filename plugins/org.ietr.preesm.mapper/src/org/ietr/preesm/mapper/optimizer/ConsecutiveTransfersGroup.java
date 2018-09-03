package org.ietr.preesm.mapper.optimizer;

import java.util.ArrayList;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.preesm.mapper.model.special.ReceiveVertex;
import org.ietr.preesm.mapper.model.special.SendVertex;
import org.ietr.preesm.mapper.model.special.TransferVertex;

/**
 *
 * @author anmorvan
 *
 */
public class ConsecutiveTransfersGroup extends ArrayList<TransferVertex> {

  private static final long              serialVersionUID = -2817359565233320423L;
  private final ComponentInstance        component;
  private final ConsecutiveTransfersList list;

  public ConsecutiveTransfersGroup(final ConsecutiveTransfersList list, final ComponentInstance component) {
    this.list = list;
    this.component = component;
  }

  @Override
  public String toString() {
    if (isEmpty()) {
      return "[]";
    } else {
      final StringBuilder sb = new StringBuilder();
      sb.append("[" + size() + "x");
      if (get(0) instanceof ReceiveVertex) {
        sb.append("R");
      } else if (get(0) instanceof SendVertex) {
        sb.append("S");
      } else {
        sb.append("GURU");
      }
      sb.append("]");
      return sb.toString();
    }
  }

  public int getGroupID() {
    return this.list.indexOf(this);
  }

  public ConsecutiveTransfersList getList() {
    return this.list;
  }

  public ConsecutiveTransfersMap getMap() {
    return getList().getMap();
  }

  public ComponentInstance getComponent() {
    return this.component;
  }
}
