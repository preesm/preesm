/**
 * 
 */
package org.ietr.preesm.plugin.abc;

import java.util.logging.Level;

import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.mapper.edgescheduling.Interval;
import org.ietr.preesm.plugin.mapper.edgescheduling.IntervalFinder;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdge;
import org.sdf4j.model.dag.DAGEdge;

/**
 * The task switcher adds a processing to the mapping algorithm. When a vertex
 * is mapped, it looks for the best place to schedule it.
 * 
 * @author mpelcat
 */
public class TaskSwitcher {

	/**
	 * Current implementation
	 */
	private MapperDAG implementation;
	
	/**
	 * Current mapped vertex
	 */
	private MapperDAGVertex vertex;

	/**
	 * Contains the rank list of all the vertices in an implementation
	 */
	protected SchedOrderManager orderManager = null;

	public TaskSwitcher(MapperDAG implementation,
			SchedOrderManager orderManager, MapperDAGVertex vertex) {
		super();
		this.implementation = implementation;
		this.orderManager = orderManager;
		this.vertex = vertex;
	}

	/**
	 * Returns the highest index of vertex predecessors
	 */
	private int getLatestPredecessorIndex(MapperDAGVertex testVertex){
		int index = -1;
		
		for(DAGEdge edge : testVertex.incomingEdges()){
			if(!(edge instanceof PrecedenceEdge)){
				index = Math.max(index, orderManager.totalIndexOf((MapperDAGVertex)edge.getSource()));
			}
			else{
				int i = 0;
				i++;
			}
		}
		
		return index;
	}

	/**
	 * Returns the lowest index of vertex successors
	 */
	private int getEarliestsuccessorIndex(MapperDAGVertex testVertex){
		int index = Integer.MAX_VALUE;
		
		for(DAGEdge edge : testVertex.outgoingEdges()){
			if(!(edge instanceof PrecedenceEdge)){
				index = Math.min(index, orderManager.totalIndexOf((MapperDAGVertex)edge.getTarget()));
			}
			else{
				int i = 0;
				i++;
			}
		}
		
		if(index == Integer.MAX_VALUE) index = -1;
		
		return index;
	}

	/**
	 * Returns the best index to schedule vertex in total order
	 */
	public int getBestIndex(){
		int index = -1;
		int latePred = getLatestPredecessorIndex(vertex);
		int earlySuc = getEarliestsuccessorIndex(vertex);
		
		if(latePred == -1){
 			getEarliestsuccessorIndex(vertex);
			getLatestPredecessorIndex(vertex);
		}
		
		IntervalFinder intervalFinder = new IntervalFinder(orderManager);
		
		Operator op = vertex.getImplementationVertexProperty().getEffectiveOperator();
		MapperDAGVertex minVertex = (latePred == -1)? null:orderManager.getVertex(latePred);
		MapperDAGVertex maxVertex = (earlySuc == -1)? null:orderManager.getVertex(earlySuc);
		
		if(op != null){
			Interval itv = intervalFinder.findLargestFreeInterval(op, minVertex, maxVertex);

			if(itv.getDuration()>0){
				index = itv.getTotalOrderIndex();
				//PreesmLogger.getLogger().log(Level.INFO,"bestidx:"+vertex.toString()+"->"+index+",pred:"+latePred+",suc:"+earlySuc+","+orderManager.getTotalOrder().toString());
				
			}
		}
		
		return index;
	}

	public void insertVertex(){
		
		// Removing the vertex if necessary before inserting it
		if(orderManager.totalIndexOf(vertex) != -1)
			orderManager.remove(vertex,true);
		
		int newIndex = getBestIndex(); 
		if(newIndex >= 0){
			orderManager.insertVertexAtIndex(newIndex, vertex);
		}
		else{
			orderManager.addLast(vertex);
		}
	}
}
