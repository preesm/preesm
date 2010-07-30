package org.ietr.preesm.plugin.mapper.model;

import java.util.HashSet;
import java.util.Set;

/**
 * @author mpelcat
 * 
 * Set of mapping groups
 * 
 */
public class MappingGroupSet {

	/**
	 * Groups of vertices. Each group must be mapped on a single operator.
	 */
	private Set<MappingGroup> mappingGroups;

	public MappingGroupSet() {
		mappingGroups = new HashSet<MappingGroup>();
	}

	public void add (MappingGroup group) {
		if(group.getMainVertex() != null){
			mappingGroups.add(group);
		}
	}

	public MappingGroup getGroup(MapperDAGVertex vertex) {
		
		for(MappingGroup g : mappingGroups){
			if(g.contains(vertex)){
				return g;
			}
		}
		return null;
	}
	
	@Override
	public String toString(){
		return mappingGroups.toString();
	}

}
