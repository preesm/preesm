/**
 * 
 */
package org.ietr.preesm.plugin.mapper.model;

import org.sdf4j.factories.DAGEdgeFactory;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;

/**
 * @author mpelcat
 *
 */
public class MapperEdgeFactory extends DAGEdgeFactory {


	@Override
	public DAGEdge createEdge(DAGVertex arg0, DAGVertex arg1) {

		return new MapperDAGEdge((MapperDAGVertex)arg0,(MapperDAGVertex)arg1);
	}
}
