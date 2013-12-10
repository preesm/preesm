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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.dftools.algorithm.importer.GMLSDFImporter;
import net.sf.dftools.algorithm.importer.InvalidModelException;
import net.sf.dftools.algorithm.model.sdf.SDFGraph;
import net.sf.dftools.architecture.slam.Design;
import net.sf.dftools.architecture.slam.SlamPackage;
import net.sf.dftools.architecture.slam.serialize.IPXACTResourceFactoryImpl;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.experiment.core.piscenario.PiScenario;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.serialize.PiParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * An xml parser retrieving {@link PiScenario} data
 * 
 * @author jheulot
 */
public class PiScenarioParser {

	/**
	 * xml tree
	 */
	private Document dom = null;

	/**
	 * scenario being retrieved
	 */
	private PiScenario piscenario = null;

	/**
	 * current algorithm
	 */
	private PiGraph algo = null;

	public PiScenarioParser() {

		piscenario = new PiScenario();
	}

	public Document getDom() {
		return dom;
	}

	/**
	 * Retrieves the DOM document
	 */
	public PiScenario parseXmlFile(IFile file) throws InvalidModelException,FileNotFoundException {
		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			dom = db.parse(file.getContents());

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		if (dom != null) {
			// get the root elememt
			Element docElt = dom.getDocumentElement();

			Node node = docElt.getFirstChild();

			while (node != null) {

				if (node instanceof Element) {
					Element elt = (Element) node;
					String type = elt.getTagName();
					if (type.equals("files")) {
						parseFileNames(elt);
					} else if (type.equals("constraints")) {
//						parseConstraintGroups(elt);
					} else if (type.equals("relativeconstraints")) {
//						parseRelativeConstraints(elt);
					} else if (type.equals("timings")) {
//						parseTimings(elt);
					} else if (type.equals("simuParams")) {
//						parseSimuParams(elt);
					} else if (type.equals("variables")) {
//						parseVariables(elt);
					}
				}

				node = node.getNextSibling();
			}
		}

		piscenario.setScenarioURL(file.getFullPath().toString());
		return piscenario;
	}

	/**
	 * Parses the archi and algo files and retrieves the file contents
	 */
	private void parseFileNames(Element filesElt) throws InvalidModelException,FileNotFoundException {

		Node node = filesElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String type = elt.getTagName();
				String url = elt.getAttribute("url");
				if (url.length() > 0) {
					if (type.equals("algorithm")) {
						piscenario.setAlgorithmURL(url);
//						algo = getAlgorithm(url);
					} else if (type.equals("architecture")) {
						piscenario.setArchitectureURL(url);
						initializeArchitectureInformation(url);
					}
				}
			}

			node = node.getNextSibling();
		}
	}

	/**
	 * Depending on the architecture model, parses the model and populates the
	 * scenario
	 */
	private void initializeArchitectureInformation(String url) {
		if (url.contains(".design")) {
			WorkflowLogger
					.getLogger()
					.log(Level.SEVERE,
							"SLAM architecture 1.0 is no more supported. Use .slam architecture files.");
		} else if (url.contains(".slam")) {
			WorkflowLogger.getLogger().log(Level.WARNING,
					"You are using SLAM architecture 2.0.");

			Map<String, Object> extToFactoryMap = Resource.Factory.Registry.INSTANCE
					.getExtensionToFactoryMap();
			Object instance = extToFactoryMap.get("slam");
			if (instance == null) {
				instance = new IPXACTResourceFactoryImpl();
				extToFactoryMap.put("slam", instance);
			}

			if (!EPackage.Registry.INSTANCE.containsKey(SlamPackage.eNS_URI)) {
				EPackage.Registry.INSTANCE.put(SlamPackage.eNS_URI,
						SlamPackage.eINSTANCE);
			}

			// Extract the root object from the resource.
			Design design = parseSlamDesign(url);

			System.out.println(design.getVlnv().getName());

//			piscenario.setOperatorIds(DesignTools.getOperatorInstanceIds(design));
//			piscenario.setComNodeIds(DesignTools.getComNodeInstanceIds(design));
//			piscenario.setOperatorDefinitionIds(DesignTools
//					.getOperatorComponentIds(design));
		}
	}

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

	public static PiGraph getAlgorithm(String path) throws InvalidModelException,CoreException {
		PiGraph algorithm = null;
		
		Path relativePath = new Path(path);

		try {
			PiParser piParser = new PiParser(null);
			
			InputStream is = ResourcesPlugin.getWorkspace().getRoot().getFile(relativePath).getContents();						
			algorithm = piParser.parse(is);

//			addVertexPathProperties(algorithm, "");
		} catch (CoreException e) {
			throw e;
		}

		return algorithm;
	}
}
