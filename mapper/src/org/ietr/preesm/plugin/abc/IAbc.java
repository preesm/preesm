/**
 * 
 */
package org.ietr.preesm.plugin.abc;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.timekeeper.ITimeKeeper;

/**
 * Clarifies the simulator API
 * 
 * @author mpelcat
 */
public interface IAbc {

	/**
	 * Gets the architecture
	 */
	public IArchitecture getArchitecture();

	public MapperDAG getDAG();

	/**
	 * Gets the effective operator of the vertex. NO_OPERATOR if not set
	 */
	public ArchitectureComponent getEffectiveComponent(MapperDAGVertex vertex);

	/**
	 * Gives the implementation time of the implementation if possible. If current
	 * implementation information is not enough to calculate this timing, returns
	 * TIME_UNKNOWN
	 */
	public int getFinalTime();

	/**
	 * Gives the final time of the given vertex in the current implementation. If
	 * current implementation information is not enough to calculate this timing,
	 * returns TIME_UNKNOWN
	 */
	public int getFinalTime(MapperDAGVertex vertex);

	/**
	 * Gives the implementation time on the given operator if possible. It
	 * considers a partially implanted graph and ignores the non implanted
	 * vertices
	 */
	public int getFinalTime(ArchitectureComponent component);

	/**
	 * Gets the rank of the given vertex on its operator. -1 if the vertex has
	 * no rank
	 */
	public int getSchedulingOrder(MapperDAGVertex vertex);

	/**
	 * Gets the total rank of the given vertex. -1 if the vertex has no rank
	 */
	public int getSchedulingTotalOrder(MapperDAGVertex vertex);

	/**
	 * Internal time keeper. Should be used for tests only
	 */
	public ITimeKeeper getTimeKeeper();

	/**
	 * Gives the B level of the given vertex in the current implementation. If
	 * current implementation information is not enough to calculate this timing,
	 * returns TIME_UNKNOWN
	 * 
	 * B Level is the time between the vertex start and the total end of
	 * execution
	 */
	public int getBLevel(MapperDAGVertex vertex);
	
	/**
	 * Gives the T level of the given vertex in the current implementation. If
	 * current implementation information is not enough to calculate this timing,
	 * returns TIME_UNKNOWN
	 * 
	 * T Level is the time between the start of execution and the vertex start
	 */
	public int getTLevel(MapperDAGVertex vertex);

	/**
	 * Gets the cost of the given vertex in the implementation
	 */
	public int getCost(MapperDAGVertex vertex);

	/**
	 * Gets the cost of the given vertex in the implementation
	 */
	public int getCost(MapperDAGEdge edge);

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
	public void plotImplementation();

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
}
