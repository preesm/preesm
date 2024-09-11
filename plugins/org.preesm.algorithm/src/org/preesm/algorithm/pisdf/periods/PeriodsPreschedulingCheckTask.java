/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2018 - 2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
 * Mickaël Dardaillon [mickael.dardaillon@insa-rennes.fr] (2020)
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
package org.preesm.algorithm.pisdf.periods;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.preesm.algorithm.pisdf.autodelays.AbstractGraph;
import org.preesm.algorithm.pisdf.autodelays.AbstractGraph.FifoAbstraction;
import org.preesm.algorithm.pisdf.autodelays.HeuristicLoopBreakingDelays;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.PeriodicElement;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.statictools.PiMMHelper;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioConstants;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.check.SlamDesignPEtypeChecker;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * This class computes necessary conditions for the schedulability of graphs with periods.
 * <p>
 * For more details, see conference paper "Scheduling of Synchronous Dataflow Graphs with Partially Periodic Real-Time
 * Constraints", published at RTNS 2020 (DOI 10.1145/3394810.3394820).
 *
 * @author ahonorat
 *
 */
@PreesmTask(id = "org.ietr.preesm.pimm.algorithm.checker.periods.PeriodsPreschedulingChecker",
    name = "Periods Prescheduling Checker",
    shortDescription = "Check necessary condition to schedule graphs with periods (at top level or in actors).",
    description = "Check necessary condition to schedule graphs with periods (at top level or in actors). "
        + "Works only on flat graphs.",

    inputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class),
        @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class),
        @Port(name = AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, type = Design.class) },

    outputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class) },

    parameters = { @Parameter(name = "Selection rate (%)", description = "Percentage of periodic actors to consider.",
        values = { @Value(name = "100", effect = "All periodic actors are checked.") }) }

)
public class PeriodsPreschedulingCheckTask extends AbstractTaskImplementation {

  /**
   * Identify the parameter to get the rate of periodic actors to analyze.
   */
  public static final String SELECTION_RATE = "Selection rate (%)";

  /**
   * By default all actors are analyzed.
   */
  public static final String DEFAULT_SELECTION_RATE = "100";

