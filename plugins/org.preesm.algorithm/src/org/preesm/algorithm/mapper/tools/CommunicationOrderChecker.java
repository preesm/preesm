/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2019)
 * Julien Hascoet <jhascoet@kalray.eu> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2017)
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
package org.preesm.algorithm.mapper.tools;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.preesm.algorithm.mapper.ScheduledDAGIterator;
import org.preesm.algorithm.mapper.model.special.ReceiveVertex;
import org.preesm.algorithm.mapper.model.special.TransferVertex;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.scenario.types.ImplementationPropertyNames;
import org.preesm.model.slam.ComponentInstance;

/**
 * The purpose of the {@link CommunicationOrderChecker} is to verify the order of communications resulting from a
 * scheduling.
 *
 *
 * @author kdesnos
 *
 */
public class CommunicationOrderChecker {

  private CommunicationOrderChecker() {
    // Private empty constructor for a static-member-only class
  }

  /**
   * Function responsible for checking the validity of the schedule as specified Hereafter.
   * 
   * In particular, the checker verifies if the Send and Receive communication primitives for each pair of core is
   * always scheduled with the exact same order on both sides. For example:<br>
   * <ul>
   * <li>Correct schedule:
   * <ul>
   * <li>Core0 schedule : SendA, ..., SendB</li>
   * <li>Core1 schedule : RecvA, ..., RecvB</li>
   * </ul>
   * </li>
   * <li>Invalid schedule:
   * <ul>
   * <li>Core0 schedule : SendA, ..., SendB</li>
   * <li>Core1 schedule : RecvB, ..., RecvA</li>
   * </ul>
   * </li>
   * </ul>
   *
   * @param dag
   *          The {@link DirectedAcyclicGraph} whose schedule is verified.
   *
   * @throws PreesmException
   *           if the schedule is incorrect.
   */
  public static void checkCommunicationOrder(final DirectedAcyclicGraph dag) {
    // Check communication ordering
    final ScheduledDAGIterator iterDAGVertices = new ScheduledDAGIterator(dag); // Iterator on DAG vertices

    // Create an array list of the Send/Receive DAGVertices, in scheduling order.
    final List<DAGVertex> sendVerticesMap = new ArrayList<>();
    final List<DAGVertex> recvVerticesMap = new ArrayList<>();

    // Store all used processing elements
    final Set<ComponentInstance> sendComponents = new LinkedHashSet<>();
    final Set<ComponentInstance> recvComponents = new LinkedHashSet<>();

    while (iterDAGVertices.hasNext()) {
      final DAGVertex currentVertex = iterDAGVertices.next();

      final String vertexType = currentVertex.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType)
          .toString();
      final boolean isSend = vertexType.equals("send");
      final boolean isReceive = vertexType.equals("receive");

      // get component
      final ComponentInstance comp = (ComponentInstance) currentVertex.getPropertyBean().getValue("Operator");

      // Get scheduling order
      if (isSend) {
        sendVerticesMap.add(currentVertex);
        sendComponents.add(comp);
      }
      if (isReceive) {
        recvVerticesMap.add(currentVertex);
        recvComponents.add(comp);
      }
    }

