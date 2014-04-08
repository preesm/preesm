package org.ietr.preesm.experiment.pimm2sdf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.experiment.core.piscenario.PiScenario;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

public class PiMM2SDFTask extends AbstractTaskImplementation {

	private final static String PISCENARIO_KEY = "scenario";
	private final static String PIGRAPH_KEY = "PiMM";
	
	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {
		
		PiScenario piscenario = (PiScenario) inputs.get(PISCENARIO_KEY);
		PiGraph graph = (PiGraph) inputs.get(PIGRAPH_KEY);
		
		PiMM2SDFLauncher launcher = new PiMM2SDFLauncher(piscenario, graph);
		Set<SDFGraph> result = launcher.launch();
		
		Map<String, Object> output = new HashMap<String, Object>();
		output.put("SDFs", result);
		return output;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		return Collections.emptyMap();
	}

	@Override
	public String monitorMessage() {
		return "Transforming PiGraph to SDFGraphs";
	}
}
