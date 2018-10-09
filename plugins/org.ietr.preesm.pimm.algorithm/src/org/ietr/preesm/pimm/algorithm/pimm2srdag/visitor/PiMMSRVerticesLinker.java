/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
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
import java.util.List;
import java.util.Map;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.factory.PiMMUserFactory;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.BroadcastActor;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.DelayActor;
import org.ietr.preesm.experiment.model.pimm.EndActor;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.InitActor;
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;
import org.ietr.preesm.pimm.algorithm.helper.PiMMHelperException;

/**
 *
 * Link SR DAG vertices
 *
 * @author farresti
 *
 */
public class PiMMSRVerticesLinker {

  /** Property name for property JOIN_VERTEX. */
  public static final String JOIN_VERTEX = "implode_";

  /** Property name for property FORK_VERTEX. */
  public static final String FORK_VERTEX = "explode_";

  // Number of delays
  private final long delays;

  // Source properties
  private final DataOutputPort sourcePort;
  private final long           sourceProduction;

  // Sink properties
  private final DataInputPort sinkPort;
  private final long          sinkConsumption;

  // The FIFO
  private final Fifo   fifo;
  private final String fifoType;

  // Prefix name of the current graph
  final String graphPrefixe;

  /**
   * Retrieve the number of delay tokens contain in a fifo, if any
   *
   * @param fifo
   *          the fifo
   * @return number of delay, 0 if the fifo does not contain any delay
   */
  private static long getNDelays(final Fifo fifo) {
    final Delay delay = fifo.getDelay();
    if (delay == null) {
      return 0;
    }
    // Get the number of delay
    final Expression sizeExpression = fifo.getDelay().getSizeExpression();
    final long nDelays = sizeExpression.evaluate();
    // Sanity check on delay value
    final DataInputPort targetPort = fifo.getTargetPort();
    final Expression portRateExpression = targetPort.getPortRateExpression();
    final long targetRate = portRateExpression.evaluate();
    if (nDelays < 0) {
      throw new WorkflowException("Invalid number of delay on fifo[" + fifo.getId() + "]: " + Long.toString(nDelays));
    } else if (nDelays < targetRate) {
      throw new WorkflowException("Insuffisiant number of delay on fifo[" + fifo.getId() + "]: number of delays: "
          + Long.toString(nDelays) + ", consumption: " + Long.toString(targetRate));
    }
    return nDelays;
  }

  /**
   * Constructor for the SR linker
   *
   * @param fifo
   *          the fifo to link
   * @param dag
   *          the dag in which we operate
   * @param scenario
   *          the scenario. Used to retrieve data size of the fifo
   */
  public PiMMSRVerticesLinker(final Fifo fifo, final PiGraph dag, final PreesmScenario scenario,
      final String graphPrefixe) {
    this.fifo = fifo;
    this.fifoType = fifo.getType();
    this.delays = PiMMSRVerticesLinker.getNDelays(fifo);
    this.graphPrefixe = graphPrefixe;

    // Setting Source properties
    this.sourcePort = fifo.getSourcePort();
    final Expression sourceExpression = this.sourcePort.getPortRateExpression();
    this.sourceProduction = sourceExpression.evaluate();

    // Setting Sink properties
    this.sinkPort = fifo.getTargetPort();
    final Expression sinkExpression = this.sinkPort.getPortRateExpression();
    this.sinkConsumption = sinkExpression.evaluate();

  }

  /**
   *
   */
  public PiMMSRVerticesLinker() {
    this.fifo = null;
    this.fifoType = "";
    this.delays = 0;
    this.graphPrefixe = "";

    // Setting Source properties
    this.sourcePort = null;
    this.sourceProduction = -1;

    // Setting Sink properties
    this.sinkPort = null;
    this.sinkConsumption = -1;
  }

