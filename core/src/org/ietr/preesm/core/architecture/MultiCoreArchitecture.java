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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.preesm.core.architecture.parser.VLNV;
import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.sdf4j.model.AbstractGraph;
import org.sdf4j.model.AbstractVertex;
import org.sdf4j.model.visitors.SDF4JException;

/**
 * Architecture based on a fixed number of cores
 * 
 * @author mpelcat
 */
public class MultiCoreArchitecture extends
		AbstractGraph<ArchitectureComponent, Interconnection> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7297201138766758092L;

	/**
	 * List of the component definitions with their IDs.
	 */
	private Map<VLNV, ArchitectureComponentDefinition> architectureComponentDefinitions;

	/**
	 * List of the bus references associated to interfaces
	 */
	private Map<String, BusReference> busReferences;

	/**
	 * List of the hierarchy ports linked to a given component
	 */
	private Set<HierarchyPort> hierarchyPorts;

	/**
	 * name and id of the archi.
	 */
	private String name;
	private String id;

	/**
	 * main operator and medium.
	 */
	private Operator mainOperator = null;
	private Medium mainMedium = null;

	/**
	 * Creating an empty architecture.
	 */
	public MultiCoreArchitecture() {
		this("");
	}

	/**
	 * Creating an empty architecture.
	 */
	public MultiCoreArchitecture(String name) {
		super(new InterconnectionFactory());
		architectureComponentDefinitions = new HashMap<VLNV, ArchitectureComponentDefinition>();
		busReferences = new HashMap<String, BusReference>();
		hierarchyPorts = new HashSet<HierarchyPort>();

		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
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
			ArchitectureComponentType type, VLNV vlnv) {

		if (vlnv.getName().isEmpty()) {
			PreesmLogger
					.getLogger()
					.log(Level.WARNING,
							"careful: at least one component has no definition in the architecture");
		}

		for (VLNV currentVlnv : architectureComponentDefinitions.keySet()) {
			if (currentVlnv.equals(vlnv)) {
				return architectureComponentDefinitions.get(currentVlnv);
			}
		}

		ArchitectureComponentDefinition def = ArchitectureComponentDefinitionFactory
				.createElement(type, vlnv);
		addComponentDefinition(def);
		return def;

	}

	/**
	 * Adds the definition of a component and returns it to let the user add
	 * specific properties
	 */
	public ArchitectureComponentDefinition addComponentDefinition(
			ArchitectureComponentDefinition def) {

		architectureComponentDefinitions.put(def.getVlnv(), def);
		return def;
	}

	/**
	 * Creates and adds a component
	 */
	public ArchitectureComponent addComponent(ArchitectureComponentType type,
			VLNV defVLNV, String name) {
		if (getVertex(name) != null) {
			return getVertex(name);
		} else {
			ArchitectureComponentDefinition newDef = addComponentDefinition(
					type, defVLNV);
			ArchitectureComponent cmp = ArchitectureComponentFactory
					.createElement(newDef, name);
			addVertex(cmp);
			return cmp;
		}
	}

	/**
	 * Creates and adds a component
	 */
	public ArchitectureComponent addComponent(ArchitectureComponent component) {
		if (getVertex(component.getName()) != null) {
			return getVertex(component.getName());
		} else {
			addComponentDefinition(component.getDefinition());
			addVertex(component);
			return component;
		}
	}

	/**
	 * Adds a hierarchy port with its corresponding component
	 */
	public void addHierarchyPort(HierarchyPort hierarchyPort) {
		hierarchyPorts.add(hierarchyPort);
	}

	public Set<HierarchyPort> getHierarchyPorts() {
		return hierarchyPorts;
	}

	public void removeHierarchyPort(HierarchyPort port) {
		hierarchyPorts.remove(port);
	}

	public HierarchyPort getHierarchyPort(String name) {
		for (HierarchyPort port : hierarchyPorts) {
			if (port.getName().equals(name)) {
				return port;
			}
		}
		return null;
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

	/**
	 * Connect two components. If the connection is directed, cmp1 and cmp2 are
	 * source and target components relatively.
	 * 
	 */
	public void connect(ArchitectureComponent cmp1, ArchitectureInterface if1,
			ArchitectureComponent cmp2, ArchitectureInterface if2,
			boolean isDirected, boolean isSetup) {
		if (!existInterconnection(cmp1, if1, cmp2, if2)) {
			Interconnection itc = this.addEdge(cmp1, cmp2);

			if (itc != null) {
				itc.setSrcIf(if1);
				itc.setTgtIf(if2);
				itc.setDirected(isDirected);
				itc.setSetup(isSetup);
			}
		}
	}

	/**
	 * Interconnections have no direction
	 */
	private boolean existInterconnection(ArchitectureComponent cmp1,
			ArchitectureInterface if1, ArchitectureComponent cmp2,
			ArchitectureInterface if2) {

		Set<Interconnection> iSet = getAllEdges(cmp1, cmp2);
		Interconnection testInter = new Interconnection(if1, if2);
		Iterator<Interconnection> iterator = iSet.iterator();

		while (iterator.hasNext()) {
			Interconnection currentInter = iterator.next();

			if (currentInter.equals(testInter))
				return true;
		}

		return false;
	}

	public Set<Interconnection> undirectedEdgesOf(ArchitectureComponent cmp) {
		Set<Interconnection> iSet = new HashSet<Interconnection>();

		for (Interconnection incoming : incomingEdgesOf(cmp)) {
			if (!incoming.isDirected())
				iSet.add(incoming);
		}

		for (Interconnection outgoing : outgoingEdgesOf(cmp)) {
			if (!outgoing.isDirected())
				iSet.add(outgoing);
		}

		return iSet;
	}

	private boolean existInterconnection(ArchitectureComponent cmp1,
			ArchitectureComponent cmp2) {

		// Traduction in case the components are equal in names but notas java
		// objects
		for (ArchitectureComponent cmp : vertexSet()) {
			if (cmp.equals(cmp1))
				cmp1 = cmp;
			if (cmp.equals(cmp2))
				cmp2 = cmp;
		}

		boolean existInterconnection = false;
		Set<Interconnection> connections = getAllEdges(cmp1, cmp2);

		if (connections != null && !connections.isEmpty()) {
			existInterconnection = true;
		} else {
			// In case of undirected edges, tests opposite edges
			Set<Interconnection> reverseConnections = getAllEdges(cmp2, cmp1);
			if (reverseConnections != null) {
				for (Interconnection i : reverseConnections) {
					if (!i.isDirected()) {
						return true;
					}
				}
			}
		}

		return existInterconnection;
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
		return (getVertex(name));
	}

	/**
	 * Returns the Component with the given name
	 */
	public ArchitectureComponent getComponent(String id) {
		for (ArchitectureComponent component : getComponents()) {
			if (component.getName().equals(id)) {
				return component;
			}
		}

		return null;
	}

	/**
	 * Returns all the components of type type in alphabetical order of their
	 * names
	 */
	public Set<ArchitectureComponent> getComponents(
			ArchitectureComponentType type) {
		Set<ArchitectureComponent> ops = new ConcurrentSkipListSet<ArchitectureComponent>(
				new ArchitectureComponent.CmpComparator());

		Iterator<ArchitectureComponent> iterator = vertexSet().iterator();

		while (iterator.hasNext()) {
			ArchitectureComponent currentCmp = iterator.next();

			if (currentCmp.getType() == type) {
				ops.add(currentCmp);
			}
		}

		return ops;
	}

	/**
	 * Returns all the components
	 */
	public List<ArchitectureComponent> getComponents() {
		return new ArrayList<ArchitectureComponent>(vertexSet());
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

		ArchitectureComponentDefinition def = null;
		for (VLNV vlnv : architectureComponentDefinitions.keySet()) {
			if (vlnv.getName().equals(id)) {
				def = architectureComponentDefinitions.get(vlnv);
			}
		}

		if (def != null && def.getType() == type) {
			return def;
		}

		return null;
	}

	/**
	 * Returns the component definition with the given vlnv and type
	 */
	public ArchitectureComponentDefinition getComponentDefinition(
			ArchitectureComponentType type, VLNV vlnv) {

		ArchitectureComponentDefinition def = architectureComponentDefinitions
				.get(vlnv);

		if (def != null && def.getType() == type) {
			return def;
		}

		return null;
	}

	/**
	 * Returns all the interconnections
	 */
	public Set<Interconnection> getInterconnections() {
		return edgeSet();
	}

	public void setMainOperator(String mainOperatorName) {
		Operator o = (Operator) getComponent(
				ArchitectureComponentType.operator, mainOperatorName);
		if (o != null) {
			this.mainOperator = o;
		}
	}

	public void setMainMedium(String mainMediumName) {
		if (!(getComponent(ArchitectureComponentType.medium, mainMediumName) instanceof Medium)) {
			this.mainMedium = null;
			PreesmLogger
					.getLogger()
					.log(Level.SEVERE,
							"Add a medium in the architecture. Even if not connected, it specifies the default transfer parameters.");
		}
		Medium m = (Medium) getComponent(ArchitectureComponentType.medium,
				mainMediumName);
		if (m != null) {
			this.mainMedium = m;
		}

	}

	@Override
	public void update(AbstractGraph<?, ?> observable, Object arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public MultiCoreArchitecture clone() {

		// Creating archi
		MultiCoreArchitecture newArchi = new MultiCoreArchitecture(this.name);
		newArchi.setId(this.getId());
		HashMap<ArchitectureComponent, ArchitectureComponent> matchCopies = new HashMap<ArchitectureComponent, ArchitectureComponent>();

		for (BusReference ref : busReferences.values()) {
			newArchi.createBusReference(ref.getId());
		}

		for (ArchitectureComponentDefinition def : architectureComponentDefinitions
				.values()) {
			newArchi.addComponentDefinition(def.clone());
		}

		for (ArchitectureComponent vertex : vertexSet()) {
			ArchitectureComponent newVertex = (ArchitectureComponent) vertex
					.clone();

			newVertex.getPropertyBean().setValue(AbstractVertex.BASE, this);

			newVertex.fill(vertex, newArchi);
			newArchi.addVertex(newVertex);
			matchCopies.put(vertex, newVertex);
		}

		for (Interconnection edge : edgeSet()) {
			ArchitectureComponent newSource = matchCopies.get(edge.getSource());
			ArchitectureComponent newTarget = matchCopies.get(edge.getTarget());
			Interconnection newEdge = newArchi.addEdge(newSource, newTarget);
			newEdge.setSrcIf(newSource.getInterface(edge.getSrcIf()
					.getBusReference()));
			newEdge.setTgtIf(newTarget.getInterface(edge.getTgtIf()
					.getBusReference()));
			newEdge.setDirected(edge.isDirected());
			newEdge.setSetup(edge.isSetup());
		}

		for (HierarchyPort hierPort : hierarchyPorts) {
			newArchi.addHierarchyPort(hierPort);
		}

		/**
		 * main operator and medium.
		 */
		if (mainOperator != null) {
			newArchi.setMainOperator(mainOperator.getName());
		}
		if (mainMedium != null) {
			newArchi.setMainMedium(mainMedium.getName());
		}

		for (String key : getPropertyBean().keys()) {
			newArchi.getPropertyBean().setValue(key,
					getPropertyBean().getValue(key));
		}

		return newArchi;
	}

	@Override
	public boolean validateModel(Logger logger) throws SDF4JException {
		// TODO Auto-generated method stub
		return false;
	}
}
