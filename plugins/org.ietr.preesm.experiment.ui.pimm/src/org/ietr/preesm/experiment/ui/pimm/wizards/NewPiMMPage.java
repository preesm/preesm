/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
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
import org.ietr.preesm.experiment.model.pimm.PiGraph;
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

	private PiGraph createGraph(IPath path) {
		graphName = getFileName();
		int idx = graphName.indexOf("diagram");
		if (idx != -1) {
			graphName = graphName.substring(0, idx - 1);
		}

		PiGraph graph = PiMMFactory.eINSTANCE.createPiGraph();
		graph.setName(graphName);

		return graph;
	}

	private void saveGraph(ResourceSet set, IPath path, PiGraph graph) {
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
		PiGraph graph = createGraph(piPath);

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
