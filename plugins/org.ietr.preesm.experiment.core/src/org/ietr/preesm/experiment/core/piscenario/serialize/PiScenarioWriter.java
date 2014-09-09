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

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.resources.IFile;
import org.ietr.preesm.experiment.core.piscenario.ActorNode;
import org.ietr.preesm.experiment.core.piscenario.ActorTree;
import org.ietr.preesm.experiment.core.piscenario.ParameterValue;
import org.ietr.preesm.experiment.core.piscenario.PiScenario;

/**
 * Writes a {@link PiScenario} as an XML
 * 
 * @author jheulot
 */
public class PiScenarioWriter {
	/**
	 * Current scenario
	 */
	private PiScenario piscenario;

	public PiScenarioWriter(PiScenario piscenario) {
		super();
		this.piscenario = piscenario;
	}

	public void writeDom(IFile file) {
		try {

			StringWriter sw = new StringWriter();
			FileWriter fileWriter = new FileWriter(file.getRawLocation().toFile());
			
			XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
			XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(sw);
			
			xmlStreamWriter.writeStartDocument();
			xmlStreamWriter.writeStartElement("piscenario");
			
			writeFiles(xmlStreamWriter);
			writeActorTree(xmlStreamWriter);
			
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndDocument();
			
			xmlStreamWriter.flush();
			xmlStreamWriter.close();
			
			/* Use of a transformer to indent xml file */
			TransformerFactory factory = TransformerFactory.newInstance();
			
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			
			transformer.transform(new StreamSource(new StringReader(sw.toString())), new StreamResult(fileWriter));
	        
		} catch (TransformerException | XMLStreamException | IOException e) {
			e.printStackTrace();
		}
	}

	private void writeFiles(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
		xmlStreamWriter.writeStartElement("files");
		
		xmlStreamWriter.writeStartElement("algorithm");
		xmlStreamWriter.writeAttribute("url", piscenario.getAlgorithmURL());
		xmlStreamWriter.writeEndElement();
		
		xmlStreamWriter.writeStartElement("architecture");
		xmlStreamWriter.writeAttribute("url", piscenario.getArchitectureURL());
		xmlStreamWriter.writeEndElement();
		
		xmlStreamWriter.writeStartElement("simulation");
		xmlStreamWriter.writeAttribute("nbExecutions", "" + piscenario.getNumberOfTopLevelExecutions());
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeEndElement();
	}
	
	private void writeActorTreeChildren(XMLStreamWriter xmlStreamWriter, ActorNode actorNode) throws XMLStreamException{
		if(actorNode.isHierarchical()){
			/* Write Child Actors */
			for(ActorNode childNode : actorNode.getChildren()){
				xmlStreamWriter.writeStartElement("actor");
				xmlStreamWriter.writeAttribute("name", childNode.getName());
				writeActorTreeChildren(xmlStreamWriter, childNode);
				xmlStreamWriter.writeEndElement();
			}
			
			/* Write Child Parameters */
			for(ParameterValue paramValue : actorNode.getParamValues()){
				xmlStreamWriter.writeStartElement("parameter");
				xmlStreamWriter.writeAttribute("name", paramValue.getName());
				xmlStreamWriter.writeAttribute("type", paramValue.getType().toString());
				switch(paramValue.getType()){
				case DEPENDENT:
					xmlStreamWriter.writeAttribute("value", paramValue.getExpression());
					break;
				case DYNAMIC:
					xmlStreamWriter.writeAttribute("value", paramValue.getValues().toString());
					break;
				case STATIC:
					xmlStreamWriter.writeAttribute("value", ""+paramValue.getValue());
					break;
				default:
					break;
				}
				xmlStreamWriter.writeEndElement();				
			}
			
		}else{
			/* Write Constraints */
			for(String core : piscenario.getOperatorIds()){
				if(actorNode.getConstraint(core)){
					xmlStreamWriter.writeStartElement("constraint");
					xmlStreamWriter.writeAttribute("core", core);
					xmlStreamWriter.writeEndElement();
				}
			}
			
			/* Write Timings */
			for(String coreType : actorNode.getTimings().keySet()){
				String value = actorNode.getTimings().get(coreType).getStringValue();

				xmlStreamWriter.writeStartElement("timing");
				xmlStreamWriter.writeAttribute("coreType", coreType);
				xmlStreamWriter.writeAttribute("value", value);
				xmlStreamWriter.writeEndElement();
			}
		}
	}
	
	private void writeActorTree(XMLStreamWriter xmlStreamWriter) throws XMLStreamException{
		ActorTree tree = piscenario.getActorTree(); 
		
		xmlStreamWriter.writeStartElement("actorTree");
		
		xmlStreamWriter.writeStartElement("actor");
		xmlStreamWriter.writeAttribute("name", tree.getRoot().getName());
		writeActorTreeChildren(xmlStreamWriter, tree.getRoot());
		xmlStreamWriter.writeEndElement();
		
		xmlStreamWriter.writeEndElement();
	}
}
