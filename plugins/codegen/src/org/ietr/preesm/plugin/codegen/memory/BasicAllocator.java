package org.ietr.preesm.plugin.codegen.memory;

import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.parameters.InvalidExpressionException;

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
	 * @param graph the graph whose edges are to allocate
	 */
	public BasicAllocator(DirectedAcyclicGraph graph) {
		super(graph);
	}

	/**
	 * Each edge of the graph is given a dedicated memory space.
	 */
	public void allocate() {
		int offset = 0;
		try {
			for (DAGEdge edge : graph.edgeSet()) {
				allocation.put(edge, offset);
				offset+=edge.getWeight().intValue();
			}
		} catch (InvalidExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
