/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019 - 2020)
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
package org.preesm.algorithm.synthesis.schedule.algos;

import com.google.common.primitives.Ints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.eclipse.emf.common.util.ECollections;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.preesm.algorithm.mapping.model.MappingFactory;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.timer.AgnosticTimer;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.AbstractActorNameComparator;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.check.SlamDesignPEtypeChecker;

/**
 * This class performs scheduling thanks to a choco constraint programming formulation.
 * <p>
 * Be aware that the mapping constraints specified by the user will not be respected!
 * 
 * @author ahonorat
 */
public class ChocoScheduler extends PeriodicScheduler {

  public static final long    maxSolution  = 100L;
  public static final long    maxSolveTime = 3600000L; // 1 hour (in ms)
  public static final boolean verbose      = true;

  @Override
  protected SynthesisResult exec(PiGraph piGraph, Design slamDesign, Scenario scenario) {

    if (!SlamDesignPEtypeChecker.isHomogeneousCPU(slamDesign)) {
      throw new PreesmSchedulingException("This task must be called with a homogeneous CPU architecture, abandon.");
    }

    int nbCores = slamDesign.getProcessingElements().get(0).getInstances().size();
    PreesmLogger.getLogger().log(Level.INFO, "Found " + nbCores + " cores.");

    long graphPeriod = piGraph.getPeriod().evaluate();
    PreesmLogger.getLogger().log(Level.INFO, "Graph period is: " + graphPeriod);

    this.piGraph = piGraph;
    this.slamDesign = slamDesign;
    this.scenario = scenario;
    // TODO test if AgnosticTimer or SimplerTimer (with special actors time) is better
    this.st = new AgnosticTimer(scenario, 1L);

    nbFiringsAllocated = 0;
    // initializes component operators and related attributes
    cores = new ArrayList<>();
    ciTOca = new HashMap<>();
    List<List<Task>> coreSchedules = new ArrayList<>();

    // the constraints in scenario are actually NOT respected in this scheduler
    possibleMappings = new TreeMap<>(new AbstractActorNameComparator());
    topParallelSchedule = ScheduleFactory.eINSTANCE.createParallelHiearchicalSchedule();
    resultMapping = MappingFactory.eINSTANCE.createMapping();
    for (ComponentInstance ci : slamDesign.getProcessingElements().get(0).getInstances()) {
      final ActorSchedule createActorSchedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
      topParallelSchedule.getScheduleTree().add(createActorSchedule);
      CoreAbstraction ca = new CoreAbstraction(ci, createActorSchedule);
      cores.add(ca);
      ciTOca.put(ci, ca);
      coreSchedules.add(new ArrayList<>());
      if (ci.equals(scenario.getSimulationInfo().getMainOperator())) {
        defaultCore = ca;
      }
    }

    // create abstraction of the input SRDAG, and initializes min/maxStartTime of periodic actors
    createAbsGraph();

    PreesmLogger.getLogger().log(Level.INFO,
        "Starting to schedule and map " + absGraph.vertexSet().size() + " actors.");

    // initializes total load, source and sinks nodes
    Ctot = 0;
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

    // initializes and check horizon
    horizon = graphPeriod;
    if (horizon <= 0) {
      horizon = Ctot;
      PreesmLogger.getLogger().log(Level.INFO,
          "No period found: scheduling performed with sequential worst case: " + Ctot + " time unit.");
    } else if (Ctot / (double) nbCores > horizon) {
      throw new PreesmSchedulingException(
          "Your graph is clearly not schedulable: utilization factor is higher than number of cores. Total load: "
              + Ctot);
    }

    // initializes min/max/averageStartTime of all actors (and updates the periodic one)
    setAbsGraph(absGraph, horizon, firstNodes, lastNodes);

    SortedMap<Integer, Task> tasks = createTasksFromAbsGraph(absGraph);

    long solverHorizon = (graphPeriod > 0) ? 0 : horizon;

    ChocoSchedModel csm = new ChocoSchedModel("testExprt", tasks, nbCores, Ints.saturatedCast(solverHorizon));
    Model schedModel = csm.generateModel();
    PreesmLogger.getLogger().info("Model built with " + nbCores + " cores.");

    // use ParallelPortfolio?
    Solver solver = schedModel.getSolver();
    // solver.limitTime(maxSolveTime);
    solver.limitSolution(maxSolution);
    long time = System.nanoTime();

    Solution s = new Solution(schedModel);
    while (solver.solve()) {
      s.record();
      // if there was a graph period, then we just look for one solution
      if (graphPeriod > 0) {
        break;
      }
    }

    if (verbose) {
      // solver.setOut(System.err);
      solver.printStatistics();
    }

    long solutionCount = solver.getSolutionCount();

    if (solutionCount > 0) {
      // do something, e.g. print out variable values
      time = System.nanoTime() - time;
      PreesmLogger.getLogger().info("Time+ " + Math.round(time / 1e6) + " ms.");
      PreesmLogger.getLogger().info("Model solved with " + nbCores + " cores.");

      if (graphPeriod <= 0) {
        // in this case there is an objective in the model and several iterations, print the values
        int latency = solver.getObjectiveManager().getBestSolutionValue().intValue();
        PreesmLogger.getLogger().info("Model solved with latency of " + latency + " time units.");
        PreesmLogger.getLogger().info("Solver iterated " + solutionCount + " times to get a good result.");
      }

      for (int i = 0; i < csm.nbTasks; i++) {
        int start = s.getIntVal(csm.startTimeVars[i]);
        Task t = tasks.get(i);
        VertexAbstraction va = t.va;
        // update start time
        va.startTime = start;
        t.st = start;
        int core = -1;
        for (int j = 0; j < csm.nbCores; j++) {
          if (s.getIntVal(csm.mapping[i][j]) == 1) {
            if (core != -1) {
              throw new PreesmSchedulingException("Mapping error for task: " + i + "\n");
            }
            core = j;
            CoreAbstraction ca = cores.get(j);
            // update mapping directly
            resultMapping.getMappings().put(va.aa, ECollections.singletonEList(ca.ci));

            PreesmLogger.getLogger().finer("Task " + va.aa.getName() + " mapped on core " + j + " at time " + start);
            // update schedule
            insertTaskInSchedule(t, coreSchedules.get(j));
          }
        }
      }

    } else if (solver.isStopCriterionMet()) {
      time = System.nanoTime() - time;
      PreesmLogger.getLogger().info("Time~ " + Math.round(time / 1e6) + " ms.");
      throw new PreesmSchedulingException(
          "The solver could not find a solution nor prove that none exists in the given limits");
    } else {
      time = System.nanoTime() - time;
      PreesmLogger.getLogger().info("Time- " + Math.round(time / 1e6) + " ms.");
      throw new PreesmSchedulingException("The solver has proved the problem has no solution");
    }

    // recopy task in schedule
    for (int j = 0; j < nbCores; j++) {
      CoreAbstraction ca = cores.get(j);
      for (Task t : coreSchedules.get(j)) {
        ca.coreSched.getActorList().add(t.va.aa);
      }
    }

    return new SynthesisResult(resultMapping, topParallelSchedule, null);

  }

