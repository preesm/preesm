package org.ietr.preesm.plugin.mapper.tools;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Given a set of T, gives a subset verifying the subsetCondition
 * 
 * @author mpelcat
 */
public abstract class SubsetFinder<T, U> {

	private U comparedValue;

	private Set<T> inputset;

	public SubsetFinder() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SubsetFinder(Set<T> inputset, U comparedValue) {
		this.inputset = inputset;
		this.comparedValue = comparedValue;
	}

	public Set<T> subset() {
		Set<T> subset = new HashSet<T>();

		Iterator<T> iterator = inputset.iterator();

		while (iterator.hasNext()) {
			T next = iterator.next();

			if (subsetCondition(next, comparedValue))
				subset.add(next);
		}
		return subset;
	}

	protected abstract boolean subsetCondition(T tested, U comparedValue);
}
