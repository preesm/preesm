/**
 * 
 */
package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.codegen.printer.AbstractPrinter;
import org.sdf4j.model.dag.DAGVertex;

/**
 * Generated code within threads consists primarily in a succession
 * of code elements. 
 * 
 * @author mpelcat
 */
public interface ICodeElement {

	
	public void accept(AbstractPrinter printer);
	
	public DAGVertex getCorrespondingVertex();
}
