/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.ietr.dftools.algorithm.importer.GMLSDFImporter;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.architecture.slam.SlamPackage;
import org.ietr.dftools.architecture.slam.serialize.IPXACTResourceFactoryImpl;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.ConstraintGroup;
import org.ietr.preesm.core.scenario.MemCopySpeed;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.core.scenario.TimingManager;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.util.SubgraphConnector;
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
	private SDFGraph algoSDF = null;
	private PiGraph algoPi = null;

	public ScenarioParser() {

		scenario = new PreesmScenario();
	}

	public Document getDom() {
		return dom;
	}

	/**
	 * Retrieves the DOM document
	 */
	public PreesmScenario parseXmlFile(IFile file)
			throws InvalidModelException, FileNotFoundException, CoreException {
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
					switch (type) {
					case "files":
						parseFileNames(elt);
						break;
					case "constraints":
						parseConstraintGroups(elt);
						break;
					case "relativeconstraints":
						parseRelativeConstraints(elt);
						break;
					case "timings":
						parseTimings(elt);
						break;
					case "simuParams":
						parseSimuParams(elt);
						break;
					case "variables":
						parseVariables(elt);
						break;
					case "parameterValues":
						parseParameterValues(elt);
						break;
					}
				}

				node = node.getNextSibling();
			}
		}

		scenario.setScenarioURL(file.getFullPath().toString());
		return scenario;
	}

	/**
	 * Retrieves all the parameter values
	 */
	private void parseParameterValues(Element paramValuesElt) {

		Node node = paramValuesElt.getFirstChild();

		PiGraph graph = null;
		try {
			graph = ScenarioParser.getPiGraph(scenario.getAlgorithmURL());
		} catch (InvalidModelException | CoreException e1) {
			e1.printStackTrace();
		}
		if (scenario.isPISDFScenario() && graph != null) {
			Set<Parameter> parameters = new HashSet<Parameter>(
					graph.getAllParameters());

			while (node != null) {
				if (node instanceof Element) {
					Element elt = (Element) node;
					String type = elt.getTagName();
					if (type.equals("parameter")) {
						parameters.remove(parseParameterValue(elt, graph));
					}
				}

				node = node.getNextSibling();
			}

			// Create a parameter value foreach parameter not yet in the
			// scenario
			for (Parameter p : parameters) {
				scenario.getParameterValueManager().addParameterValue(p);
			}
		}
	}

	/**
	 * Retrieve a ParameterValue
	 */
	private Parameter parseParameterValue(Element paramValueElt, PiGraph graph) {
		Parameter currentParameter = null;

		String type = paramValueElt.getAttribute("type");
		String parent = paramValueElt.getAttribute("parent");
		String name = paramValueElt.getAttribute("name");
		String stringValue = paramValueElt.getAttribute("value");

		currentParameter = graph.getParameterNamed(name);

		switch (type) {
		case "STATIC":
			scenario.getParameterValueManager().addParameterValue(name,
					Integer.parseInt(stringValue), parent);
			break;
		case "DYNAMIC":
			if (stringValue.charAt(0) == '['
					&& stringValue.charAt(stringValue.length() - 1) == ']') {
				stringValue = stringValue
						.substring(1, stringValue.length() - 1);
				String[] values = stringValue.split(",");

				Set<Integer> newValues = new HashSet<Integer>();

				try {
					for (String val : values) {
						newValues.add(Integer.parseInt(val.trim()));
					}
				} catch (NumberFormatException e) {
					// TODO: Do smthg
				}
				scenario.getParameterValueManager().addParameterValue(name,
						newValues, parent);
			}
			break;
		case "DEPENDENT":
			Set<String> inputParameters = new HashSet<String>();
			if (graph != null) {

				for (Parameter input : currentParameter.getInputParameters()) {
					inputParameters.add(input.getName());
				}
			}
			scenario.getParameterValueManager().addParameterValue(name,
					stringValue, inputParameters, parent);
			break;
		default:
			throw new RuntimeException("Unknown Parameter type: " + type
					+ " for Parameter: " + name);
		}

		return currentParameter;
	}

	/**
	 * Retrieves the timings
	 */
	private void parseRelativeConstraints(Element relConsElt) {

		String relConsFileUrl = relConsElt.getAttribute("excelUrl");
		scenario.getTimingManager().setExcelFileURL(relConsFileUrl);

		Node node = relConsElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String type = elt.getTagName();
				if (type.equals("relativeconstraint")) {
					parseRelativeConstraint(elt);
				}
			}

			node = node.getNextSibling();
		}
	}

	/**
	 * Retrieves one timing
	 */
	private void parseRelativeConstraint(Element timingElt) {

		int group = -1;

		if (algoSDF != null) {
			String type = timingElt.getTagName();
			if (type.equals("relativeconstraint")) {
				String vertexpath = timingElt.getAttribute("vertexname");

				try {
					group = Integer.parseInt(timingElt.getAttribute("group"));
				} catch (NumberFormatException e) {
					group = -1;
				}

				scenario.getRelativeconstraintManager().addConstraint(
						vertexpath, group);
			}

		}
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
				switch (type) {
				case "mainCore":
					scenario.getSimulationManager()
							.setMainOperatorName(content);
					break;
				case "mainComNode":
					scenario.getSimulationManager().setMainComNodeName(content);
					break;
				case "averageDataSize":
					scenario.getSimulationManager().setAverageDataSize(
							Long.valueOf(content));
					break;
				case "dataTypes":
					parseDataTypes(elt);
					break;
				case "specialVertexOperators":
					parseSpecialVertexOperators(elt);
					break;
				case "numberOfTopExecutions":
					scenario.getSimulationManager().setNumberOfTopExecutions(
							Integer.parseInt(content));
					break;
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
	private void parseFileNames(Element filesElt) throws InvalidModelException,
			FileNotFoundException, CoreException {

		Node node = filesElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String type = elt.getTagName();
				String url = elt.getAttribute("url");
				if (url.length() > 0) {
					if (type.equals("algorithm")) {
						scenario.setAlgorithmURL(url);
						if (url.endsWith(".graphml")) {
							algoSDF = getSDFGraph(url);
							algoPi = null;
						} else if (url.endsWith(".pi")) {
							algoPi = getPiGraph(url);
							algoSDF = null;
						}
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

			// Extract the root object from the resource.
			Design design = parseSlamDesign(url);

			scenario.setOperatorIds(DesignTools.getOperatorInstanceIds(design));
			scenario.setComNodeIds(DesignTools.getComNodeInstanceIds(design));
			scenario.setOperatorDefinitionIds(DesignTools
					.getOperatorComponentIds(design));
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

	public static SDFGraph getSDFGraph(String path)
			throws InvalidModelException, FileNotFoundException {
		SDFGraph algorithm = null;
		GMLSDFImporter importer = new GMLSDFImporter();

		Path relativePath = new Path(path);
		IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(relativePath);

		try {
			algorithm = (SDFGraph) importer.parse(new File(file.getLocation()
					.toOSString()));

			addVertexPathProperties(algorithm, "");
		} catch (InvalidModelException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			throw e;
		}

		return algorithm;
	}

	/**
	 * 
	 * @param url
	 *            URL of the Algorithm.
	 * @return the {@link PiGraph} algorithm.
	 * @throws InvalidModelException
	 * @throws CoreException
	 */
	public static PiGraph getPiGraph(String url) throws InvalidModelException,
			CoreException {
		PiGraph pigraph = null;
		ResourceSet resourceSet = new ResourceSetImpl();

		URI uri = URI.createPlatformResourceURI(url, true);
		if (uri.fileExtension() == null
				|| !uri.fileExtension().contentEquals("pi"))
			return null;
		Resource ressource = resourceSet.getResource(uri, true);
		pigraph = (PiGraph) (ressource.getContents().get(0));

		SubgraphConnector connector = new SubgraphConnector();
		connector.connectSubgraphs(pigraph);

		return pigraph;
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

		if (algoSDF != null || algoPi != null) {
			Node node = cstGroupElt.getFirstChild();

			while (node != null) {
				if (node instanceof Element) {
					Element elt = (Element) node;
					String type = elt.getTagName();
					String name = elt.getAttribute("name");
					if (type.equals("task")) {
						if (getActorFromPath(name) != null)
							cg.addActorPath(name);
					} else if (type.equals("operator")
							&& scenario.getOperatorIds() != null) {
						if (scenario.getOperatorIds().contains(name))
							cg.addOperatorId(name);
					}
				}
				node = node.getNextSibling();
			}
			return cg;
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
				} else if (type.equals("memcpyspeed")) {
					retrieveMemcpySpeed(scenario.getTimingManager(), elt);
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

		if (algoSDF != null || algoPi != null) {

			String type = timingElt.getTagName();
			if (type.equals("timing")) {
				String vertexpath = timingElt.getAttribute("vertexname");
				String opdefname = timingElt.getAttribute("opname");
				long time;
				String stringValue = timingElt.getAttribute("time");
				boolean isEvaluated = false;
				try {
					time = Long.parseLong(stringValue);
					isEvaluated = true;
				} catch (NumberFormatException e) {
					time = -1;
				}

				String actorName = getActorNameFromPath(vertexpath);

				if (actorName != null
						&& scenario.getOperatorDefinitionIds().contains(
								opdefname)) {
					if (isEvaluated) {
						timing = new Timing(opdefname, actorName, time);
					} else {
						timing = new Timing(opdefname, actorName, stringValue);
					}
				}
			}
		}

		return timing;
	}

	/**
	 * Returns an actor Object (either SDFAbstractVertex from SDFGraph or
	 * AbstractActor from PiGraph) from the path in its container graph
	 * 
	 * @param path
	 *            the path to the actor, where its segment is the name of an
	 *            actor and separators are "/"
	 * @return the wanted actor, if existing, null otherwise
	 */
	private Object getActorFromPath(String path) {
		Object result = null;
		if (algoSDF != null)
			result = algoSDF.getHierarchicalVertexFromPath(path);
		else if (algoPi != null)
			result = algoPi.getHierarchicalActorFromPath(path);
		return result;
	}

	/**
	 * Returns the name of an actor (either SDFAbstractVertex from SDFGraph or
	 * AbstractActor from PiGraph) from the path in its container graph
	 * 
	 * @param path
	 *            the path to the actor, where its segment is the name of an
	 *            actor and separators are "/"
	 * @return the name of the wanted actor, if we found it
	 */
	private String getActorNameFromPath(String path) {
		Object actor = getActorFromPath(path);
		if (actor != null) {
			if (actor instanceof SDFAbstractVertex)
				return ((SDFAbstractVertex) actor).getName();
			else if (actor instanceof AbstractActor)
				return ((AbstractActor) actor).getName();
		}
		return null;
	}

	/**
	 * Retrieves one memcopy speed composed of integer setup time and
	 * timeperunit
	 */
	private void retrieveMemcpySpeed(TimingManager timingManager,
			Element timingElt) {

		if (algoSDF != null || algoPi != null) {

			String type = timingElt.getTagName();
			if (type.equals("memcpyspeed")) {
				String opdefname = timingElt.getAttribute("opname");
				String sSetupTime = timingElt.getAttribute("setuptime");
				String sTimePerUnit = timingElt.getAttribute("timeperunit");
				int setupTime;
				float timePerUnit;

				try {
					setupTime = Integer.parseInt(sSetupTime);
					timePerUnit = Float.parseFloat(sTimePerUnit);
				} catch (NumberFormatException e) {
					setupTime = -1;
					timePerUnit = -1;
				}

				if (scenario.getOperatorDefinitionIds().contains(opdefname)
						&& setupTime >= 0 && timePerUnit >= 0) {
					MemCopySpeed speed = new MemCopySpeed(opdefname, setupTime,
							timePerUnit);
					timingManager.putMemcpySpeed(speed);
				}
			}

		}
	}
}
