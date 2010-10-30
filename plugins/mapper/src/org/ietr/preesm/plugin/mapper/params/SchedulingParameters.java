/**
 * 
 */
package org.ietr.preesm.plugin.mapper.params;

import org.ietr.preesm.core.task.TextParameters;

/**
 * Parameters that apply to any type of scheduling algorithms
 * 
 * @author mpelcat
 */
public class SchedulingParameters {
	
	/**
	 * Path where to export the current DAG. The exported Directed Acyclic Graph
	 * is the input of the mapping process. This DAG may be different
	 * from the SDF graph that can be exported after graph transformations because
	 * special vertices are added during the SDF to DAG transformation that
	 * happens at the beginning of the workflow mapping transformation.
	 */
	private String dagExportPath = null;
	
	public SchedulingParameters(TextParameters textParameters) {
		this.dagExportPath = textParameters.getVariable("dagExportPath");
	}

	public SchedulingParameters(String dagExportPath) {
		this.dagExportPath = dagExportPath;
	}

	public String getDagExportPath() {
		return dagExportPath;
	}
}
