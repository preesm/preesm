package org.ietr.preesm.experiment.model.pimm.serialize;

import java.io.InputStream;

import net.sf.dftools.architecture.utils.DomUtil;

import org.eclipse.emf.common.util.URI;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InputPort;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.OutputPort;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.SinkInterface;
import org.ietr.preesm.experiment.model.pimm.SourceInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PiParser {

	/**
	 * Retrieve the value of a property of the given {@link Element}. A property
	 * is a data element child of the given element.<br>
	 * <br>
	 * 
	 * This method will iterate over the properties of the element so it might
	 * not be a good idea to use it in a method that would successively retrieve
	 * all properties of the element.
	 * 
	 * @param elt
	 *            The element containing the property
	 * @param propertyName
	 *            The name of the property
	 * @return The property value or null if the property was not found
	 * @author Jonathan Piat
	 */
	protected static String getProperty(Element elt, String propertyName) {
		NodeList childList = elt.getChildNodes();
		for (int i = 0; i < childList.getLength(); i++) {
			if (childList.item(i).getNodeName().equals("data")
					&& ((Element) childList.item(i)).getAttribute("key")
							.equals(propertyName)) {
				return childList.item(i).getTextContent();
			}
		}
		return null;
	}

	/**
	 * The URI of the parsed file
	 */
	private URI uri;

	public PiParser(URI uri) {
		this.uri = uri;
	}

	/**
	 * Parse the PiMM {@link Graph} from the given {@link InputStream} using the
	 * Pi format.
	 * 
	 * @param inputStream
	 *            The Parsed input stream
	 * @return The parsed Graph or null is something went wrong
	 */
	public Graph parse(InputStream inputStream) {
		// Instantiate the graph that will be filled with parser informations
		Graph graph = PiMMFactory.eINSTANCE.createGraph();

		// Parse the input stream
		Document document = DomUtil.parseDocument(inputStream);

		// Retrieve the root element
		Element rootElt = document.getDocumentElement();

		try {
			// Fill the graph with parsed information
			parsePi(rootElt, graph);
		} catch (RuntimeException e) {
			e.printStackTrace();
			return null;
		}

		return graph;
	}

	/**
	 * Parse a node {@link Element} with kind "actor".
	 * 
	 * @param nodeElt
	 *            the {@link Element} to parse
	 * @param graph
	 *            the deserialized {@link Graph}
	 * @return the created actor
	 */
	protected AbstractActor parseActor(Element nodeElt, Graph graph) {
		// Instantiate the new actor
		Actor actor = PiMMFactory.eINSTANCE.createActor();

		// Get the actor properties
		actor.setName(nodeElt.getAttribute("id"));

		// Add the actor to the parsed graph
		graph.getVertices().add(actor);

		String refinement = getProperty(nodeElt, "graph_desc");
		actor.getRefinement().setFileName(refinement);

		return actor;
	}

	/**
	 * Parse a ConfigInputInterface (i.e. a {@link Parameter}) of the Pi File.
	 * 
	 * @param nodeElt
	 *            The node {@link Element} holding the {@link Parameter}
	 *            properties.
	 * @param graph
	 *            the deserialized {@link Graph}.
	 * @return the {@link AbstractVertex} of the {@link Parameter}.
	 */
	protected AbstractVertex parseConfigInputInterface(Element nodeElt,
			Graph graph) {
		// Instantiate the new Config Input Interface
		Parameter param = PiMMFactory.eINSTANCE.createParameter();
		param.setConfigurationInterface(true);
		param.setLocallyStatic(true);

		// Get the actor properties
		param.setName(nodeElt.getAttribute("id"));

		// Add the actor to the parsed graph
		graph.getParameters().add(param);

		return param;
	}

	/**
	 * Parse a node {@link Element} with kind "dependency".
	 * 
	 * @param edgeElt
	 *            the {@link Element} to parse
	 * @param graph
	 *            the deserialized {@link Graph}
	 */
	protected void parseDependencies(Element edgeElt, Graph graph) {
		// Instantiate the new Dependency
		Dependency dependency = PiMMFactory.eINSTANCE.createDependency();

		// Find the source and target of the fifo
		String setterName = edgeElt.getAttribute("source");
		String getterName = edgeElt.getAttribute("target");
		AbstractVertex source = graph.getVertexNamed(setterName);
		AbstractVertex target = graph.getVertexNamed(getterName);
		if (source == null) {
			throw new RuntimeException("Dependency source vertex " + setterName
					+ " does not exist.");
		}
		if (target == null) {
			throw new RuntimeException("Dependency target vertex " + setterName
					+ " does not exist.");
		}

		// Get the sourcePort and targetPort
		if (source instanceof Actor) {

			String sourcePortName = edgeElt.getAttribute("sourceport");
			sourcePortName = (sourcePortName == "") ? null : sourcePortName;
			ConfigOutputPort oPort = (ConfigOutputPort) ((Actor) source)
					.getPortNamed(sourcePortName);
			if (oPort == null) {
				throw new RuntimeException("Edge source port " + sourcePortName
						+ " does not exist for vertex " + setterName);
			}
			dependency.setSetter(oPort);
		}
		if (source instanceof Parameter) {
			dependency.setSetter((ISetter) source);
		}

		if (target instanceof Actor) {
			String targetPortName = edgeElt.getAttribute("targetport");
			targetPortName = (targetPortName == "") ? null : targetPortName;
			ConfigInputPort iPort = (ConfigInputPort) target
					.getPortNamed(targetPortName);
			if (iPort == null) {
				throw new RuntimeException("Dependency target port "
						+ targetPortName + " does not exist for vertex "
						+ getterName);
			}
			dependency.setGetter(iPort);
		}

		if (target instanceof Parameter || target instanceof InterfaceActor) {
			ConfigInputPort iCfgPort = PiMMFactory.eINSTANCE
					.createConfigInputPort();
			target.getConfigInputPorts().add(iCfgPort);
			dependency.setGetter(iCfgPort);
		}
		// TODO target instance of Fifo

		if (dependency.getGetter() == null || dependency.getSetter() == null) {
			throw new RuntimeException(
					"There was a problem parsing the following dependency: "
							+ setterName + "=>" + getterName);
		}

		// Add the new dependency to the graph
		graph.getDependencies().add(dependency);
	}

	/**
	 * Parse an edge {@link Element} of the Pi description. An edge
	 * {@link Element} can be a parameter dependency or a FIFO of the parsed
	 * graph.
	 * 
	 * @param edgeElt
	 *            The edge {@link Element} to parse
	 * @param graph
	 *            The deserialized graph
	 */
	protected void parseEdge(Element edgeElt, Graph graph) {
		// Identify if the node is an actor or a parameter
		String edgeKind = edgeElt.getAttribute("kind");

		switch (edgeKind) {
		case "fifo":
			parseFifo(edgeElt, graph);
			break;
		case "dependency":
			parseDependencies(edgeElt, graph);
			break;
		default:
			throw new RuntimeException("Parsed edge has an unknown kind: "
					+ edgeKind);
		}
	}

	/**
	 * Parse a node {@link Element} with kind "fifo".
	 * 
	 * @param nodeElt
	 *            the {@link Element} to parse
	 * @param graph
	 *            the deserialized {@link Graph}
	 */
	protected void parseFifo(Element edgeElt, Graph graph) {
		// Instantiate the new Fifo
		Fifo fifo = PiMMFactory.eINSTANCE.createFifo();

		// Find the source and target of the fifo
		String sourceName = edgeElt.getAttribute("source");
		String targetName = edgeElt.getAttribute("target");
		AbstractActor source = (AbstractActor) graph.getVertexNamed(sourceName);
		AbstractActor target = (AbstractActor) graph.getVertexNamed(targetName);
		if (source == null) {
			throw new RuntimeException("Edge source vertex " + sourceName
					+ " does not exist.");
		}
		if (target == null) {
			throw new RuntimeException("Edge target vertex " + sourceName
					+ " does not exist.");
		}

		// Get the sourcePort and targetPort
		String sourcePortName = edgeElt.getAttribute("sourceport");
		sourcePortName = (sourcePortName == "") ? null : sourcePortName;
		String targetPortName = edgeElt.getAttribute("targetport");
		targetPortName = (targetPortName == "") ? null : targetPortName;
		OutputPort oPort = (OutputPort) source.getPortNamed(sourcePortName);
		InputPort iPort = (InputPort) target.getPortNamed(targetPortName);

		if (iPort == null) {
			throw new RuntimeException("Edge target port " + targetPortName
					+ " does not exist for vertex " + targetName);
		}
		if (oPort == null) {
			throw new RuntimeException("Edge source port " + sourcePortName
					+ " does not exist for vertex " + sourceName);
		}

		fifo.setSourcePort(oPort);
		fifo.setTargetPort(iPort);

		// Add the new Fifo to the graph
		graph.getFifos().add(fifo);
	}

	/**
	 * Retrieve and parse the graph element of the Pi description
	 * 
	 * @param rootElt
	 *            The root element (that must have a graph child)
	 * @param graph
	 *            The deserialized {@link Graph}
	 */
	protected void parseGraph(Element rootElt, Graph graph) {
		// Retrieve the Graph Element
		NodeList graphElts = rootElt.getElementsByTagName("graph");
		if (graphElts.getLength() == 0) {
			throw new RuntimeException(
					"No graph was found in the parsed document");
		}
		if (graphElts.getLength() > 1) {
			throw new RuntimeException(
					"More than one graph was found in the parsed document");
		}
		// If this code is reached, a unique graph element was found in the
		// document
		Element graphElt = (Element) graphElts.item(0);

		// TODO parseGraphProperties() of the graph

		// Parse the elements of the graph
		NodeList childList = graphElt.getChildNodes();
		for (int i = 0; i < childList.getLength(); i++) {
			Node elt = childList.item(i);

			String eltName = elt.getNodeName();

			switch (eltName) {
			case "data":
				// Properties of the Graph.
				// TODO transfer this code in a separate function
				// parseGraphProperties()
				String keyName = elt.getAttributes().getNamedItem("key")
						.getNodeValue();
				String keyValue = elt.getTextContent();
				if (keyName.equals("name")) {
					graph.setName(keyValue);
				}
				break;
			case "node":
				// Node elements
				parseNode((Element) elt, graph);
				break;
			case "edge":
				// Edge elements
				parseEdge((Element) elt, graph);
				break;
			default:

			}
		}
	}

	/**
	 * Parse a node {@link Element} of the Pi description. A node
	 * {@link Element} can be a parameter or an vertex of the parsed graph.
	 * 
	 * @param nodeElt
	 *            The node {@link Element} to parse
	 * @param graph
	 *            The deserialized {@link Graph}
	 */
	protected void parseNode(Element nodeElt, Graph graph) {
		// Identify if the node is an actor or a parameter
		String nodeKind = nodeElt.getAttribute("kind");
		AbstractVertex vertex;

		switch (nodeKind) {
		case "actor":
			vertex = parseActor(nodeElt, graph);
			break;
		case "src":
			vertex = parseSourceInterface(nodeElt, graph);
			break;
		case "snk":
			vertex = parseSinkInterface(nodeElt, graph);
			break;
		case "param":
			vertex = parseParameter(nodeElt, graph);
			break;
		case "cfg_in_iface":
			vertex = parseConfigInputInterface(nodeElt, graph);
			break;
		// TODO Parse all types of nodes
		// case "implode":
		// break;
		// case "explode":
		// break;
		// case "parameter":
		// break;

		default:
			throw new RuntimeException("Parsed node " + nodeElt.getNodeName()
					+ " has an unknown kind: " + nodeKind);
		}

		// Parse the elements of the node
		NodeList childList = nodeElt.getChildNodes();
		for (int i = 0; i < childList.getLength(); i++) {
			Node elt = childList.item(i);
			String eltName = elt.getNodeName();

			switch (eltName) {
			case "port":

				parsePort((Element) elt, vertex);

				break;
			default:
				// ignore #text and unknown children
			}
		}
		// TODO parsePorts() of the vertex

	}

	/**
	 * Parse a {@link Parameter} of the Pi File.
	 * 
	 * @param nodeElt
	 *            The node {@link Element} holding the {@link Parameter}
	 *            properties.
	 * @param graph
	 *            the deserialized {@link Graph}.
	 * @return the {@link AbstractVertex} of the {@link Parameter}.
	 */
	protected AbstractVertex parseParameter(Element nodeElt, Graph graph) {
		// Instantiate the new Parameter
		Parameter param = PiMMFactory.eINSTANCE.createParameter();
		param.setConfigurationInterface(false);
		param.setLocallyStatic(true);
		param.setGraphPort(null); // No port of the graph corresponds to this
									// parameter

		// Get the actor properties
		param.setName(nodeElt.getAttribute("id"));

		// Add the actor to the parsed graph
		graph.getParameters().add(param);

		return param;
	}

	/**
	 * Parse the root element of the Pi description
	 * 
	 * @param parentElt
	 *            The Element to fill (could be removed later if it is always
	 *            rootElt)
	 * @param graph
	 *            The deserialized {@link Graph}
	 */
	protected void parsePi(Element rootElt, Graph graph) {
		// TODO parseKeys() (Not sure if it is really necessary to do that)

		// Parse the graph element
		parseGraph(rootElt, graph);

	}

	/**
	 * Parse a {@link Port} of the Pi file.
	 * 
	 * @param elt
	 *            the {@link Element} holding the {@link Port} properties.
	 * @param vertex
	 *            the {@link AbstractVertex} owning this {@link Port}
	 */
	protected void parsePort(Element elt, AbstractVertex vertex) {
		String portName = elt.getAttribute("name");
		String portKind = elt.getAttribute("kind");

		switch (portKind) {
		case "input":
			// Throw an error if the parsed vertex is not an actor
			if (!(vertex instanceof AbstractActor)) {
				throw new RuntimeException("Parsed data port " + portName
						+ " cannot belong to the non-actor vertex "
						+ vertex.getName());
			}

			InputPort iPort = PiMMFactory.eINSTANCE.createInputPort();
			iPort.setName(portName);
			// Do not parse data ports for InterfaceActor since the unique port
			// is automatically created when the vertex is instantiated
			if (!(vertex instanceof InterfaceActor)) {
				((AbstractActor) vertex).getInputPorts().add(iPort);
			}

			break;
		case "output":
			// Throw an error if the parsed vertex is not an actor
			if (!(vertex instanceof AbstractActor)) {
				throw new RuntimeException("Parsed data port " + portName
						+ " cannot belong to the non-actor vertex "
						+ vertex.getName());
			}
			OutputPort oPort = PiMMFactory.eINSTANCE.createOutputPort();
			oPort.setName(portName);
			// Do not parse data ports for InterfaceActor since the unique port
			// is automatically created when the vertex is instantiated
			if (!(vertex instanceof InterfaceActor)) {
				((AbstractActor) vertex).getOutputPorts().add(oPort);
			}
			break;
		case "cfg_input":
			ConfigInputPort iCfgPort = PiMMFactory.eINSTANCE
					.createConfigInputPort();
			iCfgPort.setName(portName);
			vertex.getConfigInputPorts().add(iCfgPort);
			break;

		case "cfg_output":
			// Throw an error if the parsed vertex is not an actor
			if (!(vertex instanceof AbstractActor)) {
				throw new RuntimeException("Parsed config. port " + portName
						+ " cannot belong to the non-actor vertex "
						+ vertex.getName());
			}
			ConfigOutputPort oCfgPort = PiMMFactory.eINSTANCE
					.createConfigOutputPort();
			oCfgPort.setName(portName);
			((AbstractActor) vertex).getConfigOutputPorts().add(oCfgPort);
			break;
		default:
			throw new RuntimeException("Parsed port " + portName
					+ " has children of unknown kind: " + portKind);
		}
	}

	/**
	 * Parse a node {@link Element} with kind "snk".
	 * 
	 * @param nodeElt
	 *            the {@link Element} to parse
	 * @param graph
	 *            the deserialized {@link Graph}
	 * @return the created {@link SinkInterface}
	 */
	protected AbstractActor parseSinkInterface(Element nodeElt, Graph graph) {
		// Instantiate the new Interface and its corresponding port
		SinkInterface snkInterface = PiMMFactory.eINSTANCE
				.createSinkInterface();

		// Set the sourceInterface properties
		snkInterface.setName(nodeElt.getAttribute("id"));

		// Add the actor to the parsed graph
		graph.getVertices().add(snkInterface);

		return snkInterface;
	}

	/**
	 * Parse a node {@link Element} with kind "src".
	 * 
	 * @param nodeElt
	 *            the {@link Element} to parse
	 * @param graph
	 *            the deserialized {@link Graph}
	 * @return the created {@link SourceInterface}
	 */
	protected AbstractActor parseSourceInterface(Element nodeElt, Graph graph) {
		// Instantiate the new Interface and its corresponding port
		SourceInterface srcInterface = PiMMFactory.eINSTANCE
				.createSourceInterface();

		// Set the sourceInterface properties
		srcInterface.setName(nodeElt.getAttribute("id"));

		// Add the actor to the parsed graph
		graph.getVertices().add(srcInterface);

		return srcInterface;
	}

}
