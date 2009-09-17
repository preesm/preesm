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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.sdf4j.model.dag.DAGVertex;

/**
 * A schedule represents the consecutive tasks mapped on a single
 * {@link ArchitectureComponent}
 * 
 * @author mpelcat
 */
public class Schedule {

	private static final long serialVersionUID = 1L;

	/**
	 * The ordered list of vertices in this schedule
	 */
	private LinkedList<IScheduleElement> elementList;

	/**
	 * The total time of the schedule vertices
	 */
	private long busyTime;

	public Schedule() {

		super();
		this.elementList = new LinkedList<IScheduleElement>();
		resetBusyTime();
	}

	/**
	 * Appends a vertex at the end of the schedule
	 */
	public void addLast(IScheduleElement vertex) {
		if (!elementList.contains(vertex)) {
			if (vertex.getTimingVertexProperty().getCost() >= 0) {
				busyTime += vertex.getTimingVertexProperty().getCost();
			}
			elementList.addLast(vertex);
		}
	}

	/**
	 * Inserts a vertex at the beginning of the schedule
	 */
	public void addFirst(IScheduleElement vertex) {
		if (!elementList.contains(vertex)) {
			if (vertex.getTimingVertexProperty().getCost() >= 0) {
				busyTime += vertex.getTimingVertexProperty().getCost();
			}
			elementList.addFirst(vertex);
		}
	}

	/**
	 * Inserts a vertex after the given one
	 */
	public void insertAfter(IScheduleElement previous, IScheduleElement vertex) {
		if (!elementList.contains(vertex)) {
			if (vertex.getTimingVertexProperty().getCost() >= 0) {
				busyTime += vertex.getTimingVertexProperty().getCost();
			}

			if (elementList.indexOf(previous) >= 0) {
				if (elementList.indexOf(previous) + 1 < elementList.size()) {
					IScheduleElement next = elementList.get(elementList
							.indexOf(previous) + 1);
					elementList.add(elementList.indexOf(next), vertex);
				} else {
					elementList.addLast(vertex);
				}
			}
		}
	}

	/**
	 * Inserts a vertex before the given one
	 */
	public void insertBefore(IScheduleElement next, IScheduleElement vertex) {
		if (!elementList.contains(vertex)) {
			if (vertex.getTimingVertexProperty().getCost() >= 0) {
				busyTime += vertex.getTimingVertexProperty().getCost();
			}

			if (elementList.indexOf(next) >= 0) {
				elementList.add(elementList.indexOf(next), vertex);
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

	public void remove(IScheduleElement vertex) {
		if (elementList.contains(vertex)) {
			if (vertex.getTimingVertexProperty().getCost() >= 0) {
				busyTime -= vertex.getTimingVertexProperty().getCost();
			}

			elementList.remove(vertex);
		}
	}

	// Access without modification

	public IScheduleElement get(int i) {
		return elementList.get(i);
	}

	public IScheduleElement getLast() {
		return elementList.getLast();
	}

	/**
	 * Gets the previous vertex in the current schedule
	 */
	public IScheduleElement getPrevious(IScheduleElement vertex) {
		if (elementList.indexOf(vertex) <= 0) {
			return null;
		} else {
			return (elementList.get(elementList.indexOf(vertex) - 1));
		}
	}

	/**
	 * Gets the next vertex in the current schedule
	 */
	public IScheduleElement getNext(IScheduleElement vertex) {
		int currentIndex = elementList.indexOf(vertex);
		if (currentIndex < 0
				|| (elementList.indexOf(vertex) >= elementList.size() - 1)) {
			return null;
		} else {
			return (elementList.get(currentIndex + 1));
		}
	}

	/**
	 * Gets the next vertices in the current schedule
	 */
	public Set<IScheduleElement> getSuccessors(IScheduleElement vertex) {
		Set<IScheduleElement> vSet = new HashSet<IScheduleElement>();
		int currentIndex = elementList.indexOf(vertex);
		if (currentIndex < 0
				|| elementList.indexOf(vertex) >= elementList.size()) {
			return null;
		}

		for (int i = currentIndex + 1; i < elementList.size(); i++) {
			vSet.add(elementList.get(i));
		}
		return vSet;
	}

	/**
	 * Giving the index of the vertex if present in the list or in a
	 * synchronized vertex from the list
	 */
	public int indexOf(IScheduleElement v) {
		int index = elementList.indexOf(v);

		// Searching in synchronized vertices
		if (index == -1) {
			for (IScheduleElement sElt : elementList) {
				if (sElt instanceof SynchronizedVertices
						&& ((SynchronizedVertices) sElt).getVertices()
								.contains(v)) {
					return elementList.indexOf(sElt);
				}
			}
		}

		return index;
	}

	public boolean contains(IScheduleElement v) {
		return indexOf(v) != -1;
	}

	public boolean isEmpty() {
		return elementList.isEmpty();
	}

	public List<IScheduleElement> getList() {
		return elementList;
	}

	@Override
	public String toString() {
		return "{" + super.toString() + "}";
	}

	public VertexOrderList toOrderList() {

		VertexOrderList order = new VertexOrderList();

		for (IScheduleElement elt : elementList) {
			if (elt instanceof MapperDAGVertex) {
				MapperDAGVertex v = (MapperDAGVertex) elt;
				VertexOrderList.OrderProperty op = order.new OrderProperty(v
						.getName(), indexOf(v));
				order.addLast(op);
			} else if (elt instanceof SynchronizedVertices) {
				for (MapperDAGVertex v : ((SynchronizedVertices) elt)
						.getVertices()) {
					VertexOrderList.OrderProperty op = order.new OrderProperty(
							v.getName(), indexOf(v));
					order.addLast(op);
				}
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
