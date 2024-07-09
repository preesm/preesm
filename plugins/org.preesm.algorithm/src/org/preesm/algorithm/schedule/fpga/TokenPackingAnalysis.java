/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2022 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2022)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2022 - 2024)
 * Dardaillon Mickael [mickael.dardaillon@insa-rennes.fr] (2022)
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.preesm.algorithm.schedule.fpga.AbstractGenericFpgaFifoEvaluator.AnalysisResultFPGA;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.scenario.Scenario;

public class TokenPackingAnalysis {

  private TokenPackingAnalysis() {
    // Forbids instantiation
  }

  private static final Long BRAM_18K = 18 * 1024L;
  private static final Long BRAM_16K = 16 * 1024L;

  private static Long noPackingSum = 0L;
  private static Long packingSum   = 0L;

  public static class PackedFifoConfig {
    // fifo to pack
    public final Fifo fifo;
    // original data width in bits
    public final Long originalWidth;
    // updated data width in bits containing multiple tokens
    public final Long updatedWidth;
    // Actor which the packer and unpacker will be clusterized with
    public final AbstractActor attachedActor;

    private PackedFifoConfig(Fifo fifo, Long originalWidth, Long updatedWidth, AbstractActor actor) {
      this.fifo = fifo;
      this.originalWidth = originalWidth;
      this.updatedWidth = updatedWidth;
      this.attachedActor = actor;
    }
  }

  public static List<PackedFifoConfig> analysis(AnalysisResultFPGA res, Scenario scenario) {
    final List<PackedFifoConfig> packedFifos = new ArrayList<>();

    noPackingSum = 0L;
    packingSum = 0L;

    for (final Fifo fifo : res.flatGraph.getAllFifos()) {
      // TODO add test to verify if fifo can be packed without creating deadlock

      if (!((fifo.getSource() instanceof InterfaceActor) || (fifo.getTarget() instanceof InterfaceActor))) {
        final long fifoWidth = scenario.getSimulationInfo().getDataTypeSizeInBit(fifo.getType());
        final PackedFifoConfig packedFifo = computePacking(fifo, res.flatFifoSizes.get(fifo) / fifoWidth, fifoWidth,
            res.flatBrv);
        if (packedFifo != null) {
          packedFifos.add(packedFifo);
        }
      }
    }

    PreesmLogger.getLogger().info(() -> "Unpacked BRAM usage: " + noPackingSum);
    PreesmLogger.getLogger().info(() -> "Packed BRAM usage: " + packingSum);

    return packedFifos;
  }

