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
import java.util.LinkedHashMap;
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
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.JoinActor;
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
  private final Map<String, ArrayList<AbstractVertex>> actor2SRActors = new LinkedHashMap<>();

  /** Map of all DataInputInterface to corresponding vertices */
  private final Map<String, ArrayList<AbstractVertex>> inPort2SRActors = new LinkedHashMap<>();

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

    ArrayList<AbstractVertex> sourceSet = null;

    // 2. Populate the source set linked to this FIFO
    if (sourceActor instanceof InterfaceActor) {
      final DataInputPort correspondingPort = (DataInputPort) sourceActor.getContainingPiGraph()
          .lookupGraphDataPortForInterfaceActor((InterfaceActor) sourceActor);
      // 2.1 Create the entry in the map
      final String key = originalGraphName + "_" + correspondingPort.getName();
      if (!inPort2SRActors.containsKey(key)) {
        inPort2SRActors.put(key, new ArrayList<>());
      }

      // 2.2 Now check if we need a roundbuffer
      long prod = Long.parseLong(correspondingPort.getPortRateExpression().getExpressionString());
      long cons = Long.parseLong(targetPort.getPortRateExpression().getExpressionString());
      long sinkRV = this.brv.get(sinkActor);
      if (prod != (cons * sinkRV)) {
        final RoundBufferActor interfaceRB = addRoundBufferIn(fifo, sourceActor, correspondingPort, cons, sinkRV);
        sourceSet = new ArrayList<>();
        sourceSet.add(interfaceRB);
        inPort2SRActors.get(key).add(interfaceRB);
      }
    } else if (!this.actor2SRActors.containsKey(this.graphName + sourceActor.getName())) {
      populateSingleRatePiMMActor(sourceActor);
    }

    // 3. Populate the sink set linked to this FIFO
    // If sink actor is hierarchical we should go inside and do partial transformation
    if (!this.actor2SRActors.containsKey(this.graphName + sinkActor.getName())) {
      populateSingleRatePiMMActor(sinkActor);
    }

    // 4. Do the Single-Rate connections
    final PiMMSRVerticesLinker srVerticesLinker = new PiMMSRVerticesLinker(fifo, this.result, this.scenario);

    if (sourceSet == null) {
      sourceSet = this.actor2SRActors.get(this.graphName + sourceActor.getName());
    }
    final ArrayList<AbstractVertex> sinkSet = this.actor2SRActors.get(this.graphName + sinkActor.getName());

    if (sourceSet == null || sinkSet == null) {
      return true;
    }

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
   * Add a roundbuffer in the place of a DataInputInterface
   * 
   * @param fifo
   *          the fifo
   * @param sourceActor
   *          the source interface
   * @param correspondingPort
   *          the corresponding data port on the hierarchical actor
   * @param cons
   *          consumption of the actor connected to the interface
   * @param sinkRV
   *          repetition vector value of the sink vertex
   * @return
   */
  private RoundBufferActor addRoundBufferIn(final Fifo fifo, final AbstractActor sourceActor,
      final DataInputPort correspondingPort, long cons, long sinkRV) {
    final RoundBufferActor interfaceRB = this.factory.createRoundBufferActor();
    interfaceRB.setName("RB_" + sourceActor.getName());
    // Copy the DataInputPort to the RoundBufferActor from the hierarchical actor
    final DataInputPort copyPort = (DataInputPort) copier.copy(correspondingPort);
    interfaceRB.getDataInputPorts().add(copyPort);
    copyPort.setName("rb_" + correspondingPort.getName());
    // Add a DataOutputPort to the RoundBufferActor
    final DataOutputPort dop = this.factory.createDataOutputPort();
    dop.setName("RB_" + sourceActor.getName() + Long.toString(cons * sinkRV));
    dop.getPortRateExpression().setExpressionString(Long.toString(cons * sinkRV));
    interfaceRB.getDataOutputPorts().add(dop);
    fifo.setSourcePort(dop);
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
    throw new UnsupportedOperationException();
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
      return;
    }

    // Ignore DataInterfaces also
    if (actor instanceof InterfaceActor) {
      return;
    }

    if (actor instanceof PiGraph) {
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
      originalGraphName = g.getName();
      for (int i = 0; i < this.brv.get(g); ++i) {
        final String prefixGraphName = backupGraphName.isEmpty() ? "" : backupGraphName + "_";
        this.graphName = prefixGraphName + g.getName() + "_" + Long.toString(i);
        doSwitch(g);
      }
      // final StaticPiMM2ASrPiMMVisitor visitorPiMM2ASRPiMM = new StaticPiMM2ASrPiMMVisitor(
      // PiMMUserFactory.instance.createPiGraph(), this.brv, this.scenario);
      // visitorPiMM2ASRPiMM.doSwitch(g);
      // final PiGraph currentResult = visitorPiMM2ASRPiMM.getResult();
      // for (int i = 0; i < this.brv.get(g); ++i) {
      // final String prefix = g.getName() + "_" + Long.toString(i);
      // final PiGraph copy = (PiGraph) copier.copy(currentResult);
      // copy.getVertices().stream().map(AbstractActor.class::cast).forEach(v -> v.setName(prefix + v.getName()));
      // copy.getVertices().stream().map(AbstractActor.class::cast)
      // .forEach(v -> graph.addActor((AbstractActor) copier.copy(v)));
      // }
    }
    // Restore the graph name
    this.graphName = backupGraphName;

    // Perform the Multi-Rate to Single-Rate transformation based on the FIFOs
    for (final Fifo f : graph.getFifos()) {
      doSwitch(f);
    }

    return true;
  }
}
