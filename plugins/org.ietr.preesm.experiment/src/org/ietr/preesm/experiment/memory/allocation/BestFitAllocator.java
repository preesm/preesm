package org.ietr.preesm.experiment.memory.allocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;

import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;

/**
 * In this class, an adapted version of the best fit allocator is implemented.
 * As the lifetime of the memory elements is not known (because of the
 * self-timed assumption), adaptation had to be made. In particular, the order
 * in which the memory elements are considered had to be defined. In the
 * original algorithm, this order is the scheduling order. The order chosen in
 * this implementation is a random order. Several random orders are tested, and
 * only the (best/mediane/average/worst) is kept. Other orders have been
 * implemented : StableSet and LargestFirst.
 * 
 * @author kdesnos
 * 
 */
public class BestFitAllocator extends OrderedAllocator {

	/**
	 * Constructor of the allocator
	 * 
	 * @param graph
	 *            the graph whose edges are to allocate
	 */
	public BestFitAllocator(DirectedAcyclicGraph graph) {
		super(graph);
	}

	/**
	 * Constructor of the allocator
	 * 
	 * @param memEx
	 *            The exclusion graph whose vertices are to allocate
	 */
	public BestFitAllocator(MemoryExclusionGraph memEx) {
		super(memEx);
	}

	/**
	 * This method allocate the memory elements with the best fit algorithm and
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

			// Construct two lists that contains the exclusion ranges in memory
			ArrayList<Integer> excludeFrom = new ArrayList<Integer>();
			ArrayList<Integer> excludeTo = new ArrayList<Integer>();
			for (MemoryExclusionVertex neighbor : neighbors) {
				if (memExNodeAllocation.containsKey(neighbor)) {
					int neighborOffset = memExNodeAllocation.get(neighbor);
					excludeFrom.add(neighborOffset);
					excludeTo.add(neighborOffset + neighbor.getWeight());
				}
			}
			Collections.sort(excludeFrom);
			Collections.sort(excludeTo);

			int bestFitOffset = -1;
			int freeFrom = 0; // Where the last exclusion ended

			// Look for best fit only if there are exclusions. Else, simply
			// allocate at 0.
			if (!excludeFrom.isEmpty()) {
				// Look for free spaces between the exclusion ranges.
				Iterator<Integer> iterFrom = excludeFrom.iterator();
				Iterator<Integer> iterTo = excludeTo.iterator();
				int from = iterFrom.next();
				int to = iterTo.next();
				// Number of from encountered minus number of to encountered. If
				// this value is 0, the space between the last "to" and the next
				// "from" is free !
				int nbExcludeFrom = 0;

				// this value is the occupation rate of the best fit occupation
				// = size_element / size_best_fit_space.
				// The closest it is from 1, the best it fits !
				double bestFitOccupation = 0;
				boolean lastFromTreated = false;
				boolean lastToTreated = false;

				while (!lastToTreated) {
					if (from <= to) {
						if (nbExcludeFrom == 0) {
							// This is the end of a free space. check if the
							// current element best fits here ?
							int freeSpaceSize = from - freeFrom;
							double occupation = (double) vertex.getWeight()
									/ (double) freeSpaceSize;
							// If the element fits in the space AND fits better
							// than previous best fit
							if (occupation <= 1.0
									&& occupation > bestFitOccupation) {
								bestFitOffset = freeFrom;
								bestFitOccupation = occupation;
							}
						}
						if (iterFrom.hasNext()) {
							from = iterFrom.next();
							nbExcludeFrom++;
						} else {
							if (!lastFromTreated) {
								lastFromTreated = true;
								// Add a from to avoid considering the end of
								// lastTo as a free space
								nbExcludeFrom++;
							}
						}
					}

					if ((to < from) || !iterFrom.hasNext()) {
						nbExcludeFrom--;
						if (nbExcludeFrom == 0) {
							// This is the beginning of a free space !
							freeFrom = to;
						}

						if (iterTo.hasNext()) {
							to = iterTo.next();
						} else {
							lastToTreated = true;
						}
					}
				}
			}

			// If no free space was found between excluding elements
			if (bestFitOffset <= -1) {
				// Put it right after the last element of the list
				bestFitOffset = freeFrom;
			}

			memExNodeAllocation.put(vertex, bestFitOffset);
			edgeAllocation.put(vertex.getEdge(), bestFitOffset);
		}

		return getMemorySize();
	}
}