  /**
   * Computes best packing to reduce the BRAM usage.
   *
   * @param fifo
   *          The fifo to pack.
   * @param depth
   *          The initial depth.
   * @param dataTypeSize
   *          The size of a token in bits.
   * @return A PackedFifoConfig containing the concerned Fifo, the token size, the final packet width and the actor to
   *         which the fifo is attached for the next analysis step.
   */
  private static PackedFifoConfig computePacking(Fifo fifo, long depth, long dataTypeSize,
      Map<AbstractVertex, Long> brv) {

    AbstractActor attachedActor = null;

    final long defaultBramWidth = dataTypeSize;

    final long baseNbBram = bramUsageVitis(depth, dataTypeSize);

    noPackingSum += baseNbBram;

    // if the BRAM usage is already at 1, no packing needed
    if (baseNbBram <= 1) {
      packingSum += baseNbBram;
      return null;
    }

    long bestPacketWidth = defaultBramWidth;
    long bestNbBram = baseNbBram;

    for (long packingSize = dataTypeSize * 2; packingSize <= dataTypeSize * 10; packingSize++) {

      final float test = packingSize / (float) dataTypeSize;

      final long nbDataInPacket = (long) Math.floor(test);

      final long srcRate = fifo.getSourcePort().getExpression().evaluate();
      final long tgtRate = fifo.getTargetPort().getExpression().evaluate();

      // Check if packing ratio is a divisor of the source rate or target rate
      if (srcRate % nbDataInPacket == 0 || tgtRate % nbDataInPacket == 0) {
        final long packedDepth = (long) Math.ceil((float) depth / nbDataInPacket);

        final long testPacketWidth = nbDataInPacket * dataTypeSize;

        final long testNbBram = bramUsageVitis(packedDepth, testPacketWidth);

        // Compare this result with the previous best case
        if (testNbBram < bestNbBram) {

          if (srcRate % nbDataInPacket == 0) {
            attachedActor = PreesmCopyTracker.getOriginalSource(fifo.getSourcePort().getContainingActor());
          } else if (tgtRate % nbDataInPacket == 0) {
            attachedActor = PreesmCopyTracker.getOriginalSource(fifo.getTargetPort().getContainingActor());
          } else {
            throw new PreesmRuntimeException("wtf");
          }

          bestNbBram = testNbBram;
          bestPacketWidth = nbDataInPacket * dataTypeSize;
        }
      }
    }

    if (bestNbBram != baseNbBram) {
      final long finalNbBram = bestNbBram;
      final long finalPacketWidth = bestPacketWidth;

      packingSum += finalNbBram;

      PreesmLogger.getLogger().finer(() -> fifo.getId() + " can be packed with a ratio of "
          + finalPacketWidth / dataTypeSize + ". Reduction from " + baseNbBram + " to " + finalNbBram + " BRAM.");

      final long newDepth = (long) Math.ceil(depth / ((double) finalPacketWidth / dataTypeSize));
      PreesmLogger.getLogger().finest(() -> "Old depth is " + depth + ", New depth is " + newDepth + ".");
      PreesmLogger.getLogger().finest(() -> dataTypeSize + "-bit packed in packets of " + finalPacketWidth + " bits.");

      return new PackedFifoConfig(fifo, dataTypeSize, finalPacketWidth, attachedActor);
    }

    packingSum += baseNbBram;
    PreesmLogger.getLogger().finer(() -> fifo.getId() + " with " + depth + " token of " + dataTypeSize + " bits on "
        + baseNbBram + " bram can't be packed.");
    return null;
  }

  /*
   * Compute how many BRAM used on a FIFO, from the depth and the token size, to match with Vitis HLS
   *
   * If depth is lower than 512, BRAM = Math.ceil(dataWidth/18) if the data is actually stored in bram.
   *
   * If depth is between 512 and 2048, bramWidth = 18*1024
   *
   * If depth is between 2048 and 4096, bramWidth = 18*1024. For dataWidth equal to 13, 21, 22 or above 28, the number
   * of bram needs to be evened.
   *
   * If depth is above 4096, bramWidth = 16*1024
   *
   *
   * @param depth The initial depth.
   *
   * @param dataWidth The size of a token in bits.
   *
   * @return The number of BRAM.
   *
   */
  private static long bramUsageVitis(long depth, long dataWidth) {

    // If less than 1KB, data isn't stored in a BRAM
    if (depth * dataWidth < 1024) {
      return 0;
    }

    // TODO Bien vérifier la formule pour le cas depth < 512. Cette division par 18 me parait étrange.
    if (depth < 512) {
      return (long) Math.ceil((double) dataWidth / 18);
    }
    if (512 <= depth && depth < 2048) {
      return (long) Math.ceil(Math.pow(2, Math.ceil(Math.log(depth) / Math.log(2))) * dataWidth / BRAM_18K);
    } else if (2048 <= depth && depth < 4096) {
      long bram = (long) Math.ceil(Math.pow(2, Math.ceil(Math.log(depth) / Math.log(2))) * dataWidth / BRAM_18K);

      if ((dataWidth == 13 || dataWidth == 21 || dataWidth == 22 || dataWidth > 28) && (bram % 2 == 1)) {
        bram++;
      }

      return bram;
    } else if (4096 <= depth) {
      return (long) Math.ceil(Math.pow(2, Math.ceil(Math.log(depth) / Math.log(2))) * dataWidth / BRAM_16K);
    }

    throw new PreesmRuntimeException("Something went wrong during BRAM computation.");
  }
}
