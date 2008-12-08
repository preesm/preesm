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
package org.ietr.preesm.plugin.mapper.listsched.descriptor;

import java.util.HashMap;

public class ComponentDescriptor {
	protected HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer = null;

	protected ComponentType type;

	protected String id;

	protected String name;

	protected int clockPeriod = 10; // unit: ns

	protected int dataWidth = 32;

	protected int surface;

	public ComponentDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer) {
		this.id = id;
		this.name = name;
		this.ComponentDescriptorBuffer = ComponentDescriptorBuffer;
		ComponentDescriptorBuffer.put(this.id, this);
	}

	public ComponentDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer,
			int clockPeriod, int dataWidth, int surface) {
		this.id = id;
		this.name = name;
		this.ComponentDescriptorBuffer = ComponentDescriptorBuffer;
		ComponentDescriptorBuffer.put(this.id, this);
		this.clockPeriod = clockPeriod;
		this.dataWidth = dataWidth;
		this.surface = surface;
	}

	public ComponentType getType() {
		return type;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setClockPeriod(int clockPeriod) {
		this.clockPeriod = clockPeriod;
	}

	public int getClockPeriod() {
		return clockPeriod;
	}

	public void setDataWidth(int dataWidth) {
		this.dataWidth = dataWidth;
	}

	public int getDataWidth() {
		return dataWidth;
	}

	/**
	 * 
	 * @param surface
	 */
	public void setSurface(int surface) {
		this.surface = surface;
	}

	/**
	 * 
	 * @return
	 */
	public int getSurface() {
		return surface;
	}
}
