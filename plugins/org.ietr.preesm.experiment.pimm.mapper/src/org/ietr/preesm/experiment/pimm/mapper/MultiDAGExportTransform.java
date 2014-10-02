package org.ietr.preesm.experiment.pimm.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.core.Activator;
import org.ietr.preesm.core.tools.PathTools;
import org.ietr.preesm.mapper.exporter.DAGExporter;

public class MultiDAGExportTransform extends AbstractTaskImplementation {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		@SuppressWarnings("unchecked")
		Set<DirectedAcyclicGraph> dags = (Set<DirectedAcyclicGraph>) inputs
				.get("DAGs");

		for (DirectedAcyclicGraph dag : dags) {
			String path = parameters.get("path");
			if (path.endsWith(".graphml")) {
				path.replace(".graphml", "");
			}
			path = path + "_" + dag.getName() + ".graphml";
			String sGraphmlPath = PathTools.getAbsolutePath(path,
					workflow.getProjectName());
			Path graphmlPath = new Path(sGraphmlPath);

			// Exporting the DAG in a GraphML
			if (graphmlPath != null) {
				DAGExporter exporter = new DAGExporter();
				exporter.exportDAG(dag, graphmlPath);
			}
		}
		Activator.updateWorkspace();

		return new HashMap<String, Object>();
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("path", "");
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Exporting DAGs.";
	}
}
