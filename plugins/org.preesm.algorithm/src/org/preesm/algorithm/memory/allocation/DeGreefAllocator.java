/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Julien Hascoet <jhascoet@kalray.eu> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionVertex;
import org.preesm.commons.math.MathFunctionsHelper;

/**
 * In this class, an adapted version of the placement algorithm presented in
 * <a href= "http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.47.3832&rep=rep1&type=pdf" >this paper</a> is
 * implemented.
 *
 * @author kdesnos
 *
 */
public class DeGreefAllocator extends MemoryAllocator {

  /**
   * Constructor of the allocator.
   *
   * @param memEx
   *          The exclusion graph whose vertices are to allocate
   */
  public DeGreefAllocator(final MemoryExclusionGraph memEx) {
    super(memEx);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.memory.allocation.MemoryAllocator#allocate()
   */
  @Override
  public void allocate() {
    // clear all previous allocation
    clear();

    ArrayList<IntegerAndVertex> nonAllocatedVertices;
    nonAllocatedVertices = new ArrayList<>();
    for (final MemoryExclusionVertex vertex : this.inputExclusionGraph.vertexSet()) {
      nonAllocatedVertices.add(new IntegerAndVertex(0, vertex));
    }

    Collections.sort(nonAllocatedVertices);

    while (!nonAllocatedVertices.isEmpty()) {
      final IntegerAndVertex currentPair = nonAllocatedVertices.remove(0);
      final long offset = currentPair.getFirst();
      final MemoryExclusionVertex vertex = currentPair.getSecond();
      // The rest of the code could be simpler, as was done before
      // revision 123, but it would be slower too.

      // Get vertex neighbors
      final Set<MemoryExclusionVertex> neighbors = this.inputExclusionGraph.getAdjacentVertexOf(vertex);

      // Construct two lists that contains the exclusion ranges in memory
      final List<Long> excludeFrom = new ArrayList<>();
      final List<Long> excludeTo = new ArrayList<>();
      for (final MemoryExclusionVertex neighbor : neighbors) {
        if (this.memExNodeAllocation.containsKey(neighbor)) {
          final long neighborOffset = this.memExNodeAllocation.get(neighbor);
          excludeFrom.add(neighborOffset);
          excludeTo.add(neighborOffset + neighbor.getWeight());
        }
      }

      Collections.sort(excludeFrom);
      Collections.sort(excludeTo);

      long newOffset = -1;
      long freeFrom = 0; // Where the last exclusion ended

      // Alignment constraint
      long align = -1;
      final Long typeSize = (Long) vertex.getPropertyBean().getValue(MemoryExclusionVertex.TYPE_SIZE);
      if (this.alignment == 0) {
        align = typeSize;
      } else if (this.alignment > 0) {
        align = MathFunctionsHelper.lcm(typeSize, this.alignment);
      }

      // Look for first fit only if there are exclusions. Else, simply
      // keep the 0 offset.
      if (!excludeFrom.isEmpty()) {
        // Look for the first free spaces between the exclusion ranges.
        final Iterator<Long> iterFrom = excludeFrom.iterator();
        final Iterator<Long> iterTo = excludeTo.iterator();
        long from = iterFrom.next();
        long to = iterTo.next();
        // Number of from encountered minus number of to encountered. If
        // this value is 0, the space between the last "to" and the next
        // "from" is free !
        long nbExcludeFrom = 0;

        boolean lastFromTreated = false;
        boolean lastToTreated = false;

        // Iterate over the excludeFrom and excludeTo lists
        while (!lastToTreated && (newOffset == -1)) {
          if (from <= to) {
            // If this is the end of a free space with an offset
            // greater or equal to offset
            if ((nbExcludeFrom == 0) && (freeFrom >= offset)) {
              // This is the end of a free space. check if the
              // current element fits here ?
              final long freeSpaceSize = from - freeFrom;

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
                freeFrom += ((freeFrom % align) == 0) ? 0 : align - (freeFrom % align);
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
        nonAllocatedVertices.add(new IntegerAndVertex(newOffset, vertex));
        Collections.sort(nonAllocatedVertices);
      } else {
        allocateMemoryObject(vertex, offset);
      }
    }
  }

  /**
   * The Class IntegerAndVertex.
   */
  private class IntegerAndVertex implements Comparable<IntegerAndVertex> {

    /** The first. */
    private final long first;

    /** The second. */
    private final MemoryExclusionVertex second;

    /**
     * Instantiates a new integer and vertex.
     *
     * @param first
     *          the first
     * @param second
     *          the second
     */
    private IntegerAndVertex(final long first, final MemoryExclusionVertex second) {
      super();
      this.first = first;
      this.second = second;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      final int hashFirst = Long.hashCode(this.first);
      final int hashSecond = this.second != null ? this.second.hashCode() : 0;

      return ((hashFirst + hashSecond) * hashSecond) + hashFirst;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object other) {
      if (other instanceof IntegerAndVertex) {
        final IntegerAndVertex otherPair = (IntegerAndVertex) other;
        return ((this.first == otherPair.first) && ((this.second == otherPair.second)
            || ((this.second != null) && (otherPair.second != null) && this.second.equals(otherPair.second))));
      }

      return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "(" + this.first + ", " + this.second + ")";
    }

    /**
     * Gets the first.
     *
     * @return the first
     */
    public long getFirst() {
      return this.first;
    }

    /**
     * Gets the second.
     *
     * @return the second
     */
    public MemoryExclusionVertex getSecond() {
      return this.second;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final IntegerAndVertex o) {
      // If the offsets are different, use them as a comparison
      final long firstDiff = this.first - o.first;
      if (firstDiff != 0) {
        return (int) firstDiff;
      }

      // Else, compare the vertices
      if (this.second.compareTo(o.second) != 0) {
        return this.second.compareTo(o.second);
      }

      // If the vertices weight and the offsets are equal, compare the
      // number of exclusion
      final int nbExclusion = DeGreefAllocator.this.inputExclusionGraph.edgesOf(this.second).size();
      final int nbExclusionO = DeGreefAllocator.this.inputExclusionGraph.edgesOf(o.second).size();
      return nbExclusion - nbExclusionO;
    }
  }
}
