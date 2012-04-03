/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
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
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.codegen.model.buffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.dftools.algorithm.model.sdf.SDFEdge;

import org.ietr.preesm.codegen.model.allocators.AllocationPolicy;
import org.ietr.preesm.codegen.model.allocators.IBufferAllocator;
import org.ietr.preesm.codegen.model.call.Variable;
import org.ietr.preesm.codegen.model.expression.VariableExpression;
import org.ietr.preesm.codegen.model.main.VariableAllocation;
import org.ietr.preesm.codegen.model.printer.CodeZoneId;
import org.ietr.preesm.codegen.model.printer.IAbstractPrinter;
import org.ietr.preesm.codegen.model.semaphore.SemaphoreContainer;
import org.ietr.preesm.core.types.DataType;

/**
 * A thread can contain buffer allocations as well as a source file (for static
 * allocations). They are both AbstractBufferContainer
 * 
 * @author Maxime Pelcat
 * @author Matthieu Wipliez
 */
public abstract class AbstractBufferContainer {

	/**
	 * Contained buffers
	 */
	protected IBufferAllocator bufferAllocator;

	protected List<BufferAllocation> allocs;

	/**
	 * If the container was created in another container, reference to the
	 * parent container. Buffers can be looked for in all ancestor containers
	 */
	private AbstractBufferContainer parentContainer;

	/**
	 * A buffer container contains a semaphore container to allocate semaphores
	 */
	private SemaphoreContainer semaphoreContainer;

	/**
	 * Contained variables
	 */
	protected List<VariableAllocation> variables;

