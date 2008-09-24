/**
 * 
 */
package org.ietr.preesm.plugin.mapper.model;

import org.sdf4j.factories.DAGVertexFactory;
import org.sdf4j.model.dag.DAGVertex;

/**
 * Creates vertices of type {@link MapperDAGVertex}
 * 
 * @author mpelcat
 */
public class MapperVertexFactory extends DAGVertexFactory {

	@Override
	public DAGVertex createVertex() {
		return new MapperDAGVertex();
	}



}
