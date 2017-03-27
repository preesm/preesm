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

package org.ietr.preesm.mapper.abc;

import org.ietr.preesm.mapper.abc.taskscheduling.TaskSchedType;

/**
 * Types of simulator to be used in parameters
 * 
 * @author mpelcat
 */
public class AbcType {

	/**
	 * Available Abc types
	 */
	public static final AbcType InfiniteHomogeneous = new AbcType(
			"InfiniteHomogeneous");
	public static final AbcType LooselyTimed = new AbcType("LooselyTimed");
	public static final AbcType ApproximatelyTimed = new AbcType(
			"ApproximatelyTimed");
	public static final AbcType AccuratelyTimed = new AbcType("AccuratelyTimed");
	public static final AbcType CommConten = new AbcType("CommConten");
	public static final AbcType DynamicQueuing = new AbcType("DynamicQueuing");

	/**
	 * Name of the current type
	 */
	private String name = null;

	/**
	 * True if the tasks are switched while mapping using algorithms that do
	 * further tests than the mapping/scheduling chosen algorithm
	 */
	private TaskSchedType taskSchedType = null;

	public AbcType(String name) {
		super();
		this.name = name;
		this.taskSchedType = TaskSchedType.Simple;
	}

	@Override
	public String toString() {

		return name;
	}

	public static AbcType fromString(String type) {

		if (type.equalsIgnoreCase("InfiniteHomogeneous")) {
			return InfiniteHomogeneous;
		} else if (type.equalsIgnoreCase("LooselyTimed")) {
			return LooselyTimed;
		} else if (type.equalsIgnoreCase("ApproximatelyTimed")) {
			return ApproximatelyTimed;
		} else if (type.equalsIgnoreCase("AccuratelyTimed")) {
			return AccuratelyTimed;
		} else if (type.equalsIgnoreCase("CommConten")) {
			return CommConten;
		} else if (type.equalsIgnoreCase("DynamicQueuing")) {
			return DynamicQueuing;
		}

		return null;
	}

	public TaskSchedType getTaskSchedType() {
		return taskSchedType;
	}

	public AbcType setTaskSchedType(TaskSchedType taskSchedType) {
		if (taskSchedType != null) {
			this.taskSchedType = taskSchedType;
		} else {
			this.taskSchedType = TaskSchedType.Simple;
		}

		return this;
	}
}
