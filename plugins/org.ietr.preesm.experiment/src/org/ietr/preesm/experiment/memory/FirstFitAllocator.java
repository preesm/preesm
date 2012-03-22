package org.ietr.preesm.experiment.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import net.sf.dftools.workflow.WorkflowException;

import org.ietr.preesm.experiment.memoryBoundsEstimator.AbstractMaximumWeightCliqueSolver;
import org.ietr.preesm.experiment.memoryBoundsEstimator.HeuristicSolver;
import org.ietr.preesm.experiment.memoryBoundsEstimator.MemoryExclusionGraph;
import org.ietr.preesm.experiment.memoryBoundsEstimator.MemoryExclusionVertex;
import org.ietr.preesm.experiment.memoryBoundsEstimator.OstergardSolver;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;

/**
 * In this class, an adapted version of the first fit allocator is implemented.
 * As the lifetime of the memory elements is not known (because of the
 * self-timed assumption), adaptation had to be made. In particular, the order
 * in which the memory elements are considered had to be defined, as in the
 * original algorithm, this order is the scheduling order. The order chosen in
 * this implementation is a random order. Several random orders are tested, and
 * only the (best/mediane/average/worst)? is kept.
 * Other orders have been implemented : StableSet and LargestFirst.
 * 
 * @author kdesnos
 * 
 */
public class FirstFitAllocator extends MemoryAllocator {

	public static enum Policy {
		average, best, mediane, none, worst
	}

	/**
	 * List of the allocations
	 */
	private ArrayList<ArrayList<MemoryExclusionVertex>> lists;

	/**
	 * List the of the allocation resulting sizes.
	 */
	public ArrayList<Integer> listsSize;

	/**
	 * The number of allocation do perform with randomly ordered vertices list.
	 */
	private int nbShuffle;;

	/**
	 * The current policy when asking for an allocation with getAllocation.
	 */
	Policy policy;

	/**
	 * Constructor of the Allocator
	 * 
	 * @param graph
	 *            The graph to analyze
	 */
	public FirstFitAllocator(DirectedAcyclicGraph graph) {
		super(graph);
		nbShuffle = 10;
		policy = Policy.mediane;
		lists = new ArrayList<ArrayList<MemoryExclusionVertex>>(nbShuffle);
		listsSize = new ArrayList<Integer>(nbShuffle);
	}

