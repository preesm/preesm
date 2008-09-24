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
import org.ietr.preesm.core.codegen.AbstractPrinter;
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
import org.jgrapht.traverse.AbstractGraphIterator;

/**
 * Visitor that generates C code from source files for a C64 target
 * 
 * @author mpelcat
 */
public class C64Printer extends AbstractPrinter {

	public C64Printer(String directory) {
		super();
		this.directory = directory;
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
	
	/**
	 * The key is the name of the current operator. The value is the code for
	 * this operator
	 */
	private Map<String, String> sourceMap = null;

	/**
	 * The current source being filled
	 */
	private String currentSource = null;

	/**
	 * Directory in which we should put the generated files
	 */
	private String directory = "d:\\";

	@Override
	public void visit(SourceFileList element, int index) {

		if (index == 0) {
			// Initializes the map
			sourceMap = new HashMap<String, String>();
		} else if (index == 1) {
			// Prints the files
			Iterator<String> it = sourceMap.keySet().iterator();

			Path path = new Path(directory);
			
			while (it.hasNext()) {
				String key = it.next();
				String value = sourceMap.get(key);

				
				try {
					PrintWriter writer = new PrintWriter(new FileOutputStream(
							new File(path.toString() + "\\" + key + ".c")), true);
					writer.print(value);
					writer.flush();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void visit(SourceFile element, int index) {
		if (index == 0) {
			currentSource = new String();
			
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
			sourceMap.put(element.getName(), currentSource);
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
	public void visit(CodeElement element, int index) {
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
