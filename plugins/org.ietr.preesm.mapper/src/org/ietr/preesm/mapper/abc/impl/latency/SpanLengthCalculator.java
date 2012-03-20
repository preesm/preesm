/**
 * 
 */
package org.ietr.preesm.mapper.abc.impl.latency;

import net.sf.dftools.architecture.slam.Design;
import net.sf.dftools.workflow.WorkflowException;

import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.taskscheduling.TaskSchedType;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.params.AbcParameters;

/**
 * Using infinite homogeneous simulation to calculate the span length of a dag
 * 
 * @author mpelcat
 */
public class SpanLengthCalculator extends InfiniteHomogeneousAbc {

	public static final String DAG_SPAN = "dag span length";

	public SpanLengthCalculator(AbcParameters params, MapperDAG dag,
			Design archi, TaskSchedType taskSchedType, PreesmScenario scenario) throws WorkflowException {
		super(params, dag, archi, taskSchedType, scenario);

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
