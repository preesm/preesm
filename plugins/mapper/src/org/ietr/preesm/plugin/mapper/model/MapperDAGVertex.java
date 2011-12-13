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

package org.ietr.preesm.plugin.mapper.model;

import java.util.HashSet;
import java.util.Set;

import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.dag.DAGVertex;

import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.plugin.abc.order.IScheduleElement;
import org.ietr.preesm.plugin.mapper.model.impl.OverheadVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdge;
import org.ietr.preesm.plugin.mapper.model.impl.ReceiveVertex;
import org.ietr.preesm.plugin.mapper.model.impl.SendVertex;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;


/**
 * Represents a vertex in a DAG of type {@link MapperDAG} used in the mapper
 * 
 * @author mpelcat
 */
public class MapperDAGVertex extends DAGVertex implements IScheduleElement {

	/**
	 * Properties set by mapper algorithm via architecture model
	 */
	//protected ImplementationVertexProperty implementationVertexProperty;
	private static final String IMPLEMENTATION_PROPERTY = "IMPLEMENTATION_PROPERTY";

	/**
	 * Properties set when converting sdf to dag
	 */
	private static final String INITIAL_PROPERTY = "INITIAL_PROPERTY";
	//protected InitialVertexProperty initialVertexProperty;

	/**
	 * Properties set by timekeeper
	 */
	private static final String TIMING_PROPERTY = "TIMING_PROPERTY";
	//protected TimingVertexProperty timingVertexProperty;
	
