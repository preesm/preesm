/**
 * 
 */
package org.ietr.preesm.core.architecture.parser;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.simplemodel.MediumDefinition;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A medium has specific properties parsed in this class to generate a MediumDefinition
 * 
 * @author mpelcat
 */
public class MediumParser {

	/**
	 * Parsing medium specific data from DOM document and filling the medium definition
	 */
	static void parse(MediumDefinition def, Element callElt){
		
		Node node = callElt.getFirstChild();

		while (node != null) {

			if (node instanceof Element) {
				Element elt = (Element) node;
				String eltType = elt.getTagName();
				String configurableElementName = elt.getAttribute("spirit:referenceId");
				if (eltType.equals("spirit:configurableElementValue") && configurableElementName.equals("medium_invDataRate")) {
					String value = elt.getTextContent();
					def.setInvSpeed(Float.parseFloat(value));
				} else if (eltType.equals("spirit:configurableElementValue") && configurableElementName.equals("medium_overhead")) {
					String value = elt.getTextContent();
					def.setOverhead(Integer.parseInt(value));
				} 
			}

			node = node.getNextSibling();
		}

	}
}