  /**
   * Do the linkage between the dag vertices
   *
   * @param brv
   *          the basic repetition vector map
   *
   * @param vertexSourceSet
   *          vertex source set
   * @param vertexSinkSet
   *          vertex sink set
   *
   *
   * @return true if no error, false else
   */
  public Boolean execute(final Map<AbstractVertex, Long> brv, final List<AbstractVertex> vertexSourceSet,
      final List<AbstractVertex> vertexSinkSet) throws PiMMHelperException {
    // List of source vertices
    final List<SourceConnection> sourceSet = getSourceSet(vertexSourceSet, vertexSinkSet, brv);

    // List of sink vertices
    final List<SinkConnection> sinkSet = getSinkSet(vertexSinkSet, vertexSourceSet, brv);

    // Connect all the source to the sinks
    connectEdges(sourceSet, sinkSet);
    return true;
  }

  /**
   *
   * @param vertexSourceSet
   *          vertex source set
   * @param vertexSinkSet
   *          vertex sink set
   *
   *
   * @return true if no error, false else
   * @throws PiMMHelperException
   *           the execption
   */
  public Boolean execute(final Map<DataOutputPort, AbstractVertex> vertexSourceSet,
      final Map<DataInputPort, AbstractVertex> vertexSinkSet) throws PiMMHelperException {
    // List of source vertices
    final List<SourceConnection> sourceSet = new ArrayList<>();
    vertexSourceSet
        .forEach((k, v) -> sourceSet.add(new SourceConnection(v, k.getPortRateExpression().evaluate(), k.getName())));

    // List of sink vertices
    final List<SinkConnection> sinkSet = new ArrayList<>();
    vertexSinkSet
        .forEach((k, v) -> sinkSet.add(new SinkConnection(v, k.getPortRateExpression().evaluate(), k.getName())));

    // Connect all the source to the sinks
    connectEdges(sourceSet, sinkSet);
    return true;
  }

  /**
   * Connect the sources to the sinks
   *
   * @param sourceSet
   *          set of SR-DAG sources
   * @param sinkSet
   *          set of SR-DAG sinks
   */
  private void connectEdges(final List<SourceConnection> sourceSet, final List<SinkConnection> sinkSet) {
    while (!sinkSet.isEmpty()) {
      if (connectSources2Sink(sourceSet, sinkSet)) {
        sinkSet.remove(0);
      }
      if (sourceSet.isEmpty() || sinkSet.isEmpty()) {
        break;
      }
      if (connectSinks2Source(sinkSet, sourceSet)) {
        sourceSet.remove(0);
      }
    }
  }

  /**
   * Connect sources to current sink
   *
   * @param sourceSet
   *          set of SR-DAG sources
   * @param sink
   *          current sink to connect to
   * @return true if it didn't explode, false else
   */
  private boolean connectSources2Sink(final List<SourceConnection> sourceSet, final List<SinkConnection> sinkSet) {
    final SinkConnection currentSink = sinkSet.get(0);
    long cons = currentSink.getConsumption();
    long prod = sourceSet.get(0).getProduction();

    // Get current sink vertex
    AbstractActor sinkVertex = (AbstractActor) currentSink.getSink();
    final PiGraph graph = sinkVertex.getContainingPiGraph();

    // Check implode condition
    final boolean implode = (cons > prod);

    if (implode) {
      // Creates the implode vertex and connect it to current sink
      final JoinActor joinActor = doImplosion(currentSink, sourceSet.get(0), sinkVertex, graph);
      // The JoinActor becomes the new sink
      sinkVertex = joinActor;
    }

    // Now, let's connect everything we can
    // Array of SourceConnection to remove
    final List<SourceConnection> toRemove = new ArrayList<>();
    // Connect the edges
    for (final SourceConnection src : sourceSet) {
      prod = src.getProduction();
      if ((cons == 0) || (prod > cons)) {
        break;
      }
      toRemove.add(src);
      // 0. Get the current source vertex
      final AbstractActor sourceVertex = (AbstractActor) src.getSource();

      // 1. Connect the source to the sink vertex
      connect(src, currentSink, prod, sourceVertex, sinkVertex);

      // 5. Update the remaining consumption
      cons = cons - prod;
    }
    // Remove all sources that got connected
    toRemove.forEach(sourceSet::remove);
    // Reset the current top sink
    sinkSet.set(0, new SinkConnection(sinkVertex, cons, currentSink.getTargetLabel()));
    // Removing the sink for the set condition
    final boolean shouldRemoveSink = (prod > cons) && (cons != 0);
    return !shouldRemoveSink;
  }

