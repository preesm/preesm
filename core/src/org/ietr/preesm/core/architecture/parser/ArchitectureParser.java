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
import org.eclipse.core.runtime.CoreException;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * An xml parser retrieving architecture data
 * 
 * @author mpelcat
 */
public class ArchitectureParser {

	/**
	 * xml tree
	 */
	private Document dom = null;

	/**
	 * current architecture
	 */
	private MultiCoreArchitecture archi = null;

	public ArchitectureParser() {
		
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
	public MultiCoreArchitecture parseDocument() {
		if(dom != null){
			// get the root elememt
			Element docElt = dom.getDocumentElement();
	
			Node node = docElt.getFirstChild();
	
			
			while (node != null) {
	
				if (node instanceof Element) {
					Element elt = (Element) node;
					String type = elt.getTagName();
					if (type.equals("spirit:name")) {
						archi = new MultiCoreArchitecture(elt.getTextContent());
					} else if (type.equals("spirit:componentInstances")) {
						parseComponentInstances(elt);
						//parseInterconnections(elt);
					} else if (type.equals("spirit:interconnections")) {
						//parseInterconnections(elt);
					} 
				}
	
				node = node.getNextSibling();
			}
		}

		return archi;
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

		String cmpName = "";
		String cmpDefId = "";
		String cmpType = "";
		
		Node node = callElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String type = elt.getTagName();
				if (type.equals("spirit:instanceName")) {
					cmpName = elt.getTextContent();
				}
				else if (type.equals("spirit:componentRef")) {
					cmpDefId = elt.getAttribute("spirit:name");
				}
				else if (type.equals("spirit:configurableElementValues")) {
					cmpType = parseComponentType(elt);
				}
			}

			node = node.getNextSibling();
		}
/*
		if(cmpType.equals("Operator")){

			OperatorDefinition opDef = new OperatorDefinition(cmpDefId);
			Operator op = new Operator(cmpName,opDef);
			
			if(op!=null)
				archi.addOperator(op);
		}
		else if(cmpType.equals("Medium")){

			MediumDefinition mDef = new MediumDefinition(cmpDefId);
			Medium m = new Medium(cmpName,mDef);
			
			if(m!=null)
				archi.addMedium(m);
			
		}*/
	}
	
	/**
	 * Parses a component type and returns the associated value
	 */
	private String parseComponentType(Element callElt) {

		String componentType = "";
		
		Node node = callElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String eltType = elt.getTagName();
				String configurableElementName = elt.getAttribute("spirit:referenceId");
				if (eltType.equals("spirit:configurableElementValue") && configurableElementName.equals("componentType")) {
					componentType = elt.getTextContent();
				}
			}

			node = node.getNextSibling();
		}

		return componentType;
	}
}
