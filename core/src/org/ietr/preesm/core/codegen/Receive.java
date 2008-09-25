/**
 * 
 */
package org.ietr.preesm.core.codegen;

import java.util.Set;

import org.ietr.preesm.core.architecture.Medium;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.codegen.printer.AbstractPrinter;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * A receive function receives a data from another core
 * 
 * @author mpelcat
 */
public class Receive extends CommunicationFunctionCall{

	/**
	 * Source of the currently received communication
	 */
	Operator source;
	


	public Receive(AbstractBufferContainer parentContainer, DAGVertex vertex, Set<Buffer> bufferSet, Medium medium, Operator source) {
		super("receive", parentContainer, bufferSet, medium, vertex);
		
		this.source = source;
	}


	public void accept(AbstractPrinter printer) {
		printer.visit(this,0);
		super.accept(printer);
		printer.visit(this,1);
	}

	public Operator getSource() {
		return source;
	}
	
	@Override
	public String toString() {

		String code = super.toString();

		code = getName() + "(" + source.getName() + ","+ code + ");";


		return code;
	}
}
