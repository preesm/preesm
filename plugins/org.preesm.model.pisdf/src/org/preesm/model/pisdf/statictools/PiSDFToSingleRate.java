/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018 - 2019)
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.preesm.commons.IntegerName;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.BroadcastActor;
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
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.NonExecutableActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.statictools.optims.BroadcastRoundBufferOptimization;
import org.preesm.model.pisdf.statictools.optims.ForkJoinOptimization;
import org.preesm.model.pisdf.util.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 * @author farresti
 *
 */
public class PiSDFToSingleRate extends PiMMSwitch<Boolean> {

  /**
   * Precondition: All.
   *
   * @return the SDFGraph obtained by visiting graph
   */
  public static final PiGraph compute(final PiGraph graph, final BRVMethod method) {

    PreesmLogger.getLogger().log(Level.FINE, " >> Start srdag transfo");

    PreesmLogger.getLogger().log(Level.FINE, " >>   - check");
    PiGraphConsistenceChecker.check(graph);
    // 1. First we resolve all parameters.
    // It must be done first because, when removing persistence, local parameters have to be known at upper level
    PreesmLogger.getLogger().log(Level.FINE, " >>   - resolve params");
    PiMMHelper.resolveAllParameters(graph);
    // 2. We perform the delay transformation step that deals with persistence
    PreesmLogger.getLogger().log(Level.FINE, " >>   - remove persistence");
    PiMMHelper.removePersistence(graph);
    // 3. Compute BRV following the chosen method
    PreesmLogger.getLogger().log(Level.FINE, " >>   - compute brv");
    Map<AbstractVertex, Long> brv = PiBRV.compute(graph, method);
    // 4. Print the RV values
    PreesmLogger.getLogger().log(Level.FINE, " >>   - print brv");
    PiBRV.printRV(brv);
    // 4.5 Check periods with BRV
    PreesmLogger.getLogger().log(Level.FINE, " >>   - check periodicity");
    PiMMHelper.checkPeriodicity(brv);
    // 5. Convert to SR-DAG
    PreesmLogger.getLogger().log(Level.FINE, " >>   - apply single rate transfo");
    PiSDFToSingleRate staticPiMM2ASrPiMMVisitor = new PiSDFToSingleRate(graph, brv);
    staticPiMM2ASrPiMMVisitor.doSwitch(graph);
    final PiGraph acyclicSRPiMM = staticPiMM2ASrPiMMVisitor.getResult();

    PreesmLogger.getLogger().log(Level.FINE, " >>   - check");
    PiGraphConsistenceChecker.check(acyclicSRPiMM);
    // 6- do some optimization on the graph
    PreesmLogger.getLogger().log(Level.FINE, " >>   - fork join optim");
    final ForkJoinOptimization forkJoinOptimization = new ForkJoinOptimization();
    forkJoinOptimization.optimize(acyclicSRPiMM);
    PreesmLogger.getLogger().log(Level.FINE, " >>   - check");
    PiGraphConsistenceChecker.check(acyclicSRPiMM);

    PreesmLogger.getLogger().log(Level.FINE, " >>   - broadcast/rbuffers optim");
    final BroadcastRoundBufferOptimization brRbOptimization = new BroadcastRoundBufferOptimization();
    brRbOptimization.optimize(acyclicSRPiMM);

    PreesmLogger.getLogger().log(Level.FINE, " >>   - check");
    PiGraphConsistenceChecker.check(acyclicSRPiMM);

    srCheck(acyclicSRPiMM);
    PreesmLogger.getLogger().log(Level.FINE, " >> End srdag transfo");
    return acyclicSRPiMM;
  }

  /**
   *
   */
  private static final void srCheck(final PiGraph graph) {
    final List<AbstractActor> actors = graph.getActors();
    for (final AbstractActor a : actors) {
      if (a instanceof PiGraph) {
        throw new PreesmRuntimeException("Flatten graph should have no children graph");
      }
      if (a instanceof InterfaceActor) {
        throw new PreesmRuntimeException("Flatten graph should have no interface");
      }
      if (a.getContainingPiGraph() != graph) {
        throw new PreesmRuntimeException();
      }
    }
  }

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

  /** Current actor name */
  private String currentActorName;

  /** Current FIFO */
  private Fifo currentFifo;

  private final PiGraph inputGraph;

