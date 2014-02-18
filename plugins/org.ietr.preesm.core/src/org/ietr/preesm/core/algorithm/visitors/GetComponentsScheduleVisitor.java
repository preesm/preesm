package org.ietr.preesm.core.algorithm.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.dag.DAGVertex;
import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.algorithm.model.visitors.IGraphVisitor;
import net.sf.dftools.algorithm.model.visitors.SDF4JException;
import net.sf.dftools.architecture.slam.ComponentInstance;

/**
 * Visitor to retrieve the schedule of each component of
 * a mapped Directed Acyclic Graph
 * 
 * @author kdesnos
 * 
 */
public class GetComponentsScheduleVisitor implements
		IGraphVisitor<DirectedAcyclicGraph, DAGVertex, DAGEdge> {
	
	protected HashMap<ComponentInstance,ArrayList<DAGVertex>> _componentsSchedule;

	/**
	 * Constructor of the GetComponentsScheduleVisitor.
	 */
	public GetComponentsScheduleVisitor() {
		_componentsSchedule = new HashMap<ComponentInstance,ArrayList<DAGVertex>>();
	}

	/**
	 * Return the result of the visitor algorithm.
	 * 
	 * @return An HashMap containing the schedule of each component of the DAG
	 */
	public HashMap<ComponentInstance,ArrayList<DAGVertex>> getResult() {
		return _componentsSchedule;
	}

	@Override
	public void visit(DAGEdge currentEdge) {
		// Nothing to do here for this visitor
	}

	@Override
	public void visit(DirectedAcyclicGraph dag) throws SDF4JException {
		// Iterate the vertices of the DirectedAcyclicGraph in their
		// total scheduling order
		Iterator<DAGVertex> iterator = dag.vertexSet().iterator();
		while(iterator.hasNext()){
			DAGVertex currentVertex = iterator.next();
			currentVertex.accept(this);			
		}		
	}

	@Override
	public void visit(DAGVertex dagVertex) throws SDF4JException {
		// We only add "task" vertices to schedules
		if(dagVertex.getPropertyBean().getValue("vertexType").toString().equals("task")){
			// Retrieve the component on which the vertex is mapped
			ComponentInstance component = (ComponentInstance) dagVertex.getPropertyBean().getValue("Operator");
			if(component != null){
				// If a component was retrieved, add the current vertex
				// to the schedule of this component.
				ArrayList<DAGVertex> schedule = _componentsSchedule.get(component);
				// If the component had no existing schedule, create one
				if(schedule == null){
					schedule = new ArrayList<DAGVertex>();
					_componentsSchedule.put(component, schedule);
				}
				schedule.add(dagVertex);
			}
		}
	}

}
