package org.ietr.preesm.plugin.transforms;

import org.ietr.preesm.core.task.IGraphTransformation;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.visitors.ToHSDFVisitor;

/**
 * Class used to transform a SDF graph into a HSDF graph
 * @author jpiat
 *
 */
public class HSDFTransformation implements IGraphTransformation{

	@Override
	public TaskResult transform(SDFGraph algorithm, TextParameters params) {
		ToHSDFVisitor toHsdf = new ToHSDFVisitor() ;
		algorithm.accept(toHsdf);
		TaskResult result = new TaskResult() ;
		result.setSDF((SDFGraph) toHsdf.getOutput());
		return result ;
	}

}
