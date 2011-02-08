/**
 * 
 */
package org.ietr.preesm.plugin.mapper.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.workflow.tools.WorkflowLogger;

/**
 * Centralizing constraints of related vertices
 * 
 * @author mpelcat
 */
public class RelativeConstraint {


	private List<MapperDAGVertex> vertices;

	public RelativeConstraint() {
		super();
		this.vertices = new ArrayList<MapperDAGVertex>();
	}
	
	public List<MapperDAGVertex> getVertices() {
		return vertices;
	}

	public void addVertex(MapperDAGVertex vertex) {
		this.vertices.add(vertex);
	}

	public void merge(RelativeConstraint constraints) {
		for(MapperDAGVertex v : constraints.getVertices()){
			if(!vertices.contains(v)){
				this.addVertex(v);
			}
		}
	}

	public List<Operator> getOperatorsIntersection() {

		List<Operator> operators = new ArrayList<Operator>();
		
		if(vertices.isEmpty()){
			WorkflowLogger.getLogger().log(
					Level.SEVERE,
					"Relative constraint with no vertex.");
			
			return operators;
		}
		else{
			MapperDAGVertex firstVertex = vertices.get(0);
			Operator op = firstVertex.getImplementationVertexProperty().getEffectiveOperator();
			if(op != null && vertices.size() > 1){
				// Forcing the mapper to put together related vertices
				operators.add(op);
			}
			else{
				operators.addAll(firstVertex.getInitialVertexProperty().getInitialOperatorList());
			}
		}
		
		for (int i=1; i<vertices.size();i++) {
			MapperDAGVertex vertex = vertices.get(i);
			Operator op = vertex.getImplementationVertexProperty().getEffectiveOperator();
			if(op != null){
				if(operators.contains(op)){
					operators.clear();
					operators.add(op);
				}
				else{
					operators.clear();
				}
			}
			else{
				operators.retainAll(vertex.getInitialVertexProperty().getInitialOperatorList());
			}
		}
		
		return operators;
	}
}
