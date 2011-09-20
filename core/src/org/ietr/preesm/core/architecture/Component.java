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

package org.ietr.preesm.core.architecture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;

import net.sf.dftools.workflow.tools.AbstractWorkflowLogger;

import org.ietr.preesm.core.architecture.simplemodel.ContentionNode;
import org.ietr.preesm.core.architecture.simplemodel.Dma;
import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.architecture.simplemodel.MediumDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.architecture.simplemodel.ParallelNode;
import org.ietr.preesm.core.architecture.simplemodel.Ram;
import org.sdf4j.model.AbstractEdge;
import org.sdf4j.model.AbstractVertex;

/**
 * Common features of components in an architecture. Media and Operators are
 * ArchitectureComponents
 * 
 * @author mpelcat
 */
public abstract class Component extends
		AbstractVertex<MultiCoreArchitecture> {

	public static class CmpComparator implements
			Comparator<Component> {
		@Override
		public int compare(Component o1, Component o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}

	public static final Component NO_COMPONENT = null;

	/**
	 * Types of bus that can be connected to the current component. Can be
	 * omitted if the component has no hierarchy
	 */
	protected List<BusType> busTypes;

	/**
	 * media interfaces available in this architecture component. Interfaces are
	 * connected via interconnections
	 */
	protected List<Interface> availableInterfaces;

	/**
	 * The definition contains the category (medium or operator) and the id
	 * (example: C64x+) as well as specific parameters
	 */
	private ComponentDefinition definition;

	/**
	 * Saving refinement name if present in IP-XACT in order to be able to
	 * export it
	 */
	private String refinementName = "";

	/**
	 * Base address of the component memory map (example: 0x08000000)
	 */
	private String baseAddress = "0x00000000";

	/**
	 * Constructor from a type and a name
	 */
	public Component(String id,
			ComponentDefinition definition) {
		setId(id);
		setName(id);
		this.busTypes = new ArrayList<BusType>();
		this.definition = definition;

		availableInterfaces = new ArrayList<Interface>();
	}

	/**
	 * Adds an interface to the architecture component
	 */
	public final Interface addInterface(Interface intf) {

		availableInterfaces.add(intf);

		return intf;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Component) {
			Component op = (Component) obj;
			return this.getName().compareTo(op.getName()) == 0;
		}

		return false;
	}

	public List<Interface> getAvailableInterfaces() {
		return availableInterfaces;
	}

	public ComponentDefinition getDefinition() {
		return definition;
	}

	/**
	 * Gets the interface for the given bus reference
	 * 
	 * @return the interface or null if it does not exist
	 */
	public Interface getInterface(BusReference busRef) {

		Interface searchedIntf = null;

		ListIterator<Interface> it = getAvailableInterfaces()
				.listIterator();

		while (it.hasNext()) {
			Interface intf = it.next();
			if (busRef.equals(intf.getBusReference())) {
				searchedIntf = intf;
			}
		}

		if (searchedIntf == null) {
			searchedIntf = new Interface(busRef, this);
		}

		return searchedIntf;
	}

	/**
	 * Gets the interfaces
	 */
	public List<Interface> getInterfaces() {
		return Collections.unmodifiableList(availableInterfaces);
	}

	public abstract ComponentType getType();

	public String getBaseAddress() {
		return baseAddress;
	}

	public void setBaseAddress(String baseAddress) {
		this.baseAddress = baseAddress;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void connectionAdded(AbstractEdge e) {
		// Nothing to do for the moment

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void connectionRemoved(AbstractEdge e) {
		// Nothing to do for the moment

	}

	public void setDefinition(ComponentDefinition definition) {
		this.definition = definition;
	}

	public void fill(Component cmp, MultiCoreArchitecture newArchi) {

		this.setBaseAddress(cmp.getBaseAddress());
		this.setRefinementName(cmp.getRefinementName());

		this.setDefinition(newArchi.getComponentDefinition(cmp.getDefinition()
				.getType(), cmp.getDefinition().getVlnv()));

		for (Interface itf : cmp.availableInterfaces) {

			if (itf.getBusReference().getId().isEmpty()) {
				AbstractWorkflowLogger.getLogger().log(Level.WARNING,
						"Dangerous unnamed ports in architecture.");
			}

			Interface newItf = new Interface(
					newArchi.createBusReference(itf.getBusReference().getId()),
					this);
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

	public List<BusType> getBusTypes() {
		return busTypes;
	}

	public void setBusTypes(List<BusType> busTypes) {
		this.busTypes = busTypes;
	}

	public void addBusType(BusType busType) {
		busTypes.add(busType);
	}

	public BusType getBusType(String id) {
		for (BusType type : busTypes) {
			if (type.getId().equals(id)) {
				return type;
			}
		}
		return null;
	}

	public final Component clone() {

		// Definition is cloned
		Component newCmp = null;

		if (this.getType().equals(ComponentType.contentionNode)) {
			newCmp = new ContentionNode(getName(), null);
		} else if (this.getType().equals(ComponentType.dma)) {
			newCmp = new Dma(getName(), null);
		} else if (this.getType().equals(ComponentType.medium)) {
			newCmp = new Medium(getName(), (MediumDefinition) getDefinition());
		} else if (this.getType().equals(ComponentType.operator)) {
			newCmp = new Operator(getName(),
					(OperatorDefinition) getDefinition());
		} else if (this.getType()
				.equals(ComponentType.parallelNode)) {
			newCmp = new ParallelNode(getName(), null);
		} else if (this.getType().equals(ComponentType.ram)) {
			newCmp = new Ram(getName(), null);
		} else {
			AbstractWorkflowLogger.getLogger().log(Level.SEVERE,
					"Cloning unknown type archi component.");
		}

		newCmp.setBusTypes(getBusTypes());

		Object o = getPropertyBean().getValue(REFINEMENT);
		if (o != null && o instanceof MultiCoreArchitecture) {
			newCmp.getPropertyBean().setValue(REFINEMENT,
					((MultiCoreArchitecture) o).clone());
		}

		String name = (String) getPropertyBean().getValue(NAME);
		newCmp.getPropertyBean().setValue(NAME, name);

		return newCmp;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getName() {
		return super.getName();
	}

	@Override
	public void setId(String id) {
		super.setId(id);
	}

	@Override
	public void setName(String name) {
		super.setName(name);
	}

	@Override
	public String getId() {
		return super.getId();
	}
}