  /**
   * Instantiates a new abstract StaticPiMM2ASrPiMMVisitor.
   *
   */
  public PiSDFToSingleRate(final PiGraph inputGraph, final Map<AbstractVertex, Long> brv) {
    this.inputGraph = inputGraph;
    this.result = PiMMUserFactory.instance.createPiGraph();
    this.result.setName(this.inputGraph.getName());
    this.brv = brv;
    this.graphName = "";
    this.graphPrefix = "";
  }

  private final Map<Parameter, Parameter> param2param = new LinkedHashMap<>();

  /**
   *
   */
  private void instantiateParameters(final AbstractActor actor, final AbstractActor copyActor) {

    // // Copy parameters

    for (final ConfigInputPort port : copyActor.getConfigInputPorts()) {
      final Port lookupPort = actor.lookupPort(port.getName());
      if (lookupPort instanceof ConfigInputPort) {
        final Dependency incomingDependency = ((ConfigInputPort) lookupPort).getIncomingDependency();
        final ISetter setter = incomingDependency.getSetter();
        final Parameter parameter = param2param.get(setter);
        if (parameter == null) {
          throw new PreesmRuntimeException();
        } else {
          this.result.addParameter(parameter);
          final Dependency dep = PiMMUserFactory.instance.createDependency(parameter, port);
          this.result.addDependency(dep);
        }
      } else {
        throw new PreesmRuntimeException();
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#caseAbstractActor(org.ietr.preesm.experiment.model.pimm.
   * AbstractActor)
   */
  @Override
  public Boolean caseAbstractActor(final AbstractActor actor) {
    if (actor instanceof PiGraph) {
      // Here we handle the replacement of the interfaces by what should be
      // Copy the actor
      final PiGraph copyActor = PiMMUserFactory.instance.copyWithHistory((PiGraph) actor);
      // Set the properties
      copyActor.setName(this.currentActorName);

      // Add the actor to the graph
      this.result.addActor(copyActor);

      // Add the actor to the FIFO source/sink sets
      this.actor2SRActors.get(this.graphPrefix + actor.getName()).add(copyActor);
      instantiateParameters(actor, copyActor);
    } else {
      doSwitch(actor);
    }
    return true;
  }

  @Override
  public Boolean caseNonExecutableActor(final NonExecutableActor actor) {
    // Copy the BroadCast actor
    final NonExecutableActor copyActor = PiMMUserFactory.instance.copyWithHistory(actor);
    // Set the properties
    copyActor.setName(this.currentActorName);

    // Add the actor to the graph
    this.result.addActor(copyActor);

    // Add the actor to the FIFO source/sink sets
    this.actor2SRActors.get(this.graphPrefix + actor.getName()).add(copyActor);

    // Set the properties
    instantiateParameters(actor, copyActor);
    return true;
  }

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
    final long actorRV = actor instanceof InterfaceActor ? 1 : this.brv.get(actor);
    // 1. Find matched actors
    IntegerName iN = new IntegerName(actorRV - 1);
    for (long i = 0; i < actorRV; ++i) {
      final String name = actor.getName() + suffixe + iN.toString(i);
      final AbstractActor foundActor = (AbstractActor) this.result.lookupVertex(name);
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
      this.result.removeActor(foundActor);
      this.result.removeFifo(fifo);
    }
    return list;
  }

  @Override
  public Boolean caseExecutableActor(final ExecutableActor actor) {
    // Copy the BroadCast actor
    final ExecutableActor copyActor = PiMMUserFactory.instance.copyWithHistory(actor);
    // Set the properties
    copyActor.setName(this.currentActorName);

    // Add the actor to the graph
    this.result.addActor(copyActor);

    // Add the actor to the FIFO source/sink sets
    this.actor2SRActors.get(this.graphPrefix + actor.getName()).add(copyActor);

    // Set the properties
    instantiateParameters(actor, copyActor);
    return true;
  }

  @Override
  public Boolean caseDataInputInterface(final DataInputInterface actor) {
    return true;
  }

  @Override
  public Boolean caseDataOutputInterface(final DataOutputInterface actor) {
    return true;
  }

  @Override
  public Boolean caseFifo(final Fifo fifo) {
    // 0. Set current FIFO
    this.currentFifo = fifo;
    final DataOutputPort sourcePort = fifo.getSourcePort();
    final DataInputPort targetPort = fifo.getTargetPort();

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
    final PiMMSRVerticesLinker srVerticesLinker = new PiMMSRVerticesLinker(fifo, this.result, this.graphPrefix);
    srVerticesLinker.execute(this.brv, sourceSet, sinkSet);

    // In the case of Interfaces we might have disconnected the FIFO so let's reconnect it
    fifo.setSourcePort(sourcePort);
    fifo.setTargetPort(targetPort);
    return true;
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
   * @return list of source actor to be linked, null else
   */
  private List<AbstractVertex> getSourceSetForSRLinker(final Fifo fifo, final DataOutputPort sourcePort,
      final DataInputPort targetPort, final AbstractActor sourceActor, final AbstractActor sinkActor) {
    // There 3 mains special cases, source is an interface, source is a graph, source is a delay
    // Otherwise, we fall in "standard" case
    if (sourceActor instanceof InterfaceActor) {
      return handleDataInputInterface(targetPort, ((DataInputInterface) sourceActor), sinkActor);
    } else if (sourceActor instanceof PiGraph) {
      // We should retrieve the correct source set
      if (!this.outPort2SRActors.containsKey(sourcePort)) {
        throw new PreesmRuntimeException("No replacement found for DataOutputPort [" + sourcePort.getName()
            + "] of hierarchical actor [" + sourceActor.getName() + "].");
      }
      final List<AbstractVertex> sourceSet = this.outPort2SRActors.remove(sourcePort);
      // Now we change the "sourcePort" of the FIFO to match the one of the sourceSet
      final AbstractActor firstOfSet = (AbstractActor) sourceSet.get(0);
      final DataOutputPort foundPort = lookForSourcePort((PiGraph) sourceActor, firstOfSet, sourcePort.getName());
      if (foundPort != null) {
        fifo.setSourcePort(foundPort);
      }
      return sourceSet;
    } else {
      final String keyActor = this.graphPrefix + sourceActor.getName();
      if (!this.actor2SRActors.containsKey(keyActor)) {
        populateSingleRatePiMMActor(sourceActor);
      }
      return this.actor2SRActors.get(keyActor);
    }
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
  private DataOutputPort lookForSourcePort(final PiGraph graph, final AbstractActor source, final String sourceName) {
    for (final DataOutputInterface doi : graph.getDataOutputInterfaces()) {
      if (doi.getName().equals(sourceName)) {
        final Fifo inFifo = doi.getDataPort().getFifo();
        final DataOutputPort sourcePort = inFifo.getSourcePort();
        final AbstractActor containingActor = sourcePort.getContainingActor();
        if (containingActor instanceof PiGraph) {
          return lookForSourcePort((PiGraph) containingActor, source, sourcePort.getName());
        }
        return (DataOutputPort) source.lookupPort(sourcePort.getName());
      }
    }
    return null;
  }

  /**
   * Handles a DataInputInterface replacement
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
      final DataInputInterface sourceActor, final AbstractActor sinkActor) {
    final PiGraph graph = sourceActor.getContainingPiGraph();
    final DataInputPort correspondingPort = (DataInputPort) graph.lookupGraphDataPortForInterfaceActor(sourceActor);

    // 2.1 Create the entry in the map
    if (!inPort2SRActors.containsKey(correspondingPort)) {
      inPort2SRActors.put(correspondingPort, new ArrayList<>());
    }

    // 2.2 Now check if we need a BroadcastActor
    long prod = correspondingPort.getPortRateExpression().evaluate();
    long cons = targetPort.getPortRateExpression().evaluate();
    long sinkRV = this.brv.get(sinkActor);
    final boolean needBroadcastInterface = prod != (cons * sinkRV);
    final boolean needBroadcastDelay = sourceActor.getDataOutputPorts().get(0).getOutgoingFifo().getDelay() != null;
    if (needBroadcastInterface || needBroadcastDelay) {
      final BroadcastActor interfaceBR = addBroadCastIn(sourceActor);
      inPort2SRActors.get(correspondingPort).add(interfaceBR);
      return Collections.singletonList(interfaceBR);
    } else {
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
      inPort2SRActors.get(correspondingPort).addAll(sinkSet);
      return Collections.emptyList();
    }
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
    this.result.addActor(interfaceBR);
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

    if (sinkActor instanceof InterfaceActor) {
      return handleDataOutputInterface(sourcePort, sourceActor, (DataOutputInterface) sinkActor, sourceSet);
    } else if (sinkActor instanceof PiGraph) {
      // We should retrieve the correct source set
      if (!this.inPort2SRActors.containsKey(targetPort)) {
        throw new PreesmRuntimeException("No replacement found for DataInputPort [" + targetPort.getName()
            + "] of hierarchical actor [" + sinkActor.getName() + "].");
      }
      final List<AbstractVertex> sinkSet = this.inPort2SRActors.remove(targetPort);
      // Now we change the "sinkPort" of the FIFO to match the one of the sinkSet if needed
      final AbstractActor firstOfSet = (AbstractActor) sinkSet.get(0);
      final DataInputPort foundPort = lookForTargetPort((PiGraph) sinkActor, firstOfSet, targetPort.getName());
      if (foundPort != null) {
        fifo.setTargetPort(foundPort);
      }
      return sinkSet;
    } else {
      final String keyActor = this.graphPrefix + sinkActor.getName();
      if (!this.actor2SRActors.containsKey(keyActor)) {
        populateSingleRatePiMMActor(sinkActor);
      }
      return this.actor2SRActors.get(keyActor);
    }
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
  private DataInputPort lookForTargetPort(final PiGraph graph, final AbstractActor target, final String targetName) {
    for (final DataInputInterface dii : graph.getDataInputInterfaces()) {
      if (dii.getName().equals(targetName)) {
        final Fifo outFifo = dii.getDataPort().getFifo();
        final DataInputPort targetPort = outFifo.getTargetPort();
        final AbstractActor containingActor = targetPort.getContainingActor();
        if (containingActor instanceof PiGraph) {
          return lookForTargetPort((PiGraph) containingActor, target, targetPort.getName());
        }
        return (DataInputPort) target.lookupPort(targetPort.getName());
      }
    }
    return null;
  }

  /**
   * Handles a DataOutputInterface replacement
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
      final AbstractActor sourceActor, final DataOutputInterface sinkActor, final List<AbstractVertex> sourceSet) {
    final PiGraph graph = sinkActor.getContainingPiGraph();
    final DataOutputPort correspondingPort = (DataOutputPort) graph.lookupGraphDataPortForInterfaceActor(sinkActor);

    // 2.1 Create the entry in the map if needed
    if (!this.outPort2SRActors.containsKey(correspondingPort)) {
      this.outPort2SRActors.put(correspondingPort, new ArrayList<>());
    }

    // 2.2 Now check if we need a RoundBufferActor
    long cons = correspondingPort.getPortRateExpression().evaluate();
    long prod = sourcePort.getPortRateExpression().evaluate();
    long sourceRV = this.brv.get(sourceActor);
    final boolean needRoundbufferInterface = cons != (prod * sourceRV);
    final boolean needRoundbufferDelay = sinkActor.getDataInputPorts().get(0).getIncomingFifo().getDelay() != null;
    if (needRoundbufferInterface || needRoundbufferDelay) {
      final RoundBufferActor interfaceRB = addRoundBufferOut(sinkActor);
      this.outPort2SRActors.get(correspondingPort).add(interfaceRB);
      return Collections.singletonList(interfaceRB);
    } else if (sourceActor instanceof PiGraph) {
      // 2.3 If sourceActor is a PiGraph then we forward the mapped actors to the next level of hierarchy
      this.outPort2SRActors.get(correspondingPort).addAll(sourceSet);
      return Collections.emptyList();
    } else {
      // 2.4 If sourceActor is any other type of actor we map it to the interface
      final String keyActor = this.graphPrefix + sourceActor.getName();
      if (!this.actor2SRActors.containsKey(keyActor)) {
        populateSingleRatePiMMActor(sourceActor);
      }
      this.outPort2SRActors.get(correspondingPort).addAll(this.actor2SRActors.get(keyActor));
      return Collections.emptyList();
    }
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
    this.result.addActor(interfaceRB);
    return interfaceRB;
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

  /**
   * Populate the new graph with the N instances of each actor w.r.t the BRV
   *
   * @param actor
   *          the actor
   */
  private void populateSingleRatePiMMActor(final AbstractVertex actor) {
    if (actor instanceof DelayActor) {
      caseDelayActor((DelayActor) actor);
      return;
    }

    // Ignore DataInterfaces
    if (actor instanceof InterfaceActor) {
      return;
    }

    // Creates the entry for the current PiMM Actor
    this.actor2SRActors.put(this.graphPrefix + actor.getName(), new ArrayList<>());

    // Populate the DAG with the appropriate number of instances of the actor
    final long actorRV = this.brv.get(actor);

    // Populate the graph with the number of instance of the current actor
    IntegerName iN = new IntegerName(actorRV - 1);
    for (long i = 0; i < actorRV; ++i) {
      // Setting the correct name
      // We fix the RV to be always > 1 for actors, this way we can not have a problem of naming as such
      // actor_#i, whose name is actor and we're at instance #i and a secondary actor named actor_x_#i with x an integer
      // In some cases it could happen that actor_x has a BRV of 1 resulting in a name of "actor_x" and
      // actor has a BRV value >= to x resulting of two actors named the same
      this.currentActorName = this.graphPrefix + actor.getName() + "_" + iN.toString(i);
      caseAbstractActor((AbstractActor) actor);
    }
  }

  @Override
  public Boolean caseParameter(final Parameter param) {
    // make sure config input interfaces are made into Parameter (since their expressions have been evaluated);
    final long paramValue = param.getValueExpression().evaluate();
    final String paramName = this.graphPrefix + param.getName();
    final Parameter copy = PiMMUserFactory.instance.createParameter(paramName, paramValue);
    this.param2param.put(param, copy);
    return true;
  }

  @Override
  public Boolean casePiGraph(final PiGraph graph) {

    // If there are no actors in the graph we leave
    if (graph.getActors().isEmpty()) {
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
    final long graphRV = this.brv.get(graph) == null ? 1 : this.brv.get(graph);
    final String currentPrefix = this.graphPrefix;
    IntegerName iN = new IntegerName(graphRV - 1);
    for (long i = 0; i < graphRV; ++i) {
      if (!currentPrefix.isEmpty()) {
        this.graphPrefix = currentPrefix + iN.toString(i) + "_";
      }
      final String backupPrefix = this.graphPrefix;
      final String backupName = this.graphName;
      for (final PiGraph g : graph.getChildrenGraphs()) {
        doSwitch(g);
        this.graphPrefix = backupPrefix;
        this.graphName = backupName;
        this.actor2SRActors.clear();
      }
      for (final Fifo f : graph.getFifosWithDelay()) {
        doSwitch(f);
      }
      for (final Fifo f : graph.getFifosWithoutDelay()) {
        doSwitch(f);
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
    final DelayActor delayActor = fifo.getDelay().getActor();
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
    parentGraph.removeActor((AbstractActor) delayActor);
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
      if (lookupVertex instanceof AbstractActor) {
        init.setEndReference((AbstractActor) lookupVertex);
        if (lookupVertex instanceof EndActor) {
          ((EndActor) lookupVertex).setInitReference(init);
        }
      }
    }
    // Create the FIFO and connect it
    final Fifo initFifo = PiMMUserFactory.instance.createFifo(init.getDataOutputPort(), delayActor.getDataInputPort(),
        fifo.getType());
    graph.addActor(init);
    graph.addFifo(initFifo);
    this.brv.put(init, (long) 1);
  }

  private void addEndActorAsGetter(final Fifo fifo, final DelayActor delayActor, final String delayExpression,
      final PiGraph graph) {
    final EndActor end = PiMMUserFactory.instance.createEndActor();
    end.getDataInputPorts().add(PiMMUserFactory.instance.createDataInputPort());
    final DataOutputPort sourcePort = fifo.getSourcePort();
    end.getDataInputPort().setName(sourcePort.getName());
    end.getDataInputPort().setExpression(delayExpression);
    // Set the proper end name
    final String endName = sourcePort.getContainingActor().getName() + "_end_" + sourcePort.getName();
    end.setName(endName);
    // Set the persistence level of the delay
    end.setLevel(fifo.getDelay().getLevel());
    // Set the INIT_REFERENCE
    // TODO: handle asymetric configuration
    final AbstractActor setterActor = fifo.getDelay().getSetterActor();
    if (setterActor != null) {
      final AbstractVertex lookupVertex = graph.lookupVertex(setterActor.getName());
      if (lookupVertex instanceof AbstractActor) {
        end.setInitReference((AbstractActor) lookupVertex);
        if (lookupVertex instanceof InitActor) {
          ((InitActor) lookupVertex).setEndReference(end);
        }
      }
    }
    // Create the FIFO and connect it
    final Fifo endFifo = PiMMUserFactory.instance.createFifo(delayActor.getDataOutputPort(), end.getDataInputPort(),
        fifo.getType());
    graph.addActor(end);
    graph.addFifo(endFifo);
    this.brv.put(end, (long) 1);
  }
}
