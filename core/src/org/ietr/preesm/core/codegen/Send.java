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
 * A send function transfers a data to another core
 * 
 * @author mpelcat
 */
public class Send extends CommunicationFunctionCall{

	/**
	 * Target of the currently sent communication
	 */
	Operator target;

	public Send(AbstractBufferContainer parentContainer, DAGVertex vertex, Set<Buffer> bufferSet, Medium medium, Operator target) {
		super("send", parentContainer, bufferSet, medium, vertex);
		
		this.target = target;
	}

	public void accept(AbstractPrinter printer) {
		printer.visit(this,0);
		super.accept(printer);
		printer.visit(this,1);
	}

	public Operator getTarget() {
		return target;
	}
	
	@Override
	public String toString() {

		String code = super.toString();

		code = getName() + "(" + target.getName() + ","+ code + ");";


		return code;
	}
}
