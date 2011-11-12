package org.ietr.preesm.core.codegen.buffer.allocators;

import java.util.List;

import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.buffer.BufferAllocation;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.ietr.preesm.core.codegen.types.DataType;
import net.sf.dftools.algorithm.model.sdf.SDFEdge;

public interface IBufferAllocator {

	public Buffer addBuffer(SDFEdge edge, String name, DataType type);

	public IBufferAllocator openNewSection(AbstractBufferContainer codeSection);

	public void accept(IAbstractPrinter printer, Object currentLocation);

	public Buffer getBuffer(String name);

	public Buffer getBuffer(SDFEdge edge);

	public List<BufferAllocation> getBufferAllocations();

	public boolean removeBufferAllocation(Buffer buff);

	public List<IBufferAllocator> getChildAllocators();

}
