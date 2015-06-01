/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clement Guy
 * 
 * [mpelcat,jnezan,kdesnos,jheulot,cguy]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.ui.pimm.layout;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.util.FifoCycleDetector;

/**
 * {@link AbstractCustomFeature} automating the layout process for PiMM graphs.
 * 
 * 
 * @author kdesnos
 */
public class AutoLayoutFeature extends AbstractCustomFeature {

	boolean hasDoneChange = false;

	public AutoLayoutFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return true;
	}

	@Override
	public void execute(ICustomContext context) {
		System.out.println("Layout the diagram");
		Diagram diagram = getDiagram();
		hasDoneChange = true;

		// Step 1 - Clear all bendpoints
		clearBendpoints(diagram);

		// Step 2 - Layout actors in precedence order
		// (ignoring cycles / delayed FIFOs in cycles)
		layoutActors(diagram);

		// Step 3 - Layout non-feedback connections

		// Step 4 - Layout feedback connections

		// Step 5 - Layout Parameters and Dependencies
	}

	private void layoutActors(Diagram diagram) {
		PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(diagram);

		// 1. Find all edges that must be ignored to have no cyclic datapath
		List<Fifo> feedbackEdges = findFeedbackEdges(graph);

	}

	private List<Fifo> findFeedbackEdges(PiGraph graph) {
		List<Fifo> feedbackEdges = new ArrayList<Fifo>();

		// Search for cycles in the graph
		boolean hasCycle = false;
		FifoCycleDetector detector = new FifoCycleDetector(false);
		do {
			hasCycle = false;

			// Find as many cycles as possible
			detector.clear();
			detector.addIgnoredFifos(feedbackEdges);
			detector.doSwitch(graph);
			List<List<AbstractActor>> cycles = detector.getCycles();

			// Find the "feedback" fifo in each cycle
			if (!cycles.isEmpty()) {
				hasCycle = true;
				// For each cycle find the feedback fifo(s).
				for (List<AbstractActor> cycle : cycles) {
					feedbackEdges.addAll(findCycleFeedbackFifos(cycle));
				}
			}
		} while (hasCycle);

		return feedbackEdges;
	}

	/**
	 * @param cycle
	 *            A list of {@link AbstractActor} forming a Cycle.
	 */
	protected List<Fifo> findCycleFeedbackFifos(List<AbstractActor> cycle) {
		// Find the Fifos between each pair of actor of the cycle
		List<List<Fifo>> cyclesFifos = new ArrayList<List<Fifo>>();
		for (int i = 0; i < cycle.size(); i++) {
			AbstractActor srcActor = cycle.get(i);
			AbstractActor dstActor = cycle.get((i + 1) % cycle.size());

			List<Fifo> outFifos = new ArrayList<Fifo>();
			srcActor.getDataOutputPorts().forEach(
					port -> {
						if (port.getOutgoingFifo().getTargetPort().eContainer()
								.equals(dstActor))
							outFifos.add(port.getOutgoingFifo());
					});
			cyclesFifos.add(outFifos);
		}

		// Find a list of FIFO between a pair of actor with delays on all FIFOs
		List<Fifo> feedbackFifos = null;
		for (List<Fifo> cycleFifos : cyclesFifos) {
			boolean hasDelays = true;
			for (Fifo fifo : cycleFifos) {
				hasDelays &= (fifo.getDelay() != null);
			}

			if (hasDelays) {
				// Keep the shortest list of feedback delay
				feedbackFifos = (feedbackFifos == null || feedbackFifos.size() > cycleFifos
						.size()) ? cycleFifos : feedbackFifos;
			}
		}
		if (feedbackFifos != null) {
			return feedbackFifos;
		} else {
			// If no feedback fifo with delays were found. Select a list with a
			// small number of fifos
			cyclesFifos.sort((l1, l2) -> l1.size() - l2.size());
			return cyclesFifos.get(0);
		}
	}

	/**
	 * Clear all the bendpoints of the {@link Fifo} and {@link Dependency} in
	 * the diagram passed as a parameter.
	 * 
	 * @param diagram
	 */
	private void clearBendpoints(Diagram diagram) {
		for (Connection connection : diagram.getConnections()) {
			((FreeFormConnection) connection).getBendpoints().clear();
		}
	}

	@Override
	public String getName() {
		return "Layout Diagram";
	}

	@Override
	public boolean hasDoneChanges() {
		return hasDoneChange;
	}
}
