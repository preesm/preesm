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

package org.ietr.preesm.plugin.mapper.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.ietr.preesm.plugin.mapper.model.impl.OverheadVertex;
import org.ietr.preesm.plugin.mapper.model.impl.ReceiveVertex;
import org.ietr.preesm.plugin.mapper.model.impl.SendVertex;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;

/**
 * Represents a vertex in a DAG of type {@link MapperDAG} used in the mapper
 * 
 * @author mpelcat
 */
public class MapperDAGVertex extends DAGVertex {

	/**
	 * Properties set by mapper algorithm via architecture model
	 */
	protected ImplementationVertexProperty implementationVertexProperty;

	/**
	 * Properties set when converting sdf to dag
	 */
	protected InitialVertexProperty initialVertexProperty;

	/**
	 * Properties set by timekeeper
	 */
	protected TimingVertexProperty timingVertexProperty;

	public MapperDAGVertex() {

		this("default", "default", null);
	}
	
	public MapperDAGVertex(String id, MapperDAG base) {

		this(id, id, base);
	}
	
	public MapperDAGVertex(String id, String name, MapperDAG base) {

		super();
		
		this.setName(name);
		this.initialVertexProperty = new InitialVertexProperty();
		this.initialVertexProperty.setParentVertex(this);
		this.implementationVertexProperty = new ImplementationVertexProperty();
		this.timingVertexProperty = new TimingVertexProperty();

		this.setBase(base);
	}
	
	@Override
	public MapperDAGVertex clone() {

		MapperDAGVertex result = null;
			
		if(this instanceof OverheadVertex){
			result = new OverheadVertex(this.getId(), (MapperDAG)this.getBase());
		}
		else if(this instanceof SendVertex){
			result = new SendVertex(this.getId(), (MapperDAG)this.getBase());
		}
		else if(this instanceof ReceiveVertex){
			result = new ReceiveVertex(this.getId(), (MapperDAG)this.getBase());
		}
		else{
			result = new MapperDAGVertex(this.getId(), this
					.getName(), (MapperDAG)this.getBase());
		}
		
		
		result.setImplantationVertexProperty(this
				.getImplementationVertexProperty().clone());
		result.setInitialVertexProperty(this.getInitialVertexProperty().clone(result));
		result.setTimingVertexProperty(this.getTimingVertexProperty().clone());
		
		for(String propertyKey : this.getPropertyBean().keys()){
			Object property = this.getPropertyBean().getValue(propertyKey);
			result.getPropertyBean().setValue(propertyKey, property);
		}
		
		return result;
	}
	
	public ImplementationVertexProperty getImplementationVertexProperty() {
		return implementationVertexProperty;
	}

	public InitialVertexProperty getInitialVertexProperty() {
		return initialVertexProperty;
	}

	public TimingVertexProperty getTimingVertexProperty() {
		return timingVertexProperty;
	}

	public void setImplantationVertexProperty(
			ImplementationVertexProperty implementationVertexProperty) {
		this.implementationVertexProperty = implementationVertexProperty;
	}

	public void setInitialVertexProperty(
			InitialVertexProperty initialVertexProperty) {
		this.initialVertexProperty = initialVertexProperty;
	}

	public void setTimingVertexProperty(
			TimingVertexProperty timingVertexProperty) {
		this.timingVertexProperty = timingVertexProperty;
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
		if (implementationVertexProperty.hasEffectiveComponent()) {
			// If the vertex is implanted, displays its component and rank
			toString = getName()
					+ "("
					+ implementationVertexProperty.getEffectiveComponent()
							.toString() + ","
					+ implementationVertexProperty.getSchedTotalOrder()
					+ ")";
		} else {
			// If the vertex is not implanted, displays its weight
			toString = getName() + "(" + this.getNbRepeat() + ")";
		}
		
		if(initialVertexProperty.getTopologicalLevel() != -1){
			toString += "[" + initialVertexProperty.getTopologicalLevel() + "]";
		}
		
		return toString;
	}

	public Set<MapperDAGVertex> getPredecessorSet() {

		Set<MapperDAGVertex> temp = new HashSet<MapperDAGVertex>();
		Set<DAGEdge> incomingedgeset = this.incomingEdges();
		Iterator<DAGEdge> iter = incomingedgeset.iterator();

		while (iter.hasNext()) {

			MapperDAGEdge edge = (MapperDAGEdge)iter.next();
			temp.add((MapperDAGVertex)edge.getSource());

		}
		return temp;
	}

	public Set<MapperDAGVertex> getSuccessorSet() {

		Set<MapperDAGVertex> temp = new HashSet<MapperDAGVertex>();
		Set<DAGEdge> outgoingSet = this.outgoingEdges();
		Iterator<DAGEdge> iter = outgoingSet.iterator();

		while (iter.hasNext()) {

			MapperDAGEdge edge = (MapperDAGEdge)iter.next();
			temp.add((MapperDAGVertex)edge.getTarget());
		}
		return temp;
	}
}
