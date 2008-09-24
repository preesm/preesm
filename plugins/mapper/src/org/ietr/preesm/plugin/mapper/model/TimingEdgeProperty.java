/**
 * 
 */
package org.ietr.preesm.plugin.mapper.model;

/**
 * Property added to a DAG edge to give its timing properties.
 * Only used within ABCs.
 * 
 * @author mpelcat
 */
public class TimingEdgeProperty {
	static public final int UNAVAILABLE = -1;

	/**
	 * time to execute the edge
	 */
	private int cost;

	public TimingEdgeProperty() {
		super();
		reset();
	}

	@Override
	public TimingEdgeProperty clone() {
		TimingEdgeProperty property = new TimingEdgeProperty();
		property.setCost(this.getCost());
		return property;
	}

	public void reset() {
		cost = UNAVAILABLE;
	}

	public String toString() {
		return "cost: " + cost;
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
