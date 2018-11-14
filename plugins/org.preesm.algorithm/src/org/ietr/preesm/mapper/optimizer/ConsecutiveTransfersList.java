/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
package org.ietr.preesm.mapper.optimizer;

import java.util.ArrayList;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.preesm.mapper.model.special.ReceiveVertex;
import org.ietr.preesm.mapper.model.special.SendVertex;
import org.ietr.preesm.mapper.model.special.TransferVertex;
import org.preesm.model.slam.ComponentInstance;

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
