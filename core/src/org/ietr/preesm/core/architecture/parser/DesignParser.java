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

package org.ietr.preesm.core.architecture.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.ArchitectureInterface;
import org.ietr.preesm.core.architecture.BusReference;
import org.ietr.preesm.core.architecture.HierarchyPort;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.ContentionNodeDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Dma;
import org.ietr.preesm.core.architecture.simplemodel.DmaDefinition;
import org.ietr.preesm.core.architecture.simplemodel.MediumDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.architecture.simplemodel.ParallelNodeDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Ram;
import org.ietr.preesm.core.architecture.simplemodel.RamDefinition;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * An xml parser retrieving architecture data from an IP-XACT design
 * 
 * @author mpelcat
 */
public class DesignParser {

	/**
	 * xml tree
	 */
	private Document dom = null;

	/**
	 * current architecture
	 */
	private MultiCoreArchitecture archi = null;

	private IFile currentFile = null;

	public DesignParser() {

	}

	public Document getDom() {
		return dom;
	}

	/**
	 * Retrieves the DOM document
	 */
	public MultiCoreArchitecture parseXmlFile(IFile file) {

		currentFile = file;
		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			dom = db.parse(file.getContents());

		} catch (ParserConfigurationException pce) {
			PreesmLogger.getLogger().log(Level.SEVERE, pce.getMessage());
		} catch (SAXException se) {
			PreesmLogger.getLogger().log(Level.SEVERE, se.getMessage());
		} catch (IOException ioe) {
			PreesmLogger.getLogger().log(Level.SEVERE, ioe.getMessage());
		} catch (CoreException e) {
			PreesmLogger.getLogger().log(Level.SEVERE, e.getMessage());
		}
		
