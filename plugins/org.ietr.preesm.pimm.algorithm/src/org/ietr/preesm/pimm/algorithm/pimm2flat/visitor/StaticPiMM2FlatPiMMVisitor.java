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
package org.ietr.preesm.pimm.algorithm.pimm2flat.visitor;

import java.util.ArrayList;
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
import org.ietr.preesm.experiment.model.pimm.PersistenceLevel;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;
import org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch;

/**
 * @author farresti
 *
 */
public class StaticPiMM2FlatPiMMVisitor extends PiMMSwitch<Boolean> {
  /** The result. */
  // Flat graph created from the outer graph
  private final PiGraph result;

  /** Basic repetition vector of the graph */
  private final Map<AbstractVertex, Long> brv;

  /** The scenario. */
  private final PreesmScenario scenario;

  /** Ecore copier utility */
  private static final EcoreUtil.Copier copier = new EcoreUtil.Copier(true);

  /** Map from original PiMM vertices to generated DAG vertices */
  private final Map<AbstractActor, AbstractActor> actor2actor = new LinkedHashMap<>();

  /** PiMM Graph name */
  String originalGraphName;

  /** Current Single-Rate Graph name */
  String graphName;

  /** Current graph prefix */
  String graphPrefix;

  /** Current actor name */
  String currentActorName;

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
  public StaticPiMM2FlatPiMMVisitor(final PiGraph graph, final Map<AbstractVertex, Long> brv,
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

    // Map the actor for linking latter
    this.actor2actor.put(actor, copyActor);

    // Add the scenario constraints
    final List<String> currentOperatorIDs = new ArrayList<>();
    final Set<ConstraintGroup> constraintGroups = this.scenario.getConstraintGroupManager().getConstraintGroups();
    for (final ConstraintGroup cg : constraintGroups) {
      final Set<String> vertexPaths = cg.getVertexPaths();
      final Set<String> operatorIds = cg.getOperatorIds();
      if (vertexPaths.contains(actor.getVertexPath())) {
        currentOperatorIDs.add((String) operatorIds.toArray()[0]);
      }
    }
    final ConstraintGroupManager constraintGroupManager = this.scenario.getConstraintGroupManager();
    currentOperatorIDs.forEach(s -> constraintGroupManager.addConstraint(s, copyActor));
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
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseEndActor(final EndActor actor) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDelayActor(final DelayActor actor) {
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
    final BroadcastActor broadcastIn = PiMMUserFactory.instance.createBroadcastActor();
    final DataPort dataPort = actor.getDataPort();
    final Expression interfaceRateExpression = dataPort.getPortRateExpression();
    // Little check on the rate values
    checkInterfaceRate(actor, interfaceRateExpression);
    // Add the input port and the output port
    final DataInputPort in = PiMMUserFactory.instance.createDataInputPort();
    in.setName(actor.getName());
    in.getPortRateExpression().setExpressionString(interfaceRateExpression.getExpressionString());
    broadcastIn.getDataInputPorts().add(in);
    final DataOutputPort out = PiMMUserFactory.instance.createDataOutputPort();
    final Fifo outFifo = actor.getDataOutputPorts().get(0).getFifo();
    final DataInputPort targetPort = outFifo.getTargetPort();
    final Expression targetRateExpression = targetPort.getPortRateExpression();
    out.setName("if_" + actor.getName());
    // Compute the appropriate out rate not to mess with repetition vector values
    final AbstractActor target = targetPort.getContainingActor();
    final long targetRate = Long.parseLong(targetRateExpression.getExpressionString()) * this.brv.get(target);
    out.getPortRateExpression().setExpressionString(Long.toString(targetRate));
    broadcastIn.getDataOutputPorts().add(out);
    setPropertiesToCopyActor(actor, broadcastIn);
    return true;
  }

  @Override
  public Boolean caseDataOutputInterface(final DataOutputInterface actor) {
    final RoundBufferActor roundbufferOut = PiMMUserFactory.instance.createRoundBufferActor();
    final DataPort dataPort = actor.getDataPort();
    final Expression interfaceRateExpression = dataPort.getPortRateExpression();
    // Little check on the rate values
    checkInterfaceRate(actor, interfaceRateExpression);
    // Add the input port and the output port
    final DataOutputPort out = PiMMUserFactory.instance.createDataOutputPort();
    out.setName(actor.getName());
    out.getPortRateExpression().setExpressionString(interfaceRateExpression.getExpressionString());
    roundbufferOut.getDataOutputPorts().add(out);
    final DataInputPort in = PiMMUserFactory.instance.createDataInputPort();
    final Fifo outFifo = actor.getDataInputPorts().get(0).getFifo();
    final DataOutputPort sourcePort = outFifo.getSourcePort();
    final Expression sourceRateExpression = sourcePort.getPortRateExpression();
    in.setName("if_" + actor.getName());
    // Compute the appropriate in rate not to mess with repetition vector values
    final AbstractActor source = sourcePort.getContainingActor();
    final long sourceRate = Long.parseLong(sourceRateExpression.getExpressionString()) * this.brv.get(source);
    in.getPortRateExpression().setExpressionString(Long.toString(sourceRate));
    roundbufferOut.getDataInputPorts().add(in);
    setPropertiesToCopyActor(actor, roundbufferOut);
    return true;
  }

  private void checkInterfaceRate(final InterfaceActor actor, final Expression interfaceRateExpression) {
    final PiGraph graph = actor.getContainingPiGraph();
    final DataPort correspondingPort = graph.lookupGraphDataPortForInterfaceActor(actor);
    final Expression correspondingExpression = correspondingPort.getExpression();
    if (!correspondingExpression.getExpressionString().equals(interfaceRateExpression.getExpressionString())) {
      throw new WorkflowException("Interface [" + actor.getName()
          + "] should have same rate as its definition. Graph rate [" + correspondingExpression.getExpressionString()
          + "] vs interface rate [" + interfaceRateExpression.getExpressionString() + "]");
    }
  }

  @Override
  public Boolean caseFifo(final Fifo fifo) {
    // Get current source / target
    final AbstractActor source = fifo.getSourcePort().getContainingActor();
    final AbstractActor target = fifo.getTargetPort().getContainingActor();
    // Create the new FIFO
    final Fifo newFifo = PiMMUserFactory.instance.createFifo();
    // Fetch the mapped actors corresponding to source / target
    final AbstractActor newSource = getActorFromActor(source, fifo.getSourcePort());
    final AbstractActor newTarget = getActorFromActor(target, fifo.getTargetPort());
    // Set the source / target ports of the new FIFO
    final String sourceName;
    // Special case for interfaces
    if (source instanceof InterfaceActor) {
      sourceName = "if_" + fifo.getSourcePort().getName();
    } else {
      sourceName = fifo.getSourcePort().getName();
    }

    final DataOutputPort sourcePort = (DataOutputPort) newSource.lookupPort(sourceName);
    final String targetName;
    // Special case for interface
    if (target instanceof InterfaceActor) {
      targetName = "if_" + fifo.getTargetPort().getName();
    } else {
      targetName = fifo.getTargetPort().getName();
    }
    final DataInputPort targetPort = (DataInputPort) newTarget.lookupPort(targetName);
    newFifo.setSourcePort(sourcePort);
    newFifo.setTargetPort(targetPort);
    // Set other properties
    newFifo.setType(fifo.getType());
    // Copy the Delay
    final Delay delay = fifo.getDelay();
    if (delay != null) {
      final Delay copy = copyDelay(delay);
      newFifo.setDelay(copy);
    }
    // Add the FIFO to the result
    this.result.addFifo(newFifo);
    return true;
  }

  private Delay copyDelay(final Delay delay) {
    final Delay copy = PiMMUserFactory.instance.createDelay();
    // Copy Delay properties
    copy.setName(this.graphPrefix + delay.getName());
    copy.setLevel(delay.getLevel());
    // Copy DelayActor properties
    final DelayActor actor = delay.getActor();
    final DelayActor copyActor = copy.getActor();
    copyActor.setName(this.graphPrefix + actor.getName());
    final DataInputPort setterPort = actor.getDataInputPort();
    final DataInputPort copySetterPort = copyActor.getDataInputPort();
    copySetterPort.setName(setterPort.getName());
    copySetterPort.getExpression().setExpressionString(setterPort.getExpression().getExpressionString());
    final DataOutputPort getterPort = actor.getDataOutputPort();
    final DataOutputPort copyGetterPort = copyActor.getDataOutputPort();
    copyGetterPort.setName(getterPort.getName());
    copyGetterPort.getExpression().setExpressionString(getterPort.getExpression().getExpressionString());
    // Adding the entry in the map
    this.actor2actor.put(actor, copyActor);
    this.result.addDelay(copy);
    return copy;
  }

  private AbstractActor getActorFromActor(final AbstractActor actor, final DataPort port) {
    if (actor instanceof PiGraph) {
      final AbstractVertex ifActor = ((PiGraph) actor).lookupVertex(port.getName());
      return this.actor2actor.get((AbstractActor) ifActor);
    }
    return this.actor2actor.get(actor);
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

  private void forkInputInterface(final DataInputInterface actor, final PiGraph graph) {
    final ForkActor fork = PiMMUserFactory.instance.createForkActor();
    // Set name
    fork.setName("explode_" + this.graphPrefix + actor.getName());
    // Set DataInputPort
    final DataPort dataPort = actor.getDataPort();
    final Expression interfaceRateExpression = dataPort.getPortRateExpression();
    // Add the input port and the output port
    final DataInputPort in = PiMMUserFactory.instance.createDataInputPort();
    in.setName(actor.getName());
    final Long graphRV = getHierarchichalRV(graph);
    final long inRate = Long.parseLong(interfaceRateExpression.getExpressionString()) * graphRV;
    in.getPortRateExpression().setExpressionString(Long.toString(inRate));
    in.setAnnotation(PortMemoryAnnotation.READ_ONLY);
    fork.getDataInputPorts().add(in);
    // Set the DataOutputPorts and connect them
    for (long i = 0; i < graphRV; ++i) {
      final String graphPrexix = this.graphPrefix + Long.toString(i) + "_";
      final String actorName = graphPrexix + actor.getName();
      // 1. Retrieve the BroadcastActor
      final BroadcastActor currentBR = (BroadcastActor) this.result.lookupVertex(actorName);
      // 2. Create the output port
      final DataOutputPort out = PiMMUserFactory.instance.createDataOutputPort();
      out.setName(actor.getName() + "_" + Long.toString(i));
      out.getPortRateExpression().setExpressionString(interfaceRateExpression.getExpressionString());
      out.setAnnotation(PortMemoryAnnotation.WRITE_ONLY);
      fork.getDataOutputPorts().add(out);
      // 3. Connect the port
      final Fifo fifo = PiMMUserFactory.instance.createFifo();
      fifo.setType(actor.getDataPort().getFifo().getType());
      fifo.setSourcePort(out);
      fifo.setTargetPort(currentBR.getDataInputPorts().get(0));
      this.result.addFifo(fifo);
    }
    this.actor2actor.put(actor, fork);
    // Add the actor to the graph
    this.result.addActor(fork);
  }

  private void joinOutputInterface(final DataOutputInterface actor, final PiGraph graph) {
    final JoinActor join = PiMMUserFactory.instance.createJoinActor();
    // Set name
    join.setName("implode_" + this.graphPrefix + actor.getName());
    // Set DataInputPort
    final DataPort dataPort = actor.getDataPort();
    final Expression interfaceRateExpression = dataPort.getPortRateExpression();
    // Add the input port and the output port
    final DataOutputPort out = PiMMUserFactory.instance.createDataOutputPort();
    out.setName(actor.getName());
    final Long graphRV = getHierarchichalRV(graph);
    final long outRate = Long.parseLong(interfaceRateExpression.getExpressionString()) * graphRV;
    out.getPortRateExpression().setExpressionString(Long.toString(outRate));
    out.setAnnotation(PortMemoryAnnotation.WRITE_ONLY);
    join.getDataOutputPorts().add(out);
    // Set the DataOutputPorts and connect them
    for (long i = 0; i < graphRV; ++i) {
      final String graphPrexix = this.graphPrefix + Long.toString(i) + "_";
      final String actorName = graphPrexix + actor.getName();
      // 1. Retrieve the BroadcastActor
      final RoundBufferActor currentRB = (RoundBufferActor) this.result.lookupVertex(actorName);
      // 2. Create the output port
      final DataInputPort in = PiMMUserFactory.instance.createDataInputPort();
      in.setName(actor.getName() + "_" + Long.toString(i));
      in.getPortRateExpression().setExpressionString(interfaceRateExpression.getExpressionString());
      in.setAnnotation(PortMemoryAnnotation.READ_ONLY);
      join.getDataInputPorts().add(in);
      // 3. Connect the port
      final Fifo fifo = PiMMUserFactory.instance.createFifo();
      fifo.setType(actor.getDataPort().getFifo().getType());
      fifo.setSourcePort(currentRB.getDataOutputPorts().get(0));
      fifo.setTargetPort(in);
      this.result.addFifo(fifo);
    }
    this.actor2actor.put(actor, join);
    // Add the actor to the graph
    this.result.addActor(join);
  }

  @Override
  public Boolean casePiGraph(final PiGraph graph) {
    // If there are no actors in the graph we leave
    if (graph.getActors().isEmpty()) {
      throw new UnsupportedOperationException(
          "Can not convert an empty graph. Check the refinement for [" + graph.getVertexPath() + "].");
    }

    // Restore the graph name
    this.graphName = graph.getContainingPiGraph() == null ? "" : graph.getName();
    this.graphName = this.graphPrefix + this.graphName;

    // Set the prefix graph name
    final String currentPrefix = this.graphPrefix;
    this.graphPrefix = this.graphName.isEmpty() ? "" : this.graphName + "_";

    // Check if the graph can be flattened
    boolean containsNonPersistent = false;
    boolean containsPersistent = false;
    for (final Fifo f : graph.getFifosWithDelay()) {
      final Delay delay = f.getDelay();
      if (delay.getLevel() == PersistenceLevel.PERMANENT || delay.getLevel() == PersistenceLevel.LOCAL) {
        containsPersistent = true;
      } else if (delay.getLevel() == PersistenceLevel.NONE) {
        containsNonPersistent = true;
      }
    }
    if (containsNonPersistent && containsPersistent) {
      throw new WorkflowException("We have detected persistent and non-persistent delays in graph [" + graph.getName()
          + "]. This is not supported by the flattening transformation for now.");
    } else if (containsNonPersistent) {
      quasiSRTransformation(graph);
    } else {
      flatteningTransformation(graph);
    }
    this.graphPrefix = currentPrefix;
    return true;
  }

  private void flatteningTransformation(final PiGraph graph) {
    for (final AbstractActor actor : graph.getActors()) {
      this.currentActorName = this.graphPrefix + actor.getName();
      doSwitch(actor);
    }
    for (final Fifo f : graph.getFifos()) {
      doSwitch(f);
    }
  }

  private void quasiSRTransformation(final PiGraph graph) {
    final String backupPrefix = this.graphPrefix;
    // We need to get the repetition vector of the graph
    Long graphRV = getHierarchichalRV(graph);
    for (long i = 0; i < graphRV; ++i) {
      if (!backupPrefix.isEmpty()) {
        this.graphPrefix = backupPrefix + Long.toString(i) + "_";
      }
      flatteningTransformation(graph);
    }
    this.graphPrefix = backupPrefix;
    // Now we need to deal with input / output interfaces
    for (final DataInputInterface dii : graph.getDataInputInterfaces()) {
      forkInputInterface(dii, graph);
    }
    for (final DataOutputInterface doi : graph.getDataOutputInterfaces()) {
      joinOutputInterface(doi, graph);
    }
    this.graphPrefix = backupPrefix;
  }

  private Long getHierarchichalRV(final PiGraph graph) {
    // We need to get the repetition vector of the graph
    final Long graphRV = this.brv.get(graph) == null ? 1 : this.brv.get(graph);
    // We also need to get the total repetition vector of the hierarchy to correctly flatten the hierarchy
    Long graphHierarchicallRV = (long) (1);
    PiGraph containingGraph = graph.getContainingPiGraph();
    while (containingGraph != null) {
      final Long currentGraphRV = this.brv.get(containingGraph) == null ? 1 : this.brv.get(containingGraph);
      graphHierarchicallRV = graphHierarchicallRV * currentGraphRV;
      containingGraph = containingGraph.getContainingPiGraph();
    }
    // We update the value of the graphRV accordingly
    return graphRV * graphHierarchicallRV;
  }

}
