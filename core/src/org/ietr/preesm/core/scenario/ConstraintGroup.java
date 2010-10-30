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

package org.ietr.preesm.core.scenario;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.ietr.preesm.core.architecture.IOperator;
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * A ConstraintGroup associates a set of graph definitions and a set of
 * processing units on which they can be matched
 * 
 * @author mpelcat
 */
public class ConstraintGroup {

	/**
	 * The set of processing units available for the constraint group
	 */
	private Set<IOperator> operators;

	/**
	 * The set of graphs belonging to the constraint group
	 */
	private Set<SDFAbstractVertex> vertices;

	public ConstraintGroup() {
		operators = new HashSet<IOperator>();
		vertices = new HashSet<SDFAbstractVertex>();

	}

	public void addOperator(IOperator opdef) {
		if (!hasOperator(opdef)) {
			operators.add(opdef);
		}

	}

	/**
	 * When a vertex is added to the constraints, its hierarchical path is
	 * added in its properties in order to separate distinct vertices with same
	 * name
	 */
	public void addVertex(SDFAbstractVertex vertex) {
		if (!hasVertex(vertex)) {
			vertices.add(vertex);

		}
	}

	public void addVertices(Set<SDFAbstractVertex> vertexSet) {
		for (SDFAbstractVertex vertex : vertexSet) {
			addVertex(vertex);
		}
	}

	public void removeVertices(Set<SDFAbstractVertex> vertexSet) {
		for (SDFAbstractVertex vertex : vertexSet) {
			removeVertex(vertex);
		}
	}

	public Set<IOperator> getOperators() {
		return new HashSet<IOperator>(operators);
	}

	public Set<SDFAbstractVertex> getVertices() {
		return new HashSet<SDFAbstractVertex>(vertices);
	}

	public boolean hasOperator(IOperator operator) {
		boolean b = false;

		Iterator<IOperator> it = operators.iterator();
		while (it.hasNext() && !b) {
			IOperator currentop = it.next();
			b = currentop.equals(operator);
		}

		return b;
	}

	public boolean hasVertex(SDFAbstractVertex vertex) {
		boolean b = false;

		Iterator<SDFAbstractVertex> it = vertices.iterator();
		while (it.hasNext() && !b) {
			SDFAbstractVertex v = it.next();
			b = (v.getInfo().equals(vertex.getInfo()));
		}

		return b;
	}

	public void removeOperator(IOperator operator) {
		Iterator<IOperator> it = operators.iterator();
		while (it.hasNext()) {
			IOperator currentop = it.next();
			if (currentop.equals(operator)) {
				it.remove();
			}
		}
	}

	public void removeVertex(SDFAbstractVertex vertex) {

		Iterator<SDFAbstractVertex> it = vertices.iterator();
		while (it.hasNext()) {
			SDFAbstractVertex v = it.next();
			if ((v.getInfo().equals(vertex.getInfo()))) {
				it.remove();

			}
		}

	}

	@Override
	public String toString() {
		String s = "<";
		s += operators.toString();
		s += vertices.toString();
		s += ">";
		
		return s;
	}
}
