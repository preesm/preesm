package graph.exporter;

import java.util.LinkedHashMap;
import java.util.Map;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IWorkbenchWindow;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.statictools.PiSDFFlattener;
import org.preesm.model.scenario.Scenario;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchPage;
import org.preesm.ui.PreesmUIPlugin;
import org.preesm.ui.pisdf.util.SavePiGraph;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

//import clustering.raiser.ClusteringRaiserTask;

/**
* Graph Exporter Task
*
* @author orenaud
*
*/
@PreesmTask(id = "graph.exporter.task.identifier", name = "Graph Exporter",
 inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class) },
 parameters = {
		 @Parameter(name = "Flat", description = "generate a flat or a hierarchical .pi",
			        values = { @Value(name = "true/false", effect = "enable the flattening of the output graph") })
 }

)
public class GraphExporterTask extends AbstractTaskImplementation {
	
	public static final String CONFIG_DEFAULT              = "true";
	public static final String CONFIG_PARAM              = "Flat";
	
	protected boolean             config;

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
			IProgressMonitor monitor, String nodeName, Workflow workflow) {
		// retrieve input parameter
	    String config = parameters.get(GraphExporterTask.CONFIG_PARAM);
	    this.config = bool(config);
	    
	    // Task inputs
	    PiGraph inputGraph = (PiGraph) inputs.get("PiMM");
	    Scenario scenario = (Scenario) inputs.get("scenario");
	    
	    final String[] uriString = scenario.getAlgorithm().getUrl().split("/");
	    String strPath = "/"+uriString[1]+"/"+uriString[2]+"/generated/";
        final IPath fromPortableString = Path.fromPortableString(strPath);
        final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPortableString);
        if(this.config) {//return flat graph
        	final PiGraph flat = PiSDFFlattener.flatten(inputGraph, false);
		    IProject iProject = file.getProject();
		    SavePiGraph.savePiGraphInFolder(iProject, fromPortableString, flat, "_wowflat");
        }else {//return hierarchical graph
        	for(PiGraph subgraph : inputGraph.getAllChildrenGraphs()) {
        		IProject iProject = file.getProject();
    		    SavePiGraph.savePiGraphInFolder(iProject, fromPortableString, subgraph, "_wowH");
        	}
        	IProject iProject = file.getProject();
		    SavePiGraph.savePiGraphInFolder(iProject, fromPortableString, inputGraph, "_wowH");
        	
        }
 
	 // Create empty output map
		return new LinkedHashMap<>();
	}

	private boolean bool(String config2) {
		if(config2.equals("true")) {
			return true;
		}
		return false;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		final Map<String, String> parameters = new LinkedHashMap<>();
		// config default
	    parameters.put(GraphExporterTask.CONFIG_PARAM, GraphExporterTask.CONFIG_DEFAULT);
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Starting Execution of graph exporter Task";
	}
	//public abstract void processPiSDF(final PiGraph pigraph, final IProject iProject, final Shell shell);
}
