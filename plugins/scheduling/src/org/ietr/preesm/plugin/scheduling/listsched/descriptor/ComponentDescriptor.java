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
package org.ietr.preesm.plugin.scheduling.listsched.descriptor;

import java.util.HashMap;

/**
 * ComponentDescriptor describes a component in the architecture
 * 
 * @author pmu
 * 
 */
public class ComponentDescriptor {

	/**
	 * Buffer of all components
	 */
	protected HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer = null;

	/**
	 * Component type
	 */
	protected ComponentType type;

	/**
	 * Component Id
	 */
	protected String id;

	/**
	 * Component name
	 */
	protected String name;

	/**
	 * Clock period, unit: ns
	 */
	protected int clockPeriod = 10;

	/**
	 * Data width
	 */
	protected int dataWidth = 32;

	/**
	 * Surface
	 */
	protected int surface;

	/**
	 * Construct a ComponentDescriptor with the given id, name and component
	 * buffer
	 * 
	 * @param id
	 *            Component Id
	 * @param name
	 *            Component name
	 * @param ComponentDescriptorBuffer
	 *            Component buffer
	 */
	public ComponentDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer) {
		this.id = id;
		this.name = name;
		this.ComponentDescriptorBuffer = ComponentDescriptorBuffer;
		ComponentDescriptorBuffer.put(this.id, this);
	}

	/**
	 * Construct a ComponentDescriptor with the given id, name, component
	 * buffer, clock period, data width and surface
	 * 
	 * @param id
	 *            Component Id
	 * @param name
	 *            Component name
	 * @param ComponentDescriptorBuffer
	 *            Component buffer
	 * @param clockPeriod
	 *            Clock period
	 * @param dataWidth
	 *            Data width
	 * @param surface
	 *            Surface
	 */
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

	/**
	 * Get clock period
	 * 
	 * @return Clock period
	 */
	public int getClockPeriod() {
		return clockPeriod;
	}

	/**
	 * Get data width
	 * 
	 * @return Data width
	 */
	public int getDataWidth() {
		return dataWidth;
	}

	/**
	 * Get component Id
	 * 
	 * @return Component Id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Get component name
	 * 
	 * @return component name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get surface
	 * 
	 * @return surface
	 */
	public int getSurface() {
		return surface;
	}

	/**
	 * Get component type
	 * 
	 * @return Component type
	 */
	public ComponentType getType() {
		return type;
	}

	/**
	 * Set clock period
	 * 
	 * @param clockPeriod
	 *            Clock period
	 */
	public void setClockPeriod(int clockPeriod) {
		this.clockPeriod = clockPeriod;
	}

	/**
	 * Set data width
	 * 
	 * @param dataWidth
	 *            Data width
	 */
	public void setDataWidth(int dataWidth) {
		this.dataWidth = dataWidth;
	}

	/**
	 * Set component Id
	 * 
	 * @param id
	 *            Component Id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Set component name
	 * 
	 * @param name
	 *            component name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set surface
	 * 
	 * @param surface
	 *            Surface
	 */
	public void setSurface(int surface) {
		this.surface = surface;
	}
}
