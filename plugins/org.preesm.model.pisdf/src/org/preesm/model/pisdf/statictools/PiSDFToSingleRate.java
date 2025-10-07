/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019 - 2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019 - 2020)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
/**
 *
 */
package org.preesm.model.pisdf.statictools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import org.preesm.commons.IntegerName;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.NonExecutableActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.PortMemoryAnnotation;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.statictools.optims.BroadcastRoundBufferOptimization;
import org.preesm.model.pisdf.statictools.optims.ForkJoinOptimization;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 * @author farresti
 *
 */
public class PiSDFToSingleRate extends PiMMSwitch<Boolean> {

  /** The result. */
  // SRDAG graph created from the outer graph
  private final PiGraph result;

  /** Basic repetition vector of the graph */
  private final Map<AbstractVertex, Long> brv;

  /** Map from original PiMM vertices to generated DAG vertices */
  private final Map<String, List<AbstractVertex>> actor2SRActors = new LinkedHashMap<>();

  /** Map of all DataInputInterface to corresponding vertices */
  private final Map<DataInputPort, List<AbstractVertex>> inPort2SRActors = new LinkedHashMap<>();

  /** Map of all DataOutputInterface to corresponding vertices */
  private final Map<DataOutputPort, List<AbstractVertex>> outPort2SRActors = new LinkedHashMap<>();

  /** Current Single-Rate Graph name */
  private String graphName;

  /** Current graph prefix */
  private String graphPrefix;

  public static final String SRDAG_NAME_SUFFIX = "_srdag";

  /**
   * Current instance: stores the firing number of the current element. This is a global firing id: from 0 to full
   * repetition vector - 1 (not local default repetition vector).
   */
  private long firingInstance;

  /** Current actor name */
  private String currentActorName;

  /** Current FIFO */
  private Fifo currentFifo;

  private final PiGraph inputGraph;

  private final Map<Parameter, Parameter> param2param = new LinkedHashMap<>();

  private Boolean currentGraphIsCluster = false;

  private PiGraph currentResultSrDAG;

  private static List<String> flattenExclusionList;

  private static List<String> srdagExclusionList;

  /**
   * Instantiates a new abstract StaticPiMM2ASrPiMMVisitor.
   *
   */
  private PiSDFToSingleRate(final PiGraph inputGraph, final Map<AbstractVertex, Long> brv) {
    this.inputGraph = inputGraph;
    this.result = PiMMUserFactory.instance.createPiGraph();
    PreesmCopyTracker.trackCopy(inputGraph, this.result);
    this.result.setName(this.inputGraph.getName());
    this.result.setUrl(this.inputGraph.getUrl());
    this.brv = brv;
    this.graphName = "";
    this.graphPrefix = "";
    this.firingInstance = 0;

    // copy input graph period
    this.result.setExpression(inputGraph.getPeriod().evaluateAsLong());
  }

  public static final PiGraph computeWithExclusionLists(final PiGraph graph, final BRVMethod method,
      List<String> flattenExclusion, List<String> srdagExclusion) {
    flattenExclusionList = flattenExclusion;
    srdagExclusionList = srdagExclusion;
    return compute(graph, method);
  }

