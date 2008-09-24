/**
 * 
 */
package org.ietr.preesm.core.codegen;


/**
 * A buffer allocation is necessary for every input and
 * output of the SDF description.
 * 
 * @author mwipliez
 * @author mpelcat
 */
public class BufferAllocation {


	/**
	 * Buffer to allocate
	 */
	private Buffer buffer;
	
	/**
	 * Constructor
	 */
	public BufferAllocation(Buffer buffer) {
		this.buffer = buffer;
	}

	public void accept(AbstractPrinter printer) {
		printer.visit(this, 0);
	}

	public Buffer getBuffer() {
		return buffer;
	}

	@Override
	public String toString() {
		String code = "";
		
		code += buffer.getType().getTypeName();
		code += "[";
		code += buffer.getSize().toString();
		code += "] ";
		code += buffer.toString();
		code += ";";
		
		return code;
	}

}
