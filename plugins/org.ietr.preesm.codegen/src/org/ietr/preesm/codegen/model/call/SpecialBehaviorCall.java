/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-B license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-B
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
knowledge of the CeCILL-B license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.codegen.model.call;

import java.util.ArrayList;
import java.util.List;

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.buffer.Buffer;
import org.ietr.preesm.codegen.model.main.AbstractCodeElement;
import org.ietr.preesm.codegen.model.printer.CodeZoneId;
import org.ietr.preesm.codegen.model.printer.IAbstractPrinter;

public abstract class SpecialBehaviorCall extends AbstractCodeElement {

	protected List<Buffer> inputBuffers;

	/**
	 * The buffer to explode
	 */
	protected List<Buffer> outputBuffers;

	public SpecialBehaviorCall(String name,
			AbstractBufferContainer parentContainer,
			SDFAbstractVertex correspondingVertex) {
		super(name, parentContainer, correspondingVertex);
		inputBuffers = new ArrayList<Buffer>();
		outputBuffers = new ArrayList<Buffer>();
		// Adding output buffers
		for (SDFEdge edge : ((SDFGraph) correspondingVertex.getBase())
				.incomingEdgesOf(correspondingVertex)) {
			AbstractBufferContainer parentBufferContainer = parentContainer;
			while (parentBufferContainer != null
					&& parentBufferContainer.getBuffer(edge) == null) {
				parentBufferContainer = parentBufferContainer
						.getParentContainer();
			}
			if (parentBufferContainer != null) {
				inputBuffers.add(parentBufferContainer.getBuffer(edge));
			}
		}

		// Adding input buffers
		for (SDFEdge edge : ((SDFGraph) correspondingVertex.getBase())
				.outgoingEdgesOf(correspondingVertex)) {
			AbstractBufferContainer parentBufferContainer = parentContainer;
			while (parentBufferContainer != null
					&& parentBufferContainer.getBuffer(edge) == null) {
				parentBufferContainer = parentBufferContainer
						.getParentContainer();
			}
			if (parentBufferContainer != null) {
				outputBuffers.add(parentBufferContainer.getBuffer(edge));
			}
		}
	}

	public abstract String getBehaviorId();

	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit
	}

	public List<Buffer> getOutputBuffers() {
		return outputBuffers;
	}

	public List<Buffer> getInputBuffers() {
		return inputBuffers;
	}

}