  /**
   * Connect sinks to current source
   *
   * @param sinkSet
   *          set of SR-DAG sinks
   * @param sink
   *          current source to connect from
   * @return true if it didn't implode, false else
   */
  private boolean connectSinks2Source(final List<SinkConnection> sinkSet, final List<SourceConnection> sourceSet) {
    final SourceConnection currentSource = sourceSet.get(0);
    long cons = sinkSet.get(0).getConsumption();
    long prod = currentSource.getProduction();

    // Get current sink vertex
    AbstractActor sourceVertex = (AbstractActor) currentSource.getSource();
    final PiGraph graph = sourceVertex.getContainingPiGraph();

    // Check explode condition
    final boolean explode = (prod > cons);

    if (explode) {
      // Creates the explode vertex and connect it to current source
      final ForkActor forkActor = doExplosion(currentSource, sinkSet.get(0), sourceVertex, graph);
      // The ForkActor becomes the new source
      sourceVertex = forkActor;
    }

    // Now, let's connect everything we can
    // Array of SinkConnection to remove
    final List<SinkConnection> toRemove = new ArrayList<>();
    // Connect the edges
    for (final SinkConnection snk : sinkSet) {
      cons = snk.getConsumption();
      if ((prod == 0) || (cons > prod)) {
        break;
      }
      toRemove.add(snk);
      // 0. Get the current sink vertex
      final AbstractActor sinkVertex = (AbstractActor) snk.getSink();

      // 1. Connect the source to the sink vertex
      connect(currentSource, snk, cons, sourceVertex, sinkVertex);

      // 2. Update the remaining production
      prod = prod - cons;
    }
    // Remove all sources that got connected
    toRemove.forEach(sinkSet::remove);
    // Reset the current top sink
    sourceSet.set(0, new SourceConnection(sourceVertex, prod, currentSource.getSourceLabel()));
    // Removing the source for the set condition
    final boolean shouldRemoveSource = (cons > prod) && (prod != 0);
    return !shouldRemoveSource;
  }

  /**
   * Create a FIFO between the sourceVertex and the sinkVertex using the connection info and with the given rate
   *
   * @param source
   *          SourceConnection information
   * @param sink
   *          SinkConnection information
   * @param rate
   *          The rate to set on the FIFO
   * @param sourceVertex
   *          The source vertex
   * @param sinkVertex
   *          The sink vertex
   */
  private void connect(final SourceConnection source, final SinkConnection sink, final long rate,
      final AbstractActor sourceVertex, final AbstractActor sinkVertex) {
    // 1. Retrieve the source port and if needed, creates it
    final DataOutputPort currentSourcePort = getOrCreateSourcePort(source, rate, sourceVertex);

    // 2. Retrieve the sink port and if needed, creates it
    final DataInputPort currentSinkPort = getOrCreateSinkPort(sink, rate, sinkVertex);

    // 3. Creates the FIFO and connect the ports
    createFifo(sourceVertex.getContainingPiGraph(), currentSourcePort, currentSinkPort);
  }

