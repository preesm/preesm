/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/
 
package org.ietr.preesm.core.codegen.model;

import java.util.HashMap;
import java.util.Map;

import org.sdf4j.model.IRefinement;

public class FunctionCall implements IRefinement {

	private String functionName;
	public FunctionCall initCall = null;
	public FunctionCall endCall = null;
	private int nbArgs ; 

	private Map<CodeGenArgument, Integer> arguments;

	private Map<CodeGenParameter, Integer> parameters;

	public FunctionCall(){
		functionName = "";
		arguments = new HashMap<CodeGenArgument, Integer>();
		parameters = new HashMap<CodeGenParameter, Integer>();
		nbArgs = 0 ;
	}
	
	public FunctionCall(String name) {
		functionName = name;
		arguments = new HashMap<CodeGenArgument, Integer>();
		parameters = new HashMap<CodeGenParameter, Integer>();
		nbArgs = 0 ;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String name) {
		functionName = name;
	}

	public void addArgument(CodeGenArgument arg) {
		arguments.put(arg, nbArgs);
		nbArgs ++ ;
	}

	public void addParameter(CodeGenParameter parameterName) {
		parameters.put(parameterName, nbArgs);
		nbArgs ++ ;
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
	
	public Map<CodeGenArgument, Integer> getArguments() {
		return arguments;
	}
	
	public Map<CodeGenParameter, Integer> getParameters() {
		return parameters;
	}
	
	public int getNbArgs(){
		return nbArgs ;
	}

}
