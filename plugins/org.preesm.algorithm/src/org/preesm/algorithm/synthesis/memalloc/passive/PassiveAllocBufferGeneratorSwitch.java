package org.preesm.algorithm.synthesis.memalloc.passive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.common.util.EMap;
import org.preesm.algorithm.memalloc.model.LogicalBuffer;
import org.preesm.algorithm.memalloc.model.MemoryAllocationFactory;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PassiveActor;
import org.preesm.model.pisdf.PassiveElt;
import org.preesm.model.pisdf.PassivePort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.PiMMSwitch;
import org.preesm.model.scenario.Scenario;

/**
 * @author rcazoulat
 */
public class PassiveAllocBufferGeneratorSwitch extends PiMMSwitch<Map<PassiveElt, LogicalBuffer>> {

  List<AbstractActor>                alreadyVisited = new ArrayList<>();
  Scenario                           scenario;
  EMap<DataInterface, LogicalBuffer> interfaceBufferMap;

  public PassiveAllocBufferGeneratorSwitch(Scenario scenario, EMap<DataInterface, LogicalBuffer> interfaceBufferMap) {
    this.scenario = scenario;
    this.interfaceBufferMap = interfaceBufferMap;
  }

  @Override
  public Map<PassiveElt, LogicalBuffer> caseAbstractActor(AbstractActor actor) {
    if (this.alreadyVisited.contains(actor)) {
      return new HashMap<>();
    }

    this.alreadyVisited.add(actor);
    final Map<PassiveElt, LogicalBuffer> results = new HashMap<>();
    for (final DataPort dp : actor.getAllDataPorts()) {
      results.putAll(doSwitch(dp));
    }
    return results;
  }

  @Override
  public Map<PassiveElt, LogicalBuffer> casePiGraph(PiGraph graph) {
    final Map<PassiveElt, LogicalBuffer> results = new HashMap<>();
    graph.getActors().forEach(a -> results.putAll(doSwitch(a)));
    return results;
  }

  @Override
  public Map<PassiveElt, LogicalBuffer> caseDataPort(DataPort dp) {
    return doSwitch(dp.getOppositePort().getContainingActor());
  }

  @Override
  public Map<PassiveElt, LogicalBuffer> caseInterfaceActor(InterfaceActor ia) {
    return new HashMap<>();
  }

  @Override
  public Map<PassiveElt, LogicalBuffer> casePassiveActor(PassiveActor pa) {

    final Map<PassiveElt, LogicalBuffer> result = caseAbstractActor(pa);

    final long typeSize = scenario.getSimulationInfo().getDataTypeSizeInBit(pa.getType());

    final LogicalBuffer actorBuffer = MemoryAllocationFactory.eINSTANCE.createLogicalBuffer();
    actorBuffer.setSizeInBit(pa.getRealBufferSize() * typeSize);

    final Set<PassivePort> connectedToInterface = new HashSet<>();

    for (final PassivePort pp : pa.getAllPassivePorts()) {
      final AbstractActor opposite = pp.getOppositePort().getContainingActor();
      if (opposite instanceof final DataInterface dataInterface) {
        final LogicalBuffer interfaceBuffer = this.interfaceBufferMap.get(dataInterface);
        actorBuffer.setContainingBuffer(interfaceBuffer);
        actorBuffer.setOffsetInBit(0);
        connectedToInterface.add(pp);
      }
    }

    if (connectedToInterface.size() > 1) {
      throw new PreesmRuntimeException("Passive Actor " + pa.getName()
          + " is connected to more than 1 Data Interface. It is not supported in the current version of PREESM.");
    }

    result.put(pa, actorBuffer);

    for (final PassivePort pp : pa.getAllPassivePorts()) {
      if (connectedToInterface.contains(pp)) {
        continue;
      }
      final LogicalBuffer portBuffer = MemoryAllocationFactory.eINSTANCE.createLogicalBuffer();
      portBuffer.setContainingBuffer(actorBuffer);
      portBuffer.setSizeInBit(pp.getSubBufferSize() * typeSize);
      portBuffer.setOffsetInBit(pp.getOffset());
      result.put(pp, portBuffer);
    }

    return result;
  }
}