  /**
   * Return the current source port, creates it if it does not exists
   *
   * @param src
   *          The source connection
   * @param rate
   *          rate to set on the port
   * @param sourceVertex
   *          source vertex
   * @return the data source port
   */
  private DataOutputPort getOrCreateSourcePort(final SourceConnection src, long rate, AbstractActor sourceVertex) {
    DataOutputPort currentSourcePort = (DataOutputPort) lookForPort(sourceVertex.getDataOutputPorts(),
        src.getSourceLabel());
    if (currentSourcePort == null) {
      currentSourcePort = PiMMUserFactory.instance.createDataOutputPort();
      currentSourcePort.setName(src.getSourceLabel());
      currentSourcePort.setExpression(rate);
      sourceVertex.getDataOutputPorts().add(currentSourcePort);
    }
    // Check if the source is JoinActor / RoundBufferActor
    final boolean isSourceJoinOrRoundBuffer = sourceVertex instanceof JoinActor
        || sourceVertex instanceof RoundBufferActor;
    if (isSourceJoinOrRoundBuffer) {
      currentSourcePort.setAnnotation(PortMemoryAnnotation.WRITE_ONLY);
    }
    // Check if the source is ForkActor / BroadcastActor
    final boolean isSourceForkOrBroadcast = sourceVertex instanceof ForkActor || sourceVertex instanceof BroadcastActor;
    if (isSourceForkOrBroadcast) {
      // Update name and source port modifier
      final int index = sourceVertex.getDataOutputPorts().indexOf(currentSourcePort);
      currentSourcePort.setName(currentSourcePort.getName() + "_" + Integer.toString(index));
      currentSourcePort.setAnnotation(PortMemoryAnnotation.WRITE_ONLY);
    }
    return currentSourcePort;
  }

  /**
   * Return the sink port corresponding to the label, creates it if it does not exists
   *
   * @param snk
   *          The sink connection
   * @param rate
   *          rate to set on the port
   * @param sinkVertex
   *          sink vertex
   * @return The sink data port
   */
  private DataInputPort getOrCreateSinkPort(final SinkConnection snk, long rate, final AbstractActor sinkVertex) {
    DataInputPort currentSinkPort = (DataInputPort) lookForPort(sinkVertex.getDataInputPorts(), snk.getTargetLabel());
    if (currentSinkPort == null) {
      currentSinkPort = PiMMUserFactory.instance.createDataInputPort();
      currentSinkPort.setName(snk.getTargetLabel());
      currentSinkPort.setExpression(rate);
      sinkVertex.getDataInputPorts().add(currentSinkPort);
    }
    // Check if the sink is ForkActor / BroadcastActor
    final boolean isSinkForkOrBroadcast = sinkVertex instanceof ForkActor || sinkVertex instanceof BroadcastActor;
    if (isSinkForkOrBroadcast) {
      currentSinkPort.setAnnotation(PortMemoryAnnotation.READ_ONLY);
    }
    // Check if the sink is JoinActor / RoundBufferActor
    final boolean isSinkJoinOrRoundBuffer = sinkVertex instanceof JoinActor || sinkVertex instanceof RoundBufferActor;
    if (isSinkJoinOrRoundBuffer) {
      // Update name and sink port modifier
      final int index = sinkVertex.getDataInputPorts().indexOf(currentSinkPort);
      currentSinkPort.setName(currentSinkPort.getName() + "_" + Integer.toString(index));
      currentSinkPort.setAnnotation(PortMemoryAnnotation.READ_ONLY);
    }
    return currentSinkPort;
  }

  /**
   * Search for a DataPort with name "name" in a given list of DataPort
   *
   * @param ports
   *          List of DataPort
   * @param name
   *          Name of the DataPort to find
   * @return Found DataPort if any, null else
   */
  private DataPort lookForPort(final List<? extends DataPort> ports, final String name) {
    for (final DataPort dp : ports) {
      if (dp.getName().equals(name)) {
        return dp;
      }
    }
    return null;
  }

