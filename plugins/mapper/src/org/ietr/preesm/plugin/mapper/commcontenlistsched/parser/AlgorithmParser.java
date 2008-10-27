package org.ietr.preesm.plugin.mapper.commcontenlistsched.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.ComputationDescriptor;
import org.sdf4j.factories.DAGEdgeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class AlgorithmParser {

	private static Document algorithmDocument;

	protected AlgorithmDescriptor algorithm;

	protected HashMap<String, ComputationDescriptor> ComputationDescriptorBuffer;

	protected HashMap<String, CommunicationDescriptor> CommunicationDescriptorBuffer;

	public AlgorithmParser(String fileName) {
		this.algorithm = new AlgorithmDescriptor(new DAGEdgeFactory());
		this.ComputationDescriptorBuffer = algorithm.getComputations();
		this.CommunicationDescriptorBuffer = algorithm.getCommunications();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// factory.setValidating(true);
		// factory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			algorithmDocument = builder.parse(new File(fileName));
		} catch (SAXException sxe) {
			// Error generated during parsing)
			Exception x = sxe;
			if (sxe.getException() != null)
				x = sxe.getException();
			x.printStackTrace();
		} catch (ParserConfigurationException pce) {
			// Parser with specified options can't be built
			pce.printStackTrace();
		} catch (IOException ioe) {
			// I/O error
			ioe.printStackTrace();
		}
	}

	public AlgorithmParser(String fileName, AlgorithmDescriptor algorithm) {
		this.algorithm = algorithm;
		this.ComputationDescriptorBuffer = algorithm.getComputations();
		this.CommunicationDescriptorBuffer = algorithm.getCommunications();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// factory.setValidating(true);
		// factory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			algorithmDocument = builder.parse(new File(fileName));
		} catch (SAXException sxe) {
			// Error generated during parsing)
			Exception x = sxe;
			if (sxe.getException() != null)
				x = sxe.getException();
			x.printStackTrace();
		} catch (ParserConfigurationException pce) {
			// Parser with specified options can't be built
			pce.printStackTrace();
		} catch (IOException ioe) {
			// I/O error
			ioe.printStackTrace();
		}
	}

	public AlgorithmDescriptor parse() {
		Node n = algorithmDocument.getDocumentElement();
		String name = n.getNodeName();
		Node n1, n2, n3, n4;
		String nodeName = "node";
		String edgeName = "edge";
		System.out.println("Parse algorithm...");
		n1 = n.getFirstChild();
		if (name.equalsIgnoreCase("design_algorithm")) {
			while (n1 != null) {
				name = n1.getNodeName();
				n2 = n1.getFirstChild();
				if (name.equalsIgnoreCase("nodes")) {
					while (n2 != null) {
						name = n2.getNodeName();
						n3 = n2.getFirstChild();
						if (name.equalsIgnoreCase("node")) {
							while (n3 != null) {
								name = n3.getNodeName();
								n4 = n3.getFirstChild();
								if (name.equalsIgnoreCase("name")) {
									nodeName = n4.getNodeValue();
									new ComputationDescriptor(nodeName,
											ComputationDescriptorBuffer);
								} else if (name.equalsIgnoreCase("time")) {
									ComputationDescriptorBuffer.get(nodeName)
											.setTime(
													Integer.parseInt(n4
															.getNodeValue()));
								} else if (name
										.equalsIgnoreCase("repeatNumber")) {
									ComputationDescriptorBuffer.get(nodeName)
											.setNbTotalRepeat(
													Integer.parseInt(n4
															.getNodeValue()));
								}
								n3 = n3.getNextSibling();
							}
						}
						n2 = n2.getNextSibling();
					}
				} else if (name.equalsIgnoreCase("edges")) {
					while (n2 != null) {
						name = n2.getNodeName();
						n3 = n2.getFirstChild();
						if (name.equalsIgnoreCase("edge")) {
							while (n3 != null) {
								name = n3.getNodeName();
								n4 = n3.getFirstChild();
								if (name.equalsIgnoreCase("name")) {
									edgeName = n4.getNodeValue();
									new CommunicationDescriptor(edgeName,
											CommunicationDescriptorBuffer);
								} else if (name.equalsIgnoreCase("source")) {
									nodeName = n4.getNodeValue();
									CommunicationDescriptorBuffer.get(edgeName)
											.setOrigin(nodeName);
									ComputationDescriptorBuffer.get(nodeName)
											.addOutputCommunication(
													CommunicationDescriptorBuffer
															.get(edgeName));
								} else if (name.equalsIgnoreCase("destination")) {
									nodeName = n4.getNodeValue();
									CommunicationDescriptorBuffer.get(edgeName)
											.setDestination(nodeName);
									ComputationDescriptorBuffer.get(nodeName)
											.addInputCommunication(
													CommunicationDescriptorBuffer
															.get(edgeName));
								} else if (name.equalsIgnoreCase("weight")) {

									CommunicationDescriptorBuffer.get(edgeName)
											.setWeight(
													Integer.parseInt(n4
															.getNodeValue()));
								}
								n3 = n3.getNextSibling();
							}
						}
						n2 = n2.getNextSibling();
					}
				}
				n1 = n1.getNextSibling();
			}
		}

		for (ComputationDescriptor indexComputation : ComputationDescriptorBuffer
				.values()) {
			indexComputation.setAlgorithm(algorithm);
			algorithm.addComputation(indexComputation);
		}
		for (CommunicationDescriptor indexCommunication : CommunicationDescriptorBuffer
				.values()) {
			indexCommunication.setAlgorithm(algorithm);
			algorithm.addCommunication(indexCommunication);
		}
		System.out.println("Algorithm parsed!");
		return algorithm;
	}
}
