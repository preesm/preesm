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

/**
 * Property added to a DAG vertex to give its timing properties
 * Only used within ABCs.
 * 
 * @author pmenuet
 */
public class TimingVertexProperty {

	static public final int UNAVAILABLE = -1;

	/**
	 * B Level is the time between the vertex start and the total end of
	 * execution. Valid only with infinite homogeneous architecture simulator
	 */
	private int bLevel;

	/**
	 * True if the current bLevel is valid. Used by time keeper to ensure the b
	 * level has not been broken by a DAG branch
	 */
	private boolean validBLevel;

	/**
	 * T Level is the time between the start of execution and the vertex start
	 */
	private int tLevel;

	/**
	 * time to execute the vertex
	 */
	private int cost;

	public TimingVertexProperty() {
		super();
		reset();
	}

	@Override
	public TimingVertexProperty clone() {
		TimingVertexProperty property = new TimingVertexProperty();
		property.setBlevel(this.getValidBlevel());
		property.setTlevel(this.getTlevel());
		property.setBlevelValidity(this.getBlevelValidity());
		property.setCost(this.getCost());
		return property;
	}

	/**
	 * returns the B level only if valid, UNAVAILABLE otherwise
	 */
	public int getValidBlevel() {

		if (validBLevel)
			return bLevel;
		else
			return UNAVAILABLE;
	}

	public int getBlevel() {

		return bLevel;
	}

	public void setBlevelValidity(boolean valid) {

		validBLevel = valid;
	}

	public boolean getBlevelValidity() {

		return validBLevel;
	}

	public int getTlevel() {
		return tLevel;
	}

	public boolean hasBlevel() {
		return (bLevel != UNAVAILABLE && validBLevel);
	}

	public boolean hasTlevel() {
		return (tLevel != UNAVAILABLE);
	}

	public void reset() {
		cost = UNAVAILABLE;
		tLevel = UNAVAILABLE;
		bLevel = UNAVAILABLE;
		validBLevel = false;
	}

	public void setBlevel(int blevel) {
		this.bLevel = blevel;
	}

	public void setTlevel(int tlevel) {
		this.tLevel = tlevel;
	}

	public String toString() {
		return "";
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public boolean hasCost() {
		return (this.cost != UNAVAILABLE);
	}

	public void resetCost() {
		setCost(UNAVAILABLE);
	}
}
