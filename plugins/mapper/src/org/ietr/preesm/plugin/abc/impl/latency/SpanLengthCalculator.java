/**
 * 
 */
package org.ietr.preesm.plugin.abc.impl.latency;

import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.plugin.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.plugin.abc.taskscheduling.TaskSchedType;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;

/**
 * Using infinite homogeneous simulation to calculate the span length of a dag
 * 
 * @author mpelcat
 */
public class SpanLengthCalculator extends InfiniteHomogeneousAbc {

	public static final String DAG_SPAN = "dag span length";

	public SpanLengthCalculator(EdgeSchedType edgeSchedType, MapperDAG dag,
			MultiCoreArchitecture archi, TaskSchedType taskSchedType,
			IScenario scenario) {
		super(edgeSchedType, dag, archi, taskSchedType, scenario);

		this.updateTimings();

		// The span corresponds to the final latency of an infinite homogeneous
		// simulation
		dag.getPropertyBean().setValue(DAG_SPAN, (Long) getFinalLatency());
	}

	@Override
	protected void setEdgeCost(MapperDAGEdge edge) {
		edge.getTimingEdgeProperty().setCost(1);
	}
}
