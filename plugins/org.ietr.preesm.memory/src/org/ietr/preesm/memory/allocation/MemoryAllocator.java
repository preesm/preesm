/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2016)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2014)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/

package org.ietr.preesm.memory.allocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.types.BufferAggregate;
import org.ietr.preesm.core.types.BufferProperties;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.memory.script.Range;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;
import org.jgrapht.graph.DefaultEdge;

/**
 * This class is both an interface and toolbox class for memory allocator.
 * 
 * @author kdesnos
 */
public abstract class MemoryAllocator {

	/**
	 * This method scan the {@link MemoryExclusionVertex memory objects} of an
	 * input {@link MemoryExclusionGraph} in order to align its internal
	 * subbuffers.<br>
	 * <br>
	 * Since a {@link DAGEdge} might be the result of an aggregation of several
	 * {@link SDFEdge} from the original {@link SDFGraph}, a
	 * {@link MemoryExclusionVertex memory object} might "contain" several
	 * subbuffer, each corresponding to one of these aggregated edges. The
	 * purpose of this method is to ensure that all subbuffers are correctly
	 * aligned in memory when allocating the {@link MemoryExclusionGraph}. To do
	 * so, each {@link MemoryExclusionVertex} is processed so that each
	 * subbuffer is given an "internal" offset in the memory object that fulfill
	 * its alignment constraint. The size of the {@link MemoryExclusionVertex}
	 * might be modified by this method. Note that we do not try here to
	 * optimize the space taken by each memory object by reordering the
	 * subbuffers.
	 * 
	 * @param meg
	 *            The {@link MemoryExclusionGraph} whose
	 *            {@link MemoryExclusionVertex} must be aligned. The size of the
	 *            {@link MemoryExclusionVertex} might be modified by this
	 *            method.
	 * @param alignment
	 *            <li><b>{@link #alignment}=-1</b>: Data should not be aligned.
	 *            </li>
	 *            <li><b>{@link #alignment}= 0</b>: Data should be aligned
	 *            according to its own type. For example, an array of int32
	 *            should begin at an offset (i.e. an address) that is a multiple
	 *            of 4.</li>
	 *            <li><b>{@link #alignment}= N</b>: All data should be aligned
	 *            to the given value N. This means that all arrays will begin at
	 *            an offset that is a multiple of N. It does not mean that ALL
	 *            array elements are aligned on N, only the first element. If an
	 *            array has a data type different than 1, then the least common
	 *            multiple of the two values is used to align the data.</li>
	 * @return the total amount of memory added to the
	 *         {@link MemoryExclusionVertex}
	 */
	@SuppressWarnings("unchecked")
	public static int alignSubBuffers(MemoryExclusionGraph meg, int alignment) {
		int addedSpace = 0;
		if (alignment != -1) {

			// Build a list of all MObject of the graph, including merged ones
			Set<MemoryExclusionVertex> allMObjects = new HashSet<MemoryExclusionVertex>();
			allMObjects.addAll(meg.vertexSet());
			// Include merged Mobjects (if any)
			Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hostMap = (Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>) meg
					.getPropertyBean().getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
			if (hostMap != null) {
				for (Set<MemoryExclusionVertex> mergedMOBjects : hostMap.values()) {
					allMObjects.addAll(mergedMOBjects);
				}
			}

			// Scan the vertices of the graph
			for (MemoryExclusionVertex memObj : allMObjects) {
				// Check alignment of DAGEdge (that may involve subbuffers)
				// other memory objects can be ignored in this method.
				DAGEdge edge = memObj.getEdge();
				if (edge != null) {

					BufferAggregate buffers = (BufferAggregate) edge.getPropertyBean()
							.getValue(BufferAggregate.propertyBeanName);
					Iterator<BufferProperties> iter = buffers.iterator();

					List<Integer> interBufferSpaces = new ArrayList<Integer>();
					int largestTypeSize = 1;
					int internalOffset = 0; // In Bytes
					while (iter.hasNext()) {
						BufferProperties properties = iter.next();
						String dataType = properties.getDataType();
						DataType type = MemoryExclusionVertex._dataTypes.get(dataType);
						int typeSize;
						// A proper type was not set for the considered edge
						if (type == null) {
							WorkflowLogger.getLogger().log(Level.SEVERE,
									"No valid data type was found on an edge between actors "
											+ edge.getSource().getName() + " and " + edge.getTarget().getName()
											+ ".\nCheck the edge in the graph editor and the declared types in the scenario.");
							typeSize = 1;
						} else {
							typeSize = type.getSize();
						}
						largestTypeSize = Math.max(typeSize, largestTypeSize);
						int interSpace = 0;

						// Data alignment case
						// If the subbuffer is not aligned, add an interspace.
						if (alignment == 0 && internalOffset % typeSize != 0) {
							interSpace = typeSize - (internalOffset % typeSize);
						}

						// Fixed alignment
						// If the subbuffer is not aligned, add an interspace.
						if (alignment > 0) {
							int align = lcm(typeSize, alignment);
							if (internalOffset % align != 0) {
								interSpace = align - (internalOffset % align);
							}
						}

						interBufferSpaces.add(interSpace);
						internalOffset += interSpace + typeSize * properties.getSize();
					}

					// Update the size of the memObject and add the interbuffer
					// space if it does not contain with 0.
					if (internalOffset - memObj.getWeight() > 0) {
						memObj.setPropertyValue(MemoryExclusionVertex.INTER_BUFFER_SPACES, interBufferSpaces);
						addedSpace += internalOffset - memObj.getWeight();
						memObj.setWeight(internalOffset);
					}
					// Backup the largest typeSize contained in the aggregate.
					// This information will be used to align the memObject
					// during allocation
					memObj.setPropertyValue(MemoryExclusionVertex.TYPE_SIZE, largestTypeSize);
				}
			}
		}
		return addedSpace;
	}

