/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2018 - 2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
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
package org.preesm.algorithm.pisdf.checker;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.preesm.algorithm.pisdf.checker.AbstractGraph.FifoAbstraction;
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
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.PeriodicElement;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioConstants;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * This class computes necessary conditions for the schedulability of graphs with periods.
 *
 * @author ahonorat
 *
 */
@PreesmTask(id = "org.ietr.preesm.pimm.algorithm.checker.periods.PeriodsPreschedulingChecker",
    name = "Periods Prescheduling Checker",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),
        @Port(name = "architecture", type = Design.class) },

    outputs = { @Port(name = "PiMM", type = PiGraph.class) },

    parameters = { @Parameter(name = "Selection rate (%)", values = { @Value(name = "100", effect = "") }) }

)

public class PeriodsPreschedulingChecker extends AbstractTaskImplementation {

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

    if (architecture.getOperatorComponents().size() != 1) {
      throw new PreesmRuntimeException("This task must be called with a homogeneous architecture, abandon.");
    }

    int nbCore = architecture.getOperatorComponents().get(0).getInstances().size();
    PreesmLogger.getLogger().log(Level.INFO, "Found " + nbCore + " cores.");

    final String rateStr = parameters.get(PeriodsPreschedulingChecker.SELECTION_RATE);
    int rate = 100;
    try {
      rate = Integer.parseInt(rateStr);
      if ((rate < 0) || (rate > 100)) {
        throw new PreesmRuntimeException(PeriodsPreschedulingChecker.GENERIC_RATE_ERROR + rate + ".");
      }
    } catch (final NumberFormatException e) {
      throw new PreesmRuntimeException(PeriodsPreschedulingChecker.GENERIC_RATE_ERROR + rateStr + ".", e);
    }

    final Map<Actor, Long> periodicActors = new LinkedHashMap<>();
    for (final AbstractActor absActor : graph.getActors()) {
      if ((absActor instanceof Actor) && (absActor instanceof PeriodicElement)) {
        final Actor actor = (Actor) absActor;
        if (!actor.isHierarchical() && !actor.isConfigurationActor()) {
          final long period = actor.getPeriod().evaluate();
          if (period > 0) {
            periodicActors.put(actor, period);
          }
        }
      }
      if (absActor instanceof DelayActor) {
        DelayActor da = (DelayActor) absActor;
        if (da.getSetterActor() != null || da.getGetterActor() != null) {
          throw new PreesmRuntimeException("DelayActor with getter or setter are not supported in this task, abandon.");
        }
      }
    }

