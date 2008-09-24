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
