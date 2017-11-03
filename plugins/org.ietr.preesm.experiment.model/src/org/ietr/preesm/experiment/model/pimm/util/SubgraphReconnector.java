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
   * @param a
   *          the a
   * @param pg
   *          the pg
   */
  public static void reconnectPiGraph(final Actor a, final PiGraph pg) {
    reconnectDataInputPorts(a, pg);
    reconnectDataOutputPorts(a, pg);
    reconnectConfigInputPorts(a, pg);
    reconnectConfigOutputPorts(a, pg);
  }

  private static void reconnectConfigOutputPorts(final Actor a, final PiGraph pg) {
    boolean found;
    for (final ConfigOutputPort cop1 : a.getConfigOutputPorts()) {
      found = false;
      for (final ConfigOutputPort cop2 : pg.getConfigOutputPorts()) {
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
        throw new RuntimeException(
            "PiGraph " + pg.getName() + " does not have a corresponding ConfigOutputPort for " + cop1.getName() + " of Actor " + a.getName());
      }
    }
  }

  private static void reconnectConfigInputPorts(final Actor a, final PiGraph pg) {
    boolean found;
    for (final ConfigInputPort cip1 : a.getConfigInputPorts()) {
      found = false;
      for (final ConfigInputPort cip2 : pg.getConfigInputPorts()) {
        if (cip1.getName().equals(cip2.getName())) {
          final Dependency dep = cip1.getIncomingDependency();
          cip2.setIncomingDependency(dep);
          dep.setGetter(cip2);
          found = true;
          break;
        }
      }
      if (!found) {
        throw new RuntimeException(
            "PiGraph" + pg.getName() + " does not have a corresponding ConfigInputPort for " + cip1.getName() + " of Actor " + a.getName());
      }
    }
  }

  private static void reconnectDataOutputPorts(final Actor a, final PiGraph pg) {
    boolean found;
    for (final DataOutputPort dop1 : a.getDataOutputPorts()) {
      found = false;
      for (final DataOutputPort dop2 : pg.getDataOutputPorts()) {
        if (dop1.getName().equals(dop2.getName())) {
          final Fifo fifo = dop1.getOutgoingFifo();
          dop2.setOutgoingFifo(fifo);
          fifo.setSourcePort(dop2);

          dop2.setExpression(dop1.getExpression());
          dop2.setAnnotation(dop1.getAnnotation());

          found = true;
          break;
        }
      }
      if (!found) {
        throw new RuntimeException(
            "PiGraph" + pg.getName() + "does not have a corresponding DataOutputPort for " + dop1.getName() + " of Actor " + a.getName());
      }
    }
  }

  private static void reconnectDataInputPorts(final Actor a, final PiGraph pg) {
    boolean found;
    for (final DataInputPort dip1 : a.getDataInputPorts()) {
      found = false;
      for (final DataInputPort dip2 : pg.getDataInputPorts()) {
        if (dip1.getName().equals(dip2.getName())) {
          final Fifo fifo = dip1.getIncomingFifo();
          dip2.setIncomingFifo(fifo);
          fifo.setTargetPort(dip2);

          dip2.setExpression(dip1.getExpression());
          dip2.setAnnotation(dip1.getAnnotation());

          found = true;
          break;
        }
      }
      if (!found) {
        throw new RuntimeException("PiGraph" + pg.getName() + "does not have a corresponding DataInputPort for " + dip1.getName() + " of Actor " + a.getName());
      }
    }
  }

}
