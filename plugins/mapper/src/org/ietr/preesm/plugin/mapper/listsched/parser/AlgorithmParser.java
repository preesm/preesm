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
package org.ietr.preesm.plugin.mapper.listsched.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ietr.preesm.plugin.mapper.listsched.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.ComputationDescriptor;
import org.sdf4j.factories.DAGEdgeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * This class parses XML document of algorithm
 * 
 * @author pmu
 * 
 */
public class AlgorithmParser {
	/**
	 * Algorithm document
	 */
	private static Document algorithmDocument;

	/**
	 * Algorithm
	 */
	protected AlgorithmDescriptor algorithm;

	/**
	 * Computation buffer
	 */
	protected HashMap<String, ComputationDescriptor> ComputationDescriptorBuffer;

	/**
	 * Communication buffer
	 */
	protected HashMap<String, CommunicationDescriptor> CommunicationDescriptorBuffer;

	/**
	 * Construct a parser with the given file name
	 * 
	 * @param fileName
	 *            The given file name
	 */
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

	/**
	 * Construct a parser with the given file name and algorithm
	 * 
	 * @param fileName
	 *            The given file name
	 * @param algorithm
	 *            Algorithm descriptor
	 */
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

	/**
	 * Parse the document
	 * 
	 * @return An algorithm
	 */
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
