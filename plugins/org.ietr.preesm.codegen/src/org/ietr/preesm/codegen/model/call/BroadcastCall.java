package org.ietr.preesm.codegen.model.call;

import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;

import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;

public class BroadcastCall extends SpecialBehaviorCall {

	private final String BROADCAST = "broadcast";

	public BroadcastCall(SDFAbstractVertex vertex,
			AbstractBufferContainer parentContainer) {
		super(vertex.getName(), parentContainer, vertex);

	}

	@Override
	public String getBehaviorId() {
		return BROADCAST;
	}

}