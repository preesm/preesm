/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2012)
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
package org.ietr.preesm.core.scenario;

import org.ietr.dftools.algorithm.model.parameters.Variable;
import org.ietr.dftools.algorithm.model.parameters.VariableSet;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.scenario.serialize.ExcelVariablesParser;

/**
 * Handles graph variables which values are redefined in the scenario
 * 
 * @author mpelcat
 */
public class VariablesManager {

	private VariableSet variables;

	/**
	 * Path to a file containing variables
	 */
	private String excelFileURL = "";

	public VariablesManager() {
		variables = new VariableSet();
	}

	public void setVariable(String name, String value) {

		if (variables.keySet().contains(name)) {
			variables.get(name).setValue(value);
		} else {
			variables.put(name, new Variable(name, value));
		}
	}

	public VariableSet getVariables() {
		return variables;
	}

	public void removeVariable(String varName) {
		variables.remove(varName);
	}

	public String getExcelFileURL() {
		return excelFileURL;
	}

	public void setExcelFileURL(String excelFileURL) {
		this.excelFileURL = excelFileURL;
	}

	public void importVariables(PreesmScenario currentScenario) {
		if (!excelFileURL.isEmpty() && currentScenario != null) {
			ExcelVariablesParser parser = new ExcelVariablesParser(
					currentScenario);
			
			try {
				parser.parse(excelFileURL);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void updateWith(SDFGraph sdfGraph) {
		getVariables().clear();
		for (String v : sdfGraph.getVariables().keySet()) {
			setVariable(v, sdfGraph.getVariable(v).getValue());
		}
	}

}
