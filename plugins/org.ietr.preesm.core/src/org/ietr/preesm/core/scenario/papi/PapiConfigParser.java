package org.ietr.preesm.core.scenario.papi;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.ietr.preesm.core.scenario.serialize.ScenarioParserException;
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

  private PapiCpuID           cpuId;
  private PapiHardware        hardware;
  private List<PapiComponent> components;

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
        System.out.println("other : " + node.getNodeName());
        break;
    }
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
        System.out.println("other : " + node.getNodeName());
        break;
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
        System.out.println("other : " + node.getNodeName());
        break;
    }
  }

  private void visitCpuIDStepping(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    this.cpuId.stepping = Integer.valueOf(textContent);
  }

  private void visitCpuIDModel(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    this.cpuId.model = Integer.valueOf(textContent);
  }

  private void visitCpuIDFamily(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    this.cpuId.family = Integer.valueOf(textContent);
  }

  private void visitTotalCPUs(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    this.hardware.totalCPUs = Integer.valueOf(textContent);
  }

  private void visitCpuPerNode(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    this.hardware.cpuPerNode = Integer.valueOf(textContent);
  }

  private void visitNodes(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    this.hardware.nodes = Integer.valueOf(textContent);
  }

  private void visitSockets(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    this.hardware.sockets = Integer.valueOf(textContent);
  }

  private void visitCores(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    this.hardware.cores = Integer.valueOf(textContent);
  }

  private void visitThreads(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    this.hardware.threads = Integer.valueOf(textContent);
  }

  private void visitCpuMinMegahertz(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    this.hardware.cpuMinMegahertz = Integer.valueOf(textContent);
  }

  private void visitCpuMaxMegahertz(final Node node) {
    final String textContent = node.getAttributes().getNamedItem("value").getTextContent();
    this.hardware.cpuMaxMegahertz = Integer.valueOf(textContent);
  }

  private void visitCpuID(final Node node) {
    this.cpuId = new PapiCpuID();
    visitChildrenSkippingTexts(node, this::switchCpuIDChildren);
    this.hardware.cpuID = this.cpuId;
  }

  private void visitCpuRevision(final Node cpuRevisionNode) {
    final String textContent = cpuRevisionNode.getAttributes().getNamedItem("value").getTextContent();
    this.hardware.cpuRevision = Double.valueOf(textContent);
  }

  private void visitModelCode(final Node modelCodeNode) {
    final String textContent = modelCodeNode.getAttributes().getNamedItem("value").getTextContent();
    this.hardware.modelCode = Integer.valueOf(textContent);
  }

  private void visitModel(final Node modelNode) {
    final String textContent = modelNode.getAttributes().getNamedItem("string").getTextContent();
    this.hardware.model = textContent;
  }

  private void visitVendorCode(final Node vendorCodeNode) {
    final String textContent = vendorCodeNode.getAttributes().getNamedItem("value").getTextContent();
    this.hardware.vendorCode = Integer.valueOf(textContent);
  }

  private void visitVendor(final Node vendorNode) {
    final String textContent = vendorNode.getAttributes().getNamedItem("string").getTextContent();
    this.hardware.vendor = textContent;
  }

  private void visitComponent(final Node componentNode) {
    final NamedNodeMap attributes = componentNode.getAttributes();
    final String componentID = Optional.ofNullable(attributes.getNamedItem("id")).map(Node::getTextContent).orElse(null);
    final String componentIndex = Optional.ofNullable(attributes.getNamedItem("index")).map(Node::getTextContent).orElse(null);
    final String componentType = Optional.ofNullable(attributes.getNamedItem("type")).map(Node::getTextContent).orElse(null);
    this.components.add(new PapiComponent(componentID, componentIndex, componentType));
  }

  private void visitHardware(final Node hardwareNode) {
    this.hardware = new PapiHardware();
    visitChildrenSkippingTexts(hardwareNode, this::switchHWChildren);
  }

}
