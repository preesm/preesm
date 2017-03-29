/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2014)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.experiment.model.pimm.serialize;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.ietr.dftools.architecture.utils.DomUtil;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.HRefinement;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation;
import org.ietr.preesm.experiment.model.pimm.util.PiIdentifiers;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parser for the PiMM Model in the Pi format
 * 
 * @author kdesnos
 * @author jheulot
 * 
 */
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
	private URI documentURI;

	public PiParser(URI uri) {
		this.documentURI = uri;
	}

	/**
	 * Parse the PiMM {@link PiGraph} from the given {@link InputStream} using
	 * the Pi format.
	 * 
	 * @param inputStream
	 *            The Parsed input stream
	 * @return The parsed Graph or null is something went wrong
	 */
	public PiGraph parse(InputStream inputStream) {
		// Instantiate the graph that will be filled with parser informations
		PiGraph graph = PiMMFactory.eINSTANCE.createPiGraph();

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
	 *            the deserialized {@link PiGraph}
	 * @return the created actor
	 */
	protected Actor parseActor(Element nodeElt, PiGraph graph) {
		// Instantiate the new actor
		Actor actor = PiMMFactory.eINSTANCE.createActor();

		// Get the actor properties
		actor.setName(nodeElt.getAttribute(PiIdentifiers.ACTOR_NAME));

		// Add the actor to the parsed graph
		graph.getVertices().add(actor);

		parseRefinement(nodeElt, actor);

		String memoryScript = getProperty(nodeElt,
				PiIdentifiers.ACTOR_MEMORY_SCRIPT);
		if (memoryScript != null && !memoryScript.isEmpty()) {
			IPath path = getWorkspaceRelativePathFrom(new Path(memoryScript));
			actor.setMemoryScriptPath(path);
		}

		return actor;
	}

	private void parseRefinement(Element nodeElt, Actor actor) {
		String refinement = getProperty(nodeElt, PiIdentifiers.REFINEMENT);
		if (refinement != null && !refinement.isEmpty()) {
			IPath path = getWorkspaceRelativePathFrom(new Path(refinement));

			// If the refinement is a .h file, then we need to create a
			// HRefinement
			if (path.getFileExtension().equals("h")) {
				HRefinement hrefinement = PiMMFactory.eINSTANCE
						.createHRefinement();
				// The nodeElt should have a loop element, and may have an init
				// element
				NodeList childList = nodeElt.getChildNodes();
				for (int i = 0; i < childList.getLength(); i++) {
					Node elt = childList.item(i);
					String eltName = elt.getNodeName();
					Element elmt;
					switch (eltName) {
					case PiIdentifiers.REFINEMENT_LOOP:
						elmt = (Element) elt;
						hrefinement
								.setLoopPrototype(parseFunctionPrototype(
										elmt,
										elmt.getAttribute(PiIdentifiers.REFINEMENT_FUNCTION_PROTOTYPE_NAME)));
						break;
					case PiIdentifiers.REFINEMENT_INIT:
						elmt = (Element) elt;
						hrefinement
								.setInitPrototype(parseFunctionPrototype(
										elmt,
										elmt.getAttribute(PiIdentifiers.REFINEMENT_FUNCTION_PROTOTYPE_NAME)));
						break;
					default:
						// ignore #text and other children
					}
				}
				actor.setRefinement(hrefinement);
			}

			actor.getRefinement().setFilePath(path);
		}
	}

	private FunctionPrototype parseFunctionPrototype(Element protoElt,
			String protoName) {
		FunctionPrototype proto = PiMMFactory.eINSTANCE
				.createFunctionPrototype();

		proto.setName(protoName);
		NodeList childList = protoElt.getChildNodes();
		for (int i = 0; i < childList.getLength(); i++) {
			Node elt = childList.item(i);
			String eltName = elt.getNodeName();
			switch (eltName) {
			case PiIdentifiers.REFINEMENT_PARAMETER:
				proto.getParameters()
						.add(parseFunctionParameter((Element) elt));
				break;
			default:
				// ignore #text and other children
			}
		}
		return proto;
	}

	private FunctionParameter parseFunctionParameter(Element elt) {
		FunctionParameter param = PiMMFactory.eINSTANCE
				.createFunctionParameter();

		param.setName(elt
				.getAttribute(PiIdentifiers.REFINEMENT_PARAMETER_NAME));
		param.setType(elt
				.getAttribute(PiIdentifiers.REFINEMENT_PARAMETER_TYPE));
		param.setDirection(PiMMFactory.eINSTANCE.createDirection(elt
				.getAttribute(PiIdentifiers.REFINEMENT_PARAMETER_DIRECTION)));
		param.setIsConfigurationParameter(Boolean.valueOf(elt
				.getAttribute(PiIdentifiers.REFINEMENT_PARAMETER_IS_CONFIG)));

		return param;
	}

	/**
	 * Parse a ConfigInputInterface (i.e. a {@link Parameter}) of the Pi File.
	 * 
	 * @param nodeElt
	 *            The node {@link Element} holding the {@link Parameter}
	 *            properties.
	 * @param graph
	 *            the deserialized {@link PiGraph}.
	 * @return the {@link AbstractVertex} of the {@link Parameter}.
	 */
	protected AbstractVertex parseConfigInputInterface(Element nodeElt,
			PiGraph graph) {
		// Instantiate the new Config Input Interface
		Parameter param = PiMMFactory.eINSTANCE.createParameter();
		param.setConfigurationInterface(true);
		// param.setLocallyStatic(true);

		// Get the actor properties
		param.setName(nodeElt.getAttribute(PiIdentifiers.PARAMETER_NAME));

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
	 *            the deserialized {@link PiGraph}
	 */
	protected void parseDependencies(Element edgeElt, PiGraph graph) {
		// Instantiate the new Dependency
		Dependency dependency = PiMMFactory.eINSTANCE.createDependency();

		// Find the source and target of the fifo
		String setterName = edgeElt
				.getAttribute(PiIdentifiers.DEPENDENCY_SOURCE);
		String getterName = edgeElt
				.getAttribute(PiIdentifiers.DEPENDENCY_TARGET);
		AbstractVertex source = graph.getVertexNamed(setterName);
		Parameterizable target = graph.getVertexNamed(getterName);
		if (source == null) {
			throw new RuntimeException("Dependency source vertex " + setterName
					+ " does not exist.");
		}
		if (target == null) {
			// The target can also be a Delay associated to a Fifo
			Fifo targetFifo = graph.getFifoIded(getterName);

			if (targetFifo == null) {
				throw new RuntimeException("Dependency target " + getterName
						+ " does not exist.");
			}

			if (targetFifo.getDelay() == null) {
				throw new RuntimeException("Dependency fifo target "
						+ getterName
						+ " has no delay to receive the dependency.");
			} else {
				target = targetFifo.getDelay();
			}
		}

		// Get the sourcePort and targetPort
		if (source instanceof ExecutableActor) {

			String sourcePortName = edgeElt
					.getAttribute(PiIdentifiers.DEPENDENCY_SOURCE_PORT);
			sourcePortName = (sourcePortName == "") ? null : sourcePortName;
			ConfigOutputPort oPort = (ConfigOutputPort) ((ExecutableActor) source)
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

		if (target instanceof ExecutableActor) {
			String targetPortName = edgeElt
					.getAttribute(PiIdentifiers.DEPENDENCY_TARGET_PORT);
			targetPortName = (targetPortName == "") ? null : targetPortName;
			ConfigInputPort iPort = (ConfigInputPort) ((AbstractVertex) target)
					.getPortNamed(targetPortName);
			if (iPort == null) {
				throw new RuntimeException("Dependency target port "
						+ targetPortName + " does not exist for vertex "
						+ getterName);
			}
			dependency.setGetter(iPort);
		}

		if (target instanceof Parameter || target instanceof InterfaceActor
				|| target instanceof Delay) {
			ConfigInputPort iCfgPort = PiMMFactory.eINSTANCE
					.createConfigInputPort();
			target.getConfigInputPorts().add(iCfgPort);
			dependency.setGetter(iCfgPort);
		}

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
	protected void parseEdge(Element edgeElt, PiGraph graph) {
		// Identify if the node is an actor or a parameter
		String edgeKind = edgeElt.getAttribute(PiIdentifiers.EDGE_KIND);

		switch (edgeKind) {
		case PiIdentifiers.FIFO:
			parseFifo(edgeElt, graph);
			break;
		case PiIdentifiers.DEPENDENCY:
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
	 *            the deserialized {@link PiGraph}
	 */
	protected void parseFifo(Element edgeElt, PiGraph graph) {
		// Instantiate the new Fifo
		Fifo fifo = PiMMFactory.eINSTANCE.createFifo();

		// Find the source and target of the fifo
		String sourceName = edgeElt.getAttribute(PiIdentifiers.FIFO_SOURCE);
		String targetName = edgeElt.getAttribute(PiIdentifiers.FIFO_TARGET);
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
		// Get the type
		String type = edgeElt.getAttribute(PiIdentifiers.FIFO_TYPE);
		// If none is find, add the default type
		if (type == null || type.equals(""))
			type = "void";
		fifo.setType(type);
		// Get the sourcePort and targetPort
		String sourcePortName = edgeElt
				.getAttribute(PiIdentifiers.FIFO_SOURCE_PORT);
		sourcePortName = (sourcePortName == "") ? null : sourcePortName;
		String targetPortName = edgeElt
				.getAttribute(PiIdentifiers.FIFO_TARGET_PORT);
		targetPortName = (targetPortName == "") ? null : targetPortName;
		DataOutputPort oPort = (DataOutputPort) source
				.getPortNamed(sourcePortName);
		DataInputPort iPort = (DataInputPort) target
				.getPortNamed(targetPortName);

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

		// Check if the fifo has a delay
		if (getProperty(edgeElt, PiIdentifiers.DELAY) != null) {
			// TODO replace with a parse Delay if delay have their own element
			// in the future
			Delay delay = PiMMFactory.eINSTANCE.createDelay();
			delay.getExpression().setString(
					edgeElt.getAttribute(PiIdentifiers.DELAY_EXPRESSION));
			fifo.setDelay(delay);
		}

		// Add the new Fifo to the graph
		graph.getFifos().add(fifo);
	}

	/**
	 * Retrieve and parse the graph element of the Pi description
	 * 
	 * @param rootElt
	 *            The root element (that must have a graph child)
	 * @param graph
	 *            The deserialized {@link PiGraph}
	 */
	protected void parseGraph(Element rootElt, PiGraph graph) {
		// Retrieve the Graph Element
		NodeList graphElts = rootElt
				.getElementsByTagName(PiIdentifiers.GRAPH);
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
			case PiIdentifiers.DATA:
				// Properties of the Graph.
				// TODO transfer this code in a separate function
				// parseGraphProperties()
				String keyName = elt.getAttributes()
						.getNamedItem(PiIdentifiers.DATA_KEY).getNodeValue();
				String keyValue = elt.getTextContent();
				if (keyName.equals(PiIdentifiers.GRAPH_NAME)) {
					graph.setName(keyValue);
				}
				break;
			case PiIdentifiers.NODE:
				// Node elements
				parseNode((Element) elt, graph);
				break;
			case PiIdentifiers.EDGE:
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
	 *            The deserialized {@link PiGraph}
	 */
	protected void parseNode(Element nodeElt, PiGraph graph) {
		// Identify if the node is an actor or a parameter
		String nodeKind = nodeElt.getAttribute(PiIdentifiers.NODE_KIND);
		AbstractVertex vertex;

		switch (nodeKind) {
		case PiIdentifiers.ACTOR:
			vertex = parseActor(nodeElt, graph);
			break;
		case PiIdentifiers.BROADCAST:
		case PiIdentifiers.FORK:
		case PiIdentifiers.JOIN:
		case PiIdentifiers.ROUND_BUFFER:
			vertex = parseSpecialActor(nodeElt, graph);
			break;
		case PiIdentifiers.DATA_INPUT_INTERFACE:
			vertex = parseSourceInterface(nodeElt, graph);
			break;
		case PiIdentifiers.DATA_OUTPUT_INTERFACE:
			vertex = parseSinkInterface(nodeElt, graph);
			break;
		case PiIdentifiers.PARAMETER:
			vertex = parseParameter(nodeElt, graph);
			break;
		case PiIdentifiers.CONFIGURATION_INPUT_INTERFACE:
			vertex = parseConfigInputInterface(nodeElt, graph);
			break;
		case PiIdentifiers.CONFIGURATION_OUTPUT_INTERFACE:
			vertex = parseConfigOutputInterface(nodeElt, graph);
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
			case PiIdentifiers.PORT:
				parsePort((Element) elt, vertex);
				break;
			default:
				// ignore #text and unknown children
			}
		}
	}

	/**
	 * Parse a {@link Parameter} of the Pi File.
	 * 
	 * @param nodeElt
	 *            The node {@link Element} holding the {@link Parameter}
	 *            properties.
	 * @param graph
	 *            the deserialized {@link PiGraph}.
	 * @return the {@link AbstractVertex} of the {@link Parameter}.
	 */
	protected AbstractVertex parseParameter(Element nodeElt, PiGraph graph) {
		// Instantiate the new Parameter
		Parameter param = PiMMFactory.eINSTANCE.createParameter();
		param.getExpression().setString(
				nodeElt.getAttribute(PiIdentifiers.PARAMETER_EXPRESSION));
		param.setConfigurationInterface(false);
		// param.setLocallyStatic(true);
		param.setGraphPort(null); // No port of the graph corresponds to this
									// parameter

		// Get the actor properties
		param.setName(nodeElt.getAttribute(PiIdentifiers.PARAMETER_NAME));

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
	 *            The deserialized {@link PiGraph}
	 */
	protected void parsePi(Element rootElt, PiGraph graph) {
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
		String portName = elt.getAttribute(PiIdentifiers.PORT_NAME);
		String portKind = elt.getAttribute(PiIdentifiers.PORT_KIND);

		switch (portKind) {
		case PiIdentifiers.DATA_INPUT_PORT:
			// Throw an error if the parsed vertex is not an actor
			if (!(vertex instanceof AbstractActor)) {
				throw new RuntimeException("Parsed data port " + portName
						+ " cannot belong to the non-actor vertex "
						+ vertex.getName());
			}

			DataInputPort iPort;

			// Do not create data ports for InterfaceActor since the unique port
			// is automatically created when the vertex is instantiated
			if (!(vertex instanceof InterfaceActor)) {
				iPort = PiMMFactory.eINSTANCE.createDataInputPort();
				((AbstractActor) vertex).getDataInputPorts().add(iPort);
				iPort.setName(portName);
			} else {
				iPort = ((AbstractActor) vertex).getDataInputPorts().get(0);
			}
			iPort.getExpression().setString(
					elt.getAttribute(PiIdentifiers.PORT_EXPRESSION));
			iPort.setAnnotation(PortMemoryAnnotation.get(elt
					.getAttribute(PiIdentifiers.PORT_MEMORY_ANNOTATION)));
			break;
		case PiIdentifiers.DATA_OUTPUT_PORT:
			// Throw an error if the parsed vertex is not an actor
			if (!(vertex instanceof AbstractActor)) {
				throw new RuntimeException("Parsed data port " + portName
						+ " cannot belong to the non-actor vertex "
						+ vertex.getName());
			}

			DataOutputPort oPort;

			// Do not create data ports for InterfaceActor since the unique port
			// is automatically created when the vertex is instantiated
			if (!(vertex instanceof InterfaceActor)) {
				oPort = PiMMFactory.eINSTANCE.createDataOutputPort();
				((AbstractActor) vertex).getDataOutputPorts().add(oPort);
				oPort.setName(portName);
			} else {
				oPort = ((AbstractActor) vertex).getDataOutputPorts().get(0);
			}
			oPort.getExpression().setString(
					elt.getAttribute(PiIdentifiers.PORT_EXPRESSION));
			oPort.setAnnotation(PortMemoryAnnotation.get(elt
					.getAttribute(PiIdentifiers.PORT_MEMORY_ANNOTATION)));
			break;
		case PiIdentifiers.CONFIGURATION_INPUT_PORT:
			ConfigInputPort iCfgPort = PiMMFactory.eINSTANCE
					.createConfigInputPort();
			iCfgPort.setName(portName);
			vertex.getConfigInputPorts().add(iCfgPort);
			break;

		case PiIdentifiers.CONFIGURATION_OUPUT_PORT:
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
	 * Parse a node {@link Element} with kind "cfg_out_iface".
	 * 
	 * @param nodeElt
	 *            the {@link Element} to parse
	 * @param graph
	 *            the deserialized {@link PiGraph}
	 * @return the created {@link ConfigOutputInterface}
	 */
	protected AbstractActor parseConfigOutputInterface(Element nodeElt,
			PiGraph graph) {
		// Instantiate the new Interface and its corresponding port
		ConfigOutputInterface cfgOutIf = PiMMFactory.eINSTANCE
				.createConfigOutputInterface();

		// Set the Interface properties
		cfgOutIf.setName(nodeElt
				.getAttribute(PiIdentifiers.CONFIGURATION_OUTPUT_INTERFACE_NAME));

		// Add the actor to the parsed graph
		graph.getVertices().add(cfgOutIf);

		return cfgOutIf;
	}

	/**
	 * Parse a node {@link Element} with kind "snk".
	 * 
	 * @param nodeElt
	 *            the {@link Element} to parse
	 * @param graph
	 *            the deserialized {@link PiGraph}
	 * @return the created {@link DataOutputInterface}
	 */
	protected AbstractActor parseSinkInterface(Element nodeElt, PiGraph graph) {
		// Instantiate the new Interface and its corresponding port
		DataOutputInterface snkInterface = PiMMFactory.eINSTANCE
				.createDataOutputInterface();

		// Set the sourceInterface properties
		snkInterface.setName(nodeElt
				.getAttribute(PiIdentifiers.DATA_OUTPUT_INTERFACE_NAME));

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
	 *            the deserialized {@link PiGraph}
	 * @return the created {@link DataInputInterface}
	 */
	protected AbstractActor parseSourceInterface(Element nodeElt, PiGraph graph) {
		// Instantiate the new Interface and its corresponding port
		DataInputInterface srcInterface = PiMMFactory.eINSTANCE
				.createDataInputInterface();

		// Set the sourceInterface properties
		srcInterface.setName(nodeElt
				.getAttribute(PiIdentifiers.DATA_INPUT_INTERFACE_NAME));

		// Add the actor to the parsed graph
		graph.getVertices().add(srcInterface);

		return srcInterface;
	}

	/**
	 * Parse a node {@link Element} with kind "broadcast", "fork", "join",
	 * "roundbuffer".
	 * 
	 * @param nodeElt
	 *            the {@link Element} to parse
	 * @param graph
	 *            the deserialized {@link PiGraph}
	 * @return the created actor
	 */
	protected AbstractActor parseSpecialActor(Element nodeElt, PiGraph graph) {
		// Identify if the node is an actor or a parameter
		String nodeKind = nodeElt.getAttribute(PiIdentifiers.NODE_KIND);
		AbstractActor actor = null;

		// Instantiate the actor.
		switch (nodeKind) {
		case PiIdentifiers.BROADCAST:
			actor = PiMMFactory.eINSTANCE.createBroadcastActor();
			break;
		case PiIdentifiers.FORK:
			actor = PiMMFactory.eINSTANCE.createForkActor();
			break;
		case PiIdentifiers.JOIN:
			actor = PiMMFactory.eINSTANCE.createJoinActor();
			break;
		case PiIdentifiers.ROUND_BUFFER:
			actor = PiMMFactory.eINSTANCE.createRoundBufferActor();
			break;
		}

		// Get the actor properties
		actor.setName(nodeElt.getAttribute(PiIdentifiers.ACTOR_NAME));

		// Add the actor to the parsed graph
		graph.getVertices().add(actor);

		return actor;
	}

	/**
	 * Transform a project relative path to workspace relative path
	 * 
	 * @param path
	 *            the IPath to transform
	 * @return the path to the file inside the project containing the parsed
	 *         file if this file exists, path otherwise
	 */
	private IPath getWorkspaceRelativePathFrom(IPath path) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		// If the file pointed by path does not exist, we try to add the
		// name of the project containing the file we parse to it
		if (!root.getFile(path).exists()) {
			// Get the project
			String platformString = documentURI.toPlatformString(true);
			IFile documentFile = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(new Path(platformString));
			IProject documentProject = documentFile.getProject();
			// Create a new path using the project name
			IPath newPath = new Path(documentProject.getName()).append(path);
			// Check there is a file where newPath points, if yes, use it
			// instead of path
			if (root.getFile(newPath).exists())
				return newPath;
		}
		return path;
	}
}