  /**
   * Precondition: All.
   *
   * @return the SDFGraph obtained by visiting graph
   */
  public static final PiGraph compute(final PiGraph graph, final BRVMethod method) {

    PreesmLogger.getLogger().log(Level.FINE, " >> Start srdag transfo");

    PreesmLogger.getLogger().log(Level.FINE, " >>   - check");
    // Check consistency of the graph (throw exception if recoverable or fatal error)
    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ANALYSIS,
        CheckerErrorLevel.NONE);
    pgcc.check(graph);
    // 0. we copy the graph since the transformation has side effects (especially on delay actors)
    final PiGraph graphCopy = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);
    // 1. First we resolve all parameters.
    // It must be done first because, when removing persistence, local parameters have to be known at upper level
    PreesmLogger.getLogger().log(Level.FINE, " >>   - resolve params");
    graphCopy.resolveAllParameters();
    // 2. Compute BRV following the chosen method
    PreesmLogger.getLogger().log(Level.FINE, " >>   - compute brv");
    final Map<AbstractVertex, Long> brvOriginal = PiBRV.compute(graphCopy, method);
    PreesmLogger.getLogger().log(Level.FINE, " >>   - print brv");
    PiBRV.printRV(brvOriginal);
    // then we remove all actors which will be not fired
    // we do it before the persistence transformation whose new delays may change the BRV
    PiMMHelper.removeNonExecutedActorsAndFifos(graphCopy, brvOriginal);
    // 3. We perform the delay transformation step that deals with persistence
    PreesmLogger.getLogger().log(Level.FINE, " >>   - remove persistence");
    PiMMHelper.removePersistence(graphCopy);
    // 3.1 recompute brv since new delays added from persistence are not known yet
    final Map<AbstractVertex, Long> brv = PiBRV.compute(graphCopy, method);
    // 3.2 adds default RV of 1 for InterfaceActor of all levels
    graphCopy.getAllActors().stream().filter(InterfaceActor.class::isInstance).forEach(x -> brv.put(x, 1L));
    // 4 Check periods with BRV
    PreesmLogger.getLogger().log(Level.FINE, " >>   - check periodicity");
    PiMMHelper.checkPeriodicity(graphCopy, brv);

    // 5. Convert to SR-DAG
    PreesmLogger.getLogger().log(Level.FINE, " >>   - apply single rate transfo");
    final PiSDFToSingleRate staticPiMM2ASrPiMMVisitor = new PiSDFToSingleRate(graphCopy, brv);
    staticPiMM2ASrPiMMVisitor.doSwitch(graphCopy);
    final PiGraph acyclicSRPiMM = staticPiMM2ASrPiMMVisitor.getResult();

    srCheck(graphCopy, acyclicSRPiMM);

    // 6- do some optimization on the graph
    PreesmLogger.getLogger().log(Level.FINE, " >> - fork join optim");
    final ForkJoinOptimization forkJoinOptimization = new ForkJoinOptimization();
    forkJoinOptimization.optimize(acyclicSRPiMM);

    PreesmLogger.getLogger().log(Level.FINE, " >> - broadcast/rbuffers optim");
    final BroadcastRoundBufferOptimization brRbOptimization = new BroadcastRoundBufferOptimization();
    brRbOptimization.optimize(acyclicSRPiMM);

    PreesmLogger.getLogger().log(Level.FINE, " >>   - check");
    // Check consistency of the graph (throw exception if recoverable or fatal error)
    final PiGraphConsistenceChecker pgccAfterwards = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ANALYSIS,
        CheckerErrorLevel.NONE);
    pgccAfterwards.check(acyclicSRPiMM);

    srCheck(graphCopy, acyclicSRPiMM);
    PreesmLogger.getLogger().log(Level.FINE, " >> End srdag transfo");

    PreesmLogger.getLogger().log(Level.INFO, () -> " SRDAG with " + acyclicSRPiMM.getAllActors().size()
        + " vertices and " + acyclicSRPiMM.getAllFifos().size() + " edges ");

    acyclicSRPiMM.setName(acyclicSRPiMM.getName() + SRDAG_NAME_SUFFIX);

    return acyclicSRPiMM;
  }

  /**
   *
   */
  private static final void srCheck(final PiGraph originalGraph, final PiGraph graph) {
    final List<AbstractActor> actors = graph.getActors();

    if (!originalGraph.getAllActors().isEmpty() && graph.getAllActors().isEmpty()) {
      throw new PreesmRuntimeException(true, "Flatten graph should not be empty if input graph is not empty", null);
    }

    for (final AbstractActor a : actors) {
      if (a instanceof PiGraph && !a.isCluster()) {
        throw new PreesmRuntimeException("Flatten graph should have no children graph: " + a.getName());
      }
      if (a.getContainingPiGraph() != graph) {
        throw new PreesmRuntimeException();
      }
    }
  }

  /**
   * creates a parameter and dependency to reflect the original actor's and adds them to result
   */
  private void instantiateParameters(final AbstractActor actor, final AbstractActor copyActor) {

    // // Copy parameters

    for (final ConfigInputPort port : copyActor.getConfigInputPorts()) {
      final Port lookupPort = actor.lookupPort(port.getName());
      if (!(lookupPort instanceof ConfigInputPort)) {
        throw new PreesmRuntimeException();
      }
      final Dependency incomingDependency = ((ConfigInputPort) lookupPort).getIncomingDependency();
      final ISetter setter = incomingDependency.getSetter();
      final Parameter parameter = param2param.get(setter);
      if (parameter == null) {
        throw new PreesmRuntimeException();
      }
      final Dependency dep = PiMMUserFactory.instance.createDependency(parameter, port);
      this.currentResultSrDAG.addParameter(parameter);
      this.currentResultSrDAG.addDependency(dep);
    }
  }

  /**
   * if actor is a PiGraph, adds a copy of it to result and the actor2SRActors map. Otherwise, does a
   * doSwitch(AbstractActor) on it.
   */
  @Override
  public Boolean caseAbstractActor(final AbstractActor actor) {
    if (actor instanceof final PiGraph piGraph) {
      // Here we handle the replacement of the interfaces by what should be
      // Copy the actor, should we use copyPiGraphWithHistory() instead ?
      final PiGraph copyGraph = PiMMUserFactory.instance.copyPiGraphWithHistory(piGraph);
      // Set the properties
      copyGraph.setName(this.currentActorName);

      // Add the actor to the graph
      this.currentResultSrDAG.addActor(copyGraph);

      if (copyGraph instanceof final PiGraph g && g.isCluster()) {
        // now we must connect the new cluster's inner config interfaces to their outer parameters in the new graph
        for (final ConfigInputPort cip : g.getConfigInputPorts()) {
          // cip.setIncomingDependency(param2param.get(cip.getIncomingDependency()));
          // param2param : fait un lien entre anciennes et nouvelles cii (entre autre)

          // d'abord on trouve le parameter correspondant dans le nouveau graphe
          // pour ça, on fait le lien entre les ports de config du nouveau et de l'ancien cluster par leur nom
          final Optional<ConfigInputInterface> oldcip = piGraph.getConfigInputInterfaces().stream()
              .filter(cii -> cii.getName().equals(cip.getName())).findFirst();
          if (oldcip.isEmpty()) {
            throw new PreesmRuntimeException("Couldn't find config input interface equivalent of parameter "
                + cip.getName() + " in cluster " + g.getName());
          }

          // ensuite on peut retrouver le paramètre setter de ce cii dans l'ancien graphe
          final var oldParam = oldcip.get().getGraphPort().getIncomingDependency().getSetter();

          // et enfin faire le lien avec le nouveau paramètre
          final var newParam = param2param.get(oldParam);

          final var dep = PiMMUserFactory.instance.createDependency(newParam, cip);

          this.currentResultSrDAG.addDependency(dep);
        }
      }

      // Add the actor to the FIFO source/sink sets
      this.actor2SRActors.get(this.graphPrefix + piGraph.getName()).add(copyGraph);
    } else {
      doSwitch(actor);
    }
    return true;
  }

  /**
   * adds a copy of actor to result and the actor2SRActors map
   */
  @Override
  public Boolean caseNonExecutableActor(final NonExecutableActor actor) {
    // Copy the BroadCast actor
    final NonExecutableActor copyActor = PiMMUserFactory.instance.copyWithHistory(actor);
    // Set the properties
    copyActor.setName(this.currentActorName);

    // Add the actor to the graph
    this.currentResultSrDAG.addActor(copyActor);

    // Add the actor to the FIFO source/sink sets
    this.actor2SRActors.get(this.graphPrefix + actor.getName()).add(copyActor);
    return true;
  }

  /**
   * adds to actor2SRActors a new map entry, with the actor's prefixed name as key and the dependency's list of targets
   * as value.
   */
  @Override
  public Boolean caseDelayActor(final DelayActor actor) {
    final AbstractActor targetActor = this.currentFifo.getTargetPort().getContainingActor();
    final AbstractActor sourceActor = this.currentFifo.getSourcePort().getContainingActor();
    final boolean isSetter = targetActor == actor;
    if (isSetter) {
      this.actor2SRActors.put(this.graphPrefix + actor.getName(),
          generateList(actor.getDataInputPort(), sourceActor, "_init_"));
    } else {
      this.actor2SRActors.put(this.graphPrefix + actor.getName(),
          generateList(actor.getDataOutputPort(), targetActor, "_end_"));
    }
    return true;
  }

  /**
   * Generate the list of actor that will replace a given DelayActor at some point in the SR transformation.
   *
   * <pre>
   *
   * 0. Original PiMM description:
   *
   *   setter * RV(setter) ---> DelayActor ---> getter * RV(getter)
   *
   * 1. Current SR-Transform:
   *
   *  InitActor * RV(setter) ---> actors setA
   *
   *  actors setB ---> EndActor * RV(getter)
   *
   * 2. Final SR-Transform:
   *
   *  setter * RV(setter) ---> actors setA
   *
   *  actors setB ---> getter * RV(getter)
   * </pre>
   *
   * @param port
   *          Data port of the DelayActor whose name is going to be replaced
   * @param actor
   *          Actor corresponding to either the Init or the End of the Delay linked to the DelayActor
   * @param suffixe
   *          Either "_init_" or "_end_" depending on whether we are dealing with Init / End of the Delay
   * @return List of actors corresponding to the setter / getter actors of the Delay.
   */
  private List<AbstractVertex> generateList(final DataPort port, final AbstractActor actor, final String suffixe) {
    final List<AbstractVertex> list = new ArrayList<>();
    // 0. Get RV value of the Actor
    final long actorRV = this.brv.get(actor);
    // 1. Find matched actors
    final IntegerName iN = new IntegerName(actorRV - 1);
    for (long i = 0; i < actorRV; ++i) {
      final String name = actor.getName() + suffixe + iN.toString(i);
      final AbstractActor foundActor = (AbstractActor) this.currentResultSrDAG.lookupVertex(name);
      if (foundActor == null) {
        throw new PreesmRuntimeException("Unable to find actor [" + name + "] in generated DAG.");
      }
      if (foundActor.getAllDataPorts().isEmpty()) {
        continue;
      }
      final DataPort dataPort = foundActor.getAllDataPorts().get(0);
      final Fifo fifo = dataPort.getFifo();
      // Retrieve the opposite port of the FIFO
      final DataPort oppositePort = dataPort instanceof DataOutputPort ? fifo.getTargetPort() : fifo.getSourcePort();
      final AbstractActor actorToAdd = oppositePort.getContainingActor();
      // Update the DataPort name to match the one of the corresponding port
      final String portName = oppositePort.getName();
      port.setName(portName);
      // Add the actor to the list
      list.add(actorToAdd);
      // Remove actor and FIFO from the result graph
      this.currentResultSrDAG.removeActor(foundActor);
      this.currentResultSrDAG.removeFifo(fifo);
    }
    return list;
  }

  /**
   * works on a copy of actor. If it is an Actor, it handles its refinement. In any case, it adds the actor to result
   * and the actor2SRActors map.
   */
  @Override
  public Boolean caseExecutableActor(final ExecutableActor actor) {
    // Copy the BroadCast actor
    final ExecutableActor copyActor = PiMMUserFactory.instance.copyWithHistory(actor);
    // Set the properties
    copyActor.setName(this.currentActorName);
    if (copyActor instanceof final Actor act) {
      act.setFiringInstance(firingInstance);
      if (firingInstance != 0) {
        // we remove the init prototype of the actor if it is not the first firing
        final Refinement rf = act.getRefinement();
        if (rf instanceof final CHeaderRefinement header) {
          final FunctionPrototype initPrototype = header.getInitPrototype();
          if (initPrototype != null) {
            header.setInitPrototype(null);
          }
        }
      }
    }

    // Add the actor to the graph
    this.currentResultSrDAG.addActor(copyActor);

    // Add the actor to the FIFO source/sink sets
    final String graphPrefix2 = this.graphPrefix;
    final String name = actor.getName();
    final String key = graphPrefix2 + name;
    final List<AbstractVertex> list = this.actor2SRActors.get(key);
    list.add(copyActor);

    // Set the properties
    instantiateParameters(actor, copyActor);
    return true;
  }

  @Override
  public Boolean caseInterfaceActor(final InterfaceActor actor) {
    if (actor.isAtTopLevel()) {
      return caseNonExecutableActor(actor);
    }
    return true;
  }

  @Override
  public Boolean caseFifo(final Fifo fifo) {
    // 0. Set current FIFO
    this.currentFifo = fifo;
    final DataOutputPort sourcePort = fifo.getSourcePort();
    final DataInputPort targetPort = fifo.getTargetPort();

    // 0. If fifo is zero, ignore it
    if (sourcePort.getExpression().evaluateAsLong() == 0 || targetPort.getExpression().evaluateAsLong() == 0) {
      return true;
    }

    // 1. Retrieve Source / Sink actors of the FIFO
    final AbstractActor sourceActor = sourcePort.getContainingActor();
    final AbstractActor sinkActor = targetPort.getContainingActor();

    if ((sinkActor instanceof EndActor && sourceActor instanceof DelayActor)
        || (sourceActor instanceof InitActor && sinkActor instanceof DelayActor)) {
      return true;
    }

    // 2. Populate the source set linked to this FIFO
    final List<
        AbstractVertex> sourceSet = getSourceSetForSRLinker(fifo, sourcePort, targetPort, sourceActor, sinkActor);
    if (sourceSet.isEmpty()) {
      fifo.setSourcePort(sourcePort);
      return true;
    }

    // 3. Populate the sink set linked to this FIFO
    final List<AbstractVertex> sinkSet = getSinkSetForSRLinker(fifo, sourcePort, targetPort, sourceActor, sinkActor,
        sourceSet);
    // If sinkSet / sourceSet is null, then we did not need a RoundBuffer / Broadcast and thus processing of connecting
    // this FIFO will be done later
    if (sinkSet.isEmpty()) {
      // In the case of Interfaces we might have disconnected the FIFO so let's reconnect it
      fifo.setSourcePort(sourcePort);
      fifo.setTargetPort(targetPort);
      return true;
    }

    // 4. Do the Single-Rate connections
    final PiMMSRVerticesLinker srVerticesLinker = new PiMMSRVerticesLinker(fifo, this.currentResultSrDAG,
        this.graphPrefix);
    srVerticesLinker.execute(this.brv, sourceSet, sinkSet);

    // In the case of Interfaces we might have disconnected the FIFO so let's reconnect it
    fifo.setSourcePort(sourcePort);
    fifo.setTargetPort(targetPort);
    return true;
  }

  private boolean PopulateAndConnectSr(Fifo fifo, DataOutputPort sourcePort, DataInputPort targetPort,
      AbstractActor sourceActor, AbstractActor sinkActor) {
    // 2. Populate the source set linked to this FIFO
    final List<
        AbstractVertex> sourceSet = getSourceSetForSRLinker(fifo, sourcePort, targetPort, sourceActor, sinkActor);
    if (sourceSet.isEmpty()) {
      fifo.setSourcePort(sourcePort);
      return true;
    }

    // 3. Populate the sink set linked to this FIFO
    final List<AbstractVertex> sinkSet = getSinkSetForSRLinker(fifo, sourcePort, targetPort, sourceActor, sinkActor,
        sourceSet);
    // If sinkSet / sourceSet is null, then we did not need a RoundBuffer / Broadcast and thus processing of connecting
    // this FIFO will be done later
    if (sinkSet.isEmpty()) {
      // In the case of Interfaces we might have disconnected the FIFO so let's reconnect it
      fifo.setSourcePort(sourcePort);
      fifo.setTargetPort(targetPort);
      return true;
    }

    // 4. Do the Single-Rate connections
    final PiMMSRVerticesLinker srVerticesLinker = new PiMMSRVerticesLinker(fifo, this.currentResultSrDAG,
        this.graphPrefix);
    srVerticesLinker.execute(this.brv, sourceSet, sinkSet);

    return false;
  }

  /**
   * if sourceActor is an InterfaceActor, calls handleDataInputInterface. If it is a PiGraph but not a cluster, does
   * stuff. Otherwise, adds sourceActor's BRV duplicate(s) to result.
   *
   * @param fifo
   *          the FIFO
   * @param sourcePort
   *          the sourcePort
   * @param targetPort
   *          the targetPort
   * @param sourceActor
   *          the source actor
   * @param sinkActor
   *          the sink actor
   * @return list of source actor to be linked, null else
   */
  private List<AbstractVertex> getSourceSetForSRLinker(final Fifo fifo, final DataOutputPort sourcePort,
      final DataInputPort targetPort, final AbstractActor sourceActor, final AbstractActor sinkActor) {
    // There are 3 mains special cases, source is an interface, source is a graph, source is a delay
    // Otherwise, we fall in "standard" case
    if (sourceActor instanceof final InterfaceActor interfaceActor) {
      return handleDataInputInterface(targetPort, interfaceActor, sinkActor);
    }
    if (sourceActor instanceof final PiGraph piGraph && !sourceActor.isCluster()) {
      // We should retrieve the correct source set
      if (!this.outPort2SRActors.containsKey(sourcePort)) {
        throw new PreesmRuntimeException("No replacement found for DataOutputPort [" + sourcePort.getName()
            + "] of hierarchical actor [" + piGraph.getName() + "].");
      }
      final List<AbstractVertex> sourceSet = this.outPort2SRActors.remove(sourcePort);
      // Now we change the "sourcePort" of the FIFO to match the one of the sourceSet
      final AbstractActor firstOfSet = (AbstractActor) sourceSet.get(0);
      final DataOutputPort foundPort = lookForSourcePort(piGraph, firstOfSet, sourcePort.getName());
      if (foundPort != null) {
        // check if foundPort is the sourcePort of a fifo whose targetPort is a dataOutputInterface
        // and also if we are in a cluster case (otherwise it's business as usual).
        // if so, we have to remove this interface and the connected fifo
        final var outgoingFifo = foundPort.getOutgoingFifo();
        if (outgoingFifo != null && outgoingFifo.getTarget() instanceof final DataOutputInterface doi) {
          this.result.removeFifo(doi.getDataInputPorts().getFirst().getFifo());
          this.result.removeActorAndDependencies(doi);
        }
        fifo.setSourcePort(foundPort);
      }
      return sourceSet;
    }
    final String keyActor = this.graphPrefix + sourceActor.getName();
    if (!this.actor2SRActors.containsKey(keyActor)) {
      populateSingleRatePiMMActor(sourceActor);
    }
    return this.actor2SRActors.get(keyActor);
  }

  /**
   * Search recursively in hierarchy for the matching source port in the real source actor connected to an interface
   *
   * @param graph
   *          Current graph containing the interface actor of name "sourceName"
   * @param source
   *          Actor for which we want to find the source port
   * @param sourceName
   *          Name of the current interface actor
   * @return The DataOutputPort found, null else
   */
  private static DataOutputPort lookForSourcePort(final PiGraph graph, final AbstractActor source,
      final String sourceName) {

    final List<InterfaceActor> outputInterfaceList = new ArrayList<>();
    outputInterfaceList.addAll(graph.getDataOutputInterfaces());
    outputInterfaceList.addAll(graph.getConfigOutputInterfaces());

    for (final InterfaceActor ia : outputInterfaceList) {
      if (ia.getName().equals(sourceName)) {
        final Fifo inFifo = ia.getDataPort().getFifo();
        if (inFifo != null) {
          final DataOutputPort sourcePort = inFifo.getSourcePort();
          final AbstractActor containingActor = sourcePort.getContainingActor();
          if (containingActor instanceof final PiGraph piGraph) {
            return lookForSourcePort(piGraph, source, sourcePort.getName());
          }
          return (DataOutputPort) source.lookupPort(sourcePort.getName());
        }
      }
    }

    return null;
  }

  /**
   * Handles a DataInputInterface replacement : adds the dataInputPort in inPort2SRActors as a key and fills the list it
   * has as value. Also adds necessary broadcast actors. If sinkActor is a PiGraph, it is removed from inPort2SRActors,
   * otherwise it populates it.
   *
   * @param targetPort
   *          the target port
   * @param sourceActor
   *          source actor
   * @param sinkActor
   *          sink actor
   * @return a list containing a BroadcastActor if needed, empty list else
   */
  private List<AbstractVertex> handleDataInputInterface(final DataInputPort targetPort,
      final InterfaceActor sourceActor, final AbstractActor sinkActor) {
    // 1 Get corresponding port in parent graph
    final DataInputPort correspondingPortInParent = (DataInputPort) sourceActor.getGraphPort();

    // 2.1 Create the entry in the map
    if (!inPort2SRActors.containsKey(correspondingPortInParent)) {
      inPort2SRActors.put(correspondingPortInParent, new ArrayList<>());
    }

    // 2.2 Now check if we need a BroadcastActor
    final long prod = correspondingPortInParent.getPortRateExpression().evaluateAsLong();
    final long cons = targetPort.getPortRateExpression().evaluateAsLong();
    final long sinkRV = this.brv.get(sinkActor);
    final boolean needBroadcastInterface = prod != (cons * sinkRV);
    final boolean needBroadcastDelay = sourceActor.getDataPort().getFifo().getDelay() != null;
    if (needBroadcastInterface || needBroadcastDelay) {
      final BroadcastActor interfaceBR = addBroadCastIn(sourceActor);
      inPort2SRActors.get(correspondingPortInParent).add(interfaceBR);
      return Collections.singletonList(interfaceBR);
    }
    final List<AbstractVertex> sinkSet;
    if (sinkActor instanceof PiGraph) {
      sinkSet = inPort2SRActors.remove(targetPort);
    } else {
      final String keyActor = this.graphPrefix + sinkActor.getName();
      if (!this.actor2SRActors.containsKey(keyActor)) {
        populateSingleRatePiMMActor(sinkActor);
      }
      sinkSet = this.actor2SRActors.get(keyActor);
    }

    final List<AbstractVertex> returnList = new LinkedList<>();
    if (sinkActor.getContainingPiGraph().isCluster()) {
      final DataInputInterface dipCopy = PiMMUserFactory.instance.copyWithHistory((DataInputInterface) sourceActor);
      currentResultSrDAG.addActor(dipCopy);
      // I don't know why, but adding dipCopy to the graph resets its expression to 0, so we hate to set it again
      dipCopy.getGraphPort()
          .setExpression(PiMMUserFactory.instance.copyWithHistory(sourceActor.getGraphPort().getExpression()));

      final String dipCopyName = dipCopy.getContainingPiGraph().getName() + "_" + dipCopy.getName();
      this.actor2SRActors.put(dipCopyName, new LinkedList<>());
      this.actor2SRActors.get(dipCopyName).add(dipCopy);
      returnList.add(dipCopy);
    }
    inPort2SRActors.get(correspondingPortInParent).addAll(sinkSet);
    return returnList;
  }

  /**
   * Add a BroadcastActor in the place of a DataInputInterface
   *
   * @param sourceActor
   *          the source interface
   * @return the BroadcastActor
   */
  private BroadcastActor addBroadCastIn(final AbstractActor sourceActor) {
    final BroadcastActor interfaceBR = PiMMUserFactory.instance.createBroadcastActor();
    interfaceBR.setName("BR_" + this.graphPrefix + "_" + sourceActor.getName());
    // Add the BroadcastActor to the graph
    this.currentResultSrDAG.addActor(interfaceBR);
    return interfaceBR;
  }

  /**
   *
   * @param fifo
   *          the FIFO
   * @param sourcePort
   *          the sourcePort
   * @param targetPort
   *          the targetPort
   * @param sourceActor
   *          the source actor
   * @param sinkActor
   *          the sink actor
   * @return list of sink actor to be linked, empty list else
   */
  private List<AbstractVertex> getSinkSetForSRLinker(final Fifo fifo, final DataOutputPort sourcePort,
      final DataInputPort targetPort, final AbstractActor sourceActor, final AbstractActor sinkActor,
      final List<AbstractVertex> sourceSet) {

    if (sinkActor instanceof final InterfaceActor interfaceActor) {
      return handleDataOutputInterface(sourcePort, sourceActor, interfaceActor, sourceSet);
    }
    if (sinkActor instanceof final PiGraph piGraph && !sinkActor.isCluster()) {
      // We should retrieve the correct source set
      if (!this.inPort2SRActors.containsKey(targetPort)) {
        throw new PreesmRuntimeException("No replacement found for DataInputPort [" + targetPort.getName()
            + "] of hierarchical actor [" + piGraph.getName() + "].");
      }
      final List<AbstractVertex> sinkSet = this.inPort2SRActors.remove(targetPort);
      // Now we change the "sinkPort" of the FIFO to match the one of the sinkSet if needed
      final AbstractActor firstOfSet = (AbstractActor) sinkSet.get(0);
      final DataInputPort foundPort = lookForTargetPort(piGraph, firstOfSet, targetPort.getName());
      if (foundPort != null) {
        fifo.setTargetPort(foundPort);
      }
      return sinkSet;
    }
    final String keyActor = this.graphPrefix + sinkActor.getName();
    if (!this.actor2SRActors.containsKey(keyActor)) {
      populateSingleRatePiMMActor(sinkActor);
    }
    return this.actor2SRActors.get(keyActor);
  }

  /**
   * Search recursively in hierarchy for the matching source port in the real source actor connected to an interface
   *
   * @param graph
   *          Current graph containing the interface actor of name "targetName"
   * @param target
   *          Actor for which we want to find the target port
   * @param targetName
   *          Name of the current interface actor
   * @return The DataInputPort found, null else
   */
  private static DataInputPort lookForTargetPort(final PiGraph graph, final AbstractActor target,
      final String targetName) {
    for (final DataInputInterface dii : graph.getDataInputInterfaces()) {
      if (dii.getName().equals(targetName)) {
        final Fifo outFifo = dii.getDataPort().getFifo();
        final DataInputPort targetPort = outFifo.getTargetPort();
        final AbstractActor containingActor = targetPort.getContainingActor();
        if (containingActor instanceof final PiGraph piGraph) {
          return lookForTargetPort(piGraph, target, targetPort.getName());
        }
        return (DataInputPort) target.lookupPort(targetPort.getName());
      }
    }
    return null;
  }

  /**
   * Handles a DataOutputInterface replacement.
   *
   * @param targetPort
   *          the target port
   * @param sourceActor
   *          source actor
   * @param sinkActor
   *          sink actor
   * @return a list containing a RoundBufferActor if needed, empty list else
   */
  private List<AbstractVertex> handleDataOutputInterface(final DataOutputPort sourcePort,
      final AbstractActor sourceActor, final InterfaceActor sinkActor, final List<AbstractVertex> sourceSet) {
    // 1 Get corresponding port in parent graph
    final DataOutputPort correspondingPort = (DataOutputPort) sinkActor.getGraphPort();
    // 2.1 Create the entry in the map if needed
    if (!this.outPort2SRActors.containsKey(correspondingPort)) {
      this.outPort2SRActors.put(correspondingPort, new ArrayList<>());
    }

    // 2.2 Now check if we need a RoundBufferActor
    final long cons = correspondingPort.getPortRateExpression().evaluateAsLong();
    final long prod = sourcePort.getPortRateExpression().evaluateAsLong();
    final long sourceRV = this.brv.get(sourceActor);
    final boolean needRoundbufferInterface = cons != (prod * sourceRV);
    final boolean needRoundbufferDelay = sinkActor.getDataPort().getFifo().getDelay() != null;
    if (needRoundbufferInterface || needRoundbufferDelay) {
      final RoundBufferActor interfaceRB = addRoundBufferOut(sinkActor);
      this.outPort2SRActors.get(correspondingPort).add(interfaceRB);
      return Collections.singletonList(interfaceRB);
    }
    if (sourceActor instanceof PiGraph) {
      // 2.3 If sourceActor is a PiGraph then we forward the mapped actors to the next level of hierarchy
      this.outPort2SRActors.get(correspondingPort).addAll(sourceSet);
      return Collections.emptyList();
    }
    // 2.4 If sourceActor is any other type of actor we map it to the interface
    final String keyActor = this.graphPrefix + sourceActor.getName();
    if (!this.actor2SRActors.containsKey(keyActor)) {
      populateSingleRatePiMMActor(sourceActor);
    }

    final List<AbstractVertex> returnList = new LinkedList<>();
    if (sourceActor.getContainingPiGraph().isCluster()) {
      final DataOutputInterface dopCopy = PiMMUserFactory.instance.copyWithHistory((DataOutputInterface) sinkActor);
      currentResultSrDAG.addActor(dopCopy);
      // I don't know why, but adding dipCopy to the graph resets its expression to 0, so we hate to set it again
      dopCopy.getGraphPort()
          .setExpression(PiMMUserFactory.instance.copyWithHistory(sinkActor.getGraphPort().getExpression()));

      final String dopCopyName = dopCopy.getContainingPiGraph().getName() + "_" + dopCopy.getName();
      this.actor2SRActors.put(dopCopyName, new LinkedList<>());
      this.actor2SRActors.get(dopCopyName).add(dopCopy);
      returnList.add(dopCopy);
    }
    this.outPort2SRActors.get(correspondingPort).addAll(this.actor2SRActors.get(keyActor));
    return returnList;
  }

  /**
   * Add a RoundBufferActor in the place of a DataOutputInterface
   *
   * @param sinkActor
   *          the sink interface
   * @return the RoundBufferActor
   */
  private RoundBufferActor addRoundBufferOut(final AbstractActor sinkActor) {
    final RoundBufferActor interfaceRB = PiMMUserFactory.instance.createRoundBufferActor();
    interfaceRB.setName("RB_" + this.graphPrefix + "_" + sinkActor.getName());
    // Add the RoundBufferActor to the graph
    this.currentResultSrDAG.addActor(interfaceRB);
    return interfaceRB;
  }

  /**
   * récupère les sources de ia et les connecte via une fausse fifo FINIR
   *
   * @param ia
   *          the InterfaceActor
   */
  private void reconnectTopLevelInterface(final InterfaceActor ia) {
    List<AbstractVertex> sourceSet = null;
    List<AbstractVertex> sinkSet = null;
    List<AbstractVertex> interfacesSet = null;
    if (ia instanceof DataInputInterface) {
      sourceSet = actor2SRActors.get(this.graphPrefix + ia.getName());
      interfacesSet = sourceSet;
      sinkSet = inPort2SRActors.get(ia.getGraphPort());
    } else {
      sourceSet = outPort2SRActors.get(ia.getGraphPort());
      sinkSet = actor2SRActors.get(this.graphPrefix + ia.getName());
      interfacesSet = sinkSet;
    }
    // reset interface name (suffixed by _0 in populate)
    // as RV is 1 for interfaces, there should be exactly one element in the list
    final InterfaceActor iaCopy = (InterfaceActor) interfacesSet.get(0);
    iaCopy.setName(ia.getName());
    // we can remove the config dependencies since all parameters have been resolved
    iaCopy.getConfigInputPorts().clear();
    // dummy fifo to trick the SR vertices linker
    final long rate = ia.getDataPort().getExpression().evaluateAsLong();
    final DataInputPort dummyDIP = PiMMUserFactory.instance.createDataInputPort(ia.getName());
    dummyDIP.setExpression(rate);
    dummyDIP.setName(ia.getName());
    final DataOutputPort dummyDOP = PiMMUserFactory.instance.createDataOutputPort(ia.getName());
    dummyDOP.setExpression(rate);
    dummyDOP.setName(ia.getName());
    final Fifo fifo = ia.getDataPort().getFifo();
    final Fifo dummyFifo = PiMMUserFactory.instance.createFifo(dummyDOP, dummyDIP, fifo.getType());
    // perform the reconnection
    final PiMMSRVerticesLinker srVerticesLinker = new PiMMSRVerticesLinker(dummyFifo, this.currentResultSrDAG,
        this.graphPrefix);
    srVerticesLinker.execute(this.brv, sourceSet, sinkSet);
  }

  /**
   * Gets the result.
   *
   * @return the result
   */
  public PiGraph getResult() {
    return this.result;
  }

  @Override
  public Boolean caseDataOutputPort(final DataOutputPort dop) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDelay(final Delay d) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDataInputPort(final DataInputPort dip) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseExpression(final Expression e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseConfigInputPort(final ConfigInputPort cip) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseConfigOutputPort(final ConfigOutputPort cop) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDependency(final Dependency d) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseISetter(final ISetter is) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean casePort(final Port p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDataPort(final DataPort p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseConfigInputInterface(final ConfigInputInterface cii) {
    if (this.currentGraphIsCluster) {
      final ConfigInputPort graphConfigPort = cii.getGraphPort();

      final ConfigInputInterface newCii = PiMMUserFactory.instance.copyWithHistory(cii);
      currentResultSrDAG.addConfigurable(newCii);
      newCii.setExpression(PiMMUserFactory.instance.copyWithHistory(cii.getExpression()));
      newCii.setName(this.graphPrefix + cii.getName());

      // the original setter Parameter
      final Parameter outerParam = (Parameter) cii.getGraphPort().getIncomingDependency().getSetter();
      final Parameter newOuterParam = param2param.get(outerParam); // the duplicated setter Parameter

      // we can now create a dependency between the new outer parameter and the new inner configInterface
      final Dependency newOuterDep = PiMMUserFactory.instance.copyWithHistory(graphConfigPort.getIncomingDependency());
      newOuterDep.setSetter(newOuterParam);
      newOuterDep.setGetter(newCii.getGraphPort());
      newOuterParam.getContainingPiGraph().addDependency(newOuterDep);

      this.param2param.put(cii, newCii);

      return true;
    }
    return null;
  }

  /**
   * Populate the new graph with the N instances of each actor w.r.t the BRV
   *
   * @param actor
   *          the actor
   */
  private void populateSingleRatePiMMActor(final AbstractVertex actor) {
    // special case for delays
    if (actor instanceof final DelayActor delayActor) {
      caseDelayActor(delayActor);
      return;
    }

    // Creates the entry for the current PiMM Actor
    this.actor2SRActors.put(this.graphPrefix + actor.getName(), new ArrayList<>());

    // Populate the DAG with the appropriate number of instances of the actor
    final long actorRV = this.brv.get(actor);
    final long backupInstance = this.firingInstance;

    // Populate the graph with the number of instance of the current actor
    final IntegerName iN = new IntegerName(actorRV - 1);
    for (long i = 0; i < actorRV; ++i) {
      // Setting the correct name
      // We fix the RV to be always > 1 for actors, this way we can not have a problem of naming as such
      // actor_#i, whose name is actor and we're at instance #i and a secondary actor named actor_x_#i with x an integer
      // In some cases it could happen that actor_x has a BRV of 1 resulting in a name of "actor_x" and
      // actor has a BRV value >= to x resulting of two actors named the same
      this.firingInstance = backupInstance * actorRV + i;
      this.currentActorName = this.graphPrefix + actor.getName() + "_" + iN.toString(i);
      // will dispatch to the correct case
      // for interface actors, it is executed only if at top level
      caseAbstractActor((AbstractActor) actor);
    }
    this.firingInstance = backupInstance;
  }

  /**
   * creates a copy of param and adds to to the param2param map
   */
  @Override
  public Boolean caseParameter(final Parameter param) {
    // make sure config input interfaces are made into Parameter (since their expressions have been evaluated)
    final double paramValue = param.getValueExpression().evaluateAsDouble();
    final String paramName = this.graphPrefix + param.getName();
    final Parameter copy = PiMMUserFactory.instance.createParameter(paramName, paramValue);
    this.param2param.put(param, copy);

    // My addition, not sure it works but otherwise I can't get the parameter's containing graph
    currentResultSrDAG.addConfigurable(copy);

    return true;
  }

  /**
   *
   */
  @Override
  public Boolean casePiGraph(final PiGraph graph) {
    // If it is a cluster, do nothing
    if (graph instanceof final PiGraph g && g.isCluster()) {
      this.currentGraphIsCluster = true;
      return caseCluster(g);
    }

    this.currentGraphIsCluster = false;
    this.currentResultSrDAG = this.result;

    // If there are no actors in the graph we leave
    final List<AbstractActor> actors = graph.getActors();
    if (actors.isEmpty()) {
      throw new UnsupportedOperationException(
          "Can not convert an empty graph. Check the refinement for [" + graph.getVertexPath() + "].");
    }

    // Set the current graph name
    this.graphName = graph.getContainingPiGraph() == null ? "" : graph.getName();
    this.graphName = this.graphPrefix + this.graphName;

    // Set the prefix graph name
    this.graphPrefix = this.graphName.isEmpty() ? "" : this.graphName + "_";

    for (final Parameter p : graph.getParameters()) {
      doSwitch(p);
    }

    // We need to split all delay actors before going in every iteration
    for (final Fifo f : graph.getFifosWithDelay()) {
      splitDelayActors(f);
    }
    final String currentPrefix = this.graphPrefix;
    final long graphRV = this.brv.getOrDefault(graph, 1L);
    final IntegerName iN = new IntegerName(graphRV - 1);
    final long backupInstance = this.firingInstance;
    for (long i = 0; i < graphRV; ++i) {
      if (!currentPrefix.isEmpty()) {
        this.graphPrefix = currentPrefix + iN.toString(i) + "_";
      }
      final long lInstance = backupInstance * graphRV + i;
      final String backupPrefix = this.graphPrefix;
      final String backupName = this.graphName;
      final boolean backupcurrentGraphIsCluster = this.currentGraphIsCluster;
      final PiGraph backupCurrentResultsrDAG = this.currentResultSrDAG;
      for (final PiGraph g : graph.getChildrenGraphs()) {
        this.firingInstance = lInstance;
        doSwitch(g);
        this.graphPrefix = backupPrefix;
        this.graphName = backupName;
        this.currentGraphIsCluster = backupcurrentGraphIsCluster;
        this.currentResultSrDAG = backupCurrentResultsrDAG;
        // This ligne removes the link between the cluster and its srdag equivalent, which we need.
        // this.actor2SRActors.clear();
        // Instead we will only remove the non-cluster actors, which will be kept forever
        clearActor2SRActors();
      }
      for (final Fifo f : graph.getFifosWithDelay()) {
        this.firingInstance = lInstance;
        doSwitch(f);
      }
      for (final Fifo f : graph.getFifosWithoutDelay()) {
        this.firingInstance = lInstance;
        doSwitch(f);
      }
    }
    this.firingInstance = backupInstance;

    // handle non connected actors (BRV = 1)
    // PiGraph are already handled by the code above, except if clustered
    // Special Actors and Delay Actors cannot have empty data ports
    // So only (regular) Actor are considered
    actors.stream().filter(a -> a.getAllDataPorts().isEmpty())
        .filter(a -> (a instanceof Actor) || (a instanceof PiGraph && a.isCluster()))
        .forEach(this::populateSingleRatePiMMActor);
    // handle the case of interfaces of top level
    if (graph.getContainingPiGraph() == null) {
      actors.stream().filter(InterfaceActor.class::isInstance).forEach(this::populateSingleRatePiMMActor);
      // now we need to reconnect the top level interfaces
      actors.stream().filter(InterfaceActor.class::isInstance)
          .forEach(a -> reconnectTopLevelInterface((InterfaceActor) a));
    }

    return true;
  }

  /**
   * Does basically the same as casePiGraph, except the actors in the clusters must not be added to the top dag, but to
   * the cluster's copy in this dag. Note it will mean that a copy of all the cluster's vertices will be added to the
   * cluster, basically making a copy of its graph. I think it won't cause problem (therefore it will)
   *
   * @param cluster
   *          the cluster
   * @return true
   */
  public Boolean caseCluster(PiGraph cluster) {
    final PiGraph copyGraph = PiMMUserFactory.instance.copyPiGraphWithHistory(cluster);

    // we have to manage all combinations of cases for graphs that should or not be flattened and/or converted to srdag
    if (srdagExclusionList.contains(cluster.getName())) {
      if (!flattenExclusionList.contains(cluster.getName())) {
        PiSDFFlattener.flatten(copyGraph, true);
      }
    }

    return true;
  }

  /**
   * Split each delay actors in two delay actors: a setter and a getter.
   *
   * <pre>
   *
   * 1. If a delay has no setter / getter actors, then init / end actors are added to replace them.
   *
   * 2. The setter actor is connected to a new delay actor and the same goes for the getter actor.
   *
   * </pre>
   *
   * The idea is to be able to first treat delay FIFOs normally and then come and replace the init / end actors added
   * during the single rate linking by the proper setter / getter ones.
   *
   * This pre-processing allows to keep the single-rate linking phase as generic as possible.
   *
   * @param fifo
   *          The current FIFO
   */
  private void splitDelayActors(final Fifo fifo) {
    final DelayActor delayActor = fifo.getDelay().getDelayActor();
    final String delayExpression = fifo.getDelay().getExpression().getExpressionAsString();
    final PiGraph parentGraph = fifo.getContainingPiGraph();
    // 0. Check if the DelayActor need to add Init / End
    if (delayActor.getSetterActor() == null) {
      addInitActorAsSetter(fifo, delayActor, delayExpression, parentGraph);
    }
    if (delayActor.getGetterActor() == null) {
      addEndActorAsGetter(fifo, delayActor, delayExpression, parentGraph);
    }
    // 1. We split the current actor in two for more convenience
    // 1.1 Let start by the setterActor
    final DelayActor setterActor = PiMMUserFactory.instance.createDelayActor();
    setterActor.setName(delayActor.getName() + "_setter");
    final DataInputPort setPort = PiMMUserFactory.instance.createDataInputPort();
    setPort.setName(delayActor.getDataInputPort().getName());
    setterActor.getDataInputPorts().add(setPort);
    // 1.1.1 Setting the new target port of the setter FIFO
    final Fifo setterFifo = delayActor.getDataInputPort().getFifo();
    setterFifo.setTargetPort(setPort);
    setPort.setExpression(setterFifo.getSourcePort().getPortRateExpression().getExpressionAsString());
    // 1.1.2 Setting the BRV value
    final AbstractActor srcContainingActor = setterFifo.getSourcePort().getContainingActor();
    final long brvSetter;
    if (this.brv.containsKey(srcContainingActor)) {
      brvSetter = this.brv.get(srcContainingActor);
    } else {
      brvSetter = 1L;
    }
    this.brv.put(setterActor, brvSetter);
    // 1.2 Now we do the getter actor
    final DelayActor getterActor = PiMMUserFactory.instance.createDelayActor();
    getterActor.setName(delayActor.getName() + "_getter");
    final DataOutputPort getPort = PiMMUserFactory.instance.createDataOutputPort();
    getPort.setName(delayActor.getDataOutputPort().getName());
    getPort.setExpression(delayExpression);
    getterActor.getDataOutputPorts().add(getPort);
    // 1.2.1 Setting the new source port of the getter FIFO
    final Fifo getterFifo = delayActor.getDataOutputPort().getFifo();
    getterFifo.setSourcePort(getPort);
    getPort.setExpression(getterFifo.getTargetPort().getPortRateExpression().getExpressionAsString());
    // 1.2.2 Setting the BRV value
    final AbstractActor tgtContainingActor = getterFifo.getTargetPort().getContainingActor();
    final long brvGetter;
    if (this.brv.containsKey(tgtContainingActor)) {
      brvGetter = this.brv.get(tgtContainingActor);
    } else {
      brvGetter = 1L;
    }
    this.brv.put(getterActor, brvGetter);
    // 2 We remove the old actor and add the new ones
    parentGraph.removeActor(delayActor);
    parentGraph.addActor(setterActor);
    parentGraph.addActor(getterActor);
  }

  private void addInitActorAsSetter(final Fifo fifo, final DelayActor delayActor, final String delayExpression,
      final PiGraph graph) {
    final InitActor init = PiMMUserFactory.instance.createInitActor();
    init.getDataOutputPorts().add(PiMMUserFactory.instance.createDataOutputPort());
    final DataInputPort targetPort = fifo.getTargetPort();
    init.getDataOutputPort().setName(targetPort.getName());
    init.getDataOutputPort().setExpression(delayExpression);
    init.getDataOutputPort().setAnnotation(PortMemoryAnnotation.WRITE_ONLY);
    // Set the proper init name
    final String initName = targetPort.getContainingActor().getName() + "_init_" + targetPort.getName();
    init.setName(initName);
    // Set the persistence level of the delay
    init.setLevel(fifo.getDelay().getLevel());
    // Set the END_REFERENCE
    // TODO: handle asymetric configuration
    final AbstractActor getterActor = fifo.getDelay().getGetterActor();
    if (getterActor != null) {
      final AbstractVertex lookupVertex = graph.lookupVertex(getterActor.getName());
      if (lookupVertex instanceof final AbstractActor aaLookupVertex) {
        init.setEndReference(aaLookupVertex);
        if (lookupVertex instanceof final EndActor eaLookupVertex) {
          eaLookupVertex.setInitReference(init);
        }
      }
    }
    // Create the FIFO and connect it
    final Fifo initFifo = PiMMUserFactory.instance.createFifo(init.getDataOutputPort(), delayActor.getDataInputPort(),
        fifo.getType());
    graph.addActor(init);
    graph.addFifo(initFifo);
    this.brv.put(init, 1L);
  }

  private void addEndActorAsGetter(final Fifo fifo, final DelayActor delayActor, final String delayExpression,
      final PiGraph graph) {
    final EndActor end = PiMMUserFactory.instance.createEndActor();
    end.getDataInputPorts().add(PiMMUserFactory.instance.createDataInputPort());
    final DataOutputPort sourcePort = fifo.getSourcePort();
    end.getDataInputPort().setName(sourcePort.getName());
    end.getDataInputPort().setExpression(delayExpression);
    end.getDataInputPort().setAnnotation(PortMemoryAnnotation.READ_ONLY);
    // Set the proper end name
    final String endName = sourcePort.getContainingActor().getName() + "_end_" + sourcePort.getName();
    end.setName(endName);
    // Set the persistence level of the delay
    // Set the INIT_REFERENCE
    final AbstractActor setterActor = fifo.getDelay().getSetterActor();
    if (setterActor != null) {
      final AbstractVertex lookupVertex = graph.lookupVertex(setterActor.getName());
      if (lookupVertex instanceof final AbstractActor aaLookupVertex) {
        end.setInitReference(aaLookupVertex);
        if (lookupVertex instanceof final InitActor iaLookupVertex) {
          iaLookupVertex.setEndReference(end);
        }
      }
    }
    // Create the FIFO and connect it
    final Fifo endFifo = PiMMUserFactory.instance.createFifo(delayActor.getDataOutputPort(), end.getDataInputPort(),
        fifo.getType());
    graph.addActor(end);
    graph.addFifo(endFifo);
    this.brv.put(end, 1L);
  }

  // TODO this can very likely be made more efficient, f.e by having a map for actors which can be cleared entirely and
  // a map for clusters which is never cleared.
  /**
   * does a clear() on actor256actors, but saves the mappings of cluster actors
   */
  private void clearActor2SRActors() {

    final Map<String, List<AbstractVertex>> clusterMap = new HashMap<>();
    // save the mappings of cluster actors
    for (final Map.Entry<String, List<AbstractVertex>> t : this.actor2SRActors.entrySet()) {
      final List<
          AbstractVertex> l = t.getValue().stream().filter(a -> a instanceof final PiGraph g && g.isCluster()).toList();
      if (!l.isEmpty()) {
        clusterMap.put(t.getKey(), t.getValue());
      }
    }
    this.actor2SRActors.clear();
    this.actor2SRActors.putAll(clusterMap);

  }
}
