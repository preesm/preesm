package org.ietr.preesm.experiment.model.pimemoc.serialize;

import java.io.InputStream;

import org.eclipse.emf.common.util.URI;
import org.ietr.preesm.experiment.model.pimemoc.Graph;

public class GraphMLParser {

	/**
	 * The URI of the parsed file
	 */
	private URI uri;
	
	public GraphMLParser(URI uri) {
		this.uri = uri;
	}

	public Graph parse(InputStream inputStream) {
		// TODO Implement GraphMLParser.parse()
		return null;
	}

}
