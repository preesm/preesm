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

}
