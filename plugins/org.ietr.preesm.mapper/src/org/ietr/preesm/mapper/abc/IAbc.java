/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2012 - 2014)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/

package org.ietr.preesm.mapper.abc;

import java.util.List;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.mapper.abc.order.VertexOrderList;
import org.ietr.preesm.mapper.abc.taskscheduling.AbstractTaskSched;
import org.ietr.preesm.mapper.gantt.GanttData;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;

// TODO: Auto-generated Javadoc
/**
 * Abc (architecture benchmark computer) keeps an internal graph (implementation) and is assigned an external graph (dag). The internal graph receives
 * dynamically special vertices (tansfers, overheads, send/receive...) to reflect graph simulation.
 *
 * @author mpelcat
 */
public interface IAbc {

  /**
   * Gets the architecture and scenario.
   *
   * @return the architecture
   */
  public Design getArchitecture();

  /**
   * Gets the scenario.
   *
   * @return the scenario
   */
  public PreesmScenario getScenario();

  /**
   * Gets the dag.
   *
   * @return the dag
   */
  public MapperDAG getDAG();

  /**
   * Gets the effective operator of the vertex. NO_OPERATOR if not set
   *
   * @param vertex
   *          the vertex
   * @return the effective component
   */
  public ComponentInstance getEffectiveComponent(MapperDAGVertex vertex);

  /**
   * Updates the internal state so as to allow final cost processing.
   */
  public void updateFinalCosts();

  /**
   * Gives the implementation time of the implementation if possible. If current implementation information is not enough to calculate this timing, returns
   * TIME_UNKNOWN
   *
   * @return the final cost
   */
  public long getFinalCost();

  /**
   * Gives the final time of the given vertex in the current implementation. If current implementation information is not enough to calculate this timing,
   * returns TIME_UNKNOWN
   *
   * @param vertex
   *          the vertex
   * @return the final cost
   */
  public long getFinalCost(MapperDAGVertex vertex);

  /**
   * Gives the implementation time on the given operator if possible. It considers a partially mapped graph and ignores the non mapped vertices
   *
   * @param component
   *          the component
   * @return the final cost
   */
  public long getFinalCost(ComponentInstance component);

  /**
   * Gets the rank of the given vertex on its operator. -1 if the vertex has no rank
   *
   * @param vertex
   *          the vertex
   * @return the scheduling order
   */
  public int getSchedulingOrder(MapperDAGVertex vertex);

  /**
   * Gets the total rank of the given vertex. -1 if the vertex has no rank
   *
   * @param vertex
   *          the vertex
   * @return the sched total order
   */
  public int getSchedTotalOrder(MapperDAGVertex vertex);

  /**
   * Gets the current total order of the ABC.
   *
   * @return the total order
   */
  public VertexOrderList getTotalOrder();

  /**
   * Reorders the implementation using the given total order or trying to find the best schedule.
   *
   * @param totalOrder
   *          the total order
   */
  public void reschedule(VertexOrderList totalOrder);

  // public void reschedule(List<MapperDAGVertex> alreadyRescheduled);

  /**
   * Gets the cost of the given vertex in the implementation.
   *
   * @param vertex
   *          the vertex
   * @return the cost
   */
  public long getCost(MapperDAGVertex vertex);

  /**
   * Gets the cost of the given vertex in the implementation.
   *
   * @param edge
   *          the edge
   * @return the cost
   */
  public long getCost(MapperDAGEdge edge);

  /**
   * maps the vertex on the operator the rank is the scheduling order. The current rank is maintained in simulator. User can choose to update the rank to the
   * current one or to keep the rank set during last implementation (changing or not the scheduling order). User can also choose to remap the whole group or
   * only the current vertex.
   *
   * @param vertex
   *          the vertex
   * @param operator
   *          the operator
   * @param updateRank
   *          the update rank
   * @param remapGroup
   *          the remap group
   * @throws WorkflowException
   *           the workflow exception
   */
  public void map(MapperDAGVertex vertex, ComponentInstance operator, boolean updateRank, boolean remapGroup) throws WorkflowException;

