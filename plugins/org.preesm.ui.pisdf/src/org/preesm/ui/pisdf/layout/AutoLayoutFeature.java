/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2017 - 2018)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2015)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.ui.pisdf.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.LayoutContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.PlatformUI;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.util.DependencyCycleDetector;
import org.preesm.model.pisdf.util.FifoCycleDetector;
import org.preesm.ui.pisdf.diagram.PiMMDiagramEditor;
import org.preesm.ui.pisdf.features.AddDelayFeature;
import org.preesm.ui.pisdf.features.AddParameterFeature;
import org.preesm.ui.pisdf.features.DeleteDelayFeature;
import org.preesm.ui.pisdf.features.LayoutActorFeature;
import org.preesm.ui.pisdf.features.LayoutPortFeature;
import org.preesm.ui.pisdf.features.MoveAbstractActorFeature;
import org.preesm.ui.pisdf.util.DiagramPiGraphLinkHelper;

/**
 * {@link AbstractCustomFeature} automating the layout process for PiMM graphs.
 *
 *
 * @author kdesnos
 */
public class AutoLayoutFeature extends AbstractCustomFeature {

  /**
   * The Class Range.
   */
  class Range {

    /** The end. */
    int end;

    /** The start. */
    int start;

    /**
     * Instantiates a new range.
     *
     * @param start
     *          the start
     * @param end
     *          the end
     */
    public Range(final int start, final int end) {
      this.start = start;
      this.end = end;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
      if (obj instanceof Range) {
        return ((((Range) obj).start == this.start) && (((Range) obj).end == this.end));
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      // see
      // https://stackoverflow.com/questions/11742593/what-is-the-hashcode-for-a-custom-class-having-just-two-int-properties/11742634#11742634
      int hash = 17;
      hash = (hash * 31) + this.start;
      hash = (hash * 31) + this.end;
      return hash;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "[" + this.start + "," + this.end + "]";
    }
  }

  /** The Constant BENDPOINT_SPACE. */
  private static final int BENDPOINT_SPACE = MoveAbstractActorFeature.BENDPOINT_SPACE;

  /** The Constant DEPENDENCY_SPACE. */
  private static final int DEPENDENCY_SPACE = 8;

  /** The Constant FIFO_SPACE. */
  private static final int FIFO_SPACE = 7;

  /** The Constant X_INIT. */
  protected static final int X_INIT = 50;

  /** The Constant X_SPACE. */
  private static final int X_SPACE = 100;

  /** The Constant X_SPACE_PARAM. */
  private static final int X_SPACE_PARAM = AutoLayoutFeature.X_SPACE / 2;

  /** The Constant Y_INIT. */
  protected static final int Y_INIT = 250;

  /** The Constant Y_SPACE. */
  private static final int Y_SPACE = 50;

  /** The Constant Y_SPACE_PARAM. */
  private static final int Y_SPACE_PARAM = 60;

  /** The Constant HINT. */
  public static final String HINT = "layout";

  /**
   * Feedback {@link Fifo} identified in a {@link PiGraph}. {@see AutoLayoutFeature#findFeedbackFifos(PiGraph)}.
   */
  private List<Fifo> feedbackFifos;

  /** The has done change. */
  boolean hasDoneChange = false;

  /** The param X positions. */
  private Map<Parameter, Integer> paramXPositions;

  /**
   * Actors sorted stage by stage. {@link AutoLayoutFeature#stageByStageSort(PiGraph, List)}.
   */
  private List<List<AbstractActor>> stagedActors;

  /**
   * For each stage, list of the Y coordinates starting a Y_SPACE gap between two actors.
   */
  private List<List<Range>> stagesGaps;

  /**
   * Width of the stages once the have been layouted.
   */
  private List<Range> stageWidth;
  /**
   * Initial vertical position for parameters. (computed in stageByStageParameterLayout).
   */
  private int         yParamInitPos;

  /**
   * Instantiates a new auto layout feature.
   *
   * @param fp
   *          the fp
   */
  public AutoLayoutFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.
   * ICustomContext)
   */
  @Override
  public boolean canExecute(final ICustomContext context) {
    return true;
  }

  /**
   * Create {@link List} of {@link List} of {@link AbstractActor} where each innermost {@link List} is called a stage.
   * An {@link AbstractActor} is put in a stage only if all its predecessors (not considering feedbackFifos) are already
   * added to previous stages.
   *
   * @param feedbackFifos
   *          {@link List} of {@link Fifo} that are ignored when scanning the predecessors of an actor.
   * @param actors
   *          {@link AbstractActor} to sort.
   * @param srcActors
   *          First stage of {@link Fifo}, given by the {@link #findSrcActors(List, List)}.
   * @return the stage by stage list of actors.
   */
  protected List<List<AbstractActor>> createActorStages(final List<Fifo> feedbackFifos,
      final List<AbstractActor> actors, final List<AbstractActor> srcActors) {
    final List<List<AbstractActor>> stages = new ArrayList<>();

    // init first stage with src actors
    final List<AbstractActor> processedActors = new ArrayList<>();
    processedActors.addAll(srcActors);
    Set<AbstractActor> nextStage = new LinkedHashSet<>();
    List<AbstractActor> currentStage = srcActors;
    final List<AbstractActor> dataOutputInterfaces = new ArrayList<>();

    // Keep DataInputInterfaces for the first stage
    final Iterator<AbstractActor> iter = srcActors.iterator();
    while (iter.hasNext()) {
      final AbstractActor actor = iter.next();
      if (!(actor instanceof DataInputInterface)) {
        iter.remove();
        processedActors.remove(actor);
        nextStage.add(actor);
      }
    }

    // Check if there is any Interface in the first stage
    if (currentStage.isEmpty()) {
      currentStage = new ArrayList<>(nextStage);
      processedActors.addAll(currentStage);
      nextStage = new LinkedHashSet<>();
    }

    // Register first stage
    stages.add(currentStage);
    boolean test;
    // System.err.println("createActorStage1");
    do {
      iterate(feedbackFifos, processedActors, nextStage, currentStage, dataOutputInterfaces);
      // System.err.println(nextStage.stream().map(AbstractActor::getName).collect(Collectors.joining("; ")));
      // Prepare next iteration
      currentStage = new ArrayList<>(nextStage);
      stages.add(currentStage);
      processedActors.addAll(currentStage);
      nextStage = new LinkedHashSet<>();

      test = processedActors.size() < actors.size() && !currentStage.isEmpty();
    } while (test);
    // System.err.println("createActorStage2");

    // If the last stage is empty (if there were only dataOutputInterface)
    // remove it
    if (stages.get(stages.size() - 1).isEmpty()) {
      stages.remove(stages.size() - 1);
    }

    if (!dataOutputInterfaces.isEmpty()) {
      stages.add(dataOutputInterfaces);
    }

    return stages;
  }

  private void iterate(final List<Fifo> feedbackFifos, final List<AbstractActor> processedActors,
      final Set<AbstractActor> nextStage, final List<AbstractActor> currentStage,
      final List<AbstractActor> dataOutputInterfaces) {
    // Find candidates for the next stage in successors of current one
    findCandidates(feedbackFifos, nextStage, currentStage);

    // Check if all predecessors of the candidates have already been
    // added in a previous stages
    check(feedbackFifos, processedActors, nextStage, dataOutputInterfaces);
  }

