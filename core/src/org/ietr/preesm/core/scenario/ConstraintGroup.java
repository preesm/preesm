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


/**
 * PREESM
 * Copyright IETR 2007 under CeCILL license.
 *
 * Jonathan PIAT <Jonathan.Piat@ens.insa-rennes.fr>
 * Matthieu WIPLIEZ <Matthieu.Wipliez@insa-rennes.fr>
 * Maxime PELCAT <Maxime.Pelcat@insa-rennes.fr>
 */
package org.ietr.preesm.core.scenario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
	 * The set of processing units available for the constraint group
	 */
	private Set<OperatorDefinition> operatordefinitions;

	/**
	 * The set of graphs belonging to the constraint group
	 */
	private Set<SDFAbstractVertex> vertices;

	public ConstraintGroup() {
		operatordefinitions = new HashSet<OperatorDefinition>();
		vertices = new HashSet<SDFAbstractVertex>();

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

	public void addVertices(Set<SDFAbstractVertex> vertexSet) {
		for(SDFAbstractVertex vertex:vertexSet){
			addVertex(vertex);
		}
	}

	public void removeVertices(Set<SDFAbstractVertex> vertexSet) {
		for(SDFAbstractVertex vertex:vertexSet){
			removeVertex(vertex);
		}
	}
	
	

	public Set<OperatorDefinition> getOperatorDefinitions() {
		return new HashSet<OperatorDefinition>(operatordefinitions);
	}

	public Set<SDFAbstractVertex> getVertices() {
		return new HashSet<SDFAbstractVertex>(vertices);
	}

	public boolean hasOperatorDefinition(OperatorDefinition operator) {
		boolean b = false;

		Iterator<OperatorDefinition> it = operatordefinitions.iterator();
		while (it.hasNext() && !b) {
			OperatorDefinition currentop = it.next();
			b = currentop.equals(operator);
		}

		return b;
	}

	public boolean hasVertex(SDFAbstractVertex vertex) {
		boolean b = false;

		Iterator<SDFAbstractVertex> it = vertices.iterator();
		while (it.hasNext() && !b) {
			SDFAbstractVertex v = it.next();
			b = (v.getName().equals(vertex.getName()));
		}

		return b;
	}

	public void removeOperatorDefinition(OperatorDefinition operator) {
		Iterator<OperatorDefinition> it = operatordefinitions.iterator();
		while (it.hasNext()) {
			OperatorDefinition currentop = it.next();
			if (currentop.equals(operator)) {
				it.remove();
			}
		}
	}

	public void removeVertex(SDFAbstractVertex vertex) {

		Iterator<SDFAbstractVertex> it = vertices.iterator();
		while (it.hasNext()) {
			SDFAbstractVertex v = it.next();
			if ((v.getName().equals(vertex.getName()))) {
				it.remove();

			}
		}

	}
}
