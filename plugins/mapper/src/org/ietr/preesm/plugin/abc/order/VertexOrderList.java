/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

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

package org.ietr.preesm.plugin.abc.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * Association of a rank and a vertex name to export a graph total ordering
 * 
 * @author mpelcat
 */
public class VertexOrderList {
	public class OrderProperty {
		private String name;
		private int order;

		public OrderProperty(String name, int order) {
			super();
			this.name = name;
			this.order = order;
		}

		public String getName() {
			return name;
		}

		public int getOrder() {
			return order;
		}

		public boolean correspondsTo(MapperDAGVertex v) {
			return v.getName().equals(name);
		}

		@Override
		public String toString() {
			return name;
		}
	}

	// Maintaining a list of the properties for iterating purpose in the given
	// order
	private List<OrderProperty> orderedList;
	// Maintaining a map of the properties for research purpose of a given name
	private Map<String, OrderProperty> nameMap;

	public VertexOrderList() {
		super();
		orderedList = new ArrayList<OrderProperty>();
		nameMap = new HashMap<String, OrderProperty>();
	}

	public int indexOf(OrderProperty p) {
		return orderedList.indexOf(p);
	}

	public List<OrderProperty> elements() {
		return Collections.unmodifiableList(orderedList);
	}

	public int orderOf(String name) {
		return nameMap.get(name).getOrder();
	}

	public boolean contains(String name) {
		return nameMap.containsKey(name);
	}

	public void addLast(OrderProperty p) {
		orderedList.add(p);
		nameMap.put(p.getName(), p);
	}

	public List<OrderProperty> getOrderedList() {
		return Collections.unmodifiableList(orderedList);
	}
}
