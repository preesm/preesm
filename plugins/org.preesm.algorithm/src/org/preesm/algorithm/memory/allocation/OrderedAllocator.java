/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2015)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2015)
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
import java.util.Comparator;
import java.util.List;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.preesm.algorithm.memory.bounds.AbstractMaximumWeightCliqueSolver;
import org.preesm.algorithm.memory.bounds.HeuristicSolver;
import org.preesm.algorithm.memory.bounds.OstergardSolver;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionVertex;
import org.preesm.commons.exceptions.PreesmRuntimeException;

/**
 * The Class OrderedAllocator.
 */
public abstract class OrderedAllocator extends MemoryAllocator {

  /**
   */
  public enum Order {
    SHUFFLE, LARGEST_FIRST, STABLE_SET, EXACT_STABLE_SET, SCHEDULING
  }

  /**
   */
  public enum Policy {
    AVERAGE, BEST, MEDIANE, WORST
  }

  /**
   * Ordered list of {@link MemoryExclusionVertex} used to perform the shuffled allocations. These lists are memorized
   * in order to retrieve the one that corresponds best to the Policy after all "shuffled" allocations were performed
   */
  private List<List<MemoryExclusionVertex>> lists;
  /**
   * For each {@link #allocateInOrder(ArrayList)} resulting from an ordered list in {@link #lists}, this list store the
   * size of the allocated memory.
   */
  private List<Long>                        listsSize;
  /**
   * The number of allocation do perform with randomly ordered vertices list.
   */
  private int                               nbShuffle;
  /**
   * The current policy when asking for an allocation with getAllocation.
   */
  private Policy                            policy;
  /**
   * The current {@link Order} used to {@link #allocate()} vertices of the {@link MemoryExclusionGraph}.
   */
  private Order                             order;

  /**
   */
  protected OrderedAllocator(final MemoryExclusionGraph memEx) {
    super(memEx);
    this.nbShuffle = 10;
    this.policy = Policy.BEST;
    this.lists = new ArrayList<>(this.nbShuffle);
    this.listsSize = new ArrayList<>(this.nbShuffle);
    this.order = Order.SHUFFLE;
  }

  @Override
  public void allocate() {
    switch (this.order) {
      case SHUFFLE -> allocateShuffledOrder();
      case LARGEST_FIRST -> allocateLargestFirst();
      case STABLE_SET -> allocateStableSetOrder(false);
      case EXACT_STABLE_SET -> allocateStableSetOrder(true);
      case SCHEDULING -> allocateSchedulingOrder();
      default -> {
        // Empty
      }
    }
  }

  /**
   * This method allocate the memory elements with the current algorithm and return the cost of the allocation.
   *
   * @param vertexList
   *          the ordered vertex list.
   * @return the resulting allocation size.
   */
  protected abstract long allocateInOrder(final List<MemoryExclusionVertex> vertexList);

  /**
   * Perform the allocation with the vertex ordered according to largest first order. If the policy of the allocator is
   * changed, the resulting allocation will be lost.
   */
  private void allocateLargestFirst() {
    final ArrayList<MemoryExclusionVertex> list = new ArrayList<>(this.inputExclusionGraph.vertexSet());
    Collections.sort(list, (v1, v2) -> Long.compare(v2.getWeight(), v1.getWeight()));
    allocateInOrder(list);
  }

  /**
   * Perform the allocation with the vertex ordered according to the scheduling order. If the policy of the allocator is
   * changed, the resulting allocation will be lost.
   */
  private void allocateSchedulingOrder() {
    // If the exclusion graph is not built, it means that is does not come
    // from the MemEx Updater, and we can do nothing
    if (this.inputExclusionGraph == null) {
      return;
    }

    // Retrieve the memEx vertices in scheduling order
    final List<MemoryExclusionVertex> memExVerticesInSchedulingOrder = this.inputExclusionGraph
        .getMemExVerticesInSchedulingOrder();
    if (memExVerticesInSchedulingOrder == null) {
      throw new PreesmRuntimeException(
          "Cannot allocate MemEx in scheduling order" + " because the MemEx was not updated with a schedule.");
    }

    // Remove hosted vertices from the memEx list in scheduling order
    memExVerticesInSchedulingOrder.retainAll(this.inputExclusionGraph.vertexSet());

    // Do the allocation
    allocateInOrder(memExVerticesInSchedulingOrder);
  }

