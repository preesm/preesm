/**
 * 
 */
package org.ietr.preesm.core.codegen.printer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.codegen.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.AbstractCodeContainer;
import org.ietr.preesm.core.codegen.Buffer;
import org.ietr.preesm.core.codegen.BufferAllocation;
import org.ietr.preesm.core.codegen.CodeElement;
import org.ietr.preesm.core.codegen.CommunicationFunctionCall;
import org.ietr.preesm.core.codegen.ForLoop;
import org.ietr.preesm.core.codegen.LinearCodeContainer;
import org.ietr.preesm.core.codegen.Receive;
import org.ietr.preesm.core.codegen.Semaphore;
import org.ietr.preesm.core.codegen.SemaphorePend;
import org.ietr.preesm.core.codegen.SemaphorePost;
import org.ietr.preesm.core.codegen.Send;
import org.ietr.preesm.core.codegen.SourceFile;
import org.ietr.preesm.core.codegen.SourceFileList;
import org.ietr.preesm.core.codegen.ThreadDeclaration;
import org.ietr.preesm.core.codegen.UserFunctionCall;


/**
 * Visitor that prints the code using given rules
 * 
 * @author mpelcat
 */
public abstract class AbstractPrinter {

	/**
	 * The current source being filled
	 */
	protected String currentSource = null;
	
	public AbstractPrinter() {
		super();
		currentSource = new String();
	}
	
	public String getCurrentSource() {
		return currentSource;
	}
	
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
	public abstract void visit(ThreadDeclaration element, int index);
	public abstract void visit(UserFunctionCall element, int index);
}
