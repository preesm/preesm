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

package org.ietr.preesm.mapper.model.property;

import java.util.HashMap;
import java.util.Map;

import org.ietr.preesm.mapper.model.MapperDAGVertex;

/**
 * Property added to a DAG vertex to give its timing properties.
 * Can be shared by several synchronous vertices.
 * 
 * @author pmenuet
 * @author mpelcat
 */
public class VertexTiming extends GroupProperty {

	static public final long UNAVAILABLE = -1;

	/**
	 * time to execute the vertex
	 */
	private long cost;

	/**
	 * B Level is the time between the vertex start and the total end of
	 * execution. Valid only with infinite homogeneous architecture simulator
	 */
	private long bLevel;

	/**
	 * T Level is the time between the start of execution and the vertex start
	 */
	private long tLevel;

	/**
	 * The total order range in the schedule.
	 * Each vertex ID is associated to its total order
	 * IDs must be consecutive to ensure possibility of synchronous scheduling!
	 */
	private Map<String,Integer> totalOrders;
	
	public VertexTiming() {
		super();
		reset();
	}

	@Override
	public VertexTiming clone() {
		VertexTiming property = (VertexTiming)super.clone();
		property.setBLevel(this.getBLevel());
		property.setTLevel(this.getTLevel());
		property.setCost(this.getCost());
		for(String id : totalOrders.keySet()){
			property.setTotalOrder(id, totalOrders.get(id));
		}
		return property;
	}

	public void reset() {
		cost = UNAVAILABLE;
		tLevel = UNAVAILABLE;
		bLevel = UNAVAILABLE;
		totalOrders = new HashMap<String,Integer>();
	}

	@Override
	public String toString() {
		return "";
	}

	public long getCost() {
		return cost;
	}

	public void setCost(long cost) {
		this.cost = cost;
	}

	public boolean hasCost() {
		return (this.cost != UNAVAILABLE);
	}

	public void resetCost() {
		setCost(UNAVAILABLE);
	}

	public long getBLevel() {
		return bLevel;
	}

	public void setBLevel(long newbLevel) {
		this.bLevel = newbLevel;
	}

	public void resetBLevel() {
		bLevel = UNAVAILABLE;
	}

	public boolean hasBLevel() {
		return bLevel != UNAVAILABLE;
	}

	public long getTLevel() {
		return tLevel;
	}

	public void setTLevel(long newtLevel) {
		this.tLevel = newtLevel;
	}

	public void resetTLevel() {
		tLevel = UNAVAILABLE;
	}

	public boolean hasTLevel() {
		return (tLevel != UNAVAILABLE);
	}

	public int getTotalOrder(MapperDAGVertex v) {
		return this.totalOrders.get(v.getName());
	}

	public void setTotalOrder(String vertexId, int totalOrder) {
		this.totalOrders.put(vertexId, totalOrder);
	}
}
