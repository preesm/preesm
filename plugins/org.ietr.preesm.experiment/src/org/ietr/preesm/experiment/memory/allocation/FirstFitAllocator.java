package org.ietr.preesm.experiment.memory.allocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;

import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;

/**
 * In this class, an adapted version of the first fit allocator is implemented.
 * As the lifetime of the memory elements is not known (because of the
 * self-timed assumption), adaptation had to be made. In particular, the order
 * in which the memory elements are considered had to be defined, as in the
 * original algorithm, this order is the scheduling order. The order chosen in
 * this implementation is a random order. Several random orders are tested, and
 * only the (best/mediane/average/worst)? is kept. Other orders have been
 * implemented : StableSet and LargestFirst.
 * 
 * @author kdesnos
 * 
 */
public class FirstFitAllocator extends OrderedAllocator {

	/**
	 * Constructor of the Allocator
	 * 
	 * @param graph
	 *            The graph to analyze
	 */
	public FirstFitAllocator(DirectedAcyclicGraph graph) {
		super(graph);
	}

	/**
	 * Constructor of the Allocator
	 * 
	 * @param memEx
	 *            The exclusion graph to analyze
	 */
	public FirstFitAllocator(MemoryExclusionGraph memEx) {
		super(memEx);
	}

	/**
	 * This method allocate the memory elements with the first fit algorithm and
	 * return the cost of the allocation.
	 * 
	 * @param vertexList
	 *            the ordered vertex list.
	 * @return the resulting allocation size.
	 */
	protected int allocateInOrder(List<MemoryExclusionVertex> vertexList) {
		// clear all previous allocation
		memExNodeAllocation = new HashMap<MemoryExclusionVertex, Integer>();
		edgeAllocation = new HashMap<DAGEdge, Integer>();

		// Allocate vertices in the list order
		for (MemoryExclusionVertex vertex : vertexList) {
			// Get vertex neighbors
			HashSet<MemoryExclusionVertex> neighbors = inputExclusionGraph
					.getAdjacentVertexOf(vertex);

			// The offset of the current vertex
			int offset = 0;

			// This boolean indicate that the offset chosen for the vertex is
			// compatible with all the neighbors that are already allocated.
			boolean validOffset = false;

			// Iterate until a valid offset is found (this offset will be the
			// smallest possible)
			while (!validOffset) {
				validOffset = true;
				for (MemoryExclusionVertex neighbor : neighbors) {
					if (memExNodeAllocation.containsKey(neighbor)) {
						// If an allocated neighbor overlap with the current
						// vertex allocaed at the current offset
						int neighborOffset = memExNodeAllocation.get(neighbor);
						if (neighborOffset < (offset + vertex.getWeight())
								&& (neighborOffset + neighbor.getWeight()) > offset) {
							validOffset = false;
							// Then, try the top of this neighbor as a new
							// offset
							offset = neighborOffset + neighbor.getWeight();
							break;
						}
					}
				}
			}
			// Allocate the vertex at the resulting offset
			memExNodeAllocation.put(vertex, offset);
			edgeAllocation.put(vertex.getEdge(), offset);
		}

		return getMemorySize();
	}
}