	/**
	 * Get the greatest common divisor.
	 * 
	 * @param a
	 *            an int
	 * @param b
	 *            an int > to 0
	 * @return the gcd of the two numbers or a if b==0.
	 */
	private static int gcd(int a, int b) {
		while (b > 0) {
			int temp = b;
			b = a % b; // % is remainder
			a = temp;
		}
		return a;
	}

	/**
	 * Get the least common multiple.
	 */
	protected static int lcm(int a, int b) {
		return a * (b / gcd(a, b));
	}

	/**
	 * This value is used to configure how allocated memory objects should be
	 * aligned in memory.<br>
	 * The following configurations are valid:<br>
	 * 
	 * <li><b>{@link #alignment}=-1</b>: Data should not be aligned.</li>
	 * <li><b>{@link #alignment}= 0</b>: Data should be aligned according to its
	 * own type. For example, an array of int32 should begin at an offset (i.e.
	 * an address) that is a multiple of 4.</li>
	 * <li><b>{@link #alignment}= N</b>: All data should be aligned to the given
	 * value N. This means that all arrays will begin at an offset that is a
	 * multiple of N. It does not mean that ALL array elements are aligned on N,
	 * only the first element.</li>
	 */
	protected int alignment;

	/**
	 * An allocation is a map of edges associated to an integer which represents
	 * their offset in a monolithic memory.<br>
	 * <br>
	 * <table border>
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
	protected HashMap<DAGEdge, Integer> edgeAllocation;

	/**
	 * An allocation is a map of fifo associated to an integer which represents
	 * their offset in a monolithic memory.<br>
	 * <br>
	 * <table border>
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
	protected HashMap<MemoryExclusionVertex, Integer> fifoAllocation;

	/**
	 * An allocation is a map of actor working memory associated to an integer
	 * which represents their offset in a monolithic memory.<br>
	 * <br>
	 * <table border>
	 * <tr>
	 * <td>MObject<sub>size</sub></td>
	 * <td>Offset</td>
	 * </tr>
	 * <tr>
	 * <td>A<sub>100</sub></td>
	 * <td>0</td>
	 * </tr>
	 * <tr>
	 * <td>B<sub>200</sub></td>
	 * <td>100</td>
	 * </tr>
	 * </table>
	 */
	protected HashMap<MemoryExclusionVertex, Integer> workingMemAllocation;

