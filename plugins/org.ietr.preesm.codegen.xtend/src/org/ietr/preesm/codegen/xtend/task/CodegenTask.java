package org.ietr.preesm.codegen.xtend.task;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;

import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.architecture.slam.Design;
import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.elements.Workflow;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;

public class CodegenTask extends AbstractTaskImplementation {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		// Retrieve inputs
		PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");
		Design archi = (Design) inputs.get("architecture");
		MemoryExclusionGraph memEx = (MemoryExclusionGraph) inputs.get("MemEx");
		DirectedAcyclicGraph dag = (DirectedAcyclicGraph) inputs.get("DAG");

		// Generate intermediary model
		CodegenModelGenerator generator = new CodegenModelGenerator(archi, dag,
				memEx, scenario);

		generator.generate();

		// Create empty output map
		Map<String, Object> output = new HashMap<String, Object>();
		return output;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Generate code";
	}

}
