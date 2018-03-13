/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2018)
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

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import org.ietr.dftools.algorithm.iterators.DAGIterator;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.preesm.core.types.ImplementationPropertyNames;

/**
 * The purpose of this class is to remove redundant synchronization created during the scheduling of an application. <br>
 * <br>
 * A synchronization is a communication supported by a communication node with the "zero-copy" properties. If several synchronization occur between a give pair
 * of core, come of them may be redundant, which means that they enforce a synchronization which is already enforced by a previous synchronization.
 * 
 * @author kdesnos
 *
 */
public class RedundantSynchronizationCleaner {
  /**
   * Private empty constructor to prevent use of this class.
   */
  private RedundantSynchronizationCleaner() {
  }

  /**
   * Analyzes the communications in the {@link DirectedAcyclicGraph} and remove redundant synchronizations.
   * 
   * @param dag
   *          The {@link DirectedAcyclicGraph} whose synchronizations are optimized. The DAG is modified during call to the function.
   */
  public static void cleanRedundantSynchronization(DirectedAcyclicGraph dag) {
    // This Map associates to each component a Deque of all its received communications.
    // Received communications are stored as a Deque of Set where each set represents a group of consecutive receive communication primitive that is not
    // "interrupted" by any other computation.
    Map<ComponentInstance, Deque<Set<DAGVertex>>> receiveGroups = new HashMap<>();

    // Fill the receiveGroups with an empty list for each component
    final DAGIterator iterDAGVertices = new DAGIterator(dag); // Iterator on DAG vertices
    // Store if the type of the last DAGVertex scheduled on each core (during the scan of the DAG) is receive or not (to identify groups)
    final Map<ComponentInstance, Boolean> lastVertexScheduled = new HashMap<>();
    while (iterDAGVertices.hasNext()) {
      final DAGVertex currentVertex = iterDAGVertices.next();

      // Get vertex type
      final String vertexType = currentVertex.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType).toString();
      final boolean isReceive = vertexType.equals("receive");

      // Get component
      final ComponentInstance component = (ComponentInstance) currentVertex.getPropertyBean().getValue("Operator");

      // If the currentVertex is a receive communication, store it in the receiveGroups
      if (isReceive) {
        // Get or create the appropriate receive group
        Set<DAGVertex> receiveGroup;
        if (!receiveGroups.containsKey(component) || lastVertexScheduled.get(component)) {
          // If the component still has no receive group OR if its last scheduled vertex is not a receive.
          // Create a new group
          receiveGroup = new LinkedHashSet<DAGVertex>();
        } else {
          // The component is associated with a group of receive AND the last scheduled vertex was a receive.
          receiveGroup = receiveGroups.get(component).getLast();
        }
        receiveGroup.add(currentVertex);

        // Store the receiveGroup (if needed) with the appropriate component.
        if (!receiveGroups.containsKey(component)) {
          // Create first receive group of the component
          Deque<Set<DAGVertex>> componentDeque = new LinkedList<>();
          receiveGroups.put(component, componentDeque);
          componentDeque.add(receiveGroup);
        } else if (lastVertexScheduled.get(component)) {
          // Add the new receive group to the component
          receiveGroups.get(component).add(receiveGroup);
        } // Else to both ifs, the vertex was directly added to an existing receive group in previous if statement.
      }

      // Save last vertex (replace previously saved type)
      lastVertexScheduled.put(component, isReceive);
    }
  }
}
