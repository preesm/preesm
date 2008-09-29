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
package org.ietr.preesm.core.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ietr.preesm.core.codegen.printer.AbstractPrinter;
import org.sdf4j.model.AbstractEdge;
import org.sdf4j.model.AbstractGraph;
import org.sdf4j.model.AbstractVertex;

public class FiniteForLoop extends AbstractBufferContainer implements
		ICodeElement {

	@SuppressWarnings("unchecked")
	private Map<AbstractEdge, Buffer> buffers ;
	
	private int startIndex ;
	private int stopIndex;
	private int increment;
	private LoopIndex index ;
	private AbstractVertex<?> vertexDescription ;
	private List<ICodeElement> calls ;
	
	@SuppressWarnings("unchecked")
	public FiniteForLoop(AbstractBufferContainer parentContainer, AbstractVertex vertex, int nbIter) {
		super(parentContainer);
		startIndex = 0 ;
		stopIndex = nbIter ;
		increment = 1;
		vertexDescription = vertex ;
		calls = new ArrayList<ICodeElement>() ;
		buffers = new HashMap<AbstractEdge, Buffer>();
		for(VariableAllocation varDecl : parentContainer.getVariables()){
			if(varDecl.getVariable() instanceof LoopIndex){
				index = (LoopIndex) varDecl.getVariable();
			}
		}
		if(index == null){
			if(parentContainer instanceof FiniteForLoop){
				char indexName = ((FiniteForLoop) parentContainer).getIndex().getNameAsChar();
				indexName = (char) ((int) indexName ++);
				index = new LoopIndex(indexName, new DataType("int"));
			}else{
				index = new LoopIndex('i', new DataType("int"));
				parentContainer.addVariable(index);
			}
		}
		for(AbstractEdge edge : ((AbstractVertex<AbstractGraph<AbstractVertex, AbstractEdge>>) vertexDescription).getBase().edgesOf(vertex)){
			for(Buffer buf : parentContainer.getBuffers(edge)){
				SubBuffer subBuff = new SubBuffer(buf.getName(), buf.getSize(), buf.getType(), buf.getAggregate()) ;
				subBuff.setParentBuffer(buf);
				subBuff.setIndex(index);
				this.addBuffer(subBuff, edge);
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Set<Buffer> getBuffers(AbstractEdge edge) {
		Set<Buffer> buffer = new TreeSet<Buffer>() ;
		buffer.add(buffers.get(edge));
		return buffer ;
	}
	
	@SuppressWarnings("unchecked")
	public void addBuffer(Buffer buffer, AbstractEdge edge){
		buffers.put(edge, buffer);
	}

	@Override
	public void accept(AbstractPrinter printer) {
		Iterator<VariableAllocation> iterator2 = variables.iterator();

		while (iterator2.hasNext()) {
			VariableAllocation alloc = iterator2.next();
			alloc.accept(printer); // Accepts allocations
		}
		printer.visit(this, 1);
		for(ICodeElement call : calls){
			call.accept(printer);
		}
		printer.visit(this, 2);
	}

	@Override
	public AbstractVertex<?> getCorrespondingVertex() {
		return vertexDescription;
	}
	
	/**
	 * Sets the loop start index
	 * @param start The start index of the loop
	 */
	public void setStartIndex(int start){
		this.startIndex = start ;
	}
	
	/**
	 * Sets the stop index of the loop
	 * @param stop The stop index of the loop
	 */
	public void setStopIndex(int stop){
		this.stopIndex = stop ;
	}
	
	/**
	 * Sets the increment of the loop
	 * @param inc The increment of the loop
	 */
	public void setIncrement(int inc){
		this.increment = inc ;
	}
	
	public void addCall(ICodeElement call){
		calls.add(call);
	}
	
	public int getStartIndex(){
		return startIndex ;
	}
	public int getStopIndex(){
		return stopIndex ;
	}
	
	public int getIncrement(){
		return increment ;
	}
	
	public LoopIndex getIndex(){
		return index ;
	}
	
	public String toString(){
		String result = new String() ;
		result += "for(int i ="+startIndex+" ; i < "+stopIndex+" ; i +="+increment+" ){\n";
		for(ICodeElement call : calls){
			result += call.toString()+";\n";
		}
		result +=" }";
		return result ;
	}

}
