package org.preesm.algorithm.synthesis.schedule.algos;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * This scheduler handles the periods defined in the PiGraph and in its actors. However, it does not take into account
 * communication time.
 * 
 * @author ahonorat
 */
public class PeriodicScheduler extends AbstractScheduler {

  /**
   * 
   * @author ahonorat
   *
   */
  protected static class VertexAbstraction {

    long startTime;
    long maxStartTime;
    long minStartTime;
    long averageStartTime;

    long load;
    int  nbVisits;

    AbstractActor aa;

    private VertexAbstraction(AbstractActor aa) {
      this.aa = aa;
      this.load = 0;

      this.startTime = 0;
      this.maxStartTime = 0;
      this.minStartTime = 0;
      this.averageStartTime = 0;
    }
  }

  /**
   * 
   * @author ahonorat
   *
   */
  protected static class EdgeAbstraction {

    long weight;

    private EdgeAbstraction() {
      this.weight = 0;
    }
  }

  @Override
  protected SynthesisResult exec(PiGraph piGraph, Design slamDesign, Scenario scenario) {

    if (slamDesign.getOperatorComponents().size() != 1) {
      throw new PreesmRuntimeException("This task must be called with a homogeneous architecture, abandon.");
    }

    int nbCore = slamDesign.getOperatorComponents().get(0).getInstances().size();
    PreesmLogger.getLogger().log(Level.INFO, "Found " + nbCore + " cores.");

    long graphPeriod = piGraph.getPeriod().evaluate();
    PreesmLogger.getLogger().log(Level.INFO, "Graph period is: " + graphPeriod);

    DefaultDirectedGraph<VertexAbstraction, EdgeAbstraction> absGraph = createAbsGraph(piGraph);
    PreesmLogger.getLogger().log(Level.INFO,
        "Starting to schedule and map " + absGraph.vertexSet().size() + " actors.");

    throw new PreesmRuntimeException("It stops here for now!");
  }

  protected static DefaultDirectedGraph<VertexAbstraction, EdgeAbstraction> createAbsGraph(final PiGraph graph) {
    final DefaultDirectedGraph<VertexAbstraction,
        EdgeAbstraction> absGraph = new DefaultDirectedGraph<>(EdgeAbstraction.class);

    Map<AbstractActor, VertexAbstraction> aaTOva = new HashMap<>();
    for (final AbstractActor aa : graph.getActors()) {
      VertexAbstraction va = new VertexAbstraction(aa);
      absGraph.addVertex(va);
      aaTOva.put(aa, va);
    }

    for (final Fifo f : graph.getFifos()) {
      final DataOutputPort dop = f.getSourcePort();
      final DataInputPort dip = f.getTargetPort();

      final AbstractActor aaSrc = dop.getContainingActor();
      final AbstractActor aaTgt = dip.getContainingActor();

      final VertexAbstraction vaSrc = aaTOva.get(aaSrc);
      final VertexAbstraction vaTgt = aaTOva.get(aaTgt);

      EdgeAbstraction fa = absGraph.getEdge(vaSrc, vaTgt);
      if (fa == null) {
        fa = new EdgeAbstraction();
        final boolean res = absGraph.addEdge(vaSrc, vaTgt, fa);
        if (!res) {
          throw new PreesmRuntimeException("Problem while creating graph copy.");
        }
      }
    }
    return absGraph;
  }

}
