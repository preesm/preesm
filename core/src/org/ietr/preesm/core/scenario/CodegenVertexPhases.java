/**
 * 
 */
package org.ietr.preesm.core.scenario;

import java.util.HashMap;
import java.util.Map;

import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * Represents the phases for which code should be generated for a given vertex.
 * 
 * @author mpelcat
 */
public class CodegenVertexPhases {

	/**
	 * list of the phases for a vertex with their names and a boolean precising if code should
	 * be generated for this phase
	 */
	private Map<String,Boolean> phases;
	public static final String initPhaseId = "init";
	public static final String loopPhaseId = "loop";
	public static final String closePhaseId = "close";

	/**
	 * Vertex to which the preceding phases are associated
	 */
	private SDFAbstractVertex vertex = null;
	public CodegenVertexPhases(SDFAbstractVertex vertex) {
		super();
		this.vertex = vertex;
		phases = new HashMap<String, Boolean>();
		phases.put(initPhaseId, false);
		phases.put(loopPhaseId, true);
		phases.put(closePhaseId, false);
	}

	public boolean hasPhase(String name) {
		return phases.get(name);
	}

	public void setPhase(String name,Boolean exist) {
		phases.put(name,exist);
	}

	public SDFAbstractVertex getVertex() {
		return vertex;
	}

	public Map<String, Boolean> getPhases() {
		return phases;
	}

}
