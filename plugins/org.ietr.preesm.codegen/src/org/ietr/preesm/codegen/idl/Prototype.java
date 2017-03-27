/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/

package org.ietr.preesm.codegen.idl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.ietr.dftools.algorithm.model.IRefinement;
import org.ietr.preesm.codegen.model.CodeGenArgument;
import org.ietr.preesm.codegen.model.CodeGenParameter;

/**
 * Function prototype
 * 
 * @author mpelcat
 */
public class Prototype implements IRefinement {

	private String functionName;
	private int nbArgs;

	/**
	 * This map associates {@link CodeGenArgument} of the prototype with an
	 * integer that represent the order of the argument in the prototype.
	 */
	private Map<CodeGenArgument, Integer> arguments;

	/**
	 * This map associates {@link CodeGenParameter} of the prototype with an
	 * integer that represent the order of the argument in the prototype.
	 */
	private Map<CodeGenParameter, Integer> parameters;

	public Prototype() {
		functionName = "";
		arguments = new LinkedHashMap<CodeGenArgument, Integer>();
		parameters = new LinkedHashMap<CodeGenParameter, Integer>();
		nbArgs = 0;
	}

	public Prototype(String name) {
		functionName = name;
		arguments = new LinkedHashMap<CodeGenArgument, Integer>();
		parameters = new LinkedHashMap<CodeGenParameter, Integer>();
		nbArgs = 0;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String name) {
		functionName = name;
	}

	public void addArgument(CodeGenArgument arg) {
		arguments.put(arg, nbArgs);
		nbArgs++;
	}

	public void addParameter(CodeGenParameter parameterName) {
		parameters.put(parameterName, nbArgs);
		nbArgs++;
	}

	public Map<CodeGenArgument, Integer> getArguments() {
		return arguments;
	}

	public Map<CodeGenParameter, Integer> getParameters() {
		return parameters;
	}

	public int getNbArgs() {
		return nbArgs;
	}

}
