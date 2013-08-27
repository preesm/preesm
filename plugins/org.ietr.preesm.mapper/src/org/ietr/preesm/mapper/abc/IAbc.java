/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.mapper.abc;

import java.util.List;

import net.sf.dftools.architecture.slam.ComponentInstance;
import net.sf.dftools.architecture.slam.Design;
import net.sf.dftools.workflow.WorkflowException;

import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.types.IMapperAbc;
import org.ietr.preesm.mapper.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.mapper.abc.order.VertexOrderList;
import org.ietr.preesm.mapper.abc.taskscheduling.AbstractTaskSched;
import org.ietr.preesm.mapper.gantt.GanttData;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;

/**
 * Abc (architecture benchmark computer) keeps an internal graph (implementation)
 * and is assigned an external graph (dag). The internal graph receives dynamically
 * special vertices (tansfers, overheads, send/receive...) to reflect graph simulation.
 * 
 * @author mpelcat
 */
public interface IAbc extends IMapperAbc {

	/**
	 * Gets the architecture and scenario
	 */
	public Design getArchitecture();

	public PreesmScenario getScenario();

	public MapperDAG getDAG();

	/**
	 * Gets the effective operator of the vertex. NO_OPERATOR if not set
	 */
	public ComponentInstance getEffectiveComponent(MapperDAGVertex vertex);

	/**
	 * Updates the internal state so as to allow final cost processing
	 */
	public void updateFinalCosts();

	/**
	 * Gives the implementation time of the implementation if possible. If
	 * current implementation information is not enough to calculate this
	 * timing, returns TIME_UNKNOWN
	 */
	public long getFinalCost();

	/**
	 * Gives the final time of the given vertex in the current implementation.
	 * If current implementation information is not enough to calculate this
	 * timing, returns TIME_UNKNOWN
	 */
	public long getFinalCost(MapperDAGVertex vertex);

	/**
	 * Gives the implementation time on the given operator if possible. It
	 * considers a partially mapped graph and ignores the non mapped vertices
	 */
	public long getFinalCost(ComponentInstance component);

	/**
	 * Gets the rank of the given vertex on its operator. -1 if the vertex has
	 * no rank
	 */
	public int getSchedulingOrder(MapperDAGVertex vertex);

	/**
	 * Gets the total rank of the given vertex. -1 if the vertex has no rank
	 */
	public int getSchedTotalOrder(MapperDAGVertex vertex);

	/**
	 * Gets the current total order of the ABC
	 */
	public VertexOrderList getTotalOrder();

	/**
	 * Reorders the implementation using the given total order or trying to find
	 * the best schedule
	 */
	public void reschedule(VertexOrderList totalOrder);

	//public void reschedule(List<MapperDAGVertex> alreadyRescheduled);

	/**
	 * Gets the cost of the given vertex in the implementation
	 */
	public long getCost(MapperDAGVertex vertex);

	/**
	 * Gets the cost of the given vertex in the implementation
	 */
	public long getCost(MapperDAGEdge edge);

	/**
	 * maps the vertex on the operator the rank is the scheduling order. The
	 * current rank is maintained in simulator. User can choose to update the
	 * rank to the current one or to keep the sank set during last
	 * implementation
	 * @throws WorkflowException 
	 */
	public void map(MapperDAGVertex vertex, ComponentInstance operator,
			boolean updateRank) throws WorkflowException;

	public void unmap(MapperDAGVertex dagvertex);

	/**
	 * maps all the vertices on the given operator
	 * @throws WorkflowException 
	 */
	public boolean mapAllVerticesOnOperator(ComponentInstance operator) throws WorkflowException;

	/**
	 * Checks in the vertex implementation properties if it can be mapped on the
	 * given operator
	 * @throws WorkflowException 
	 */
	public boolean isMapable(MapperDAGVertex vertex, ComponentInstance operator) throws WorkflowException;

	/**
	 * Extracting from the Abc information the data to display in the Gantt chart
	 */
	public GanttData getGanttData();

	/**
	 * Unmaps all vertices in internal implementation
	 */
	public void resetImplementation();

	/**
	 * Gets internal implementation graph.
	 * Use only for debug!
	 */
	public MapperDAG getImplementation();
	
	/**
	 * Returns the implementation vertex corresponding to the DAG vertex
	 */
	public MapperDAGVertex translateInImplementationVertex(MapperDAGVertex vertex);

	/**
	 * Unmaps all vertices in both DAG and implementation
	 */
	public void resetDAG();

	/**
	 * Sets the DAG as current DAG and retrieves all implementation to calculate
	 * timings
	 * @throws WorkflowException 
	 */
	public void setDAG(MapperDAG dag) throws WorkflowException;

	/**
	 * Sets the total orders in the dag
	 */
	public void retrieveTotalOrder();

	/**
	 * Gets the type of the current ABC
	 */
	public AbcType getType();

	public EdgeSchedType getEdgeSchedType();

	/**
	 * Sets the task scheduler of the current ABC
	 */
	public void setTaskScheduler(AbstractTaskSched taskSched);

	/**
	 * Reschedule all the transfers generated during mapping
	 */
	// public void rescheduleTransfers(List<MapperDAGVertex> orderlist);

	/**
	 * Looks for an operator able to execute currentvertex (preferably the given
	 * operator)
	 * @throws WorkflowException 
	 */
	public ComponentInstance findOperator(MapperDAGVertex currentvertex,
			ComponentInstance preferedOperator) throws WorkflowException;

	/**
	 * Looks for operators able to execute currentvertex
	 * @throws WorkflowException 
	 */
	public List<ComponentInstance> getCandidateOperators(
			MapperDAGVertex currentvertex) throws WorkflowException;

}
