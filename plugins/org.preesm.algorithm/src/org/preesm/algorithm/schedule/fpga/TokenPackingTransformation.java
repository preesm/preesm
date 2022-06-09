package org.preesm.algorithm.schedule.fpga;

import java.util.List;
import org.preesm.algorithm.schedule.fpga.FpgaAnalysisMainTask.AnalysisResultFPGA;
import org.preesm.algorithm.schedule.fpga.TokenPackingAnalysis.PackedFifoConfig;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.TimingType;

public final class TokenPackingTransformation {

  public static void transform(AnalysisResultFPGA res, Scenario scenario, List<PackedFifoConfig> workList) {
    for (PackedFifoConfig config : workList) {
      addTokenPacking(res, scenario, config.fifo, config.originalWidth, config.updatedWidth);
    }
  }

  public static void addTokenPacking(AnalysisResultFPGA res, Scenario scenario, Fifo fifo, long unpackedSize,
      long packedSize) {
    long compressionRatio = packedSize / unpackedSize;

    // Create packer and unpacker
    final Actor packer = PiMMUserFactory.instance.createActor(fifo.getId() + "_packing");
    createRefinement(packer, true);
    res.flatGraph.addActor(packer);
    final Actor unpacker = PiMMUserFactory.instance.createActor(fifo.getId() + "_unpacking");
    createRefinement(unpacker, false);
    res.flatGraph.addActor(unpacker);

    // Map packer and unpacker to FPGA targeted by packed actor
    ComponentInstance target = scenario.getPossibleMappings((AbstractActor) fifo.getTarget()).get(0);
    scenario.getConstraints().addConstraint(target, packer);
    scenario.getConstraints().addConstraint(target, unpacker);

    // Add packer and unpacker timing infos
    scenario.getTimings().setTiming(packer, target.getComponent(), TimingType.EXECUTION_TIME,
        Long.toString(compressionRatio));
    scenario.getTimings().setTiming(packer, target.getComponent(), TimingType.INITIATION_INTERVAL,
        Long.toString(compressionRatio));
    scenario.getTimings().setTiming(unpacker, target.getComponent(), TimingType.EXECUTION_TIME,
        Long.toString(compressionRatio));
    scenario.getTimings().setTiming(unpacker, target.getComponent(), TimingType.INITIATION_INTERVAL,
        Long.toString(compressionRatio));

    // Connect size parameters
    final Parameter unpacked = PiMMUserFactory.instance.createParameter(fifo.getId() + "_unpacked", unpackedSize);
    unpacked.setContainingGraph(res.flatGraph);
    final Parameter packed = PiMMUserFactory.instance.createParameter(fifo.getId() + "_packed", packedSize);
    packed.setContainingGraph(res.flatGraph);
    connectParameter(packer, unpacked, Direction.IN);
    connectParameter(packer, packed, Direction.OUT);
    connectParameter(unpacker, packed, Direction.IN);
    connectParameter(unpacker, unpacked, Direction.OUT);

    // Set repetition value
    final long rep = fifo.getSourcePort().getPortRateExpression().evaluate()
        * res.flatBrv.get(fifo.getSourcePort().getContainingActor()) / compressionRatio;
    res.flatBrv.put(packer, rep);
    res.flatBrv.put(unpacker, rep);

    // Create data ports
    final DataInputPort packerInput = createDataInputPort(packer, compressionRatio);
    final DataOutputPort packerOutput = createDataOutputPort(packer, 1);
    final DataInputPort unpackerInput = createDataInputPort(unpacker, 1);
    final DataOutputPort unpackerOutput = createDataOutputPort(unpacker, compressionRatio);

    // Create and connect FIFOs
    final Fifo newFifo = createFifo(fifo.getType(), unpackerOutput, fifo.getTargetPort());
    final Fifo newPackedFifo = createFifo("ap_uint<" + packedSize + ">", packerOutput, unpackerInput);
    fifo.setTargetPort(packerInput);
    scenario.getSimulationInfo().getDataTypes().put("ap_uint<" + packedSize + ">", packedSize);

    // Set FIFO sizes
    res.flatFifoSizes.put(newPackedFifo, res.flatFifoSizes.get(fifo));
    res.flatFifoSizes.put(fifo, 2 * unpackedSize);
    res.flatFifoSizes.put(newFifo, 2 * unpackedSize);
  }

  private static Fifo createFifo(String type, DataOutputPort source, DataInputPort sink) {
    final Fifo newFifo = PiMMUserFactory.instance.createFifo();
    newFifo.setContainingGraph(source.getContainingActor().getContainingGraph());
    newFifo.setType(type);
    newFifo.setSourcePort(source);
    newFifo.setTargetPort(sink);
    return newFifo;
  }

  private static DataInputPort createDataInputPort(final Actor actor, long rate) {
    final DataInputPort inputPort = PiMMUserFactory.instance.createDataInputPort();
    inputPort.setName("input");
    inputPort.setExpression(rate);
    actor.getDataInputPorts().add(inputPort);
    return inputPort;
  }

  private static DataOutputPort createDataOutputPort(final Actor actor, long rate) {
    final DataOutputPort outputPort = PiMMUserFactory.instance.createDataOutputPort();
    outputPort.setName("output");
    outputPort.setExpression(rate);
    actor.getDataOutputPorts().add(outputPort);
    return outputPort;
  }

  private static void connectParameter(final Actor actor, final Parameter param, Direction dir) {
    final ConfigInputPort width = PiMMUserFactory.instance.createConfigInputPort();
    width.setName(dir == Direction.IN ? "IN_W" : "OUT_W");
    actor.getConfigInputPorts().add(width);
    final Dependency depInputWidth = PiMMUserFactory.instance.createDependency();
    depInputWidth.setContainingGraph(actor.getContainingGraph());
    depInputWidth.setGetter(width);
    width.setIncomingDependency(depInputWidth);
    depInputWidth.setSetter(param);
    param.getOutgoingDependencies().add(depInputWidth);
  }

  private static void createRefinement(final Actor actor, boolean packer) {
    final FunctionPrototype funcProto = PiMMUserFactory.instance.createFunctionPrototype();
    funcProto.setIsCPPdefinition(true);
    if (packer) {
      funcProto.setName("packTokens<IN_W, OUT_W, in_t>");
    } else {
      funcProto.setName("unpackTokens<IN_W, OUT_W, out_t>");
    }

    createArgument(funcProto, Direction.IN);
    createArgument(funcProto, Direction.OUT);

    final CHeaderRefinement newRefinement = PiMMUserFactory.instance.createCHeaderRefinement();
    newRefinement.setLoopPrototype(funcProto);
    newRefinement.setFilePath(actor.getName() + "/packing.hpp");
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
