/**
 * 
 */
package org.ietr.preesm.plugin.mapper.edgescheduling;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.abc.order.Schedule;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.TimingVertexProperty;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;

/**
 * During edge scheduling, one needs to find intervals to fit the transfers. This class
 * deals with intervals in the transfer scheduling
 * 
 * @author mpelcat
 */
public class IntervalFinder {
	
	/**
	 * Contains the rank list of all the vertices in an implementation
	 */
	private SchedOrderManager orderManager = null;
	
	public IntervalFinder(SchedOrderManager orderManager) {
		super();
		this.orderManager = orderManager;
	}

	/**
	 * Finds the largest free interval in a medium schedule
	 */
	public Interval findLargestFreeInterval(ArchitectureComponent component, MapperDAGVertex minVertex, MapperDAGVertex maxVertex){

		int minIndex = orderManager.totalIndexOf(minVertex);
		int maxIndex = orderManager.totalIndexOf(maxVertex);;
		Schedule schedule = orderManager.getSchedule(component);

		MapperDAGVertex minIndexVertex = orderManager.getVertex(minIndex);
		int minIndexVertexEndTime = -1;
		
		if(minIndexVertex != null){
			TimingVertexProperty props = minIndexVertex.getTimingVertexProperty();
			if(props.getTlevel() >= 0){
				minIndexVertexEndTime = props.getTlevel() + props.getCost();
			}
		}
		
		Interval oldInt = new Interval(0,0,-1);
		Interval newInt = null;
		Interval biggestFreeInterval = new Interval(-1,-1,0);
		
		if(schedule != null){
			for(MapperDAGVertex v : schedule){
				TimingVertexProperty props = v.getTimingVertexProperty();
				if(props.getTlevel() >= 0){
					newInt = new Interval(props.getCost(),props.getTlevel(),orderManager.totalIndexOf(v));
	
					if(newInt.getTotalOrderIndex() > minIndex && newInt.getTotalOrderIndex() <= maxIndex){
						int oldEnd = oldInt.getStartTime() + oldInt.getDuration();
						int available = Math.max(minIndexVertexEndTime, oldEnd);
						int freeIntervalSize = newInt.getStartTime() - available;
						
						if(freeIntervalSize > biggestFreeInterval.getDuration()){
							biggestFreeInterval = new Interval(freeIntervalSize,available,newInt.getTotalOrderIndex());
						}
					}
					oldInt = newInt;	
				}
			}
		}
		
		return biggestFreeInterval;
		
	}
	
	public void displayCurrentSchedule(TransferVertex vertex, MapperDAGVertex source){

		ArchitectureComponent component = vertex.getImplementationVertexProperty().getEffectiveComponent();
		Schedule schedule = orderManager.getSchedule(component);

		TimingVertexProperty sourceProps = source.getTimingVertexProperty();
		int availability = sourceProps.getTlevel() + sourceProps.getCost();
		if(sourceProps.getTlevel() < 0) 
			availability = -1;
		
		String trace = "schedule of " + vertex.getName() + " available at " + availability + ": ";
		
		if(schedule != null){
			for(MapperDAGVertex v : schedule){
				TimingVertexProperty props = v.getTimingVertexProperty();
				if(props.getTlevel()>=0)
					trace += "<" + props.getTlevel() + "," + (props.getTlevel() + props.getCost()) + ">";
			}
		}

		PreesmLogger.getLogger().log(Level.INFO,trace);
	}
}