	/**
	 * Creates a new buffer container with the given parent container.
	 * 
	 * @param parentContainer
	 *            A parent buffer container. May be <code>null</code>.
	 */
	public AbstractBufferContainer(AbstractBufferContainer parentContainer) {
		if (parentContainer != null && parentContainer.getHeap() != null) {
			this.bufferAllocator = parentContainer.getHeap().openNewSection(
					this);
		} else {
			this.bufferAllocator = AllocationPolicy.getInstance().getAllocator(
					this);
		}
		allocs = new ArrayList<BufferAllocation>();
		this.variables = new ArrayList<VariableAllocation>();
		this.parentContainer = parentContainer;
		this.semaphoreContainer = new SemaphoreContainer(this);
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {

		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit
		// self

		// print the buffer allocator
		bufferAllocator.accept(printer, currentLocation);

		// allocate sub-buffers
		if (allocs.size() > 0) {
			Iterator<BufferAllocation> iterator = allocs.iterator();

			while (iterator.hasNext()) {
				BufferAllocation alloc = iterator.next();
				alloc.accept(printer, currentLocation); // Accepts allocations
			}
		}
		if (variables.size() > 0) {
			Iterator<VariableAllocation> iterator2 = variables.iterator();

			while (iterator2.hasNext()) {
				VariableAllocation alloc = iterator2.next();
				alloc.accept(printer, currentLocation); // Accepts allocations
			}
		}
	}

	/**
	 * Adds the given buffer to the buffer list.
	 * 
	 * @param buffer
	 *            A {@link BufferAllocation}.
	 */
	public Buffer allocateBuffer(SDFEdge edge, String name, DataType type) {
		return bufferAllocator.addBuffer(edge, name, type);
	}

	/**
	 * Adds the given buffer to the buffer list.
	 * 
	 * @param buffer
	 *            A {@link BufferAllocation}.
	 */
	public Buffer allocateBuffer(String name, int size, DataType type) {
		Buffer newBuffer = new Buffer(name, size, new DataType(type), null,
				this);
		addBuffer(new BufferAllocation(newBuffer));
		return newBuffer;
	}

	public void addBuffer(BufferAllocation alloc) {
		allocs.add(alloc);
	}

	/**
	 * Adds the given buffer to the buffer list.
	 * 
	 * @param buffer
	 *            A {@link BufferAllocation}.
	 */
	public void addSubBufferAllocation(SubBufferAllocation alloc) {
		allocs.add(alloc);
	}

	/**
	 * Adds the given variable to this buffer container variable list.
	 * 
	 * @param var
	 *            A {@link VariableExpression}.
	 */
	public void addVariable(Variable var) {
		VariableAllocation alloc = new VariableAllocation(var);
		variables.add(alloc);
	}

	/**
	 * Gives the variable with the given name
	 * 
	 * @param name
	 *            The name of the variable to return
	 * @return The variable if found, null otherwise
	 */
	public Variable getVariable(String name) {
		for (VariableAllocation var : variables) {
			if (var.getVariable().getName().equals(name)) {
				return var.getVariable();
			}
		}
		if (parentContainer != null) {
			return parentContainer.getVariable(name);
		} else {
			return null;
		}
	}

	/**
	 * Returns true the buffer with the given name already exists somewhere
	 * (accessible or not).
	 */
	public boolean existBuffer(String name, boolean searchInOtherFiles) {
		if (bufferAllocator == null) {
			return false;
		}

		if (bufferAllocator.getBuffer(name) != null) {
			return true;
		}

		// Looks for the buffer in the current sub-buffers
		Iterator<BufferAllocation> iterator = allocs.iterator();
		while (iterator.hasNext()) {
			BufferAllocation alloc = iterator.next();
			@SuppressWarnings("unused")
			Buffer buffer = alloc.getBuffer();

			if (alloc.getBuffer().getName().equalsIgnoreCase(name)) {
				return true;
			}
		}

		// If not found, searching in the parent container
		if (parentContainer != null) {
			if (parentContainer.existBuffer(name, searchInOtherFiles)) {
				return true;
			}
		}

		return false;
	}

	public List<Buffer> getBuffers(Set<SDFEdge> edges) {
		List<Buffer> result = new ArrayList<Buffer>();
		for (SDFEdge edge : edges) {
			Buffer buf = getBuffer(edge);
			if (buf != null) {
				result.add(buf);
			}
		}
		return result;
	}

	public Buffer getBuffer(SDFEdge edge) {
		for (BufferAllocation alloc : allocs) {
			if (alloc.getBuffer().getEdge() != null
					&& alloc.getBuffer().getEdge().equals(edge)) {
				return alloc.getBuffer();
			}
		}
		if (bufferAllocator.getBuffer(edge) != null) {
			return bufferAllocator.getBuffer(edge);
		}
		if (parentContainer != null)
			return (parentContainer.getBuffer(edge));

		return null;
	}

	/**
	 * Gets the container corresponding to global allocations
	 */
	public AbstractBufferContainer getGlobalContainer() {
		if (parentContainer != null)
			return parentContainer.getGlobalContainer();
		else
			return this;
	}

	/**
	 * Returns the name of this buffer container. This method should be
	 * implemented by classes extending {@link AbstractBufferContainer}.
	 * 
	 * @return The buffer container name, or <code>""</code>.
	 */
	public abstract String getName();

	public AbstractBufferContainer getParentContainer() {
		return parentContainer;
	}

	public SemaphoreContainer getSemaphoreContainer() {
		return semaphoreContainer;
	}

	public List<VariableAllocation> getVariables() {
		return variables;
	}

	public List<BufferAllocation> getBufferAllocations() {
		return bufferAllocator.getBufferAllocations();
	}

	public List<BufferAllocation> getSubBufferAllocations() {
		return allocs;
	}

	/**
	 * Add a global buffer declaration
	 * 
	 * @param alloc
	 */
	public Buffer addGlobalBuffer(SDFEdge edge, String name, DataType type) {
		return getGlobalContainer().allocateBuffer(edge, name, type);
	}

	public void removeBufferAllocation(Buffer buff) {
		if (bufferAllocator.removeBufferAllocation(buff)) {
			return;
		}

		for (int i = 0; i < allocs.size(); i++) {
			BufferAllocation alloc = allocs.get(i);
			if (alloc.getBuffer().equals(buff)) {
				allocs.remove(alloc);
				return;
			}
		}
		if (this.parentContainer != null) {
			this.parentContainer.removeBufferAllocation(buff);
		}
	}

	@Override
	public String toString() {
		String code = "";

		code += "\n//Buffer allocation for " + getName() + "\n";

		// Displays allocations
		for (BufferAllocation alloc : bufferAllocator.getBufferAllocations()) {
			code += alloc.toString();
			code += "\n";
		}

		return code;
	}

	public IBufferAllocator getHeap() {
		return bufferAllocator;
	}

	public void setParentContainer(AbstractBufferContainer parent) {
		parentContainer = parent;
	}
}