	static {
		{
			public_properties.add(ImplementationPropertyNames.Vertex_OperatorDef);
			public_properties.add(ImplementationPropertyNames.Vertex_Available_Operators);
			public_properties.add(ImplementationPropertyNames.Vertex_originalVertexId);
			public_properties.add(ImplementationPropertyNames.Task_duration);
			public_properties.add(ImplementationPropertyNames.Vertex_schedulingOrder);
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
		this.getPropertyBean().setValue(INITIAL_PROPERTY, new InitialVertexProperty());
		this.getInitialVertexProperty().setParentVertex(this);
		this.getPropertyBean().setValue(IMPLEMENTATION_PROPERTY,new ImplementationVertexProperty(
				this));
		this.getPropertyBean().setValue(TIMING_PROPERTY, new TimingVertexProperty());

		this.setBase(base);
	}

	@Override
	public MapperDAGVertex clone() {

		MapperDAGVertex result = null;

		if (this instanceof OverheadVertex) {
			result = new OverheadVertex(this.getId(),
					(MapperDAG) this.getBase());
		} else if (this instanceof SendVertex) {
			result = new SendVertex(this.getId(), (MapperDAG) this.getBase());
		} else if (this instanceof ReceiveVertex) {
			result = new ReceiveVertex(this.getId(), (MapperDAG) this.getBase());
		} else if (this instanceof TransferVertex) {
			TransferVertex t = (TransferVertex) this;
			result = new TransferVertex(this.getId(),
					(MapperDAG) this.getBase(), t.getSource(), t.getTarget(),
					t.getRouteStepIndex(), t.getNodeIndex());
		} else {
			result = new MapperDAGVertex(this.getId(), this.getName(),
					(MapperDAG) this.getBase());
		}

		ImplementationVertexProperty impProp = this
				.getImplementationVertexProperty().clone();
		result.setImplementationVertexProperty(impProp);

		result.setInitialVertexProperty(this.getInitialVertexProperty().clone(
				result));
		result.setTimingVertexProperty(this.getTimingVertexProperty().clone());

		for (String propertyKey : this.getPropertyBean().keys()) {
			Object property = this.getPropertyBean().getValue(propertyKey);
			result.getPropertyBean().setValue(propertyKey, property);
		}

		return result;
	}

	public ImplementationVertexProperty getImplementationVertexProperty() {
		return (ImplementationVertexProperty) this.getPropertyBean().getValue(IMPLEMENTATION_PROPERTY);
	}

	public InitialVertexProperty getInitialVertexProperty() {
		return (InitialVertexProperty) this.getPropertyBean().getValue(INITIAL_PROPERTY);
	}

	public TimingVertexProperty getTimingVertexProperty() {
		return (TimingVertexProperty) this.getPropertyBean().getValue(TIMING_PROPERTY);
	}

	public void setImplementationVertexProperty(
			ImplementationVertexProperty implementationVertexProperty) {
		this.getPropertyBean().setValue(IMPLEMENTATION_PROPERTY, implementationVertexProperty);
	}

	public void setInitialVertexProperty(
			InitialVertexProperty initialVertexProperty) {
		this.getPropertyBean().setValue(INITIAL_PROPERTY, initialVertexProperty);
	}

	public void setTimingVertexProperty(
			TimingVertexProperty timingVertexProperty) {
		this.getPropertyBean().setValue(TIMING_PROPERTY, timingVertexProperty) ;
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
		if (this.getImplementationVertexProperty().hasEffectiveComponent()) {
			// If the vertex is mapped, displays its component and rank
			toString = getName()
					+ "("
					+ this.getImplementationVertexProperty().getEffectiveComponent()
							.toString() + ","
					+ this.getImplementationVertexProperty().getSchedTotalOrder() + ")";
		} else {
			// If the vertex is not mapped, displays its weight
			toString = getName() + "(" + this.getNbRepeat() + ")";
		}

		if (this.getInitialVertexProperty().getTopologicalLevel() != -1) {
			toString += "[" + this.getInitialVertexProperty().getTopologicalLevel() + "]";
		}

		return toString;
	}

	/**
	 * Getting all predecessors, ignoring or not the precedence edges
	 */
	public Set<MapperDAGVertex> getPredecessorSet(boolean ignorePrecedence) {

		Set<MapperDAGVertex> predSet = new HashSet<MapperDAGVertex>();
		Set<DAGEdge> incomingSet = this.incomingEdges();

		if (ignorePrecedence) {
			for (DAGEdge edge : incomingSet) {
				if (!(edge instanceof PrecedenceEdge)) {
					predSet.add((MapperDAGVertex) edge.getSource());
				}
			}
		} else {
			for (DAGEdge edge : incomingSet) {
				predSet.add((MapperDAGVertex) edge.getSource());
			}
		}
		return predSet;
	}

	/**
	 * Getting all successors, ignoring or not the precedence edges
	 */
	public Set<MapperDAGVertex> getSuccessorSet(boolean ignorePrecedence) {

		Set<MapperDAGVertex> succSet = new HashSet<MapperDAGVertex>();
		Set<DAGEdge> outgoingSet = this.outgoingEdges();

		if (ignorePrecedence) {
			for (DAGEdge edge : outgoingSet) {
				if (!(edge instanceof PrecedenceEdge)) {
					succSet.add((MapperDAGVertex) edge.getTarget());
				}
			}
		} else {
			for (DAGEdge edge : outgoingSet) {
				succSet.add((MapperDAGVertex) edge.getTarget());
			}
		}
		return succSet;
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
	
	public String getPropertyStringValue(String propertyName){
		if(propertyName.equals(ImplementationPropertyNames.Vertex_OperatorDef)){
			return getImplementationVertexProperty().getEffectiveOperator()
			.getComponent().getVlnv().getName();
		}else if(propertyName.equals(ImplementationPropertyNames.Vertex_Available_Operators)){
			return getInitialVertexProperty().getInitialOperatorList()
			.toString() ;
		}else if(propertyName.equals(ImplementationPropertyNames.Vertex_originalVertexId)){
			return getInitialVertexProperty().getParentVertex().getId() ;
		}else if(propertyName.equals(ImplementationPropertyNames.Task_duration)){
			return String.valueOf(getTimingVertexProperty().getCost() );
		}else if(propertyName.equals(ImplementationPropertyNames.Vertex_schedulingOrder)){
			return String.valueOf(this.getImplementationVertexProperty().getSchedTotalOrder());
		}else if(propertyName.equals(ImplementationPropertyNames.Vertex_Operator)){
			return this.getImplementationVertexProperty().getEffectiveComponent().getInstanceName();
		}
		return super.getPropertyStringValue(propertyName);
	}

}
