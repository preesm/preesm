/**
 * 
 */
package org.ietr.preesm.mapper.model.property;

import java.util.ArrayList;
import java.util.HashSet;
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
	
	public void addVertexID(String id){
		for(String i:vertexIDs){
			if(i.equals(id)){
				return;
			}
		}
		vertexIDs.add(id);
	}
	
	public int getNumberOfVertices(){
		return vertexIDs.size();
	}
	
	public List<MapperDAGVertex> getVertices(MapperDAG dag){
		List<MapperDAGVertex> vertices = new ArrayList<MapperDAGVertex>();
		
		for(String id: vertexIDs){
			vertices.add((MapperDAGVertex)dag.getVertex(id));
		}
		
		return vertices;
	}
}