  /**
   * Creates a new join actor in the context of an implosion
   *
   * @param currentSink
   *          The current sink connection
   * @param sinkVertex
   *          The current sink vertex
   * @param graph
   *          The graph to which the sink belong
   * @return The newly created join vertex
   */
  private JoinActor doImplosion(final SinkConnection currentSink, final SourceConnection source,
      final AbstractActor sinkVertex, final PiGraph graph) {
    final String implodeName = this.graphPrefixe + PiMMSRVerticesLinker.JOIN_VERTEX + sinkVertex.getName() + "_"
        + currentSink.getTargetLabel();
    final JoinActor joinActor = PiMMUserFactory.instance.createJoinActor();
    // Set name property
    joinActor.setName(implodeName);
    // Add a DataOutputPort
    final DataOutputPort joinOutputPort = PiMMUserFactory.instance.createDataOutputPort();
    joinOutputPort.setName(source.getSourceLabel());
    joinOutputPort.setExpression(currentSink.getConsumption());
    joinOutputPort.setAnnotation(PortMemoryAnnotation.WRITE_ONLY);
    joinActor.getDataOutputPorts().add(joinOutputPort);

    // Create the FIFO to connect the Join to the Sink
    final DataInputPort targetPort = getOrCreateSinkPort(currentSink, currentSink.getConsumption(), sinkVertex);

    createFifo(graph, joinOutputPort, targetPort);

    // Add the join actor to the graph
    graph.addActor(joinActor);

    return joinActor;
  }

  /**
   * Creates a new fork actor in the context of an explosion
   *
   * @param currentSource
   *          The current source connection
   * @param sourceVertex
   *          The current source vertex
   * @param graph
   *          The graph to which the sink belong
   * @return The newly created join vertex
   */
  private ForkActor doExplosion(final SourceConnection currentSource, final SinkConnection sink,
      final AbstractActor sourceVertex, final PiGraph graph) {
    final String explodeName = this.graphPrefixe + PiMMSRVerticesLinker.FORK_VERTEX + sourceVertex.getName() + "_"
        + currentSource.getSourceLabel();
    final ForkActor forkActor = PiMMUserFactory.instance.createForkActor();
    // Set name property
    forkActor.setName(explodeName);
    // Add a DataInputPort
    final DataInputPort forkInputPort = PiMMUserFactory.instance.createDataInputPort();
    forkInputPort.setName(sink.getTargetLabel());
    forkInputPort.setExpression(currentSource.getProduction());
    forkInputPort.setAnnotation(PortMemoryAnnotation.READ_ONLY);
    forkActor.getDataInputPorts().add(forkInputPort);

    // Create the FIFO to connect the Join to the Sink
    final DataOutputPort currentSourcePort = getOrCreateSourcePort(currentSource, currentSource.getProduction(),
        sourceVertex);

    createFifo(graph, currentSourcePort, forkInputPort);

    // Add the join actor to the graph
    graph.addActor(forkActor);

    return forkActor;
  }

  /**
   * Creates a FIFO and connects it to the source / target ports. Finally, the FIFO is added to the graph
   *
   * @param graph
   *          the graph in which the FIFO must be added
   * @param sourcePort
   *          the source port
   * @param targetPort
   *          the target port
   */
  private void createFifo(final PiGraph graph, final DataOutputPort sourcePort, final DataInputPort targetPort) {
    final Fifo newFifo = PiMMUserFactory.instance.createFifo();
    if (!this.fifoType.isEmpty()) {
      newFifo.setType(this.fifoType);
    } else {
      // Try to check if one of the port already has a FIFO to get the type
      // Otherwise we infer "char" type by default
      final String type;
      if (sourcePort.getFifo() != null) {
        type = sourcePort.getFifo().getType();
      } else if (targetPort.getFifo() != null) {
        type = targetPort.getFifo().getType();
      } else {
        type = "char";
      }
      newFifo.setType(type);
    }
    newFifo.setSourcePort(sourcePort);
    newFifo.setTargetPort(targetPort);

    // Add the fifo to the graph
    graph.addFifo(newFifo);
  }