  /**
   * Unmap.
   *
   * @param dagvertex
   *          the dagvertex
   */
  public void unmap(MapperDAGVertex dagvertex);

  /**
   * maps all the vertices on the given operator.
   *
   * @param operator
   *          the operator
   * @return true, if successful
   * @throws WorkflowException
   *           the workflow exception
   */
  public boolean mapAllVerticesOnOperator(ComponentInstance operator) throws WorkflowException;

  /**
   * Checks in the vertex implementation properties if it can be mapped on the given operator.
   *
   * @param vertex
   *          the vertex
   * @param operator
   *          the operator
   * @param protectGroupMapping
   *          the protect group mapping
   * @return true, if is mapable
   * @throws WorkflowException
   *           the workflow exception
   */
  public boolean isMapable(MapperDAGVertex vertex, ComponentInstance operator, boolean protectGroupMapping) throws WorkflowException;

  /**
   * Extracting from the Abc information the data to display in the Gantt chart.
   *
   * @return the gantt data
   */
  public GanttData getGanttData();

  /**
   * Unmaps all vertices in internal implementation.
   */
  public void resetImplementation();

  /**
   * Gets internal implementation graph. Use only for debug!
   *
   * @return the implementation
   */
  public MapperDAG getImplementation();

  /**
   * Returns the implementation vertex corresponding to the DAG vertex.
   *
   * @param vertex
   *          the vertex
   * @return the mapper DAG vertex
   */
  public MapperDAGVertex translateInImplementationVertex(MapperDAGVertex vertex);

  /**
   * Unmaps all vertices in both DAG and implementation.
   */
  public void resetDAG();

  /**
   * Sets the DAG as current DAG and retrieves all implementation to calculate timings.
   *
   * @param dag
   *          the new dag
   * @throws WorkflowException
   *           the workflow exception
   */
  public void setDAG(MapperDAG dag) throws WorkflowException;

  /**
   * Sets the total orders in the dag.
   */
  public void retrieveTotalOrder();

  /**
   * Gets the type of the current ABC.
   *
   * @return the type
   */
  public AbcType getType();

  /**
   * Gets the edge sched type.
   *
   * @return the edge sched type
   */
  public EdgeSchedType getEdgeSchedType();

  /**
   * Sets the task scheduler of the current ABC.
   *
   * @param taskSched
   *          the new task scheduler
   */
  public void setTaskScheduler(AbstractTaskSched taskSched);

  /**
   * Reschedule all the transfers generated during mapping.
   *
   * @param currentvertex
   *          the currentvertex
   * @param preferedOperator
   *          the prefered operator
   * @param protectGroupMapping
   *          the protect group mapping
   * @return the component instance
   * @throws WorkflowException
   *           the workflow exception
   */
  // public void rescheduleTransfers(List<MapperDAGVertex> orderlist);

  /**
   * Looks for an operator able to execute currentvertex (preferably the given operator or an operator with same type) If the boolean protectGroupMapping is
   * true and at least one vertex is mapped in the current vertex group, this unique operator is compared to the prefered one. Otherwise, the prefered operator
   * is checked of belonging to available operators of the group.
   */
  public ComponentInstance findOperator(MapperDAGVertex currentvertex, ComponentInstance preferedOperator, boolean protectGroupMapping)
      throws WorkflowException;

  /**
   * Looks for operators able to execute currentvertex. If the boolean protectGroupMapping is true and at least one vertex is mapped in the current vertex
   * group, this unique operator is returned. Otherwise, the intersection of the available operators for the group is returned.
   *
   * @param currentvertex
   *          the currentvertex
   * @param protectGroupMapping
   *          the protect group mapping
   * @return the candidate operators
   * @throws WorkflowException
   *           the workflow exception
   */
  public List<ComponentInstance> getCandidateOperators(MapperDAGVertex currentvertex, boolean protectGroupMapping) throws WorkflowException;

}
