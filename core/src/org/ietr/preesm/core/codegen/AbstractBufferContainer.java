/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

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
 
package org.ietr.preesm.core.codegen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.core.codegen.printer.AbstractPrinter;
import org.ietr.preesm.core.codegen.sdfProperties.BufferAggregate;
import org.ietr.preesm.core.log.PreesmLogger;
import org.sdf4j.model.AbstractEdge;

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
	protected List<BufferAllocation> buffers;

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
		this.buffers = new ArrayList<BufferAllocation>();
		this.variables = new ArrayList<VariableAllocation>();
		this.parentContainer = parentContainer;
		this.semaphoreContainer = new SemaphoreContainer(this);
	}

	public void accept(AbstractPrinter printer) {
		if (buffers.size() > 0) {
			printer.visit(this, 0); // Visit self
			Iterator<BufferAllocation> iterator = buffers.iterator();

			while (iterator.hasNext()) {
				BufferAllocation alloc = iterator.next();
				alloc.accept(printer); // Accepts allocations
				printer.visit(this, 2); // Visit self
			}
		}
		if (variables.size() > 0) {
			printer.visit(this, 1); // Visit self
			Iterator<VariableAllocation> iterator2 = variables.iterator();

			while (iterator2.hasNext()) {
				VariableAllocation alloc = iterator2.next();
				alloc.accept(printer); // Accepts allocations
				printer.visit(this, 2); // Visit self
			}
		}
		printer.visit(this, 3); // Visit self
	}

	/**
	 * Adds the given buffer to the buffer list.
	 * 
	 * @param buffer
	 *            A {@link BufferAllocation}.
	 */
	public void addBuffer(BufferAllocation alloc) {
		if (getBuffer(alloc.getBuffer().getName()) == null)
			buffers.add(alloc);
		else
			PreesmLogger.getLogger()
					.log(
							Level.FINE,
							"buffer " + alloc.getBuffer().getName()
									+ " already exists");
	}

	/**
	 * Adds the given variable to this buffer container variable list.
	 * 
	 * @param var
	 *            A {@link Variable}.
	 */
	public void addVariable(Variable var) {
		VariableAllocation alloc = new VariableAllocation(var);
		variables.add(alloc);
	}

	/**
	 * Gets the buffer with the given name.
	 * 
	 * @param name
	 *            The buffer name.
	 * @return The buffer created.
	 */
	public Buffer getBuffer(String name) {
		Buffer buffer = null;
		Iterator<BufferAllocation> iterator = buffers.iterator();

		// Looks for the buffer in the current container
		while (iterator.hasNext()) {
			BufferAllocation alloc = iterator.next();
			buffer = alloc.getBuffer();

			if (alloc.getBuffer().getName().equalsIgnoreCase(name)) {
				return buffer;
			}
		}

		// If not found, searching in the parent container
		if (parentContainer != null)
			return (parentContainer.getBuffer(name));

		return null;
	}

	/**
	 * Gets the buffers corresponding to the given edge from its aggregate
	 */
	@SuppressWarnings("unchecked")
	public Set<Buffer> getBuffers(AbstractEdge edge) {
		BufferAggregate agg = (BufferAggregate) edge.getPropertyBean()
				.getValue(BufferAggregate.propertyBeanName);
		if (agg != null) {
			Set<Buffer> bufferSet = getBuffers(agg);
			return bufferSet;
		}
		return null;
	}

	/**
	 * Gets the buffers corresponding to the given edge from its aggregate
	 */
	public Set<Buffer> getBuffers(BufferAggregate agg) {
		Set<Buffer> bufferSet = new HashSet<Buffer>();

		Iterator<BufferAllocation> iterator = buffers.iterator();

		while (iterator.hasNext()) {
			BufferAllocation alloc = iterator.next();
			Buffer buffer = alloc.getBuffer();
			BufferAggregate bufferAggregate = buffer.getAggregate();

			if (bufferAggregate.equals(agg)) {
				bufferSet.add(buffer);
			}
		}

		// Searching in the parent container
		if (parentContainer != null)
			bufferSet.addAll(parentContainer.getBuffers(agg));

		return bufferSet;
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

	@Override
	public String toString() {
		String code = "";

		code += "\n//Buffer allocation for " + getName() + "\n";

		// Displays allocations
		for (BufferAllocation alloc : buffers) {
			code += alloc.toString();
			code += "\n";
		}

		return code;
	}

}
