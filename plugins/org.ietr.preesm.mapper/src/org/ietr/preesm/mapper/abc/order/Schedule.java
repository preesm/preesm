/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.mapper.abc.order;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ietr.preesm.mapper.model.MapperDAGVertex;

/**
 * A schedule represents the consecutive tasks mapped on a single component
 * 
 * @author mpelcat
 */
public class Schedule {

	/**
	 * The ordered list of vertices in this schedule
	 */
	private LinkedList<MapperDAGVertex> elementList;

	/**
	 * The total time of the schedule vertices
	 */
	private long busyTime;

	public Schedule() {

		super();
		this.elementList = new LinkedList<MapperDAGVertex>();
		resetBusyTime();
	}

	/**
	 * Appends a vertex at the end of the schedule
	 */
	public void addLast(MapperDAGVertex vertex) {
		if (!contains(vertex)) {
			if (vertex.getTiming().hasCost()) {
				busyTime += vertex.getTiming().getCost();
			}
			elementList.addLast(vertex);
		}
	}

	/**
	 * Inserts a vertex at the beginning of the schedule
	 */
	public void addFirst(MapperDAGVertex vertex) {
		if (!contains(vertex)) {
			if (vertex.getTiming().hasCost()) {
				busyTime += vertex.getTiming().getCost();
			}
			elementList.addFirst(vertex);
		}
	}

	/**
	 * Inserts a vertex after the given one
	 */
	public void insertAfter(MapperDAGVertex previous, MapperDAGVertex vertex) {
		if (!contains(vertex)) {
			if (vertex.getTiming().hasCost()) {
				busyTime += vertex.getTiming().getCost();
			}

			int prevIndex = indexOf(previous);
			if (prevIndex >= 0) {
				if (prevIndex + 1 < elementList.size()) {
					MapperDAGVertex next = elementList.get(prevIndex + 1);
					elementList.add(indexOf(next), vertex);
				} else {
					elementList.addLast(vertex);
				}
			}
		}
	}

	/**
	 * Inserts a vertex at the given index
	 */
	public void insertAtIndex(MapperDAGVertex vertex, int index) {
		if (!contains(vertex)) {
			if (vertex.getTiming().hasCost()) {
				busyTime += vertex.getTiming().getCost();
			}

			if (index >= 0) {
				elementList.add(index, vertex);
			}
		}
	}

	/**
	 * Inserts a vertex before the given one
	 */
	public void insertBefore(MapperDAGVertex next, MapperDAGVertex vertex) {
		if (!contains(vertex)) {
			if (vertex.getTiming().hasCost()) {
				busyTime += vertex.getTiming().getCost();
			}

			int nextIndex = indexOf(next);
			if (nextIndex >= 0) {
				elementList.add(nextIndex, vertex);
			}
		}
	}

	public void clear() {
		resetBusyTime();

		elementList.clear();
	}

	public void resetBusyTime() {
		busyTime = 0;
	}

	public void remove(MapperDAGVertex element) {
		if (elementList.contains(element)) {
			if (element.getTiming().hasCost()) {
				busyTime -= element.getTiming().getCost();
			}

			elementList.remove(element);
		}
	}

	// Access without modification

	public MapperDAGVertex get(int i) {
		return elementList.get(i);
	}

	public MapperDAGVertex getLast() {
		return elementList.getLast();
	}

	/**
	 * Gets the previous vertex in the current schedule
	 */
	public MapperDAGVertex getPrevious(MapperDAGVertex vertex) {
		int index = indexOf(vertex);
		if (index <= 0) {
			return null;
		} else {
			return (elementList.get(index - 1));
		}
	}

	/**
	 * Gets the next vertex in the current schedule
	 */
	public MapperDAGVertex getNext(MapperDAGVertex vertex) {
		int currentIndex = indexOf(vertex);
		if (currentIndex < 0 || (currentIndex >= elementList.size() - 1)) {
			return null;
		} else {
			return (elementList.get(currentIndex + 1));
		}
	}

	/**
	 * Gets the next vertices in the current schedule
	 */
	public Set<MapperDAGVertex> getSuccessors(MapperDAGVertex vertex) {
		Set<MapperDAGVertex> vSet = new HashSet<MapperDAGVertex>();
		int currentIndex = indexOf(vertex);
		if (currentIndex < 0 || currentIndex >= elementList.size()) {
			return null;
		}

		for (int i = currentIndex + 1; i < elementList.size(); i++) {
			vSet.add(elementList.get(i));
		}
		return vSet;
	}

	/**
	 * Giving the index of the vertex if present in the list
	 */
	public int indexOf(MapperDAGVertex v) {
		return elementList.indexOf(getScheduleElt(v));
	}

	/**
	 * Giving the vertex if present in the list
	 */
	public MapperDAGVertex getScheduleElt(MapperDAGVertex v) {
		int index = elementList.indexOf(v);

		// Searching in synchronized vertices
		if (index != -1) {
			return v;
		}

		return null;
	}

	/**
	 * Looks into the synchronized vertices to extract the vertex
	 */
	public boolean contains(MapperDAGVertex v) {
		return getScheduleElt(v) != null;
	}

	public boolean isEmpty() {
		return elementList.isEmpty();
	}

	public List<MapperDAGVertex> getList() {
		return Collections.unmodifiableList(elementList);
	}

	@Override
	public String toString() {
		return elementList.toString();
	}

	/**
	 * Converts this schedule to a list associating a vertex to its rank
	 */
	public VertexOrderList toOrderList() {

		VertexOrderList order = new VertexOrderList();

		for (MapperDAGVertex elt : elementList) {
			if (elt instanceof MapperDAGVertex) {
				MapperDAGVertex v = (MapperDAGVertex) elt;
				VertexOrderList.OrderProperty op = order.new OrderProperty(
						v.getName(), indexOf(v));
				order.addLast(op);
			}
		}

		return order;
	}

	public long getBusyTime() {
		return busyTime;
	}

	public int size() {
		return elementList.size();
	}
}
