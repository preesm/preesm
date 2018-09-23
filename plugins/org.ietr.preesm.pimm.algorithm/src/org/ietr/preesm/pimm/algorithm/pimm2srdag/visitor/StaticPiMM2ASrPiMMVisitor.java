/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
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
package org.ietr.preesm.pimm.algorithm.pimm2srdag.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.preesm.core.scenario.ConstraintGroup;
import org.ietr.preesm.core.scenario.ConstraintGroupManager;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.factory.PiMMUserFactory;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.BroadcastActor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.DelayActor;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.EndActor;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InitActor;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;
import org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch;
import org.ietr.preesm.pimm.algorithm.helper.PiMMHelperException;

/**
 * @author farresti
 *
 */
public class StaticPiMM2ASrPiMMVisitor extends PiMMSwitch<Boolean> {
  /** The result. */
  // SRDAG graph created from the outer graph
  private final PiGraph result;

  /** Basic repetition vector of the graph */
  private final Map<AbstractVertex, Long> brv;

  /** The scenario. */
  private final PreesmScenario scenario;

  /** Ecore copier utility */
  private static final EcoreUtil.Copier copier = new EcoreUtil.Copier(true);

  /** Map from original PiMM vertices to generated DAG vertices */
  private final Map<String, List<AbstractVertex>> actor2SRActors = new LinkedHashMap<>();

  /** Map of all DataInputInterface to corresponding vertices */
  private final Map<String, List<AbstractVertex>> inPort2SRActors = new LinkedHashMap<>();

  /** Map of all DataOutputInterface to corresponding vertices */
  private final Map<String, List<AbstractVertex>> outPort2SRActors = new LinkedHashMap<>();

  /** List of the constrants operator ID of the current actor */
  ArrayList<String> currentOperatorIDs;

  /** PiMM Graph name */
  String originalGraphName;

  /** Current Single-Rate Graph name */
  String graphName;

  /** Current graph prefix */
  String graphPrefix;

  /** Current actor name */
  String currentActorName;

  /** Current FIFO */
  Fifo currentFifo;

  /**
   * Instantiates a new abstract StaticPiMM2ASrPiMMVisitor.
   *
   * @param graph
   *          The original PiGraph to be converted
   * @param brv
   *          the Basic Repetition Vector Map
   * @param scenario
   *          the scenario
   * 
   */
  public StaticPiMM2ASrPiMMVisitor(final PiGraph graph, final Map<AbstractVertex, Long> brv,
      final PreesmScenario scenario) {
    this.result = PiMMUserFactory.instance.createPiGraph();
    this.result.setName(graph.getName());
    this.brv = brv;
    this.scenario = scenario;
    this.graphName = "";
    this.graphPrefix = "";
  }

  /**
   * Build proper name for instance [index] of a given vertex.
   * 
   * @param prefixe
   *          Prefix to apply to the vertex name
   * @param vertexName
   *          The vertex name
   * @param index
   *          Instance of the vertex in the single-rate graph
   * @return The proper built name
   */
  private String buildName(final String prefixe, final String vertexName, final long index) {
    return index > 0 ? prefixe + vertexName + "_" + Long.toString(index) : prefixe + vertexName;
  }

  /**
   * Set basic properties from a PiMM actor to the copied actor
   * 
   * @param actor
   *          original PiMM actor
   * @param copyActor
   *          copied PiMM actor
   */
  private void setPropertiesToCopyActor(final AbstractActor actor, final AbstractActor copyActor) {
    // Set the properties
    copyActor.setName(this.currentActorName);

    // // Copy parameters
    for (final Parameter p : actor.getInputParameters()) {
      final ConfigInputPort originalCIP = actor.lookupConfigInputPortConnectedWithParameter(p);
      final ConfigInputPort cip = (ConfigInputPort) copyActor.lookupPort(originalCIP.getName());
      if (cip != null) {
        final Parameter copy = (Parameter) copier.copy(p);
        final Dependency dep = PiMMUserFactory.instance.createDependency();
        dep.setSetter(copy);
        cip.setIncomingDependency(dep);
      }
    }

    // Add the actor to the graph
    this.result.addActor(copyActor);

    // Add the actor to the FIFO source/sink sets
    this.actor2SRActors.get(this.graphName + actor.getName()).add(copyActor);

    // Add the scenario constraints
    final ConstraintGroupManager constraintGroupManager = this.scenario.getConstraintGroupManager();
    this.currentOperatorIDs.forEach(s -> constraintGroupManager.addConstraint(s, copyActor));
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
      final PiGraph copyActor = (PiGraph) copier.copy(actor);
      setPropertiesToCopyActor(actor, copyActor);
      return true;
    }
    doSwitch(actor);
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitActor(org.ietr.preesm.experiment.model.pimm.Actor)
   */
  @Override
  public Boolean caseActor(final Actor actor) {
    // Copy the actor
    final Actor copyActor = (Actor) copier.copy(actor);

    // Set the properties
    setPropertiesToCopyActor(actor, copyActor);
    return true;
  }

