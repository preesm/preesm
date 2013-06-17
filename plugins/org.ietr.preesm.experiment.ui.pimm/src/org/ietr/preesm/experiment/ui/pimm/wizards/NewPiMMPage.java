package org.ietr.preesm.experiment.ui.pimm.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;

public class NewPiMMPage extends WizardNewFileCreationPage {

	/**
	 * The name of the saved file
	 */
	private String graphName;

	/**
	 * Constructor for {@link NewPiMMPage}
	 * 
	 * @param pageName
	 *            The name of the Page
	 * @param selection
	 *            The current resource selection
	 */
	public NewPiMMPage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
		// if the selection is a file, gets its file name and removes its
		// extension. Otherwise, let fileName be null.
		Object obj = selection.getFirstElement();
		if (obj instanceof IFile) {
			IFile file = (IFile) obj;
			String ext = file.getFileExtension();
			graphName = file.getName();
			int idx = graphName.indexOf(ext);
			if (idx != -1) {
				graphName = graphName.substring(0, idx - 1);
			}
		}

		setTitle("Choose file name and parent folder");
	}

	private Graph createGraph(IPath path) {
		graphName = getFileName();
		int idx = graphName.indexOf("diagram");
		if (idx != -1) {
			graphName = graphName.substring(0, idx - 1);
		}

		Graph graph = PiMMFactory.eINSTANCE.createGraph();
		graph.setName(graphName);

		return graph;
	}

	private void saveGraph(ResourceSet set, IPath path, Graph graph) {
		URI uri = URI.createPlatformResourceURI(path.toString(), true);

		// Following lines corresponds to a copy of
		// EcoreHelper.putEObject(set, uri, graph);
		// from net.sf.orcc.util.util
		// @author mwipliez
		// date of copy 2012.10.12
		{
			Resource resource = set.getResource(uri, false);
			if (resource == null) {
				resource = set.createResource(uri);
			} else {
				resource.getContents().clear();
			}

			resource.getContents().add(graph);
			try {
				resource.save(null);
				// return true;
			} catch (IOException e) {
				e.printStackTrace();
				// return false;
			}
		}
		Resource resource = set.getResource(uri, false);
		resource.setTrackingModification(true);
	}

	@Override
	protected InputStream getInitialContents() {
		IPath path = getContainerFullPath();

		// create graph
		IPath piPath = path.append(getFileName()).removeFileExtension()
				.addFileExtension("pi");
		Graph graph = createGraph(piPath);

		// save graph
		ResourceSet set = new ResourceSetImpl();
		saveGraph(set, piPath, graph);

		// create diagram
		Diagram diagram = Graphiti.getPeCreateService().createDiagram(
				"PiMM", graphName, true);

		// link diagram to network
		PictogramLink link = PictogramsFactory.eINSTANCE.createPictogramLink();
		link.getBusinessObjects().add(graph);
		diagram.setLink(link);

		// create the resource (safe because the wizard does not allow existing
		// resources to be overridden)
		URI uri = URI.createPlatformResourceURI(path.append(getFileName()).toString(),
				true);
		Resource resource = set.createResource(uri);
		resource.getContents().add(diagram);

		// save to a byte array output stream
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			resource.save(outputStream, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ByteArrayInputStream(outputStream.toByteArray());
	}

}
