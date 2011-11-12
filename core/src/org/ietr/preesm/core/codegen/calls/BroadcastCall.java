package org.ietr.preesm.core.codegen.calls;

import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;

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