  @Override
  public Boolean caseInitActor(final InitActor actor) {
    // Fetch the actor
    final InitActor initActor = (InitActor) this.result.lookupVertex(actor.getName());
    // Update name
    initActor.setName(this.graphPrefix + initActor.getName());
    return true;
  }

  @Override
  public Boolean caseEndActor(final EndActor actor) {
    // Fetch the actor
    final EndActor endActor = (EndActor) this.result.lookupVertex(actor.getName());
    // Update name
    endActor.setName(this.graphPrefix + endActor.getName());
    // Update END_REFERENCE
    endActor.setEndReference(this.graphPrefix + endActor.getEndReference());
    return true;
  }

  @Override
  public Boolean caseDelayActor(final DelayActor actor) {
    final boolean isSetter = this.currentFifo.getTargetPort().getContainingActor() instanceof DelayActor;
    if (isSetter) {
      this.actor2SRActors.put(this.graphName + actor.getName(), generateSetterList(actor));
    } else {
      this.actor2SRActors.put(this.graphName + actor.getName(), generateGetterList(actor));
    }
    return true;
  }

  private List<AbstractVertex> generateSetterList(final DelayActor actor) {
    final AbstractActor sourceActor = this.currentFifo.getSourcePort().getContainingActor();
    final List<AbstractVertex> setterList = new ArrayList<>();
    final long setterRV = sourceActor instanceof InterfaceActor ? 1 : this.brv.get(sourceActor);
    // // First we need to find the end actors
    for (int i = 0; i < setterRV; ++i) {
      final String currentInitName = sourceActor.getName() + "_init_" + Integer.toString(i);
      final InitActor currentInit = (InitActor) this.result.lookupVertex(currentInitName);
      final DataOutputPort outputPort = currentInit.getDataOutputPort();
      final Fifo fifo = outputPort.getOutgoingFifo();
      final DataInputPort targetPort = fifo.getTargetPort();
      final AbstractActor target = targetPort.getContainingActor();
      // Update port name
      actor.getDataInputPort().setName(targetPort.getName());
      setterList.add(target);
      this.result.removeActor(currentInit);
      this.result.removeFifo(fifo);
    }
    return setterList;
  }

  private List<AbstractVertex> generateGetterList(final DelayActor actor) {
    final AbstractActor targetActor = this.currentFifo.getTargetPort().getContainingActor();
    final List<AbstractVertex> getterList = new ArrayList<>();
    final long getterRV = targetActor instanceof InterfaceActor ? 1 : this.brv.get(targetActor);
    // First we need to find the end actors
    for (int i = 0; i < getterRV; ++i) {
      final String currentEndName = targetActor.getName() + "_end_" + Integer.toString(i);
      final EndActor currentEnd = (EndActor) this.result.lookupVertex(currentEndName);
      final DataInputPort inputPort = currentEnd.getDataInputPort();
      final Fifo fifo = inputPort.getIncomingFifo();
      final DataOutputPort sourcePort = fifo.getSourcePort();
      final AbstractActor source = sourcePort.getContainingActor();
      // Update port name
      actor.getDataOutputPort().setName(sourcePort.getName());
      getterList.add(source);
      this.result.removeActor(currentEnd);
      this.result.removeFifo(fifo);
    }
    return getterList;
  }

  @Override
  public Boolean caseBroadcastActor(final BroadcastActor actor) {
    // Copy the BroadCast actor
    final BroadcastActor copyActor = (BroadcastActor) copier.copy(actor);

    // Set the properties
    setPropertiesToCopyActor(actor, copyActor);
    return true;
  }

