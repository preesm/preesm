package org.preesm.algorithm.synthesis.schedule.algos;

import java.util.List;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.PassiveActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;

public class PassiveFreeSubgraphBuilder extends PiSDFSubgraphBuilder {

  public PassiveFreeSubgraphBuilder(PiGraph parentGraph, List<AbstractActor> subGraphActors, String subGraphName) {
    super(parentGraph, subGraphActors, subGraphName);
  }

  @Override
  public Boolean caseDataOutputPort(DataOutputPort dop) {
    if (dop.getOppositePort().getContainingActor() instanceof final PassiveActor pa) {
      for (final DataOutputPort passiveDop : pa.getDataOutputPorts()) {
        doSwitch(passiveDop);
      }
      return true;
    }
    return super.caseDataOutputPort(dop);
  }

  @Override
  public Boolean caseDataInputPort(DataInputPort dip) {
    if (dip.getOppositePort().getContainingActor() instanceof final PassiveActor pa) {
      for (final DataInputPort passiveDip : pa.getDataInputPorts()) {
        doSwitch(passiveDip);
      }
      return true;
    }
    return super.caseDataInputPort(dip);
  }

  /**
   * Performs subgraph actors extraction from parent graph.
   *
   * @return The resulting subgraph.
   */
  @Override
  public PiGraph build() {
    // Add subgraph to parent graph
    parentGraph.addActor(subGraph);

    // Add actors to the new subgraph
    for (final AbstractActor actor : this.subGraphActors) {
      doSwitch(actor);
    }

    // This line is here because of a bug occurring when creating a cluster in a cluster.
    // In details, it seems that the actorIndex (existing just to keep track of the number of actors in a graph) of
    // cluster is not updated when adding an existing actor (of parentGraph) in cluster. So we update by hand.
    this.subGraph.setActorIndex(this.subGraph.getActors().size());
    this.subGraph.setFifoWithoutDelayIndex(this.subGraph.getFifosWithoutDelay().size());
    this.subGraph.setFifoWithDelayIndex(this.subGraph.getFifosWithDelay().size());

    return this.subGraph;
  }

}
