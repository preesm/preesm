/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2012 - 2014)
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
			e.printStackTrace();
		}
		return newProp;
	}

	/**
	 * Adding a new member to the group
	 */
	public void addVertexID(String id) {
		for (String i : vertexIDs) {
			if (i.equals(id)) {
				return;
			}
		}
		vertexIDs.add(id);
	}

	/**
	 * Removing a member from the group
	 */
	public void removeVertexID(String id) {
		Iterator<String> it = vertexIDs.iterator();
		while (it.hasNext()) {
			String i = it.next();
			if (i.equals(id)) {
				it.remove();
			}
		}
	}
	/**
	 * Returns the number of actors sharing the same property
	 */
	public int getNumberOfVertices() {
		return vertexIDs.size();
	}
	
	/**
	 * Gets the vertices corresponding to the group
	 */
	public List<MapperDAGVertex> getVertices(MapperDAG dag) {
		List<MapperDAGVertex> vertices = new ArrayList<MapperDAGVertex>();
		for (String id : vertexIDs) {
			vertices.add((MapperDAGVertex) dag.getVertex(id));
		}
		
		return vertices;
	}

	@Override
	public String toString() {
		return vertexIDs.toString();
	}
}
