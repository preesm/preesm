package org.ietr.preesm.core.codegen.model;

public class CodeGenArgument {
	private String type ;
	private String name ;
	
	public CodeGenArgument(String name){
		this.name = name ;
	}
	
	public void setType(String type){
		this.type = type ;
	}
	
	public String getName(){
		return name ;
	}
	
	public String getType(){
		return type ;
	}
}
