package org.ietr.preesm.plugin.codegen.memory;

/**
 * MemoryExclusionGraphNode is used to represent vertices in the Exclusion
 * graph.
 * 
 * @author kdesnos
 * 
 */
public class MemoryExclusionGraphNode implements WeightedVertex<Integer>,
		Comparable<MemoryExclusionGraphNode> {
	
	/**
	 * unique identifier of node for user convenience 
	 */
	private int identifier;
	
	/**
	 * ID of the task consuming the memory.
	 */
	private String sink;

	/**
	 * Size of the memory used
	 */
	private int size;

	/**
	 * ID of the task producing the memory.
	 */
	private String source;

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
	
	
	public int compareTo(MemoryExclusionGraphNode o) {
		return this.size - o.size;
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
		if (o instanceof MemoryExclusionGraphNode) {
			return (this.source.equals(((MemoryExclusionGraphNode) o).source) && this.sink
					.equals(((MemoryExclusionGraphNode) o).sink));
		} else {
			return false;
		}
	}

	/**
	 * @return the unique identifier of the node
	 */
	public int getIdentifier() {
		return identifier;
	}

	/**
	 * @return the sink
	 */
	public String getSink() {
		return sink;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	public Integer getWeight() {
		return size;
	}

	/**
	 * Method added to enable the use of contains() method in
	 * Set<MemoryExclusionGraphNode>
	 */
	public int hashCode() {
		return sink.hashCode() | source.hashCode();
	}


	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}


	public String toString() {
		return source + "=>" + sink + ":" + size;
	}
}
