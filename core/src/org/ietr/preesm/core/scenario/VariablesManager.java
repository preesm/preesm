package org.ietr.preesm.core.scenario;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ietr.preesm.core.scenario.editor.constraints.ExcelConstraintsParser;
import org.ietr.preesm.core.scenario.editor.variables.ExcelVariablesParser;
import org.sdf4j.model.parameters.Variable;
import org.sdf4j.model.parameters.VariableSet;

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
	
	public void setVariable(String name,String value){
		
		if(variables.keySet().contains(name)){
			variables.get(name).setValue(value);
		}
		else{
			variables.put(name,new Variable(name,value));
		}
	}
	
	public VariableSet getVariables(){
		return variables;
	}
	
	public void removeVariable(String varName){
		variables.remove(varName);
	}

	public String getExcelFileURL() {
		return excelFileURL;
	}

	public void setExcelFileURL(String excelFileURL) {
		this.excelFileURL = excelFileURL;
	}

	public void importVariables(Scenario currentScenario) {
		if (!excelFileURL.isEmpty() && currentScenario != null) {
			ExcelVariablesParser parser = new ExcelVariablesParser(
					currentScenario);
			parser.parse(excelFileURL);
		}
	}
	
}
