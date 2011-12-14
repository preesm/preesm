package org.ietr.preesm.core.scenario;

import net.sf.dftools.algorithm.model.parameters.Variable;
import net.sf.dftools.algorithm.model.parameters.VariableSet;

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
			parser.parse(excelFileURL);
		}
	}

}