		return parseDocument();
	}

	/**
	 * Parses the first level of hierarchy
	 */
	private MultiCoreArchitecture parseDocument() {
		if (dom != null) {
			// get the root elememt
			Element docElt = dom.getDocumentElement();

			Node node = docElt.getFirstChild();
			archi = new MultiCoreArchitecture();

			while (node != null) {

				if (node instanceof Element) {
					Element elt = (Element) node;
					String type = elt.getTagName();
					if (type.equals("spirit:name")) {
						archi.setName(elt.getTextContent());
					} else {
						if (archi == null) {
							PreesmLogger.getLogger().log(Level.SEVERE,
									"enter a name in the architecture");
						}

						if (type.equals("spirit:id")) {
							archi.setId(elt.getTextContent());
						} else if (type.equals("spirit:componentInstances")) {
							parseComponentInstances(elt);
						} else if (type.equals("spirit:interconnections")) {
							parseInterconnections(elt);
						} else if (type.equals("spirit:hierConnections")) {
							parseHierConnections(elt);
						}
					}
				}

				node = node.getNextSibling();
			}
		}

		return archi;
	}

	/**
	 * Parses all hierarchical connections
	 */
	private void parseHierConnections(Element callElt) {

		Node node = callElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String type = elt.getTagName();
				if (type.equals("spirit:hierConnection")) {
					parseHierConnection(elt);
				}
			}

			node = node.getNextSibling();
		}
	}

	/**
	 * Parses one hierarchical interconnection
	 */
	private void parseHierConnection(Element callElt) {

		String intRef = callElt.getAttribute("spirit:interfaceRef");
		String busRef = null;
		String componentRef = null;

		Node node = callElt.getFirstChild();

		while (node != null) {
			if (node instanceof Element) {
				Element elt = (Element) node;
				String type = elt.getTagName();
				if (type.equals("spirit:activeInterface")) {
					busRef = elt.getAttribute("spirit:busRef");
					componentRef = elt.getAttribute("spirit:componentRef");
					break;
				}
			}
			node = node.getNextSibling();
		}

		if (intRef != null && busRef != null && componentRef != null) {
			HierarchyPort hierPort = new HierarchyPort(intRef, componentRef,
					busRef);
			archi.addHierarchyPort(hierPort);
		}

	}

	/**
	 * Parses the component instances
	 */
	private void parseComponentInstances(Element callElt) {

		Node node = callElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String type = elt.getTagName();
				if (type.equals("spirit:componentInstance")) {
					parseComponentInstance(elt);
				}
			}

			node = node.getNextSibling();
		}
	}

	/**
	 * Parses one component instance
	 */
	private void parseComponentInstance(Element callElt) {

		VLNV cmpDefVLNV = new VLNV();

		String cmpName = "";
		String cmpType = "";
		IFile refinementFile = null;
		String refinementName = "";

		Element configElt = null;

		Node node = callElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String type = elt.getTagName();
				if (type.equals("spirit:instanceName")) {
					cmpName = elt.getTextContent();
				} else if (type.equals("spirit:componentRef")) {
					cmpDefVLNV.setVendor(elt.getAttribute("spirit:vendor"));
					cmpDefVLNV.setLibrary(elt.getAttribute("spirit:library"));
					cmpDefVLNV.setName(elt.getAttribute("spirit:name"));
					cmpDefVLNV.setVersion(elt.getAttribute("spirit:version"));
				} else if (type.equals("spirit:configurableElementValues")) {
					configElt = elt;
					cmpType = parseComponentType(configElt);
					refinementName = getComponentRefinementName(configElt);
					refinementFile = findComponentRefinementFile(refinementName);
				}
			}

			node = node.getNextSibling();
		}

		// If the component was well parsed, it is created and added to the
		// architecture
		ArchitectureComponentType type = ArchitectureComponentType
				.getType(cmpType);

		if (type != null) {
			ArchitectureComponent cmp = archi.addComponent(
					ArchitectureComponentType.getType(cmpType), cmpDefVLNV,
					cmpName);

			cmp.setRefinementName(refinementName);

			// parse components
			if (configElt != null) {
				// Simple model
				// Looking for definitions
				if (type == ArchitectureComponentType.medium) {
					MediumParser.parse((MediumDefinition) cmp.getDefinition(),
							configElt);
				} else if (type == ArchitectureComponentType.operator) {
					OperatorParser.parse((OperatorDefinition) cmp
							.getDefinition(), configElt);
				} else if (type == ArchitectureComponentType.contentionNode) {
					ContentionNodeParser.parse((ContentionNodeDefinition) cmp
							.getDefinition(), configElt);
				} else if (type == ArchitectureComponentType.parallelNode) {
					ParallelNodeParser.parse((ParallelNodeDefinition) cmp
							.getDefinition(), configElt);
				}
			}

			// Parsing the component file if present and retrieving data
			if (refinementFile != null) {
				ComponentParser cmpParser = new ComponentParser(archi, cmp);
				cmpParser.parseXmlFile(refinementFile);
			}
		}
	}

	/**
	 * Parses a component type and returns the associated value. The component
	 * type is the most important property of a component. It states how to
	 * consider the component (as an operator or as a medium for instance)
	 */
	private String parseComponentType(Element callElt) {

		String componentType = "";

		Node node = callElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String eltType = elt.getTagName();
				String configurableElementName = elt
						.getAttribute("spirit:referenceId");
				if (eltType.equals("spirit:configurableElementValue")
						&& configurableElementName.equals("componentType")) {
					componentType = elt.getTextContent();
				}
			}

			node = node.getNextSibling();
		}

		return componentType;
	}

	/**
	 * The refinement is the file containing the IP-XACT component definition.
	 * If such a file is found, a parser is called and information is retrieved
	 * from it.
	 */
	private String getComponentRefinementName(Element callElt) {

		String componentRefinement = "";

		Node node = callElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String eltType = elt.getTagName();
				String configurableElementName = elt
						.getAttribute("spirit:referenceId");
				if (eltType.equals("spirit:configurableElementValue")
						&& configurableElementName.equals("refinement")) {
					componentRefinement = elt.getTextContent();
				}
			}

			node = node.getNextSibling();
		}

		return componentRefinement;
	}

	/**
	 * The refinement is the file containing the IP-XACT component definition.
	 * If such a file is found, a parser is called and information is retrieved
	 * from it.
	 */
	private IFile findComponentRefinementFile(String name) {

		IFile file = null;

		if (!name.isEmpty()) {

			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			String fileExt = "component";

			IPath path = new Path(name);
			IPath currentPath = currentFile.getFullPath();
			currentPath = currentPath.removeLastSegments(1);
			currentPath = currentPath.append(path);

			if(path.getFileExtension() != null
					&& path.getFileExtension().equals(fileExt)){
				file = workspace.getRoot().getFile(currentPath);
			}
			else {
				PreesmLogger
						.getLogger()
						.log(
								Level.SEVERE,
								"The refinement of a component must exist and have the extension ."
										+ fileExt
										+ ". The following file is not usable: "
										+ name);
			}
		}

		return file;
	}

	/**
	 * Parses all interconnections
	 */
	private void parseInterconnections(Element callElt) {

		Node node = callElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String type = elt.getTagName();
				if (type.equals("spirit:interconnection")) {
					parseInterconnection(elt);
				}
			}

			node = node.getNextSibling();
		}
	}

	/**
	 * Parses one interconnection
	 */
	private void parseInterconnection(Element callElt) {

		List<String> busRefList = new ArrayList<String>();
		List<String> componentRefList = new ArrayList<String>();

		boolean isDirected = false;
		boolean isSetup = false;

		Node node = callElt.getFirstChild();
		String description = "";

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String type = elt.getTagName();
				if (type.equals("spirit:activeInterface")) {
					busRefList.add(elt.getAttribute("spirit:busRef"));
					componentRefList.add(elt
							.getAttribute("spirit:componentRef"));
				} else if (type.equals("spirit:displayName")) {
					isDirected = (elt.getTextContent()
							.equalsIgnoreCase("directed"));
					isSetup = (elt.getTextContent().equalsIgnoreCase("setup"));
				} else if (type.equals("spirit:description")) {
					description = elt.getTextContent();
				}
			}

			node = node.getNextSibling();
		}

		if (busRefList.size() == 2 && componentRefList.size() == 2) {

			ArchitectureComponent cmp1 = archi.getComponent(componentRefList
					.get(0));
			cmp1 = archi.getComponent(componentRefList.get(0));
			BusReference busRef1 = archi.createBusReference(busRefList.get(0));
			ArchitectureInterface if1 = cmp1
					.addInterface(new ArchitectureInterface(busRef1, cmp1));

			ArchitectureComponent cmp2 = archi.getComponent(componentRefList
					.get(1));
			BusReference busRef2 = archi.createBusReference(busRefList.get(1));
			ArchitectureInterface if2 = cmp2
					.addInterface(new ArchitectureInterface(busRef2, cmp2));

			// Simple architecture
			if (isSetup) {
				// A setup is directed and its description gives the setup time
				isDirected = true;
				if (cmp1 instanceof Operator
						&& (cmp2 instanceof Dma || cmp2 instanceof Ram)) {
					int setupTime = 0;

					try {
						setupTime = Integer.valueOf(description);
					} catch (NumberFormatException e) {
						PreesmLogger.getLogger().log(
								Level.INFO,
								"No setup type entered for the setup link of "
										+ cmp1 + " . 0 used.");
					}

					if (cmp2 instanceof Dma) {
						((DmaDefinition) ((Dma) cmp2).getDefinition())
								.addSetupTime((Operator) cmp1, setupTime);
					} else {
						((RamDefinition) ((Ram) cmp2).getDefinition())
								.addSetupTime((Operator) cmp1, setupTime);
					}
				} else {
					PreesmLogger
							.getLogger()
							.log(Level.SEVERE,
									"a setup link must join an operator to a dma or a ram.");
				}

				archi.connect(cmp1, if1, cmp2, if2, true, isSetup);
			} else {
				archi.connect(cmp1, if1, cmp2, if2, isDirected, false);
			}
		}

	}
}
