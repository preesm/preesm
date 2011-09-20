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
	private Set<String> operatorIds;

	/**
	 * The set of graphs belonging to the constraint group
	 */
	private Set<String> sdfVertexPaths;

	public ConstraintGroup() {
		operatorIds = new HashSet<String>();
		sdfVertexPaths = new HashSet<String>();

	}

	public void addOperatorId(String opId) {
		if (!hasOperatorId(opId)) {
			operatorIds.add(opId);
		}

	}

	/**
	 * When a vertex is added to the constraints, its hierarchical path is added
	 * in its properties in order to separate distinct vertices with same name
	 */
	public void addVertexPath(String vertexId) {
		if (!hasVertexPath(vertexId)) {
			sdfVertexPaths.add(vertexId);

		}
	}

	public void addVertexPaths(Set<String> vertexIdSet) {
		for (String vertexId : vertexIdSet) {
			addVertexPath(vertexId);
		}
	}

	public void removeVertexPaths(Set<String> vertexIdSet) {
		for (String vertexId : vertexIdSet) {
			removeVertexPath(vertexId);
		}
	}

	public Set<String> getOperatorIds() {
		return new HashSet<String>(operatorIds);
	}

	public Set<String> getVertexPaths() {
		return new HashSet<String>(sdfVertexPaths);
	}

	public boolean hasOperatorId(String operatorId) {

		for (String opId : operatorIds) {
			if (opId.equals(operatorId)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasVertexPath(String vertexInfo) {

		for (String vId : sdfVertexPaths) {
			if (vId.equals(vertexInfo)) {
				return true;
			}
		}

		return false;
	}

	public void removeOperatorId(String operatorId) {
		Iterator<String> it = operatorIds.iterator();
		while (it.hasNext()) {
			String currentopId = it.next();
			if (currentopId.equals(operatorId)) {
				it.remove();
			}
		}
	}

	public void removeVertexPath(String sdfVertexInfo) {
		Iterator<String> it = sdfVertexPaths.iterator();
		while (it.hasNext()) {
			String v = it.next();
			if ((v.equals(sdfVertexInfo))) {
				it.remove();
			}
		}

	}

	@Override
	public String toString() {
		String s = "<";
		s += operatorIds.toString();
		s += sdfVertexPaths.toString();
		s += ">";

		return s;
	}
}
