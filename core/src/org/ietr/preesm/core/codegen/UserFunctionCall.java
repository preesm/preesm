/**
 * 
 */
package org.ietr.preesm.core.codegen;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.ietr.preesm.core.codegen.printer.AbstractPrinter;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

/**
 * Generated code consists primarily in a succession
 * of code elements. A User Function Call is a call
 * corresponding to a vertex in the graph
 * 
 * @author mpelcat
 */
public class UserFunctionCall extends CodeElement {
	
	/**
	 * The buffer set contains all the buffers usable by the user function
	 */
	private Set<Buffer> availableBuffers;

	public UserFunctionCall(String name, DAGVertex vertex, AbstractBufferContainer parentContainer) {
		super(name,parentContainer, vertex);
		
		availableBuffers = new HashSet<Buffer>();
		
		// Adds all buffers that can be called by the user function
		addVertexBuffers(vertex);
	}
	
	public void addBuffer(Buffer buffer){
		availableBuffers.add(buffer);
	}
	
	public void addBuffers(Set<Buffer> buffers){
		availableBuffers.addAll(buffers);
	}

	/**
	 * Adds to the function call all the buffers created by the vertex.
	 */
	public void addVertexBuffers(DAGVertex vertex){
		
		addVertexBuffers(vertex,true);
		addVertexBuffers(vertex,false);
	}
	
	/**
	 * Add input or output buffers for a vertex, depending on the direction
	 */
	public void addVertexBuffers(DAGVertex vertex, boolean isInputBuffer) {

		Iterator<DAGEdge> eIterator;

		if (isInputBuffer)
			eIterator = vertex.getBase().incomingEdgesOf(vertex).iterator();
		else
			eIterator = vertex.getBase().outgoingEdgesOf(vertex).iterator();

		// Iteration on all the edges of each vertex belonging to ownVertices
		while (eIterator.hasNext()) {
			DAGEdge edge = eIterator.next();
			
			addBuffers(getParentContainer().getBuffers(edge));
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
		
		while(iterator.hasNext()){

			if(!first)
				code += ",";
			else
				first = false;
			
			Buffer buf = iterator.next();

			code += buf.toString();
		}
		
		code += ");";
		
		return code;
	}
	
	public void accept(AbstractPrinter printer) {
		printer.visit(this,0); // Visit self
	}
}
