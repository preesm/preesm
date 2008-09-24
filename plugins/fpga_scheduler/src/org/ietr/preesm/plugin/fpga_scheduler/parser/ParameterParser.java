package org.ietr.preesm.plugin.fpga_scheduler.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ietr.preesm.plugin.fpga_scheduler.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.BusDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComponentDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.FifoDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.IpDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.NetworkDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.OperatorDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ProcessorDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class ParameterParser {

	private static Document designParametersDocument;

	private HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer;

	private HashMap<String, ComputationDescriptor> ComputationDescriptorBuffer;

	private HashMap<String, CommunicationDescriptor> CommunicationDescriptorBuffer;

	public ParameterParser(String fileName,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer,
			HashMap<String, ComputationDescriptor> ComputationDescriptorBuffer) {
		this.ComponentDescriptorBuffer = ComponentDescriptorBuffer;
		this.ComputationDescriptorBuffer = ComputationDescriptorBuffer;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// factory.setValidating(true);
		// factory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			designParametersDocument = builder.parse(new File(fileName));
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

	public ParameterParser(
			String fileName,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer,
			HashMap<String, ComputationDescriptor> ComputationDescriptorBuffer,
			HashMap<String, CommunicationDescriptor> CommunicationDescriptorBuffer) {
		this.ComponentDescriptorBuffer = ComponentDescriptorBuffer;
		this.ComputationDescriptorBuffer = ComputationDescriptorBuffer;
		this.CommunicationDescriptorBuffer = CommunicationDescriptorBuffer;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// factory.setValidating(true);
		// factory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			designParametersDocument = builder.parse(new File(fileName));
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

	public ParameterParser(String fileName,
			ArchitectureDescriptor architecture, AlgorithmDescriptor algorithm) {
		ComponentDescriptorBuffer = architecture.getComponents();
		ComputationDescriptorBuffer = algorithm.getComputations();
		CommunicationDescriptorBuffer = algorithm.getCommunications();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// factory.setValidating(true);
		// factory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			designParametersDocument = builder.parse(new File(fileName));
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

	public int parse() {
		// Parse the design parameter document
		Node n = designParametersDocument.getDocumentElement();
		String name = n.getNodeName();
		Node n1;
		int timeConstraint = 0;
		System.out.println("Parse parameter...");
		if (name.equalsIgnoreCase("design_parameter")) {
			n1 = n.getFirstChild();
			while (n1 != null) {
				name = n1.getNodeName();
				if (name.equalsIgnoreCase("timeConstraint")) {
					timeConstraint = Integer.parseInt(n1.getFirstChild()
							.getNodeValue());
				} else if (name.equalsIgnoreCase("components")) {
					parseComponents(n1);
				} else if (name.equalsIgnoreCase("computations")) {
					parseComputations(n1);
				} else if (name.equalsIgnoreCase("communications")) {
					parseCommunications(n1);
				}
				n1 = n1.getNextSibling();
			}
		}
		System.out.println("Parameter parsed!");
		return timeConstraint;
	}

	private void parseComponents(Node n1) {
		String name;
		Node n2, n3;
		String componentName = "component";
		n2 = n1.getFirstChild();
		System.out.println(" 1. parse parameter of components...");
		while (n2 != null) {
			name = n2.getNodeName();
			if (name.equalsIgnoreCase("component")) {
				if (((Element) n2).getAttribute("type").equalsIgnoreCase("IP")) {
					n3 = n2.getFirstChild();
					while (n3 != null) {
						name = n3.getNodeName();
						if (name.equalsIgnoreCase("name")) {
							componentName = n3.getFirstChild().getNodeValue();
							new IpDescriptor(componentName, componentName,
									ComponentDescriptorBuffer);
						} else if (name.equalsIgnoreCase("clockPeriod")) {
							ComponentDescriptorBuffer.get(componentName)
									.setClockPeriod(
											Integer.parseInt(n3.getFirstChild()
													.getNodeValue()));
						} else if (name.equalsIgnoreCase("userInterfaceType")) {
							((IpDescriptor) ComponentDescriptorBuffer
									.get(componentName))
									.setUserInterfaceType(n3.getFirstChild()
											.getNodeValue());
						} else if (name.equalsIgnoreCase("dataWidth")) {
							ComponentDescriptorBuffer.get(componentName)
									.setDataWidth(
											Integer.parseInt(n3.getFirstChild()
													.getNodeValue()));
						} else if (name.equalsIgnoreCase("inputDataNumber")) {
							((IpDescriptor) ComponentDescriptorBuffer
									.get(componentName))
									.setNbInputData(Integer.parseInt(n3
											.getFirstChild().getNodeValue()));
						} else if (name.equalsIgnoreCase("outputDataNumber")) {
							((IpDescriptor) ComponentDescriptorBuffer
									.get(componentName))
									.setNbOutputData(Integer.parseInt(n3
											.getFirstChild().getNodeValue()));
						} else if (name.equalsIgnoreCase("latency")) {
							((IpDescriptor) ComponentDescriptorBuffer
									.get(componentName))
									.setLatency(Integer.parseInt(n3
											.getFirstChild().getNodeValue()));
						} else if (name.equalsIgnoreCase("cadence")) {
							((IpDescriptor) ComponentDescriptorBuffer
									.get(componentName))
									.setCadence(Integer.parseInt(n3
											.getFirstChild().getNodeValue()));
						} else if (name.equalsIgnoreCase("surface")) {
							ComponentDescriptorBuffer.get(componentName)
									.setSurface(
											Integer.parseInt(n3.getFirstChild()
													.getNodeValue()));
						}
						n3 = n3.getNextSibling();
					}
				} else if (((Element) n2).getAttribute("type")
						.equalsIgnoreCase("Processor")) {
					n3 = n2.getFirstChild();
					while (n3 != null) {
						name = n3.getNodeName();
						if (name.equalsIgnoreCase("name")) {
							componentName = n3.getFirstChild().getNodeValue();
							new ProcessorDescriptor(componentName,
									componentName, ComponentDescriptorBuffer);
						} else if (name.equalsIgnoreCase("clockPeriod")) {
							ComponentDescriptorBuffer.get(componentName)
									.setClockPeriod(
											Integer.parseInt(n3.getFirstChild()
													.getNodeValue()));
						} else if (name.equalsIgnoreCase("dataWidth")) {
							ComponentDescriptorBuffer.get(componentName)
									.setDataWidth(
											Integer.parseInt(n3.getFirstChild()
													.getNodeValue()));
						} else if (name.equalsIgnoreCase("surface")) {
							ComponentDescriptorBuffer.get(componentName)
									.setSurface(
											Integer.parseInt(n3.getFirstChild()
													.getNodeValue()));
						}
						n3 = n3.getNextSibling();
					}
				} else if (((Element) n2).getAttribute("type")
						.equalsIgnoreCase("Bus")) {
					n3 = n2.getFirstChild();
					while (n3 != null) {
						name = n3.getNodeName();
						if (name.equalsIgnoreCase("name")) {
							componentName = n3.getFirstChild().getNodeValue();
							new BusDescriptor(componentName, componentName,
									ComponentDescriptorBuffer);
						} else if (name.equalsIgnoreCase("clockPeriod")) {
							ComponentDescriptorBuffer.get(componentName)
									.setClockPeriod(
											Integer.parseInt(n3.getFirstChild()
													.getNodeValue()));
						} else if (name.equalsIgnoreCase("dataWidth")) {
							ComponentDescriptorBuffer.get(componentName)
									.setDataWidth(
											Integer.parseInt(n3.getFirstChild()
													.getNodeValue()));
						} else if (name
								.equalsIgnoreCase("averageClockCyclesPerTransfer")) {
							((BusDescriptor) ComponentDescriptorBuffer
									.get(componentName))
									.setAverageClockCyclesPerTransfer(Double
											.parseDouble(n3.getFirstChild()
													.getNodeValue()));
						} else if (name.equalsIgnoreCase("portNumber")) {
							((BusDescriptor) ComponentDescriptorBuffer
									.get(componentName))
									.setPortNumber(Integer.parseInt(n3
											.getFirstChild().getNodeValue()));
						} else if (name.equalsIgnoreCase("surface")) {
							ComponentDescriptorBuffer.get(componentName)
									.setSurface(
											Integer.parseInt(n3.getFirstChild()
													.getNodeValue()));
						}
						n3 = n3.getNextSibling();
					}
				} else if (((Element) n2).getAttribute("type")
						.equalsIgnoreCase("Fifo")) {
					n3 = n2.getFirstChild();
					while (n3 != null) {
						name = n3.getNodeName();
						if (name.equalsIgnoreCase("name")) {
							componentName = n3.getFirstChild().getNodeValue();
							new FifoDescriptor(componentName, componentName,
									ComponentDescriptorBuffer);
						} else if (name.equalsIgnoreCase("clockPeriod")) {
							ComponentDescriptorBuffer.get(componentName)
									.setClockPeriod(
											Integer.parseInt(n3.getFirstChild()
													.getNodeValue()));
						} else if (name.equalsIgnoreCase("dataWidth")) {
							ComponentDescriptorBuffer.get(componentName)
									.setDataWidth(
											Integer.parseInt(n3.getFirstChild()
													.getNodeValue()));
						} else if (name
								.equalsIgnoreCase("averageClockCyclesPerTransfer")) {
							((FifoDescriptor) ComponentDescriptorBuffer
									.get(componentName))
									.setAverageClockCyclesPerTransfer(Double
											.parseDouble(n3.getFirstChild()
													.getNodeValue()));
						} else if (name.equalsIgnoreCase("surface")) {
							ComponentDescriptorBuffer.get(componentName)
									.setSurface(
											Integer.parseInt(n3.getFirstChild()
													.getNodeValue()));
						}
						n3 = n3.getNextSibling();
					}
				} else if (((Element) n2).getAttribute("type")
						.equalsIgnoreCase("Network")) {
					n3 = n2.getFirstChild();
					while (n3 != null) {
						name = n3.getNodeName();
						if (name.equalsIgnoreCase("name")) {
							componentName = n3.getFirstChild().getNodeValue();
							new NetworkDescriptor(componentName, componentName,
									ComponentDescriptorBuffer);
						} else if (name.equalsIgnoreCase("clockPeriod")) {
							ComponentDescriptorBuffer.get(componentName)
									.setClockPeriod(
											Integer.parseInt(n3.getFirstChild()
													.getNodeValue()));
						} else if (name.equalsIgnoreCase("dataWidth")) {
							ComponentDescriptorBuffer.get(componentName)
									.setDataWidth(
											Integer.parseInt(n3.getFirstChild()
													.getNodeValue()));
						} else if (name
								.equalsIgnoreCase("averageClockCyclesPerTransfer")) {
							((NetworkDescriptor) ComponentDescriptorBuffer
									.get(componentName))
									.setAverageClockCyclesPerTransfer(Double
											.parseDouble(n3.getFirstChild()
													.getNodeValue()));
						} else if (name.equalsIgnoreCase("portNumber")) {
							((NetworkDescriptor) ComponentDescriptorBuffer
									.get(componentName))
									.setPortNumber(Integer.parseInt(n3
											.getFirstChild().getNodeValue()));
						} else if (name.equalsIgnoreCase("surface")) {
							ComponentDescriptorBuffer.get(componentName)
									.setSurface(
											Integer.parseInt(n3.getFirstChild()
													.getNodeValue()));
						}
						n3 = n3.getNextSibling();
					}
				}
			}
			n2 = n2.getNextSibling();
		}
	}

	private void parseComputations(Node n1) {

		String name;
		Node n2, n3, n4, n5;
		String componentName = "component";
		String computationName = "computation";

		n2 = n1.getFirstChild();
		System.out.println(" 2. parse parmeter of computations...");
		while (n2 != null) {
			name = n2.getNodeName();
			if (name.equalsIgnoreCase("computation")) {
				n3 = n2.getFirstChild();
				while (n3 != null) {
					name = n3.getNodeName();
					if (name.equalsIgnoreCase("name")) {
						computationName = n3.getFirstChild().getNodeValue();
					} else if (name.equalsIgnoreCase("associatedOperator")) {
						componentName = n3.getFirstChild().getNodeValue();
						ComputationDescriptorBuffer
								.get(computationName)
								.setOperator(
										(OperatorDescriptor) ComponentDescriptorBuffer
												.get(componentName));
					} else if (name.equalsIgnoreCase("computationDurations")) {
						n4 = n3.getFirstChild();
						while (n4 != null) {
							name = n4.getNodeName();
							if (name.equalsIgnoreCase("computationDuration")) {
								n5 = n4.getFirstChild();
								while (n5 != null) {
									name = n5.getNodeName();
									if (name.equalsIgnoreCase("operatorName")) {
										componentName = n5.getFirstChild()
												.getNodeValue();
									} else if (name
											.equalsIgnoreCase("duration")) {
										ComputationDescriptorBuffer
												.get(computationName)
												.addComputationDuration(
														(OperatorDescriptor) ComponentDescriptorBuffer
																.get(componentName),
														Integer
																.parseInt(n5
																		.getFirstChild()
																		.getNodeValue()));
									}
									n5 = n5.getNextSibling();
								}
							}
							n4 = n4.getNextSibling();
						}
					}
					n3 = n3.getNextSibling();
				}
			}
			n2 = n2.getNextSibling();
		}
	}

	private void parseCommunications(Node n1) {
		String name;
		Node n2, n3, n4, n5;
		String communicationName = null;
		String networkName = null;
		String duration = null;
		String operatorName = null;
		String sendOverhead = null;
		String receiveOverhead = null;
		String linkName = null;
		String sendInvolvement = null;
		String receiveInvolvement = null;

		n2 = n1.getFirstChild();
		System.out.println(" 3. parse parameter of communications...");
		while (n2 != null) {
			name = n2.getNodeName();
			if (name.equalsIgnoreCase("communication")) {
				n3 = n2.getFirstChild();
				while (n3 != null) {
					name = n3.getNodeName();
					if (name.equalsIgnoreCase("name")) {
						communicationName = n3.getFirstChild().getNodeValue();
					} else if (name.equalsIgnoreCase("communicationDurations")) {
						n4 = n3.getFirstChild();
						while (n4 != null) {
							name = n4.getNodeName();
							if (name.equalsIgnoreCase("communicationDuration")) {
								n5 = n4.getFirstChild();
								while (n5 != null) {
									name = n5.getNodeName();
									if (name.equalsIgnoreCase("networkName")) {
										networkName = n5.getFirstChild()
												.getNodeValue();
									} else if (name
											.equalsIgnoreCase("duration")) {
										duration = n5.getFirstChild()
												.getNodeValue();
									}
									n5 = n5.getNextSibling();
								}
								if (networkName != null && duration != null) {
									CommunicationDescriptorBuffer
											.get(communicationName)
											.addCommunicationDuration(
													(NetworkDescriptor) ComponentDescriptorBuffer
															.get(networkName),
													Integer.parseInt(duration));
									duration = null;
									networkName = null;
								}
							}
							n4 = n4.getNextSibling();
						}
					} else if (name.equalsIgnoreCase("overheads")) {
						n4 = n3.getFirstChild();
						while (n4 != null) {
							name = n4.getNodeName();
							if (name.equalsIgnoreCase("overhead")) {
								n5 = n4.getFirstChild();
								while (n5 != null) {
									name = n5.getNodeName();
									if (name.equalsIgnoreCase("operatorName")) {
										operatorName = n5.getFirstChild()
												.getNodeValue();
									} else if (name
											.equalsIgnoreCase("sendOverhead")) {
										sendOverhead = n5.getFirstChild()
												.getNodeValue();
									} else if (name
											.equalsIgnoreCase("receiveOverhead")) {
										receiveOverhead = n5.getFirstChild()
												.getNodeValue();
									}
									n5 = n5.getNextSibling();
								}
								if (operatorName != null) {
									if (sendOverhead != null) {
										CommunicationDescriptorBuffer
												.get(communicationName)
												.addSendOverhead(
														operatorName,
														Integer
																.parseInt(sendOverhead));
										sendOverhead = null;
									}
									if (receiveOverhead != null) {
										CommunicationDescriptorBuffer
												.get(communicationName)
												.addReceiveOverhead(
														operatorName,
														Integer
																.parseInt(receiveOverhead));
										receiveOverhead = null;
									}
								}
								operatorName = null;
							}
							n4 = n4.getNextSibling();
						}
					} else if (name.equalsIgnoreCase("involvements")) {
						n4 = n3.getFirstChild();
						while (n4 != null) {
							name = n4.getNodeName();
							if (name.equalsIgnoreCase("involvement")) {
								n5 = n4.getFirstChild();
								while (n5 != null) {
									name = n5.getNodeName();
									if (name.equalsIgnoreCase("linkName")) {
										linkName = n5.getFirstChild()
												.getNodeValue();
									} else if (name
											.equalsIgnoreCase("sendInvolvement")) {
										sendInvolvement = n5.getFirstChild()
												.getNodeValue();
									} else if (name
											.equalsIgnoreCase("receiveInvolvement")) {
										receiveInvolvement = n5.getFirstChild()
												.getNodeValue();
									}
									n5 = n5.getNextSibling();
								}
								if (linkName != null) {
									if (sendInvolvement != null) {
										CommunicationDescriptorBuffer
												.get(communicationName)
												.addSendInvolvement(
														linkName,
														Integer
																.parseInt(sendInvolvement));
										sendInvolvement = null;
									}
									if (receiveInvolvement != null) {
										CommunicationDescriptorBuffer
												.get(communicationName)
												.addReceiveInvolvement(
														linkName,
														Integer
																.parseInt(receiveInvolvement));
										receiveInvolvement = null;
									}
								}
								linkName = null;
							}
							n4 = n4.getNextSibling();
						}
					}
					n3 = n3.getNextSibling();
				}
				communicationName = null;
			}
			n2 = n2.getNextSibling();
		}
	}

}
