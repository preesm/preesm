/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
package org.preesm.model.scenario.papi;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.preesm.model.scenario.serialize.ScenarioParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 *
 * @author anmorvan
 *
 */
public class PapiConfigParser {

  public PapiConfigParser() {
    // nothing particular
  }

  private final void visitChildrenSkippingTexts(final Node node, final Consumer<Node> c) {
    final NodeList childNodes = node.getChildNodes();
    final int length = childNodes.getLength();
    for (int i = 0; i < length; i++) {
      final Node child = childNodes.item(i);
      if (child instanceof Text) {
        // skip
      } else {
        c.accept(child);
      }
    }
  }

  private PapiCpuID               cpuId;
  private PapiHardware            hardware;
  private List<PapiComponent>     components;
  private List<PapiEvent>         events;
  private List<PapiEventSet>      eventSets;
  private List<PapiEventModifier> modifiers;

  /**
   * @return
   *
   */
  public final PapiEventInfo parse(final String filePath) {
    final File xmlConfigFile = Paths.get(filePath).toFile();
    final Document document = openDocument(xmlConfigFile);
    final Element classElement = document.getDocumentElement();

    this.components = new ArrayList<>();
    visitChildrenSkippingTexts(classElement, this::switchRootChildren);
    return new PapiEventInfo(this.hardware, this.components);
  }

  private final Document openDocument(final File xmlConfigFile) {
    final Document doc;
    try {
      final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      doc = dBuilder.parse(xmlConfigFile);
    } catch (final Exception e) {
      throw new ScenarioParserException("Could not open the PAPI config xml file", e);
    }
    return doc;
  }

