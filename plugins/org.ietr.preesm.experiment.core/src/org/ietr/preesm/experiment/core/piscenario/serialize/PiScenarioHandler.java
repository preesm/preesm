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

import java.util.HashSet;
import java.util.Set;

import org.ietr.preesm.experiment.core.piscenario.ActorNode;
import org.ietr.preesm.experiment.core.piscenario.ParameterValue;
import org.ietr.preesm.experiment.core.piscenario.ParameterValue.ParameterType;
import org.ietr.preesm.experiment.core.piscenario.PiScenario;
import org.ietr.preesm.experiment.core.piscenario.Timing;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX {@link DefaultHandler} used to parse {@link PiScenario}
 * @author jheulot
 */
public class PiScenarioHandler extends DefaultHandler {
	/**
	 * Currently parsed {@link PiScenario}.
	 */
	private PiScenario piscenario;
	
	/**
	 * Currently parsed {@link ActorNode}.
	 */
	private ActorNode currentActorNode;
	
	/**
	 * Variable used to remember actors no more present in the Graph.
	 * When the attribute is > 0, the parser parse an unused actor.
	 */
	private int unusedActorLevel = 0;
	
	/**
	 * Default Constructor.
	 */
	public PiScenarioHandler(){
		super();
		currentActorNode = null;
		unusedActorLevel = 0;
	}
	
	@Override
	public void startElement(String uri, String localName,
			String qName, Attributes attributes) throws SAXException{
		if(unusedActorLevel != 0) 
			return;
		
		switch(qName){
		case "piscenario":
			piscenario = new PiScenario();
			break;
		case "files":
			break;
		case "algorithm":
			piscenario.setAlgorithmURL(attributes.getValue("url"));
			break;
		case "architecture":
			piscenario.setArchitectureURL(attributes.getValue("url"));
			break;
		case "simulation":
			piscenario.setNumberOfTopLevelExecutions(Integer.parseInt(attributes.getValue("nbExecutions")));
			break;
		case "actorTree":
			piscenario.update();
			currentActorNode = null;
			break;
		case "actor":
			if(currentActorNode == null){
				/* Root */
				currentActorNode = piscenario.getActorTree().getRoot();
				if(!currentActorNode.getName().equals(attributes.getValue("name"))){
					unusedActorLevel++;
				}
				break;
			}
			ActorNode n = currentActorNode.getChild(attributes.getValue("name"));
			if(n == null){
				unusedActorLevel++;
			}else{
				currentActorNode = n;
			}
			break;
		case "constraint":
			currentActorNode.setConstraint(attributes.getValue("core"), true);
			break;
		case "timing":
			String coreType = attributes.getValue("coreType");
			Timing t = currentActorNode.getTiming(coreType);
			if(t!=null)
				t.setStringValue(attributes.getValue("value"));
			break;
		case "parameter":
			ParameterValue paramValue = currentActorNode.getParamValue(attributes.getValue("name"));
			if(paramValue != null){
				switch(attributes.getValue("type")){
				case "DEPENDANT":
					if(paramValue.getType() == ParameterType.DEPENDENT)
						paramValue.setExpression(attributes.getValue("value"));
					break;
				case "STATIC":
					if(paramValue.getType() == ParameterType.STATIC)
						paramValue.setValue(Integer.parseInt(attributes.getValue("value")));
					break;
				case "DYNAMIC":
					if(paramValue.getType() == ParameterType.DYNAMIC){
						String value = (String) attributes.getValue("value");
						
						if(value.charAt(0) == '[' && value.charAt(value.length()-1) == ']'){
							value = value.substring(1,value.length()-1);
							String[] values = value.split(",");
							
							Set<Integer> newValues = new HashSet<Integer>();

							try {
								for(String val : values){
									newValues.add(Integer.parseInt(val.trim()));				
								}
								paramValue.getValues().clear();
								paramValue.getValues().addAll(newValues);
								
							} catch (NumberFormatException e) {
							}	

						}
						paramValue.setExpression(attributes.getValue("value"));
					}
					break;
				}
			}
			break;
		default:
			throw new SAXException("Unknown tag "+qName);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException{
		switch(qName){
		case "piscenario":
		case "files":
		case "algorithm":
		case "architecture":
		case "simulation":
		case "actorTree":
			break;
		case "actor":
			if(unusedActorLevel !=0)
				unusedActorLevel--;
			else if(currentActorNode != null)
				currentActorNode = currentActorNode.getParent();
			break;
		case "constraint":
			break;
		case "timing":
			break;
		case "parameter":
			break;
		default:
			throw new SAXException("Unknown tag "+qName);
		}     
	}
	
	@Override
	public void characters(char[] ch,int start, int length)
			throws SAXException{  
	}
	
	@Override
	public void startDocument() throws SAXException {
		currentActorNode = null;
		unusedActorLevel = 0;
	}
	
	@Override
	public void endDocument() throws SAXException {
	}

	/**
	 * @return The parsed {@link PiScenario}.
	 */
	public PiScenario getPiscenario() {
		return piscenario;
	}	
}
