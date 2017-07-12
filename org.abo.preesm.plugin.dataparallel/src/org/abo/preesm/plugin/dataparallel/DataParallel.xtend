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
import org.abo.preesm.plugin.dataparallel.operations.visitor.RootExitOperations
import org.abo.preesm.plugin.dataparallel.operations.visitor.DependencyAnalysisOperations

/**
 * Wrapper class that performs the data-parallel checks and transforms
 * 
 * @author Sudeep Kanur
 */
class DataParallel extends AbstractTaskImplementation {
	
	/**
	 * Execute the plugin
	 * Construct the DAG from SDF and pass it to the output
	 * Report if the SDF is data-parallel, if not, report the actors that are not
	 * data-parallel
	 * 
	 */
	override execute(Map<String, Object> inputs, Map<String, String> parameters, IProgressMonitor monitor, String nodeName, Workflow workflow) throws WorkflowException {
		val sdf = inputs.get("SDF") as SDFGraph
		val logger = WorkflowLogger.logger
		
		val dagGen = new SDF2DAG(sdf, logger as Logger)
		val dag = dagGen.outputGraph
		
		val rootOps = new RootExitOperations
		dagGen.accept(rootOps)
		
		logger.log(Level.INFO, "Root instances are: \n" + rootOps.rootInstances)
		
		val depOps = new DependencyAnalysisOperations
		
		if(depOps.isIndependent)		
			logger.log(Level.INFO, "SDF is data-Parallel")
		else {
			logger.log(Level.INFO, "SDF is not data-parallel")
			logger.log(Level.INFO, "Non data-parallel actors are: " + depOps.instanceDependentActors)	
		}
		
		return newHashMap("SDF" -> dag)
	}
	
	/**
	 * No default parameters yet
	 */
	override getDefaultParameters() {
		return newHashMap
	}
	
	/**
	 * Default monitor message
	 */
	override monitorMessage() {
		return "Running Data-parallel checks and transformations"
	}
	
}