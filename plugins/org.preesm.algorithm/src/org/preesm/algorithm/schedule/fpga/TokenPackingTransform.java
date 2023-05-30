package org.preesm.algorithm.schedule.fpga;

import java.util.List;
import org.preesm.algorithm.schedule.fpga.AbstractGenericFpgaFifoEvaluator.AnalysisResultFPGA;
import org.preesm.algorithm.schedule.fpga.TokenPackingAnalysis.PackedFifoConfig;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.TimingType;

/**
 * This class includes packer and unpacker on targeted fifos for memory optimization
 *
 * @author mdardail
 */

public final class TokenPackingTransform extends ActorConstructTransform {

  public static void transform(AnalysisResultFPGA res, Scenario scenario, List<PackedFifoConfig> workList) {
    for (final PackedFifoConfig config : workList) {
      addTokenPacking(res, scenario, config.fifo, config.originalWidth, config.updatedWidth);
    }
  }

  public static void addTokenPacking(AnalysisResultFPGA res, Scenario scenario, Fifo fifo, long unpackedSize,
      long packedSize) {

    // Create packer and unpacker
    final Actor packer = PiMMUserFactory.instance.createActor(fifo.getId() + "_packing");
    createRefinement(packer, "packTokens<IN_W, OUT_W, in_t>", "/packing.hpp");
    res.flatGraph.addActor(packer);
    final Actor unpacker = PiMMUserFactory.instance.createActor(fifo.getId() + "_unpacking");
    createRefinement(unpacker, "unpackTokens<IN_W, OUT_W, in_t>", "/packing.hpp");
    res.flatGraph.addActor(unpacker);

    // Map packer and unpacker to FPGA targeted by packed actor
    final ComponentInstance target = scenario.getPossibleMappings((AbstractActor) fifo.getTarget()).get(0);
    scenario.getConstraints().addConstraint(target, packer);
    scenario.getConstraints().addConstraint(target, unpacker);

    final long compressionRatio = packedSize / unpackedSize;

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
    connectParameter(packer, unpacked, "IN_W");
    connectParameter(packer, packed, "OUT_W");
    connectParameter(unpacker, packed, "IN_W");
    connectParameter(unpacker, unpacked, "OUT_W");

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
}
