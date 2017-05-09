package org.abo.preesm.plugin.dataparallel

import java.util.Map
import org.eclipse.core.runtime.IProgressMonitor
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.workflow.WorkflowException
import org.ietr.dftools.workflow.elements.Workflow
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation
import org.ietr.dftools.workflow.tools.WorkflowLogger
import java.util.logging.Logger

class DataParallel extends AbstractTaskImplementation {
	
	override execute(Map<String, Object> inputs, Map<String, String> parameters, IProgressMonitor monitor, String nodeName, Workflow workflow) throws WorkflowException {
		val sdf = inputs.get("SDF") as SDFGraph
		
		val dagGen = new DAGConstructor(sdf, WorkflowLogger.logger as Logger)
		val dag = dagGen.outputGraph
		
		return newHashMap("SDF" -> dag)
	}
	
	override getDefaultParameters() {
		return newHashMap()
	}
	
	override monitorMessage() {
		return "Running Data-parallel checks and transformations"
	}
	
}