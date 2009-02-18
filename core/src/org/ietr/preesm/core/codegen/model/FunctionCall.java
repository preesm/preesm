package org.ietr.preesm.core.codegen.model;

import java.util.ArrayList;
import java.util.List;

import org.sdf4j.model.IRefinement;

public class FunctionCall implements IRefinement {

	private String functionName;
	public FunctionCall initCall = null;
	public FunctionCall endCall = null;

	private List<CodeGenArgument> inputs;
	private List<CodeGenArgument> outputs;
	private List<CodeGenParameter> parameters;

	public FunctionCall(){
		functionName = "";
		inputs = new ArrayList<CodeGenArgument>();
		outputs = new ArrayList<CodeGenArgument>();
		parameters = new ArrayList<CodeGenParameter>();
	}
	
	public FunctionCall(String name) {
		functionName = name;
		inputs = new ArrayList<CodeGenArgument>();
		outputs = new ArrayList<CodeGenArgument>();
		parameters = new ArrayList<CodeGenParameter>();
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String name) {
		functionName = name;
	}

	public void addInput(CodeGenArgument inputName) {
		inputs.add(inputName);
	}

	public void addOutput(CodeGenArgument outputName) {
		outputs.add(outputName);
	}

	public void addParameter(CodeGenParameter parameterName) {
		parameters.add(parameterName);
	}

	public FunctionCall getInitCall() {
		return initCall;
	}

	public FunctionCall getEndCall() {
		return initCall;
	}

	public void setInitCall(FunctionCall init) {
		initCall = init;
	}

	public void setEndCall(FunctionCall end) {
		endCall = end;
	}

}
