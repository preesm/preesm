/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2015)
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
import java.util.List;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.preesm.algorithm.memory.bounds.AbstractMaximumWeightCliqueSolver;
import org.preesm.algorithm.memory.bounds.HeuristicSolver;
import org.preesm.algorithm.memory.bounds.OstergardSolver;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionVertex;

// TODO: Auto-generated Javadoc
/**
 * The Class OrderedAllocator.
 */
public abstract class OrderedAllocator extends MemoryAllocator {

  /**
   * The Enum Order.
   */
  public static enum Order {

  /** The shuffle. */
  SHUFFLE,
  /** The largest first. */
  LARGEST_FIRST,
  /** The stable set. */
  STABLE_SET,
  /** The exact stable set. */
  EXACT_STABLE_SET,
  /** The scheduling. */
  SCHEDULING
  }

  /**
   * The Enum Policy.
   */
  public static enum Policy {

    /** The average. */
    average,
    /** The best. */
    best,
    /** The mediane. */
    mediane,
    /** The none. */
    none,
    /** The worst. */
    worst
  }

  /**
   * Ordered list of {@link MemoryExclusionVertex} used to perform the shuffled allocations. These lists are memorized
   * in order to retrieve the one that corresponds best to the Policy after all "shuffled" allocations were performed
   */
  protected List<List<MemoryExclusionVertex>> lists;
  /**
   * For each {@link #allocateInOrder(ArrayList)} resulting from an ordered list in {@link #lists}, this list store the
   * size of the allocated memory.
   */
  public List<Long>                           listsSize;
  /**
   * The number of allocation do perform with randomly ordered vertices list.
   */
  protected int                               nbShuffle;
  /**
   * The current policy when asking for an allocation with getAllocation.
   */
  protected Policy                            policy;

  /**
   * The current {@link Order} used to {@link #allocate()} vertices of the {@link MemoryExclusionGraph}.
   */
  protected Order order;

