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

package org.ietr.preesm.core.scenario;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.ietr.preesm.core.architecture.parser.ArchitectureParser;
import org.sdf4j.importer.GMLSDFImporter;
import org.sdf4j.importer.InvalidFileException;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;
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
	private Scenario scenario = null;

	/**
	 * current algorithm
	 */
	private SDFGraph algo = null;

	/**
	 * current architecture
	 */
	private MultiCoreArchitecture archi = null;

	public ScenarioParser() {

		scenario = new Scenario();
	}

	public Document getDom() {
		return dom;
	}

	/**
	 * Retrieves the DOM document
	 */
	public void parseXmlFile(IFile file) {
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
	}

	/**
	 * Parses the first level of hierarchy
	 */
	public Scenario parseDocument() {
		if(dom != null){
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
					}
				}
	
				node = node.getNextSibling();
			}
		}

		return scenario;
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
				if (type.equals("algorithm")) {
					scenario.setAlgorithmURL(url);
					algo = getAlgorithm(url);
				}
				else if (type.equals("architecture")){
					scenario.setArchitectureURL(url);
					archi = getArchitecture(url);
				}
				else if (type.equals("timingfile")){
					scenario.getTimingManager().setTimingFileURL(url, null);
				}
			}

			node = node.getNextSibling();
		}
	}

	/**
	 * Gets the Algorithm from its url by parsing the algorithm file
	 */
	static public SDFGraph getAlgorithm(String url) {

		SDFGraph graph = null;
		if (ResourcesPlugin.getWorkspace() != null) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

			IFile file = root.getFile(new Path(url));

			GMLSDFImporter importer = new GMLSDFImporter();
			try {
				graph = (SDFGraph) importer.parse(new File(file
						.getLocation().toOSString()));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (InvalidFileException e) {
				e.printStackTrace();
			}
		}

		return graph;

	}

	/**
	 * Gets the Architecture from its url by parsing the architecture file
	 */
	static public MultiCoreArchitecture getArchitecture(String url) {

		String filename = url;
		ArchitectureParser parser = new ArchitectureParser();
		
		Path relativePath = new Path(filename);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(relativePath);
		
		parser.parseXmlFile(file);
		return parser.parseDocument();
	}

	/**
	 * Retrieves all the constraint groups
	 */
	private void parseConstraintGroups(Element cstGroupsElt) {

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

		if (algo != null && archi != null) {

			Node node = cstGroupElt.getFirstChild();

			while (node != null) {

				if (node instanceof Element) {
					Element elt = (Element) node;
					String type = elt.getTagName();
					String name = elt.getAttribute("name");
					if (type.equals("task")) {
						SDFAbstractVertex vertex = algo.getVertex(name);
						if (vertex != null)
							cg.addVertex(vertex);
					} else if (type.equals("operator")) {
						Operator def = (Operator)archi
								.getComponent(ArchitectureComponentType.operator,name);
						if (def != null)
							cg.addOperator(def);
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

		Node node = timingsElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String type = elt.getTagName();
				if (type.equals("timing")) {
					Timing timing = getTiming(elt);
					if(timing!=null)
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

		if (algo != null && archi != null) {

			String type = timingElt.getTagName();
			if (type.equals("timing")) {
				String vertexname = timingElt.getAttribute("vertexname");
				String opdefname = timingElt.getAttribute("opname");
				int time;

				try {
					time = Integer.parseInt(timingElt.getAttribute("time"));
				} catch (NumberFormatException e) {
					time = -1;
				}

				SDFAbstractVertex vertex = algo.getVertex(vertexname);
				OperatorDefinition opdef = (OperatorDefinition)archi
						.getComponentDefinition(ArchitectureComponentType.operator,opdefname);

				if (vertex != null && opdef != null && time >= 0) {
					timing = new Timing(opdef, vertex, time);
				}
			}

		}

		return timing;
	}

}
