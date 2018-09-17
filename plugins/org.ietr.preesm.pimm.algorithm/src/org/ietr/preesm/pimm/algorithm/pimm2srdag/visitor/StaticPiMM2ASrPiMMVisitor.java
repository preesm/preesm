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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.ecore.util.EcoreUtil;
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
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;
import org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch;
import org.ietr.preesm.pimm.algorithm.helper.PiMMHelperException;

/**
 * @author farresti
 *
 */
public class StaticPiMM2ASrPiMMVisitor extends PiMMSwitch<Boolean> {
  /** Property name for property TARGET_VERTEX. */
  public static final String PARENT_DAG_VERTEX = "parent_dag_vertex";

  /** The result. */
  // SRDAG graph created from the outer graph
  private final PiGraph result;

  // The factories
  private final PiMMUserFactory factory;

  /** Basic repetition vector of the graph */
  private final Map<AbstractVertex, Long> brv;

  /** The scenario. */
  private final PreesmScenario scenario;

  /** Ecore copier utility */
  private static final EcoreUtil.Copier copier = new EcoreUtil.Copier(true);

  /** Map from original PiMM vertices to generated DAG vertices */
  private final HashMap<String, ArrayList<AbstractVertex>> actor2SRActors = new HashMap<>();

  /** Map of all DataInputInterface to corresponding vertices */
  private final HashMap<String, ArrayList<AbstractVertex>> inPort2SRActors = new HashMap<>();

  /** Map of all DataOutputInterface to corresponding vertices */
  private final HashMap<String, ArrayList<AbstractVertex>> outPort2SRActors = new HashMap<>();

  /** List of the constrants operator ID of the current actor */
  ArrayList<String> currentOperatorIDs;

  /** PiMM Graph name */
  String originalGraphName;

  /** Current Single-Rate Graph name */
  String graphName;

  /** Current actor name */
  String currentActorName;

  /** Current Fifo */
  Fifo currentFifo;

