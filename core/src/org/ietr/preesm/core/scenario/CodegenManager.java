/**
 * 
 */
package org.ietr.preesm.core.scenario;

import java.util.HashMap;
import java.util.Map;

import org.ietr.preesm.core.codegen.DataType;
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * Handles code generation parameters
 * 
 * @author mpelcat
 */
public class CodegenManager {

	/**
	 * Names of the data types with their size
	 */
	private Map<SDFAbstractVertex,CodegenVertexPhases> codegenPhases;
	
	

	public CodegenManager() {
		super();

		codegenPhases = new HashMap<SDFAbstractVertex, CodegenVertexPhases>();
	}

	public Map<SDFAbstractVertex, CodegenVertexPhases> getCodegenPhases() {
		return codegenPhases;
	}

}
