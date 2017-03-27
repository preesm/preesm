package pisdf.task.export2c;

import java.util.Map;

import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.elements.Workflow;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

import xtend.writer.CWriter;

public class ExportToC extends AbstractTaskImplementation {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {
		PiGraph pisdf = (PiGraph) inputs.get("PiSDF");
		IPath outputPath = new Path(parameters.get("path"));
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IFile iFile = workspace.getRoot().getFile(outputPath);
		
		CWriter writer = new CWriter();
		try {
			if (!iFile.exists()) {
				iFile.create(null, false, new NullProgressMonitor());
			}
			writer.writeHierarchyLevel("TOP", pisdf.getVertices());
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		
		return null;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String monitorMessage() {
		// TODO Auto-generated method stub
		return "Creates the C++ representation of a PiSDF.";
	}

}
