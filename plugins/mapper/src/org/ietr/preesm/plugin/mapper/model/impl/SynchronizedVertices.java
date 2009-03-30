/**
 * 
 */
package org.ietr.preesm.plugin.mapper.model.impl;

import java.util.HashSet;
import java.util.Set;

import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * A group of vertices that need to be synchronized (same starting date, same
 * duration)
 * 
 * @author mpelcat
 */
public class SynchronizedVertices {

	/**
	 * Synchronized vertices
	 */
	private Set<MapperDAGVertex> synchros = null;

	/**
	 * vertices that must be all scheduled before synchros
	 */
	private Set<MapperDAGVertex> predecessors = null;

	/**
	 * vertices that must be all scheduled after synchros
	 */
	private Set<MapperDAGVertex> successors = null;

	public SynchronizedVertices() {
		super();
		this.synchros = new HashSet<MapperDAGVertex>();
		this.predecessors = new HashSet<MapperDAGVertex>();
		this.successors = new HashSet<MapperDAGVertex>();
	}
	
	void addVertex(MapperDAGVertex v){
		synchros.add(v);
	}
	
	void addPredecessor(MapperDAGVertex v){
		predecessors.add(v);
	}
	
	void addSuccessor(MapperDAGVertex v){
		successors.add(v);
	}
}
