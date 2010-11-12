/**
 * 
 */
package org.ietr.preesm.core.scenario.editor;

/**
 * Represents a vertex with its path in a hierarchy
 * 
 * @author mpelcat
 */
public interface IHierarchicalVertex {

	/**
	 * Returns the vertex
	 */
	public Object getStoredVertex();

	/**
	 * Returns the vertex name
	 */
	public String getName();

	/**
	 * Compares two vertices in hierarchy
	 */
	public boolean equals(Object e);

	/**
	 * Returns the path of the vertex
	 */
	public String toString();
}
