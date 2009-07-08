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

package org.ietr.preesm.plugin.abc;

import java.util.List;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.types.IMapperAbc;
import org.ietr.preesm.plugin.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.plugin.abc.order.Schedule;
import org.ietr.preesm.plugin.abc.taskscheduling.TaskSchedType;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.plot.IImplementationPlotter;

/**
 * Clarifies the simulator API
 * 
 * @author mpelcat
 */
public interface IAbc extends IMapperAbc {

	/**
	 * Gets the architecture and scenario
	 */
	public MultiCoreArchitecture getArchitecture();
	public IScenario getScenario();

	public MapperDAG getDAG();

	/**
	 * Gets the effective operator of the vertex. NO_OPERATOR if not set
	 */
	public ArchitectureComponent getEffectiveComponent(MapperDAGVertex vertex);

	/**
	 * Updates the internal state so as to allow final cost processing
	 */
	public void updateFinalCosts();
	
	/**
	 * Gives the implementation time of the implementation if possible. If current
	 * implementation information is not enough to calculate this timing, returns
	 * TIME_UNKNOWN
	 */
	public long getFinalCost();

	/**
	 * Gives the final time of the given vertex in the current implementation. If
	 * current implementation information is not enough to calculate this timing,
	 * returns TIME_UNKNOWN
	 */
	public long getFinalCost(MapperDAGVertex vertex);

	/**
	 * Gives the implementation time on the given operator if possible. It
	 * considers a partially implanted graph and ignores the non implanted
	 * vertices
	 */
	public long getFinalCost(ArchitectureComponent component);

	/**
	 * Returns the sum of execution times on the given component
	 */
	public long getLoad(ArchitectureComponent component);

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
	 * Gets the current total schedule of the ABC
	 */
	public Schedule getTotalOrder();

	/**
	 * Reorders the implementation using the given total order
	 */
	public void reorder(List<String> totalOrder);

	/**
	 * Gets the cost of the given vertex in the implementation
	 */
	public long getCost(MapperDAGVertex vertex);

	/**
	 * Gets the cost of the given vertex in the implementation
	 */
	public long getCost(MapperDAGEdge edge);

	/**
	 * Implants the vertex on the operator the rank is the scheduling order. The
	 * current rank is maintained in simulator. User can choose to update the
	 * rank to the current one or to keep the sank set during last implementation
	 */
	public void implant(MapperDAGVertex vertex, Operator operator,
			boolean updateRank);

	/**
	 * implants all the vertices on the given operator
	 */
	public boolean implantAllVerticesOnOperator(Operator operator);

	/**
	 * Checks in the vertex implementation properties if it can be implanted on
	 * the given operator
	 */
	public boolean isImplantable(MapperDAGVertex vertex, Operator operator);

	/**
	 * Plots the current implementation
	 */
	public IImplementationPlotter plotImplementation(boolean delegateDisplay);

	/**
	 * Unimplants all vertices in internal implementation
	 */
	public void resetImplementation();

	/**
	 * Unimplants all vertices in both DAG and implementation
	 */
	public void resetDAG();

	/**
	 * Sets the DAG as current DAG and retrieves all implementation to calculate
	 * timings
	 */
	public void setDAG(MapperDAG dag);

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
	public void resetTaskScheduler(TaskSchedType taskSchedType);

	/**
	 * Reschedule all the transfers generated during mapping
	 */
	public void rescheduleTransfers(List<MapperDAGVertex> orderlist);
}
