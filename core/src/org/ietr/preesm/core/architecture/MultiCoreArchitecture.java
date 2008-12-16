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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.ietr.preesm.core.architecture.advancedmodel.Fifo;
import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.architecture.simplemodel.Operator;

/**
 * Architecture based on a fixed number of cores
 * 
 * @author mpelcat
 */
public class MultiCoreArchitecture {

	/**
	 * List of the cores + accelerators + media with their IDs.
	 */
	private Map<String, ArchitectureComponent> architectureComponents;

	/**
	 * List of the component definitions with their IDs.
	 */
	private Map<String, ArchitectureComponentDefinition> architectureComponentDefinitions;

	/**
	 * List of the interconnections between components.
	 */
	private Set<Interconnection> interconnections;

	/**
	 * List of the bus references associated to interfaces
	 */
	private Map<String, BusReference> busReferences;

	/**
	 * name of the archi.
	 */
	private String name;

	/**
	 * main operator and medium.
	 */
	private Operator mainOperator = null;
	private Medium mainMedium = null;

	/**
	 * Creating an empty architecture.
	 */
	public MultiCoreArchitecture(String name) {
		architectureComponents = new HashMap<String, ArchitectureComponent>();
		architectureComponentDefinitions = new HashMap<String, ArchitectureComponentDefinition>();
		busReferences = new HashMap<String, BusReference>();

		interconnections = new HashSet<Interconnection>();
		// fifos = new HashSet<Fifo>();

		this.name = name;
	}

	public BusReference getBusReference(String id) {
		return busReferences.get(id);
	}

	/**
	 * Adds the definition of a component and returns it to let the user add
	 * specific properties
	 */
	public ArchitectureComponentDefinition addComponentDefinition(
			ArchitectureComponentType type, String id) {
		if (architectureComponentDefinitions.containsKey(id)) {
			return architectureComponentDefinitions.get(id);
		} else {
			ArchitectureComponentDefinition def = ArchitectureComponentDefinitionFactory
					.createElement(type, id);
			architectureComponentDefinitions.put(def.getId(), def);
			return def;
		}
	}

	/**
	 * Creates and adds a component
	 */
	public ArchitectureComponent addComponent(ArchitectureComponentType type,
			String defId, String name) {
		if (architectureComponents.containsKey(name)) {
			return architectureComponents.get(name);
		} else {
			ArchitectureComponentDefinition newDef = addComponentDefinition(
					type, defId);
			ArchitectureComponent cmp = ArchitectureComponentFactory
					.createElement(newDef, name);
			architectureComponents.put(name, cmp);
			return cmp;
		}
	}

	/**
	 * Adds the reference of a bus and returns it to let the user add specific
	 * properties
	 */
	public BusReference createBusReference(String id) {
		if (busReferences.containsKey(id)) {
			return busReferences.get(id);
		} else {
			BusReference def = new BusReference(id);
			busReferences.put(def.getId(), def);
			return def;
		}
	}

	@Override
	public MultiCoreArchitecture clone() {

		// Creating archi
		MultiCoreArchitecture newArchi = new MultiCoreArchitecture(this.name);

		// Iterating on components
		Iterator<ArchitectureComponent> cmpIt = architectureComponents.values()
				.iterator();

		while (cmpIt.hasNext()) {
			ArchitectureComponent next = cmpIt.next();

			// each component is cloned and added to the new archi
			ArchitectureComponent newCmp = newArchi.addComponent(
					next.getType(), next.getDefinition().getId(), next
							.getName());
			newCmp.getDefinition().fill(next.getDefinition());
		}

		// We iterate on interconnections
		Iterator<Interconnection> intIt = interconnections.iterator();

		while (intIt.hasNext()) {
			Interconnection nextInt = intIt.next();
			newArchi.connect(nextInt.getCp1(), nextInt.getIf1(), nextInt
					.getCp2(), nextInt.getIf2(), false);
		}
		return newArchi;
	}

	// /**
	// * Connects a medium and an operator
	// *
	// * @return true if the medium could be added
	// */
	// public void connect(ArchitectureComponent cmp1, ArchitectureInterface
	// if1,
	// ArchitectureComponent cmp2, ArchitectureInterface if2,
	// boolean isFifo) {
	//
	// if (isFifo) {
	// fifos.add(new Fifo(cmp1, if1, cmp2, if2));
	// } else {
	// if (!existInterconnection(cmp1, if1, cmp2, if2))
	// interconnections.add(new Interconnection(cmp1, if1, cmp2, if2));
	// }
	//
	// }

	/**
	 * Connect two components. If the connection is directed, cmp1 and cmp2 are
	 * source and target components relatively.
	 * 
	 */
	public void connect(ArchitectureComponent cmp1, ArchitectureInterface if1,
			ArchitectureComponent cmp2, ArchitectureInterface if2,
			boolean isDirected) {
		if (!existInterconnection(cmp1, if1, cmp2, if2)) {
			interconnections.add(new Interconnection(cmp1, if1, cmp2, if2, isDirected));
			if (isDirected) {
				if (cmp1.getType() == ArchitectureComponentType.fifo) {
					if (((Fifo) cmp1).getOutputInterface() == null) {
						((Fifo) cmp1).setOutputInterface(if1);
					}
				} else if (cmp2.getType() == ArchitectureComponentType.fifo) {
					if (((Fifo) cmp2).getInputInterface() == null) {
						((Fifo) cmp2).setInputInterface(if2);
					}
				}
			}
		}
	}

