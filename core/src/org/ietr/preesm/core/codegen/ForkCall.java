package org.ietr.preesm.core.codegen;

import java.util.ArrayList;
import java.util.List;

import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

public class ForkCall extends AbstractCodeElement {

	/**
	 * The buffer set contains all the buffers usable by the user function
	 */
	private List<Buffer> outputBuffers;
	
	/**
	 * The buffer to explode
	 */
	private Buffer inputBuffer;

	public ForkCall(SDFAbstractVertex vertex,
			AbstractBufferContainer parentContainer) {
		super(vertex.getName(), parentContainer, vertex);

		outputBuffers = new  ArrayList<Buffer>();
			// Adding output buffers
			for (SDFEdge edge : vertex.getBase().outgoingEdgesOf(vertex)) {
				AbstractBufferContainer parentBufferContainer = parentContainer;
				while (parentBufferContainer != null
						&& parentBufferContainer.getBuffer(edge) == null) {
					parentBufferContainer = parentBufferContainer
							.getParentContainer();
				}
				if (parentBufferContainer != null) {
					outputBuffers.add(parentBufferContainer
							.getBuffer(edge));
				}
			}

			// Adding input buffers
			for (SDFEdge edge : vertex.getBase().incomingEdgesOf(vertex)) {
				AbstractBufferContainer parentBufferContainer = parentContainer;
				while (parentBufferContainer != null
						&& parentBufferContainer.getBuffer(edge) == null) {
					parentBufferContainer = parentBufferContainer
							.getParentContainer();
				}
				if (parentBufferContainer != null) {
					inputBuffer= parentBufferContainer
							.getBuffer(edge);
				}
			}
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit
	}

	public Buffer getInputBuffer(){
		return inputBuffer ;
	}
	
	public List<Buffer> getOutputBuffers(){
		return outputBuffers ;
	}
	/**
	 * Displays pseudo-code for test
	 */
	public String toString() {
		return "";
	}
}
