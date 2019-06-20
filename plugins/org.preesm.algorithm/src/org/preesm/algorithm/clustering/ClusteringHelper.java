package org.preesm.algorithm.clustering;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.preesm.model.algorithm.schedule.Schedule;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.scenario.Scenario;

/**
 *
 * @author anmorvan
 *
 */
public class ClusteringHelper {

  /**
   *
   */
  public static final List<DataInputPort> getExternalyConnectedPorts(final Schedule cluster) {
    List<DataInputPort> res = new ArrayList<>();
    final List<AbstractActor> actors = cluster.getActors();
    for (final AbstractActor actor : actors) {
      final EList<DataInputPort> dataInputPorts = actor.getDataInputPorts();
      for (final DataInputPort port : dataInputPorts) {
        final Fifo fifo = port.getFifo();
        // filter ports connected within the cluster
        final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
        if (ECollections.indexOf(actors, sourceActor, 0) != -1) {
          // source actor is within the cluster
          // skip
        } else {
          res.add(port);
        }
      }
    }

    return res;
  }

  /**
   *
   */
  public static final long getTotalMachin(final Schedule cluster, final Scenario scenario) {
    final List<DataInputPort> ports = getExternalyConnectedPorts(cluster);
    return ports.stream().mapToLong(p -> p.getPortRateExpression().evaluate()
        * scenario.getSimulationInfo().getDataTypeSizeOrDefault(p.getFifo().getType())).sum();
  }

}
