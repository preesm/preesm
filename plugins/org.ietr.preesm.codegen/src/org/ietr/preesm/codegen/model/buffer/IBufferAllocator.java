package org.ietr.preesm.codegen.model.buffer;

import java.util.List;

import net.sf.dftools.algorithm.model.sdf.SDFEdge;

import org.ietr.preesm.codegen.model.printer.IAbstractPrinter;
import org.ietr.preesm.core.types.DataType;

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
