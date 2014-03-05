package org.ietr.preesm.experiment.pimm.subgraph.connector;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

public class SubgraphConnectorTask extends AbstractTaskImplementation {

	public static String GRAPH_KEY = "PiMM";
	
	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {
		
		//Get the input
		PiGraph pg = (PiGraph) inputs.get(GRAPH_KEY);
		
		//Visit it with the subgraph connector
		SubgraphConnector connector = new SubgraphConnector();
		connector.visit(pg);
		
		//Replace Actors with refinement by PiGraphs in pg and all its subgraphs
		Map<PiGraph, List<ActorByGraphReplacement>> replacements = connector.getGraphReplacements();
		for (PiGraph key : replacements.keySet()) {
			for (ActorByGraphReplacement r : replacements.get(key)) {
				key.getVertices().remove(r.toBeRemoved());
				key.getVertices().add(r.toBeAdded());
			}
		}
		
		//Return pg
		Map<String, Object> outputs = new HashMap<String, Object>();
		outputs.put(GRAPH_KEY, pg);
		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		return Collections.emptyMap();
	}

	@Override
	public String monitorMessage() {
		return "Connecting subgraphs";
	}

}
