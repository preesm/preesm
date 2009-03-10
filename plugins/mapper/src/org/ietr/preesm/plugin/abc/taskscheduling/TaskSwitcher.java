/**
 * 
 */
package org.ietr.preesm.plugin.abc.taskscheduling;

import java.util.Random;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.plugin.abc.edgescheduling.Interval;
import org.ietr.preesm.plugin.abc.edgescheduling.IntervalFinder;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdge;
import org.sdf4j.model.dag.DAGEdge;

/**
 * The task switcher adds a processing to the mapping algorithm. When a vertex
 * is mapped, it looks for the best place to schedule it.
 * 
 * @author mpelcat
 */
public class TaskSwitcher extends AbstractTaskSched{

	private IntervalFinder intervalFinder;
	
	public TaskSwitcher(SchedOrderManager orderManager) {
		super(orderManager);
		intervalFinder = new IntervalFinder(orderManager);
	}

	/**
	 * Returns the highest index of vertex predecessors
	 */
	private int getLatestPredecessorIndex(MapperDAGVertex testVertex) {
		int index = -1;

		for (DAGEdge edge : testVertex.incomingEdges()) {
			if (!(edge instanceof PrecedenceEdge)) {
				index = Math.max(index, orderManager
						.totalIndexOf((MapperDAGVertex) edge.getSource()));
			} else {
				int i = 0;
				i++;
			}
		}

		return index;
	}

	/**
	 * Returns the lowest index of vertex successors
	 */
	private int getEarliestsuccessorIndex(MapperDAGVertex testVertex) {
		int index = Integer.MAX_VALUE;

		for (DAGEdge edge : testVertex.outgoingEdges()) {
			if (!(edge instanceof PrecedenceEdge)) {
				index = Math.min(index, orderManager
						.totalIndexOf((MapperDAGVertex) edge.getTarget()));
			} else {
				int i = 0;
				i++;
			}
		}

		if (index == Integer.MAX_VALUE)
			index = -1;

		return index;
	}

	/**
	 * Returns the best index to schedule vertex in total order
	 */
	public int getBestIndex(MapperDAGVertex vertex) {
		int index = -1;
		int latePred = getLatestPredecessorIndex(vertex);
		int earlySuc = getEarliestsuccessorIndex(vertex);

		if (latePred == -1) {
			getEarliestsuccessorIndex(vertex);
			getLatestPredecessorIndex(vertex);
		}

		Operator op = vertex.getImplementationVertexProperty()
				.getEffectiveOperator();
		MapperDAGVertex source = (latePred == -1) ? null : orderManager
				.getVertex(latePred);
		MapperDAGVertex target = (earlySuc == -1) ? null : orderManager
				.getVertex(earlySuc);

		if (op != null) {
			Interval largestInterval = intervalFinder.findLargestFreeInterval(op, source, target);
			
			if(largestInterval.getDuration()>0){
				index = largestInterval.getTotalOrderIndex();
			}
			else{
				int sourceIndex = intervalFinder.getOrderManager().totalIndexOf(source)+1;
				int targetIndex = intervalFinder.getOrderManager().totalIndexOf(target);
				
				if(targetIndex-sourceIndex > 0){
					Random r = new Random();
					int randomVal = Math.abs(r.nextInt());
					randomVal = randomVal%(targetIndex-sourceIndex);
					index = sourceIndex+randomVal;
				}
			}
			
		}

		return index;
	}

	public void insertVertexBefore(MapperDAGVertex successor, MapperDAGVertex vertex) {

		// Removing the vertex if necessary before inserting it
		if (orderManager.totalIndexOf(vertex) != -1)
			orderManager.remove(vertex, true);

		int newIndex = getBestIndex(vertex);
		if (newIndex >= 0) {
			orderManager.insertVertexAtIndex(newIndex, vertex);
		} else {
			orderManager.insertVertexBefore(successor, vertex);
		}
	}

	@Override
	public void insertVertex(MapperDAGVertex vertex) {

		// Removing the vertex if necessary before inserting it
		if (orderManager.totalIndexOf(vertex) != -1)
			orderManager.remove(vertex, true);

		int newIndex = getBestIndex(vertex);
		if (newIndex >= 0) {
			orderManager.insertVertexAtIndex(newIndex, vertex);
		} else {
			orderManager.addLast(vertex);
		}
	}
}
