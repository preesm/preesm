package org.preesm.algorithm.schedule.fpga;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.preesm.algorithm.schedule.fpga.FpgaAnalysisMainTask.AnalysisResultFPGA;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.scenario.Scenario;

public class TokenPackingAnalysis {
  // Might be better to remove sizes of 1, 2, 4, 8, 16, 32 and 64 bits as they only allow
  // the use of 32k/36k of the bram, if packing ends up being used.
  // private static final Long[] bramSizes = { 1L, 2L, 4L, 8L, 9L, 16L, 18L, 32L, 36L, 64L, 72L };

  // private static final Map<Long, Long> peeps = Map.of(1L, 32L * 1024, 2L, 16L * 1024, 4L, 8L * 1024, 8L, 4L * 1024,
  // 9L,
  // 4L * 1024, 16L, 2L * 1024, 18L, 2L * 1024, 32L, 1L * 1024, 36L, 1L * 1024, 64L, 512L, 72L, 512L);

  // private static final Map<Long,
  // Long> bramMap = Stream.of(new Long[][] { { 1L, 32L * 1024 }, { 2L, 16L * 1024 }, { 4L, 8L * 1024 },
  // { 8L, 4L * 1024 }, { 9L, 4L * 1024 }, { 16L, 2L * 1024 }, { 18L, 2L * 1024 }, { 32L, 1L * 1024 },
  // { 36L, 1L * 1024 }, { 64L, 512L }, { 72L, 512L } }).collect(Collectors.toMap(p -> p[0], p -> p[1]));

  // Would ideally be pulled from the scenario
  // private static final Map<Long, Long> bramMap = Stream.of(new AbstractMap.SimpleImmutableEntry<>(1L, 32L * 1024),
  // new AbstractMap.SimpleImmutableEntry<>(2L, 16L * 1024), new AbstractMap.SimpleImmutableEntry<>(4L, 8L * 1024),
  // new AbstractMap.SimpleImmutableEntry<>(8L, 4L * 1024), new AbstractMap.SimpleImmutableEntry<>(9L, 4L * 1024),
  // new AbstractMap.SimpleImmutableEntry<>(16L, 2L * 1024), new AbstractMap.SimpleImmutableEntry<>(18L, 2L * 1024),
  // new AbstractMap.SimpleImmutableEntry<>(32L, 1L * 1024), new AbstractMap.SimpleImmutableEntry<>(36L, 1L * 1024),
  // new AbstractMap.SimpleImmutableEntry<>(64L, 512L), new AbstractMap.SimpleImmutableEntry<>(72L, 512L))
  // .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

  private static final Map<Long, Long> bramMap = Stream
      .of(new AbstractMap.SimpleImmutableEntry<>(1L, 16L * 1024), new AbstractMap.SimpleImmutableEntry<>(2L, 8L * 1024),
          new AbstractMap.SimpleImmutableEntry<>(4L, 4L * 1024), new AbstractMap.SimpleImmutableEntry<>(8L, 2L * 1024),
          new AbstractMap.SimpleImmutableEntry<>(9L, 2L * 1024), new AbstractMap.SimpleImmutableEntry<>(16L, 1L * 1024),
          new AbstractMap.SimpleImmutableEntry<>(18L, 1L * 1024), new AbstractMap.SimpleImmutableEntry<>(32L, 1L * 512),
          new AbstractMap.SimpleImmutableEntry<>(36L, 1L * 512), new AbstractMap.SimpleImmutableEntry<>(64L, 256L),
          new AbstractMap.SimpleImmutableEntry<>(72L, 256L))
      .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

  private static final Long BRAM_36K = 36 * 1024L;
  private static final Long BRAM_32K = 32 * 1024L;
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
    // If dataTypeSize matches a default size
    if (bramMap.containsKey(dataTypeSize))
      return null;

    long defaultBramWidth = Long.MAX_VALUE;

    for (Map.Entry<Long, Long> bramEntry : bramMap.entrySet()) {
      if (bramEntry.getKey() > dataTypeSize && bramEntry.getKey() < defaultBramWidth)
        defaultBramWidth = bramEntry.getKey();
    }

    final long actualFootprint = depth * defaultBramWidth;
    final long baseNbBram = bramUsageVitis(depth, dataTypeSize);

    // if the BRAM usage is already at 1, no packing needed
    if (baseNbBram <= 1)
      return null;

    long bestPacketWidth = defaultBramWidth;
    long bestNbBram = baseNbBram;

    // if defaultBramWidth is a power of 2, bram size is 32K, else it is 36K
    // TODO: Only keep the 36K bram size and pack powers of 2 when needed.
    if ((((defaultBramWidth & (defaultBramWidth - 1)) == 0) && (actualFootprint > BRAM_32K))
        || (((defaultBramWidth & (defaultBramWidth - 1)) != 0) && (actualFootprint > BRAM_36K))) {

      for (Map.Entry<Long, Long> bramEntry : bramMap.entrySet()) {

        final long packingSize = bramEntry.getKey();

        final float test = packingSize / (float) dataTypeSize;

        // Skip if there isn't at least 2 data packed
        if (test < 2.0f)
          continue;

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

          // final long packedFootprint = packedDepth * packingSize;

          final long testPacketWidth = nbData * dataTypeSize;

          final long testNbBram = bramUsageVitis(packedDepth, testPacketWidth);

          // Compare this result with the previous best case
          if (testNbBram < bestNbBram) {
            bestNbBram = testNbBram;
            bestPacketWidth = nbData * dataTypeSize;
          }
        }
      }
    }

    if (bestNbBram != baseNbBram) {
      PreesmLogger.getLogger()
          .fine(fifo.getId() + " can be packed. Reduction from " + baseNbBram + " to " + bestNbBram + " BRAM");

      final long newDepth = (long) Math.ceil((float) depth / ((double) bestPacketWidth / dataTypeSize));
      PreesmLogger.getLogger().finer(() -> "New depth is " + newDepth);
      PreesmLogger.getLogger().finer(dataTypeSize + "-bit packed in packets of " + bestPacketWidth + " bits");

      return new PackedFifoConfig(fifo, dataTypeSize, bestPacketWidth);
    }

    PreesmLogger.getLogger().fine(fifo.getId() + " (on " + baseNbBram + " bram) can't be packed.");
    return null;
  }

  /*
   * Compute how many BRAM used on a FIFO, from the depth and the token size, to match with Vitis HLS
   *
   * If depth < 512 Math.ceil(dataWidth/18) A partir du moment où ça passe en BRAM
   *
   * If 512 < depth < 2048 bramWidth = 18*1024
   *
   * If 2048 < depth < 4096 bramWidth = 18*1024 If dataWidth == 13 || dataWidth == 21 || dataWidth == 22 || dataWidth >
   * 28 BRAM++ as result need to be even
   *
   * If 4096 < depth bramWidth = 16*1024
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
      return (long) Math.ceil(dataWidth / 18);
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
