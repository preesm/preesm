package org.ietr.preesm.core.workflow;


public enum WorkflowEdgeType {
	SDF, DAG, ARCHITECTURE, SCENARIO, SOURCELIST;

	public static WorkflowEdgeType fromString(String s) {
		if (s.equals("SDF")) {
			return WorkflowEdgeType.SDF;
		} else if (s.equals("DAG")) {
			return WorkflowEdgeType.DAG;
		} else if (s.equals("architecture")) {
			return WorkflowEdgeType.ARCHITECTURE;
		} else if (s.equals("scenario")) {
			return WorkflowEdgeType.SCENARIO;
		} else if (s.equals("sources")) {
			return WorkflowEdgeType.SOURCELIST;
		} else {
			return null;
		}
	}
}
