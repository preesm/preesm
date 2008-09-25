package org.ietr.preesm.plugin.mapper.timekeeper;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * The interface of the ABC time keeper which calculates and stores time
 * data on tasks
 * 
 * @author mpelcat
 */
public interface ITimeKeeper {

	/**
	 * Gives the implementation time of the implementation if possible. If current
	 * implementation information is not enough to calculate this timing, returns
	 * TIME_UNKNOWN
	 */
	public int getFinalTime(MapperDAG implementation);

	/**
	 * Gives the implementation time on the given operator if possible. It
	 * considers a partially implanted graph and ignores the non implanted
	 * vertices If current implementation information is not enough to calculate
	 * the timings, returns TIME_UNKNOWN but this should not occur
	 */
	public int getFinalTime(MapperDAG implementation,
			ArchitectureComponent component);

	/**
	 * Gives the final time of the given vertex in the current implementation. If
	 * current implementation information is not enough to calculate this timing,
	 * returns TIME_UNKNOWN but this should not occur
	 */
	public int getFinalTime(MapperDAGVertex vertex);

	/**
	 * Updates the whole timing info of the implementation
	 */
	public void updateTLevels(MapperDAG implementation);

	/**
	 * Updates the whole timing info of the implementation
	 */
	public void updateTandBLevels(MapperDAG implementation);
	public void updateTandBLevels(MapperDAG implementation, MapperDAGVertex vertex);
	/**
	 * Resets the time keeper timings of the whole DAG
	 */
	public void resetTimings(MapperDAG implementation);
}
