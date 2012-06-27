package org.ietr.preesm.mapper.model.property;

import java.util.HashMap;
import java.util.Map;

import org.ietr.preesm.mapper.model.MapperDAGVertex;

/**
 * MapperDAG stores mapping properties shared by several of its vertices that
 * are synchronized
 * 
 * @author mpelcat
 */
public class DAGTimings {
	
	Map<String,VertexTiming> timings = null;
	
	
	public DAGTimings() {
		timings = new HashMap<String, VertexTiming>();
	}

	public VertexTiming getTiming(String vertexId){
		return timings.get(vertexId);
	}

	/**
	 * Dedicates a created VertexMapping object to a single vertex
	 */
	public void dedicate(MapperDAGVertex vertex){
		VertexTiming newTiming = new VertexTiming();
		put(vertex.getName(),newTiming);
	}
	
	private void put(String vertexId, VertexTiming m){
		timings.put(vertexId,m);
		m.addVertexID(vertexId);
	}
	
	public void remove(MapperDAGVertex vertex){
		timings.remove(vertex.getName());
	}

	@Override
	public Object clone() {
		DAGTimings newTimings = new DAGTimings();
		for(String s : timings.keySet()){
			newTimings.put(s,timings.get(s).clone());
		}
		return newTimings;
	}
}
