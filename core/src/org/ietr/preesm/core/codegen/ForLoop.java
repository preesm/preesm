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

import org.ietr.preesm.core.codegen.UserFunctionCall.CodeSection;
import org.ietr.preesm.core.codegen.model.CodeGenSDFVertex;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

/**
 * Each thread runs indefinitely. It contains a for loop. Thanks to SDF
 * transformation, for loops can be generated to reduce the overall length of
 * the code.
 * 
 * @author mwipliez
 * @author mpelcat
 */
public class ForLoop extends AbstractBufferContainer implements ICodeElement {

	private HashMap<SDFEdge, SubBuffer> allocatedBuffers;

	private SDFAbstractVertex correspondingVertex;

	private Variable index;
	
	private ICodeElement content ;

	private AbstractBufferContainer parentContainer;

	public ForLoop(AbstractBufferContainer parentContainer,
			CodeGenSDFVertex correspondingVertex) {
		super(parentContainer);
		index = new Variable("i", new DataType("long"));
		allocatedBuffers = new HashMap<SDFEdge, SubBuffer>();
		this.parentContainer = parentContainer;
		this.correspondingVertex = correspondingVertex;
		for (SDFEdge edge : correspondingVertex.getBase().outgoingEdgesOf(
				correspondingVertex)) {
			this.addBuffer(new SubBuffer("sub", edge.getProd().intValue(), index, parentContainer.getBuffer(edge)), edge);
		}
		for (SDFEdge edge : correspondingVertex.getBase().incomingEdgesOf(
				correspondingVertex)) {
			this.addBuffer(new SubBuffer("sub", edge.getProd().intValue(), index, parentContainer.getBuffer(edge)), edge);
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

	public Buffer getBuffer(SDFEdge edge) {
		if (super.getBuffer(edge) == null) {
			return allocatedBuffers.get(edge);
		} else {
			return super.getBuffer(edge);
		}
	}

	@Override
	public void accept(IAbstractPrinter printer, Object currentLocation) {

	}

	@Override
	public SDFAbstractVertex getCorrespondingVertex() {
		return correspondingVertex;
	}

	public String getName() {
		return "for";
	}

	public AbstractBufferContainer getParentContainer() {
		return parentContainer;
	}

}
