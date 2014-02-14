/**
 * 
 */
package org.ietr.preesm.mapper.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.util.DesignTools;

/**
 * Centralizing constraints of related vertices
 * 
 * @author mpelcat
 */
public class RelativeConstraint {

	private List<MapperDAGVertex> vertices;

	public RelativeConstraint() {
		super();
		this.vertices = new ArrayList<MapperDAGVertex>();
	}

	public List<MapperDAGVertex> getVertices() {
		return vertices;
	}

	public void addVertex(MapperDAGVertex vertex) {
		this.vertices.add(vertex);
	}

	public void merge(RelativeConstraint constraints) {
		for (MapperDAGVertex v : constraints.getVertices()) {
			if (!vertices.contains(v)) {
				this.addVertex(v);
			}
		}
	}

	public List<ComponentInstance> getOperatorsIntersection() {

		List<ComponentInstance> operators = new ArrayList<ComponentInstance>();

		if (vertices.isEmpty()) {
			WorkflowLogger.getLogger().log(Level.SEVERE,
					"Relative constraint with no vertex.");

			return operators;
		} else {
			MapperDAGVertex firstVertex = vertices.get(0);
			ComponentInstance op = firstVertex
					.getMapping().getEffectiveOperator();
			if (op != null && vertices.size() > 1) {
				// Forcing the mapper to put together related vertices
				operators.add(op);
			} else {
				operators.addAll(firstVertex.getInit()
						.getInitialOperatorList());
			}
		}

		for (int i = 1; i < vertices.size(); i++) {
			MapperDAGVertex vertex = vertices.get(i);
			ComponentInstance op = vertex.getMapping()
					.getEffectiveOperator();
			if (op != null) {
				if (DesignTools.contains(operators, op)) {
					operators.clear();
					operators.add(op);
				} else {
					operators.clear();
				}
			} else {
				DesignTools.retainAll(operators, vertex
						.getInit().getInitialOperatorList());
			}
		}

		return operators;
	}
}
