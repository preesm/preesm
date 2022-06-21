package org.preesm.algorithm.schedule.fpga;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.preesm.algorithm.schedule.fpga.FpgaAnalysisMainTask.AnalysisResultFPGA;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.scenario.Scenario;

public class TokenPackingAnalysis {

  private static final Long BRAM_18K = 18 * 1024L;
  private static final Long BRAM_16K = 16 * 1024L;

  public static class PackedFifoConfig {
    // fifo to pack
    public final Fifo fifo;
    // original data width in bits
    public final Long originalWidth;
    // updated data width in bits containing multiple tokens
    public final Long updatedWidth;

    private PackedFifoConfig(Fifo fifo, Long originalWidth, Long updatedWidth) {
      this.fifo = fifo;
      this.originalWidth = originalWidth;
      this.updatedWidth = updatedWidth;
    }
  }

  public static List<PackedFifoConfig> analysis(AnalysisResultFPGA res, Scenario scenario) {
    final List<PackedFifoConfig> packedFifos = new ArrayList<>();

    for (Fifo fifo : res.flatGraph.getAllFifos()) {
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
   * @return A Pair<Long, Long> containing the width of the data pack and the new depth.
   */
  private static PackedFifoConfig computePacking(Fifo fifo, long depth, long dataTypeSize,
      Map<AbstractVertex, Long> brv) {

    long defaultBramWidth = dataTypeSize;

    final long baseNbBram = bramUsageVitis(depth, dataTypeSize);

    // if the BRAM usage is already at 1, no packing needed
    if (baseNbBram <= 1)
      return null;

    long bestPacketWidth = defaultBramWidth;
    long bestNbBram = baseNbBram;

    for (long packingSize = dataTypeSize * 2; packingSize <= 72; packingSize++) {

      final float test = packingSize / (float) dataTypeSize;

      final long nbData = (long) Math.floor(test);

      final long srcRv = brv.get(fifo.getSource());
      final long tgtRv = brv.get(fifo.getTarget());

      final long srcRate = fifo.getSourcePort().getExpression().evaluate();
      final long tgtRate = fifo.getTargetPort().getExpression().evaluate();

      if (srcRv * srcRate != tgtRv * tgtRate) {
        throw new PreesmRuntimeException(
            fifo.getId() + " prod and cons do not match: " + srcRv * srcRate + "," + tgtRv * tgtRate);
      }

      // Check if the number of bram is reduced, and keep the best reduction
      if ((srcRate * srcRv) % nbData == 0) {
        final long packedDepth = (long) Math.ceil((float) depth / nbData);

        final long testPacketWidth = nbData * dataTypeSize;

        final long testNbBram = bramUsageVitis(packedDepth, testPacketWidth);

        // Compare this result with the previous best case
        if (testNbBram < bestNbBram) {
          bestNbBram = testNbBram;
          bestPacketWidth = nbData * dataTypeSize;
        }
      }
    }

    if (bestNbBram != baseNbBram) {
      final long finalNbBram = bestNbBram;
      final long finalPacketWidth = bestPacketWidth;

      PreesmLogger.getLogger()
          .fine(() -> fifo.getId() + " can be packed. Reduction from " + baseNbBram + " to " + finalNbBram + " BRAM");

      final long newDepth = (long) Math.ceil((float) depth / ((double) finalPacketWidth / dataTypeSize));
      PreesmLogger.getLogger().finer(() -> "New depth is " + newDepth);
      PreesmLogger.getLogger().finer(() -> dataTypeSize + "-bit packed in packets of " + finalPacketWidth + " bits");

      return new PackedFifoConfig(fifo, dataTypeSize, finalPacketWidth);
    }

    PreesmLogger.getLogger().fine(() -> fifo.getId() + " (on " + baseNbBram + " bram) can't be packed.");
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

    if (depth < 512)
      return (long) Math.ceil((double) dataWidth / 18);
    else if (512 <= depth && depth < 2048)
      return (long) Math.ceil(Math.pow(2, Math.ceil(Math.log(depth) / Math.log(2))) * dataWidth / BRAM_18K);
    else if (2048 <= depth && depth < 4096) {
      long bram = (long) Math.ceil(Math.pow(2, Math.ceil(Math.log(depth) / Math.log(2))) * dataWidth / BRAM_18K);

      if ((dataWidth == 13 || dataWidth == 21 || dataWidth == 22 || dataWidth > 28) && (bram % 2 == 1))
        bram++;

      return bram;
    } else if (4096 <= depth)
      return (long) Math.ceil(Math.pow(2, Math.ceil(Math.log(depth) / Math.log(2))) * dataWidth / BRAM_16K);

    throw new PreesmRuntimeException("Something went wrong during BRAM computation.");
  }
}
