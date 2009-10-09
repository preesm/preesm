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
import org.ietr.preesm.core.architecture.ArchitectureComponentDefinition;
import org.ietr.preesm.core.architecture.BusType;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * An xml parser retrieving architecture data from an IP-XACT component. The
 * data from the IP-XACT component is set directly in the
 * {@link ArchitectureComponent}. If the component contains one single
 * subDesign, this subdesign is set as the component refinement.
 * 
 * @author mpelcat
 */
public class ComponentParser {

	/**
	 * xml tree
	 */
	private Document dom = null;

	/**
	 * current component
	 */
	private ArchitectureComponent cmp = null;

	private IFile currentFile = null;

	public ComponentParser(MultiCoreArchitecture archi,
			ArchitectureComponent cmp) {
		this.cmp = cmp;
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

		currentFile = file;

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

		parseDocument();
	}

	/**
	 * Parses the first level of hierarchy
	 */
	private void parseDocument() {
		if (dom != null) {
			// get the root elememt
			Element docElt = dom.getDocumentElement();

			Node node = docElt.getFirstChild();

			while (node != null) {

				if (node instanceof Element) {
					Element elt = (Element) node;
					String type = elt.getTagName();
					if (type.equals("spirit:memoryMaps")) {

						parseMemoryMaps(elt);
					} else if (type.equals("spirit:model")) {
						parseSubDesign(elt);
					} else if (type.equals("spirit:busInterfaces")) {
						parseBusInterfaces(elt);
					}
				}

				node = node.getNextSibling();
			}
		}
	}

	/**
	 * Parses the memory maps to retrieve the address base and range of a
	 * component
	 */
	private String parseMemoryMaps(Element callElt) {

		String componentType = "";

		Node node = callElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String eltType = elt.getTagName();
				if (eltType.equals("spirit:memoryMap")) {
					parseMemoryMap(elt);
				}
			}

			node = node.getNextSibling();
		}

		return componentType;
	}

	/**
	 * Parses a multicore architecture being the subdesign of the current
	 * component and sets it as the component refinement
	 */
	private void parseSubDesign(Element callElt) {

		Node node = callElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String eltType = elt.getTagName();
				if (eltType.equals("spirit:views")) {
					parseSubDesignViews(elt);
				}
			}

			node = node.getNextSibling();
		}
	}

	/**
	 * Parses a subdesign view
	 */
	private void parseSubDesignViews(Element callElt) {

		Node node = callElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String eltType = elt.getTagName();
				if (eltType.equals("spirit:view")) {
					parseView(elt);
					// Only the first found view is parsed
					break;
				}
			}

			node = node.getNextSibling();
		}
	}

	/**
	 * Parses a multicore architecture being the subdesign of the current
	 * component and sets it as the component refinement
	 */
	private void parseView(Element callElt) {

		String name = null;

		Node node = callElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String eltType = elt.getTagName();
				if (eltType.equals("spirit:name")) {
					name = elt.getTextContent();
				}
			}

			node = node.getNextSibling();
		}

		if (name != null) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			String fileExt = "design";

			if (!name.isEmpty() && !name.contains("." + fileExt)) {
				name += "." + fileExt;
			}

			IPath path = new Path(name);
			IPath currentPath = currentFile.getFullPath();
			currentPath = currentPath.removeLastSegments(1);
			currentPath = currentPath.append(path);
			IFile file = null;

			if (path.getFileExtension() != null
					&& path.getFileExtension().equals(fileExt)) {
				file = workspace.getRoot().getFile(currentPath);
			}

			if (file != null) {
				DesignParser designParser = new DesignParser();
				MultiCoreArchitecture subDesign = designParser
						.parseXmlFile(file);

				if (subDesign != null) {
					cmp.setRefinement(subDesign);
				}
			}

		}
	}

	/**
	 * Parses the main memory map to retrieve the address base and range of a
	 * component
	 */
	private String parseMemoryMap(Element callElt) {

		String componentType = "";

		Node node = callElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String eltType = elt.getTagName();
				if (eltType.equals("spirit:name")) {
				} else if (eltType.equals("spirit:addressBlock")) {
					parseAddressBlock(elt);
				}
			}

			node = node.getNextSibling();
		}

		return componentType;
	}

	/**
	 * Parses the bus interfaces of the component
	 */
	private void parseBusInterfaces(Element callElt) {

		Node node = callElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String eltType = elt.getTagName();
				if (eltType.equals("spirit:busInterface")) {
					parseBusInterface(elt);
				}
			}

			node = node.getNextSibling();
		}
	}

	/**
	 * Parses one bus interface of the component
	 */
	private void parseBusInterface(Element callElt) {

		String name = null;
		VLNV vlnv = null;
		Node node = callElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String eltType = elt.getTagName();
				if (eltType.equals("spirit:name")) {
					name = elt.getTextContent();
				} else if (eltType.equals("spirit:busType")) {
					vlnv = new VLNV(elt.getAttribute("spirit:vendor"), elt
							.getAttribute("spirit:library"), elt
							.getAttribute("spirit:name"), elt
							.getAttribute("spirit:version"));
				}
			}

			node = node.getNextSibling();
		}
		
		if(name != null && vlnv != null){
			cmp.addBusType(new BusType(name,vlnv));
		}
	}

	/**
	 * Parses the main memory map address block to retrieve the address base and
	 * range of a component
	 */
	private String parseAddressBlock(Element callElt) {

		String componentType = "";

		Node node = callElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String eltType = elt.getTagName();
				if (eltType.equals("spirit:baseAddress") && cmp != null) {
					cmp.setBaseAddress(elt.getTextContent());
				} else if (eltType.equals("spirit:range") && cmp != null) {

				}
			}

			node = node.getNextSibling();
		}

		return componentType;
	}
}