  /**
   * Generate a set of SourceConnection
   *
   * @param brv
   *          repetition vector values
   * @param vertexSourceSet
   *          Set of source vertices
   * @return set of SourceConnection
   * @throws PiMMHelperException
   *           the exception
   */
  private List<SourceConnection> getSourceSet(final List<AbstractVertex> vertexSourceSet,
      final List<AbstractVertex> vertexSinkSet, final Map<AbstractVertex, Long> brv) throws PiMMHelperException {
    // Initialize empty source set
    final List<SourceConnection> sourceSet = new ArrayList<>();

    // Deals delays
    if (this.delays != 0) {
      // 0. Check if Delay has setter
      final Delay delay = this.fifo.getDelay();
      // 1. Get the DelayActor
      final DelayActor delayActor = delay.getActor();
      final PiGraph resultGraph = vertexSinkSet.get(0).getContainingPiGraph();
      final PiGraph originalGraph = this.fifo.getContainingPiGraph();
      // 1.1 Now get the DelayActor associated to the setter of the delay
      final DelayActor setterDelayActor = (DelayActor) originalGraph.lookupVertex(delayActor.getName() + "_setter");
      if (setterDelayActor == null) {
        throw new PiMMHelperException("Setter actor [" + delayActor.getName() + "_setter] not found.");
      }
      final Long setterRV = brv.get(setterDelayActor);

      Fifo incomingFifo = setterDelayActor.getDataInputPort().getIncomingFifo();
      final AbstractActor setterActor = incomingFifo.getSourcePort().getContainingActor();
      final String setterName = setterActor.getName();
      final Expression setterRateExpression = incomingFifo.getSourcePort().getPortRateExpression();
      final long setterRate = setterRateExpression.evaluate();
      if (setterActor instanceof InitActor) {
        final InitActor init = PiMMUserFactory.instance.createInitActor();
        init.setName(this.graphPrefixe + setterName);
        init.getDataOutputPort().setName(this.sinkPort.getName());
        init.setLevel(((InitActor) setterActor).getLevel());
        init.getDataOutputPort().setExpression(setterRate);
        resultGraph.addActor(init);
        sourceSet.add(new SourceConnection(init, setterRate, this.sinkPort.getName()));
      } else {
        for (int i = 0; i < setterRV; ++i) {
          final InitActor init = PiMMUserFactory.instance.createInitActor();
          final String name = setterName + "_init_" + Integer.toString(i);
          init.setName(name);
          init.getDataOutputPort().setName(this.sinkPort.getName());
          resultGraph.addActor(init);
          sourceSet.add(new SourceConnection(init, setterRate, this.sinkPort.getName()));
        }
      }
    }

    final AbstractActor realSource = (AbstractActor) vertexSourceSet.get(0);
    if (realSource instanceof BroadcastActor && vertexSourceSet.size() == 1) {
      // Case of a Broadcast added afterward for an interface or single Broadcast
      long sinkRV = vertexSinkSet.size();
      sourceSet
          .add(new SourceConnection(vertexSourceSet.get(0), this.sinkConsumption * sinkRV, this.sourcePort.getName()));
    } else if (realSource instanceof AbstractActor) {
      // Add the list of the SR-DAG vertex associated with the source
      vertexSourceSet
          .forEach(v -> sourceSet.add(new SourceConnection(v, this.sourceProduction, this.sourcePort.getName())));
    } else {
      throw new PiMMHelperException("Unhandled type of actor: " + realSource.getClass().toString());
    }

    return sourceSet;
  }

