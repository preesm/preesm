/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/
 
package org.ietr.preesm.plugin.abc.taskscheduling;

import java.util.Random;

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
	private Random random;
	
	public TaskSwitcher() {
		super();
		random = new Random(System.nanoTime());
	}

	@Override
	public void setOrderManager(SchedOrderManager orderManager) {
		super.setOrderManager(orderManager);
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
				int sourceIndex = latePred+1;
				int targetIndex = earlySuc;
				if(targetIndex == -1){
					targetIndex = orderManager.getTotalOrder().size();
				}
				
				if(targetIndex-sourceIndex > 0){
					int randomVal = random.nextInt(targetIndex-sourceIndex);
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
