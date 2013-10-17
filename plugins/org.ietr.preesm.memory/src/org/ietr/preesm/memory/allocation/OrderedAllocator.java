package org.ietr.preesm.memory.allocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.dag.DAGVertex;

import org.ietr.preesm.memory.bounds.AbstractMaximumWeightCliqueSolver;
import org.ietr.preesm.memory.bounds.HeuristicSolver;
import org.ietr.preesm.memory.bounds.OstergardSolver;
import org.ietr.preesm.memory.exclusiongraph.MemExBroadcastMerger;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public abstract class OrderedAllocator extends MemoryAllocator {

	public static enum Order {
		SHUFFLE, LARGEST_FIRST, STABLE_SET, EXACT_STABLE_SET, SCHEDULING
	}

	public static enum Policy {
		average, best, mediane, none, worst
	}

	/**
	 * Ordered list of {@link MemoryExclusionVertex} used to perform the
	 * shuffled allocations. These lists are memorized in order to retrieve the
	 * one that corresponds best to the Policy after all "shuffled" allocations
	 * were performed
	 */
	protected ArrayList<ArrayList<MemoryExclusionVertex>> lists;
	/**
	 * For each {@link #allocateInOrder(ArrayList)} resulting from an ordered
	 * list in {@link #lists}, this list store the size of the allocated memory.
	 */
	public ArrayList<Integer> listsSize;
	/**
	 * The number of allocation do perform with randomly ordered vertices list.
	 */
	protected int nbShuffle;
	/**
	 * The current policy when asking for an allocation with getAllocation.
	 */
	protected Policy policy;

	/**
	 * The current {@link Order} used to {@link #allocate()} vertices of the
	 * {@link MemoryExclusionGraph}.
	 */
	protected Order order;

	/**
	 * Constructor of the allocator
	 * 
	 * @param memEx
	 *            The exclusion graph whose vertices are to allocate
	 */
	protected OrderedAllocator(MemoryExclusionGraph memEx) {
		super(memEx);
		nbShuffle = 10;
		policy = Policy.best;
		lists = new ArrayList<ArrayList<MemoryExclusionVertex>>(nbShuffle);
		listsSize = new ArrayList<Integer>(nbShuffle);
		order = Order.SHUFFLE;
	}

	@Override
	public void allocate() {
		switch (order) {
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

		}
	}

	/**
	 * This method allocate the memory elements with the current algorithm and
	 * return the cost of the allocation.
	 * 
	 * @param vertexList
	 *            the ordered vertex list.
	 * @return the resulting allocation size.
	 */
	protected abstract int allocateInOrder(
			List<MemoryExclusionVertex> vertexList);

	/**
	 * Perform the allocation with the vertex ordered according to largest first
	 * order. If the policy of the allocator is changed, the resulting
	 * allocation will be lost.
	 */
	public void allocateLargestFirst() {

		ArrayList<MemoryExclusionVertex> list = new ArrayList<MemoryExclusionVertex>(
				inputExclusionGraph.vertexSet());
		Collections.sort(list, Collections.reverseOrder());
		allocateInOrder(list);
	}

	/**
	 * Perform the allocation with the vertex ordered according to the
	 * scheduling order. If the policy of the allocator is changed, the
	 * resulting allocation will be lost.
	 */
	@SuppressWarnings("unchecked")
	public void allocateSchedulingOrder() {
		// If the exclusion graph is not built, it means that is does not come
		// from the MemEx Updater, and we can do nothing
		if (inputExclusionGraph == null) {
			return;
		}

		// Retrieve the DAG vertices in scheduling order
		List<DAGVertex> dagVertices = inputExclusionGraph
				.getDagVerticesInSchedulingOrder();
		if (dagVertices == null) {
			throw new RuntimeException(
					"Cannot allocate MemEx in scheduling order"
							+ " because the MemEx was not updated with a schedule.");
		}

		// Create a List of MemEx Vertices
		List<MemoryExclusionVertex> memExVertices = new ArrayList<MemoryExclusionVertex>(
				inputExclusionGraph.vertexSet());
		// scan the dag vertices to retrieve the MemEx vertices in Scheduling
		// order
		List<MemoryExclusionVertex> memExVerticesInSchedulingOrder = new ArrayList<MemoryExclusionVertex>(
				inputExclusionGraph.vertexSet().size());

		/** Begin by putting all FIFO related Memory objects (if any) */
		for (MemoryExclusionVertex vertex : inputExclusionGraph.vertexSet()) {
			if (vertex.getSource().startsWith("FIFO_Head_")
					|| vertex.getSource().startsWith("FIFO_Body_")) {
				memExVerticesInSchedulingOrder.add(vertex);
			}
		}

		for (DAGVertex vertex : dagVertices) {
			/** 1- Retrieve the Working Memory MemEx Vertex (if any) */
			{
				// Re-create the working memory exclusion vertex (weight does
				// not matter to find the vertex in the Memex)
				MemoryExclusionVertex wMemVertex = new MemoryExclusionVertex(
						vertex.getName(), vertex.getName(), 0);
				int index;
				if ((index = memExVertices.indexOf(wMemVertex)) != -1) {
					// The working memory exists
					memExVerticesInSchedulingOrder
							.add(memExVertices.get(index));
				}
			}

			/** 2- Retrieve the MemEx Vertices of outgoing edges (if any) */
			{
				for (DAGEdge outgoingEdge : vertex.outgoingEdges()) {
					if (outgoingEdge.getTarget().getPropertyBean()
							.getValue("vertexType").toString().equals("task")) {
						MemoryExclusionVertex edgeVertex = new MemoryExclusionVertex(
								outgoingEdge);
						int index;
						if ((index = memExVertices.indexOf(edgeVertex)) != -1) {
							// The working memory exists
							memExVerticesInSchedulingOrder.add(memExVertices
									.get(index));
						} else {
							// Ignore the issue if the object was merged by a
							// MemExBroadcastMerger
							if (!((Set<MemoryExclusionVertex>) inputExclusionGraph
									.getPropertyBean()
									.getValue(
											MemExBroadcastMerger.MERGED_OBJECT_PROPERTY))
									.contains(edgeVertex)) {
								throw new RuntimeException(
										"Missing MemEx Vertex: " + edgeVertex);
							}
						}
					}
				}
			}
		}

		// Do the allocation
		allocateInOrder(memExVerticesInSchedulingOrder);
	}

	/**
	 * Perform the allocation with the vertex ordered randomly. The allocation
	 * will be performet {@link #nbShuffle} times.
	 */
	protected void allocateShuffledOrder() {

		// Backup the policy. At the end of the allocation list computation, the
		// policy will be reset in order to select the allocation in the list
		// according to this policy.
		Policy backupPolicy = this.policy;
		setPolicy(null);

		// Allocate the lists
		lists = new ArrayList<ArrayList<MemoryExclusionVertex>>(nbShuffle);
		listsSize = new ArrayList<Integer>(nbShuffle);

		for (int iter = 0; iter < nbShuffle; iter++) {
			// Create a list containing the nodes of the exclusion Graph
			ArrayList<MemoryExclusionVertex> list = new ArrayList<MemoryExclusionVertex>(
					inputExclusionGraph.vertexSet());

			Collections.shuffle(list);

			// Allocate it
			int size = allocateInOrder(list);

			// Store the results
			lists.add(new ArrayList<MemoryExclusionVertex>(list));
			listsSize.add(size);
		}

		// Re-set the policy to select the appropriate allocation
		setPolicy(backupPolicy);
	}

	/**
	 * Perform the BestFit allocation with the vertex ordered according to the
	 * exact Stable Set order. If the policy of the allocator is changed, the
	 * resulting allocation will be lost.
	 * 
	 */
	public void allocateStableSetOrder() {
		allocateStableSetOrder(true);
	}

	/**
	 * Perform the BestFit allocation with the vertex ordered according to the
	 * Stable Set order. If the policy of the allocator is changed, the
	 * resulting allocation will be lost.
	 * 
	 * @param heuristic
	 *            this boolean indicate if an exact Maximum-Weight Stable Set
	 *            should be used or an approximation.
	 */
	public void allocateStableSetOrder(boolean exactStableSet) {
		ArrayList<MemoryExclusionVertex> list = getStableSetOrderedList(exactStableSet);
		allocateInOrder(list);
	}

	/**
	 * @return the nbShuffle
	 */
	public int getNbShuffle() {
		return nbShuffle;
	}

	/**
	 * Return the number of allocation that have a size lower or equal to the
	 * reference
	 * 
	 * @param reference
	 *            the number to compare with
	 * @return the number of allocation that give a better memory size
	 */
	public int getNumberBetter(int reference) {
		int result = 0;
		for (int size : listsSize) {
			if (size < reference) {
				result++;
			}
		}
		return result;
	}

	/**
	 * @return the order
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * @return the policy
	 */
	public Policy getPolicy() {
		return policy;
	}

	/**
	 * This method return the list of vertices of the exclusionGraph. The order
	 * of this list is the following :<br>
	 * The Maximum-Weight Stable set is computed.<br>
	 * Its vertices are added to the list in decreasing weight order.<br>
	 * Its vertices are removed from the graph, and the operation is repeated
	 * until the graph is empty.
	 * 
	 * @param heuristic
	 *            this boolean indicate if an exact Maximum-Weight Stable Set
	 *            should be used or an approximation.
	 * 
	 * @return The ordered vertices list
	 */
	protected ArrayList<MemoryExclusionVertex> getStableSetOrderedList(
			boolean exactStableSet) {
		ArrayList<MemoryExclusionVertex> orderedList;
		orderedList = new ArrayList<MemoryExclusionVertex>();

		SimpleGraph<MemoryExclusionVertex, DefaultEdge> inclusionGraph = inputExclusionGraph
				.getComplementary();

		while (!inclusionGraph.vertexSet().isEmpty()) {
			AbstractMaximumWeightCliqueSolver<MemoryExclusionVertex, DefaultEdge> solver;

			if (exactStableSet) {
				solver = new OstergardSolver<MemoryExclusionVertex, DefaultEdge>(
						inclusionGraph);
			} else {
				solver = new HeuristicSolver<MemoryExclusionVertex, DefaultEdge>(
						inclusionGraph);
			}

			solver.solve();

			ArrayList<MemoryExclusionVertex> stableSet = new ArrayList<MemoryExclusionVertex>(
					solver.getHeaviestClique());
			Collections.sort(stableSet, Collections.reverseOrder());
			orderedList.addAll(stableSet);

			inclusionGraph.removeAllVertices(stableSet);
		}
		return (orderedList);
	}

	/**
	 * @param nbShuffle
	 *            the nbShuffle to set
	 */
	public void setNbShuffle(int nbShuffle) {
		this.nbShuffle = nbShuffle;
	}

	/**
	 * @param order
	 *            the order to set
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * The change of policy is relevant only if the classic random list version
	 * of the allocate method was called before.
	 * 
	 * @param policy
	 *            the policy to set
	 */
	public void setPolicy(Policy newPolicy) {
		Policy oldPolicy = policy;
		policy = newPolicy;
		if (newPolicy != null && newPolicy != oldPolicy && !listsSize.isEmpty()) {

			int index = 0; // The index of the solution corresponding to the new
							// policy

			switch (policy) {
			case best:
				int min = listsSize.get(0);
				for (int iter = 1; iter < listsSize.size(); iter++) {
					min = (min < listsSize.get(iter)) ? min : listsSize
							.get(iter);
					index = (min == listsSize.get(iter)) ? iter : index;
				}
				break;

			case worst:
				int max = listsSize.get(0);
				for (int iter = 1; iter < listsSize.size(); iter++) {
					max = (max > listsSize.get(iter)) ? max : listsSize
							.get(iter);
					index = (max == listsSize.get(iter)) ? iter : index;
				}
				break;

			case mediane:
				@SuppressWarnings("unchecked")
				ArrayList<Integer> listCopy = (ArrayList<Integer>) listsSize
						.clone();
				Collections.sort(listCopy);
				int mediane = listCopy.get(listsSize.size() / 2);
				index = listsSize.indexOf(mediane);
				break;

			case average:
				double average = 0;
				for (int iter = 0; iter < listsSize.size(); iter++) {
					average += (double) listsSize.get(iter);
				}
				average /= listsSize.size();

				double smallestDifference = Math.abs((double) listsSize.get(0)
						- average);
				for (int iter = 1; iter < listsSize.size(); iter++) {
					if (smallestDifference > Math.abs((double) listsSize
							.get(iter) - average)) {
						smallestDifference = Math.abs((double) listsSize
								.get(iter) - average);
						index = iter;
					}
				}
				break;

			default:
				index = 0;
				break;
			}
			if (index < lists.size()) {
				allocateInOrder(lists.get(index));
			}
		}
	}

}
