package org.ietr.preesm.mapper.model.property;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ietr.preesm.mapper.model.MapperDAGVertex;


/**
 * MapperDAG stores mapping properties shared by several of its vertices that
 * have relative constraints. If the mapping of a vertex in the group is modified,
 * all mappings of the vertices in the group are modified.
 * 
 * @author mpelcat
 */
public class DAGMappings {
	
	/**
	 * A mapping is associated to IDs of the vertices belonging to it (for fast access).
	 */
	Map<String,VertexMapping> mappings = null;
	
	
	public DAGMappings() {
		super();
		mappings = new HashMap<String, VertexMapping>();
	}


	public VertexMapping getMapping(String vertexId){
		return mappings.get(vertexId);
	}
	
	/**
	 * Associates vertices by making them share a created VertexMapping object
	 */
	public void associate(Set<MapperDAGVertex> vertices){
		VertexMapping newMapping = new VertexMapping();
		for(MapperDAGVertex v : vertices){
			put(v.getName(),newMapping);
		}
	}
	
	/**
	 * Dedicates a created VertexMapping object to a single vertex
	 */
	public void dedicate(MapperDAGVertex vertex){
		VertexMapping newMapping = new VertexMapping();
		put(vertex.getName(),newMapping);
	}
	
	/**
	 * Associating a vertex to an existing mapping
	 */
	private void put(String vertexId, VertexMapping m){
		mappings.put(vertexId,m);
		m.addVertexID(vertexId);
	}

	/**
	 * Associating a vertex to an existing mapping
	 */
	public void remove(MapperDAGVertex vertex){
		mappings.get(vertex.getName()).removeVertexID(vertex.getName());
		mappings.remove(vertex.getName());
	}

	@Override
	public Object clone() {
		DAGMappings newMappings = new DAGMappings();
		for(String s : mappings.keySet()){
			newMappings.put(s,mappings.get(s).clone());
		}
		return newMappings;
	}
	
	
}
