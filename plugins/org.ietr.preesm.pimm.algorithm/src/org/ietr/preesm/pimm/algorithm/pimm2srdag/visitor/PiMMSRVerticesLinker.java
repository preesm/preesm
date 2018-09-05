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
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGBroadcastVertex;
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
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation;
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

  // Source actor
  private final AbstractActor source;
  // Source Port
  private DataOutputPort sourcePort;

  // Sink Actor
  private final AbstractActor sink;
  // Sink Port
  private DataInputPort sinkPort;

  // The fifo
  private final Fifo   fifo;
  private final String fifoName;

  // The DAG in which we operate
  private final PiGraph dag;

  // Port modifiers annotations
  private final String sourceModifier;
  private final String targetModifier;

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
   * Get the repetition value for the sink / source actor
   *
   * @param actor
   *          the sink actor
   * @param brv
   *          the basic repetition vector map
   * @return repetition value of the actor, 1 if it is an interface actor
   */
  private static long getRVorDefault(final AbstractActor actor, final Map<AbstractVertex, Long> brv) {
    if (actor instanceof InterfaceActor) {
      return 1;
    }
    return (brv.get(actor));
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
    this.source = fifo.getSourcePort().getContainingActor();
    this.sink = fifo.getTargetPort().getContainingActor();
    this.sourcePort = fifo.getSourcePort();
    this.sinkPort = fifo.getTargetPort();
    this.dag = dag;
    this.sourceModifier = getAnnotationFromPort(this.sourcePort);
    this.targetModifier = getAnnotationFromPort(this.sinkPort);
    this.fifoName = this.fifo.getId().replace(".", "_").replace("-", "_");
  }

  /**
   * Convert annotations from to.
   *
   * @param piPort
   *          the pi port
   * @param edge
   *          the edge
   * @param property
   *          the property
   */
  private String getAnnotationFromPort(final DataPort piPort) {
    return piPort.getAnnotation().getLiteral();
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
    final ArrayList<PiMMSRVerticesLinker.SourceConnection> sourceSet = getSourceSet(brv, vertexSourceSet);

    // List of sink vertices
    final ArrayList<PiMMSRVerticesLinker.SinkConnection> sinkSet = getSinkSet(brv, vertexSinkSet);

    // Connect all the source to the sinks
    connectEdges(sourceSet, sinkSet);
    return true;
  }

  /**
   * Test if a vertex is a roundbuffer
   *
   * @param vertex
   *          the vertex to test
   * @return true if the vertex is a roundbuffer, false else
   */
  private boolean isRoundBuffer(final DAGVertex vertex) {
    if (!vertex.getKind().equals(DAGBroadcastVertex.DAG_BROADCAST_VERTEX)) {
      return false;
    }
    final String value = (String) vertex.getPropertyBean().getValue(DAGBroadcastVertex.SPECIAL_TYPE);
    return value.equals(DAGBroadcastVertex.SPECIAL_TYPE_ROUNDBUFFER);
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
    final SinkConnection currentSink = sinkSet.get(0);
    long cons = currentSink.getConsumption();
    long prod = sourceSet.get(0).getProduction();

    // Get current sink vertex
    AbstractActor sinkVertex = (AbstractActor) currentSink.getSink();
    final PiGraph graph = sinkVertex.getContainingPiGraph();

    // Check implode condition
    final boolean implode = (cons > prod);

    // Check if sink is already a join actor
    final boolean isSinkJoinActor = (sinkVertex instanceof JoinActor);

    // If we need to implode
    if (implode && !isSinkJoinActor) {
      // Creates the implode vertex and connect it to current sink
      final JoinActor joinActor = doImplosion(currentSink, sinkVertex, graph);
      // The join actor becomes the new sink
      sinkVertex = joinActor;
    }

    // Now, let's connect everything we can
    // Array of sources to remove
    final ArrayList<PiMMSRVerticesLinker.SourceConnection> toRemove = new ArrayList<>();
    // Connect the edges
    for (final SourceConnection src : sourceSet) {
      prod = src.getProduction();
      if ((cons == 0) || (prod > cons)) {
        break;
      }
      toRemove.add(src);
      final AbstractActor sourceVertex = (AbstractActor) src.getSource();

      // Retrieve the source port and if needed, creates it
      DataOutputPort currentSourcePort = (DataOutputPort) sourceVertex.lookupPort(this.sourcePort.getName());
      if (currentSourcePort == null) {
        currentSourcePort = PiMMUserFactory.instance.createDataOutputPort();
        currentSourcePort.setName(src.getSourceLabel());
        currentSourcePort.getPortRateExpression().setExpressionString(Long.toString(prod));
        sourceVertex.getDataOutputPorts().add(currentSourcePort);
      }

      // Retrieve the sink port and if needed, creates it
      DataInputPort currentSinkPort = (DataInputPort) sinkVertex.lookupPort(this.sinkPort.getName());
      if (currentSinkPort == null) {
        currentSinkPort = PiMMUserFactory.instance.createDataInputPort();
        currentSinkPort.setName(currentSink.getTargetLabel());
        currentSinkPort.getPortRateExpression().setExpressionString(Long.toString(prod));
        sinkVertex.getDataInputPorts().add(currentSinkPort);
      }

      // Creates the Fifo and connect the ports
      final Fifo newFifo = PiMMUserFactory.instance.createFifo();
      newFifo.setSourcePort(currentSourcePort);
      newFifo.setTargetPort(currentSinkPort);
      graph.addFifo(newFifo);

      // If the target is join (new or not) /roundbuffer with new ports
      final boolean isJoinOrRoundBuffer = (sinkVertex != currentSink.getSink()) || (implode && !isSinkJoinActor);
      if (isJoinOrRoundBuffer) {
        // Update name and source port modifier
        currentSinkPort.setName(currentSinkPort.getName() + "_" + Long.toString(cons));
        currentSinkPort.setAnnotation(PortMemoryAnnotation.READ_ONLY);
      }
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
    final Fifo newFifo = PiMMUserFactory.instance.createFifo();
    final DataInputPort targetPort = (DataInputPort) sinkVertex.lookupPort(currentSink.getTargetLabel());
    if (targetPort != null) {
      newFifo.setTargetPort(targetPort);
    }
    newFifo.setSourcePort(outputPort);
    newFifo.setType(this.fifo.getType());

    // Add the fifo to the graph
    graph.addFifo(newFifo);

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
    final SourceConnection currentSource = sourceSet.get(0);
    long cons = sinkSet.get(0).getConsumption();
    long prod = currentSource.getProduction();

    // Get current sink vertex
    AbstractActor sourceVertex = (AbstractActor) currentSource.getSource();
    final PiGraph graph = sourceVertex.getContainingPiGraph();

    // Check implode condition
    final boolean explode = (prod > cons);

    // Check if sink is already a fork actor
    final boolean isSourceForkActor = (sourceVertex instanceof ForkActor);
    // Check if sink is already a broadcast actor
    final boolean isSourceBroadcastActor = (sourceVertex instanceof BroadcastActor);

    // If we need to implode
    if (explode && !isSourceForkActor && !isSourceBroadcastActor) {
      // Creates the implode vertex and connect it to current sink
      final ForkActor forkActor = doExplosion(currentSource, sourceVertex, graph);

      // The join actor becomes the new sink
      sourceVertex = forkActor;
    }

    // Now, let's connect everything we can
    // Array of sources to remove
    final ArrayList<PiMMSRVerticesLinker.SinkConnection> toRemove = new ArrayList<>();
    // Connect the edges
    for (final SinkConnection snk : sinkSet) {
      cons = snk.getConsumption();
      if ((prod == 0) || (cons > prod)) {
        break;
      }
      toRemove.add(snk);
      final AbstractActor sinkVertex = (AbstractActor) snk.getSink();

      // Retrieve the source port and if needed, creates it
      DataOutputPort currentSourcePort = (DataOutputPort) sourceVertex.lookupPort(this.sourcePort.getName());
      if (currentSourcePort == null) {
        currentSourcePort = PiMMUserFactory.instance.createDataOutputPort();
        currentSourcePort.setName(currentSource.getSourceLabel());
        currentSourcePort.getPortRateExpression().setExpressionString(Long.toString(cons));
        sourceVertex.getDataOutputPorts().add(currentSourcePort);
      }

      // Retrieve the sink port and if needed, creates it
      DataInputPort currentSinkPort = (DataInputPort) sinkVertex.lookupPort(this.sinkPort.getName());
      if (currentSinkPort == null) {
        currentSinkPort = PiMMUserFactory.instance.createDataInputPort();
        currentSinkPort.setName(snk.getTargetLabel());
        currentSinkPort.getPortRateExpression().setExpressionString(Long.toString(cons));
        sinkVertex.getDataInputPorts().add(currentSinkPort);
      }

      // Creates the Fifo and connect the ports
      final Fifo newFifo = PiMMUserFactory.instance.createFifo();
      newFifo.setSourcePort(currentSourcePort);
      newFifo.setTargetPort(currentSinkPort);
      graph.addFifo(newFifo);

      // If the target is join (new or not) /roundbuffer with new ports
      final boolean isForkOrBroadcast = !((sourceVertex == currentSource.getSource())
          && (!explode || !(isSourceBroadcastActor || isSourceForkActor)));
      if (isForkOrBroadcast) {
        // Update name and source port modifier
        currentSourcePort.setName(currentSourcePort.getName() + "_" + Long.toString(prod));
        currentSourcePort.setAnnotation(PortMemoryAnnotation.WRITE_ONLY);
      }
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
    final DataInputPort inputPort = PiMMUserFactory.instance.createDataInputPort();
    inputPort.setName(this.sinkPort.getName());
    inputPort.getPortRateExpression().setExpressionString(Long.toString(currentSource.getProduction()));
    inputPort.setAnnotation(PortMemoryAnnotation.READ_ONLY);
    forkActor.getDataInputPorts().add(inputPort);

    // Create the FIFO to connect the Join to the Sink
    final Fifo newFifo = PiMMUserFactory.instance.createFifo();
    final DataOutputPort currentSourcePort = (DataOutputPort) sourceVertex.lookupPort(currentSource.getSourceLabel());
    if (currentSourcePort != null) {
      newFifo.setSourcePort(currentSourcePort);
    }
    newFifo.setTargetPort(inputPort);
    newFifo.setType(this.fifo.getType());

    // Add the fifo to the graph
    graph.addFifo(newFifo);

    // Add the join actor to the graph
    graph.addActor(forkActor);

    return forkActor;
  }

  /**
   * Generate DAG set of sources
   *
   * @param brv
   *          repetition vector values
   * @param pimm2dag
   *          map of PiMM actors and their associated list of dag vertices
   * @return set of dag sources
   * @throws PiMMHelperException
   *           the exception
   */
  private ArrayList<PiMMSRVerticesLinker.SourceConnection> getSourceSet(final Map<AbstractVertex, Long> brv,
      final ArrayList<AbstractVertex> vertexSourceSet) throws PiMMHelperException {
    // Initialize empty source set
    final ArrayList<PiMMSRVerticesLinker.SourceConnection> sourceSet = new ArrayList<>();

    // Deals delays

    // Port expressions
    final Expression sourceExpression = this.sourcePort.getPortRateExpression();
    final long sourceProduction = Long.parseLong(sourceExpression.getExpressionString());

    if (this.source instanceof AbstractActor) {
      // Add the list of the SR-DAG vertex associated with the source
      vertexSourceSet.forEach(v -> sourceSet.add(new SourceConnection(v, sourceProduction, this.sourcePort.getName())));
    } else {
      throw new PiMMHelperException("Unhandled type of actor: " + this.source.getClass().toString());
    }

    return sourceSet;
  }

  /**
   * Generate DAG set of sources
   *
   * @param brv
   *          repetition vector values
   * @param pimm2dag
   *          map of PiMM actors and their associated list of dag vertices
   * @return set of dag sources
   * @throws PiMMHelperException
   *           the exception
   */
  private ArrayList<PiMMSRVerticesLinker.SinkConnection> getSinkSet(final Map<AbstractVertex, Long> brv,
      final ArrayList<AbstractVertex> vertexSourceSet) throws PiMMHelperException {
    // Initialize empty source set
    final ArrayList<PiMMSRVerticesLinker.SinkConnection> sinkSet = new ArrayList<>();

    // Deals delays

    // Port expressions
    final Expression sinkExpression = this.sinkPort.getPortRateExpression();
    final long sinkConsumption = Long.parseLong(sinkExpression.getExpressionString());

    if (this.sink instanceof AbstractActor) {
      // Add the list of the SR-DAG vertex associated with the source
      vertexSourceSet.forEach(v -> sinkSet.add(new SinkConnection(v, sinkConsumption, this.sinkPort.getName())));
    } else {
      throw new PiMMHelperException("Unhandled type of actor: " + this.sink.getClass().toString());
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
