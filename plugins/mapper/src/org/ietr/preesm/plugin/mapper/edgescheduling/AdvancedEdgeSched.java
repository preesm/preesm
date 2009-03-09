/**
 * 
 */
package org.ietr.preesm.plugin.mapper.edgescheduling;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;

/**
 * A complex edge scheduler
 * 
 * @author mpelcat
 */
public class AdvancedEdgeSched extends AbstractEdgeSched {

	private IntervalFinder intervalFinder = null;
	
	public AdvancedEdgeSched(SchedOrderManager orderManager) {
		super(orderManager);
		
		intervalFinder = new IntervalFinder(orderManager);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	@Override
	public void schedule(TransferVertex vertex, MapperDAGVertex source, MapperDAGVertex target) {

		ArchitectureComponent component = vertex.getImplementationVertexProperty().getEffectiveComponent();
		//intervalFinder.displayCurrentSchedule(vertex, source);
		Interval earliestInterval = intervalFinder.findEarliestNonNullInterval(component, source, target);
		
		if(earliestInterval.getDuration() >= 0){
			orderManager.insertVertexAtIndex(earliestInterval.getTotalOrderIndex(), vertex);
		}
		else{
			orderManager.insertVertexAfter(source, vertex);
		}
		
		if(source.getCorrespondingSDFVertex().getId().equals("PreEncode")){
			int i=0;
			i++;
		}


	}

	public EdgeSchedType getEdgeSchedType(){
		return EdgeSchedType.Advanced;
	}

}
