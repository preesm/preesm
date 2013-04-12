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

package org.ietr.preesm.mapper.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.dag.DAGVertex;

import org.ietr.preesm.core.types.ImplementationPropertyNames;
import org.ietr.preesm.mapper.model.property.VertexInit;
import org.ietr.preesm.mapper.model.property.VertexMapping;
import org.ietr.preesm.mapper.model.property.VertexTiming;
import org.ietr.preesm.mapper.model.special.OverheadVertex;
import org.ietr.preesm.mapper.model.special.PrecedenceEdge;
import org.ietr.preesm.mapper.model.special.ReceiveVertex;
import org.ietr.preesm.mapper.model.special.SendVertex;
import org.ietr.preesm.mapper.model.special.TransferVertex;

/**
 * Represents a vertex in a DAG of type {@link MapperDAG} used in the mapper
 * 
 * @author mpelcat
 */
public class MapperDAGVertex extends DAGVertex {

	/**
	 * Properties set when converting sdf to dag
	 */
	private static final String INITIAL_PROPERTY = "INITIAL_PROPERTY";
	// protected InitialVertexProperty initialVertexProperty;

	static {
		{
			public_properties
					.add(ImplementationPropertyNames.Vertex_OperatorDef);
			public_properties
					.add(ImplementationPropertyNames.Vertex_Available_Operators);
			public_properties
					.add(ImplementationPropertyNames.Vertex_originalVertexId);
			public_properties.add(ImplementationPropertyNames.Task_duration);
			public_properties
					.add(ImplementationPropertyNames.Vertex_schedulingOrder);
			public_properties.add(ImplementationPropertyNames.Vertex_Operator);

		}
	};

	public MapperDAGVertex() {

		this("default", "default", null);
	}

	public MapperDAGVertex(String id, MapperDAG base) {

		this(id, id, base);
	}

	public MapperDAGVertex(String id, String name, MapperDAG base) {

		super();

		this.setName(name);
		this.getPropertyBean().setValue(INITIAL_PROPERTY, new VertexInit());
		this.getInit().setParentVertex(this);

	}

	@Override
	public MapperDAGVertex clone() {

		MapperDAGVertex result = null;

		if (this instanceof OverheadVertex) {
			result = new OverheadVertex(this.getId(),
					(MapperDAG) this.getBase());
		} else if (this instanceof SendVertex) {
			result = new SendVertex(this.getId(), (MapperDAG) this.getBase(),
					((SendVertex) this).getSource(),
					((SendVertex) this).getTarget(),
					((SendVertex) this).getRouteStepIndex(),
					((SendVertex) this).getNodeIndex());
		} else if (this instanceof ReceiveVertex) {
			result = new ReceiveVertex(this.getId(),
					(MapperDAG) this.getBase(),
					((ReceiveVertex) this).getSource(),
					((ReceiveVertex) this).getTarget(),
					((ReceiveVertex) this).getRouteStepIndex(),
					((ReceiveVertex) this).getNodeIndex());
		} else if (this instanceof TransferVertex) {
			TransferVertex t = (TransferVertex) this;
			result = new TransferVertex(this.getId(),
					(MapperDAG) this.getBase(), t.getSource(), t.getTarget(),
					t.getRouteStepIndex(), t.getNodeIndex());
		} else {
			result = new MapperDAGVertex(this.getId(), this.getName(),
					(MapperDAG) this.getBase());
		}

		result.setInit(this.getInit().clone(result));

		for (String propertyKey : this.getPropertyBean().keys()) {
			Object property = this.getPropertyBean().getValue(propertyKey);
			result.getPropertyBean().setValue(propertyKey, property);
		}

		return result;
	}

	public VertexInit getInit() {
		return (VertexInit) this.getPropertyBean().getValue(INITIAL_PROPERTY);
	}

	private void setInit(VertexInit initialVertexProperty) {
		this.getPropertyBean()
				.setValue(INITIAL_PROPERTY, initialVertexProperty);
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof MapperDAGVertex) {
			MapperDAGVertex v = (MapperDAGVertex) obj;
			return (this.getName().compareTo(v.getName()) == 0);
		}