    Map<AbstractVertex, Long> brv = PiBRV.compute(graph, BRVMethod.LCM);
    Map<AbstractVertex, Long> wcets = new HashMap<>();
    for (final Entry<AbstractVertex, Long> en : brv.entrySet()) {
      final AbstractVertex a = en.getKey();
      AbstractVertex actor = PreesmCopyTracker.getOriginalSource(a);
      long wcetMin = Long.MAX_VALUE;
      if (actor instanceof AbstractActor) {
        for (final Component operatorDefinitionID : architecture.getOperatorComponents()) {
          final long timing = scenario.getTimings().evaluateTimingOrDefault((AbstractActor) actor,
              operatorDefinitionID);
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

    // 0. find all cycles and retrieve actors placed after delays.
    HeuristicLoopBreakingDelays heurFifoBreaks = new HeuristicLoopBreakingDelays();
    heurFifoBreaks.performAnalysis(graph, brv);

    // 1. find all actor w/o incoming edges and all others w/o outgoing edge
    final Set<AbstractActor> sourceActors = new LinkedHashSet<>(heurFifoBreaks.additionalSourceActors);
    final Set<AbstractActor> sinkActors = new LinkedHashSet<>(heurFifoBreaks.additionalSinkActors);
    for (final AbstractActor absActor : graph.getActors()) {
      if (absActor instanceof ExecutableActor) {
        if (absActor.getDataOutputPorts().isEmpty()) {
          sinkActors.add(absActor);
        }
        if (absActor.getDataInputPorts().isEmpty()) {
          sourceActors.add(absActor);
        }
      }
    }

    StringBuilder sources = new StringBuilder();
    sourceActors.stream().forEach(a -> sources.append(a.getName() + " / "));
    PreesmLogger.getLogger().log(Level.FINE, "Sources: " + sources.toString());
    StringBuilder sinks = new StringBuilder();
    sinkActors.stream().forEach(a -> sinks.append(a.getName() + " / "));
    PreesmLogger.getLogger().log(Level.FINE, "Sinks: " + sinks.toString());

    // 2. perform heuristic to select periodic nodes
    final StringBuilder sbNBFF = new StringBuilder();
    final Map<Actor, Long> actorsNBFF = HeuristicPeriodicActorSelection.selectActors(periodicActors, sourceActors,
        heurFifoBreaks.actorsNbVisitsTopoRank, rate, wcets, false);
    actorsNBFF.keySet().forEach(a -> sbNBFF.append(a.getName() + " / "));
    PreesmLogger.getLogger().log(Level.INFO, "Periodic actor for NBFF: " + sbNBFF.toString());

    final StringBuilder sbNBLF = new StringBuilder();
    final Map<Actor, Long> actorsNBLF = HeuristicPeriodicActorSelection.selectActors(periodicActors, sinkActors,
        heurFifoBreaks.actorsNbVisitsTopoRankT, rate, wcets, true);
    actorsNBLF.keySet().forEach(a -> sbNBLF.append(a.getName() + " / "));
    PreesmLogger.getLogger().log(Level.INFO, "Periodic actor for NBLF: " + sbNBLF.toString());

    // 3. for each selected periodic node for nblf:
    performNBF(actorsNBLF, periodicActors, false, heurFifoBreaks.absGraph, heurFifoBreaks.breakingFifosAbs, wcets,
        heurFifoBreaks.minCycleBrv, nbCore);

    // 4. for each selected periodic node for nbff:
    performNBF(actorsNBFF, periodicActors, true, heurFifoBreaks.absGraph, heurFifoBreaks.breakingFifosAbs, wcets,
        heurFifoBreaks.minCycleBrv, nbCore);

    final Map<String, Object> output = new LinkedHashMap<>();
    output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, graph);
    return output;
  }

  /**
   * Compare actors per increasing period.
   * 
   * @author ahonorat
   */
  private static class ActorPeriodComparator implements Comparator<Actor> {

    @Override
    public int compare(Actor arg0, Actor arg1) {
      return Long.compare(arg0.getPeriod().evaluate(), arg1.getPeriod().evaluate());
    }

  }

  /**
   * Call NBF on given actors plus extra smaller periods.
   * 
   */
  private static void performNBF(Map<Actor, Long> actorsNBF, Map<Actor, Long> allPeriodicActors, boolean reverse,
      DefaultDirectedGraph<AbstractActor, FifoAbstraction> absGraph, Set<FifoAbstraction> breakingFifosAbs,
      Map<AbstractVertex, Long> wcets, Map<AbstractVertex, Long> minCycleBrv, int nbCore) {
    SortedSet<Actor> askedToTest = new TreeSet<>(new ActorPeriodComparator());
    askedToTest.addAll(actorsNBF.keySet());
    final long greatestPeriod = askedToTest.last().getPeriod().evaluate();

    SortedSet<Actor> toTest = new TreeSet<>(new ActorPeriodComparator());
    allPeriodicActors.keySet().forEach(a -> {
      if (a.getPeriod().evaluate() <= greatestPeriod) {
        toTest.add(a);
      }
    });

    for (Actor a : toTest) {
      DefaultDirectedGraph<AbstractActor,
          FifoAbstraction> subgraph = AbstractGraph.subDAGFrom(absGraph, a, breakingFifosAbs, reverse);
      performNBFinternal(a, subgraph, allPeriodicActors, wcets, minCycleBrv, nbCore, reverse);
    }

  }

  private static void performNBFinternal(Actor start, DefaultDirectedGraph<AbstractActor, FifoAbstraction> subgraph,
      Map<Actor, Long> allPeriodicActors, Map<AbstractVertex, Long> wcets, Map<AbstractVertex, Long> minCycleBrv,
      int nbCore, boolean reverse) {
    HashMap<AbstractActor, Long> timeTo = new HashMap<>();
    HashMap<AbstractActor, Integer> nbVisits = new HashMap<>();
    HashMap<AbstractActor, Long> nbf = new HashMap<>();
    for (AbstractActor a : subgraph.vertexSet()) {
      timeTo.put(a, 0L);
      nbVisits.put(a, 0);
      nbf.put(a, 0L);
    }
    nbf.put(start, 1L);

    long slack = allPeriodicActors.get(start) - wcets.get(start);
    List<AbstractActor> toVisit = new LinkedList<>();
    toVisit.add(start);

    while (!toVisit.isEmpty()) {
      Iterator<AbstractActor> it = toVisit.iterator();
      AbstractActor current = it.next();
      it.remove();

      for (FifoAbstraction fa : subgraph.outgoingEdgesOf(current)) {
        AbstractActor dest = subgraph.getEdgeTarget(fa);
        int nbVisitsDest = nbVisits.get(dest) + 1;
        nbVisits.put(dest, nbVisitsDest);
        long destTimeTo = Math.max(timeTo.get(dest), timeTo.get(current));
        timeTo.put(dest, destTimeTo);
        long nbfDest = 0;
        long delay = fa.delays.stream().min(Long::compare).get();
        if (reverse) {
          nbfDest = (nbf.get(current) * fa.consRate - delay + fa.prodRate - 1) / fa.prodRate;
        } else {
          nbfDest = (nbf.get(current) * fa.prodRate - delay + fa.consRate - 1) / fa.consRate;
        }
        nbf.put(dest, Math.max(nbfDest, nbf.get(dest)));
        if (nbVisitsDest == subgraph.inDegreeOf(dest) && nbfDest > 0) {
          toVisit.add(dest);
          long wcet = wcets.get(dest);
          long minBrv = minCycleBrv.get(dest);
          long factorBrv = nbfDest / minBrv;
          long remainingBrv = nbfDest % minBrv;
          long timeRegular = wcet * factorBrv * Math.max(1L, minBrv / nbCore);
          long timeRemaining = wcet * (remainingBrv / nbCore);
          long time = destTimeTo + Math.max(wcet, timeRegular + timeRemaining);
          timeTo.put(dest, time);
          if (subgraph.outDegreeOf(dest) == 0) {
            if (time > slack) {
              throw new PreesmRuntimeException("Critical path from/to <" + start.getName()
                  + "> is too long compared to its period ( " + time + ").");
            }
          }
        }
      }

    }

    long totC = -wcets.get(start);
    for (AbstractActor a : subgraph.vertexSet()) {
      totC += wcets.get(a) * nbf.get(a);
    }
    if (totC > nbCore * slack) {
      throw new PreesmRuntimeException("Utilization factor from/to" + start.getName()
          + "> is too heavy compared to its period and the number of cores (" + (totC / (double) nbCore) + ").");
    }

  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(PeriodsPreschedulingChecker.SELECTION_RATE, PeriodsPreschedulingChecker.DEFAULT_SELECTION_RATE);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Checking if periods constraints prevent to have a schedulable application.";
  }

}
