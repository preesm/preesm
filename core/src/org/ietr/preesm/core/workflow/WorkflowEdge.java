/**
 * 
 */
package org.ietr.preesm.core.workflow;

import org.ietr.preesm.core.task.TaskResult;

/**
 * @author mwipliez
 * 
 */
public class WorkflowEdge {

	private TaskResult data;

	private WorkflowEdgeType carriedDataType;

	/**
	 * 
	 */
	public WorkflowEdge() {
		data = new TaskResult();
	}

	public TaskResult getCarriedData() {
		return data;
	}

	public void setCarriedData(TaskResult data) {
		switch (carriedDataType) {
		case SDF:
			this.data.setSDF(data.getSDF());
			break;
		case DAG:
			this.data.setDAG(data.getDAG());
			break;
		case ARCHITECTURE:
			this.data.setArchitecture(data.getArchitecture());
			break;
		case SCENARIO:
			this.data.setScenario(data.getScenario());
			break;
		case SOURCELIST:
			this.data.setSourcefilelist(data.getSourcefilelist());
			break;
		default:

		}
	}
	
	public WorkflowEdgeType getCarriedDataType(){
		return carriedDataType ;
	}

	public void setCarriedDataType(String type) {
		carriedDataType = WorkflowEdgeType.fromString(type);
	}

}
