/**
 * 
 */
package org.ietr.preesm.core.scenario;

import java.util.HashMap;
import java.util.Map;

import org.ietr.preesm.core.codegen.DataType;

/**
 * Handles simulation parameters
 * 
 * @author mpelcat
 */
public class SimulationManager {

	/**
	 * Names of the main operator and medium
	 */
	private String mainMediumName = "";
	private String mainOperatorName = "";

	/**
	 * Names of the data types with their size
	 */
	private Map<String,DataType> dataTypes;
	
	
	public SimulationManager() {
		super();

		dataTypes = new HashMap<String, DataType>();
	}

	public String getMainMediumName() {
		return mainMediumName;
	}

	public void setMainMediumName(String mainMediumName) {
		this.mainMediumName = mainMediumName;
	}

	public String getMainOperatorName() {
		return mainOperatorName;
	}

	public void setMainOperatorName(String mainOperatorName) {
		this.mainOperatorName = mainOperatorName;
	}
	
	public Map<String, DataType> getDataTypes() {
		return dataTypes;
	}
	
	public void putDataType(DataType dataType) {
		dataTypes.put(dataType.getTypeName(), dataType);
	}
	
	public void removeDataType(String dataTypeName) {
		dataTypes.remove(dataTypeName);
	}
}