  /**
   * Constructor of the allocator.
   *
   * @param memEx
   *          The exclusion graph whose vertices are to allocate
   */
  protected OrderedAllocator(final MemoryExclusionGraph memEx) {
    super(memEx);
    this.nbShuffle = 10;
    this.policy = Policy.best;
    this.lists = new ArrayList<>(this.nbShuffle);
    this.listsSize = new ArrayList<>(this.nbShuffle);
    this.order = Order.SHUFFLE;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.memory.allocation.MemoryAllocator#allocate()
   */
  @Override
  public void allocate() {
    switch (this.order) {
      case SHUFFLE:
        allocateShuffledOrder();
        break;
      case LARGEST_FIRST:
        allocateLargestFirst();
        break;
      case STABLE_SET:
        allocateStableSetOrder(false);
        break;
      case EXACT_STABLE_SET:
        allocateStableSetOrder();
        break;
      case SCHEDULING:
        allocateSchedulingOrder();
        break;
      default:
    }
  }

  /**
   * This method allocate the memory elements with the current algorithm and return the cost of the allocation.
   *
   * @param vertexList
   *          the ordered vertex list.
   * @return the resulting allocation size.
   */
  protected abstract long allocateInOrder(List<MemoryExclusionVertex> vertexList);

  /**
   * Perform the allocation with the vertex ordered according to largest first order. If the policy of the allocator is
   * changed, the resulting allocation will be lost.
   */
  public void allocateLargestFirst() {

    final ArrayList<MemoryExclusionVertex> list = new ArrayList<>(this.inputExclusionGraph.vertexSet());
    Collections.sort(list, Collections.reverseOrder());
    allocateInOrder(list);
  }

  /**
   * Perform the allocation with the vertex ordered according to the scheduling order. If the policy of the allocator is
   * changed, the resulting allocation will be lost.
   */
  public void allocateSchedulingOrder() {
    // If the exclusion graph is not built, it means that is does not come
    // from the MemEx Updater, and we can do nothing
    if (this.inputExclusionGraph == null) {
      return;
    }

    // Retrieve the memEx vertices in scheduling order
    final List<MemoryExclusionVertex> memExVerticesInSchedulingOrder = this.inputExclusionGraph
        .getMemExVerticesInSchedulingOrder();
    if (memExVerticesInSchedulingOrder == null) {
      throw new RuntimeException(
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
  protected void allocateShuffledOrder() {

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
   * Perform the BestFit allocation with the vertex ordered according to the exact Stable Set order. If the policy of
   * the allocator is changed, the resulting allocation will be lost.
   *
   */
  public void allocateStableSetOrder() {
    allocateStableSetOrder(true);
  }

  /**
   * Perform the BestFit allocation with the vertex ordered according to the Stable Set order. If the policy of the
   * allocator is changed, the resulting allocation will be lost.
   *
   * @param exactStableSet
   *          the exact stable set
   */
  public void allocateStableSetOrder(final boolean exactStableSet) {
    final ArrayList<MemoryExclusionVertex> list = getStableSetOrderedList(exactStableSet);
    allocateInOrder(list);
  }

  /**
   * Gets the nb shuffle.
   *
   * @return the nbShuffle
   */
  public int getNbShuffle() {
    return this.nbShuffle;
  }

  /**
   * Return the number of allocation that have a size lower or equal to the reference.
   *
   * @param reference
   *          the number to compare with
   * @return the number of allocation that give a better memory size
   */
  public int getNumberBetter(final long reference) {
    int result = 0;
    for (final long size : this.listsSize) {
      if (size < reference) {
        result++;
      }
    }
    return result;
  }

  /**
   * Gets the order.
   *
   * @return the order
   */
  public Order getOrder() {
    return this.order;
  }

  /**
   * Gets the policy.
   *
   * @return the policy
   */
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
  protected ArrayList<MemoryExclusionVertex> getStableSetOrderedList(final boolean exactStableSet) {
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
      Collections.sort(stableSet, Collections.reverseOrder());
      orderedList.addAll(stableSet);

      inclusionGraph.removeAllVertices(stableSet);
    }
    return (orderedList);
  }

  /**
   * Sets the nb shuffle.
   *
   * @param nbShuffle
   *          the nbShuffle to set
   */
  public void setNbShuffle(final int nbShuffle) {
    this.nbShuffle = nbShuffle;
  }

  /**
   * Sets the order.
   *
   * @param order
   *          the order to set
   */
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

      int index = 0; // The index of the solution corresponding to the new
      // policy

      switch (this.policy) {
        case best:
          long min = this.listsSize.get(0);
          for (int iter = 1; iter < this.listsSize.size(); iter++) {
            min = (min < this.listsSize.get(iter)) ? min : this.listsSize.get(iter);
            index = (min == this.listsSize.get(iter)) ? iter : index;
          }
          break;

        case worst:
          long max = this.listsSize.get(0);
          for (int iter = 1; iter < this.listsSize.size(); iter++) {
            max = (max > this.listsSize.get(iter)) ? max : this.listsSize.get(iter);
            index = (max == this.listsSize.get(iter)) ? iter : index;
          }
          break;

        case mediane:
          final List<Long> listCopy = new ArrayList<>(listsSize);
          Collections.sort(listCopy);
          final long mediane = listCopy.get(this.listsSize.size() / 2);
          index = this.listsSize.indexOf(mediane);
          break;

        case average:
          double average = 0;
          for (int iter = 0; iter < this.listsSize.size(); iter++) {
            average += (double) this.listsSize.get(iter);
          }
          average /= this.listsSize.size();

          double smallestDifference = Math.abs((double) this.listsSize.get(0) - average);
          for (int iter = 1; iter < this.listsSize.size(); iter++) {
            if (smallestDifference > Math.abs((double) this.listsSize.get(iter) - average)) {
              smallestDifference = Math.abs((double) this.listsSize.get(iter) - average);
              index = iter;
            }
          }
          break;

        default:
          index = 0;
          break;
      }
      if (index < this.lists.size()) {
        allocateInOrder(this.lists.get(index));
      }
    }
  }

}
