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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.plugin.mapper.model.ImplementationVertexProperty;
import org.ietr.preesm.plugin.mapper.model.InitialVertexProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.TimingVertexProperty;
import org.sdf4j.model.dag.DAGEdge;

/**
 * A group of vertices that have the same total order and the same T-Level
 * 
 * @author mpelcat
 */
public class SynchronizedVertices implements IScheduleElement {

	private List<MapperDAGVertex> vertices = null;

	public SynchronizedVertices() {
		super();
		this.vertices = new ArrayList<MapperDAGVertex>();
	}

	public SynchronizedVertices(List<MapperDAGVertex> vertices) {
		super();
		this.vertices = new ArrayList<MapperDAGVertex>(vertices);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public TimingVertexProperty getTimingVertexProperty() {
		if (!vertices.isEmpty()) {
			return vertices.get(0).getTimingVertexProperty();
		}
		return null;
	}

	@Override
	public ImplementationVertexProperty getImplementationVertexProperty() {
		if (!vertices.isEmpty()) {
			return vertices.get(0).getImplementationVertexProperty();
		}
		return null;
	}

	@Override
	public InitialVertexProperty getInitialVertexProperty() {
		if (!vertices.isEmpty()) {
			return vertices.get(0).getInitialVertexProperty();
		}
		return null;
	}

	public List<MapperDAGVertex> vertices() {
		return Collections.unmodifiableList(vertices);
	}

	public boolean contains(MapperDAGVertex vertex) {
		return vertices.contains(vertex);
	}

	public MapperDAGVertex getVertex(ArchitectureComponent cmp) {
		for (MapperDAGVertex v : vertices) {
			if (v.getImplementationVertexProperty().getEffectiveComponent()
					.equals(cmp)) {
				return v;
			}
		}
		return null;
	}

	public void remove(MapperDAGVertex v) {
		vertices.remove(v);

		// Desynchronizes the timing props
		if (!vertices.isEmpty()) {
			v.setTimingVertexProperty(vertices.get(0).getTimingVertexProperty()
					.clone());
		} else {
			int i = 0;
			i++;
		}
	}

	public void add(MapperDAGVertex v) {
		if (!vertices.contains(v)) {
			// All synchronize vertices share the same timing vertex property
			if (!vertices.isEmpty()) {
				v.setTimingVertexProperty(vertices.get(0)
						.getTimingVertexProperty());
			}

			vertices.add(v);
		}
	}

	@Override
	public Set<DAGEdge> incomingEdges() {
		Set<DAGEdge> incomingEdges = new HashSet<DAGEdge>();
		for (MapperDAGVertex v : vertices) {
			incomingEdges.addAll(v.incomingEdges());
		}
		return incomingEdges;
	}

	@Override
	public String toString() {
		return "#synch " + vertices.toString() + " #";
	}

	public boolean isEmpty() {
		return vertices.isEmpty();
	}

	/**
	 * Two synchro vertices objects are equivalent if they contain the same
	 * vertices
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj instanceof SynchronizedVertices) {
			SynchronizedVertices newS = (SynchronizedVertices) obj;
			if (this == obj) {
				return true;
			}
			if (newS.vertices().size() != vertices().size()) {
				return false;
			}

			for (MapperDAGVertex v : newS.vertices()) {
				if (!contains(v)) {
					return false;
				}
			}

			return true;
		}
		return false;
	}
}
