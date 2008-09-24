package org.ietr.preesm.plugin.transforms;

import org.ietr.preesm.core.task.IGraphTransformation;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.visitors.FlatteningVisitor;

/**
 * Class used to flatten the hierarchy of a given graph
 * @author jpiat
 *
 */
public class HierarchyFlattening implements IGraphTransformation{

	@Override
	public TaskResult transform(SDFGraph algorithm, TextParameters params) {
		FlatteningVisitor flatHier  = new FlatteningVisitor() ;
		algorithm.accept(flatHier);
		TaskResult result = new TaskResult() ;
		result.setSDF((SDFGraph) flatHier.getOutput());
		return result ;
		
	}

}
