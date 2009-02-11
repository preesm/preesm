package org.ietr.preesm.plugin.codegen.model;

import java.util.ArrayList;
import java.util.List;

public class FunctionCall {

	private String functionName;

	private List<String> inputs;
	private List<String> outputs;
	private List<String> parameters;

	public FunctionCall(String name) {
		functionName = name;
		inputs = new ArrayList<String>();
		outputs = new ArrayList<String>();
		parameters = new ArrayList<String>();
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String name) {
		functionName = name;
	}

	public void addInput(String inputName) {
		inputs.add(inputName);
	}

	public void addOutput(String outputName) {
		outputs.add(outputName);
	}
	
	public void addParameter(String parameterName) {
		parameters.add(parameterName);
	}
	
}
