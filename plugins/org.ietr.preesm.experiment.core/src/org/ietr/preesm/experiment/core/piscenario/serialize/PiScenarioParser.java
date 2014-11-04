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
package org.ietr.preesm.experiment.core.piscenario.serialize;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.preesm.experiment.core.piscenario.PiScenario;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.xml.sax.SAXException;

/**
 * An XML parser retrieving {@link PiScenario} data
 * 
 * @author jheulot
 */
public class PiScenarioParser {
	/**
	 * Default Constructor.
	 */
	public PiScenarioParser() {
	}

	/**
	 * Retrieves the {@link PiScenario}.
	 */
	public PiScenario parseXmlFile(IFile file) throws InvalidModelException,FileNotFoundException {

		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			PiScenarioHandler handler = new PiScenarioHandler();
			file.refreshLocal(IResource.DEPTH_ZERO, null);
			parser.parse(file.getContents(), handler);
			return handler.getPiscenario();
		} catch (SAXException | IOException | CoreException | ParserConfigurationException e) {
			e.printStackTrace();
			return new PiScenario();
		}
	}

	/**
	 * @param url URL of the S-LAM
	 * @return the S-LAM {@link Design}.
	 */
	public static Design parseSlamDesign(String url) {
		// Demand load the resource into the resource set.
		ResourceSet resourceSet = new ResourceSetImpl();
		
		Path relativePath = new Path(url);
		IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(relativePath);
		String completePath = file.getLocation().toString();

		// resourceSet.
		Resource resource = resourceSet.getResource(
				URI.createFileURI(completePath), true);
		// Extract the root object from the resource.
		Design design = (Design) resource.getContents().get(0);

		return design;
	}

	/**
	 * 
	 * @param url URL of the Algorithm.
	 * @return the {@link PiGraph} algorithm.
	 * @throws InvalidModelException
	 * @throws CoreException
	 */
	public static PiGraph getAlgorithm(String url) throws InvalidModelException,CoreException {
		PiGraph algorithm = null;
		ResourceSet resourceSet = new ResourceSetImpl();
		
		URI uri = URI.createPlatformResourceURI(url, true);
		if(uri.fileExtension() == null || !uri.fileExtension().contentEquals("pi")) return null;
		Resource ressource = resourceSet.getResource(uri, true);					
		algorithm = (PiGraph) (ressource.getContents().get(0));

		return algorithm;
	}
	
}
