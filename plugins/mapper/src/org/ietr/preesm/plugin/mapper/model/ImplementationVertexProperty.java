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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.SpecialVertexManager;

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
	 * Corresponding vertex
	 */
	private MapperDAGVertex parentVertex;

	/**
	 * The order in the schedule of a processor is determined by the order of
	 * the calls to implant() method.
	 */
	private int schedulingTotalOrder;

	public ImplementationVertexProperty(MapperDAGVertex parentVertex) {
		super();
		effectiveComponent = Operator.NO_COMPONENT;
		this.parentVertex = parentVertex;
		schedulingTotalOrder = -1;
	}

	@Override
	public ImplementationVertexProperty clone() {

		ImplementationVertexProperty property = new ImplementationVertexProperty(
				null);
		property.setEffectiveComponent(this.getEffectiveComponent());
		property.setSchedTotalOrder(this.schedulingTotalOrder);
		return property;
	}

	public void setParentVertex(MapperDAGVertex parentVertex) {
		this.parentVertex = parentVertex;
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

	/**
	 * Returns all the operators that can execute the vertex at the given time
	 */
	public List<Operator> getAdaptiveOperatorList() {
		List<Operator> localOperators = null;
		InitialVertexProperty initProp = parentVertex
				.getInitialVertexProperty();

		// For a non special vertex, the available operators are simply the ones
		// allowed in scenario
		if (!SpecialVertexManager.isSpecial(parentVertex)) {
			List<MapperDAGVertex> precedingJoins = getPrecedingJoins();

			localOperators = parentVertex.getInitialVertexProperty()
					.getInitialOperatorList();

			if (precedingJoins.isEmpty()) {
				localOperators = parentVertex.getInitialVertexProperty()
						.getInitialOperatorList();
			} else {
				// If we follow at least one implode, the implode fixes the
				// mapping of the current vertex
				Operator o = null;
				for (MapperDAGVertex precJoin : precedingJoins) {
					Operator newO = precJoin.getImplementationVertexProperty()
							.getEffectiveOperator();
					if (o != null && !newO.equals(o)) {
						o = null;
						break;
					}
					o = newO;
				}

				if (o != null) {
					localOperators = new ArrayList<Operator>();
					localOperators.add(o);
				} else {
					localOperators = parentVertex.getInitialVertexProperty()
							.getInitialOperatorList();
				}
			}

			// forks and broadcasts are mapped if possible on the same operator
			// as their predecessor.
		} else if (SpecialVertexManager.isFork(parentVertex)
				|| SpecialVertexManager.isBroadCast(parentVertex)) {
			Set<Operator> predOs = getPredImplantations();
			localOperators = new ArrayList<Operator>();
			for (Operator o : predOs) {
				if (initProp.isImplantable(o)) {
					localOperators.add(o);
				}
			}
			if (localOperators.isEmpty()) {
				localOperators = parentVertex.getInitialVertexProperty()
						.getInitialOperatorList();
			}
			// The operator set of an implode is calculated depending on its
			// successor operator set. It compels its successor mapping
		} else if (SpecialVertexManager.isJoin(parentVertex)) {
			localOperators = new ArrayList<Operator>();
			for (Operator o : initProp.getInitialOperatorList()) {
				if (initProp.isSuccImplantable(o)) {
					localOperators.add(o);
				}
			}

			if (localOperators.isEmpty()) {
				PreesmLogger
						.getLogger()
						.log(
								Level.SEVERE,
								"There is a conflict in the constraints of join/implode vertex "
										+ parentVertex
										+ "and of its successors. You can for instance add cores for implode/explode/broadcast in the scenario.");
				localOperators = parentVertex.getInitialVertexProperty()
						.getInitialOperatorList();
			}
		} else {
			localOperators = parentVertex.getInitialVertexProperty()
					.getInitialOperatorList();
		}

		if (parentVertex.getName().contains("bit_proc")) {
			int i = 0;
			i++;
		}

		return localOperators;
	}

	/**
	 * Returns the operator of the first non special predecessor
	 */
	public Set<Operator> getPredImplantations() {

		Set<Operator> predImplantations = new HashSet<Operator>();

		for (MapperDAGVertex pred : parentVertex.getPredecessorSet(true)) {
			if (pred == null) {
			} else if (SpecialVertexManager.isSpecial(pred)) {
				predImplantations.addAll(pred.getImplementationVertexProperty()
						.getPredImplantations());
			} else {
				predImplantations.add(pred.getImplementationVertexProperty()
						.getEffectiveOperator());
			}
		}

		return predImplantations;
	}

	public List<MapperDAGVertex> getPrecedingJoins() {

		List<MapperDAGVertex> precedingJoins = new ArrayList<MapperDAGVertex>();

		for (MapperDAGVertex pred : parentVertex.getPredecessorSet(true)) {
			if (pred != null && SpecialVertexManager.isJoin(pred)) {
				precedingJoins.add(pred);
			}
		}

		return precedingJoins;
	}
}
