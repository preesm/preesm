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
	 * Time interval for the transfer scheduling
	 */
	public class Interval {
		private int startTime;
		private int duration;
		private TransferVertex vertex;

		public Interval(int duration, int startTime, TransferVertex vertex) {
			super();
			this.duration = duration;
			this.startTime = startTime;
			this.vertex = vertex;
		}
	}
	
	/**
	 * Contains the rank list of all the vertices in an implementation
	 */
	private SchedOrderManager orderManager = null;
	
	public IntervalFinder(SchedOrderManager orderManager) {
		super();
		this.orderManager = orderManager;
	}

	/**
	 * Returns the earliest transfer vertex following an empty interval of minimal size "size" 
	 */
	public TransferVertex find(ArchitectureComponent component, int after, int size){

		Schedule schedule = orderManager.getSchedule(component);

		Interval oldInt = new Interval(0,0,null);
		Interval newInt = null;
		
		if(schedule != null){
			for(MapperDAGVertex v : schedule){
				TimingVertexProperty props = v.getTimingVertexProperty();
				if(props.getTlevel() >= 0){
					newInt = new Interval(props.getCost(),props.getTlevel(),(TransferVertex)v);
	
					int oldEnd = oldInt.startTime + oldInt.duration;
					int availableInterval = newInt.startTime - oldEnd;
					if(oldEnd < after)availableInterval = newInt.startTime - after;
						
					if(size <= availableInterval){
						return newInt.vertex;
					}
					
					oldInt = newInt;
				}
					
			}
		}
		
		return null;
		
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
