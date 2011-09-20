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

import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.ietr.preesm.core.architecture.parser.VLNV;
import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.sdf4j.model.AbstractGraph;
import org.sdf4j.model.AbstractVertex;
import org.sdf4j.model.visitors.SDF4JException;

/**
 * Architecture based on a fixed number of cores
 * 
 * @author mpelcat
 */
public class MultiCoreArchitecture extends
		AbstractGraph<Component, Interconnection> implements
		IDistributedArchitecture {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7297201138766758092L;

	/**
	 * List of the component definitions with their IDs.
	 */
	private Map<VLNV, ComponentDefinition> architectureComponentDefinitions;

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
		architectureComponentDefinitions = new HashMap<VLNV, ComponentDefinition>();
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
	public ComponentDefinition addComponentDefinition(ComponentType type,
			VLNV vlnv) {

		if (vlnv.getName().isEmpty()) {
			WorkflowLogger
					.getLogger()
					.log(Level.WARNING,
							"careful: at least one component has no definition in the architecture");
		}

		for (VLNV currentVlnv : architectureComponentDefinitions.keySet()) {
			if (currentVlnv.equals(vlnv)) {
				return architectureComponentDefinitions.get(currentVlnv);
			}
		}

		ComponentDefinition def = ComponentDefinitionFactory.createElement(
				type, vlnv);
		addComponentDefinition(def);
		return def;

	}

	/**
	 * Adds the definition of a component and returns it to let the user add
	 * specific properties
	 */
	public ComponentDefinition addComponentDefinition(ComponentDefinition def) {

		architectureComponentDefinitions.put(def.getVlnv(), def);
		return def;
	}

	/**
	 * Creates and adds a component
	 */
	public Component addComponent(ComponentType type, VLNV defVLNV, String name) {
		if (getVertex(name) != null) {
			return getVertex(name);
		} else {
			ComponentDefinition newDef = addComponentDefinition(type, defVLNV);
			Component cmp = ComponentFactory.createElement(newDef, name);
			addVertex(cmp);
			return cmp;
		}
	}

	/**
	 * Creates and adds a component
	 */
	public Component addComponent(Component component) {
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
	public void connect(Component cmp1, Interface if1, Component cmp2,
			Interface if2, boolean isDirected, boolean isSetup) {
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
	private boolean existInterconnection(Component cmp1, Interface if1,
			Component cmp2, Interface if2) {

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

	public Set<Interconnection> undirectedEdgesOf(Component cmp) {
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

	private boolean existInterconnection(Component cmp1, Component cmp2) {

		// Traduction in case the components are equal in names but notas java
		// objects
		for (Component cmp : vertexSet()) {
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
			Set<Component> cmpSet = getComponents(ComponentType.medium);
			if (!cmpSet.isEmpty())
				return (Medium) getComponents(ComponentType.medium).toArray()[0];
			else
				return null;
		} else {
			return mainMedium;
		}
	}

	public Operator getMainOperator() {
		if (mainOperator == null) {
			Set<Component> cmpSet = getComponents(ComponentType.operator);
			if (!cmpSet.isEmpty())
				return (Operator) getComponents(ComponentType.operator)
						.toArray()[0];
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
		Iterator<Component> iterator = getComponents(ComponentType.medium)
				.iterator();

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
		return getComponents(ComponentType.operator).size();
	}

	/**
	 * Returns the Component with the given type and name
	 */
	public Component getComponent(ComponentType type, String name) {
		return (getVertex(name));
	}

	/**
	 * Returns the Component with the given name
	 */
	public Component getComponent(String id) {
		for (Component component : getComponents()) {
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
	public Set<Component> getComponents(ComponentType type) {
		Set<Component> ops = new ConcurrentSkipListSet<Component>(
				new Component.CmpComparator());

		Iterator<Component> iterator = vertexSet().iterator();

		while (iterator.hasNext()) {
			Component currentCmp = iterator.next();

			if (currentCmp.getType() == type) {
				ops.add(currentCmp);
			}
		}

		return ops;
	}

	/**
	 * Returns all the components
	 */
	public List<Component> getComponents() {
		return new ArrayList<Component>(vertexSet());
	}

	/**
	 * Returns all the components definitions of type type
	 */
	public Set<ComponentDefinition> getComponentDefinitions(ComponentType type) {
		Set<ComponentDefinition> opdefs = new HashSet<ComponentDefinition>();

		Iterator<ComponentDefinition> iterator = architectureComponentDefinitions
				.values().iterator();

		while (iterator.hasNext()) {
			ComponentDefinition currentCmp = iterator.next();

			if (currentCmp.getType() == type) {
				opdefs.add(currentCmp);
			}
		}

		return opdefs;
	}

	/**
	 * Returns the component definition with the given id and type
	 */
	public ComponentDefinition getComponentDefinition(ComponentType type,
			String id) {

		ComponentDefinition def = null;
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
	public ComponentDefinition getComponentDefinition(ComponentType type,
			VLNV vlnv) {

		ComponentDefinition def = architectureComponentDefinitions.get(vlnv);

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
		Operator o = (Operator) getComponent(ComponentType.operator,
				mainOperatorName);
		if (o != null) {
			this.mainOperator = o;
		}
	}

	public void setMainMedium(String mainMediumName) {
		if (!(getComponent(ComponentType.medium, mainMediumName) instanceof Medium)) {
			this.mainMedium = null;
			WorkflowLogger
					.getLogger()
					.log(Level.SEVERE,
							"Add a medium in the architecture. Even if not connected, it specifies the default transfer parameters.");
		}
		Medium m = (Medium) getComponent(ComponentType.medium, mainMediumName);
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
		HashMap<Component, Component> matchCopies = new HashMap<Component, Component>();

		for (BusReference ref : busReferences.values()) {
			newArchi.createBusReference(ref.getId());
		}

		for (ComponentDefinition def : architectureComponentDefinitions
				.values()) {
			newArchi.addComponentDefinition(def.clone());
		}

		for (Component vertex : vertexSet()) {
			Component newVertex = (Component) vertex.clone();

			newVertex.getPropertyBean().setValue(AbstractVertex.BASE, this);

			newVertex.fill(vertex, newArchi);
			newArchi.addVertex(newVertex);
			matchCopies.put(vertex, newVertex);
		}

		for (Interconnection edge : edgeSet()) {
			Component newSource = matchCopies.get(edge.getSource());
			Component newTarget = matchCopies.get(edge.getTarget());
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

	/**
	 * Getting IDs identifying each operator definition.
	 */
	@Override
	public Set<String> getOperatorDefinitionIds() {

		Set<String> opdefs = new HashSet<String>();

		for (ComponentDefinition def : architectureComponentDefinitions
				.values()) {

			if (def.getType() == ComponentType.operator) {
				opdefs.add(def.getId());
			}
		}

		return opdefs;

	}

	public Set<String> getAllOperatorIds() {
		Set<String> ids = new HashSet<String>();

		for (Component c : getComponents(ComponentType.operator)) {
			ids.add(c.getName());
		}

		return ids;
	}
}
