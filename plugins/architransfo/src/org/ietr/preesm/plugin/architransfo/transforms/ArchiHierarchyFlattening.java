package org.ietr.preesm.plugin.architransfo.transforms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureInterface;
import org.ietr.preesm.core.architecture.BusReference;
import org.ietr.preesm.core.architecture.HierarchyPort;
import org.ietr.preesm.core.architecture.Interconnection;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Dma;
import org.ietr.preesm.core.architecture.simplemodel.DmaDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.workflow.tools.WorkflowLogger;
import org.sdf4j.model.AbstractGraph;
import org.sdf4j.model.AbstractVertex;
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.visitors.AbstractHierarchyFlattening;
import org.sdf4j.model.visitors.SDF4JException;

/**
 * HierarchyFlattening of an architecture with a given depth
 * 
 * @author mpelcat
 * 
 */
public class ArchiHierarchyFlattening extends
		AbstractHierarchyFlattening<MultiCoreArchitecture> {

	/**
	 * Flatten one vertex given it's parent
	 * 
	 * @param vertex
	 *            The vertex to flatten
	 * @param parentGraph
	 *            The new parent graph
	 * @throws InvalidExpressionException
	 */
	private void treatSubDesign(ArchitectureComponent subDesignCmp,
			MultiCoreArchitecture parentGraph)
			throws InvalidExpressionException {
		MultiCoreArchitecture subDesign = (MultiCoreArchitecture) subDesignCmp
				.getRefinement();
		// We work on a clone of the subDesign to move directly its components
		// to the parent design
		subDesign = subDesign.clone();

		// An interface in the component of the upper design corresponds to a
		// hierarchy port in the subDesign.
		Map<ArchitectureInterface, HierarchyPort> refSubdesignPorts = new HashMap<ArchitectureInterface, HierarchyPort>();

		String prefix = subDesignCmp.getName() + "_";

		// Checking correct component definition and linking hierarchy with
		// parent graph
		for (ArchitectureInterface intf : subDesignCmp.getInterfaces()) {
			if (subDesignCmp.getBusType(intf.getBusReference().getId()) == null) {
				WorkflowLogger.getLogger().log(
						Level.SEVERE,
						"The component " + subDesignCmp.getName()
								+ " should contain the port with id: "
								+ intf.getBusReference().getId());
				return;
			}

			HierarchyPort port = subDesign.getHierarchyPort(intf
					.getBusReference().getId());
			if (port == null) {
				WorkflowLogger
						.getLogger()
						.log(
								Level.SEVERE,
								"The subdesign "
										+ subDesign.getId()
										+ " should contain a hierarchical port with id: "
										+ intf.getBusReference().getId());
				return;
			} else {
				port.setConnectedCmpId(prefix + port.getConnectedCmpId());
				refSubdesignPorts.put(intf, port);
			}
		}

		// Adding the subgraphs components
		for (ArchitectureComponent component : subDesign.getComponents()) {
			component.setName(prefix + component.getName());
			parentGraph.addComponent(component);
		}

		// Adding the subgraphs interconnections
		for (Interconnection intercon : subDesign.getInterconnections()) {
			Interconnection newI = parentGraph.addEdge(intercon.getSource(),
					intercon.getTarget());
			newI.setSrcIf(intercon.getSrcIf());
			newI.setTgtIf(intercon.getTgtIf());
			newI.setDirected(intercon.isDirected());
			newI.setSetup(intercon.isSetup());
		}

		// Retrieving the upper graph input connections
		for (Interconnection i : parentGraph.incomingEdgesOf(subDesignCmp)) {
			ArchitectureInterface sourceIntf = i.getInterface(i.getSource());
			HierarchyPort targetHPort = refSubdesignPorts.get(i.getInterface(i
					.getTarget()));

			ArchitectureComponent target = parentGraph.getComponent(targetHPort
					.getConnectedCmpId());
			BusReference busRef = parentGraph.getBusReference(targetHPort
					.getBusRefName());
			Interconnection newI = parentGraph.addEdge(i.getSource(), target);
			newI.setSrcIf(sourceIntf);
			newI.setTgtIf(new ArchitectureInterface(busRef, target));
			newI.setDirected(i.isDirected());
			newI.setSetup(i.isSetup());
		}

		// Retrieving the upper graph output connections
		for (Interconnection i : parentGraph.outgoingEdgesOf(subDesignCmp)) {
			ArchitectureInterface targetIntf = i.getInterface(i.getTarget());
			HierarchyPort sourceHPort = refSubdesignPorts.get(i.getInterface(i
					.getSource()));

			ArchitectureComponent source = parentGraph.getComponent(sourceHPort
					.getConnectedCmpId());
			BusReference busRef = parentGraph.createBusReference(sourceHPort
					.getBusRefName());
			Interconnection newI = parentGraph.addEdge(source, i.getTarget());
			newI.setSrcIf(new ArchitectureInterface(busRef, source));
			newI.setTgtIf(targetIntf);
			newI.setDirected(i.isDirected());
			newI.setSetup(i.isSetup());

			// Retrieving Dma setup times
			if (newI.isSetup() && i.getTarget() instanceof Dma) {
				DmaDefinition def = ((DmaDefinition) i.getTarget()
						.getDefinition());
				long setupTime = 0;
				if (subDesignCmp instanceof Operator) {
					setupTime = def.getSetupTime((Operator) subDesignCmp);
					def.removeSetupTime((Operator) subDesignCmp);
				}
				def.addSetupTime(newI.getSource().getName(), setupTime);
			}
		}

		// Connecting hierarchical ports to subdesign
		for (HierarchyPort port : new HashSet<HierarchyPort>(parentGraph.getHierarchyPorts())) {
			if (port.getConnectedCmpId().equals(subDesignCmp.getName())) {
				parentGraph.addHierarchyPort(new HierarchyPort(port.getName(),
						prefix + subDesign.getHierarchyPort(port.getBusRefName())
								.getConnectedCmpId(), port.getBusRefName()));
			}
		}

	}

	/**
	 * Flatten the hierarchy of the given graph to the given depth
	 * 
	 * @param sdf
	 *            The graph to flatten
	 * @param depth
	 *            The depth to flatten the graph
	 * @param log
	 *            The logger in which output information
	 * @throws SDF4JException
	 */
	public void flattenGraph(MultiCoreArchitecture archi, int depth)
			throws SDF4JException {

		// Treating each component with subgraph
		Set<ArchitectureComponent> subDesignCmps = new HashSet<ArchitectureComponent>();
		if (depth > 0) {

			int newDepth = depth - 1;
			Set<ArchitectureComponent> originalComponents = new HashSet<ArchitectureComponent>(
					archi.vertexSet());
			for (ArchitectureComponent cmp : originalComponents) {
				if (cmp.getRefinement() != null
						&& cmp.getRefinement() instanceof MultiCoreArchitecture) {
					MultiCoreArchitecture subDesign = (MultiCoreArchitecture) cmp
							.getRefinement();

					flattenGraph(subDesign, newDepth);
					
					try {
						treatSubDesign(cmp, archi);
					} catch (InvalidExpressionException e) {
						e.printStackTrace();
						throw (new SDF4JException(e.getMessage()));
					}
					subDesignCmps.add(cmp);
				}
			}
		}

		for (HierarchyPort port : new HashSet<HierarchyPort>(archi
				.getHierarchyPorts())) {
			for (ArchitectureComponent cmp : subDesignCmps) {
				if (cmp.getName().equals(port.getConnectedCmpId())) {
					archi.removeHierarchyPort(port);
				}
			}
		}

		// Removing original vertices with subgraph and corresponding edges
		for (ArchitectureComponent cmp : subDesignCmps) {
			for (Interconnection i : new HashSet<Interconnection>(archi
					.edgesOf(cmp))) {
				archi.removeEdge(i);
			}
			archi.removeVertex(cmp);
		}

		output = archi;
	}

	@SuppressWarnings("rawtypes")
	protected void treatSinkInterface(AbstractVertex port,
			AbstractGraph parentGraph, int depth)
			throws InvalidExpressionException {
	}

	@SuppressWarnings("rawtypes")
	protected void treatSourceInterface(AbstractVertex port,
			AbstractGraph parentGraph, int depth)
			throws InvalidExpressionException {
	}

}
