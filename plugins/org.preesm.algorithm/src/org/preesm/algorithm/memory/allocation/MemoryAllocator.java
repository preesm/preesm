/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Julien Hascoet [jhascoet@kalray.eu] (2017)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2016)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2014)
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
package org.preesm.algorithm.memory.allocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.jgrapht.graph.DefaultEdge;
import org.preesm.algorithm.mapper.graphtransfo.BufferAggregate;
import org.preesm.algorithm.mapper.graphtransfo.BufferProperties;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionVertex;
import org.preesm.algorithm.memory.script.Range;
import org.preesm.algorithm.model.PropertyBean;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.math.MathFunctionsHelper;

/**
 * This class is both an interface and toolbox class for memory allocator.
 *
 * @author kdesnos
 */
public abstract class MemoryAllocator {

  /**
   * This method scan the {@link MemoryExclusionVertex memory objects} of an input {@link MemoryExclusionGraph} in order
   * to align its internal subbuffers.<br>
   * <br>
   * Since a {@link DAGEdge} might be the result of an aggregation of several {@link SDFEdge} from the original
   * {@link SDFGraph}, a {@link MemoryExclusionVertex memory object} might "contain" several subbuffer, each
   * corresponding to one of these aggregated edges. The purpose of this method is to ensure that all subbuffers are
   * correctly aligned in memory when allocating the {@link MemoryExclusionGraph}. To do so, each
   * {@link MemoryExclusionVertex} is processed so that each subbuffer is given an "internal" offset in the memory
   * object that fulfill its alignment constraint. The size of the {@link MemoryExclusionVertex} might be modified by
   * this method. Note that we do not try here to optimize the space taken by each memory object by reordering the
   * subbuffers.
   *
   * @param meg
   *          The {@link MemoryExclusionGraph} whose {@link MemoryExclusionVertex} must be aligned. The size of the
   *          {@link MemoryExclusionVertex} might be modified by this method.
   * @param alignment
   *          <li><b>{@link #alignment}=-1</b>: Data should not be aligned.</li>
   *          <li><b>{@link #alignment}= 0</b>: Data should be aligned according to its own type. For example, an array
   *          of int32 should begin at an offset (i.e. an address) that is a multiple of 4.</li>
   *          <li><b>{@link #alignment}= N</b>: All data should be aligned to the given value N. This means that all
   *          arrays will begin at an offset that is a multiple of N. It does not mean that ALL array elements are
   *          aligned on N, only the first element. If an array has a data type different than 1, then the least common
   *          multiple of the two values is used to align the data.</li>
   * @return the total amount of memory added to the {@link MemoryExclusionVertex}
   */
  public static long alignSubBuffers(final MemoryExclusionGraph meg, final long alignment) {
    long addedSpace = 0;

    if (alignment == -1) {
      return addedSpace;
    }

    // Build a list of all MObject of the graph, including merged ones
    final Set<MemoryExclusionVertex> allMObjects = new LinkedHashSet<>(meg.vertexSet());
    // Include merged Mobjects (if any)
    final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hostMap = meg.getPropertyBean()
        .getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);

    if (hostMap != null) {
      hostMap.values().stream().forEach(mergedMObjects -> allMObjects.addAll(mergedMObjects));
    }

