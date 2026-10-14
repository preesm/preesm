package org.preesm.codegen.model.generator2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.CodegenFactory;
import org.preesm.codegen.model.SubBuffer;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PassiveActor;
import org.preesm.model.pisdf.PassiveElt;
import org.preesm.model.pisdf.PassivePort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.PiMMSwitch;
import org.preesm.model.scenario.Scenario;

public class PassiveBufferGeneratorSwitch extends PiMMSwitch<Map<PassiveElt, Buffer>> {

  List<AbstractActor> alreadyVisited = new ArrayList<>();
  Scenario            scenario;

  public PassiveBufferGeneratorSwitch(Scenario scenario) {
    this.scenario = scenario;
  }

  @Override
  public Map<PassiveElt, Buffer> caseAbstractActor(AbstractActor actor) {
    if (this.alreadyVisited.contains(actor)) {
      return new HashMap<>();
    }

    this.alreadyVisited.add(actor);
    final Map<PassiveElt, Buffer> results = new HashMap<>();
    for (final DataPort dp : actor.getAllDataPorts()) {
      results.putAll(doSwitch(dp));
    }
    return results;
  }

  @Override
  public Map<PassiveElt, Buffer> casePiGraph(PiGraph graph) {
    final Map<PassiveElt, Buffer> results = new HashMap<>();
    for (final AbstractActor a : graph.getActors()) {
      results.putAll(doSwitch(a));
    }
    return results;
  }

  @Override
  public Map<PassiveElt, Buffer> caseDataPort(DataPort dp) {
    return doSwitch(dp.getOppositePort().getContainingActor());
  }

  @Override
  public Map<PassiveElt, Buffer> caseInterfaceActor(InterfaceActor ia) {
    return new HashMap<>();
  }

  @Override
  public Map<PassiveElt, Buffer> casePassiveActor(PassiveActor pa) {
    if (this.alreadyVisited.contains(pa)) {
      return new HashMap<>();
    }

    final Map<PassiveElt, Buffer> result = new HashMap<>();
    final long typeSize = scenario.getSimulationInfo().getDataTypeSizeInBit(pa.getType());

    final SubBuffer actorBuffer = CodegenFactory.eINSTANCE.createSubBuffer();
    actorBuffer.setName("mem_" + pa.getName());
    actorBuffer.setNbToken(pa.getRealBufferSize());
    actorBuffer.setTokenTypeSizeInBit(typeSize);
    actorBuffer.setType(pa.getType());

    result.put(pa, actorBuffer);

    for (final PassivePort pp : pa.getAllPassivePorts()) {
      final SubBuffer portBuffer = CodegenFactory.eINSTANCE.createSubBuffer();
      portBuffer.setContainer(actorBuffer);
      portBuffer.setName(pa.getName() + "__" + pp.getOppositePort().getContainingActor().getName());
      portBuffer.setNbToken(pp.getSubBufferSize());
      portBuffer.setOffsetInBit(pp.getOffset());
      portBuffer.setTokenTypeSizeInBit(typeSize);
      portBuffer.setType(pa.getType());
      result.put(pp, portBuffer);

    }

    result.putAll(caseAbstractActor(pa));

    return result;
  }
}
