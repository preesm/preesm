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

package org.ietr.preesm.workflow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.workflow.elements.IWorkflowNode;
import org.ietr.preesm.workflow.elements.ScenarioNode;
import org.ietr.preesm.workflow.elements.TaskNode;
import org.ietr.preesm.workflow.elements.Workflow;
import org.ietr.preesm.workflow.elements.WorkflowEdge;
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
	/*
	*//**
	 * The last parsed transformation node is saved to receive its variables.
	 */
	TaskNode lastTransformationNode = null;

	private Map<String, IWorkflowNode> nodes;

	private Workflow workflow = null;

	public WorkflowParser() {
		this.nodes = new HashMap<String, IWorkflowNode>();
		this.workflow = new Workflow();
	}

	/**
	 * Creates a new workflow parser.
	 * 
	 * @param fileName
	 *            The source file name.
	 * @param workflow
	 *            The workflow represented as a graph.
	 */
	public Workflow parse(String fileName) {

		Path relativePath = new Path(fileName);
		IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(relativePath);

		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(this);
			reader.parse(new InputSource(file.getContents()));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return workflow;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {

		if (qName.equals("preesm:scenario")) {
			String pluginId = attributes.getValue("pluginId");
			IWorkflowNode node = new ScenarioNode(pluginId);
			workflow.addNode(node);
			nodes.put("__scenario", node);
		} else if (qName.equals("preesm:task")) {
			String id = attributes.getValue("taskId");
			String pluginId = attributes.getValue("pluginId");
			lastTransformationNode = new TaskNode(pluginId);
			IWorkflowNode node = lastTransformationNode;
			workflow.addNode(node);
			nodes.put(id, node);
		} else if (qName.equals("preesm:dataTransfer")) {
			IWorkflowNode source = nodes.get(attributes.getValue("from"));
			IWorkflowNode target = nodes.get(attributes.getValue("to"));
			String dataType = attributes.getValue("targetport");
			WorkflowEdge edge = workflow.addEdge(source, target);
			edge.setDataType(dataType);
		} else if (qName.equals("variable")) {
			if (lastTransformationNode != null) {
				lastTransformationNode.addParameter(
						attributes.getValue("name"),
						attributes.getValue("value"));
			}
		}
	}

	public Workflow getWorkflow() {
		return workflow;
	}
}
