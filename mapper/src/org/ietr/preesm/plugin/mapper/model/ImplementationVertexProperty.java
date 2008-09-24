package org.ietr.preesm.plugin.mapper.model;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.Medium;
import org.ietr.preesm.core.architecture.Operator;

/**
 * Properties of an implanted vertex
 * 
 * @author mpelcat
 */
public class ImplementationVertexProperty {

	/**
	 * Operator to which the vertex has been affected by the mapping algorithm
	 */
	private ArchitectureComponent effectiveComponent;

	/**
	 * The order in the schedule of a processor is determined by the order of
	 * the calls to implant() method.
	 */
	private int schedulingTotalOrder;

	public ImplementationVertexProperty() {
		super();
		effectiveComponent = Operator.NO_COMPONENT;

		schedulingTotalOrder = -1;
	}

	@Override
	public ImplementationVertexProperty clone() {

		ImplementationVertexProperty property = new ImplementationVertexProperty();
		property.setEffectiveComponent(this.getEffectiveComponent());
		property.setSchedulingTotalOrder(this.schedulingTotalOrder);
		return property;
	}

	/**
	 * A computation vertex has an effective operator
	 */
	public Operator getEffectiveOperator() {
		if (effectiveComponent instanceof Operator)
			return (Operator) effectiveComponent;
		else
			return (Operator) Operator.NO_COMPONENT;
	}

	public boolean hasEffectiveOperator() {
		return getEffectiveOperator() != Operator.NO_COMPONENT;
	}

	public void setEffectiveOperator(Operator effectiveOperator) {
		this.effectiveComponent = effectiveOperator;
	}

	/**
	 * A Communication vertex has an effective medium
	 */
	public Medium getEffectiveMedium() {
		if (effectiveComponent instanceof Medium)
			return (Medium) effectiveComponent;
		else
			return (Medium) Medium.NO_COMPONENT;
	}

	public boolean hasEffectiveMedium() {
		return getEffectiveMedium() != Operator.NO_COMPONENT;
	}

	public void setEffectiveMedium(Medium effectiveMedium) {
		this.effectiveComponent = effectiveMedium;
	}

	/**
	 * Effective component is common to communication and computation vertices
	 */
	public ArchitectureComponent getEffectiveComponent() {
		return effectiveComponent;
	}

	public boolean hasEffectiveComponent() {
		return getEffectiveComponent() != ArchitectureComponent.NO_COMPONENT;
	}

	public void setEffectiveComponent(ArchitectureComponent component) {
		this.effectiveComponent = component;
	}

	public int getSchedulingTotalOrder() {
		return schedulingTotalOrder;
	}

	public void setSchedulingTotalOrder(int schedulingTotalOrder) {
		this.schedulingTotalOrder = schedulingTotalOrder;
	}

}
