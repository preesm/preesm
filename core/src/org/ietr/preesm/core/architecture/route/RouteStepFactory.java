/**
 * 
 */
package org.ietr.preesm.core.architecture.route;

import java.util.List;

import org.ietr.preesm.core.architecture.Interconnection;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.AbstractNode;
import org.ietr.preesm.core.architecture.simplemodel.Dma;
import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.architecture.simplemodel.Operator;

/**
 * Depending on the architecture nodes separating two operators, generates a
 * suited route step.
 * 
 * @author mpelcat
 */
public class RouteStepFactory {

	private MultiCoreArchitecture archi = null;

	public RouteStepFactory(MultiCoreArchitecture archi) {
		super();
		this.archi = archi;
	}

	/**
	 * Generates the suited route steps from intermediate nodes
	 */
	public AbstractRouteStep getRouteStep(Operator source,
			List<AbstractNode> nodes, Operator target) {
		AbstractRouteStep step = null;

		if (nodes.size() == 1 && nodes.get(0) instanceof Medium) {
			return new MediumRouteStep(source, (Medium) nodes.get(0), target);
		} else {
			Dma dma = getDma(nodes, source);
			if (dma != null) {
				step = new DmaRouteStep(source, nodes, target, dma);
			} else {
				step = new NodeRouteStep(source, nodes, target);
			}
		}

		return step;
	}

	/**
	 * Gets the dma corresponding to the step if any exists.
	 * The Dma must have a setup link with the source.
	 */
	private Dma getDma(List<AbstractNode> nodes, Operator dmaSetup) {
		Dma dma = null;
		for (AbstractNode node : nodes) {
			for (Interconnection i : archi.undirectedEdgesOf(node)) {
				if (i.getSource() instanceof Dma)
					dma = (Dma) i.getSource();
				if (i.getSource() instanceof Dma)
					dma = (Dma) i.getSource();

				if (dma != null) {
					if (existSetup(dma, dmaSetup)) {
						return dma;
					}
				}
			}
		}
		return null;
	}


	/**
	 * Checks if a setup link exists between dma and operator
	 */
	private boolean existSetup(Dma dma, Operator op) {

		for (Interconnection i : archi.incomingEdgesOf(dma)) {
			if (i.getSource().equals(op)) {
				return true;
			}
		}

		return false;
	}
}
