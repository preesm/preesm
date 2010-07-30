package org.ietr.preesm.core.codegen.buffer;

import java.util.List;

import org.ietr.preesm.core.codegen.types.DataType;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.sdf4j.model.sdf.SDFEdge;

public interface IBufferAllocator {

	public Buffer addBuffer(SDFEdge edge, String name, int size, DataType type);

	public IBufferAllocator openNewSection(AbstractBufferContainer codeSection);

	public void accept(IAbstractPrinter printer, Object currentLocation);

	public Buffer getBuffer(String name);

	public Buffer getBuffer(SDFEdge edge);

	public List<BufferAllocation> getBufferAllocations();
	
	public boolean removeBufferAllocation(Buffer buff);
	
	public List<IBufferAllocator> getChildAllocators();

}
