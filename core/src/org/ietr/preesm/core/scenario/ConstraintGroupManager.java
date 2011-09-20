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
import java.util.Set;

import org.ietr.preesm.core.scenario.serialize.ExcelConstraintsParser;
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * container and manager of Constraint groups. It can load and store constraint
 * groups
 * 
 * @author mpelcat
 */
public class ConstraintGroupManager {

	/**
	 * List of all constraint groups
	 */
	private Set<ConstraintGroup> constraintgroups;

	/**
	 * Path to a file containing constraints
	 */
	private String excelFileURL = "";

	public ConstraintGroupManager() {
		constraintgroups = new HashSet<ConstraintGroup>();
	}

	public void addConstraintGroup(ConstraintGroup cg) {

		constraintgroups.add(cg);
	}

	/**
	 * Adding a simple constraint on one vertex and one operator
	 */
	public void addConstraint(String opId, SDFAbstractVertex vertex) {

		Set<ConstraintGroup> cgSet = getOpConstraintGroups(opId);

		if (cgSet.isEmpty()) {
			ConstraintGroup cg = new ConstraintGroup();
			cg.addOperatorId(opId);
			cg.addVertexPath(vertex.getInfo());
			constraintgroups.add(cg);
		} else {
			((ConstraintGroup) cgSet.toArray()[0]).addVertexPath(vertex
					.getInfo());
		}
	}

	/**
	 * Adding a constraint group on several vertices and one core
	 */
	public void addConstraints(String opId, Set<String> vertexSet) {

		Set<ConstraintGroup> cgSet = getOpConstraintGroups(opId);

		if (cgSet.isEmpty()) {
			ConstraintGroup cg = new ConstraintGroup();
			cg.addOperatorId(opId);
			cg.addVertexPaths(vertexSet);
			constraintgroups.add(cg);
		} else {
			((ConstraintGroup) cgSet.toArray()[0]).addVertexPaths(vertexSet);
		}
	}

	/**
	 * Removing a simple constraint on one vertex and one core
	 */
	public void removeConstraint(String opId, SDFAbstractVertex vertex) {

		Set<ConstraintGroup> cgSet = getOpConstraintGroups(opId);

		if (!cgSet.isEmpty()) {
			for (ConstraintGroup cg : cgSet) {
				cg.removeVertexPath(vertex.getInfo());
			}
		}
	}

	public Set<ConstraintGroup> getConstraintGroups() {

		return new HashSet<ConstraintGroup>(constraintgroups);
	}

	public Set<ConstraintGroup> getGraphConstraintGroups(
			SDFAbstractVertex vertex) {
		Set<ConstraintGroup> graphConstraintGroups = new HashSet<ConstraintGroup>();

		for (ConstraintGroup cg : constraintgroups) {
			if (cg.hasVertexPath(vertex.getInfo()))
				graphConstraintGroups.add(cg);
		}

		return graphConstraintGroups;
	}

	public Set<ConstraintGroup> getOpConstraintGroups(String opId) {
		Set<ConstraintGroup> graphConstraintGroups = new HashSet<ConstraintGroup>();

		for (ConstraintGroup cg : constraintgroups) {
			if (cg.hasOperatorId(opId))
				graphConstraintGroups.add(cg);
		}

		return graphConstraintGroups;
	}

	public boolean isCompatibleToConstraints(SDFAbstractVertex vertex,
			String opId) {
		Set<ConstraintGroup> opGroups = getOpConstraintGroups(opId);
		Set<ConstraintGroup> graphGroups = getGraphConstraintGroups(vertex);

		opGroups.retainAll(graphGroups);

		return !opGroups.isEmpty();
	}

	public void removeAll() {

		constraintgroups.clear();
	}

	@Override
	public String toString() {
		String s = "";

		for (ConstraintGroup cg : constraintgroups) {
			s += cg.toString();
		}

		return s;
	}

	public String getExcelFileURL() {
		return excelFileURL;
	}

	public void setExcelFileURL(String excelFileURL) {
		this.excelFileURL = excelFileURL;
	}

	public void importConstraints(PreesmScenario currentScenario) {
		if (!excelFileURL.isEmpty() && currentScenario != null) {
			ExcelConstraintsParser parser = new ExcelConstraintsParser(
					currentScenario);
			parser.parse(excelFileURL, currentScenario.getOperatorIds());
		}
	}
}
