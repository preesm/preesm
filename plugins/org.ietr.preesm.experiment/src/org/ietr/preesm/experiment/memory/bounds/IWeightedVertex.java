package org.ietr.preesm.experiment.memory.bounds;

/**
 * This interface must be implemented by weighted Vertex classes. A weighted
 * vertex class is mandatory when solving the Maximum-Weight Clique problem
 * 
 * @author kdesnos
 * 
 * @param <W>
 *            is the type of the weight
 */
public interface IWeightedVertex<W> {
	/**
	 * Accessor to the weight of the vertex
	 * 
	 * @return the weight of the vertex
	 */
	abstract public W getWeight();

	/**
	 * Set the weight of the vertex
	 * 
	 * @param weight
	 *            the weight to set
	 */
	abstract public void setWeight(W weight);

	/**
	 * Get the unique identifier of the vertex. Each vertex must have a unique
	 * identifier. If two vertices have the same identifier, they might be
	 * confused in some function, list, ...
	 * 
	 * @return the unique identifier of the vertex
	 */
	abstract public int getIdentifier();

	/**
	 * Set the unique identifier of the vertex. Each vertex must have a unique
	 * identifier. If two vertices have the same identifier, they might be
	 * confused in some function, list, ...
	 * 
	 * @param id
	 *            the new identifier of the vertex
	 */
	abstract public void setIdentifier(int id);

	/**
	 * Get a deep copy of the vertex.
	 * 
	 * @return the deep copy of the vertex
	 */
	abstract public IWeightedVertex<W> getClone();
}
