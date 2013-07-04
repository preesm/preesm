package org.ietr.preesm.memory.allocation;

import java.util.HashMap;
import java.util.Map;

import net.sf.dftools.algorithm.model.PropertyBean;
import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;

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
	 * This method clear the attributes of the allocator from any trace of a
	 * previous allocation.
	 */
	public void clear() {
		edgeAllocation.clear();
		fifoAllocation.clear();
		workingMemAllocation.clear();
		memExNodeAllocation.clear();
		inputExclusionGraph.setPropertyValue(
				MemoryExclusionGraph.ALLOCATED_MEMORY_SIZE, 0);
	}

	/**
	 * Constructor of the MemoryAllocator
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

		inputExclusionGraph.setPropertyValue(
				MemoryExclusionGraph.DAG_EDGE_ALLOCATION, edgeAllocation);
		inputExclusionGraph.setPropertyValue(
				MemoryExclusionGraph.DAG_FIFO_ALLOCATION, fifoAllocation);
		inputExclusionGraph.setPropertyValue(
				MemoryExclusionGraph.WORKING_MEM_ALLOCATION, workingMemAllocation);
	}

	/**
	 * This method will perform the memory allocation of graph edges and store
	 * the result in the allocation HashMap.
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

		vertex.setPropertyValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY,
				offset);
		Integer size = (Integer) inputExclusionGraph.getPropertyBean()
				.getValue(MemoryExclusionGraph.ALLOCATED_MEMORY_SIZE,
						Integer.class);
		if (size == null || size < offset + vertex.getWeight()) {
			inputExclusionGraph.setPropertyValue(
					MemoryExclusionGraph.ALLOCATED_MEMORY_SIZE,
					offset + vertex.getWeight());
		}
	}

	/**
	 * This method is responsible for checking the conformity of a memory
	 * allocation with the following constraints : <li>An input buffer of an
	 * actor can not share a memory space with an output. <li>As all actors are
	 * considered self-scheduled, buffers in parallel branches of the DAG can
	 * not share the same memory space.
	 * 
	 * @return The list of conflicting memory elements. Empty list if allocation
	 *         follow the rules.
	 */
	public final HashMap<MemoryExclusionVertex, Integer> checkAllocation() {
		HashMap<MemoryExclusionVertex, Integer> conflictingElements;
		conflictingElements = new HashMap<MemoryExclusionVertex, Integer>();

		// Check that no edge of the exclusion graph is violated
		for (DefaultEdge edge : inputExclusionGraph.edgeSet()) {
			MemoryExclusionVertex source = inputExclusionGraph
					.getEdgeSource(edge);
			MemoryExclusionVertex target = inputExclusionGraph
					.getEdgeTarget(edge);

			int sourceOffset;
			int targetOffset;

			// If an allocation was created only based on a memory exclusion
			// graph, the edge attribute of MemoryExclusionGraphNodes will be
			// null and
			// allocation table won't be valid.
			if (memExNodeAllocation != null) {
				sourceOffset = memExNodeAllocation.get(source);
				targetOffset = memExNodeAllocation.get(target);
			} else {
				sourceOffset = edgeAllocation.get(source.getEdge());
				targetOffset = edgeAllocation.get(target.getEdge());
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
					memorySize = memExNodeAllocation.get(vertex)
							+ vertex.getWeight();
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
						memorySize = edgeAllocation.get(edge)
								+ edge.getWeight().intValue();
					}
				}
			} catch (InvalidExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return memorySize;
		}
		return -1;
	}
}
