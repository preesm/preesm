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

import java.util.HashMap;
import java.util.Iterator;

import org.ietr.preesm.core.codegen.UserFunctionCall.CodeSection;
import org.ietr.preesm.core.codegen.model.CodeGenSDFVertex;
import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

public class FiniteForLoop extends AbstractBufferContainer implements ICodeElement {

	private HashMap<SDFEdge, SubBuffer> allocatedBuffers;

	private SDFAbstractVertex correspondingVertex;

	private LoopIndex index;
	
	private ICodeElement content ;

	private AbstractBufferContainer parentContainer;

	public FiniteForLoop(AbstractBufferContainer parentContainer,
			CodeGenSDFVertex correspondingVertex) {
		super(parentContainer);
		AbstractBufferContainer parentLoop = parentContainer ;
		while(parentLoop != null && !(parentLoop instanceof FiniteForLoop)){
			parentLoop = parentLoop.getParentContainer();
		}
		if(parentLoop != null && parentLoop instanceof FiniteForLoop){
			char newIndex = (char) (((int)((FiniteForLoop) parentLoop).getIndex().getNameAsChar()) + 1);
			index = new LoopIndex( newIndex, new DataType("long"));
		}else{
			index = new LoopIndex('i', new DataType("long"));
		}
		allocatedBuffers = new HashMap<SDFEdge, SubBuffer>();
		this.parentContainer = parentContainer;
		this.correspondingVertex = correspondingVertex;
		for (SDFEdge edge : correspondingVertex.getBase().outgoingEdgesOf(
				correspondingVertex)) {
			AbstractBufferContainer parentBufferContainer = parentContainer ;
			while(parentBufferContainer != null && parentBufferContainer.getBuffer(edge)==null){
				parentBufferContainer = parentBufferContainer.getParentContainer();
			}if(parentBufferContainer != null ){
				this.addBuffer(new SubBuffer("sub_"+index.getName()+"_"+parentBufferContainer.getBuffer(edge).getName(), edge.getProd().intValue(), index, parentBufferContainer.getBuffer(edge)), edge);
			}
		}
		for (SDFEdge edge : correspondingVertex.getBase().incomingEdgesOf(
				correspondingVertex)) {
			AbstractBufferContainer parentBufferContainer = parentContainer ;
			while(parentBufferContainer != null && parentBufferContainer.getBuffer(edge)==null){
				parentBufferContainer = parentBufferContainer.getParentContainer();
			}if(parentBufferContainer != null ){
				this.addBuffer(new SubBuffer("sub_"+index.getName()+"_"+parentBufferContainer.getBuffer(edge).getName(), edge.getProd().intValue(), index, parentBufferContainer.getBuffer(edge)), edge);
			}
		}
		if(correspondingVertex.getGraphDescription() != null){
			content = new CompoundCodeElement(correspondingVertex.getName(), this, correspondingVertex);
		}else{
			content = new UserFunctionCall(correspondingVertex, this, CodeSection.LOOP);
		}
	}
	public ICodeElement getContent(){
		return content ;
	}

	public void addBuffer(SubBuffer buff, SDFEdge edge) {
		if (allocatedBuffers.get(edge) == null) {
			allocatedBuffers.put(edge, buff);
		}
	}

	public int getNbIteration(){
		return correspondingVertex.getNbRepeat();
	}
	
	public Buffer getBuffer(SDFEdge edge) {
		if (super.getBuffer(edge) == null) {
			return allocatedBuffers.get(edge);
		} else {
			return super.getBuffer(edge);
		}
	}

	@Override
	public SDFAbstractVertex getCorrespondingVertex() {
		return correspondingVertex;
	}

	public AbstractBufferContainer getParentContainer() {
		return parentContainer;
	}
	@Override
	public void accept(IAbstractPrinter printer, Object currentLocation) {
		Iterator<VariableAllocation> iterator2 = variables.iterator();
		while (iterator2.hasNext()) {
			VariableAllocation alloc = iterator2.next();
			alloc.accept(printer, currentLocation); // Accepts allocations
		}
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation);
		content.accept(printer, currentLocation);
	}

	public LoopIndex getIndex() {
		return index;
	}

	/**
	 * Returns the name of this finite for loop.
	 * 
	 * @return <code>""</code>.
	 */
	@Override
	public String getName() {
		return "for";
	}

	public String toString() {
		return "";
	}

}
