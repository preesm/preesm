/**
 * PREESM
 * Copyright IETR 2007 under CeCILL license.
 *
 * Jonathan PIAT <Jonathan.Piat@ens.insa-rennes.fr>
 * Matthieu WIPLIEZ <Matthieu.Wipliez@insa-rennes.fr>
 * Maxime PELCAT <Maxime.Pelcat@insa-rennes.fr>
 */
package org.ietr.preesm.core.constraints;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * A ConstraintGroup associates a set of graph definitions and a set of
 * processing units on which they can be matched
 * 
 * @author mpelcat
 */
public class ConstraintGroup {

	/**
	 * Name of the constraint group
	 */
	private String id;

	/**
	 * The set of processing units available for the constraint group
	 */
	private List<OperatorDefinition> operatordefinitions;

	/**
	 * The set of graphs belonging to the constraint group
	 */
	private List<SDFAbstractVertex> vertices;

	public ConstraintGroup(String groupId) {
		id = groupId;
		operatordefinitions = new ArrayList<OperatorDefinition>();
		vertices = new ArrayList<SDFAbstractVertex>();

	}

	public void addOperatorDefinition(OperatorDefinition opdef) {
		if (!hasOperatorDefinition(opdef)) {
			operatordefinitions.add(opdef);
		}

	}

	public void addVertex(SDFAbstractVertex vertex) {
		if (!hasVertex(vertex)) {
			vertices.add(vertex);

		}

	}

	public String getId() {
		return id;
	}

	public List<OperatorDefinition> getOperatorDefinitions() {
		return new ArrayList<OperatorDefinition>(operatordefinitions);
	}

	public List<SDFAbstractVertex> getVertices() {
		return new ArrayList<SDFAbstractVertex>(vertices);
	}

	public boolean hasOperatorDefinition(OperatorDefinition operator) {
		boolean b = false;

		Iterator<OperatorDefinition> it = operatordefinitions.iterator();
		while (it.hasNext() && !b) {
			OperatorDefinition currentop = it.next();
			b = (currentop == operator);
		}

		return b;
	}

	public boolean hasVertex(SDFAbstractVertex vertex) {
		boolean b = false;

		Iterator<SDFAbstractVertex> it = vertices.iterator();
		while (it.hasNext() && !b) {
			SDFAbstractVertex v = it.next();
			b = (v == vertex);
		}

		return b;
	}

	public void removeOperatorDefinition(OperatorDefinition operator) {
		Iterator<OperatorDefinition> it = operatordefinitions.iterator();
		while (it.hasNext()) {
			OperatorDefinition currentop = it.next();
			if (currentop == operator) {
				it.remove();
			}
		}
	}

	public void removeVertex(SDFAbstractVertex vertex) {

		Iterator<SDFAbstractVertex> it = vertices.iterator();
		while (it.hasNext()) {
			SDFAbstractVertex v = it.next();
			if (v == vertex) {
				it.remove();

			}
		}

	}

	public void setId(String groupId) {
		id = groupId;
	}
}
