/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.core.workflow;

import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.FileEditorInput;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.codegen.SourceFileList;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.task.ICodeGeneration;
import org.ietr.preesm.core.task.ICodeTranslation;
import org.ietr.preesm.core.task.IExporter;
import org.ietr.preesm.core.task.IFileConversion;
import org.ietr.preesm.core.task.IGraphTransformation;
import org.ietr.preesm.core.task.IMapping;
import org.ietr.preesm.core.task.IPlotter;
import org.ietr.preesm.core.task.ITask;
import org.ietr.preesm.core.task.PreesmException;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.core.ui.Activator;
import org.ietr.preesm.core.ui.perspectives.CorePerspectiveFactory;
import org.ietr.preesm.core.workflow.sources.AlgorithmConfiguration;
import org.ietr.preesm.core.workflow.sources.ArchitectureConfiguration;
import org.ietr.preesm.core.workflow.sources.ScenarioConfiguration;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * This class provides methods to check and execute a workflow. A workflow
 * consists of several transformation plug-ins applied to a scenario
 * 
 * @author Matthieu Wipliez
 * @author mpelcat
 * @author jpiat
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
				// Testing only connected nodes
				if (!workflow.edgesOf(node).isEmpty())
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
	public void execute(IProgressMonitor monitor,
			AlgorithmConfiguration algorithmConfiguration,
			ArchitectureConfiguration architectureConfiguration,
			ScenarioConfiguration scenarioConfiguration,
			Map<String, String> envVars) throws PreesmException{

		// Activates the Preesm perspective
		activatePerspective();


		WorkflowStepManager stepManager = new WorkflowStepManager(monitor,workflow.vertexSet().size());
		SDFGraph sdf = null;
		DirectedAcyclicGraph dag = null;
		MultiCoreArchitecture architecture = null;
		IScenario scenario = null;
		SourceFileList sourceFiles = null;
		Object customData = null; // This input type is known from the sender and the receiver

		TopologicalOrderIterator<IWorkflowNode, WorkflowEdge> it = new TopologicalOrderIterator<IWorkflowNode, WorkflowEdge>(
				workflow);
		while (it.hasNext()) {
			IWorkflowNode node = it.next();

			for (WorkflowEdge edge : workflow.incomingEdgesOf(node)) {
				if (edge.getCarriedDataType().equals(WorkflowEdgeType.SDF)) {
					sdf = edge.getCarriedData().getSDF();
				} else if (edge.getCarriedDataType().equals(
						WorkflowEdgeType.DAG)) {
					dag = edge.getCarriedData().getDAG();
				} else if (edge.getCarriedDataType().equals(
						WorkflowEdgeType.ARCHITECTURE)) {
					architecture = edge.getCarriedData().getArchitecture();
				} else if (edge.getCarriedDataType().equals(
						WorkflowEdgeType.SCENARIO)) {
					scenario = edge.getCarriedData().getScenario();
				} else {
					if (edge.getCarriedData().getSDF() != null) {
						sdf = edge.getCarriedData().getSDF();
					}
					if (edge.getCarriedData().getDAG() != null) {
						dag = edge.getCarriedData().getDAG();
					}
					if (edge.getCarriedData().getArchitecture() != null) {
						architecture = edge.getCarriedData().getArchitecture();
					}
					if (edge.getCarriedData().getScenario() != null) {
						scenario = edge.getCarriedData().getScenario();
					}
					if (edge.getCarriedData().getCustomData() != null) {
						customData = edge.getCarriedData().getCustomData();
					}
				}
			}
			TaskResult nodeResult = new TaskResult();
			if (node.isAlgorithmNode()) {
				stepManager.retrieveAlgorithm("loading algorithm",scenario,nodeResult);
			} else if (node.isArchitectureNode()) {				
				stepManager.retrieveArchitecture("loading architecture",scenario,nodeResult);
			} else if (node.isScenarioNode()) {
				stepManager.retrieveScenario("loading scenario",scenarioConfiguration,nodeResult);
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
						PreesmLogger.getLogger().log(Level.INFO, "scheduling");

						// mapping
						IMapping mapping = (IMapping) transformation;

						nodeResult = mapping.transform(sdf, architecture,
								parameters, scenario, monitor);

					} else if (transformation instanceof IGraphTransformation) {
						monitor.subTask("transforming");
						PreesmLogger.getLogger().log(Level.INFO, "transforming");

						// mapping
						IGraphTransformation tranform = (IGraphTransformation) transformation;

						nodeResult = tranform.transform(sdf, parameters);
					} else if (transformation instanceof IFileConversion) {
						monitor.subTask("converting file");
						PreesmLogger.getLogger().log(Level.INFO, "converting file");

						// mapping
						IFileConversion tranform = (IFileConversion) transformation;

						nodeResult = tranform.transform(parameters);
					} else if (transformation instanceof ICodeGeneration) {
						monitor.subTask("code generation");
						PreesmLogger.getLogger().log(Level.INFO, "code generation");

						// generic code generation
						ICodeGeneration codeGen = (ICodeGeneration) transformation;

						if (dag != null)
							nodeResult = codeGen.transform(dag, architecture,
									parameters);

						sourceFiles = nodeResult.getSourcefilelist();

						// Updates the workspace to show the generated files
						// updateWorkspace(monitor);

					} else if (transformation instanceof ICodeTranslation) {
						monitor.subTask("code translation");
						PreesmLogger.getLogger().log(Level.INFO, "code translation");

						// code translation
						ICodeTranslation codeTrans = (ICodeTranslation) transformation;
						codeTrans.transform(sourceFiles);
					} else if (transformation instanceof IExporter) {
						monitor.subTask("content exporter");
						PreesmLogger.getLogger().log(Level.INFO, "content exporter");
						// code translation
						IExporter exporter = (IExporter) transformation;

						if (exporter.isSDFExporter())
							exporter.transform(sdf, parameters);
						else
							exporter.transform(dag, sdf, architecture,
									scenario, parameters);

						IWorkspace workspace = updateWorkspace(monitor);
						Path path = new Path(parameters.getVariable("path"));
						IResource resource = workspace.getRoot().findMember(
								path.toOSString());

						if (resource != null) {
							FileEditorInput input = new FileEditorInput(
									(IFile) resource);
							openEditor(((IFile) resource).getName(), input);
						}
					} else if (transformation instanceof IPlotter) {
						monitor.subTask("plot");
						PreesmLogger.getLogger().log(Level.INFO, "plot");

						// code translation
						IPlotter plotter = (IPlotter) transformation;
						
						plotter.transform(customData,scenario,parameters);
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

	/**
	 * Opens an editor on the given {@link IEditorInput}.
	 * 
	 * @param input
	 *            An {@link IEditorInput}.
	 */
	private void openEditor(String fileName, IEditorInput input) {
		IEditorRegistry registry = PlatformUI.getWorkbench()
				.getEditorRegistry();
		IEditorDescriptor[] editors = registry.getEditors(fileName);
		IEditorDescriptor editor;
		if (editors.length == 0) {
			editor = registry.getDefaultEditor(fileName);

			// if no editor found, use the default text editor
			if (editor == null) {
				final String editorId = "org.eclipse.ui.DefaultTextEditor";
				editor = registry.findEditor(editorId);
			}
		} else if (editors.length == 1) {
			editor = editors[0];
		} else {
			editor = editors[0];
		}
		PlatformUI.getWorkbench().getDisplay().asyncExec(
				new OpenWorkflowOutput(input, editor.getId()));
	}

	private IWorkspace updateWorkspace(IProgressMonitor monitor) {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			workspace.getRoot().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return workspace;
	}

	private void activatePerspective() {
		PreesmLogger.getLogger().createConsole();
		PreesmLogger.getLogger().setLevel(Level.INFO);
		
		Activator.getDefault().getWorkbench().getDisplay().syncExec(
				new Runnable() {
					@Override
					public void run() {
						IWorkbenchWindow window = Activator.getDefault()
								.getWorkbench().getActiveWorkbenchWindow();
						try {
							Activator.getDefault().getWorkbench()
									.showPerspective(CorePerspectiveFactory.ID,
											window);
						} catch (WorkbenchException e) {
							e.printStackTrace();
						}
					}
				});
	}
}
