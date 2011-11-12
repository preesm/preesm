/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.core.scenario.serialize;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.dftools.architecture.slam.Design;
import net.sf.dftools.architecture.slam.SlamPackage;
import net.sf.dftools.architecture.slam.serialize.IPXACTResourceFactoryImpl;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.codegen.types.DataType;
import org.ietr.preesm.core.scenario.ConstraintGroup;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;
import net.sf.dftools.algorithm.importer.GMLGenericImporter;
import net.sf.dftools.algorithm.importer.InvalidFileException;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.algorithm.model.sdf.SDFGraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * An xml parser retrieving scenario data
 * 
 * @author mpelcat
 */
public class ScenarioParser {

	/**
	 * xml tree
	 */
	private Document dom = null;

	/**
	 * scenario being retrieved
	 */
	private PreesmScenario scenario = null;

	/**
	 * current algorithm
	 */
	private SDFGraph algo = null;

	public ScenarioParser() {

		scenario = new PreesmScenario();
	}

	public Document getDom() {
		return dom;
	}

	/**
	 * Retrieves the DOM document
	 */
	public PreesmScenario parseXmlFile(IFile file) {
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
			// TODO Auto-generated catch block
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
						parseConstraintGroups(elt);
					} else if (type.equals("timings")) {
						parseTimings(elt);
					} else if (type.equals("simuParams")) {
						parseSimuParams(elt);
					} else if (type.equals("variables")) {
						parseVariables(elt);
					}
				}

				node = node.getNextSibling();
			}
		}

		scenario.setScenarioURL(file.getFullPath().toString());
		return scenario;
	}

	/**
	 * Retrieves the timings
	 */
	private void parseVariables(Element varsElt) {

		String excelFileUrl = varsElt.getAttribute("excelUrl");
		scenario.getVariablesManager().setExcelFileURL(excelFileUrl);

		Node node = varsElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String type = elt.getTagName();
				if (type.equals("variable")) {
					String name = elt.getAttribute("name");
					String value = elt.getAttribute("value");

					scenario.getVariablesManager().setVariable(name, value);
				}
			}

			node = node.getNextSibling();
		}
	}

	/**
	 * Parses the simulation parameters
	 */
	private void parseSimuParams(Element filesElt) {

		Node node = filesElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String type = elt.getTagName();
				String content = elt.getTextContent();
				if (type.equals("mainCore")) {
					scenario.getSimulationManager()
							.setMainOperatorName(content);
				} else if (type.equals("mainComNode")) {
					scenario.getSimulationManager().setMainComNodeName(content);
				} else if (type.equals("averageDataSize")) {
					scenario.getSimulationManager().setAverageDataSize(
							Long.valueOf(content));
				} else if (type.equals("dataTypes")) {
					parseDataTypes(elt);
				} else if (type.equals("specialVertexOperators")) {
					parseSpecialVertexOperators(elt);
				}
			}

			node = node.getNextSibling();
		}
	}

	/**
	 * Retrieves the data types
	 */
	private void parseDataTypes(Element dataTypeElt) {

		Node node = dataTypeElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String type = elt.getTagName();
				if (type.equals("dataType")) {
					String name = elt.getAttribute("name");
					String size = elt.getAttribute("size");

					if (!name.isEmpty() && !size.isEmpty()) {
						DataType dataType = new DataType(name,
								Integer.parseInt(size));
						scenario.getSimulationManager().putDataType(dataType);
					}
				}
			}

			node = node.getNextSibling();
		}
	}

	/**
	 * Retrieves the operators able to execute fork/join/broadcast
	 */
	private void parseSpecialVertexOperators(Element spvElt) {

		Node node = spvElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String type = elt.getTagName();
				if (type.equals("specialVertexOperator")) {
					String path = elt.getAttribute("path");

					if (path != null) {
						scenario.getSimulationManager()
								.addSpecialVertexOperatorId(path);
					}
				}
			}

			node = node.getNextSibling();
		}

		/*
		 * It is not possible to remove all operators from special vertex
		 * executors: if no operator is selected, all of them are!!
		 */
		if (scenario.getSimulationManager().getSpecialVertexOperatorIds()
				.isEmpty()
				&& scenario.getOperatorIds() != null) {
			for (String opId : scenario.getOperatorIds()) {
				scenario.getSimulationManager()
						.addSpecialVertexOperatorId(opId);
			}
		}
	}

	/**
	 * Parses the archi and algo files and retrieves the file contents
	 */
	private void parseFileNames(Element filesElt) {

		Node node = filesElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String type = elt.getTagName();
				String url = elt.getAttribute("url");
				if (url.length() > 0) {
					if (type.equals("algorithm")) {
						scenario.setAlgorithmURL(url);
						algo = getAlgorithm(url);
					} else if (type.equals("architecture")) {
						scenario.setArchitectureURL(url);
						initializeArchitectureInformation(url);
					} else if (type.equals("codegenDirectory")) {
						scenario.getCodegenManager().setCodegenDirectory(url);
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

			Path relativePath = new Path(url);
			IFile file = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(relativePath);
			String completePath = file.getLocation().toString();

			// Extract the root object from the resource.
			Design design = parseSlamDesign(completePath);

			System.out.println(design.getVlnv().getName());

			scenario.setOperatorIds(DesignTools.getOperatorInstanceIds(design));
			scenario.setComNodeIds(DesignTools.getComNodeInstanceIds(design));
			scenario.setOperatorDefinitionIds(DesignTools
					.getOperatorComponentIds(design));
		}
	}

	public static Design parseSlamDesign(String completePath) {
		// Demand load the resource into the resource set.
		ResourceSet resourceSet = new ResourceSetImpl();

		// resourceSet.
		Resource resource = resourceSet.getResource(
				URI.createFileURI(completePath), true);
		// Extract the root object from the resource.
		Design design = (Design) resource.getContents().get(0);

		return design;
	}

	public static SDFGraph getAlgorithm(String path) {
		SDFGraph algorithm = null;
		GMLGenericImporter importer = new GMLGenericImporter();

		Path relativePath = new Path(path);
		IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(relativePath);

		try {
			algorithm = (SDFGraph) importer.parse(file.getContents(), file
					.getLocation().toOSString());

			addVertexPathProperties(algorithm, "");
		} catch (InvalidFileException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return algorithm;
	}

	/**
	 * Adding an information that keeps the path of each vertex relative to the
	 * hierarchy
	 */
	private static void addVertexPathProperties(SDFGraph algorithm,
			String currentPath) {

		for (SDFAbstractVertex vertex : algorithm.vertexSet()) {
			String newPath = currentPath + vertex.getName();
			vertex.setInfo(newPath);
			newPath += "/";
			if (vertex.getGraphDescription() != null) {
				addVertexPathProperties(
						(SDFGraph) vertex.getGraphDescription(), newPath);
			}
		}
	}

	/**
	 * Retrieves all the constraint groups
	 */
	private void parseConstraintGroups(Element cstGroupsElt) {

		String excelFileUrl = cstGroupsElt.getAttribute("excelUrl");
		scenario.getConstraintGroupManager().setExcelFileURL(excelFileUrl);

		Node node = cstGroupsElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String type = elt.getTagName();
				if (type.equals("constraintGroup")) {
					ConstraintGroup cg = getConstraintGroup(elt);
					scenario.getConstraintGroupManager().addConstraintGroup(cg);
				}
			}

			node = node.getNextSibling();
		}
	}

	/**
	 * Retrieves a constraint group
	 */
	private ConstraintGroup getConstraintGroup(Element cstGroupElt) {

		ConstraintGroup cg = new ConstraintGroup();

		if (algo != null) {

			Node node = cstGroupElt.getFirstChild();

			while (node != null) {

				if (node instanceof Element) {
					Element elt = (Element) node;
					String type = elt.getTagName();
					String name = elt.getAttribute("name");
					if (type.equals("task")) {
						SDFAbstractVertex vertex = algo
								.getHierarchicalVertexFromPath(name);
						if (vertex != null)
							cg.addVertexPath(name);
					} else if (type.equals("operator")) {
						if (scenario.getOperatorIds().contains(name))
							cg.addOperatorId(name);
					}
				}

				node = node.getNextSibling();
			}
		}

		return cg;
	}

	/**
	 * Retrieves the timings
	 */
	private void parseTimings(Element timingsElt) {

		String timingFileUrl = timingsElt.getAttribute("excelUrl");
		scenario.getTimingManager().setExcelFileURL(timingFileUrl);

		Node node = timingsElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String type = elt.getTagName();
				if (type.equals("timing")) {
					Timing timing = getTiming(elt);
					if (timing != null)
						scenario.getTimingManager().addTiming(timing);
				}
			}

			node = node.getNextSibling();
		}
	}

	/**
	 * Retrieves one timing
	 */
	private Timing getTiming(Element timingElt) {

		Timing timing = null;

		if (algo != null) {

			String type = timingElt.getTagName();
			if (type.equals("timing")) {
				String vertexpath = timingElt.getAttribute("vertexname");
				String opdefname = timingElt.getAttribute("opname");
				int time;

				try {
					time = Integer.parseInt(timingElt.getAttribute("time"));
				} catch (NumberFormatException e) {
					time = -1;
				}

				SDFAbstractVertex vertex = algo
						.getHierarchicalVertex(vertexpath);

				if (vertex != null
						&& scenario.getOperatorDefinitionIds().contains(
								opdefname) && time >= 0) {
					timing = new Timing(opdefname, vertex.getName(), time);
				}
			}

		}

		return timing;
	}
}