  /**
   * Stores info for the choco model.
   * 
   * @author ahonorat
   *
   */
  protected class Task {

    protected final int          id;
    protected final int          load;
    protected final int          ns;
    protected final int          xs;
    protected int                st;       // will be set after sched
    protected final Set<Integer> predId;
    protected final Set<Integer> allPredId;

    protected final VertexAbstraction va;

    private Task(int id, int load, int ns, int xs, Set<Integer> predId, VertexAbstraction va) {
      this.id = id; // in graph topological order
      this.load = load;
      this.ns = ns;
      this.xs = xs;
      this.predId = predId;

      // set at the end, by transitive closure
      this.allPredId = new HashSet<>();

      this.va = va;
    }
  }

  protected SortedMap<Integer, Task>
      createTasksFromAbsGraph(DefaultDirectedGraph<VertexAbstraction, EdgeAbstraction> absGraph) {

    final SortedMap<Integer, Task> res = new TreeMap<>();
    final Map<VertexAbstraction, Task> vaTOtask = new HashMap<>();
    int tid = 0; // it must start at 0 since it is use in arrays in the model
    // set ids and create task set
    List<VertexAbstraction> toVisit = new LinkedList<>(firstNodes);
    while (!toVisit.isEmpty()) {
      VertexAbstraction va = toVisit.remove(0);
      Set<Integer> predId = new HashSet<>();
      // TODO unsafe cast to int
      Task t = new Task(tid, Ints.saturatedCast(va.load), Ints.saturatedCast(va.minStartTime),
          Ints.saturatedCast(va.maxStartTime), predId, va);
      res.put(tid, t);
      vaTOtask.put(va, t);
      tid++;

      for (EdgeAbstraction ea : absGraph.outgoingEdgesOf(va)) {
        VertexAbstraction oppositeva = absGraph.getEdgeTarget(ea);
        updateNbVisits(absGraph, oppositeva, false, toVisit);
      }
    }
    // set predId
    toVisit = new LinkedList<>(lastNodes);
    while (!toVisit.isEmpty()) {
      VertexAbstraction va = toVisit.remove(0);
      Task tva = vaTOtask.get(va);
      for (EdgeAbstraction ea : absGraph.incomingEdgesOf(va)) {
        VertexAbstraction oppositeva = absGraph.getEdgeSource(ea);
        tva.predId.add(vaTOtask.get(oppositeva).id);
        tva.allPredId.add(vaTOtask.get(oppositeva).id);
        updateNbVisits(absGraph, oppositeva, true, toVisit);
      }
    }
    // set allPredId
    toVisit = new LinkedList<>(firstNodes);
    while (!toVisit.isEmpty()) {
      VertexAbstraction va = toVisit.remove(0);
      Task tva = vaTOtask.get(va);
      for (EdgeAbstraction ea : absGraph.outgoingEdgesOf(va)) {
        VertexAbstraction oppositeva = absGraph.getEdgeTarget(ea);
        vaTOtask.get(oppositeva).allPredId.addAll(tva.predId);
        updateNbVisits(absGraph, oppositeva, false, toVisit);
      }
    }

    return res;
  }

  // ascending startTime
  protected static void insertTaskInSchedule(Task t, List<Task> queue) {
    ListIterator<Task> it = queue.listIterator();
    while (it.hasNext()) {
      Task next = it.next();
      if (next.st < t.st) {
        // continue until we are smaller than the next start time
        continue;
      } else if ((next.st > t.st) || ((next.st == t.st) && (next.id > t.id))) {
        // at equal start time (may happen if some load = 0), we respect the graph topological order
        it.previous();
        break;
      }
    }
    it.add(t);
  }

}
