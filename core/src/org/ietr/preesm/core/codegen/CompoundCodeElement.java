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
import java.util.List;

import org.ietr.preesm.core.codegen.model.CodeGenSDFGraph;
import org.ietr.preesm.core.codegen.model.CodeGenSDFVertex;
import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;
import org.sdf4j.model.sdf.SDFInterfaceVertex;
import org.sdf4j.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.sdf4j.model.sdf.esdf.SDFSourceInterfaceVertex;

public class CompoundCodeElement extends AbstractBufferContainer implements ICodeElement {

	private List<ICodeElement> calls;
	
	private HashMap<SDFEdge, Buffer> allocatedBuffers ;

	private SDFAbstractVertex correspondingVertex;

	private String name;

	private AbstractBufferContainer parentContainer;

	
	public CompoundCodeElement(String name,
			AbstractBufferContainer parentContainer,
			CodeGenSDFVertex correspondingVertex) {
		super(parentContainer);
		allocatedBuffers = new HashMap<SDFEdge, Buffer>();
		this.name = name;
		this.parentContainer = parentContainer;
		this.correspondingVertex = correspondingVertex;
		calls = new ArrayList<ICodeElement>();
		if(correspondingVertex.getGraphDescription() != null){
			CodeGenSDFGraph graph = (CodeGenSDFGraph) correspondingVertex.getGraphDescription();
			for(SDFEdge edge : graph.edgeSet()){
				if(edge.getSource() instanceof SDFSourceInterfaceVertex){
					SDFEdge outEdge = correspondingVertex.getAssociatedEdge((SDFSourceInterfaceVertex) edge.getSource());
					Buffer parentBuffer = parentContainer.getBuffer(outEdge);
					this.addBuffer(parentBuffer, edge);
				}else if(edge.getTarget() instanceof SDFSinkInterfaceVertex){
					SDFEdge outEdge = correspondingVertex.getAssociatedEdge((SDFSinkInterfaceVertex) edge.getTarget());
					Buffer parentBuffer = parentContainer.getBuffer(outEdge);
					this.addBuffer(parentBuffer, edge);
				}else{
					String bufferName = edge.getSourceInterface().getName()+"_"+edge.getTargetInterface().getName();
					this.addBuffer(new BufferAllocation(new Buffer(bufferName, Math.max(edge.getProd().intValue(), edge.getCons().intValue()), new DataType(edge.getDataType().toString()),edge)));
				}
			}
			for(SDFAbstractVertex vertex : graph.vertexSet()){
				if(!(vertex instanceof SDFInterfaceVertex)){
					this.addCall(CodeElementFactory.createElement(vertex.getName(), this, vertex));
				}
			}
		}
	}

	public void addBuffer(Buffer buff, SDFEdge edge){
		if(allocatedBuffers.get(edge) == null){
			allocatedBuffers.put(edge, buff);
		}
	}
	
	public Buffer getBuffer(SDFEdge edge){
		if(allocatedBuffers.get(edge) == null){
			return super.getBuffer(edge);
		}else{
			return allocatedBuffers.get(edge) ;
		}
	}
	
	@Override
	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation);
		for(Buffer buff : allocatedBuffers.values()){
			if(buff != null){
				buff.accept(printer, currentLocation);
			}else{
				System.out.println();
			}
		}
		for (ICodeElement call : calls) {
			call.accept(printer, currentLocation);
		}
	}

	public void addCall(ICodeElement elt) {
		calls.add(elt);
	}

	@Override
	public SDFAbstractVertex getCorrespondingVertex() {
		return correspondingVertex;
	}

	public String getName() {
		return name;
	}

	public AbstractBufferContainer getParentContainer() {
		return parentContainer;
	}

}