  private static final String GENERIC_RATE_ERROR = "Periodic actors selection rate "
      + "must be an integer between 1 and 100 (%), instead of: ";

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final PiGraph graph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);

    if (!graph.getChildrenGraphs().isEmpty()) {
      throw new PreesmRuntimeException("This task must be called with a flatten PiMM graph, abandon.");
    }

    if (!SlamDesignPEtypeChecker.isHomogeneousCPU(architecture)) {
      throw new PreesmRuntimeException("This task must be called with a homogeneous CPU architecture, abandon.");
    }

    final int nbCore = architecture.getProcessingElements().get(0).getInstances().size();
    PreesmLogger.getLogger().info(() -> "Found " + nbCore + " cores.");

    final long time = System.nanoTime();

    final Map<String, Object> output = new LinkedHashMap<>();
    output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, graph);
    final PiGraph graphCopy = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);

    final String rateStr = parameters.get(PeriodsPreschedulingCheckTask.SELECTION_RATE);
    int rate = 100;
    try {
      rate = Integer.parseInt(rateStr);
      if ((rate < 0) || (rate > 100)) {
        throw new PreesmRuntimeException(PeriodsPreschedulingCheckTask.GENERIC_RATE_ERROR + rate + ".");
      }
    } catch (final NumberFormatException e) {
      throw new PreesmRuntimeException(PeriodsPreschedulingCheckTask.GENERIC_RATE_ERROR + rateStr + ".", e);
    }

    final Map<Actor, Long> periodicActors = new LinkedHashMap<>();
    for (final AbstractActor absActor : graphCopy.getActors()) {
      if ((absActor instanceof final Actor actor) && (absActor instanceof PeriodicElement) && !actor.isHierarchical()
          && !actor.isConfigurationActor()) {
        final long period = actor.getPeriod().evaluateAsLong();
        if (period > 0) {
          periodicActors.put(actor, period);
        }
      }

      if (absActor instanceof final DelayActor da && (da.getSetterActor() != null || da.getGetterActor() != null)) {
        throw new PreesmRuntimeException("DelayActor with getter or setter are not supported in this task, abandon.");
      }

    }

    final Map<AbstractVertex, Long> brv = PiBRV.compute(graphCopy, BRVMethod.LCM);
    // check that are all actor periods times brv are equal and set the graph period if needed
    PiMMHelper.checkPeriodicity(graphCopy, brv);

    final long graphPeriod = graphCopy.getPeriod().evaluateAsLong();
    if (graphPeriod <= 0 && periodicActors.isEmpty()) {
      PreesmLogger.getLogger().log(Level.WARNING, "This task is useless when there is no period in the graph.");
      return output;
    }

    final Map<AbstractVertex, Long> wcets = new HashMap<>();
    for (final Entry<AbstractVertex, Long> en : brv.entrySet()) {
      final AbstractVertex a = en.getKey();
      final AbstractVertex actor = PreesmCopyTracker.getOriginalSource(a);
      long wcetMin = Long.MAX_VALUE;
      if (actor instanceof final AbstractActor aActor) {
        for (final Component operatorDefinitionID : architecture.getProcessingElements()) {
          final long timing = scenario.getTimings().evaluateExecutionTimeOrDefault(aActor, operatorDefinitionID);
          if (timing < wcetMin) {
            wcetMin = timing;
          }
        }
      } else {
        wcetMin = ScenarioConstants.DEFAULT_TIMING_TASK.getValue();
      }
      wcets.put(a, wcetMin);
      if (periodicActors.getOrDefault(a, wcetMin) < wcetMin) {
        throw new PreesmRuntimeException("Actor <" + a.getName() + "> has an execution time greater than its period.");
      }
    }

    // simply check sum of wcets and return.
    final long totC = wcets.entrySet().stream().map(en -> en.getValue() * brv.get(en.getKey())).reduce(0L,
        (a, b) -> a + b);

    if (totC > nbCore * graphPeriod) {
      throw new PreesmRuntimeException("Utilization factor is greater than number of cores, not schedulable.");
    }
    if (periodicActors.isEmpty()) {
      // then there is no need for further analysis
      PreesmLogger.getLogger()
          .info(() -> "Periodic prescheduling check : valid schedule *might* exist! (total load: " + totC + ")");
      return output;
    }

    // 0. find all cycles and retrieve actors placed after delays.
    PiMMHelper.removeNonExecutedActorsAndFifos(graphCopy, brv);
    final HeuristicLoopBreakingDelays heurFifoBreaks = new HeuristicLoopBreakingDelays();
    heurFifoBreaks.performAnalysis(graphCopy, brv);

    // 1. log all actor w/o incoming edges and all others w/o outgoing edge
    final StringBuilder sources = new StringBuilder();
    heurFifoBreaks.allSourceActors.stream().forEach(a -> sources.append(a.getName() + " / "));
    PreesmLogger.getLogger().fine(() -> "Sources: " + sources.toString());
    final StringBuilder sinks = new StringBuilder();
    heurFifoBreaks.allSinkActors.stream().forEach(a -> sinks.append(a.getName() + " / "));
    PreesmLogger.getLogger().fine(() -> "Sinks: " + sinks.toString());

    // 2. perform heuristic to select periodic nodes
    final StringBuilder sbNBFF = new StringBuilder();
    final Map<Actor, Double> actorsNBFF = HeuristicPeriodicActorSelection.selectActors(periodicActors, heurFifoBreaks,
        rate, wcets, false);
    actorsNBFF.keySet().forEach(a -> sbNBFF.append(a.getName() + " / "));
    PreesmLogger.getLogger().info(() -> "Periodic actor for NBFF: " + sbNBFF.toString());

    final StringBuilder sbNBLF = new StringBuilder();
    final Map<Actor, Double> actorsNBLF = HeuristicPeriodicActorSelection.selectActors(periodicActors, heurFifoBreaks,
        rate, wcets, true);
    actorsNBLF.keySet().forEach(a -> sbNBLF.append(a.getName() + " / "));
    PreesmLogger.getLogger().info(() -> "Periodic actor for NBLF: " + sbNBLF.toString());

    // 3. for each selected periodic node for nblf:
    performAllNBF(actorsNBLF, periodicActors, false, heurFifoBreaks, wcets, nbCore);

    // 4. for each selected periodic node for nbff:
    performAllNBF(actorsNBFF, periodicActors, true, heurFifoBreaks, wcets, nbCore);

    final long duration = System.nanoTime() - time;
    PreesmLogger.getLogger().info(() -> "Time+ " + Math.round(duration / 1e6) + " ms.");

    // 5. greetings to the user
    PreesmLogger.getLogger()
        .info(() -> "Periodic prescheduling check succeeded: valid schedule *might* exist! (total load: " + totC + ")");

    return output;
  }

  /**
   * Compare actors per increasing period.
   *
   * @author ahonorat
   */
  private static class ActorPeriodComparator implements Comparator<Actor> {

    private final boolean reverse;

    private ActorPeriodComparator() {
      this(false);
    }

    private ActorPeriodComparator(boolean reverse) {
      this.reverse = reverse;
    }

    @Override
    public int compare(Actor arg0, Actor arg1) {
      if (reverse) {
        return Long.compare(arg1.getPeriod().evaluateAsLong(), arg0.getPeriod().evaluateAsLong());
      }
      return Long.compare(arg0.getPeriod().evaluateAsLong(), arg1.getPeriod().evaluateAsLong());
    }

  }

  /**
   * Call NBF on given actors plus extra smaller periods.
   *
   */
  private static void performAllNBF(Map<Actor, Double> actorsNBF, Map<Actor, Long> allPeriodicActors, boolean reverse,
      HeuristicLoopBreakingDelays hlbd, Map<AbstractVertex, Long> wcets, int nbCore) {

    final DefaultDirectedGraph<AbstractActor, FifoAbstraction> absGraph = hlbd.getAbsGraph();
    for (final Actor a : actorsNBF.keySet()) {
      final long slack = allPeriodicActors.get(a) - wcets.get(a);
      long totC = 0;
      final HashMap<AbstractActor, Long> nbf = new HashMap<>();
      nbf.put(a, 1L);

      final DefaultDirectedGraph<AbstractActor,
          FifoAbstraction> subgraph = AbstractGraph.subDAGFrom(absGraph, a, hlbd.breakingFifosAbs, reverse);
      totC += performNBFinternal(a, subgraph, wcets, nbf, nbCore, reverse, slack);

      final TreeMap<Actor, Long> nbTimesDuringAperiod = new TreeMap<>(new ActorPeriodComparator(true));
      allPeriodicActors.keySet().forEach(e -> {
        final long ePeriod = e.getPeriod().evaluateAsLong();
        if (ePeriod <= slack && !nbf.containsKey(e)) {
          nbTimesDuringAperiod.put(e, slack / ePeriod);
        }
      });

      // we perform nbf on actors not connected to the initial subgraph, updating each time the nbf values
      // iterating in the descendant order
      for (final Entry<Actor, Long> entry : nbTimesDuringAperiod.entrySet()) {
        if (nbf.containsKey(entry.getKey())) {
          continue;
        }
        nbf.put(entry.getKey(), entry.getValue());
        final DefaultDirectedGraph<AbstractActor, FifoAbstraction> unconnectedsubgraph = AbstractGraph
            .subDAGFrom(absGraph, entry.getKey(), hlbd.breakingFifosAbs, reverse);
        totC += performNBFinternal(entry.getKey(), unconnectedsubgraph, wcets, nbf, nbCore, reverse, slack);
        totC += wcets.get(entry.getKey()) * entry.getValue();
      }

      if (totC > nbCore * slack) {
        throw new PreesmRuntimeException("Utilization factor from/to <" + a.getName()
            + "> is too heavy compared to its period and the number of cores (" + (totC / (double) nbCore) + ").");
      }

    }

  }

  private static long performNBFinternal(Actor start, DefaultDirectedGraph<AbstractActor, FifoAbstraction> subgraph,
      Map<AbstractVertex, Long> wcets, Map<AbstractActor, Long> previousNbf, int nbCore, boolean reverse, long slack) {
    final HashMap<AbstractActor, Long> timeTo = new HashMap<>();
    final HashMap<AbstractActor, Integer> nbVisits = new HashMap<>();
    final HashMap<AbstractActor, Long> nbf = new HashMap<>();

    for (final AbstractActor a : subgraph.vertexSet()) {
      timeTo.put(a, 0L);
      nbVisits.put(a, 0);
      nbf.put(a, 0L);
    }
    nbf.put(start, previousNbf.get(start));

    final List<AbstractActor> toVisit = new LinkedList<>();
    toVisit.add(start);

    while (!toVisit.isEmpty()) {
      final Iterator<AbstractActor> it = toVisit.iterator();
      final AbstractActor current = it.next();
      it.remove();

      for (final FifoAbstraction fa : subgraph.outgoingEdgesOf(current)) {
        final AbstractActor dest = subgraph.getEdgeTarget(fa);
        final int nbVisitsDest = nbVisits.get(dest) + 1;
        nbVisits.put(dest, nbVisitsDest);
        final long destTimeTo = Math.max(timeTo.get(dest), timeTo.get(current));
        timeTo.put(dest, destTimeTo);
        long nbfDest = 0;
        final long delay = fa.delays.stream().min(Long::compare).orElse(0L);
        if (reverse) {
          nbfDest = (nbf.get(current) * fa.getConsRate() - delay + fa.getProdRate() - 1) / fa.getProdRate();
        } else {
          nbfDest = (nbf.get(current) * fa.getProdRate() - delay + fa.getConsRate() - 1) / fa.getConsRate();
        }
        nbf.put(dest, Math.max(nbfDest, nbf.get(dest)));
        if (nbVisitsDest == subgraph.inDegreeOf(dest) && nbfDest > 0) {
          final long prevNBF = previousNbf.getOrDefault(dest, 0L);
          if (prevNBF >= nbfDest) {
            continue;
          }
          previousNbf.put(dest, nbfDest);
          nbfDest -= prevNBF;
          nbf.put(dest, nbfDest);

          toVisit.add(dest);
          final long time = destTimeTo + wcets.get(dest) * Math.max(1L, nbfDest / nbCore);
          timeTo.put(dest, time);
          if (subgraph.outDegreeOf(dest) == 0 && time > slack) {
            throw new PreesmRuntimeException("Critical path from/to <" + start.getName()
                + "> is too long compared to its period (duration is " + time + " while slack time is " + slack + ").");
          }

        }
      }

    }

    long totC = -wcets.get(start) * previousNbf.get(start);
    for (final AbstractActor a : subgraph.vertexSet()) {
      totC += wcets.get(a) * nbf.get(a);
    }
    if (totC > nbCore * slack) {
      throw new PreesmRuntimeException("Utilization factor from/to <" + start.getName()
          + "> is too heavy compared to its period and the number of cores (" + (totC / (double) nbCore) + ").");
    }
    return totC;

  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(PeriodsPreschedulingCheckTask.SELECTION_RATE, PeriodsPreschedulingCheckTask.DEFAULT_SELECTION_RATE);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Checking if periods constraints prevent to have a schedulable application.";
  }

}
