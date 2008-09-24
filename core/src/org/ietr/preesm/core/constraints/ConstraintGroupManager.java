package org.ietr.preesm.core.constraints;

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
