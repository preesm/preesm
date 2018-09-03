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
