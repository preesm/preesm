package org.ietr.preesm.plugin.transforms;

import org.ietr.preesm.core.task.IGraphTransformation;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.optimisations.clustering.internalisation.SDFInternalisation;

public class Internalisation implements IGraphTransformation{

	@Override
	public TaskResult transform(SDFGraph algorithm, TextParameters params) {
		TaskResult result = new TaskResult() ;
		result.setSDF(SDFInternalisation.PartitionGraph(algorithm));
		return result ;
	}

}
