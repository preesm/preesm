package org.ietr.preesm.core.codegen.model;

import java.util.ArrayList;
import java.util.List;

import org.sdf4j.model.IRefinement;

public class FunctionCall implements IRefinement {

	private String functionName;
	public FunctionCall initCall = null;
	public FunctionCall endCall = null;

	private List<CodeGenArgument> arguments;

	private List<CodeGenParameter> parameters;

	public FunctionCall(){
		functionName = "";
		arguments = new ArrayList<CodeGenArgument>();
		parameters = new ArrayList<CodeGenParameter>();
	}
	
	public FunctionCall(String name) {
		functionName = name;
		arguments = new ArrayList<CodeGenArgument>();
		parameters = new ArrayList<CodeGenParameter>();
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String name) {
		functionName = name;
	}

	public void addArgument(CodeGenArgument arg) {
		arguments.add(arg);
	}

	public void addParameter(CodeGenParameter parameterName) {
		parameters.add(parameterName);
	}

	public FunctionCall getInitCall() {
		return initCall;
	}

	public FunctionCall getEndCall() {
		return endCall;
	}

	public void setInitCall(FunctionCall init) {
		initCall = init;
	}

	public void setEndCall(FunctionCall end) {
		endCall = end;
	}
	
	public List<CodeGenArgument> getArguments() {
		return arguments;
	}

}
