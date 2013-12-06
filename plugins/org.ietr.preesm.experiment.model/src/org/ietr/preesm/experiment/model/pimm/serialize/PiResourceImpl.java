/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
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
 ******************************************************************************/
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
