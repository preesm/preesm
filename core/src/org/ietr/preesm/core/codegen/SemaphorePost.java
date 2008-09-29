/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

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


/**
 * 
 */
package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.codegen.printer.AbstractPrinter;
import org.ietr.preesm.core.codegen.sdfProperties.BufferAggregate;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;

/**
 * Special function call posting a semaphore
 * 
 * @author mpelcat
 */
public class SemaphorePost extends AbstractCodeElement {

	private Semaphore semaphore;
	
	/**
	 * Creates a semaphore post function to protect the data transmitted by a communication vertex.
	 * protectCom = true means that the pending function is being put in the communication thread.
	 * protectCom = false means that the pending function is being put in the computation thread
	 * to protect the sender and receiver function calls
	 */
	public SemaphorePost(AbstractBufferContainer globalContainer, DAGVertex vertex, SemaphoreType semType) {
		super("semaphorePost", globalContainer, vertex);

		SemaphoreContainer semContainer = globalContainer.getSemaphoreContainer();
		
		DAGEdge outEdge = (DAGEdge)(vertex.getBase().outgoingEdgesOf(vertex).toArray()[0]);
		BufferAggregate agg = (BufferAggregate)outEdge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
		
		semaphore = semContainer.createSemaphore(agg,semType);
	}
	
	public void accept(AbstractPrinter printer) {
		printer.visit(this,0); // Visit self
		semaphore.accept(printer); // Accept the code container
		printer.visit(this,1); // Visit self
	}
	
	public Semaphore getSemaphore() {
		return semaphore;
	}
	
	/**
	 * Displays pseudo-code for test
	 */
	public String toString() {
		
		String code =  super.getName();
		
		code += "(" + semaphore.toString() + ");";
		
		return code;
	}
}
