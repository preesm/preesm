/**
 * 
 */
package org.ietr.preesm.mapper.model.property;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;

/**
 * Property that corresponds to a group of vertices
 * 
 * @author mpelcat
 */
abstract public class GroupProperty implements Cloneable {

	/**
	 * IDs of the vertices that share the property
	 */
	private Set<String> vertexIDs;

	
	public GroupProperty() {
		vertexIDs = new HashSet<String>();
	}

	/**
	 * Duplicating the group property
	 */
	@Override
	protected Object clone() {
		GroupProperty newProp = null;
		try {
			newProp = (GroupProperty) super.clone();
			newProp.vertexIDs.addAll(vertexIDs);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newProp;
	}
	
	/**
	 * Adding a new member to the group
	 */
	public void addVertexID(String id){
		for(String i:vertexIDs){
			if(i.equals(id)){
				return;
			}
		}
		vertexIDs.add(id);
	}
	
	/**
	 * Removing a member from the group
	 */
	public void removeVertexID(String id){
		Iterator<String> it = vertexIDs.iterator();
		while(it.hasNext()){
			String i = it.next();
			if(i.equals(id)){
				it.remove();
			}
		}
	}

	/**
	 * Returns the number of actors sharing the same property
	 */
	public int getNumberOfVertices(){
		return vertexIDs.size();
	}
	
	/**
	 * Gets the vertices corresponding to the group
	 */
	public List<MapperDAGVertex> getVertices(MapperDAG dag){
		List<MapperDAGVertex> vertices = new ArrayList<MapperDAGVertex>();
		
		for(String id: vertexIDs){
			vertices.add((MapperDAGVertex)dag.getVertex(id));
		}
		
		return vertices;
	}

	@Override
	public String toString() {
		return vertexIDs.toString();
	}
}
