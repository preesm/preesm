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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.core.codegen.model.CodeGenSDFVertex;
import org.ietr.preesm.core.codegen.model.FunctionCall;
import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

/**
 * Generated code consists primarily in a succession of code elements. A User
 * Function Call is a call corresponding to a vertex in the graph
 * 
 * @author mpelcat
 */
public class UserFunctionCall extends AbstractCodeElement {

	public enum CodeSection{
		INIT,
		LOOP,
		END;
	}
	/**
	 * The buffer set contains all the buffers usable by the user function
	 */
	private Set<Buffer> availableBuffers;

	public UserFunctionCall(SDFAbstractVertex vertex,
			AbstractBufferContainer parentContainer, CodeSection section) {
		super(vertex.getName(), parentContainer, vertex);
		availableBuffers = new HashSet<Buffer>();
		if(vertex instanceof CodeGenSDFVertex){
			FunctionCall call = ((FunctionCall) vertex.getRefinement());
			if(call != null){
				switch(section){
				case INIT:
					if(call.getInitCall() != null){
						this.setName(call.getInitCall().getFunctionName());
					}
					break ;
				case LOOP:
					this.setName(call.getFunctionName());
					break ;
				case END:
					if(call.getEndCall() != null){
						this.setName(call.getEndCall().getFunctionName());
					}
					break ;
						
				}
			}
			for (SDFEdge edge : vertex.getBase().outgoingEdgesOf(
					vertex)) {
				AbstractBufferContainer parentBufferContainer = parentContainer ;
				while(parentBufferContainer != null && parentBufferContainer.getBuffer(edge)==null){
					parentBufferContainer = parentBufferContainer.getParentContainer();
				}if(parentBufferContainer != null ){
					this.addBuffer(parentBufferContainer.getBuffer(edge));
				}
			}
			for (SDFEdge edge : vertex.getBase().incomingEdgesOf(
					vertex)) {
				AbstractBufferContainer parentBufferContainer = parentContainer ;
				while(parentBufferContainer != null && parentBufferContainer.getBuffer(edge)==null){
					parentBufferContainer = parentBufferContainer.getParentContainer();
				}if(parentBufferContainer != null ){
					this.addBuffer(parentBufferContainer.getBuffer(edge));
				}
			}
		}
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer
				.visit(this, CodeZoneId.body, currentLocation); // Visit self
		for (Buffer buffer : availableBuffers) {
			buffer.accept(printer, currentLocation);
		}
	}

	public void addBuffer(Buffer buffer) {

		if (buffer == null)
			PreesmLogger.getLogger().log(Level.SEVERE, "null buffer");
		else
			availableBuffers.add(buffer);
	}

	public void addBuffers(Set<Buffer> buffers) {
		if (buffers != null) {
			for (Buffer buffer : buffers) {
				addBuffer(buffer);
			}
		}
	}

	/**
	 * Adds to the function call all the buffers created by the vertex.
	 */
	public void addVertexBuffers(SDFAbstractVertex vertex) {
		addVertexBuffers(vertex, true);
		addVertexBuffers(vertex, false);
	}

	/**
	 * Add input or output buffers for a vertex, depending on the direction
	 */
	public void addVertexBuffers(SDFAbstractVertex vertex, boolean isInputBuffer) {

		Iterator<SDFEdge> eIterator;

		if (isInputBuffer)
			eIterator = vertex.getBase().incomingEdgesOf(vertex).iterator();
		else
			eIterator = vertex.getBase().outgoingEdgesOf(vertex).iterator();

		// Iteration on all the edges of each vertex belonging to ownVertices
		while (eIterator.hasNext()) {
			SDFEdge edge = eIterator.next();

			addBuffer(getParentContainer().getBuffer((SDFEdge)edge));
		}
	}

	public Set<Buffer> getAvailableBuffers() {
		return availableBuffers;
	}

	/**
	 * Displays pseudo-code for test
	 */
	public String toString() {

		String code = "";
		boolean first = true;

		code += super.toString();
		code += "(";

		Iterator<Buffer> iterator = availableBuffers.iterator();

		while (iterator.hasNext()) {

			if (!first)
				code += ",";
			else
				first = false;

			Buffer buf = iterator.next();

			code += buf.toString();
		}

		code += ");";

		return code;
	}
}
