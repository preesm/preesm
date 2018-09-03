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

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.preesm.mapper.ScheduledDAGIterator;

/**
 *
 */
public class ConsecutiveTransfersMap extends LinkedHashMap<ComponentInstance, ConsecutiveTransfersList> {

  private static final long serialVersionUID = -8987927834037340036L;

  static final ComponentInstance findComponent(final DAGVertex vertex) {
    return (ComponentInstance) vertex.getPropertyBean().getValue("Operator");
  }

  final ConsecutiveTransfersGroup findGroup(final DAGVertex vertex) {
    final ComponentInstance findComponent = ConsecutiveTransfersMap.findComponent(vertex);
    final ConsecutiveTransfersList consecutiveTransfersList = get(findComponent);
    for (final ConsecutiveTransfersGroup group : consecutiveTransfersList) {
      if (group.contains(vertex)) {
        return group;
      }
    }
    return null;
  }

  private ConsecutiveTransfersMap() {
    super();
  }

  /**
   *
   */
  public static final ConsecutiveTransfersMap initFrom(final DirectedAcyclicGraph dag) {
    final ConsecutiveTransfersMap res = new ConsecutiveTransfersMap();

    final ScheduledDAGIterator dagIterator = new ScheduledDAGIterator(dag);
    while (dagIterator.hasNext()) {
      final DAGVertex currentVertex = dagIterator.next();
      final ComponentInstance findComponent = ConsecutiveTransfersMap.findComponent(currentVertex);

      final ConsecutiveTransfersList transferList = res.getOrDefault(findComponent,
          new ConsecutiveTransfersList(res, findComponent));
      transferList.process(currentVertex);
      res.put(findComponent, transferList);
    }
    return res;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    for (final Entry<ComponentInstance, ConsecutiveTransfersList> e : entrySet()) {
      final ComponentInstance componentInstance = e.getKey();
      final ConsecutiveTransfersList transferGroupList = e.getValue();
      sb.append("  - " + componentInstance.getInstanceName() + ": " + transferGroupList.toString() + "\n");
    }
    return sb.toString();
  }
}