	/**
	 * Interconnections have no direction
	 */
	private boolean existInterconnection(ArchitectureComponent cmp1,
			ArchitectureInterface if1, ArchitectureComponent cmp2,
			ArchitectureInterface if2) {

		Interconnection testInter = new Interconnection(cmp1, if1, cmp2, if2);
		Iterator<Interconnection> iterator = interconnections.iterator();

		while (iterator.hasNext()) {
			Interconnection currentInter = iterator.next();

			if (currentInter.equals(testInter))
				return true;
		}

		return false;
	}

	private boolean existInterconnection(ArchitectureComponent cmp1,
			ArchitectureComponent cmp2) {

		Iterator<Interconnection> iterator = interconnections.iterator();

		while (iterator.hasNext()) {
			Interconnection currentInter = iterator.next();

			if ((currentInter.getCp1().equals(cmp1) && currentInter.getCp2()
					.equals(cmp2))
					|| (currentInter.getCp2().equals(cmp1) && currentInter
							.getCp1().equals(cmp2)))
				return true;
		}

		return false;
	}

	public Medium getMainMedium() {
		if (mainMedium == null) {
			Set<ArchitectureComponent> cmpSet = getComponents(ArchitectureComponentType.medium);
			if (!cmpSet.isEmpty())
				return (Medium) getComponents(ArchitectureComponentType.medium)
						.toArray()[0];
			else
				return null;
		} else {
			return mainMedium;
		}
	}

	public Operator getMainOperator() {
		if (mainOperator == null) {
			Set<ArchitectureComponent> cmpSet = getComponents(ArchitectureComponentType.operator);
			if (!cmpSet.isEmpty())
				return (Operator) getComponents(
						ArchitectureComponentType.operator).toArray()[0];
			else
				return null;
		} else {
			return mainOperator;
		}
	}

	/**
	 * 
	 */
	public Set<Medium> getMedia(Operator op) {
		Set<Medium> media = new HashSet<Medium>();
		Iterator<ArchitectureComponent> iterator = getComponents(
				ArchitectureComponentType.medium).iterator();

		while (iterator.hasNext()) {

			Medium currentMedium = (Medium) iterator.next();

			if (existInterconnection(currentMedium, op)) {
				media.add(currentMedium);
			}
		}

		return media;
	}

	public Set<Medium> getMedia(Operator op1, Operator op2) {

		Set<Medium> intersection = getMedia(op1);
		intersection.retainAll(getMedia(op2));

		return intersection;
	}

	public int getNumberOfOperators() {
		return getComponents(ArchitectureComponentType.operator).size();
	}

	/**
	 * Returns the Component with the given type and name
	 */
	public ArchitectureComponent getComponent(ArchitectureComponentType type,
			String name) {
		Iterator<ArchitectureComponent> iterator = getComponents(type)
				.iterator();

		while (iterator.hasNext()) {
			ArchitectureComponent currentcmp = iterator.next();

			if (currentcmp.getName().compareToIgnoreCase(name) == 0) {
				return (currentcmp);
			}
		}

		return null;
	}

	/**
	 * Returns the Component with the given name
	 */
	public ArchitectureComponent getComponent(String name) {
		return architectureComponents.get(name);
	}

	/**
	 * Returns all the components of type type
	 */
	public Set<ArchitectureComponent> getComponents(
			ArchitectureComponentType type) {
		Set<ArchitectureComponent> ops = new HashSet<ArchitectureComponent>();

		Iterator<ArchitectureComponent> iterator = architectureComponents
				.values().iterator();

		while (iterator.hasNext()) {
			ArchitectureComponent currentCmp = iterator.next();

			if (currentCmp.getType() == type) {
				ops.add(currentCmp);
			}
		}

		return ops;
	}

	/**
	 * Returns all the components definitions of type type
	 */
	public Set<ArchitectureComponentDefinition> getComponentDefinitions(
			ArchitectureComponentType type) {
		Set<ArchitectureComponentDefinition> opdefs = new HashSet<ArchitectureComponentDefinition>();

		Iterator<ArchitectureComponentDefinition> iterator = architectureComponentDefinitions
				.values().iterator();

		while (iterator.hasNext()) {
			ArchitectureComponentDefinition currentCmp = iterator.next();

			if (currentCmp.getType() == type) {
				opdefs.add(currentCmp);
			}
		}

		return opdefs;
	}

	/**
	 * Returns the component definition with the given id and type
	 */
	public ArchitectureComponentDefinition getComponentDefinition(
			ArchitectureComponentType type, String id) {

		ArchitectureComponentDefinition def = architectureComponentDefinitions
				.get(id);

		if (def != null && def.getType() == type) {
			return def;
		}

		return null;
	}

	/**
	 * Returns all the interconnections
	 */
	public Set<Interconnection> getInterconnections() {
		return interconnections;
	}

	public String getName() {
		return name;
	}

	public void setMainOperator(String mainOperatorName) {
		Operator o = (Operator) getComponent(
				ArchitectureComponentType.operator, mainOperatorName);
		if (o != null) {
			this.mainOperator = o;
		}
	}

	public void setMainMedium(String mainMediumName) {
		Medium m = (Medium) getComponent(ArchitectureComponentType.medium,
				mainMediumName);
		if (m != null) {
			this.mainMedium = m;
		}

	}

}
