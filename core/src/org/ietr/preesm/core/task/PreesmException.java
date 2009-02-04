package org.ietr.preesm.core.task;

public class PreesmException extends Exception{

	private String message;
	/**
	 * 
	 */
	private static final long serialVersionUID = 620727777815265095L;
	
	public PreesmException(String message){
		this.message = message ;
	}
	
	public String toString(){
		return message ;
	}

}
