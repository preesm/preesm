/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-B license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-B
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-B license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.codegen.model.allocators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.buffer.Buffer;
import org.ietr.preesm.codegen.model.buffer.BufferAllocation;
import org.ietr.preesm.codegen.model.buffer.BufferToolBox;
import org.ietr.preesm.codegen.model.buffer.Pointer;
import org.ietr.preesm.codegen.model.expression.IExpression;
import org.ietr.preesm.codegen.model.printer.CodeZoneId;
import org.ietr.preesm.codegen.model.printer.IAbstractPrinter;
import org.ietr.preesm.core.types.DataType;

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
