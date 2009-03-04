/**
 * 
 */
package org.ietr.preesm.core.scenario;

import java.util.HashMap;
import java.util.Map;

import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * Handles code generation parameters
 * 
 * @author mpelcat
 */
public class CodegenManager {

	/**
	 * Directory in which generated code is stored
	 */
	private String codegenDirectory;


	public CodegenManager() {
		super();

		codegenDirectory = new String();
	}
	

	public String getCodegenDirectory() {
		return codegenDirectory;
	}

	public void setCodegenDirectory(String codegenDirectory) {
		this.codegenDirectory = codegenDirectory;
	}

}
