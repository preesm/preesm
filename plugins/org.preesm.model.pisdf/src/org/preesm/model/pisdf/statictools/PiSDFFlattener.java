/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2018 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.preesm.commons.IntegerName;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputInterface;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.NonExecutableActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PersistenceLevel;
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
public class PiSDFFlattener extends PiMMSwitch<Boolean> {

  /** The result. */
  // Flat graph created from the outer graph
  private final PiGraph result;

  /** Basic repetition vector of the graph */
  private final Map<AbstractVertex, Long> brv;

  /** Map from original PiMM vertices to generated DAG vertices */
  private final Map<AbstractActor, AbstractActor> actor2actor = new LinkedHashMap<>();

  /** Current Single-Rate Graph name */
  private String graphName;

  /** Current graph prefix */
  private String graphPrefix;

  private final Map<Parameter, Parameter> param2param = new LinkedHashMap<>();

  /**
   * Instantiates a new abstract StaticPiMM2ASrPiMMVisitor.
   *
   *
   */
  private PiSDFFlattener(Map<AbstractVertex, Long> brv) {
    this.result = PiMMUserFactory.instance.createPiGraph();
    this.brv = brv;
    this.graphName = "";
    this.graphPrefix = "";

  }

  /**
   * Precondition: All.
   *
   * @return the SDFGraph obtained by visiting graph
   */
  public static final PiGraph flatten(final PiGraph graph, boolean performOptim) {
    // Check consistency of the graph (throw exception if recoverable or fatal error)
    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ANALYSIS,
        CheckerErrorLevel.NONE);
    pgcc.check(graph);