  private void findCandidates(final List<Fifo> feedbackFifos, final Set<AbstractActor> nextStage,
      final List<AbstractActor> currentStage) {
    for (final AbstractActor actor : currentStage) {
      if (actor instanceof DelayActor) {
        final Delay delay = ((DelayActor) actor).getLinkedDelay();
        final Fifo fifo = delay.getContainingFifo();
        if (!feedbackFifos.contains(fifo) && (fifo != null)) {
          final DataInputPort targetPort = fifo.getTargetPort();
          final AbstractActor targetActor = targetPort.getContainingActor();
          nextStage.add(targetActor);
        }
      }
      for (final DataOutputPort port : actor.getDataOutputPorts()) {
        final Fifo outgoingFifo = port.getOutgoingFifo();
        if (!feedbackFifos.contains(outgoingFifo) && (outgoingFifo != null)) {
          final DataInputPort targetPort = outgoingFifo.getTargetPort();
          final AbstractActor targetActor = targetPort.getContainingActor();
          nextStage.add(targetActor);
        }
      }

      // for (final DataOutputPort port : actor.getDataOutputPorts()) {
      // final Fifo outgoingFifo = port.getOutgoingFifo();
      // if (!feedbackFifos.contains(outgoingFifo) && (outgoingFifo != null)) {
      // final DataInputPort targetPort = outgoingFifo.getTargetPort();
      // final AbstractActor targetActor = targetPort.getContainingActor();
      // // We skip the delay actors for now
      // if (targetActor instanceof DelayActor) {
      // continue;
      // }
      // nextStage.add(targetActor);
      // }
      // }
    }
  }

  private void check(final List<Fifo> feedbackFifos, final List<AbstractActor> processedActors,
      final Set<AbstractActor> nextStage, final List<AbstractActor> dataOutputInterfaces) {
    Iterator<AbstractActor> iter;
    iter = nextStage.iterator();
    while (iter.hasNext()) {
      final AbstractActor actor = iter.next();
      boolean hasUnstagedPredecessor = false;
      for (final DataInputPort port : actor.getDataInputPorts()) {
        final Fifo incomingFifo = port.getIncomingFifo();
        final boolean isFeedbackFifo = feedbackFifos.contains(incomingFifo);
        boolean containedInProcessedActor = true;
        boolean predecessorIsDelayActor = false;
        if (incomingFifo != null) {
          final AbstractActor containingActor = incomingFifo.getSourcePort().getContainingActor();
          containedInProcessedActor = processedActors.contains(containingActor);
          predecessorIsDelayActor = containingActor instanceof DelayActor;
        }
        // If predecessor is a DelayActor, override the decision
        if (predecessorIsDelayActor) {
          continue;
        }
        hasUnstagedPredecessor |= !isFeedbackFifo && !containedInProcessedActor;

        // For delay with setter/getter, the delay Actor must always be in the previous stage
        if ((incomingFifo != null) && (incomingFifo.getDelay() != null)) {
          if (incomingFifo.getDelay().hasSetterActor()) {
            hasUnstagedPredecessor |= !processedActors.contains(incomingFifo.getDelay().getActor());
            hasUnstagedPredecessor |= !processedActors.contains(incomingFifo.getDelay().getSetterActor());
          } else if (incomingFifo.getDelay().hasGetterActor()) {
            hasUnstagedPredecessor |= !processedActors.contains(incomingFifo.getDelay().getActor());
            hasUnstagedPredecessor |= !processedActors.contains(incomingFifo.getDelay().getGetterActor());
          }
        }
      }
      if (hasUnstagedPredecessor) {
        iter.remove();
      } else if ((actor instanceof DataOutputInterface)) {
        dataOutputInterfaces.add(actor);
        processedActors.add(actor);
        iter.remove();
      }
    }
  }

  /**
   * Create {@link List} of {@link List} of {@link Parameter} where each innermost {@link List} is called a stage. An
   * {@link Parameter} is put in a stage only if all its predecessors are already added to previous stages.
   *
   * @param params
   *          the {@link List} of {@link Parameter} to organize into stages.
   * @param roots
   *          the roots {@link Parameter} (i.e. parameters without predecessors).
   * @return the created {@link List} of stages where each stage is a {@link List} of {@link Parameter}.
   */
  protected List<List<Parameter>> createParameterStages(final List<Parameter> params, final List<Parameter> roots) {
    // Initializations
    final List<List<Parameter>> stages = new ArrayList<>();
    final List<Parameter> processedParams = new ArrayList<>(roots);
    Set<Parameter> nextStage = new LinkedHashSet<>();
    stages.add(roots);
    List<Parameter> currentStage = roots;

    do {
      // Find candidates for the next stage in successors of current one
      for (final Parameter param : currentStage) {
        for (final Dependency dependency : param.getOutgoingDependencies()) {
          final ConfigInputPort getter = dependency.getGetter();
          final EObject eContainer = getter.eContainer();
          if (eContainer instanceof Parameter) {
            nextStage.add((Parameter) eContainer);
          }
        }
      }

      // Check if all predecessors of the candidates have already been
      // added in a previous stages
      for (final Iterator<Parameter> iter = nextStage.iterator(); iter.hasNext();) {
        final Parameter param = iter.next();

        boolean hasUnstagedPredecessor = false;
        for (final ConfigInputPort port : param.getConfigInputPorts()) {
          final Dependency incomingDependency = port.getIncomingDependency();
          hasUnstagedPredecessor |= (incomingDependency.getSetter() instanceof Parameter)
              && !processedParams.contains(incomingDependency.getSetter());
        }
        if (hasUnstagedPredecessor) {
          iter.remove();
        }
      }

      // Prepare next iteration
      currentStage = new ArrayList<>(nextStage);
      stages.add(currentStage);
      processedParams.addAll(currentStage);
      nextStage = new LinkedHashSet<>();
    } while (processedParams.size() < params.size());

    return stages;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
   */
  @Override
  public void execute(final ICustomContext context) {
    final Diagram diagram = getDiagram();

    // some unexplained behavior makes the auto layout feature crash when the selection is not empty.
    // exact cause is not uncovered yet ...
    emptyEditorSelcetion(diagram);

    // Check if there are parameterization cycles in the graph.
    // In such a case, do not layout !
    final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(diagram);
    final DependencyCycleDetector dcd = new DependencyCycleDetector();
    dcd.doSwitch(graph);

    if (dcd.cyclesDetected()) {
      final IStatus warning = new Status(IStatus.ERROR, "org.ietr.preesm.experiment", 1,
          "This graph contains cyclic parameterization dependencies.\n"
              + "Remove these cycles before layouting the graph.",
          null);
      ErrorDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Layout error", null,
          warning);
      return;

    }

    this.hasDoneChange = true;

    // Step 1 - Layout actor content (name, ports, ...)
    layoutActorContent(diagram);

    // Step 2 - Clear all bendpoints
    DiagramPiGraphLinkHelper.clearBendpoints(diagram);

    // System.err.println("Error in autolayout (before actors)");

    // Step 3 - Layout actors in precedence order
    // (ignoring cycles / delayed FIFOs in cycles)
    layoutActors(diagram);

    // Step 4 - Layout fifo connections
    layoutFifos(diagram);

    // Step 5 - Layout Parameters and dependencies
    layoutParameters(diagram);
  }

