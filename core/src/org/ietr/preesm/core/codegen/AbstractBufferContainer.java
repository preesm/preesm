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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.sdf4j.model.sdf.SDFEdge;

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

	public void accept(IAbstractPrinter printer, Object currentLocation) {

		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit
		// self

		if (buffers.size() > 0) {
			Iterator<BufferAllocation> iterator = buffers.iterator();

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
	public void addBuffer(BufferAllocation alloc) {
		if (!existBuffer(alloc.getBuffer().getName(),true))
			buffers.add(alloc);
		else{
			alloc.getBuffer().reduceName(this);
			addBuffer(alloc);
			PreesmLogger.getLogger().log(
					Level.SEVERE,
					"buffer " + alloc.getBuffer().getName()
							+ " already exists in the source file list renamed to "+alloc.getBuffer().getName());
		}
			
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
	 * Gives the variable with the given name
	 * @param name The name of the variable to return
	 * @return The variable if found, null otherwise
	 */
	public Variable getVariable(String name){
		for(VariableAllocation var : variables){
			if(var.getVariable().getName().equals(name)){
				return var.getVariable();
			}
		}
		return null ;
	}

	/**
	 * Returns true the buffer with the given name already exists somewhere
	 * (accessible or not).
	 */
	public boolean existBuffer(String name, boolean searchInOtherFiles) {
		Iterator<BufferAllocation> iterator = buffers.iterator();

		// Looks for the buffer in the current container
		while (iterator.hasNext()) {
			BufferAllocation alloc = iterator.next();
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
		for (BufferAllocation alloc : buffers) {
			if (alloc.getBuffer().getEdge().equals(edge)) {
				return alloc.getBuffer();
			}
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

	
	public List<BufferAllocation> getBufferAllocations(){
		return buffers;
	}
	
	
	/**
	 * Add a global buffer declaration
	 * @param alloc
	 */
	public void addGlobalBuffer(BufferAllocation alloc){
		getGlobalContainer().addBuffer(alloc);
	}
	
	public void removeBufferAllocation(Buffer buff){
		for(int i = 0 ; i < buffers.size() ; i ++){
			BufferAllocation alloc = buffers.get(i);
			if(alloc.getBuffer().equals(buff)){
				buffers.remove(alloc);
				return ;
			}
		}
		this.parentContainer.removeBufferAllocation(buff);
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
