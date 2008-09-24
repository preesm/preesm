/**
 * 
 */
package org.ietr.preesm.core.workflow;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.codegen.SourceFileList;
import org.ietr.preesm.core.constraints.IScenario;
import org.ietr.preesm.core.task.ICodeGeneration;
import org.ietr.preesm.core.task.ICodeTranslation;
import org.ietr.preesm.core.task.IExporter;
import org.ietr.preesm.core.task.IGraphTransformation;
import org.ietr.preesm.core.task.IMapping;
import org.ietr.preesm.core.task.ITask;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.core.workflow.sources.AlgorithmConfiguration;
import org.ietr.preesm.core.workflow.sources.AlgorithmRetriever;
import org.ietr.preesm.core.workflow.sources.ArchitectureConfiguration;
import org.ietr.preesm.core.workflow.sources.ArchitectureRetriever;
import org.ietr.preesm.core.workflow.sources.ScenarioConfiguration;
import org.ietr.preesm.core.workflow.sources.ScenarioRetriever;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * This class provides methods to check and execute a workflow. A workflow
 * consists of several transformation plug-ins applied to an algorithm or an
 * algorithm *and* an architecture.
 * 
 * @author Matthieu Wipliez
 * 
 */
public class Workflow {

	private DirectedGraph<IWorkflowNode, WorkflowEdge> workflow;

	/**
	 * Creates a new workflow.
	 */
	public Workflow() {
		workflow = new SimpleDirectedGraph<IWorkflowNode, WorkflowEdge>(
				WorkflowEdge.class);
	}

	/**
	 * Checks the workflow.
	 * 
	 * @param monitor
	 * @return True if the workflow is correct and can be executed, false
	 *         otherwise.
	 */
	public boolean check(IProgressMonitor monitor) {
		monitor.subTask("Checking workflow...");

		boolean workflowOk = true;
		TopologicalOrderIterator<IWorkflowNode, WorkflowEdge> it = new TopologicalOrderIterator<IWorkflowNode, WorkflowEdge>(
				workflow);
		while (it.hasNext() && workflowOk) {
			IWorkflowNode node = it.next();
			if (node.isTaskNode()) {
				workflowOk = ((TaskNode) node).isTaskPossible();
			}
		}

		// Check the workflow
		monitor.worked(1);

		return workflowOk;
	}

