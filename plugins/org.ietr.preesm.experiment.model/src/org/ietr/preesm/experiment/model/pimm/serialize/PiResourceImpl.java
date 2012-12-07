package org.ietr.preesm.experiment.model.pimm.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.ietr.preesm.experiment.model.pimm.Graph;

/**
 * This class defines a resource implementation for the PiMM model which is
 * used to serialize/deserialize from Pi.
 * 
 * @author Karol Desnos
 * 
 */
public class PiResourceImpl extends ResourceImpl {

	/**
	 * Default constructor of the {@link PiResourceImpl}
	 * 
	 * This constructor is protected and should not be used.
	 */
	protected PiResourceImpl() {
	}

	/**
	 * Constructor of the {@link PiResourceImpl}
	 * 
	 * @param uri
	 *            The URI of the resource
	 */
	public PiResourceImpl(URI uri) {
		super(uri);
	}

	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options)
			throws IOException {
		// Get the unique graph of the resource
		Graph graph = (Graph) this.getContents().get(0);

		// Write the Graph to the OutputStream using the Pi format
		new PiWriter().write(graph, outputStream);
	}

	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options)
			throws IOException {
		// Parse the Graph from the InputStream using the Pi format
		Graph graph = new PiParser(uri).parse(inputStream);

		// If the graph was correctly parsed, add it to the Resource
		if (graph != null) {
			// TODO Why is !this.getContents.contains(graph) checked in Slam
			this.getContents().add(graph);
		}
	}
}
