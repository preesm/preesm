package org.abo.preesm.plugin.dataparallel

import java.util.Map
import org.eclipse.core.runtime.IProgressMonitor
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.workflow.WorkflowException
import org.ietr.dftools.workflow.elements.Workflow
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation
import org.ietr.dftools.workflow.tools.WorkflowLogger
import java.util.logging.Logger
import java.util.logging.Level
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGFromSDFOperations

class DataParallel extends AbstractTaskImplementation {
	
	override execute(Map<String, Object> inputs, Map<String, String> parameters, IProgressMonitor monitor, String nodeName, Workflow workflow) throws WorkflowException {
		val sdf = inputs.get("SDF") as SDFGraph
		val logger = WorkflowLogger.logger
		
		val dagGen = new SDF2DAG(sdf, logger as Logger)
		val dag = dagGen.outputGraph
		
		val dagOps = new DAGFromSDFOperations(dagGen)
		if(dagOps.isDAGInd)		
			logger.log(Level.INFO, "SDF is data-Parallel")
		else {
			logger.log(Level.INFO, "SDF is not data-parallel")
			logger.log(Level.INFO, "Non data-parallel actors are: " + dagOps.nonParallelActors)	
		}
		
		return newHashMap("SDF" -> dag)
	}
	
	override getDefaultParameters() {
		return newHashMap()
	}
	
	override monitorMessage() {
		return "Running Data-parallel checks and transformations"
	}
	
}