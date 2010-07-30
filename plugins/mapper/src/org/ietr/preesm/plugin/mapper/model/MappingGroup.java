package org.ietr.preesm.plugin.mapper.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.SpecialVertexManager;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.sdf.esdf.SDFEndVertex;
import org.sdf4j.model.sdf.esdf.SDFInitVertex;

/**
 * @author mpelcat
 * 
 *         Group of vertices that are compelled to be mapped on a single
 *         operator. It contains one main vertex and, if it is non special, its
 *         preceding init vertices, its following end vertices, its preceding
 *         join vertices, its following fork vertices and its following
 *         broadcasts
 */
public class MappingGroup {

	/**
	 * Set of join vertices in the mapping group
	 */
	private Set<MapperDAGVertex> joinSet = null;

	/**
	 * Set of init vertices in the mapping group
	 */
	private Set<MapperDAGVertex> initSet = null;
	/**
	 * Main vertex, reference of the mapping group
	 */
	private MapperDAGVertex mainVertex;

	/**
	 * Set of fork vertices in the mapping group
	 */
	private Set<MapperDAGVertex> forkSet = null;

	/**
	 * Set of end vertices in the mapping group
	 */
	private Set<MapperDAGVertex> endSet = null;

	/**
	 * Set of broadcast vertices in the mapping group
	 */
	private Set<MapperDAGVertex> broadcastSet = null;

	/**
	 * List of all vertices in the mapping group in topoligical order
	 */
	private List<MapperDAGVertex> specialVertices = null;

	/**
	 * Intersection of all initial operator sets
	 */
	private List<Operator> operators = null;

	/**
	 * Constructor. If degenerated = true, the mapping group is forced to
	 * contain only the main vertex.
	 */
	public MappingGroup(MapperDAGVertex mainVertex, boolean degenerated) {

		this.mainVertex = mainVertex;
		this.operators = new ArrayList<Operator>();
		operators.addAll(mainVertex.getInitialVertexProperty()
				.getInitialOperatorList());

		if (!degenerated) {
			this.joinSet = new HashSet<MapperDAGVertex>();
			this.initSet = new HashSet<MapperDAGVertex>();
			this.forkSet = new HashSet<MapperDAGVertex>();
			this.endSet = new HashSet<MapperDAGVertex>();
			this.broadcastSet = new HashSet<MapperDAGVertex>();
			this.specialVertices = new ArrayList<MapperDAGVertex>();

			// Inserts the adequate special vertices in the group
			populateGroup();

			// Retrieves the operators capable of executing all vertices
			intersectOperators();
		}
	}

	private void populateGroup() {

		for (MapperDAGVertex pred : mainVertex.getPredecessorSet(true)) {
			if (pred != null && SpecialVertexManager.isJoin(pred)) {
				joinSet.add(pred);
			}

			if (pred != null && SpecialVertexManager.isInit(pred)) {
				initSet.add(pred);
			}
		}

		for (MapperDAGVertex initV : initSet) {
			SDFEndVertex endVertex = (SDFEndVertex) ((SDFInitVertex) initV
					.getCorrespondingSDFVertex()).getEndReference();
			DirectedAcyclicGraph parentGraph = (DirectedAcyclicGraph) initV
					.getBase();
			MapperDAGVertex endDagVertex = (MapperDAGVertex) parentGraph
					.getVertex(endVertex.getName());

			endSet.add((MapperDAGVertex) endDagVertex);
		}

		for (MapperDAGVertex suc : mainVertex.getSuccessorSet(true)) {
			if (suc != null && SpecialVertexManager.isFork(suc)) {
				forkSet.add(suc);
			}

			if (suc != null && SpecialVertexManager.isBroadCast(suc)) {
				broadcastSet.add(suc);
			}
		}

		specialVertices.addAll(initSet);
		specialVertices.addAll(joinSet);
		specialVertices.addAll(endSet);
		specialVertices.addAll(broadcastSet);
		specialVertices.addAll(forkSet);
	}

	private void intersectOperators() {
		for (MapperDAGVertex v : specialVertices) {
			operators.retainAll(v.getInitialVertexProperty()
					.getInitialOperatorList());
		}

		if (operators.isEmpty()) {
			PreesmLogger.getLogger().log(
					Level.SEVERE,
					"Empty operator set for mapping group: " + toString()
							+ ". Consider relaxing constraints in scenario.");
		}
	}

	public List<Operator> getOperators() {
		return operators;
	}

	@Override
	public String toString() {
		if (specialVertices == null) {
			return "$" + mainVertex.toString() + "$";
		} else {
			return "$" + mainVertex.toString() + "$"
					+ specialVertices.toString();
		}
	}

	public boolean contains(MapperDAGVertex vertex) {

		if (mainVertex.getName().equals(vertex.getName())) {
			return true;
		}

		if (specialVertices != null) {
			for (MapperDAGVertex v : specialVertices) {
				if (v.getName().equals(vertex.getName())) {
					return true;
				}
			}
		}

		return false;
	}

	public Set<MapperDAGVertex> getJoins() {
		return joinSet;
	}

	public Set<MapperDAGVertex> getInits() {
		return initSet;
	}

	public Set<MapperDAGVertex> getForks() {
		return forkSet;
	}

	public Set<MapperDAGVertex> getBroadcasts() {
		return broadcastSet;
	}

	public Set<MapperDAGVertex> getEnds() {
		return endSet;
	}

	public MapperDAGVertex getMainVertex() {
		return mainVertex;
	}
}
