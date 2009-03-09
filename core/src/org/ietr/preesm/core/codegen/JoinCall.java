package org.ietr.preesm.core.codegen;

import java.util.ArrayList;
import java.util.List;

import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

public class JoinCall extends AbstractCodeElement {

	/**
	 * The buffer set contains all the buffers usable by the user function
	 */
	private List<Buffer> inputBuffers;

	/**
	 * The buffer to explode
	 */
	private Buffer outputBuffer;

	public JoinCall(SDFAbstractVertex vertex,
			AbstractBufferContainer parentContainer) {
		super(vertex.getName(), parentContainer, vertex);

		inputBuffers = new ArrayList<Buffer>();
		// Adding output buffers
		for (SDFEdge edge : vertex.getBase().incomingEdgesOf(vertex)) {
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
		for (SDFEdge edge : vertex.getBase().outgoingEdgesOf(vertex)) {
			AbstractBufferContainer parentBufferContainer = parentContainer;
			while (parentBufferContainer != null
					&& parentBufferContainer.getBuffer(edge) == null) {
				parentBufferContainer = parentBufferContainer
						.getParentContainer();
			}
			if (parentBufferContainer != null) {
				outputBuffer = parentBufferContainer.getBuffer(edge);
			}
		}
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit
	}

	public Buffer getOutputBuffer() {
		return outputBuffer;
	}

	public List<Buffer> getInputBuffers() {
		return inputBuffers;
	}

	/**
	 * Displays pseudo-code for test
	 */
	public String toString() {
		return "";
	}
}
