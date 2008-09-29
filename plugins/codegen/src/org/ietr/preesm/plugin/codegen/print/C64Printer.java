/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-B license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-B
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
knowledge of the CeCILL-B license and that you accept its terms.
 *********************************************************/


/**
 * 
 */
package org.ietr.preesm.plugin.codegen.print;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

import org.ietr.preesm.core.codegen.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.AbstractCodeContainer;
import org.ietr.preesm.core.codegen.AbstractCodeElement;
import org.ietr.preesm.core.codegen.Buffer;
import org.ietr.preesm.core.codegen.BufferAllocation;
import org.ietr.preesm.core.codegen.CommunicationFunctionCall;
import org.ietr.preesm.core.codegen.FiniteForLoop;
import org.ietr.preesm.core.codegen.ForLoop;
import org.ietr.preesm.core.codegen.LinearCodeContainer;
import org.ietr.preesm.core.codegen.Receive;
import org.ietr.preesm.core.codegen.Semaphore;
import org.ietr.preesm.core.codegen.SemaphorePend;
import org.ietr.preesm.core.codegen.SemaphorePost;
import org.ietr.preesm.core.codegen.Send;
import org.ietr.preesm.core.codegen.SourceFile;
import org.ietr.preesm.core.codegen.SubBuffer;
import org.ietr.preesm.core.codegen.ThreadDeclaration;
import org.ietr.preesm.core.codegen.UserFunctionCall;
import org.ietr.preesm.core.codegen.VariableAllocation;
import org.ietr.preesm.core.codegen.printer.AbstractPrinter;

/**
 * Visitor that generates C code from source files for a C64 target
 * 
 * @author mpelcat
 */
public class C64Printer extends AbstractPrinter {

	
	public C64Printer() {
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
		}if (index == 1) {
			currentSource += "\n//Variables allocation for " + element.getName()
			+ "\n";
		} 
		else if (index == 2) {
			currentSource += "\n";
		} else if (index == 3) {
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
		}
		else if (index == 0) {
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
		if(element.getParentContainer() instanceof FiniteForLoop){
			for(int i = 0 ; i < index+1 ; i ++){
				currentSource += "\t";
			}
		}
		currentSource += element.getName() + "(";
		
		ConcurrentSkipListSet<Buffer> listset = new ConcurrentSkipListSet<Buffer>(new AlphaOrderComparator());
		listset.addAll(element.getAvailableBuffers());
		Iterator<Buffer> iterator = listset.iterator();
		
		while(iterator.hasNext()){
			Buffer buf = iterator.next();
			if(buf instanceof SubBuffer){
				visit((SubBuffer) buf,0); // Accept the code container
			}else{
				visit(buf,0); // Accept the code container
			}
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

	@Override
	public void visit(FiniteForLoop element, int index) {
		if (index == 1) {
			currentSource +="for("+element.getIndex()+" = "+element.getStartIndex()+"; "+element.getIndex()+" < "+element.getStopIndex()+" ; "+element.getIndex()+" += "+element.getIncrement()+"){\n";
		} else if (index == 2) {
			currentSource += "\n\t}";
		}
	}

	@Override
	public void visit(SubBuffer element, int index) {
		Buffer topElement = element ;
		List<SubBuffer> hierarchy = new ArrayList<SubBuffer>() ;
		while(topElement instanceof SubBuffer){
			hierarchy.add((SubBuffer)topElement);
			topElement = ((SubBuffer)topElement).getParentBuffer();
		}
		currentSource += topElement.getName();
		for(int i = hierarchy.size()-1 ; i >= 0 ; i --){
			currentSource += "["+hierarchy.get(i).getIndex()+"] ";
		}
	}


	@Override
	public void visit(VariableAllocation element, int index) {
		currentSource += element.getVariable().getType().getTypeName();
		currentSource += " ";
		currentSource += element.getVariable().toString();
		currentSource += ";";
	}


}