	/**
	 * An allocation is a map of {@link MemoryExclusionVertex memory objects}
	 * associated to an integer which represents their offset in a monolithic
	 * memory.<br>
	 * <br>
	 * <table border>
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
	protected HashMap<MemoryExclusionVertex, Integer> memExNodeAllocation;

	protected MemoryExclusionGraph inputExclusionGraph;

	/**
	 * Constructor of the MemoryAllocator.
	 * 
	 * Default {@link MemoryAllocator} has no {@link #alignment}.
	 * 
	 * @param memEx
	 *            The exclusion graph to analyze
	 */
	protected MemoryAllocator(MemoryExclusionGraph memEx) {
		edgeAllocation = new HashMap<DAGEdge, Integer>();
		fifoAllocation = new HashMap<MemoryExclusionVertex, Integer>();
		workingMemAllocation = new HashMap<MemoryExclusionVertex, Integer>();

		memExNodeAllocation = new HashMap<MemoryExclusionVertex, Integer>();
		inputExclusionGraph = memEx;

		inputExclusionGraph.setPropertyValue(MemoryExclusionGraph.DAG_EDGE_ALLOCATION, edgeAllocation);
		inputExclusionGraph.setPropertyValue(MemoryExclusionGraph.DAG_FIFO_ALLOCATION, fifoAllocation);
		inputExclusionGraph.setPropertyValue(MemoryExclusionGraph.WORKING_MEM_ALLOCATION, workingMemAllocation);
		alignment = -1;
	}

	/**
	 * This method will perform the memory allocation of graph edges and store
	 * the result in the allocation HashMap.
	 * 
	 * This method does not call {@link #alignSubBuffers(MemoryExclusionGraph)}.
	 * To ensure a correct alignment, the
	 * {@link #alignSubBuffers(MemoryExclusionGraph)} method must be called
	 * before the {@link #allocate()} method. The {@link #inputExclusionGraph}
	 * might be modified by calling this function. (new
	 * {@link MemoryExclusionVertex} might be added because of
	 * HostMemoryObjects). To put the {@link #inputExclusionGraph} back in its
	 * original state, call the deallocate method.
	 */
	public abstract void allocate();

	/**
	 * Method used to allocate a {@link MemoryExclusionVertex memory object} in
	 * memory at the given offset. The method allocates both the
	 * {@link MemoryExclusionVertex} in the {@link #memExNodeAllocation} table
	 * and its corresponding {@link DAGEdge} in the {@link #edgeAllocation}
	 * table. It also updates the {@link PropertyBean} of the
	 * {@link MemoryExclusionVertex memObject} with the allocation information
	 * (i.e. the offset).
	 * 
	 * @param vertex
	 *            the allocated {@link MemoryExclusionVertex memory object}
	 * @param offset
	 *            the memory offset at which the {@link MemoryExclusionVertex
	 *            memory object} is allocated.
	 */
	@SuppressWarnings("unchecked")
	protected void allocateMemoryObject(MemoryExclusionVertex vertex, int offset) {
		// TODO change the return type from void to boolean.
		// The returned value will be used to tell if the allocation
		// is authorized (i.e. if there is no conflict with already allocated
		// memObjects).
		// A performance check should be performed when implementing this change
		// in order to make sure that this does not kill the perf.

		memExNodeAllocation.put(vertex, offset);

		if (vertex.getEdge() != null) {
			edgeAllocation.put(vertex.getEdge(), offset);
		} else if (vertex.getSink().equals(vertex.getSource())) {
			workingMemAllocation.put(vertex, offset);
		} else if (vertex.getSource().startsWith("FIFO_")) {
			fifoAllocation.put(vertex, offset);
		}

		vertex.setPropertyValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY, offset);
		Integer size = (Integer) inputExclusionGraph.getPropertyBean()
				.getValue(MemoryExclusionGraph.ALLOCATED_MEMORY_SIZE, Integer.class);
		if (size == null || size < offset + vertex.getWeight()) {
			inputExclusionGraph.setPropertyValue(MemoryExclusionGraph.ALLOCATED_MEMORY_SIZE,
					offset + vertex.getWeight());
		}

