package org.preesm.codegen.model.generator2.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.CodegenFactory;
import org.preesm.codegen.model.SubBuffer;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PassiveActor;
import org.preesm.model.pisdf.PassiveElt;
import org.preesm.model.pisdf.PassivePort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.PortKind;
import org.preesm.model.pisdf.util.PiMMSwitch;
import org.preesm.model.scenario.Scenario;

public class PassiveBufferGeneratorSwitch extends PiMMSwitch<Map<PassiveElt, Buffer>> {

  List<AbstractActor>        alreadyVisited = new ArrayList<>();
  Scenario                   scenario;
  Map<DataInterface, Buffer> i2b;

  public PassiveBufferGeneratorSwitch(Scenario scenario, Map<DataInterface, Buffer> i2b) {
    this.scenario = scenario;
    this.i2b = i2b;
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

    final Map<PassiveElt, Buffer> result = caseAbstractActor(pa);

    final long typeSize = scenario.getSimulationInfo().getDataTypeSizeInBit(pa.getType());

    final SubBuffer actorBuffer = CodegenFactory.eINSTANCE.createSubBuffer();
    actorBuffer.setName("mem_" + pa.getName());
    actorBuffer.setNbToken(pa.getRealBufferSize());
    actorBuffer.setTokenTypeSizeInBit(typeSize);
    actorBuffer.setType(pa.getType());

    final Set<PassivePort> connectedToInterface = new HashSet<>();

    for (final PassivePort pp : pa.getAllPassivePorts()) {
      final AbstractActor opposite = pp.getOppositePort().getContainingActor();
      if (opposite instanceof final DataInterface dataInterface) {
        if (!this.i2b.containsKey(dataInterface)) {
          PreesmLogger.getLogger()
              .warning(() -> "The following interface " + dataInterface.getName() + " should be in i2b");
          continue;
        }
        final Buffer interfaceBuffer = this.i2b.get(dataInterface);
        actorBuffer.setContainer(interfaceBuffer);
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
      final SubBuffer portBuffer = CodegenFactory.eINSTANCE.createSubBuffer();
      portBuffer.setContainer(actorBuffer);
      String name = pa.getName() + "_" + pp.getName();
      name += pp.getKind() == PortKind.DATA_INPUT ? "_write" : "_read";
      portBuffer.setName(name);
      portBuffer.setNbToken(pp.getSubBufferSize());
      portBuffer.setOffsetInBit(pp.getOffset());
      portBuffer.setTokenTypeSizeInBit(typeSize);
      portBuffer.setType(pa.getType());
      result.put(pp, portBuffer);

    }
    return result;
  }
}