	/**
	 * Constructor of the Allocator
	 * 
	 * @param memEx
	 *            The exclusion graph to analyze
	 */
	public FirstFitAllocator(MemoryExclusionGraph memEx) {
		super(memEx);
		nbShuffle = 10;
		policy = Policy.mediane;
		lists = new ArrayList<ArrayList<MemoryExclusionVertex>>(nbShuffle);
		listsSize = new ArrayList<Integer>(nbShuffle);
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
	 * @param heuristic this boolean indicate if an exact Maximum-Weight Stable Set should be used or an approximation.
	 */
	public void allocateStableSetOrder(boolean exactStableSet) {
		// Build the MemoryExclusionGraph if necessary
		if (inputExclusionGraph == null) {
			inputExclusionGraph = new MemoryExclusionGraph();
			try {
				inputExclusionGraph.buildGraph(graph);
			} catch (InvalidExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WorkflowException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		ArrayList<MemoryExclusionVertex> list = getStableSetOrderedList(exactStableSet);
		allocateInOrder(list);
	}

	/**
	 * Perform the BestFit allocation with the vertex ordered according to
	 * largest first order. If the policy of the allocator is changed, the
	 * resulting allocation will be lost.
	 */
	public void allocateLargestFirst() {
		// Build the MemoryExclusionGraph if necessary
		if (inputExclusionGraph == null) {
			inputExclusionGraph = new MemoryExclusionGraph();
			try {
				inputExclusionGraph.buildGraph(graph);
			} catch (InvalidExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WorkflowException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		ArrayList<MemoryExclusionVertex> list = new ArrayList<MemoryExclusionVertex>(
				inputExclusionGraph.vertexSet());
		Collections.sort(list, Collections.reverseOrder());
		allocateInOrder(list);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void allocate() {

		// Backup the policy. At the end of the allocation list computation, the
		// policy will be reset in order to select the allocation in the list
		// according to this policy.
		Policy backupPolicy = this.policy;
		setPolicy(null);

		// Build the MemoryExclusionGraph if necessary
		if (inputExclusionGraph == null) {
			inputExclusionGraph = new MemoryExclusionGraph();
			try {
				inputExclusionGraph.buildGraph(graph);
			} catch (InvalidExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WorkflowException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Allocate the lists
		lists = new ArrayList<ArrayList<MemoryExclusionVertex>>(nbShuffle);
		listsSize = new ArrayList<Integer>(nbShuffle);

		for (int iter = 0; iter < nbShuffle ; iter++) {
			// Create a list containing the nodes of the exclusion Graph
			ArrayList<MemoryExclusionVertex> list = new ArrayList<MemoryExclusionVertex>(
					inputExclusionGraph.vertexSet());

			
			Collections.shuffle(list);
			
			// Allocate it
			int size = allocateInOrder(list);

			// Store the results
			lists.add((ArrayList<MemoryExclusionVertex>) list.clone());
			listsSize.add(size);
		}

		// Re-set the policy to select the appropriate allocation
		setPolicy(backupPolicy);
	}

	/**
	 * This method allocate the memory elements with the first fit algorithm and
	 * return the cost of the allocation.
	 * 
	 * @param vertexList
	 *            the ordered vertex list.
	 * @return the resulting allocation size.
	 */
	protected int allocateInOrder(ArrayList<MemoryExclusionVertex> vertexList) {
		// clear all previous allocation
		memExNodeAllocation = new HashMap<MemoryExclusionVertex, Integer>();
		allocation = new HashMap<DAGEdge, Integer>();

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
						int neighborOffset = memExNodeAllocation.get(neighbor);
						if (neighborOffset < (offset + vertex.getWeight())
								&& (neighborOffset + neighbor.getWeight()) > offset) {
							validOffset = false;
							offset += neighbor.getWeight();
							break;
						}
					}
				}
			}
			// Allocate the vertex at the resulting offset
			memExNodeAllocation.put(vertex, offset);
			allocation.put(vertex.getEdge(), offset);
		}

		return getMemorySize();
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
	 * @return the policy
	 */
	public Policy getPolicy() {
		return policy;
	}

	/**
	 * @param nbShuffle
	 *            the nbShuffle to set
	 */
	public void setNbShuffle(int nbShuffle) {
		this.nbShuffle = nbShuffle;
	}

	/**
	 * The change of policy is relevant only if the classic random list version of the allocate method was called before.
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

	/**
	 * This method return the list of vertices of the exclusionGraph. The order
	 * of this list is the following :<br>
	 * The Maximum-Weight Stable set is computed.<br>
	 * Its vertices are added to the list in decreasing weight order.<br>
	 * Its vertices are removed from the graph, and the operation is repeated
	 * until the graph is empty.
	 * 
	 * @param heuristic this boolean indicate if an exact Maximum-Weight Stable Set should be used or an approximation.
	 * 
	 * @return The ordered vertices list
	 */
	protected ArrayList<MemoryExclusionVertex> getStableSetOrderedList(boolean exactStableSet) {
		ArrayList<MemoryExclusionVertex> orderedList;
		orderedList = new ArrayList<MemoryExclusionVertex>();

		SimpleGraph<MemoryExclusionVertex, DefaultEdge> inclusionGraph = inputExclusionGraph
				.getComplementary();

		while (!inclusionGraph.vertexSet().isEmpty()) {
			AbstractMaximumWeightCliqueSolver<MemoryExclusionVertex, DefaultEdge> solver;
			
			if(exactStableSet){
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
}
