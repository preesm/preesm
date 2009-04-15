/**
 * 
 */
package org.ietr.preesm.plugin.codegen.jobposting;

import java.util.ArrayList;
import java.util.List;

/**
 * Every data necessary to launch a job
 * 
 * @author mpelcat
 */
public class JobDescriptor {

	private String vertexName = "";

	private int id = 0;
	private int time = 0;
	private String functionName = "";
	private List<String> bufferNames = null;
	private List<JobDescriptor> prededessors = null;

	public JobDescriptor() {
		bufferNames = new ArrayList<String>();
		prededessors = new ArrayList<JobDescriptor>();
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

	public String getVertexName() {
		return vertexName;
	}

	public int getId() {
		return id;
	}

	public int getTime() {
		return time;
	}

	public String getFunctionName() {
		return functionName;
	}

	public JobDescriptor getPredecessor(int id) {
		for (JobDescriptor pred : prededessors) {
			if (pred.getId() == id) {
				return pred;
			}
		}
		return null;
	}

	public void addPredecessor(JobDescriptor pred) {
		if (getPredecessor(pred.getId()) == null) {
			prededessors.add(pred);
		}
	}

	public List<JobDescriptor> getPrededessors() {
		return prededessors;
	}

}
