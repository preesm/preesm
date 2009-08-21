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

import java.util.List;

/**
 * Property added to a DAG vertex to give its timing properties Only used within
 * ABCs.
 * 
 * @author pmenuet
 * @author mpelcat
 */
public class TimingVertexProperty {

	static public final long UNAVAILABLE = -1;

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
	 * time to execute the vertex
	 */
	private long cost;

	/**
	 * Vertices that must appear in the Gantt at the exact same time as the
	 * current vertex.
	 */
	private List<MapperDAGVertex> synchronizedVertices = null;

	/**
	 * B Level is the time between the vertex start and the total end of
	 * execution. Valid only with infinite homogeneous architecture simulator
	 */
	private long newbLevel;

	/**
	 * T Level is the time between the start of execution and the vertex start
	 */
	private long newtLevel;

	public TimingVertexProperty() {
		super();
		reset();
	}

	@Override
	public TimingVertexProperty clone() {
		TimingVertexProperty property = new TimingVertexProperty();
		property.setBlevel(this.getBlevel());
		property.setTlevel(this.getTlevel());
		property.setCost(this.getCost());
		return property;
	}

	public long getBlevel() {

		return bLevel;
	}

	public long getTlevel() {
		return tLevel;
	}

	public boolean hasBlevel() {
		return bLevel != UNAVAILABLE;
	}

	public boolean hasTlevel() {
		return (tLevel != UNAVAILABLE);
	}

	public void reset() {
		cost = UNAVAILABLE;
		tLevel = UNAVAILABLE;
		bLevel = UNAVAILABLE;
		newtLevel = UNAVAILABLE;
		newbLevel = UNAVAILABLE;
		
		synchronizedVertices = null;
	}

	public void setBlevel(long blevel) {
		this.bLevel = blevel;
	}

	public void setTlevel(long tlevel) {
		this.tLevel = tlevel;
	}

	public void resetTLevel() {
		this.tLevel = UNAVAILABLE;
	}

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

	public List<MapperDAGVertex> getSynchronizedVertices() {
		return synchronizedVertices;
	}

	public void setSynchronizedVertices(
			List<MapperDAGVertex> synchronizedVertices) {
		this.synchronizedVertices = synchronizedVertices;
	}

	public long getNewbLevel() {
		return newbLevel;
	}

	public void setNewbLevel(long newbLevel) {
		this.newbLevel = newbLevel;
	}

	public long getNewtLevel() {
		return newtLevel;
	}

	public void setNewtLevel(long newtLevel) {
		this.newtLevel = newtLevel;
	}

	public boolean hasNewblevel() {
		return newbLevel != UNAVAILABLE;
	}

	public boolean hasNewtLevel() {
		return (newtLevel != UNAVAILABLE);
	}

	
}
