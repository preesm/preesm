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
 * BusDescriptor is used to describe a bus
 * 
 * @author pmu
 * 
 */
public class BusDescriptor extends LinkDescriptor {

	/**
	 * The number of port of this bus
	 */
	private int portNumber = Integer.MAX_VALUE;

	/**
	 * The TGVertices connected to this bus
	 */
	private HashMap<String, TGVertexDescriptor> TGVertices;

	/**
	 * Construct the BusDescriptor with the given id, name and component buffer
	 * 
	 * @param id
	 *            Bus id
	 * @param name
	 *            Bus name
	 * @param componentDescriptorBuffer
	 *            Component buffer
	 */
	public BusDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> componentDescriptorBuffer) {
		super(id, name, componentDescriptorBuffer);
		this.type = ComponentType.Bus;
		TGVertices = new HashMap<String, TGVertexDescriptor>();
	}

	/**
	 * Construct the BusDescriptor with the given id, name, component buffer,
	 * clock period, data width and surface
	 * 
	 * @param id
	 *            Bus id
	 * @param name
	 *            Bus name
	 * @param componentDescriptorBuffer
	 *            Component buffer
	 * @param clockPeriod
	 *            Clock period of bus
	 * @param dataWidth
	 *            Data width of bus
	 * @param surface
	 *            Surface of bus
	 */
	public BusDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> componentDescriptorBuffer,
			int clockPeriod, int dataWidth, int surface) {
		super(id, name, componentDescriptorBuffer, clockPeriod, dataWidth,
				surface);
		this.type = ComponentType.Bus;
		TGVertices = new HashMap<String, TGVertexDescriptor>();
	}

	/**
	 * Add a TGVertex to this bus
	 * 
	 * @param TGVertex
	 *            A TGVertex to be added
	 * @return A boolean to indicate success or not
	 */
	public boolean addTGVertex(TGVertexDescriptor TGVertex) {
		if (TGVertices.size() < portNumber) {
			TGVertices.put(TGVertex.getId(), TGVertex);
			return true;
		} else {
			System.out.println("Error: The bus doesn't have a port for "
					+ TGVertex.getId());
			return false;
		}
	}

	/**
	 * Get the number of port
	 * 
	 * @return The number of port
	 */
	public int getPortNumber() {
		return portNumber;
	}

	/**
	 * Get the TGVertex with given name
	 * 
	 * @param name
	 *            The name of TGVertex
	 * @return A TGVertex
	 */
	public TGVertexDescriptor getTGVertex(String name) {
		return TGVertices.get(name);
	}

	/**
	 * Get all the TGVertices connected to the bus
	 * 
	 * @return A HashMap of all the TGVertices
	 */
	public HashMap<String, TGVertexDescriptor> getTGVertices() {
		return TGVertices;
	}

	/**
	 * Set the number of port
	 * 
	 * @param portNumber
	 *            The number of port
	 */
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	/**
	 * Set the TGVertices of the bus
	 * 
	 * @param TGVertices
	 *            A HashMap of the TGVertices
	 */
	public void setTGVertices(HashMap<String, TGVertexDescriptor> TGVertices) {
		this.TGVertices = TGVertices;
	}

}
