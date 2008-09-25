/**
 * 
 */
package org.ietr.preesm.plugin.codegen.print;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.codegen.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.AbstractCodeContainer;
import org.ietr.preesm.core.codegen.Buffer;
import org.ietr.preesm.core.codegen.BufferAllocation;
import org.ietr.preesm.core.codegen.AbstractCodeElement;
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
import org.ietr.preesm.core.codegen.printer.AbstractPrinter;

/**
 * Visitor that generates C code from source files for a PC target
 * 
 * @author mpelcat
 */
public class PCPrinter extends AbstractPrinter {

	
	public PCPrinter() {
		super();
	}

	/**
	 * Compares two buffers by their alphabetical order
	 */
	public class AlphaOrderComparator implements
	Comparator<Buffer>{

		@Override
		public int compare(Buffer o1, Buffer o2) {
			return o1.getName().compareTo(o2.getName());
		}
		
	}


	@Override
	public void visit(SourceFile element, int index) {
		
		if (index == 0) {
			
			// Very specific to c64
			currentSource += "#include <stdio.h>\n";
			currentSource += "#include <stdlib.h>\n";
			currentSource += "#include <std.h>\n";
			currentSource += "#include <tsk.h>\n";
			currentSource += "#define uchar unsigned char\n";
			currentSource += "#define ushort unsigned short\n";
			currentSource += "#define ulong unsigned long\n";
			currentSource += "#define prec_synchro int\n";
			currentSource += "#define stream uchar\n";
			currentSource += "#include \"..\\..\\lib_RACH\\common.h\"";
			
			
		} else if (index == 3) {
			
		}
		
	}

	@Override
	public void visit(AbstractBufferContainer element, int index) {
		if (index == 0) {
			currentSource += "\n//Buffer allocation for " + element.getName()
					+ "\n";
		} else if (index == 1) {
			currentSource += "\n";
		} else if (index == 2) {
			currentSource += "\n";
		}

	}

	@Override
	public void visit(ThreadDeclaration element, int index) {
		if (index == 0) {
			currentSource += "\n//Thread: " + element.getName() + "\n";
			currentSource += "void " + element.getName() + "()\n{\n";
		} else if (index == 1) {
			currentSource += "\n//beginningCode\n";
		} else if (index == 2) {
			currentSource += "\n//loopCode\n";
		} else if (index == 3) {
			currentSource += "\n//endCode\n";
		} else if (index == 4) {
			currentSource += "}//end thread: " + element.getName() + "\n";
		}

	}

	@Override
	public void visit(BufferAllocation element, int index) {
		currentSource += element.getBuffer().getType().getTypeName();
		currentSource += "[";
		currentSource += element.getBuffer().getSize().toString();
		currentSource += "] ";
		currentSource += element.getBuffer().toString();
		currentSource += ";";
	}

	@Override
	public void visit(LinearCodeContainer element, int index) {

		if (index == 0) {
			currentSource += "\n";
		} else if (index == 1) {
			currentSource += "\n";
		}
	}

	@Override
	public void visit(ForLoop element, int index) {
		if (index == 0) {
			currentSource += "\n\nfor(;;)";
		} else if (index == 1) {
			currentSource += "\n\n";
		}

	}

	@Override
	public void visit(AbstractCodeContainer element, int index) {
		if (index == 0) {
			currentSource += "{\n";
		} else if (index == 1) {
			currentSource += "\t";
		} else if (index == 2) {
			currentSource += "\n";
		} else if (index == 3) {
			currentSource += "}\n";
		}

	}

	@Override
	public void visit(AbstractCodeElement element, int index) {
		if (index == 0) {
			currentSource += element.getName();
		}
	}

	@Override
	public void visit(SemaphorePend element, int index) {
		if (index == 0) {
			currentSource += element.getName() + "(";
		} else if (index == 1) {
			currentSource += ");";
		}
	}

	@Override
	public void visit(SemaphorePost element, int index) {
		if (index == 0) {
			currentSource += element.getName() + "(";
		} else if (index == 1) {
			currentSource += ");";
		}
	}

	@Override
	public void visit(UserFunctionCall element, int index) {
		currentSource += element.getName() + "(";
		
		ConcurrentSkipListSet<Buffer> listset = new ConcurrentSkipListSet<Buffer>(new AlphaOrderComparator());
		listset.addAll(element.getAvailableBuffers());
		Iterator<Buffer> iterator = listset.iterator();
		
		while(iterator.hasNext()){
			Buffer buf = iterator.next();
			
			visit(buf,0); // Accept the code container
			
			if(iterator.hasNext())
				currentSource += ",";
		}
	
		currentSource += ");";
	}

	@Override
	public void visit(CommunicationFunctionCall element, int index) {
	}

	@Override
	public void visit(Buffer element, int index) {
		currentSource += element.getName();

	}

	@Override
	public void visit(Semaphore element, int index) {
		currentSource += "sem[" + element.getSemaphoreNumber() + "], "
				+ element.getSemaphoreType().toString();
	}

	@Override
	public void visit(Receive element, int index) {
		
		if (index == 0) {
			currentSource += element.getName() + "(" + element.getSource().getName() + ",";
		} else if (index == 1) {
			currentSource += ");";
		}

	}

	@Override
	public void visit(Send element, int index) {
		if (index == 0) {
			currentSource += element.getName() + "(" + element.getTarget().getName() + ",";
		} else if (index == 1) {
			currentSource += ");";
		}
	}

}
