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
import java.util.Map;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.factory.PiMMUserFactory;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.BroadcastActor;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.DelayActor;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
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
  private final AbstractActor  source;
  private final DataOutputPort sourcePort;
  private final long           sourceProduction;

  // Sink properties
  private final AbstractActor sink;
  private final DataInputPort sinkPort;
  private final long          sinkConsumption;

  // The FIFO
  private final Fifo fifo;

  // Delay init / end id
  String delayInitID;
  String delayEndID;

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
    final long nDelays = Long.parseLong(sizeExpression.getExpressionString());
    // Sanity check on delay value
    final DataInputPort targetPort = fifo.getTargetPort();
    final Expression portRateExpression = targetPort.getPortRateExpression();
    final long targetRate = Long.parseLong(portRateExpression.getExpressionString());
    if (nDelays < 0) {
      throw new RuntimeException("Invalid number of delay on fifo[" + fifo.getId() + "]: " + Long.toString(nDelays));
    } else if (nDelays < targetRate) {
      throw new RuntimeException("Insuffisiant number of delay on fifo[" + fifo.getId() + "]: number of delays: "
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
  public PiMMSRVerticesLinker(final Fifo fifo, final PiGraph dag, final PreesmScenario scenario) {
    this.fifo = fifo;
    this.delays = PiMMSRVerticesLinker.getNDelays(fifo);

    // Setting Source properties
    this.source = fifo.getSourcePort().getContainingActor();
    this.sourcePort = fifo.getSourcePort();
    final Expression sourceExpression = this.sourcePort.getPortRateExpression();
    this.sourceProduction = Long.parseLong(sourceExpression.getExpressionString());

    // Setting Sink properties
    this.sink = fifo.getTargetPort().getContainingActor();
    this.sinkPort = fifo.getTargetPort();
    final Expression sinkExpression = this.sinkPort.getPortRateExpression();
    this.sinkConsumption = Long.parseLong(sinkExpression.getExpressionString());

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
  public Boolean execute(final Map<AbstractVertex, Long> brv, final ArrayList<AbstractVertex> vertexSourceSet,
      final ArrayList<AbstractVertex> vertexSinkSet) throws PiMMHelperException {
    // These connections are already dealt with
    if ((this.source instanceof DelayActor) || (this.sink instanceof DelayActor)) {
      return true;
    }

    // Initialize delay init / end IDs
    this.delayInitID = "";
    this.delayEndID = "";

    // List of source vertices
    final ArrayList<PiMMSRVerticesLinker.SourceConnection> sourceSet = getSourceSet(vertexSourceSet, vertexSinkSet);

    // List of sink vertices
    final ArrayList<PiMMSRVerticesLinker.SinkConnection> sinkSet = getSinkSet(vertexSinkSet, vertexSourceSet);

    // Connect all the source to the sinks
    connectEdges(sourceSet, sinkSet);
    return true;
  }

  /**
   * Connect the sources to the sinks
   *
   * @param sourceSet
   *          set of dag sources
   * @param sinkSet
   *          set of dag sinks
   */
  private void connectEdges(final ArrayList<PiMMSRVerticesLinker.SourceConnection> sourceSet,
      final ArrayList<PiMMSRVerticesLinker.SinkConnection> sinkSet) {
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
   *          set of dag sources
   * @param sink
   *          current sink to connect to
   * @return true if it didn't explode, false else
   */
  private boolean connectSources2Sink(final ArrayList<PiMMSRVerticesLinker.SourceConnection> sourceSet,
      final ArrayList<PiMMSRVerticesLinker.SinkConnection> sinkSet) {
    // Check if source is a BroadcastActor
    final boolean isSourceBroadcastActor = (sourceSet.get(0).getSource() instanceof BroadcastActor);
    // If source is a BroadcastActor, it should be dealt with connectSinks2Source
    if (isSourceBroadcastActor) {
      return false;
    }

    final SinkConnection currentSink = sinkSet.get(0);
    long cons = currentSink.getConsumption();
    long prod = sourceSet.get(0).getProduction();

    // Get current sink vertex
    AbstractActor sinkVertex = (AbstractActor) currentSink.getSink();
    final PiGraph graph = sinkVertex.getContainingPiGraph();

    // Check implode condition
    final boolean implode = (cons > prod);
    // Check if sink is already a JoinActor
    final boolean isSinkJoinActor = (sinkVertex instanceof JoinActor);
    // Check if sink is already a RoundBufferActor
    final boolean isSinkRoundBufferActor = (sinkVertex instanceof RoundBufferActor);

    // Do we really need to implode ?
    if (implode && !isSinkJoinActor && !isSinkRoundBufferActor) {
      // Creates the implode vertex and connect it to current sink
      final JoinActor joinActor = doImplosion(currentSink, sinkVertex, graph);
      // The JoinActor becomes the new sink
      sinkVertex = joinActor;
    }

    // Now, let's connect everything we can
    // Array of SourceConnection to remove
    final ArrayList<PiMMSRVerticesLinker.SourceConnection> toRemove = new ArrayList<>();
    // Connect the edges
    for (final SourceConnection src : sourceSet) {
      prod = src.getProduction();
      if ((cons == 0) || (prod > cons)) {
        break;
      }
      toRemove.add(src);
      // 0. Get the current source vertex
      final AbstractActor sourceVertex = (AbstractActor) src.getSource();

      // 1. Retrieve the source port and if needed, creates it
      final DataOutputPort currentSourcePort = getCurrentSourcePort(src, prod, sourceVertex);

      // 2. Retrieve the sink port and if needed, creates it
      final DataInputPort currentSinkPort = getCurrentSinkPort(currentSink, prod, sinkVertex);

      // 3. Creates the Fifo and connect the ports
      createFifo(graph, currentSourcePort, currentSinkPort);

      // 4. If the sink is JoinActor / RoundBufferActor
      final boolean isJoinOrRoundBuffer = implode || isSinkJoinActor || isSinkRoundBufferActor;
      if (isJoinOrRoundBuffer) {
        // Update name and source port modifier
        currentSinkPort.setName(currentSinkPort.getName() + "_" + Long.toString(cons));
        currentSinkPort.setAnnotation(PortMemoryAnnotation.READ_ONLY);
      }
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
  private JoinActor doImplosion(final SinkConnection currentSink, final AbstractActor sinkVertex, final PiGraph graph) {
    final String implodeName = PiMMSRVerticesLinker.JOIN_VERTEX + sinkVertex.getName() + "_" + this.sinkPort.getName();
    final JoinActor joinActor = PiMMUserFactory.instance.createJoinActor();
    // Set name property
    joinActor.setName(implodeName);
    // Add a DataOutputPort
    final DataOutputPort outputPort = PiMMUserFactory.instance.createDataOutputPort();
    outputPort.setName(this.sourcePort.getName());
    outputPort.getPortRateExpression().setExpressionString(Long.toString(currentSink.getConsumption()));
    outputPort.setAnnotation(PortMemoryAnnotation.WRITE_ONLY);
    joinActor.getDataOutputPorts().add(outputPort);

    // Create the FIFO to connect the Join to the Sink
    final DataInputPort targetPort = (DataInputPort) sinkVertex.lookupPort(currentSink.getTargetLabel());
    createFifo(graph, outputPort, targetPort);

    // Add the join actor to the graph
    graph.addActor(joinActor);

    return joinActor;
  }

  /**
   * Connect sinks to current source
   *
   * @param sinkSet
   *          set of dag sinks
   * @param sink
   *          current source to connect from
   * @return true if it didn't implode, false else
   */
  private boolean connectSinks2Source(final ArrayList<PiMMSRVerticesLinker.SinkConnection> sinkSet,
      final ArrayList<PiMMSRVerticesLinker.SourceConnection> sourceSet) {
    // Check if source is a RoundBufferActor
    final boolean isSourceRoundBufferActor = (sinkSet.get(0).getSink() instanceof RoundBufferActor);
    // If source is a RoundBufferActor, it should be dealt with connectSources2Sink
    if (isSourceRoundBufferActor) {
      return false;
    }

    final SourceConnection currentSource = sourceSet.get(0);
    long cons = sinkSet.get(0).getConsumption();
    long prod = currentSource.getProduction();

    // Get current sink vertex
    AbstractActor sourceVertex = (AbstractActor) currentSource.getSource();
    final PiGraph graph = sourceVertex.getContainingPiGraph();

    // Check explode condition
    final boolean explode = (prod > cons);
    // Check if source is already a ForkActor
    final boolean isSourceForkActor = (sourceVertex instanceof ForkActor);
    // Check if source is already a BroadcastActor
    final boolean isSourceBroadcastActor = (sourceVertex instanceof BroadcastActor);

    // If we need to explode
    if (explode && !isSourceForkActor && !isSourceBroadcastActor) {
      // Creates the explode vertex and connect it to current source
      final ForkActor forkActor = doExplosion(currentSource, sourceVertex, graph);
      // The ForkActor becomes the new source
      sourceVertex = forkActor;
    }

    // Now, let's connect everything we can
    // Array of SinkConnection to remove
    final ArrayList<PiMMSRVerticesLinker.SinkConnection> toRemove = new ArrayList<>();
    // Connect the edges
    for (final SinkConnection snk : sinkSet) {
      cons = snk.getConsumption();
      if ((prod == 0) || (cons > prod)) {
        break;
      }
      toRemove.add(snk);
      // 0. Get the current sink vertex
      final AbstractActor sinkVertex = (AbstractActor) snk.getSink();

      // 1. Retrieve the source port and if needed, creates it
      final DataOutputPort currentSourcePort = getCurrentSourcePort(currentSource, cons, sourceVertex);

      // 2. Retrieve the sink port and if needed, creates it
      final DataInputPort currentSinkPort = getCurrentSinkPort(snk, cons, sinkVertex);

      // 3. Creates the FIFO and connect the ports
      createFifo(graph, currentSourcePort, currentSinkPort);

      // 4. If the source is a Fork (new) or a Broadcast
      final boolean isForkOrBroadcast = explode || isSourceForkActor || isSourceBroadcastActor;
      if (isForkOrBroadcast) {
        // Update name and source port modifier
        currentSourcePort.setName(currentSourcePort.getName() + "_" + Long.toString(prod));
        currentSourcePort.setAnnotation(PortMemoryAnnotation.WRITE_ONLY);
      }
      // 5. Update the remaining production
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
   * Return the current source port, creates it if it does not exists
   * 
   * @param src
   *          current source connection
   * @param rate
   *          rate to set on the port
   * @param sourceVertex
   *          source vertex
   * @return
   */
  private DataOutputPort getCurrentSourcePort(final SourceConnection src, long rate, AbstractActor sourceVertex) {
    DataOutputPort currentSourcePort = (DataOutputPort) sourceVertex.lookupPort(this.sourcePort.getName());
    if (currentSourcePort == null) {
      currentSourcePort = PiMMUserFactory.instance.createDataOutputPort();
      currentSourcePort.setName(src.getSourceLabel());
      currentSourcePort.getPortRateExpression().setExpressionString(Long.toString(rate));
      sourceVertex.getDataOutputPorts().add(currentSourcePort);
    }
    return currentSourcePort;
  }

  /**
   * Return the current sink port, creates it if it does not exists
   * 
   * @param snk
   *          current sink connection
   * @param rate
   *          rate to set on the port
   * @param sinkVertex
   *          sink vertex
   * @return
   */
  private DataInputPort getCurrentSinkPort(final SinkConnection snk, long rate, final AbstractActor sinkVertex) {
    DataInputPort currentSinkPort = (DataInputPort) sinkVertex.lookupPort(this.sinkPort.getName());
    if (currentSinkPort == null) {
      currentSinkPort = PiMMUserFactory.instance.createDataInputPort();
      currentSinkPort.setName(snk.getTargetLabel());
      currentSinkPort.getPortRateExpression().setExpressionString(Long.toString(rate));
      sinkVertex.getDataInputPorts().add(currentSinkPort);
    }
    return currentSinkPort;
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
  private ForkActor doExplosion(final SourceConnection currentSource, final AbstractActor sourceVertex,
      final PiGraph graph) {
    final String explodeName = PiMMSRVerticesLinker.FORK_VERTEX + sourceVertex.getName() + "_"
        + this.sourcePort.getName();
    final ForkActor forkActor = PiMMUserFactory.instance.createForkActor();
    // Set name property
    forkActor.setName(explodeName);
    // Add a DataOutputPort
    final DataInputPort targetPort = PiMMUserFactory.instance.createDataInputPort();
    targetPort.setName(this.sinkPort.getName());
    targetPort.getPortRateExpression().setExpressionString(Long.toString(currentSource.getProduction()));
    targetPort.setAnnotation(PortMemoryAnnotation.READ_ONLY);
    forkActor.getDataInputPorts().add(targetPort);

    // Create the FIFO to connect the Join to the Sink
    final DataOutputPort currentSourcePort = (DataOutputPort) sourceVertex.lookupPort(currentSource.getSourceLabel());

    createFifo(graph, currentSourcePort, targetPort);

    // Add the join actor to the graph
    graph.addActor(forkActor);

    return forkActor;
  }

  /**
   * Creates a Fifo and connects it to the source / target ports. Finally, the Fifo is added to the graph
   * 
   * @param graph
   *          the graph in which the fifo must be added
   * @param sourcePort
   *          the source port
   * @param targetPort
   *          the target port
   * @return the created fifo
   */
  private void createFifo(final PiGraph graph, final DataOutputPort sourcePort, final DataInputPort targetPort) {
    final Fifo newFifo = PiMMUserFactory.instance.createFifo();
    newFifo.setSourcePort(sourcePort);
    newFifo.setTargetPort(targetPort);
    newFifo.setType(this.fifo.getType());
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
  private ArrayList<PiMMSRVerticesLinker.SourceConnection> getSourceSet(final ArrayList<AbstractVertex> vertexSourceSet,
      final ArrayList<AbstractVertex> vertexSinkSet) throws PiMMHelperException {
    // Initialize empty source set
    final ArrayList<PiMMSRVerticesLinker.SourceConnection> sourceSet = new ArrayList<>();

    // Deals delays
    if (this.delays != 0) {
      // 0. Create the delay actor
      final DelayActor init = PiMMUserFactory.instance.createDelayActor();
      init.setName(vertexSinkSet.get(0).getName() + "_init_" + this.sinkPort.getName());
      // 2. Add the init actor to the graph
      vertexSinkSet.get(0).getContainingPiGraph().addActor(init);
      // 3. Add an entry in the sourceSet
      sourceSet.add(new SourceConnection(init, this.delays, this.sinkPort.getName()));
    }

    if (this.source instanceof InterfaceActor) {
      // If source is an InterfaceActor, then we have a broadcast
      final long sinkRV = vertexSinkSet.size();
      // for (int i = 0; i < sinkRV; ++i) {
      // sourceSet.add(new SourceConnection(vertexSourceSet.get(0), this.sinkConsumption, this.sourcePort.getName()));
      // }
      sourceSet
          .add(new SourceConnection(vertexSourceSet.get(0), this.sinkConsumption * sinkRV, this.sourcePort.getName()));
    } else if (this.source instanceof AbstractActor) {
      // Add the list of the SR-DAG vertex associated with the source
      vertexSourceSet
          .forEach(v -> sourceSet.add(new SourceConnection(v, this.sourceProduction, this.sourcePort.getName())));
    } else {
      throw new PiMMHelperException("Unhandled type of actor: " + this.source.getClass().toString());
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
  private ArrayList<PiMMSRVerticesLinker.SinkConnection> getSinkSet(final ArrayList<AbstractVertex> vertexSinkSet,
      final ArrayList<AbstractVertex> vertexSourceSet) throws PiMMHelperException {
    // Initialize empty source set
    final ArrayList<PiMMSRVerticesLinker.SinkConnection> sinkSet = new ArrayList<>();

    if (this.sink instanceof InterfaceActor) {
      // If sink is an InterfaceActor, then we have a roundbuffer
      final long sourceRV = vertexSourceSet.size();
      // for (int i = 0; i < sourceRV; ++i) {
      // sinkSet.add(new SinkConnection(vertexSinkSet.get(0), this.sourceProduction, this.sinkPort.getName()));
      // }
      sinkSet.add(new SinkConnection(vertexSinkSet.get(0), this.sourceProduction * sourceRV, this.sinkPort.getName()));
    } else if (this.sink instanceof AbstractActor) {
      // Add the list of the SR-DAG vertex associated with the source
      vertexSinkSet.forEach(v -> sinkSet.add(new SinkConnection(v, this.sinkConsumption, this.sinkPort.getName())));
    } else {
      throw new PiMMHelperException("Unhandled type of actor: " + this.sink.getClass().toString());
    }

    // Deals delays
    if (this.delays != 0) {
      // 0. Create the delay actor
      final DelayActor end = PiMMUserFactory.instance.createDelayActor();
      end.setName(vertexSinkSet.get(vertexSinkSet.size() - 1).getName() + "_end_" + this.sourcePort.getName());
      // 2. Add the end actor to the graph
      vertexSinkSet.get(0).getContainingPiGraph().addActor(end);
      // 3. Add an entry in the sinkSet
      sinkSet.add(new SinkConnection(end, this.delays, this.sourcePort.getName()));
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
