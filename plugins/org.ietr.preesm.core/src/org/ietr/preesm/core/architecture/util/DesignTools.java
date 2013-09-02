/**
 * 
 */
package org.ietr.preesm.core.architecture.util;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.dftools.architecture.slam.ComponentInstance;
import net.sf.dftools.architecture.slam.Design;
import net.sf.dftools.architecture.slam.attributes.Parameter;
import net.sf.dftools.architecture.slam.component.Component;
import net.sf.dftools.architecture.slam.component.Operator;
import net.sf.dftools.architecture.slam.component.impl.ComNodeImpl;
import net.sf.dftools.architecture.slam.link.Link;

/**
 * Provides specific getters and setters for S-LAM architecture
 * 
 * @author mpelcat
 */
public class DesignTools {

	/**
	 * Value used to state a non-existing component
	 */
	public static ComponentInstance NO_COMPONENT_INSTANCE = null;

	/**
	 * Key of instance parameter used to store a property used in Preesm
	 */
	public static String OPERATOR_BASE_ADDRESS = "BaseAddress";

	/**
	 * Comparing two components using their names
	 */
	public static class ComponentInstanceComparator implements
			Comparator<ComponentInstance> {

		@Override
		public int compare(ComponentInstance o1, ComponentInstance o2) {
			return o1.getInstanceName().compareTo(o2.getInstanceName());
		}

	}

	/**
	 * Getting all operator instance ids in architecture
	 */
	public static Set<String> getOperatorInstanceIds(Design design) {
		Set<String> operatorInstanceIds = new HashSet<String>();

		for (ComponentInstance cmpInstance : design.getComponentInstances()) {
			if (cmpInstance.getComponent() instanceof Operator) {
				operatorInstanceIds.add(cmpInstance.getInstanceName());
			}
		}

		return operatorInstanceIds;
	}

	/**
	 * Getting all communication node instance ids in architecture
	 */
	public static Set<String> getComNodeInstanceIds(Design design) {
		Set<String> operatorInstanceIds = new HashSet<String>();

		for (ComponentInstance cmpInstance : design.getComponentInstances()) {
			if (cmpInstance.getComponent() instanceof ComNodeImpl) {
				operatorInstanceIds.add(cmpInstance.getInstanceName());
			}
		}

		return operatorInstanceIds;
	}

	/**
	 * Getting all operator instances in architecture
	 */
	public static Set<ComponentInstance> getOperatorInstances(Design design) {
		Set<ComponentInstance> operatorInstances = new HashSet<ComponentInstance>();

		for (ComponentInstance cmpInstance : design.getComponentInstances()) {
			if (cmpInstance.getComponent() instanceof Operator) {
				operatorInstances.add(cmpInstance);
			}
		}

		return operatorInstances;
	}

	/**
	 * Getting all operator ids in architecture
	 */
	public static Set<String> getOperatorComponentIds(Design design) {
		Set<String> operatorIds = new HashSet<String>();

		for (net.sf.dftools.architecture.slam.component.Component component : design
				.getComponentHolder().getComponents()) {
			if (component instanceof Operator) {
				operatorIds.add(component.getVlnv().getName());
			}
		}

		return operatorIds;
	}

	/**
	 * Getting all operator instances in architecture
	 */
	public static Set<Component> getOperatorComponents(Design design) {
		Set<Component> operators = new HashSet<Component>();

		for (net.sf.dftools.architecture.slam.component.Component component : design
				.getComponentHolder().getComponents()) {
			if (component instanceof Operator) {
				operators.add(component);
			}
		}

		return operators;
	}

	/**
	 * Getting all component instances in architecture
	 */
	public static Set<ComponentInstance> getComponentInstances(Design design) {
		Set<ComponentInstance> instances = new HashSet<ComponentInstance>();

		for (ComponentInstance cmpInstance : design.getComponentInstances()) {
			instances.add(cmpInstance);
		}

		return instances;
	}

