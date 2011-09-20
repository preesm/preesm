/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

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

package org.ietr.preesm.core.architecture.simplemodel;

import java.util.HashMap;
import java.util.Map;

import org.ietr.preesm.core.architecture.ComponentDefinition;
import org.ietr.preesm.core.architecture.ComponentType;
import org.ietr.preesm.core.architecture.parser.VLNV;

/**
 * A Direct Memory Access is set up by an operator to transfer data
 * 
 * @author mpelcat
 */
public class DmaDefinition extends ComponentDefinition {

	/**
	 * The time needed to set-up a communication depending on the operator doing
	 * the set-up. Using operator names as keys
	 */
	Map<String, Long> setupTimes = null;

	public DmaDefinition(VLNV vlnv) {
		super(vlnv, "dma");
		setupTimes = new HashMap<String, Long>();
	}

	public ComponentType getType() {
		return ComponentType.dma;
	}

	/*
	 * public DmaDefinition clone() { // A new OperatorDefinition is created
	 * with same id DmaDefinition newdef = new DmaDefinition(this.getVlnv());
	 * return newdef; }
	 */

	public void fill(ComponentDefinition origin) {
	}

	public void addSetupTime(String opName, long time) {
		setupTimes.put(opName, time);
	}

	public long getSetupTime(String opName) {
		return setupTimes.get(opName);
	}

	public void addSetupTime(Operator o, long time) {
		addSetupTime(o.getName(), time);
	}

	public void removeSetupTime(Operator o) {
		setupTimes.remove(o);
	}

	public long getSetupTime(Operator o) {
		return getSetupTime(o.getId());
	}

	public Map<String, Long> getSetupTimes() {
		return setupTimes;
	}
}
