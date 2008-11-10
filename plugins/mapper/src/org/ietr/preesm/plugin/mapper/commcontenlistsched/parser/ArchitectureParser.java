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
 
package org.ietr.preesm.plugin.mapper.commcontenlistsched.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.BusDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.ComponentDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.FifoDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.IpDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.ProcessorDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.SwitchDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.TGVertexDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class ArchitectureParser {

	private static Document architectureDocument;

	private ArchitectureDescriptor architecture;

	private HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer;

	public ArchitectureParser(String fileName) {
		this.architecture = new ArchitectureDescriptor();
		this.ComponentDescriptorBuffer = architecture.getComponents();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// factory.setValidating(true);
		// factory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			architectureDocument = builder.parse(new File(fileName));
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

	public ArchitectureParser(String fileName,
			ArchitectureDescriptor architecture) {
		this.architecture = architecture;
		this.ComponentDescriptorBuffer = architecture.getComponents();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// factory.setValidating(true);
		// factory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			architectureDocument = builder.parse(new File(fileName));
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

	public ArchitectureDescriptor parse() {
		Node n = architectureDocument.getDocumentElement();
		String name = n.getNodeName();
		Node n1, n2, n3;
		String componentName = null;
		String componentId = null;
		String vertexName = null;
		String linkName = null;
		String originName = null;
		String destinationName = null;

		System.out.println("Parse architecture...");
		if (name.equalsIgnoreCase("design_architecture")) {
			n1 = n.getFirstChild();
			while (n1 != null) {
				name = n1.getNodeName();
				if (name.equalsIgnoreCase("componentInstances")) {
					n2 = n1.getFirstChild();
					while (n2 != null) {
						name = n2.getNodeName();
						if (name.equalsIgnoreCase("componentInstance")) {
							n3 = n2.getFirstChild();
							if (((Element) n2).getAttribute("type")
									.equalsIgnoreCase("processor")) {
								while (n3 != null) {
									name = n3.getNodeName();
									if (name.equalsIgnoreCase("id")) {
										componentId = n3.getFirstChild()
												.getNodeValue();
										if (componentName != null) {
											new ProcessorDescriptor(
													componentId, componentName,
													ComponentDescriptorBuffer);
											componentName = null;
											componentId = null;
										}
									} else if (name.equalsIgnoreCase("name")) {
										componentName = n3.getFirstChild()
												.getNodeValue();
										if (componentId != null) {
											new ProcessorDescriptor(
													componentId, componentName,
													ComponentDescriptorBuffer);
											componentName = null;
											componentId = null;
										}
									}
									n3 = n3.getNextSibling();
								}
							} else if (((Element) n2).getAttribute("type")
									.equalsIgnoreCase("Ip")) {
								while (n3 != null) {
									name = n3.getNodeName();
									if (name.equalsIgnoreCase("id")) {
										componentId = n3.getFirstChild()
												.getNodeValue();
										if (componentName != null) {
											new IpDescriptor(componentId,
													componentName,
													ComponentDescriptorBuffer);
											componentName = null;
											componentId = null;
										}
									} else if (name.equalsIgnoreCase("name")) {
										componentName = n3.getFirstChild()
												.getNodeValue();
										if (componentId != null) {
											new IpDescriptor(componentId,
													componentName,
													ComponentDescriptorBuffer);
											componentName = null;
											componentId = null;
										}
									}
									n3 = n3.getNextSibling();
								}
							} else if (((Element) n2).getAttribute("type")
									.equalsIgnoreCase("Bus")) {
								while (n3 != null) {
									name = n3.getNodeName();
									if (name.equalsIgnoreCase("id")) {
										componentId = n3.getFirstChild()
												.getNodeValue();
										if (componentName != null) {
											new BusDescriptor(componentId,
													componentName,
													ComponentDescriptorBuffer);
											componentName = null;
											componentId = null;
										}
									} else if (name.equalsIgnoreCase("name")) {
										componentName = n3.getFirstChild()
												.getNodeValue();
										if (componentId != null) {
											new BusDescriptor(componentId,
													componentName,
													ComponentDescriptorBuffer);
											componentName = null;
											componentId = null;
										}
									}
									n3 = n3.getNextSibling();
								}
							} else if (((Element) n2).getAttribute("type")
									.equalsIgnoreCase("Fifo")) {
								while (n3 != null) {
									name = n3.getNodeName();
									if (name.equalsIgnoreCase("id")) {
										componentId = n3.getFirstChild()
												.getNodeValue();
										if (componentName != null) {
											new FifoDescriptor(componentId,
													componentName,
													ComponentDescriptorBuffer);
											componentName = null;
											componentId = null;
										}
									} else if (name.equalsIgnoreCase("name")) {
										componentName = n3.getFirstChild()
												.getNodeValue();
										if (componentId != null) {
											new FifoDescriptor(componentId,
													componentName,
													ComponentDescriptorBuffer);
											componentName = null;
											componentId = null;
										}
									}
									n3 = n3.getNextSibling();
								}
							} else if (((Element) n2).getAttribute("type")
									.equalsIgnoreCase("Switch")) {
								while (n3 != null) {
									name = n3.getNodeName();
									if (name.equalsIgnoreCase("id")) {
										componentId = n3.getFirstChild()
												.getNodeValue();
										if (componentName != null) {
											new SwitchDescriptor(componentId,
													componentName,
													ComponentDescriptorBuffer);
											componentName = null;
											componentId = null;
										}
									} else if (name.equalsIgnoreCase("name")) {
										componentName = n3.getFirstChild()
												.getNodeValue();
										if (componentId != null) {
											new SwitchDescriptor(componentId,
													componentName,
													ComponentDescriptorBuffer);
											componentName = null;
											componentId = null;
										}
									}
									n3 = n3.getNextSibling();
								}
							}
						}
						n2 = n2.getNextSibling();
					}
				} else if (name.equalsIgnoreCase("interconnections")) {
					n2 = n1.getFirstChild();
					while (n2 != null) {
						name = n2.getNodeName();
						if (name.equalsIgnoreCase("interconnection")) {
							n3 = n2.getFirstChild();
							if (((Element) n2).getAttribute("type")
									.equalsIgnoreCase("undirected")) {
								while (n3 != null) {
									name = n3.getNodeName();
									if (name.equalsIgnoreCase("vertexRef")) {
										vertexName = n3.getFirstChild()
												.getNodeValue();
									} else if (name.equalsIgnoreCase("linkRef")) {
										linkName = n3.getFirstChild()
												.getNodeValue();
									}
									if (vertexName != null && linkName != null) {
										((TGVertexDescriptor) ComponentDescriptorBuffer
												.get(vertexName))
												.addInputLink((BusDescriptor) ComponentDescriptorBuffer
														.get(linkName));
										((TGVertexDescriptor) ComponentDescriptorBuffer
												.get(vertexName))
												.addOutputLink((BusDescriptor) ComponentDescriptorBuffer
														.get(linkName));
										((BusDescriptor) ComponentDescriptorBuffer
												.get(linkName))
												.addTGVertex((TGVertexDescriptor) ComponentDescriptorBuffer
														.get(vertexName));
										vertexName = null;
										linkName = null;
									}
									n3 = n3.getNextSibling();
								}
							} else if (((Element) n2).getAttribute("type")
									.equalsIgnoreCase("directed")) {
								while (n3 != null) {
									name = n3.getNodeName();
									if (name.equalsIgnoreCase("originRef")) {
										originName = n3.getFirstChild()
												.getNodeValue();
									} else if (name
											.equalsIgnoreCase("destinationRef")) {
										destinationName = n3.getFirstChild()
												.getNodeValue();
									} else if (name.equalsIgnoreCase("linkRef")) {
										linkName = n3.getFirstChild()
												.getNodeValue();
									}
									if (linkName != null) {
										if (originName != null) {
											((TGVertexDescriptor) ComponentDescriptorBuffer
													.get(originName))
													.addOutputLink((FifoDescriptor) ComponentDescriptorBuffer
															.get(linkName));
											((FifoDescriptor) ComponentDescriptorBuffer
													.get(linkName))
													.setOrigin((TGVertexDescriptor) ComponentDescriptorBuffer
															.get(originName));
											originName = null;
											linkName = null;
										} else if (destinationName != null) {
											((TGVertexDescriptor) ComponentDescriptorBuffer
													.get(originName))
													.addInputLink((FifoDescriptor) ComponentDescriptorBuffer
															.get(linkName));
											((FifoDescriptor) ComponentDescriptorBuffer
													.get(linkName))
													.setDestination((TGVertexDescriptor) ComponentDescriptorBuffer
															.get(destinationName));
											destinationName = null;
											linkName = null;
										}
									}
									n3 = n3.getNextSibling();
								}
							}
						}
						n2 = n2.getNextSibling();
					}
				}
				n1 = n1.getNextSibling();
			}
		}
		System.out.println("Architecture parsed!");
		return architecture;
	}
}
