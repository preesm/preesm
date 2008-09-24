package org.ietr.preesm.plugin.transforms;

import org.ietr.preesm.core.task.IGraphTransformation;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.optimisations.loops.pipelining.SimplePipeline;

public class LoopPipelining implements IGraphTransformation{

	@Override
	public TaskResult transform(SDFGraph algorithm, TextParameters params) {
		SDFGraph inGraph = algorithm.clone();
		SimplePipeline pipeliner = new SimplePipeline();
		pipeliner.extractPipeline(inGraph);
		TaskResult result = new TaskResult();
		result.setSDF(inGraph);
		return result;
	}

}