  /**
   * Instantiates a new abstract pi MM 2 SR-DAG visitor.
   *
   * @param acyclicSRPiMM
   *          the Acyclic Single-Rate PiMM graph to be filled
   * @param brv
   *          the Basic Repetition Vector Map
   * @param scenario
   *          the scenario
   * 
   */
  public StaticPiMM2ASrPiMMVisitor(final PiGraph acyclicSRPiMM, final Map<AbstractVertex, Long> brv,
      final PreesmScenario scenario) {
    this.result = acyclicSRPiMM;
    this.brv = brv;
    this.factory = PiMMUserFactory.instance;
    this.scenario = scenario;
    this.graphName = "";
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
  public Boolean caseBroadcastActor(final BroadcastActor actor) {
    // Copy the BroadCast actor
    final BroadcastActor copyActor = (BroadcastActor) copier.copy(actor);

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
    // 0. Set current Fifo
    this.currentFifo = fifo;
    final DataOutputPort sourcePort = fifo.getSourcePort();
    final DataInputPort targetPort = fifo.getTargetPort();

    // 1. Retrieve Source / Sink actors of the FIFO
    final AbstractActor sourceActor = sourcePort.getContainingActor();
    final AbstractActor sinkActor = targetPort.getContainingActor();

    // 2. Populate the source set linked to this FIFO
    final ArrayList<
        AbstractVertex> sourceSet = getSourceSetForSRLinker(fifo, sourcePort, targetPort, sourceActor, sinkActor);
    if (sourceSet == null) {
      fifo.setSourcePort(sourcePort);
      return true;
    }

    // 3. Populate the sink set linked to this FIFO
    final ArrayList<
        AbstractVertex> sinkSet = getSinkSetForSRLinker(fifo, sourcePort, targetPort, sourceActor, sinkActor);

    // If sinkSet / sourceSet is null, then we did not need a RoundBuffer / Broadcast and thus processing of connecting
    // this FIFO will be done later
    if (sinkSet == null) {
      // In the case of Interfaces we might have disconnected the FIFO so let's reconnect it
      fifo.setSourcePort(sourcePort);
      fifo.setTargetPort(targetPort);
      return true;
    }

    // 4. Do the Single-Rate connections
    final PiMMSRVerticesLinker srVerticesLinker = new PiMMSRVerticesLinker(fifo, this.result, this.scenario);
    try {
      srVerticesLinker.execute(this.brv, sourceSet, sinkSet);
    } catch (final PiMMHelperException e) {
      throw new RuntimeException(e.getMessage());
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
  private ArrayList<AbstractVertex> getSourceSetForSRLinker(final Fifo fifo, final DataOutputPort sourcePort,
      final DataInputPort targetPort, final AbstractActor sourceActor, final AbstractActor sinkActor) {
    final ArrayList<AbstractVertex> sourceSet;
    // There 3 mains special cases, source is an interface, source is a graph, source is a delay
    // Otherwise, we fall in "standard" case
    if (sourceActor instanceof InterfaceActor) {
      sourceSet = handleDataInputInterface(targetPort, sourceActor, sinkActor);
    } else if (sourceActor instanceof PiGraph) {
      // We should retrieve the correct source set
      final String key = sourceActor.getName() + "_" + sourcePort.getName();
      sourceSet = this.outPort2SRActors.get(key);
      if (sourceSet == null) {
        throw new RuntimeException("No replacement found for DataOutputPort [" + sourcePort.getName()
            + "] of hierarchical actor [" + sourceActor.getName() + "].");
      }
      // Now we change the "sourcePort" of the FIFO to match the one of the sourceSet
      final AbstractActor firstOfSet = (AbstractActor) sourceSet.get(0);
      final Port lookupPort = firstOfSet.lookupPort(fifo.getSourcePort().getName());
      if (lookupPort == null) {
        fifo.setSourcePort(((AbstractActor) sourceSet.get(0)).getDataOutputPorts().get(0));
      } else {
        fifo.setSourcePort((DataOutputPort) lookupPort);
      }
    } else {
      final String keyActor = this.graphName + sourceActor.getName();
      // if (sourceActor instanceof DelayActor) {
      // this.actor2SRActors.put(key, new ArrayList<>());
      // final long getterRV = sinkActor instanceof InterfaceActor ? 1 : this.brv.get(sinkActor);
      // // First we need to find the end actors
      // for (int i = 0; i < getterRV; ++i) {
      // final String currentEndName = sinkActor.getName() + "_end_" + Integer.toString(i);
      // final DelayActor currentEnd = (DelayActor) this.result.lookupVertex(currentEndName);
      // final DataInputPort inputPort = currentEnd.getDataInputPort();
      // final Fifo currentFifo = inputPort.getIncomingFifo();
      // final AbstractActor source = currentFifo.getSourcePort().getContainingActor();
      // currentFifo.getSourcePort().setName(targetPort.getName());
      // this.actor2SRActors.get(key).add(source);
      // this.result.removeActor(currentEnd);
      // this.result.removeFifo(currentFifo);
      // }
      // } else if (!this.actor2SRActors.containsKey(key)) {
      // populateSingleRatePiMMActor(sourceActor);
      // }
      // sourceSet = this.actor2SRActors.get(key);
      if (!this.actor2SRActors.containsKey(keyActor)) {
        populateSingleRatePiMMActor(sourceActor);
      }
      sourceSet = this.actor2SRActors.get(keyActor);
    }
    return sourceSet;
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
  private ArrayList<AbstractVertex> handleDataInputInterface(final DataInputPort targetPort,
      final AbstractActor sourceActor, final AbstractActor sinkActor) {
    final DataInputPort correspondingPort = (DataInputPort) sourceActor.getContainingPiGraph()
        .lookupGraphDataPortForInterfaceActor((InterfaceActor) sourceActor);
    ArrayList<AbstractVertex> sourceSet = null;

    // 2.1 Create the entry in the map
    final String keyPort = originalGraphName + "_" + correspondingPort.getName();
    if (!inPort2SRActors.containsKey(keyPort)) {
      inPort2SRActors.put(keyPort, new ArrayList<>());
    }

    // 2.2 Now check if we need a BroadcastActor
    long prod = Long.parseLong(correspondingPort.getPortRateExpression().getExpressionString());
    long cons = Long.parseLong(targetPort.getPortRateExpression().getExpressionString());
    long sinkRV = this.brv.get(sinkActor);
    if (prod != (cons * sinkRV)) {
      final BroadcastActor interfaceBR = addBroadCastIn(sourceActor, correspondingPort);
      sourceSet = new ArrayList<>();
      sourceSet.add(interfaceBR);
      inPort2SRActors.get(keyPort).add(interfaceBR);
    } else if (sinkActor instanceof PiGraph) {
      final String subKeyPort = sinkActor.getName() + "_" + targetPort.getName();
      inPort2SRActors.get(keyPort).addAll(inPort2SRActors.remove(subKeyPort));
    } else {
      // 2.4 If we don't, then we can directly link this port to the sinkSet for later processing
      // if (sinkActor instanceof DelayActor) {
      // final String initName = sourceActor.getName() + "_init_0";
      // final DelayActor initActor = (DelayActor) this.result.lookupVertex(initName);
      // final DataOutputPort outputPort = initActor.getDataOutputPort();
      // final Fifo currentFifo = outputPort.getOutgoingFifo();
      // final AbstractActor target = currentFifo.getTargetPort().getContainingActor();
      // currentFifo.getTargetPort().setName(correspondingPort.getName());
      // this.inPort2SRActors.get(key).add(target);
      // this.result.removeActor(initActor);
      // this.result.removeFifo(currentFifo);
      // } else {
      // final String subKey = this.graphName + sinkActor.getName();
      // if (!this.actor2SRActors.containsKey(subKey)) {
      // populateSingleRatePiMMActor(sinkActor);
      // }
      // inPort2SRActors.get(key).addAll(this.actor2SRActors.get(subKey));
      // }
      final String keyActor = this.graphName + sinkActor.getName();
      if (!this.actor2SRActors.containsKey(keyActor)) {
        populateSingleRatePiMMActor(sinkActor);
      }
      inPort2SRActors.get(keyPort).addAll(this.actor2SRActors.get(keyActor));
    }
    return sourceSet;
  }

  /**
   * Add a BroadcastActor in the place of a DataInputInterface
   * 
   * @param sourceActor
   *          the source interface
   * @param correspondingPort
   *          the corresponding data port on the hierarchical actor
   * @return the BroadcastActor
   */
  private BroadcastActor addBroadCastIn(final AbstractActor sourceActor, final DataInputPort correspondingPort) {
    final BroadcastActor interfaceBR = this.factory.createBroadcastActor();
    interfaceBR.setName("BR_" + this.graphName + "_" + sourceActor.getName());
    // Copy the DataInputPort to the RoundBufferActor from the hierarchical actor
    final DataInputPort copyPort = (DataInputPort) copier.copy(correspondingPort);
    copyPort.setAnnotation(PortMemoryAnnotation.READ_ONLY);
    interfaceBR.getDataInputPorts().add(copyPort);
    copyPort.setName("br_" + correspondingPort.getName());
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
  private ArrayList<AbstractVertex> getSinkSetForSRLinker(final Fifo fifo, final DataOutputPort sourcePort,
      final DataInputPort targetPort, final AbstractActor sourceActor, final AbstractActor sinkActor) {
    final ArrayList<AbstractVertex> sinkSet;
    if (sinkActor instanceof InterfaceActor) {
      sinkSet = handleDataOutputInterface(sourcePort, sourceActor, sinkActor);
    } else if (sinkActor instanceof PiGraph) {
      // We should retrieve the correct source set
      final String key = sinkActor.getName() + "_" + targetPort.getName();
      sinkSet = this.inPort2SRActors.remove(key);
      if (sinkSet == null) {
        throw new RuntimeException("No replacement found for DataInputPort [" + targetPort.getName()
            + "] of hierarchical actor [" + sinkActor.getName() + "].");
      }
      // Now we change the "sinkPort" of the FIFO to match the one of the sinkSet
      final AbstractActor firstOfSet = (AbstractActor) sinkSet.get(0);
      final Port lookupPort = firstOfSet.lookupPort(fifo.getTargetPort().getName());
      if (lookupPort == null) {
        fifo.setTargetPort(((AbstractActor) sinkSet.get(0)).getDataInputPorts().get(0));
      } else {
        fifo.setTargetPort((DataInputPort) lookupPort);
      }
    } else {
      final String keyActor = this.graphName + sinkActor.getName();
      // if (sinkActor instanceof DelayActor) {
      // this.actor2SRActors.put(key, new ArrayList<>());
      // final long setterRV = sourceActor instanceof InterfaceActor ? 1 : this.brv.get(sourceActor);
      // // First we need to find the end actors
      // for (int i = 0; i < setterRV; ++i) {
      // final String currentEndName = sourceActor.getName() + "_init_" + Integer.toString(i);
      // final DelayActor currentInit = (DelayActor) this.result.lookupVertex(currentEndName);
      // final DataOutputPort outputPort = currentInit.getDataOutputPort();
      // final Fifo currentFifo = outputPort.getOutgoingFifo();
      // final AbstractActor target = currentFifo.getTargetPort().getContainingActor();
      // this.actor2SRActors.get(key).add(target);
      // this.result.removeActor(currentInit);
      // this.result.removeFifo(currentFifo);
      // }
      // } else if (!this.actor2SRActors.containsKey(key)) {
      // populateSingleRatePiMMActor(sinkActor);
      // }
      // sinkSet = this.actor2SRActors.get(key);
      if (!this.actor2SRActors.containsKey(keyActor)) {
        populateSingleRatePiMMActor(sinkActor);
      }
      sinkSet = this.actor2SRActors.get(keyActor);
    }
    return sinkSet;
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
  private ArrayList<AbstractVertex> handleDataOutputInterface(final DataOutputPort sourcePort,
      final AbstractActor sourceActor, final AbstractActor sinkActor) {
    final DataOutputPort correspondingPort = (DataOutputPort) sinkActor.getContainingPiGraph()
        .lookupGraphDataPortForInterfaceActor((InterfaceActor) sinkActor);
    ArrayList<AbstractVertex> sinkSet = null;

    // 2.1 Create the entry in the map
    final String keyPort = originalGraphName + "_" + correspondingPort.getName();
    if (!outPort2SRActors.containsKey(keyPort)) {
      outPort2SRActors.put(keyPort, new ArrayList<>());
    }

    // 2.2 Now check if we need a RoundBufferActor
    long cons = Long.parseLong(correspondingPort.getPortRateExpression().getExpressionString());
    long prod = Long.parseLong(sourcePort.getPortRateExpression().getExpressionString());
    long sourceRV = this.brv.get(sourceActor);
    if (cons != (prod * sourceRV)) {
      final RoundBufferActor interfaceRB = addRoundBufferOut(sinkActor, correspondingPort);
      sinkSet = new ArrayList<>();
      sinkSet.add(interfaceRB);
      outPort2SRActors.get(keyPort).add(interfaceRB);
    } else if (sourceActor instanceof PiGraph) {
      final String subKeyPort = sourceActor.getName() + "_" + sourcePort.getName();
      outPort2SRActors.get(keyPort).addAll(outPort2SRActors.remove(subKeyPort));
    } else {
      // if (sourceActor instanceof DelayActor) {
      // this.outPort2SRActors.get(key).addAll(this.actor2SRActors.get(this.graphName + sourceActor.getName()));
      // } else {
      // // 2.4 If we don't, then we can directly link this port to the sinkSet for later processing
      // if (!this.actor2SRActors.containsKey(this.graphName + sourceActor.getName())) {
      // populateSingleRatePiMMActor(sourceActor);
      // }
      // outPort2SRActors.get(key).addAll(this.actor2SRActors.get(this.graphName + sourceActor.getName()));
      // }
      final String keyActor = this.graphName + sourceActor.getName();
      if (!this.actor2SRActors.containsKey(keyActor)) {
        populateSingleRatePiMMActor(sourceActor);
      }
      outPort2SRActors.get(keyPort).addAll(this.actor2SRActors.get(keyActor));
    }
    return sinkSet;
  }

  /**
   * Add a RoundBufferActor in the place of a DataOutputInterface
   * 
   * @param sinkActor
   *          the sink interface
   * @param correspondingPort
   *          the corresponding data port on the hierarchical actor
   * @return the RoundBufferActor
   */
  private RoundBufferActor addRoundBufferOut(final AbstractActor sinkActor, final DataOutputPort correspondingPort) {
    final RoundBufferActor interfaceRB = this.factory.createRoundBufferActor();
    interfaceRB.setName("RB_" + this.graphName + "_" + sinkActor.getName());
    // Copy the DataOutputPort to the RoundBufferActor from the hierarchical actor
    final DataOutputPort copyPort = (DataOutputPort) copier.copy(correspondingPort);
    interfaceRB.getDataOutputPorts().add(copyPort);
    copyPort.setName("rb_" + correspondingPort.getName());
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
  public Boolean caseDelayActor(final DelayActor actor) {
    final boolean isSetter = this.currentFifo.getTargetPort().getContainingActor() instanceof DelayActor;
    if (isSetter) {
      this.actor2SRActors.put(this.graphName + actor.getName(),
          generateSetterList(this.currentFifo.getSourcePort().getContainingActor()));
    } else {
      this.actor2SRActors.put(this.graphName + actor.getName(),
          generateGetterList(this.currentFifo.getTargetPort().getContainingActor()));
    }
    return true;
  }

  private ArrayList<AbstractVertex> generateSetterList(final AbstractActor sourceActor) {
    final ArrayList<AbstractVertex> setterList = new ArrayList<>();
    final long setterRV = sourceActor instanceof InterfaceActor ? 1 : this.brv.get(sourceActor);
    // // First we need to find the end actors
    for (int i = 0; i < setterRV; ++i) {
      final String currentInitName = sourceActor.getName() + "_init_" + Integer.toString(i);
      final InitActor currentInit = (InitActor) this.result.lookupVertex(currentInitName);
      final DataOutputPort outputPort = currentInit.getDataOutputPort();
      final Fifo fifo = outputPort.getOutgoingFifo();
      final AbstractActor target = fifo.getTargetPort().getContainingActor();
      setterList.add(target);
      this.result.removeActor(currentInit);
      this.result.removeFifo(fifo);
    }
    return setterList;
  }

  private ArrayList<AbstractVertex> generateGetterList(final AbstractActor targetActor) {
    final ArrayList<AbstractVertex> getterList = new ArrayList<>();
    final long getterRV = targetActor instanceof InterfaceActor ? 1 : this.brv.get(targetActor);
    // // First we need to find the end actors
    for (int i = 0; i < getterRV; ++i) {
      final String currentEndName = targetActor.getName() + "_end_" + Integer.toString(i);
      final EndActor currentEnd = (EndActor) this.result.lookupVertex(currentEndName);
      final DataInputPort inputPort = currentEnd.getDataInputPort();
      final Fifo fifo = inputPort.getIncomingFifo();
      final AbstractActor source = fifo.getSourcePort().getContainingActor();
      getterList.add(source);
      this.result.removeActor(currentEnd);
      this.result.removeFifo(fifo);
    }
    return getterList;
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
    // Ignore delay actors, after this transformation, they no longer serve any purpose.
    if (actor instanceof DelayActor) {
      // String newKey = key;
      // // Check if the DelayActor need to be split
      // final DelayActor delayActor = (DelayActor) actor;
      // final boolean isSetter = this.currentFifo.getTargetPort().getContainingActor() instanceof DelayActor;
      // if (delayActor.getGetterActor() != null && delayActor.getSetterActor() != null) {
      // // We split the current actor in two for more convenience
      // // Let start by the setterActor
      // final DelayActor setterActor = PiMMUserFactory.instance.createDelayActor();
      // setterActor.setName(actor.getName() + "_setter");
      // final DataInputPort setPort = PiMMUserFactory.instance.createDataInputPort();
      // setPort.setName("set");
      // setterActor.getDataInputPorts().add(setPort);
      // // Now we do the getter actor
      // final DelayActor getterActor = PiMMUserFactory.instance.createDelayActor();
      // getterActor.setName(actor.getName() + "_getter");
      // final DataOutputPort getPort = PiMMUserFactory.instance.createDataOutputPort();
      // getPort.setName("get");
      // getterActor.getDataOutputPorts().add(getPort);
      // if (isSetter) {
      // this.currentFifo.setTargetPort(setPort);
      // final Fifo getterFifo = delayActor.getDataOutputPort().getOutgoingFifo();
      // getterFifo.setSourcePort(getPort);
      // this.actor2SRActors.put(this.graphName + setterActor.getName(),
      // generateSetterList(this.currentFifo.getSourcePort().getContainingActor()));
      // this.actor2SRActors.put(this.graphName + getterActor.getName(),
      // generateGetterList(getterFifo.getTargetPort().getContainingActor()));
      // // Updating the corresponding key
      // newKey = this.graphName + setterActor.getName();
      // } else {
      // this.currentFifo.setSourcePort(getPort);
      // final Fifo setterFifo = delayActor.getDataInputPort().getIncomingFifo();
      // setterFifo.setTargetPort(setPort);
      // this.actor2SRActors.put(this.graphName + setterActor.getName(),
      // generateSetterList(setterFifo.getSourcePort().getContainingActor()));
      // this.actor2SRActors.put(this.graphName + getterActor.getName(),
      // generateGetterList(this.currentFifo.getTargetPort().getContainingActor()));
      // // Updating the corresponding key
      // newKey = this.graphName + getterActor.getName();
      // }
      // final PiGraph graph = actor.getContainingPiGraph();
      // graph.removeActor((AbstractActor) actor);
      // graph.addActor(setterActor);
      // graph.addActor(getterActor);
      // }
      // return newKey;
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

    // We treat hierarchical graphs as normal actors
    // This populate the DAG with the right amount of hierarchical instances w.r.t the BRV value
    final String prefixGraphName = this.graphName.isEmpty() ? "" : this.graphName + "_";
    if (actorNBRepeat > 1) {
      for (long i = 0; i < actorNBRepeat; ++i) {
        // Setting the correct name
        this.currentActorName = prefixGraphName + actor.getName() + "_" + Long.toString(i);
        caseAbstractActor((AbstractActor) actor);
      }
    } else {
      // In this case we don't need to add number to names
      this.currentActorName = prefixGraphName + actor.getName();
      caseAbstractActor((AbstractActor) actor);
    }
  }

  @Override
  public Boolean casePiGraph(final PiGraph graph) {
    // If there are no actors in the graph we leave
    if (graph.getActors().isEmpty()) {
      throw new UnsupportedOperationException(
          "Can not convert an empty graph. Check the refinement for [" + graph.getVertexPath() + "].");
    }
    // Save the current graph name
    final String backupGraphName = this.graphName;
    // Go check hierarchical graphs
    // The hierarchy gets flatten as we go deeper
    for (final PiGraph g : graph.getChildrenGraphs()) {
      // We need to split all delay actors before going in every iteration
      for (final Fifo f : g.getFifosWithDelay()) {
        splitDelayActors(f);
      }
      for (int i = 0; i < this.brv.get(g); ++i) {
        final String prefixGraphName = backupGraphName.isEmpty() ? "" : backupGraphName + "_";
        this.graphName = prefixGraphName + g.getName() + "_" + Long.toString(i);
        doSwitch(g);
      }
    }
    // Restore the graph name
    this.graphName = backupGraphName;

    // Perform the Multi-Rate to Single-Rate transformation based on the FIFOs
    originalGraphName = graph.getName();
    for (final Fifo f : graph.getFifosWithDelay()) {
      doSwitch(f);
    }
    for (final Fifo f : graph.getFifosWithoutDelay()) {
      doSwitch(f);
    }
    return true;
  }

  private void splitDelayActors(final Fifo fifo) {
    final DelayActor delayActor = fifo.getDelay().getActor();
    final String delayExpression = fifo.getDelay().getExpression().getExpressionString();
    final PiGraph graph = delayActor.getContainingPiGraph();
    // 0. Check if the DelayActor need to add Init / End
    if (delayActor.getSetterActor() == null) {
      final InitActor init = PiMMUserFactory.instance.createInitActor();
      init.getDataOutputPort().setName(fifo.getTargetPort().getName());
      init.getDataOutputPort().getPortRateExpression().setExpressionString(delayExpression);
      final Fifo initFifo = PiMMUserFactory.instance.createFifo();
      initFifo.setTargetPort(delayActor.getDataInputPort());
      initFifo.setSourcePort(init.getDataOutputPort());
      graph.addActor(init);
      graph.addFifo(initFifo);
      this.brv.put(init, (long) 1);
    }
    if (delayActor.getGetterActor() == null) {
      final EndActor end = PiMMUserFactory.instance.createEndActor();
      end.getDataInputPort().setName(fifo.getSourcePort().getName());
      end.getDataInputPort().getPortRateExpression().setExpressionString(delayExpression);
      final Fifo endFifo = PiMMUserFactory.instance.createFifo();
      endFifo.setTargetPort(end.getDataInputPort());
      endFifo.setSourcePort(delayActor.getDataOutputPort());
      graph.addActor(end);
      graph.addFifo(endFifo);
      this.brv.put(end, (long) 1);
    }
    // 1. We split the current actor in two for more convenience
    // 1.1 Let start by the setterActor
    final DelayActor setterActor = PiMMUserFactory.instance.createDelayActor();
    setterActor.setName(delayActor.getName() + "_setter");
    final DataInputPort setPort = PiMMUserFactory.instance.createDataInputPort();
    setPort.setName(fifo.getTargetPort().getName());
    setPort.getPortRateExpression().setExpressionString(delayExpression);
    setterActor.getDataInputPorts().add(setPort);
    // 1.1.1 Setting the new target port of the setter FIFO
    final Fifo setterFifo = delayActor.getDataInputPort().getFifo();
    setterFifo.setTargetPort(setPort);
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
    getPort.setName(fifo.getSourcePort().getName());
    getPort.getPortRateExpression().setExpressionString(delayExpression);
    getterActor.getDataOutputPorts().add(getPort);
    // 1.2.1 Setting the new source port of the getter FIFO
    final Fifo getterFifo = delayActor.getDataOutputPort().getFifo();
    getterFifo.setSourcePort(getPort);
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
}
