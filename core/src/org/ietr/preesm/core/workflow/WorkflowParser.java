/**
 * 
 */
package org.ietr.preesm.core.workflow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jgrapht.DirectedGraph;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This class provides a workflow parser.
 * 
 * @author Matthieu Wipliez
 * 
 */
public class WorkflowParser extends DefaultHandler2 {

	/**
	 * The last parsed transformation node is saved to receive its variables.
	 */
	TaskNode lastTransformationNode = null;

	private Map<String, IWorkflowNode> nodes;

	private DirectedGraph<IWorkflowNode, WorkflowEdge> workflow;
	
	/**
	 * Creates a new workflow parser.
	 * 
	 * @param fileName
	 *            The source file name.
	 * @param workflow
	 *            The workflow represented as a graph.
	 */
	public WorkflowParser(String fileName,
			DirectedGraph<IWorkflowNode, WorkflowEdge> workflow) {
		this.nodes = new HashMap<String, IWorkflowNode>();
		this.workflow = workflow;
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(this);
			reader.parse(new InputSource(new FileInputStream(fileName)));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		
		if (qName.equals("preesm:algorithm")) {
			IWorkflowNode node = new AlgorithmNode();
			workflow.addVertex(node);
			nodes.put("__algorithm", node);
		} else if (qName.equals("preesm:architecture")) {
			IWorkflowNode node = new ArchitectureNode();
			workflow.addVertex(node);
			nodes.put("__architecture", node);
		} else if (qName.equals("preesm:scenario")) {
			IWorkflowNode node = new ScenarioNode();
			workflow.addVertex(node);
			nodes.put("__scenario", node);
		} else if (qName.equals("preesm:task")) {
			String id = attributes.getValue("taskId");
			String pluginId = attributes.getValue("pluginId");
			lastTransformationNode = new TaskNode(pluginId);
			IWorkflowNode node = lastTransformationNode;
			workflow.addVertex(node);
			nodes.put(id, node);
		} else if (qName.equals("preesm:dataTransfer")) {
			IWorkflowNode source = nodes.get(attributes.getValue("from"));
			IWorkflowNode target = nodes.get(attributes.getValue("to"));
			String dataType = attributes.getValue("targetport");
			WorkflowEdge edge = workflow.addEdge(source, target);
			edge.setCarriedDataType(dataType);
		} else if (qName.equals("variable")) {
			if(lastTransformationNode != null){
				lastTransformationNode.addVariable(attributes.getValue("name"),attributes.getValue("value"));
			}
		}
	}
}
