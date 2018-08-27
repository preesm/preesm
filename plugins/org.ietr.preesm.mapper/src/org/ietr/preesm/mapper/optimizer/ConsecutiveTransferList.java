package org.ietr.preesm.mapper.optimizer;

import java.util.ArrayList;
import org.ietr.preesm.mapper.model.special.TransferVertex;

/**
 *
 * @author anmorvan
 *
 */
public class ConsecutiveTransferList extends ArrayList<TransferVertex> {

  public ConsecutiveTransferList(TransferVertex currentVertex) {
    super();
    this.add(currentVertex);
  }

  /**
   *
   */
  private static final long serialVersionUID = -8985393970586882459L;

}