    // Check the order is identical on send and receive sides
    for (final ComponentInstance sendComponent : sendComponents) {
      for (final ComponentInstance recvComponent : recvComponents) {
        // For each pair of sender/receiver

        // Skip self-send/receive
        if (sendComponent.equals(recvComponent)) {
          continue;
        }

        // Collect sender and receivers DAGVertices for this pair (in scheduling order)
        final List<DAGVertex> senders = new ArrayList<>(sendVerticesMap);
        senders.removeIf(vertex -> !((ComponentInstance) vertex.getPropertyBean()
            .getValue(ImplementationPropertyNames.Vertex_Operator)).equals(sendComponent));

        final List<DAGVertex> receivers = new ArrayList<>(recvVerticesMap);
        receivers.removeIf(vertex -> !((ComponentInstance) vertex.getPropertyBean()
            .getValue(ImplementationPropertyNames.Vertex_Operator)).equals(recvComponent));

        // Get corresponding edges (in scheduling order)
        final List<DAGEdge> senderDagEdges = new ArrayList<>(senders.size());
        senders.forEach(sender -> senderDagEdges.add(
            (DAGEdge) sender.getPropertyBean().getValue(ImplementationPropertyNames.SendReceive_correspondingDagEdge)));
        final List<DAGEdge> receiverDagEdges = new ArrayList<>(receivers.size());
        receivers.forEach(receiver -> receiverDagEdges.add((DAGEdge) receiver.getPropertyBean()
            .getValue(ImplementationPropertyNames.SendReceive_correspondingDagEdge)));

        // Keep only the DAGEdges in common (they are the one corresponding to communications between the selected
        // sender and receiver
        receiverDagEdges.retainAll(senderDagEdges);
        senderDagEdges.retainAll(receiverDagEdges);

        // Throws an exception if the schedule is incorrect.
        if (!senderDagEdges.equals(receiverDagEdges)) {
          throw new PreesmRuntimeException(
              "Order of communication primitives (Send/Receive) is not preserved between components " + sendComponent
                  + " and " + recvComponent + ". Contact Preesm developers for more information.");
        }
      }
    }
  }

  /**
   * The purpose of this method is to check if pairs of Send/Receive vertex generated on an intermediate processing
   * element when a multi-step route is taken are correctly ordered.
   * 
   * In particular, this method checks that for any given multi-step communication passing through a processing element,
   * the Receive operation always preceeds the corresponding Send operation.
   * 
   * This issue may arise if an improper re-ordering of send/receive operation is performed on such a multi-step route.
   * 
   * @param dag
   *          The {@link DirectedAcyclicGraph} whose scheduling is verified.
   * 
   * @throws PreesmException
   *           if the schedule is incorrect.
   */
  public static void checkMultiStepSendReceiveValidity(DirectedAcyclicGraph dag) {
    // Iterator on DAG vertices in scheduling order
    final ScheduledDAGIterator iterDAGVertices = new ScheduledDAGIterator(dag);

    // Create an array list of the Send/Receive DAGVertices, in scheduling order.
    final List<DAGVertex> sendReceiveVertices = new ArrayList<>();

    // Store all used processing elements
    final Set<ComponentInstance> components = new LinkedHashSet<>();

    while (iterDAGVertices.hasNext()) {
      final DAGVertex currentVertex = iterDAGVertices.next();

      final String vertexType = currentVertex.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType)
          .toString();
      final boolean isSend = vertexType.equals("send");
      final boolean isReceive = vertexType.equals("receive");

      // get component
      final ComponentInstance comp = (ComponentInstance) currentVertex.getPropertyBean().getValue("Operator");

      // Get scheduling order
      if (isSend || isReceive) {
        sendReceiveVertices.add(currentVertex);
        components.add(comp);
      }
    }

    // Check that Send-Receive corresponding to multi-step intermediate step are correctly ordered.
    for (final ComponentInstance component : components) {
      // For each component

      // Collect sender and receivers DAGVertices for this component (in scheduling order)
      final List<DAGVertex> sendersReceivers = new ArrayList<>(sendReceiveVertices);
      sendersReceivers.removeIf(vertex -> !((ComponentInstance) vertex.getPropertyBean()
          .getValue(ImplementationPropertyNames.Vertex_Operator)).equals(component));

      // Keep only senderReceivers that corresponds to intermediate steps
      sendersReceivers.removeIf(vertex -> ((vertex instanceof ReceiveVertex) ? ((TransferVertex) vertex).getTarget()
          : ((TransferVertex) vertex).getSource()).getPropertyBean().getValue("Operator").equals(component));

      // For each receive, check that the corresponding send if in the rest of the list
      // Create the list of corresponding dagEdge
      List<DAGEdge> senderReceiverEdges = new ArrayList<DAGEdge>(sendersReceivers.size());
      sendersReceivers.forEach(vertex -> senderReceiverEdges
          .add(vertex.getPropertyBean().getValue(ImplementationPropertyNames.SendReceive_correspondingDagEdge)));
      int i;
      for (i = 0; i < sendersReceivers.size() - 1; i++) {
        DAGVertex vertex = sendersReceivers.get(i);
        if (vertex instanceof ReceiveVertex) {
          if (i == sendersReceivers.size()) {
            // This should not happen, if a receive is the last of the list, its corresponding send cannot be after it
            // in the list.
            throw new PreesmRuntimeException("On operator: " + component
                + ", a Receive vertex of an intermediate step of a multi-step communication"
                + " is scheduled after its corresponding Send." + " Contact Preesm developers for more information.");
          }

          DAGEdge edge = senderReceiverEdges.get(i);
          if (!senderReceiverEdges.subList(i + 1, sendersReceivers.size()).contains(edge)) {
            throw new PreesmRuntimeException("On operator: " + component
                + ", a Receive vertex of an intermediate step of a multi-step communication"
                + " is scheduled after its corresponding Send." + " Contact Preesm developers for more information.");
          }
        }
      }
    }
  }
}