		return false;
	}

	public String toString() {

		String toString = "";
		if (this.getMapping().hasEffectiveComponent()) {
			// If the vertex is mapped, displays its component and rank
			toString = getName() + "("
					+ this.getMapping().getEffectiveComponent().toString()
					+ "," + this.getTiming().getTotalOrder(this) + ")";
		} else {
			// If the vertex is not mapped, displays its weight
			toString = getName() + "(" + this.getNbRepeat() + ")";
		}

		return toString;
	}

	/**
	 * Getting all predecessors, ignoring or not the precedence edges.
	 * Predecessors are given as keys for a map containing corresponding edges.
	 */
	public Map<MapperDAGVertex, MapperDAGEdge> getPredecessors(
			boolean ignorePrecedence) {

		Map<MapperDAGVertex, MapperDAGEdge> preds = new HashMap<MapperDAGVertex, MapperDAGEdge>();
		Set<DAGEdge> incomingSet = this.incomingEdges();

		if (ignorePrecedence) {
			for (DAGEdge edge : incomingSet) {
				if (!(edge instanceof PrecedenceEdge)
						&& edge.getSource() != null) {
					preds.put((MapperDAGVertex) edge.getSource(),
							(MapperDAGEdge) edge);
				}
			}
		} else {
			for (DAGEdge edge : incomingSet) {
				if (edge.getSource() != null) {
					preds.put((MapperDAGVertex) edge.getSource(),
							(MapperDAGEdge) edge);
				}
			}
		}
		return preds;
	}

	/**
	 * Getting all successors, ignoring or not the precedence edges Successors
	 * are given as keys for a map containing corresponding edges.
	 */
	public Map<MapperDAGVertex, MapperDAGEdge> getSuccessors(
			boolean ignorePrecedence) {

		Map<MapperDAGVertex, MapperDAGEdge> succs = new HashMap<MapperDAGVertex, MapperDAGEdge>();
		Set<DAGEdge> outgoingSet = this.outgoingEdges();

		if (ignorePrecedence) {
			for (DAGEdge edge : outgoingSet) {
				if (!(edge instanceof PrecedenceEdge)
						&& edge.getTarget() != null) {
					succs.put((MapperDAGVertex) edge.getTarget(),
							(MapperDAGEdge) edge);
				}
			}
		} else {
			for (DAGEdge edge : outgoingSet) {
				if (edge.getTarget() != null) {
					succs.put((MapperDAGVertex) edge.getTarget(),
							(MapperDAGEdge) edge);
				}
			}
		}
		return succs;
	}

	@Override
	public Set<DAGEdge> incomingEdges() {
		// TODO Auto-generated method stub
		return super.incomingEdges();
	}

	@Override
	public Set<DAGEdge> outgoingEdges() {
		// TODO Auto-generated method stub
		return super.outgoingEdges();
	}

	public String getPropertyStringValue(String propertyName) {
		if (propertyName.equals(ImplementationPropertyNames.Vertex_OperatorDef)) {
			return getMapping().getEffectiveOperator().getComponent().getVlnv()
					.getName();
		} else if (propertyName
				.equals(ImplementationPropertyNames.Vertex_Available_Operators)) {
			return getInit().getInitialOperatorList().toString();
		} else if (propertyName
				.equals(ImplementationPropertyNames.Vertex_originalVertexId)) {
			return getInit().getParentVertex().getId();
		} else if (propertyName
				.equals(ImplementationPropertyNames.Task_duration)) {
			return String.valueOf(getTiming().getCost());
		} else if (propertyName
				.equals(ImplementationPropertyNames.Vertex_schedulingOrder)) {
			return String.valueOf(this.getTiming().getTotalOrder(this));
		} else if (propertyName
				.equals(ImplementationPropertyNames.Vertex_Operator)) {
			return this.getMapping().getEffectiveComponent().getInstanceName();
		}
		return super.getPropertyStringValue(propertyName);
	}

	public int getTotalOrder() {
		return getTiming().getTotalOrder(this);
	}

	public void setTotalOrder(int schedulingTotalOrder) {
		getTiming().setTotalOrder(this.getName(), schedulingTotalOrder);
	}

	/**
	 * Timing properties are store in the graph and possibly shared with other
	 * vertices.
	 */
	public VertexTiming getTiming() {
		return ((MapperDAG) getBase()).getTimings().getTiming(getName());
	}

	/**
	 * Mapping properties are store in the graph and possibly shared with other
	 * vertices.
	 */
	public VertexMapping getMapping() {
		return ((MapperDAG) getBase()).getMappings().getMapping(getName());
	}

}
