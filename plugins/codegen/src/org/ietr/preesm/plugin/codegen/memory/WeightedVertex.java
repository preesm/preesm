package org.ietr.preesm.plugin.codegen.memory;

/**
 * This interface must be implemented by weighted Vertex classes.
 * A weighted vertex class is mandatory when solving the 
 * Maximum-Weight Clique problem
 * 
 * @author kdesnos
 *
 * @param <W> is the type of the weight
 */
public interface WeightedVertex<W> {
	/**
	 * Accessor to the weight of the vertex
	 * @return the weight of the vertex
	 */
	abstract public W getWeight();
}
