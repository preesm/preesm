package org.preesm.algorithm.synthesis.schedule.algos;

import java.util.List;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.SimplePassiveActor;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;

public class PassiveAwareSubgraphBuilder extends PiSDFSubgraphBuilder {

  public PassiveAwareSubgraphBuilder(PiGraph parentGraph, List<AbstractActor> subGraphActors, String subGraphName) {
    super(parentGraph, subGraphActors, subGraphName);
  }

  @Override
  public Boolean caseFifo(Fifo fifo) {

    // Is the fifo connecting two actors of the desired subgraph?
    boolean betweenActorsOfSubGraph = true;

    if (fifo.getSource() instanceof final SimplePassiveActor spa) {
      betweenActorsOfSubGraph &= this.subGraphActors
          .contains(spa.getInputPort().getOppositePort().getContainingActor());
    } else {
      betweenActorsOfSubGraph &= this.subGraphActors.contains(fifo.getSource());
    }

    if (fifo.getTarget() instanceof final SimplePassiveActor spa) {
      betweenActorsOfSubGraph &= this.subGraphActors
          .contains(spa.getOutputPort().getOppositePort().getContainingActor());
    } else {
      betweenActorsOfSubGraph &= this.subGraphActors.contains(fifo.getTarget());
    }

    // If fifo should be contained in the subgraph, add it.
    if (betweenActorsOfSubGraph && !this.visitedFifo.contains(fifo)) {
      final Fifo newFifo;
      if (fifo.getTarget() instanceof SimplePassiveActor || fifo.getSource() instanceof SimplePassiveActor) {
        DataOutputPort sourcePort;
        DataInputPort targetPort;
        if (fifo.getSource() instanceof final SimplePassiveActor spa) {
          sourcePort = spa.getInputPort().getOppositePort();
          this.parentGraph.removeFifo(spa.getInputPort().getFifo());
          this.parentGraph.removeActor(spa);
        } else {
          sourcePort = fifo.getSourcePort();
        }

        if (fifo.getTarget() instanceof final SimplePassiveActor spa) {
          targetPort = spa.getOutputPort().getOppositePort();
          this.parentGraph.removeFifo(spa.getOutputPort().getFifo());
          this.parentGraph.removeActor(spa);
        } else {
          targetPort = fifo.getTargetPort();
        }

        newFifo = PiMMUserFactory.instance.createFifo(sourcePort, targetPort, fifo.getType());
        this.parentGraph.removeFifo(fifo);

      } else {
        newFifo = fifo;
      }

      this.visitedFifo.add(newFifo);
      this.subGraph.addFifo(newFifo);

      // If there is a delay, add it into the subgraph
      final Delay delay = newFifo.getDelay();
      if (delay != null) {
        this.subGraph.addDelay(delay);
        if (delay.getLevel().equals(PersistenceLevel.NONE) && delay.hasGetterActor()) {

          for (final Port delayPort : delay.getDelayActor().getAllPorts()) {
            doSwitch(delayPort);
          }
        }
      }
    }
    return !betweenActorsOfSubGraph;
  }
}
