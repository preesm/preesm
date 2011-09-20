package org.ietr.preesm.plugin.codegen.memory;

/**
 * MemoryExclusionGraphNode is used to represent vertices in the Exclusion
 * graph.
 * 
 * @author kdesnos
 * 
 */
public class MemoryExclusionGraphNode {

	/**
	 * ID of the task producing the memory.
	 */
	private String source;

	/**
	 * ID of the task consuming the memory.
	 */
	private String sink;

	/**
	 * Size of the memory used
	 */
	private int size;

	/**
	 * Constructor of the class
	 * 
	 * @param sourceTask
	 *            The ID of the task producing memory
	 * @param sourceTask
	 *            The ID of the task consuming memory
	 * @param sourceTask
	 *            The size of the memory
	 */
	public MemoryExclusionGraphNode(String sourceTask, String sinkTask,
			int sizeMem) {
		source = sourceTask;
		sink = sinkTask;
		size = sizeMem;
	}

	/**
	 * Method added to enable the use of contains() method in
	 * Set<MemoryExclusionGraphNode>
	 */
	public int hashCode() {
		return sink.hashCode() | source.hashCode();
	}

	public String toString() {
		return source + "=>" + sink + ":" + size;
	}

	/**
	 * Test equality of two nodes.<br>
	 * Two nodes are considered equals if their source and sink are the same.
	 * The weight of the node is not taken into account.
	 * 
	 * @param o
	 *            the object to compare.
	 * @return true if the object is a node a similar, false else.
	 */
	public boolean equals(Object o) {

		if (o instanceof MemoryExclusionGraphNode)
			return (this.source.equals(((MemoryExclusionGraphNode) o).source) && this.sink
					.equals(((MemoryExclusionGraphNode) o).sink));
		else
			return false;

	}
}
