/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011 - 2012)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.ietr.dftools.workflow;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.workflow.elements.AbstractWorkflowNode;
import org.ietr.dftools.workflow.elements.ScenarioNode;
import org.ietr.dftools.workflow.elements.TaskNode;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.elements.WorkflowEdge;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This class provides a workflow parser, allowing to parse a .workflow file and to obtain a Workflow java object.
 *
 * @author Matthieu Wipliez
 *
 */
public class WorkflowParser extends DefaultHandler2 {
  /**
   * The last parsed transformation node is saved to receive its variables.
   */
  TaskNode lastTransformationNode = null;

  /** The nodes. */
  private final Map<String, AbstractWorkflowNode> nodes;

  /** The workflow. */
  private Workflow workflow = null;

  /**
   * Instantiates a new workflow parser.
   */
  public WorkflowParser() {
    this.nodes = new LinkedHashMap<>();
    this.workflow = new Workflow();
  }

  /**
   * Parse a workflow source file with the given fileName and returns the corresponding Workflow.
   *
   * @param fileName
   *          The source file name.
   * @return the workflow
   */
  public Workflow parse(final String fileName) {

    final Path relativePath = new Path(fileName);
    this.workflow.setPath(relativePath);
    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(relativePath);
    return parse(file);
  }

  /**
   * Parse a workflow source file and returns the corresponding Workflow.
   *
   * @param file
   *          The source file.
   * @return the workflow
   */
  public Workflow parse(final IFile file) {
    try {
      final XMLReader reader = XMLReaderFactory.createXMLReader();
      reader.setContentHandler(this);
      reader.parse(new InputSource(file.getContents()));
    } catch (SAXException | IOException | CoreException e) {
      throw new WorkflowException("Could not parse workflow", e);
    }
    return this.workflow;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String,
   * org.xml.sax.Attributes)
   */
  @Override
  public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) {

    if (qName.equals("dftools:scenario")) {
      final String pluginId = attributes.getValue("pluginId");
      final AbstractWorkflowNode node = new ScenarioNode(pluginId);
      this.workflow.addVertex(node);
      this.nodes.put("scenario", node);
    } else if (qName.equals("dftools:task")) {
      final String taskId = attributes.getValue("taskId");
      final String pluginId = attributes.getValue("pluginId");
      this.lastTransformationNode = new TaskNode(pluginId, taskId);
      final AbstractWorkflowNode node = this.lastTransformationNode;
      this.workflow.addVertex(node);
      this.nodes.put(taskId, node);
    } else if (qName.equals("dftools:dataTransfer")) {
      final AbstractWorkflowNode source = this.nodes.get(attributes.getValue("from"));
      final AbstractWorkflowNode target = this.nodes.get(attributes.getValue("to"));
      final String sourcePort = attributes.getValue("sourceport");
      final String targetPort = attributes.getValue("targetport");
      final WorkflowEdge edge = this.workflow.addEdge(source, target);
      edge.setSourcePort(sourcePort);
      edge.setTargetPort(targetPort);
    } else if (qName.equals("dftools:variable") && this.lastTransformationNode != null) {
      this.lastTransformationNode.addParameter(attributes.getValue("name"), attributes.getValue("value"));
    }
  }

  /**
   * Gets the workflow.
   *
   * @return the workflow
   */
  public Workflow getWorkflow() {
    return this.workflow;
  }
}
