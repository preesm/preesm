/**
 * 
 */
package org.ietr.preesm.plugin.abc;

import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.sdf4j.model.dag.DAGVertex;

/**
 * The special vertices are special to the mapper because they have
 * additional mapping rules. 
 * 
 * @author mpelcat
 */
public class SpecialVertexManager {


	/**
	 * Tests if a vertex is of type broadcast
	 */
	static public boolean isBroadCast(DAGVertex vertex) {

		if(vertex.getKind().equalsIgnoreCase("dag_broadcast_vertex")){
			return true;
		}
		
		return false;
	}

	/**
	 * Tests if a vertex is of type fork
	 */
	static public boolean isFork(DAGVertex vertex) {

		if(vertex.getKind().equalsIgnoreCase("dag_fork_vertex")){
			return true;
		}
		
		return false;
	}

	/**
	 * Tests if a vertex is of type join
	 */
	static public boolean isJoin(DAGVertex vertex) {

		if(vertex.getKind().equalsIgnoreCase("dag_join_vertex")){
			return true;
		}
		
		return false;
	}
	
}
