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


package org.ietr.preesm.core.scenario;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * container and manager of Constraint groups. It can load and store constraint
 * groups
 * 
 * @author mpelcat
 */
public class ConstraintGroupManager extends Observable {

	/**
	 * List of all constraint groups
	 */
	private List<ConstraintGroup> constraintgroups;

	public ConstraintGroupManager() {
		constraintgroups = new ArrayList<ConstraintGroup>();
	}

	public void addConstraintGroup(ConstraintGroup cg, boolean isNotyfying) {

		constraintgroups.add(cg);

		if (isNotyfying) {
			notifyProperty(new String("addcg"));
		}
	}

	public void addConstraintGroup(String name, boolean isNotyfying) {

		ConstraintGroup cg = new ConstraintGroup(name);
		constraintgroups.add(cg);

		if (isNotyfying) {
			notifyProperty(new String("addcg"));
		}
	}

	public ConstraintGroup getConstraintGroup(String cstGroupId) {
		ConstraintGroup group = null;

		for (ConstraintGroup cg : constraintgroups) {
			if (cg.getId().compareTo(cstGroupId) == 0)
				group = cg;
		}

		return group;
	}

	public List<ConstraintGroup> getConstraintGroups() {

		return new ArrayList<ConstraintGroup>(constraintgroups);
	}

	public List<ConstraintGroup> getGraphConstraintGroups(
			SDFAbstractVertex vertex) {
		List<ConstraintGroup> graphConstraintGroups = new ArrayList<ConstraintGroup>();

		for (ConstraintGroup cg : constraintgroups) {
			if (cg.hasVertex(vertex))
				graphConstraintGroups.add(cg);
		}

		return graphConstraintGroups;
	}

	public void notifyProperty(String type) {
		setChanged();
		notifyObservers(type);
	}

	public void removeAll(boolean isNotyfying) {

		constraintgroups.clear();
		if (isNotyfying) {
			notifyProperty(new String("removeall"));
		}
	}

	public void removeConstraintGroup(String name, boolean isNotyfying) {

		Iterator<ConstraintGroup> it = constraintgroups.iterator();
		while (it.hasNext()) {
			ConstraintGroup cg = it.next();
			if (cg.getId().compareTo(name) == 0) {
				it.remove();
				if (isNotyfying) {
					notifyProperty(new String("removecg"));
				}
			}
		}

	}
}
