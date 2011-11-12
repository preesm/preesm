package org.ietr.preesm.core.codegen.buffer;

import java.util.List;

import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.ietr.preesm.core.codegen.types.DataType;
import net.sf.dftools.algorithm.model.sdf.SDFEdge;

public class DistributedHeap implements IBufferAllocator {

	@Override
	public void accept(IAbstractPrinter printer, Object currentLocation) {
		// TODO Auto-generated method stub

	}

	@Override
	public Buffer addBuffer(SDFEdge edge, String name, int size, DataType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Buffer getBuffer(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Buffer getBuffer(SDFEdge edge) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BufferAllocation> getBufferAllocations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IBufferAllocator> getChildAllocators() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBufferAllocator openNewSection(AbstractBufferContainer codeSection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeBufferAllocation(Buffer buff) {
		// TODO Auto-generated method stub
		return false;
	}

}
