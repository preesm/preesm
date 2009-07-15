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

package org.ietr.preesm.core.architecture;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.advancedmodel.Bus;
import org.ietr.preesm.core.architecture.advancedmodel.CommunicationNode;
import org.ietr.preesm.core.architecture.advancedmodel.Communicator;
import org.ietr.preesm.core.architecture.advancedmodel.Fifo;
import org.ietr.preesm.core.architecture.advancedmodel.IpCoprocessor;
import org.ietr.preesm.core.architecture.advancedmodel.Memory;
import org.ietr.preesm.core.architecture.advancedmodel.Processor;
import org.ietr.preesm.core.architecture.simplemodel.ContentionNode;
import org.ietr.preesm.core.architecture.simplemodel.Dma;
import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.architecture.simplemodel.MediumDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.architecture.simplemodel.ParallelNode;
import org.ietr.preesm.core.architecture.simplemodel.Ram;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.sdf4j.model.AbstractEdge;
import org.sdf4j.model.AbstractVertex;

/**
 * Common features of components in an architecture. Media and Operators are
 * ArchitectureComponents
 * 
 * @author mpelcat
 */
public abstract class ArchitectureComponent extends
		AbstractVertex<MultiCoreArchitecture> {

	public static class ArchitectureComponentComparator implements
			Comparator<ArchitectureComponent> {
		@Override
		public int compare(ArchitectureComponent o1, ArchitectureComponent o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}

	public static final ArchitectureComponent NO_COMPONENT = null;

	/**
	 * media interfaces available in this architecture component. Interfaces are
	 * be connected via interconnections
	 */
	protected List<ArchitectureInterface> availableInterfaces;

	/**
	 * The definition contains the category (medium or operator) and the id
	 * (example: C64x+) as well as specific parameters
	 */
	private ArchitectureComponentDefinition definition;

	/**
	 * Saving refinement name if present in IP-XACT in order to be able to
	 * export it
	 */
	private String refinementName = "";

	/**
	 * Name of the component instance
	 */
	private String name;

	/**
	 * Base address of the component memory map (example: 0x08000000)
	 */
	private String baseAddress = "0x00000000";

	/**
	 * Constructor from a type and a name
	 */
	public ArchitectureComponent(String name,
			ArchitectureComponentDefinition definition) {
		this.name = new String(name);
		this.definition = definition;

		availableInterfaces = new ArrayList<ArchitectureInterface>();
	}

	/**
	 * Adds an interface to the architecture component
	 */
	public final ArchitectureInterface addInterface(ArchitectureInterface intf) {

		availableInterfaces.add(intf);

		return intf;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ArchitectureComponent) {
			ArchitectureComponent op = (ArchitectureComponent) obj;
			return this.getName().compareTo(op.getName()) == 0;
		}

		return false;
	}

	public List<ArchitectureInterface> getAvailableInterfaces() {
		return availableInterfaces;
	}

	public ArchitectureComponentDefinition getDefinition() {
		return definition;
	}

	/**
	 * Gets the interface for the given bus type
	 * 
	 * @return the interface or null if it does not exist
	 */
	public ArchitectureInterface getInterface(BusReference busRef) {

		ArchitectureInterface searchedIntf = null;

		ListIterator<ArchitectureInterface> it = getAvailableInterfaces()
				.listIterator();

		while (it.hasNext()) {
			ArchitectureInterface intf = it.next();
			if (busRef.equals(intf.getBusReference())) {
				searchedIntf = intf;
			}
		}

		if (searchedIntf == null) {
			searchedIntf = new ArchitectureInterface(busRef, this);
		}

		return searchedIntf;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public abstract ArchitectureComponentType getType();

	public String getBaseAddress() {
		return baseAddress;
	}

	public void setBaseAddress(String baseAddress) {
		this.baseAddress = baseAddress;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void ConnectionAdded(AbstractEdge e) {
		// Nothing to do for the moment

	}

	@SuppressWarnings("unchecked")
	@Override
	public void ConnectionRemoved(AbstractEdge e) {
		// Nothing to do for the moment

	}

	public void setDefinition(ArchitectureComponentDefinition definition) {
		this.definition = definition;
	}

	public void fill(ArchitectureComponent cmp, MultiCoreArchitecture newArchi) {
		this.setBaseAddress(cmp.getBaseAddress());
		this.setDefinition(newArchi.getComponentDefinition(cmp.getDefinition()
				.getType(), cmp.getDefinition().getVlnv()));

		for (ArchitectureInterface itf : availableInterfaces) {
			ArchitectureInterface newItf = new ArchitectureInterface(newArchi
					.getBusReference(itf.getBusReference().getId()), this);
			this.getAvailableInterfaces().add(newItf);
		}
	}

	public abstract boolean isNode();

	public String getRefinementName() {
		return refinementName;
	}

	public void setRefinementName(String refinementName) {
		this.refinementName = refinementName;
	}

	public final ArchitectureComponent clone() {

		// Definition is cloned
		ArchitectureComponent newCmp = null;

		if (this.getType().equals(ArchitectureComponentType.bus)) {
			newCmp = new Bus(getName(), null);
		} else if (this.getType().equals(
				ArchitectureComponentType.communicationNode)) {
			newCmp = new CommunicationNode(getName(), null);
		} else if (this.getType()
				.equals(ArchitectureComponentType.communicator)) {
			Communicator com = new Communicator(getName(), null);
			com.getSetupTimes().putAll(((Communicator) this).getSetupTimes());
			newCmp = com;
		} else if (this.getType().equals(
				ArchitectureComponentType.contentionNode)) {
			newCmp = new ContentionNode(getName(), null);
		} else if (this.getType().equals(ArchitectureComponentType.dma)) {
			newCmp = new Dma(getName(), null);
		} else if (this.getType().equals(ArchitectureComponentType.fifo)) {
			newCmp = new Fifo(getName(), null);
		} else if (this.getType().equals(
				ArchitectureComponentType.ipCoprocessor)) {
			newCmp = new IpCoprocessor(getName(), null);
		} else if (this.getType().equals(ArchitectureComponentType.medium)) {
			newCmp = new Medium(getName(), (MediumDefinition) getDefinition());
		} else if (this.getType().equals(ArchitectureComponentType.memory)) {
			newCmp = new Memory(getName(), null);
		} else if (this.getType().equals(ArchitectureComponentType.operator)) {
			newCmp = new Operator(getName(),
					(OperatorDefinition) getDefinition());
		} else if (this.getType()
				.equals(ArchitectureComponentType.parallelNode)) {
			newCmp = new ParallelNode(getName(), null);
		} else if (this.getType().equals(ArchitectureComponentType.processor)) {
			newCmp = new Processor(getName(), null);
		} else if (this.getType().equals(ArchitectureComponentType.ram)) {
			newCmp = new Ram(getName(), null);
		} else {
			PreesmLogger.getLogger().log(Level.SEVERE,
					"Cloning unknown type archi component.");
		}

		newCmp.setBaseAddress(getBaseAddress());
		newCmp.setRefinementName(getRefinementName());

		return newCmp;
	}

}