  private final void switchRootChildren(final Node node) {
    switch (node.getNodeName()) {
      case "hardware":
        visitHardware(node);
        break;
      case "component":
        visitComponent(node);
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  private void switchComponentChildren(final Node node) {
    switch (node.getNodeName()) {
      case "eventset":
        visitEventSet(node);
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  private void switchEventSetChildren(final Node node) {
    switch (node.getNodeName()) {
      case "event":
        visitEvent(node);
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  private void switchEventChildren(final Node node) {
    switch (node.getNodeName()) {
      case "modifier":
        visitModifier(node);
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  private void visitModifier(final Node node) {
    final PapiEventModifier papiEventModifier = new PapiEventModifier();
    final String name = Optional.ofNullable(node.getAttributes().getNamedItem("name")).map(Node::getTextContent)
        .orElse(null);
    final String desc = Optional.ofNullable(node.getAttributes().getNamedItem("desc")).map(Node::getTextContent)
        .orElse(null);
    papiEventModifier.setName(name);
    papiEventModifier.setDescription(desc);
    this.modifiers.add(papiEventModifier);
  }

  private void visitEvent(final Node node) {
    final int index = Optional.ofNullable(node.getAttributes().getNamedItem("index")).map(Node::getTextContent)
        .map(Integer::valueOf).orElse(null);
    final String name = Optional.ofNullable(node.getAttributes().getNamedItem("name")).map(Node::getTextContent)
        .orElse(null);
    final String desc = Optional.ofNullable(node.getAttributes().getNamedItem("desc")).map(Node::getTextContent)
        .orElse(null);
    this.modifiers = new ArrayList<>();
    visitChildrenSkippingTexts(node, this::switchEventChildren);
    final PapiEvent event = new PapiEvent();
    event.setIndex(index);
    event.setName(name);
    event.setDesciption(desc);
    event.setModifiers(this.modifiers);
    this.events.add(event);
  }

  private void visitEventSet(final Node node) {
    this.events = new ArrayList<>();
    visitChildrenSkippingTexts(node, this::switchEventSetChildren);
    final PapiEventSet eventSet = new PapiEventSet();
    final PapiEventSetType type = Optional.ofNullable(node.getAttributes().getNamedItem("type"))
        .map(Node::getTextContent).map(PapiEventSetType::valueOf).orElse(null);
    eventSet.setType(type);
    eventSet.setEvents(this.events);
    this.eventSets.add(eventSet);
  }

  private void switchHWChildren(final Node node) {
    switch (node.getNodeName()) {
      case "vendor":
        visitVendor(node);
        break;
      case "vendorCode":
        visitVendorCode(node);
        break;
      case "model":
        visitModel(node);
        break;
      case "modelCode":
        visitModelCode(node);
        break;
      case "cpuRevision":
        visitCpuRevision(node);
        break;
      case "cpuID":
        visitCpuID(node);
        break;
      case "cpuMaxMegahertz":
        visitCpuMaxMegahertz(node);
        break;
      case "cpuMinMegahertz":
        visitCpuMinMegahertz(node);
        break;
      case "threads":
        visitThreads(node);
        break;
      case "cores":
        visitCores(node);
        break;
      case "sockets":
        visitSockets(node);
        break;
      case "nodes":
        visitNodes(node);
        break;
      case "cpuPerNode":
        visitCpuPerNode(node);
        break;
      case "totalCPUs":
        visitTotalCPUs(node);
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  private void switchCpuIDChildren(final Node node) {
    switch (node.getNodeName()) {
      case "family":
        visitCpuIDFamily(node);
        break;
      case "model":
        visitCpuIDModel(node);
        break;
      case "stepping":
        visitCpuIDStepping(node);
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  private void visitCpuIDStepping(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    final Integer value = Integer.valueOf(textContent);
    this.cpuId.setStepping(value);
  }

  private void visitCpuIDModel(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    final Integer value = Integer.valueOf(textContent);
    this.cpuId.setModel(value);
  }

  private void visitCpuIDFamily(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    final Integer value = Integer.valueOf(textContent);
    this.cpuId.setFamily(value);
  }

  private void visitTotalCPUs(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    final Integer value = Integer.valueOf(textContent);
    this.hardware.setTotalCPUs(value);
  }

  private void visitCpuPerNode(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    final Integer value = Integer.valueOf(textContent);
    this.hardware.setCpuPerNode(value);
  }

  private void visitNodes(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    final Integer value = Integer.valueOf(textContent);
    this.hardware.setNodes(value);
  }

  private void visitSockets(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    final Integer value = Integer.valueOf(textContent);
    this.hardware.setSockets(value);
  }

  private void visitCores(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    final Integer value = Integer.valueOf(textContent);
    this.hardware.setCores(value);
  }

  private void visitThreads(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    final Integer value = Integer.valueOf(textContent);
    this.hardware.setThreads(value);
  }

  private void visitCpuMinMegahertz(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    final Integer value = Integer.valueOf(textContent);
    this.hardware.setCpuMinMegahertz(value);
  }

  private void visitCpuMaxMegahertz(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    final Integer value = Integer.valueOf(textContent);
    this.hardware.setCpuMaxMegahertz(value);
  }

  private void visitCpuID(final Node node) {
    this.cpuId = new PapiCpuID();
    visitChildrenSkippingTexts(node, this::switchCpuIDChildren);
    this.hardware.setCpuID(this.cpuId);
  }

  private void visitCpuRevision(final Node cpuRevisionNode) {
    final String textContent = cpuRevisionNode.getAttributes().getNamedItem("value").getTextContent();
    final Double value = Double.valueOf(textContent);
    this.hardware.setCpuRevision(value);
  }

  private void visitModelCode(final Node modelCodeNode) {
    final String textContent = modelCodeNode.getAttributes().getNamedItem("value").getTextContent();
    final Integer value = Integer.valueOf(textContent);
    this.hardware.setModelCode(value);
  }

  private void visitModel(final Node modelNode) {
    final String textContent = modelNode.getAttributes().getNamedItem("string").getTextContent();
    this.hardware.setModel(textContent);
  }

  private void visitVendorCode(final Node vendorCodeNode) {
    final String textContent = vendorCodeNode.getAttributes().getNamedItem("value").getTextContent();
    final Integer value = Integer.valueOf(textContent);
    this.hardware.setVendorCode(value);
  }

  private void visitVendor(final Node vendorNode) {
    final String textContent = vendorNode.getAttributes().getNamedItem("string").getTextContent();
    this.hardware.setVendor(textContent);
  }

  private void visitComponent(final Node componentNode) {
    final NamedNodeMap attributes = componentNode.getAttributes();
    final String componentID = Optional.ofNullable(attributes.getNamedItem("id")).map(Node::getTextContent)
        .orElse(null);
    final String componentIndex = Optional.ofNullable(attributes.getNamedItem("index")).map(Node::getTextContent)
        .orElse(null);
    final String componentType = Optional.ofNullable(attributes.getNamedItem("type")).map(Node::getTextContent)
        .orElse(null);
    final PapiComponent component = new PapiComponent(componentID, componentIndex, componentType);
    this.eventSets = new ArrayList<>();
    visitChildrenSkippingTexts(componentNode, this::switchComponentChildren);
    component.setEventSets(this.eventSets);
    this.components.add(component);

  }

  private void visitHardware(final Node hardwareNode) {
    this.hardware = new PapiHardware();
    visitChildrenSkippingTexts(hardwareNode, this::switchHWChildren);
  }

}
