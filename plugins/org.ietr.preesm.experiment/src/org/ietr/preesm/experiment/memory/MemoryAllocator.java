package org.ietr.preesm.experiment.memory;

import java.util.HashMap;
import net.sf.dftools.workflow.WorkflowException;

import org.ietr.preesm.experiment.memoryBoundsEstimator.MemoryExclusionGraph;
import org.ietr.preesm.experiment.memoryBoundsEstimator.MemoryExclusionVertex;
import org.jgrapht.graph.DefaultEdge;
import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;

/**
 * This class is both an interface and toolbox class for memory allocator.
 * 
 * @author kdesnos
 */
public abstract class MemoryAllocator {

	/**
	 * An allocation is a map of edges associated to an integer which represents
	 * their offset in a monolithic memory.<br>
	 * <br>
	 * <table border>
	 * <tr>
	 * <td>Edge<sub>size</sub></td>
	 * <td>Offset</td>
	 * </tr>
	 * <tr>
	 * <td>A->B<sub>100</sub></td>
	 * <td>0</td>
	 * </tr>
	 * <tr>
	 * <td>B->C<sub>200</sub></td>
	 * <td>100</td>
	 * </tr>
	 * <tr>
	 * <td>C->D<sub>50</sub></td>
	 * <td>0</td>
	 * </tr>
	 * <tr>
	 * <td>C->E<sub>25</sub></td>
	 * <td>50</td>
	 * </tr>
	 * </table>
	 */
	protected HashMap<DAGEdge, Integer> allocation;

	/**
	 * The SDF graph whose edges (memory transfers between actors) are to
	 * allocate.<br>
	 * This graph should not be modified. Make a local copy if needed.
	 */
	protected final DirectedAcyclicGraph graph;

	protected HashMap<MemoryExclusionVertex, Integer> memExNodeAllocation;

	protected MemoryExclusionGraph inputExclusionGraph;

	/**
	 * Constructor of the MemoryAllocator
	 * 
	 * @param graph
	 *            The graph to analyze
	 */
	protected MemoryAllocator(DirectedAcyclicGraph graph) {
		this.graph = graph;
		allocation = new HashMap<DAGEdge, Integer>();

		memExNodeAllocation = null;
		inputExclusionGraph = null;
	}

	/**
	 * Constructor of the MemoryAllocator
	 * 
	 * @param memEx
	 *            The exclusion graph to analyze
	 */
	protected MemoryAllocator(MemoryExclusionGraph memEx) {
		this.graph = null;
		allocation = new HashMap<DAGEdge, Integer>();

		memExNodeAllocation = new HashMap<MemoryExclusionVertex, Integer>();
		inputExclusionGraph = memEx;
	}

	/**
	 * This method will perform the memory allocation of graph edges and store
	 * the result in the allocation HashMap.
	 */
	public abstract void allocate();

	/**
	 * This function return an allocation of the edges of the SDF stored in
	 * graph attribute.
	 * 
	 * An allocation is a map of edges associated to an integer which represents
	 * their offset in memory. Different allocator policy exists (First Fit,
	 * Best Fit...)
	 * 
	 * @return An allocation
	 */
	public HashMap<DAGEdge, Integer> getAllocation() {
		return allocation;
	}
	

	/**
	 * This method computes and return the size of the allocated memory.
	 * 
	 * @return the memory Size
	 */
	public int getMemorySize() {
		int memorySize = 0;

		// Use the memExNodeAllocation if available
		if (memExNodeAllocation != null) {
			for (MemoryExclusionVertex vertex : memExNodeAllocation.keySet()) {
				if ((memExNodeAllocation.get(vertex) + vertex.getWeight()) > memorySize) {
					memorySize = memExNodeAllocation.get(vertex)
							+ vertex.getWeight();
				}
			}
			return memorySize;
		}
		
		if (! allocation.isEmpty()) {
			try {
				// Look for the maximum value of (offset + edge.size) in
				// allocation map
				for (DAGEdge edge : allocation.keySet()) {
					if ((allocation.get(edge) + edge.getWeight().intValue()) > memorySize) {
						memorySize = allocation.get(edge)
								+ edge.getWeight().intValue();
						}
				}
			} catch (InvalidExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return memorySize;
		}
		return -1;
	}

	/**
	 * This method is responsible for checking the conformity of a DAG memory
	 * allocation with the following constraints : <li>An input buffer of an
	 * actor can not share a memory space with an output. <li>As all actors are
	 * considered self-scheduled, buffers in parallel branches of the DAG can
	 * not share the same memory space.
	 * 
	 * @return The list of conflicting memory elements. Empty list if allocation
	 *         follow the rules.
	 */
	public HashMap<MemoryExclusionVertex, Integer> checkAllocation() {
		HashMap<MemoryExclusionVertex, Integer> conflictingElements;
		conflictingElements = new HashMap<MemoryExclusionVertex, Integer>();

		// If this allocator did not build the exclusion graph, build it
		if (this.inputExclusionGraph == null && graph != null) {
			try {
				inputExclusionGraph = new MemoryExclusionGraph();
				this.inputExclusionGraph.buildGraph(graph);
			} catch (InvalidExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WorkflowException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Check that no edge of the exclusion graph is violated
		for (DefaultEdge edge : inputExclusionGraph.edgeSet()) {
			MemoryExclusionVertex source = inputExclusionGraph
					.getEdgeSource(edge);
			MemoryExclusionVertex target = inputExclusionGraph
					.getEdgeTarget(edge);

			int sourceOffset;
			int targetOffset;

			// If an allocation was created only based on a memory exclusion
			// graph, the edge attribute of MemoryExclusionGraphNodes will be
			// null and
			// allocation table won't be valid.
			if (memExNodeAllocation != null) {
				sourceOffset = memExNodeAllocation.get(source);
				targetOffset = memExNodeAllocation.get(target);
			} else {
				sourceOffset = allocation.get(source.getEdge());
				targetOffset = allocation.get(target.getEdge());
			}

			// If the memory element share memory space
			if ((sourceOffset < (targetOffset + target.getWeight()))
					&& ((sourceOffset + source.getWeight()) > targetOffset)) {
				conflictingElements.put(source, sourceOffset);
				conflictingElements.put(target, targetOffset);
			}
		}
		return conflictingElements;
	}
}
