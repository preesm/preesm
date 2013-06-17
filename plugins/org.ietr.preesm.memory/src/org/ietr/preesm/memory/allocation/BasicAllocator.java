package org.ietr.preesm.memory.allocation;

import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;

import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;

/**
 * This implementation of the MemoryAllocator mainly is an implementation
 * example.<br>
 * The allocation performed here simply consists in allocating each edge of the
 * graph into a dedicated memory space (i.e. there will be no re-use). This
 * memory allocation will always give the worst memory allocation possible. <br>
 * 
 * @author kdesnos
 * 
 */
public class BasicAllocator extends MemoryAllocator {

	/**
	 * Constructor of the class
	 * 
	 * @param graph
	 *            the graph whose edges are to allocate
	 */
	public BasicAllocator(DirectedAcyclicGraph graph) {
		super(graph);
	}

	/**
	 * Constructor of the MemoryAllocator
	 * 
	 * @param memEx
	 *            The exclusion graph to analyze
	 */
	public BasicAllocator(MemoryExclusionGraph memEx) {
		super(memEx);
	}

	/**
	 * Each edge of the graph is given a dedicated memory space.
	 */
	public void allocate() {
		int offset = 0;
		if (this.graph != null && inputExclusionGraph == null) {

			// Karol: I think we should forbid the allocation without an
			// exclusion graph
			throw new RuntimeException("Allocation without a MemEx.");
		}

		if (inputExclusionGraph != null) {
			for (MemoryExclusionVertex vertex : inputExclusionGraph.vertexSet()) {
				allocateMemoryObject(vertex, offset);
				offset += vertex.getWeight();
			}
		}
	}
}
