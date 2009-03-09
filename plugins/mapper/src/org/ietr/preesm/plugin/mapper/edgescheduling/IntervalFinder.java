/**
 * 
 */
package org.ietr.preesm.plugin.mapper.edgescheduling;

import java.util.logging.Level;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.abc.order.Schedule;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.TimingVertexProperty;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;

/**
 * During edge scheduling, one needs to find intervals to fit the transfers.
 * This class deals with intervals in the transfer scheduling
 * 
 * @author mpelcat
 */
public class IntervalFinder {

	/**
	 * Contains the rank list of all the vertices in an implementation
	 */
	private SchedOrderManager orderManager = null;

	private static class FindType {
		public static final FindType largestFreeInterval = new FindType();
		public static final FindType earliestNonNullInterval = new FindType();
	}

	public IntervalFinder(SchedOrderManager orderManager) {
		super();
		this.orderManager = orderManager;
	}

	/**
	 * Finds the largest free interval in a schedule
	 */
	public Interval findLargestFreeInterval(ArchitectureComponent component,
			MapperDAGVertex minVertex, MapperDAGVertex maxVertex) {

		return findInterval(component,
				minVertex, maxVertex, FindType.largestFreeInterval);

	}
	
	public Interval findEarliestNonNullInterval(ArchitectureComponent component,
			MapperDAGVertex minVertex, MapperDAGVertex maxVertex) {

		return findInterval(component,
				minVertex, maxVertex, FindType.earliestNonNullInterval);

	}

	/**
	 * Finds the largest free interval in a schedule
	 */
	public Interval findInterval(ArchitectureComponent component,
			MapperDAGVertex minVertex, MapperDAGVertex maxVertex, FindType type) {

		Schedule schedule = orderManager.getSchedule(component);

		long minIndexVertexEndTime = -1;
		int minIndex = -1;

		if (minVertex != null) {
			minIndex = orderManager.totalIndexOf(minVertex);

			TimingVertexProperty props = minVertex.getTimingVertexProperty();
			if (props.getTlevel() >= 0) {
				minIndexVertexEndTime = props.getTlevel() + props.getCost();
			}
		}

		int maxIndex = Integer.MAX_VALUE;
		if (maxVertex != null)
			maxIndex = orderManager.totalIndexOf(maxVertex);

		Interval oldInt = new Interval(0, 0, -1);
		Interval newInt = null;
		Interval biggestFreeInterval = new Interval(-1, -1, 0);

		if (schedule != null) {
			for (MapperDAGVertex v : schedule) {
				TimingVertexProperty props = v.getTimingVertexProperty();
				if (props.getTlevel() >= 0) {
					// newInt is the interval corresponding to the execution of
					// the vertex v
					newInt = new Interval(props.getCost(), props.getTlevel(),
							orderManager.totalIndexOf(v));

					if (type == FindType.largestFreeInterval) {
						if (newInt.getTotalOrderIndex() > minIndex
								&& newInt.getTotalOrderIndex() <= maxIndex) {
							long oldEnd = oldInt.getStartTime()
									+ oldInt.getDuration();
							long available = Math.max(minIndexVertexEndTime,
									oldEnd);
							long freeIntervalSize = newInt.getStartTime()
									- available;

							if (freeIntervalSize > biggestFreeInterval
									.getDuration()) {
								// The free interval takes the index of its
								// following task v.
								// Inserting a vertex in this interval means
								// inserting it before v.
								biggestFreeInterval = new Interval(
										freeIntervalSize, available, newInt
												.getTotalOrderIndex());
							}
						}
					}
					else if (type == FindType.earliestNonNullInterval) {
						if (newInt.getTotalOrderIndex() > minIndex
								&& newInt.getTotalOrderIndex() <= maxIndex) {
							long oldEnd = oldInt.getStartTime()
									+ oldInt.getDuration();
							long available = Math.max(minIndexVertexEndTime,
									oldEnd);
							long freeIntervalSize = newInt.getStartTime()
									- available;

							if (freeIntervalSize > 0) {
								// The free interval takes the index of its
								// following task v.
								// Inserting a vertex in this interval means
								// inserting it before v.
								biggestFreeInterval = new Interval(
										freeIntervalSize, available, newInt
												.getTotalOrderIndex());
							}
						}
					}
					oldInt = newInt;
				}
			}
		}

		return biggestFreeInterval;

	}

	public void displayCurrentSchedule(TransferVertex vertex,
			MapperDAGVertex source) {

		ArchitectureComponent component = vertex
				.getImplementationVertexProperty().getEffectiveComponent();
		Schedule schedule = orderManager.getSchedule(component);

		TimingVertexProperty sourceProps = source.getTimingVertexProperty();
		long availability = sourceProps.getTlevel() + sourceProps.getCost();
		if (sourceProps.getTlevel() < 0)
			availability = -1;

		String trace = "schedule of " + vertex.getName() + " available at "
				+ availability + ": ";

		if (schedule != null) {
			for (MapperDAGVertex v : schedule) {
				TimingVertexProperty props = v.getTimingVertexProperty();
				if (props.getTlevel() >= 0)
					trace += "<" + props.getTlevel() + ","
							+ (props.getTlevel() + props.getCost()) + ">";
			}
		}

		PreesmLogger.getLogger().log(Level.INFO, trace);
	}
}
