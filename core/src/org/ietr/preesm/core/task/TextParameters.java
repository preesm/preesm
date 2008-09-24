package org.ietr.preesm.core.task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ietr.preesm.core.workflow.TaskNode;

/**
 * Store textual parameters in a map transmitted to the transformations.
 * 
 * @author mpelcat
 */
public class TextParameters {

	/**
	 * Variables retrieved form {@link TaskNode} and 
	 * used to parameterize the transformation.
	 */
	private Map<String,String> variables = null;
	
	public TextParameters() {
		super();
		this.variables = new HashMap<String,String>();
	}
	
	public TextParameters(Map<String, String> variables) {
		super();
		this.variables = variables;
	}

	public void addVariable(String key, boolean value) {
		Boolean bool = new Boolean(value);
		variables.put(key, bool.toString());
	}

	public void addVariable(String key, int value) {
		variables.put(key, String.format("%d", value));
	}

	/**
	 * Creates a new variable.
	 * 
	 * @param key 
	 * 				the name of the variable.
	 * @param value 
	 * 				the value of the variable.
	 */
	public void addVariable(String key, String value) {
		variables.put(key, value);
	}
	
	/**
	 * Retrieves the variable as a boolean.
	 */
	public boolean getBooleanVariable(String key) {
		
		if(variables.containsKey(key)){
			return Boolean.valueOf(variables.get(key));
		}
		
		return false;
	}
	
	/**
	 * Retrieves the variable as an integer.
	 */
	public int getIntVariable(String key) {
		
		if(variables.containsKey(key)){
			return Integer.valueOf(variables.get(key));
		}
		
		return 0;
	}
	
	/**
	 * Retrieves the variable as a string.
	 */
	public String getVariable(String key) {
		
		if(variables.containsKey(key)){
			return variables.get(key);
		}
		
		return "";
	}
	
	public boolean hasVariable(String key) {
		return variables.containsKey(key);
	}

	/**
	 * Replaces the environment variables by their values
	 */
	public void resolveEnvironmentVars(Map<String,String> envVars) {
		
		Iterator<String> localVarIterator = variables.keySet().iterator();
		
		while(localVarIterator.hasNext()){
			String localVarKey = localVarIterator.next();
			String localVarValue = variables.get(localVarKey);
			
			Iterator<String> envVarIterator = envVars.keySet().iterator();
			
			while(envVarIterator.hasNext()){
				String envVarKey = envVarIterator.next();
				String envVarValue = envVars.get(envVarKey);
				
				localVarValue = localVarValue.replace("${"+envVarKey+"}",envVarValue);
				variables.put(localVarKey, localVarValue);
			}
		}
	}
}
