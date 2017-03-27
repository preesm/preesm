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

package org.ietr.preesm.core.scenario;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.ietr.preesm.core.architecture.route.Route;
import org.ietr.preesm.core.types.DataType;

/**
 * Handles simulation parameters
 * 
 * @author mpelcat
 */
public class SimulationManager {

	/**
	 * Names of the main operator and com node
	 */
	private String mainComNodeName = "";
	private String mainOperatorName = "";

	/**
	 * Average transfer size sizes in base unit (usually byte). This size is
	 * used while calculating the routing table. The routes between operators
	 * are static and will be optimized for the given data size.
	 */
	private long averageDataSize = Route.averageTransfer;

	/**
	 * Names of the data types with their size
	 */
	private Map<String, DataType> dataTypes;

	/**
	 * Operators able to execute special vertices
	 */
	private Set<String> specialVertexOperatorIds;

	/**
	 * Number of executions of the top graph when simulating PiGraphs
	 */
	private int numberOfTopExecutions = 1;
	
	public SimulationManager() {
		super();

		dataTypes = new HashMap<String, DataType>();
		specialVertexOperatorIds = new HashSet<String>();
	}

	public String getMainComNodeName() {
		return mainComNodeName;
	}

	public void setMainComNodeName(String mainComNodeName) {
		this.mainComNodeName = mainComNodeName;
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

	public DataType getDataType(String name) {
		return dataTypes.get(name);
	}

	public int getDataTypeSizeOrDefault(String name) {
		if (dataTypes.get(name) == null) {
			return DataType.defaultDataTypeSize;
		} else {
			return dataTypes.get(name).getSize();
		}
	}

	public void putDataType(DataType dataType) {
		dataTypes.put(dataType.getTypeName(), dataType);
	}

	public void removeDataType(String dataTypeName) {
		dataTypes.remove(dataTypeName);
	}

	public void setAverageDataSize(long size) {
		averageDataSize = size;
	}

	public long getAverageDataSize() {
		return averageDataSize;
	}

	public Set<String> getSpecialVertexOperatorIds() {
		return specialVertexOperatorIds;
	}

	public void addSpecialVertexOperatorId(String opId) {
		if (!hasSpecialVertexOperatorId(opId)) {
			specialVertexOperatorIds.add(opId);
		}
	}

	public void removeSpecialVertexOperatorId(String id) {
		Iterator<String> it = specialVertexOperatorIds.iterator();
		while (it.hasNext()) {
			String cmpId = it.next();
			if ((cmpId.equals(id))) {
				it.remove();

			}
		}
	}

	public boolean hasSpecialVertexOperatorId(String id) {
		for (String currentId : specialVertexOperatorIds) {
			if ((currentId.equals(id))) {
				return true;
			}
		}
		return false;
	}

	public int getNumberOfTopExecutions() {
		if (numberOfTopExecutions < 1) setNumberOfTopExecutions(1);
		return numberOfTopExecutions;
	}

	public void setNumberOfTopExecutions(int numberOfTopExecutions) {
		this.numberOfTopExecutions = numberOfTopExecutions;
	}

}
