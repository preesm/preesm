package org.preesm.codegen.model.generator2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.preesm.algorithm.clustering.ClusterHelper;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memalloc.model.LogicalBuffer;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.SubBuffer;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.PortKind;
import org.preesm.model.scenario.Scenario;

/**
 * This class is an extension of the {@link PiCodegenModelGenerator} class. The new feature here is that it takes an
 * {@link Allocation} as an input to make the buffer memory allocation, as the {@link PiCodegenModelGenerator} makes the
 * buffer memory allocation directly from the {@link Fifo fifos} of the cluster. This class allows a more complex and a
 * smarter memory allocation, without modifying its behavior. In any case, the interfaces fifos will be processed in a
 * top down way. In other words, if a fifo is linked to a {@link DataInterface} actor, it is supposed that it is already
 * allocated, and the external fifo will be retrieved.
 *
 * @author rcazoulat
 */
public class PiCodegenModelGenerator2 extends PiCodegenModelGenerator {

  /**
   * The allocation, to make the buffer memory allocation
   */
  Allocation alloc = null;

  /**
   * Alloc buffer to Codegen buffer
   */
  Map<org.preesm.algorithm.memalloc.model.Buffer, Buffer> b2b;

  PiGraph originalCluster;

  public PiCodegenModelGenerator2(PiGraph originalCluster, Scenario scenario) {
    super(originalCluster, scenario);
    this.originalCluster = originalCluster;
    b2b = new HashMap<>();
  }

  public void generate(Schedule schedule, Allocation allocation) {
    this.alloc = allocation;
    // This line is already in super.generate(...) but we need topCluster to not be null for link
    topCluster = (PiGraph) ((HierarchicalSchedule) schedule).getAttachedActor();
    super.generate(schedule);
  }

  @Override
  protected void generateParameterClusterBuffers(PiGraph graph) {
    for (final DataInterface i : graph.getDataInterfaces()) {
      final Fifo fifo = i.getDataPort().getFifo();
      final Buffer codegenBuffer = retrieveAssociatedBuffer(fifo, i.getDataPort().getKind());

      // We (re?)write name
      codegenBuffer.setName(i.getName());

      // To ensure correct behavior of the rest of the process
      parameterBufferMap.put(fifo, codegenBuffer);

      if (i instanceof DataInputInterface) {
        loopFuncBlock.getInputArgs().add(codegenBuffer);
      } else {
        loopFuncBlock.getOutputArgs().add(codegenBuffer);
      }
    }
  }

  @Override
  protected final List<Buffer> generateInternalClusterBuffers(final PiGraph cluster, final long scopeRep) {

    // List of local internal buffer that will be defined in cluster scope
    final List<Buffer> localInternalBuffer = new LinkedList<>();
    for (final Fifo fifo : ClusterHelper.getInternalClusterFifo(cluster)) {
      final Buffer buffer = retrieveAssociatedBuffer(fifo, null);
      bufferMap.put(fifo, buffer);
      localInternalBuffer.add(buffer);

    }
    return localInternalBuffer;
  }

  @Override
  protected final Buffer retrieveAssociatedBuffer(final Fifo fifo, final PortKind dir) {
    try {
      // Trying to get the buffer in the maps
      return super.retrieveAssociatedBuffer(fifo, dir);
    } catch (final Exception e) {
      // If it doesn't work, trying to get it in b2b map
      final org.preesm.algorithm.memalloc.model.Buffer allocBuffer = alloc.getFifoAllocations().get(fifo)
          .getSourceBuffer();

      if (allocBuffer == null) {
        throw new PreesmRuntimeException("The fifo" + fifo.getId() + "is not link to any allocation buffer");
      }

      // If here, it meeans that it the first time that the FIFO is seen
      if (!b2b.containsKey(allocBuffer)) {
        createBufferFromAlloc(fifo);
      }
      return b2b.get(allocBuffer);
    }
  }

  /**
   * This method will create a {@link Buffer codegen buffer} from an {@link org.preesm.algorithm.memalloc.model.Buffer
   * allocation buffer}, by retrieving the alloc buffer linked to the input fifo. The result will be stored in the
   * {@link #b2b} attribute
   *
   * @param fifo
   *          input fifo
   */
  protected void createBufferFromAlloc(final Fifo fifo) {
    final org.preesm.algorithm.memalloc.model.Buffer allocBuffer = alloc.getFifoAllocations().get(fifo)
        .getSourceBuffer();

    Buffer codegenBuffer;
    if (allocBuffer instanceof final LogicalBuffer logicalBuffer) {
      codegenBuffer = CodegenModelUserFactory.eINSTANCE.createSubBuffer();
      final long offset = logicalBuffer.getOffsetInBit();
      ((SubBuffer) codegenBuffer).setOffsetInBit(offset);

    }
    codegenBuffer = CodegenModelUserFactory.eINSTANCE.createBuffer();

    final long typeSizeInBit = scenario.getSimulationInfo().getDataTypeSizeInBit(fifo.getType());
    final String name = "mem_" + fifo.getSource().getName() + "_to_" + fifo.getTarget().getName();
    codegenBuffer.setName(name);
    codegenBuffer.setType(fifo.getType());
    codegenBuffer.setTokenTypeSizeInBit(typeSizeInBit);
    codegenBuffer.setNbToken(allocBuffer.getSizeInBit() / typeSizeInBit);

    b2b.put(allocBuffer, codegenBuffer);
  }
}
