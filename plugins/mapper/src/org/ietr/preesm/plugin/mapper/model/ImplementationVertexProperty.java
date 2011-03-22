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

package org.ietr.preesm.plugin.mapper.model;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.simplemodel.Operator;

/**
 * Properties of a mapped vertex
 * 
 * @author mpelcat
 */
public class ImplementationVertexProperty {

	/**
	 * Operator to which the vertex has been affected by the mapping algorithm
	 */
	private ArchitectureComponent effectiveComponent;

	/**
	 * This object is shared between all vertices that share relative
	 * constraints.
	 */
	private RelativeConstraint relativeConstraint = null;

	/**
	 * The order in the schedule of a processor is determined by the order of
	 * the calls to map() method.
	 */
	private int schedulingTotalOrder;

	public ImplementationVertexProperty(MapperDAGVertex parentVertex) {
		super();
		effectiveComponent = Operator.NO_COMPONENT;
		schedulingTotalOrder = -1;
	}

	public RelativeConstraint getRelativeConstraint() {
		return relativeConstraint;
	}

	public void setRelativeConstraint(RelativeConstraint relativeConstraints) {
		this.relativeConstraint = relativeConstraints;
	}

	@Override
	public ImplementationVertexProperty clone() {

		ImplementationVertexProperty property = new ImplementationVertexProperty(
				null);
		property.setEffectiveComponent(this.getEffectiveComponent());
		property.setSchedTotalOrder(this.schedulingTotalOrder);
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

	public int getSchedTotalOrder() {
		return schedulingTotalOrder;
	}

	public void setSchedTotalOrder(int schedulingTotalOrder) {
		this.schedulingTotalOrder = schedulingTotalOrder;
	}

}
