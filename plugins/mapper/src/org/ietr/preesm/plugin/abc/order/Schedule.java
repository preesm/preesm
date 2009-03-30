/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.plugin.abc.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * A schedule represents the consecutive tasks mapped on a single
 * {@link ArchitectureComponent}
 * @author mpelcat
 */
public class Schedule extends LinkedList<MapperDAGVertex> {

	private static final long serialVersionUID = 1L;

	public Schedule() {

		super();
	}
	
	/**
	 * Gets the previous vertex in the current schedule
	 */
	public MapperDAGVertex getPreviousVertex(MapperDAGVertex vertex) {
		if (indexOf(vertex) <= 0)
			return null;
		return (get(indexOf(vertex) - 1));
	}
	
	/**
	 * Gets the next vertex in the current schedule
	 */
	public MapperDAGVertex getNextVertex(MapperDAGVertex vertex) {
		int currentIndex = indexOf(vertex);
		if (currentIndex < 0 || indexOf(vertex) >= this.size() - 1)
			return null;
		return (get(currentIndex + 1));
	}

	/**
	 * Appends a vertex at the end of the schedule
	 */
	public void addVertex(MapperDAGVertex vertex) {
		if (!contains(vertex))
			addLast(vertex);
	}

	/**
	 * Inserts a vertex at the beginning of the schedule
	 */
	public void addVertexFirst(MapperDAGVertex vertex) {
		if (!contains(vertex))
			addFirst(vertex);
	}

	/**
	 * Inserts a vertex after the given one
	 */
	public void insertVertexAfter(MapperDAGVertex previous,
			MapperDAGVertex vertex) {

		if (!contains(vertex))
			if (indexOf(previous) >= 0) {
				if (indexOf(previous) + 1 < size())
					add(indexOf(previous) + 1, vertex);
				else
					addLast(vertex);
			}
	}

	/**
	 * Inserts a vertex before the given one
	 */
	public void insertVertexBefore(MapperDAGVertex next,
			MapperDAGVertex vertex) {

		if (!contains(vertex))
			if (indexOf(next) >= 0) {
				add(indexOf(next),vertex);
			}
	}

	@Override
	public String toString() {
		return "{" + super.toString() + "}";
	}

	public List<String> toList() {
		
		List<String> order = new ArrayList<String>();
		
		for(MapperDAGVertex v : this){
			order.add(v.getName());
		}
		
		return order;
	}
	
}