    // 0. we copy the graph since the transformation has side effects (especially on delay actors)
    final PiGraph graphCopy = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);
    // 1. First we resolve all parameters.
    // It must be done first because, when removing persistence, local parameters have to be known at upper level
    PiMMHelper.resolveAllParameters(graphCopy);
    // 2. Compute BRV following the chosen method
    Map<AbstractVertex, Long> brv = PiBRV.compute(graphCopy, BRVMethod.LCM);
    PiBRV.printRV(brv);
    // then we remove all actors which will be not fired
    // we do it before the persistence transformation whose new delays may change the BRV
    PiMMHelper.removeNonExecutedActorsAndFifos(graphCopy, brv);
    // 3. We perform the delay transformation step that deals with persistence
    PiMMHelper.removePersistence(graphCopy);
    // recompute brv since new delays added from persistence are not known yet
    brv = PiBRV.compute(graphCopy, BRVMethod.LCM);
    // 4 Check periods with BRV
    PiMMHelper.checkPeriodicity(graphCopy, brv);
    // 5. Now, flatten the graph
    PiSDFFlattener staticPiMM2FlatPiMMVisitor = new PiSDFFlattener(brv);
    staticPiMM2FlatPiMMVisitor.doSwitch(graphCopy);
    PiGraph result = staticPiMM2FlatPiMMVisitor.result;

    if (performOptim) {
      // 6- do some optimization on the graph
      final ForkJoinOptimization forkJoinOptimization = new ForkJoinOptimization();
      forkJoinOptimization.optimize(result);
      final BroadcastRoundBufferOptimization brRbOptimization = new BroadcastRoundBufferOptimization();
      brRbOptimization.optimize(result);
      removeUselessStuffAfterOptim(result);
    }

    // Check consistency of the graph (throw exception if recoverable or fatal error)
    final PiGraphConsistenceChecker pgccResult = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ANALYSIS,
        CheckerErrorLevel.NONE);
    pgccResult.check(result);
    flattenCheck(graphCopy, result);

    return result;
  }

  private static final void removeUselessStuffAfterOptim(final PiGraph graph) {
    // remove loops on DelayActor, including their own redundant DelayActors
    List<AbstractActor> originalDAs = new ArrayList<>(graph.getDelayActors());
    for (AbstractActor da : originalDAs) {
      if (graph.getDelayActors().contains(da)) {
        removeLoopOnDelayActor(graph, (DelayActor) da);
      }
    }
    // remove params that are not connected to anything
    for (final Parameter p : graph.getParameters()) {
      if (p.getOutgoingDependencies().isEmpty()) {
        graph.getVertices().remove(p);
      }
    }
  }

  private static final void removeLoopOnDelayActor(final PiGraph graph, final DelayActor da) {
    AbstractActor setter = da.getSetterActor();
    AbstractActor getter = da.getGetterActor();
    if (setter == null || getter == null || (setter != da && getter != da)) {
      return;
    }
    Fifo f1 = da.getDataOutputPort().getFifo();
    Fifo f2 = da.getDataInputPort().getFifo();
    if (f1 != f2) {
      throw new PreesmRuntimeException(
          "Loop detected on delay actor <" + da.getName() + "> but input and output fifos are different !");
    }
    Delay dExt = f1.getDelay();
    if (dExt == null) {
      graph.removeFifo(f1);
      return;
    }

    DelayActor daExt = dExt.getActor();
    if (daExt == null) {
      throw new PreesmRuntimeException("Delay <" + dExt.getName() + "> without DelayActor.");
    }

    Delay d = da.getLinkedDelay();
    long value = d.getExpression().evaluate();
    dExt.setActor(null);
    if (dExt.getExpression().evaluate() != value) {
      PreesmLogger.getLogger().log(Level.WARNING,
          "A delay actor loop  on <" + da.getName() + "had a wrong delay size, it is removed anyway.");
    }
    graph.removeDelay(dExt);
    graph.removeFifo(f1);

    PersistenceLevel plExt = dExt.getLevel();
    d.setLevel(plExt);
    d.setActor(daExt);
    PiMMHelper.removeActorAndDependencies(graph, da);
  }

  /**
   * @param originalGraph
   *
   */
  private static final void flattenCheck(final PiGraph originalGraph, final PiGraph graph) {
    final List<AbstractActor> actors = graph.getActors();

    if (!originalGraph.getAllActors().isEmpty() && graph.getAllActors().isEmpty()) {
      throw new PreesmRuntimeException("Flatten graph should not be empty if input graph is not empty");
    }

    for (final AbstractActor a : actors) {
      if (a instanceof PiGraph) {
        throw new PreesmRuntimeException("Flatten graph should have no children graph");
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
      // Copy the actor, should we use copyPiGraphWithHistory() instead ?
      final PiGraph copyActor = PiMMUserFactory.instance.copyWithHistory((PiGraph) actor);
      copyActor.setName(graphPrefix + actor.getName());
      // Add the actor to the graph
      this.result.addActor(copyActor);
      // Map the actor for linking latter
      this.actor2actor.put(actor, copyActor);
      instantiateDependencies(actor, copyActor);
      return true;
    } else {
      throw new UnsupportedOperationException();
    }
  }

  /**
   *
   */
  private void instantiateDependencies(final AbstractActor actor, final AbstractActor copyActor) {
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
          final Dependency dep = PiMMUserFactory.instance.createDependency(parameter, port);
          this.result.addDependency(dep);
        }
      } else {
        throw new PreesmRuntimeException();
      }
    }

  }

  @Override
  public Boolean caseDelayActor(final DelayActor actor) {
    return true;
  }

  @Override
  public Boolean caseDataInputInterface(final DataInputInterface actor) {
    // if we are at the top level then we keep the data interfaces
    if (PiMMHelper.isVertexAtTopLevel(actor)) {
      // parameters have been resolved we do not need dependencies
      actor.getConfigInputPorts().clear();
      return caseNonExecutableActor(actor);
    }
    // otherwise, we replace the interface
    final BroadcastActor broadcastIn = PiMMUserFactory.instance.createBroadcastActor();
    broadcastIn.setName(graphPrefix + actor.getName());
    final DataPort dataPort = actor.getDataPort();
    final Expression interfaceRateExpression = dataPort.getPortRateExpression();
    // Little check on the rate values
    checkInterfaceRate(actor, interfaceRateExpression);
    // Add the input port and the output port
    final DataInputPort in = PiMMUserFactory.instance.createDataInputPort();
    in.setName(actor.getName());
    in.setExpression(interfaceRateExpression.getExpressionAsString());
    broadcastIn.getDataInputPorts().add(in);
    final DataOutputPort out = PiMMUserFactory.instance.createDataOutputPort();
    final Fifo outFifo = actor.getDataOutputPorts().get(0).getFifo();
    final DataInputPort targetPort = outFifo.getTargetPort();
    final Expression targetRateExpression = targetPort.getPortRateExpression();
    out.setName("if_" + actor.getName());
    // Compute the appropriate out rate not to mess with repetition vector values
    final AbstractActor target = targetPort.getContainingActor();
    final long targetRate = targetRateExpression.evaluate() * this.brv.get(target);
    out.setExpression(targetRate);
    broadcastIn.getDataOutputPorts().add(out);
    // Add the actor to the graph
    this.result.addActor(broadcastIn);

    // Map the actor for linking latter
    this.actor2actor.put(actor, broadcastIn);
    instantiateDependencies(actor, broadcastIn);
    return true;
  }

  @Override
  public Boolean caseDataOutputInterface(final DataOutputInterface actor) {
    // if we are at the top level then we keep the data interfaces
    if (PiMMHelper.isVertexAtTopLevel(actor)) {
      // parameters have been resolved we do not need dependencies
      actor.getConfigInputPorts().clear();
      return caseNonExecutableActor(actor);
    }

    final RoundBufferActor roundbufferOut = createRbForOutputInterface(actor);
    // Add the actor to the graph
    this.result.addActor(roundbufferOut);

    // Map the actor for linking latter
    this.actor2actor.put(actor, roundbufferOut);
    instantiateDependencies(actor, roundbufferOut);
    return true;
  }

  @Override
  public Boolean caseConfigOutputInterface(final ConfigOutputInterface actor) {
    // if we are at the top level then we keep the data interfaces
    if (PiMMHelper.isVertexAtTopLevel(actor)) {
      // parameters have been resolved we do not need dependencies
      actor.getConfigInputPorts().clear();
      return caseNonExecutableActor(actor);
    }

    final RoundBufferActor roundbufferOut = createRbForOutputInterface(actor);
    // Add the actor to the graph
    this.result.addActor(roundbufferOut);

    // Map the actor for linking latter
    this.actor2actor.put(actor, roundbufferOut);
    // output dependencies must be carried out later
    return true;
  }

  private RoundBufferActor createRbForOutputInterface(final InterfaceActor actor) {
    if (actor instanceof DataInputInterface) {
      throw new IllegalArgumentException("This method cannot be used wih input interfaces.");
    }
    // otherwise, we replace the interface
    final RoundBufferActor roundbufferOut = PiMMUserFactory.instance.createRoundBufferActor();
    roundbufferOut.setName(graphPrefix + actor.getName());
    final DataPort dataPort = actor.getDataPort();
    final Expression interfaceRateExpression = dataPort.getPortRateExpression();
    // Little check on the rate values
    checkInterfaceRate(actor, interfaceRateExpression);
    // Add the input port and the output port
    final DataOutputPort out = PiMMUserFactory.instance.createDataOutputPort();
    out.setName(actor.getName());
    out.setExpression(interfaceRateExpression.getExpressionAsString());
    roundbufferOut.getDataOutputPorts().add(out);
    final DataInputPort in = PiMMUserFactory.instance.createDataInputPort();
    final Fifo outFifo = actor.getDataInputPorts().get(0).getFifo();
    final DataOutputPort sourcePort = outFifo.getSourcePort();
    final Expression sourceRateExpression = sourcePort.getPortRateExpression();
    in.setName("if_" + actor.getName());
    // Compute the appropriate in rate not to mess with repetition vector values
    final AbstractActor source = sourcePort.getContainingActor();
    final long sourceRate = sourceRateExpression.evaluate() * this.brv.get(source);
    in.setExpression(sourceRate);
    roundbufferOut.getDataInputPorts().add(in);

    return roundbufferOut;
  }

  private static void checkInterfaceRate(final InterfaceActor actor, final Expression interfaceRateExpression) {
    final DataPort correspondingPort = actor.getGraphPort();
    final Expression correspondingExpression = correspondingPort.getExpression();
    if (!correspondingExpression.getExpressionAsString().equals(interfaceRateExpression.getExpressionAsString())) {
      throw new PreesmRuntimeException("Interface [" + actor.getName()
          + "] should have same rate as its definition. Graph rate [" + correspondingExpression.getExpressionAsString()
          + "] vs interface rate [" + interfaceRateExpression.getExpressionAsString() + "]");
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
    if ((source instanceof InterfaceActor) && !PiMMHelper.isVertexAtTopLevel(source)) {
      sourceName = "if_" + fifo.getSourcePort().getName();
    } else {
      sourceName = fifo.getSourcePort().getName();
    }

    final DataOutputPort sourcePort = findOutputPort(newSource, sourceName);
    final String targetName;
    // Special case for interface
    if ((target instanceof InterfaceActor) && !PiMMHelper.isVertexAtTopLevel(target)) {
      targetName = "if_" + fifo.getTargetPort().getName();
    } else {
      targetName = fifo.getTargetPort().getName();
    }
    final DataInputPort targetPort = findInputPort(newTarget, targetName);
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

  private DataOutputPort findOutputPort(final AbstractActor actor, final String portName) {
    for (final DataOutputPort dop : actor.getDataOutputPorts()) {
      if (dop.getName().equals(portName)) {
        return dop;
      }
    }
    return null;
  }

  private DataInputPort findInputPort(final AbstractActor actor, final String portName) {
    for (final DataInputPort dip : actor.getDataInputPorts()) {
      if (dip.getName().equals(portName)) {
        return dip;
      }
    }
    return null;
  }

  private Delay copyDelay(final Delay delay) {
    final Delay copy = PiMMUserFactory.instance.createDelay();
    // We do not use {@link PiMMUserFactory#copyWithHistory} here
    // because ports of DelayActor contain an expression proxy
    // (see DelayLinkedExpression in the PiSDF model)

    // Copy Delay properties
    copy.setName(this.graphPrefix + delay.getName());
    copy.setLevel(delay.getLevel());
    copy.setExpression(delay.getExpression().getExpressionAsString());
    // Copy DelayActor properties
    final DelayActor actor = delay.getActor();
    final DelayActor copyActor = copy.getActor();
    copyActor.setName(this.graphPrefix + actor.getName());
    // tracking is useful for FPGA hls codegen where refinements of delay actors are supported
    PreesmCopyTracker.trackCopy(actor, copyActor);
    final Refinement ref = actor.getRefinement();
    if (ref != null) {
      final Refinement copyRef = PiMMUserFactory.instance.copy(ref);
      copyActor.setRefinement(copyRef);
    }

    final DataInputPort setterPort = actor.getDataInputPort();
    final DataInputPort copySetterPort = copyActor.getDataInputPort();
    copySetterPort.setName(setterPort.getName());

    final DataOutputPort getterPort = actor.getDataOutputPort();
    final DataOutputPort copyGetterPort = copyActor.getDataOutputPort();
    copyGetterPort.setName(getterPort.getName());

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

  @Override
  public Boolean caseDataInputPort(final DataInputPort dip) {
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
  public Boolean caseNonExecutableActor(NonExecutableActor actor) {
    // Copy the actor
    final NonExecutableActor copyActor = PiMMUserFactory.instance.copyWithHistory(actor);
    copyActor.setName(graphPrefix + actor.getName());

    // Add the actor to the graph
    this.result.addActor(copyActor);

    // Map the actor for linking latter
    this.actor2actor.put(actor, copyActor);
    return true;
  }

  @Override
  public Boolean caseParameter(final Parameter param) {
    // make sure config input interfaces are made into Parameter (since their expressions have been evaluated)
    final Parameter copy = PiMMUserFactory.instance.createParameter();
    copy.setExpression(param.getValueExpression().evaluate());
    copy.setName(graphPrefix + param.getName());
    this.result.addParameter(copy);
    this.param2param.put(param, copy);
    return true;
  }

  @Override
  public Boolean caseExecutableActor(final ExecutableActor actor) {
    // Copy the actor
    final ExecutableActor copyActor = PiMMUserFactory.instance.copyWithHistory(actor);
    copyActor.setName(graphPrefix + actor.getName());

    // Add the actor to the graph
    this.result.addActor(copyActor);

    // Map the actor for linking latter
    this.actor2actor.put(actor, copyActor);

    // Set the properties
    instantiateDependencies(actor, copyActor);
    return true;
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
    final Long graphRV = PiMMHelper.getHierarchichalRV(graph, this.brv);
    final long inRate = interfaceRateExpression.evaluate() * graphRV;
    in.setExpression(inRate);
    in.setAnnotation(PortMemoryAnnotation.READ_ONLY);
    fork.getDataInputPorts().add(in);
    // Set the DataOutputPorts and connect them
    IntegerName iN = new IntegerName(graphRV - 1);
    for (long i = 0; i < graphRV; ++i) {
      final String graphPrexix = this.graphPrefix + iN.toString(i) + "_";
      final String actorName = graphPrexix + actor.getName();
      // 1. Retrieve the BroadcastActor
      final BroadcastActor currentBR = (BroadcastActor) this.result.lookupVertex(actorName);
      // 2. Create the output port
      final DataOutputPort out = PiMMUserFactory.instance.createDataOutputPort();
      out.setName(actor.getName() + "_" + iN.toString(i));
      out.setExpression(interfaceRateExpression.getExpressionAsString());
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
    final Long graphRV = PiMMHelper.getHierarchichalRV(graph, this.brv);
    final long outRate = interfaceRateExpression.evaluate() * graphRV;
    out.setExpression(outRate);
    out.setAnnotation(PortMemoryAnnotation.WRITE_ONLY);
    join.getDataOutputPorts().add(out);
    // Set the DataOutputPorts and connect them
    IntegerName iN = new IntegerName(graphRV - 1);
    for (long i = 0; i < graphRV; ++i) {
      final String graphPrexix = this.graphPrefix + iN.toString(i) + "_";
      final String actorName = graphPrexix + actor.getName();
      // 1. Retrieve the BroadcastActor
      final RoundBufferActor currentRB = (RoundBufferActor) this.result.lookupVertex(actorName);
      // 2. Create the output port
      final DataInputPort in = PiMMUserFactory.instance.createDataInputPort();
      in.setName(actor.getName() + "_" + iN.toString(i));
      in.setExpression(interfaceRateExpression.getExpressionAsString());
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
    if (graph.getContainingPiGraph() == null) {
      result.setName(graph.getName() + "_flat");
      result.setUrl(graph.getUrl());
      result.setExpression(graph.getPeriod().evaluate());
      PreesmCopyTracker.trackCopy(graph, this.result);
    }

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

    for (final Parameter p : graph.getParameters()) {
      doSwitch(p);
    }

    for (final Fifo f : graph.getFifosWithDelay()) {
      final Delay delay = f.getDelay();
      if (delay.getLevel() == PersistenceLevel.PERMANENT || delay.getLevel() == PersistenceLevel.LOCAL) {
        containsPersistent = true;
      } else if (delay.getLevel() == PersistenceLevel.NONE) {
        containsNonPersistent = true;
      }
    }

    if (containsNonPersistent && containsPersistent) {
      throw new PreesmRuntimeException("We have detected persistent and non-persistent delays in graph ["
          + graph.getName() + "]. This is not supported by the flattening transformation for now.");
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
      doSwitch(actor);
    }
    for (final Fifo f : graph.getFifosWithDelay()) {
      doSwitch(f);
    }
    for (final Fifo f : graph.getFifosWithoutDelay()) {
      doSwitch(f);
    }
  }

  private void quasiSRTransformation(final PiGraph graph) {
    final String backupPrefix = this.graphPrefix;
    // We need to get the repetition vector of the graph
    final long graphRV = PiMMHelper.getHierarchichalRV(graph, this.brv);
    IntegerName iN = new IntegerName(graphRV - 1);
    for (long i = 0; i < graphRV; ++i) {
      if (!backupPrefix.isEmpty()) {
        this.graphPrefix = backupPrefix + iN.toString(i) + "_";
      }
      flatteningTransformation(graph);
    }
    this.graphPrefix = backupPrefix;
    if (graph.getContainingPiGraph() != null) {
      // if we are not the parent graph, we need to deal with input / output interfaces
      for (final DataInputInterface dii : graph.getDataInputInterfaces()) {
        forkInputInterface(dii, graph);
      }
      for (final DataOutputInterface doi : graph.getDataOutputInterfaces()) {
        joinOutputInterface(doi, graph);
      }
    } else if (graph.getContainingPiGraph() == null && graphRV != 1L) {
      throw new PreesmRuntimeException(
          "Inconsistent state reached during flattenning: the top graph has a repetition vector (RV) > 1, "
              + "which cannot be handled with non persistent NONE delays. RV = " + graphRV);
    }

    this.graphPrefix = backupPrefix;
  }

}
