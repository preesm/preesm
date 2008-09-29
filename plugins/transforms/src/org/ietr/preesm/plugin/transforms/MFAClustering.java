package org.ietr.preesm.plugin.transforms;

import org.ietr.preesm.core.task.IGraphTransformation;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.optimisations.clustering.mfa.MFAPartitioning;

public class MFAClustering implements IGraphTransformation {

	@Override
	public TaskResult transform(SDFGraph algorithm, TextParameters params) {
		
		TaskResult result = new TaskResult() ;
		SDFGraph clone = algorithm.clone() ;
		String nbClust = params.getVariable("nbCluster") ;
		MFAPartitioning partitioner = new MFAPartitioning(clone, Integer.decode(nbClust), 1, 1) ;
		partitioner.compute(1);
		partitioner.performClustering();
		result.setSDF(clone);
		return result ;
	}

}
