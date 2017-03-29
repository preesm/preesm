/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2014)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2013 - 2014)
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.ietr.dftools.algorithm.exporter.Key;
import org.ietr.dftools.architecture.utils.DomUtil;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.BroadcastActor;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.HRefinement;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;
import org.ietr.preesm.experiment.model.pimm.util.PiIdentifiers;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Writer for the PiMM Model in the Pi format
 * 
 * @author kdesnos
 * @author jheulot
 * 
 */
public class PiWriter {

	/**
	 * The document created by this writer
	 */
	protected Document domDocument;

	// URI where the document will be saved
	private URI documentURI;

	/**
	 * This HashMap associates a List to each <b>element</b> (graph, node, edge,
	 * port) of a Pi description. For each <b>element</b>, a list of {@link Key}
	 * s is associated. A {@link Key} can be seen as an attribute of this
	 * element.
	 */
	protected HashMap<String, List<Key>> elementKeys;

	/**
	 * Graph {@link Element} of the DOM {@link Document} of this Writer
	 */
	protected Element graphElement;

	/**
	 * Root {@link Element} of the DOM {@link Document} of this Writer
	 */
	protected Element rootElement;

	/**
	 * Default constructor of the {@link PiWriter}
	 * 
	 * @param uri
	 */
	public PiWriter(URI uri) {
		// Instantiate an empty elementKeys Map
		elementKeys = new HashMap<>();

		// Initialize attributes to null
		rootElement = null;
		graphElement = null;
		documentURI = uri;
	}

	/**
	 * Creates and appends the Graph Element of the document
	 * 
	 * @param parentElement
	 *            The parent element of the graph
	 * @return The created element
	 */
	protected Element addGraphElt(Element parentElement) {
		Element newElt = appendChild(parentElement, PiIdentifiers.GRAPH);
		graphElement = newElt;
		newElt.setAttribute(PiIdentifiers.GRAPH_EDGE_DEFAULT,
				PiIdentifiers.GRAPH_DIRECTED);

		return newElt;
	}

	/**
	 * Adds a Key to the elementKeys map with the specified informations. Also
	 * insert the key at the document root.
	 * 
	 * @param id
	 *            Id of the key (identical to name)
	 * @param name
	 *            The Name of the key
	 * @param elt
	 *            The Class this key applies to
	 * @param type
	 *            The value type of this key (can be null)
	 * @param desc
	 *            This key description (can be null)
	 */
	protected void addKey(String id, String name, String elt, String type,
			Class<?> desc) {
		// Create the new Key
		Key key = new Key(name, elt, type, desc);
		key.setId(id);

		// Create the corresponding keyElement (if it does not exists)
		Element keyElt = createKeyElt(key);

		// If the new keyElement was created
		if (keyElt != null) {
			// Put the Key in the list of its element
			elementKeys.get(key.getApplyTo()).add(key);

			// Add the KeyElement to the document
			// also works if graphElt does not exist yet
			rootElement.insertBefore(keyElt, graphElement);
		}

	}

	/**
	 * Creates a new child for the given parent Element with the name "name"
	 * 
	 * @param parentElement
	 *            The element to add a child
	 * @param name
	 *            The name of this Element
	 * @return The created Element
	 * 
	 * @author Jonathan Piat
	 */
	protected Element appendChild(Node parentElement, String name) {
		Element newElt = domDocument.createElement(name);
		parentElement.appendChild(newElt);
		return newElt;
	}

