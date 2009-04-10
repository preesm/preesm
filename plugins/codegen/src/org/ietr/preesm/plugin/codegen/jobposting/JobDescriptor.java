/**
 * 
 */
package org.ietr.preesm.plugin.codegen.jobposting;

import java.util.ArrayList;
import java.util.List;

/**
 * Every data necessary to launch a job
 * @author mpelcat
 */
public class JobDescriptor {
	
	private String vertexName = "";
	
	private int id = 0;
	private int time = 0;
	private String functionName = "";
	private List<String> bufferNames = null;
	private List<Integer> prededessorIds = null;

	public JobDescriptor() {
		bufferNames = new ArrayList<String>();
		prededessorIds = new ArrayList<Integer>();
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public void setVertexName(String vertexName) {
		this.vertexName = vertexName;
	}

	public void setPrededessorIds(List<Integer> prededessorIds) {
		this.prededessorIds = prededessorIds;
	}

	public String getVertexName() {
		return vertexName;
	}

	public int getId() {
		return id;
	}

	public String getFunctionName() {
		return functionName;
	}
	
	
}
