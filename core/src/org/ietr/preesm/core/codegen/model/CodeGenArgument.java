package org.ietr.preesm.core.codegen.model;

/**
 * Representation of a buffer in a prototype
 * 
 * @author jpiat
 * @author mpelcat
 */
public class CodeGenArgument {
	
	private String type ;
	private String name ;
	
	public static final String INPUT = "INPUT";
	public static final String OUTPUT = "OUTPUT";
	public static final String INOUT = "INOUT";
	private String direction ;
	
	public CodeGenArgument(String name, String direction){
		this.name = name ;
		this.direction = direction;
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
	
	public String getDirection() {
		return direction;
	}
}
