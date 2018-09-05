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
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.ietr.dftools.algorithm.model.IRefinement;
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

  /** The current SDF refinement. */
  protected IRefinement currentRefinement;

  /** The scenario. */
  private final PreesmScenario scenario;

  /** Ecore copier utility */
  private static final EcoreUtil.Copier copier = new EcoreUtil.Copier(true);

  /** Map from original PiMM vertices to generated DAG vertices */
  private final Map<Fifo, ArrayList<AbstractVertex>> fifoSourceSet = new LinkedHashMap<>();
  private final Map<Fifo, ArrayList<AbstractVertex>> fifoSinkSet   = new LinkedHashMap<>();

  /** Current Graph name */
  String graphName;

  /** Current actor name */
  String currentActorName;

  /** Current Fifo */
  Fifo currentFifo;

  /** Current type of actor */
  boolean isSinkActor;

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
   * Copy all the DataPorts and ConfigInputPorts of an actor to a secondary actor
   * 
   * @param actor
   *          original actor from which the ports are copied
   * @param copyActor
   *          secondary actor to which the ports are copied
   */
  private void copyPortsFromActor(final AbstractActor actor, final AbstractActor copyActor) {
    // Copy the DataInputPorts
    copyActor.getDataInputPorts().addAll(copier.copyAll(actor.getDataInputPorts()));

    // Copy the DataOutputPorts
    copyActor.getDataOutputPorts().addAll(copier.copyAll(actor.getDataOutputPorts()));

    // Copy the ConfigInputPorts
    copyActor.getConfigInputPorts().addAll(copier.copyAll(actor.getConfigInputPorts()));
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
    // Since we are constructing from the P.O.V of Fifo, the actor may already exist
    final AbstractVertex lookupVertex = this.result.lookupVertex(this.currentActorName);
    if (lookupVertex != null) {
      addActorToFifoSet((AbstractActor) lookupVertex);
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
    final Actor srActor = (Actor) copier.copy(actor);

    // Set the name
    srActor.setName(this.currentActorName);

    // Add the actor to the graph
    this.result.addActor(srActor);

    // Add the actor to the FIFO source/sink sets
    addActorToFifoSet(srActor);
    return true;
  }

  @Override
  public Boolean caseBroadcastActor(final BroadcastActor actor) {
    // Copy the BroadCast actor
    final BroadcastActor srBrActor = (BroadcastActor) copier.copy(actor);

    // Set the properties
    srBrActor.setName(this.currentActorName);

    // Add the actor to the graph
    this.result.addActor(srBrActor);

    // Add the actor to the FIFO source/sink sets
    addActorToFifoSet(srBrActor);
    return true;
  }

  @Override
  public Boolean caseJoinActor(final JoinActor actor) {
    // Copy the Join actor
    final JoinActor srJoinActor = (JoinActor) copier.copy(actor);

    // Set the properties
    srJoinActor.setName(this.currentActorName);

    // Add the actor to the graph
    this.result.addActor(srJoinActor);

    // Add the actor to the FIFO source/sink sets
    addActorToFifoSet(srJoinActor);
    return true;
  }

  @Override
  public Boolean caseForkActor(final ForkActor actor) {
    // Copyt the Fork actor
    final ForkActor srForkActor = (ForkActor) copier.copy(actor);

    // Set the properties
    srForkActor.setName(this.currentActorName);

    // Add the actor to the graph
    this.result.addActor(srForkActor);

    // Add the actor to the FIFO source/sink sets
    addActorToFifoSet(srForkActor);
    return true;
  }

  private void addActorToFifoSet(final AbstractActor actor) {
    if (this.isSinkActor) {
      this.fifoSinkSet.get(this.currentFifo).add(actor);
    } else {
      this.fifoSourceSet.get(this.currentFifo).add(actor);
    }
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

    // 1. Create the Map to associate the source / sink vertices to the Fifo
    this.fifoSourceSet.put(fifo, new ArrayList<>());
    this.fifoSinkSet.put(fifo, new ArrayList<>());

    // 2. Retrieve Source / Sink actors of the FIFO
    final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
    final AbstractActor sinkActor = fifo.getTargetPort().getContainingActor();

    // 3. Populate the source set linked to this Fifo
    this.isSinkActor = false;
    populateSingleRatePiMMActor(sourceActor);

    // 4. Populate the sink set linked to this Fifo
    this.isSinkActor = true;
    populateSingleRatePiMMActor(sinkActor);

    // 5. Do the Single-Rate connections
    final PiMMSRVerticesLinker srVerticesLinker = new PiMMSRVerticesLinker(fifo, this.result, this.scenario);
    try {
      srVerticesLinker.execute(this.brv, this.fifoSourceSet.get(fifo), this.fifoSinkSet.get(fifo));
    } catch (final PiMMHelperException e) {
      throw new RuntimeException(e.getMessage());
    }

    return true;
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

    // Perform the Multi-Rate to Single-Rate transformation based on the Fifos
    for (final Fifo f : graph.getFifos()) {
      doSwitch(f);
    }

    // Save the current graph name
    final String backupGraphName = this.graphName;
    // Go check hierarchical graphs
    // The hierarchy gets flatten as we go deeper
    // for (final PiGraph g : graph.getChildrenGraphs()) {
    //
    // }

    // Restore the graph name
    this.graphName = backupGraphName;

    return true;
  }
}