	/**
	 * Check if the key already exists
	 * 
	 * @param id
	 *            Id of the key (identical to name or null)
	 * @param name
	 *            The Name of the key
	 * @param elt
	 *            The Class this key applies to (can be null)
	 * @param type
	 *            The value type of this key (can be null)
	 * @param desc
	 *            This key description (can be null)
	 * @return
	 */
	protected Element createKeyElt(Key key) {
		// Check if the element already has a Key list
		if (elementKeys.get(key.getApplyTo()) == null) {
			// If not, create the Key list for this element and add it to
			// elementKeys
			ArrayList<Key> keys = new ArrayList<Key>();
			elementKeys.put(key.getApplyTo(), keys);
		} else {
			// If the element already exists
			// Check if this key is already in its Key list
			if (elementKeys.get(key.getApplyTo()).contains(key)) {
				// The element already exists, no need to create a new one
				return null;
			}
		}

		// If this code is reached, the new element must be created
		// Create the element
		Element newElt = domDocument.createElement("key");
		newElt.setAttribute("attr.name", key.getName());
		if (key.getApplyTo() != null) {
			newElt.setAttribute("for", key.getApplyTo());
		}
		if (key.getType() != null) {
			newElt.setAttribute("attr.type", key.getType());
		}
		if (key.getId() != null) {
			newElt.setAttribute("id", key.getId());
		}
		if (key.getTypeClass() != null) {
			Element descElt = appendChild(newElt, "desc");
			descElt.setTextContent(key.getTypeClass().getName());
		}

		return newElt;
	}

