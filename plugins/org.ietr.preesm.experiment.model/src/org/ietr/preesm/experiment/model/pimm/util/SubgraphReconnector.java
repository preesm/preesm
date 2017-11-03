package org.ietr.preesm.experiment.model.pimm.util;

import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

/**
 * This class provides a single method to connect a hierarchical actor to its underlying subgraph. Synthetically, it takes edges from the super graph (the graph
 * containing the hierarchical actor), and connect them to subgraph (which is also an actor).
 *
 * @author anmorvan
 *
 */
public class SubgraphReconnector {

  private SubgraphReconnector() {
  }

  /**
   * Reconnect pi graph.
   *
   * @param hierarchicalActor
   *          the actor
   * @param subGraph
   *          the subgraph linked to the hierarchical actor
   */
  public static void reconnectPiGraph(final Actor hierarchicalActor, final PiGraph subGraph) {
    SubgraphReconnector.reconnectDataInputPorts(hierarchicalActor, subGraph);
    SubgraphReconnector.reconnectDataOutputPorts(hierarchicalActor, subGraph);
    SubgraphReconnector.reconnectConfigInputPorts(hierarchicalActor, subGraph);
    SubgraphReconnector.reconnectConfigOutputPorts(hierarchicalActor, subGraph);
  }

  private static void reconnectConfigOutputPorts(final Actor hierarchicalActor, final PiGraph subGraph) {
    boolean found;
    for (final ConfigOutputPort cop1 : hierarchicalActor.getConfigOutputPorts()) {
      found = false;
      for (final ConfigOutputPort cop2 : subGraph.getConfigOutputPorts()) {
        if (cop1.getName().equals(cop2.getName())) {
          for (final Dependency dep : cop1.getOutgoingDependencies()) {
            cop2.getOutgoingDependencies().add(dep);
            dep.setSetter(cop2);
          }
          found = true;
          break;
        }
      }
      if (!found) {
        throw new RuntimeException("PiGraph " + subGraph.getName() + " does not have a corresponding ConfigOutputPort for " + cop1.getName() + " of Actor "
            + hierarchicalActor.getName());
      }
    }
  }

  private static void reconnectConfigInputPorts(final Actor hierarchicalActor, final PiGraph subGraph) {
    boolean found;
    for (final ConfigInputPort cip1 : hierarchicalActor.getConfigInputPorts()) {
      found = false;
      for (final ConfigInputPort cip2 : subGraph.getConfigInputPorts()) {
        if (cip1.getName().equals(cip2.getName())) {
          found = true;
          final Dependency dep = cip1.getIncomingDependency();
          if (dep != null) {
            cip2.setIncomingDependency(dep);
            dep.setGetter(cip2);
          }
          break;
        }
      }
      if (!found) {
        throw new RuntimeException("PiGraph" + subGraph.getName() + " does not have a corresponding ConfigInputPort for " + cip1.getName() + " of Actor "
            + hierarchicalActor.getName());
      }
    }
  }

  private static void reconnectDataOutputPorts(final Actor hierarchicalActor, final PiGraph subGraph) {
    boolean found;
    for (final DataOutputPort dop1 : hierarchicalActor.getDataOutputPorts()) {
      found = false;
      for (final DataOutputPort dop2 : subGraph.getDataOutputPorts()) {
        if (dop1.getName().equals(dop2.getName())) {
          final Fifo fifo = dop1.getOutgoingFifo();
          if (fifo != null) {
            dop2.setOutgoingFifo(fifo);
            fifo.setSourcePort(dop2);

            dop2.setExpression(dop1.getExpression());
            dop2.setAnnotation(dop1.getAnnotation());
          }
          found = true;
          break;
        }
      }
      if (!found) {
        throw new RuntimeException(
            "PiGraph" + subGraph.getName() + "does not have a corresponding DataOutputPort for " + dop1.getName() + " of Actor " + hierarchicalActor.getName());
      }
    }
  }

  private static void reconnectDataInputPorts(final Actor hierarchicalActor, final PiGraph subGraph) {
    boolean found;
    for (final DataInputPort dip1 : hierarchicalActor.getDataInputPorts()) {
      found = false;
      for (final DataInputPort dip2 : subGraph.getDataInputPorts()) {
        if (dip1.getName().equals(dip2.getName())) {
          final Fifo fifo = dip1.getIncomingFifo();
          if (fifo != null) {
            dip2.setIncomingFifo(fifo);
            fifo.setTargetPort(dip2);

            dip2.setExpression(dip1.getExpression());
            dip2.setAnnotation(dip1.getAnnotation());
          }
          found = true;
          break;
        }
      }
      if (!found) {
        throw new RuntimeException(
            "PiGraph" + subGraph.getName() + "does not have a corresponding DataInputPort for " + dip1.getName() + " of Actor " + hierarchicalActor.getName());
      }
    }
  }

}
