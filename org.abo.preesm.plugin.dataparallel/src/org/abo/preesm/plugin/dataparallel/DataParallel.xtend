package org.abo.preesm.plugin.dataparallel

import java.util.Map
import java.util.logging.Logger
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.visitors.SDF4JException
import org.ietr.dftools.workflow.WorkflowException
import org.ietr.dftools.workflow.elements.Workflow
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation
import org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation
import org.ietr.dftools.workflow.tools.WorkflowLogger
import org.abo.preesm.plugin.dataparallel.operations.visitor.DataParallelCheckOperations

/**
 * Wrapper class that performs the data-parallel checks and transforms
 * 
 * @author Sudeep Kanur
 */
class DataParallel extends AbstractTaskImplementation {
	
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val KEY_TrSDF = "TrSDF"
	
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val KEY_CySDF = "CySDF"
	
	/**
	 * Execute the plugin
	 * Construct the DAG from SDF and pass it to the output
	 * Report if the SDF is data-parallel, if not, report the actors that are not
	 * data-parallel
	 * 
	 */
	override execute(Map<String, Object> inputs, Map<String, String> parameters, IProgressMonitor monitor, String nodeName, Workflow workflow) throws WorkflowException {
		val sdf = inputs.get(AbstractWorkflowNodeImplementation.KEY_SDF_GRAPH) as SDFGraph
		// Check if sdf is schedulable
		if(!sdf.isSchedulable) {
			throw new SDF4JException("Graph " + sdf + " not schedulable")
		}
		
		val logger = WorkflowLogger.logger
		
		val checker = new DataParallelCheckOperations(logger as Logger)
		sdf.accept(checker)
		
		return newHashMap(KEY_TrSDF -> null,
						  KEY_CySDF -> checker.cyclicGraph)
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