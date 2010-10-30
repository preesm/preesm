/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
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
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.core.codegen.printer;

import org.ietr.preesm.core.codegen.AbstractCodeElement;
import org.ietr.preesm.core.codegen.Assignment;
import org.ietr.preesm.core.codegen.SourceFile;
import org.ietr.preesm.core.codegen.VariableAllocation;
import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.buffer.BufferAllocation;
import org.ietr.preesm.core.codegen.buffer.BufferAtIndex;
import org.ietr.preesm.core.codegen.buffer.SubBuffer;
import org.ietr.preesm.core.codegen.buffer.SubBufferAllocation;
import org.ietr.preesm.core.codegen.calls.Constant;
import org.ietr.preesm.core.codegen.calls.PointerOn;
import org.ietr.preesm.core.codegen.calls.SpecialBehaviorCall;
import org.ietr.preesm.core.codegen.calls.UserFunctionCall;
import org.ietr.preesm.core.codegen.calls.Variable;
import org.ietr.preesm.core.codegen.com.CommunicationFunctionCall;
import org.ietr.preesm.core.codegen.com.CommunicationFunctionInit;
import org.ietr.preesm.core.codegen.com.ReceiveDma;
import org.ietr.preesm.core.codegen.com.ReceiveMsg;
import org.ietr.preesm.core.codegen.com.SendDma;
import org.ietr.preesm.core.codegen.com.SendMsg;
import org.ietr.preesm.core.codegen.com.WaitForCore;
import org.ietr.preesm.core.codegen.containers.AbstractCodeContainer;
import org.ietr.preesm.core.codegen.containers.CompoundCodeElement;
import org.ietr.preesm.core.codegen.containers.FiniteForLoop;
import org.ietr.preesm.core.codegen.containers.ForLoop;
import org.ietr.preesm.core.codegen.containers.LinearCodeContainer;
import org.ietr.preesm.core.codegen.semaphore.Semaphore;
import org.ietr.preesm.core.codegen.semaphore.SemaphoreInit;
import org.ietr.preesm.core.codegen.semaphore.SemaphorePend;
import org.ietr.preesm.core.codegen.semaphore.SemaphorePost;
import org.ietr.preesm.core.codegen.threads.LaunchThread;
import org.ietr.preesm.core.codegen.threads.ThreadDeclaration;


/**
 * Visitor that prints the code using given rules
 * 
 * @author mpelcat
 */
public interface IAbstractPrinter {

	public Object visit(AbstractBufferContainer element, CodeZoneId index, Object currentLocation);
	public Object visit(AbstractCodeContainer element, CodeZoneId index, Object currentLocation);
	public Object visit(Buffer element, CodeZoneId index, Object currentLocation);
	public Object visit(Constant element, CodeZoneId index, Object currentLocation);
	public Object visit(SubBuffer element, CodeZoneId index, Object currentLocation);
	public Object visit(BufferAtIndex element, CodeZoneId index, Object currentLocation);
	public Object visit(BufferAllocation element, CodeZoneId index, Object currentLocation);
	public Object visit(SubBufferAllocation element, CodeZoneId index, Object currentLocation);
	public Object visit(VariableAllocation element, CodeZoneId index, Object currentLocation);
	public Object visit(Variable element, CodeZoneId index, Object currentLocation);
	public Object visit(AbstractCodeElement element, CodeZoneId index, Object currentLocation);
	public Object visit(CommunicationFunctionCall element, CodeZoneId index, Object currentLocation);
	public Object visit(CommunicationFunctionInit element, CodeZoneId index, Object currentLocation);
	public Object visit(ForLoop element, CodeZoneId index, Object currentLocation);
	public Object visit(LinearCodeContainer element, CodeZoneId index, Object currentLocation);
	public Object visit(FiniteForLoop element, CodeZoneId index, Object currentLocation);
	public Object visit(CompoundCodeElement element, CodeZoneId index, Object currentLocation);
	public Object visit(Semaphore element, CodeZoneId index, Object currentLocation);
	public Object visit(SemaphorePend element, CodeZoneId index, Object currentLocation);
	public Object visit(SemaphorePost element, CodeZoneId index, Object currentLocation);
	public Object visit(SemaphoreInit element, CodeZoneId index, Object currentLocation);
	public Object visit(SendMsg element, CodeZoneId index, Object currentLocation);
	public Object visit(ReceiveMsg element, CodeZoneId index, Object currentLocation);
	public Object visit(SendDma element, CodeZoneId index, Object currentLocation);
	public Object visit(ReceiveDma element, CodeZoneId index, Object currentLocation);
	public Object visit(SourceFile element, CodeZoneId index, Object currentLocation);
	public Object visit(ThreadDeclaration element, CodeZoneId index, Object currentLocation);
	public Object visit(LaunchThread element, CodeZoneId index, Object currentLocation);
	public Object visit(UserFunctionCall element, CodeZoneId index, Object currentLocation);
	public Object visit(SpecialBehaviorCall element, CodeZoneId index, Object currentLocation);
	public Object visit(WaitForCore element, CodeZoneId index, Object currentLocation);
	public Object visit(Assignment element, CodeZoneId index, Object currentLocation);
	public Object visit(PointerOn element, CodeZoneId index, Object currentLocation);
}
