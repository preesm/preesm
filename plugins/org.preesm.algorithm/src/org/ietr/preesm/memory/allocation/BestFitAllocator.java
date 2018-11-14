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
package org.ietr.preesm.memory.allocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;
import org.ietr.preesm.utils.math.MathFunctionsHelper;

/**
 * In this class, an adapted version of the best fit allocator is implemented. As the lifetime of the memory elements is
 * not known (because of the self-timed assumption), adaptation had to be made. In particular, the order in which the
 * memory elements are considered had to be defined. In the original algorithm, this order is the scheduling order. The
 * order chosen in this implementation is a random order. Several random orders are tested, and only the
 * (best/mediane/average/worst) is kept. Other orders have been implemented : StableSet and LargestFirst.
 *
 * @author kdesnos
 *
 */
public class BestFitAllocator extends OrderedAllocator {

  /**
   * Constructor of the allocator.
   *
   * @param memEx
   *          The exclusion graph whose vertices are to allocate
   */
  public BestFitAllocator(final MemoryExclusionGraph memEx) {
    super(memEx);
  }

  /**
   * This method allocate the memory elements with the best fit algorithm and return the cost of the allocation.
   *
   * @param vertexList
   *          the ordered vertex list.
   * @return the resulting allocation size.
   */
  @Override
  protected long allocateInOrder(final List<MemoryExclusionVertex> vertexList) {
    // clear all previous allocation
    clear();

    // Allocate vertices in the list order
    for (final MemoryExclusionVertex vertex : vertexList) {
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

      long bestFitOffset = -1;
      long freeFrom = 0; // Where the last exclusion ended

      // Alignment constraint
      long align = -1;
      final Long typeSize = (Long) vertex.getPropertyBean().getValue(MemoryExclusionVertex.TYPE_SIZE);
      if (this.alignment == 0) {
        align = typeSize;
      } else if (this.alignment > 0) {
        align = MathFunctionsHelper.lcm(typeSize, this.alignment);
      }

      // Look for best fit only if there are exclusions. Else, simply
      // allocate at 0.
      if (!excludeFrom.isEmpty()) {
        // Look for free spaces between the exclusion ranges.
        final Iterator<Long> iterFrom = excludeFrom.iterator();
        final Iterator<Long> iterTo = excludeTo.iterator();
        long from = iterFrom.next();
        long to = iterTo.next();
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
              final long freeSpaceSize = from - freeFrom;
              final double occupation = (double) vertex.getWeight() / (double) freeSpaceSize;
              // If the element fits in the space AND fits better
              // than previous best fit
              if ((occupation <= 1.0) && (occupation > bestFitOccupation)) {
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
      if (bestFitOffset <= -1) {
        // Put it right after the last element of the list
        bestFitOffset = freeFrom;
      }

      allocateMemoryObject(vertex, bestFitOffset);
    }

    return getMemorySize();
  }
}