    // Scan the vertices of the graph
    for (final MemoryExclusionVertex memObj : allMObjects) {
      // Check alignment of DAGEdge (that may involve subbuffers)
      // other memory objects can be ignored in this method.
      final DAGEdge edge = memObj.getEdge();
      if (edge == null) {
        continue;
      }

      final BufferAggregate buffers = edge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
      final List<Long> interBufferSpaces = new ArrayList<>();
      long largestTypeSize = 1;
      long internalOffset = 0; // In Bits
      for (final BufferProperties properties : buffers) {
        final String dataType = properties.getDataType();
        final long typeSize = meg.getScenario().getSimulationInfo().getDataTypeSizeInBit(dataType);
        largestTypeSize = Math.max(typeSize, largestTypeSize);
        long interSpace = 0;

        // Data alignment case
        // If the subbuffer is not aligned, add an interspace.
        if ((alignment == 0) && ((internalOffset % typeSize) != 0)) {
          interSpace = typeSize - (internalOffset % typeSize);
        }

        // Fixed alignment
        // If the subbuffer is not aligned, add an interspace.
        if (alignment > 0) {
          final long align = MathFunctionsHelper.lcm(typeSize, alignment);
          if ((internalOffset % align) != 0) {
            interSpace = align - (internalOffset % align);
          }
        }

        interBufferSpaces.add(interSpace);
        internalOffset += interSpace + (typeSize * properties.getNbToken());
      }

      // Update the size of the memObject and add the interbuffer
      // space if it does not contain with 0.
      if ((internalOffset - memObj.getWeight()) > 0) {
        memObj.setPropertyValue(MemoryExclusionVertex.INTER_BUFFER_SPACES, interBufferSpaces);
        addedSpace += internalOffset - memObj.getWeight();
        memObj.setWeight(internalOffset);
      }
      // Backup the largest typeSize contained in the aggregate.
      // This information will be used to align the memObject
      // during allocation
      memObj.setPropertyValue(MemoryExclusionVertex.TYPE_SIZE, largestTypeSize);

    }
    return addedSpace;
  }

  /**
   * This value is used to configure how allocated memory objects should be aligned in memory.<br>
   * The following configurations are valid:<br>
   *
   * <li><b>{@link #alignment}=-1</b>: Data should not be aligned.</li>
   * <li><b>{@link #alignment}= 0</b>: Data should be aligned according to its own type. For example, an array of int32
   * should begin at an offset (i.e. an address) that is a multiple of 4.</li>
   * <li><b>{@link #alignment}= N</b>: All data should be aligned to the given value N. This means that all arrays will
   * begin at an offset that is a multiple of N. It does not mean that ALL array elements are aligned on N, only the
   * first element.</li>
   */
  protected long alignment;

  /**
   * An allocation is a map of edges associated to an integer which represents their offset in a monolithic memory.<br>
   * <br>
   * <table>
   * <tr>
   * <td>Edge<sub>size</sub></td>
   * <td>Offset</td>
   * </tr>
   * <tr>
   * <td>A->B<sub>100</sub></td>
   * <td>0</td>
   * </tr>
   * <tr>
   * <td>B->C<sub>200</sub></td>
   * <td>100</td>
   * </tr>
   * <tr>
   * <td>C->D<sub>50</sub></td>
   * <td>0</td>
   * </tr>
   * <tr>
   * <td>C->E<sub>25</sub></td>
   * <td>50</td>
   * </tr>
   * </table>
   */
  private final Map<DAGEdge, Long> edgeAllocation;

  /**
   * An allocation is a map of fifo associated to an integer which represents their offset in a monolithic memory.<br>
   * <br>
   * <table border="1">
   * <tr>
   * <td>FIFO<sub>size</sub></td>
   * <td>Offset</td>
   * </tr>
   * <tr>
   * <td>FIFO_Head_B_end->A_init<sub>100</sub></td>
   * <td>0</td>
   * </tr>
   * <tr>
   * <td>FIFO_Body_C_end->B_init<sub>200</sub></td>
   * <td>100</td>
   * </tr>
   * </table>
   */
  private final Map<MemoryExclusionVertex, Long> fifoAllocation;

  /**
   * An allocation is a map of {@link MemoryExclusionVertex memory objects} associated to an integer which represents
   * their offset in a monolithic memory.<br>
   * <br>
   * <table border="1">
   * <tr>
   * <td>Edge<sub>size</sub></td>
   * <td>Offset</td>
   * </tr>
   * <tr>
   * <td>A->B<sub>100</sub></td>
   * <td>0</td>
   * </tr>
   * <tr>
   * <td>B->C<sub>200</sub></td>
   * <td>100</td>
   * </tr>
   * <tr>
   * <td>C->D<sub>50</sub></td>
   * <td>0</td>
   * </tr>
   * <tr>
   * <td>C->E<sub>25</sub></td>
   * <td>50</td>
   * </tr>
   * </table>
   */
  protected Map<MemoryExclusionVertex, Long> memExNodeAllocation;

  /** The input exclusion graph. */
  protected MemoryExclusionGraph inputExclusionGraph;

  /**
   * Constructor of the MemoryAllocator.
   *
   * <p>
   * Default {@link MemoryAllocator} has no {@link #alignment}.
   * </p>
   *
   * @param memEx
   *          The exclusion graph to analyze
   */
  protected MemoryAllocator(final MemoryExclusionGraph memEx) {
    this.edgeAllocation = new LinkedHashMap<>();
    this.fifoAllocation = new LinkedHashMap<>();

    this.memExNodeAllocation = new LinkedHashMap<>();
    this.inputExclusionGraph = memEx;

    this.inputExclusionGraph.setPropertyValue(MemoryExclusionGraph.DAG_EDGE_ALLOCATION, this.edgeAllocation);
    this.inputExclusionGraph.setPropertyValue(MemoryExclusionGraph.DAG_FIFO_ALLOCATION, this.fifoAllocation);
    this.alignment = -1;
  }

  /**
   * This method will perform the memory allocation of graph edges and store the result in the allocation LinkedHashMap.
   *
   * <p>
   * This method does not call {@link #alignSubBuffers(MemoryExclusionGraph)}. To ensure a correct alignment, the
   * {@link #alignSubBuffers(MemoryExclusionGraph)} method must be called before the {@link #allocate()} method. The
   * {@link #inputExclusionGraph} might be modified by calling this function. (new {@link MemoryExclusionVertex} might
   * be added because of HostMemoryObjects). To put the {@link #inputExclusionGraph} back in its original state, call
   * the deallocate method.
   * </p>
   */
  public abstract void allocate();

  /**
   * Method used to allocate a {@link MemoryExclusionVertex memory object} in memory at the given offset. The method
   * allocates both the {@link MemoryExclusionVertex} in the {@link #memExNodeAllocation} table and its corresponding
   * {@link DAGEdge} in the {@link #edgeAllocation} table. It also updates the {@link PropertyBean} of the
   * {@link MemoryExclusionVertex memObject} with the allocation information (i.e. the offset).
   *
   * @param vertex
   *          the allocated {@link MemoryExclusionVertex memory object}
   * @param offset
   *          the memory offset at which the {@link MemoryExclusionVertex memory object} is allocated.
   */
  protected void allocateMemoryObject(final MemoryExclusionVertex vertex, final long offset) {
    // TODO change the return type from void to boolean.
    // The returned value will be used to tell if the allocation
    // is authorized (i.e. if there is no conflict with already allocated
    // memObjects).
    // A performance check should be performed when implementing this change
    // in order to make sure that this does not kill the perf.

    this.memExNodeAllocation.put(vertex, offset);

    if (vertex.getEdge() != null) {
      this.edgeAllocation.put(vertex.getEdge(), offset);
    } else if (vertex.getSource().startsWith(MemoryExclusionGraph.FIFO_HEAD_PREFIX)) {
      this.fifoAllocation.put(vertex, offset);
    }

    vertex.setPropertyValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY, offset);
    final Object sizeValue = this.inputExclusionGraph.getPropertyBean()
        .getValue(MemoryExclusionGraph.ALLOCATED_MEMORY_SIZE);
    final long size;
    if (sizeValue == null) {
      size = Long.MIN_VALUE;
    } else {
      size = (long) sizeValue;
    }
    if (size < (offset + vertex.getWeight())) {
      this.inputExclusionGraph.setPropertyValue(MemoryExclusionGraph.ALLOCATED_MEMORY_SIZE,
          offset + vertex.getWeight());
    }

    // If the allocated memory object is the result from a merge
    // do the specific processing.
    final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hostMap = this.inputExclusionGraph.getPropertyBean()
        .getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
    if ((hostMap != null) && hostMap.containsKey(vertex)) {
      allocateHostMemoryObject(vertex, hostMap.get(vertex), offset);
    }
  }

  /**
   * Special processing for {@link MemoryExclusionVertex memory objects} resulting from memory script merges.<br>
   * Put back all hosted {@link MemoryExclusionVertex} in the {@link MemoryExclusionVertex} with their original
   * exclusions (i.e. their exclusion before script application). Put the host {@link MemoryExclusionVertex} back to its
   * original size give it its original exclusions.
   *
   * @param hostVertex
   *          the "host" {@link MemoryExclusionVertex}, i.e. the {@link MemoryExclusionVertex} that "contains" several
   *          other {@link MemoryExclusionVertex memory objects} from the original {@link MemoryExclusionGraph}.
   *
   * @param vertices
   *          the {@link Set} of {@link MemoryExclusionVertex} contained in the "host".
   * @param offset
   *          the offset of the hostVertex
   */
  private void allocateHostMemoryObject(final MemoryExclusionVertex hostVertex,
      final Set<MemoryExclusionVertex> vertices, final long offset) {
    // 1 - Put back all hosted mobj in the meg (with their exclusions)
    // 2 - Put the host Mobj back to its original size and exclusions
    final List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> list = hostVertex.getPropertyBean()
        .getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);
    final Pair<MemoryExclusionVertex, Pair<Range, Range>> hostRealTokenRange = list.get(0);

    final long hostZeroIndexOffset = hostRealTokenRange.getValue().getValue().getStart();

    // 1- Put back all hosted mobj in the meg (with their exclusions)
    // For each vertex of the group
    for (final MemoryExclusionVertex vertex : vertices) {

      // Get its offset within the host vertex
      final List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> realTokenRange = vertex.getPropertyBean()
          .getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);

      final List<MemoryExclusionVertex> neighbors = vertex.getPropertyBean()
          .getValue(MemoryExclusionVertex.ADJACENT_VERTICES_BACKUP);

      // If the Mobject is not splitted
      if (realTokenRange.size() == 1) {
        final long startOffset = realTokenRange.get(0).getValue().getValue().getStart();

        // Compute the space that must be left empty before the
        // allocated space to ensure that the MObject has its own
        // cache line.
        long emptySpace = 0;
        if ((this.alignment > 0) && (((offset + startOffset + hostZeroIndexOffset) % this.alignment) != 0)) {
          emptySpace = (offset + startOffset + hostZeroIndexOffset) % this.alignment;
        }
        vertex.setPropertyValue(MemoryExclusionVertex.EMPTY_SPACE_BEFORE, emptySpace);
        // Enlarge the weight of the vertex to include the empty
        // space
        vertex.setWeight(vertex.getWeight() + emptySpace);

        // Allocate it at the right place
        final long memOffset = (offset + startOffset + hostZeroIndexOffset) - emptySpace;
        this.memExNodeAllocation.put(vertex, memOffset);
        this.edgeAllocation.put(vertex.getEdge(), memOffset);
        vertex.setPropertyValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY, memOffset);

        // Put the MObject Back in the MEG
        this.inputExclusionGraph.addVertex(vertex);

        // Put back the exclusions with all neighbors
        for (final MemoryExclusionVertex neighbor : neighbors) {
          // If the neighbor is not part of the same merge
          // operation
          if (!vertices.contains(neighbor) && (neighbor != hostVertex)) {
            // Restore its old exclusions
            if (this.inputExclusionGraph.containsVertex(neighbor)) {
              this.inputExclusionGraph.addEdge(vertex, neighbor);
            } else {
              excludeWithHostedNeighbor(vertex, neighbor);
            }
          }
        }

      } else {
        // If the Mobject is splitted Null buffer since the memory of this MObj is no longer contiguous
        vertex.setWeight(0L);
        // Allocate it at index -1
        this.memExNodeAllocation.put(vertex, -1L);
        this.edgeAllocation.put(vertex.getEdge(), -1L);
        vertex.setPropertyValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY, -1L);
        this.inputExclusionGraph.addVertex(vertex);
        vertex.setPropertyValue(MemoryExclusionVertex.EMPTY_SPACE_BEFORE, -1L);

        // Put a fake MObject in the MEG for each subrange
        final List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> realRanges = vertex.getPropertyBean()
            .getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);

        int indexPart = 0;
        // For each contiguous range
        for (final Pair<MemoryExclusionVertex, Pair<Range, Range>> realRange : realRanges) {
          // If the host of this subrange is the current host vertex (else do nothing here for this subrange)
          if (hostVertex == realRange.getKey()) {

            // Get host range
            final Range hostRange = realRange.getValue().getValue();
            final long startOffset = hostRange.getStart();

            // Create new fake Mobj
            final MemoryExclusionVertex fakeMObj = new MemoryExclusionVertex(
                "part" + indexPart + "_" + vertex.getSource(), vertex.getSink(), hostRange.getLength(),
                hostVertex.getScenario());

            // Compute the space that must be left empty before the allocated space to ensure that the MObject has its
            // own cache line.
            long emptySpace = 0;
            if ((this.alignment > 0) && (((offset + startOffset + hostZeroIndexOffset) % this.alignment) != 0)) {
              emptySpace = (offset + startOffset + hostZeroIndexOffset) % this.alignment;
            }
            fakeMObj.setPropertyValue(MemoryExclusionVertex.EMPTY_SPACE_BEFORE, emptySpace);
            // Enlarge the weight of the vertex to include the
            // empty space
            fakeMObj.setWeight(fakeMObj.getWeight() + emptySpace);

            // Allocate the fake Mobject
            // (in order to be considered when checking for
            // exclusions)

            this.memExNodeAllocation.put(fakeMObj, (offset + startOffset + hostZeroIndexOffset) - emptySpace);
            fakeMObj.setPropertyValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY,
                (offset + startOffset + hostZeroIndexOffset) - emptySpace);

            // Put the Fake MObject in the MEG
            this.inputExclusionGraph.addVertex(fakeMObj);

            // Backup the fakeMobj in the original vertex (for
            // deallocation purpose)
            List<MemoryExclusionVertex> fakeMobjects = vertex.getPropertyBean()
                .getValue(MemoryExclusionVertex.FAKE_MOBJECT);
            if (fakeMobjects == null) {
              fakeMobjects = new ArrayList<>();
              vertex.setPropertyValue(MemoryExclusionVertex.FAKE_MOBJECT, fakeMobjects);
            }
            fakeMobjects.add(fakeMObj);

            // Put back the exclusions with all neighbors
            for (final MemoryExclusionVertex neighbor : neighbors) {
              // If the neighbor is not part of the same merge
              // operation
              if (!vertices.contains(neighbor) && (neighbor != hostVertex)) {
                // Restore its old exclusions
                if (this.inputExclusionGraph.containsVertex(neighbor)) {
                  this.inputExclusionGraph.addEdge(fakeMObj, neighbor);
                } else {
                  // The neighbor is not in the graph, it must be hosted by another mObject or divided.
                  excludeWithHostedNeighbor(fakeMObj, neighbor);
                }
              }
            }
          }

          indexPart++;
        }
      }
    }

    // 2 - Put the host Mobj back to its original size and exclusions
    // Backup the host size
    hostVertex.setPropertyValue(MemoryExclusionVertex.HOST_SIZE, hostVertex.getWeight());

    // Put it back to its real size
    hostVertex.setWeight(hostRealTokenRange.getValue().getValue().getLength());

    // Allocate it at the right place (replace old value)
    // (no empty space for host since their alignment with range start
    // is taken care of in meg update with script)
    this.memExNodeAllocation.put(hostVertex, offset + hostZeroIndexOffset);
    this.edgeAllocation.put(hostVertex.getEdge(), offset + hostZeroIndexOffset);
    hostVertex.setPropertyValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY, offset + hostZeroIndexOffset);

    // Get real neighbors
    final List<MemoryExclusionVertex> neighbors = hostVertex.getPropertyBean()
        .getValue(MemoryExclusionVertex.ADJACENT_VERTICES_BACKUP);

    // Remove all neighbors
    this.inputExclusionGraph.removeVertex(hostVertex);
    this.inputExclusionGraph.addVertex(hostVertex);

    // Put back the exclusions with all neighbors
    for (final MemoryExclusionVertex neighbor : neighbors) {
      // If the neighbor is not part of the same merge
      // operation
      if (!vertices.contains(neighbor)) {
        // Restore its old exclusions
        if (this.inputExclusionGraph.containsVertex(neighbor)) {
          this.inputExclusionGraph.addEdge(hostVertex, neighbor);
        } else {
          excludeWithHostedNeighbor(hostVertex, neighbor);
        }
      }
    }
  }

  /**
   * Add exclusion with a neighbor that is not in the {@link MemoryExclusionGraph}. The Neighbor is either a
   * {@link MemoryExclusionVertex} hosted by another {@link MemoryExclusionVertex} that is not yet allocated. Or the
   * neighbor is divided into parts.
   *
   * @param vertex
   *          The {@link MemoryExclusionVertex} to exclude with.
   * @param neighbor
   *          The {@link MemoryExclusionVertex} that is not yet in the {@link #inputExclusionGraph}.
   */
  private void excludeWithHostedNeighbor(final MemoryExclusionVertex vertex, final MemoryExclusionVertex neighbor) {
    // The neighbor is not in the graph, it must be
    // hosted by another mObject or divided.
    // Find the host(s)
    final List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> neighborHosts = neighbor.getPropertyBean()
        .getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);
    // Scan the hosted part(s) of the neighbor
    for (final Pair<MemoryExclusionVertex, Pair<Range, Range>> neighborHost : neighborHosts) {
      if (!this.memExNodeAllocation.containsKey(neighborHost.getKey())) {
        // if the host is not allocated, add an exclusion
        // with it
        this.inputExclusionGraph.addEdge(vertex, neighborHost.getKey());
      } else {
        // If the host is allocated, then the
        // neighbor was divided
        // => Add exclusions with the current
        // divided part

        // First, retrieve the fakeMobj of the part
        final int partIndex = neighborHosts.indexOf(neighborHost);
        final Function1<MemoryExclusionVertex,
            Boolean> function = it -> it.getSource().startsWith("part" + partIndex + "_");
        final List<
            MemoryExclusionVertex> fakeMobjs = neighbor.getPropertyBean().getValue(MemoryExclusionVertex.FAKE_MOBJECT);
        final MemoryExclusionVertex fakeMobj = IterableExtensions.findFirst(fakeMobjs, function);

        // Add the exclusion
        this.inputExclusionGraph.addEdge(vertex, fakeMobj);
      }
    }
  }

  /**
   * This method also checks that the {@link #alignment} constraint was fulfilled.
   *
   * @return The list of {@link MemoryExclusionVertex memory objects} that is not aligned. Empty list if allocation
   *         follow the rules.
   */
  public Map<MemoryExclusionVertex, Long> checkAlignment() {
    final Map<MemoryExclusionVertex, Long> unalignedObjects = new LinkedHashMap<>();

    // Check the alignment constraint
    if (this.alignment == -1) {
      return unalignedObjects;
    }

    for (final MemoryExclusionVertex memObj : this.inputExclusionGraph.vertexSet()) {
      final long offset = this.memExNodeAllocation.get(memObj);

      // Check if the buffer was merged as a result of memory script
      // execution.
      final boolean isMerged = memObj.getPropertyBean().getValue(MemoryExclusionVertex.EMPTY_SPACE_BEFORE) != null;

      // Check alignment of DAGEdge (that may involve subbuffers)
      // Do not perform the test for buffers involved in a merge
      // operation
      final DAGEdge edge = memObj.getEdge();
      if ((edge != null) && !isMerged) {
        final BufferAggregate buffers = edge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
        final List<
            Long> interBufferSpaces = memObj.getPropertyBean().getValue(MemoryExclusionVertex.INTER_BUFFER_SPACES);

        long internalOffset = 0;
        int i = 0;
        for (final BufferProperties properties : buffers) {
          final String dataType = properties.getDataType();
          final long typeSize = memObj.getScenario().getSimulationInfo().getDataTypeSizeInBit(dataType);

          if (interBufferSpaces != null) {
            internalOffset += interBufferSpaces.get(i);
          }
          i++;

          // Both data and fixed alignment must be aligned on
          // data typeSize
          // Check the fixed alignment
          if (((this.alignment >= 0) && (((internalOffset + offset) % typeSize) != 0))
              || ((this.alignment > 0) && (((internalOffset + offset) % this.alignment) != 0))) {
            unalignedObjects.put(memObj, offset);
            break;
          }

          internalOffset += typeSize * properties.getNbToken();
        }
      } else if (memObj.getSource().startsWith(MemoryExclusionGraph.FIFO_HEAD_PREFIX)) {
        // Check alignment of memory objects not associated with an edge.
        // In the current version, working memory of actor is not aligned since it has no declared type.
        // Process fifo memobjects here
        final Long typeSize = memObj.getPropertyBean().getValue(MemoryExclusionVertex.TYPE_SIZE);
        if ((this.alignment == 0) && ((offset % typeSize) != 0)) {
          unalignedObjects.put(memObj, offset);
        }

      }
    }
    return unalignedObjects;
  }

  /**
   * This method is responsible for checking the conformity of a memory allocation with the following constraints :
   * <li>An input buffer of an actor can not share a memory space with an output.
   * <li>As all actors are considered self-scheduled, buffers in parallel branches of the DAG can not share the same
   * memory space.
   *
   *
   * @return The list of conflicting memory elements. Empty list if allocation follow the rules.
   */
  public Map<MemoryExclusionVertex, Long> checkAllocation() {
    if (this.memExNodeAllocation == null) {
      throw new PreesmRuntimeException("Cannot check memory allocation because no allocation was performed.");
    }

    Map<MemoryExclusionVertex, Long> conflictingElements;
    conflictingElements = new LinkedHashMap<>();

    // Check that no edge of the exclusion graph is violated
    for (final DefaultEdge edge : this.inputExclusionGraph.edgeSet()) {
      final MemoryExclusionVertex source = this.inputExclusionGraph.getEdgeSource(edge);
      final MemoryExclusionVertex target = this.inputExclusionGraph.getEdgeTarget(edge);

      long sourceOffset;
      long targetOffset;

      // If an allocation was created only based on a memory exclusion
      // graph, the edge attribute of MemoryExclusionGraphNodes will be
      // null and
      // allocation table won't be valid.

      sourceOffset = this.memExNodeAllocation.get(source);
      targetOffset = this.memExNodeAllocation.get(target);

      // If the memory element share memory space
      if ((sourceOffset < (targetOffset + target.getWeight()))
          && ((sourceOffset + source.getWeight()) > targetOffset)) {
        conflictingElements.put(source, sourceOffset);
        conflictingElements.put(target, targetOffset);
      }
    }

    return conflictingElements;
  }

  /**
   * This method clear the attributes of the allocator from any trace of a previous allocation.
   */
  public void clear() {
    this.edgeAllocation.clear();
    this.fifoAllocation.clear();
    this.memExNodeAllocation.clear();
    this.inputExclusionGraph.setPropertyValue(MemoryExclusionGraph.ALLOCATED_MEMORY_SIZE, 0L);
    this.inputExclusionGraph.deallocate();
  }

  /**
   * Get the value of the {@link #alignment} attribute.
   *
   * <li><b>{@link #alignment}=-1</b>: Data should not be aligned.</li>
   * <li><b>{@link #alignment}= 0</b>: Data should be aligned according to its own type. For example, an array of int32
   * should begin at an offset (i.e. an address) that is a multiple of 4.</li>
   * <li><b>{@link #alignment}= N</b>: All data should be aligned to the given value N. This means that all arrays will
   * begin at an offset that is a multiple of N. It does not mean that ALL array elements are aligned on N, only the
   * first element.If an array has a data type different than 1, then the least common multiple of the two values is
   * used to align the data</li>
   *
   * @return the value of the {@link #alignment} attribute.
   */
  public long getAlignment() {
    return this.alignment;
  }

  /**
   * This function return an allocation of the edges of the SDF stored in graph attribute.
   *
   * <p>
   * An allocation is a map of edges associated to an integer which represents their offset in memory. Different
   * allocator policy exists (First Fit, Best Fit...)
   * </p>
   *
   * @return An allocation
   */
  public Map<DAGEdge, Long> getEdgeAllocation() {
    return this.edgeAllocation;
  }

  /**
   * This function return an allocation of the {@link MemoryExclusionVertex Memory Objects} of the
   * {@link MemoryExclusionGraph MeMex graph} stored in graph attribute.
   *
   * <p>
   * An allocation is a map of @link MemoryExclusionVertex Memory Objects} associated to an integer which represents
   * their offset in memory. Different allocator policy exists (First Fit, Best Fit...)
   * </p>
   *
   * @return An allocation
   */
  public Map<MemoryExclusionVertex, Long> getMemObjectAllocation() {
    return this.memExNodeAllocation;
  }

  /**
   * This method computes and return the size of the allocated memory.
   *
   * @return the memory Size
   */
  public long getMemorySizeInByte() {
    long memorySize = 0;

    // Use the memExNodeAllocation if available
    if (this.memExNodeAllocation != null) {
      for (final Entry<MemoryExclusionVertex, Long> entry : this.memExNodeAllocation.entrySet()) {
        final MemoryExclusionVertex vertex = entry.getKey();
        final Long value = entry.getValue();
        if ((value + vertex.getWeight()) > memorySize) {
          memorySize = value + vertex.getWeight();
        }
      }
      return (memorySize + 7L) / 8L;
    }

    if (!this.edgeAllocation.isEmpty()) {
      // Look for the maximum value of (offset + edge.size) in allocation map
      for (final Entry<DAGEdge, Long> entry : this.edgeAllocation.entrySet()) {
        final DAGEdge edge = entry.getKey();
        final Long value = entry.getValue();
        if ((value + edge.getWeight().longValue()) > memorySize) {
          memorySize = value + edge.getWeight().longValue();
        }
      }
      return (memorySize + 7L) / 8L;
    }
    return -1;
  }

  /**
   * Set the value of the {@link #alignment} attribute.
   *
   * @param alignment
   *          <li><b>{@link #alignment}=-1</b>: Data should not be aligned.</li>
   *          <li><b>{@link #alignment}= 0</b>: Data should be aligned according to its own type. For example, an array
   *          of int32 should begin at an offset (i.e. an address) that is a multiple of 4.</li>
   *          <li><b>{@link #alignment}= N</b>: All data should be aligned to the given value N. This means that all
   *          arrays will begin at an offset that is a multiple of N. It does not mean that ALL array elements are
   *          aligned on N, only the first element.If an array has a data type different than 1, then the least common
   *          multiple of the two values is used to align the data</li>
   */
  public void setAlignment(final long alignment) {
    this.alignment = alignment;
  }
}