		// If the allocated memory object is the result from a merge
		// do the specific processing.
		Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hostMap = (Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>) inputExclusionGraph
				.getPropertyBean().getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
		if (hostMap != null && hostMap.containsKey(vertex)) {
			allocateHostMemoryObject(vertex, hostMap.get(vertex), offset);
		}
	}

	/**
	 * Special processing for {@link MemoryExclusionVertex memory objects}
	 * resulting from memory script merges.<br>
	 * Put back all hosted {@link MemoryExclusionVertex} in the
	 * {@link MemoryExclusionVertex} with their original exclusions (i.e. their
	 * exclusion before script application). Put the host
	 * {@link MemoryExclusionVertex} back to its original size give it its
	 * original exclusions.
	 * 
	 * @param hostVertex
	 *            the "host" {@link MemoryExclusionVertex}, i.e. the
	 *            {@link MemoryExclusionVertex} that "contains" several other
	 *            {@link MemoryExclusionVertex memory objects} from the original
	 *            {@link MemoryExclusionGraph}.
	 * 
	 * @param vertices
	 *            the {@link Set} of {@link MemoryExclusionVertex} contained in
	 *            the "host".
	 * @param offset
	 *            the offset of the hostVertex
	 */
	protected void allocateHostMemoryObject(MemoryExclusionVertex hostVertex, Set<MemoryExclusionVertex> vertices,
			int offset) {
		// 1 - Put back all hosted mobj in the meg (with their exclusions)
		// 2 - Put the host Mobj back to its original size and exclusions
		@SuppressWarnings("unchecked")
		Pair<MemoryExclusionVertex, Pair<Range, Range>> hostRealTokenRange = ((List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>) hostVertex
				.getPropertyBean().getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY)).get(0);

		int hostZeroIndexOffset = hostRealTokenRange.getValue().getValue().getStart();

		// 1- Put back all hosted mobj in the meg (with their exclusions)
		{
			// For each vertex of the group
			for (MemoryExclusionVertex vertex : vertices) {

				// Get its offset within the host vertex
				@SuppressWarnings("unchecked")
				List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> realTokenRange = (List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>) vertex
						.getPropertyBean().getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);

				@SuppressWarnings("unchecked")
				List<MemoryExclusionVertex> neighbors = (List<MemoryExclusionVertex>) vertex.getPropertyBean()
						.getValue(MemoryExclusionVertex.ADJACENT_VERTICES_BACKUP);

				// If the Mobject is not splitted
				if (realTokenRange.size() == 1) {
					int startOffset = realTokenRange.get(0).getValue().getValue().getStart();

					// Compute the space that must be left empty before the
					// allocated space to ensure that the MObject has its own
					// cache line.
					int emptySpace = 0;
					if (alignment > 0 && (offset + startOffset + hostZeroIndexOffset) % alignment != 0) {
						emptySpace = (offset + startOffset + hostZeroIndexOffset) % alignment;
					}
					vertex.setPropertyValue(MemoryExclusionVertex.EMPTY_SPACE_BEFORE, emptySpace);
					// Enlarge the weight of the vertex to include the empty
					// space
					vertex.setWeight(vertex.getWeight() + emptySpace);

					// Allocate it at the right place
					memExNodeAllocation.put(vertex, offset + startOffset + hostZeroIndexOffset - emptySpace);
					edgeAllocation.put(vertex.getEdge(), offset + startOffset + hostZeroIndexOffset - emptySpace);
					vertex.setPropertyValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY,
							offset + startOffset + hostZeroIndexOffset - emptySpace);

					// Put the MObject Back in the MEG
					inputExclusionGraph.addVertex(vertex);

					// Put back the exclusions with all neighbors
					for (MemoryExclusionVertex neighbor : neighbors) {
						// If the neighbor is not part of the same merge
						// operation
						if (!vertices.contains(neighbor) && neighbor != hostVertex) {
							// Restore its old exclusions
							if (inputExclusionGraph.containsVertex(neighbor)) {
								inputExclusionGraph.addEdge(vertex, neighbor);
							} else {
								excludeWithHostedNeighbor(vertex, neighbor);
							}
						}
					}

				} else {
					// If the Mobject is splitted
					// Null buffer since the memory of this MObj is no longer
					// contiguous
					vertex.setWeight(0);
					// Allocate it at index -1
					memExNodeAllocation.put(vertex, -1);
					edgeAllocation.put(vertex.getEdge(), -1);
					vertex.setPropertyValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY, -1);
					inputExclusionGraph.addVertex(vertex);
					vertex.setPropertyValue(MemoryExclusionVertex.EMPTY_SPACE_BEFORE, -1);

					// Put a fake MObject in the MEG for each subrange
					@SuppressWarnings("unchecked")
					List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> realRanges = (List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>) vertex
							.getPropertyBean().getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);

					int indexPart = 0;
					// For each contiguous range
					for (Pair<MemoryExclusionVertex, Pair<Range, Range>> realRange : realRanges) {
						// If the host of this subrange is the current host
						// vertex
						// (else do nothing here for this subrange)
						if (hostVertex == realRange.getKey()) {

							// Get host range
							Range hostRange = realRange.getValue().getValue();
							int startOffset = hostRange.getStart();

							// Create new fake Mobj
							MemoryExclusionVertex fakeMObj = new MemoryExclusionVertex(
									"part" + indexPart + "_" + vertex.getSource(), vertex.getSink(),
									hostRange.getLength());

							// Compute the space that must be left empty before
							// the
							// allocated space to ensure that the MObject has
							// its own
							// cache line.
							int emptySpace = 0;
							if (alignment > 0 && (offset + startOffset + hostZeroIndexOffset) % alignment != 0) {
								emptySpace = (offset + startOffset + hostZeroIndexOffset) % alignment;
							}
							fakeMObj.setPropertyValue(MemoryExclusionVertex.EMPTY_SPACE_BEFORE, emptySpace);
							// Enlarge the weight of the vertex to include the
							// empty space
							fakeMObj.setWeight(fakeMObj.getWeight() + emptySpace);

							// Allocate the fake Mobject
							// (in order to be considered when checking for
							// exclusions)

							memExNodeAllocation.put(fakeMObj, offset + startOffset + hostZeroIndexOffset - emptySpace);
							fakeMObj.setPropertyValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY,
									offset + startOffset + hostZeroIndexOffset - emptySpace);

							// Put the Fake MObject in the MEG
							inputExclusionGraph.addVertex(fakeMObj);

							// Backup the fakeMobj in the original vertex (for
							// deallocation purpose)
							@SuppressWarnings("unchecked")
							List<MemoryExclusionVertex> fakeMobjects = (List<MemoryExclusionVertex>) vertex
									.getPropertyBean().getValue(MemoryExclusionVertex.FAKE_MOBJECT);
							if (fakeMobjects == null) {
								fakeMobjects = new ArrayList<MemoryExclusionVertex>();
								vertex.setPropertyValue(MemoryExclusionVertex.FAKE_MOBJECT, fakeMobjects);
							}
							fakeMobjects.add(fakeMObj);

							// Put back the exclusions with all neighbors
							for (MemoryExclusionVertex neighbor : neighbors) {
								// If the neighbor is not part of the same merge
								// operation
								if (!vertices.contains(neighbor) && neighbor != hostVertex) {
									// Restore its old exclusions
									if (inputExclusionGraph.containsVertex(neighbor)) {
										inputExclusionGraph.addEdge(fakeMObj, neighbor);
									} else {
										// The neighbor is not in the graph, it
										// must be
										// hosted by another mObject or divided.
										excludeWithHostedNeighbor(fakeMObj, neighbor);
									}
								}
							}
						}

						indexPart++;
					}
				}
			}
		}

		// 2 - Put the host Mobj back to its original size and exclusions
		{
			// Backup the host size
			hostVertex.setPropertyValue(MemoryExclusionVertex.HOST_SIZE, hostVertex.getWeight());

			// Put it back to its real size
			hostVertex.setWeight(hostRealTokenRange.getValue().getValue().getLength());

			// Allocate it at the right place (replace old value)
			// (no empty space for host since their alignment with range start
			// is taken care of in meg update with script)
			memExNodeAllocation.put(hostVertex, offset + hostZeroIndexOffset);
			edgeAllocation.put(hostVertex.getEdge(), offset + hostZeroIndexOffset);
			hostVertex.setPropertyValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY, offset + hostZeroIndexOffset);

			// Get real neighbors
			@SuppressWarnings("unchecked")
			List<MemoryExclusionVertex> neighbors = (List<MemoryExclusionVertex>) hostVertex.getPropertyBean()
					.getValue(MemoryExclusionVertex.ADJACENT_VERTICES_BACKUP);

			// Remove all neighbors
			inputExclusionGraph.removeVertex(hostVertex);
			inputExclusionGraph.addVertex(hostVertex);

			// Put back the exclusions with all neighbors
			for (MemoryExclusionVertex neighbor : neighbors) {
				// If the neighbor is not part of the same merge
				// operation
				if (!vertices.contains(neighbor)) {
					// Restore its old exclusions
					if (inputExclusionGraph.containsVertex(neighbor)) {
						inputExclusionGraph.addEdge(hostVertex, neighbor);
					} else {
						excludeWithHostedNeighbor(hostVertex, neighbor);
					}
				}
			}
		}
	}

	/**
	 * Add exclusion with a neighbor that is not in the
	 * {@link MemoryExclusionGraph}. The Neighbor is either a
	 * {@link MemoryExclusionVertex} hosted by another
	 * {@link MemoryExclusionVertex} that is not yet allocated. Or the neighbor
	 * is divided into parts.
	 * 
	 * @param vertex
	 *            The {@link MemoryExclusionVertex} to exclude with.
	 * @param neighbor
	 *            The {@link MemoryExclusionVertex} that is not yet in the
	 *            {@link #inputExclusionGraph}.
	 */
	protected void excludeWithHostedNeighbor(MemoryExclusionVertex vertex, MemoryExclusionVertex neighbor) {
		// The neighbor is not in the graph, it must be
		// hosted by another mObject or divided.
		// Find the host(s)
		@SuppressWarnings("unchecked")
		List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> neighborHosts = (List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>) neighbor
				.getPropertyBean().getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);
		// Scan the hosted part(s) of the neighbor
		for (Pair<MemoryExclusionVertex, Pair<Range, Range>> neighborHost : neighborHosts) {
			if (!memExNodeAllocation.containsKey(neighborHost.getKey())) {
				// if the host is not allocated, add an exclusion
				// with it
				inputExclusionGraph.addEdge(vertex, neighborHost.getKey());
			} else {
				// If the host is allocated, then the
				// neighbor was divided
				// => Add exclusions with the current
				// divided part

				// First, retrieve the fakeMobj of the part
				final int partIndex = neighborHosts.indexOf(neighborHost);
				Function1<MemoryExclusionVertex, Boolean> function = new Function1<MemoryExclusionVertex, Boolean>() {
					@Override
					public Boolean apply(final MemoryExclusionVertex it) {
						return it.getSource().startsWith("part" + partIndex + "_");
					}
				};
				@SuppressWarnings("unchecked")
				List<MemoryExclusionVertex> fakeMobjs = (List<MemoryExclusionVertex>) neighbor.getPropertyBean()
						.getValue(MemoryExclusionVertex.FAKE_MOBJECT);
				MemoryExclusionVertex fakeMobj = IterableExtensions.findFirst(fakeMobjs, function);

				// Add the exclusion
				inputExclusionGraph.addEdge(vertex, fakeMobj);
			}
		}
	}

	/**
	 * This method also checks that the {@link #alignment} constraint was
	 * fulfilled.
	 * 
	 * @return The list of {@link MemoryExclusionVertex memory objects} that is
	 *         not aligned. Empty list if allocation follow the rules.
	 */
	public Map<MemoryExclusionVertex, Integer> checkAlignment() {
		Map<MemoryExclusionVertex, Integer> unalignedObjects = new HashMap<MemoryExclusionVertex, Integer>();

		// Check the alignment constraint
		if (alignment != -1) {
			for (MemoryExclusionVertex memObj : inputExclusionGraph.vertexSet()) {
				int offset = memExNodeAllocation.get(memObj);

				// Check if the buffer was merged as a result of memory script
				// execution.
				boolean isMerged = memObj.getPropertyBean().getValue(MemoryExclusionVertex.EMPTY_SPACE_BEFORE) != null;

				// Check alignment of DAGEdge (that may involve subbuffers)
				// Do not perform the test for buffers involved in a merge
				// operation
				DAGEdge edge = memObj.getEdge();
				if (edge != null && !isMerged) {
					BufferAggregate buffers = (BufferAggregate) edge.getPropertyBean()
							.getValue(BufferAggregate.propertyBeanName);
					Iterator<BufferProperties> iter = buffers.iterator();

					@SuppressWarnings("unchecked")
					List<Integer> interBufferSpaces = (List<Integer>) memObj.getPropertyBean()
							.getValue(MemoryExclusionVertex.INTER_BUFFER_SPACES, List.class);

					int internalOffset = 0;
					int i = 0;
					while (iter.hasNext()) {
						BufferProperties properties = iter.next();
						String dataType = properties.getDataType();
						DataType type = MemoryExclusionVertex._dataTypes.get(dataType);
						int typeSize = type.getSize();

						if (interBufferSpaces != null) {
							internalOffset += interBufferSpaces.get(i);
						}
						i++;

						// Both data and fixed alignment must be aligned on
						// data typeSize
						if (alignment >= 0 && (internalOffset + offset) % typeSize != 0) {
							unalignedObjects.put(memObj, offset);
							break;
						}

						// Check the fixed alignment
						if (alignment > 0 && (internalOffset + offset) % alignment != 0) {
							unalignedObjects.put(memObj, offset);
							break;
						}

						internalOffset += typeSize * properties.getSize();

					}
				} else {
					// Check alignment of memory objects not associated with an
					// edge.
					// In the current version, working memory of actor is not
					// aligned since it has
					// no declared type.
					// Process fifo memobjects here
					if (memObj.getSource().startsWith("FIFO_")) {
						Integer typeSize = (Integer) memObj.getPropertyBean().getValue(MemoryExclusionVertex.TYPE_SIZE,
								Integer.class);
						if (alignment == 0 && offset % typeSize != 0) {
							unalignedObjects.put(memObj, offset);
						}

					}

				}
			}
		}
		return unalignedObjects;
	}

	/**
	 * This method is responsible for checking the conformity of a memory
	 * allocation with the following constraints :
	 * <li>An input buffer of an actor can not share a memory space with an
	 * output.
	 * <li>As all actors are considered self-scheduled, buffers in parallel
	 * branches of the DAG can not share the same memory space.
	 * 
	 * 
	 * @return The list of conflicting memory elements. Empty list if allocation
	 *         follow the rules.
	 */
	public HashMap<MemoryExclusionVertex, Integer> checkAllocation() {
		if (memExNodeAllocation == null) {
			throw new RuntimeException("Cannot check memory allocation because no allocation was performed.");
		}

		HashMap<MemoryExclusionVertex, Integer> conflictingElements;
		conflictingElements = new HashMap<MemoryExclusionVertex, Integer>();

		// Check that no edge of the exclusion graph is violated
		for (DefaultEdge edge : inputExclusionGraph.edgeSet()) {
			MemoryExclusionVertex source = inputExclusionGraph.getEdgeSource(edge);
			MemoryExclusionVertex target = inputExclusionGraph.getEdgeTarget(edge);

			Integer sourceOffset;
			Integer targetOffset;

			// If an allocation was created only based on a memory exclusion
			// graph, the edge attribute of MemoryExclusionGraphNodes will be
			// null and
			// allocation table won't be valid.

			sourceOffset = memExNodeAllocation.get(source);
			targetOffset = memExNodeAllocation.get(target);

			if (sourceOffset == null) {
				throw new RuntimeException(
						"Allocation check failed because " + source + " memory object was not allocated.");
			}
			if (targetOffset == null) {
				throw new RuntimeException(
						"Allocation check failed because " + target + " memory object was not allocated.");
			}
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
	 * This method clear the attributes of the allocator from any trace of a
	 * previous allocation.
	 */
	public void clear() {
		edgeAllocation.clear();
		fifoAllocation.clear();
		workingMemAllocation.clear();
		memExNodeAllocation.clear();
		inputExclusionGraph.setPropertyValue(MemoryExclusionGraph.ALLOCATED_MEMORY_SIZE, 0);
		inputExclusionGraph.deallocate();
	}

	/**
	 * Get the value of the {@link #alignment} attribute.
	 * 
	 * <li><b>{@link #alignment}=-1</b>: Data should not be aligned.</li>
	 * <li><b>{@link #alignment}= 0</b>: Data should be aligned according to its
	 * own type. For example, an array of int32 should begin at an offset (i.e.
	 * an address) that is a multiple of 4.</li>
	 * <li><b>{@link #alignment}= N</b>: All data should be aligned to the given
	 * value N. This means that all arrays will begin at an offset that is a
	 * multiple of N. It does not mean that ALL array elements are aligned on N,
	 * only the first element.If an array has a data type different than 1, then
	 * the least common multiple of the two values is used to align the data
	 * </li>
	 * 
	 * @return the value of the {@link #alignment} attribute.
	 */
	public int getAlignment() {
		return alignment;
	}

	/**
	 * This function return an allocation of the edges of the SDF stored in
	 * graph attribute.
	 * 
	 * An allocation is a map of edges associated to an integer which represents
	 * their offset in memory. Different allocator policy exists (First Fit,
	 * Best Fit...)
	 * 
	 * @return An allocation
	 */
	public Map<DAGEdge, Integer> getEdgeAllocation() {
		return edgeAllocation;
	}

	/**
	 * This function return an allocation of the {@link MemoryExclusionVertex
	 * Memory Objects} of the {@link MemoryExclusionGraph MeMex graph} stored in
	 * graph attribute.
	 * 
	 * An allocation is a map of @link MemoryExclusionVertex Memory Objects}
	 * associated to an integer which represents their offset in memory.
	 * Different allocator policy exists (First Fit, Best Fit...)
	 * 
	 * @return An allocation
	 */
	public Map<MemoryExclusionVertex, Integer> getMemObjectAllocation() {
		return memExNodeAllocation;
	}

	/**
	 * This method computes and return the size of the allocated memory.
	 * 
	 * @return the memory Size
	 */
	public int getMemorySize() {
		int memorySize = 0;

		// Use the memExNodeAllocation if available
		if (memExNodeAllocation != null) {
			for (MemoryExclusionVertex vertex : memExNodeAllocation.keySet()) {
				if ((memExNodeAllocation.get(vertex) + vertex.getWeight()) > memorySize) {
					memorySize = memExNodeAllocation.get(vertex) + vertex.getWeight();
				}
			}
			return memorySize;
		}

		if (!edgeAllocation.isEmpty()) {
			try {
				// Look for the maximum value of (offset + edge.size) in
				// allocation map
				for (DAGEdge edge : edgeAllocation.keySet()) {
					if ((edgeAllocation.get(edge) + edge.getWeight().intValue()) > memorySize) {
						memorySize = edgeAllocation.get(edge) + edge.getWeight().intValue();
					}
				}
			} catch (InvalidExpressionException e) {
				e.printStackTrace();
			}
			return memorySize;
		}
		return -1;
	}

	/**
	 * Set the value of the {@link #alignment} attribute.
	 * 
	 * @param alignment
	 *            <li><b>{@link #alignment}=-1</b>: Data should not be aligned.
	 *            </li>
	 *            <li><b>{@link #alignment}= 0</b>: Data should be aligned
	 *            according to its own type. For example, an array of int32
	 *            should begin at an offset (i.e. an address) that is a multiple
	 *            of 4.</li>
	 *            <li><b>{@link #alignment}= N</b>: All data should be aligned
	 *            to the given value N. This means that all arrays will begin at
	 *            an offset that is a multiple of N. It does not mean that ALL
	 *            array elements are aligned on N, only the first element.If an
	 *            array has a data type different than 1, then the least common
	 *            multiple of the two values is used to align the data</li>
	 */
	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}
}
