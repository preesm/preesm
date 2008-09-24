/**
 * 
 */
package org.ietr.preesm.plugin.mapper.tools;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.sdf4j.model.dag.DAGVertex;

/**
 * Useful tools to handle problems of compatibility with SDF4J models
 * 
 * @author mpelcat
 */
public class ToolBox {


	/**
	 * Adds all vertices from newSet in destination
	 */
	static public void addAllNodes(List<MapperDAGVertex> destination,
			Set<DAGVertex> newSet) {
		Iterator<DAGVertex> it = newSet.iterator();

		while (it.hasNext()) {
			MapperDAGVertex vertex = (MapperDAGVertex) it.next();
			destination.add(vertex);
		}
	}
}
