/*********************************************************
Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
Karol Desnos, Julien Heulot

[mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.memory.allocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;

/**
 * In this class, an adapted version of the placement algorithm presented in <a
 * href=
 * "http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.47.3832&rep=rep1&type=pdf"
 * >this paper</a> is implemented.
 * 
 * @author kdesnos
 * 
 */
public class DeGreefAllocator extends MemoryAllocator {

	/**
	 * Constructor of the allocator
	 * 
	 * @param memEx
	 *            The exclusion graph whose vertices are to allocate
	 */
	public DeGreefAllocator(MemoryExclusionGraph memEx) {
		super(memEx);
	}

	@Override
	public void allocate() {
		// clear all previous allocation
		clear();

		ArrayList<IntegerAndVertex> nonAllocatedVertices;
		nonAllocatedVertices = new ArrayList<IntegerAndVertex>();
		for (MemoryExclusionVertex vertex : inputExclusionGraph.vertexSet()) {
			nonAllocatedVertices.add(new IntegerAndVertex(0, vertex));
		}

		Collections.sort(nonAllocatedVertices);

		while (!nonAllocatedVertices.isEmpty()) {
			IntegerAndVertex currentPair = nonAllocatedVertices.remove(0);
			int offset = currentPair.getFirst();
			MemoryExclusionVertex vertex = currentPair.getSecond();
			// The rest of the code could be simpler, as was done before
			// revision 123, but it would be slower too.

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

			int newOffset = -1;
			int freeFrom = 0; // Where the last exclusion ended

			// Alignment constraint
			int align = -1;
			Integer typeSize = (Integer) vertex.getPropertyBean().getValue(
					MemoryExclusionVertex.TYPE_SIZE, Integer.class);
			if (alignment == 0) {
				align = typeSize;
			} else if (alignment > 0) {
				align = lcm(typeSize, alignment);
			}

			// Look for first fit only if there are exclusions. Else, simply
			// keep the 0 offset.
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
				while (!lastToTreated && newOffset == -1) {
					if (from <= to) {
						// If this is the end of a free space with an offset
						// greater or equal to offset
						if (nbExcludeFrom == 0 && freeFrom >= offset) {
							// This is the end of a free space. check if the
							// current element fits here ?
							int freeSpaceSize = from - freeFrom;

							// If the element fits in the space
							if (vertex.getWeight() <= freeSpaceSize) {
								newOffset = freeFrom;
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
							// Correct the from if an alignment is needed
							if (align > -1) {
								freeFrom += ((freeFrom % align) == 0) ? 0
										: align - (freeFrom % align);
							}
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
			if (newOffset <= -1) {
				// Put it right after the last element of the list
				newOffset = freeFrom;
			}

			// If the offset was not valid
			if (newOffset != offset) {
				nonAllocatedVertices
						.add(new IntegerAndVertex(newOffset, vertex));
				Collections.sort(nonAllocatedVertices);
			} else {
				allocateMemoryObject(vertex, offset);
			}
		}
	}

	protected class IntegerAndVertex implements Comparable<IntegerAndVertex> {
		private Integer first;
		private MemoryExclusionVertex second;

		public IntegerAndVertex(Integer first, MemoryExclusionVertex second) {
			super();
			this.first = first;
			this.second = second;
		}

		@Override
		public int hashCode() {
			int hashFirst = first != null ? first.hashCode() : 0;
			int hashSecond = second != null ? second.hashCode() : 0;

			return (hashFirst + hashSecond) * hashSecond + hashFirst;
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof IntegerAndVertex) {
				IntegerAndVertex otherPair = (IntegerAndVertex) other;
				return ((this.first == otherPair.first || (this.first != null
						&& otherPair.first != null && this.first
							.equals(otherPair.first))) && (this.second == otherPair.second || (this.second != null
						&& otherPair.second != null && this.second
							.equals(otherPair.second))));
			}

			return false;
		}

		@Override
		public String toString() {
			return "(" + first + ", " + second + ")";
		}

		public Integer getFirst() {
			return first;
		}

		public void setFirst(Integer first) {
			this.first = first;
		}

		public MemoryExclusionVertex getSecond() {
			return second;
		}

		public void setSecond(MemoryExclusionVertex second) {
			this.second = second;
		}

		@Override
		public int compareTo(IntegerAndVertex o) {
			// If the offsets are different, use them as a comparison
			if (first.compareTo(o.first) != 0) {
				return first.compareTo(o.first);
			}

			// Else, compare the vertices
			if (second.compareTo(o.second) != 0) {
				return second.compareTo(o.second);
			}

			// If the vertices weight and the offsets are equal, compare the
			// number of exclusion
			int nbExclusion = inputExclusionGraph.edgesOf(second).size();
			int nbExclusionO = inputExclusionGraph.edgesOf(o.second).size();
			return nbExclusion - nbExclusionO;
		}
	}
}
