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

package org.ietr.preesm.core.architecture.advancedmodel;

/**
 * An interface is contained in a component. Containing an interface of type M
 * and multiplicity N means that this component can be connected to N links of
 * type M
 * 
 * @author pmu
 */
public class SpiritInterface {

	private String name;

	/**
	 * type of the corresponding link
	 */
	private AbstractLinkDefinition linkDefinition;

	/**
	 * owner of the corresponding interface
	 */
	private Component owner;

	/**
	 * There are seven possible modes for an interface: "master", "slave",
	 * "mirroredMaster", "mirroredSlave", "system", "mirroredSystem" and
	 * "monitor".
	 */
	private String interfaceMode;

	/**
	 * Indicate whether this interface is connected to a non-monitor interface
	 */
	private boolean connected;

	/**
	 * 
	 * Constructor
	 */
	public SpiritInterface(String name, AbstractLinkDefinition linkDef,
			Component owner, String interfaceMode) {
		this.name = name;
		this.linkDefinition = linkDef;
		this.owner = owner;
		this.interfaceMode = interfaceMode;
		this.connected = false;
	}

	public boolean canConnectTo(SpiritInterface intf) {
		if (this.getLinkDefinition().equals(intf.getLinkDefinition())) {
			if ((this.getInterfaceMode().equalsIgnoreCase("master") && intf
					.getInterfaceMode().equalsIgnoreCase("slave"))
					|| (this.getInterfaceMode().equalsIgnoreCase("slave") && intf
							.getInterfaceMode().equalsIgnoreCase("master"))
					|| (this.getInterfaceMode().equalsIgnoreCase("master") && intf
							.getInterfaceMode().equalsIgnoreCase(
									"mirroredMaster"))
					|| (this.getInterfaceMode().equalsIgnoreCase(
							"mirroredMaster") && intf.getInterfaceMode()
							.equalsIgnoreCase("master"))
					|| (this.getInterfaceMode().equalsIgnoreCase("slave") && intf
							.getInterfaceMode().equalsIgnoreCase(
									"mirroredSlave"))
					|| (this.getInterfaceMode().equalsIgnoreCase(
							"mirroredSlave") && intf.getInterfaceMode()
							.equalsIgnoreCase("slave"))
					|| (this.getInterfaceMode().equalsIgnoreCase("system") && intf
							.getInterfaceMode().equalsIgnoreCase(
									"mirroredSystem"))
					|| (this.getInterfaceMode().equalsIgnoreCase(
							"mirroredSystem") && intf.getInterfaceMode()
							.equalsIgnoreCase("system"))) {
				if (!this.isConnected() && !intf.isConnected()) {
					return true;
				} else {
					return false;
				}
			} else if ((this.getInterfaceMode().equalsIgnoreCase("monitor") && !intf
					.getInterfaceMode().equalsIgnoreCase("monitor"))
					|| (!this.getInterfaceMode().equalsIgnoreCase("monitor") && intf
							.getInterfaceMode().equalsIgnoreCase("monitor"))) {
				return true;
			}
		}
		return false;
	}

	public SpiritInterface clone() {
		/*
		 * The interface definition is cloned and references the given link
		 * definition
		 */
		SpiritInterface newIntf = new SpiritInterface(
				new String(this.getName()), this.getLinkDefinition(), this
						.getOwner(), this.getInterfaceMode());
		return newIntf;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SpiritInterface) {
			SpiritInterface intf = (SpiritInterface) obj;
			return name.equalsIgnoreCase(intf.getName())
					&& owner.equals(intf.owner)
					&& linkDefinition.equals(intf.getLinkDefinition());
		}
		return false;
	}

	public AbstractLinkDefinition getLinkDefinition() {
		return linkDefinition;
	}

	public String getInterfaceMode() {
		return interfaceMode;
	}

	public String getName() {
		return name;
	}

	public Component getOwner() {
		return owner;
	}

	/**
	 * @return true if the interface has connected to a non-monitor interface
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * set the status of the connected
	 */
	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public void setOwner(Component owner) {
		this.owner = owner;
	}
}
