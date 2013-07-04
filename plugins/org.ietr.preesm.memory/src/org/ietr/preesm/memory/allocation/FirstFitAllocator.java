package org.ietr.preesm.memory.allocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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
		clear();

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

			int firstFitOffset = -1;
			int freeFrom = 0; // Where the last exclusion ended

			// Look for first fit only if there are exclusions. Else, simply
			// allocate at 0.
			if (!excludeFrom.isEmpty()) {
				// Look for the first free spaces between the exclusion ranges.
				Iterator<Integer> iterFrom = excludeFrom.iterator();
				Iterator<Integer> iterTo = excludeTo.iterator();
				int from = iterFrom.next();
				int to = iterTo.next();
				// Number of from encountered minus number of to encountered. If
				// this value is 0, the space between the last "to" and the next
				// "from" is free !
				int nbExcludeFrom = 0;

				boolean lastFromTreated = false;
				boolean lastToTreated = false;

				// Iterate over the excludeFrom and excludeTo lists
				while (!lastToTreated && firstFitOffset == -1) {
					if (from <= to) {
						if (nbExcludeFrom == 0) {
							// This is the end of a free space. check if the
							// current element fits here ?
							int freeSpaceSize = from - freeFrom;

							// If the element fits in the space
							if (vertex.getWeight() <= freeSpaceSize) {
								firstFitOffset = freeFrom;
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
			if (firstFitOffset <= -1) {
				// Put it right after the last element of the list
				firstFitOffset = freeFrom;
			}

			allocateMemoryObject(vertex, firstFitOffset);
		}

		return getMemorySize();
	}
}
