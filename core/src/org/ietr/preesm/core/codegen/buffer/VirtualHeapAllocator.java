package org.ietr.preesm.core.codegen.buffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ietr.preesm.core.codegen.DataType;
import org.ietr.preesm.core.codegen.expression.ConstantValue;
import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.sdf4j.model.sdf.SDFEdge;

public class VirtualHeapAllocator extends BufferAllocation implements
		IBufferAllocator {

	private static final int MIN_SIZE = 0;
	private Map<AbstractBufferContainer, HeapSectionAllocator> subHeap;
	private HeapSectionAllocator currentSection;
	protected int currentPos;
	private AbstractBufferContainer container;
	protected Map<SDFEdge, SubBufferAllocation> alloc;
	protected Map<BufferAllocation, Integer> allocToPos;
	protected int basePos;

	public VirtualHeapAllocator(AbstractBufferContainer container) {
		super(new Buffer("virtual_heap", MIN_SIZE, new DataType("char"),
				container));
		basePos = 0;
		this.container = container;
		currentPos = 0;
		subHeap = new HashMap<AbstractBufferContainer, HeapSectionAllocator>();
		alloc = new HashMap<SDFEdge, SubBufferAllocation>();
		allocToPos = new HashMap<BufferAllocation, Integer>();
	}

	public HeapSectionAllocator openNewSection(
			AbstractBufferContainer codeSection) {
		currentSection = new HeapSectionAllocator(this, codeSection, currentPos);
		subHeap.put(codeSection, currentSection);
		allocToPos.put(currentSection, currentPos);
		return currentSection;

	}

	public void setSize(int size) {
		this.getBuffer().setSize(size);
	}

	public SubBuffer addBuffer(SDFEdge edge, String name, int size,
			DataType type) {
		int trueSize = type.getSize() * size;
		SubBuffer newBuffer = new SubBuffer(name, size, new ConstantValue("",
				type, currentPos), new DataType(type), this.getBuffer(), edge,
				container);
		alloc.put(edge, new SubBufferAllocation(newBuffer));
		allocToPos.put(alloc.get(edge), currentPos);
		setSize(getSize() + trueSize);
		currentPos += trueSize;
		return newBuffer;
	}

	public int getSize() {
		return this.getBuffer().getSize();
	}

	public Buffer getBuffer(SDFEdge edge) {
		if (alloc.get(edge) != null) {
			return alloc.get(edge).getBuffer();
		}
		return null;
	}

	public int getCurrentPos() {
		return currentPos;
	}

	public int getBasePos() {
		return basePos;
	}

	public void removeBuffer(BufferAllocation buff) {
		for (SDFEdge key : alloc.keySet()) {
			if (alloc.get(key) == buff) {
				alloc.remove(key);
				setSize(getSize() - buff.getBuffer().getSize());
				refreshBufferPos(allocToPos.get(buff), buff.getSize());
				return;
			}
		}
	}

	public void refreshBufferPos(int oldBase, int diffSize) {
		for (BufferAllocation alloc : allocToPos.keySet()) {
			if (allocToPos.get(alloc) > oldBase) {
				if (alloc instanceof HeapSectionAllocator) {
					((HeapSectionAllocator) alloc).setBasePos(allocToPos
							.get(alloc)
							- diffSize);
					allocToPos.put(alloc, allocToPos.get(alloc) - diffSize);
				} else {
					((SubBuffer) alloc.getBuffer()).setIndex(new ConstantValue(
							allocToPos.get(alloc) - diffSize));
					allocToPos.put(alloc, allocToPos.get(alloc) - diffSize);
				}
			}
		}
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
	public List<BufferAllocation> getBufferAllocations() {
		return new ArrayList<BufferAllocation>(alloc.values());
	}

	@Override
	public boolean removeBufferAllocation(Buffer buff) {
		for (SDFEdge key : alloc.keySet()) {
			if (alloc.get(key).getBuffer() == buff) {
				alloc.remove(key);
				setSize(getSize() - buff.getSize());
				refreshBufferPos(allocToPos.get(buff), buff.getSize());
				return true;
			}
		}
		return false;
	}

	@Override
	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation);
		List<IBufferAllocator> allocStack = new ArrayList<IBufferAllocator>();
		allocStack.add(this);
		while (allocStack.size() > 0) {
			if (allocStack.get(0) instanceof VirtualHeapAllocator ) {
				for (BufferAllocation allocToPrint : allocStack.get(0)
						.getBufferAllocations()) {
					printer.visit((SubBufferAllocation) allocToPrint, CodeZoneId.body,
							currentLocation);
				}
				allocStack.addAll(((VirtualHeapAllocator) allocStack.get(0))
						.getChildAllocators());
			} else {
				allocStack.get(0).accept(printer, currentLocation);
			}
			allocStack.remove(0);
		}
	}

	@Override
	public List<IBufferAllocator> getChildAllocators() {
		return new ArrayList<IBufferAllocator>(subHeap.values());
	}

}
