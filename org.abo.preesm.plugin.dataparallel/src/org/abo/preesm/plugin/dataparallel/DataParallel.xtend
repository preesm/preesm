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
import org.abo.preesm.plugin.dataparallel.operations.visitor.RearrangeDAG
import org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation
import org.eclipse.xtend.lib.annotations.Accessors
import org.abo.preesm.plugin.dataparallel.operations.visitor.LevelsOperations
import org.abo.preesm.plugin.dataparallel.operations.visitor.OperationsUtils

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
		val logger = WorkflowLogger.logger
		
		val dagGen = new SDF2DAG(sdf, logger as Logger)
		
		val rootOps = new RootExitOperations
		dagGen.accept(rootOps)
		
		logger.log(Level.INFO, "Root instances are: \n" + rootOps.rootInstances)
		
		val depOps = new DependencyAnalysisOperations
		dagGen.accept(depOps)
		if(depOps.isIndependent) {
			logger.log(Level.INFO, "SDF is DAG-independent")
			val levelOps = new LevelsOperations
			dagGen.accept(levelOps)
			val levels = levelOps.levels
			if(OperationsUtils.isParallel(dagGen, levels)) {
				logger.log(Level.INFO, "SDF is also data-parallel")
			} else {
				logger.log(Level.INFO, "SDF is not data-parallel. Rearranging...")
				
				// Get transient graph
				val rearrangeVisitor = new RearrangeDAG(sdf, logger)
				dagGen.accept(rearrangeVisitor)
				
				return newHashMap(KEY_TrSDF -> rearrangeVisitor.transientGraph,
								  KEY_CySDF -> new SDF2DAG(rearrangeVisitor.cyclicGraph).outputGraph)
			}
		}
		else {
			logger.log(Level.INFO, "SDF is not DAG-independent")
			logger.log(Level.INFO, " Actors with dependent instances are: " + depOps.instanceDependentActors)
		}
		
		return newHashMap(KEY_TrSDF -> null,
						  KEY_CySDF -> null)
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