  @Override
  public Boolean caseRoundBufferActor(final RoundBufferActor actor) {
    // Copy the RoundBuffer actor
    final RoundBufferActor copyActor = (RoundBufferActor) copier.copy(actor);

    // Set the properties
    setPropertiesToCopyActor(actor, copyActor);
    return true;
  }

  @Override
  public Boolean caseJoinActor(final JoinActor actor) {
    // Copy the Join actor
    final JoinActor copyActor = (JoinActor) copier.copy(actor);

    // Set the properties
    setPropertiesToCopyActor(actor, copyActor);
    return true;
  }

  @Override
  public Boolean caseForkActor(final ForkActor actor) {
    // Copyt the Fork actor
    final ForkActor copyActor = (ForkActor) copier.copy(actor);

    // Set the properties
    setPropertiesToCopyActor(actor, copyActor);
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

    if (sinkActor instanceof EndActor || sourceActor instanceof InitActor) {
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
    final PiMMSRVerticesLinker srVerticesLinker = new PiMMSRVerticesLinker(fifo, this.result, this.scenario,
        this.graphPrefix);
    try {
      srVerticesLinker.execute(this.brv, sourceSet, sinkSet);
    } catch (final PiMMHelperException e) {
      throw new WorkflowException(e.getMessage());
    }

    // In the case of Interfaces we might have disconnected the FIFO so let's reconnect it
    fifo.setSourcePort(sourcePort);
    fifo.setTargetPort(targetPort);
    return true;
  }

  /**
   * 
   * @param fifo
   *          the fifo
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
      return handleDataInputInterface(targetPort, sourceActor, sinkActor);
    } else if (sourceActor instanceof PiGraph) {
      // We should retrieve the correct source set
      final String key = sourceActor.getName() + "_" + sourcePort.getName();
      if (!this.outPort2SRActors.containsKey(key)) {
        throw new WorkflowException("No replacement found for DataOutputPort [" + sourcePort.getName()
            + "] of hierarchical actor [" + sourceActor.getName() + "].");
      }
      final List<AbstractVertex> sourceSet = this.outPort2SRActors.remove(key);
      // Now we change the "sourcePort" of the FIFO to match the one of the sourceSet
      final AbstractActor firstOfSet = (AbstractActor) sourceSet.get(0);
      for (final DataOutputInterface doi : ((PiGraph) sourceActor).getDataOutputInterfaces()) {
        if (doi.getName().equals(sourcePort.getName())) {
          final DataInputPort dataInputPort = doi.getDataInputPorts().get(0);
          final Fifo incomingFifo = dataInputPort.getIncomingFifo();
          final DataOutputPort correspondingSourcePort = incomingFifo.getSourcePort();
          final Port lookupPort = firstOfSet.lookupPort(correspondingSourcePort.getName());
          if (lookupPort != null) {
            fifo.setSourcePort((DataOutputPort) lookupPort);
          }
          break;
        }
      }
      return sourceSet;
    } else {
      final String keyActor = this.graphName + sourceActor.getName();
      if (!this.actor2SRActors.containsKey(keyActor)) {
        populateSingleRatePiMMActor(sourceActor);
      }
      return this.actor2SRActors.get(keyActor);
    }
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
   * @return a list containing a BroadcastActor if needed, null else
   */
  private List<AbstractVertex> handleDataInputInterface(final DataInputPort targetPort, final AbstractActor sourceActor,
      final AbstractActor sinkActor) {
    final DataInputPort correspondingPort = (DataInputPort) ((DataInputInterface) sourceActor).getGraphPort();

    // 2.1 Create the entry in the map
    final String keyPort = originalGraphName + "_" + correspondingPort.getName();
    if (!inPort2SRActors.containsKey(keyPort)) {
      inPort2SRActors.put(keyPort, new ArrayList<>());
    }

    // 2.2 Now check if we need a BroadcastActor
    long prod = Long.parseLong(correspondingPort.getPortRateExpression().getExpressionString());
    long cons = Long.parseLong(targetPort.getPortRateExpression().getExpressionString());
    long sinkRV = this.brv.get(sinkActor);
    final boolean needBroadcastInterface = prod != (cons * sinkRV);
    final boolean needBroadcastDelay = sourceActor.getDataOutputPorts().get(0).getOutgoingFifo().getDelay() != null;
    if (needBroadcastInterface || needBroadcastDelay) {
      final BroadcastActor interfaceBR = addBroadCastIn(sourceActor);
      inPort2SRActors.get(keyPort).add(interfaceBR);
      return Collections.singletonList(interfaceBR);
    } else {
      final List<AbstractVertex> sinkSet;
      if (sinkActor instanceof PiGraph) {
        final String subKeyPort = sinkActor.getName() + "_" + targetPort.getName();
        sinkSet = inPort2SRActors.remove(subKeyPort);
      } else {
        final String keyActor = this.graphName + sinkActor.getName();
        if (!this.actor2SRActors.containsKey(keyActor)) {
          populateSingleRatePiMMActor(sinkActor);
        }
        sinkSet = this.actor2SRActors.get(keyActor);
      }
      inPort2SRActors.get(keyPort).addAll(sinkSet);
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
    interfaceBR.setName("BR_" + this.graphName + "_" + sourceActor.getName());
    // Add the BroadcastActor to the graph
    this.result.addActor(interfaceBR);
    return interfaceBR;
  }

  /**
   * 
   * @param fifo
   *          the fifo
   * @param sourcePort
   *          the sourcePort
   * @param targetPort
   *          the targetPort
   * @param sourceActor
   *          the source actor
   * @param sinkActor
   *          the sink actor
   * @return list of sink actor to be linked, null else
   */
  private List<AbstractVertex> getSinkSetForSRLinker(final Fifo fifo, final DataOutputPort sourcePort,
      final DataInputPort targetPort, final AbstractActor sourceActor, final AbstractActor sinkActor,
      final List<AbstractVertex> sourceSet) {

    if (sinkActor instanceof InterfaceActor) {
      return handleDataOutputInterface(sourcePort, sourceActor, sinkActor, sourceSet);
    } else if (sinkActor instanceof PiGraph) {
      // We should retrieve the correct source set
      final String key = sinkActor.getName() + "_" + targetPort.getName();
      if (!this.inPort2SRActors.containsKey(key)) {
        throw new WorkflowException("No replacement found for DataInputPort [" + targetPort.getName()
            + "] of hierarchical actor [" + sinkActor.getName() + "].");
      }
      final List<AbstractVertex> sinkSet = this.inPort2SRActors.remove(key);
      // Now we change the "sinkPort" of the FIFO to match the one of the sinkSet if needed
      final AbstractActor firstOfSet = (AbstractActor) sinkSet.get(0);
      for (final DataInputInterface dii : ((PiGraph) sinkActor).getDataInputInterfaces()) {
        if (dii.getName().equals(targetPort.getName())) {
          final DataOutputPort dataOutputPort = dii.getDataOutputPorts().get(0);
          final Fifo outgoingFifo = dataOutputPort.getOutgoingFifo();
          final DataInputPort correspondingTargetPort = outgoingFifo.getTargetPort();
          final Port lookupPort = firstOfSet.lookupPort(correspondingTargetPort.getName());
          if (lookupPort != null) {
            fifo.setTargetPort((DataInputPort) lookupPort);
          }
          break;
        }
      }
      return sinkSet;
    } else {
      final String keyActor = this.graphName + sinkActor.getName();
      if (!this.actor2SRActors.containsKey(keyActor)) {
        populateSingleRatePiMMActor(sinkActor);
      }
      return this.actor2SRActors.get(keyActor);
    }
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
   * @return a list containing a RoundBufferActor if needed, null else
   */
  private List<AbstractVertex> handleDataOutputInterface(final DataOutputPort sourcePort,
      final AbstractActor sourceActor, final AbstractActor sinkActor, final List<AbstractVertex> sourceSet) {
    final DataOutputPort correspondingPort = (DataOutputPort) ((DataOutputInterface) sinkActor).getGraphPort();

    // 2.1 Create the entry in the map if needed
    final String keyPort = originalGraphName + "_" + correspondingPort.getName();
    if (!this.outPort2SRActors.containsKey(keyPort)) {
      this.outPort2SRActors.put(keyPort, new ArrayList<>());
    }

    // 2.2 Now check if we need a RoundBufferActor
    long cons = Long.parseLong(correspondingPort.getPortRateExpression().getExpressionString());
    long prod = Long.parseLong(sourcePort.getPortRateExpression().getExpressionString());
    long sourceRV = this.brv.get(sourceActor);
    final boolean needRoundbufferInterface = cons != (prod * sourceRV);
    final boolean needRoundbufferDelay = sinkActor.getDataInputPorts().get(0).getIncomingFifo().getDelay() != null;
    if (needRoundbufferInterface || needRoundbufferDelay) {
      final RoundBufferActor interfaceRB = addRoundBufferOut(sinkActor);
      this.outPort2SRActors.get(keyPort).add(interfaceRB);
      return Collections.singletonList(interfaceRB);
    } else if (sourceActor instanceof PiGraph) {
      // 2.3 If sourceActor is a PiGraph then we forward the mapped actors to the next level of hierarchy
      this.outPort2SRActors.get(keyPort).addAll(sourceSet);
      return Collections.emptyList();
    } else {
      // 2.4 If sourceActor is any other type of actor we map it to the interface
      final String keyActor = this.graphName + sourceActor.getName();
      if (!this.actor2SRActors.containsKey(keyActor)) {
        populateSingleRatePiMMActor(sourceActor);
      }
      this.outPort2SRActors.get(keyPort).addAll(this.actor2SRActors.get(keyActor));
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
    interfaceRB.setName("RB_" + this.graphName + "_" + sinkActor.getName());
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

  @Override
  public Boolean caseExecutableActor(final ExecutableActor ea) {
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

    // Ignore DataInterfaces also
    if (actor instanceof InterfaceActor) {
      return;
    }

    // Populate the DAG with the appropriate number of instances of the actor
    final Long actorNBRepeat = this.brv.get(actor);

    // Creates the entry for the current PiMM Actor
    this.actor2SRActors.put(this.graphName + actor.getName(), new ArrayList<>());

    // Initialize the operator IDs list
    this.currentOperatorIDs = new ArrayList<>();
    final Set<ConstraintGroup> constraintGroups = this.scenario.getConstraintGroupManager().getConstraintGroups();
    for (final ConstraintGroup cg : constraintGroups) {
      final Set<String> vertexPaths = cg.getVertexPaths();
      final Set<String> operatorIds = cg.getOperatorIds();
      if (vertexPaths.contains(actor.getVertexPath())) {
        currentOperatorIDs.add((String) operatorIds.toArray()[0]);
      }
    }

    // Populate the graph with the number of instance of the current actor
    for (long i = 0; i < actorNBRepeat; ++i) {
      // Setting the correct name
      this.currentActorName = buildName(this.graphPrefix, actor.getName(), i);
      caseAbstractActor((AbstractActor) actor);
    }
  }

  @Override
  public Boolean casePiGraph(final PiGraph graph) {
    // Set the prefix graph name
    this.graphPrefix = this.graphName.isEmpty() ? "" : this.graphName + "_";

    // If there are no actors in the graph we leave
    if (graph.getActors().isEmpty()) {
      throw new UnsupportedOperationException(
          "Can not convert an empty graph. Check the refinement for [" + graph.getVertexPath() + "].");
    }
    if (graph.getContainingPiGraph() == null) {
      // Do the split of the delay actors for the top-level here
      for (final Fifo f : graph.getFifosWithDelay()) {
        splitDelayActors(f);
      }
    }

    // Save the current graph name
    final String backupGraphName = this.graphName;

    // Go check hierarchical graphs
    for (final PiGraph g : graph.getChildrenGraphs()) {
      // We need to split all delay actors before going in every iteration
      for (final Fifo f : g.getFifosWithDelay()) {
        splitDelayActors(f);
      }
      final Long brvGraph = this.brv.get(g);
      final String currentPrefix = this.graphPrefix;
      for (long i = 0; i < brvGraph; ++i) {
        this.graphPrefix = currentPrefix;
        this.graphName = buildName(this.graphPrefix, g.getName(), i);
        doSwitch(g);
      }
    }
    // Restore the graph name
    this.graphName = backupGraphName;

    this.originalGraphName = graph.getName();

    // Set the prefix graph name
    this.graphPrefix = this.graphName.isEmpty() ? "" : this.graphName + "_";

    // Perform the Multi-Rate to Single-Rate transformation based on the FIFOs
    // We first deal with the FIFOs with delays, so we can replace Init / End actors by the proper Setter / Getter
    for (final Fifo f : graph.getFifosWithDelay()) {
      doSwitch(f);
    }
    for (final Fifo f : graph.getFifosWithoutDelay()) {
      doSwitch(f);
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
   *          The current fifo
   */
  private void splitDelayActors(final Fifo fifo) {
    final DelayActor delayActor = fifo.getDelay().getActor();
    final String delayExpression = fifo.getDelay().getExpression().getExpressionString();
    final PiGraph graph = fifo.getContainingPiGraph();
    // 0. Check if the DelayActor need to add Init / End
    if (delayActor.getSetterActor() == null) {
      addInitActorAsSetter(fifo, delayActor, delayExpression, graph);
    }
    if (delayActor.getGetterActor() == null) {
      addEndActorAsGetter(fifo, delayActor, delayExpression, graph);
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
    setPort.getPortRateExpression()
        .setExpressionString(setterFifo.getSourcePort().getPortRateExpression().getExpressionString());
    // 1.1.2 Setting the BRV value
    Long brvSetter = this.brv.get(setterFifo.getSourcePort().getContainingActor());
    if (brvSetter == null) {
      brvSetter = (long) 1;
    }
    this.brv.put(setterActor, brvSetter);
    // 1.2 Now we do the getter actor
    final DelayActor getterActor = PiMMUserFactory.instance.createDelayActor();
    getterActor.setName(delayActor.getName() + "_getter");
    final DataOutputPort getPort = PiMMUserFactory.instance.createDataOutputPort();
    getPort.setName(delayActor.getDataOutputPort().getName());
    getPort.getPortRateExpression().setExpressionString(delayExpression);
    getterActor.getDataOutputPorts().add(getPort);
    // 1.2.1 Setting the new source port of the getter FIFO
    final Fifo getterFifo = delayActor.getDataOutputPort().getFifo();
    getterFifo.setSourcePort(getPort);
    getPort.getPortRateExpression()
        .setExpressionString(getterFifo.getTargetPort().getPortRateExpression().getExpressionString());
    // 1.2.2 Setting the BRV value
    Long brvGetter = this.brv.get(getterFifo.getTargetPort().getContainingActor());
    if (brvGetter == null) {
      brvGetter = (long) 1;
    }
    this.brv.put(getterActor, brvGetter);
    // 2 We remove the old actor and add the new ones
    graph.removeActor((AbstractActor) delayActor);
    graph.addActor(setterActor);
    graph.addActor(getterActor);
  }

  private void addInitActorAsSetter(final Fifo fifo, final DelayActor delayActor, final String delayExpression,
      final PiGraph graph) {
    final InitActor init = PiMMUserFactory.instance.createInitActor();
    final DataInputPort targetPort = fifo.getTargetPort();
    init.getDataOutputPort().setName(targetPort.getName());
    init.getDataOutputPort().getPortRateExpression().setExpressionString(delayExpression);
    // Set the proper init name
    final String initName = this.graphPrefix + targetPort.getContainingActor().getName() + "_init_"
        + targetPort.getName();
    init.setName(initName);
    // Set the persistence level of the delay
    init.setLevel(fifo.getDelay().getLevel());
    // Create the FIFO and connect it
    final Fifo initFifo = PiMMUserFactory.instance.createFifo();
    initFifo.setType(fifo.getType());
    initFifo.setTargetPort(delayActor.getDataInputPort());
    initFifo.setSourcePort(init.getDataOutputPort());
    graph.addActor(init);
    graph.addFifo(initFifo);
    this.brv.put(init, (long) 1);
  }

  private void addEndActorAsGetter(final Fifo fifo, final DelayActor delayActor, final String delayExpression,
      final PiGraph graph) {
    final EndActor end = PiMMUserFactory.instance.createEndActor();
    final DataOutputPort sourcePort = fifo.getSourcePort();
    end.getDataInputPort().setName(sourcePort.getName());
    end.getDataInputPort().getPortRateExpression().setExpressionString(delayExpression);
    // Set the proper end name
    final String endName = this.graphPrefix + sourcePort.getContainingActor().getName() + "_end_"
        + sourcePort.getName();
    end.setName(endName);
    // Set the persistence level of the delay
    end.setLevel(fifo.getDelay().getLevel());
    // Set the END_REFERENCE
    // TODO: handle asymetric configuration
    end.setEndReference(fifo.getDelay().getSetterActor().getName());
    // Create the FIFO and connect it
    final Fifo endFifo = PiMMUserFactory.instance.createFifo();
    endFifo.setType(fifo.getType());
    endFifo.setTargetPort(end.getDataInputPort());
    endFifo.setSourcePort(delayActor.getDataOutputPort());
    graph.addActor(end);
    graph.addFifo(endFifo);
    this.brv.put(end, (long) 1);
  }
}
