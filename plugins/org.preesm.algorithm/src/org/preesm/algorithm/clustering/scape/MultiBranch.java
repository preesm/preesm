package org.preesm.algorithm.clustering.scape;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 * Handles cases where dataflow applications present several branches that are clearly blocking the clustering and
 * pipeline heuristics.
 *
 * This class provides methods to add an initial source actor and connect it to multiple sources, as well as remove the
 * initial source actor and clean up the connections.
 *
 * @author orenaud
 */
public class MultiBranch {
  /**
   * Input graph.
   */
  private final PiGraph graph;

  /**
   * Constructs a MultiBranch object with the given PiGraph.
   *
   * @param graph
   *          The PiGraph to be processed.
   */
  public MultiBranch(PiGraph graph) {
    this.graph = graph;
  }

  /**
   * Adds an initial source actor and connects it to multiple sources in the graph.
   *
   * @return The modified PiGraph after adding the initial source.
   */
  public PiGraph addInitialSource() {
    if (!graph.getChildrenGraphs().isEmpty()) {
      PreesmLogger.getLogger().log(Level.INFO, () -> "only flat graphs or subgraphs can be processed");

    }
    // Identify multiple sources
    final List<AbstractActor> sourceList = new ArrayList<>();
    // seek sources
    for (final AbstractActor source : graph.getActors()) {
      // candidate is a source if
      // - it s not a delay or an interface
      // - it doesnot have inputport or it should be a delay or a data input interface
      if (!(source instanceof DelayActor) && !(source instanceof DataInputInterface)
          && (source instanceof ExecutableActor
              && (source.getDataInputPorts().isEmpty() || source.getDataInputPorts().stream().allMatch(
                  x -> x.getFifo().isHasADelay() || x.getFifo().getSource() instanceof DataInputInterface)))) {
        sourceList.add(source);
      }
    }
    final Map<AbstractVertex, Long> brv = PiBRV.compute(graph, BRVMethod.LCM);

    if (sourceList.size() <= 1) {
      return graph;
    }

    final Actor src = PiMMUserFactory.instance.createActor();
    src.setName("single_source");
    src.setContainingGraph(graph);
    int indexOutput = 0;
    // connect to multiple sources
    for (final AbstractActor actor : sourceList) {

      // add output on single source actor
      final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
      src.getDataOutputPorts().add(dout);
      dout.setName("out_" + indexOutput);
      dout.setExpression(1L * brv.get(actor));

      // add input
      final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
      actor.getDataInputPorts().add(din);
      din.setName("in");
      din.setExpression(1L);

      // connect
      final Fifo fifo = PiMMUserFactory.instance.createFifo(dout, din, "char");
      fifo.setContainingGraph(graph);

      indexOutput++;
    }

    return graph;
  }

  /**
   * Removes the initial source actor and cleans up the connections in the graph.
   *
   * @return The modified PiGraph after removing the initial source.
   */
  public PiGraph removeInitialSource() {
    // Identify dummy source
    for (final AbstractActor dummySrc : graph.getActors()) {
      if (dummySrc.getName().equals("single_source")) {
        // delete port

        for (final DataOutputPort dout : dummySrc.getDataOutputPorts()) {
          final AbstractActor a = (AbstractActor) dout.getFifo().getTarget();
          a.getDataInputPorts().remove(dout.getFifo().getTargetPort());
          final Fifo f = dout.getFifo();
          graph.removeFifo(f);
          graph.removeActor(dummySrc);

        }
      }
    }
    return graph;
  }
}
