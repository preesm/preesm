/**
 * 
 */
package org.ietr.preesm.plugin.mapper.tools;

import org.jgrapht.DirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;

/**
 * Just parameterizing TopologicalOrderIterator
 * 
 * @author mpelcat
 */
public class TopologicalDAGIterator extends
		TopologicalOrderIterator<DAGVertex, DAGEdge> {

	public TopologicalDAGIterator(DirectedGraph<DAGVertex, DAGEdge> dag) {
		
		super(dag);
	}
}
