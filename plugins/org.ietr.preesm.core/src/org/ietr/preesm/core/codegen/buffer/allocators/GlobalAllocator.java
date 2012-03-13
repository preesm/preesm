package org.ietr.preesm.core.codegen.buffer.allocators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.dftools.algorithm.model.sdf.SDFEdge;

import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.buffer.BufferAllocation;
import org.ietr.preesm.core.codegen.buffer.BufferToolBox;
import org.ietr.preesm.core.codegen.buffer.Pointer;
import org.ietr.preesm.core.codegen.expression.IExpression;
import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.ietr.preesm.core.codegen.types.DataType;

public class GlobalAllocator implements IBufferAllocator {

	protected AbstractBufferContainer container;
	protected Map<SDFEdge, BufferAllocation> alloc;
	protected IBufferAllocator parentAllocator;
	protected List<IBufferAllocator> childAllocator;

	public GlobalAllocator(AbstractBufferContainer container) {
		this.container = container;
		alloc = new HashMap<SDFEdge, BufferAllocation>();
		parentAllocator = null;
		childAllocator = new ArrayList<IBufferAllocator>();
	}

	public GlobalAllocator(IBufferAllocator parent,
			AbstractBufferContainer container) {
		this.container = container;
		alloc = new HashMap<SDFEdge, BufferAllocation>();
		parentAllocator = parent;
		childAllocator = new ArrayList<IBufferAllocator>();
	}

	@Override
	public Buffer addBuffer(SDFEdge edge, String name, DataType type) {
		Buffer newBuffer;
		int size = BufferToolBox.getBufferSize(edge);
		if (size == 0) {
			IExpression ssize = BufferToolBox.getBufferSymbolicSize(edge);
			;
			newBuffer = new Pointer(name, new DataType(type), edge, ssize,
					container);
		} else {
			newBuffer = new Buffer(name, size, new DataType(type), edge,
					container);
		}
		alloc.put(edge, new BufferAllocation(newBuffer));
		return newBuffer;

	}

	@Override
	public IBufferAllocator openNewSection(AbstractBufferContainer codeSection) {
		IBufferAllocator newAlloc = new GlobalAllocator(this, codeSection);
		this.childAllocator.add(newAlloc);
		return newAlloc;
	}

	@Override
	public void accept(IAbstractPrinter printer, Object currentLocation) {
		if (parentAllocator == null) {
			List<IBufferAllocator> allocStack = new ArrayList<IBufferAllocator>();
			allocStack.add(this);
			while (allocStack.size() > 0) {
				if (allocStack.get(0) instanceof GlobalAllocator) {
					for (BufferAllocation allocToPrint : allocStack.get(0)
							.getBufferAllocations()) {
						printer.visit(allocToPrint, CodeZoneId.body,
								currentLocation);
					}
					allocStack.addAll(((GlobalAllocator) allocStack.get(0))
							.getChildAllocators());
				} else {
					allocStack.get(0).accept(printer, currentLocation);
				}
				allocStack.remove(0);
			}
		}

	}

	public List<IBufferAllocator> getChildAllocators() {
		return childAllocator;
	}

	@Override
	public Buffer getBuffer(String name) {
		for (SDFEdge key : alloc.keySet()) {
			if (alloc.get(key).getBuffer().getName().equals(name)) {
				return alloc.get(key).getBuffer();
			}
		}
		return null;
	}

	@Override
	public Buffer getBuffer(SDFEdge edge) {
		if (alloc.get(edge) != null) {
			return alloc.get(edge).getBuffer();
		}
		return null;
	}

	@Override
	public List<BufferAllocation> getBufferAllocations() {
		return new ArrayList<BufferAllocation>(alloc.values());
	}

	@Override
	public boolean removeBufferAllocation(Buffer buff) {
		for (SDFEdge key : alloc.keySet()) {
			if (alloc.get(key).getBuffer() == buff) {
				alloc.remove(key);
				return true;
			}
		}
		return false;
	}

}
