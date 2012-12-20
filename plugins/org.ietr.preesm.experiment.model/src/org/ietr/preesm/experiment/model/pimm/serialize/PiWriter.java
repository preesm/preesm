package org.ietr.preesm.experiment.model.pimm.serialize;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.dftools.algorithm.exporter.Key;
import net.sf.dftools.architecture.utils.DomUtil;

import org.eclipse.emf.common.util.EList;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Writer for the PiMM Model in the Pi format
 * 
 * @author kdesnos
 * 
 */
public class PiWriter {

	/**
	 * The document created by this writer
	 */
	protected Document domDocument;

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
	 */
	public PiWriter() {
		// Instantiate an empty elementKeys Map
		elementKeys = new HashMap<>();

		// Initialize attributes to null
		rootElement = null;
		graphElement = null;

	}

	/**
	 * Creates and appends the Graph Element of the document
	 * 
	 * @param parentElement
	 *            The parent element of the graph
	 * @return The created element
	 */
	protected Element addGraphElt(Element parentElement) {
		Element newElt = appendChild(parentElement, "graph");
		graphElement = newElt;
		newElt.setAttribute("edgedefault", "directed");

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
	 * Write the PiMM {@link Graph} to the given {@link OutputStream} using the
	 * Pi format.
	 * 
	 * @param graph
	 *            The Graph to write
	 * @param outputStream
	 *            The written OutputStream
	 */
	public void write(Graph graph, OutputStream outputStream) {
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
		Element vertexElt = appendChild(graphElt, "node");

		// Set the unique ID of the node (equal to the vertex name)
		vertexElt.setAttribute("id", abstractActor.getName());

		// Add the name in the data of the node
		// writeDataElt(vertexElt, "name", vertex.getName());

		if (abstractActor instanceof Actor) {
			writeActor(vertexElt, (Actor) abstractActor);
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
		// TODO change this method when severa kinds will exist
		// Set the kind of the Actor
		vertexElt.setAttribute("kind", "actor");
		writeRefinement(vertexElt, actor.getRefinement());
		// writeDataElt(vertexElt, "kind", "actor");
		// Write ports of the actor
		writePorts(vertexElt, actor.getConfigInputPorts());
		writePorts(vertexElt, actor.getConfigOutputPorts());
		writePorts(vertexElt, actor.getInputPorts());
		writePorts(vertexElt, actor.getOutputPorts());

	}

	protected void writeConfigPorts(Element vertexElt,
			EList<ConfigInputPort> configInputPorts, String string) {
		// TODO Auto-generated method stub

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
		Element nameElt = appendChild(parentElt, "data");
		nameElt.setAttribute("key", keyName);
		nameElt.setTextContent(textContent);
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
		Element dependencyElt = appendChild(graphElt, "edge");

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

		AbstractVertex target = (AbstractVertex) dependency.getGetter()
				.eContainer();
		dependencyElt.setAttribute("kind", "dependency");
		dependencyElt.setAttribute("source", source.getName());
		dependencyElt.setAttribute("target", target.getName());

		if (setter instanceof ConfigOutputPort) {
			dependencyElt.setAttribute("sourceport", ((Port) setter).getName());
		}

		if (target instanceof Actor) {
			dependencyElt.setAttribute("targetport", dependency.getGetter()
					.getName());
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
		Element fifoElt = appendChild(graphElt, "edge");

		// Set the source and target attributes
		AbstractActor source = (AbstractActor) fifo.getSourcePort()
				.eContainer();
		AbstractActor target = (AbstractActor) fifo.getTargetPort()
				.eContainer();
		fifoElt.setAttribute("kind", "fifo");
		fifoElt.setAttribute("source", source.getName());
		fifoElt.setAttribute("target", target.getName());
		fifoElt.setAttribute("sourceport", fifo.getSourcePort().getName());
		fifoElt.setAttribute("targetport", fifo.getTargetPort().getName());

		// TODO write Delays and other Fifo properties
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
	protected void writeGraph(Element rootElt, Graph graph) {
		// Create and add the graphElt to the Document
		Element graphElt = addGraphElt(rootElt);
		writeDataElt(graphElt, "name", graph.getName());

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
		vertexElt.setAttribute("kind", vertex.getKind());
		// writeDataElt(vertexElt, "kind", "actor");
		// Write ports of the actor
		switch (vertex.getKind()) {
		case "src":
			writePorts(vertexElt, vertex.getOutputPorts());
			break;
		case "snk":
			writePorts(vertexElt, vertex.getInputPorts());
			break;
		default:
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
		Element paramElt = appendChild(graphElt, "node");

		// Set the unique ID of the node (equal to the param name)
		paramElt.setAttribute("id", param.getName());

		// Set the kind of the node
		if (!param.isConfigurationInterface()) {
			paramElt.setAttribute("kind", "param");
		} else {
			paramElt.setAttribute("kind", "cfg_in_iface");
		}
	}

	/**
	 * Fill the {@link Element} with a description of the input {@link Graph}
	 * 
	 * @param parentElt
	 *            The Element to fill (could be removed later if it is always
	 *            rootElt)
	 * @param graph
	 *            The serialized Graph
	 */
	protected void writePi(Element parentElt, Graph graph) {
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
			Element portElt = appendChild(vertexElt, "port");
			portElt.setAttribute("name", port.getName());
			portElt.setAttribute("kind", port.getKind());
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
		if (refinement.getFileName() != null) {
			writeDataElt(vertexElt, "graph_desc", refinement.getFileName());
		}
	}
}