  private final void layoutActorContent(final Diagram diagram) {
    final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(diagram);

    final List<Actor> actors = graph.getActorsWithRefinement();
    for (final Actor a : actors) {
      final List<PictogramElement> actorPEs = Graphiti.getLinkService().getPictogramElements(diagram, a);
      for (final PictogramElement p : actorPEs) {
        if (p instanceof ContainerShape) {
          final LayoutContext layoutContext = new LayoutContext(p);
          final ILayoutFeature layoutFeature = new LayoutActorFeature(getFeatureProvider());
          layoutFeature.layout(layoutContext);
          break;
        }
      }
      final EList<Port> allPorts = a.getAllPorts();
      for (final Port p : allPorts) {
        final List<PictogramElement> pictogramElements = Graphiti.getLinkService().getPictogramElements(diagram, p);
        for (final PictogramElement pe : pictogramElements) {
          if (pe instanceof BoxRelativeAnchor) {
            final LayoutContext layoutContext = new LayoutContext(pe);
            final ILayoutFeature layoutPortFeature = new LayoutPortFeature(getFeatureProvider());
            layoutPortFeature.layout(layoutContext);
            break;
          }
        }
      }
    }
  }

  private void emptyEditorSelcetion(final Diagram diagram) {
    final PiMMDiagramEditor activeEditor = (PiMMDiagramEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
        .getActivePage().getActiveEditor();
    activeEditor.selectPictogramElements(new PictogramElement[] { diagram });
  }

  /**
   * Given a list of vertical gaps (i.e. a {@link List} of {@link Range}) and a y-coordinate, this method finds the
   * {@link Range} that is closest to the given coordinate.
   *
   * @param gaps
   *          the {@link List} of {@link Range}
   * @param optimY
   *          the searched y-coordinate
   * @param closestGap
   *          {@link Range} used as an output {@link Parameter}. Its attributes will be set to the start and end values
   *          of the closest gap found in the list.
   * @return Whether the given y Coordinate is closest to the top ( <code>true</code>) or bottom (<code>false</code>) of
   *         the found closest Gap.
   */
  protected boolean findClosestGap(final List<Range> gaps, final int optimY, final Range closestGap) {
    boolean isTop = false; // closest to the top or the bottom of the
    // range

    int distance = Integer.MAX_VALUE;
    for (final Range range : gaps) {
      final int startDist = Math.abs(optimY - range.start);
      final int endDist = Math.abs(optimY - range.end);

      final int minDist = (startDist < endDist) ? startDist : endDist;

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
   * This method identifies so-called feedback {@link Fifo} that, if removed, break all cyclic data-paths from a graph.
   *
   * @param graph
   *          the graph within which feedback {@link Fifo} are searched.
   * @return a {@link List} of {@link Fifo}
   */
  protected List<Fifo> findFeedbackFifos(final PiGraph graph) {
    final List<Fifo> feedbackEdges = new ArrayList<>();

    // Search for cycles in the graph
    boolean hasCycle = false;
    // fast search, find cycles one by one
    final FifoCycleDetector detector = new FifoCycleDetector(true);
    do {
      hasCycle = false;

      // Find as many cycles as possible
      detector.clear();
      detector.addIgnoredFifos(feedbackEdges);
      detector.doSwitch(graph);
      final List<List<AbstractActor>> cycles = detector.getCycles();

      // Find the "feedback" fifo in each cycle
      if (!cycles.isEmpty()) {
        hasCycle = true;
        // For each cycle find the feedback fifo(s).
        for (final List<AbstractActor> cycle : cycles) {
          feedbackEdges.addAll(FifoCycleDetector.findCycleFeedbackFifos(cycle));
        }
      }
    } while (hasCycle);

    return feedbackEdges;
  }

  /**
   * Find {@link Parameter} of a graph that do no depend on other {@link Parameter}. {@link Dependency} to Configuration
   * {@link AbstractActor} are not considered when searching for root {@link Parameter}.
   *
   * @param params
   *          the {@link List} of {@link Parameter} within which roots are searched.
   * @return the {@link List} of roots.
   */
  protected List<Parameter> findRootParameters(final List<Parameter> params) {
    final List<Parameter> roots = new ArrayList<>();

    for (final Parameter p : params) {
      boolean hasDependencies = false;
      for (final ConfigInputPort port : p.getConfigInputPorts()) {
        final Dependency incomingDependency = port.getIncomingDependency();
        final ISetter setter = incomingDependency.getSetter();
        hasDependencies |= setter instanceof Parameter;
      }

      if (!hasDependencies) {
        roots.add(p);
      }
    }
    return roots;
  }

  /**
   * Find {@link AbstractActor} without any predecessors. {@link Fifo} passed as parameters are ignored.
   *
   * @param feedbackFifos
   *          {@link List} of ignored {@link Fifo}.
   * @param actors
   *          {@link AbstractActor} containing source actors.
   * @return the list of {@link AbstractActor} that do not have any predecessors
   */
  protected List<AbstractActor> findSrcActors(final List<Fifo> feedbackFifos, final List<AbstractActor> actors) {
    final List<AbstractActor> srcActors = new ArrayList<>();
    for (final AbstractActor actor : actors) {
      boolean hasInputFifos = false;

      for (final DataInputPort port : actor.getDataInputPorts()) {
        final Fifo incomingFifo = port.getIncomingFifo();
        final boolean contains = feedbackFifos.contains(incomingFifo);
        final boolean fifoNotNull = incomingFifo != null;
        hasInputFifos |= !contains && fifoNotNull;
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
   *          The searched {@link AbstractActor}
   * @param stagedActors
   *          the stages
   * @return the index of the stage containing the actor.
   */
  protected int getActorStage(final AbstractActor actor, final List<List<AbstractActor>> stagedActors) {
    for (int i = 0; i < stagedActors.size(); i++) {
      if (stagedActors.get(i).contains(actor)) {
        return i;
      }
    }
    return -1;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
   */
  @Override
  public String getName() {
    return "Layout Diagram\tCtrl+Shift+F";
  }

  /**
   * Get the stage within which a {@link Parameter} was placed within a {@link List} of stage. (cf.
   * {@link AutoLayoutFeature#createParameterStages(List, List)}).
   *
   * @param stagedParameters
   *          the list of stages, as create by the {@link AutoLayoutFeature#createParameterStages(List, List)}) method.
   * @param param
   *          the {@link Parameter} whose stage index is searched.
   * @return the index of the {@link Parameter} stage, or <code>-1</code> if the {@link Parameter} was not found in the
   *         given {@link List}.
   */
  protected int getParameterStage(final List<List<Parameter>> stagedParameters, final Parameter param) {
    int setterStage = -1;
    for (final List<Parameter> stage : stagedParameters) {
      if (stage.contains(param)) {
        setterStage = stagedParameters.indexOf(stage);
      }
    }
    return setterStage;
  }

  /**
   * Sort the {@link Parameter} in the vertical order in which they will be layouted. Each {@link Parameter} will have
   * its own vertical column during the layout process (but will share a stage with other parameters). This method makes
   * sure that the vertical order puts as close as possible to each other parameters with dependencies.
   *
   * @param stagedParameters
   *          the {@link List} of stage produced by {@link #createParameterStages(List, List)}.
   * @return the {@link List} of {@link Parameter} sorted in their vertical order.
   */
  protected List<Parameter> getParameterVerticalOrder(final List<List<Parameter>> stagedParameters) {
    // Initialize the list with last stage
    final List<Parameter> paramVertOrder = new LinkedList<>(stagedParameters.get(stagedParameters.size() - 1));
    for (int stageIdx = stagedParameters.size() - 2; stageIdx >= 0; stageIdx--) {
      for (final Parameter param : stagedParameters.get(stageIdx)) {
        reorderParameter(paramVertOrder, param);
      }
    }
    return paramVertOrder;
  }

  private void reorderParameter(final List<Parameter> paramVertOrder, final Parameter param) {
    // Find index of successors in paramVertOrder
    int lastIdx = -1;
    int firstIdx = -1;
    for (final Dependency dependency : param.getOutgoingDependencies()) {
      final Object getter = dependency.getGetter().eContainer();
      if (getter instanceof Parameter) {
        final int paramOrder = paramVertOrder.indexOf(getter);
        firstIdx = ((firstIdx == -1) || (paramOrder < firstIdx)) ? paramOrder : firstIdx;
        lastIdx = (paramOrder > lastIdx) ? paramOrder : lastIdx;
      }
    }

    // Insert in the middle of param indexes
    lastIdx = (lastIdx == -1) ? 0 : lastIdx;
    firstIdx = (firstIdx == -1) ? 0 : firstIdx;
    paramVertOrder.add((lastIdx + firstIdx + 1) / 2, param);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#hasDoneChanges()
   */
  @Override
  public boolean hasDoneChanges() {
    return this.hasDoneChange;
  }

  /**
   * Layout the {@link AbstractActor} of a {@link Diagram}.
   *
   * @param diagram
   *          the {@link Diagram} whose {@link AbstractActor} are layouted.
   */
  protected void layoutActors(final Diagram diagram) {
    final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(diagram);

    if (!graph.getActors().isEmpty()) {
      this.feedbackFifos = findFeedbackFifos(graph);

      // 2. Sort stage by stage (ignoring feedback FIFO)
      this.stagedActors = stageByStageActorSort(graph, this.feedbackFifos);

      // 3. Layout actors according to the topological order
      stageByStageActorLayout(diagram, this.stagedActors);
    } else {
      this.stagedActors = Collections.emptyList();
      this.feedbackFifos = Collections.emptyList();
      this.stagesGaps = Collections.emptyList();
    }
  }

  /**
   * Layout the {@link Dependency} of a {@link Diagram}.
   *
   * @param diagram
   *          the {@link Diagram} whose {@link AbstractActor} are layouted.
   * @param stagedParameters
   *          stage of {@link Parameter}, as created by {@link #createParameterStages(List, List)}.
   * @param paramVertOrder
   *          {@link List} of {@link Parameter} in their vertical order, as sorted by
   *          {@link #getParameterVerticalOrder(List)}.
   */
  protected void layoutDependencies(final Diagram diagram, final List<List<Parameter>> stagedParameters,
      final List<Parameter> paramVertOrder) {

    // Variable used for the straight horizontal dependencies used to
    // distributes values to actors
    int currentY = this.yParamInitPos + AutoLayoutFeature.DEPENDENCY_SPACE;
    int currentX = 0;
    boolean currentYUsed = false;
    final List<Dependency> processedDependencies = new ArrayList<>();

    // Process dependencies one by one, scanning the parameters
    for (final Parameter param : paramVertOrder) {

      if (currentYUsed) {
        currentY += AutoLayoutFeature.DEPENDENCY_SPACE;
        currentX += AutoLayoutFeature.DEPENDENCY_SPACE / 2;
        currentYUsed = false;
      }

      for (final Dependency dependency : param.getOutgoingDependencies()) {
        processedDependencies.add(dependency);
        currentYUsed = processDependency(diagram, stagedParameters, currentY, currentX, currentYUsed, param,
            dependency);
      }
    }
    // Check if dependencies were not layouted
    final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(diagram);
    final Set<Dependency> allDependencies = new LinkedHashSet<>(graph.getDependencies());
    allDependencies.removeAll(processedDependencies);

    // Each remaining dependency is a configuration link
    currentX = 0;
    for (final Dependency dependency : allDependencies) {
      currentY += AutoLayoutFeature.DEPENDENCY_SPACE;
      currentX += AutoLayoutFeature.DEPENDENCY_SPACE / 2;

      // get the FFC
      final FreeFormConnection ffc = DiagramPiGraphLinkHelper.getFreeFormConnectionOfEdge(diagram, dependency);

      // Get the first bendpoint and move it
      final Point firstBPoint = ffc.getBendpoints().get(0);
      firstBPoint.setX(firstBPoint.getX() + currentX);

      // Add a bPoint on top of it in the horizontal param space
      ffc.getBendpoints().add(Graphiti.getCreateService().createPoint(firstBPoint.getX(), currentY));
      // Get the target parameter
      final Parameter param = (Parameter) dependency.getGetter().eContainer();
      final int paramXPosition = this.paramXPositions.get(param);
      final int paramStage = getParameterStage(stagedParameters, param);

      // Add last 2 bendpoints
      ffc.getBendpoints()
          .add(Graphiti.getCreateService().createPoint(paramXPosition - AutoLayoutFeature.X_SPACE_PARAM, currentY));
      ffc.getBendpoints()
          .add(Graphiti.getCreateService().createPoint(paramXPosition - AutoLayoutFeature.X_SPACE_PARAM,
              this.yParamInitPos - ((stagedParameters.size() - 1 - paramStage) * AutoLayoutFeature.Y_SPACE_PARAM)
                  - (AddParameterFeature.PARAM_HEIGHT / 2)));
    }
  }

  private boolean processDependency(final Diagram diagram, final List<List<Parameter>> stagedParameters,
      final int currentY, final int currentX, final boolean currentYUsed, final Parameter param,
      final Dependency dependency) {
    // Get the polyline
    final FreeFormConnection ffc = DiagramPiGraphLinkHelper.getFreeFormConnectionOfEdge(diagram, dependency);

    // Get the type of the getter
    final EObject getter = dependency.getGetter().eContainer();
    final boolean newYUsed;
    if (getter instanceof Parameter) {
      newYUsed = currentYUsed;
      layoutDependencyToParamter(stagedParameters, param, ffc, getter);
    } else {
      // Add a first point below the parameter
      newYUsed = true;
      final int xPosition = this.paramXPositions.get(param);
      final Point bPoint = Graphiti.getGaCreateService().createPoint(xPosition, currentY);
      ffc.getBendpoints().add(0, bPoint);

      if ((getter instanceof DataInputInterface) || (getter instanceof DataOutputInterface)) {
        // fix strange behavior with FFC for interfaces ...
        ffc.getBendpoints().clear();
        ffc.getBendpoints().add(0, bPoint);
        layoutDependencyToInterface(diagram, currentY, currentX, ffc, getter);
      } else if (getter instanceof AbstractActor) {
        layoutDependencyToActor(currentY, currentX, ffc);
      } else if (getter instanceof Delay) {
        layoutDependencyToDelay(diagram, currentY, currentX, ffc, getter);
      } else {
        throw new UnsupportedOperationException();
      }
    }
    return newYUsed;
  }

  private void layoutDependencyToParamter(final List<List<Parameter>> stagedParameters, final Parameter param,
      final FreeFormConnection ffc, final EObject getter) {
    // Get stage
    final int getterStage = getParameterStage(stagedParameters, (Parameter) getter);
    // layout only if getter is more than one stage away from
    // setter
    final int xPosition = this.paramXPositions.get(param);
    final int yPosition = this.yParamInitPos
        - ((stagedParameters.size() - 1 - (getterStage - 1)) * AutoLayoutFeature.Y_SPACE_PARAM);
    final Point bPoint = Graphiti.getGaCreateService().createPoint(xPosition, yPosition);
    ffc.getBendpoints().add(bPoint);
  }

  private void layoutDependencyToDelay(final Diagram diagram, final int currentY, final int currentX,
      final FreeFormConnection ffc, final EObject getter) {
    // Get the gap end of the delay
    // (or the gap just before if the delay is a feedback
    // delay
    // of an actor)
    final PictogramElement delayPE = DiagramPiGraphLinkHelper.getDelayPE(diagram, ((Delay) getter).getContainingFifo());
    final GraphicsAlgorithm delayGA = delayPE.getGraphicsAlgorithm();

    int gapEnd = -1;
    for (int i = 0; i < this.stageWidth.size(); i++) {
      final Range range = this.stageWidth.get(i);
      // If the delay is within this stage
      if ((range.start < delayGA.getX()) && (range.end > delayGA.getX())) {
        gapEnd = range.start;
      }

      // If the delay is between this stage and the
      // previous
      if ((i > 0) && (range.start > delayGA.getX()) && (gapEnd == -1)) {
        gapEnd = range.start;
      }
    }

    // Add a new bendpoint on top of the gap
    final int xPos = gapEnd - AutoLayoutFeature.BENDPOINT_SPACE - currentX;
    ffc.getBendpoints().add(Graphiti.getGaCreateService().createPoint(xPos, currentY));

    // Add a bendpoint next to the delay
    int yPos = delayGA.getY();
    yPos += ((delayGA.getX() < xPos) && ((delayGA.getX() + delayGA.getWidth()) > xPos)) ? -AutoLayoutFeature.FIFO_SPACE
        : 3 * AutoLayoutFeature.FIFO_SPACE;
    ffc.getBendpoints().add(Graphiti.getGaCreateService().createPoint(xPos, yPos));
  }

  private void layoutDependencyToActor(final int currentY, final int currentX, final FreeFormConnection ffc) {
    // Retrieve the last bendpoint of the ffc (added when
    // the
    // actor was moved.)
    final Point lastBp = ffc.getBendpoints().get(ffc.getBendpoints().size() - 1);
    // Move it
    lastBp.setX(lastBp.getX() - currentX);
    // Add a new bendpoint on top of it
    ffc.getBendpoints().add(ffc.getBendpoints().size() - 1,
        Graphiti.getGaCreateService().createPoint(lastBp.getX(), currentY));
  }

  private void layoutDependencyToInterface(final Diagram diagram, final int currentY, final int currentX,
      final FreeFormConnection ffc, final EObject getter) {
    // Get position of target
    final PictogramElement getterPE = DiagramPiGraphLinkHelper.getActorPE(diagram, (AbstractActor) getter);

    // Get the Graphics algorithm
    final GraphicsAlgorithm actorGA = getterPE.getGraphicsAlgorithm();

    // Check if actor is first of its stage
    final int stage = getActorStage((AbstractActor) getter, this.stagedActors);
    final int index = this.stagedActors.get(stage).indexOf(getter);

    if (index == 0) {
      // Add a new bendpoint on top of it
      ffc.getBendpoints().add(ffc.getBendpoints().size(),
          Graphiti.getGaCreateService().createPoint(actorGA.getX() + (actorGA.getWidth() / 2), currentY));
    } else {
      int xPos = actorGA.getX();
      xPos -= currentX;
      // Add a new bendpoint on top of it
      ffc.getBendpoints().add(ffc.getBendpoints().size(), Graphiti.getGaCreateService().createPoint(xPos, currentY));
      // Add a new bendpoint next to it
      ffc.getBendpoints().add(ffc.getBendpoints().size(),
          Graphiti.getGaCreateService().createPoint(xPos, actorGA.getY() - AutoLayoutFeature.BENDPOINT_SPACE));
    }
  }

  /**
   * Layout the feedback {@link Fifo} of a {@link Diagram} (cf. {@link #findFeedbackFifos(PiGraph)}).
   *
   * @param diagram
   *          {@link Diagram} whose feedback {@link Fifo} are layouted.
   * @param feedbackFifos
   *          the {@link List} of feedback {@link Fifo} (cf. {@link #findFeedbackFifos(PiGraph)}).
   * @param stagedActors
   *          the {@link AbstractActor} of the {@link Diagram} sorted stage-by-stage.
   * @param stagesGaps
   *          the vertical gaps between {@link AbstractActor} in each stage.
   * @param stageWidth
   *          the horizontal width of each stage of {@link AbstractActor}
   */
  protected void layoutFeedbackFifos(final Diagram diagram, final List<Fifo> feedbackFifos,
      final List<List<AbstractActor>> stagedActors, final List<List<Range>> stagesGaps, final List<Range> stageWidth) {
    // Sort FIFOs according to the number of stages through which they're
    // going
    final List<Fifo> sortedFifos = new ArrayList<>(feedbackFifos);
    sortedFifos.sort((f1, f2) -> {
      final int srcStage1 = getActorStage((AbstractActor) f1.getSourcePort().eContainer(), stagedActors);
      final int dstStage1 = getActorStage((AbstractActor) f1.getTargetPort().eContainer(), stagedActors);

      final int srcStage2 = getActorStage((AbstractActor) f2.getSourcePort().eContainer(), stagedActors);
      final int dstStage2 = getActorStage((AbstractActor) f2.getTargetPort().eContainer(), stagedActors);

      return Math.abs(srcStage1 - dstStage1) - Math.abs(srcStage2 - dstStage2);
    });

    // Add new gap on top of all stages
    for (final List<Range> gaps : stagesGaps) {
      gaps.add(new Range(0, AutoLayoutFeature.X_INIT));
    }

    // Layout feedback FIFOs one by one, from short to long distances
    for (final Fifo fifo : sortedFifos) {
      final FreeFormConnection ffc = DiagramPiGraphLinkHelper.getFreeFormConnectionOfEdge(diagram, fifo);

      final int srcStage = getActorStage((AbstractActor) fifo.getSourcePort().eContainer(), stagedActors);
      final int dstStage = getActorStage((AbstractActor) fifo.getTargetPort().eContainer(), stagedActors);

      // Do the layout for each stage
      for (int stageIdx = srcStage; stageIdx >= dstStage; stageIdx--) {
        // Find the closest gap to the feedback fifo
        final Point penultimate = ffc.getBendpoints().get(ffc.getBendpoints().size() - 2);
        final Range closestGap = new Range(-1, -1);

        final boolean isTop = findClosestGap(stagesGaps.get(stageIdx), penultimate.getY(), closestGap);

        // Make the Fifo go through this gap
        int keptY = (isTop) ? closestGap.start + AutoLayoutFeature.FIFO_SPACE
            : closestGap.end - AutoLayoutFeature.FIFO_SPACE;
        keptY = (((closestGap.start + AutoLayoutFeature.FIFO_SPACE) <= penultimate.getY())
            && ((closestGap.end == -1) || ((closestGap.end - AutoLayoutFeature.FIFO_SPACE) >= penultimate.getY())))
                ? penultimate.getY()
                : keptY;
        if (keptY != penultimate.getY()) {
          ffc.getBendpoints().add(ffc.getBendpoints().size() - 1, Graphiti.getGaCreateService()
              .createPoint(stageWidth.get(stageIdx).end + AutoLayoutFeature.BENDPOINT_SPACE, keptY));
        }
        ffc.getBendpoints().add(ffc.getBendpoints().size() - 1, Graphiti.getGaCreateService()
            .createPoint(stageWidth.get(stageIdx).start - AutoLayoutFeature.BENDPOINT_SPACE, keptY));

        // Update Gaps
        updateGaps(stagesGaps.get(stageIdx), keptY, closestGap);
      }
    }
  }

  /**
   * Layout the {@link Fifo} of a {@link Diagram}.
   *
   * @param diagram
   *          the {@link Diagram} whose {@link Fifo} are layouted.
   */
  protected void layoutFifos(final Diagram diagram) {

    final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(diagram);

    // 0. Disconnect all delays from FIFOs
    final List<Fifo> fifos = graph.getFifos();
    for (final Fifo fifo : fifos) {
      if (fifo.getDelay() != null) {
        final PictogramElement pe = DiagramPiGraphLinkHelper.getDelayPE(diagram, fifo);

        // Do the disconnection
        final DeleteDelayFeature df = new DeleteDelayFeature(getFeatureProvider());
        final IDeleteContext dc = new DeleteContext(pe);
        df.disconnectDelayFromFifo(dc);
      }
    }

    // 1. Layout forward FIFOs
    final List<Fifo> interStageFifos = new ArrayList<>();
    for (int i = 0; i < (this.stagedActors.size() - 1); i++) {
      // Identify Fifo that do not stop at the next stage.
      // (ignoring feedback Fifos)
      final List<AbstractActor> stageSrc = this.stagedActors.get(i);
      final List<AbstractActor> stageDst = this.stagedActors.get(i + 1);

      // Get all outgoingFifos of current stage
      final List<Fifo> outgoingFifos = new ArrayList<>();
      stageSrc.forEach(a -> a.getDataOutputPorts().forEach(p -> outgoingFifos.add(p.getOutgoingFifo())));

      // Ignoring fifos going to delay actor
      outgoingFifos.removeIf(f -> (f != null) && ((f.getTargetPort().getContainingActor() instanceof DelayActor)
          || (f.getSourcePort().getContainingActor() instanceof DelayActor)));

      // Remove feedback fifos
      outgoingFifos.removeAll(this.feedbackFifos);

      // Add to interstage FIFOs
      interStageFifos.addAll(outgoingFifos);

      // Remove all FIFOs ending at next stage from the interstage Fifo
      // list
      interStageFifos.removeIf(f -> (f == null) || stageDst.contains(f.getTargetPort().eContainer()));

      // Layout Fifos to reach the next stage without going over an actor
      layoutInterStageFifos(diagram, interStageFifos, this.stageWidth.get(i + 1), this.stagesGaps.get(i + 1));

    }

    // 2. Layout feedback FIFOs
    layoutFeedbackFifos(diagram, this.feedbackFifos, this.stagedActors, this.stagesGaps, this.stageWidth);

    // 3. Reconnect Delays to fifos
    for (final Fifo fifo : fifos) {
      if (fifo.getDelay() != null) {
        final FreeFormConnection ffc = DiagramPiGraphLinkHelper.getFreeFormConnectionOfEdge(diagram, fifo);
        // Find the position of the delay
        int posX = 0;
        final int srcStage = getActorStage((AbstractActor) fifo.getSourcePort().eContainer(), this.stagedActors);
        final int dstStage = getActorStage((AbstractActor) fifo.getTargetPort().eContainer(), this.stagedActors);
        // If there is only one stage
        if (srcStage == dstStage) {
          posX = (this.stageWidth.get(dstStage).end + this.stageWidth.get(dstStage).start) / 2;
        } else {
          // If the fifo goes over more than one stage
          final int midFifo = (srcStage + dstStage) / 2;
          posX = (this.stageWidth.get(midFifo).end + this.stageWidth.get(midFifo + 1).start) / 2;
        }
        posX -= AddDelayFeature.DELAY_SIZE / 2;
        // Find the Y position
        // find the two closest bendpoints
        final List<Point> bPoints = new ArrayList<>(ffc.getBendpoints());
        // Cannot be the first and last bendpoints (unless there is not
        // 2 other bendpoints)
        if (bPoints.size() > 3) {
          bPoints.remove(bPoints.size() - 1);
          bPoints.remove(0);
        }
        final int pX = posX;
        bPoints.sort((p1, p2) -> Math.abs(p1.getX() - pX) - Math.abs(p2.getX() - pX));

        final int posY = ((bPoints.get(0).getY() + bPoints.get(1).getY()) - AddDelayFeature.DELAY_SIZE) / 2;

        // Move the delay to this position
        final ContainerShape pe = DiagramPiGraphLinkHelper.getDelayPE(diagram, fifo);
        pe.getGraphicsAlgorithm().setX(posX);
        pe.getGraphicsAlgorithm().setY(posY);

        // Do the connection

        final ChopboxAnchor cba = (ChopboxAnchor) pe.getAnchors().get(0);
        final AddDelayFeature ad = new AddDelayFeature(getFeatureProvider());
        // Add the delaySize / 2 to compute distance of the center of
        // the delay to the segments of the ffc
        ad.connectDelayToFifo(ffc, fifo, pe, cba, posX + (AddDelayFeature.DELAY_SIZE / 2),
            posY + (AddDelayFeature.DELAY_SIZE / 2));

      }
    }
  }

  private void layoutFifoToDelay(final Diagram diagram, final int currentY, final int currentX,
      final FreeFormConnection ffc, final Delay delay) {
    // Get the gap end of the delay
    // (or the gap just before if the delay is a feedback
    // delay
    // of an actor)
    final PictogramElement delayPE = DiagramPiGraphLinkHelper.getDelayPE(diagram, delay.getContainingFifo());
    final GraphicsAlgorithm delayGA = delayPE.getGraphicsAlgorithm();

    int gapEnd = -1;
    for (int i = 0; i < this.stageWidth.size(); i++) {
      final Range range = this.stageWidth.get(i);
      // If the delay is within this stage
      if ((range.start < delayGA.getX()) && (range.end > delayGA.getX())) {
        gapEnd = range.start;
      }

      // If the delay is between this stage and the
      // previous
      if ((i > 0) && (range.start > delayGA.getX()) && (gapEnd == -1)) {
        gapEnd = range.start;
      }
    }

    // Add a new bendpoint on top of the gap
    final int xPos = gapEnd - AutoLayoutFeature.BENDPOINT_SPACE - currentX;
    ffc.getBendpoints().add(Graphiti.getGaCreateService().createPoint(xPos, currentY));

    // Add a bendpoint next to the delay
    int yPos = delayGA.getY();
    yPos += ((delayGA.getX() < xPos) && ((delayGA.getX() + delayGA.getWidth()) > xPos)) ? -AutoLayoutFeature.FIFO_SPACE
        : 3 * AutoLayoutFeature.FIFO_SPACE;
    ffc.getBendpoints().add(Graphiti.getGaCreateService().createPoint(xPos, yPos));
  }

  /**
   * Layout {@link Fifo} spanning over multiple stages of {@link AbstractActor}.
   *
   * @param diagram
   *          {@link Diagram} containing the {@link Fifo}
   * @param interStageFifos
   *          {@link List} of {@link Fifo} to layout.
   * @param width
   *          the width of the current stage as a {@link Range} of x-coordinate.
   * @param gaps
   *          Vertical gaps for this stage of {@link AbstractActor} as a {@link List} of {@link Range} of y-coordinates.
   */
  protected void layoutInterStageFifos(final Diagram diagram, final List<Fifo> interStageFifos, final Range width,
      final List<Range> gaps) {

    // Find the FreeFormConnection of each FIFO
    // LinkedHashMap to preserve order
    final Map<Fifo, FreeFormConnection> fifoFfcMap = new LinkedHashMap<>();
    for (final Fifo fifo : interStageFifos) {
      // Get freeform connection
      final FreeFormConnection ffc = DiagramPiGraphLinkHelper.getFreeFormConnectionOfEdge(diagram, fifo);
      fifoFfcMap.put(fifo, ffc);
    }

    // Check if any FIFO has a Gap right in front of it
    final List<Fifo> fifoToLayout = new ArrayList<>(fifoFfcMap.keySet());
    final Iterator<Fifo> iter = fifoToLayout.iterator();
    while (iter.hasNext()) {
      final Fifo fifo = iter.next();
      final FreeFormConnection ffc = fifoFfcMap.get(fifo);

      AbstractActor containingActor = fifo.getTargetPort().getContainingActor();

      final EList<Point> bendpoints = ffc.getBendpoints();
      if (containingActor instanceof DelayActor) {
        final int currentX = bendpoints.get(0).getX();
        final int currentY = bendpoints.get(0).getY();
        layoutFifoToDelay(diagram, currentY, currentX, ffc, ((DelayActor) containingActor).getLinkedDelay());
      }

      final int index = bendpoints.size() < 2 ? 0 : bendpoints.size() - 2;

      final Point penultimate = bendpoints.get(index);

      // Check Gaps one by one
      Range matchedRange = null;
      for (final Range range : gaps) {
        if (((range.start + AutoLayoutFeature.FIFO_SPACE) <= penultimate.getY())
            && (((range.end - AutoLayoutFeature.FIFO_SPACE) >= penultimate.getY()) || (range.end == -1))) {
          matchedRange = range;
          break;
        }
      }

      if (matchedRange != null) {
        // Create bendpoint
        iter.remove();
        bendpoints.add(bendpoints.size() - 1, Graphiti.getGaCreateService()
            .createPoint(width.end + AutoLayoutFeature.BENDPOINT_SPACE, penultimate.getY()));
        // Update ranges of gaps
        updateGaps(gaps, penultimate.getY(), matchedRange);
      }
    }

    // Layout remaining Fifo
    for (final Fifo fifo : fifoToLayout) {
      final FreeFormConnection ffc = fifoFfcMap.get(fifo);
      // Get last 2 bendpoints (Since all FIFOs where layouted when actors
      // were moved, all FIFO have at least 2 bendpoints.)
      final EList<Point> bendpoints = ffc.getBendpoints();
      final Point last = bendpoints.get(bendpoints.size() - 1);
      final int index = bendpoints.size() < 2 ? 0 : bendpoints.size() - 2;

      final Point penultimate = bendpoints.get(index);

      // Find the optimal place of added bendpoints (not considering
      // actors)
      final int optimX = width.start - AutoLayoutFeature.BENDPOINT_SPACE;
      final int optimY = Math.round(((float) (optimX - penultimate.getX()) / (float) (last.getX() - penultimate.getX()))
          * (last.getY() - penultimate.getY())) + penultimate.getY();

      // Find the closest gap
      final Range closestGap = new Range(-1, -1);
      final boolean isTop = findClosestGap(gaps, optimY, closestGap);

      // Make the Fifo go through this gap
      final int keptY = (isTop) ? closestGap.start + AutoLayoutFeature.FIFO_SPACE
          : closestGap.end - AutoLayoutFeature.FIFO_SPACE;
      bendpoints.add(bendpoints.size() - 1, Graphiti.getGaCreateService().createPoint(optimX, keptY));
      bendpoints.add(bendpoints.size() - 1,
          Graphiti.getGaCreateService().createPoint(width.end + AutoLayoutFeature.BENDPOINT_SPACE, keptY));

      // Update Gaps
      updateGaps(gaps, keptY, closestGap);
    }
  }

  /**
   * Layout the {@link Parameter} of a {@link Diagram}.
   *
   * @param diagram
   *          the {@link Diagram} whose {@link Parameter} are layouted.
   */
  protected void layoutParameters(final Diagram diagram) {
    // Layout parameters in an inverted tree fashion (root at the top).
    // Dependencies coming from configuration actors do not count.
    final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(diagram);
    final List<Parameter> params = new ArrayList<>(graph.getParameters());

    // 1. Sort parameters alphabetically
    params.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));

    // 2. Find the root(s)
    final List<Parameter> roots = findRootParameters(params);

    // 3. Find the "stages" of the tree
    // (i.e. parameters with equal distances to a their farthest root)
    final List<List<Parameter>> stagedParameters = createParameterStages(params, roots);

    // 4. Stage by stage layout
    final List<Parameter> paramVertOrder = stageByStageParameterLayout(diagram, stagedParameters);

    // 5. Layout Parameters dependencies
    layoutDependencies(diagram, stagedParameters, paramVertOrder);
  }

  /**
   * Layout the {@link AbstractActor} of a {@link Diagram} in the stage-by-stage fashion.
   *
   * @param diagram
   *          {@link Diagram} whose {@link AbstractActor} are layouted.
   * @param stagedActors
   *          {@link List} of stages, where each stage is a {@link List} of {@link AbstractActor}.
   * @throws RuntimeException
   *           the runtime exception
   */
  protected void stageByStageActorLayout(final Diagram diagram, final List<List<AbstractActor>> stagedActors) {
    // Init the stageGap and stageWidth attributes
    this.stageWidth = new ArrayList<>();
    this.stagesGaps = new ArrayList<>();

    int currentX = AutoLayoutFeature.X_INIT;
    for (final List<AbstractActor> stage : stagedActors) {
      final List<Range> stageGaps = new ArrayList<>();
      this.stagesGaps.add(stageGaps);
      int currentY = AutoLayoutFeature.Y_INIT;
      int maxX = 0;
      // First we need to sort the stage so that the delay actor on feedback loop are
      // always processed after the concerned actor
      // for (final AbstractActor actor : stage) {
      // // Check for delay actor belonging to a feedback loop
      // if (actor instanceof DelayActor) {
      // final Delay delay = ((DelayActor) actor).getLinkedDelay();
      // final Fifo fifo = delay.getContainingFifo();
      // // Check if the fifo is a feedback fifo
      // if (this.feedbackFifos.contains(fifo)) {
      // int indexDelay = stage.indexOf(actor);
      // // source or target does not matter, it is a feedback loop
      // int indexLoopedActor = stage.indexOf(fifo.getSourcePort().getContainingActor());
      // if (indexLoopedActor < 0) {
      // // try with target, delay actor and feedback actor should be on the same stage
      // indexLoopedActor = stage.indexOf(fifo.getTargetPort().getContainingActor());
      // if (indexLoopedActor < 0) {
      // throw new RuntimeException("Delay Actor and feedback actor should be on same layout stage !");
      // }
      // }
      // if (indexDelay < indexLoopedActor) {
      // Collections.swap(stage, indexDelay, indexLoopedActor);
      // }
      // }
      // }
      // }
      for (final AbstractActor actor : stage) {
        final PictogramElement actorPE = DiagramPiGraphLinkHelper.getActorPE(diagram, actor);

        // Get the Graphics algorithm
        final GraphicsAlgorithm actorGA = actorPE.getGraphicsAlgorithm();

        // Move the actor
        final MoveAbstractActorFeature moveFeature = new MoveAbstractActorFeature(getFeatureProvider());
        final MoveShapeContext moveContext = new MoveShapeContext((Shape) actorPE);
        moveContext.setX(currentX);
        moveContext.setY(currentY);
        moveFeature.moveShape(moveContext);

        stageGaps
            .add(new Range(currentY + actorGA.getHeight(), currentY + actorGA.getHeight() + AutoLayoutFeature.Y_SPACE));
        currentY += actorGA.getHeight() + AutoLayoutFeature.Y_SPACE;
        maxX = (maxX > actorGA.getWidth()) ? maxX : actorGA.getWidth();

      }
      // last range of gap has no end
      stageGaps.get(stageGaps.size() - 1).end = -1;
      this.stageWidth.add(new Range(currentX, currentX + maxX));
      currentX += maxX + AutoLayoutFeature.X_SPACE;

    }
  }

  /**
   * Create the stages of {@link AbstractActor}. An actor can be put in a stage if all its predecessors have been put in
   * previous stages.
   *
   * @param graph
   *          the {@link PiGraph} whose {@link AbstractActor} are sorted into stages.
   * @param feedbackFifos
   *          the {@link Fifo} that must be ignored when considering predecessors of an {@link AbstractActor}
   * @return the {@link List} of stages, where eac stage is a {@link List} of {@link AbstractActor}.
   */
  protected List<List<AbstractActor>> stageByStageActorSort(final PiGraph graph, final List<Fifo> feedbackFifos) {
    // 1. Sort actor in alphabetical order
    final List<AbstractActor> actors = new ArrayList<>(graph.getActors());

    // 2. Remove Delay Actors that are not connected to avoid weird delay placement
    // actors.removeIf(a -> (a instanceof DelayActor));

    actors.sort((a1, a2) -> a1.getName().compareTo(a2.getName()));

    // 3. Find source actors (actor without input non feedback FIFOs)
    final List<AbstractActor> srcActors = findSrcActors(feedbackFifos, actors);

    // 4. BFS-style stage by stage construction
    return createActorStages(feedbackFifos, actors, srcActors);
  }

  /**
   * Layout the stages of {@link Parameter}. Aparameter can be put in a stage if all its predecessors have been put in
   * previous stages.
   *
   * @param diagram
   *          the {@link PiGraph} whose {@link Parameter} are layouted.
   * @param stagedParameters
   *          Stages of {@link Parameter} produced by the {@link #createParameterStages(List, List)} method.
   * @return the {@link Parameter} in their {@link #getParameterVerticalOrder(List)}.
   */
  protected List<Parameter> stageByStageParameterLayout(final Diagram diagram,
      final List<List<Parameter>> stagedParameters) {
    this.paramXPositions = new LinkedHashMap<>();

    // 1. Sort the parameters so that each parameter has its own vertical
    // line
    final List<Parameter> paramVertOrder = getParameterVerticalOrder(stagedParameters);

    // 2. Move the parameters
    // Vert position of first param is aligned with the first stage of
    // actors.
    int xPos = ((this.stagedActors.size() > 1) && (this.stagedActors.get(0).get(0) instanceof DataInputInterface))
        ? this.stageWidth.get(1).start
        : AutoLayoutFeature.X_INIT;

    // On top of actors, with enough space to layout all dependencies
    // plus some space to leave some air
    this.yParamInitPos = AutoLayoutFeature.Y_INIT - (paramVertOrder.size() * AutoLayoutFeature.DEPENDENCY_SPACE)
        - AutoLayoutFeature.Y_SPACE;
    for (final Parameter param : paramVertOrder) {
      // Get the PE
      final List<PictogramElement> pes = Graphiti.getLinkService().getPictogramElements(diagram, param);
      PictogramElement paramPE = null;
      for (final PictogramElement pe : pes) {
        if (pe instanceof ContainerShape) {
          paramPE = pe;
          break;
        }
      }

      if (paramPE == null) {
        throw new NullPointerException("No PE was found for parameter :" + param.getName());
      }

      // Get the Graphics algorithm
      final GraphicsAlgorithm paramGA = paramPE.getGraphicsAlgorithm();

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
      paramGA.setY(this.yParamInitPos - ((stagedParameters.size() - 1 - paramStage) * AutoLayoutFeature.Y_SPACE_PARAM)
          - AddParameterFeature.PARAM_HEIGHT);
      this.paramXPositions.put(param, xPos + (paramGA.getWidth() / 2));
      xPos += paramGA.getWidth() + AutoLayoutFeature.X_SPACE_PARAM;
    }

    return paramVertOrder;
  }

  /**
   * Update a list of {@link Range} after a {@link Fifo} passing through this gap at coordinate keptY was added.
   *
   * @param gaps
   *          the {@link List} of {@link Range} to update.
   * @param keptY
   *          the Y coordinate of the {@link Fifo}
   * @param matchedRange
   *          the {@link Range} within which the {@link Fifo} is going through.
   */
  protected void updateGaps(final List<Range> gaps, final int keptY, final Range matchedRange) {
    gaps.remove(matchedRange);
    final Range before = new Range(matchedRange.start, keptY - AutoLayoutFeature.FIFO_SPACE);
    if ((before.end - before.start) >= (AutoLayoutFeature.FIFO_SPACE * 2)) {
      gaps.add(before);
    }

    final Range after = new Range(keptY + AutoLayoutFeature.FIFO_SPACE, matchedRange.end);
    if (((after.end - after.start) >= (AutoLayoutFeature.FIFO_SPACE * 2)) || (after.end == -1)) {
      gaps.add(after);
    }
  }
}
