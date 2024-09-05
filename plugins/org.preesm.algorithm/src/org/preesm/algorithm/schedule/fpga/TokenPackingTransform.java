/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2023) :
 *
 * Dardaillon Mickael [mickael.dardaillon@insa-rennes.fr] (2023)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

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
    final long rep = fifo.getSourcePort().getPortRateExpression().evaluateAsLong()
        * res.flatBrv.get(fifo.getSourcePort().getContainingActor()) / compressionRatio;
    res.flatBrv.put(packer, rep);
    res.flatBrv.put(unpacker, rep);

    // Create data ports
    final DataInputPort packerInput = createDataInputPort(packer, compressionRatio);
    final DataOutputPort packerOutput = createDataOutputPort(packer, 1);
    final DataInputPort unpackerInput = createDataInputPort(unpacker, 1);
    final DataOutputPort unpackerOutput = createDataOutputPort(unpacker, compressionRatio);

    // Create and connect FIFOs
    final Fifo newFifo = PiMMUserFactory.instance.createFifo(unpackerOutput, fifo.getTargetPort(), fifo.getType());
    res.flatGraph.addFifo(newFifo);
    final Fifo newPackedFifo = PiMMUserFactory.instance.createFifo(packerOutput, unpackerInput,
        "ap_uint<" + packedSize + ">");
    res.flatGraph.addFifo(newPackedFifo);
    fifo.setTargetPort(packerInput);
    scenario.getSimulationInfo().getDataTypes().put("ap_uint<" + packedSize + ">", packedSize);

    // Set FIFO sizes
    res.flatFifoSizes.put(newPackedFifo, res.flatFifoSizes.get(fifo));
    res.flatFifoSizes.put(fifo, 2 * unpackedSize);
    res.flatFifoSizes.put(newFifo, 2 * unpackedSize);
  }
}
