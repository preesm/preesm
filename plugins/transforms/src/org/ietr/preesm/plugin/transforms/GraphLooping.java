package org.ietr.preesm.plugin.transforms;

import java.util.List;

import org.ietr.preesm.core.task.IGraphTransformation;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.optimisations.loops.detection.LoopDetector;
import org.sdf4j.optimisations.clustering.Clusterize;

public class GraphLooping implements IGraphTransformation{

	@Override
	public TaskResult transform(SDFGraph algorithm, TextParameters params) {
		SDFGraph inGraph = algorithm.clone();
		LoopDetector detector = new LoopDetector(inGraph) ;
		String nbClust = params.getVariable("loopLength") ;
		List<List<SDFAbstractVertex>> loops = detector.getLoops(Integer.decode(nbClust));
		int i = 0 ;
		for(List<SDFAbstractVertex> loop : loops){
			Clusterize.culsterizeBlocks(inGraph, loop, "cluster_"+i);
			i ++ ;
		}
		TaskResult result = new TaskResult();
		result.setSDF(inGraph);
		return result;
	}

}
