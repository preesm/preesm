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

package org.ietr.preesm.plugin.abc.edgescheduling;

import java.util.List;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.order.IScheduleElement;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
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
		@Override
		public String toString() {
			if(this == largestFreeInterval) return "largestFreeInterval";
			if(this == earliestNonNullInterval) return "largestFreeInterval";
			return "";
		}
	}

	public IntervalFinder(SchedOrderManager orderManager) {
		super();
		this.orderManager = orderManager;
	}

	/**
	 * Finds the largest free interval in a schedule
	 */
	public Interval findLargestFreeInterval(ArchitectureComponent component,
			IScheduleElement minVertex, IScheduleElement maxVertex) {

		return findInterval(component, minVertex, maxVertex,
				FindType.largestFreeInterval);

	}

	public Interval findEarliestNonNullInterval(
			ArchitectureComponent component, MapperDAGVertex minVertex,
			MapperDAGVertex maxVertex) {

		return findInterval(component, minVertex, maxVertex,
				FindType.earliestNonNullInterval);

	}

	/**
	 * Finds the largest free interval in a schedule between a minVertex and a maxVertex
	 */
	public Interval findInterval(ArchitectureComponent component,
			IScheduleElement minVertex, IScheduleElement maxVertex, FindType type) {

		List<MapperDAGVertex> schedule = orderManager.getVertexList(component);

		long minIndexVertexEndTime = -1;
		int minIndex = -1;

		if (minVertex != null) {
			minIndex = orderManager.totalIndexOf(minVertex);

			TimingVertexProperty props = minVertex.getTimingVertexProperty();
			if (props.getNewtLevel() >= 0) {
				minIndexVertexEndTime = props.getNewtLevel() + props.getCost();
			}
		}

		int maxIndex = Integer.MAX_VALUE;
		if (maxVertex != null) {
			maxIndex = orderManager.totalIndexOf(maxVertex);
		} else {
			maxIndex = orderManager.getTotalOrder().size();
		}

		Interval oldInt = new Interval(0, 0, -1);
		Interval newInt = null;
		Interval freeInterval = new Interval(-1, -1, 0);

		if (schedule != null) {
			for (MapperDAGVertex v : schedule) {
				TimingVertexProperty props = v.getTimingVertexProperty();
				
				// If we have the current vertex tLevel
				if (props.getNewtLevel() >= 0) {
					
					// newInt is the interval corresponding to the execution of
					// the vertex v: a non free interval
					newInt = new Interval(props.getCost(),
							props.getNewtLevel(), orderManager.totalIndexOf(v));

					if (type == FindType.largestFreeInterval) {
						// Verifying that newInt is in the interval of search
						if (newInt.getTotalOrderIndex() > minIndex
								&& newInt.getTotalOrderIndex() <= maxIndex) {
							
							// end of the preceding non free interval
							long oldEnd = oldInt.getStartTime()
									+ oldInt.getDuration();
							// latest date between the end of minVertex and the end of oldInt
							long available = Math.max(minIndexVertexEndTime,
									oldEnd);
							// Computing the size of the free interval
							long freeIntervalSize = newInt.getStartTime()
									- available;

							if (freeIntervalSize > freeInterval
									.getDuration()) {
								// The free interval takes the index of its
								// following task v.
								// Inserting a vertex in this interval means
								// inserting it before v.
								freeInterval = new Interval(
										freeIntervalSize, available, newInt
												.getTotalOrderIndex());
							}
						}
					} else if (type == FindType.earliestNonNullInterval) {
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
								freeInterval = new Interval(
										freeIntervalSize, available, newInt
												.getTotalOrderIndex());
								break;
							}
						}
					}
					oldInt = newInt;
				}
			}
		}

		return freeInterval;

	}

	public void displayCurrentSchedule(TransferVertex vertex,
			MapperDAGVertex source) {

		ArchitectureComponent component = vertex
				.getImplementationVertexProperty().getEffectiveComponent();
		List<MapperDAGVertex> schedule = orderManager.getVertexList(component);

		TimingVertexProperty sourceProps = source.getTimingVertexProperty();
		long availability = sourceProps.getNewtLevel() + sourceProps.getCost();
		if (sourceProps.getNewtLevel() < 0)
			availability = -1;

		String trace = "schedule of " + vertex.getName() + " available at "
				+ availability + ": ";

		if (schedule != null) {
			for (IScheduleElement v : schedule) {
				TimingVertexProperty props = v.getTimingVertexProperty();
				if (props.getNewtLevel() >= 0)
					trace += "<" + props.getNewtLevel() + ","
							+ (props.getNewtLevel() + props.getCost()) + ">";
			}
		}

		PreesmLogger.getLogger().log(Level.INFO, trace);
	}

	public SchedOrderManager getOrderManager() {
		return orderManager;
	}
}
