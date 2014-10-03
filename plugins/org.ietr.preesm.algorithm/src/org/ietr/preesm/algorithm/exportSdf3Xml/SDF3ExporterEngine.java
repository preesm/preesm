package org.ietr.preesm.algorithm.exportSdf3Xml;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.preesm.core.scenario.PreesmScenario;

public class SDF3ExporterEngine {
	public void printSDFGraphToSDF3File(SDFGraph sdf, PreesmScenario scenario, Design architecture, IPath path) {
		// Create the exporter
		Sdf3Printer exporter = new Sdf3Printer(sdf, scenario, architecture);

		try {
			if (path.getFileExtension() == null
					|| !path.getFileExtension().equals("xml")) {
				path = path.addFileExtension("xml");
			}
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IFile iFile = workspace.getRoot().getFile(path);
			if (!iFile.exists()) {
				iFile.create(null, false, new NullProgressMonitor());
			}
			File file = new File(iFile.getRawLocation().toOSString());

			// Write the result into the text file
			exporter.write(file);

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
