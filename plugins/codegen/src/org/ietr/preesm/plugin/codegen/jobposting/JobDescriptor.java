/**
 * 
 */
package org.ietr.preesm.plugin.codegen.jobposting;

import java.util.ArrayList;
import java.util.List;

import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.calls.Constant;

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
	private List<Buffer> buffers = null;
	private List<Constant> constants = null;
	private List<JobDescriptor> prededessors = null;

	public JobDescriptor() {
		buffers = new ArrayList<Buffer>();
		constants = new ArrayList<Constant>();
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

	public void addBuffer(Buffer b) {
		buffers.add(b);
	}

	public void addConstant(Constant c) {
		constants.add(c);
	}

	public List<Buffer> getBuffers() {
		return buffers;
	}

	public List<Constant> getConstants() {
		return constants;
	}

}