  /**
   * Generate set of SinkConnection
   *
   * @param brv
   *          repetition vector values
   * @param vertexSinkSet
   *          Set of sink vertices
   * @return Set of SinkConnection
   * @throws PiMMHelperException
   *           the exception
   */
  private List<SinkConnection> getSinkSet(final List<AbstractVertex> vertexSinkSet,
      final List<AbstractVertex> vertexSourceSet, final Map<AbstractVertex, Long> brv) throws PiMMHelperException {
    // Initialize empty source set
    final List<SinkConnection> sinkSet = new ArrayList<>();

    final AbstractVertex realSink = vertexSinkSet.get(0);

    if (realSink instanceof RoundBufferActor && vertexSinkSet.size() == 1) {
      // Case of a Roundbuffer added afterward for an interface or single Roundbuffer
      final long sourceRV = vertexSourceSet.size();
      sinkSet.add(new SinkConnection(vertexSinkSet.get(0), this.sourceProduction * sourceRV, this.sinkPort.getName()));
    } else if (realSink instanceof AbstractActor) {
      // Add the list of the SR-DAG vertex associated with the source
      vertexSinkSet.forEach(v -> sinkSet.add(new SinkConnection(v, this.sinkConsumption, this.sinkPort.getName())));
    } else {
      throw new PiMMHelperException("Unhandled type of actor: " + realSink.getClass().toString());
    }

    // Deals delays
    if (this.delays != 0) {
      // 0. Check if Delay has setter
      final Delay delay = this.fifo.getDelay();
      // 1. Get the DelayActor
      final DelayActor delayActor = delay.getActor();
      final PiGraph resultGraph = vertexSinkSet.get(0).getContainingPiGraph();
      // 1.1 Now get the DelayActor associated to the setter of the delay
      final PiGraph originalGraph = this.fifo.getContainingPiGraph();
      final DelayActor getterDelayActor = (DelayActor) originalGraph.lookupVertex(delayActor.getName() + "_getter");
      if (getterDelayActor == null) {
        throw new PiMMHelperException("Getter actor [" + delayActor.getName() + "_setter] not found.");
      }
      final Long brvGetter = brv.get(getterDelayActor);

      Fifo outgoingFifo = getterDelayActor.getDataOutputPort().getOutgoingFifo();
      final AbstractActor getterActor = outgoingFifo.getTargetPort().getContainingActor();
      final String getterName = getterActor.getName();
      final Expression getterRateExpression = outgoingFifo.getTargetPort().getPortRateExpression();
      final long getterRate = getterRateExpression.evaluate();
      if (getterActor instanceof EndActor) {
        final EndActor end = PiMMUserFactory.instance.createEndActor();
        end.setName(this.graphPrefixe + getterName);
        end.getDataInputPort().setName(this.sourcePort.getName());
        end.setLevel(((EndActor) getterActor).getLevel());
        end.setEndReference(this.graphPrefixe + ((EndActor) getterActor).getEndReference());
        resultGraph.addActor(end);
        sinkSet.add(new SinkConnection(end, getterRate, this.sourcePort.getName()));
      } else {
        for (int i = 0; i < brvGetter; ++i) {
          final EndActor end = PiMMUserFactory.instance.createEndActor();
          final String name = getterName + "_end_" + Integer.toString(i);
          end.setName(name);
          end.getDataInputPort().setName(this.sourcePort.getName());
          resultGraph.addActor(end);
          sinkSet.add(new SinkConnection(end, getterRate, this.sourcePort.getName()));
        }
      }
    }

    return sinkSet;
  }

  /**
   * Class modeling source connection for single rate edge connections
   *
   * @author farresti
   *
   */
  class SourceConnection {
    private AbstractVertex source;
    private Long           production;
    private String         sourceLabel;

    public SourceConnection(final AbstractVertex source, final Long prod, final String sourceLabel) {
      this.source = source;
      this.production = prod;
      this.sourceLabel = sourceLabel;
    }

    public AbstractVertex getSource() {
      return source;
    }

    public Long getProduction() {
      return production;
    }

    public String getSourceLabel() {
      return sourceLabel;
    }
  }

  /**
   * Class modeling sink connection for single rate edge connections
   *
   * @author farresti
   *
   */
  class SinkConnection {
    private AbstractVertex sink;
    private Long           consumption;
    private String         targetLabel;

    public SinkConnection(final AbstractVertex sink, final Long cons, final String targetLabel) {
      this.sink = sink;
      this.consumption = cons;
      this.targetLabel = targetLabel;
    }

    public AbstractVertex getSink() {
      return sink;
    }

    public Long getConsumption() {
      return consumption;
    }

    public String getTargetLabel() {
      return targetLabel;
    }
  }
}
