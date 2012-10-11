package org.ietr.preesm.experiment.model.pimemoc.serialize;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.dftools.algorithm.exporter.Key;
import net.sf.dftools.architecture.utils.DomUtil;

import org.ietr.preesm.experiment.model.pimemoc.Graph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Writer for the PIMeMoC Model in the GraphML format
 * 
 * @author kdesnos
 * 
 */
public class GraphMLWriter {

	/**
	 * The document created by this writer
	 */
	protected Document domDocument;

	/**
	 * Root {@link Element} of the DOM {@link Document} of this Writer
	 */
	protected Element rootElt;

	/**
	 * Graph {@link Element} of the DOM {@link Document} of this Writer
	 */
	protected Element graphElt;

	/**
	 * This HashMap associates a List to each <b>element</b> (graph, node, edge,
	 * port) of a GraphML description. For each <b>element</b>, a list of
	 * {@link Key}s is associated. A {@link Key} can be seen as an attribute of
	 * this element.
	 */
	protected HashMap<String, List<Key>> elementKeys;

	/**
	 * Default constructor of the GraphMLWriter
	 */
	public GraphMLWriter() {
		// Instantiate an empty elementKeys Map
		elementKeys = new HashMap<>();
		
		// Initialize attributes to null
		rootElt = null;
		graphElt = null;
		
	}

	/**
	 * Adds a Key to the elementKeys map with the specified informations. Also
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
			rootElt.insertBefore(keyElt, graphElt);
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
	 * Creates and appends the Graph Element of the document
	 * 
	 * @param parentElement
	 *            The parent element of the graph
	 * @return The created element
	 */
	protected Element createGraphElt(Element parentElement) {
		Element newElt = appendChild(parentElement, "graph");
		graphElt = newElt;
		newElt.setAttribute("edgedefault", "directed");

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

		return null;
	}

	/**
	 * Write the PIMeMoC {@link Graph} to the given {@link OutputStream} using
	 * the GraphML format.
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
		rootElt = domDocument.getDocumentElement();

		// Fill the root Element with the Graph
		writeGraphML(rootElt, graph);

		// Produce the output file
		DomUtil.writeDocument(outputStream, domDocument);
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
	protected void writeGraphML(Element parentElt, Graph graph) {
		// Add IBSDF Keys - Might not be needed.
		addKey("parameters", "parameters", "graph", null, null);
		addKey("variables", "variables", "graph", null, null);
		addKey("arguments", "arguments", "node", null, null);
		
		// Create and add the graphElt to the Document
		createGraphElt(parentElt);

		
	}
	
	protected void exportKeys(PropertySource source, String forElt,
			Element parentElt) {
		for (String key : source.getPublicProperties()) {
			if (!(key.equals("parameters") || key.equals("variables") || key
					.equals("arguments"))) {
				if (source.getPropertyStringValue(key) != null) {
					Element dataElt = appendChild(parentElt, "data");
					dataElt.setAttribute("key", key);
					dataElt.setTextContent(source.getPropertyStringValue(key));
					if (source.getPropertyBean().getValue(key) != null
							&& source.getPropertyBean().getValue(key) instanceof Number) {
						this.addKey(forElt, new Key(key, forElt, "int", null));
					} else {
						this.addKey(forElt,
								new Key(key, forElt, "string", null));
					}

				}
			}
		}
	}

}
