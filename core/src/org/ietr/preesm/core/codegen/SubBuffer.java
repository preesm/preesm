package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.codegen.sdfProperties.BufferAggregate;

public class SubBuffer extends Buffer{
	
	private Buffer parentBuffer ;
	private String index ;

	public SubBuffer(String name, Integer size, DataType type,
			BufferAggregate aggregate) {
		super(name, size, type, aggregate);
	}
	
	public void setParentBuffer(Buffer parentBuffer){
		this.parentBuffer = parentBuffer ;
	}
	
	public Buffer getParentBuffer(){
		return parentBuffer ;
	}
	
	public void setIndex(String index){
		this.index = index ;
	}
	
	public String getIndex(){
		return index ;
	}

}
