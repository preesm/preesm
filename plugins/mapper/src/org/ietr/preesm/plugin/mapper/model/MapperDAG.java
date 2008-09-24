/**
 * 
 */
package org.ietr.preesm.plugin.mapper.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ietr.preesm.plugin.mapper.tools.TopologicalDAGIterator;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * @author mpelcat
 * 
 * This class represents a Directed Acyclic Graph in the mapper
 */
public class MapperDAG extends DirectedAcyclicGraph {


	/**
	 * 
	 */
	private static final long serialVersionUID = -6757893466692519433L;

	/**
	 * Corresponding vertex in the input SDF graph
	 */
	private SDFGraph sdfGraph;

	/**
	 * The latency of the implementation
	 */
	private int ScheduleLatency;
	
	/**
	 * Creactor of a DAG from a edge factory and a converted graph
	 */
	public MapperDAG(MapperEdgeFactory factory,SDFGraph graph) {
		super(factory);
		this.sdfGraph = graph;
		this.ScheduleLatency = 0;
	}

	/**
	 * give the number of vertices in the DAG
	 */
	public int getNumberOfVertices() {
		return vertexSet().size();
	}

	/**
	 * Adds all vertices of a given set
	 */
	public void addAllVertices(Set<MapperDAGVertex> set) {
		Iterator<MapperDAGVertex> iterator = set.iterator();

		while (iterator.hasNext()) {
			addVertex(iterator.next());
		}
	}

	public int getScheduleLatency() {
		return ScheduleLatency;
	}

	public void setScheduleLatency(int scheduleLatency) {
		ScheduleLatency = scheduleLatency;
	}

	public SDFGraph getReferenceSdfGraph() {
		return sdfGraph;
	}

	public void setReferenceSdfGraph(SDFGraph sdfGraph) {
		this.sdfGraph = sdfGraph;
	}

	/**
	 * Clone a MapperDAG
	 */
	@Override
	public MapperDAG clone() {

		// create clone
		MapperDAG newDAG = new MapperDAG(new MapperEdgeFactory(), this
				.getReferenceSdfGraph());
		newDAG.setScheduleLatency(this.getScheduleLatency());
		
		// add vertex
		Iterator<DAGVertex> iterV = this.vertexSet().iterator();
		while (iterV.hasNext()) {
			MapperDAGVertex currentVertex = (MapperDAGVertex)iterV.next();
			currentVertex = ((MapperDAGVertex)currentVertex).clone();
			newDAG.addVertex(currentVertex);
		}

		// add edge
		Iterator<DAGEdge> iterE = this.edgeSet().iterator();
		while (iterE.hasNext()) {
			MapperDAGEdge origEdge = (MapperDAGEdge)iterE.next();
			
			DAGVertex source = origEdge.getSource();
			DAGVertex target = origEdge.getTarget();
			
			String sourceName = source.getName();
			String targetName = target.getName();
			MapperDAGEdge newEdge = (MapperDAGEdge)newDAG.addEdge(newDAG
					.getVertex(sourceName), newDAG.getVertex(targetName));
			newEdge.setInitialEdgeProperty(origEdge.getInitialEdgeProperty()
					.clone());
			newEdge.setTimingEdgeProperty(origEdge.getTimingEdgeProperty()
					.clone());
			
			for(String propertyKey : origEdge.getPropertyBean().keys()){
				Object property = origEdge.getPropertyBean().getValue(propertyKey);
				newEdge.getPropertyBean().setValue(propertyKey, property);
			}
		}

		for(String propertyKey : this.getPropertyBean().keys()){
			Object property = this.getPropertyBean().getValue(propertyKey);
			newDAG.getPropertyBean().setValue(propertyKey, property);
		}
		return newDAG;
	}

	/**
	 * Gets the vertex with the given reference graph
	 */
	public MapperDAGVertex getVertex(SDFAbstractVertex sdfvertex) {

		Iterator<DAGVertex> iter = vertexSet().iterator();
		MapperDAGVertex currentvertex = null;
		while (iter.hasNext()) {
			currentvertex = (MapperDAGVertex)iter.next();
			if (currentvertex.getName().equals(sdfvertex.getName())) {
				return currentvertex;
			}
		}
		return null;
	}

	/**
	 * Gets all the DAG vertices corresponding to a given SDF graph
	 */
	public Set<MapperDAGVertex> getVertices(SDFAbstractVertex sdfvertex) {

		Set<MapperDAGVertex> currentset = new HashSet<MapperDAGVertex>();
		Iterator<DAGVertex> iter = vertexSet().iterator();
		MapperDAGVertex currentvertex = null;
		while (iter.hasNext()) {
			currentvertex = (MapperDAGVertex)iter.next();
			if (currentvertex.getName().equals(sdfvertex.getName())) {
				currentset.add(currentvertex);
			}
		}
		return currentset;
	}

	/**
	 * Give a list of vertices in topological order
	 */
	public List<MapperDAGVertex> getVertexTopologicalList() {

		TopologicalDAGIterator iter = new TopologicalDAGIterator(this);
		List<MapperDAGVertex> list = new ArrayList<MapperDAGVertex>();
		MapperDAGVertex currentvertex = null;
		while (iter.hasNext()) {
			currentvertex = (MapperDAGVertex)iter.next();
			if (!list.contains(currentvertex))
				list.add(currentvertex);
		}
		return list;
	}
	
	public Set<MapperDAGVertex> getVertexSet(Set<String> nameSet) {
		Set<MapperDAGVertex> vSet = new HashSet<MapperDAGVertex>();

		Iterator<String> iterator = nameSet.iterator();

		while (iterator.hasNext()) {
			String name = iterator.next();
			MapperDAGVertex v = (MapperDAGVertex)this.getVertex(name);
			vSet.add(v);

		}

		return vSet;
	}
	

	public MapperDAGVertex getMapperDAGVertex(String name) {
		
		return (MapperDAGVertex)super.getVertex(name);
	}
}