	/**
	 * Getting the number of operator instances in architecture
	 */
	public static int getNumberOfOperatorInstances(Design design) {
		return getOperatorInstances(design).size();
	}

	/**
	 * Testing the presence of an instance in a list based on instance names
	 */
	public static boolean contains(List<ComponentInstance> instances,
			ComponentInstance instance) {
		for (ComponentInstance cmpInstance : instances) {
			if (cmpInstance.getInstanceName()
					.equals(instance.getInstanceName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Intersecting lists based on instance names
	 */
	public static void retainAll(List<ComponentInstance> instances,
			List<ComponentInstance> intersectInstances) {
		Iterator<ComponentInstance> iterator = instances.iterator();
		while (iterator.hasNext()) {
			ComponentInstance current = iterator.next();

			if (!contains(intersectInstances, current)) {
				iterator.remove();
			}
		}
	}

	/**
	 * Getting the instance of the given name
	 */
	public static ComponentInstance getComponentInstance(Design design,
			String name) {

		for (ComponentInstance cmpInstance : design.getComponentInstances()) {
			if (cmpInstance.getInstanceName().equals(name)) {
				return cmpInstance;
			}
		}

		return null;
	}

	/**
	 * Getting all instances of a given component
	 */
	public static Set<ComponentInstance> getInstancesOfComponent(Design design,
			Component component) {
		Set<ComponentInstance> instances = new HashSet<ComponentInstance>();

		for (ComponentInstance cmpInstance : design.getComponentInstances()) {
			if (cmpInstance.getComponent().getVlnv().getName()
					.equals(component.getVlnv().getName())) {
				instances.add(cmpInstance);
			}
		}

		return instances;
	}

	/**
	 * Getting a component parameter corresponding to the given key
	 */
	public static String getParameter(ComponentInstance instance, String key) {
		for (Parameter p : instance.getParameters()) {
			if (p.getKey().equals(key)) {
				return p.getValue();
			}
		}
		return null;
	}

	/**
	 * Getting the other extremity component of a link
	 */
	public static ComponentInstance getOtherEnd(Link link, ComponentInstance c) {
		if (!link.getDestinationComponentInstance().getInstanceName()
				.equals(c.getInstanceName()))
			return link.getDestinationComponentInstance();
		else
			return link.getSourceComponentInstance();
	}

	/**
	 * All undirected links linked to instance c
	 */
	public static Set<Link> getUndirectedLinks(Design design,
			ComponentInstance c) {
		Set<Link> undirectedLinks = new HashSet<Link>();

		for (Link link : design.getLinks()) {
			if (!link.isDirected()) {
				if (link.getDestinationComponentInstance().getInstanceName()
						.equals(c.getInstanceName())
						|| link.getSourceComponentInstance().getInstanceName()
								.equals(c.getInstanceName())) {
					undirectedLinks.add(link);
				}
			}
		}
		return undirectedLinks;
	}

	/**
	 * All undirected links linked to instance c
	 */
	public static Set<Link> getOutgoingDirectedLinks(Design design,
			ComponentInstance c) {
		Set<Link> directedLinks = new HashSet<Link>();

		for (Link link : design.getLinks()) {
			if (link.isDirected()) {
				if (link.getSourceComponentInstance().getInstanceName()
						.equals(c.getInstanceName())) {
					directedLinks.add(link);
				}
			}
		}
		return directedLinks;
	}

	/**
	 * All undirected links linked to instance c
	 */
	public static Set<Link> getIncomingDirectedLinks(Design design,
			ComponentInstance c) {
		Set<Link> directedLinks = new HashSet<Link>();

		for (Link link : design.getLinks()) {
			if (link.isDirected()) {
				if (link.getDestinationComponentInstance().getInstanceName()
						.equals(c.getInstanceName())) {
					directedLinks.add(link);
				}
			}
		}
		return directedLinks;
	}
}