	/**
	 * executes a workflow
	 * 
	 * @param monitor
	 * @param algorithmFileName
	 */
	public void execute(IProgressMonitor monitor, AlgorithmConfiguration algorithmConfiguration,
			ArchitectureConfiguration architectureConfiguration, ScenarioConfiguration scenarioConfiguration, Map<String,String> envVars ) {
		
		monitor.beginTask("Executing workflow",workflow.vertexSet().size());
		int numberOfTasksDone = 0;
		SDFGraph sdf = null;
		DirectedAcyclicGraph dag = null;
		IArchitecture architecture = null ;
		IScenario scenario = null ;
		SourceFileList sourceFiles = null ;

		TopologicalOrderIterator<IWorkflowNode, WorkflowEdge> it = new TopologicalOrderIterator<IWorkflowNode, WorkflowEdge>(
				workflow);
		while (it.hasNext()) {
			IWorkflowNode node = it.next();
			
			for (WorkflowEdge edge : workflow.incomingEdgesOf(node)) {
				if (edge.getCarriedDataType().equals(WorkflowEdgeType.SDF)) {
					sdf = edge.getCarriedData().getSDF();
				}
				else if (edge.getCarriedDataType().equals(WorkflowEdgeType.DAG)) {
					dag = edge.getCarriedData().getDAG();
				}
				else if (edge.getCarriedDataType().equals(WorkflowEdgeType.ARCHITECTURE)) {
					architecture = edge.getCarriedData().getArchitecture();
				}
				else if (edge.getCarriedDataType().equals(WorkflowEdgeType.SCENARIO)) {
					scenario = edge.getCarriedData().getScenario();
				}else{
					if(edge.getCarriedData().getSDF() != null){
						sdf = edge.getCarriedData().getSDF();
					}
					if(edge.getCarriedData().getDAG() != null){
						dag = edge.getCarriedData().getDAG();
					}
					if(edge.getCarriedData().getArchitecture() != null){
						architecture = edge.getCarriedData().getArchitecture();
					}
					if(edge.getCarriedData().getScenario() != null){
						scenario = edge.getCarriedData().getScenario();
					}
				}
			}
			TaskResult nodeResult = new TaskResult();
			if (node.isAlgorithmNode()) {
				monitor.subTask("loading algorithm");
				numberOfTasksDone++;
				monitor.worked(numberOfTasksDone);
				AlgorithmRetriever retriever = new AlgorithmRetriever(
						algorithmConfiguration);
				nodeResult.setSDF(retriever.getAlgorithm());
				sdf = nodeResult.getSDF();

			} else if (node.isArchitectureNode()) {
				monitor.subTask("loading architecture");
				numberOfTasksDone++;
				monitor.worked(numberOfTasksDone);

				// ArchitectureRetriever retriever = new
				// ArchitectureRetriever(architectureConfiguration);
				nodeResult.setArchitecture(ArchitectureRetriever
						.ExampleArchitecture());
				architecture = nodeResult.getArchitecture();

			} else if (node.isScenarioNode()) {
				monitor.subTask("loading scenario");
				numberOfTasksDone++;
				monitor.worked(numberOfTasksDone);

			/*	ScenarioRetriever retriever = new ScenarioRetriever(
						scenarioConfiguration);
				theScenario = retriever.getScenario();*/
				
				IScenario theScenario = ScenarioRetriever.RandomScenario(
						sdf, architecture);
				// TODO: load the scenario
				nodeResult.setScenario(theScenario);

			} else if (node.isTaskNode()) {

				// A transformation is taken into account only if connected.
				if (!workflow.edgesOf(node).isEmpty()) {

					ITask transformation = ((TaskNode) node).getTask();

					TextParameters parameters = new TextParameters(
							((TaskNode) node).getVariables());
					
					// Replacing environment variables by their values
					parameters.resolveEnvironmentVars(envVars);
	
					if (transformation instanceof IMapping) {
						monitor.subTask("scheduling");
						numberOfTasksDone++;
						monitor.worked(numberOfTasksDone);

						// mapping
						IMapping mapping = (IMapping) transformation;

						nodeResult = mapping.transform(sdf,
								architecture, parameters, scenario);

					} else if (transformation instanceof IGraphTransformation) {
						monitor.subTask("transforming");
						numberOfTasksDone++;
						monitor.worked(numberOfTasksDone);

						// mapping
						IGraphTransformation tranform = (IGraphTransformation) transformation;

						nodeResult= tranform.transform(sdf, parameters);
					} else if (transformation instanceof ICodeGeneration) {
						monitor.subTask("code generation");
						numberOfTasksDone++;
						monitor.worked(numberOfTasksDone);

						// generic code generation
						ICodeGeneration codeGen = (ICodeGeneration) transformation;
						nodeResult= codeGen.transform(dag,
								architecture, parameters);
						sourceFiles = nodeResult.getSourcefilelist();

					} else if (transformation instanceof ICodeTranslation) {
						monitor.subTask("code translation");
						numberOfTasksDone++;
						monitor.worked(numberOfTasksDone);

						// code translation
						ICodeTranslation codeTrans = (ICodeTranslation) transformation;
						codeTrans.transform(sourceFiles);
					} else if (transformation instanceof IExporter) {
						monitor.subTask(" content exporter");
						numberOfTasksDone++;
						monitor.worked(numberOfTasksDone);

						// code translation
						IExporter exporter = (IExporter) transformation;
						exporter.transform(sdf, parameters);
					}
				}

				// Updates the monitor
				monitor.worked(1);
			}
			for (WorkflowEdge outEdges : workflow.outgoingEdgesOf(node)) {
				outEdges.setCarriedData(nodeResult);
			}
		}

		// Workflow completed
		monitor.done();
	}

	/**
	 * Parses the workflow from the given file <code>fileName</code>. Currently
	 * this method creates a predefined workflow.
	 * 
	 * @param fileName
	 *            The name of a file that contains a workflow.
	 */
	public void parse(String fileName) {
		new WorkflowParser(fileName, workflow);
	}

}
