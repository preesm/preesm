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

package org.ietr.preesm.core.task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ietr.preesm.core.workflow.TaskNode;

/**
 * Store textual parameters in a map transmitted to the transformations.
 * 
 * @author mpelcat
 */
public class TextParameters {

	/**
	 * Variables retrieved form {@link TaskNode} and used to parameterize the
	 * transformation.
	 */
	private Map<String, String> variables = null;

	public TextParameters() {
		super();
		this.variables = new HashMap<String, String>();
	}

	public TextParameters(Map<String, String> variables) {
		super();
		this.variables = variables;
	}

	public void addVariable(String key, boolean value) {
		Boolean bool = new Boolean(value);
		variables.put(key, bool.toString());
	}

	public void addVariable(String key, int value) {
		variables.put(key, String.format("%d", value));
	}

	/**
	 * Creates a new variable.
	 * 
	 * @param key
	 *            the name of the variable.
	 * @param value
	 *            the value of the variable.
	 */
	public void addVariable(String key, String value) {
		variables.put(key, value);
	}

	/**
	 * Retrieves the variable as a boolean.
	 */
	public boolean getBooleanVariable(String key) {

		if (variables.containsKey(key)) {
			return Boolean.valueOf(variables.get(key));
		}

		return false;
	}

	/**
	 * Retrieves the variable as an integer.
	 */
	public int getIntVariable(String key) {

		if (variables.containsKey(key)) {
			return Integer.valueOf(variables.get(key));
		}

		return 0;
	}

	/**
	 * Retrieves the variable as a string.
	 */
	public String getVariable(String key) {

		if (variables.containsKey(key)) {
			return variables.get(key);
		}

		return "";
	}

	public boolean hasVariable(String key) {
		return variables.containsKey(key);
	}

	/**
	 * Replaces the environment variables by their values
	 */
	public void resolveEnvironmentVars(Map<String, String> envVars) {

		Iterator<String> localVarIterator = variables.keySet().iterator();

		while (localVarIterator.hasNext()) {
			String localVarKey = localVarIterator.next();
			String localVarValue = variables.get(localVarKey);

			Iterator<String> envVarIterator = envVars.keySet().iterator();

			while (envVarIterator.hasNext()) {
				String envVarKey = envVarIterator.next();
				String envVarValue = envVars.get(envVarKey);

				localVarValue = localVarValue.replace("${" + envVarKey + "}",
						envVarValue);
				variables.put(localVarKey, localVarValue);
			}
		}
	}
}
