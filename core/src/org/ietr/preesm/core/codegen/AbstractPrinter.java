/**
 * 
 */
package org.ietr.preesm.core.codegen;

import java.util.Map;


/**
 * Visitor that prints the code using given rules
 * 
 * @author mpelcat
 */
public abstract class AbstractPrinter {
	
	Map<String,String> sourceCode;
	
	public abstract void visit(AbstractBufferContainer element, int index);
	public abstract void visit(AbstractCodeContainer element, int index);
	public abstract void visit(Buffer element, int index);
	public abstract void visit(BufferAllocation element, int index);
	public abstract void visit(CodeElement element, int index);
	public abstract void visit(CommunicationFunctionCall element, int index);
	public abstract void visit(ForLoop element, int index);
	public abstract void visit(LinearCodeContainer element, int index);
	public abstract void visit(Receive element, int index);
	public abstract void visit(Semaphore element, int index);
	public abstract void visit(SemaphorePend element, int index);
	public abstract void visit(SemaphorePost element, int index);
	public abstract void visit(Send element, int index);
	public abstract void visit(SourceFile element, int index);
	public abstract void visit(SourceFileList element, int index);
	public abstract void visit(ThreadDeclaration element, int index);
	public abstract void visit(UserFunctionCall element, int index);
}