	/**
	 * Write the PiMM {@link PiGraph} to the given {@link OutputStream} using
	 * the Pi format.
	 * 
	 * @param graph
	 *            The Graph to write
	 * @param outputStream
	 *            The written OutputStream
	 */
	public void write(PiGraph graph, OutputStream outputStream) {
		// Create the domDocument
		domDocument = DomUtil.createDocument(
				"http://graphml.graphdrawing.org/xmlns", "graphml");

		// Retrieve the root element of the document
		rootElement = domDocument.getDocumentElement();

		// Fill the root Element with the Graph
		writePi(rootElement, graph);

		// Produce the output file
		DomUtil.writeDocument(outputStream, domDocument);

		// Close the output stream
		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create and add a node {@link Element} to the given parent {@link Element}
	 * for the given {@link AbstractActor} and write its informations.
	 * 
	 * @param graphElt
	 *            The parent element of the node element (i.e. the graph of the
	 *            document)
	 * @param abstractActor
	 *            The {@link AbstractActor} to write in the {@link Document}
	 */
	protected void writeAbstractActor(Element graphElt,
			AbstractActor abstractActor) {
		// Add the node to the document
		Element vertexElt = appendChild(graphElt, PiIdentifiers.NODE);

		// Set the unique ID of the node (equal to the vertex name)
		vertexElt.setAttribute(PiIdentifiers.ACTOR_NAME,
				abstractActor.getName());

		// Add the name in the data of the node
		// writeDataElt(vertexElt, "name", vertex.getName());

		if (abstractActor instanceof Actor) {
			writeActor(vertexElt, (Actor) abstractActor);
		} else if (abstractActor instanceof ExecutableActor) {
			writeSpecialActor(vertexElt, abstractActor);
		} else if (abstractActor instanceof InterfaceActor) {
			writeInterfaceVertex(vertexElt, (InterfaceActor) abstractActor);
		}

		// TODO addProperties() of the vertex
	}

	/**
	 * Write information of the {@link Actor} in the given {@link Element}.
	 * 
	 * @param vertexElt
	 *            The {@link Element} to write
	 * @param actor
	 *            The {@link Actor} to serialize
	 */
	protected void writeActor(Element vertexElt, Actor actor) {
		// TODO change this method when several kinds will exist
		// Set the kind of the Actor
		vertexElt.setAttribute(PiIdentifiers.NODE_KIND,
				PiIdentifiers.ACTOR);
		Refinement refinement = actor.getRefinement();
		if (refinement != null) writeRefinement(vertexElt, refinement);
		IPath memoryScriptPath = actor.getMemoryScriptPath();
		if (memoryScriptPath != null) writeMemoryScript(vertexElt,
				getProjectRelativePathFrom(memoryScriptPath));
		// writeDataElt(vertexElt, "kind", "actor");
		// Write ports of the actor
		writePorts(vertexElt, actor.getConfigInputPorts());
		writePorts(vertexElt, actor.getConfigOutputPorts());
		writePorts(vertexElt, actor.getDataInputPorts());
		writePorts(vertexElt, actor.getDataOutputPorts());

	}

	/**
	 * Add a data child {@link Element} to the parent {@link Element} whit the
	 * given key name and the given content. If the {@link Key} does not exist
	 * yet, it will be created automatically.
	 * 
	 * @param parentElt
	 *            The element to which the data is added
	 * @param keyName
	 *            The name of the key for the added data
	 * @param textContent
	 *            The text content of the data element
	 */
	protected void writeDataElt(Element parentElt, String keyName,
			String textContent) {
		addKey(null, keyName, parentElt.getTagName(), "string", null);
		Element nameElt = appendChild(parentElt, PiIdentifiers.DATA);
		nameElt.setAttribute(PiIdentifiers.DATA_KEY, keyName);
		nameElt.setTextContent(textContent);
	}

	/**
	 * Write the {@link Delay} information in the given {@link Element}.
	 * 
	 * @param fifoElt
	 *            the {@link Element} to write
	 * @param delay
	 *            the {@link Delay} to serialize
	 */
	protected void writeDelay(Element fifoElt, Delay delay) {
		writeDataElt(fifoElt, PiIdentifiers.DELAY, null);
		fifoElt.setAttribute(PiIdentifiers.DELAY_EXPRESSION, delay
				.getExpression().getString());
		// TODO when delay class will be updated, modify the writer/parser.
		// Maybe a specific element will be needed to store the Expression
		// associated to a delay as well as it .h file storing the default value
		// of tokens.
	}

	/**
	 * Create and add a node {@link Element} to the given parent {@link Element}
	 * for the given {@link Dependency} and write its informations.
	 * 
	 * @param graphElt
	 *            The parent element of the node element (i.e. the graph of the
	 *            document)
	 * @param dependency
	 *            The {@link Dependency} to write in the {@link Document}
	 */
	protected void writeDependency(Element graphElt, Dependency dependency) {
		// Add the node to the document
		Element dependencyElt = appendChild(graphElt, PiIdentifiers.EDGE);
		dependencyElt.setAttribute(PiIdentifiers.EDGE_KIND,
				PiIdentifiers.DEPENDENCY);

		// Set the source and target attributes
		ISetter setter = dependency.getSetter();
		AbstractVertex source = null;
		if (setter instanceof ConfigOutputPort) {
			source = (AbstractVertex) setter.eContainer();
		}

		if (setter instanceof Parameter) {
			source = (AbstractVertex) setter;
		}

		if (source == null) {
			throw new RuntimeException(
					"Setter of the dependency has a type not supported by the writer: "
							+ setter.getClass());
		}
		dependencyElt.setAttribute(PiIdentifiers.DEPENDENCY_SOURCE,
				source.getName());
		if (setter instanceof ConfigOutputPort) {
			dependencyElt.setAttribute(PiIdentifiers.DEPENDENCY_SOURCE_PORT,
					((Port) setter).getName());
		}

		Parameterizable target = (Parameterizable) dependency.getGetter()
				.eContainer();
		if (target instanceof AbstractVertex) {

			dependencyElt.setAttribute(PiIdentifiers.DEPENDENCY_TARGET,
					((AbstractVertex) target).getName());

			if (target instanceof ExecutableActor) {
				dependencyElt.setAttribute(
						PiIdentifiers.DEPENDENCY_TARGET_PORT, dependency
								.getGetter().getName());
			}
		}

		if (target instanceof Delay) {
			dependencyElt.setAttribute(PiIdentifiers.DEPENDENCY_TARGET,
					((Fifo) target.eContainer()).getId());
		}
	}

	/**
	 * Create and add a node {@link Element} to the given parent {@link Element}
	 * for the given fifo and write its informations.
	 * 
	 * @param graphElt
	 *            The parent element of the node element (i.e. the graph of the
	 *            document)
	 * @param fifo
	 *            The {@link Fifo} to write in the {@link Document}
	 */
	protected void writeFifos(Element graphElt, Fifo fifo) {
		// Add the node to the document
		Element fifoElt = appendChild(graphElt, PiIdentifiers.EDGE);

		// Set the source and target attributes
		AbstractActor source = (AbstractActor) fifo.getSourcePort()
				.eContainer();
		AbstractActor target = (AbstractActor) fifo.getTargetPort()
				.eContainer();
		fifoElt.setAttribute(PiIdentifiers.EDGE_KIND, PiIdentifiers.FIFO);
		fifoElt.setAttribute(PiIdentifiers.FIFO_TYPE, fifo.getType());
		fifoElt.setAttribute(PiIdentifiers.FIFO_SOURCE, source.getName());
		fifoElt.setAttribute(PiIdentifiers.FIFO_TARGET, target.getName());
		fifoElt.setAttribute(PiIdentifiers.FIFO_SOURCE_PORT, fifo
				.getSourcePort().getName());
		fifoElt.setAttribute(PiIdentifiers.FIFO_TARGET_PORT, fifo
				.getTargetPort().getName());

		if (fifo.getDelay() != null) {
			writeDelay(fifoElt, fifo.getDelay());
		}
		// TODO other Fifo properties (if any)
	}

	/**
	 * Create the Graph Element of the document and fill it
	 * 
	 * @param rootElt
	 *            The parent element of the Graph element (i.e. the root of the
	 *            document)
	 * @param graph
	 *            The serialized Graph
	 */
	protected void writeGraph(Element rootElt, PiGraph graph) {
		// Create and add the graphElt to the Document
		Element graphElt = addGraphElt(rootElt);
		writeDataElt(graphElt, PiIdentifiers.GRAPH_NAME, graph.getName());

		// TODO addProperties() of the graph
		// TODO writeParameters()
		for (Parameter param : graph.getParameters()) {
			writeParameter(graphElt, param);
		}

		// Write the vertices of the graph
		for (AbstractActor actor : graph.getVertices()) {
			writeAbstractActor(graphElt, actor);
		}

		for (Fifo fifo : graph.getFifos()) {
			writeFifos(graphElt, fifo);
		}

		for (Dependency dependency : graph.getDependencies()) {
			writeDependency(graphElt, dependency);
		}
	}

	/**
	 * Write information of the {@link InterfaceActor} in the given
	 * {@link Element}.
	 * 
	 * @param vertexElt
	 *            The {@link Element} to write
	 * @param vertex
	 *            The {@link InterfaceActor} to serialize
	 */
	protected void writeInterfaceVertex(Element vertexElt, InterfaceActor vertex) {
		// Set the kind of the Actor
		vertexElt.setAttribute(PiIdentifiers.NODE_KIND, vertex.getKind());
		// writeDataElt(vertexElt, "kind", "actor");
		// Write ports of the actor
		switch (vertex.getKind()) {
		case PiIdentifiers.DATA_INPUT_INTERFACE:
			writePorts(vertexElt, vertex.getDataOutputPorts());
			break;
		case PiIdentifiers.DATA_OUTPUT_INTERFACE:
			writePorts(vertexElt, vertex.getDataInputPorts());
			break;
		default:
		}

	}

	/**
	 * Write information of the memory script in the given {@link Element}.
	 * 
	 * @param vertexElt
	 *            The {@link Element} to write
	 * @param memScriptPath
	 *            The memory script path to serialize
	 */
	protected void writeMemoryScript(Element vertexElt, IPath memScriptPath) {
		if (memScriptPath != null) {
			// The makeRelative() call ensures that the path is relative to the
			// project.
			writeDataElt(vertexElt, PiIdentifiers.ACTOR_MEMORY_SCRIPT,
					memScriptPath.makeRelative().toPortableString());
		}
	}

	/**
	 * Create and add a node {@link Element} to the given parent {@link Element}
	 * for the given parameter and write its informations.
	 * 
	 * @param graphElt
	 *            The parent element of the node element (i.e. the graph of the
	 *            document)
	 * @param param
	 *            The {@link Parameter} to write in the {@link Document}
	 */
	protected void writeParameter(Element graphElt, Parameter param) {
		// Add the node to the document
		Element paramElt = appendChild(graphElt, PiIdentifiers.NODE);

		// Set the unique ID of the node (equal to the param name)
		paramElt.setAttribute(PiIdentifiers.PARAMETER_NAME, param.getName());

		// Set the kind of the node
		if (!param.isConfigurationInterface()) {
			paramElt.setAttribute(PiIdentifiers.NODE_KIND,
					PiIdentifiers.PARAMETER);
			paramElt.setAttribute(PiIdentifiers.PARAMETER_EXPRESSION, param
					.getExpression().getString());
		} else {
			paramElt.setAttribute(PiIdentifiers.NODE_KIND,
					PiIdentifiers.CONFIGURATION_INPUT_INTERFACE);
		}
	}

	/**
	 * Fill the {@link Element} with a description of the input {@link PiGraph}
	 * 
	 * @param parentElt
	 *            The Element to fill (could be removed later if it is always
	 *            rootElt)
	 * @param graph
	 *            The serialized Graph
	 */
	protected void writePi(Element parentElt, PiGraph graph) {
		// Add IBSDF Keys - Might not be needed.
		addKey("parameters", "parameters", "graph", null, null);
		addKey("variables", "variables", "graph", null, null);
		addKey("arguments", "arguments", "node", null, null);

		// Write the Graph
		writeGraph(parentElt, graph);
	}

	/**
	 * Write the {@link Port} in the given {@link Element}
	 * 
	 * @param vertexElt
	 *            the {@link Element} to write
	 * @param actor
	 *            the {@link Actor} to serialize
	 */
	protected void writePorts(Element vertexElt, EList<?> ports) {
		for (Object portObj : ports) {
			Port port = (Port) portObj;
			Element portElt = appendChild(vertexElt, PiIdentifiers.PORT);

			String name = port.getName();
			if (name == null || name.isEmpty()) {
				EObject container = port.eContainer();
				if (container instanceof AbstractVertex)
					name = ((AbstractVertex) container).getName();
			}

			portElt.setAttribute(PiIdentifiers.PORT_NAME, port.getName());
			portElt.setAttribute(PiIdentifiers.PORT_KIND, port.getKind());

			switch (port.getKind()) {
			case PiIdentifiers.DATA_INPUT_PORT:
				portElt.setAttribute(PiIdentifiers.PORT_EXPRESSION,
						((DataInputPort) port).getExpression().getString());
				break;
			case PiIdentifiers.DATA_OUTPUT_PORT:
				portElt.setAttribute(PiIdentifiers.PORT_EXPRESSION,
						((DataOutputPort) port).getExpression().getString());
				break;
			case PiIdentifiers.CONFIGURATION_INPUT_PORT:
				break;
			case PiIdentifiers.CONFIGURATION_OUPUT_PORT:
				break;
			}
			if (port instanceof DataPort) {
				DataPort dataPort = (DataPort) port;
				if (dataPort.getAnnotation() != null)
					portElt.setAttribute(
							PiIdentifiers.PORT_MEMORY_ANNOTATION, dataPort
									.getAnnotation().toString());
			}
		}
	}

	/**
	 * Write information of the {@link Refinement} in the given {@link Element}.
	 * 
	 * @param vertexElt
	 *            The {@link Element} to write
	 * @param refinement
	 *            The {@link Refinement} to serialize
	 */
	protected void writeRefinement(Element vertexElt, Refinement refinement) {
		if (refinement.getFilePath() != null) {

			IPath refinementPath = getProjectRelativePathFrom(refinement
					.getFilePath());

			// The makeRelative() call ensures that the path is relative to the
			// project.
			writeDataElt(vertexElt, PiIdentifiers.REFINEMENT, refinementPath
					.makeRelative().toPortableString());
			if (refinement instanceof HRefinement) {
				HRefinement hrefinement = (HRefinement) refinement;
				writeFunctionPrototype(vertexElt,
						hrefinement.getLoopPrototype(),
						PiIdentifiers.REFINEMENT_LOOP);
				if (hrefinement.getInitPrototype() != null)
					writeFunctionPrototype(vertexElt,
							hrefinement.getInitPrototype(),
							PiIdentifiers.REFINEMENT_INIT);
			}
		}
	}

	/**
	 * Returns an IPath without the project name (project relative IPath) if the
	 * file pointed by path is contained by the same project as the file we
	 * write
	 * 
	 * @param path
	 *            the IPath to make project relative
	 * @return a project relative IPath if possible, path otherwise
	 */
	private IPath getProjectRelativePathFrom(IPath path) {
		// If the refinement file is contained in the same project than the .pi
		// we are serializing, then save a project relative path
		// Get the project
		String platformString = documentURI.toPlatformString(true);
		IFile documentFile = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(new Path(platformString));
		IProject documentProject = documentFile.getProject();
		// If the project name is the first segment of the refinement file path,
		// then remove it
		if (path.segment(0).equals(documentProject.getName()))
			return path.removeFirstSegments(1);
		return path;
	}

	private void writeFunctionPrototype(Element vertexElt,
			FunctionPrototype prototype, String functionName) {
		Element protoElt = appendChild(vertexElt, functionName);
		protoElt.setAttribute(
				PiIdentifiers.REFINEMENT_FUNCTION_PROTOTYPE_NAME,
				prototype.getName());
		for (FunctionParameter p : prototype.getParameters()) {
			writeFunctionParameter(protoElt, p);
		}
	}

	private void writeFunctionParameter(Element prototypeElt,
			FunctionParameter p) {
		Element protoElt = appendChild(prototypeElt,
				PiIdentifiers.REFINEMENT_PARAMETER);
		protoElt.setAttribute(PiIdentifiers.REFINEMENT_PARAMETER_NAME,
				p.getName());
		protoElt.setAttribute(PiIdentifiers.REFINEMENT_PARAMETER_TYPE,
				p.getType());
		protoElt.setAttribute(PiIdentifiers.REFINEMENT_PARAMETER_DIRECTION,
				p.getDirection().toString());
		protoElt.setAttribute(PiIdentifiers.REFINEMENT_PARAMETER_IS_CONFIG,
				String.valueOf(p.isIsConfigurationParameter()));
	}

	/**
	 * Write information of the {@link Actor} in the given {@link Element}. The
	 * {@link AbstractActor} serialized by this method is either:
	 * {@link BroadcastActor}, {@link JoinActor}, {@link ForkActor}, and
	 * {@link RoundBufferActor}.
	 * 
	 * @param vertexElt
	 *            The {@link Element} to write
	 * @param actor
	 *            The {@link Actor} to serialize
	 */
	protected void writeSpecialActor(Element vertexElt, AbstractActor actor) {
		String kind = null;
		if (actor instanceof BroadcastActor) {
			kind = PiIdentifiers.BROADCAST;
		} else if (actor instanceof JoinActor) {
			kind = PiIdentifiers.JOIN;
		} else if (actor instanceof ForkActor) {
			kind = PiIdentifiers.FORK;
		} else if (actor instanceof RoundBufferActor) {
			kind = PiIdentifiers.ROUND_BUFFER;
		}
		vertexElt.setAttribute(PiIdentifiers.NODE_KIND, kind);

		writePorts(vertexElt, actor.getConfigInputPorts());
		writePorts(vertexElt, actor.getDataInputPorts());
		writePorts(vertexElt, actor.getDataOutputPorts());
	}
}
