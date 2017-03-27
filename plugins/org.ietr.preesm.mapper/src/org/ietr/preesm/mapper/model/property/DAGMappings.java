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
	 * A mapping is associated to IDs of the vertices belonging to it (for fast
	 * access).
	 */
	Map<String, VertexMapping> mappings = null;
	public DAGMappings() {
		super();
		mappings = new HashMap<String, VertexMapping>();
	}


	public VertexMapping getMapping(String vertexId) {
		return mappings.get(vertexId);
	}
	
	/**
	 * Associates vertices by making them share a created VertexMapping object
	 */
	public void associate(Set<MapperDAGVertex> vertices) {
		VertexMapping newMapping = new VertexMapping();
		for (MapperDAGVertex v : vertices) {
			put(v.getName(), newMapping);
		}
	}
	
	/**
	 * Dedicates a created VertexMapping object to a single vertex
	 */
	public void dedicate(MapperDAGVertex vertex) {
		VertexMapping newMapping = new VertexMapping();
		put(vertex.getName(), newMapping);
	}
	
	/**
	 * Associating a vertex to an existing mapping
	 */
	private void put(String vertexId, VertexMapping m) {
		mappings.put(vertexId, m);
		m.addVertexID(vertexId);
	}

	/**
	 * Associating a vertex to an existing mapping
	 */
	public void remove(MapperDAGVertex vertex) {
		mappings.get(vertex.getName()).removeVertexID(vertex.getName());
		mappings.remove(vertex.getName());
	}

	/**
	 * Cloning the common mappings of vertices and ensuring that several vertices with same group share the same mapping object
	 */
	@Override
	public Object clone() {
		Map<VertexMapping,VertexMapping> relateOldAndNew = new HashMap<VertexMapping,VertexMapping>();
		DAGMappings newMappings = new DAGMappings();
		for(String s : mappings.keySet()){
			VertexMapping m = mappings.get(s);
			if(relateOldAndNew.containsKey(m)){
				newMappings.put(s,relateOldAndNew.get(m));
			} else {
				VertexMapping newM = mappings.get(s).clone();
				relateOldAndNew.put(m, newM);
				newMappings.put(s,newM);
			}
		}
		return newMappings;
	}
	@Override
	public String toString() {
		return mappings.toString();
	}
	
	
	
}