  /**
   * Perform the allocation with the vertex ordered randomly. The allocation will be performet {@link #nbShuffle} times.
   */
  private void allocateShuffledOrder() {

    // Backup the policy. At the end of the allocation list computation, the
    // policy will be reset in order to select the allocation in the list
    // according to this policy.
    final Policy backupPolicy = this.policy;
    setPolicy(null);

    // Allocate the lists
    this.lists = new ArrayList<>(this.nbShuffle);
    this.listsSize = new ArrayList<>(this.nbShuffle);

    for (int iter = 0; iter < this.nbShuffle; iter++) {
      clear();

      // Create a list containing the nodes of the exclusion Graph
      final ArrayList<MemoryExclusionVertex> list = new ArrayList<>(this.inputExclusionGraph.vertexSet());

      Collections.shuffle(list);

      // Allocate it
      final long size = allocateInOrder(list);

      // Store the results
      this.lists.add(new ArrayList<>(list));
      this.listsSize.add(size);
    }

    // Re-set the policy to select the appropriate allocation
    setPolicy(backupPolicy);
  }

  /**
   * Perform the BestFit allocation with the vertex ordered according to the Stable Set order. If the policy of the
   * allocator is changed, the resulting allocation will be lost.
   *
   * @param exactStableSet
   *          the exact stable set
   */
  private void allocateStableSetOrder(final boolean exactStableSet) {
    final List<MemoryExclusionVertex> list = getStableSetOrderedList(exactStableSet);
    allocateInOrder(list);
  }

  public int getNbShuffle() {
    return this.nbShuffle;
  }

  public Order getOrder() {
    return this.order;
  }

  public Policy getPolicy() {
    return this.policy;
  }

  /**
   * This method return the list of vertices of the exclusionGraph. The order of this list is the following :<br>
   * The Maximum-Weight Stable set is computed.<br>
   * Its vertices are added to the list in decreasing weight order.<br>
   * Its vertices are removed from the graph, and the operation is repeated until the graph is empty.
   *
   * @param exactStableSet
   *          the exact stable set
   * @return The ordered vertices list
   */
  private ArrayList<MemoryExclusionVertex> getStableSetOrderedList(final boolean exactStableSet) {
    ArrayList<MemoryExclusionVertex> orderedList;
    orderedList = new ArrayList<>();

    final SimpleGraph<MemoryExclusionVertex, DefaultEdge> inclusionGraph = this.inputExclusionGraph.getComplementary();

    while (!inclusionGraph.vertexSet().isEmpty()) {
      AbstractMaximumWeightCliqueSolver<MemoryExclusionVertex, DefaultEdge> solver;

      if (exactStableSet) {
        solver = new OstergardSolver<>(inclusionGraph);
      } else {
        solver = new HeuristicSolver<>(inclusionGraph);
      }

      solver.solve();

      final ArrayList<MemoryExclusionVertex> stableSet = new ArrayList<>(solver.getHeaviestClique());
      Collections.sort(stableSet, (v1, v2) -> Long.compare(v2.getWeight(), v1.getWeight()));
      orderedList.addAll(stableSet);

      inclusionGraph.removeAllVertices(stableSet);
    }
    return (orderedList);
  }

  public void setNbShuffle(final int nbShuffle) {
    this.nbShuffle = nbShuffle;
  }

  public void setOrder(final Order order) {
    this.order = order;
  }

  /**
   * The change of policy is relevant only if the classic random list version of the allocate method was called before.
   *
   * @param newPolicy
   *          the new policy
   */
  public void setPolicy(final Policy newPolicy) {
    final Policy oldPolicy = this.policy;
    this.policy = newPolicy;
    if ((newPolicy != null) && (newPolicy != oldPolicy) && !this.listsSize.isEmpty()) {

      // The index of the solution corresponding to the new policy
      final int index = switch (this.policy) {
        case BEST -> {
          final long min = this.listsSize.stream().mapToLong(i -> (long) i).min().orElseThrow();
          yield this.listsSize.indexOf(min);
        }
        case WORST -> {
          final long max = this.listsSize.stream().mapToLong(i -> (long) i).max().orElseThrow();
          yield this.listsSize.indexOf(max);
        }
        case MEDIANE -> {
          final List<Long> listCopy = new ArrayList<>(this.listsSize);
          Collections.sort(listCopy);
          final long mediane = listCopy.get(this.listsSize.size() / 2);
          yield this.listsSize.indexOf(mediane);
        }
        case AVERAGE -> {
          final double average = this.listsSize.stream().mapToDouble(i -> (double) i).average().orElseThrow();

          final long closestToAverage = this.listsSize.stream().map(i -> (double) i)
              .min(Comparator.comparingDouble(i -> Math.abs(i - average))).orElseThrow().longValue();

          yield this.listsSize.indexOf(closestToAverage);
        }
        default -> 0;
      };

      if (index < this.lists.size()) {
        allocateInOrder(this.lists.get(index));
      }
    }
  }
}
