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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.PlatformUI;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.util.DependencyCycleDetector;
import org.ietr.preesm.experiment.model.pimm.util.FifoCycleDetector;
import org.ietr.preesm.ui.pimm.features.AddDelayFeature;
import org.ietr.preesm.ui.pimm.features.AddParameterFeature;
import org.ietr.preesm.ui.pimm.features.DeleteDelayFeature;
import org.ietr.preesm.ui.pimm.features.MoveAbstractActorFeature;

/**
 * {@link AbstractCustomFeature} automating the layout process for PiMM graphs.
 * 
 * 
 * @author kdesnos
 */
public class AutoLayoutFeature extends AbstractCustomFeature {

	class Range {
		int end;
		int start;

		public Range(int start, int end) {
			this.start = start;
			this.end = end;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Range) {
				return (((Range) obj).start == this.start && ((Range) obj).end == this.end);
			} else {
				return false;
			}
		}

		@Override
		public String toString() {
			return "[" + start + "," + end + "]";
		}
	}

	private static final int BENDPOINT_SPACE = MoveAbstractActorFeature.BENDPOINT_SPACE;
	private static final int FIFO_SPACE = 7;
	protected static final int X_INIT = 50;
	private static final int X_SPACE = 100;
	private static final int X_SPACE_PARAM = X_SPACE / 2;
	protected static final int Y_INIT = 250;
	private static final int Y_SPACE = 50;
	private static final int Y_SPACE_PARAM = 60;
	private static final int DEPENDENCY_SPACE = 8;

	/**
	 * Feedback {@link Fifo} identified in a {@link PiGraph}. {@see
	 * AutoLayoutFeature#findFeedbackFifos(PiGraph)}.
	 */
	private List<Fifo> feedbackFifos;

	boolean hasDoneChange = false;

	/**
	 * Actors sorted stage by stage.
	 * {@link AutoLayoutFeature#stageByStageSort(PiGraph, List).}
	 */
	private List<List<AbstractActor>> stagedActors;

	/**
	 * For each stage, list of the Y coordinates starting a Y_SPACE gap between
	 * two actors.
	 */
	private List<List<Range>> stagesGaps;

	/**
	 * Width of the stages once the have been layouted.
	 */
	private List<Range> stageWidth;

	/**
	 * Initial vertical position for parameters. (computed in
	 * stageByStageParameterLayout).
	 */
	private int yParamInitPos;
	private Map<Parameter, Integer> paramXPositions;

	public AutoLayoutFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return true;
	}

	/**
	 * Clear all the bendpoints of the {@link Fifo} and {@link Dependency} in
	 * the diagram passed as a parameter.
	 * 
	 * @param diagram
	 */
	protected void clearBendpoints(Diagram diagram) {
		for (Connection connection : diagram.getConnections()) {
			((FreeFormConnection) connection).getBendpoints().clear();
		}
	}

	/**
	 * Create {@link List} of {@link List} of {@link AbstractActor} where each
	 * innermost {@link List} is called a stage. An {@link AbstractActor} is put
	 * in a stage only if all its predecessors (not considering feedbackFifos)
	 * are already added to previous stages.
	 * 
	 * @param feedbackFifos
	 *            {@link List} of {@link Fifo} that are ignored when scanning
	 *            the predecessors of an actor.
	 * @param actors
	 *            {@link AbstractActor} to sort.
	 * @param srcActors
	 *            First stage of {@link Fifo}, given by the
	 *            {@link #findSrcActors(List, List)}.
	 * @return the stage by stage list of actors.
	 */
	protected List<List<AbstractActor>> createActorStages(
			List<Fifo> feedbackFifos, List<AbstractActor> actors,
			final List<AbstractActor> srcActors) {
		List<List<AbstractActor>> stages = new ArrayList<List<AbstractActor>>();

		//
		List<AbstractActor> processedActors = new ArrayList<AbstractActor>();
		processedActors.addAll(srcActors);
		Set<AbstractActor> nextStage = new LinkedHashSet<AbstractActor>();
		List<AbstractActor> currentStage = srcActors;
		List<AbstractActor> dataOutputInterfaces = new ArrayList<AbstractActor>();

		// Keep DataInputInterfaces for the first stage
		Iterator<AbstractActor> iter = srcActors.iterator();
		while (iter.hasNext()) {
			AbstractActor actor = iter.next();
			if (!(actor instanceof DataInputInterface)) {
				iter.remove();
				processedActors.remove(actor);
				nextStage.add(actor);
			}
		}

		// Check if there is any Interface in the first stage
		if (currentStage.isEmpty()) {
			currentStage = new ArrayList<AbstractActor>(nextStage);
			processedActors.addAll(currentStage);
			nextStage = new LinkedHashSet<AbstractActor>();
		}

		// Register first stage
		stages.add(currentStage);

		do {
			// Find candidates for the next stage in successors of current one
			for (AbstractActor actor : currentStage) {
				for (DataOutputPort port : actor.getDataOutputPorts()) {
					if (!feedbackFifos.contains(port.getOutgoingFifo())) {
						nextStage.add((AbstractActor) port.getOutgoingFifo()
								.getTargetPort().eContainer());
					}
				}
			}

			// Check if all predecessors of the candidates have already been
			// added in a previous stages
			iter = nextStage.iterator();
			while (iter.hasNext()) {
				AbstractActor actor = iter.next();
				boolean hasUnstagedPredecessor = false;
				for (DataInputPort port : actor.getDataInputPorts()) {
					Fifo incomingFifo = port.getIncomingFifo();
					hasUnstagedPredecessor |= !feedbackFifos
							.contains(incomingFifo)
							&& !processedActors.contains(incomingFifo
									.getSourcePort().eContainer());
				}
				if (hasUnstagedPredecessor) {
					iter.remove();
				} else if ((actor instanceof DataOutputInterface)) {
					dataOutputInterfaces.add(actor);
					processedActors.add(actor);
					iter.remove();
				}
			}

			// Prepare next iteration
			currentStage = new ArrayList<AbstractActor>(nextStage);
			stages.add(currentStage);
			processedActors.addAll(currentStage);
			nextStage = new LinkedHashSet<AbstractActor>();
		} while (processedActors.size() < actors.size());

		// If the last stage is empty (if there were only dataOutputInterface)
		// remove it
		if (stages.get(stages.size() - 1).size() == 0) {
			stages.remove(stages.size() - 1);
		}

		if (!dataOutputInterfaces.isEmpty()) {
			stages.add(dataOutputInterfaces);
		}

		return stages;
	}

	protected List<List<Parameter>> createParameterStages(
			List<Parameter> params, List<Parameter> roots) {
		// Initializations
		List<List<Parameter>> stages = new ArrayList<List<Parameter>>();
		List<Parameter> processedParams = new ArrayList<Parameter>(roots);
		Set<Parameter> nextStage = new LinkedHashSet<Parameter>();
		stages.add(roots);
		List<Parameter> currentStage = roots;

		do {
			// Find candidates for the next stage in successors of current one
			for (Parameter param : currentStage) {
				for (Dependency dependency : param.getOutgoingDependencies()) {
					if (dependency.getGetter().eContainer() instanceof Parameter) {
						nextStage.add((Parameter) dependency.getGetter()
								.eContainer());
					}
				}
			}

			// Check if all predecessors of the candidates have already been
			// added in a previous stages
			for (Iterator iter = nextStage.iterator(); iter.hasNext();) {
				Parameter param = (Parameter) iter.next();

				boolean hasUnstagedPredecessor = false;
				for (ConfigInputPort port : param.getConfigInputPorts()) {
					Dependency incomingDependency = port
							.getIncomingDependency();
					hasUnstagedPredecessor |= incomingDependency.getSetter() instanceof Parameter
							&& !processedParams.contains(incomingDependency
									.getSetter());
				}
				if (hasUnstagedPredecessor) {
					iter.remove();
				}
			}

			// Prepare next iteration
			currentStage = new ArrayList<Parameter>(nextStage);
			stages.add(currentStage);
			processedParams.addAll(currentStage);
			nextStage = new LinkedHashSet<Parameter>();
		} while (processedParams.size() < params.size());

		return stages;
	}

	@Override
	public void execute(ICustomContext context) {
		System.out.println("Layout the diagram");
		Diagram diagram = getDiagram();

		// Check if there are parameterization cycles in the graph.
		// In such a case, do not layout !
		PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(diagram);
		DependencyCycleDetector dcd = new DependencyCycleDetector();
		dcd.doSwitch(graph);

		if (dcd.cyclesDetected()) {
			IStatus warning = new Status(
					IStatus.ERROR,
					"org.ietr.preesm.experiment",
					1,
					"This graph contains cyclic parameterization dependencies.\n"
							+ "Remove these cycles before layouting the graph.",
					null);
			ErrorDialog.openError(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), "Layout error",
					null, warning);
			return;

		}

		hasDoneChange = true;

		// Step 1 - Clear all bendpoints
		clearBendpoints(diagram);

		// Step 2 - Layout actors in precedence order
		// (ignoring cycles / delayed FIFOs in cycles)
		layoutActors(diagram);

		// Step 3 - Layout fifo connections
		layoutFifos(diagram);

		// Step 4 - Layout Parameters and dependencies
		layoutParameters(diagram);
	}

	/**
	 * @param gaps
	 * @param optimY
	 * @param closestGap
	 * @return Whether the given y Coordinate is closest to the top (
	 *         <code>true</code>) or bottom (<code>false</code>) of the found
	 *         closest Gap.
	 */
	protected boolean findClosestGap(List<Range> gaps, int optimY,
			Range closestGap) {
		boolean isTop = false; // closest to the top or the bottom of the
								// range

		int distance = Integer.MAX_VALUE;
		for (Range range : gaps) {
			int startDist = Math.abs(optimY - range.start);
			int endDist = Math.abs(optimY - range.end);

			int minDist = (startDist < endDist) ? startDist : endDist;

			if (minDist <= distance) {
				closestGap.start = range.start;
				closestGap.end = range.end;
				distance = minDist;
				isTop = (startDist < endDist);
			}
		}
		return isTop;
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

	protected List<Fifo> findFeedbackFifos(PiGraph graph) {
		List<Fifo> feedbackEdges = new ArrayList<Fifo>();

		// Search for cycles in the graph
		boolean hasCycle = false;
		// fast search, find cycles one by one
		FifoCycleDetector detector = new FifoCycleDetector(true);
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

	protected List<Parameter> findRootParameters(PiGraph graph,
			List<Parameter> params) {
		List<Parameter> roots = new ArrayList<Parameter>();

		for (Parameter p : params) {
			boolean hasDependencies = false;
			for (ConfigInputPort port : p.getConfigInputPorts()) {
				hasDependencies |= port.getIncomingDependency().getSetter() instanceof Parameter;
			}

			if (!hasDependencies) {
				roots.add(p);
			}
		}
		return roots;
	}

	/**
	 * Find {@link AbstractActor} without any predecessors. {@link Fifo} passed
	 * as parameters are ignored.
	 * 
	 * @param feedbackFifos
	 *            {@link List} of ignored {@link Fifo}.
	 * @param actors
	 *            {@link AbstractActor} containing source actors.
	 * @return the list of {@link AbstractActor} that do not have any
	 *         predecessors
	 */
	protected List<AbstractActor> findSrcActors(List<Fifo> feedbackFifos,
			List<AbstractActor> actors) {
		final List<AbstractActor> srcActors = new ArrayList<AbstractActor>();
		for (AbstractActor actor : actors) {
			boolean hasInputFifos = false;

			for (DataInputPort port : actor.getDataInputPorts()) {
				hasInputFifos |= !feedbackFifos
						.contains(port.getIncomingFifo());
			}

			if (!hasInputFifos) {
				srcActors.add(actor);
			}
		}
		return srcActors;
	}

	/**
	 * Get the index of the stage to which the actor belongs.
	 * 
	 * @param actor
	 *            The searched {@link AbstractActor}
	 * @param stagedActors
	 *            the stages
	 * @return the index of the stage containing the actor.
	 */
	protected int getActorStage(AbstractActor actor,
			List<List<AbstractActor>> stagedActors) {
		for (int i = 0; i < stagedActors.size(); i++) {
			if (stagedActors.get(i).contains(actor)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param diagram
	 * @param fifo
	 * @return
	 * @throws RuntimeException
	 */
	protected ContainerShape getDelayPE(Diagram diagram, Fifo fifo)
			throws RuntimeException {
		// Get all delays with identical attributes (may not be the
		// right delay is several delays have the same properties.)
		List<PictogramElement> pes = Graphiti.getLinkService()
				.getPictogramElements(diagram, fifo.getDelay());
		PictogramElement pe = null;
		for (PictogramElement p : pes) {
			if (p instanceof ContainerShape
					&& getBusinessObjectForPictogramElement(p) == fifo
							.getDelay()) {
				pe = p;
			}
		}
		// if PE is still null.. something is deeply wrong with this
		// graph !
		if (pe == null) {
			throw new RuntimeException(
					"Pictogram element associated to delay of Fifo "
							+ fifo.getId() + " could not be found.");
		}
		return (ContainerShape) pe;
	}

	/**
	 * @param diagram
	 * @param edge
	 * @return
	 * @throws RuntimeException
	 */
	protected FreeFormConnection getFreeFormConnectionOfEdge(Diagram diagram,
			EObject edge) throws RuntimeException {
		List<PictogramElement> pes = Graphiti.getLinkService()
				.getPictogramElements(diagram, edge);
		FreeFormConnection ffc = null;
		for (PictogramElement pe : pes) {
			if (getBusinessObjectForPictogramElement(pe) == edge
					&& pe instanceof FreeFormConnection) {
				ffc = (FreeFormConnection) pe;
			}
		}

		// if PE is still null.. something is deeply wrong with this
		// graph !
		if (ffc == null) {
			throw new RuntimeException("Pictogram element associated Edge "
					+ edge + " could not be found.");
		}
		return ffc;
	}

	@Override
	public String getName() {
		return "Layout Diagram";
	}

	/**
	 * @param stagedParameters
	 * @return
	 */
	protected List<Parameter> getParameterVerticalOrder(
			List<List<Parameter>> stagedParameters) {
		// Initialize the list with last stage
		List<Parameter> paramVertOrder = new LinkedList<Parameter>(
				stagedParameters.get(stagedParameters.size() - 1));
		for (int stageIdx = stagedParameters.size() - 2; stageIdx >= 0; stageIdx--) {
			for (Parameter param : stagedParameters.get(stageIdx)) {
				// Find index of successors in paramVertOrder
				int lastIdx = -1;
				int firstIdx = -1;
				for (Dependency dependency : param.getOutgoingDependencies()) {
					Object getter = dependency.getGetter().eContainer();
					if (getter instanceof Parameter) {
						int paramOrder = paramVertOrder.indexOf(getter);
						firstIdx = (firstIdx == -1 || paramOrder < firstIdx) ? paramOrder
								: firstIdx;
						lastIdx = (paramOrder > lastIdx) ? paramOrder : lastIdx;
					}
				}

				// Insert in the middle of param indexes
				lastIdx = (lastIdx == -1) ? 0 : lastIdx;
				firstIdx = (firstIdx == -1) ? 0 : firstIdx;
				paramVertOrder.add((lastIdx + firstIdx + 1) / 2, param);
			}
		}
		return paramVertOrder;
	}

	@Override
	public boolean hasDoneChanges() {
		return hasDoneChange;
	}

	protected void layoutActors(Diagram diagram) {
		PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(diagram);

		if (!graph.getVertices().isEmpty()) {
			feedbackFifos = findFeedbackFifos(graph);

			// 2. Sort stage by stage (ignoring feedback FIFO)
			stagedActors = stageByStageActorSort(graph, feedbackFifos);

			// 3. Layout actors according to the topological order
			stageByStageActorLayout(diagram, stagedActors);
		}
	}

	protected void layoutDependencies(Diagram diagram,
			List<List<Parameter>> stagedParameters,
			List<Parameter> paramVertOrder) {

		// Variable used for the straight horizontal dependencies used to
		// distributes values to actors
		int currentY = yParamInitPos + DEPENDENCY_SPACE;
		int currentX = 0; //(DEPENDENCY_SPACE / 2) * paramVertOrder.size() ;
		boolean currentYUsed = false;

		// Process dependencies one by one, scanning the parameters
		for (Parameter param : paramVertOrder) {

			if (currentYUsed) {
				currentY += DEPENDENCY_SPACE;
				currentX += DEPENDENCY_SPACE / 2;
				currentYUsed = false;
			}

			// Get the stage of the dependences setter
			int setterStage = getParameterStage(stagedParameters, param);

			for (Dependency dependency : param.getOutgoingDependencies()) {
				// Get the polyline
				FreeFormConnection ffc = getFreeFormConnectionOfEdge(diagram,
						dependency);

				// Get the type of the getter
				EObject getter = dependency.getGetter().eContainer();
				if (getter instanceof Parameter) {
					// Get stage
					int getterStage = getParameterStage(stagedParameters,
							(Parameter) getter);
					// layout only if getter is more than one stage away from
					// setter
					int xPosition = paramXPositions.get(param);
					int yPosition = yParamInitPos
							- (stagedParameters.size() - 1 - (getterStage - 1))
							* Y_SPACE_PARAM;
					Point bPoint = Graphiti.getGaCreateService().createPoint(
							xPosition, yPosition);
					ffc.getBendpoints().add(bPoint);

				} else if (getter instanceof DataInputInterface
						|| getter instanceof DataOutputInterface) {
				} else if (getter instanceof AbstractActor) {
					currentYUsed = true;
					// Add a first point below the actor
					int xPosition = paramXPositions.get(param);
					Point bPoint = Graphiti.getGaCreateService().createPoint(
							xPosition, currentY);
					ffc.getBendpoints().add(0, bPoint);

					// Retrieve the last bendpoint of the ffc (added when the
					// actor was moved.)
					Point lastBp = ffc.getBendpoints().get(
							ffc.getBendpoints().size() - 1);
					// Move it
					lastBp.setX(lastBp.getX() - currentX);
					// Add a new bendpoint on top of it
					ffc.getBendpoints().add(
							ffc.getBendpoints().size() - 1,
							Graphiti.getGaCreateService().createPoint(
									lastBp.getX(), currentY));

				} else {
					System.out.println(getter.getClass());
				}
			}
		}
	}

	/**
	 * @param stagedParameters
	 * @param param
	 * @param setterStage
	 * @return
	 */
	protected int getParameterStage(List<List<Parameter>> stagedParameters,
			Parameter param) {
		int setterStage = -1;
		for (List<Parameter> stage : stagedParameters) {
			if (stage.contains(param)) {
				setterStage = stagedParameters.indexOf(stage);
			}
		}
		return setterStage;
	}

	protected void layoutFeedbackFifos(Diagram diagram,
			List<Fifo> feedbackFifos, List<List<AbstractActor>> stagedActors,
			List<List<Range>> stagesGaps, List<Range> stageWidth) {
		// Sort FIFOs according to the number of stages through which they're
		// going
		List<Fifo> sortedFifos = new ArrayList<Fifo>(feedbackFifos);
		sortedFifos.sort((f1, f2) -> {
			int srcStage1 = getActorStage((AbstractActor) f1.getSourcePort()
					.eContainer(), stagedActors);
			int dstStage1 = getActorStage((AbstractActor) f1.getTargetPort()
					.eContainer(), stagedActors);

			int srcStage2 = getActorStage((AbstractActor) f2.getSourcePort()
					.eContainer(), stagedActors);
			int dstStage2 = getActorStage((AbstractActor) f2.getTargetPort()
					.eContainer(), stagedActors);

			return Math.abs(srcStage1 - dstStage1)
					- Math.abs(srcStage2 - dstStage2);
		});

		// Add new gap on top of all stages
		for (List<Range> gaps : stagesGaps) {
			gaps.add(new Range(0, X_INIT));
		}

		// Layout feedback FIFOs one by one, from short to long distances
		for (Fifo fifo : sortedFifos) {
			FreeFormConnection ffc = getFreeFormConnectionOfEdge(diagram, fifo);

			int srcStage = getActorStage((AbstractActor) fifo.getSourcePort()
					.eContainer(), stagedActors);
			int dstStage = getActorStage((AbstractActor) fifo.getTargetPort()
					.eContainer(), stagedActors);

			// Do the layout for each stage
			for (int stageIdx = srcStage; stageIdx >= dstStage; stageIdx--) {
				// Find the closest gap to the feedback fifo
				Point penultimate = ffc.getBendpoints().get(
						ffc.getBendpoints().size() - 2);
				Range closestGap = new Range(-1, -1);
				boolean isTop = findClosestGap(stagesGaps.get(stageIdx),
						penultimate.getY(), closestGap);

				// Make the Fifo go through this gap
				int keptY = (isTop) ? closestGap.start + FIFO_SPACE
						: closestGap.end - FIFO_SPACE;
				keptY = (closestGap.start + FIFO_SPACE <= penultimate.getY() && closestGap.end
						- FIFO_SPACE >= penultimate.getY()) ? penultimate
						.getY() : keptY;
				if (keptY != penultimate.getY()) {
					ffc.getBendpoints().add(
							ffc.getBendpoints().size() - 1,
							Graphiti.getGaCreateService().createPoint(
									stageWidth.get(stageIdx).end
											+ BENDPOINT_SPACE, keptY));
				}
				ffc.getBendpoints().add(
						ffc.getBendpoints().size() - 1,
						Graphiti.getGaCreateService().createPoint(
								stageWidth.get(stageIdx).start
										- BENDPOINT_SPACE, keptY));

				// Update Gaps
				updateGaps(stagesGaps.get(stageIdx), keptY, closestGap);
			}
		}
	}

	protected void layoutFifos(Diagram diagram) {

		PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(diagram);

		// 0. Disconnect all delays from FIFOs
		List<Fifo> fifos = graph.getFifos();
		for (Fifo fifo : fifos) {
			if (fifo.getDelay() != null) {
				PictogramElement pe = getDelayPE(diagram, fifo);

				// Do the disconnection
				DeleteDelayFeature df = new DeleteDelayFeature(
						getFeatureProvider());
				IDeleteContext dc = new DeleteContext(pe);
				df.disconnectDelayFromFifo(dc);
			}
		}

		// 1. Layout forward FIFOs
		final List<Fifo> interStageFifos = new ArrayList<Fifo>();
		for (int i = 0; i < stagedActors.size() - 1; i++) {
			// Identify Fifo that do not stop at the next stage.
			// (ignoring feedback Fifos)
			List<AbstractActor> stageSrc = stagedActors.get(i);
			List<AbstractActor> stageDst = stagedActors.get(i + 1);

			// Get all outgoingFifos of current stage
			final List<Fifo> outgoingFifos = new ArrayList<Fifo>();
			stageSrc.forEach(a -> a.getDataOutputPorts().forEach(
					p -> outgoingFifos.add(p.getOutgoingFifo())));

			// Remove feedback fifos
			outgoingFifos.removeAll(feedbackFifos);

			// Add to interstage FIFOs
			interStageFifos.addAll(outgoingFifos);

			// Remove all FIFOs ending at next stage from the interstage Fifo
			// list
			interStageFifos.removeIf(f -> stageDst.contains(f.getTargetPort()
					.eContainer()));

			// Layout Fifos to reach the next stage without going over an actor
			layoutInterStageFifos(diagram, interStageFifos,
					stageWidth.get(i + 1), stagesGaps.get(i + 1));

		}

		// 2. Layout feedback FIFOs
		layoutFeedbackFifos(diagram, feedbackFifos, stagedActors, stagesGaps,
				stageWidth);

		// 3. Reconnect Delays to fifos
		for (Fifo fifo : fifos) {
			if (fifo.getDelay() != null) {
				FreeFormConnection ffc = getFreeFormConnectionOfEdge(diagram,
						fifo);
				// Find the position of the delay
				int posX = 0;
				int posY = 0;
				int srcStage = getActorStage((AbstractActor) fifo
						.getSourcePort().eContainer(), stagedActors);
				int dstStage = getActorStage((AbstractActor) fifo
						.getTargetPort().eContainer(), stagedActors);
				// If there is only one stage
				if (srcStage == dstStage) {
					posX = (stageWidth.get(dstStage).end + stageWidth
							.get(dstStage).start) / 2;
				} else {
					// If the fifo goes over more than one stage
					int midFifo = (srcStage + dstStage) / 2;
					posX = (stageWidth.get(midFifo).end + stageWidth
							.get(midFifo + 1).start) / 2;
				}
				posX -= AddDelayFeature.DELAY_SIZE / 2;
				// Find the Y position
				// find the two closest bendpoints
				List<Point> bPoints = new ArrayList<Point>(ffc.getBendpoints());
				// Cannot be the first and last bendpoints (unless there is not
				// 2 other bendpoints)
				if (bPoints.size() > 3) {
					bPoints.remove(bPoints.size() - 1);
					bPoints.remove(0);
				}
				final int pX = posX;
				bPoints.sort((p1, p2) -> {
					return Math.abs(p1.getX() - pX) - Math.abs(p2.getX() - pX);
				});

				posY = ((bPoints.get(0).getY() + bPoints.get(1).getY()) - AddDelayFeature.DELAY_SIZE) / 2;

				// Move the delay to this position
				ContainerShape pe = (ContainerShape) getDelayPE(diagram, fifo);
				pe.getGraphicsAlgorithm().setX(posX);
				pe.getGraphicsAlgorithm().setY(posY);

				// Do the connection

				ChopboxAnchor cba = (ChopboxAnchor) pe.getAnchors().get(0);
				AddDelayFeature ad = new AddDelayFeature(getFeatureProvider());
				// Add the delaySize / 2 to compute distance of the center of
				// the delay to the segments of the ffc
				ad.connectDelayToFifo(ffc, fifo, pe, cba, posX
						+ AddDelayFeature.DELAY_SIZE / 2, posY
						+ AddDelayFeature.DELAY_SIZE / 2);

			}
		}
	}

	protected void layoutInterStageFifos(Diagram diagram,
			List<Fifo> interStageFifos, Range width, List<Range> gaps) {

		// Find the FreeFormConnection of each FIFO
		// LinkedHashMap to preserve order
		Map<Fifo, FreeFormConnection> fifoFfcMap = new LinkedHashMap<Fifo, FreeFormConnection>();
		for (Fifo fifo : interStageFifos) {
			// Get freeform connection
			FreeFormConnection ffc = getFreeFormConnectionOfEdge(diagram, fifo);
			fifoFfcMap.put(fifo, ffc);
		}

		// Check if any FIFO has a Gap right in front of it
		List<Fifo> fifoToLayout = new ArrayList<Fifo>(fifoFfcMap.keySet());
		Iterator<Fifo> iter = fifoToLayout.iterator();
		while (iter.hasNext()) {
			Fifo fifo = iter.next();
			FreeFormConnection ffc = fifoFfcMap.get(fifo);
			Point penultimate = ffc.getBendpoints().get(
					ffc.getBendpoints().size() - 2);

			// Check Gaps one by one
			Range matchedRange = null;
			for (Range range : gaps) {
				if ((range.start + FIFO_SPACE) <= penultimate.getY()
						&& ((range.end - FIFO_SPACE) >= penultimate.getY() || range.end == -1)) {
					matchedRange = range;
					break;
				}
			}

			if (matchedRange != null) {
				// Create bendpoint
				iter.remove();
				ffc.getBendpoints().add(
						ffc.getBendpoints().size() - 1,
						Graphiti.getGaCreateService()
								.createPoint(width.end + BENDPOINT_SPACE,
										penultimate.getY()));
				// Update ranges of gaps
				updateGaps(gaps, penultimate.getY(), matchedRange);
			}
		}

		// Layout remaining Fifo
		for (Fifo fifo : fifoToLayout) {
			FreeFormConnection ffc = fifoFfcMap.get(fifo);
			// Get last 2 bendpoints (Since all FIFOs where layouted when actors
			// were moved, all FIFO have at least 2 bendpoints.)
			Point last = ffc.getBendpoints()
					.get(ffc.getBendpoints().size() - 1);
			Point penultimate = ffc.getBendpoints().get(
					ffc.getBendpoints().size() - 2);

			// Find the optimal place of added bendpoints (not considering
			// actors)
			int optimX = width.start - BENDPOINT_SPACE;
			int optimY = Math
					.round(((float) (optimX - penultimate.getX()) / (float) (last
							.getX() - penultimate.getX()))
							* (float) (last.getY() - penultimate.getY()))
					+ penultimate.getY();

			// Find the closest gap
			Range closestGap = new Range(-1, -1);
			boolean isTop = findClosestGap(gaps, optimY, closestGap);

			// Make the Fifo go through this gap
			int keptY = (isTop) ? closestGap.start + FIFO_SPACE
					: closestGap.end - FIFO_SPACE;
			ffc.getBendpoints().add(ffc.getBendpoints().size() - 1,
					Graphiti.getGaCreateService().createPoint(optimX, keptY));
			ffc.getBendpoints().add(
					ffc.getBendpoints().size() - 1,
					Graphiti.getGaCreateService().createPoint(
							width.end + BENDPOINT_SPACE, keptY));

			// Update Gaps
			updateGaps(gaps, keptY, closestGap);
		}
	}

	protected void layoutParameters(Diagram diagram) {
		// Layout parameters in an inverted tree fashion (root at the top).
		// Dependencies coming from configuration actors do not count.
		PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(diagram);
		List<Parameter> params = new ArrayList<Parameter>(graph.getParameters());

		// 1. Sort parameters alphabetically
		params.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));

		// 2. Find the root(s)
		List<Parameter> roots = findRootParameters(graph, params);

		// 3. Find the "stages" of the tree
		// (i.e. parameters with equal distances to a their farthest root)
		List<List<Parameter>> stagedParameters = createParameterStages(params,
				roots);

		// 4. Stage by stage layout
		List<Parameter> paramVertOrder = stageByStageParameterLayout(diagram,
				stagedParameters);

		// 5. Layout Parameters dependencies
		layoutDependencies(diagram, stagedParameters, paramVertOrder);
	}

	/**
	 * @param diagram
	 * @param stagedActors
	 * @throws RuntimeException
	 */
	protected void stageByStageActorLayout(Diagram diagram,
			List<List<AbstractActor>> stagedActors) throws RuntimeException {
		// Init the stageGap and stageWidth attributes
		stageWidth = new ArrayList<Range>();
		stagesGaps = new ArrayList<List<Range>>();

		int currentX = X_INIT;
		for (List<AbstractActor> stage : stagedActors) {
			List<Range> stageGaps = new ArrayList<Range>();
			stagesGaps.add(stageGaps);
			int currentY = Y_INIT;
			int maxX = 0;
			for (AbstractActor actor : stage) {
				// Get the PE
				List<PictogramElement> pes = Graphiti.getLinkService()
						.getPictogramElements(diagram, actor);
				PictogramElement actorPE = null;
				for (PictogramElement pe : pes) {
					if (pe instanceof ContainerShape) {
						actorPE = pe;
						break;
					}
				}

				if (actorPE == null) {
					throw new RuntimeException("No PE was found for actor :"
							+ actor.getName());
				}

				// Get the Graphics algorithm
				GraphicsAlgorithm actorGA = actorPE.getGraphicsAlgorithm();

				// Move the actor
				MoveAbstractActorFeature moveFeature = new MoveAbstractActorFeature(
						getFeatureProvider());
				MoveShapeContext moveContext = new MoveShapeContext(
						(Shape) actorPE);
				moveContext.setX(currentX);
				moveContext.setY(currentY);
				// ILocation csLoc = Graphiti.getPeLayoutService()
				// .getLocationRelativeToDiagram((Shape) actorPE);
				// moveContext.setLocation(csLoc.getX(), csLoc.getY());
				moveFeature.moveShape(moveContext);

				stageGaps.add(new Range(currentY + actorGA.getHeight(),
						currentY + actorGA.getHeight() + Y_SPACE));
				currentY += actorGA.getHeight() + Y_SPACE;
				maxX = (maxX > actorGA.getWidth()) ? maxX : actorGA.getWidth();

			}
			// last range of gap has no end
			stageGaps.get(stageGaps.size() - 1).end = -1;
			stageWidth.add(new Range(currentX, currentX + maxX));
			currentX += maxX + X_SPACE;

		}
	}

	protected List<List<AbstractActor>> stageByStageActorSort(PiGraph graph,
			List<Fifo> feedbackFifos) {
		// 1. Sort actor in alphabetical order
		List<AbstractActor> actors = new ArrayList<AbstractActor>(
				graph.getVertices());
		actors.sort((a1, a2) -> a1.getName().compareTo(a2.getName()));

		// 2. Find source actors (actor without input non feedback FIFOs)
		final List<AbstractActor> srcActors = findSrcActors(feedbackFifos,
				actors);

		// 3. BFS-style stage by stage construction
		List<List<AbstractActor>> stages = createActorStages(feedbackFifos,
				actors, srcActors);

		return stages;
	}

	protected List<Parameter> stageByStageParameterLayout(Diagram diagram,
			List<List<Parameter>> stagedParameters) {
		paramXPositions = new HashMap<Parameter, Integer>();

		// 1. Sort the parameters so that each parameter has its own vertical
		// line
		List<Parameter> paramVertOrder = getParameterVerticalOrder(stagedParameters);

		// 2. Move the parameters
		// Vert position of first param is aligned with the first stage of
		// actors.
		int xPos = (stagedActors.size() > 1 && stagedActors.get(0).get(0) instanceof DataInputInterface) ? stageWidth
				.get(1).start : X_INIT;

		yParamInitPos = Y_INIT - paramVertOrder.size() * DEPENDENCY_SPACE;
		for (Parameter param : paramVertOrder) {
			// Get the PE
			List<PictogramElement> pes = Graphiti.getLinkService()
					.getPictogramElements(diagram, param);
			PictogramElement paramPE = null;
			for (PictogramElement pe : pes) {
				if (pe instanceof ContainerShape) {
					paramPE = pe;
					break;
				}
			}

			if (paramPE == null) {
				throw new RuntimeException("No PE was found for parameter :"
						+ param.getName());
			}

			// Get the Graphics algorithm
			GraphicsAlgorithm paramGA = paramPE.getGraphicsAlgorithm();

			// Param stage
			int paramStage = -1;
			for (int stage = 0; stage < stagedParameters.size(); stage++) {
				if (stagedParameters.get(stage).contains(param)) {
					paramStage = stage;
				}
			}

			// Move the parameter (Do not use the MoveShapeFeature as it would
			// also mess up the dependencies
			paramGA.setX(xPos);
			paramGA.setY(yParamInitPos
					- (stagedParameters.size() - 1 - paramStage)
					* Y_SPACE_PARAM - AddParameterFeature.PARAM_HEIGHT);
			paramXPositions.put(param, xPos + paramGA.getWidth() / 2);
			xPos += paramGA.getWidth() + X_SPACE_PARAM;
		}

		return paramVertOrder;
	}

	/**
	 * Update a list of {@link Range} after a {@link Fifo} passing through this
	 * gap at coordinate keptY was added.
	 * 
	 * @param gaps
	 *            the {@link List} of {@link Range} to update.
	 * @param keptY
	 *            the Y coordinate of the {@link Fifo}
	 * @param matchedRange
	 *            the {@link Range} within which the {@link Fifo} is going
	 *            through.
	 */
	protected void updateGaps(List<Range> gaps, int keptY, Range matchedRange) {
		gaps.remove(matchedRange);
		Range before = new Range(matchedRange.start, keptY - FIFO_SPACE);
		if ((before.end - before.start) >= FIFO_SPACE * 2) {
			gaps.add(before);
		}

		Range after = new Range(keptY + FIFO_SPACE, matchedRange.end);
		if (((after.end - after.start) >= FIFO_SPACE * 2) || after.end == -1) {
			gaps.add(after);
		}
	}
}
