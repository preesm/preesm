package org.ietr.preesm.core.codegen;

import java.util.ArrayList;
import java.util.List;

import org.ietr.preesm.core.codegen.printer.AbstractPrinter;
import org.sdf4j.model.AbstractVertex;

public class CompoundCodeElement implements ICodeElement{

	private List<ICodeElement> calls ;
	private String name ;
	private AbstractBufferContainer parentContainer ;
	private AbstractVertex<?> correspondingVertex ;
	
	
	public CompoundCodeElement(String name,AbstractBufferContainer parentContainer, AbstractVertex<?> correspondingVertex) {
		this.name = name;
		this.parentContainer = parentContainer;
		this.correspondingVertex = correspondingVertex;
		calls = new ArrayList<ICodeElement>() ;
	}
	
	@Override
	public void accept(AbstractPrinter printer) {
		for(ICodeElement call : calls){
			call.accept(printer);
		}
	}

	@Override
	public AbstractVertex<?> getCorrespondingVertex() {
		return correspondingVertex;
	}
	
	public void addCall(ICodeElement elt){
		calls.add(elt);
	}

}
