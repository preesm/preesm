package org.preesm.algorithm.synthesis.schedule.algos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PeriodicElement;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioConstants;
import org.preesm.model.slam.Component;
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

    long    load;
    int     nbVisits;
    boolean isPeriodic;

    AbstractActor aa;

    private VertexAbstraction(AbstractActor aa) {
      this.aa = aa;
      this.load = 0;
      this.nbVisits = 0;
      this.isPeriodic = false;

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

  protected PiGraph  piGraph;
  protected Design   slamDesign;
  protected Scenario scenario;

  protected DefaultDirectedGraph<VertexAbstraction, EdgeAbstraction> absGraph;
  protected List<VertexAbstraction>                                  firstNodes;
  protected List<VertexAbstraction>                                  lastNodes;

  @Override
  protected SynthesisResult exec(PiGraph piGraph, Design slamDesign, Scenario scenario) {

    if (slamDesign.getOperatorComponents().size() != 1) {
      throw new PreesmRuntimeException("This task must be called with a homogeneous architecture, abandon.");
    }

    int nbCore = slamDesign.getOperatorComponents().get(0).getInstances().size();
    PreesmLogger.getLogger().log(Level.INFO, "Found " + nbCore + " cores.");

    long graphPeriod = piGraph.getPeriod().evaluate();
    PreesmLogger.getLogger().log(Level.INFO, "Graph period is: " + graphPeriod);

    this.piGraph = piGraph;
    this.slamDesign = slamDesign;
    this.scenario = scenario;
    absGraph = createAbsGraph(piGraph, slamDesign, scenario);

    PreesmLogger.getLogger().log(Level.INFO,
        "Starting to schedule and map " + absGraph.vertexSet().size() + " actors.");

    long Ctot = 0;
    firstNodes = new ArrayList<>();
    lastNodes = new ArrayList<>();
    for (VertexAbstraction va : absGraph.vertexSet()) {
      Ctot += va.load;
      if (absGraph.incomingEdgesOf(va).isEmpty()) {
        firstNodes.add(va);
      }
      if (absGraph.outgoingEdgesOf(va).isEmpty()) {
        lastNodes.add(va);
      }
    }

    long horizon = piGraph.getPeriod().evaluate();
    if (horizon <= 0) {
      horizon = Ctot;
      PreesmLogger.getLogger().log(Level.INFO,
          "No period found: scheduling performed with sequential worst case: " + Ctot + " time unit.");
    } else if (Ctot / (double) nbCore > horizon) {
      throw new PreesmRuntimeException(
          "Your graph is clearly not schedulable: utilization factor is higher than number of cores. Total load: "
              + Ctot);
    }
    setAbsGraph(absGraph, horizon, firstNodes, lastNodes);

    throw new PreesmRuntimeException("It stops here for now!");
  }

  /**
   * Compare actors per name.
   * 
   * @author ahonorat
   */
  private static class ActorNameComparator implements Comparator<AbstractActor> {
    @Override
    public int compare(AbstractActor arg0, AbstractActor arg1) {
      return arg0.getName().compareTo(arg1.getName());
    }
  }

  protected static DefaultDirectedGraph<VertexAbstraction, EdgeAbstraction> createAbsGraph(final PiGraph graph,
      Design slamDesign, Scenario scenario) {
    final DefaultDirectedGraph<VertexAbstraction,
        EdgeAbstraction> absGraph = new DefaultDirectedGraph<>(EdgeAbstraction.class);

    Map<AbstractActor, VertexAbstraction> aaTOva = new TreeMap<>(new ActorNameComparator());
    Map<AbstractActor, Long> loadMemoization = new TreeMap<>(new ActorNameComparator());
    for (final AbstractActor aa : graph.getActors()) {
      VertexAbstraction va = new VertexAbstraction(aa);
      absGraph.addVertex(va);
      aaTOva.put(aa, va);

      AbstractActor originalActor = PreesmCopyTracker.getOriginalSource(aa);
      if (!loadMemoization.containsKey(originalActor)) {
        long load = getLoad(originalActor, slamDesign, scenario);
        loadMemoization.put(originalActor, load);
        va.load = load;
      } else {
        va.load = loadMemoization.get(originalActor);
      }

      if (aa instanceof PeriodicElement) {
        PeriodicElement pe = (PeriodicElement) aa;
        long period = pe.getPeriod().evaluate();
        if (period > 0 && pe instanceof Actor) {
          va.isPeriodic = true;
          Actor a = (Actor) pe;
          long firingInstance = a.getFiringInstance();
          long ns = firingInstance * period;
          va.minStartTime = ns;
          va.maxStartTime = (firingInstance + 1) * period;
        }
      }

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

    // TODO check if DAG?

    return absGraph;
  }

  protected static long getLoad(AbstractActor actor, Design slamDesign, Scenario scenario) {
    long wcet = ScenarioConstants.DEFAULT_TIMING_TASK.getValue();
    for (final Component operatorDefinitionID : slamDesign.getOperatorComponents()) {
      wcet = scenario.getTimings().evaluateTimingOrDefault((AbstractActor) actor, operatorDefinitionID);
    }
    return wcet;
  }

  protected static void setAbsGraph(DefaultDirectedGraph<VertexAbstraction, EdgeAbstraction> absGraph, long horizon,
      List<VertexAbstraction> firstNodes, List<VertexAbstraction> lastNodes) {
    for (VertexAbstraction va : absGraph.vertexSet()) {
      if (!va.isPeriodic) {
        va.maxStartTime = horizon;
      }
    }
    // set min start time
    List<VertexAbstraction> toVisit = new LinkedList<>(firstNodes);
    while (!toVisit.isEmpty()) {
      VertexAbstraction va = toVisit.remove(0);
      long succns = va.minStartTime + va.load;
      for (EdgeAbstraction ea : absGraph.outgoingEdgesOf(va)) {
        VertexAbstraction oppositeva = absGraph.getEdgeTarget(ea);
        oppositeva.minStartTime = Math.max(oppositeva.minStartTime, succns);
        updateNbVisits(absGraph, oppositeva, false, toVisit);
      }
    }
    // set max and average start time
    toVisit = new LinkedList<>(lastNodes);
    while (!toVisit.isEmpty()) {
      VertexAbstraction va = toVisit.remove(0);
      va.maxStartTime -= va.load;
      if (va.minStartTime > va.maxStartTime) {
        throw new PreesmRuntimeException("Cannot schedule firing: " + va.aa.getName());
      }
      va.averageStartTime = (va.minStartTime + va.maxStartTime) / 2;
      long predxs = va.maxStartTime;
      for (EdgeAbstraction ea : absGraph.incomingEdgesOf(va)) {
        VertexAbstraction oppositeva = absGraph.getEdgeSource(ea);
        oppositeva.maxStartTime = Math.min(oppositeva.maxStartTime, predxs);
        updateNbVisits(absGraph, oppositeva, true, toVisit);
      }
    }

  }

  protected static void updateNbVisits(DefaultDirectedGraph<VertexAbstraction, EdgeAbstraction> absGraph,
      VertexAbstraction va, boolean reverse, List<VertexAbstraction> queue) {
    va.nbVisits += 1;
    Set<EdgeAbstraction> seteas = null;
    if (reverse) {
      seteas = absGraph.outgoingEdgesOf(va);
    } else {
      seteas = absGraph.incomingEdgesOf(va);
    }
    if (va.nbVisits == seteas.size()) {
      queue.add(va);
      va.nbVisits = 0;
    }

  }

}
