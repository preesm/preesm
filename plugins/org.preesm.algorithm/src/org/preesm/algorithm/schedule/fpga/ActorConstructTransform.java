package org.preesm.algorithm.schedule.fpga;

import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 * Helper class to generate specific actors for FPGA
 *
 * @author mdardail
 */

public abstract class ActorConstructTransform {

  protected ActorConstructTransform() {
    // Forbids instantiation
  }

  protected static DataInputPort createDataInputPort(final Actor actor, long rate) {
    final DataInputPort inputPort = PiMMUserFactory.instance.createDataInputPort("input");
    inputPort.setExpression(rate);
    actor.getDataInputPorts().add(inputPort);
    return inputPort;
  }

  protected static DataOutputPort createDataOutputPort(final Actor actor, long rate) {
    final DataOutputPort outputPort = PiMMUserFactory.instance.createDataOutputPort("output");
    outputPort.setExpression(rate);
    actor.getDataOutputPorts().add(outputPort);
    return outputPort;
  }

  protected static void connectParameter(final Actor actor, final Parameter param, final String name) {
    final ConfigInputPort width = PiMMUserFactory.instance.createConfigInputPort();
    width.setName(name);
    actor.getConfigInputPorts().add(width);
    final Dependency depInputWidth = PiMMUserFactory.instance.createDependency(param, width);
    actor.getContainingPiGraph().addDependency(depInputWidth);
    width.setIncomingDependency(depInputWidth);
    param.getOutgoingDependencies().add(depInputWidth);
  }

  protected static void createRefinement(final Actor actor, String actorName, String actorFilePath) {
    final FunctionPrototype funcProto = PiMMUserFactory.instance.createFunctionPrototype();
    funcProto.setIsCPPdefinition(true);
    funcProto.setName(actorName);

    createArgument(funcProto, Direction.IN);
    createArgument(funcProto, Direction.OUT);

    final CHeaderRefinement newRefinement = PiMMUserFactory.instance.createCHeaderRefinement();
    newRefinement.setLoopPrototype(funcProto);
    newRefinement.setFilePath(actor.getName() + actorFilePath);
    newRefinement.setRefinementContainer(actor);
    newRefinement.setGenerated(true);
    actor.setRefinement(newRefinement);
  }

  private static void createArgument(FunctionPrototype func, Direction dir) {
    final FunctionArgument arg = PiMMUserFactory.instance.createFunctionArgument();
    arg.setName(dir == Direction.IN ? "input" : "output");
    arg.setDirection(dir);
    arg.setPosition(dir == Direction.IN ? 0 : 1);
    arg.setType(dir == Direction.IN ? "hls::stream<in_t>" : "hls::stream<out_t>");
    arg.setIsCPPdefinition(true);
    func.getArguments().add(arg);
  }
}
